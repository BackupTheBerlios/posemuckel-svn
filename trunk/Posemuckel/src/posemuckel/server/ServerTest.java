package posemuckel.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import junit.framework.TestCase;
import posemuckel.common.Message;

public class ServerTest extends TestCase {

	private final static int PORT = 8081;
	private Socket socket;
	Random rand = new Random();
	private final String randomuser = String.valueOf(rand.nextInt()); 
	private final String randompass = String.valueOf(rand.nextInt());
	private static DB testDB = new DB("root", "lifecycle", "localhost", "posemuckel");
	
	protected void setUp() throws Exception {
		//wird in der Testsuite gestartet
		//Server.startInThread(new String[] {"localhost"});
		connect();
		System.out.println("USER: "+randomuser+"\nPASS: "+randompass);
		super.setUp();
	}	
	
	protected void tearDown() throws Exception {
		disconnect();
		//wird in der TestSuite beendet
		//Server.terminate();
		super.tearDown();
	}
	
	protected void connect() throws Exception {
		System.out.println("Server Status: " + Server.isTerminated());
	    InetAddress addr = InetAddress.getByName(null);

	    // open connection to given IP-Adress
	    addr = InetAddress.getByName("localhost");

	    // open socket to server given by addr on Port Client.PORT
	    socket = new Socket(addr, PORT);
	}

	protected void disconnect() {
	    try
	    {
	      socket.close();
	    } catch (IOException ex) {
	    }
	}
		
