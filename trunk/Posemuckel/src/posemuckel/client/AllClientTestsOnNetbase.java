package posemuckel.client;

import posemuckel.server.Test_InitDB;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Führt alle Tests aus, die die Schnittstelle von posemuckel.client.model
 * verwenden, um die Daten auf dem Server zu manipulieren. Diese Tests setzen
 * einen laufenden Server voraus. Die erste Testklasse ist kein 'richtiger'
 * JUnit-Test, da sie dazu dient, Testdaten in die Datenbank zu schreiben: Fehler
 * in dieser Testklasse können Folgefehler in den anderen Tests nach sich ziehen.
 * <br>
 * Es werden folgende Konfigurationen vorausgesetzt:
 * <ul>
 * <li>ein laufender Server</li>
 * <ul>
 * <li>IP: localhost</li> 
 * <li>Port: 8081</li>
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
 * @author Posemuckel Team
 *
 */
public class AllClientTestsOnNetbase {	

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for posemuckel.client");
		//$JUnit-BEGIN$	
		suite.addTestSuite(Test_InitDB.class);
		suite.addTestSuite(FolderTest.class);
		suite.addTestSuite(WebtraceTest.class);
		suite.addTestSuite(TestInvitations.class);
		suite.addTestSuite(ChatTest.class);
		suite.addTestSuite(ProfileTest.class);
		suite.addTestSuite(BuddyTests.class);
		suite.addTestSuite(ShowBuddyBug.class);
		suite.addTestSuite(ProjectTest.class);
		suite.addTestSuite(TestGetters.class);
		suite.addTestSuite(UserTest.class);
		//$JUnit-END$
		return suite;
	}
	
}
