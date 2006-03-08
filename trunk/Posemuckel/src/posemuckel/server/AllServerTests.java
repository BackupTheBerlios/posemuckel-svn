package posemuckel.server;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllServerTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for posemuckel.server");
		//$JUnit-BEGIN$
		suite.addTestSuite(ModelTest.class);
		suite.addTestSuite(ProjectInfoTest.class);
		suite.addTestSuite(DBTest.class);
		//Der ServerTest in AllTestsOnRunningServer gewandert
		suite.addTestSuite(ClientMessageTest.class);
		suite.addTestSuite(ServerMessageTest.class);
		suite.addTestSuite(ChatInfoTest.class);
		//$JUnit-END$
		return suite;
	}

}
