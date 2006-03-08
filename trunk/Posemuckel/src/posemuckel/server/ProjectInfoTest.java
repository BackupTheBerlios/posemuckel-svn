package posemuckel.server;

import java.util.Vector;

import posemuckel.common.ClientHash;

import junit.framework.TestCase;

public class ProjectInfoTest extends TestCase {

	ClientInfo ci1;
	ClientInfo ci2;
	ClientInfo ci3;
	ClientInfo ci4;
	ClientInfo ci5;
	ClientInfo ci6;
	
	ProjectInfo p1;
	ProjectInfo p2;	
	ProjectInfo p3;
	
	protected void setUp() throws Exception {
		super.setUp();
		ci1 = new ClientInfo(null,"Sandro");
		ci1.setStatus(ClientInfo.ClientStatus.ONLINE);
		ci2 = new ClientInfo(null,"Lars");
		ci2.setStatus(ClientInfo.ClientStatus.ONLINE);
		ci3 = new ClientInfo(null,"Holger");
		ci3.setStatus(ClientInfo.ClientStatus.ONLINE);
		ci4 = new ClientInfo(null,"Christian");
		ci4.setStatus(ClientInfo.ClientStatus.OFFLINE);
		ci5 = new ClientInfo(null,"Tanja");
		ci5.setStatus(ClientInfo.ClientStatus.OFFLINE);
		ci6 = new ClientInfo(null,"Jens");
		ci6.setStatus(ClientInfo.ClientStatus.OFFLINE);
		
		p1 = new ProjectInfo("1");
		p1.setTopic("p1");
		p1.setMaxUsers(10);
		p2 = new ProjectInfo("2");
		p2.setTopic("p2");
		p2.setMaxUsers(20);
		p3 = new ProjectInfo("3");
		p3.setMaxUsers(30);
		p3.setTopic("p3");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'posemuckel.server.ProjectInfo.setMaxUsers(int)'
	 */
//	public void testUserInMultipleProjects() {
//		p1.add_client(ci1);
//		p2.add_client(ci1);
//		Vector proj = ci1.getProjects();
//		//das Projekt ist eindeutig; es ist das letzte Projekt, zu dem der Client 
//		//hinzugefügt wurde
//		assertFalse("der Client sollte nur im zweiten Projekt registriert sein!", proj.contains((Object)p1));
//		assertTrue("der Client sollte im zweiten Projekt registriert sein!", proj.contains((Object)p2) );
//		if ( proj.contains((Object)p3) )
//			fail("Die Klasse ClientInfo hat einen Fehler bei der Verwaltung der Projekte.");
//	}

	/*
	 * Test method for 'posemuckel.server.ProjectInfo.getMaxUsers()'
	 */
	public void testGetOnlineClients() {
		p1.add_client(ci1);
		p1.add_client(ci2);
		p1.add_client(ci3);
		p1.add_client(ci4);		
		p1.add_client(ci5);
		p1.add_client(ci6);
		Vector onliners = p1.get_clients(ClientInfo.ClientStatus.ONLINE);
		if ( !onliners.contains((Object)ci1) || !onliners.contains((Object)ci2) || !onliners.contains((Object)ci3) )
			fail("Die Klasse ProjectInfo hat einen Fehler bei der Verwaltung der Clients.");		
		if ( onliners.contains((Object)ci4) || onliners.contains((Object)ci5) || onliners.contains((Object)ci6) )
			fail("Die Klasse ProjectInfo hat einen Fehler bei der Verwaltung der Clients.");		
	}

	/*
	 * Test method for 'posemuckel.server.ProjectInfo.setTopic(String)'
	 */
	public void testGet_client_by_hash() {
		p1.add_client(ci1);
		String hash = ClientHash.getClientHash("Sandro");
		ClientInfo ret = p1.get_client_by_hash(hash);
		if ( ret != ci1 )
			fail("Die Rückgabe des ClientInfos via get_client_by_hash funktioniert nicht.");
	}

}
