/**
 * 
 */
package posemuckel.client.model.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Liest einen Webtrace aus einer Datei ein (Format wie im RFC ohne Header)
 * und konstruiert daraus eine ArrayListe mit Strings.
 * @author Posemuckel Team
 *
 */
public class WebtraceFileReader {
	
	/**
	 * Liest einen Webtrace aus einer Datei aus und verpackt ihn zeilenweise in
	 * einer ArrayList.
	 * @param path Pfad zur Datei mit dem Webtrace
	 * @return Zeilen des Webtrace
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ArrayList<String> readFile(String path) throws FileNotFoundException, IOException {
		ArrayList<String> lines = new ArrayList<String>();
		File file = new File(path);
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = null;
		while((line = in.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

}
