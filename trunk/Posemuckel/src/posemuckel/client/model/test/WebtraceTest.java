package posemuckel.client.model.test;

import java.util.ArrayList;

import junit.framework.TestCase;
import posemuckel.client.model.MemberList;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.Project;
import posemuckel.client.model.Root;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.Webtrace;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;

/**
 * Testet die öffentlichen Schnittstellen von Webtrace und Webpage.
 * Damit Webtrace korrekt initialisiert werden kann, wird in der Testfixture
 * das Öffnen eines Projektes und das Laden des Webtrace von der Database
 * simuliert.
 * 
 * @author Posemuckel Team
 *
 */
public class WebtraceTest extends TestCase {
	
	private Webtrace webtrace;
	private Model model;
	private Project project;
	private MyListener listener;

	/**
	 * Die Fixture hat die folgenden Eckdaten:
	 * <ul>
	 * <li>der Anwender ist "tanja"</li>
	 * <li>das Projekt hat zwei weitere Mitglieder: "wiede" und "jens"</li>
	 * <li>das Projekt ist bereits geöffnet</li>
	 * </ul>
	 */
	@Override
	protected void setUp() throws Exception {
		/*
		 * im wesentlichen wird das öffnen eines Projektes simuliert
		 */
		model = new Model();
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
		members.add(new Person("wiede", Person.UNKNOWN));
		members.add(new Person("jens", Person.UNKNOWN));
		model.getAllPersons().confirmLoad(members);
		project.getMemberList().confirmLoad(members);
		//das Projekt öffenen
		model.getAllProjects().confirmOpen(project, "3");
		webtrace = project.getWebtrace();
		listener = new MyListener();
		webtrace.addListener(listener);
	}
	
	/**
	 * Die Korrekte Initialisierung der Testfixture testen:
	 * <ul>
	 * <li>Sind alle Projektmitglieder bekannt?</li>
	 * <li>Ist das Projekt als geöffnet im Model registriert?</li>
	 * <li>Weiß das Projekt, dass es geöffnet ist?</li>
	 * </ul>
	 */
	public void testInit() {
		MemberList all = model.getAllPersons();
		MemberList pm = model.getAllPersons();
		assertEquals(3, all.size());
		assertEquals(Person.ONLINE, all.getMember("tanja").getState());
		assertEquals(all.getMember("tanja"), pm.getMember("tanja"));
		assertEquals(all.getMember("wiede"), pm.getMember("wiede"));
		assertEquals(all.getMember("jens"), pm.getMember("jens"));
		assertEquals(project, model.getOpenProject());
		assertTrue(project.isOpen());
	}
	
	/**
	 * Testet eine VISITING-Nachricht auf einem leeren Webtrace. Es wird die
	 * korrekte Datenaktualisierung des Visitors und der Webpage getestet.
	 *
	 */
	public void testVisiting() {
		String url = "http:\\www.heise.de";
		String parent = "http:\\www.google.de";
		//die Daten, die vom Server kommen, simulieren
		model.informAboutVisiting("jens", url, "Heise", parent);
		//für beide URLs wird eine Nachricht versendet
		assertEquals(2, listener.countV);
		Webpage page = webtrace.getPageForUrl(url);
		//die Daten der page prüfen
		assertNotNull(page);
		assertEquals(url, page.getURL());
		assertEquals("Heise", page.getTitle());
		assertTrue(page.hasFather());
		assertNotNull(page.getVisitorByName("jens"));
		assertTrue(webtrace.hasURL("jens", url, parent));
		assertEquals(webtrace.getPageForUrl(parent), page.getFathers()[0]);
	}
	
