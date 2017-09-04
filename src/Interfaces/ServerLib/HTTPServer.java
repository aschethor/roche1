package Interfaces.ServerLib;

import StudyMe.Login;
import StudyMe.Study;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Ni on 19.02.2015.
 * simple HTTP-Server (NiWa Nightly ;)
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
                String request = "";

                //Client-request
                while (true) {
                    String text = read();
                    System.out.println(text);

                    Thread.sleep(100);
                    if (text.startsWith("GET") || text.startsWith("HEAD")) {
                        request = text.split(" ")[1];
                        if (request.equals("/")) request += "index";
                        else if (request.contains("..")) request = "/index";
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
                    byte[] datab = new byte[0];

                    write("HTTP/1.1 200 OK");
                    write("Server: NiWa Nightly");
                    write("Content-Lenght: " + datab.length);
                    if (request.endsWith("html")) {
                        datab = Files.readAllBytes(Paths.get(folder + "/index.html"));
                        write("Content-Type: text/html");
                    } else if (request.endsWith("js")) {
                        datab = Files.readAllBytes(Paths.get(folder + request));
                        write("Content-Type: application/javascript");
                    } else if (request.endsWith("css")) {
                        datab = Files.readAllBytes(Paths.get(folder + request));
                        //datab = cssinterp(datab);
                        write("Content-Type: text/css");
                    } else if (request.endsWith("ico")) {
                        datab = Files.readAllBytes(Paths.get(folder + request));
                        write("Content-Type: image/x-icon");
                    } else if (request.endsWith("svg")) {
                        datab = Files.readAllBytes(Paths.get(folder + request));
                        write("Content-Type: image/svg+xml");
                    } else if (request.equals("/index")){
                        datab = Files.readAllBytes(Paths.get(folder + "/index.html"));
                        write("Content-Type: text/html");
                    } else if (request.equals("/login")){
                        datab = Files.readAllBytes(Paths.get(folder + "/login.html"));
                        write("Content-Type: text/html");
                    } else if (request.equals("/signup")){
                        datab = Files.readAllBytes(Paths.get(folder + "/signup.html"));
                        write("Content-Type: text/html");
                    } else if (request.equals("/home")){
                        datab = Files.readAllBytes(Paths.get(folder + "/home.html"));
                        write("Content-Type: text/html");
                    } else if (request.startsWith("/study")){
                        if(request.contains("export.csv")){
                            System.out.println(request.split("/")[2]);
                            Study study = new Study(Integer.parseInt(request.split("/")[2]));
                            String File = study.exportSimilarChannels2CSV(Login.login("Nils","1234"));//"a, b, c\n1, 2, 3\n 4, 5, 6";
                            System.out.println(File);
                            datab = File.getBytes();
                            write("Content-Type: text/csv");
                        }else {
                            datab = Files.readAllBytes(Paths.get(folder + "/study.html"));
                            write("Content-Type: text/html");
                        }
                    } else if (request.equals("/account")){
                        datab = Files.readAllBytes(Paths.get(folder + "/account.html"));
                        write("Content-Type: text/html");
                    } else if (request.startsWith("/channel")){
                        datab = Files.readAllBytes(Paths.get(folder + "/channel.html"));
                        write("Content-Type: text/html");
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
    }
}
