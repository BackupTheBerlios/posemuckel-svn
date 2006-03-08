package posemuckel.client;

import lib.RetriedAssert;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import posemuckel.client.model.event.UserEvent;
import posemuckel.client.model.event.UserListenerAdapter;

/**
 * 
 * In diesem Test wird die Funktionalität eines <code>User</code>,
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
public class UserTest extends TestComponents {
	
	private Model model;
	private User user;
	private UserRecord listener;
	private ConnectionHelper connection;
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		/*
		 * f&uuml; jeden Test einzeln an und abschalten!
		 * damit lassen sich Probleme selektiv beheben
		 * das Abschalten in tearDown nicht vergessen
		 */
		Settings.setDubuggingMode(true, false, false, false);
		connection = new ConnectionHelper();
		connection.startClient();
		model = new Model();
		user = model.getUser();
		listener = new UserRecord();
		user.addListener(listener);
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
	 * In diesem Test wird eine fehlgeschlagene Registrierung getestet. Nach
	 * einer fehlgeschlagenen Registrierung sollte man sich mit einem anderen
	 * Usernamen registrieren k&ouml;nnen.
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Registrierung mit bereits existierendem Usernamen</li>
	 * <li>Registrierung mit noch nicht existierendem Usernamen</li>
	 * </ul>
	 */
	public void testFailedRegister() {
		listener.setSucceeded(true);
		user.register("tiger", "tiger", "chris", "w", "w@gmx.de", "de", "male", "hagen", "no comment");
		/*
		 * prüft alle Settings.checkIntervallms, ob das Login erfolgreich war
		 * der Test läuft höchstens 3000 ms, dann schlägt er fehl
		 */
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL/2) {
				@Override
				public void run() throws Exception {
					assertFalse(listener.isSucceeded());
				}			
			}.start();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		listener.setSucceeded(false);
		user.register("lietaer", "GeldDerZukunft", "chris", "w", "w@gmx.de", "de", "male", "hagen", "no comment");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isSucceeded());
				}			
			}.start();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	/**
	 * In diesem Test wird eine erfolgreiche Registrierung getestet. Nach
	 * einer Registrierung sollte man sich einloggen k&ouml;nnen.
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Registrierung mit noch nicht existierendem Usernamen</li>
	 * <li>Login</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testSuccessfulRegistration() {
		listener.setSucceeded(false);
		user.register("apple", "apple", "chris", "x", "w@gmx.de", "de", "male", "hagen", "no comment");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isSucceeded());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		listener.setSucceeded(false);
		login("apple", "apple");
		logout();
	}
	
	/**
	 * In diesem Test wird ein erfolgreiches Login getestet. Nach
	 * dem Login sollte man sich ausloggen k&ouml;nnen.
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testLogin() {
		login("tiger", "tiger");
		logout();
	}

	/**
	 * In diesem Test wird ein erfolgreiches Login getestet. Nach
	 * dem Login sollte man sich ausloggen k&ouml;nnen.
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testAnotherLogin() {
		login("niko", "mac14");
		logout();
	}
	
	/**
	 * In diesem Test wird ein Login mit falschem Passwort getestet. Nach
	 * dem Login sollte man sich mit dem richtigen Passwort einloggen k&ouml;nnen.
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Loginversuch mit falschem Passwort</li>
	 * <li>Login mit korrektem Passwort</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testFailedLogin() {
		listener.setSucceeded(true);
		user.login("tiger", "tier");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL/2) {
				@Override
				public void run() throws Exception {
					assertFalse(listener.isSucceeded());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		login("tiger", "tiger");
		logout();
	}
	
	/**
	 * In diesem Test wird versucht, sich einzuloggen, während der Nutzer im . Nach
	 * Server als eingeloggt registriert ist. Der zweite Einloggversuch sollte
	 * also fehlschlagen.
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>erneutes Login mit gleichem Benutzernamen</li>
	 * </ul>
	 */
	public void testDoubleLogin() {
		//das erstel Mal erfolgreich einloggen
		login(user);
		listener.setSucceeded(true);
		//tiger ist schon angemeldet
		user.login("tiger", "tiger");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(listener.isSucceeded());
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Führt das Login durch. Es wird auf die Benachrichtigung der Listener 
	 * gewartet.
	 * @param name Benutzername
	 * @param pwd Passwort
	 */
	private void login(String name, String pwd) {
		listener.setSucceeded(false);
		user.login(name, pwd);
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL/2) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isSucceeded());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Führt das Logout durch. Es wird auf die Benachrichtigung der Listener 
	 * gewartet.
	 *
	 */
	private void logout() {
		listener.setSucceeded(false);
		user.logout();;
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL/2) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isSucceeded());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ein DummyListener, der feststellt, ob eine Operation erfolgreich war.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class UserRecord extends UserListenerAdapter {
		
		private boolean succeeded = false;
		
		/**
		 * Setzt das Erfolgsflag.
		 * @param b boolscher Wert für das Flag 
		 */
		synchronized void setSucceeded(boolean b) {
			succeeded = b;
		}
		
		/**
		 * Gibt an, ob die letzte Aktion erfolgreich war. 
		 * @return true , falls die letzte Aktion erfolgreich war
		 */
		synchronized boolean isSucceeded() {
			return succeeded;
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.UserListener#login(posemuckel.client.model.event.UserEvent)
		 */
		public void login(UserEvent event) {
			setSucceeded(event.isSucceeded());
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.UserListener#register(posemuckel.client.model.event.UserEvent)
		 */
		public void register(UserEvent event) {
			setSucceeded(event.isSucceeded());
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.UserListener#logout(posemuckel.client.model.event.UserEvent)
		 */
		public void logout(UserEvent event) {
			setSucceeded(event.isSucceeded());
		}
		
	}
	
}
