package posemuckel;

import junit.framework.Test;
import junit.framework.TestSuite;
import posemuckel.client.AllClientTestsOnNetbase;
import posemuckel.server.ServerStart;
import posemuckel.server.ServerStop;

/**
 * hier werden alle Tests eingetragen, die &uuml;ber localhost auf den 
 * Server zugreifen; der Server wird zu Beginn in einem Thread gestartet
 * und am Ende der Testsuite wieder gestoppt. Die Reihenfolge der Tests kann
 * nur bedingt geändert werden: das Starten und Stoppen des Server sollte nicht
 * verschoben werden. 
 * Es werden folgende Konfigurationen vorausgesetzt:
 * <ul>
 * <li>Server-Konfigurationsdatei:</li>
 * <ul>
 * <li>welche?: posemuckel_test_server.cfg</li> 
 * <li>wo?: C:/lib/getopt/</li>
 * </ul>
 * <li>MySQL-Datenbank mit</li>
 * <ul>
 * <li>Datenbank: pose_test (siehe pose_test.sql)</li>
 * <li>User: root</li>
 * <li>Passwort: lifecycle</li>
 * <li>host: localhost</li>
 * </ul>
 * </ul>
 *
 * Der Pfad zur Konfigurationsdatei kann in ServerStart geändert werden. Um die
 * Datenbankkonfiguration zu ändern, muss sowohl die Konfigurationsdatei als
 * auch die Klasse Test_InitDB angepasst werden.
 * @see posemuckel.server.ServerStart#config
 * @see posemuckel.server.Test_InitDB#setUp()
 * @author Posemuckel Team
 *
 */
public class AllTestsOnRunningServer {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for posemuckel");
		//$JUnit-BEGIN$
		/*
		 * startet den Server und initialisiert die DB
		 */
		suite.addTestSuite(ServerStart.class);
		//tests beginnen
		suite.addTest(AllClientTestsOnNetbase.suite());
		//suite.addTestSuite(ServerTest.class);
		//tests enden
		/*
		 * stoppt den Server und räumt die DB wieder auf
		 */
		suite.addTestSuite(ServerStop.class);
		//$JUnit-END$
		return suite;
	}

}
