package posemuckel.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;


/**
 * Dient der zentralen Verwaltung der Konfiguration einer
 * Anwendung.
 * 
 * @author Posemuckel Team
 */
public class Config {

	/**
	 * Properties
	 */
	private static Properties prop;
	
	/**
	 * Dient zum Einlesen der Konfiguration
	 */
	private static FileInputStream in;
	
	/**
	 * die einzige Konfigurationsinstanz
	 */
	private static Config config = null;
	
	/**
	 * Dient zum Schreiben der Konfiguration in eine Datei
	 */
	private FileOutputStream out;
	
	/**
	 * privater Konstruktor
	 *
	 */
	private Config() {
		prop = new Properties();
	}
	
	/**
	 * Liefert die einzige Instanz der Klasse <code>Config<code>
	 * @return Instanz der Klasse <code>Config<code>
	 */
	public static Config getInstance(){
		if( config == null ) {
			config = new Config();
		}
		return config;
	}
	
	/**
	 * Diese Methode holt die Daten aus der Konfigurationsdatei.
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadFromFile(String filename) throws FileNotFoundException, IOException {
		in = new FileInputStream(filename);
		prop.load(in);
	}

	/**
	 * Diese Methode holt die Daten aus der Konfigurationsdatei.
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void saveToFile(String filename) throws FileNotFoundException, IOException {
		out = new FileOutputStream(filename);
		String comment = " Diese Datei wurde automagisch erzeugt."+EnumsAndConstants.LS;
		comment += "# NICHT von Hand ändern!"+EnumsAndConstants.LS;
		comment += "#"+EnumsAndConstants.LS;
		comment += "# This file has been created automagically."+EnumsAndConstants.LS;
		comment += "# Do NOT edit manually!"+EnumsAndConstants.LS;
		prop.store((OutputStream) out,comment);
	}
	
	/**
	 * Holt den zu key passenden Wert aus der Konfiguration,
	 * sofern vorhanden.
	 * @param key Der Schlüssel aus der Konfigurationsdatei.
	 * @return Der zu key gehörende Wert.
	 */
	public String getconfig(String key) {
		return prop.getProperty(key);
	}

	/**
	 * Setzt ein Schlüssel-Wert paar in der Konfiguration.
	 * @param key Der Schlüssel, der als String übergeben wird
	 * @param value Der Wert, der einen "beliebigen" Typ haben kann.
	 */	
	public void setconfig(String key, String value) {
		prop.put(key, value);
	}
	
}
