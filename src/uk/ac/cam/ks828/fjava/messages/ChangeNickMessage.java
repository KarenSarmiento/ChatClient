package uk.ac.cam.ks828.fjava.messages;

import java.io.Serializable;

/**
 Client -> Server
 Update nickname of the client stored by the server.
  */
public class ChangeNickMessage extends Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String name;

	public ChangeNickMessage(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
