package posemuckel.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Basisklasse zur Verarbeitung von Server- und Clientnachrichten; sorgt dafür,
 * dass die richtige Methode beim Eintreffen einer Nachricht aufgerufen
 * wird.
 * Die Methoden eat_up_ClientPacket und eat_up_ServerPacket werden von
 * den empfangenden Programmteilen des Servers und des Clients aufgerufen,
 * um die empfangenen Nachrichten zu verarbeiten. Dadurch wird auf die
 * einzelnen Methoden verzweigt, die in einer entsprechend abgeleiteten
 * Klasse implementiert werden. Diese Klasse ist abstrakt.
 */
public abstract class Message_Handler {
	
	/**
	 * Dient der Ausgabe der eingelesenen Nachrichtenheader
	 */
	private static boolean debug = false;

	public Message_Handler() {
		super();
	}
	
	/**
	 * Dient zum Setzen des Debug-Modus
	 * @param debug bei true wird in den Debug-Modus geschaltet, bei
	 *              false wird er ausgeschaltet.
	 */
	public static void setDebugModus(boolean debug) {
		Message_Handler.debug = debug;
	}
	
	/**
	 * Setzt die ID der zuletzt geparsten und bearbeiteten Nachricht. Die Methode
	 * muss überschrieben werden!
	 * @param id ID der Nachricht
	 */
	protected void setMessageID(String id) {
	}
	
	/**
	 * Hier sind die Methoden, die bei Nachrichten für den Server auf-
	 * gerufen werden.
	 */

    /**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * START_PROJECT-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     * @throws SQLException 
     */
    protected boolean startProject(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
	    throw new InvalidMessageException("Packet type unknown.");
	}
    
    /**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * JOIN_PROJECT-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     * @throws SQLException 
     */
    protected boolean joinProject(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
    	throw new InvalidMessageException("Packet type unknown.");
    }
    
    /**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * START_CHAT-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     * @throws SQLException 
     */
    protected boolean startChat(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
    	throw new InvalidMessageException("Packet type unknown.");
    }
    
    /**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * JOIN_CHAT-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     * @throws SQLException 
     */
    protected boolean joinChat(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
    	throw new InvalidMessageException("Packet type unknown.");
    }

