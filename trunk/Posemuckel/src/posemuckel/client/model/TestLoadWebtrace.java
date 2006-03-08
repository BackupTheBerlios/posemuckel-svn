package posemuckel.client.model;

import java.util.ArrayList;

import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;

import junit.framework.TestCase;

/**
 * Es werden die Methoden, die in Webtrace als protected gekennzeichnet sind, getestet.
 * Insbesondere wird confirmLoad getestet.
 * 
 * @author Posemuckel Team
 *
 */
public class TestLoadWebtrace extends TestCase {
	
	private Webtrace trace;
	private ArrayList<String> data;
	private MyListener listener;

	protected void setUp() throws Exception {
		trace = new Webtrace(new Project());
		data = new ArrayList<String>();
		listener = new MyListener();
		trace.addListener(listener);
	}
	
	/**
	 * Die Nachricht vom Server enthält genau eine URL, mit einer Vaterurl, die
	 * im Webtrace nicht bekannt ist. Es wird getestet, ob die Vaterurl und die 
	 * URL in den Webtrace eingefügt werden und eine Vater-Kind-Beziehung erfüllen.
	 * 
	 * Die Benachrichtigung der Listener wird während des Ladens abgeschaltet.
	 * Die Abfrage von Webtrace#isLoaded wird ebenfalls überprüft.
	 */
	public void testLoadOneURL() {		
		addURL(data, "url", "father", "title", "visitor");
		addNoVistorData(data);
		assertFalse(trace.isLoaded());
		trace.confirmLoad(data);
		assertTrue(trace.isLoaded());
		//die url ist nicht null
		assertFalse(trace.getPageForUrl("url", "visitor").hasNote());
		assertEquals("title", trace.getPageForUrl("url").getTitle());
		//der Vater ist auch bekannt
		assertFalse(trace.getPageForUrl("father", "visitor").hasNote());
		assertEquals("", trace.getPageForUrl("father").getTitle());
		//testet Vater-Kind-Beziehungen
		assertEquals(0, trace.getPageForUrl("father").getFathers().length);
		assertEquals(trace.getPageForUrl("father"), trace.getPageForUrl("url").getFathers()[0]);
		//der Listener wurde von viewing/visiting nicht benachrichtigt
		assertEquals(0, listener.count);
		assertTrue(listener.loaded);
	}
	
	/**
	 * Die Nachricht vom Server enthält genau eine URL, mit einer Vaterurl, die
	 * im Webtrace nicht bekannt ist. Zu der URL hat der Besucher eine Notiz geschrieben
	 * und eine Bewertung abgegeben. Es wird getestet, ob das Rating korrekt berechnet
	 * wird und die Notiz im Webtrace vorhanden ist.
	 *
	 */
	public void testLoadOneURLWithNote() {
		addURL(data, "url2", "father2", "title2", "visitor2");
		addVisitorData(data, "3", "1");
		trace.confirmLoad(data);
		Webpage url2 = trace.getPageForUrl("url2", "visitor2");
		assertTrue(url2.hasNote());
		// 3/5 als float ist das Rating
		assertEquals(0.6f, url2.getRating());
		assertEquals(1, url2.getVisitors().length);
		Visitor visitor = url2.getVisitorByName("visitor2");
		assertEquals("3", visitor.getRatingAsString());
		assertTrue(visitor.hasComment());
	}
	
	/**
	 * Es wird erst der Vater und dann das Kind in den Webtrace eingebaut. Das ist
	 * die normale Reihenfolge beim Laden. Es wird getestet, ob die Vater-Kind-
	 * Beziehung korrekt aufgebaut wird.
	 *
	 */
	public void testLoadFatherChild() {
		addURL(data, "father", "", "fathersTitle", "fathersVisitor");
		addNoVistorData(data);
		addURL(data, "child", "father", "title", "visitor");
		addNoVistorData(data);
		trace.confirmLoad(data);
		assertEquals("fathersTitle", trace.getPageForUrl("father").getTitle());
		//testet Vater-Kind-Beziehungen
		assertEquals(0, trace.getPageForUrl("father").getFathers().length);
		assertEquals(trace.getPageForUrl("father"), trace.getPageForUrl("child").getFathers()[0]);
	}
	
	/**
	 * Die Nachricht vom Server enthält zwei URL-Datensätze. Die Vaterurl des ersten
	 * Datensatzes ist die Kindurl des zweiten Datensatzes. Es wird getestet, ob
	 * die URLs aus dem zweiten Datensatz in den Webtrace eingefügt werden und
	 * ob die Vater-Kind-Beziehung beim zweiten Datensatz korrekt aufgebaut wird.
	 * Außerdem wird geprüft, ob der Titel der URL aus dem zweiten Datensatz aktualisiert
	 * wird.
	 * 
	 * Die Benachrichtigung der Listener wird während des Ladens abgeschaltet.
	 */
	public void testLoadTwoURLs() {
		addURL(data, "child", "father", "title", "visitor");
		addNoVistorData(data);
		addURL(data, "father", "grandfather", "fathersTitle", "fathersVisitor");
		addNoVistorData(data);
		trace.confirmLoad(data);
		assertEquals("fathersTitle", trace.getPageForUrl("father").getTitle());
		//testet Vater-Kind-Beziehungen
		assertEquals(0, trace.getPageForUrl("grandfather").getFathers().length);
		assertEquals(trace.getPageForUrl("grandfather"), trace.getPageForUrl("father").getFathers()[0]);
		//der Listener wurde von viewing/visiting nicht benachrichtigt
		assertEquals(0, listener.count);
		assertTrue(listener.loaded);
	}
	
