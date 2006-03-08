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
 * 
 * In diesem Test wird die Funktionalität einer <code>ProjectList</code>
 * bzw. eines <code>Project</code> getestet. Der Test umfasst den gesamten 
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
public class ProjectTest extends TestComponents {

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
		Settings.setDubuggingMode(false, false, false, false);
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
	 * Hilfsmethode zum Laden der Anwenderprojekte.
	 */
	protected void getMyProjects(final User user) {
		user.getProjects().load();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(user.getProjects().isEmpty());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * In diesem Test wird das Anlegen eines neuen Projektes getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Buddyliste</li>
	 * <li>Projekt starten</li>
	 * <li>dem Projekt beitreten (der Owner tritt einem privaten Projekt bei)</li>
	 * </ul>
	 */
	public void testStartProject() {
		if(Settings.debug) System.out.println("testStartProject");
		login(user);
		/*
		 * durch das Laden der Buddys wird dem Model die Menge der Nicknames
		 * der Buddys bekannt gemacht; sonst beschwert sich das Projekt, 
		 * dass ein Unbekannter als Mitglied eingetragen wird.
		 */
		user.getBuddyList().load();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(user.getBuddyList().isEmpty());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Project project = new Project(model);
		project.setData("0", "tiger", "tiger", Project.PRIVATE_TYPE, 2+"", 4+"", "a description", "2005-12-29");
		project.addMember("niko");
		project.setMaxNumber(5);
		model.getAllProjects().addListener(listener);
		model.getAllProjects().startProject(project);
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertNotNull(model.getAllProjects().searchByTopic("tiger"));
					assertTrue(listener.isNotified());
					assertNotNull(model.getAllProjects().searchByTopic("tiger").getID());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		project = model.getAllProjects().searchByTopic("tiger");
		final String id = project.getID();
		int free = Integer.parseInt(project.getFreeSeats());
		project.join();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertNotNull(user.getProjects().getProject(id));
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("die Zahl der freien Plätze sollte kleiner werden", 
				free-1, Integer.parseInt(project.getFreeSeats()));
	}
	
	/**
	 * In diesem Test wird das Laden der Projekte eines Anwenders getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden aller Projekte</li>
	 * <li>Laden aller Projekte eines Anwenders</li>
	 * </ul>
	 */
	public void testMyProjects() {
		if(Settings.debug) System.out.println("testMyProjects");
		login(user);
		user.getProjects().addListener(listener);
		getAllProjects(model);
		getMyProjects(user);
	}
	
	/**
	 * In diesem Test wird der Beitritt zu einem Projekt (Typ: private) 
	 * getestet. Der Anwender kann dem Projekt nicht beitreten, da er
	 * nicht eingeladen wurde.
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden aller Projekte</li>
	 * <li>fehlschlagender Beitritt zum Projekte</li>
	 * </ul>
	 */
	public void testJoinProjectOnFail() {
		if(Settings.debug) System.out.println("testJoinProjectOnFail");
		login(user);
		user.getProjects().addListener(listener);
		getAllProjects(model);
		Project uni = model.getAllProjects().searchByTopic("Uni");
		listener.notified = false;
		listener.succeeded = true;
		int free = Integer.parseInt(uni.getFreeSeats());
		final String id = uni.getID();
		uni.join();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isNotified());
					assertFalse(listener.isSucceeded());
					assertNull(user.getProjects().getProject(id));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("die Zahl der freien Plätze sollte sich nicht verändern", 
				free, Integer.parseInt(uni.getFreeSeats()));
	}
	
	/**
	 * In diesem Test wird der Beitritt zu einem Projekt (Typ: public) 
	 * getestet. Anschließend wird die Mitgliederliste geladen und das
	 * &Ouml;ffnen des Projektes getestet.
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden aller Projekte</li>
	 * <li>Beitritt zum Projekte</li>
	 * <li>Laden der Projektmitglieder</li>
	 * <li>das Projekt &ouml;ffnen</li>
	 * </ul>
	 */
	public void testJoinAndOpenProject() {
		if(Settings.debug) System.out.println("testJoinProject");
		login(user);
		user.getProjects().addListener(listener);
		getAllProjects(model);
		final Project uni = model.getAllProjects().searchByTopic("Lernen");
		listener.notified = false;
		listener.succeeded = false;
		int free = Integer.parseInt(uni.getFreeSeats());
		final String id = uni.getID();
		uni.join();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isNotified());
					assertTrue(listener.isSucceeded());
					//die Projekte wurden nicht geladen, aber das Projekt Lernen
					//muss trotzdem enthalten sein
					assertNotNull(user.getProjects().getProject(id));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("die Zahl der freien Plätze sollte kleiner werden", 
				(free-1), Integer.parseInt(uni.getFreeSeats()));
		uni.getMemberList().load();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(uni.getMemberList().isEmpty());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		listener.notified = false;
		listener.succeeded = false;
		uni.open();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isNotified());
					assertTrue(listener.isSucceeded());
					//die Projekte wurden nicht geladen, aber das Projekt Lernen
					//muss trotzdem enthalten sein
					assertEquals(uni, model.getOpenProject());
					assertNotNull(uni.getChatID());
					assertNotNull(model.getChat(uni.getChatID()));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * In diesem Test wird das Verlassen eines Projektes getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden aller Projekte</li>
	 * <li>Verlassen eines Projektes</li>
	 * </ul>
	 */
	public void testLeaveProject() {
		if(Settings.debug) System.out.println("testLeaveProject");
		login(user);
		user.getProjects().addListener(listener);
		getAllProjects(model);
		Project vfb = model.getAllProjects().searchByTopic("vfb");
		listener.notified = false;
		listener.succeeded = false;
		int free = Integer.parseInt(vfb.getFreeSeats());
		final String id = vfb.getID();
		vfb.leave();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.isNotified());
					assertTrue(listener.isSucceeded());
					assertNull(user.getProjects().getProject(id));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("die Zahl der freien Plätze sollte größer werden", 
				(free+1), Integer.parseInt(vfb.getFreeSeats()));
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
		
		/**
		 * Ändert die Konfiguration des Listeners.
		 * @param notified 
		 * @param succeeded
		 */
		synchronized void setBool(boolean notified, boolean succeeded) {
			this.notified = notified;
			this.succeeded = succeeded;
		}
		
		/**
		 * Gibt an, ob eine Operation erfolgreich war.
		 * @return true , falls die letzte Operation erfolgreich war
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
		
	}


}
