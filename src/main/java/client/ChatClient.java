package client;

import java.io.IOException;

import com.sun.org.apache.bcel.internal.generic.SWITCH;
import common.ChatIF;
import logic.Message;
import logic.Subscriber;
import ocsf.client.AbstractClient;

public class ChatClient extends AbstractClient {
	ChatIF clientUI;
	public static boolean awaitResponse = false;
	public static Subscriber sub = null;
	
	//constructor
	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		openConnection();
	}
	//handles messages from client
	public void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		//if subscriber is not found
		if (msg == null) {
			sub = null;
			return;
		}
		Message messageFromServer = (Message) msg;
		switch (messageFromServer.GetCommand()){
			case "Quit":
				quit();
				break;
			case "viewReaderCard":
				sub = (Subscriber) messageFromServer.getData();
				break;
			default:
				System.out.println("Message type not recognized");
				break;
		}
	}
	
	public void handleMessageFromClientUI(Object message) {
		try {
			awaitResponse = true;
			sendToServer(message);
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		}
	}
	
	public void quit() {
		try {
			//close the connection
			closeConnection();
		} catch (IOException e) {
			//return to enter ip for server page
		}
	}
}