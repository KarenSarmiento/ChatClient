package uk.ac.cam.ks828.fjava.messages;

import java.io.Serializable;

/**
 Client -> Server
 Message written by a user is sent to the server.
  */
public class ChatMessage extends Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;

	public ChatMessage(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
