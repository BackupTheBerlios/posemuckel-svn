package posemuckel.client;

import lib.RetriedAssert;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.PersonsData;
import posemuckel.client.model.User;
import posemuckel.client.model.event.MemberListAdapter;
import posemuckel.client.model.event.PersonsEvent;
import posemuckel.client.model.event.UserEvent;
import posemuckel.client.model.event.UserListenerAdapter;


/**
 * In diesem Test wird alles, was mit dem Profil eines Anwenders zusammenhängt,
 * getestet. Der Test umfasst den gesamten 
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
public class ProfileTest extends TestComponents {

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
	 * In diesem Test wird ein erfolgreiches Laden der Daten eines Anwenders
	 * getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Buddyliste</li>
	 * <li>Laden der Daten des Buddys</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testGetProfile() {
		if(Settings.debug)System.out.println("test: testGetProfile");
		login(user);
		user.getBuddyList().addListener(listener);
		loadBuddys(user);
		user.getBuddyList().loadPersonsData(new String[] {"niko"});
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertEquals("niko", listener.person.getNickname());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logout(user);
	}
	
	/**
	 * In diesem Test wird ein erfolgreiches &Auml;ndern der Daten eines Anwenders
	 * getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Anwenderdaten</li>
	 * <li>&Auml;ndern des Vornames</li>
	 * <li>&Auml;ndern der email-Adresse</li>
	 * <li>&Auml;ndern des Passwortes</li>
	 * <li>Logout</li>
	 * </ul>
	 * Die &Auml;nderungen der Daten erfolgen jeweils in einer eigenen Nachricht.
	 */
	public void testSetProfile() {
		if(Settings.debug)System.out.println("test: testSetProfile");
		//wenn der Anwender "tiger" verwendet wird, funktionieren die andern Tests
		//wegen des geänderten Passwortes nicht mehr
		login(user, "a_friend", "pwd");
		model.getAllPersons().addListener(listener);
		model.getAllPersons().loadPersonsData(new String[] {user.getNickname()});
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.notified);
					assertEquals(user, listener.person);
					assertEquals("Christian", user.getData().getFirstName());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final String firstname = "Chris";
		final String email = "dummy@gmx.com";
		final String pwd = "Iseaike0gGCz23M";
		PersonsData data = user.getData();
		//prüfen, ob die daten nicht schon gesetzt sind
		assertNotSame(firstname, data.getFirstName());
		assertNotSame(email, data.getEmail());
		assertNotSame(pwd, user.getPassword());
		final ProfileChangedListener profileListener = new ProfileChangedListener();
		user.addListener(profileListener);
		user.setProfile(user.getPassword(), firstname, data.getSurname(), data.getEmail(), data.getLang(), data.getGender(), data.getLocation(), data.getComment());
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(profileListener.notified);
					assertEquals(firstname, user.getData().getFirstName());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		data = user.getData();
		profileListener.notified = false;
		user.setProfile(user.getPassword(), data.getFirstName(), data.getSurname(), email, data.getLang(), data.getGender(), data.getLocation(), data.getComment());
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(profileListener.notified);
					assertEquals(email, user.getData().getEmail());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		data = user.getData();
		profileListener.notified = false;
		user.setProfile(pwd, data.getFirstName(), data.getSurname(), data.getEmail(), data.getLang(), data.getGender(), data.getLocation(), data.getComment());
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(profileListener.notified);
					assertEquals(pwd, user.getPassword());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logout(user);
	}
	
	/**
	 * In diesem Test wird ein fehlgeschlagenes Laden der Daten eines Anwenders
	 * getestet. Das Laden sollte fehlschlagen, weil die MemberList den angegebenen
	 * Nickname nicht kennt.
	 * 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Buddyliste</li>
	 * <li>Versuch, die Daten des Buddys zu laden</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testFail_GetProfile() {
		if(Settings.debug)System.out.println("test: testGetProfile");
		login(user);
		user.getBuddyList().addListener(listener);
		loadBuddys(user);
		try {
			user.getBuddyList().loadPersonsData(new String[] {"nobocy"});
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected){}
		logout(user);
	}
	
	/**
	 * Wartet darauf, dass die Daten einer Person geladen werden.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class TestListener extends MemberListAdapter {
		boolean notified;
		Person person;

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.MemberListAdapter#personsDataLoaded(posemuckel.client.model.event.PersonsEvent)
		 */
		@Override
		public void personsDataLoaded(PersonsEvent event) {
			notified = true;
			person = event.getSource();
		}		
	}
	
	/**
	 * Wartet darauf, dass sich das Profil des Users ändert.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class ProfileChangedListener extends UserListenerAdapter {
		
		boolean notified;

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.UserListenerAdapter#profileChanged(posemuckel.client.model.event.UserEvent)
		 */
		@Override
		public void profileChanged(UserEvent event) {
			notified = true;
		}
		
	}

}
