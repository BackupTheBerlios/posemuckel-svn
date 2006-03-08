package posemuckel.client.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.test.WebtraceFileReader;

/**
 * Testet den Fehler bei der Anzeige von AllUrls im Projekt Posemckel.
 * 
 * @author Posemuckel Team
 *
 */
public class TestWebtraceRoots extends TestCase {
	
	private static final String file = "bin/posemuckel/client/model/test/trace";
	
	private Webtrace webtrace;
	private Model model;
	private Project project;
	private MyListener listener;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		model = new Model();
		project = new Project(model);
		//das Projekt hat drei Aktive
		project.setData("1", "testen", "Tanja", "1", "2", "5", "no", "2006 23 01");
		//Projektliste laden
		ArrayList<Project> pl = new ArrayList<Project>();
		pl.add(project);
		model.getAllProjects().confirmLoad(pl);
		//das Login setzt den Anwendernamen
		model.getUser().login("Tanja", "tanja");
		//die Mitglieder laden
		ArrayList<Person> members = new ArrayList<Person>();
		members.add(new Person("Tanja", Person.ONLINE));
		members.add(new Person("wiede", Person.UNKNOWN));
		members.add(new Person("Jens", Person.UNKNOWN));
		members.add(new Person("stephan", Person.UNKNOWN));
		members.add(new Person("Holger", Person.UNKNOWN));
		members.add(new Person("Lars", Person.UNKNOWN));
		members.add(new Person("Sandro", Person.UNKNOWN));
		model.getAllPersons().confirmLoad(members);
		project.getMemberList().confirmLoad(members);
		//das Projekt öffenen
		model.getAllProjects().confirmOpen(project, "3");
		webtrace = project.getWebtrace();
		listener = new MyListener();
		webtrace.addListener(listener);		
	}

	/**
	 * Liest den Webtrace aus einer Datei aus und packt ihn zeilenweise in eine
	 * ArrayList.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void testParsing() throws FileNotFoundException, IOException {
		ArrayList<String> lines = WebtraceFileReader.readFile(file);
		assertEquals("Fehler beim parsen: die Zeilenzahl stimmt nicht", 1038, lines.size());
	}
	
	/**
	 * Testet das Laden des Webtrace. Der Listener sollte benachrichtigt
	 * werden.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 *
	 */
	public void testLoading() throws FileNotFoundException, IOException {
		webtrace.confirmLoad(WebtraceFileReader.readFile(file));
		assertTrue("der Listener wurde nicht benachrichtigt", listener.loaded);
		assertEquals("falsche Anzahl von Root-Children bei Tanja", 6, webtrace.getUserRoot("Tanja").getChildren().length);
		assertEquals("falsche Anzahl von Root-Children bei Jens", 14, webtrace.getUserRoot("Jens").getChildren().length);
		assertEquals("falsche Anzahl von Root-Children bei wiede", 9, webtrace.getUserRoot("wiede").getChildren().length);
		assertEquals("falsche Anzahl von Root-Children bei stephan", 4, webtrace.getUserRoot("stephan").getChildren().length);
		assertEquals("falsche Anzahl von Root-Children bei Holger", 3, webtrace.getUserRoot("Holger").getChildren().length);
		assertEquals("falsche Anzahl von Root-Children bei Lars", 7, webtrace.getUserRoot("Lars").getChildren().length);
		assertEquals("falsche Anzahl von Root-Children bei Sandro", 6, webtrace.getUserRoot("Sandro").getChildren().length);
	}
	
	/**
	 * Testet die Details des Webtrace: für jede URL wird deren Titel, der Visitor
	 * (incl. Daten) und die VaterURL getestet.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void testWebpages() throws FileNotFoundException, IOException {
		webtrace.confirmLoad(WebtraceFileReader.readFile(file));
		ArrayList<String> list = WebtraceFileReader.readFile(file);
		Webpage testedPage = null;
		Visitor testedVisitor = null;
		Webpage testedFather = null;
		for (int i = 0; i < list.size(); i+=6) {
			testedPage = webtrace.getPageForUrl(list.get(i));
			//die Daten der Webpage testen
			assertNotNull("die URL wurde nicht in den Webtrace eingefügt", testedPage);
			assertEquals("der Titel stimmt nicht", list.get(i+2), testedPage.getTitle());
			//die Daten des Visitors testen
			testedVisitor = testedPage.getVisitorByName(list.get(i+3));
			assertNotNull("der Visitor wurde nicht eingetragen", testedVisitor);
			assertEquals("Bewertung falsch", list.get(i+4), testedVisitor.getRatingAsString());
			assertEquals("die Notiz stimmt nicht", list.get(i+5).equals("1"), testedVisitor.hasComment());
			//die Daten der Vaterpage testen
			//es existiert ein Vater UND der Vater ist ungleich der URL
			if(list.get(i+1).length() > 0 && !list.get(i+1).equals(list.get(i))) {
				testedFather = webtrace.getPageForUrl(list.get(i+1));
				assertNotNull("der Vater wurde nicht in den Webtrace eingefügt", testedFather);
				assertTrue("der Vater wurde nicht beim Kind eingetragen", testedPage.hasFather(list.get(i+1)));
				assertNotNull("der Visitor wurde beim Vater nicht eingetragen", testedFather.getVisitorByName(list.get(i+3)));
				assertTrue("der Vater hat die URL des Kindes nicht", testedFather.hasChild(list.get(i)));
			}
		}
	}
	
	/**
	 * Überprüft die Wurzelelemente der UserRoot von Sandro.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void testSandrosRoot() throws FileNotFoundException, IOException {
		webtrace.confirmLoad(WebtraceFileReader.readFile(file));
		Root sandro = webtrace.getUserRoot("Sandro");
		assertTrue(sandro.hasChild("http://dict.leo.org/cgi-bin/dico/forum.cgi?action=show&sort_order=&list_size=&list_skip=0&group=forum003_correct&file=20051101134503"));
		assertTrue(sandro.hasChild("http://www.dogpile.com/"));
		assertTrue(sandro.hasChild("http://de.wikipedia.org/wiki/Bild:Einwohner.jpg"));
		assertTrue(sandro.hasChild("http://posemuckel.no-ip.org/index.php?op=pubproj&id=1"));
		assertTrue(sandro.hasChild("http://posemuckel.kleine-planeten.de/"));
		assertTrue(sandro.hasChild("http://posemuckel.kleine-planeten.de/index.htm"));
		//da die Wurzel nur 6 Kinder hat, sind alle anderen Kinder nicht enthalten
	}
	
	/**
	 * Überprüft die die Wurzelelemente der UserRoot von Stephan.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void testStephansRoot() throws FileNotFoundException, IOException {
		webtrace.confirmLoad(WebtraceFileReader.readFile(file));
		Root stephan = webtrace.getUserRoot("stephan");
		assertTrue(stephan.hasChild("http://www.google.de/"));
		assertTrue(stephan.hasChild("http://www.stauff.de/paedagogik/dateien/surfenscrollen.htm"));
		assertTrue(stephan.hasChild("http://www.artmarine.co.uk/"));
		assertTrue(stephan.hasChild("http://www.de.map24.com/"));
		assertFalse(stephan.hasChild("http://www.redensarten-index.de/liste/2004/6145.php"));
		assertFalse(stephan.hasChild("http://www.google.de/search?q=posemuckel&hl=de&lr=&start=10&sa=N"));
	}
	
	/**
	 * Überprüft die die Wurzelelemente der UserRoot von Holger.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void testHolgersRoot() throws FileNotFoundException, IOException {
		webtrace.confirmLoad(WebtraceFileReader.readFile(file));
		Root holger = webtrace.getUserRoot("Holger");
		assertTrue(holger.hasChild("http://www.google.de/"));
		assertTrue(holger.hasChild("http://de.wikipedia.org/wiki/Bild:Einwohner.jpg"));
		assertTrue(holger.hasChild("http://www.posemuckel.de/"));
		assertFalse(holger.hasChild("http://posemuckel.no-ip.org/index.php?op=pubproj&id=1"));		
	}
	
	/**
	 * Dieser Listener hält in einem boolschen Flag 
	 * fest, ob der Webtrace geladen wurde.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class MyListener extends WebTraceAdapter {
		
		boolean loaded;

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#traceLoaded(posemuckel.client.model.Webtrace)
		 */
		@Override
		public void traceLoaded(Webtrace webtrace) {
			loaded = true;
		}
	}

}
