package posemuckel.client.net;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Führt alle Tests aus dem package posemuckel.client.net aus. Es wird die 
 * Erzeugung von MessageIDs und die Formatierung der Nachrichten des Clients
 * getestet.
 * 
 * @author Posemuckel Team
 *
 */
public class AllClientTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for posemuckel.client");
		//$JUnit-BEGIN$
		suite.addTestSuite(MessageIDTest.class);
		suite.addTestSuite(ClientMessageTest.class);
		//$JUnit-END$
		return suite;
	}

}
