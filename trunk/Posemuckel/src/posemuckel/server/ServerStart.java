package posemuckel.server;

import junit.framework.TestCase;

/**
 * ServerStart benötigt zum Starten des Servers eine Konfigurationsdatei. Diese
 * muss im Feld ServerStart#config manuell vor dem Starten der Testsuite gesetzt
 * werden, damit der Server im Test korrekt gestartet werden kann. Es wird der absolute
 * Pfad zu der Konfigurationsdatei erwartet.
 *  
 * @author Posemuckel Team
 *
 */
public class ServerStart extends TestCase {
	
	/**
	 * Der Pfad zur Konfigurationsdatei. Ohne diese Datei kann der Server
	 * nicht gestartet werden.
	 */
	private String config = "C:/lib/getopt/posemuckel_test_server.cfg";

	public void testStartServer() {
		Server.configure(config);
		Server.startInThread();
		System.out.println("server was started");
	}

}
