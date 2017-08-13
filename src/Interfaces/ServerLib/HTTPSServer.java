package Interfaces.ServerLib;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Ni on 19.02.2015.
 */
public class HTTPSServer extends Thread {
    int port;
    String folder;
    String keyFile;
    String pw;
    //erstellen des Keys mit keytool (in jdkX_Y\jre\bin zu finden):
    //keytool -genkey -keystore Name.keystore -keyalg RSA
    //final static String pathToStores = "C:\\Programmieren\\Java\\jdk7_80\\jre\\bin";
    //final static String keyStoreFile = "Test.keystore";
    //final static String pw = "QAYWsxedexswyaq1";

    public HTTPSServer(String folder, String keyFile, String pw){
        port=443;
        this.folder=folder;
        this.keyFile=keyFile;
        this.pw=pw;
    }

    public HTTPSServer(String folder, int port, String keyFile, String pw){
        this.port=port;
        this.folder=folder;
        this.keyFile=keyFile;
        this.pw=pw;
    }

    public WSSServer.WebSocket getWSS(){return null;};

    public void run(){
        try{
            System.setProperty("javax.net.ssl.keyStore", keyFile);
            System.setProperty("javax.net.ssl.keyStorePassword", pw);

            SSLServerSocketFactory SSLssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) SSLssf.createServerSocket(port);
            while (true)
                new HTTPSSocket(serverSocket).start();
        }catch(Exception e){
            System.out.println("HTTPSServer-Error: " + e);
            e.printStackTrace();
        }
    }

    public class HTTPSSocket extends Thread {
        SSLSocket connection;

        public HTTPSSocket(SSLServerSocket serverSocket) throws Exception {
            connection = accept(serverSocket);
        }

        public SSLSocket accept(SSLServerSocket serverSocket) throws Exception {
            SSLSocket ret = (SSLSocket) serverSocket.accept();
            return ret;
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

        public void run() {
            try {

                String filename = "";

                //Client-request
                while (true) {
                    String text = read();
                    //System.out.println(text);
                    if (text.startsWith("GET") || text.startsWith("HEAD")) {
                        filename = text.split(" ")[1];
                        if (filename.equals("/")) filename += "index.html";
                        else if (filename.contains("..")) filename = "/index.html";
                    }
                    if(text.equalsIgnoreCase("Upgrade: websocket")|| //chrome
                            text.startsWith("Sec-WebSocket")||       //firefox
                            text.startsWith("Origin")){              //ie
                        WSSServer.WebSocket ws = getWSS();
                        if(ws!=null)ws.accept(connection).run();
                        return;
                    }
                    if (text.equals("")) break;
                }
                //response
                try {
                    if(filename=="")throw new Exception("no filename");
                    String path = folder + filename;
                    byte[] datab = Files.readAllBytes(Paths.get(path));

                    write("HTTP/1.1 200 OK");
                    write("Server: NiWa Nightly");
                    write("Content-Lenght: " + datab.length);
                    if (filename.endsWith("html")) {
                        write("Content-Type: text/html");
                    } else if (filename.endsWith("js")) {
                        write("Content-Type: application/javascript");
                    } else if (filename.endsWith("css")) {
                        datab = cssinterp(datab);
                        write("Content-Type: text/css");
                    } else if (filename.endsWith("ico")) {
                        write("Content-Type: image/x-icon");
                    } else if (filename.endsWith("svg")) {
                        write("Content-Type: image/svg+xml");
                    }
                    write("");
                    connection.getOutputStream().write(datab);
                    connection.getOutputStream().flush();
                } catch (Exception e) {
                    //System.out.println("HTTPSSocket-Error(1): " + e);
                    //e.printStackTrace();
                    write("HTTP/1.1 404");
                    write("Server: NiWa Nightly");
                    write("");
                }
            } catch (Exception e) {
                //System.out.println("HTTPSSocket-Error(2): " + e);
                //e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.out.println("HTTPSSocket-Error(3): " + e);
                }
            }
        }

        private byte[] cssinterp(byte[] data) throws Exception {
            String css=new String(data);
            String[] vars=css.split("#");
            for(int i=1;i<vars.length;i++){
                String[] name=vars[i].split(":",2);
                String[] value=name[1].split(";", 2);
                css=css.replace(name[0], value[0]);
                css=css.replace("#" + value[0] + ":" + value[0] + ";", "");
            }
            return css.getBytes();
        }
    }
}