    /**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * CHAT-Nachricht eintrifft.
     * @param user Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     * @throws SQLException 
     */
	protected boolean chat(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}		
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * LOGIN-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean login(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}	
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * LOGOUT-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean logout(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * REGISTER-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */
	protected boolean register(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * MY_BUDDIES-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws SQLException 
	 * @throws IOException 
     */
	protected boolean myBuddies(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
	    throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * GET_NOTES-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws SQLException 
	 * @throws IOException 
     */
	protected boolean getNotes(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
	    throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * ADD_NOTE-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws SQLException 
	 * @throws IOException 
     */
	protected boolean addNote(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
	    throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * WEBTRACE-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws SQLException 
	 * @throws IOException 
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean webtrace(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
	    throw new InvalidMessageException("Packet type unknown.");
	}


	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * DEL_BUDDY-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws SQLException 
	 * @throws IOException 
     * @throws IOException
     */
	protected boolean delBuddy(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * ADD_BUDDY-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean addBuddy(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * LEAVE_PROJECT-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean leaveProject(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * MY_PROJECTS-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean myProjects(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * SEARCH_USERS-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean searchUsers(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * GET_SAME_URL_VIEWERS-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean getSameURLViewers(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * GET_FOLDERSYSTEM-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean getFoldersystem(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
	 * Hier sind die Methoden, die bei Server- und Clientnachrichten
	 * aufgerufen werden.
	 */

	/**
     * Wird von den Methoden eat_up_ClientPacket oder eat_up_ServerPacket
     * aufgerufen, wenn eine TYPING-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */
	protected boolean typing(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von den Methoden eat_up_ClientPacket oder eat_up_ServerPacket
     * aufgerufen, wenn eine VIEWING-Nachricht eintrifft.
     * @param user Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean viewing(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von den Methoden eat_up_ClientPacket oder eat_up_ServerPacket
     * aufgerufen, wenn eine READING-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
     */
	protected boolean reading(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von den Methoden eat_up_ClientPacket oder eat_up_ServerPacket
     * aufgerufen, wenn eine PARENTFOLDER_CHANGED-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
	 * @throws SQLException 
     */
	protected boolean parentfolderChanged(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
	 * Hier sind die Methoden, die bei Nachrichten für den Client
	 * aufgerufen werden.
	 */
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * ACCESS_DENIED-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */	
	protected boolean access_denied(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * USER_EXISTS-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */	
	protected boolean userExists(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * ACK-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */	
	protected boolean ack(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * ACCESS_GRANTED-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */	
	protected boolean access_granted(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * GET_PROJECTS-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws SQLException 
	 * @throws IOException 
     * @throws IOException
     */	
	protected boolean getProjects(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * GET_INVITATIONS-Nachricht eintrifft.
     * 
     * @param user Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws SQLException 
	 * @throws IOException 
     * @throws IOException
     */	
	protected boolean getInvitations(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
    /**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * DONT_ACCEPT_PROJECT-Nachricht eintrifft.
     * 
     * @param user Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */
	protected boolean projectNotAccepted(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * NEW_INVITATION-Nachricht eintrifft.
     * 
     * @param user Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */	
	protected boolean newInvitation(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * GET_BUDDIES-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */	
	protected boolean yourBuddies(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * ACCESS_DENIED-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
     */	
	protected boolean newChat(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * ERROR-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */	
	protected boolean error(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * YOUR_PROJECTS-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
     */	
	protected boolean yourProjects(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * NEW_BUDDY-Nachricht eintrifft.
     * 
     * @param user Der Name des Benutzers
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
     * @throws IOException
     */	
	protected boolean newBuddy(String user, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException  {
		throw new InvalidMessageException("Packet type unknown.");
	}


	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * NEW_PROJECT-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     * @throws IOException
     */	
	protected boolean newProject(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * USERS-Nachricht eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean users(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * PROJECTS-Nachricht eintrifft.
     * 
     * @param hash Der Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean projects(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * CHAT_MEMBERS-Nachricht eintrifft.
     * 
     * @param user
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean chatMembers(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * MEMBER_CHANGE-Nachricht eintrifft. Die Nachricht zeigt an, dass ein neues
     * Mitglied zu einem Projekt oder einem Chat hinzugekommen ist.
     * 
     * @param user Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean memberChange(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * GET_CHAT_MEMBERS-Nachricht beim Server eintrifft.
     * 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean getChatMembers(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * GET_PROFILE-Nachricht eintrifft.
     * 
     * @param hash Der Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
	 * @throws SQLException 
     */	
	protected boolean getProfile(String hash, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * SET_PROFILE-Nachricht eintrifft.
     * 
     * @param hash Der Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
	 * @throws SQLException 
     */	
	protected boolean setProfile(String hash, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * PROFILE-Nachricht eintrifft.
     * 
     * @param user
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean profile(String user, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * USER_STATUS-Nachricht eintrifft.
     * 
     * @param user
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean userStatus(String user, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * ACTIVE_USERS-Nachricht eintrifft.
     * 
     * @param user
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean activeUsers(String user, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * PROJECT_CHAT-Nachricht eintrifft.
     * 
     * @param user 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean projectOpened(String user, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * FREESPACES_CHANGED-Nachricht eintrifft.
     * 
     * @param user hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean freeSpacesChanged(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
    }
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * NOTES-Nachricht eintrifft.
     * 
     * @param user 
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean notes(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
    }
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * NEW_NOTE-Nachricht eintrifft.
     * 
     * @param user
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean newNote(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
    }
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * SAME_URL_VIEWERS-Nachricht eintrifft.
     * 
     * @param user
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
     */	
	protected boolean sameURLViewers(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		throw new InvalidMessageException("Packet type unknown.");
    }
	
	/**
     * Wird von der Methode eat_up_ServerPacket aufgerufen, wenn eine
     * FOLDERSYSTEM-Nachricht eintrifft.
     * 
     * @param user
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
	 * @throws SQLException 
     */	
	protected boolean foldersystem(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		throw new InvalidMessageException("Packet type unknown.");
    }
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * OPEN_PROJECT-Nachricht eintrifft.
     * 
     * @param hash Der Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
	 * @throws SQLException 
     */	
	protected boolean openProject(String hash, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException , SQLException{
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
     * Wird von der Methode eat_up_ClientPacket aufgerufen, wenn eine
     * GET_ACTIVE_USERS-Nachricht eintrifft.
     * 
     * @param hash Der Client-Hash
     * @param id Message-ID
     * @param count Anzahl der Parameter der eingetroffenen Nachricht
     * @param in Reader, der den Zeichenstrom einliest
     * @return true, falls die Nachricht korrekt gelesen werden konnte,
     *         sonst false
     * @throws InvalidMessageException
	 * @throws IOException 
	 * @throws SQLException 
     */	
	protected boolean getActiveUsers(String hash, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException , SQLException{
		throw new InvalidMessageException("Packet type unknown.");
	}

	/**
	 * Diese Nachricht wird sowohl vom Client wie auch vom Server
	 * empfangen und verarbeitet. Dabei wird die von einem Client
	 * neu besuchte URL übertragen.
	 * 
	 * @param hash Client-Hash
	 * @param id Message-ID
	 * @param count Anzahl der Parameter der eingetroffenen Nachricht
	 * @param in Reader, der den Zeichenstrom einliest
	 * @return true, falls die Nachricht korrekt gelesen werden konnte,
	 *         sonst false
	 * @throws InvalidMessageException
	 * @throws IOException
	 * @throws SQLException
	 */	
	protected boolean visiting(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException , SQLException{
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
	 * Diese Nachricht wird sowohl vom Client wie auch vom Server
	 * empfangen und verarbeitet. Es wird das Verschieben eines Folders
	 * (genmäß RFC0815) angezeigt.
	 * 
	 * @param hash Client-Hash
	 * @param id Message-ID
	 * @param count Anzahl der Parameter der eingetroffenen Nachricht
	 * @param in Reader, der den Zeichenstrom einliest
	 * @return true, falls die Nachricht korrekt gelesen werden konnte,
	 *         sonst false
	 * @throws InvalidMessageException
	 * @throws IOException
	 * @throws SQLException
	 */	
	protected boolean moveFolder(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException , SQLException{
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
	 * Diese Nachricht wird sowohl vom Client wie auch vom Server
	 * empfangen und verarbeitet. Es wird das Löschen eines Folders
	 * (genmäß RFC0815) angezeigt.
	 *  
	 * @param hash Client-Hash
	 * @param id Message-ID
	 * @param count Anzahl der Parameter der eingetroffenen Nachricht
	 * @param in Reader, der den Zeichenstrom einliest
	 * @return true, falls die Nachricht korrekt gelesen werden konnte,
	 *         sonst false
	 * @throws InvalidMessageException
	 * @throws IOException
	 * @throws SQLException
	 */	
	protected boolean deleteFolder(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException , SQLException{
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
	 * Diese Nachricht wird sowohl vom Client wie auch vom Server
	 * empfangen und verarbeitet. Es wird das Anlegen eines neuen Folders
	 * (genmäß RFC0815) angezeigt.
	 * 
	 * @param hash Client-Hash
	 * @param id Message-ID
	 * @param count Anzahl der Parameter der eingetroffenen Nachricht
	 * @param in Reader, der den Zeichenstrom einliest
	 * @return true, falls die Nachricht korrekt gelesen werden konnte,
	 *         sonst false
	 * @throws InvalidMessageException
	 * @throws IOException
	 * @throws SQLException
	 */	
	protected boolean newFolder(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException , SQLException{
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
	 * Diese Nachricht wird sowohl vom Client wie auch vom Server
	 * empfangen und verarbeitet. Dabei wird die von einem Client
	 * neu erfolgte Bewertung &uuml;bertragen
	 * 
	 * @param hash Client-Hash
	 * @param id Message-ID
	 * @param count Anzahl der Parameter der eingetroffenen Nachricht
	 * @param in Reader, der den Zeichenstrom einliest
	 * @return true, falls die Nachricht korrekt gelesen werden konnte,
	 *         sonst false
	 * @throws InvalidMessageException
	 * @throws IOException
	 * @throws SQLException
	 */	
	protected boolean voting(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException , SQLException{
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	
	/**
	 * Verarbeitung der NOTIFY-Nachricht.
	 * @param hash Der Client-Hash.
	 * @param id Die Nachrichten-ID.
	 * @param count Anzahl der Parameter der eingetroffenen Nachricht
	 * @param in Reader, der den Zeichenstrom einliest
	 * @return true, falls die Nachricht korrekt gelesen werden konnte,
	 *         sonst false
	 * @throws InvalidMessageException
	 * @throws IOException
	 * @throws SQLException
	 */
	protected boolean notify(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException , SQLException{
		throw new InvalidMessageException("Packet type unknown.");
	}
	
	/**
	 * Dies ist die zentrale Methode für den Server, die einen Eingangs-
	 * strom liest und dann zu den passenenden Schlüsselwörtern die
	 * jeweiligen Methoden aufruft.
	 * 
	 * In der aktuellen Implementierung wird jeweils nur ein Paket
	 * bearbeitet und die Methode kehrt dann wieder zurück.
	 * 
	 * Diese Methode kann eine InvalidMessageException werfen, wenn
	 * die entsprechenden aufgerufenen Methoden dies tun.
	 *
	 * @param in Reader zum Lesen des Eingangsstroms
	 * @throws SQLException 
	 * @throws IOException 
	 */	
	public boolean eat_up_ClientPacket(BufferedReader in)
	throws InvalidMessageException, SQLException, IOException {
	    boolean result = false;
		String buf;
		// Zuerst werden die Header eingelesen
		String[] header = readHeader(in);
		// Hier wird die Methode eingelesen
		buf = in.readLine();
		// header kann null sein.
		if( header != null )
			setMessageID(header[1]);
		if ( buf != null  && header != null ) {
			// Jetzt werden endlich die Methoden aufgeufen:
			if ( buf.equals("CHAT") ) {
				result = chat(header[0], header[1], header[2], in);
			} else if ( buf.equals("TYPING") ) {
				result = typing(header[0], header[1], header[2], in);			
			} else if ( buf.equals("READING") ) {
				result = reading(header[0], header[1], header[2], in);
			} else if ( buf.equals("VIEWING") ) {
				result = viewing(header[0], header[1], header[2], in);
			} else if ( buf.equals("VISITING") ) {
				result = visiting(header[0], header[1], header[2], in);
			} else if ( buf.equals("VOTING") ) {
				result = voting(header[0], header[1], header[2], in);
			} else if ( buf.equals("PARENTFOLDER_CHANGED")) {
				result = parentfolderChanged(header[0], header[1], header[2], in);
			} else if ( buf.equals("GET_SAME_URL_VIEWRS")) {
				result = getSameURLViewers(header[0], header[1], header[2], in);
			} else if ( buf.equals("START_CHAT")) {
				result = startChat(header[0], header[1], header[2], in);
			} else if ( buf.equals("JOIN_CHAT")) {
				result = joinChat(header[0], header[1], header[2], in);
			} else if ( buf.equals("MY_PROJECTS")) {
				result = myProjects(header[0], header[1], header[2], in);
			} else if ( buf.equals("JOIN_PROJECT")) {
				result = joinProject(header[0], header[1], header[2], in);
			} else if ( buf.equals("DONT_ACCEPT_PROJECT")) {
				result = projectNotAccepted(header[0], header[1], header[2], in);
			} else if ( buf.equals("GET_WEBTRACE")) {
				result = webtrace(header[0], header[1], header[2], in);
			} else if ( buf.equals("LEAVE_PROJECT")) {
				result = leaveProject(header[0], header[1], header[2], in);
			} else if ( buf.equals("START_PROJECT")) {
				result = startProject(header[0], header[1], header[2], in);
			} else if ( buf.equals("GET_PROJECTS")) {
				result = getProjects(header[0], header[1], header[2], in);
			} else if ( buf.equals("GET_INVITATIONS")) {
				result = getInvitations(header[0], header[1], header[2], in);
			} else if ( buf.equals("OPEN_PROJECT")) {
				result = openProject(header[0], header[1], header[2], in);
			} else if ( buf.equals("GET_FOLDERSYSTEM")) {
				result = getFoldersystem(header[0], header[1], header[2], in);
			} else if ( buf.equals("GET_CHAT_MEMBERS")) {
				result = getChatMembers(header[0], header[1], header[2], in);
			} else if ( buf.equals("ADD_BUDDY")) {
				result = addBuddy(header[0], header[1], header[2], in);		
			} else if ( buf.equals("DEL_BUDDY")) {
				result = delBuddy(header[0], header[1], header[2], in);			
			} else if ( buf.equals("MY_BUDDIES")) {
				result = myBuddies(header[0], header[1], header[2], in);
			} else if ( buf.equals("NEW_FOLDER")) {
				result = newFolder(header[0], header[1], header[2], in);
			} else if ( buf.equals("DELETE_FOLDER")) {
				result = deleteFolder(header[0], header[1], header[2], in);
			} else if ( buf.equals("MOVE_FOLDER")) {
				result = moveFolder(header[0], header[1], header[2], in);
			} else if ( buf.equals("LOGIN")) {
				result = login(header[0], header[1], header[2], in);
			} else if ( buf.equals("LOGOUT")) {
				result = logout(header[0], header[1], header[2], in);
			} else if ( buf.equals("REGISTER")) {
				result = register(header[0], header[1], header[2], in);
			} else if ( buf.equals("SEARCH_USERS")) {
				result = searchUsers(header[0], header[1], header[2], in);
			} else if ( buf.equals("GET_PROFILE")) {
				result = getProfile(header[0], header[1], header[2], in);
			} else if ( buf.equals("SET_PROFILE")) {
				result = setProfile(header[0], header[1], header[2], in);
			} else if ( buf.equals("GET_ACTIVE_USERS")) {
				result = getActiveUsers(header[0], header[1], header[2], in);
			} else if ( buf.equals("NOTIFY")) {
				result = notify(header[0], header[1], header[2], in);
			} else if ( buf.equals("ADD_NOTE")) {
				result = addNote(header[0], header[1], header[2], in);
			} else if ( buf.equals("GET_NOTES")) {
				result = getNotes(header[0], header[1], header[2], in);
			} else {
				result = false;
			}
	    } else {
		    // Fehler beim Lesen des Streams.
		    return false;
	    }	
		return result;
	}

	/**
	 * Dies ist die zentrale Methode für den Client, die einen Eingangs-
	 * strom liest und dann zu den passenenden Schlüsselwörtern die
	 * jeweiligen Methoden aufruft.
	 * 
	 * In der aktuellen Implementierung wird jeweils nur ein Paket
	 * bearbeitet und die Methode kehrt dann wieder zurück.
	 * 
	 * Diese Methode kann eine InvalidMessageException werfen, wenn
	 * die entsprechenden aufgerufenen Methoden dies tun.
	 *
	 * @param in Reader zum Lesen des Eingangsstroms
	 * @throws SQLException 
	 * @throws SQLException 
	 * @throws IOException 
	 */	
	public boolean eat_up_ServerPacket(BufferedReader in)
	throws InvalidMessageException, SQLException {
		boolean result = false;
		String buf;
		try {
			// Zuerst werden die Header eingelesen
			String[] header = readHeader(in);
			// Hier wird die Methode eingelesen
			buf = in.readLine();
			if ( buf != null  && header != null ) {
				// Jetzt werden endlich die Methoden aufgerufen:
				if ( buf.equals("CHAT") ) {
					result = chat(header[0], header[1], header[2], in);
				} else if ( buf.equals("NEW_CHAT")) {
					result = newChat(header[0], header[1], header[2], in);
				} else if ( buf.equals("TYPING") ) {
					result = typing(header[0], header[1], header[2], in);			
				} else if ( buf.equals("READING") ) {
					result = reading(header[0], header[1], header[2], in);
				} else if ( buf.equals("VIEWING") ) {
					result = viewing(header[0], header[1], header[2], in);
				} else if ( buf.equals("VISITING") ) {
					result = visiting(header[0], header[1], header[2], in);
				} else if ( buf.equals("VOTING") ) {
					result = voting(header[0], header[1], header[2], in);
				} else if ( buf.equals("PARENTFOLDER_CHANGED")) {
					result = parentfolderChanged(header[0], header[1], header[2], in);
				} else if ( buf.equals("NEW_FOLDER")) {
					result = newFolder(header[0], header[1], header[2], in);
				} else if ( buf.equals("DELETE_FOLDER")) {
					result = deleteFolder(header[0], header[1], header[2], in);
				} else if ( buf.equals("MOVE_FOLDER")) {
					result = moveFolder(header[0], header[1], header[2], in);
				} else if ( buf.equals("SAME_URL_VIEWERS")) {
					result = sameURLViewers(header[0], header[1], header[2], in);
				} else if ( buf.equals("FOLDERSYSTEM")) {
					result = foldersystem(header[0], header[1], header[2], in);
				} else if ( buf.equals("WEBTRACE") ) {
					result = webtrace(header[0], header[1], header[2], in);
				} else if ( buf.equals("ERROR")) {
					result = error(header[0], header[1], header[2], in);
				} else if ( buf.equals("YOUR_BUDDIES")) {
					result = yourBuddies(header[0], header[1], header[2], in);
				} else if ( buf.equals("NEW_BUDDY")) {
					result = newBuddy(header[0], header[1], header[2], in);
				} else if ( buf.equals("USER_STATUS")) {
					result = userStatus(header[0], header[1], header[2], in);
				} else if ( buf.equals("PROJECTS")) {
					result = projects(header[0], header[1], header[2], in);
				} else if ( buf.equals("YOUR_PROJECTS")) {
					result = yourProjects(header[0], header[1], header[2], in);
				} else if ( buf.equals("NEW_PROJECT")) {
					result = newProject(header[0], header[1], header[2], in);
				} else if ( buf.equals("NEW_INVITATION")) {
					result = newInvitation(header[0], header[1], header[2], in);
				} else if ( buf.equals("PROJECT_CHAT")) {
					result = projectOpened(header[0], header[1], header[2], in);
				} else if ( buf.equals("FREESPACES_CHANGED")) {
					result = freeSpacesChanged(header[0], header[1], header[2], in);
				} else if ( buf.equals("ACCESS_GRANTED")) {
					result = access_granted(header[0], header[1], header[2], in);
				} else if ( buf.equals("ACCESS_DENIED")) {
					result = access_denied(header[0], header[1], header[2], in);
				} else if ( buf.equals("ACK")) {
					result = ack(header[0], header[1], header[2], in);		
				} else if ( buf.equals("USER_EXISTS")) {
					result = userExists(header[0], header[1], header[2], in);
				} else if ( buf.equals("USERS")) {
					result = users(header[0], header[1], header[2], in);
				} else if ( buf.equals("CHAT_MEMBERS")) {
					result = chatMembers(header[0], header[1], header[2], in);
				} else if ( buf.equals("MEMBER_CHANGE")) {
					result = memberChange(header[0], header[1], header[2], in);
				} else if ( buf.equals("PROFILE")) {
					result = profile(header[0], header[1], header[2], in);
				} else if ( buf.equals("ACTIVE_USERS")) {
					result = activeUsers(header[0], header[1], header[2], in);
				} else if ( buf.equals("NOTIFY")) {
					result = notify(header[0], header[1], header[2], in);
				} else if ( buf.equals("NEW_NOTE")) {
					result = newNote(header[0], header[1], header[2], in);
				} else if ( buf.equals("NOTES")) {
					result = notes(header[0], header[1], header[2], in);
				} else {
					result = false;
				}
		    } else {
			    // Fehler mein Lesen des Streams.
			    return false;		
			}
		} catch (IOException e) {
			Logger.getLogger(Message_Handler.class).warn(e.toString());
		}
		return result;
	}

	/**
	 * Hilfsmethode der Methoden, die die Nachrichten einlesen.
	 * Liest die Header der Nachrichten.
	 * @param in Reader, der den Eingangsstrom einliest.
	 * @return Die eingelesenen Header in einem String-Array
	 * 			oder null, wenn der Socket/Eingabestrom geschlossen ist.
	 * @throws IOException
	 * @throws InvalidMessageException
	 */	
	private String[] readHeader(BufferedReader in) throws IOException, 
	InvalidMessageException {
		String[] header = null;
		// ClientHash bei Servernachrichten oder ein Username bei 
		// weitergeleiteten Chatnachrichten
		// Wenn null gelesen wird, ist der Socket geschlossen
		// und diese Methode muss null zurück geben.
		String userOrHash = in.readLine();
		if ( userOrHash == null )
			return null;
		// Message-ID
		String id = in.readLine();
		if ( id == null )
			return null;
		// Anzahl der Nachrichtenparameter
		String count = in.readLine();
		if ( count == null )
			return null;
		// Im Debug-Modus werden die Header ausgegeben
		if (debug) {
			// Ausgabe der Header
			Logger.getLogger(Message_Handler.class).debug("1.Header: "+userOrHash);
			Logger.getLogger(Message_Handler.class).debug("2.Header: "+id);
			Logger.getLogger(Message_Handler.class).debug("3.Header: "+count);
		}
		// Die InvalidMessageException wird geworfen, wenn etwas gelesen
		// werden konnte, was aber nicht dem Paketformat nach RFC 0815 entspricht.
		if ((id.compareTo("") == 0)||(count.compareTo("") == 0)) {
			throw new InvalidMessageException("Headerfield leer:\nuserOrHash="+userOrHash+"\nid="+id+"\ncount="+count+"\n");
		} else {
			header = new String[3];
			header[0] = userOrHash;
			header[1] = id;
			header[2] = count;
		}
		return header;
	}

}
