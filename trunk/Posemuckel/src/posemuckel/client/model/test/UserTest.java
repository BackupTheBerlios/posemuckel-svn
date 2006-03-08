package posemuckel.client.model.test;

import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import junit.framework.TestCase;

/**
 * Testet Einloggen, Registrieren und Ausloggen des Benutzers. Es wird
 * gegen Mockbase getestet, um die Kommúnikation zwischen Client und
 * Database zu entwickeln.
 * 
 * @author Posemuckel Team
 *
 */
public class UserTest extends TestCase {
	
	private User user;
	private User fake;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DatabaseFactory.createRegistry(DatabaseFactory.USE_MOCKBASE);
		user = new User("somebody", new Model());
		fake = new User("nobody", new Model());
		register(user, "password");
	}

	/**
	 * prüft, ob ein neuer User weder eingeloggt noch verbunden ist
	 *
	 */
	public void testInitalStatus() {
		assertFalse(user.isLoggedIn());
		assertEquals("somebody", user.getNickname());
		assertEquals("password", user.getPassword());
		assertFalse(fake.isLoggedIn());
		assertEquals("nobody", fake.getNickname());
		assertEquals("", fake.getPassword());
	}
	
	/**
	 * prüft, ob sich der Login- und der Connected-Status ändert, wenn 
	 * ein registrierter User sich einloggt und ob sich der User das Passwort
	 * auch merkt
	 *
	 */
	public void testLogin() {
		user.login("password");
		assertTrue(user.isLoggedIn());
		assertTrue(user.checkPassword("password"));
		assertFalse(user.checkPassword("fake"));
		assertEquals("password", user.getPassword());
	}
	
	/**
	 * prüft, ob ein nicht registrierter User sich einloggen kann
	 *
	 */
	public void testNoRegistration() {
		fake.login("password");
		assertFalse(fake.isLoggedIn());
	}
	
	/**
	 * prüft, ob ein User bei Angabe eines falschen Passwortes eingeloggt ist
	 *
	 */
	public void testWrongPassword() {
		user.login("wrongPswd");
		assertFalse(user.isLoggedIn());
		assertNotSame("password", user.getPassword());
	}
		
	/**
	 * prüft, ob der User sich nach einer Registrierung auch einloggen
	 * kann
	 *
	 */
	public void testRegistration() {
		register(fake, "password");
		fake.login("password");
		assertTrue(fake.isLoggedIn());
		assertEquals("nobody", fake.getNickname());
		assertEquals("password", fake.getPassword());
	}
	
	/**
	 * prüft, ob der User nach einem Logout auch wirklich nicht mehr eingeloggt ist
	 * prüft, ob die Verbindung nach einem Logout auch wirklich nicht mehr besteht
	 *
	 */
	public void testLogout() {
		user.login("password");
		assertTrue(user.isLoggedIn());
		user.logout();
		assertFalse(user.isLoggedIn());
	}
	
	/**
	 * Hilfsmethode zur Registrierung. Der Anwender wird über 
	 * <code>User#register()</code> registriert. 
	 * @param user Benutzerername
	 * @param pwd Passwort
	 */
	private void register(User user, String pwd) {
		String name = user.getNickname();
		user.register(name, pwd, name, "surname", name + "@gmx.de","GERMAN", "MALE", "no location", "no comment");
	}

}
