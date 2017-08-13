package Interfaces.ServerLib;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Ni on 19.02.2015.
 * einfacher HTTP-Server (NiWa Nightly ;)
 */
public class HTTPServer extends Thread {
    int port;
    String folder;

    public HTTPServer(String folder, int port){
        this.port=port;
        this.folder=folder;
    }

    public HTTPServer(String folder){
        port=80;
        this.folder=folder;
    }

    public WSServer.WebSocket getWS(){return null;};

    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            while (true)
                new HTTPSocket(serverSocket).start();
        }catch(Exception e){
            System.out.println("HTTPServer-Error: " + e);
        }
    }

    public class HTTPSocket extends Thread {
        public Socket connection;

        public HTTPSocket(ServerSocket serverSocket) throws Exception {
            connection = accept(serverSocket);
        }

        public Socket accept(ServerSocket serverSocket) throws Exception {
            Socket ret = serverSocket.accept();
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
                //PrintStream OUT = new PrintStream(connection.getOutputStream());
                String filename = "";

                //Client-request
                while (true) {
                    String text = read();
                    System.out.println(text);

                    Thread.sleep(100);
                    if (text.startsWith("GET") || text.startsWith("HEAD")) {
                        filename = text.split(" ")[1];
                        if (filename.equals("/")) filename += "cover.html";
                        else if (filename.contains("..")) filename = "/cover.html";
                    }
                    if(text.equalsIgnoreCase("Upgrade: websocket")|| //chrome
                            text.startsWith("Sec-WebSocket")||       //firefox
                            text.startsWith("Origin")){              //ie
                        WSServer.WebSocket ws = getWS();
                        if(ws!=null)ws.accept(connection).run();
                        return;
                    }
                    if (text.equals("")) break;
                }

                //response
                try {
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
                        //datab = cssinterp(datab);
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
                    System.out.println("HTTPSocket-Error(1): " + e);
                    e.printStackTrace();
                    write("HTTP/1.1 404");
                    write("Server: NiWa Nightly");
                    write("");
                }
            } catch (Exception e) {
                System.out.println("HTTPSocket-Error(2): " + e);
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.out.println("HTTPSocket-Error(3): " + e);
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
