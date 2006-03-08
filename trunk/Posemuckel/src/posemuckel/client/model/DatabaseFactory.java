/**
 * 
 */
package posemuckel.client.model;

import posemuckel.client.model.test.BlockedBase;
import posemuckel.client.model.test.ErrorBase;
import posemuckel.client.model.test.HeavyMockbase;
import posemuckel.client.model.test.Mockbase;
import posemuckel.client.net.Netbase;

/**
 * Erstellt eine Database. F&uuml;r die Applikation sollte der Typ 
 * <code>USE_NETWORK</code> verwendet werden. Damit die JUnitTests
 * nicht alle &uuml;ber den Server laufen m&uuml;ssen, gibt es auch
 * diverse Mockups.
 * 
 * @author Posemuckel Team
 *
 */
public class DatabaseFactory {

	private static Database db;
	
	/**
	 * DummyDatenbank zum Testen. Bei einigen Anforderungen wird keine Antwort gegeben.
	 */
	public static int USE_BLOCKED = 0;
	
	/**
	 * DummyDatenbank zum Testen des Models.
	 */
	public static int USE_MOCKBASE = 1;
	
	/**
	 * die in der Applikation verwendete Datenbank. Es wird &uuml;ber eine Netzwerk
	 * verbindung auf einen Server zugegriffen.
	 */
	public static int USE_NETWORK = 2;
	
	/**
	 * DummyDatenbank zum Testen der GUI. Die Anfragen an die Datenbank werden
	 * in separaten Threads ausgef&uuml;hrt.
	 */
	public static int USE_THREADBASE = 3;
	
	/**
	 * Liefert zu ausgewählten Anfragen Fehlermeldungen an die Tasks zurück.
	 */
	public static int USE_ERRORBASE = 4;
	
	/**
	 * Erstellt eine Database des angegebenen Typs. 
	 * 
	 * @param type Typ der Database
	 */
	public static void createRegistry(int type) {
		if(type == USE_BLOCKED) {
			db = new BlockedBase();
		} else if (type == USE_MOCKBASE){
			db = new Mockbase();
		} else if (type == USE_NETWORK) {
			db = new Netbase();
		} else if (type == USE_THREADBASE) {
			db = new HeavyMockbase();
		} else if (type == USE_ERRORBASE) {
			db = new ErrorBase();
		} else throw new IllegalArgumentException("this type doesnt exist");	
		
	}
	
	/**
	 * Gibt ein einfaches Modell für die Datenbank zurück.
	 * @return eine systemweit eindeutige Database
	 */
	public static Database getRegistry() {
		return db;
	}


}
