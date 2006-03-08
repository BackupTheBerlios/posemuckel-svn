package posemuckel.client.model;

import junit.framework.TestCase;

/**
 * Testet die Methoden der Klasse posemuckel.client.model.Comparator.
 * 
 * @author Posemuckel Team
 *
 */
public class TestComparator extends TestCase {
	
	Comparator comp;	

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		comp = new Comparator();
	}

	/*
	 * Test method for 'posemuckel.client.model.Comparator.compareStrings(String, String)'
	 */
	public void testCompareStrings() {
		String one = "Hello";
		String two = "in the house";
		//Groﬂschreibung vor Kleinschreibung
		assertTrue(comp.compareStrings(one, two) < 0);
		assertEquals(0, comp.compareStrings(one, one));
		assertTrue(comp.compareStrings(two, one) > 0);
	}

	/*
	 * Test method for 'posemuckel.client.model.Comparator.compareIgnoreCase(String, String)'
	 */
	public void testCompareIgnoreCase() {
		String one = "hello";
		String two = "In the house";
		//h vor i
		assertTrue(comp.compareStrings(one, two) > 0);
		assertEquals(0, comp.compareStrings(one, one));
		assertTrue(comp.compareStrings(two, one) < 0);
	}

	/*
	 * Test method for 'posemuckel.client.model.Comparator.compareInteger(String, String)'
	 */
	public void testCompareIntegerStrings() {
		String one = 1+"";
		String two = 2+"";
		//1 ist kleiner 2
		assertTrue(comp.compareInteger(one, two) == -1);
		assertTrue(comp.compareInteger(one, one) == 0);
		assertTrue(comp.compareInteger(two, one) == 1);
	}

	/*
	 * Test method for 'posemuckel.client.model.Comparator.compareFloat(String, String)'
	 */
	public void testCompareFloatStrings() {
		String one = 1.01 + "";
		String two = 1.02+"";
		//1.01 ist kleiner wie 1.02
		assertTrue(comp.compareFloat(one, two) < 0);
		assertTrue(comp.compareFloat(one, one) == 0);
		assertTrue(comp.compareFloat(two ,one) > 0);
	}

	/*
	 * Test method for 'posemuckel.client.model.Comparator.compareBoolean(boolean, boolean)'
	 */
	public void testCompareBoolean() {
		boolean one = true;
		boolean two = false;
		//false ist kleiner wie true
		assertTrue(comp.compareBoolean(two, one) == -1);
		assertTrue(comp.compareBoolean(one, one) == 0);
		assertTrue(comp.compareBoolean(two, two) == 0);
		assertTrue(comp.compareBoolean(one, two) == 1);
	}
}
