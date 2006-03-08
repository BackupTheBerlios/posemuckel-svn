package posemuckel.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

public class ShowBuddyBug extends TestCase {
	
	/**
	 * Testdatenbank
	 */
	private DB testDB;

	protected void setUp() throws Exception {
		super.setUp();
		testDB = new DB("root", "lifecycle", "localhost", "posemuckel");
		testDB.deleteBuddy("wiede", "niko");
		testDB.deleteBuddy("niko", "wiede");
		testDB.deleteUser("wiede");
		testDB.deleteUser("niko");
	}

	protected void tearDown() throws Exception {
		testDB.deleteBuddy("wiede", "niko");
		testDB.deleteBuddy("niko", "wiede");
		testDB.deleteUser("wiede");
		testDB.deleteUser("niko");
	}

	/**
	 * hier wird das 'normale' Verhalten getestet:
	 * die Buddy-Relation ist einseitig implementiert. Wenn wiede niko zu 
	 * seinem Buddy macht, hat niko noch keinen Buddy wiede. Das ist konsistent
	 * mit der Implementierung im Client und im RFC.
	 *
	 */
	public void testAddBuddy() {
		Statement st = testDB.getStatement();
		ResultSet rs;
		try {
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			//wiede macht niko zu seinem Buddy
			rs = st.executeQuery("SELECT * FROM buddies WHERE "+
			"user_nickname='wiede' AND buddy_nickname='niko';");
			assertFalse("wide hat bereits einen Buddy niko", rs.next());
			testDB.addBuddy("wiede", "niko");
			rs = st.executeQuery("SELECT * FROM buddies WHERE "+
			"user_nickname='wiede' AND buddy_nickname='niko';");
			assertTrue("wiede hat keinen Buddy niko!", rs.next());
			//niko macht wiede zu seinem Buddy
			rs = st.executeQuery("SELECT * FROM buddies WHERE "+
			"user_nickname='niko' AND buddy_nickname='wiede';");
			assertFalse("niko hat bereits einen Buddy wiede", rs.next());
			testDB.addBuddy("niko", "wiede");
			rs = st.executeQuery("SELECT * FROM buddies WHERE "+
			"user_nickname='niko' AND buddy_nickname='wiede';");
			assertTrue("niko hat keinen Buddy!", rs.next());
		} catch (SQLException e) {
			e.printStackTrace();
			fail("unexpected SQLException");
		}
	}
	
	/**
	 * folgende Situation kann auftreten, wenn ein Anwender eine Aktion zweimal
	 * hintereinander ausführt (weil der Server zu langsam in den Reaktionen ist
	 * oder er einen Doppelklick auf einem Button ausführt)
	 *
	 */
	public void testAddBuddyTwice() {
		Statement st = testDB.getStatement();
		ResultSet rs;		
		try {
			//hier sollte alles normal verlaufen
			addStandardUser("wiede", "mac11");
			addStandardUser("niko", "mac12");
			testDB.addBuddy("wiede", "niko");
			rs = st.executeQuery("SELECT * FROM buddies WHERE "+
			"user_nickname='wiede' AND buddy_nickname='niko';");
			assertTrue("wiede hat keinen Buddy niko!", rs.next());
		} catch (SQLException e) {
			e.printStackTrace();
			fail("unexpected SQLException");
		}
		try {
			/* 
			 * jetzt fügen wir die gleiche Aktion noch einmal durch
			 * entspricht einem Doppelklick auf den Button im ProfileDialog;
			 * wenn der Server sehr langsam ist, dann kann das auch durch zweimaliges
			 * ausführen der Aktion passieren (der Anwender denkt vielleicht, das
			 * die Aktion nicht angekommen ist)
			 * 
			 * das gleiche Problem kann auch bei anderen rfc-Nachrichten auftreten
			 * (welchen?)
			 */
			testDB.addBuddy("wiede", "niko");
			rs = st.executeQuery("SELECT * FROM buddies WHERE "+
			"user_nickname='wiede' AND buddy_nickname='niko';");
			assertTrue("wiede hat keinen Buddy niko!", rs.next());
		} catch (SQLException e) {
			e.printStackTrace();
			fail("es war nicht möglich, den Buddy zweimal hintereinander einzufügen");
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

}
