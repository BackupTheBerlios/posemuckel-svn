package posemuckel.client;

import lib.RetriedAssert;
import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Folder;
import posemuckel.client.model.FolderTree;
import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.client.model.User;
import posemuckel.client.model.event.FolderEvent;
import posemuckel.client.model.event.FolderTreeListener;

/**
 * In diesem Test wird die Funktionalität des <code>FolderTrees</code> und der 
 * Folder getestet. Der Test umfasst den gesamten 
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
public class FolderTest extends TestComponents {

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
		Settings.setDubuggingMode(true, true, false, false);
		connection = new ConnectionHelper();
		connection.startClient();
		model = new Model();
		//diese Initialisierung wird in der statischen Methode getModel() vorgenommen
		DatabaseFactory.getRegistry().setReceiver(model);
		user = model.getUser();
		listener = new MyListener();
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
	 * Führt das Öffnen eines Projektes durch. Das Projekt enthält den FolderTree, 
	 * der getestet werden soll.</br>
	 * Im Einzelnen werden folgende Aktionen durchgeführt:
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der eigenen Projekte</li>
	 * <li>Öffnen eines Projektes</li>
	 * <li>Laden des Webtrace</li>
	 * </ul>	
	 * @return das geöffnete Projekt
	 */
	protected Project openProject() {
		login(user);
		getMyProjects(user);
		final Project project = user.getProjects().searchByTopic("webtraceTest");
		openProject(project, model);
		assertTrue(project.getMemberList().getMember(user.getNickname()) != null);
		this.waitForWebtrace(project);
		return project;
	}
	
	/**
	 * Bereitet den FolderTree des geöffneten Projektes für die 
	 * Tests vor. Es wird ein Listener im FolderTree registriert.
	 * @return der FolderTree des geöffneten Projektes
	 */
	protected FolderTree getTree() {
		Project project = openProject();
		FolderTree folderTree = project.getFolderTree();
		folderTree.addListener(listener);
		return folderTree;
	}
	
	/**
	 * In diesem Test wird das Anlegen eines neuen Folders im FolderTree getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der eigenen Projekte</li>
	 * <li>Öffnen eines Projektes</li>
	 * <li>Laden des Webtrace</li>
	 * <li>Erstellen eines neuen Folders</li>
	 * </ul>
	 */
	public void testNewFolder() {
		FolderTree folderTree = getTree();
		folderTree.requestNewFolder("a title", "");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.succeeded);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * In diesem Test wird der Wechsel eines ParentFolder für einen Folder getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der eigenen Projekte</li>
	 * <li>Öffnen eines Projektes</li>
	 * <li>Laden des Webtrace</li>
	 * <li>Erstellen von drei neuen Foldern, der Parent des zweiten Folders
	 * ist dabei der erste Folder</li>
	 * <li>der zweite Folder ändert seinen ParentFolder: der neue ParentFolder
	 * ist der dritte Folder</li>
	 * </ul>
	 */
	public void testChangeParent() {
		FolderTree tree = getTree();
		final Folder fOne = newFolder(tree, "a title", "");
		final Folder fTwo = newFolder(tree, "title", fOne.getID());
		final Folder fThree = newFolder(tree, "title3", "");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertEquals(fOne, fTwo.getParentFolder());
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		tree.requestChangeParent(fTwo.getID(), fThree.getID());
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.succeeded);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * In diesem Test wird das Löschen eines Folders aus dem FolderTree getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der eigenen Projekte</li>
	 * <li>Öffnen eines Projektes</li>
	 * <li>Laden des Webtrace</li>
	 * <li>Erstellen eines neuen Folders</li>
	 * <li>Löschen des Folders</li>
	 * </ul>
	 * 
	 * Der Folder wird lokal erst gelöscht, nachdem die Bestätigung vom Server
	 * gekommen ist.
	 */
	public void testDeleteFolder() {
		FolderTree tree = getTree();
		final Folder fOne = newFolder(tree, "a title", "");
		tree.requestDeleteFolder(fOne.getID());
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.succeeded);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Legt einen neuen Folder im FolderTree an. Die Aufforderung zum Erstellen
	 * des Folders wird an den Server gesendet und der Folder wird lokal angelegt, 
	 * sobald die Bestätigung vom Server gekommen ist.
	 * @param tree der FolderTree
	 * @param title der Titel des Folders
	 * @param parent die ID des ParentFolders
	 * @return der angelegte Folder
	 */
	protected Folder newFolder(FolderTree tree, String title, String parent) {
		tree.requestNewFolder(title, parent);
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.succeeded);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		listener.succeeded = false;
		return listener.event.getFolder();
	}
	
	/**
	 * Lauscht auf FolderEvents. Über die Events kann festgestellt werden, welche
	 * Antwort der Server gegeben hat.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class MyListener implements FolderTreeListener {
		
		boolean succeeded;
		FolderEvent event;
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.FolderTreeListener#foldersLoaded(posemuckel.client.model.event.FolderEvent)
		 */
		public void foldersLoaded(FolderEvent event) {
			
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.FolderTreeListener#urlsLoaded(posemuckel.client.model.event.FolderEvent)
		 */
		public void urlsLoaded(FolderEvent event) {
			
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.FolderTreeListener#urlChangedParent(posemuckel.client.model.event.FolderEvent)
		 */
		public void urlChangedParent(FolderEvent event) {
			
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.FolderTreeListener#folderChangedParent(posemuckel.client.model.event.FolderEvent)
		 */
		public void folderChangedParent(FolderEvent event) {
			succeeded = true;
			this.event = event;
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.FolderTreeListener#newFolder(posemuckel.client.model.event.FolderEvent)
		 */
		public void newFolder(FolderEvent event) {
			succeeded = true;
			this.event = event;
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.FolderTreeListener#deletedFolder(posemuckel.client.model.event.FolderEvent)
		 */
		public void deletedFolder(FolderEvent event) {
			succeeded = true;
			this.event = event;
		}

	}

}
