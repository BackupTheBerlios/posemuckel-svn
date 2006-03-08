package posemuckel.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import org.apache.log4j.Logger;

import posemuckel.common.Config;
import posemuckel.common.EnumsAndConstants;

/**
 * ClientConnection initialisiert und managt die Verbindung zum Server.
 * Diese Klasse ist ein Singleton, denn pro Client soll es genau eine
 * Netzwerkverbindung geben. In Membervariablen werden der socket
 * und weitere verbindungsabhängige Daten gehalten.<br>
 * 
 * @author Posemuckel Team
 */
public class ClientConnection {

	private int PORT = EnumsAndConstants.PORT;
	
	private Socket socket = null;
	private String host;
	private RecvMessage reader;
	private SendMessage writer;
	private InetAddress addr;
	/*
	 * die zwei sind erst einmal temporär hier, um statische Referenzen aufzulösen
	 */
	private ServerMessage handler;
	private ClientMessage messages;
	
	private Thread readerT;
	private Thread writerT;

	private Config config;
	private static ClientConnection Instance = null;
	private static Logger logger = Logger.getLogger(ClientConnection.class);
	
	/**
	 * ClientConnection ist ein Singelton.
	 */
	private ClientConnection() {
	}
	
	/**
	 * Gibt die einzige Instanz von ClientConnection aus.
	 * @return die einzige Instanz von ClientConnection
	 */
	public static ClientConnection getInstance(){
		if( Instance == null ) {
			Instance = new ClientConnection();
		}
		return Instance;
	}
	
	
	/**
	 * Öffnet die Verbindung zum Server. Dabei werden die Verbindungsdaten
	 * wie Server-Host und Server-Post aus der globalen Konfiguration des
	 * Client geholt, die in einer entsprechenden lokalen Konfigurationsdatei
	 * gespeichert ist. Die geworfenen Exceptions geben Hinweise auf Fehler
	 * beim Verbindungsaufbau.
	 * <p>
	 * @throws IOException : server not ready
	 * @throws UnknownHostException : host does not exist
	 * @throws InterruptedException : program interrupted
	 */
	public void openConnection() throws IOException,
			UnknownHostException, InterruptedException {
		Client.setClientHash("");
		config = Config.getInstance();
		
		String stdServer = config.getconfig("DEFAULT_SERVER");
		// Versuche, die Server-IP zu holen:
		String ip = config.getconfig("SERVER_"+stdServer+"_HOST");
		if(ip == null) return; // Keiner weiß, welchen Server wir suchen
		// Versuche, die Server-IP zu holen:
		String port = config.getconfig("SERVER_"+stdServer+"_PORT");
		if(port == null) {
			// Versuche den Standard-Port 8081:
			config.setconfig("SERVER_PORT",String.valueOf(PORT));
			config.saveToFile(EnumsAndConstants.CLIENT_CONFIG_FILE);
		} else {
			PORT = Integer.parseInt(port);
		}
			
		// open connection to given IP-Adress
		addr = InetAddress.getByName(ip);
		logger.info("trying connection to " + addr);

		// open socket to server given by addr on Port Client.PORT
		socket = new Socket(addr, PORT);
		this.host = ip;
		logger.info("connected to " + addr);
	}
	
