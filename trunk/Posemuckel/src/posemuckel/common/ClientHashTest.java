package posemuckel.common;

import junit.framework.TestCase;

/**
 * Dient zum Testen der Klasse <code>ClientHash<code>
 * @author Posemuckel Team
 *
 */
public class ClientHashTest extends TestCase {
	
	/**
	 * Überprüft, ob bei zwei fast identischen Strings unterschiedliche
	 * Hashes berechnet werden.
	 *
	 */
	public void testClientHash() {
		String testString1 = "wiedelifecycle";
		String testString2 = "wiedelifecycl";
		String output1 = ClientHash.getClientHash(testString1);
		String output2 = ClientHash.getClientHash(testString2);
		System.out.println(output1);
		assertTrue("Die erzeugten Digest sind identisch!",
				output1.compareTo(output2) != 0);
	}
}
