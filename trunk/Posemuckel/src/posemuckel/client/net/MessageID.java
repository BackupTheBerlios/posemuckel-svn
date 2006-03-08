/**
 * 
 */
package posemuckel.client.net;

/**
 * Hier werden �ber statische Methoden Nachrichten-ID erzeugt,
 * mit deren Hilfe der Client Anfragen und Antworten zuordnen kann.
 */
public class MessageID {

	/**
	 * Ein statischer Z�hler f�r die Nachrichten-IDs.
	 */
	public static int id = 0;
	
	/**
	 * Ein synchronisierter Zugriff auf die ID, die dabei
	 * inkrementiert und zur�ckgeliefert wird.
	 * @return Die n�chste Nachrichten-ID.
	 */
	public static synchronized int getNextID() {
			return id++;
	}
	
	/**
	 * Wie getNextID, nur dass die ID als String zur�ck kommt.
	 * @return Die n�chste ID als String.
	 */
	public static synchronized String getNextIDAsString() {
		return String.valueOf(id++);
	}

}
