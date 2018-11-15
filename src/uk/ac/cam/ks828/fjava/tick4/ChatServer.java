package uk.ac.cam.ks828.fjava.tick4;

import uk.ac.cam.cl.fjava.messages.Message;
import uk.ac.cam.cl.fjava.messages.StatusMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String args[]) {
        if (args.length != 1) {
            System.err.println("Usage: java ChatServer <port>");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);
            MultiQueue<Message> multiQueue = new MultiQueue<>();
            while (true) {
                // Blocks until receives client connection request and accepts.
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, multiQueue);
            }

        } catch (NumberFormatException e) {
            System.err.println("Usage: java ChatServer <port>");
        } catch (IOException e) {
            System.err.println("Cannot use port number <port>");
        }
    }
}