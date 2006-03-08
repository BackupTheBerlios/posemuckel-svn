package posemuckel.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.TestCase;

public class ClientMessageTest extends TestCase {
//TODO kommentieren
	private Socket socket;
	private String testString;
	private ClientMessage testMessage;
	public PrintWriter out;
	public BufferedReader in;
	
	public ClientMessageTest(){
		try {
			socket = new Socket("localhost", 8081);
	        out = new PrintWriter(
		              new BufferedWriter(
		                new OutputStreamWriter(
		                  socket.getOutputStream())), true);
	        in = new BufferedReader(
	        		new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		testString = "user1\r\nuser2\r\nuser3\r\n";
		testMessage = new ClientMessage(null,null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for
	 * 'posemuckel.server.ClientMessage.getStrings(BufferedReader)'
	 */
	public void testGetStrings() throws IOException{
		BufferedReader in = new BufferedReader(new StringReader(testString));
		Vector testVector = testMessage.getStrings(3, in);
		Iterator it = testVector.iterator();
		int i = 1;
		while (it.hasNext()) {
			String st = (String)it.next();
			assertTrue("Unerwarteter Eintrag!", st.compareTo("user"+i) == 0);
			i++;
		}
	}
//	
//	public void testStartProject(){
//		try {
//		out.print("hash\r\n12\r\n6\r\nWarum geht das alles nicht?\r\n0\r\n23000\r\n" +
//		"Hier kannst du dich mal so richtig auskotzen\r\nHolger\r\nJens\r\n");
//		out.flush();
//		out.print("hash\r\n12\r\n25\r\nWarum geht das alles nicht?\r\n0\r\n23000\r\n" +
//		"Hier kannst du dich mal so richtig auskotzen\r\nHolger\r\nJens\r\n");
//		out.flush();
//		if(in.equals("false")) fail("Falches Format der START_PROJECT-Nachricht");
//
//		}catch (Exception e){
//			fail("Falches Format der START_PROJECT-Nachricht");
//		}
//
//	}
}
