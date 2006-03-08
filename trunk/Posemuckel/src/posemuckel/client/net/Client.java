package posemuckel.client.net;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import posemuckel.client.gui.Colors;
import posemuckel.client.gui.GUI_Main_Window;
import posemuckel.client.gui.Messages;
import posemuckel.client.gui.browser.LabBrowserVariant2;
import posemuckel.client.gui.browser.TreeIcons;
import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.common.Config;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.ForkedWriter;
import posemuckel.common.GetText;
import posemuckel.common.Message_Handler;
import posemuckel.common.VerboseClientReader;
import posemuckel.common.VerboseServerReader;

/**
 * Dies ist die zentrale Klasse des Clients. Hier befindet sich die main
 * Methode, über die der Client gestartet wird. In einem Objekt der Klasse
 * ClientConnection werden die Verbindungsdaten gehalten. Die initiale
 * Konfiguration wird über eine Instanz der Klasse Config erhalten, wobei
 * im Zweifelsfall eine neue Konfigurationsdatei lokal angelegt wird.
 */
public class Client {
	
	/**
	 * Version des Meilensteins 
	 */
	protected final static String VERSION = "0.4";
	
	/*
	 * Verbindung zum Server
	 */
	private static ClientConnection connection;
	
	/*
	 * der Logger der Klasse
	 */
	private static Logger logger = Logger.getLogger(Client.class);

	/*
	 * steuert den Verlauf der Anwendung; wenn die Variable auf true gesetzt wird
	 * wird das n&auml;chste Fenster ge&ouml:ffnet. 
	 */
	private static boolean openNextWindow;
	
	private static boolean goToMain;
	
	private static boolean goToBrowser;
	
	private static boolean reopenConnection;
	
	private static boolean existsMain=false;

	private static boolean existsBrowser=false;
	
	private static GUI_Main_Window mainWindow;
	
	private static LabBrowserVariant2 browserWindow;
	
	/*
	 * f&uuml;r die Kommunikation mit dem Server unerl&auml;sslich
	 */
	private static String clientHash;
	
	private static String versionTitle;
	private static String versionText;
	/**
	 * checks command line for valid syntax and prints out version, if wished
	 * <p>
	 * @param args : String array containing command line
	 */
	private static void getConfiguration(String[] args) {
		Config config = Config.getInstance();
		 try {
			  config.loadFromFile(EnumsAndConstants.CLIENT_CONFIG_FILE);
		  } catch (FileNotFoundException e) {
			  System.out.println("creating new config file "+EnumsAndConstants.CLIENT_CONFIG_FILE);
		  } catch (IOException e) {
			  System.out.println("error while reading the config file");
		  }
		  if(args.length > 0) {
			  if (args[0].equals("-version")) {
				  Messages.showInfo(versionText + Client.VERSION, versionTitle);
				  System.exit(1);
			  }
		  }
	}

