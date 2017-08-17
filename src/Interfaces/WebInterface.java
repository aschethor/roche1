package Interfaces;

import Interfaces.ServerLib.HTTPServer;
import Interfaces.ServerLib.WSServer;

/**
 * https server for graphical user interface
 */

public class WebInterface {
    //private static final String directory = "C:\\Users\\NiWa\\IdeaProjects\\roche1\\src\\Website\\bootstrap-3.3.7-dist";
    private static final String directory = "C:\\Users\\wandeln\\IdeaProjects\\roche1\\src\\Website";

    public static void main(String... args){
        new HTTPServer(directory){
            @Override
            public WSServer.WebSocket getWS() {
                return new WebSocket();
            }
        }.start();
    }
}
