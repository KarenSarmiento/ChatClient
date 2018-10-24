package uk.ac.cam.ks828.fjava.tick1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class StringChat {
	public static void main(String[] args) {
		final String server;
		final int port;
		try {
			if (args.length == 2) {
				server = args[0];
				port = Integer.parseInt(args[1]);
				try {
					final Socket socket = new Socket(server, port);
					Thread output = new Thread() {
						@Override
						public void run() {
							//Read bytes from the socket, interpret them as string data and print
							//the resulting string data to the screen.
							try {
								byte[] serverInput = new byte[1024];
								while (true) {
									int bytesRead = socket.getInputStream().read(serverInput);
									String inputString = new String(serverInput, 0, bytesRead);
									System.out.println(inputString);
								}
							} catch (IOException ioe) {
								System.err.println("Cannot connect to " +  server + " on port " + port);
							}
						}
					};
					// Sets the thread to a daemon (background process). JVM can still exit despite
					// the running of such threads.
					output.setDaemon(true);
					// execute output.run() concurrently with the rest of the program.
					output.start();

					BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					byte[] userInput = new byte[1024];
					while(true) {
						//Read data from the user, blocking until ready. Convert the
						//string data from the user into an array of bytes and write
						//the array of bytes to "socket".
						userInput = r.readLine().getBytes();
						String inputString = new String(userInput, 0, userInput.length);
						out.println(inputString);
					}
				} catch (IOException ioe) {
					System.err.println("Cannot connect to " +  server + " on port " + port);
				}
			} else 
				System.err.println("This application requires two arguments: <machine> <port>");
		} catch (NumberFormatException e) {
			System.err.println("This application requires two arguments: <machine> <port>");
		}
	}
}
