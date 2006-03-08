package posemuckel.common;

import junit.framework.TestCase;
import java.io.*;

public class Message_HandlerTest extends TestCase {
	
	/**
	 * Eine von Message_Handler abgeleitete Klasse,
	 * die die Methoden für Chat, Login und Logout implementiert.
	 *
	 * @author Posemuckel Team
	 *
	 */
	private class TestHandler extends Message_Handler {
	protected boolean chat(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException {
		String buf;
		System.out.print("Chat: ");
		try {
			buf = in.readLine();
			System.out.println(buf);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return true;
	}
	
	protected boolean logout(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException {
		System.out.println("Logout.");
		return true;
	}
	
	protected boolean login(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException {
		System.out.print("Login: ");
		try {
			System.out.print(" "+in.readLine());
			System.out.println(" "+in.readLine());
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return true;
	}

	}	
	
	private TestHandler packet_eater;
	private StringReader is_reader;
	private String test_msg;
	
	/**
	 *  Ein bisschen Set-Up.
	 * 
	 */
	protected void setUp() {
		test_msg =  "date\r\n0\r\n2\r\nLOGIN\r\nPittyPlatsch\r\nSchnatter\r\n";
		test_msg += "date\r\n0\r\n2\r\nCHAT\r\ndfhsjdk wqsalkyxcncn äöpüü^8398327&/$§=)?ß\r\n";
		test_msg += "date\r\n0\r\n2\r\nLOGOUT\r\n";
		test_msg += "BlabLa\r\n0\r\n2\r\njkahdkajshdsakjdh\r\n";
		test_msg += "BlabLa\r\n0\r\n2\r\njkahdkajshdsakjdh\r\n";
		test_msg += "date\r\n0\r\n2\r\nLOGOUT\r\n";
		
		packet_eater = new TestHandler();
		is_reader = new StringReader(test_msg);
	}
	
	
	/**
	 * 
	 * Testet, ob die Methode eat_up_packet die passenden Methoden aufruft.
	 *
	 */
	public void testEat_up_packet() {
		boolean ret = true;
		BufferedReader in = new BufferedReader(is_reader);
		try {
			ret = packet_eater.eat_up_ClientPacket(in);
			if( ret == false )
				fail("Fehler beim Lesen des LOGIN-Pakets.");
			ret = packet_eater.eat_up_ClientPacket(in);
			if( ret == false )
				fail("Fehler beim Lesen des CHAT-Pakets.");
			ret = packet_eater.eat_up_ClientPacket(in);
			if( ret == false )
				fail("Fehler beim Lesen des 1. LOGOUT-Pakets.");
			ret = packet_eater.eat_up_ClientPacket(in);
			if( ret == true )
				fail("Ausbleibende Fehlermeldung beim Lesen von unbekanntem Paket.");
			ret = packet_eater.eat_up_ClientPacket(in);
			if( ret == true )
				fail("Ausbleibende Fehlermeldung beim Lesen von unbekanntem Paket.");
			ret = packet_eater.eat_up_ClientPacket(in);
			if( ret == false )
				fail("Fehler beim Lesen des 2. LOGOUT-Pakets.");
		} catch (Exception e) {
			System.out.println(e.toString());
			assertTrue(false);
		}
		assertTrue(true);
	}
	

}
