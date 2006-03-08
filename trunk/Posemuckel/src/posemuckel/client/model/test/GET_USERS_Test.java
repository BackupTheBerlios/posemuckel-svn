package posemuckel.client.model.test;

import junit.framework.TestCase;
import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.MemberList;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;

/**
 * Testet das Laden einer Liste mit Anwendern von Mockbase. 
 * 
 * @author Posemuckel Team
 *
 */
public class GET_USERS_Test extends TestCase {
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DatabaseFactory.createRegistry(DatabaseFactory.USE_MOCKBASE);
		User sandro = new User("sandro", new Model());
		User jens = new User("jens", new Model());
		User tanja = new User("tanja", new Model());
		register(tanja, "pwd");
		register(jens, "apple");
		register(sandro, "avatura");
	}
	
	/**
	 * Testet das Laden einer Liste von Anwendern von der
	 * Database.
	 *
	 */
	public void testInitial() {
		Model model = Model.getModel();
		MemberList members = model.getAllPersons();
		assertTrue(members.isEmpty());
		members.load();
		assertFalse(members.isEmpty());
		assertEquals(3, members.getMembers().size());
	}
	
	/**
	 * Hilfsmethode zur Registrierung eines Anwenders in der Database.
	 * @param user gewünschter Anwendername
	 * @param pwd Passwort des Anwenders
	 */
	private void register(User user, String pwd) {
		String name = user.getNickname();
		user.register(name, pwd, "firstname", "surname", name + "@gmx.de","GERMAN", "MALE", "no location", "no comment");
	}

}
