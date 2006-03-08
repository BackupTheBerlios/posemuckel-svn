package posemuckel.client;

import lib.RetriedAssert;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;

/**
 * 
 * In diesem Test werden die Funktionalitäten, die im RFC als GETTER bezeichnet
 * werden, getestet.  Der Test umfasst den gesamten 
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
public class TestGetters extends TestComponents {

	private Model model;
	private User user;
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
		Settings.setDubuggingMode(false, false, false, false);
		connection = new ConnectionHelper();
		connection.startClient();
		model = new Model();
		user = model.getUser();
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
	 * In diesem Test wird ein erfolgreiches Laden aller Projekte getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden aller Projekte</li>
	 * </ul>
	 */
	public void testGetAllProjects() {
		login(user);
		model.getAllProjects().load();
		try {
			new RetriedAssert(3*Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(model.getAllProjects().isEmpty());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(Settings.debug)System.out.println(model.getAllProjects().toString());
	}
	
	/**
	 * In diesem Test wird ein erfolgreiches Laden aller User getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden aller User</li>
	 * </ul>
	 */
	public void testGetAllUsers() {
		login(user);
		model.getAllPersons().load();
		try {
			new RetriedAssert(3*Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(model.getAllPersons().size() > 1);
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
