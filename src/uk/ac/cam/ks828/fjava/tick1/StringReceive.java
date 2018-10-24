package uk.ac.cam.ks828.fjava.tick1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class StringReceive {
    public static void main(String[] args) {
        String server = null;
        int port = 0;
        try {
            if (args.length == 2) {
                server = args[0];
                port = Integer.parseInt(args[1]);
            } else {
                System.err.println("This application requires two arguments: <machine> <port>");
                return;
            }
            receiveStrings(server, port);
        } catch (IOException ioe) {
            System.err.println("Cannot connect to " +  server + " on port " + port);
        } catch (NumberFormatException e) {
            System.err.println("This application requires two arguments: <machine> <port>");
        }
    }
    /**
     * Connects to the server using and instance of the socket class and does the
     * following in an infinite loop:
     * (1) Read bytes from the socket object
     * (2) Interpret the bytes returned as text
     * (3) Prints the text to console
     *
     * @param serverName name of server to receiveStrings to
     * @param portNumber number of port to conntect to
     */
    public static void receiveStrings(String serverName, int portNumber) throws IOException {
        try (
                Socket socket = new Socket(serverName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            byte[] input = new byte[1024];
            while (true) {
                int bytesRead = socket.getInputStream().read(input);
                String textString = new String(input, 0, bytesRead);
                System.out.println(textString);
            }
        }
    }
}
