package posemuckel.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import junit.framework.TestCase;

public class DBTest extends TestCase {
	
	/**
	 * Testdatenbank
	 */
	private DB testDB;
	
	/**
	 * speichert die erzeugten ProjektIDs zwischen, damit sie in tear-Down wieder 
	 * gelöscht werden müssen
	 */
	private Vector<Integer> projectIDs;


	/**
	 * Die Testdatenbank wird initialisiert
	 */
	protected void setUp() throws Exception {
		super.setUp();
		projectIDs = new Vector<Integer>();
		testDB = new DB("root", "lifecycle", "localhost", "posemuckel");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		/*TODO was für einen Test an Daten in die Datenbank eingetragen wird,
		 * muss in tearDown wieder gelöscht werden, sonst bleiben die 
		 * Einträge für den nächsten Test erhalten und es ergibt sich ein
		 * Kaskadeneffekt
		 * 
		 * tearDown wird nach jedem Test aufgerufen, die Methoden zum Löschen von
		 * Dateneinträgen müssen natürlich funtionieren, aber diese hier sind in 
		 * Ordnung; wenn eine der folgenden Methoden nicht mehr funktioniert, kann
		 * der ganze TestCase fehlschlagen
		 * 
		 * bei mir (Tanja) funktioniert das mit dem CASCADE ON DELETE nicht (oder ich verstehe
		 * es nicht), deshalb wird alles, was erzeugt wird, auch wieder gelöscht
		 */
		testDB.deleteBuddy("wiede", "niko");
		testDB.deleteBuddy("wiede", "chris");
		testDB.deleteBuddy("chris", "niko");
		deleteStandardUser("wiede");
		deleteStandardUser("niko");
		deleteStandardUser("chris");
		deleteStandardUser("gabi");
		for (Object o : projectIDs) {
			testDB.deleteProject(((Integer)o).intValue());
		}
	}
	
	/**
	 * Hilfsmethode für die Testmethoden, die einen Standardbenutzer in
	 * die Datenbank schreibt.
	 * @throws SQLException
	 */
	private void addStandardUser(String nickname, String pass) throws SQLException {
		testDB.addUser("Christian", "Wiedemann", "christian@host.de",
				nickname, pass, "de", "male", "Oberguenzburg",
				"no comment", "localhost");
	}
	
	/**
	 * Hilfsmethode für die Testmethoden, die den Standardbenutzer aus
	 * der Datenbank löscht.
	 * @throws SQLException
	 */
	private void deleteStandardUser(String nickname) throws SQLException {
		testDB.deleteUser(nickname);
	}

	/**
	 * Es wird getestet, ob ein User korrekt in die Datenbank geschrieben
	 * und anschliessend gelöscht wird
	 * @throws SQLException
	 */
	public final void testAddUser() throws SQLException {
		addStandardUser("wiede", "mac12");
		assertTrue("Eintrag nicht vorhanden!", testDB.existsUser("wiede"));
		deleteStandardUser("wiede");
		assertFalse("Eintrag wurde nicht gelöscht!",
				testDB.existsUser("wiede"));
	}
	
	/**
	 * Es wird getestet, ob der Owner eines Projektes korrekt erkannt wird.
	 * @throws SQLException
	 */
	public final void testIsOwner() throws SQLException{
		int id = setStandardForProjectTests();
		assertTrue("Der Besitzer des Projektes wird nicht erkannt.", testDB.isOwner("wiede", id+""));
		assertFalse("Das Projekt gehört nicht niko.", testDB.isOwner("niko", id+""));
	}
	
	/**
	 * Es wird getestet, ob die Profile von zwei Anwendern aus der Datenbank
	 * erhalten werden. Dabei werden die Benutzernamen und die Anzahl der Elemente
	 * des Ergebnisvektors &uuml;berpr&uuml;ft.
	 * @throws SQLException
	 */
	public final void testGetProfile() throws SQLException {
		addStandardUser("wiede", "mac12");
		addStandardUser("niko", "event");
		Vector data = testDB.getProfile(new String[] {"wiede", "niko"});
		assertEquals("wiede", data.get(3));
		assertEquals("niko", data.get(11));
		assertEquals(16, data.size());
	}
	
