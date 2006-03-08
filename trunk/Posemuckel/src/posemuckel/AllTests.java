package posemuckel;

import org.apache.log4j.BasicConfigurator;

import junit.framework.Test;
import junit.framework.TestSuite;
import posemuckel.client.model.test.AllModelTests;
import posemuckel.client.net.AllClientTests;
import posemuckel.common.AllCommonTests;
import posemuckel.server.AllServerTests;

/**
 * Führt alle Tests aus, die nicht auf einem laufenden Server arbeiten.
 * Enthält die Tests aus zwei TestSuites:
 * <ul>
 * <li>AllModelTests</li>
 * <li>AllClientTests</li>
 * <li>AllCommonTests</li>
 * <li>AllServerTests</li>
 * </ul>
 * 
 * @author Posemuckel Team
 *
 */
public class AllTests {
	
	public static Test suite() {
		BasicConfigurator.configure();
		TestSuite suite = new TestSuite("Test for posemuckel");
		//$JUnit-BEGIN$
		suite.addTest(AllModelTests.suite());
		suite.addTest(AllClientTests.suite());
		suite.addTest(AllCommonTests.suite());
		//gibt Konflikte mit dem ServerTest :-(
		//suite.addTest(AllTestsOnRunningServer.suite());
		suite.addTest(AllServerTests.suite());
		//$JUnit-END$
		return suite;
	}

}
