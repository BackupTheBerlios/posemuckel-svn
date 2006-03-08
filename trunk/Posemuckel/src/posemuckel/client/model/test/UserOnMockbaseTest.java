package posemuckel.client.model.test;

import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import junit.framework.TestCase;

/**
 * Dieser Test testet das Verhalten von <b>Mockbase<\b>, wenn ein Anwender
 * in Mockbase eingef�gt oder gel�scht wird.
 * 
 * @author Posemuckel Team
 *
 */
public class UserOnMockbaseTest extends TestCase {
	
	private Mockbase registry;
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		DatabaseFactory.createRegistry(DatabaseFactory.USE_MOCKBASE);
		registry = (Mockbase)DatabaseFactory.getRegistry();
	}
	
	/**
	 * Am Anfang ist Mockbase leer.
	 *
	 */
	public void testIsEmpty() {
		assertTrue(registry.hasNoUsers());
	}
	
	/**
	 * Wenn ein Anwender in Mockbase eingef�gt wird, dann ist Mockbase nicht mehr
	 * leer.
	 *
	 */
	public void testAddUser() {
		registry.overwriteUser("patrick", "pwd");
		assertFalse(registry.hasNoUsers());
	}
	
	/**
	 * Wenn ein Anwender in Mcokbase eingef�gt und anschlie�end wieder entfernt 
	 * wird, ist Mockbase wieder leer.
	 *
	 */
	public void testRemoveUser() {
		registry.overwriteUser("patrick", "pwd");
		registry.deleteUser("patrick", "pwd");
		assertTrue(registry.hasNoUsers());
	}
	
	/**
	 * Wenn das falsche Passwort verwendet wird, kann der Anwender nicht 
	 * gel�scht werden.
	 *
	 */
	public void testRemoveWithWrongPwd() {
		registry.overwriteUser("patrick", "pwd");
		registry.deleteUser("patrick", "fake");
		assertFalse(registry.hasNoUsers());
	}
	
	/**
	 * Das Passwort wird auch in Mockbase gespeichert.
	 *
	 */
	public void testCheckPwd() {
		registry.overwriteUser("patrick", "pwd");
		assertFalse(registry.checkPwd("patrick", "fake"));
		assertTrue(registry.checkPwd("patrick", "pwd"));
	}
	
	/**
	 * Hey, der Anwender kann sich �ber die Methode <code>User#register()</code>
	 * in Mockbase registrieren!
	 *
	 */
	public void testRegisteredUser() {
		registry.overwriteUser("patrick", "pwd");
		Model model = new Model();
		User user = model.getUser();
		user.register("patrick", "password", "", "", "", "", "","","");
		assertTrue(registry.checkPwd("patrick", "pwd"));
		assertFalse(registry.checkPwd("patrick", "password"));
	}
	

}