	/**
	 * Es wird getestet, ob bei Anforderung des Profils eines unbekannten
	 * Benutzers eine IllegalArgumentException geworfen wird.
	 *  
	 * @throws SQLException
	 */
	public final void testFailed_getProfile() throws SQLException {
		addStandardUser("wiede", "mac12");
		try {
			testDB.getProfile(new String[] {"niko"});
			fail("IllgalArgumentException expected");
		} catch(IllegalArgumentException expected) {
		}
	}

	/**
	 * Zunächst wird getestet, ob ein User, der noch nicht in der Datenbank
	 * steht, nicht verifiziert wird.
	 * Danach wird getestet, ob man sich mit der richtigen Kombination von
	 * Nickname und Passwort einloggen kann, und ob die falsche Kombination
	 * abgewiesen wird.
	 * @throws SQLException
	 */
	public final void testCorrect_login() throws SQLException {
		assertFalse("Nicht eingetragener User wurde verifiziert!",
				testDB.correct_login("wiede", "mac12"));
		addStandardUser("wiede", "mac12");
		assertTrue("Korrekter Login nicht erkannt!",
				testDB.correct_login("wiede", "mac12"));
		assertFalse("Inkorrekter Login nicht erkannt!", 
				testDB.correct_login("wiede", "mac11"));
		deleteStandardUser("wiede");
	}

	/**
	 * Zunächst wird getestet, ob ein neu eingetragener User als nicht ein-
	 * geloggt geführt ist.
	 * Danach ermittelt der Test, ob nach einem Login der User auch als ein-
	 * geloggt gilt.
	 * @throws SQLException
	 */
	public final void testLogin() throws SQLException {
		addStandardUser("wiede", "mac12");
		assertFalse("Nicht eingeloggter User wurde als eingeloggt "+
				"erkannt!", testDB.isLoggedIn("wiede"));
		testDB.login("wiede", "wiedemac12");
		assertTrue("Eingeloggter User wurde als nicht eingeloggt "+
				"erkannt!", testDB.isLoggedIn("wiede"));
		deleteStandardUser("wiede");
	}

	/**
	 * Es wird ermittelt, ob ein User nach einem Logout auch als
	 * ausgeloggt geführt wird.
	 * @throws SQLException
	 */
	public final void testLogout() throws SQLException {
		addStandardUser("wiede", "mac12");
		testDB.login("wiede", "wiedemac12");
		testDB.logout("wiede");
		assertFalse("User wurde nicht ausgeloggt!",
				testDB.isLoggedIn("wiede"));
		deleteStandardUser("wiede");
	}
	
	/**
	 * Testet, ob immer die zuletzt generierte ID zurückgegeben wird.
	 * Dazu muss nach einem Eintrag in der Tabelle gelten:
	 * getLatestGeneratedID()(vor dem Eintrag) + 1 = 
	 * getLatestGeneratedID()(nach dem Eintrag)
	 * @throws SQLException
	 */
	public final void testGetLatestGeneratedID() throws SQLException {
		Vector v = new Vector();
		addStandardUser("wiede", "mac12");
		addStandardUser("niko", "mac13");
		testDB.addChat(false, "wiede", v);
		int i = testDB.getLatestGeneratedID("chat", "chat_id");
		testDB.addChat(false, "niko", v);
		assertTrue("Falsche ChatID!",
				testDB.getLatestGeneratedID("chat", "chat_id") 
				== i + 1);
		deleteStandardUser("wiede");
		addStandardUser("wiede", "mac12");
		testDB.addChat(false, "wiede", v);
		assertTrue("Falsche ChatID!",
				testDB.getLatestGeneratedID("chat", "chat_id")
				== i + 2);
		deleteStandardUser("wiede");
		deleteStandardUser("niko");
	}
	
	/**
	 * Testet, ob ein Projekt ohne Datenbankfehler in die Datenbank
	 * geschrieben werden kann
	 *
	 */
	public final void testAddProject() {
		try {
			setStandardForProjectTests();
			deleteStandardUsers();
		} catch (SQLException e) {
			assertTrue("Datenbankfehler!", false);
			System.out.print(e.getMessage());
		}		
	}
	
	/**
	 * Hilfsmethode zum Löschen der Standardbenutzer in den Tests
	 * @throws SQLException
	 */
	private void deleteStandardUsers() throws SQLException {
		deleteStandardUser("wiede");
		deleteStandardUser("niko");
		deleteStandardUser("chris");
		deleteStandardUser("gabi");		
	}

