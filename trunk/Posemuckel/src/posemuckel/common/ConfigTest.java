package posemuckel.common;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

public class ConfigTest extends TestCase {
	
	
	
	/*
	 * Test method for 'posemuckel.common.Config.getconfig(String)'
	 */
	public void testConfig() {
		try {
			Config myconfig = Config.getInstance();
			myconfig.setconfig("CONFIG_TEST","HalliHallo");
			myconfig.saveToFile(EnumsAndConstants.CLIENT_CONFIG_FILE);
			myconfig.loadFromFile(EnumsAndConstants.CLIENT_CONFIG_FILE);
			String res = myconfig.getconfig("CONFIG_TEST");
			if (!res.equals("HalliHallo"))
				fail("Aus der Datei "+EnumsAndConstants.CLIENT_CONFIG_FILE+" wurde ein unerwarteter Wert gelesen!");
		} catch (FileNotFoundException e) {
			fail("Die Konfigurationsdatei "+EnumsAndConstants.CLIENT_CONFIG_FILE+" konnte nicht gefunden werden.");	
		} catch (IOException e) {
			fail("Fehler beim Lesen der Datei "+EnumsAndConstants.CLIENT_CONFIG_FILE+".");
		}
		
	}

}
