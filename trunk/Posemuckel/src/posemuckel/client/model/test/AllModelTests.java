package posemuckel.client.model.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import posemuckel.client.model.TestComparator;
import posemuckel.client.model.TestLoadWebtrace;
import posemuckel.client.model.TestWebpageComparator;
import posemuckel.client.model.TestWebtraceRoots;
import posemuckel.client.model.event.ListenerManagmentTest;

/**
 * Enthält die Tests aus den Packages 
 * 
 * <ul>
 * <li>posemuckel.client.model</li>
 * <li>posemuckel.client.model.event</li>
 * <li>posemuckel.client.model.test</li>
 * </ul>
 * @author Posemuckel Team
 *
 */
public class AllModelTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for posemuckel.client.model");
		//$JUnit-BEGIN$
		//posemuckel.client.model.test
		suite.addTestSuite(UserOnMockbaseTest.class);
		suite.addTestSuite(UserTest.class);
		suite.addTestSuite(UserOnBlockedBaseTest.class);
		suite.addTestSuite(BuddysOnMockBaseTest.class);
		suite.addTestSuite(GET_USERS_Test.class);
		suite.addTestSuite(ProjectTest.class);
		suite.addTestSuite(WebtraceTest.class);
		suite.addTestSuite(FolderTest.class);
		suite.addTestSuite(BuddysOnErrorBaseTest.class);
		suite.addTestSuite(TestNoLogin.class);
		suite.addTestSuite(ProjectComparatorTest.class);
		//posemuckel.client.model
		suite.addTestSuite(TestComparator.class);
		suite.addTestSuite(TestWebpageComparator.class);
		suite.addTestSuite(TestLoadWebtrace.class);
		suite.addTestSuite(TestWebtraceRoots.class);
		//posemuckel.client.model.event
		suite.addTestSuite(ListenerManagmentTest.class);
		//$JUnit-END$
		return suite;
	}

}
