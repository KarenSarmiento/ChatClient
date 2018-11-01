package uk.ac.cam.ks828.fjava.tick2;

import uk.ac.cam.cl.fjava.messages.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

@FurtherJavaPreamble(
        author = "Karen Sarmiento",
        date = "1st November 2018",
        crsid = "ks828",
        summary = "Create chat client which serialises and deserialises Java objects.",
        ticker = FurtherJavaPreamble.Ticker.A
)
public class ChatClient {
    public static void main(String[] args) {
        final String server;
        final int port;
        if (args.length != 2) {
            System.err.println("This application requires two arguments: <machine> <port>");
            return;
        }
        try {
            server = args[0];
            port = Integer.parseInt(args[1]);
            try {
                final Socket socket = new Socket(server, port);
                final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

                // Welcome user
                System.out.println(sdfTime.format(new Date()) + " [Client] Connected to " + server + " on port " +
                        port + ".");

                Thread output = new Thread() {
                    @Override
                    public void run() {
                        try {
                            // Read message objects received from server
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                            DynamicObjectInputStream objectInputStream = new DynamicObjectInputStream(bufferedInputStream);
                            while (true) {
                                Message message = (Message) objectInputStream.readObject();
                                if (message instanceof StatusMessage) {
                                    StatusMessage statusMessage = (StatusMessage) message;
                                    System.out.println(sdfTime.format(statusMessage.getCreationTime()) + " [Server] " +
                                            statusMessage.getMessage());
                                } else if (message instanceof RelayMessage) {
                                    RelayMessage relayMessage = (RelayMessage) message;
                                    System.out.println(sdfTime.format(relayMessage.getCreationTime()) + " [" +
                                            relayMessage.getFrom() + "] " + relayMessage.getMessage());
                                } else if (message instanceof NewMessageType) {
                                    // If unknown message type, add to DynamicObjectInputStream.
                                    // This makes the new class available to the JVM class loader,
                                    // allowing it to be able to be serialised/deserialised.
                                    NewMessageType newMessage = (NewMessageType) message;
                                    objectInputStream.addClass(newMessage.getName(), newMessage.getClassData());
                                    System.out.println(sdfTime.format(newMessage.getCreationTime()) +
                                            " [Client] New class " + newMessage.getName() + " loaded.");
                                } else {
                                    // Print contents of fields
                                    Class<?> messageClass = message.getClass();
                                    String output = sdfTime.format(message.getCreationTime()) +
                                            " [Client] " + messageClass.getSimpleName() + ": ";
                                    Field[] fields = messageClass.getDeclaredFields();
                                    for (int i = 0; i < fields.length; i++) {
                                        fields[i].setAccessible(true);
                                        output +=  fields[i].getName() + "(" + fields[i].get(message) + ")";
                                        if (i != fields.length-1)
                                            output += ", ";
                                    }
                                    System.out.println(output);
                                    // Invoke methods annotated as @Execute and have no parameters.
                                    for (Method method : messageClass.getDeclaredMethods()) {
                                        if (method.isAnnotationPresent(Execute.class)
                                                && method.getParameterTypes().length == 0) {
                                            method.invoke(message);
                                        }
                                    }
                                }
                            }
                        } catch (IOException ioe) {
                            System.err.println("Cannot connect to " +  server + " on port " + port);
                        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                };
                output.setDaemon(true);
                output.start();

                // Read typed user input and send to chat server
                BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                byte[] userInput = new byte[1024];
                while(true) {
                    userInput = r.readLine().getBytes();
                    String inputString = new String(userInput, 0, userInput.length);
                    if (inputString.startsWith("\\")) {
                        String[] split = inputString.split(" ");
                        switch (split[0].substring(1)) {
                            case "nick":
                                ChangeNickMessage nickMessage = new ChangeNickMessage(split[1]);
                                out.writeObject(nickMessage);
                                break;
                            case "quit":
                                System.out.println(sdfTime.format(new Date()) + " [Client] Connection terminated.");
                                return;
                            default:
                                System.out.println(sdfTime.format(new Date()) + " [Client] Unknown command \"" +
                                        split[0].substring(1) + "\"");
                                break;
                        }
                    } else {
                        ChatMessage chatMessage = new ChatMessage(inputString);
                        out.writeObject(chatMessage);
                    }
                }
            } catch (IOException ioe) {
                System.err.println("Cannot connect to " +  server + " on port " + port);
            }
        } catch (NumberFormatException e) {
            System.err.println("This application requires two arguments: <machine> <port>");
        }
    }
}
