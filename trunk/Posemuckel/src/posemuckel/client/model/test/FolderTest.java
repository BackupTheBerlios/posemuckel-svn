package posemuckel.client.model.test;

import java.util.ArrayList;

import junit.framework.TestCase;
import posemuckel.client.model.Folder;
import posemuckel.client.model.FolderTree;
import posemuckel.client.model.InformationReceiver;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.Project;
import posemuckel.client.model.Webpage;

/**
 * FolderTest testet die Schnittstelle von der Database zum FolderTree. Die
 * Methoden, mit denen ein FolderTree über Informationen aus der Database 
 * unterrichtet wird, werden direkt aufgerufen, da sich das Task-Konzept 
 * etabliert hat. Die aufzurufenden Methoden befinden sich entweder direkt
 * in der Klasse <code>FolderTree</code> oder sie werden von 
 * <code>InformationReceiver</code> deklariert.
 * 
 * @author Posemuckel Team
 *
 */
public class FolderTest extends TestCase {
	
	private String folderTreeRoot;
	
	private FolderTree folderTree;
	private InformationReceiver informationReceiver;
	private Project project;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		/*
		 * im wesentlichen wird das öffnen eines Projektes simuliert
		 */
		Model model = new Model();
		informationReceiver = model;
		project = new Project(model);
		//das Projekt hat drei Aktive
		project.setData("1", "testen", "tanja", "1", "2", "5", "no", "2006 23 01");
		//Projektliste laden
		ArrayList<Project> pl = new ArrayList<Project>();
		pl.add(project);
		model.getAllProjects().confirmLoad(pl);
		//das Login setzt den Anwendernamen
		model.getUser().login("tanja", "tanja");
		//die Mitglieder laden
		ArrayList<Person> members = new ArrayList<Person>();
		members.add(new Person("tanja", Person.ONLINE));
		members.add(new Person("Sandro", Person.UNKNOWN));
		members.add(new Person("jens", Person.UNKNOWN));
		model.getAllPersons().confirmLoad(members);
		project.getMemberList().confirmLoad(members);
		//das Projekt öffenen
		model.getAllProjects().confirmOpen(project, "3");
		folderTree = project.getFolderTree();
		folderTreeRoot = folderTree.getFolderRoot().getID();
	}
	
	/**
	 * Es wird dem Model über das Interface InformationReceiver mitgeteilt, dass
	 * ein neuer Folder erzeugt wurde. In diesem Test wird überprüft, ob der 
	 * Folder im FolderTree korrekt eingefügt wird und die Daten des Folders auch
	 * über dessen Schnittstelle abgefragt werden können.
	 *
	 */
	public void testNewFolder() {
		//Titel, id, parentID: kein parent
		informationReceiver.informAboutNewFolder("title", "1", folderTreeRoot);
		Folder root = folderTree.getFolderRoot();
		assertTrue(root.hasChildren());
		assertTrue(root.hasSubfolder("1"));
		assertEquals("title", root.getSubfolder("1").getName());
		assertEquals(root, root.getSubfolder("1").getParentFolder());
		assertTrue(root.hasChildren());
	}
	
	/**
	 * In diesem Test wird dem Model über das Interface InformationReceiver 
	 * mitgeteilt, dass ein neuer Folder ohne ParentFolder erzeugt wurde.
	 * Es wird überprüft, ob der neue Folder korrekt in den Baum eingefügt wird.
	 * Als Parent des neuen Folders wird der Folder mit der ID '-1' ausgegeben:
	 * dieser Folder dient als Parent für Folder, die in der Datenbank ohne Parentfolder
	 * geführt werden.
	 *
	 */
	public void testNewRootFolder() {
		informationReceiver.informAboutNewFolder("root", "2", null);
		Folder root = folderTree.getFolderRoot();
		assertTrue(root.hasChildren());
		assertTrue(root.hasSubfolder("2"));
		assertEquals(root.getSubfolders().get(0), root.getSubfolder("2"));
		assertEquals("root", root.getSubfolder("2").getName());
		assertEquals(root, root.getSubfolder("2").getParentFolder());
	}
	
	/**
	 * Testet, ob die Wurzel des FolderTrees <code>null</code> ausgibt, wenn
	 * sie nach ihrem Parent gefragt wird.
	 *
	 */
	public void testParentForFolderRoot() {
		Folder root = folderTree.getFolderRoot();
		assertNull(root.getParentFolder());
	}
	
	/**
	 * Es werden zwei Folder ohne Parent erzeugt. Es wird getestet, ob die 
	 * Wurzel des FolderTrees zwei Unterordner enthält.
	 *
	 */
	public void testTwoSubfolders() {
		informationReceiver.informAboutNewFolder("title", "1", folderTreeRoot);
		Folder root = folderTree.getFolderRoot();
		informationReceiver.informAboutNewFolder("title", "2", folderTreeRoot);
		assertEquals(2, root.getSubfolders().size());		
	}
	
	/**
	 * Hier werden drei Folder erzeugt. Alle drei Folder haben die Wurzel von
	 * FolderTree als Parent. Anschließend wird der InformationReceiver
	 * über einen Wechsel des ParentFolders von Folder 3 informiert. Es wird
	 * getestet, ob die Unterordner der Wurzel des FolderTrees und die Unterordner
	 * des neuen ParentFolder geändert werden.   
	 *
	 */
	public void testChangeParent() {
		informationReceiver.informAboutNewFolder("title", "1", folderTreeRoot);
		informationReceiver.informAboutNewFolder("title", "2", folderTreeRoot);
		informationReceiver.informAboutNewFolder("title", "3", folderTreeRoot);
		informationReceiver.informAboutParentFolderChanged("3", "2");
		assertEquals(folderTree.getFolderForID("2"), folderTree.getFolderForID("3").getParentFolder());
		assertEquals(2, folderTree.getFolderRoot().getSubfolders().size());
		assertEquals(1, folderTree.getFolderForID("2").getSubfolders().size());
	}
	
	/**
	 * Daraus kann man schließen: ein anderer Anwender hat den Folder verschoben,
	 * bevor er in der DB als gelöscht markiert werden konnte. Ich muss den gelöschten
	 * Folder also wiederherstellen 
	 *
	 */
	public void testChangeToNonexistendParent() {
		//TODO der Ansatz ist nicht mehr aktuell!
		informationReceiver.informAboutNewFolder("title", "1", folderTreeRoot);
		informationReceiver.informAboutNewFolder("title", "3", folderTreeRoot);
		informationReceiver.informAboutParentFolderChanged("3", "2");
		//es existiert ein Folder mit der ID 2!
		assertEquals(1, folderTree.getFolderForID("2").getSubfolders().size());
		assertEquals(folderTree.getFolderForID("2"), folderTree.getFolderForID("3").getParentFolder());
		assertEquals(2, folderTree.getFolderRoot().getSubfolders().size());
	}
	
	/**
	 * Ein Folder wird gelöscht: Hat der ParentFolder einen Unterordner weniger?
	 *
	 */
	public void testDeleteFolder() {
		informationReceiver.informAboutNewFolder("title", "1", folderTreeRoot);
		informationReceiver.informAboutNewFolder("title", "3", folderTreeRoot);
		informationReceiver.informAboutFolderRemoval("1");
		assertEquals(1, folderTree.getFolderRoot().getSubfolders().size());
	}
	
	/**
	 * Die Daten zum Aufbau des FolderTrees kommen in einer ArrayListe verpackt an.
	 * Die Daten sind noch im 'Rohformat': es sind die Strings, die in der gleichen
	 * Reihenfolge wie im RFC0815 angeordnet sind. 
	 * Läßt sich mit diesen Daten der FolderTree konstruieren?
	 *
	 */
	public void testBuidFolderTree() {
		ArrayList<String> data = new ArrayList<String>();
		addFolderData(data, "3", "folder3", "2");
		addFolderData(data, "2", "folder2", "");
		folderTree.folderStructureLoaded(data);
		assertEquals(1, folderTree.getFolderRoot().getSubfolders().size());
		assertEquals(folderTree.getFolderRoot(), folderTree.getFolderForID("2").getParentFolder());
		assertEquals(folderTree.getFolderForID("2"), folderTree.getFolderForID("3").getParentFolder());
	}
	
	/**
	 * Der InformationReceiver erhält die Mitteilung, dass eine URL in einen Folder
	 * eingeordnet wurde.
	 * 
	 * Weiß der Folder, dass er eine URL enthält?
	 * Weiß die Webpage der URL, dass sie in einen Folder einsortiert wurde?
	 *
	 */
	public void testAddURLToFolder() {
		folderTree.folderStructureLoaded(new ArrayList<String>());
		folderTree.urlStructureLoaded(new ArrayList<String>());
		informationReceiver.informAboutNewFolder("title", "1", "");
		informationReceiver.informAboutVisiting("Sandro", "www.url.de", "title", "");
		informationReceiver.informAboutParentFolderForUrl("www.url.de", "1");
		Webpage page = project.getWebtrace().getPageForUrl("www.url.de");
		assertNotNull(folderTree.getFolderForID("1").getChildren());
		assertEquals(page, folderTree.getFolderForID("1").getChildren()[0]);
	}
	
	/**
	 * Der Folder einer URL ändert sich. (Der InformationReceiver hat eine entsprechende
	 * Mitteilung bekommen). 
	 * 
	 * Funktioniert das Ändern des ParentFolder einer URL über die gleiche Methode
	 * wie das erste Setzen des ParentFolders?
	 * Werden alle Beziehungen und Daten korrekt geändert?
	 * Bekommt der alte ParentFolder mit, dass er eine URL weniger enthält?
	 *
	 */
	public void testChangeURLFolder() {
		folderTree.folderStructureLoaded(new ArrayList<String>());
		folderTree.urlStructureLoaded(new ArrayList<String>());
		informationReceiver.informAboutNewFolder("title", "1", "");
		informationReceiver.informAboutNewFolder("title2", "2", null);
		informationReceiver.informAboutVisiting("Sandro", "www.url.de", "title", "");
		informationReceiver.informAboutParentFolderForUrl("www.url.de", "1");
		Webpage page = project.getWebtrace().getPageForUrl("www.url.de");
		//die gleiche Methode wie vorher!
		informationReceiver.informAboutParentFolderForUrl("www.url.de", "2");
		assertNotNull(folderTree.getFolderForID("2").getChildren());
		assertEquals(page, folderTree.getFolderForID("2").getChildren()[0]);
		//aus dem alten Folder entfernt
		assertEquals(0, folderTree.getFolderForID("1").getChildren().length);
	}
	
	/**
	 * Dem InformationReceiver wird mitgeteilt, dass eine URL keinen ParentFolder mehr
	 * hat. 
	 * 
	 * Funktioniert das Entfernen des ParentFolders mit der gleichen Methode, die
	 * auch einen neuen ParentFolder setzt?
	 * Werden die Beziehungen und Daten korrekt geändert?
	 *
	 */
	public void testRemoveURLFromFolder() {
		folderTree.folderStructureLoaded(new ArrayList<String>());
		folderTree.urlStructureLoaded(new ArrayList<String>());
		informationReceiver.informAboutNewFolder("title2", "2", null);
		informationReceiver.informAboutVisiting("Sandro", "www.url.de", "title", "");
		Webpage page = project.getWebtrace().getPageForUrl("www.url.de");
		informationReceiver.informAboutParentFolderForUrl("www.url.de", "2");
		//mit null wird gelöscht
		assertEquals(page, folderTree.getFolderForID("2").getChildren()[0]);
		informationReceiver.informAboutParentFolderForUrl("www.url.de", "");
		assertNull(page.getParentFolder());
		assertEquals(0, folderTree.getFolderForID("2").getChildren().length);
		
	}
	
	/**
	 * Neben der Folderstruktur werden auch Informationen zur Einordnung von URLs
	 * in die Folder geladen. Werden die URLs korrekt in die Folder einsortiert?
	 * 
	 *
	 */
	public void testLoadFolderUrlStructure() {
		ArrayList<String> folders = new ArrayList<String>();
		addFolderData(folders, "3", "folder3", "2");
		addFolderData(folders, "2", "folder2", "");
		folderTree.folderStructureLoaded(folders);
		informationReceiver.informAboutVisiting("Sandro", "www.url.de", "title", "");
		informationReceiver.informAboutVisiting("Sandro", "www.url2.de", "title2", "");
		informationReceiver.informAboutVisiting("Sandro", "www.url3.de", "title3", "");
		//Folder und Webpages müssen bekannt sein, sonst wird keine Garantie übernommen
		ArrayList<String> urls = new ArrayList<String>();
		addStructureData(urls, "www.url.de", "3");
		addStructureData(urls, "www.url2.de", "2");
		addStructureData(urls, "www.url3.de", "3");
		folderTree.urlStructureLoaded(urls);
		assertNotNull(folderTree.getFolderForID("2").getChildren());
		assertEquals(1, folderTree.getFolderForID("2").getChildren().length);
		assertEquals(2, folderTree.getFolderForID("3").getChildren().length);
	}
	
	//ArrayListe mit den FolderDaten verarbeiten
	
	/*
	 * fügt die Daten in die Listen ein; es wird die gleiche Reihenfolge wie
	 * in NEW_FOLDER verwendet
	 */
	private void addFolderData(ArrayList<String> list, String id, String name, String parent) {
		list.add(id);
		list.add(name);
		list.add(parent);
	}
	
	/*
	 * fügt die Daten in die Liste ein: erst die URL und dann die FolderID
	 */
	private void addStructureData(ArrayList<String> list, String url, String folderID) {
		list.add(folderID);
		list.add(url);
	}
}
