package posemuckel.client.model;

import junit.framework.TestCase;

/**
 * Testet die Methoden der Klasse posemuckel.lang.WebpageComparator.
 * Es kann nach den Kriterien 
 * <ul>
 * <li>Webpage.RATING</li>
 * <li>Webpage.TITLE</li>
 * <li>Webpage.URL</li>
 * </ul>
 * verglichen werden.
 * 
 * @author Posemuckel Team
 *
 */
public class TestWebpageComparator extends TestCase {
	
	private Webpage one;
	private Webpage two;
	private Webpage three;
	private WebpageComparator comp;
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		one = new Webpage("http:\\Jens.de", "neppe", null);
		two = new Webpage("http:\\tanja.de", "Buttler", null);
		three = new Webpage("http:\\bambi.de", "Bambi", null);
	}
	
	/**
	 * Testen der Vergleich der Webseiten nach dem Kriterium 
	 * Webpage.TITLE. Es wird die lexikalische Ordnung ohne Beachtung
	 * von Groﬂ-und Kleinschreibung verwendet.
	 *
	 */
	public void testTitle() {
		comp = new WebpageComparator(Webpage.TITLE);
		assertTrue(comp.compare(two, one) < 0);
		assertEquals(0, comp.compare(one, one));
		assertTrue(comp.compare(one, two) > 0);
		assertTrue(comp.compare(three, two) < 0);
		assertTrue(comp.compare(three, one) <0);
	}
	
	/**
	 * Testen der Vergleich der Webseiten nach dem Kriterium 
	 * Webpage.URL. Es wird die lexikalische Ordnung ohne Beachtung
	 * von Groﬂ-und Kleinschreibung verwendet.
	 *
	 */
	public void testURL() {
		comp = new WebpageComparator(Webpage.URL);
		assertTrue(comp.compare(one, two) < 0);
		assertEquals(0, comp.compare(one, one));
		assertTrue(comp.compare(two, one) > 0);
		assertTrue(comp.compare(three, two) < 0);
		assertTrue(comp.compare(three, one) <0);
	}
	
	/**
	 * Testen der Vergleich der Webseiten nach dem Kriterium 
	 * Webpage.RATING. Webseiten mit hoher Bewertung werden vor
	 * Webseiten mit geringer Bewertung angeordnet.
	 *
	 */
	public void testRATING() {
		comp = new WebpageComparator(Webpage.RATING);
		one.addVisitor("Jens");
		one.updateVisitor("Jens", "1", 3);
		two.addVisitor("tanja");
		two.updateVisitor("tanja", "1", 1);
		three.addVisitor("bambi");
		three.updateVisitor("bambi", "1", 2);
		//hohe Bewertungen kommen zuerst!
		assertTrue(comp.compare(one, two) < 0);
		assertEquals(0, comp.compare(one, one));
		assertTrue(comp.compare(two, one) > 0);
		assertTrue(comp.compare(three, two) < 0);
		assertTrue(comp.compare(three, one) >0);
	}
	
	/**
	 * Wenn ein unbekannter Vergleichstyp gew‰hlt wird, wird eine
	 * IllegalArgumentException geworfen.
	 *
	 */
	public void testUnknownType() {
		try {
			new WebpageComparator("-111");
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException expected) {
			assertEquals("I dont know how to deal with -111", expected.getMessage());
		}
	}
	
	/**
	 * Wenn einer der beiden Vergleichsargumente null ist, wird
	 * eine NullPointerException geworfen.
	 *
	 */
	public void testNull() {
		try {
			comp = new WebpageComparator(Webpage.URL);
			comp.compare(null, one);
			fail("NullPointerException expected");
		} catch (NullPointerException expected) {
		}
		try {
			comp = new WebpageComparator(Webpage.URL); 
			comp.compare(one, null);
			fail("NullPointerException expected");
		} catch(NullPointerException expected) {
		}
	}
}
