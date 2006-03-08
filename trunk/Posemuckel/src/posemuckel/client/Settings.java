/**
 * 
 */
package posemuckel.client;

import posemuckel.common.Message_Handler;
import posemuckel.common.VerboseClientReader;
import posemuckel.common.VerboseServerReader;

/**
 * Es werden einige Flags verwaltet, die die Konsolenausgabe während der Tests regeln.
 * </br>
 * Einstellungsmöglichkeiten zur Konsolenausgabe:
 * <ul>
 * <li>die vom Client gelesenen Zeilen ausgeben</li>
 * <li>die Kopfdaten aus der Klasse MessageHandler ausgeben</li>
 * <li>die Debug-Meldungen in den Testklassen ausgeben</li>
 * </ul>
 * 
 * Außerdem können die Standardparameter für RetriedAssert gesetzt werden. 
 * 
 * @see lib.RetriedAssert
 * @author Posemuckel Team
 *
 */
public class Settings {
	
	/**
	 * Eine von RetriedAssert abgeleitete Klasse schaut alle INTERVALL ms nach,
	 * ob die assert...Tests grün laufen.
	 * @see lib.RetriedAssert#RetriedAssert(int, int)
	 */
	public static final int INTERVALL = 100;
	
	/**
	 * Damit eine von RetriedAssert abgeleitete Klasse nicht in einer Endlosschleife
	 * festhängt, wird der Test nach TIMEOUT ms abgebrochen und als rot gelaufener
	 * Test gezählt.
	 * @see lib.RetriedAssert#RetriedAssert(int, int)
	 */
	public static final int TIMEOUT = 3000;
	
	/**
	 * Dieses Flag regelt, ob Debug-Ausgaben in den einzelnen Testklassen auf der
	 * Konsole ausgegeben werden sollen.
	 */
	static boolean debug = false;
	
	/**
	 * Setzt die Flags für das Debugging.
	 * @param verboseClient true: die vom Client gelesenen Zeilen auf der Konsole ausgeben
	 * @param verboseServer muss direkt im Server eingestellt werden: hier funktioniert
	 * das nicht :-(
	 * @param messageHandler true: Kopfzeilen auf der Konsole ausgeben
	 * @param tests true: debug-Meldungen der Tests auf der Konsole ausgeben
	 */
	public static void setDubuggingMode(boolean verboseClient, boolean verboseServer, boolean messageHandler, boolean tests) {
		VerboseClientReader.isVerbose(verboseClient);
		VerboseServerReader.isVerbose(verboseServer);
		debug = tests;
		Message_Handler.setDebugModus(messageHandler);
	}
	
	/**
	 * Setzt alle Flags wieder zurück.
	 *
	 */
	public static void resetDebuggingMode() {
		setDubuggingMode(false, false, false, false);
	}



}
