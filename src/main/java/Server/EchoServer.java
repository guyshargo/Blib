package Server;

import java.io.IOException;

import gui.ServerGUI.ServerPortFrameController;
import logic.Message;
import logic.Subscriber;
import logic.subscriberLogic;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class EchoServer extends AbstractServer {
	private ServerPortFrameController serverGuiController;
	private String ip;
	private String hostName;

	// constructor
	public EchoServer(int port, ServerPortFrameController serverGuiController) {
		super(port);
		this.serverGuiController = serverGuiController;
	}

	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		// get object Message
		Message messageC = (Message) msg;
		String[] data = (String[])messageC.getData();
		subscriberLogic subLogic = new subscriberLogic();
		Subscriber sub = null;
		// switch loop using the provided command in msg
		switch (messageC.GetCommand()) {
		// case where command is ViewReaderCard
		case "ViewReaderCard":
			sub = subLogic.fetchSubscriberById(Integer.valueOf(data[0]));
			Message returnViewSub = new Message("viewReaderCard",sub);
			try {
				client.sendToClient(returnViewSub); // Send the subscriber details to the client
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		// case where command is updateReaderCardMember
		case "UpdateReaderCardMember":
			// Fetch the subscriber by ID
			sub = subLogic.fetchSubscriberById(Integer.valueOf(data[0]));
			// updated phone and email as part of the message
			String phone = data[1];
			String email = data[2];

			boolean isUpdated = subLogic.updateSubscriberContact(sub.getSubscriber_id(), phone, email);

			try {
				// Send back updated subscriber or a failure message
				if (isUpdated) {
					Message returnUpdatedSub = new Message("viewReaderCard",sub);
					client.sendToClient(returnUpdatedSub); // Send return subscriber message to the client
				} else {
					client.sendToClient(null);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		// case where command is quit
		case "Quit":
			try {
				Message quitUpdatedSub = new Message("Quit",null);
				client.sendToClient(quitUpdatedSub); // Send the updated subscriber details to the client
				ip = client.getInetAddress().getHostAddress();
				hostName = client.getInetAddress().getHostName();
				client.close(); // Close the client connection
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void clientConnected(ConnectionToClient client) {
		// Get the client's IP address and hostname and change connection status
		String ip = client.getInetAddress().getHostAddress();
		String hostName = client.getInetAddress().getHostName();
		if (serverGuiController != null) {
			serverGuiController.updateConnectionInfo(ip, hostName, "Connected");
		}
	}

	@Override
	public void clientDisconnected(ConnectionToClient client) {
		//client has disconnected so update connection status
		if (serverGuiController != null) {
			serverGuiController.updateConnectionInfo(ip, hostName, "Disconnected");
		}
	}

	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}
}