package posemuckel.server;

import java.util.Vector;

import junit.framework.TestCase;

public class ChatInfoTest extends TestCase {

	private ChatInfo Chat;
	private ClientInfo ci, ci2, ci3;
	
	protected void setUp() throws Exception {
		super.setUp();
		Chat = new ChatInfo("0", false);
		ci = new ClientInfo(null,"user");
		ci.setHash("hashi");
		ci.setStatus(ClientInfo.ClientStatus.ONLINE);
		ci2 = new ClientInfo(null,"user2");
		ci2.setHash("hashi2");
		ci2.setStatus(ClientInfo.ClientStatus.ONLINE);
		ci3 = new ClientInfo(null,"user3");
		ci3.setHash("hashi3");
		ci3.setStatus(ClientInfo.ClientStatus.ONLINE);
	}
	
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetID() {
		if ( !Chat.getID().equals("0") )
			fail("Fehler in ChatInfo.getID.");
	}
	
	public void testAddClient() {
		Chat.addClient(ci);
		if ( !Chat.isParticipant(ci) )
			fail("Fehler in ChatInfo.addClient oder ChatInfo.isParticipant.");
	}
	
	public void testDelClient() {
		Chat.addClient(ci);
		Chat.addClient(ci2);
		Chat.delClient(ci);
		if ( Chat.isParticipant(ci) )
			fail("Fehler in ChatInfo.delClient.");
		
	}
	
	public void testGetClients() {
		Chat.addClient(ci);
		Chat.addClient(ci2);
		Vector clients = Chat.getClients(ClientInfo.ClientStatus.ONLINE);
		if( !clients.contains(ci) || !clients.contains(ci2) || clients.contains(ci3) )	
			fail("Fehler in ChatInfo.getClients.");
	}

}
