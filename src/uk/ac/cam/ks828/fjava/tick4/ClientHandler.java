package uk.ac.cam.ks828.fjava.tick4;

import java.io.*;
import java.net.Socket;
import java.util.Random;

import uk.ac.cam.cl.fjava.messages.*;

public class ClientHandler {
    private Socket socket;
    private MultiQueue<Message> multiQueue;
    private String nickname;
    private MessageQueue<Message> clientMessages;
    private Thread fromClient;
    private Thread toClient;

    private static Random rng = new Random();

    public ClientHandler(Socket s, MultiQueue<Message> q) {
        socket = s;
        multiQueue = q;
        clientMessages = new SafeMessageQueue<>();
        multiQueue.register(clientMessages);
        nickname = "Anonymous" + (rng.nextInt(9999) + 1);
        // Notify clients that a new client has entered the chat.
        StatusMessage newClientMessage = new StatusMessage("[Server] " +
                nickname + "connected from " + socket.getInetAddress().getHostName()
                + ".");
        multiQueue.put(newClientMessage);
        fromClient = new Thread() {
            @Override
            public void run() {
                try {
                    // Read message objects received from server
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                    ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
                    while (true) {
                        Message message = (Message) objectInputStream.readObject();
                        if (message instanceof ChangeNickMessage) {
                            ChangeNickMessage nickMessage = (ChangeNickMessage) message;
                            StatusMessage nameChangeMessage = new StatusMessage("[Server] " + nickname +
                                    " is now known as " + nickMessage.name + ".");
                            multiQueue.put(nameChangeMessage);
                            nickname = nickMessage.name;
                        } else if (message instanceof ChatMessage) {
                            ChatMessage chatMessage = (ChatMessage) message;
                            RelayMessage relayMessage = new RelayMessage(nickname, chatMessage);
                            multiQueue.put(relayMessage);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        fromClient.setDaemon(true);
        fromClient.start();

        toClient = new Thread() {
            @Override
            public void run () {
                try {
                    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    while (true) {
                        out.writeObject(clientMessages.take());
                    }
                } catch (IOException e) {
                    // Indicates that client has disconnected.
                    multiQueue.deregister(clientMessages);
                    StatusMessage exitMessage = new StatusMessage("[Server] " + nickname +
                            " has disconnected.");
                    multiQueue.put(exitMessage);
                    // TODO: Check that this terminates the threads.
                    fromClient.interrupt();
                    toClient.interrupt();
                }
            }
        };
        toClient.setDaemon(true);
        toClient.start();
    }

}