	/**
	 * Diese neue Main-Funktion startet die Login-GUI und
	 * initialisiert die Backend-Klassen. Im weiteren Verlauf des Programmes wird
	 * das &Ouml;ffnen der Fenster kontrolliert.
	 */
	public static void main(String[] args) {
		//TODO durch ein Konfigurationsfile ersetzen
		BasicConfigurator.configure();
		getConfiguration(args);
		VerboseServerReader.isVerbose(false);
		VerboseClientReader.isVerbose(true);
		Message_Handler.setDebugModus(false);
		ForkedWriter.printToStream(false);
		initDescriptions();
			reopenConnection = true;
			/*
			 * goToMain wird bei erfolgreichem Login von der GUI_Login_Group 
			 * auf true gesetzt
			 */
			openNextWindow = true;
			goToMain = true;
			//TODO aufräumen: wird die Schleife noch vollständig verwendet?
			while(openNextWindow) {
				if(goToMain) {
					goToMain = false;
					openNextWindow = false;
					existsBrowser=false;
					if (existsMain==false) {
						existsMain=true;
					}
					//ist in den Konstruktor von mainWindow gewandert
					//das Fenster muss ja noch geöffnet werden
					//mainWindow.setEnabledOpenProject(true);
					new GUI_Main_Window();
					logger.debug("main window was closed");
 				} else if(goToBrowser) {
					goToBrowser = false;
					openNextWindow = false;
					logger.debug("open Browser in MainMethod");
					LabBrowserVariant2 window = new LabBrowserVariant2(mainWindow.getShell());
					window.setBlockOnOpen(true);
					window.open();
				}
			}
			/*
			 * das Flag zurücksetzen, sonst wird eine Endlosschleife ausgeführt
			 */
			reopenConnection = false;
			/*
			 * was selbst allokiert wurde, muss auch entsorgt werden
			 */
			Colors.dispose();
			TreeIcons.disposeIcons();
		//der Code wird erst aufgerufen, wenn alle Fenster geschlossen sind
		ClientConnection.getInstance().closeConnection();
		//System.out.println("Application closed");
		ThreadLauncher.interruptAll();
	}
	
	/**
	 * Initialisiert die Netzwerkklassen und die Database auf Clientseite.
	 * Dabei werden in zwei Thread der Reader (RecvMessage) und der Sender
	 * (SendMessage) des Clients gestartet, die vom Socket lesen bzw. auf
	 * diesen schreiben.
	 */
	public static void initBackend() {
		Vector<String> sendqueue = new Vector<String>();
		//initialisiert die statische Variable connection
		connection = ClientConnection.getInstance();
		if(connection.isOpen())
			return;
		try {
			connection.openConnection();
		} catch (IOException e) { // no socket
			String noServer = GetText.gettext("NO_SERVER");
			String errorTitle = GetText.gettext("ERROR");
			Config config = Config.getInstance();
			String stdServer = config.getconfig("DEFAULT_SERVER");
			String Server = config.getconfig("SERVER_"+stdServer+"_HOST");
			noServer = GetText.macroreplace(noServer,"HOSTNAME",Server);
			InetAddress addr;
			try {
				addr = InetAddress.getByName(Server);
				noServer = GetText.macroreplace(noServer,"IPADDRESS",addr.toString());
			} catch (UnknownHostException e1) {
				noServer = GetText.macroreplace(noServer,"IPADDRESS","x.x.x.x");
			}

			Messages.showError(noServer, errorTitle);
		} catch (InterruptedException e3) { // connection has been interrupted
			String connectionInterrupted = GetText.gettext("CONNECTION_INTERRUPTED");
			String errorTitle = GetText.gettext("ERROR");
			Messages.showError(connectionInterrupted, errorTitle);
		}
		connection.setMessages(new ClientMessage(sendqueue));
		connection.setMessageHandler(new ServerMessage());
		connection.openReader();
		connection.openWriter(sendqueue);

		//DatabaseSchnittstelle initialisieren
		//die Initialisierung muss erfolgen, nachdem ServerMessage und ClientMessage
		//initialisiert worden sind
		DatabaseFactory.createRegistry(DatabaseFactory.USE_NETWORK);

		// Reader und Sender als Threads starten
		connection.startConnection();
	}
	
	/**
	 * Versucht, die Verbindung nach einer Unterbrechung erneut zu öffnene.
	 *
	 */
	public static void reopenConnection() {
		if(reopenConnection) {
			logger.info("connection should reopen");
			connection.reopenConnection();
			if(connection.isOpen()) {
				Model.getModel().getUser().login();
				if(Model.getModel().getOpenProject() != null) {
					Model.getModel().getOpenProject().open();
				}
				//TODO ReopenConnection: laufenden Chats wieder beitreten
			} else {
				reopenConnection = false;
			}
		}
	}
	
