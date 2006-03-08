package posemuckel.client.net;

import java.util.Vector;

import junit.framework.TestCase;

/**
 * Dies ist ein langweiliger Testcase, der prüft, ob
 * die Nachrichten-ID auch tatsächlich inkrementiert wird.
 */
public class MessageIDTest extends TestCase {

	/**
	 * Eine Interne Klasse, die als Thread genutzt wird,
	 * um, das korrekte Verhalten bei Aufrufen aus verschiedenen
	 * Threads heraus zu testen.
	 * Die erhaltenen IDs werden in einem Vector abgelegt
	 * und geprüft, ob welche mehrfach vorkommen. 
	 */
	private class FetchID extends Thread {
		private  Vector<Integer> IDs;
		private int id;
		
		public FetchID (Vector<Integer> IDs) {
			this.IDs = IDs;
		}
		
		public void run() {
			for ( int i=0; i <10000 ; i++ ) {
				id = MessageID.getNextID();
				synchronized (IDs) {
					if ( IDs.contains((int)id) ) {
						fail("Die ID  "+id+"  wurde doppelt ausgegeben. Fehler in MessageID.");
					}
					IDs.addElement(id);
				}
			}
		}
	}
	
	/**
	 * Testet 'posemuckel.client.net.MessageID.getID()'
	 */
	public void testGetNextID() {
		for ( int i=0; i<5 ; i++ ) {
			int j = MessageID.getNextID();
			if ( i != j ) {
				fail("Der Nachrichtenzähler in MessageID funktioniert nicht!");
			}
		}
		showThreads();
	}

	/**
	 * Prüft das Verhalten bei Multithreading.
	 */
	public void showThreads() {
		FetchID fetcher;
		Vector<Integer> IDs = new Vector<Integer>();
		for ( int i=0; i <10 ; i++ ) {
			fetcher = new FetchID(IDs);
			fetcher.start();
		}
	}
		
}
