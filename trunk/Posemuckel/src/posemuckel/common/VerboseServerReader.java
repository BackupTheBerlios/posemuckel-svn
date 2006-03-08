/**
 * 
 */
package posemuckel.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;

/**
 * Diese Klasse ist zum Debugge da und gibt das vom Reader gelesene im Gleichschritt
 * mit dem Gelesenen auf der Konsole aus.
 * 
 * @author Posemuckel Team
 *
 */
public class VerboseServerReader extends BufferedReader {
	
	public static boolean verbose;
	private static Logger logger;
	
	public VerboseServerReader(Reader in) {
		super(in);
		logger = Logger.getLogger(this.getClass());
	}
	
	public static void isVerbose(boolean b) {
		verbose = b;
	}

	/* (non-Javadoc)
	 * @see java.io.BufferedReader#readLine()
	 */
	@Override
	public String readLine() throws IOException {
		String str =super.readLine(); 
		if(verbose) {
			//str für die bessere Lesbarkeit der Ausgabe verarbeiten
			if(str == null) {
				//wenn der Stream eine null liest, ist der Eingabestrom zu Ende
				//das kann bei uns zum Beispiel vorkommen, weil der Client die 
				//Verbindung unterbrochen hat
			    logger.debug("InputStream was closed");
			} else {
				String print = str.equals("") ? "empty line" : str;
				//zum Testen wird das Gelesene auf die Konsole
				//ausgegeben
				// kürze den String:
				int end = print.length();
				String towrite;
				if ( end > 70 ) {
					end = 70;
					towrite = print.substring(0,end)+"...";
				} else
					towrite = print;
				logger.debug("server: " + towrite);
			}
		}
		//es wird der eingelesene String ausgegeben
		return str;
	}

}
