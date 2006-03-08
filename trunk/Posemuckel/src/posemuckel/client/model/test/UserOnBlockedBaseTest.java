package posemuckel.client.model.test;

import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import junit.framework.TestCase;

/**
 * BlockedBase antwortet auf die Anfragen des Objektes user nicht: die Daten
 * von user dürfen sich daher nicht ändern. Diese Tests dienen vor allem zur
 * Entwicklung der Kommunikation zwischen Client und Database. Es gilt das
 * Prinzip, dass Änderungen erst vorgenommen werden, wenn sie in der Database 
 * realisiert wurden und die Database eine Bestätigung versendet hat.
 * 
 * @author Posemuckel Team
 *
 */
public class UserOnBlockedBaseTest extends TestCase {
	
	private User user;
	private Mockbase registry;
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DatabaseFactory.createRegistry(DatabaseFactory.USE_BLOCKED);
		registry = (Mockbase)DatabaseFactory.getRegistry();
		user = new User(new Model());
	}
	
	/**
	 * Wenn die Database ein Login nicht bestätigt, ist der User nicht 
	 * eingeloggt.
	 *
	 */
	public void testLogin() {
		user.login("somebody", "password");
		assertFalse(registry.isLoggedIn("somebody"));
		assertFalse(user.isLoggedIn());
	}

	/**
	 * Wenn die Database eine Registrierung nicht bestätigt, ist der User nicht
	 * registriert.
	 */
	public void testRegister() {
		user.register("nobody", "pwd", "nobody", "surname", "nobody@gmx.de", "GERMAN", "MALE", "no location", "no comment");
		user.login("pwd");
		assertFalse(registry.exists("nobody"));
		assertFalse(user.isLoggedIn());
	}
	
}