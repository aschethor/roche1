package Interfaces;

import StudyMe.*;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

/**
 * simple web-server for smart devices (e.g. sensors) and analysis applications
 * be careful: this connection is not encrypted => only for use in intranet!
 */
public class SmartInterface {
    public static final int port = 1234;

    public static void main(String... args)throws Exception{
        System.out.println("Smart Interface");
        ServerSocket serverSocket = new ServerSocket(port);
        while(true)new Server(serverSocket.accept());
    }

    private static class Server extends Thread{
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        public Server(Socket socket)throws Exception{
            this.socket = socket;
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
            this.start();
        }

        @Override
        public void run() {
            String username="";
            try {
                username = in.nextLine();
                String password = in.nextLine();
                Account account = Login.login(username,password);
                if(!password.equals(account.getPassword())){
                    System.out.println("someone tried to connect as "+username);
                    out.println("wrong password!");
                    out.flush();
                    socket.close();
                    return;
                }
                System.out.println("new connection with "+username);
                while(true){
                    String option = in.nextLine();
                    if(option.equals("exit")){
                        break;
                    }else if(option.equals("read channel")){
                        System.out.print(username+" reads channel ");
                        int channelId = Integer.parseInt(in.nextLine());
                        System.out.println(""+channelId);
                        Channel channel = new Channel(channelId);
                        out.println(channel.getName(account));
                        out.println(channel.getUnit(account));
                        Vector<Sample> samples = channel.getSamples(account);
                        for(Sample s:samples){
                            out.println(""+s.getTime());
                            out.println(""+s.getValue());
                        }
                        out.println("end");
                    }else if(option.equals("insert into channel")){
                        System.out.print(username+" inserts into channel ");
                        int channelId = Integer.parseInt(in.nextLine());
                        System.out.println(""+channelId);
                        Channel channel = new Channel(channelId);
                        out.println(channel.getName(account));
                        out.println(channel.getUnit(account));
                        while(true) {
                            String time = in.nextLine();
                            if(time.equals("end"))break;
                            double value = Double.parseDouble(in.nextLine());
                            channel.createSample(account, time, value);
                            System.out.println("Sample created: "+time+" "+value);
                        }
                    }else if(option.equals("import study")) {
                        Study study = account.createStudy(in.nextLine());
                        study.setDescription(account,in.nextLine());
                        while (true){
                            option = in.nextLine();
                            if(option.equals("done"))break;
                            else if(option.equals("create tag")){
                                Tag tag = study.createTag(account,in.nextLine());
                                System.out.println("created tag: "+tag.getName(account));
                                out.println(tag.getId());
                                out.flush();
                            }else if(option.equals("link tags")){
                                study.linkTags(account,new Tag(Integer.parseInt(in.nextLine())),new Tag(Integer.parseInt(in.nextLine())));
                            }else if(option.equals("create channel")){
                                Channel channel = study.createChannel(account,in.nextLine());
                                out.println(channel.getId());
                                out.flush();
                                channel.setUnit(account,in.nextLine());
                                channel.setComment(account,in.nextLine());
                                while (true){
                                    option = in.nextLine();
                                    if(option.equals("end"))break;
                                    String value = in.nextLine();
                                    String comment = in.nextLine();
                                    try{
                                        channel.createSample(account,option,Double.parseDouble(value),comment);
                                    }catch(Exception e){
                                        //for NaN - responses
                                        channel.createSample(account,option,Double.NaN,comment);
                                        e.printStackTrace();
                                    }
                                }
                            }else if(option.equals("link channel")){
                                new Channel(Integer.parseInt(in.nextLine())).appendTag(account,new Tag(Integer.parseInt(in.nextLine())));
                            }
                        }
                    }else{
                        out.println("wrong option!");
                        out.flush();
                    }
                    out.println("bye");
                    out.flush();
                }
                System.out.println("Connection with "+username+" ended");
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Connection with "+username+" abruptly ended");
                //e.printStackTrace();
            }
        }
    }
}