package posemuckel.client.model.test;

import junit.framework.TestCase;
import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.User;

/**
 * Testet die Verwaltung der Buddys eines Anwenders. Es wird auf einer 
 * Mockup-Implementierung der Database gearbeitet, um auch die 
 * Kommunikationsschnittstelle zwischen dem Model des Client und der Database
 * entwickeln zu können. Die verwendete Mockbase führt alle Anforderunen korrekt
 * aus; zum Testen bei Fehlverhalten muss <code>BlockedBase</code> oder 
 * <code>ErrorBase</code> verwendet werden.
 * 
 * @author Posemuckel Team
 *
 */
public class BuddysOnMockBaseTest extends TestCase {

	private Mockbase registry;
	private User user;
	private Model model;
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DatabaseFactory.createRegistry(DatabaseFactory.USE_MOCKBASE);
		model = new Model();
		registry = (Mockbase)DatabaseFactory.getRegistry();
		user = new User("somebody", model);
		//registry.setClientHash("absc");
	}
	
	/**
	 * Testet den Anfangszustand: in der Registry sind keine Buddys enthalten.
	 *
	 */
	public void testInitial() {
		assertFalse(registry.hasBuddy());
	}
	
	/**
	 * Testet das Hinzufügen eines Buddys: die Aufforderung zum Hinzufügen
	 * eines Buddys kommt in der Database an und der Buddy wird
	 * in die Buddyliste des Anwenders eingefügt. 
	 *
	 */
	public void testAddBuddy() {
		String buddy = "a_friend";
		registry.overwriteUser(buddy, buddy);
		model.getAllPersons().load();
		assertTrue(model.getAllPersons().getMember(buddy)!= null);
		user.getBuddyList().addBuddy(buddy);
		assertTrue(registry.hasBuddy());
		assertFalse(user.getBuddyList().isEmpty());
		Person buddyP = user.getBuddyList().getMembers().get(0);
		assertEquals(buddy, buddyP.getNickname());
		assertEquals(Person.ONLINE, buddyP.getState());
	}
	
	/**
	 * Fügt erst einen Buddy in die Buddyliste ein, um ihn anschließend wieder 
	 * zu löschen.
	 *
	 */
	public void testDelBuddy() {
		user.getBuddyList().addBuddy("no_friend");
		user.getBuddyList().deleteMember("no_friend");
		assertFalse(registry.hasBuddy());
		assertTrue(user.getBuddyList().isEmpty());
	}
	
	/**
	 * In Mockbase wird eine Liste, bestehend aus zwei Buddys, abgelegt. Es wird
	 * getestet, ob diese Liste korrekt geladen wird.
	 *
	 */
	public void testLoadBuddys() {
		addBuddyToTable("Sandro", "avatura", Person.ONLINE);
		addBuddyToTable("Jens", "0815", Person.OFFLINE);
		assertTrue(user.getBuddyList().isEmpty());
		user.getBuddyList().load();
		assertEquals(2, user.getBuddyList().size());
		assertEquals(Person.ONLINE, user.getBuddyList().getMember("Sandro").getState());
		assertEquals(Person.OFFLINE, user.getBuddyList().getMember("Jens").getState());
	}
	
	/**
	 * Es wird eine Buddyliste mit einem Buddy von Mockbase geladen. Es wird getestet, 
	 * ob der Buddy nach dem Laden in der Buddyliste vorhanden ist.
	 *
	 */
	public void testLoadOneBuddy() {
		addBuddyToTable("Sandro", "avatura", Person.OFFLINE);		
		assertTrue(user.getBuddyList().getMembers().isEmpty());		
		user.getBuddyList().load();		
		assertEquals("Sandro", user.getBuddyList().getMembers().get(0).getNickname());
	}
	
	/**
	 * Speichert den Buddy in einer Tabelle in Mockbase ab.
	 * @param name Benutzername des Buddys
	 * @param hash des Buddys
	 * @param state Online-Status des Buddys
	 */
	private void addBuddyToTable(String name, String hash, String state) {
		Person buddy = new Person(name, state);
		registry.addBuddy(registry.getClientHash(), buddy);
	}
	
	

}