	/**
	 * Testet eine VISITING_Nachricht auf einem leeren Webtrace. Die Parent-URL
	 * ist nicht leer. Es wird die korrekte Datenaktualisierung bezüglich
	 * des Visitors und der Webpage für die Parenturl getestet.
	 *
	 */
	public void testVisiting_Parent() {
		String url = "http:\\www.heise.de";
		String parent = "http:\\www.google.de";
		//die Daten, die vom Server kommen, simulieren
		model.informAboutVisiting("jens", url, "Heise", parent);
		Webpage page = webtrace.getPageForUrl(parent);
		assertNotNull(page);
		assertEquals(parent, page.getURL());
		assertEquals("", page.getTitle());
		assertFalse(page.hasFather());
		assertNotNull(page.getVisitorByName("jens"));
		//diese Kombination wird nicht gefunden!
		assertFalse(webtrace.hasURL("jens", parent, ""));
	}
	
	/**
	 * Nach dem Besuch einer Webseite wird diese vom Anwender mit einer 
	 * Bewertung und einer Notiz versehen. Es wird getestet, ob der Visitor 
	 * der Webseite korrekt aktualisiert wird.
	 */
	public void testAddNote() {
		String url = "http:\\www.heise.de";
		String parent = "http:\\www.google.de";
		//die Daten, die vom Server kommen, simulieren
		model.informAboutVisiting("jens", url, "Heise", parent);
		Webpage page = webtrace.getPageForUrl(url);
		//status quo: es wird -1 als default-Wert verwendet
		assertEquals(-1, page.getVisitorByName("jens").getRating());
		//teilt implizit mit, dass der Anwender auch eine Notiz abgegeben hat
		model.newNote("jens", url, 3+"", "1");
		assertEquals(3, page.getVisitorByName("jens").getRating());
		assertTrue(page.getVisitorByName("jens").hasComment());
		//Standardkommentar ist der leere String
		assertEquals("", page.getVisitorByName("jens").getComment());
	}
	
	/**
	 * Führt ein Update des Visitors durch. Es wird getestet, ob die Daten auch
	 * korrekt gesetzt werden.
	 *
	 */
	public void testUpdateVisitor() {
		String url = "http:\\www.heise.de";
		String parent = "http:\\www.google.de";
		//die Daten, die vom Server kommen, simulieren
		model.informAboutVisiting("jens", url, "Heise", parent);
		Webpage page = webtrace.getPageForUrl(url);
		page.updateVisitor("jens", "no comment", 3);
		assertTrue(page.hasNote());
		assertEquals("no comment", page.getVisitorByName("jens").getComment());
		assertEquals(3, page.getVisitorByName("jens").getRating());
	}
	
	/**
	 * Versucht ein Update für einen nichtexistierenden Besucher. Es wird keine
	 * NullPointerException geworfen.
	 *
	 */
	public void testUpdateNonexistendVisitor() {
		String url = "http:\\www.heise.de";
		String parent = "http:\\www.google.de";
		//die Daten, die vom Server kommen, simulieren
		model.informAboutVisiting("jens", url, "Heise", parent);
		Webpage page = webtrace.getPageForUrl(url);
		page.updateVisitor("hans", null, 3);
		assertEquals(1, page.getVisitors().length);
	}
	
	/**
	 * Der Webtrace erhält zwei VISITING-Nachrichten. Durch die zweite
	 * Nachricht ändert sich die Zusammensetzung der Wurzelelemente (sowohl 
	 * des Anwenders als auch des gesamten Webtrace). Es wird getestet, ob
	 * die Änderungen im Webtrace korrekt vorgenommen werden. 
	 *
	 */
	public void testVisiting_RootChange() {
		String url = "http:\\www.heise.de";
		String parent = "http:\\www.google.de";
		//die Daten, die vom Server kommen, simulieren
		Root root = webtrace.getRootForName("jens");
		Root troot = webtrace.getRootElementForWholeTrace(); 
		assertFalse(root.hasChildren());
		assertFalse(troot.hasChildren());
		model.informAboutVisiting("jens", url, "Heise", parent);
		assertEquals(1, listener.countR);
		assertTrue(root.hasChildren());
		assertTrue(troot.hasChildren());
		assertEquals(webtrace.getPageForUrl(parent), root.getChildren()[0]);
		assertEquals(webtrace.getPageForUrl(parent), troot.getChildren()[0]);
	}
	
