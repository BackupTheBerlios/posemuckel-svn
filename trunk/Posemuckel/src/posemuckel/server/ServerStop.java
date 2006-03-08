package posemuckel.server;


import junit.framework.TestCase;

/**
 * Räumt die in der Testsuite AllTestsOnRunningServer angelegten Daten wieder auf 
 * und fährt den Server herunter.
 * 
 * @author Posemuckel Team
 *
 */
public class ServerStop extends TestCase {
	
	protected void tearDown() throws Exception {
		super.tearDown();
		Server.terminate();
		DB testDB = new DB("root", "lifecycle", "localhost", "pose_test");
		testDB.deleteUser("tiger");
		testDB.deleteUser("apple");
		testDB.deleteUser("a_friend");
		testDB.deleteUser("lietaer");
		testDB.deleteUser("niko");
		
		testDB.deleteBuddy("tiger", "a_friend");
		testDB.deleteBuddy("tiger", "niko");
		testDB.deleteBuddy("tiger", "no_friend");
		testDB.deleteBuddy("tiger", "another_buddy");
		testDB.deleteProjectFolders("webtraceTest");
		testDB.deleteProject("tiger");
		testDB.deleteProject("Schule");
		testDB.deleteProject("Uni");
		testDB.deleteProject("Lernen");
		testDB.deleteProject("vfb");
		testDB.deleteProject("school");
		testDB.deleteProject("invitationTest");
		testDB.deleteProject("webtraceTest");
		testDB.deleteProject("Bäume");
		System.out.println("___________cleanup finished");
	}
		
	public void testStop() {
		try {
			//so was dauert etwas
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Server was closed");
	}

}
