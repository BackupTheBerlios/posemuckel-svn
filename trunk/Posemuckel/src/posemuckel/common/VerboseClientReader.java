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
public class VerboseClientReader extends BufferedReader {
	
	private static boolean verbose;
	
	private static boolean old;
	
	private static Logger logger = Logger.getLogger(VerboseClientReader.class);
	
	
	public VerboseClientReader(Reader in) {
		super(in);
	}
	
	public static void isVerbose(boolean b) {
		verbose = b;
	}
	
	public static void switchOff() {
		old=verbose;
		verbose=false;
	}
	
	public static void switchOn() {
		verbose=old;
	}

	/* (non-Javadoc)
	 * @see java.io.BufferedReader#readLine()
	 */
	@Override
	public String readLine() throws IOException {
		String str =super.readLine(); 
		if(verbose) {
			//str für die bessere Lesbarkeit der Ausgabe verarbeiten
			if(str != null) {
				String print = str.equals("") ? "empty line" : str;
				//zum Testen wird das Gelesene auf die Konsole ausgegeben
				logger.debug(print);
			}
		}
		//es wird der eingelesene String ausgegeben
		return str;
	}

}
