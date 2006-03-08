package posemuckel.common;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCommonTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for posemuckel.common");
		//$JUnit-BEGIN$
		suite.addTestSuite(Message_HandlerTest.class);
		suite.addTestSuite(MessageTest.class);
		suite.addTestSuite(ClientHashTest.class);
		suite.addTestSuite(GetTextTest.class);
		//$JUnit-END$
		return suite;
	}

}
