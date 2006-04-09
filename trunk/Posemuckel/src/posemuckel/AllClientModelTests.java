package posemuckel;

import posemuckel.client.model.test.AllModelTests;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Führt alle Tests aus, die die Funktionalität des Models des Clients testen. 
 * Enthält die Tests aus zwei TestSuites:
 * <ul>
 * <li>AllModelTests</li>
 * <li>AllTestsOnRunningServer</li>
 * </ul>
 * 
 * <b>Damit die Tests laufen können, muss die Konfiguration aus 
 * AllTestsOnRunningServer verwendet werden!</b>
 * 
 * @see posemuckel.AllTestsOnRunningServer
 * @see posemuckel.client.model.test.AllModelTests
 * @author Posemuckel Team
 *
 */
public class AllClientModelTests {
	
	/**
	 * Erstellt einen JUnit-Test zum Testen der Funktionalität
	 * des Clients
	 * @return Test für die Client-Funktionen
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for posemuckel");
		//$JUnit-BEGIN$
		suite.addTest(AllModelTests.suite());
		suite.addTest(AllTestsOnRunningServer.suite());
		//$JUnit-END$
		return suite;
	}

}
