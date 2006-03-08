package posemuckel.client;

import lib.RetriedAssert;
import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.client.model.User;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.Webtrace;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;

/**
 * 
 * In diesem Test wird der Server über den Besuch von Webseiten informiert.
 * Es werden die VISITING- und die VIEWING-Nachricht getestet.
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
public class WebtraceTest extends TestComponents {
//TODO kommentieren
	private Model model;
	private User user;
	private ConnectionHelper connection;
	private MyListener listener;
	
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
		listener = new MyListener();
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
	 * Testet die Verwendung einer VISITING-Nachricht.  
	 * Der Ablauf des Testes ist:<br>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der eigenen Projekte</li>
	 * <li>Öffnen eines Projektes</li>
	 * <li>Senden einer VISITING-Nachricht: die URL hat keinen Vorgänger</li>
	 * <li>Senden einer VISITING-Nachricht: die URL hat einen Vorgänger</li>
	 * </ul>
	 */
	public void testVisiting() {
		final Project project = openProject();
		//die URL hat keinen Vater!
		project.setCurrentURL("no url at all");
		project.visiting();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.succeeded);
					assertNotNull(listener.page);
					assertEquals("mit der URL stimmt was nicht", "no url at all", listener.page.getURL());
					assertFalse("angeblich hat die page einen Vater", listener.page.hasFather());
					assertTrue("die Änderung der Wurzel wurde nicht mitgeteilt", listener.rootChanged);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		project.setPreviousURL("no url at all");
		project.setCurrentURL("father of no url");
		project.visiting();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertEquals("mit der URL stimmt was nicht", "father of no url", listener.page.getURL());
					assertTrue("angeblich hat die page keinen Vater", listener.page.hasFather());
					assertTrue(project.getWebtrace().getPageForUrl("no url at all").hasChildren());
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Es wird die gleiche URL zweimal besucht. Beim zweiten Besuch hat sich
	 * der Titel der Webseite geändert, was dem Client mitgeteilt wird.  
	 * Der Ablauf des Testes ist:<br>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der eigenen Projekte</li>
	 * <li>Öffnen eines Projektes</li>
	 * <li>Senden einer VISITING-Nachricht</li>
	 * <li>Senden einer VISITING-Nachricht: der Titel hat sich geändert</li>
	 * <li>das Projekt wird neu geladen</li>
	 * <li>Laden des Webtrace und Prüfen des Titels</li>
	 * </ul>
	 */
	public void testTitleChange() {
		//TODO
		Project project = openProject();
		//die URL hat keinen Vater!
		project.setCurrentURL("a title");
		project.setUrlTitle("title one");
		//erster Besuch
		visit(project);
		Webpage page = project.getWebtrace().getPageForUrl("a title");
		assertEquals("title one", page.getTitle());
		project.setUrlTitle("title two");
		//zweiter Besuch
		visit(project);
		assertEquals("title two", page.getTitle());
		//das Projekt neu laden
		openProject(project, model);
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					//wartet auf den Webtrace
					assertTrue(listener.succeeded);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		page = project.getWebtrace().getPageForUrl("a title");
		assertEquals("title two", page.getTitle());
	}
	
	/**
	 * Testet die Verwendung einer VIEWING-Nachricht. 
	 * Der Ablauf des Testes ist:<br>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der eigenen Projekte</li>
	 * <li>Öffnen eines Projektes</li>
	 * <li>Senden einer VIEWING-Nachricht</li>
	 * </ul>
	 */
	public void testViewing() {
		final Project project = openProject();
		project.useViewing();
		project.setCurrentURL("no url at all");
		project.visiting();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.viewing);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Führt die Kommunikation mit dem Server vom Login bis zum Öffnen des Projektes
	 * durch. Im einzelnen werden ausgeführt:
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der eigenen Projekte</li>
	 * <li>Öffnen eines Projektes</li>
	 * <li>zum Webtrace des Projektes wird ein Listener hinzugefügt</li
	 * </ul>
	 * @return das geöffnete Projekt
	 */
	protected Project openProject() {
		login(user);
		getMyProjects(user);
		final Project project = user.getProjects().searchByTopic("webtraceTest");
		openProject(project, model);
		assertTrue(project.getMemberList().getMember(user.getNickname()) != null);
		project.getWebtrace().addListener(listener);
		return project;
	}
	
	/**
	 * Ein DummyListener, der feststellt, ob eine Operation erfolgreich war
	 * und ob ein WebTraceListener benachrichtigt wird.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class MyListener extends WebTraceAdapter {
		
		Webpage page;
		boolean succeeded;
		boolean rootChanged;
		boolean viewing;

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#rootChanged(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void rootChanged(WebTraceEvent event) {
			rootChanged = true;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#traceLoaded(posemuckel.client.model.Webtrace)
		 */
		@Override
		public void traceLoaded(Webtrace webtrace) {
			//System.out.println(" trace loaded");
			succeeded = true;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#visiting(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void visiting(WebTraceEvent event) {
			succeeded = true;
			String url = event.getURL();
			page = model.getOpenProject().getWebtrace().getPageForUrl(url);
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#viewing(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void viewing(WebTraceEvent event) {
			viewing = true;
		}
		
	}
}