	/**
	 * Testet, ob die Methode isPrivateProject(...) die richtige
	 * Antwort gibt
	 * @throws SQLException
	 */
	public final void testIsPrivateProject() throws SQLException {
		int id = setStandardForProjectTests();
		assertTrue("Privates Projekt als öffentlich eingestuft!",
				testDB.isPrivateProject(""+id));
		deleteStandardUsers();
	}
	
	/**
	 * Testet, ob ein nicht eingeladener User abgelehnt und ein
	 * eingeladener User akzeptiert wird
	 * @throws SQLException
	 */
	public final void testIsInvited() throws SQLException {
		int id = setStandardForProjectTests();
		assertTrue("Eingeladener User wurde nicht erkannt!",
				testDB.isInvited("niko", ""+id));
		assertFalse("Nicht eingeladener User wurde verifiziert!",
				testDB.isInvited("holger", ""+id));
		deleteStandardUsers();
	}
	
	/**
	 * Testet, ob die Methode addUserToProject(..) ohne Datenbank-
	 * fehler arbeitet
	 *
	 */
	public final void testAddUserToProject() {
		try {
			int id = setStandardForProjectTests();
			testDB.addUserToProject("chris", ""+id);
			assertTrue(testDB.isInvited("chris", ""+id));
			assertTrue(testDB.hasAccepted("chris", "" + id));
			deleteStandardUsers();
		} catch (SQLException e) {
			assertTrue("Datenbankfehler aufgetreten!", false);
		}		
	}
	
	/**
	 * Testet zunächst, ob bei addUserToProject(...) die notwendigen
	 * Eintragungen gemacht werden. Danach wird nach removeUserFromProjekt(...)
	 * geprüft, ob alle Eintragungen gelöscht wurden. Ausserdem wird
	 * die Methode auf Datenbankfehler geprüft
	 *
	 */
	public final void testRemoveUserFromProject() {
		Statement st = testDB.getStatement();
		ResultSet rs;
		try {
			int id = setStandardForProjectTests();
			testDB.addUserToProject("chris", ""+id);			
			String chatID = testDB.getChatID(id);
			rs = st.executeQuery("SELECT * FROM "+
					"members WHERE project_id='"+id+
					"' AND user_nickname='chris';");
			assertTrue("User nicht in Projekt eingetragen!", rs.next());
			rs = st.executeQuery("SELECT * FROM user_chat WHERE "+
					"chat_id='"+chatID+"' AND user_nickname='chris';");
			assertTrue("User nicht in user_chat eingetragen!", rs.next());
			testDB.removeUserFromProject("chris", ""+id);
			rs = st.executeQuery("SELECT * FROM "+
					"members WHERE project_id='"+id+
					"' AND user_nickname='chris';");
			assertFalse("User nicht aus Projekt gelöscht!", rs.next());
			rs = st.executeQuery("SELECT * FROM user_chat WHERE "+
					"chat_id='"+chatID+"' AND user_nickname='chris';");
			assertFalse("User nicht aus user_chat gelöscht!", rs.next());
			Connection c = testDB.getConnection();
			c.commit();
			deleteStandardUsers();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			assertTrue("Datenbankfehler!", false);
		}
	}
	
