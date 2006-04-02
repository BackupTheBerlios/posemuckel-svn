package posemuckel.server;

/**
 * Posemuckel Server
 * @author Posemuckel Team
 * @version 0.1
 */

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import posemuckel.common.Config;
import posemuckel.common.EnumsAndConstants;

/**
 * Die Hauptklasse des Servers. Hier wird der Server initialiert und
 * läuft solange, bis er von aussen beendet wird. Wenn sich ein neuer
 * Client mit dem Server verbindet, wird ein <code>ServerProcess<code>
 * für den Client erzeugt.
 * @author Posemuckel Team
 *
 */
public class Server {
	
	/**
	 * Version des Servers
	 */
    public static final String VERSION = "0.4.1";
  
    /**
     * Gibt an, ob der Server beendet ist
     */
    private static boolean terminated = false;
    
    /**
     * ServerSocket für Verbindungen mit den Clients
     */
    private static ServerSocket s;
  
    /**
     * Kommando für die Konfiguration
     */
    private static String configCommand;

    /**
     * Logt die Abläufe im Server
     */
    private static Logger logger;

    /**
     * Startet den Server mit den eingegebenen Parametern
     * @param args eingegebene Parameter
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
	    startServer(args);
    }
  
    /**
     * Beendet den Server
     *
     */
    public static synchronized void terminate() {
	    terminated = true;
	    try {
		    s.close();
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
    }
  
    /**
     * Gibt an, ob der Server beendet ist
     * @return true, falls der Server schon beendet ist, false sonst
     */
    public static synchronized boolean isTerminated() {
	    return terminated;
    }
  
    /**
     * Setzt das Konfigurationskommando über den angegebenen Pfad, in
     * dem die Konfigurationsdatei steht.
     * @param configFilePath Pfad der Konfigurationsdatei
     */
    public static void configure(String configFilePath) {
	    configCommand = "-c" + configFilePath;
    }
  
    /**
     * Startet den Server in einem separaten Thread. 
     * 
     * Die Konfigurationsdatei muss vor dem Starten des Servers mit 
     * Server#configure() angegeben werden
     */
    public static void startInThread() {
	    //starte den Server in einem separaten Thread
	    //wird zum Testen des Clients gebraucht!
	    Thread server = new Thread() {

		    public void run() {
			    try {
				    if(configCommand != null) {
					    Server.startServer(new String[]{configCommand});
				    }
			    } catch (UnknownHostException e) {
				    e.printStackTrace();
			    } catch (IOException e) {
				    e.printStackTrace();
			    }		
		    }		  
	    };
	    server.start();
    }
  
    /**
     * Gibt die Hilfe für die Eingabe der Parameter auf der Konsole aus.
     *
     */
    public static void printHelp() {
	    String out;
	    out = "Es stehen folgende Optionen zur Verfügung:"+EnumsAndConstants.LS;
	    out +="-c Konfigurationsdatei  Angabe des Pfades zur Konfiguration (erforderlich)."+EnumsAndConstants.LS;
	    out +="-a Hostadresse          Überschreibt den Hostnamen der Konfiguration."+EnumsAndConstants.LS;
	    out +="-v                      Gibt die Version aus."+EnumsAndConstants.LS;
	    System.out.println(out);
    }
  
    /**
     * Startet den Server mit den übergebenen Argumenten
     * @param args übergebene Argumente
     * @throws UnknownHostException
     * @throws IOException
     */
    public static void startServer(String[] args) throws UnknownHostException, IOException {
	    LongOpt[] longopts = new LongOpt[3];
	    longopts[0] = new LongOpt("configfile", LongOpt.REQUIRED_ARGUMENT, null, 'c');
	    longopts[1] = new LongOpt("address", LongOpt.REQUIRED_ARGUMENT, null, 'a'); 
	    longopts[2] = new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'v');
	  
	    Getopt g = new Getopt("posemuckel-server", args, "c:a:v", longopts);
	    int character;
	    String file = null;
	    String address = null;
	    while ((character = g.getopt()) != -1) {
	    	// Kommandozeilen-Parameter werden ausgelesen
	        switch(character) {
	             case 'c':
	                 file = g.getOptarg();
	                 if(file == null || file.equals("")) {
	            	     System.err.println("Das Argument -c erwartet einen Pfad zur Konfigurationsdatei.");
	            	     printHelp();
	            	     System.exit(-1);
	                 }
	                 break;
	             case 'a':
		             address = g.getOptarg();
		             if(address == null || address.equals("")) {
		            	 System.err.println("Das Argument -a erwartet einen Hostnamen.");
		            	 printHelp();
		            	 System.exit(-1);
		             }
		             break;
	             case 'v':
	        	   	 System.out.println("Posemuckel server version " + Server.VERSION);
	        	   	 System.exit(1);
	        	   	 break;
	             default:
	            	 System.err.println("Unbekannte Kommandozeilenoption.");
	           		 printHelp();
	           		 System.exit(-1);

	        }
	    }
        if (file == null) {
		    System.err.println("Es wurde keine Konfigurationdatei angegeben!");
		    printHelp();
		    System.exit(-1);
	    }
	  
	    Config config = Config.getInstance();
	    // Den Thread anwerfen, der die Nachrichten verschickt
	    SendMessage sm = new SendMessage();
	    Thread t1 = new Thread(sm);
	    t1.setName("SendMessage-Thread");
	    t1.start();
	 
	    try {
		    config.loadFromFile(file);
		    if (address == null)
	            address = config.getconfig("LISTEN_ADDRESS"); 
	    } catch (FileNotFoundException e) {
		    System.out.println("Die Konfigurationsdatei "+file+" konnte nicht gelesen werden!");
		    System.exit(-1);
	    } catch (IOException e) {
		    System.out.println("Fehler beim Lesen der Konfigurationsdatei!");
	    }
	   
	    // Richte den Logger ein:
	    logger = Logger.getLogger(Server.class.getName());
	    // Die Konfiguration des Loggers wird aus der
	    // Konfigurationsdatei des Servers gelesen:
	    PropertyConfigurator.configure(file);
	  
	    validate(config);
	  		
	    System.out.println("Waiting for Socket...");

	    // Den port holen:
	    int port = Integer.valueOf(config.getconfig("LISTEN_PORT")).intValue();
	    // Neuen ServerSocket öffnen
	    try {
	    	s = new ServerSocket(port, 0, InetAddress.getByName(address));
	    } catch (BindException e) {
	    	System.err.println("Could not bind to port number "+config.getconfig("LISTEN_PORT")+"!"+EnumsAndConstants.LS+"There might be another Server using this port."+EnumsAndConstants.LS+"That's why I'm exiting now...");
	    	logger.fatal("Could not bind to port number "+config.getconfig("LISTEN_PORT")+"!"+EnumsAndConstants.LS+"There might be another Server using this port."+EnumsAndConstants.LS+"That's why I'm exiting now...");
	    	System.exit(-1);
	    }
	    System.out.println("Posemuckel server v" + Server.VERSION +" started on " + s);
	    logger.info("Posemuckel server v" + Server.VERSION +" started on " + s);

	    // while: Läuft bis das Programm von aussen beendet wurde, oder
	    // terminate() aufgerufen wurde
	    while (!isTerminated()) {
	        Socket socket = s.accept(); // auf die Verbindung mit dem 
	                                    // Client warten
            // ein neuer Client wird erzeugt
	        ClientInfo c = new ClientInfo(socket, "unknown");
            // neuer ServerProcess wird erzeugt und gestartet
	        ServerProcess sProcess = new ServerProcess(c);
	        sProcess.start();
	    }
    }

    /**
     * Prüft, ob alle für den Server notwendigen
     * Konfigurationsparameter da sind.
     * Wenn nicht, wird mit einer Fehlermeldung abgebrochen.
     * Es wird jedoch nicht geprüft, ob die Werte sinnvoll sind.
     * @param config Konfiguration
     */
    private static void validate(Config config) {
	    String[] keys = { "SENDMAIL_CMD",
					    "DO_SENDMAIL",
					    "LISTEN_PORT",
					    "LISTEN_ADDRESS",
					    "DB_HOST",
					    "DB_NAME",
					    "DB_USER",
					    "DB_PASS"
					    };
	
        for (String key : keys ) {
		    String value = config.getconfig(key);
		    if( value == null ) {
			    if (logger != null)
				    logger.fatal("Der Wert "+key+" ist in der Konfiguration nicht definiert!");
			    else {
				    System.err.println("Der Wert "+key+" ist in der Konfiguration nicht definiert!");
			        System.exit(-1);
			    }
		    }
	    }	
    }
}
