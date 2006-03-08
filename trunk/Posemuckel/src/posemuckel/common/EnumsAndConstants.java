package posemuckel.common;


/**
 * Diese Klasse enth�lt Aufz�hlungstypen und Konstanten,
 * die sowohl f�r den Server, wie auch f�r den Client
 * interessant sind.
 * 
 * Wenn Server und Client diese benutzen, k�nnen
 * Missverst�ndnisse besser vermieden werden.
 * 
 * @author Posemuckel Team
 *
 */
public class EnumsAndConstants {

	// Ein Array mit den String zum Geschlecht so wie
	// dies laut RFC �bertragen werden soll.
	public static final String[] GENDER = { "MALE", "FEMALE" }; 
	
	// Ein Array mit den Strings zu den unterst�tzten Sprachen
	// so wie sie �bertragen werden sollen und in der DB
	// eingetragen werden.
	public static final String[] LANG = { "DE", "EN" };
	
	// Der Standard-Port:
	public static final String DefaultPort = "8081"; // als String
	public static final int PORT = 8081; // als Int
	// Die Standard-Adresse:
	public static final String DefaultAddress = "localhost";
	// LS := LineSeparator :-)
	public static final String LS = System.getProperty("line.separator");
	// Position der Konfigurationsdatei des Clients
	public static final String CLIENT_CONFIG_FILE = System.getProperty("user.home")+System.getProperty("file.separator")+".posemuckel_client.cfg";
	
	public static final String LOG_CHAT_ID = "-1";

}
