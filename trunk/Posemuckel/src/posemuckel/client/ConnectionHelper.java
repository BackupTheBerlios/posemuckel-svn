/**
 * 
 */
package posemuckel.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;

import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.net.Client;
import posemuckel.client.net.ClientConnection;
import posemuckel.client.net.ClientMessage;
import posemuckel.client.net.ServerMessage;

/**
 * Wird von den Tests zum Managen der Netzwerkverbindung des Clients verwendet.
 * @author Posemuckel Team
 *
 */
public class ConnectionHelper {
	
	private ClientConnection connection;
	
	/**
	 * Baut die Verbindung des Clients zum Server auf und initialisiert
	 * die Database neu, damit keine Ergebnisse aus vorhergehenden Tests
	 * Seiteneffekte erzeugen k&ouml;nnen.
	 */
	void startClient() {
		  Vector<String> sendqueue = new Vector<String>();
	      connection = ClientConnection.getInstance();
	      try {
			connection.openConnection("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
		} catch (InterruptedException e) {
		} 

	      ClientMessage sender = new ClientMessage(sendqueue);
	      ServerMessage serverMessage = new ServerMessage();
	      connection.setMessageHandler(serverMessage);
	      connection.setMessages(sender);
	      connection.openReader();
	      connection.openWriter(sendqueue);
	      Client.setConnection(connection);
	      //DatabaseSchnittstelle initialisieren
	      //die Initialisierung muss erfolgen, nachdem ServerMessage und ClientMessage
	      //initialisiert worden sind
	      DatabaseFactory.createRegistry(DatabaseFactory.USE_NETWORK);
	      connection.startConnection();
	}
	
	/**
	 * Schlie&szlig;t die Verbindung vom Client zum Server.
	 *
	 */
	void stopClient() {
		connection.destroyConnection();
	}



}
