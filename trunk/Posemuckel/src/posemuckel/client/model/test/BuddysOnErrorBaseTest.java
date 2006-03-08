/**
 * 
 */
package posemuckel.client.model.test;

import junit.framework.TestCase;
import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import posemuckel.client.model.event.MemberListAdapter;

/**
 * Es wird getestet, wie die Projektlisten und die Projekte auf Fehler in der 
 * Datenbank reagieren.
 * 
 * @author Posemuckel Team
 *
 */
public class BuddysOnErrorBaseTest extends TestCase {
	
	private Model model;
	private User user;
	private MyBuddyListener listener;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		DatabaseFactory.createRegistry(DatabaseFactory.USE_ERRORBASE);
		model = new Model();
		user = model.getUser();
		user.login("tiger", "tiger");
		listener = new MyBuddyListener();
		user.getBuddyList().addListener(listener);
	}
	
	/**
	 * Pr�ft, ob die Listener eine Fehlermeldung erhalten, wenn das Laden der 
	 * Buddyliste nicht funktioniert.
	 *
	 */
	public void testLoadBuddys() {
		user.getBuddyList().load();
		assertTrue(listener.error);
	}
	
	/**
	 * Testet, ob die Listener eine Fehlermeldung erhalten, wenn das Hinzuf�gen
	 * eines Buddys nicht funktioniert.
	 *
	 */
	public void testAddBuddy() {
		user.getBuddyList().addBuddy("hans");
		assertTrue(listener.error);
	}
	
	/**
	 * Testet, ob die Listener eine Fehlermeldung erhalten, wenn das L�schen 
	 * eines Buddys nicht funtkioniert.
	 *
	 */
	public void testDeleteBuddy() {
		user.getBuddyList().deleteMember("hans");
		assertTrue(listener.error);
	}
	
	/**
	 * Dieser Listener ist nur an den Fehlermeldungen interessiert, die f�r die
	 * Tests in BuddysOnErrorBase getestet werden.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class MyBuddyListener extends MemberListAdapter {
		
		boolean error;

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.MemberListAdapter#error(java.lang.String)
		 */
		@Override
		public void error(String string) {
			error = true;
		}
		
	}
}
