/**
 * 
 */
package posemuckel.client.net;

/**
 * Hier werden über statische Methoden Nachrichten-ID erzeugt,
 * mit deren Hilfe der Client Anfragen und Antworten zuordnen kann.
 */
public class MessageID {

	/**
	 * Ein statischer Zähler für die Nachrichten-IDs.
	 */
	public static int id = 0;
	
	/**
	 * Ein synchronisierter Zugriff auf die ID, die dabei
	 * inkrementiert und zurückgeliefert wird.
	 * @return Die nächste Nachrichten-ID.
	 */
	public static synchronized int getNextID() {
			return id++;
	}
	
	/**
	 * Wie getNextID, nur dass die ID als String zurück kommt.
	 * @return Die nächste ID als String.
	 */
	public static synchronized String getNextIDAsString() {
		return String.valueOf(id++);
	}

}