	/**
	 * Schliesst die Verbindung zum Server um sie wieder zu öffnen.
	 * Diese Methode wird benötigt, wenn die Verbindung zu Server
	 * unterbrochen wird.
	 */
	public void reopenConnection() {
		closeConnection();
		try {
			socket = new Socket(host, PORT);
			reader.setSocket(socket);
			writer.setSocket(socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		startConnection();		
	}
	
	/**
	 * Schliesst den Socket zum Server und
	 * unterbricht die Netzwerkthreads.
	 *
	 */
	public void destroyConnection() {
		try {
			if( socket != null )
				socket.close();
		} catch (IOException e1) {
			//e1.printStackTrace();
		}
		while(writerT.isAlive() || readerT.isAlive()) {
			try {
				interruptThreads();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Initialisiert den Empfänger der Servernachrichten.
	 */
	public void openReader() {
		reader = new RecvMessage(handler);
		reader.setSocket(socket);
	}
	
	/**
	 * Initialisiert den Sender zum Server.
	 * @param sendqueue ist die Warteschlange, aus der die zu sendenden 
	 * Nachrichten entnommen werden
	 */
	public void openWriter(Vector sendqueue) {
		writer = new SendMessage(sendqueue);
		writer.setSocket(socket);
	}
	
	/**
	 * Startet die an der Netzwerkverbindung beteiligten Threads und bringt
	 * damit die Verbindung zum Server zum Laufen
	 * Die Threads werden im ThreadLauncher als Referenz gemerkt,
	 * damit sie aus jeden beliebigen Programmteil beendet werden
	 * können.
	 */
	public void startConnection() {
	    writerT = new Thread(writer);
	    readerT = new Thread(reader);
	    writerT.setName("Sender-Thread");
	    readerT.setName("Reader-Thread");
	    ThreadLauncher.getInstance().startThread(writerT);
	    ThreadLauncher.getInstance().startThread(readerT);
	}
	
	/**
	 * Unterbricht die an der Verbindung beteiligten Threads.
	 */
	protected void interruptThreads() {
		if ( writerT != null )
			writerT.interrupt();
		if ( readerT != null )
			readerT.interrupt();
	}
	
	/**
	 * Gibt an, ob eine Verbindung zum Server besteht.
	 * @return true, falls eine Verbindung zum Server besteht
	 */
	protected boolean isOpen() {
		if( socket == null )
			return false;
		return socket.isConnected();
	}
	
	/**
	 * Schliesst die Verbindung zum Server und beendet die Threads der 
	 * Netzwerkverbindung.
	 *
	 */
	public void closeConnection() {
		interruptThreads();
		if ( socket == null )
			return;
		while (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gibt den verwendeten Socket aus.
	 * @return verwendeter Socket
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Gibt den MessageHandler für vom Server kommende Nachrichten aus.
	 * @return MessageHandler für Nachrichten vom Server
	 */
	public ServerMessage getMessageHandler() {
		return handler;
	}
	
	/**
	 * Setzt den MessageHandler für vom Server kommende Nachrichten.
	 * @param handler MessageHandler für Nachrichten vom Server
	 */
	public void setMessageHandler(ServerMessage handler) {
		this.handler = handler;
	}

	/**
	 * Gibt die Instanz zum Kodieren der Nachrichten, die an den Server 
	 * gesendet werden, aus.
	 * @return Gibt die Instanz zum Formatieren der Nachrichten aus.
	 */
	public ClientMessage getMessages() {
		return messages;
	}

	/**
	 * @param messages Setzt den messages 
	 */
	public void setMessages(ClientMessage messages) {
		this.messages = messages;
	}

	/**
	 * Liefert den übergebenen Hostnamen zurück.
	 * @return der Hostname
	 */
	public String getHost() {
		return addr.getHostName();
	}

	/**
	 * Liefert die IP-Adresse zusammen mit dem Port.
	 * @return IP-Adresse <b>mit </b> Port
	 */
	public String getIP() {
		return addr.getHostAddress()+":"+String.valueOf(PORT);
	}
	
	/**
	 * Öffnet die Verbindung zum Server und trägt die standard-Verbindungsdaten in die
	 * Konfigurationsdatei ein. Es wird der Port 8081 verwendet.
	 * @param ip IP des Servers
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void openConnection(String ip) throws UnknownHostException, IOException, InterruptedException {
		config = Config.getInstance();
		config.setconfig("DEFAULT_SERVER","1");
		config.setconfig("SERVER_1_HOST",ip);
		config.setconfig("SERVER_1_PORT","8081");
		openConnection();
	}



}