	/**
	 * Das Projekt bekommt ein neues Mitglied: "holger". Es wird getestet, ob
	 * der Webtrace ein (leeres) Wurzelelement für holger hat.
	 *
	 */
	public void testNewMember() {
		model.informAboutProjectMemberChange("holger", project.getID(), true);
		assertEquals(4, project.getMemberList().size());
		assertNotNull(project.getMemberList().getMember("holger"));
		assertNotNull(webtrace.getRootForName("holger"));
		Root root = webtrace.getRootForName("holger");
		assertFalse(root.hasChildren());
	}
	
	/**
	 * holger besucht eine Webseite. Es wird getestet, ob das Wurzelelement von
	 * holger ein Kindelement hat.
	 *
	 */
	public void testNewMemberVisits() {
		String url = "http:\\www.heise.de";
		String parent = "http:\\www.google.de";
		//die Daten, die vom Server kommen, simulieren
		model.informAboutProjectMemberChange("holger", project.getID(), true);
		model.informAboutVisiting("holger", url, "Heise", parent);
		Root root = webtrace.getRootForName("holger");
		model.informAboutVisiting("jens", url, "Heise", parent);
		assertTrue(root.hasChildren());
		assertEquals(webtrace.getPageForUrl(parent), root.getChildren()[0]);
	}
	
	/**
	 * Durch den Besuch einer URL, die auf eine URL in der Wurzel verweist, ändert
	 * sich die Zusammensetzung sowohl der Wurzel für den gesamten Webtrace als
	 * auch die Zusammensetzung der Wurzel des Anwenders.
	 *
	 */
	public void testRootChange() {
		String url = "http:\\www.heise.de";
		String parent = "http:\\www.google.de";
		String grandparent = "http:\\www.amazon.de";
		//die Daten, die vom Server kommen, simulieren
		model.informAboutVisiting("jens", url, "Heise", parent);
		Webpage page = webtrace.getPageForUrl(parent);
		assertEquals(1, webtrace.getRootElementForWholeTrace().getChildren().length);
		assertEquals(1, webtrace.getRootForName("jens").getChildren().length);
		assertEquals(page, webtrace.getRootElementForWholeTrace().getChildren()[0]);
		assertEquals(page, webtrace.getRootForName("jens").getChildren()[0]);
		//die Wurzeln ändern sich durch durch den nächsten Besuch
		model.informAboutVisiting("jens", parent, "Heise", grandparent);
		page = webtrace.getPageForUrl(grandparent);
		assertEquals(1, webtrace.getRootElementForWholeTrace().getChildren().length);
		assertEquals(1, webtrace.getRootForName("jens").getChildren().length);
		assertEquals(page, webtrace.getRootElementForWholeTrace().getChildren()[0]);
		assertEquals(page, webtrace.getRootForName("jens").getChildren()[0]);
	}
		
	/**
	 * Dieser Listener zählt, wie oft eine Viewing/Visiting-Nachricht und wie
	 * oft eine Änderung der Wurzel eingegangen ist. In einem boolschen Flag wird
	 * festgehalten, ob der Webtrace geladen wurde.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class MyListener extends WebTraceAdapter {
		
		int countV = 0;
		int countR = 0;
		boolean loaded;

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#viewing(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void viewing(WebTraceEvent event) {
			countV++;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#visiting(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void visiting(WebTraceEvent event) {
			countV++;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#rootChanged(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void rootChanged(WebTraceEvent event) {
			countR++;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#traceLoaded(posemuckel.client.model.Webtrace)
		 */
		@Override
		public void traceLoaded(Webtrace webtrace) {
			loaded = true;
		}
	}
}
