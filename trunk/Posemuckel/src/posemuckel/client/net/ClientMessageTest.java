package posemuckel.client.net;

import java.util.Vector;

import posemuckel.common.ClientHash;
import posemuckel.common.EnumsAndConstants;

import junit.framework.TestCase;


/**
 * Dieser Test überprüft, ob die Pakete dem RFC entsprechen.
 * 
 * @author Posemuckel Team
 */

public class ClientMessageTest extends TestCase {

	private ClientMessage cm;
	private Vector<String> queue;
	private static final String hash = ClientHash.getClientHash("Lala");
	private static final String RFCVERSION = "1.6";
	
	/**
	 * Schafft die Voraussetzungen für den Test.
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		Client.setClientHash(hash);
		queue  = new Vector<String>();
		cm = new ClientMessage(queue);
	}
	
	/**
	 * Räumt auf.
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 *  Wird zum Testen der Pakete genutzt:
	 *  Diese Methode prüft, ob in der Warteschlange ein Paket mit
	 *  gewünschtem Inhalt ist.
	 *  
	 *  @param packetname Name des Paketes, z.B. LOGIN. Wird für Fehlerausgaben benötgt.
	 *  @param content Der Inhalt des korrekten Paketes nach RFC.
	 */
	private void queuedMessageTest(String packetname, String content) {
		if ( queue.isEmpty() ) {
			fail("Fehler beim Hinzufügen von Paketen in die Warteschlange.");
		} else {
			assertEquals("Das "+packetname+" Paket ist nicht RFC 0815 "+RFCVERSION+" konform!", content, queue.firstElement());
		}
	}
	
	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.register(String, String, String, String, String, String, String, String, String)'
	 */
	public void testRegister() {
		cm.register("1", "Hans","Schmidt","hs@posemuckel.xx","hansischmidt","kirschkuchen","de","MALE",null,null);
		queuedMessageTest("REGISTER","\r\n1\r\n9\r\nREGISTER\r\nHans\r\nSchmidt\r\nhs@posemuckel.xx\r\nhansischmidt\r\nkirschkuchen\r\nde\r\nMALE\r\n\r\n\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.login(String, String)'
	 */
	public void testLogin() {
		cm.login("1", "Peter","Spocht");
		queuedMessageTest("LOGIN","\r\n1\r\n2\r\nLOGIN\r\nPeter\r\nSpocht\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.addBuddy(String)'
	 */
	public void testAddBuddy() {
		cm.addBuddy("1", "Peter");
		queuedMessageTest("ADD_BUDDY",hash+"\r\n1\r\n1\r\nADD_BUDDY\r\nPeter\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.delBuddy(String)'
	 */
	public void testDelBuddy() {
		cm.delBuddy("1", "Peter");
		queuedMessageTest("DEL_BUDDY",hash+"\r\n1\r\n1\r\nDEL_BUDDY\r\nPeter\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.myBuddies()'
	 */
	public void testMyBuddies() {
		cm.myBuddies("1");
		queuedMessageTest("MY_BUDDY",hash+"\r\n1\r\n0\r\nMY_BUDDIES\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.startProject(String, boolean, int, String[])'
	 */
	public void testStartProject() {
		String[] users = {"Angy","Münti","Stoibi","Gerdchen"};
		cm.startProject("1", "HILFE","description", false,10+"",users);
		queuedMessageTest("START_PROJECT",hash+"\r\n1\r\n8\r\nSTART_PROJECT\r\nHILFE\r\n0\r\n10\r\ndescription\r\nAngy\r\nMünti\r\nStoibi\r\nGerdchen\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.joinProject(String)'
	 */
	public void testJoinProject() {
		cm.joinProject("1", "ID");
		queuedMessageTest("JOIN_PROJECT",hash+"\r\n1\r\n1\r\nJOIN_PROJECT\r\nID\r\n");		
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.leaveProject(String)'
	 */
	public void testLeaveProject() {
		cm.leaveProject("1", "ID");
		queuedMessageTest("LEAVE_PROJECT",hash+"\r\n1\r\n1\r\nLEAVE_PROJECT\r\nID\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.myProjects()'
	 */
	public void testMyProjects() {
		cm.myProjects("1");
		queuedMessageTest("MY_PROJECTS",hash+"\r\n1\r\n0\r\nMY_PROJECTS\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.chat(String, String)'
	 */
	public void testChat() {
		cm.chat("1", "ID","Blabla");
		queuedMessageTest("CHAT",hash+"\r\n1\r\n2\r\nCHAT\r\nID\r\nBlabla\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.startChat(boolean, String[])'
	 */
	public void testStartChat() {
		String[] users = {"Angy","Münti","Stoibi","Gerdchen"};
		cm.startChat("1", false,users);
		queuedMessageTest("START_CHAT",hash+"\r\n1\r\n5\r\nSTART_CHAT\r\n0\r\nAngy\r\nMünti\r\nStoibi\r\nGerdchen\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.chat(String, String)'
	 */
	public void testJoinChat() {
		cm.joinChat("1", "ID");
		queuedMessageTest("JOIN_CHAT",hash+"\r\n1\r\n1\r\nJOIN_CHAT\r\nID\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.typing(String)'
	 */
	public void testTyping() {
		cm.typing("1", "ID");
		queuedMessageTest("TYPING",hash+"\r\n1\r\n1\r\nTYPING\r\nID\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.reading(String)'
	 */
	public void testReading() {
		cm.reading("1", "ID");
		queuedMessageTest("READING",hash+"\r\n1\r\n1\r\nREADING\r\nID\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.logout()'
	 */
	public void testLogout() {
		cm.logout("1");
		queuedMessageTest("LOGOUT",hash+"\r\n1\r\n0\r\nLOGOUT\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.getProjects()'
	 */
	public void testGetProjects() {
		cm.getProjects("1");
		queuedMessageTest("GET_PROJECTS",hash+"\r\n1\r\n0\r\nGET_PROJECTS\r\n");
	}

	/**
	 * Test method for 'posemuckel.client.net.ClientMessage.getUsers()'
	 */
	public void testSearchUsers() {
		cm.searchUsers("1","*eter*","*","*",EnumsAndConstants.LANG[0],EnumsAndConstants.GENDER[0],"*");
		queuedMessageTest("GET_USERS",hash+"\r\n1\r\n6\r\nSEARCH_USERS\r\n*eter*\r\n*\r\n*\r\n"+EnumsAndConstants.LANG[0]+"\r\n"+EnumsAndConstants.GENDER[0]+"\r\n*\r\n");
	}
	
}
