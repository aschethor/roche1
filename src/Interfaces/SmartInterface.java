package Interfaces;

import StudyMe.Account;
import StudyMe.Channel;
import StudyMe.Sample;

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
                Account account = Account.getAccountByUsername(username);
                String password = in.nextLine();
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
                        Vector<Sample> samples = channel.getSamples(account);
                        for(Sample s:samples){
                            out.println(""+s.getTime());
                            out.println(""+s.getValue());
                        }
                        out.println("end");
                    }else if(option.equals("insert into channel")){
                        int channelId = Integer.parseInt(in.nextLine());
                        while(true) {
                            Channel channel = new Channel(channelId);
                            String time = in.nextLine();
                            if(time.equals("end"))
                            channel.createSample(account, time, Double.parseDouble(in.nextLine()));
                        }
                    }else{
                        out.println("wrong option!");
                    }
                    out.flush();
                }
                System.out.println("Connection with "+username+" ended");
                socket.close();
            }catch (Exception e){
                System.out.println("Connection with "+username+" abruptly ended");
                //e.printStackTrace();
            }
        }
    }
}