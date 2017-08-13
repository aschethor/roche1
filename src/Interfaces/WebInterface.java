package Interfaces;

import Interfaces.ServerLib.HTTPServer;

/**
 * https server for graphical user interface
 */

public class WebInterface {
    private static final String directory = "C:\\Users\\wandeln\\IdeaProjects\\roche1\\src\\Website\\bootstrap-3.3.7-dist";
    public static void main(String... args){
        new HTTPServer(directory).start();
    }
    //TODO
}
