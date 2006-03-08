package posemuckel.client.model.event;

import junit.framework.TestCase;

/**
 * Testet die Verwaltung des ListenerManagment.
 * 
 * @author Posemuckel Team
 *
 */
public class ListenerManagmentTest extends TestCase {
	
	private ListenerManagment<PosemuckelListenerDummy> managment;
	private PosemuckelListenerDummy listenerOne;
	private PosemuckelListenerDummy listenerTwo;
	private PosemuckelListenerDummy listenerThree;

	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		managment = new ListenerManagment<PosemuckelListenerDummy>();
		listenerOne = new PosemuckelListenerDummy();
		listenerTwo = new PosemuckelListenerDummy();
		listenerThree = new PosemuckelListenerDummy();
	}

	/**
	 * pr&uuml;ft das Hinzuf&uuml;gen und Entfernen eines Listeners
	 */
	public void testAddAndRemoveListener() {
		assertTrue(managment.isEmpty());
		managment.addListener(listenerOne);
		assertFalse(managment.isEmpty());
		assertTrue(managment.getListener().get(0) == listenerOne);
		managment.removeListener(listenerOne);
		assertTrue(managment.isEmpty());
	}
	
	/**
	 * pr&uuml;ft, ob ein Listener nur einmal eingef&uuml;gt werden kann
	 *
	 */
	public void testDoubleAdd() {
		managment.addListener(listenerOne);
		managment.addListener(listenerOne);
		assertTrue(managment.getListener().size() == 1);
	}
	
	/**
	 * pr&uuml;ft das Entfernen des zweiten Listeners, wenn zwei Listener eingef&uuml;gt wurden
	 *
	 */
	public void testRemoveSecListeners() {
		managment.addListener(listenerOne);
		managment.addListener(listenerTwo);
		managment.removeListener(listenerTwo);
		assertFalse(managment.isEmpty());
		assertTrue(managment.getListener().get(0) == listenerOne);
	}
	
	/**
	 * pr&uuml;ft das Entfernen des ersten Listeners, wenn zwei Listener eingef&uuml;gt wurden
	 *
	 */
	public void testRemoveFirstListeners() {
		managment.addListener(listenerOne);
		managment.addListener(listenerTwo);
		managment.removeListener(listenerOne);
		assertFalse(managment.isEmpty());
		assertTrue(managment.getListener().get(0) == listenerTwo);
	}
	
	/**
	 * pr&uuml;ft das Entfernen des zweiten Listeners, wenn drei Listener eingef&uuml;gt wurden
	 *
	 */
	public void testRemoveMiddleListeners() {
		managment.addListener(listenerOne);
		managment.addListener(listenerTwo);
		managment.addListener(listenerThree);
		managment.removeListener(listenerTwo);
		assertFalse(managment.isEmpty());
		assertTrue(managment.getListener().get(0) == listenerOne);
		assertTrue(managment.getListener().get(1) == listenerThree);
	}


	
	/**
	 * pr&uuml;ft, ob eine NullPointerException bei addListener(null) geworfen wird
	 *
	 */
	public void testAddNULL() {
		try {
			managment.addListener(null);
			fail();
		} catch (NullPointerException expected) {			
		}
	}
	
	/**
	 * pr&uuml;ft, ob eine NullPointerException bei removeListener(null) geworfen wird
	 *
	 */
	public void testRemoveNULL() {
		try {
			managment.removeListener(null);
			fail();
		} catch (NullPointerException expected) {			
		}		
	}
		
	/**
	 * DummyListener zum Testen
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class PosemuckelListenerDummy implements PosemuckelListener {
		
	}
	
}