	/**
	 * Überprüft, ob die richtigen Chat-Typen erkannt werden
	 *
	 */
	public final void testGetChatType() {
		try {
			Vector v = new Vector();
			int id = setStandardForProjectTests();
			Statement st = testDB.getStatement();
			Connection c = testDB.getConnection();
			ResultSet rs = st.executeQuery("SELECT project_chat FROM "+
					"projects WHERE project_id='"+id+"';");
			c.commit();
			rs.next();
			String type = testDB.getChatType(""+rs.getInt("project_chat"));
			assertTrue("Unerwarteter Chat-Typ!",
					type.compareTo("PROJECT") == 0);
			int chatID = testDB.addChat(true, "wiede", v);
			type = testDB.getChatType(""+chatID);
			assertTrue("Unerwarteter Chat-Typ!",
					type.compareTo("PRIVATE") == 0);
			chatID = testDB.addChat(false, "wiede", v);
			type = testDB.getChatType(""+chatID);
			assertTrue("Unerwarteter Chat-Typ!",
					type.compareTo("PUBLIC") == 0);
			type = testDB.getChatType("0");
			assertTrue("Unerwarteter Chat-Typ!",
					type.compareTo("UNKNOWN") == 0);
			deleteStandardUsers();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Überprüft, ob die richtigen Projekte ausgewählt werden und ob 
	 * SQL-Fehler auftreten.
	 *
	 */
	public final void testGetProjects() {
		try {
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			String[] v = new String[0];
			String date = "2005-12-29";
			Vector projects = testDB.getProjects("niko");
			int numProjects = projects.size();
			int id1 = testDB.addProject("wiede", "Sonne", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id1));
			int id2 = testDB.addProject("wiede", "Mond", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id2));
			int id3 = testDB.addProject("wiede", "Sterne", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id3));
			int id4 = testDB.addProject("niko", "Mars", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id4));
			int id5 = testDB.addProject("niko", "Venus", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id5));
			int id6 = testDB.addProject("chris", "Jupiter", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id6));
			testDB.addUserToProject("wiede", id1+"");
			testDB.addUserToProject("wiede", id2+"");
			testDB.addUserToProject("wiede", id3+"");
			testDB.addUserToProject("wiede", id4+"");
			testDB.addUserToProject("niko", id1+"");
			testDB.addUserToProject("niko", id4+"");
			testDB.addUserToProject("niko", id5+"");
			testDB.addUserToProject("chris", id6+"");
			projects = testDB.getProjects("niko");
            // Es wird geprüft, ob die richtigen Projekt-Titel enthalten sind
			// und die falschen nicht enthalten sind
			assertTrue("Der Titel des Sonne-Projektes ist bei niko nicht enthalten",
					projects.contains("Sonne"));
			assertTrue("Der Titel des Mars-Projektes ist bei niko nicht enthalten",
					projects.contains("Mars"));
			assertTrue("Der Titel des Venus-Projektes ist bei niko nicht enthalten",
					projects.contains("Venus"));
			assertTrue("Der Titel des Mond-Projektes ist bei niko enthalten",
					!projects.contains("Mond"));
			assertTrue("Der Titel des Sterne-Projektes ist bei niko enthalten",
					!projects.contains("Sterne"));
			assertTrue("Der Titel des Jupiter-Projektes ist bei niko enthalten",
					!projects.contains("Jupiter"));
			/*
			 * siehe testGetAllProjects
			 */
			assertEquals("Fehlerhafte Anzahl an Vector-Elementen", numProjects + 24, projects.size());
			deleteStandardUser("wiede");
			deleteStandardUser("niko");
			deleteStandardUser("chris");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob bei der Methode getProjects() der Klasse DB
	 * SQL-Fehler auftreten und ob alle Projekte enthalten sind.
	 *
	 */
	public final void testGetAllProjects() {
		try {
			Vector projects = testDB.getProjects();
			int numProjects = projects.size();
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			String[] v = new String[0];
			String date = "2005-12-29";
			int id1 = testDB.addProject("wiede", "Sonne", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id1));
			int id2 = testDB.addProject("wiede", "Mond", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id2));
			int id3 = testDB.addProject("wiede", "Sterne", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id3));
			int id4 = testDB.addProject("niko", "Mars", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id4));
			int id5 = testDB.addProject("niko", "Venus", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id5));
			int id6 = testDB.addProject("chris", "Jupiter", "1", "10", "no", date, v);
			projectIDs.add(new Integer(id6));
			testDB.addUserToProject("wiede", id1+"");
			testDB.addUserToProject("wiede", id2+"");
			testDB.addUserToProject("wiede", id3+"");
			testDB.addUserToProject("wiede", id4+"");
			testDB.addUserToProject("niko", id1+"");
			testDB.addUserToProject("niko", id4+"");
			testDB.addUserToProject("niko", id5+"");
			testDB.addUserToProject("chris", id6+"");
			projects = testDB.getProjects();
			/*
			 * was an Projekten in der Datenbank bereits vorhanden war, wird berücksichtigt
			 */
			assertEquals("Falsche Anzahl von Vector-Elementen!", numProjects + 48, projects.size());
			deleteStandardUser("wiede");
			deleteStandardUser("niko");
			deleteStandardUser("chris");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}		
	}
	
	/**
	 * Testet ob ein neuer Buddy wirklich in die Datenbank geschrieben 
	 * wird, und ob SQL-Fehler auftreten.
	 *
	 */
	public final void testAddBuddy() {
		Statement st = testDB.getStatement();
		ResultSet rs;
		try {
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			testDB.addBuddy("wiede", "niko");
			rs = st.executeQuery("SELECT * FROM buddies WHERE "+
					"user_nickname='wiede' AND buddy_nickname='niko';");
			assertTrue("Kein Eintrag gefunden!", rs.next());
			deleteStandardUser("wiede");
			deleteStandardUser("niko");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet ob ein gelöschter Buddy wirklich aus der Datenbank entfernt
	 * wurde, und ob SQL-Fehler auftreten.
	 *
	 */
	public final void testDeleteBuddy() {
		Statement st = testDB.getStatement();
		ResultSet rs;
		try {
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			testDB.addBuddy("wiede", "niko");
			testDB.deleteBuddy("wiede", "niko");
			rs = st.executeQuery("SELECT * FROM buddies WHERE "+
					"user_nickname='wiede' AND buddy_nickname='niko';");
			assertTrue("Eintrag gefunden!", !rs.next());
			deleteStandardUser("wiede");
			deleteStandardUser("niko");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob die Methode getBuddies(String user) die richtigen
	 * Nicknamen holt. Ausserdem wird auf SQL-Fehler getestet.
	 *
	 */
	public final void testGetBuddies() {
		try {
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			testDB.addBuddy("wiede", "niko");
			testDB.addBuddy("wiede", "chris");
			Vector buddies = testDB.getBuddies("wiede");
			assertTrue("Buddy nicht enthalten!", buddies.contains("niko")&&
					buddies.contains("chris"));
			assertTrue("Buddy-Vector enthält eine falsche Anzahl von "+
					"Elementen!", buddies.size() == 2);
			testDB.deleteBuddy("wiede", "niko");
			buddies = testDB.getBuddies("wiede");
			assertTrue("Fehlerhafter Buddy enthalten!",
					buddies.contains("chris")&&!buddies.contains("niko"));
			assertTrue("Buddy-Vector enthält eine falsche Anzahl von "+
					"Elementen!", buddies.size() == 1);
			deleteStandardUser("wiede");
			deleteStandardUser("niko");
			deleteStandardUser("chris");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	/**
	 * Testet, ob die Methode getUsersForStatusChange(...) korrekt arbeitet,
	 * und ob SQL-Exceptions auftreten.
	 *
	 */
	public final void testUsersForStatusChange() {
		try {
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			testDB.addBuddy("wiede", "niko");
			testDB.addBuddy("chris", "niko");
			Vector users = testDB.getUsersForStatusChange("niko");
			assertTrue("User nicht enthalten!", users.contains("wiede")&&
					users.contains("chris"));
			assertTrue("Buddy-Vector enthält eine falsche Anzahl von "+
					"Elementen!", users.size() == 2);
			deleteStandardUser("wiede");
			deleteStandardUser("niko");
			deleteStandardUser("chris");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob die Methode isFull(...) krrekt arbeitet, und ob SQL-Exceptions 
	 * auftreten
	 *
	 */
	public final void testIsFull() {
		try {
			String[] userToInvite = new String[0];
			String date = "2005-12-29";
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			int id = testDB.addProject("wiede", "Schule", "0", "3", "Hausaufgaben",
					date, userToInvite);
			projectIDs.add(new Integer(id));
			testDB.addUserToProject("wiede", id+"");
			testDB.addUserToProject("niko", id+"");
			assertFalse("Projekt ist zu früh gefüllt!", testDB.isFull(id+""));
			testDB.addUserToProject("chris", id+"");
			assertTrue("Projekt ist nicht gefüllt!", testDB.isFull(id+""));
			deleteStandardUser("wiede");
			deleteStandardUser("niko");
			deleteStandardUser("chris");
		} catch (SQLException e) {
			assertTrue(false);
		}	
	}
	
	/**
	 * Testet, ob  bei addURL(..) SQLExceptions auftreten.
	 *
	 */
	public final void testAddURL() {
		try {
			String[] userToInvite = new String[0];
			String date = "2005-12-29";
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			int id = testDB.addProject("wiede", "Schule", "0", "3", "Hausaufgaben",
					date, userToInvite);
			projectIDs.add(new Integer(id));
			testDB.addUserToProject("wiede", id+"");
			int urlID1 = testDB.addURL("wiede", id+"", "www.alternate.de", "Alternate", "");
			int urlID2 = testDB.addURL("wiede", id+"", "www.google.de", "Suchmaschine Google", "www.alternate.de");
			testDB.deleteURL(urlID1+"");
			testDB.deleteURL(urlID2+"");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob die Methode isMember(...) korrekt arbeitet und ob
	 * SQL-Exceptions auftreten.
	 *
	 */
	public final void testIsMember() {
		try {
			String[] userToInvite = new String[0];
			String date = "2005-12-29";
	        addStandardUser("wiede", "mac11");
			int id = testDB.addProject("wiede", "Schule", "0", "3", "Hausaufgaben",
					date, userToInvite);
			projectIDs.add(new Integer(id));
			assertFalse("Fälschlicherweise als Mitglied erkannt!", 
					testDB.isMember("wiede", id+""));
			testDB.addUserToProject("wiede", id+"");
			assertTrue("Fälschlicherweise nicht als Mitglied erkannt!",
					testDB.isMember("wiede", id+""));
			deleteStandardUser("wiede");
		} catch (SQLException e) {
			assertTrue(false);
		}		
	}
	
	/**
	 * Testet, ob bei der Methode getWebtrace(...) SQLExceptions auftreten
	 * und ob der Vector korrekt gefüllt wird.
	 *
	 */
	public final void testGetWebtrace() {
		try {
			String[] userToInvite = new String[0];
			String date = "2005-12-29";
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			int id = testDB.addProject("wiede", "Schule", "0", "3", "Hausaufgaben",
					date, userToInvite);
			projectIDs.add(new Integer(id));
			testDB.addUserToProject("wiede", id+"");
			int urlID1 = testDB.addURL("wiede", id+"", "www.alternate.de", "Alternate", "");
			int urlID2 = testDB.addURL("wiede", id+"", "www.google.de", "Suchmaschine Google", "www.alternate.de");
			Vector webtrace = testDB.getWebtrace(id+"");
			assertTrue("Falsches Element an erster Stelle!", 
					webtrace.elementAt(0).equals("www.alternate.de")||
					webtrace.elementAt(0).equals("www.google.de"));
			assertTrue("Falsches Element an zweiter Stelle!", 
					webtrace.elementAt(1).equals("")||
					webtrace.elementAt(1).equals("www.alternate.de"));
			assertTrue("Falsches Element an dritter Stelle!", 
					webtrace.elementAt(2).equals("Alternate")||
					webtrace.elementAt(2).equals("Suchmaschine Google"));
			assertTrue("Falsches Element an vierter Stelle!", 
					webtrace.elementAt(3).equals("wiede"));
			assertTrue("Falsches Element an fünfter Stelle!", 
					webtrace.elementAt(4).equals("-1"));
			assertTrue("Falsches Element an sechster Stelle!", 
					webtrace.elementAt(5).equals("0"));
			assertTrue("Falsche Grösse des Vectors!", webtrace.size() == 12);
			testDB.deleteURL(urlID1+"");
			testDB.deleteURL(urlID2+"");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob bei der Methode addNote(...) SQL-Exceptions auftreten.
	 *
	 */
	public final void testAddNote() {
		try {
			String[] userToInvite = new String[0];
			String date = "2005-12-29";
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			int id = testDB.addProject("wiede", "Schule", "0", "3", "Hausaufgaben",
					date, userToInvite);
			projectIDs.add(new Integer(id));
			testDB.addUserToProject("wiede", id+"");
			int urlID1 = testDB.addURL("wiede", id+"", "www.alternate.de", "Alternate", "");
			int urlID2 = testDB.addURL("wiede", id+"", "www.google.de", "Suchmaschine Google", "www.alternate.de");
			int noteID = testDB.addNote("wiede", id+"", "www.google.de", "3", "Sehr gute Suchmaschine");
			testDB.deleteNote(noteID+"");
			testDB.deleteURL(urlID1+"");
			testDB.deleteURL(urlID2+"");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob die Methode getNotes(...) korrekt arbeitet, und ob
	 * SQL-Exceptions auftreten.
	 *
	 */
	public final void testGetNote() {
		try {
			String[] userToInvite = new String[0];
			String date = "2005-12-29";
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			int id = testDB.addProject("wiede", "Schule", "0", "3", "Hausaufgaben",
					date, userToInvite);
			projectIDs.add(new Integer(id));
			testDB.addUserToProject("wiede", id+"");
			int urlID1 = testDB.addURL("wiede", id+"", "www.alternate.de", "Alternate", "");
			int urlID2 = testDB.addURL("wiede", id+"", "www.google.de", "Suchmaschine Google", "www.alternate.de");
			int noteID = testDB.addNote("wiede", id+"", "www.google.de", "3", "Sehr gute Suchmaschine");
			Vector notes = testDB.getNotes("www.google.de");
			assertTrue("Falsches erstes Element!", notes.elementAt(0).equals("wiede"));
			assertTrue("Falsches zweites Element!", notes.elementAt(1).equals("Sehr gute Suchmaschine"));
			testDB.deleteNote(noteID+"");
			testDB.deleteURL(urlID1+"");
			testDB.deleteURL(urlID2+"");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob die folder-Methoden korrekt arbeiten, und ob
	 * SQL-Exceptions auftreten.
	 *
	 */
	public final void testFolders() {
		try {
			String[] userToInvite = new String[0];
			String date = "2005-12-29";
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			int id = testDB.addProject("wiede", "Schule", "0", "3", "Hausaufgaben",
					date, userToInvite);
			projectIDs.add(new Integer(id));
			testDB.addFolder(id+"", "Wurzel", "");
			int folderID = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "1. Sohn", folderID+"");
			int folderID2 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "2. Sohn", folderID+"");
			int folderID3 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "1. Sohn vom 1. Sohn", folderID2+"");
			int folderID4 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "2. Sohn vom 1. Sohn", folderID2+"");
			int folderID5 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "1. Sohn v. 2. Sohn v. 1. Sohn", folderID5+"");
			int folderID6 = testDB.getLatestFolderID();			
			testDB.moveFolder(folderID2+"", "0");
			assertTrue("Fehler beim Löschen!", !testDB.deleteFolder(folderID2+"", true));
			assertTrue("Fehler beim Löschen!", !testDB.deleteFolder(folderID+"", true));
			assertTrue("Fehler beim Einfügen!", testDB.addFolder(id+"", "Test", folderID2+""));
			assertTrue("Fehler beim Löschen!", testDB.deleteFolder(folderID3+"", true));
			assertTrue("Fehler beim Löschen!", testDB.deleteFolder(folderID4+"", true));
			assertTrue("Fehler beim Löschen!", testDB.deleteFolder(folderID5+"", true));
			assertTrue("Fehler beim Löschen!", testDB.deleteFolder(folderID6+"", true));
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob die Methode changeParentFolder(...) korrekt arbeitet, und ob
	 * SQL-Exceptions auftreten.
	 *
	 */
	public final void testChangeParentFolder() {
		try {
			String[] userToInvite = new String[0];
			String date = "2005-12-29";
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			int id = testDB.addProject("wiede", "Schule", "0", "3", "Hausaufgaben",
					date, userToInvite);
			projectIDs.add(new Integer(id));
			int urlID1 = testDB.addURL("wiede", id+"", "www.alternate.de", "Alternate", "");
			int urlID2 = testDB.addURL("wiede", id+"", "www.google.de", "Suchmaschine Google", "www.alternate.de");
			testDB.addFolder(id+"", "Wurzel", "");
			int folderID = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "1. Sohn", folderID+"");
			int folderID2 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "2. Sohn", folderID+"");
			int folderID3 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "1. Sohn vom 1. Sohn", folderID2+"");
			int folderID4 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "2. Sohn vom 1. Sohn", folderID2+"");
			int folderID5 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "1. Sohn v. 2. Sohn v. 1. Sohn", folderID5+"");
			int folderID6 = testDB.getLatestFolderID();
			boolean folderAlreadyDeleted = testDB.changeParentfolder("www.alternate.de", folderID2+"");
			assertFalse("Fehler beim Einfügen der URL!", folderAlreadyDeleted);
			ResultSet rs = testDB.getStatement().executeQuery("SELECT * FROM folder_urls "+
					"WHERE folder_id='"+folderID2+"' AND url_id='"+urlID1+"';");
			assertTrue("URL nicht eingefügt!", rs.next());
			folderAlreadyDeleted = testDB.changeParentfolder("www.alternate.de", folderID4+"");
			assertFalse("Fehler beim Einfügen der URL!", folderAlreadyDeleted);
			testDB.deleteFolder(folderID2+"", true);
			folderAlreadyDeleted = testDB.changeParentfolder("www.google.de", folderID2+"");
			assertTrue("Fehler beim Einfügen der URL!", folderAlreadyDeleted);
			testDB.deleteFolder(folderID+"", true);
			testDB.deleteFolder(folderID3+"", true);
			testDB.deleteFolder(folderID4+"", true);
			testDB.deleteFolder(folderID5+"", true);
			testDB.deleteFolder(folderID6+"", true);
			testDB.deleteURL(urlID1+"");
			testDB.deleteURL(urlID2+"");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob die Methode getFoldersWithURLs(...) korrekt arbeitet, und ob
	 * SQL-Exceptions auftreten.
	 *
	 */
	public final void testGetFoldersWithURLs() {
		try {
			String[] userToInvite = new String[0];
			String date = "2005-12-29";
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			addStandardUser("chris", "mac13");
			int id = testDB.addProject("wiede", "Schule", "0", "3", "Hausaufgaben",
					date, userToInvite);
			projectIDs.add(new Integer(id));
			int urlID1 = testDB.addURL("wiede", id+"", "www.alternate.de", "Alternate", "");
			int urlID2 = testDB.addURL("wiede", id+"", "www.google.de", "Suchmaschine Google", "www.alternate.de");
			testDB.addFolder(id+"", "Wurzel", "");
			int folderID = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "1. Sohn", folderID+"");
			int folderID2 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "2. Sohn", folderID+"");
			int folderID3 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "1. Sohn vom 1. Sohn", folderID2+"");
			int folderID4 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "2. Sohn vom 1. Sohn", folderID2+"");
			int folderID5 = testDB.getLatestFolderID();
			testDB.addFolder(id+"", "1. Sohn v. 2. Sohn v. 1. Sohn", folderID5+"");
			int folderID6 = testDB.getLatestFolderID();
			testDB.changeParentfolder("www.alternate.de", folderID2+"");
	        testDB.changeParentfolder("www.google.de", folderID4+"");
	        @SuppressWarnings("unused") Vector<String> foldersWithURLs = testDB.getFoldersWithURLs(id+"");
	        System.out.println(foldersWithURLs);
			testDB.deleteFolder(folderID2+"", true);
			testDB.deleteFolder(folderID+"", true);
			testDB.deleteFolder(folderID3+"", true);
			testDB.deleteFolder(folderID4+"", true);
			testDB.deleteFolder(folderID5+"", true);
			testDB.deleteFolder(folderID6+"", true);
			testDB.deleteURL(urlID1+"");
			testDB.deleteURL(urlID2+"");
		} catch (SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Testet, ob die Methode setProfile(...) korrekt arbeitet.
	 *
	 */
	public final void testSetProfile() throws SQLException {
		testDB.setProfile("Holger", "Hochl", "Bachus", "meine@email.de", "Bach", "EN", "FEMALE", "Unter der Brücke 14", "kein Kommentar");
	}

	/**
	 * initialisiert die eingeladenen Benutzer, die registrierten
	 * User und ein Projekt für die Projekt-Tests
	 * @return
	 * @throws SQLException
	 */
	private int setStandardForProjectTests() throws SQLException {
		String[] userToInvite = new String[3];
		userToInvite[0] = "niko";
		userToInvite[1] = "chris";
		userToInvite[2] = "gabi";
		addStandardUser("wiede", "mac12");
		addStandardUser("niko", "mac13");
		addStandardUser("chris", "mac14");
		addStandardUser("gabi", "mac15");
		String date = "2005-12-29";
		int id = testDB.addProject("wiede", "Schule", "0", "8", "Hausaufgaben",
				date, userToInvite);
		projectIDs.add(new Integer(id));
		return id;
	}

	/**
	 * Testet, ob die Methode searchUserss(...) korrekt arbeitet.
	 *
	 */
	public final void testSearchUsers() throws SQLException {
		String[] testdata = { "*o*","*","*ach*","DE","MALE","" };
		testDB.searchUsers(testdata);
		String[] testdata2 = { "","*","*","*","","*ommen*" };
		testDB.searchUsers(testdata2);
		String[] testdata3 = { "","","","","","" };
		testDB.searchUsers(testdata3);
	}
	
}
