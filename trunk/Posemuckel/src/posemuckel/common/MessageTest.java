package posemuckel.common;

import junit.framework.TestCase;

/**
 * 
 * @author Posemuckel Team
 *
 * Diese Klasse testet die Message.java Klasse.
 */

public class MessageTest extends TestCase {

	private String test_msg;
	
	/*
	 * Testfunktion 'posemuckel.common.Message.format(String, String[])'
	 */
	public void testFormat() {
		String[] args = {"Dies ist ein TestÖÄÜ?."};
		test_msg = Message.format("date", "0", "0", "CHAT",args);
		assertTrue( 0 == test_msg.compareTo(
				"date\r\n0\r\n0\r\nCHAT\r\nDies ist ein TestÖÄÜ?.\r\n") );
	}
	
	public final void testLineEndHandler() {
		String[] data = {"Das \r\nist ein \rTest\nmit Zeilenende\r\n"};
		test_msg = Message.format("", "0", "1", "CHAT", data);
		assertTrue( 0 == test_msg.compareTo(
				"\r\n0\r\n5\r\nCHAT\r\nDas \r\nist ein \r\nTest\r\n"+
				"mit Zeilenende\r\n\r\n"));
	}

}