	/**
	 * Kontrolliert das WindowManagment: es soll das Hauptfenster geöffnet werden.
	 *
	 */
	public static void goToMain() {
		Client.openNextWindow = true;
		Client.goToMain = true;
	}
	
	/**
	 * Kontrolliert das WindowManagment: öffnet das Browserfenster.
	 *
	 */
	public static void goToBrowser() {
		if (existsBrowser==false) {
			existsBrowser=true;
			mainWindow.setEnabledOpenProject(false);
			browserWindow = new LabBrowserVariant2(null);
			browserWindow.setBlockOnOpen(true);
			browserWindow.open();
			//der Code wird erst ausgeführt, wenn das Browserfenster geschlossen wird
			//damit läßt sich dann ein weiteres Fenster öffnen
			existsBrowser = false;
		}
	}
	
	/**
	 * Schliesst das Browserfenster, falls dieses geöffnet wurde
	 *
	 */
	public static void closeBrowser() {
		if( browserWindow != null )
			browserWindow.close();
	}
	
	/**
	 * Teilt dem Client die Instanz von GUI_Main_Window mit, die geöffnet wurde.
	 * @param mw das Hauptfenster
	 */
	public static void setMainWindow(GUI_Main_Window mw) {
		mainWindow=mw;
	}
	
	
	/**
	 * Gibt an, ob eine Verbindung zum Server initialisiert wurde. Diese Methode 
	 * wird beim Debuggen der GUI verwendet.
	 * 
	 * @return true, falls eine ClientConnection initialisiert wurde
	 */
	public static boolean hasConnection() {
		if (Client.getConnection() == null)
			return false;
		return true;
	}
	
	/**
	 * Gibt den ClientHash f&uuml;r die aktuelle Session aus.
	 * 
	 * @return ClientHash
	 */
	protected static String getClientHash() {
		String clientHashString = String.valueOf(clientHash);
		return clientHashString;
	}
	
	/**
	 * Setzt den ClientHash f&uuml;r diese Session.
	 * 
	 * @param hash clientHash
	 */
	protected static void setClientHash(String hash) {
		clientHash = hash;
	}

	/**
	 * Gibt die ClientConnection, die vom Client verwendet wird, aus.
	 * Diese Methode sollte nur von der GUI aus verwendet werden! Das
	 * h&auml;lt uns die M&ouml;glichkeit offen, f&uuml;r Testzwecke zwei ClientConnections
	 * aufzubauen. Wenn in client.net eine Klasse die ClientConnection ben&ouml;tigt, 
	 * sollte sie durchgereicht werden
	 * 
	 * @return ClientConnection
	 */
	public static ClientConnection getConnection() {
		return connection;
	}
	
	/**
	 * Setzt die zu verwendende ClientConnection.
	 * @param connect ClientConnection
	 */
	public static void setConnection(ClientConnection connect) {
		connection = connect;
	}

	/**
	 * Gibt den Socket für die Verbindung zum Server aus.
	 * @return Socket zum Server
	 */
	protected Socket getSocket() {
		return connection.getSocket();
	}
	
	/**
	 * Initialisiert die Versionstexte.
	 *
	 */
	private static void initDescriptions() {
		GetText.setResourceName("posemuckel.lang.Messages");
		versionTitle = GetText.gettext("VERSION_TITLE");
		versionText = GetText.gettext("VERSION_TEXT");
	}
	
	/**
	 * Setzt das enabled-Flag für die OpenProjectAction: gibt an, ob der aktuelle
	 * Zustand des Clients das Öffnen von Projekten erlaubt
	 * @param enabled Flag für die OpenProjectAction
	 */
	public static void enableOpenProject(boolean enabled) {
		if(mainWindow != null && !mainWindow.getShell().isDisposed()) {
			mainWindow.setEnabledOpenProject(enabled);
		}
	}
	
	/**
	 * Gibt den Versionsstring aus.
	 * @return Versionsstring
	 */
	public static String getVersion() {
		return VERSION;
	}
}
