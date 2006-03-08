package posemuckel.client;

import lib.RetriedAssert;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import posemuckel.client.model.event.MemberListAdapter;
import posemuckel.client.model.event.MemberListEvent;

/**
 *
 * In diesem Test wird ein Hinzuf&uuml;gen eines Buddys getestet. Der Ablauf hatte
 * zu einem Fehler bei der Präsentation des ersten Meilensteines geführt.
 * Der Ablauf ist:<br/>
 * <ul>
 * <li>Login</li>
 * <li>Laden der Buddyliste</li>
 * <li>Hinzuf&uuml;gen eines Buddys</li>
 * <li>Hinzuf&uuml;gen des gleichen Buddys</li>
 * <li>Logout</li>
 * </ul>
 * 
 * Wenn der Buddy hinzugefügt wurde, sollte der Server eine Fehlermeldung senden.
 * 
 * @author Posemuckel Team
 *
 */
public class ShowBuddyBug extends TestComponents {

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
	 * In diesem Test wird ein Hinzuf&uuml;gen eines Buddys getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Buddyliste</li>
	 * <li>Hinzuf&uuml;gen eines Buddys</li>
	 * <li>Hinzuf&uuml;gen des gleichen Buddys</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testAddBuddy() {
		if(Settings.debug)System.out.println("test: testAddBuddys");
		login(user);
		final String buddy = "another buddy";
		user.getBuddyList().addListener(listener);
		user.getBuddyList().load();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertFalse(user.getBuddyList().isEmpty());
					assertNull(user.getBuddyList().getMember(buddy));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		listener.notified = false;
		user.getBuddyList().addBuddy(buddy);
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertFalse(listener.error);
					assertNotNull(user.getBuddyList().getMember(buddy));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//wir versuchen das auch zweimal
		listener.notified = false;
		user.getBuddyList().addBuddy(buddy);
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue("das zweimalige Aufrufen von addBuddy schlägt fehl", 
							listener.notified);
					assertTrue(listener.error);
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
		boolean error = false;
		
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
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.MemberListListener#error(java.lang.String)
		 */
		public void error(String string) {
			notified = true;
			error = true;
		}		
	}
}
