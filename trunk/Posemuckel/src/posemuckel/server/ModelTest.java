package posemuckel.server;

import java.util.Vector;

import junit.framework.TestCase;

public class ModelTest extends TestCase {

	ClientInfo ci1;
	ClientInfo ci2;
	ClientInfo ci3;
	ClientInfo ci4;
	ClientInfo ci5;
	ClientInfo ci6;
	
	ChatInfo ch1;
	ChatInfo ch2;
	
	ProjectInfo p1;
	ProjectInfo p2;	
	ProjectInfo p3;
	
	protected void setUp() throws Exception {
		super.setUp();
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
		
		ch1 = new ChatInfo("0", false);
		ch2 = new ChatInfo("1", false);
		
		p1 = new ProjectInfo("0");
		p1.setTopic("p1");
		p1.setMaxUsers(10);
		p1.add_client(ci1);
		p1.add_client(ci2);
		p1.add_client(ci3);
		p1.add_client(ci4);
		p2 = new ProjectInfo("1");
		p2.setTopic("p2");
		p2.setMaxUsers(20);
		p3 = new ProjectInfo("2");
		p3.setMaxUsers(30);
		p3.setTopic("p3");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	/*
	 * Test method for 'posemuckel.server.Model.getInstance()'
	 */
	public void testGetInstance() {
		Model mod1 = Model.getInstance();
		Model mod2 = Model.getInstance();
		if ( mod1 != mod2 )
			fail("Fehler beim Instanziieren der Singleton-Klasse.");
	}

	public void testAddDelClient() {
		Model mod1 = Model.getInstance();
		if ( !mod1.addClient(ci1) )
			fail("Hinzufügen des neuen Clients gescheitert.");
		mod1.delClient(ci1);
	}
	
	
	public void testAddDelChat() {
		Model mod1 = Model.getInstance();
		if ( !mod1.addChat(ch1) )
			fail("Hinzufügen des neuen Chats gescheitert.");
		mod1.delChat(ch1);
	}
	
	public void testGetClients() {
		Model mod1 = Model.getInstance();
		assertEquals(0, mod1.getClients(ClientInfo.ClientStatus.ONLINE).size());
		mod1.addClient(ci1);
		mod1.addClient(ci2);
		mod1.addClient(ci3);
		mod1.addClient(ci4);
		Vector<ClientInfo> clients = mod1.getClients(ClientInfo.ClientStatus.ONLINE);
		if (clients.size() != 3) {
			fail("Model.getClients liefert die falsche Zahl von Clients zurück.");
		}
	}
	
	public void testIsLoggedIn() {
		Model mod1 = Model.getInstance();
		if ( !mod1.isLoggedIn("LALA") ) 
			fail("Fehler in Model.isLoggedIn. Eingelogter Benutzer wird als nicht eingelogt erkannt.");
		if ( mod1.isLoggedIn("BLABLA") ) 
			fail("Fehler in Model.isLoggedIn. Nicht eingelogter Benutzer wird als eingelogt erkannt.");
		if  ( mod1.isLoggedIn("X") ) 
			fail("Fehler in Model.isLoggedIn. Nicht vorhandener Client-Hash wird einem Benutzer zugeordnet.");
	}
	
	public void testGetUser() {
		Model mod1 = Model.getInstance();
		String user = mod1.getUser("LALA");
		if ( user == null || !user.equals("Sandro") )
			fail("Fehler in Model.getUser");
		user = mod1.getUser("X");
		if ( user != null )
			fail("Fehler in Model.getUser");
	}
	
	public void testGetHashForNames() {
		Model mod1 = Model.getInstance();
		Vector<String> usernames = new Vector<String>();
		usernames.add("Sandro");
		usernames.add("Christian");
		usernames.add("Niemand");
		Vector<String> hashi = mod1.getHashForNames(usernames);
		if( !hashi.contains("LALA"))
			fail("Fehler in Model.getHashForNames. Eingelogter Benutzer wurde nicht erkannt.");
		if( hashi.size()>1 )
			fail("Fehler in Model.getHashForNames. Es wurden zu viele Benutzer dem Vektor hinzugefügt.");
	}
	
	public void testStatusByUsername() {
		Model mod1 = Model.getInstance();
		if ( mod1.statusByUsername("Sandro")
				!= ClientInfo.ClientStatus.ONLINE )
			fail("In Model.statusByUserame wurde der falsche Status zurückgegeben.");
		if ( mod1.statusByUsername("Tanja")
				!= ClientInfo.ClientStatus.OFFLINE )
			fail("In Model.statusByUserame wurde der falsche Status zurückgegeben.");
		if ( mod1.statusByUsername("NIEMAND")
				!= ClientInfo.ClientStatus.OFFLINE )
			fail("In Model.statusByUserame wurde der falsche Status zurückgegeben.");
	}
	
	public void testSelectedClients() {
		Model mod1 = Model.getInstance();
		Vector<String> query = new Vector<String>();
		query.add("LALA");
		Vector<ClientInfo> clients = mod1.selectedClients(query);
		for( ClientInfo client : clients ) {
			String user = client.getUserName();
			if ( user == null || !user.equals("Sandro") )
				fail("Fehler in Model.selectedClients");
		}
		query.clear();
		query.add("Xsadasd");
		clients = mod1.selectedClients(query);
		if(clients.size()>0)
			fail("Fehler in Model.selectedClients");
	}
	
	public void testchatmembers() {
		Model mod1 = Model.getInstance();
		mod1.addChat(ch1);
		mod1.addChat(ch2);
		mod1.add2Chat(ci1,"0");
		mod1.add2Chat(ci3,"0");
		Vector<ClientInfo> members = mod1.chatmembers("0");
		if( !members.contains(ci1) || !members.contains(ci3) || members.size() != 2 ) {
			fail("Fehler in Model.chatmembers");
		}
		mod1.removeFromChat(ci1,"0");
		members = mod1.chatmembers("0");
		if( !members.contains(ci3) || members.size() != 1 ) {
			fail("Fehler in Model.chatmembers");
		}
	}

	public void testGetCurrentProjectMembers() {
		Model mod1 = Model.getInstance();
		mod1.addClient(ci1);
		mod1.addClient(ci2);
		mod1.addClient(ci3);
		mod1.addClient(ci4);

		Vector<ClientInfo> members = mod1.getCurrentProjectMembers("LALA");
		
		if( !members.contains(ci1) || !members.contains(ci2) || !members.contains(ci3) || members.size() != 3 ) {
			fail("Fehler in Model.getCurrentProjectMembers");
		}
		mod1.delClient(ci1);
		mod1.delClient(ci2);
		mod1.delClient(ci3);
		mod1.delClient(ci4);
	}

}