	/**
	 * Eine Webseite wird von zwei Anwendern besucht und bewertet. Es wird die
	 * Gesamtbewertung berechnet und getestet, ob bei der Webseite zwei Anwender
	 * als Besucher registriert sind.
	 *
	 */
	public void testLoadOneURLWithTwoVisitors() {
		addURL(data, "child", "father", "title", "visitorOne");
		addVisitorData(data, "3", "1");
		addURL(data, "child", "father", "title", "visitorTwo");
		addVisitorData(data, "2", "1");
		trace.confirmLoad(data);
		assertEquals(2, trace.getPageForUrl("child").getVisitors().length);
		// 5/10 als float
		assertEquals(0.5f, trace.getPageForUrl("child", "visitorTwo").getRating());
	}
		
	/**
	 * Es werden zwei Kinder zu einer Vaterurl hinzugefügt. Es wird getestet, ob
	 * ob die URLs korrekt in die Wurzeln eingefügt werden. Die Vaterurl muss
	 * zwei Kinder haben.
	 *
	 */
	public void testSameRoots() {
		addURL(data, "child", "father", "title", "visitor");
		addNoVistorData(data);
		addURL(data, "child2", "father", "title", "visitor");
		addNoVistorData(data);
		trace.confirmLoad(data);
		//
		assertTrue(trace.getRootElementForWholeTrace().hasChildren());
		assertTrue(trace.getRootElementForWholeTrace().hasChild("father"));
		assertTrue(trace.getRootForName("visitor").hasChildren());
		assertTrue(trace.getRootForName("visitor").hasChild("father"));
		assertEquals(2, trace.getPageForUrl("father").getChildren().length);
	}
	
	/**
	 * Die Nachricht vom Server enthält zwei URLs. Es wird getestet, ob die 
	 * Wurzelelemente korrekt aktualisiert werden.
	 *
	 */
	public void testDifferentRoots() {
		addURL(data, "child", "father", "title", "visitor");
		addNoVistorData(data);
		addURL(data, "father", "grandfather", "fathersTitle", "fathersVisitor");
		addNoVistorData(data);
		trace.confirmLoad(data);
		//
		assertTrue(trace.getRootElementForWholeTrace().hasChildren());
		assertTrue(trace.getRootElementForWholeTrace().hasChild("grandfather"));
		assertTrue(trace.getRootForName("visitor").hasChildren());
		assertTrue(trace.getRootForName("visitor").hasChild("father"));
		assertTrue(trace.getRootForName("fathersVisitor").hasChildren());
		assertTrue(trace.getRootForName("fathersVisitor").hasChild("grandfather"));
	}
		
	/**
	 * Prüft, ob die Listener nach dem Laden wieder aktiviert werden.
	 *
	 */
	public void testVisitAfterLoad() {
		addURL(data, "child", "father", "title", "visitor");
		addNoVistorData(data);
		trace.confirmLoad(data);
		trace.informAboutNewVisit("grandchild", "child", "grandTitle", "visitor");
		assertEquals(1, listener.count);
	}
	
	/**
	 * Wenn der Webtrace keine Webpage zu einer URL hat oder ein Besucher eine
	 * Webpage mit bekannter URL nicht besucht hat, wird null ausgegeben.
	 *
	 */
	public void testGetPageForInvalidURL() {
		addURL(data, "url", "", "title", "visitor");
		addNoVistorData(data);
		trace.confirmLoad(data);
		assertNull(trace.getPageForUrl("unknown"));
		assertNull(trace.getPageForUrl("url", "unknownVisitor"));
		assertNull(trace.getPageForUrl("unknown", "unknownVisitor"));
		assertNull(trace.getPageForUrl("unknown", "visitor"));
	}
	
	/**
	 * Fügt eine URL zu der Datenliste hinzu. 
	 * @param data Datenliste
	 * @param url der Webseite
	 * @param previous Webseite mit dem Link, dem jemand gefolgt ist
	 * @param title der Webseite
	 * @param visitor Benutzername der Person, die den Link verwendet hat
	 */
	private void addURL(ArrayList<String> data, String url, String previous, 
			String title, String visitor) {
		data.add(url);
		data.add(previous);
		data.add(title);
		data.add(visitor);
	}
	
	/**
	 * Simuliert die Daten, die ein Anwender hinterläßt, der weder eine Notiz
	 * angefügt noch eine Bewertung abgegeben hat. Zu Details siehe
	 * RFC0815.
	 * @param data Datenliste
	 */
	private void addNoVistorData(ArrayList<String> data) {
		data.add("-1");
		data.add("");
	}
	
	/**
	/**
	 * Simuliert die Daten, die ein Anwender hinterläßt, der eine Notiz
	 * angefügt und eine Bewertung abgegeben hat. Zu Details siehe
	 * RFC0815.
	 * @param data Datenliste
	 * @param rating Bewertung des Anwenders als String
	 * @param hasNote hat der Anwender eine Notiz geschrieben?
	 */
	private void addVisitorData(ArrayList<String> data, String rating, String hasNote) {
		data.add(rating);
		data.add(hasNote);
	}
	
	/**
	 * Zählt die Benachrichtigungen für Visited/Viewing/RootChanged-Events.
	 * Hält fest, wenn eine trace-Loaded-Nachricht gesendet wird.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class MyListener extends WebTraceAdapter {
		
		int count = 0;
		boolean loaded;

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#viewing(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void viewing(WebTraceEvent event) {
			count++;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#visiting(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void visiting(WebTraceEvent event) {
			count++;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#traceLoaded(posemuckel.client.model.Webtrace)
		 */
		@Override
		public void traceLoaded(Webtrace webtrace) {
			loaded = true;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#rootChanged(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void rootChanged(WebTraceEvent event) {
			count++;
		}
		
		
		
		
		
	}

}
