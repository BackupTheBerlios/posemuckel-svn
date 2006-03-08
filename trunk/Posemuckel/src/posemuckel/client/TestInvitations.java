package posemuckel.client;

import lib.RetriedAssert;
import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.client.model.User;
import posemuckel.client.model.event.ProjectEvent;
import posemuckel.client.model.event.ProjectListener;
import posemuckel.client.model.event.ProjectListenerAdapter;

/**
 * In diesem Test wird das Annehmen und das Ablehnen von offenen Einladungen 
 * sowie das Laden der Liste mit Einladungen getestet.
 * Der Test umfasst den gesamten 
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
public class TestInvitations extends TestComponents {

	private Model model;
	private User user;
	private ConnectionHelper connection;
	private ProjectTestListener listener;
	
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
		Settings.setDubuggingMode(true, true, false, true);
		connection = new ConnectionHelper();
		connection.startClient();
		model = new Model();
		//diese Initialisierung wird in der statischen Methode getModel() vorgenommen
		DatabaseFactory.getRegistry().setReceiver(model);
		listener = new ProjectTestListener();
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
	 * In diesem Test wird das Annehmen einer Einladung zu einem Projekt getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden aller Projekte</li>
	 * <li>Laden der Liste mit den Einladungen</li>
	 * <li>Annehmen der Einladung: dem Projekt beitreten</li>
	 * <li>das Projekt wieder verlassen</li>
	 * <li>dem Projekt noch einmal beitreten</li>
	 * </ul>
	 * 
	 * Besonderes Augenmerk wird auf die Änderung der freien Plätze im Projekt
	 * gelegt.
	 */
	public void testOpenInvitations() {
		login(user);
		getAllProjects(model);
		user.getInvitations().addListener(listener);
		user.getInvitations().load();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isNotified());
					assertTrue(listener.isSucceeded());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Project invitationTest = user.getInvitations().searchByTopic("invitationTest");
		assertNotNull(invitationTest);
		final String id = invitationTest.getID();
		int free = Integer.valueOf(invitationTest.getFreeSeats());
		listener.notified = false;
		listener.succeeded = false;
		invitationTest.join();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isNotified());
					assertTrue(listener.isSucceeded());
					//die Projekte wurden nicht geladen, aber das Projekt invitationTest
					//muss trotzdem enthalten sein
					assertNotNull(user.getProjects().searchByTopic("invitationTest"));
					assertNull(user.getInvitations().getProject(id));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		invitationTest = user.getProjects().getProject(id);
		int actual = Integer.valueOf(invitationTest.getFreeSeats());
		assertEquals("Die Zahl der freien Plätze hat sich geändert", free, actual);
		free = actual;
		user.getInvitations().removeListener(listener);
		user.getProjects().addListener(listener);
		model.getAllProjects().addListener(listener);
		//das Projekt wieder verlassen und noch mal beitreten
		listener.setBool(false, false);
		invitationTest.leave();
		final String expectedFree = (free+1)+"";
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue("die Listener wurden nicht benachrichtigt", listener.isNotified());
					assertTrue(listener.isSucceeded());
					assertNotNull(model.getAllProjects().getProject(id));
					assertNull(user.getProjects().getProject(id));
					assertTrue(listener.changed);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		invitationTest = model.getAllProjects().getProject(id);
		assertEquals("Die Zahl der freien Plätze wurde nicht korrekt angepasst",
				expectedFree, invitationTest.getFreeSeats());
		listener.setBool(false, false);
		invitationTest.join();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue("die Listener wurden nicht benachrichtigt", listener.isNotified());
					assertTrue(listener.isSucceeded());
					assertNotNull(user.getProjects().getProject(id));
					assertTrue(listener.changed);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Die Zahl der freien Plätze wurde nicht korrekt angepasst",
				String.valueOf(free), invitationTest.getFreeSeats());

	}
	
	/**
	 * In diesem Test wird das Annehmen einer Einladung zu einem Projekt getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden aller Projekte</li>
	 * <li>Laden der Liste mit den Einladungen</li>
	 * <li>Ablehnen der Einladung</li>
	 * </ul>
	 * 
	 * Besonderes Augenmerk wird auf die Änderung der freien Plätze im Projekt
	 * gelegt.
	 */ 
	public void testRejectInvitation() {
		login(user);
		getAllProjects(model);
		user.getInvitations().addListener(listener);
		user.getInvitations().load();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(user.getInvitations().isEmpty());
					assertNotNull(user.getInvitations().searchByTopic("school"));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Project project = user.getInvitations().searchByTopic("school");
		final String id = project.getID();
		int free = Integer.parseInt(project.getFreeSeats());
		listener.notified = false;
		listener.succeeded = false;
		project.rejectInvitation();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isNotified());
					assertTrue(listener.isSucceeded());
					assertNull(user.getInvitations().getProject(id));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		project = model.getAllProjects().getProject(id);
		assertEquals("Die Zahl der freien Plätze sollte größer werden", (free +1), Integer.parseInt(project.getFreeSeats()));
	}
	
	/**
	 * Ein DummyListener, der feststellt, ob eine Operation erfolgreich war
	 * und ob ein ProjectListener benachrichtigt wird.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class ProjectTestListener extends ProjectListenerAdapter 
		implements ProjectListener {
		
		boolean notified = false;
		boolean succeeded = false;
		boolean changed = false;
		
		/**
		 * Setzt die boolschen Flags.
		 * @param notified gibt an, ob der Listener benachrichtigt wurde
		 * @param succeeded gibt an, ob die letzte Nachricht einen Erfolg gemeldet hat
		 */
		synchronized void setBool(boolean notified, boolean succeeded) {
			this.notified = notified;
			this.succeeded = succeeded;
			changed = false;
		}
		
		/**
		 * Gibt an, ob die letzte Benachrichtigung des Listeners eine
		 * Erfolgsmeldung enthalten hat.
		 * @return true , falls die letzte Aktion erfolgreich war
		 */
		synchronized boolean isSucceeded() {
			return succeeded;
		}
		
		/**
		 * Gibt an, ob der Listener benachrichtigt wurde.
		 * @return true , falls der Listener benachrichtigt wurde
		 */
		synchronized boolean isNotified() {
			return notified;
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ProjectListener#newProject(posemuckel.client.model.event.ProjectEvent)
		 */
		public void newProject(ProjectEvent project) {
			setBool(true, true);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ProjectListener#listLoaded(posemuckel.client.model.event.ProjectEvent)
		 */
		public void listLoaded(ProjectEvent event) {
			setBool(true, true);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ProjectListener#accessDenied()
		 */
		public void accessDenied() {
			//System.out.println("answer: access denied");
			setBool(true, false);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ProjectListener#deleteProject(posemuckel.client.model.event.ProjectEvent)
		 */
		public void deleteProject(ProjectEvent event) {
			setBool(true, true);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ProjectListener#openProject(posemuckel.client.model.event.ProjectEvent)
		 */
		public void openProject(ProjectEvent event) {
			setBool(true, true);
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.ProjectListenerAdapter#elementChanged(posemuckel.client.model.event.ProjectEvent)
		 */
		@Override
		public void elementChanged(ProjectEvent event) {
			changed = true;
		}
		
		
		
	}

}
