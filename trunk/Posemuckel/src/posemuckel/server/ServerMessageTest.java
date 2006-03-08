package posemuckel.server;

import java.util.Vector;

import junit.framework.TestCase;
import posemuckel.common.ClientHash;

public class ServerMessageTest extends TestCase {
	private ServerMessage sm;
	private Vector<Model.QueueItem> queue;
	private static final String hash = ClientHash.getClientHash("Lala");
	private static final String RFCVERSION = "1.17";
	ClientInfo ci1;
	ClientInfo ci2;
	ClientInfo ci3;
	ClientInfo ci4;
	ClientInfo ci5;
	ClientInfo ci6;
	Vector<ClientInfo> recievers;
	
	protected void setUp() throws Exception {
		super.setUp();
		queue  = Model.getInstance().getSendqueue();
		sm = new ServerMessage();
		recievers = new Vector<ClientInfo>();
		ci1 = new ClientInfo(null,"Sandro");
		ci1.setStatus(ClientInfo.ClientStatus.ONLINE);
		ci1.setHash("LALA");
		ci2 = new ClientInfo(null,"Lars");
		ci2.setStatus(ClientInfo.ClientStatus.ONLINE);
		ci3 = new ClientInfo(null,"Holger");
		ci3.setStatus(ClientInfo.ClientStatus.ONLINE);
		ci4 = new ClientInfo(null,"Christian");
		ci4.setStatus(ClientInfo.ClientStatus.OFFLINE);
		ci4.setHash("BLABLA");
		ci5 = new ClientInfo(null,"Tanja");
		ci5.setStatus(ClientInfo.ClientStatus.OFFLINE);
		ci6 = new ClientInfo(null,"Jens");
		ci6.setStatus(ClientInfo.ClientStatus.OFFLINE);
		
		recievers.add(ci1);
		recievers.add(ci2);
		recievers.add(ci3);
	}

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
			String res = ((Model.QueueItem)queue.firstElement()).message; 
			queue.remove(0);
			if ( 0 != res.compareTo(content) ) {
				//System.out.println(res);
				fail("Das "+packetname+" Paket ist nicht RFC 0815 "+RFCVERSION+" konform!");
			}
		}
	}
	
	
	/*
	 * Test method for 'posemuckel.server.ServerMessage.ack(String, String)'
	 */
	public void testAck() {
		sm.ack(recievers,"jens","id");
		queuedMessageTest("ACK","jens\r\nid\r\n0\r\nACK\r\n");
	}

	/*
	 * Test method for 'posemuckel.server.ServerMessage.user_exists(String, String)'
	 */
	public void testUser_exists() {
		sm.user_exists(recievers,"id");
		queuedMessageTest("USER_EXISTS","\r\nid\r\n0\r\nUSER_EXISTS\r\n");
	}

	/*
	 * Test method for 'posemuckel.server.ServerMessage.access_denied(String, String)'
	 */
	public void testAccess_denied() {
		sm.access_denied(recievers,hash,"id");
		queuedMessageTest("ACCESS_DENIED",hash+"\r\nid\r\n0\r\nACCESS_DENIED\r\n");
	}

	/*
	 * Test method for 'posemuckel.server.ServerMessage.access_granted(String, String)'
	 */
	public void testAccess_granted() {
		sm.access_granted(recievers,hash,"id");
		queuedMessageTest("ACCESS_GRANTED",hash+"\r\nid\r\n0\r\nACCESS_GRANTED\r\n");
	}

	/*
	 * Test method for 'posemuckel.server.ServerMessage.chat(String, String, String, String)'
	 */
	public void testChat() {
		sm.chat(recievers,"jens", "msgid","chatid", "Message");
		queuedMessageTest("CHAT","jens\r\nmsgid\r\n2\r\nCHAT\r\nchatid\r\nMessage\r\n");
	}

	/*
	 * Test method for 'posemuckel.server.ServerMessage.typing(String, String, String)'
	 */
	public void testTyping() {
		sm.typing(recievers, "jens", "msgid","chatid");
		queuedMessageTest("TYPING","jens\r\nmsgid\r\n1\r\nTYPING\r\nchatid\r\n");
	}

	/*
	 * Test method for 'posemuckel.server.ServerMessage.reading(String, String, String)'
	 */
	public void testReading() {
		sm.reading(recievers, "jens", "msgid","chatid");
		queuedMessageTest("READING","jens\r\nmsgid\r\n1\r\nREADING\r\nchatid\r\n");
	}

	
	/*
	 * Test method for 'posemuckel.server.ServerMessage.chat_members(String chatid, String[] members)'
	 */
	public void testChat_members() {
		String[] members = {"Anne","Peter","Hansi"};
		sm.chat_members(recievers, "0",members);
		queuedMessageTest("CHAT_MEMBERS","\r\n-1\r\n4\r\nCHAT_MEMBERS\r\n0\r\nAnne\r\nPeter\r\nHansi\r\n");
	}
	
	/*
	 * Test method for 'posemuckel.server.ServerMessage.newchat'
	 */
	public void testNewchat() {
		String[] idandmembers = {"chatID","Anne","Peter","Hansi"};
		sm.newchat(recievers,"0",idandmembers);
		queuedMessageTest("NEW_CHAT","\r\n0\r\n4\r\nNEW_CHAT\r\nchatID\r\nAnne\r\nPeter\r\nHansi\r\n");
	}
	
	/*
	 * Test method for 'posemuckel.server.ServerMessage.newchat'
	 */
	public void testVisiting() {
		String[] data = {"http://www.neueurl.xx","Titel der neuen Seite","http://www.alteseite.de"};
		sm.visiting(recievers,"0","Hansi",data[0],data[1],data[2]);
		queuedMessageTest("VISITING","Hansi\r\n0\r\n3\r\nVISITING\r\nhttp://www.neueurl.xx\r\nTitel der neuen Seite\r\nhttp://www.alteseite.de\r\n");
	}	
}
