package client;

import java.io.IOException;

import common.ChatIF;
import logic.Message;

public class ClientController implements ChatIF {

	public static int DEFAULT_PORT;
	ChatClient client;

	public ClientController(String host, int port) throws IOException {
		try {
			client = new ChatClient(host, port, this);
		} catch (IOException exception) {
			throw new IOException("Error: Cannot connect to the server at " + host + ":" + port + ". Reason: " + exception.getMessage());
		}
	}

	public void accept(String str) {
		//splits received message to seperate command and data values
		String[] MessageValues = str.split(":");
		//splits data string with "," to an array
		String[] MessageData = MessageValues[1].split(",");
		//set final message to client as command + data array
		Message msg = new Message(MessageValues[0],MessageData);
		//send msg
		client.handleMessageFromClientUI(msg);
	}

	/**displays messages in console**/
	public void display(String message) {
		System.out.println("> " + message);
	}
}