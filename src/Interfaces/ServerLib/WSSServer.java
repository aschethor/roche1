package Interfaces.ServerLib;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by Ni on 05.06.2015.
 * einfacher WebSocket-Server
 * Achtung: Wichtig!!!;) :
 * wenn man self-assigned certificates verwendet, muss man sicherstellen, dass die URL der HTTPS-Anfrage mit der
 * der WSS-Anfrage übereinstimmt! (nicht etwa: localhost und 192.168.1.x !)
 * -> sonst checkt Chrome nicht, dass das Zertifikat für beide URLs vom Anwender bestätigt wurde...
 * ...funzt bisher nur auf Chrome :(
 */
public abstract class WSSServer extends Thread {
    int port;
    String keyFile;
    String pw;

    public WSSServer(String keyFile,String pw){
        port=8080;
        this.keyFile=keyFile;
        this.pw=pw;
    }

    public WSSServer(int port,String keyFile,String pw){
        this.port=port;
        this.keyFile=keyFile;
        this.pw=pw;
    }

    public void run(){
        try{
            //System.setProperty("javax.net.ssl.keyStore",keyFile);
            //System.setProperty("javax.net.ssl.keyStorePassword",pw);

            SSLServerSocketFactory SSLssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) SSLssf.createServerSocket(port);
            while (true){
                try{
                    getWS().accept(serverSocket).start();
                }catch (Exception e){
                    System.out.println("WSSServer-Error(1): " + e);
                }
            }
        }catch(Exception e){
            System.out.println("WSSServer-Error(2): " + e);
        }
    }

    public abstract WebSocket getWS();

    public static class WebSocket extends Thread {
        SSLSocket connection;

        public final WebSocket accept(SSLServerSocket serverSocket) throws Exception {
            return accept((SSLSocket)serverSocket.accept());
        }

        public final WebSocket accept(SSLSocket connection) throws Exception {
            this.connection = connection;

            //Client-request
            String code="";
            while(true){
                String text = read();
                //System.out.println(text);
                String spl[]=text.split(": ");
                if(spl[0].equals("Sec-WebSocket-Key"))
                    code=spl[1];
                if(text.equals(""))break;
            }

            //response
            write("HTTP/1.1 101 Switching Protocols");
            write("Upgrade: websocket");
            write("Connection: Upgrade");
            write("Sec-WebSocket-Accept: " + Base64enc(sha1(code + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")));
            write("");

            new Ping(this).start();

            return this;
        }

        public void run(){
            try {
                onStart();
                while (true){
                    onMessage(recv());
                }
            }catch (Exception e) {
                //e.printStackTrace();
                System.out.println("WebSocket-Error: " + e);
                onEnd();
            }finally {
                try {
                    connection.close();
                } catch (Exception e1) {
                    System.out.println("Websocket-Error: on close");
                }
            }
        }

        public void onStart() throws Exception {};

        public void onMessage(String Message) throws Exception {
            System.out.println(Message);
        }

        public void onEnd(){};

        public void send(String msg) throws Exception {
            send(1,msg.getBytes());
        }

        public void send(int opcode, byte[] data) throws Exception {
            connection.getOutputStream().write((byte)(128+opcode));
            if(data.length>65535){
                throw new Exception("send too big data-frame");
            }else if(data.length>125){
                connection.getOutputStream().write((byte)126);
                connection.getOutputStream().write((byte)(data.length>>8));
                connection.getOutputStream().write((byte)(data.length%256));
            }else {
                connection.getOutputStream().write((byte) data.length);
            }
            connection.getOutputStream().write(data);
            connection.getOutputStream().flush();
        }

        public void sendmasked(int opcode, byte[] data) throws Exception{
            //wird vom Browser eh nicht akzeptiert...
            connection.getOutputStream().write((byte)(128+opcode));
            if(data.length>65535){
                throw new Exception("send too big data-frame");
            }else if(data.length>125){
                connection.getOutputStream().write((byte)254);
                connection.getOutputStream().write((byte)(data.length>>8));
                connection.getOutputStream().write((byte)(data.length%256));
            }else{
                connection.getOutputStream().write((byte)(data.length|128));
            }
            byte[] mask = new byte[4];
            new Random().nextBytes(mask);
            for (int i = 0; i < data.length; i++)
                data[i] = (byte) (data[i] ^ mask[i % 4]);
            connection.getOutputStream().write(data);
            connection.getOutputStream().flush();
        }

        public String recv() throws Exception {
            byte[] data;
            boolean istext=true;
            do {
                //header
                byte[] header = new byte[2];
                int end=connection.getInputStream().read(header, 0, 2);
                int opcode=header[0] & 0x0F;
                if (opcode == 8||end==-1)
                    throw new Exception("Connection closed");
                if (opcode != 1)
                    istext = false;

                //datasize
                int datasize = header[1] & 0x7F;
                if (datasize == 126) {
                    byte[] extdatasize = new byte[2];
                    connection.getInputStream().read(extdatasize, 0, 2);
                    datasize = (extdatasize[0] << 8) + (extdatasize[1] & 0xFF);
                } else if (datasize == 127) {
                    byte[] extdatasize = new byte[8];
                    connection.getInputStream().read(extdatasize, 0, 2);
                    if (extdatasize[0] != 0 || extdatasize[1] != 0 || extdatasize[2] != 0 ||
                            extdatasize[3] != 0 || (extdatasize[4] & 0x80) != 0)
                        throw new Exception("recv too big data-frame");
                    datasize = (extdatasize[4] << 24) + (extdatasize[5] << 16) +
                            (extdatasize[6] << 8) + (extdatasize[7] & 0xFF);
                }

                //mask
                boolean ismasked = (header[1] & 0x80) != 0;
                byte[] mask = null;
                if (ismasked) {
                    mask = new byte[4];
                    connection.getInputStream().read(mask, 0, 4);
                }

                //data + unmask
                data = new byte[datasize];
                connection.getInputStream().read(data, 0, datasize);
                if (ismasked)
                    for (int i = 0; i < datasize; i++)
                        data[i] = (byte) (data[i] ^ mask[i % 4]);

                if(opcode==0x9){
                    send(0xA, data);}
                if(opcode!=0x1){
                    System.out.println("No-Text-msg: opcode: "+opcode+"; data: "+UTF8ToString(data));
                }

                if(opcode==0x0)
                    throw new Exception("Continuation frames aren't supported :"+UTF8ToString(data)+".");

            }while (!istext);
            return UTF8ToString(data);
        }

        public String read() throws Exception {
            String ret="";
            while (true){
                byte[] proret = new byte[1];
                int end = connection.getInputStream().read(proret,0,1);
                if(end==-1||proret[0]==10)break;
                if(proret[0]!=13)
                    ret+=(char)proret[0];
            }
            return ret;
        }

        public void write(String msg) throws Exception {
            msg+="\n";
            connection.getOutputStream().write(msg.getBytes());
            connection.getOutputStream().flush();
        }

        static String UTF8ToString(byte[] a) throws Exception {
            return new String(a,"UTF-8");
        }

        static String Base64enc(byte[] a){
            return DatatypeConverter.printBase64Binary(a);
            //return Base64.getEncoder().encodeToString(a);
        }

        static byte[] sha1(String input) throws Exception {
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            return mDigest.digest(input.getBytes());
        }

        class Ping extends Thread {
            WebSocket ws;
            public Ping(WebSocket ws){
                this.ws=ws;
            }

            public void run() {
                try {
                    while (true) {
                        //operation-code 0x9 produces problems -> 0xA
                        ws.send(0xA, new String("Ping").getBytes());
                        Thread.sleep(10000);
                    }
                }catch (Exception e){
                }
            }
        }

    }
}
