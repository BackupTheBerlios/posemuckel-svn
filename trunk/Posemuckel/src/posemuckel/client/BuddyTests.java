package posemuckel.client;

import lib.RetriedAssert;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.User;
import posemuckel.client.model.event.MemberListAdapter;
import posemuckel.client.model.event.MemberListEvent;

/**
 * In diesem Test wird die Funktionalität einer <code>MemberList</code>,
 * genauer gesagt einer BuddyListe, getestet. Der Test umfasst den gesamten 
 * Ablauf der Kommunikation zwischen Client und Server:<br\>
 * <ul>
 * <li>Modelklasse (Anforderung eines Update über das Netz)</li>
 * <li>Client (senden)</li>
 * <li>Server (empfangen, verarbeiten und senden)</li>
 * <li>Client (empfangen und parsen)</li>
 * <li>Modelklasse (update der Klasse)</li>
 * <li>Listener (benachrichtigen)</li>
 * </ul>
 * 
 * @author Posemuckel Team
 *
 */
public class BuddyTests extends TestComponents {

	private Model model;
	private User user;
	private ConnectionHelper connection;
	private TestListener listener;
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		/*
		 * f&uuml; jeden Test einzeln an und abschalten!
		 * damit lassen sich Probleme selektiv beheben
		 */
		Settings.setDubuggingMode(false, false, false, false);
		connection = new ConnectionHelper();
		connection.startClient();
		model = new Model();
		user = model.getUser();
		listener = new TestListener();
		if(Settings.debug)System.out.println("setUp finished");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		if(Settings.debug)System.out.println("tearDown started");
		super.tearDown();
		connection.stopClient();
		Settings.resetDebuggingMode();
	} 
	
	/**
	 * In diesem Test wird ein erfolgreiches Laden aller Buddys eines Users getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Buddyliste</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testGetBuddys() {
		if(Settings.debug)System.out.println("test: testGetBuddys");
		login(user);
		user.getBuddyList().addListener(listener);
		user.getBuddyList().load();
		try {
			new RetriedAssert(2*Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertFalse(user.getBuddyList().isEmpty());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logout(user);
	}
	
	/**
	 * In diesem Test wird ein Hinzuf&uuml;gen eines Buddys getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Buddyliste</li>
	 * <li>Hinzuf&uuml;gen eines Buddys</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testAddBuddy() {
		if(Settings.debug)System.out.println("test: testAddBuddys");
		login(user);
		user.getBuddyList().addListener(listener);
		user.getBuddyList().load();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertFalse(user.getBuddyList().isEmpty());
					assertNull(user.getBuddyList().getMember("a_friend"));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		listener.notified = false;
		user.getBuddyList().addBuddy("a_friend");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertNotNull(user.getBuddyList().getMember("a_friend"));
					assertEquals(Person.OFFLINE, user.getBuddyList().getMember("a_friend").getState());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//wir versuchen das auch zweimal
		user.getBuddyList().addBuddy("a_friend");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertNotNull(user.getBuddyList().getMember("a_friend"));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logout(user);
	}
	
	
	/**
	 * In diesem Test wird das L&ouml;schen eines Buddys getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Buddyliste</li>
	 * <li>L&ouml;schen eines Buddys</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testDeleteBuddy() {
		if(Settings.debug)System.out.println("test: testDeleteBuddys");
		login(user);
		user.getBuddyList().addListener(listener);
		user.getBuddyList().load();
		//warten, bis die Liste geladen ist
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertFalse(user.getBuddyList().isEmpty());
					assertNotNull(user.getBuddyList().getMember("no_friend"));
					assertEquals(Person.OFFLINE, user.getBuddyList().getMember("no_friend").getState());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		listener.notified = false;
		user.getBuddyList().deleteMember("no_friend");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertNull(user.getBuddyList().getMember("no_friend"));
					assertEquals(Person.UNKNOWN, model.getAllPersons().getMember("no_friend").getState());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logout(user);
	}
	
	/**
	 * Ein DummyListener, der feststellt, ob ein MemberListListener benachrichtig wird.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class TestListener extends MemberListAdapter {
		
		boolean notified = false;
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.MemberListListener#listLoaded(posemuckel.client.model.event.MemberListEvent)
		 */
		public void listLoaded(MemberListEvent event) {
			notified = true;
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.MemberListListener#memberAdded(posemuckel.client.model.event.MemberListEvent)
		 */
		public void memberAdded(MemberListEvent event) {
			notified = true;
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.MemberListListener#buddyDeleted(posemuckel.client.model.event.MemberListEvent)
		 */
		public void buddyDeleted(MemberListEvent event) {
			notified = true;
		}
		
	}
	
	
}
