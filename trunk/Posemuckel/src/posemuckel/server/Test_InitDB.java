package posemuckel.server;

import java.sql.SQLException;

import posemuckel.client.model.Project;
import junit.framework.TestCase;

/**
 * Dieser 'Test' initalisiert die Datenbank für die Tests auf einem laufenden
 * Server. Wenn hier etwas fehlschlägt, können nachfolgende Tests eventuell nicht
 * laufen.
 * 
 * @author Posemuckel Team
 *
 */
public class Test_InitDB extends TestCase {
	
	/**
	 * Testdatenbank
	 */
	private DB testDB;
	
	private String date;

	protected void setUp() throws Exception {
		testDB = new DB("root", "lifecycle", "localhost", "pose_test");
		//das Datum für die Projekte, die angelegt werden
		date = "2005-12-29";
	}
		
	/**
	 * Löscht einige Buddys aus der Datenbank
	 * @throws SQLException
	 */
	public void testDeleteBuddys() throws SQLException {
//		testDB.deleteBuddy("tiger", "another buddy");
//		testDB.deleteBuddy("tiger", "niko");
//		testDB.deleteBuddy("tiger", "aChatMember");
//		testDB.deleteBuddy("tiger", "a_friend");
//		testDB.deleteBuddy("tiger", "no_friend");
		testDB.deleteTable("buddies");
	}
	
	/**
	 * Löscht das Foldersystem des Projektes webtraceTest.
	 * @throws SQLException
	 */
	public void testDeleteFoldersystem() throws SQLException {
		testDB.deleteTable("ratings");
		testDB.deleteTable("user_urls");
		testDB.deleteTable("folders");
		testDB.deleteTable("url");
		//testDB.deleteProjectFolders("webtraceTest");
	}
	
	/**
	 * Löscht einige Projekte.
	 * @throws SQLException
	 */
	public void testDeleteProjects() throws SQLException {
		testDB.deleteTable("project_inviteduser");
		testDB.deleteTable("members");
		testDB.deleteTable("projects");
//		testDB.deleteProject("tiger");
//		testDB.deleteProject("Schule");
//		testDB.deleteProject("Uni");
//		testDB.deleteProject("Lernen");
//		testDB.deleteProject("vfb");
//		testDB.deleteProject("school");
//		testDB.deleteProject("invitationTest");
//		testDB.deleteProject("webtraceTest");
//		testDB.deleteProject("Bäume");
	}
	
	/**
	 * Löscht einige Anwender aus der Datenbank.
	 * @throws SQLException
	 */
	public void testDeleteUsers() throws SQLException {
		testDB.deleteTable("user_chat");
		testDB.deleteTable("chat");
		testDB.deleteTable("user");
//		testDB.deleteUser("apple");
//		testDB.deleteUser("niko");
//		testDB.deleteUser("tiger");
//		testDB.deleteUser("a_friend");
//		testDB.deleteUser("a buddy");
//		testDB.deleteUser("no_friend");
//		testDB.deleteUser("another buddy");
//		testDB.deleteUser("aChatMember");
//		testDB.deleteUser("lietaer");
	}
	
	/**
	 * Erstellt den Standardanwender für die meisten Tests.
	 * @throws SQLException
	 */
	public void testInitDefaultUser() throws SQLException {
		/*
		 * der Standardanwender für die meisten Testfälle
		 */
		addStandardUser("tiger", "tiger");
	}
	
	/**
	 * Initialisiert Daten für den TestCase ChatTest.
	 * @throws SQLException
	 */
	public void testChat() throws SQLException {
		//für ChatTest#testChat()
		addStandardUser("aChatMember", "pwd");				
		testDB.addBuddy("tiger", "aChatMember");
	}
	
	/**
	 * Initialisiert die Daten für die TestCase BuddyTests und
	 * ShowBuddyBug
	 * @throws SQLException
	 */
	public void testInitBuddys() throws SQLException {
		//für ShowBuddyBug#testAddBuddy()
		addStandardUser("another buddy", "pwd");
		/*
		 * Für den TestCase BuddyTests
		 */		
		//testAddBuddy() und ProfileTest#testSetProfile()
		addStandardUser("a_friend", "pwd");
		//testDeleteBuddy()
		addStandardUser("no_friend", "nopwd");
		testDB.addBuddy("tiger", "no_friend");
		//testGetProfile()
		addStandardUser("niko", "mac14");
		testDB.addBuddy("tiger", "niko");

	}
	
	/**
	 * Initialisiert Daten für den Testcase ProjectTest.
	 * @throws SQLException
	 */
	public void testInitProject() throws SQLException {
		testDB.deleteUser("wiede");
		addStandardUser("wiede","mann");
		//testJoinProjectOnFail()
		testDB.addProject("wiede", "Uni", Project.PRIVATE_TYPE, "3", "Seminararbeit",
				date, new String[0]);
		//testJoinAndOpenProject()
		testDB.addProject("wiede", "Lernen", Project.PUBLIC_TYPE, "3", "allgemeine Lerntips",
				date, new String[0]);
		//testLeaveProject()
		int id = testDB.addProject("wiede", "vfb", Project.PRIVATE_TYPE, "8", "birkenbihl",
				date, new String[0]);
		testDB.addUserToProject("tiger", ""+id);
	}
	
	/**
	 * Initialisiert Daten für den TestCase TestInvitations.
	 * @throws SQLException
	 */
	public void testInitInvitation() throws SQLException {
		//testRejectInvitation()
		int id = testDB.addProject("wiede", "Schule", Project.PUBLIC_TYPE, "8", "Hausaufgaben",
				date, new String[0]);
		testDB.addUserToProject("tiger", ""+id);
		//testOpenInvitations()
		String[] invited = {"tiger"};
		testDB.addProject("wiede", "invitationTest", Project.PRIVATE_TYPE, "8", "birkenbihl",
				date, invited);
		invited = new String[1];
		invited[0] = "tiger";
		testDB.addProject("wiede", "school", Project.PUBLIC_TYPE, "8", "birkenbihl",
				date, invited);
	}
	
	/**
	 * Initialisiert Daten für die TestCase WebtraceTest und FolderTest.
	 * @throws SQLException
	 */
	public void testInitWebtrace() throws SQLException {
		int id = testDB.addProject("wiede", "webtraceTest", Project.PRIVATE_TYPE, "8", "Hausaufgaben",
				date, new String[0]);
		String pID = id+"";
		testDB.addUserToProject("tiger", pID);
		testDB.addUserToProject("wiede", pID);
		testDB.addURL("tiger", pID, "www.webtrace1.de", "_test1", "");
		testDB.addURL("tiger", pID, "www.webtrace2.de", "_test2", "www.webtrace1.de");
		testDB.addURL("wiede", pID, "www.webtrace2.de", "_test2", "");
	}
	
	/**
	 * Hilfsmethode für die Testmethoden, die einen Standardbenutzer in
	 * die Datenbank schreibt.
	 * @throws SQLException
	 */
	private void addStandardUser(String nickname, String pass) throws SQLException {
		testDB.addUser("Christian", "Wiedemann", "christian@host.de",
				nickname, pass, "de", "male", "Oberguenzburg",
				"no comment", "localhost");
	}


}