	/**
	 * Hier wird das Verhalten eines Clients nachgespielt. Dazu wird ein
	 * zufälliger Username und Passwort erzeugt.
	 * 1. Es wird eine REGISTRIERUNG vorgenommen. Diese muss klappen.
	 * 2. Es wird eine LOGIN Nachricht geschickt. Dabei wird ein ACCESS_GRANTED
	 * 		erwartet und der Hash gespeichert.
	 * 3. Es wird eine CHAT Nachricht geschickt und überprüft, on die Daten in
	 * 		der Antwort-CHAT-Nachricht entsprechend sind.
	 * 4. Eine LOGOUT-Nachricht wird geschickt.
	 * 5. Eine zweite Registrierung wird versucht. Diese muss scheitern.
	 *
	 */
	public void testFakeClient() {
		String[] regData = {randomuser,
					"Panne",
					"peter@sdsajhasdl.xx",
					randomuser,
					randompass,
					"de",
					"male",
					"Meine Adresse",					
					"Kein Kommentar!"
					};

		String message = Message.format("", "0", "9", "REGISTER", regData);
		String reply;
		String hash;
		String id;
		String count;
		try {
			PrintWriter sout = new PrintWriter(
				      new BufferedWriter(
				        new OutputStreamWriter(
				          socket.getOutputStream())), true);
			sout.print(message);
			sout.flush();
			BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
			in.readLine();
			id = in.readLine();
			if (id.compareTo("0") != 0) 
				fail("Bei Registrierung ungültige Message-ID!");
			count = in.readLine();
			if (count.compareTo("0") != 0) 
				fail("Bei Registrierung ungültige Parameteranzahl!");
			reply = in.readLine();	
			if (reply.compareTo("ACK")!=0)
				fail("Die Registrierung des Users "+randomuser+" ist fälschlicherweise gescheitert.");
			// LOGIN
			String[] loginData = {randomuser,
					  randompass};
			sout.print(Message.format("", "1", "2", "LOGIN", loginData));
			sout.flush();
			hash = in.readLine();
			id = in.readLine();
			if (id.compareTo("1") != 0) 
				fail("Bei LOGIN fehlerhafte Message-ID!");
			count = in.readLine();
			if (count.compareTo("0") != 0) 
				fail("Bei LOGIN fehlerhafte Parameteranzahl!");
			reply = in.readLine();
			if (reply.compareTo("ACCESS_GRANTED")!=0)
				fail("Der LOGIN für "+randomuser+" ist fälschlicherweise gescheitert.");
			// Lege einen Chat in der DB an:
			Vector v = new Vector();
			int chatID = testDB.addChat(false, randomuser, v);
			// Schicke eine Chat-Nachricht:
			String[] chatData = {chatID+"", "Dies ist ein Text."};
			sout.print(Message.format(hash, "2", "2", "CHAT", chatData));
			sout.flush();			
			// Der Server arbeitet korrekt, wenn er die CHAT-Nachricht zurück schickt
			// wobei der Hash durch den Benutzernamen ersetzt ist.
			reply = in.readLine();
			if (reply.compareTo(randomuser) != 0) 
				fail("In der CHAT Antwort wird nicht der Nickname des Absenders verschickt.");
			id = in.readLine();
			if (id.compareTo("2") != 0) 
				fail("Bei CHAT fehlerhafte Message-ID!");
			count = in.readLine();
			if (count.compareTo("2") != 0) 
				fail("Bei CHAT fehlerhafte Parameteranzahl!");
			reply = in.readLine();
			if (reply.compareTo("CHAT") != 0) {
				fail("Als Antwort auf eine CHAT-Nachricht wird eine andere Nachricht verschicht als vorgesehen.");
			}
			reply = in.readLine();
			if (reply.compareTo(chatData[0]) != 0) {
				fail("Die Antwort auf eine CHAT-Nachricht enthält eine andere ChatID.");
			}
			reply = in.readLine();
			if (reply.compareTo(chatData[1]) != 0) {
				fail("Die Antwort CHAT-Nachricht enthält einen anderen Text.");
			}
			// LOGOUT
			sout.print(Message.format(hash, "3", "0", "LOGOUT", null));
			sout.flush();
			// Nochmalige Registrierung (muss fehlschlagen)
			sout.print(message);
			sout.flush();
			// Hash, Message-ID und Anzahl der Parameter werden eingelesen,
			// aber nicht getestet
			in.readLine();
			in.readLine();
			in.readLine();
			// die Methodenkennung wird getestet
			reply = in.readLine();
			in.readLine();
			if (reply.compareTo("ACK")==0)
				fail("Die zweite Registrierung des Users "+randomuser+" hat fälschlicherweise geklappt.");
			else if (reply.compareTo("USER_EXISTS")!=0)
				assertTrue(true);
			else fail("Als Antwort auf die zweite Registrierung wurde der String "+reply+" gelesen.");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(true);
	}
/*
	public void testLoginandLogout() {
		String[] loginData = {randomuser,
							  randompass};
		String message = Message.format("", "LOGIN", loginData);

	    try {
			PrintWriter sout = new PrintWriter(
				      new BufferedWriter(
				        new OutputStreamWriter(
				          socket.getOutputStream())), true);
			sout.print(message);
			sout.flush();
			BufferedReader in = new BufferedReader(
		              new InputStreamReader(socket.getInputStream()));
			System.out.println(in.readLine());
			sout.print("data\r\nLOGOUT\r\n\r\n");
			sout.flush();	
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(true);
	}


	public void testChat() {
		String[] loginData = {"Dies ist ein Text."};
		String message = Message.format("date", "CHAT", loginData);

	    try {
			PrintWriter sout = new PrintWriter(
				      new BufferedWriter(
				        new OutputStreamWriter(
				          socket.getOutputStream())), true);
			sout.print(message);
			sout.flush();
			BufferedReader in = new BufferedReader(
		              new InputStreamReader(socket.getInputStream()));
			System.out.println("Antwort des Servers:\n"+in.readLine()+"\r\n"+in.readLine()+"\r\n"+in.readLine()+"\r\n\r\n");
			in.readLine();
			in.readLine();
	    } catch (Exception e) {
			assertTrue(false);
		}
		assertTrue(true);
	}
	*/

}
