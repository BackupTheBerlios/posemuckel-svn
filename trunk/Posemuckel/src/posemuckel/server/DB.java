/**
 * 
 */
package posemuckel.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import posemuckel.common.Config;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.GetText;

/**
 * Objekte dieser Klasse bieten Methoden an, um auf eine Posemuckel-
 * Datenbank zuzugreifen.
 * @author Posemuckel Team
 *
 */
public class DB {

	/**
	 * Verbindung zur MySQL-Datenbank
	 */
	private Connection connection;
	
	/**
	 * ResultSet, das Ergebnisse von Anfragen an die Datenbank enthält
	 */
	private ResultSet dbresult;
	
	/**
	 * Enthält den Status nach Datenbankoperationen
	 */
	private int dbupdate;
	
	/**
	 * Dient zum Stellen von Anfragen und Updates an die Datenbank
	 */
	private Statement statement;
	
	/**
	 * Die zuletzt generierte Ordner-ID
	 */
	private int latestFolderID;
	
	/**
	 * Logbuch über die Datenbank
	 */
	private Logger logger;
	
	/**
	 * Dies ist der Konstruktor, der sich die DB-Konfiguration
	 * aus den globalen Konfigurationsdaten holt.
	 */
	public DB() {
		super();
		Config config = Config.getInstance();
		logger = Logger.getLogger(this.getClass());
		connect2DB(config.getconfig("DB_USER"), config.getconfig("DB_PASS"), config.getconfig("DB_HOST"), config.getconfig("DB_NAME"));
	}
	
	/**
	 * Dies ist der "alte" Konstruktor. Er muss erhalten bleiben, weil
	 * er (noch) von den Unit-Tests verwendet wird.
	 * @param user Benutzername
	 * @param pass
	 * @param host
	 * @param db
	 */
	public DB(String user, String pass, String host, String db) {
			super();
			logger = Logger.getLogger(this.getClass());
			connect2DB(user, pass, host, db);	
	}

	  /**
	   * Baut eine Verbindung zur Datenbank auf.
	   * @param user Benutzername
	   * @param pass Passwort
	   * @param host Host
	   * @param db Name der Datenbank
	   */
	private void connect2DB(String user, String pass, String host, String db) {
		try {
			ResourceBundle message = PropertyResourceBundle.getBundle("posemuckel.server.Messages",Locale.getDefault());
			if( message != null ) {
				try {
					Class.forName("com.mysql.jdbc.Driver");
				} catch (Exception e) {
					logger.error(message.getString("ERR_DB_REFLECT")+"\n"+e.getMessage());
					//System.out.println(message.getString("ERR_DB_REFLECT")+"\n"+e.getMessage());
				}
				try {
					// Try to connect to the database.
					connection = DriverManager.getConnection("jdbc:mysql://"+host+"/"+db+"?user="+user+"&password="+pass);
					connection.setAutoCommit(false);
				  	statement = connection.createStatement();
				} catch (SQLException e) {
					logger.error(message.getString("ERR_DB_CONNECT")+"\n"+e.getMessage());
					//System.out.println(message.getString("ERR_DB_CONNECT")+"\n"+e.getMessage());
				} 
			}
		} catch (MissingResourceException e) {
			logger.error("ERROR: Could not find Resource Messages. Check if it is in CLASSPATH.");
			//System.out.println("ERROR: Could not find Resource Messages. Check if it is in CLASSPATH.");
		}
	}

	/**
	 * Hilfsmethode, die die IP des übergebenen Benutzers überprüft und
	 * gegebenenfalls in der Datenbank aktualisiert.
	 * @param user Benutzername
	 * @throws SQLException
	 */
	private void checkIP(String user) throws SQLException {
		String aktIP = "127.0.0.1"; //clientInfo.getSocket().getInetAddress().toString();
		String query = "SELECT user_ip FROM user WHERE nickname='"+user+"';";
		dbresult = statement.executeQuery(query);
		dbresult.next();
		String oldIP = dbresult.getString("user_ip");
		if (!oldIP.equals(aktIP)) {
			statement.executeUpdate("UPDATE user SET user_ip='"+aktIP+"' WHERE nickname='"+user+"';");
		}	
    }
	
	/**
	 * Hilfsmethode, die die aktuelle Zeit im Format hh:mm:ss als String
	 * zurückgibt.
	 * @return die aktuelle Zeit als String
	 */
	private String getTime() {
        String temp = new String();
		Calendar calendar = new GregorianCalendar();
	    Date curDate = new Date();
		calendar.setTime(curDate);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		if (hours < 10) {
		    temp = "0" + hours;
		} else {
		    temp = "" + hours;
		}
        temp = temp + ":";
        if (minutes < 10) {
		    temp = temp + "0" + minutes;
        } else {
		    temp = temp + minutes;
        }
		return temp + ":" + seconds;
	}
	
	/**
	 * Methode, die das aktuelle Datum und die aktuelle Zeit als
	 * String zurückgibt
	 * @return aktuelles Datum und Zeit als String
	 */
	public String getDatetime() {			
	    return getDate() + " " + getTime();
	}
	
	/**
	 * Liefert das aktuelle Datum als String im Format YY-MM-DD
	 * @return das aktuelle Datum als String
	 */
	public String getDate() {
		String temp = new String();
		Calendar calendar = new GregorianCalendar();
		Date curDate = new Date();
		calendar.setTime(curDate);	
		int year = calendar.get(Calendar.YEAR);
	    int month = calendar.get(Calendar.MONTH);
	    /*
	     * Calendar fängt bei den Monaten bei 0 an zu zählen
	     */
	    month++;
	    int day = calendar.get(Calendar.DAY_OF_MONTH);		
		temp += year+"-";		
		if (month < 10) {
			temp += "0"+ month + "-";	
		} else {
			temp += month + "-";
		}		
		if (day < 10) {
			temp += "0" + day + " ";
		} else {
			temp += day + " ";
		}
		return temp;
	}

	/**
	 * Fügt einen neuen Benutzer der Datenbank hinzu.
	 * @param name Nachname des Benutzers
	 * @param surname Vorname des Benutzers
	 * @param email E-Mailadresse des Benutzers
	 * @param nickname Nickname des Benutzers
	 * @param password Passwort
	 * @param lang Sprache des Benutzers
	 * @param gender Geschlecht des Benutzers
	 * @param location Wohnort des Benutzers
	 * @param comment Kommentar des Benutzers
	 * @param ip IP-Adresse des Benutzers
	 * @throws SQLException
	 */
	protected void addUser(String name, String surname, String email,
			String nickname, String password, String lang, String gender,
			String location, String comment, String ip) throws SQLException {
		String stmt = "INSERT INTO user SET firstName='"+
		name+"', LastName='"+surname+"', email='"+email+
		"', nickname='"+nickname+"', password='"+password+
		"', lang='"+lang+"', gender='"+gender+
		"', location='"+location+
		"', user_comment='"+comment+
		"', user_ip='"+ip+"', logged_in='0';";
    	statement.executeUpdate(stmt);
		connection.commit();
	}
	
	/**
	 * Entfernt den Benutzer mit dem übergebenen Nicknamen aus der
	 * Datenbank
	 * @param nickname Nickname des zu löschenden Benutzers
	 * @throws SQLException
	 */
	protected void deleteUser(String nickname) throws SQLException {
		statement.executeUpdate("DELETE FROM user WHERE nickname='"+
				nickname+"';");
		connection.commit();	
	}
	
	/**
	 * Für die Datenbanktests: Liefert ein STATEMENT-Objekt der 
	 * Datenbank für Datenbankoperationen
	 * @return ein STATEMENT-Objekt der Datenbank
	 */
	protected Statement getStatement() {
		return this.statement;
	}
	
	/**
	 * Gibt an, ob der übergebene Benutzer in der Datenbank schon ent-
	 * halten ist.
	 * @param name Benutzername (Nickname)
	 * @return true, falls der Benutzer schon in der Datenbank enthalten
	 * ist, false sonst
	 * @throws SQLException
	 */
	public boolean existsUser(String name) throws SQLException{
		String query = "SELECT nickname FROM user WHERE nickname='"+name+"';";
		dbresult = statement.executeQuery(query);
		if(!dbresult.next()) {
			return false;
		} else {
			checkIP(dbresult.getString("nickname"));
	    }
		connection.commit();	
		return true;
	}

	/**
	 * Gibt an, ob es bereits ein Projekt mit dem angegebenen Namen gibt.
	 * @param title Titel des Projektes
	 * @return true, falls es schon ein Projekt mit diesem Namen gibt, false sonst
	 * @throws SQLException
	 */
	public boolean projectExists(String title) throws SQLException{
		String query = "SELECT project_title FROM projects WHERE project_title='"+title+"';";
		dbresult = statement.executeQuery(query);
		if(!dbresult.next()) {
			return false;
		} else {
			checkIP(dbresult.getString("project_title"));
	    }
		connection.commit();	
		return true;
	}
	
	/**
	 * Fügt einen Chattext mit Zeitstempel in die Datenbank ein.
	 * @param text der einzufügende Text
	 * @param user Benutzer, der den Text verfasst hat
	 * @throws SQLException
	 */
	public void addText(String text, String user, String chatID, String projectID) throws SQLException {
		if ((!chatID.equals("0"))&&(!chatID.equals(EnumsAndConstants.LOG_CHAT_ID))) {
			statement.executeUpdate("INSERT INTO chat_progress SET user_nickname='"+user
					+"', phrase='"+text+"', chat_id='"+chatID+"', progress_timestamp='"+getDatetime()+"'");
		}
		if (chatID.equals(EnumsAndConstants.LOG_CHAT_ID)) {
			statement.executeUpdate("INSERT INTO log SET project_id='"+projectID+
					"', text='"+text+"', timestamp='"+getDatetime()+"';");
		}
		connection.commit();	
	}

	/**
	 * @param user Der Benutzername (nickname), der Systemweit eindeutig ist.
	 * @param pass Das Passwort des Benutzers.
	 * @return Gibt true zurück, falls Benutzername und Passwort zusammenpassen,
	 * sonst false.
	 * @throws SQLException 
	 */
	public boolean correct_login(String user, String pass) throws SQLException {
		boolean b = false;
		dbresult = statement.executeQuery("SELECT password,nickname FROM user "+
				"WHERE nickname='"+user+"';");
		if (dbresult.next()) {
			String s1 = dbresult.getString("password");
			String s2 = dbresult.getString("nickname");
			if ( s1 != null && s2 != null ) {
				if (s1.equals(pass) && s2.equals(user) ) {
					b = true;
				}
			}
		}
		connection.commit();	
		return b;
	}
	
	/**
	 * Legt ein neues Projekt in der Datenbank an
	 * @param user Eigentümer des Projektes
	 * @param topic Thema des Projektes
	 * @param isPrivate gibt an, ob das Projekt öffentlich oder privat ist
	 * @param maxUsers maximale Anzahl von Projektteilnehmern
	 * @param description Beschreibung des Projektes
	 * @param date Erstellungsdatum des Projektes
	 * @param userToInvite enthält die Benutzer, die zum Projekt einge-
	 * laden werden
	 * @return die Projekt-ID des Projektes
	 * @throws SQLException
	 */
	public int addProject(String user, String topic, String isPrivate,
			String maxUsers, String description, String date, String[] userToInvite) throws SQLException {
		String priv;
		String s;
		int projectID = 0;
		if (isPrivate.compareTo("0") == 0) {
			priv = "1";
			s = "PRIVATE";
		} else {
			priv = "0";
			s = "PUBLIC";
		}
		int chatID;
		/*
		 * Projektchat wird angelegt
		 */
		statement.executeUpdate("INSERT INTO chat SET private_chat='"+
				priv+"', chat_owner='"+user+"';");
		chatID = getLatestGeneratedID("chat", "chat_id");
		/*
		 * Projekt wird angelegt
		 */
		statement.executeUpdate("INSERT INTO projects SET project_title='"+topic+
				"', project_description='"+description+"', count_members='0',"+
				"max_members='"+maxUsers+"', project_owner='"+user+"', project_type='"+
				s+"', project_chat='"+chatID+"', project_date='"+date+"';");
	    projectID = getLatestGeneratedID("projects", "project_id");
	    /*
	     * Projektordner für unsortierte URLs wird angelegt
	     */
	    statement.executeUpdate("INSERT INTO folders SET project_id='"+
	    		projectID+"', name='UNSORTED', unsorted_folder='1';");
	    insertInvitedUsers(projectID, userToInvite);
	    connection.commit();	
		return projectID;
	}
	
	/**
	 * Liefert die Chat-ID eines Projektchats zu einer gegebenen Projekt-
	 * ID.
	 * @param projectID die übergebene Projekt-ID
	 * @return die Chat-ID des Projektchats
	 * @throws SQLException
	 */
	public String getChatID(int projectID) throws SQLException {
		String chatID = "";
		dbresult = statement.executeQuery("SELECT project_chat FROM projects "+
				"WHERE project_id='"+projectID+"';");
		dbresult.next();
		chatID = dbresult.getString("project_chat");
		connection.commit();	
		return chatID;
	}
	
	/**
	 * Hilfsmethode, die die zu einem Projekt eingeladenen Benutzer in die
	 * Tabelle project_inviteduser der Datenbank einträgt.
	 * @param projectID Projekt-ID des Projektes, zu dem die Benutzer ein-
	 * geladen werden
	 * @param userToInvite enthält die Benutzer, die eingeladen werden 
	 * sollen.
	 * @throws SQLException
	 */
	private void insertInvitedUsers(int projectID, String[] userToInvite) throws SQLException {
		for ( String user : userToInvite ) {
			statement.executeUpdate("INSERT INTO project_inviteduser "+
					"SET project_id='"+projectID+"', invited_user='"+
					user+"', " +
					"invitation_confirm='0', invitation_answered='0';");
		}	
	}

	/**
	 * Entfernt ein Projekt aus der Datenbank
	 * @param projectID Projekt-ID des Projektes, das entfernt werden soll
	 * @throws SQLException
	 */
	public void deleteProject(int projectID) throws SQLException {
		deleteFolders(projectID);
		statement.executeUpdate("DELETE FROM projects WHERE "+
				"project_id='"+projectID+"';");
		connection.commit();	
	}
	
	/**
	 * Entfernt ein Projekt aus der Datenbank, das das übergebene Thema
	 * enthält.
	 * @param topic Thema des Projektes, das gelöscht werden soll
	 * @throws SQLException
	 */
	public void deleteProject(String topic) throws SQLException {
		try {
			connection.createStatement().executeUpdate("DELETE FROM "+
					"projects WHERE project_title='"+topic+"';");
			//TODO: Folder und URLs löschen
		} catch (SQLException e) {
			System.err.println("trying to delete project " + topic);
			e.printStackTrace();
		}
		connection.commit();			
	}
	
	/**
	 * Entfernt die Ordner aus der Datenbank, die zu dem Projekt mit der
	 * übergebenen Projekt-ID gehört
	 * @param projectID die übergebene Projekt-ID
	 * @throws SQLException
	 */
	private void deleteFolders(int projectID) throws SQLException {
		statement.executeUpdate("DELETE folders, folder_urls FROM folder_urls "+
				"NATURAL JOIN folders WHERE project_id='"+projectID
				+"';");		
	}
	
	/**
	 * Entfernt die Ordner aus der Datenbank, die zu dem Projekt mit dem
	 * übergebenen Thema gehört
	 * @param topic das Thema des Projektes, dessen Ordner entfernt werden
	 * sollen
	 * @throws SQLException
	 */
	protected void deleteProjectFolders(String topic) throws SQLException {
		dbresult = statement.executeQuery("SELECT project_id FROM projects WHERE "+
				"project_title='"+topic+"';");
		if(dbresult.next()) {
			deleteFolders(dbresult.getInt("project_id"));
		}
		connection.commit();			
	}
	
	/**
	 * Schaut in der Datenbank nach, ob der Benutzer mit dem Nicknamen
	 * user eingeloggt ist
	 * @param user String, der den Nicknamen des Benutzers enthält
	 * @return true, falls der Benutzer in der Datenbank steht und ein-
	 * geloggt ist, false sonst
	 * @throws SQLException 
	 */
	public boolean isLoggedIn(String user) throws SQLException {
		boolean b = false;
		dbresult = statement.executeQuery("SELECT logged_in FROM "+
				"user WHERE nickname='"+user+"';");
		if (!dbresult.next()) {
			System.out.println("User noch nicht in DB eingefügt!");
			b = false;
		} else {
			b = dbresult.getBoolean("logged_in");
		}
		connection.commit();	
		return b;
	}

	/**
	 * Setzt bei dem Benutzer mit dem Nicknamen user den Loginstatus
	 * auf true
	 * @param user Nicknamen des Benutzers, der in die DB eingeloggt wird
	 * @throws SQLException 
	 */
	public void login(String user, String hash) throws SQLException {
		statement.executeUpdate("UPDATE user SET hash='"+hash+"', "+
				"logged_in='1' WHERE nickname='"+user+"';");
		connection.commit();	
	}

	/**
	 * Wird aufgerufen, wenn ein neuer Chat in die Datenbank aufgenommen
	 * werden soll.
	 * @param isPrivate gibt an, ob dies ein privater Chat ist
	 * @param user bestimmt den Chatbesitzer
	 * @param users 
	 * @throws SQLException 
	 */
	public int addChat(boolean isPrivate, String user, Vector users) throws SQLException {
		Iterator it = users.iterator();
		String s;
		int chatID = 0;
		if (isPrivate) {
			s = "1";				
		} else {
			s = "0";
		}
		statement.executeUpdate("INSERT INTO chat SET private_chat='"+
				s+"', chat_owner='"+user+"';");
		chatID = getLatestGeneratedID("chat", "chat_id");
		statement.executeUpdate("INSERT INTO user_chat SET "+
				"chat_id='"+chatID+"', user_nickname='"+user+"';");
		while (it.hasNext()) {
			statement.executeUpdate("INSERT INTO user_chat SET "+
					"chat_id='"+chatID+"', user_nickname='"+
					(String)it.next()+"';");
		}
		connection.commit();	
		return chatID;
	}

	/**
	 * Setzt den Status des übergebenen Benutzers auf ausgeloggt.
	 * @param user Benutzer, dessen Status auf ausgeloggt gesetzt wird
	 * @throws SQLException
	 */
	public void logout(String user) throws SQLException {
		statement.executeUpdate("UPDATE user SET logged_in='0' "+
				"WHERE nickname='"+user+"';");
		connection.commit();	
	}
	
	/**
	 * Gibt den zuletzt generierten Schlüssel zurück, der in der Tabelle
	 * table in der Spalte column erzeugt wurde
	 * @param table Name der Tabelle
	 * @param column Name der Spalte
	 * @return 0, falls die Tabelle leer ist, sonst den zuletzt generierten
	 * Schlüssel
	 * @throws SQLException 
	 */
	protected int getLatestGeneratedID(String table, String column) throws SQLException {
		int i = 0;
		dbresult = statement.executeQuery("SELECT MAX("+
				column+") FROM "+table+";");
		if (dbresult.next()) {
			i = dbresult.getInt(1);
		} 	
		return i;
	}

	/**
	 * Wird aufgerufen, wenn ein user die Einladung zu einem chat annimmt
	 * Die datenbank bietet meiner Meinung nach allerdings noch nicht die M&ouml;glichkeit eine
	 * Benutzer zu einem Chat zu einem Projekt einzuladen, der parallel 
	 * zu einem schon bestehenden Projektchat besteht. Der Methodenrumpf muss
	 * deher noch gef&uuml;llt werden.
	 * 
	 * @param user Benutzer, der eine Einladung annimmt
	 * @param chatID ID des Chats zu dem er die Einladung annimmt
	 */
	public void addUserToChat(String user, String chatID) {
		//TODO Methodenrumpf füllen
	}
	
	/**
	 * Wird aufgerufen, um zu überprüfen, ob ein Chat privat ist oder nicht
	 * @param chatID
	 * @return true, wenn der Chat privat ist, false sonst
	 */
	public boolean isPrivateChat(String chatID){
		return false;
	}

	/**
	 * Fügt einen Benutzer einem bestimmten Projekt hinzu.
	 * @param user Benutzer, der dem Projekt hinzugefügt werden soll
	 * @param projectID Projekt-ID des Projektes, zu dem der Benutzer 
	 * hinzugefügt werden soll
	 * @throws SQLException
	 */
	public void addUserToProject(String user, String projectID) throws SQLException {
		dbresult = statement.executeQuery("SELECT project_chat, "+
				"count_members, max_members FROM projects WHERE project_id='"+
				projectID+"';");
		if (!dbresult.next()) {
			throw new SQLException("Projekt noch nicht eingetragen!");
		} else {
			String chatID = dbresult.getString("project_chat");
			int count = new Integer(dbresult.getString(
			"count_members")).intValue();
			int members = new Integer(dbresult.getString(
					"max_members")).intValue();
			/*
			 * Projekt noch nicht voll
			 */
			if (count != members) {
				statement.executeUpdate("INSERT INTO user_chat SET "+
						"chat_id='"+chatID+"', user_nickname='"+user+
						"';");
				statement.executeUpdate("INSERT INTO members SET "+
						"project_id='"+projectID+"', user_nickname='"+
						user+"';");			
				dbupdate = statement.executeUpdate("UPDATE projects SET "+
						"count_members='"+(count+1)+"' "+
						"WHERE project_id='"+projectID+"';");
				statement.executeUpdate("UPDATE project_inviteduser "+
    					"SET invitation_confirm='0', invitation_answered='0'" +
    					"WHERE project_id='"+projectID+"' AND invited_user='"+
    					user+"';");
				if(isInvited(user, projectID)) {
					answerInvitation(user, projectID, true);
				}
			} else {
				throw new SQLException("Projekt ist schon voll!");
			}
			
		}
		connection.commit();	
	}

	/**
	 * Gibt an, ob ein bestimmtes Projekt privat ist, oder nicht
	 * @param projectID Projekt-ID des Projektes, das überprüft werden
	 * soll
	 * @return true, falls das Projekt privat ist, false sonst
	 * @throws SQLException
	 */
	public boolean isPrivateProject(String projectID) throws SQLException {
		boolean b = true;
		dbresult = statement.executeQuery("SELECT project_type "+
				"FROM projects WHERE project_id='"+projectID+"';");
		if (dbresult.next()) {
			String s = dbresult.getString("project_type");
			if (s.compareTo("PRIVATE") != 0) {
				b = false;
			}
		}
		connection.commit();	
		return b;
	}

	/**
	 * Gibt an, ob ein bestimmter Benutzer zu einem bestimmten Projekt
	 * eingeladen ist, oder nicht.
	 * @param user Benutzer
	 * @param projectID Projekt-ID des Projektes
	 * @return true, falls der Benutzer zu dem Projekt eingeladen ist, 
	 * false sonst.
	 * @throws SQLException
	 */
	public boolean isInvited(String user, String projectID) throws SQLException {
		boolean b = true;
		dbresult = statement.executeQuery("SELECT * FROM "+
				"project_inviteduser WHERE project_id='"+projectID+
				"' AND invited_user='"+user+"';");
		if (!dbresult.next()) {
			b = false;
		}
		connection.commit();	
		return b;
	}
	
	/**
	 * Aktualisiert die Tabelle project_inviteduser als Antwort, ob eine
	 * Einladung angenommen wurde, oder nicht.
	 * @param user Benutzer
	 * @param projectID Projekt-ID des Projektes
	 * @param accept gibt an, ob die Einladung zu dem Projekt angenommen
	 * wurde oder nicht
	 * @throws SQLException
	 */
	public void answerInvitation(String user, String projectID, boolean accept) throws SQLException {
		String s = accept ? "1" : "0";
		statement.executeUpdate("UPDATE project_inviteduser "+
				"SET invitation_confirm='" + s + "', invitation_answered='1'" +
				"WHERE project_id='"+projectID+"' AND invited_user='"+
				user+"';");	
	}
	
	/**
	 * Gibt an, ob der übergebene Benutzer die Einladung zu einem be-
	 * stimmten Projekt angenommen hat, oder nicht
	 * @param user Benutzer
	 * @param projectID Projekt-ID des Projektes
	 * @return true, falls die Einladung angenommen wurde, false sonst
	 * @throws SQLException
	 */
	public boolean hasAccepted(String user, String projectID) throws SQLException {
		boolean b = true;
		dbresult = statement.executeQuery("SELECT invitation_confirm, invitation_answered " +
				"FROM project_inviteduser WHERE project_id='"+projectID+
				"' AND invited_user='"+user+"';");
		if (!dbresult.next()) {
			b = false;
		} else if(!dbresult.getBoolean("invitation_answered")){
			b = false;
		} else if(!dbresult.getBoolean("invitation_confirm")) {
			b = false;
		}
		connection.commit();	
		return b;
	}
	
	/**
	 * Gibt an, ob der übergebene Benutzer Eigentümer des übergebenen 
	 * Projektes ist.
	 * @param user Benutzer
	 * @param projectID Projekt-ID des Projektes
	 * @return true, falls der Benutzer Eigentümer des Projektes ist, false
	 * sonst
	 * @throws SQLException
	 */
	public boolean isOwner(String user, String projectID) throws SQLException {
		boolean b = true;
		dbresult = statement.executeQuery("SELECT project_owner "+
				"FROM projects WHERE project_id='"+projectID+"';");
		dbresult.next();
		b = dbresult.getString("project_owner").equals(user);
		connection.commit();	
		return b;
	}

	/**
	 * Entfernt den übergebenen Benutzer aus einem bestimmten Projekt
	 * @param user Benutzer
	 * @param projectID Projekt-ID des Projektes, aus der der Benutzer
	 * entfernt werden soll.
	 * @throws SQLException
	 */
	public void removeUserFromProject(String user, String projectID) throws SQLException {
		String chatID = null;
		Integer count = null;
		dbresult = statement.executeQuery("SELECT "+
				"project_chat, count_members FROM projects WHERE "+
				"project_id='"+projectID+"';");
		if (!dbresult.next()) {
			throw new SQLException("Projekt nicht vorhanden!");
		} else {
			chatID = dbresult.getString("project_chat");
			count = new Integer(dbresult.getString("count_members"));
			statement.executeUpdate("DELETE FROM members WHERE "+
					"project_id='"+projectID+"' AND user_nickname='"+
					user+"';");
			/*
			 * Benutzer wird vom Projektchat entfernt
			 */
			statement.executeUpdate("DELETE FROM user_chat WHERE "+
					"chat_id='"+chatID+"' AND user_nickname='"+user+"';");
			/*
			 * die Anzahl der Projektmitglieder wird aktualisiert
			 */
			statement.executeUpdate("UPDATE projects SET count_members="+
					"'"+(count.intValue()-1)+"' WHERE project_id='"+
					projectID+"';");
		}
		connection.commit();	
	}
	
	/**
	 * Liefert den Chat-Typ zu einem bestimmten Chat
	 * @param chatID Chat-ID des Chats
	 * @return den String, der dem Typ des übergebenen Chats entspricht 
	 * (Typen: PROJECT, PRIVATE, PUBLIC, UNKNOWN)
	 * @throws SQLException
	 */
	public String getChatType(String chatID) throws SQLException {
		String type = null;
        // Prüfung, ob Projekt-Chat
		dbresult = statement.executeQuery("SELECT * FROM projects "+
				"WHERE project_chat='"+chatID+"';");
		if (dbresult.next()) {
			type = new String("PROJECT");
		} else {
            // Prüfung, ob privater oder öffentlicher Chat
			dbresult = statement.executeQuery("SELECT private_chat FROM"+
					" chat WHERE chat_id='"+chatID+"';");
			if (dbresult.next()) {
				boolean priv = dbresult.getBoolean("private_chat");
				if (priv) {
					type = new String("PRIVATE");
				} else {
					type = new String("PUBLIC");
				}
			} else {
				type = new String("UNKNOWN");
			}
		}
		connection.commit();	
		return type;
	}

	/**
	 * Liefert die Verbindung der Datenbank
	 * @return Verbindung mit der Datenbank
	 */
	protected Connection getConnection() {
		return this.connection;
	}

	/**
	 * Liefert zu einer gegebenen Chat-ID alle Benutzer, die an diesem
	 * Chat teilnehmen, in einem HashSet zurück.
	 * @param chatID Schlüssel für den Chat
	 * @return Liste aller Benutzer, die an diesem Chat teilnehmen, als 
	 * HashSet
	 * @throws SQLException 
	 */
	public HashSet getChatUsers(String chatID) throws SQLException {
		HashSet<String> hs = new HashSet<String>();
		dbresult = statement.executeQuery("SELECT user_nickname "+
				"FROM user_chat WHERE chat_id='"+chatID+"';");
		while (dbresult.next()) {
			hs.add(dbresult.getString("user_nickname"));
		}
		connection.commit();	
		return hs;
	}

	/**
	 * Liefert alle Projektinformationen (siehe RFC0815) zu den Projekten
	 * an denen der übergebene Benutzer teilnimmt.
	 * @param user Benutzer
	 * @return Projektinformationen als Vector
	 * @throws SQLException
	 */
	public Vector getProjects(String user) throws SQLException {
		Vector<String> projects = new Vector<String>();
		dbresult = statement.executeQuery("SELECT * FROM projects WHERE "+
				"project_id IN (SELECT project_id FROM members WHERE "+
				"user_nickname='"+user+"');");
		fillProjectVector(projects, dbresult);
		connection.commit();
		return projects;
	}
	
	/**
	 * Liefert alle Projektinformationen (siehe RFC0815) der Projekte,
	 * zu denen der übergebene Benutzer eingeladen ist.
	 * @param user Benutzer
	 * @return Projektinformationen als Vector
	 * @throws SQLException
	 */
	public Vector getInvitations(String user) throws SQLException {
		Vector<String> projects = new Vector<String>();
		dbresult = statement.executeQuery("SELECT * FROM projects WHERE "+
				"project_id IN (SELECT project_id FROM project_inviteduser WHERE "+
				"invited_user='"+user+"' AND invitation_answered='0');");
		fillProjectVector(projects, dbresult);
		connection.commit();
		return projects;
	}

	/**
	 * Liefert alle Projektinformationen (siehe RFC0815) zu allen Projekten,
	 * die angelegt worden sind.
	 * @return Projektinformationen als Vector
	 * @throws SQLException
	 */
	public Vector getProjects() throws SQLException {
		Vector<String> projects = new Vector<String>();
		dbresult = statement.executeQuery("SELECT * FROM projects;");
		fillProjectVector(projects, dbresult);
		connection.commit();
		return projects;
	}

	/**
	 * Hilfsmethode, die den übergebenen Vector mit Projektinformationen füllt.
	 * @param projects der Vector, der mit den Projektinformationen gefüllt
	 * wird
	 * @param dbresult enthält die, aus der Datenbank, gewonnenen Projektdaten
	 * @throws SQLException
	 */
	private void fillProjectVector(Vector<String> projects, ResultSet dbresult)
	throws SQLException {
		String priv;
		Integer max, count;
		while (dbresult.next()) {
			String projectID = dbresult.getString("project_id");
			projects.add(projectID);
			projects.add(dbresult.getString("project_title"));
			projects.add(dbresult.getString("project_owner"));
			priv = dbresult.getString("project_type");
			if (priv.compareTo("PRIVATE") == 0) {
				projects.add("0");
			} else {
				projects.add("1");
			}
			count = new Integer(dbresult.getString("count_members"));
			max = new Integer(dbresult.getString("max_members"));
			projects.add((max.intValue()-count.intValue()-
					reservationCount(projectID))+"");
			projects.add(dbresult.getString("max_members"));
			projects.add(dbresult.getString("project_description"));
			projects.add(convertDate(dbresult.getString("project_date")));
		}		
	}

	/**
	 * Hilfsmethode, die die Anzahl der reservierten Plätze eines be-
	 * stimmten Projektes zurückgibt.
	 * @param projectID Projekt-ID des Projektes, zu der die Anzahl der
	 * reservierten Plätze ermittelt werden soll.
	 * @return Anzahl der reservierten Plätze
	 * @throws SQLException
	 */
	private int reservationCount(String projectID) throws SQLException {
		int count;
		Statement st = connection.createStatement();
		ResultSet buffer = st.executeQuery("SELECT COUNT(*) FROM "+
				"project_inviteduser WHERE project_id='"+projectID+
				"' AND invitation_confirm='0' AND "+
				"invitation_answered='0';");
		if (buffer.next()) {
			count = buffer.getInt(1);
		} else {
			throw new SQLException("Fehlerhafte Anfrage der reservierten Plätze!");
		}
		return count;
	}
	
	/**
	 * Gibt an, ob die Einladung zu einem bestimmten Projekt von dem
	 * übergebenen Benutzer beantwortet worden ist.
	 * @param projectID Projekt-ID des Projektes
	 * @param user Benutzer
	 * @return true, falls der Benutzer die Einladung schon beantwortet
	 * hat, false sonst
	 * @throws SQLException
	 */
	protected boolean hasAnsweredInvitation(String projectID, String user) throws SQLException {
		boolean b = true;
		dbresult = statement.executeQuery("SELECT invitation_answered FROM "+
				"project_inviteduser WHERE project_id='"+projectID+
				"' AND invited_user='"+user+"';");
		if (!dbresult.next()) {
			b = false;
		} else {
			b = dbresult.getBoolean("invitation_answered");
		}
		connection.commit();	
		return b;
	}

	/**
	 * Fügt dem übergebenen Benutzer einen neuen Buddy hinzu
	 * @param user Benutzer
	 * @param buddy Buddy (Benutzername)
	 * @throws SQLException
	 */
	public void addBuddy(String user, String buddy) throws SQLException {
		statement.executeUpdate("INSERT INTO buddies SET user_nickname="+
				"'"+user+"', buddy_nickname='"+buddy+"';");
		connection.commit();
	}

	/**
	 * Löscht bei dem übergebenen Benutzer den übergebenen Buddy.
	 * @param user Benutzer
	 * @param buddy Buddy
	 * @throws SQLException
	 */
	public void deleteBuddy(String user, String buddy) throws SQLException {
		statement.executeUpdate("DELETE FROM buddies WHERE "+
				"user_nickname='"+user+"' AND buddy_nickname='"+buddy+"';");
		connection.commit();		
	}

	/**
	 * Liefert in einem Vector alle Buddies des übergebenen Benutzers
	 * @param user Benutzer
	 * @return die Buddies des Benutzers in einem Vector
	 * @throws SQLException
	 */
	public Vector getBuddies(String user) throws SQLException {
		Vector<String> buddies = new Vector<String>();
		dbresult = statement.executeQuery("SELECT buddy_nickname FROM "+
				"buddies WHERE user_nickname='"+user+"';");
		while (dbresult.next()) {
			buddies.add(dbresult.getString("buddy_nickname"));
		}
		connection.commit();
		return buddies;
	}
	
	/**
	 * Sucht alle Anwender, die an einer Statusänderung des Users 
	 * interessiert sind. Als Ergebnis wird eine Menge der ClientHash ausgegeben.
	 * Zur Zeit sind nur die Anwender, die den User
	 * als Buddy haben, an der Statusänderung interessiert.
	 * @param user der User
	 * @return Vektor mit allen an der Statusänderung interessierten Anwendern
	 * @throws SQLException
	 */
	public Vector<String> getUsersForStatusChange(String user) throws SQLException {
		Vector<String> users = new Vector<String>();
		dbresult = statement.executeQuery("SELECT user_nickname FROM "+
				"buddies WHERE buddy_nickname='"+user+"';");
		while (dbresult.next()) {
			users.add(dbresult.getString("user_nickname"));
		}
		connection.commit();
		return users;
	}

	/**
	 * Liefert alle Mitglieder zu einem bestimmten Projekt in einem 
	 * Vector.
	 * @param projectID Projekt-ID des betroffenen Projektes
	 * @return die Mitglieder des Projektes in einem Vector
	 * @throws SQLException
	 */
	public Vector getProjectMembers(int projectID) throws SQLException { 
		Vector<String> members = new Vector<String>();
		dbresult = statement.executeQuery("SELECT * FROM "+
				"members WHERE project_id='"+projectID+"';");
		while (dbresult.next()) {
			members.add(dbresult.getString("user_nickname"));
		}
		return members;
	}

	/**
	 * Liefert alle Benutzer, die in der Datenbank stehen, in einem 
	 * Vector
	 * @return alle Benutzer in einem Vector
	 * @throws SQLException
	 */
	public Vector getAllUsers() throws SQLException {
		Vector<String> users = new Vector<String>();
		dbresult = statement.executeQuery("SELECT nickname, LastName, "+
				"firstName, email, user_comment, gender, location FROM "+
				"user;");
		while (dbresult.next()) {
			users.add(dbresult.getString("nickname"));
			users.add(dbresult.getString("LastName"));
			users.add(dbresult.getString("firstName"));
			users.add(dbresult.getString("email"));
			users.add(dbresult.getString("user_comment"));
			users.add(dbresult.getString("gender"));
			users.add(dbresult.getString("location"));
		}
		connection.commit();
		return users;
	}

	/**
	 * Liefert alle Profilinformationen (siehe RFC0815) der übergebenen
	 * Benutzer (Nicknamen der Benutzer) in einem Vector
	 * @param nicknames Array von Nicknamen
	 * @return die Profilinformationen aller Nicknamen als Vector
	 * @throws SQLException
	 */
	public Vector<String> getProfile(String[] nicknames) throws SQLException {
		Vector<String> data = new Vector<String>();
		for (String name : nicknames) {
			dbresult = statement.executeQuery("SELECT firstName, LastName, "+
					"email, lang, nickname, gender, location, user_comment  FROM "+
					"user WHERE nickname='"+name+"';");
			if(dbresult.first()) {
				data.add(dbresult.getString("firstName"));
				data.add(dbresult.getString("LastName"));
				data.add(dbresult.getString("email"));
				data.add(dbresult.getString("nickname"));
				data.add(dbresult.getString("lang"));
				data.add(dbresult.getString("gender"));
				data.add(dbresult.getString("location"));
				data.add(dbresult.getString("user_comment"));
			} else throw new IllegalArgumentException("unknown nickname: " + name);
		}
		connection.commit();
		return data;
	}

	/**
	 * Setzt ein neues Profil bei dem übergebenen Benutzer
	 * @param user Nickname des Benutzers, bei dem das Profil geändert
	 * werden soll
	 * @param name neuer Vorname
	 * @param surname neuer Nachname
	 * @param email neue E-Mailadresse
	 * @param password neues Passwort
	 * @param language neue Sprache
	 * @param gender neues Geschlecht
	 * @param location neuer Wohnort
	 * @param comment neuer Kommentar
	 * @return true, falls der Benutzer in der Datenbank enthalten ist,
	 * false sonst
	 * @throws SQLException
	 */
	public boolean setProfile(String user, String name, String surname, String email, 
			String password, String language, String gender, String location, String comment) throws SQLException {
		dbupdate = statement.executeUpdate("UPDATE user SET" +
				" firstName = '" + name + "'," +
				" LastName = '" + surname + "'," +
				" email = '" + email + "'," +
				" password = '" + password + "'," +
				" lang = '" + language + "'," +
				" gender = '" + gender + "'," +
				" location= '" + location + "'," +
				" user_comment = '" + comment + "'" +
				" WHERE nickname='"+user+"';");
		connection.commit();
		if (dbupdate != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Konvertiert das Datum vom Format YY-MM-DD zum Format DD.MM.YY
	 * @param date das Datum das konvertiert werden soll
	 * @return das konvertierte Datum als String
	 */
	public String convertDate(String date) {
		StringTokenizer token = new StringTokenizer(date, "-");
		String year = token.nextToken();
		String month = token.nextToken();
		String day = token.nextToken();
		return new String(day+"."+month+"."+year);
	}

	/**
	 * Gibt an, ob das Projekt mit der übergebenen Projekt-ID schon voll
	 * ist.
	 * @param projectID Projekt-ID
	 * @return true, falls das Projekt schon voll ist, false sonst
	 * @throws SQLException
	 */
	public boolean isFull(String projectID) throws SQLException {
		int members;
		int max;
		dbresult = statement.executeQuery("SELECT count_members, "+
				"max_members FROM projects WHERE project_id='"+projectID+
				"';");
		if (!dbresult.next()) {
			throw new SQLException("Projekt mit der ID "+projectID+
			"nicht verhanden!");
		} else {
			members = new Integer(dbresult.getString("count_members")).
			intValue();
			max = new Integer(dbresult.getString("max_members")).
			intValue();
		}
		connection.commit();
		return members == max;
	}

	/**
	 * Gibt an, ob der übergebene Benutzer in dem Projekt mit 
	 * der übergebenen Projekt-ID Mitglied ist.
	 * @param user Benutzer (Nickname)
	 * @param projectID Projekt-ID
	 * @return true, falls der Benutzer Mitglied ist, false sonst
	 * @throws SQLException
	 */
	public boolean isMember(String user, String projectID) throws SQLException {
		dbresult = statement.executeQuery("SELECT project_id FROM "+
				"members WHERE project_id='"+projectID+"' AND "+
				"user_nickname='"+user+"';");
		connection.commit();
		return dbresult.next();
	}

	/**
	 * Suche nach Benutzern. Weitere Optimierung ist erwünscht. :-)
	 * 
	 * @author Posemuckel Team
	 * @param query
	 * @return Ein String-Array mit den Profildaten der gefundenen Benutzer.
	 * @throws SQLException 
	 */	
	public String[] searchUsers(String[] query) throws SQLException {
		// Indikator, ob man das WHERE Schlüsselwort
		// voranstellen muss
		boolean whereset = false;

		Vector<String> users = new Vector<String>();
		
		for(int i=0; i<query.length; i++) {
			while ( query[i].contains("*")) {
				query[i] = GetText.macroreplace(query[i],"*","%");
			}
			while ( query[i].contains("?")) {
				query[i] = GetText.macroreplace(query[i],"?","_");
			}
		}
		
		String dbquery = "SELECT firstName, LastName, ";
		dbquery += "email, lang, nickname, gender, location, user_comment ";
		dbquery += "FROM user ";
		if ( !query[0].equals("") && !query[0].equals("%") ) {
				dbquery += "WHERE nickname LIKE '"+query[0]+"' ";
				whereset = true;
		}
		if ( !query[1].equals("") && !query[1].equals("%") ) {
			if(!whereset) {
				dbquery += "WHERE firstName LIKE '"+query[1]+"' ";							
				whereset = true;
			} else {				
				dbquery += "AND firstName LIKE '"+query[1]+"' ";
			}
		}
		if ( !query[2].equals("") && !query[2].equals("%") ) {
			if(!whereset) {
				dbquery += "WHERE LastName LIKE '"+query[2]+"' ";							
				whereset = true;
			} else {		 
				dbquery += "AND LastName LIKE '"+query[2]+"' ";
			}
		}
		if ( !query[3].equals("") && !query[3].equals("%") ) {
			if(!whereset) {
				dbquery += "WHERE lang='"+query[3]+"' ";
				whereset = true;
			} else {		 
				dbquery += "AND lang='"+query[3]+"' ";
			}
		}
		if ( !query[4].equals("") && !query[4].equals("%") ) {
			if(!whereset) {
				dbquery += "WHERE gender='"+query[4]+"' ";
				whereset = true;
			} else {		 
				dbquery += "AND gender='"+query[4]+"' ";
			}
		} 
		if ( !query[5].equals("") && !query[0].equals("%") ) {
			if(!whereset)
				dbquery += "WHERE nickname LIKE '"+query[5]+"' ";
			else
				dbquery += "OR nickname LIKE '"+query[5]+"' ";
			dbquery += "OR firstName LIKE '"+query[5]+"' ";
			dbquery += "OR LastName LIKE '"+query[5]+"' ";
			dbquery += "OR email LIKE '"+query[5]+"' ";
			dbquery += "OR user_comment LIKE '"+query[5]+"' ";
			dbquery += "OR location LIKE '"+query[5]+"' ";
		}
		dbquery += ";";
		System.out.println("Anfrage: "+dbquery);
		dbresult = statement.executeQuery(dbquery);
		
		while (dbresult.next()) {
			users.add(dbresult.getString("nickname"));
			users.add(dbresult.getString("firstName"));
			users.add(dbresult.getString("LastName"));
			users.add(dbresult.getString("email"));
			users.add(dbresult.getString("lang"));
			users.add(dbresult.getString("gender"));
			users.add(dbresult.getString("location"));
			users.add(dbresult.getString("user_comment"));
		}
		
		String[] result = new String[users.size()];
		result = users.toArray(result);
		connection.commit();		
		return result;
	}

	/**
	 * Fügt der Datenbank eine URL hinzu
	 * @param user Benutzer, von dem die URL ist
	 * @param projectID Projekt-ID, des Projektes, in dem der Benutzer
	 * gerade Mitglied ist
	 * @param newURL die aktuelle URL, die vom Benutzer besucht wird
	 * @param newTitle der Titel der aktuellen URL
	 * @param oldURL die zuletzt besuchte URL
	 * @return die URL-ID der eingefügten URL
	 * @throws SQLException
	 */
    public int addURL(String user, String projectID, String newURL, 
    		String newTitle, String oldURL) throws SQLException {
    	//erst prüfen, ob die URL schon vorhanden ist
    	int urlID = 0;
    	newTitle = GetText.escape_sql_write(newTitle);
    	dbresult = statement.executeQuery("SELECT url_id,title FROM url "+
				"WHERE address='"+newURL+"';");
						//" AND "+"title='"+newTitle+"';");
    	if(!dbresult.next()) {
    		//TODO in DEBUG ändern, wenn das Problem behoben ist
    		logger.warn("neue URL " + newURL+ " " + newTitle);
        	statement.executeUpdate("INSERT INTO url SET address='"+newURL+
        			"', title='"+newTitle+"';");
        	urlID = getLatestGeneratedID("url", "url_id");
    	} else {
    		urlID = dbresult.getInt("url_id");
    		String oldTitle = dbresult.getString("title");
    		if(!newTitle.equals(oldTitle)) {
    			logger.debug("Titel ändern " + newTitle + " " + oldTitle);
				statement.executeUpdate("UPDATE url "+
						"SET title='"+newTitle+"' WHERE url_id='"+ urlID +"';");
    		}
    	}
    	String timeStamp = getDatetime();
    	if (oldURL.equals("")) {
    		statement.executeUpdate("INSERT INTO user_urls SET user_nickname='"+
        			user+"', project_id='"+projectID+"', url_timestamp='"+
        			timeStamp+"', url_id='"+urlID+
        			"';");
    	} else {
    		dbresult = statement.executeQuery("SELECT url_id FROM url "+
    				"WHERE address='"+oldURL+"';");
    		if (!dbresult.next()) {
    			throw new SQLException("URL-Eintrag nicht vorhanden!");
    		} else {
    			statement.executeUpdate("INSERT INTO user_urls SET user_nickname='"+
            			user+"', project_id='"+projectID+"', url_timestamp='"+
            			timeStamp+"', referred_by_url='"+dbresult.getString("url_id")+"', url_id='"+urlID+
            			"';");
    		}
    	}
    	connection.commit();
    	return urlID;
    }
    
    /**
     * Fügt einer URL in der Datenbank eine Wählerstimme hinzu
     * @param user Benutzer, der die Stimme abgegeben hat
     * @param projectID Projekt-ID des aktuellen Projektes
     * @param url die betroffene URL
     * @param vote die abgegebene Stimme
     * @throws SQLException
     */
    public void addVote(String user, String projectID, String url, String vote)
    	throws SQLException {
    	String timeStamp = getDatetime();
		dbresult = statement.executeQuery("SELECT url_id FROM url "+
				"WHERE address='"+url+"';");
		if (!dbresult.next()) {
			throw new SQLException("URL-Eintrag nicht vorhanden!");
		} else {
			int urlID = dbresult.getInt("url_id");
			// Schauen, ob es schon einen Eintrag gibt.
			dbresult = statement.executeQuery("SELECT url_id,project_id,user_nickname FROM ratings "+
					"WHERE url_id='"+urlID+"' AND project_id='"+projectID+"' AND user_nickname='"+user+"';");
			if (dbresult.next()) {
				// Wenn ja, UPDATE
				statement.executeUpdate("UPDATE ratings "+
						"SET rating='"+vote+"', rating_timestamp='"+timeStamp+"' "+
						"WHERE project_id='"+projectID+"' AND url_id='"+urlID+"' "+
						"AND user_nickname='"+user+"';");
			} else {
				// sonst INSERT
				statement.executeUpdate("INSERT INTO ratings SET user_nickname='"+
	        			user+"', project_id='"+projectID+"',rating='" + vote + "', rating_timestamp='"+
	        			timeStamp+"', url_id='"+urlID+"';");
			}
		}
		connection.commit();
    }
    
    /**
     * Löscht eine URL aus der Datenbank
     * @param urlID die URL-ID der URL, die gelöscht werden soll
     * @throws SQLException
     */
    public void deleteURL(String urlID) throws SQLException {
    	statement.executeUpdate("DELETE FROM user_urls WHERE url_id='"+
    			urlID+"';");
    	statement.executeUpdate("DELETE FROM url WHERE url_id='"+
    			urlID+"';");
    	statement.executeUpdate("UPDATE user_urls SET referred_by_url="+
    			"NULL WHERE referred_by_url='"+urlID+"';");
    	connection.commit();
    }

    
    /**
     * Liefert zu einem Vector von Benutzern deren Daten
     * aus der DB.
     * @param users Vector von Benutzernamen.
     * @return Einen Vector von ClientInfos der betreffenden Benutzer.
     * @throws SQLException
     */
	public Vector<ClientInfo> getClients(Vector<String> users) throws SQLException {
		Vector<ClientInfo> result = new Vector<ClientInfo>();
		for ( String username : users ) {
			ClientInfo user = new ClientInfo(null,username);
			getClientData(user);
			result.add(user);
		}
		return result;
	}

	/**
	 * Liefert die Daten des Benutzers aus der DB, soweit
	 * diese in einem ClientInfo Objekt gespeichert werden
	 * können. Es wird ein ClientInfo übergeben und vorausgesetzt,
	 * dass darin zumindest schonmal der Benutzername gespeichert
	 * ist. Anhand des Benutzernamens werden dann die Daten aus
	 * der DB geholt und in diesem Objekt gespeichert.
	 * 
	 * @param user Ein ClientInfo-Objekt.
	 * @throws SQLException
	 */
	public void getClientData(ClientInfo user) throws SQLException {
		dbresult = statement.executeQuery("SELECT firstName,LastName,email,lang "+
				"FROM user WHERE nickname='"+user.getUserName()+"';");
		if (!dbresult.next()) {
			throw new SQLException("Benutzer mit Benutzername "+user.getUserName()+
			"nicht verhanden!");
		} else {
			user.setName(dbresult.getString("firstName"));
			user.setSurname(dbresult.getString("LastName"));
			user.seteMail(dbresult.getString("email"));
			String lang = dbresult.getString("lang");
			if( lang == null ) lang = "EN";
			user.setLanguage(lang);
			//user.setGender(dbresult.getString("gender"));
			//user.setLocation(dbresult.getString("location"));
			//user.setComment(dbresult.getString("user_comment"));
		}
		connection.commit();
	}

	/**
	 * Liefert zu einem bestimmten Projekt den Verlauf der Webseiten, die
	 * in dem Projekt besucht wurden (nach RFC 0815).
	 * @param projectID Projekt-ID des Projektes, zu der der Webseiten-
	 * Verlauf geliefert werden soll
	 * @return den Verlauf der Webseiten in einem Vector
	 * @throws SQLException
	 */
	public Vector getWebtrace(String projectID) throws SQLException {
		Vector<String> webtrace = new Vector<String>();
		ResultSet buffer;
		dbresult = statement.executeQuery("SELECT address, referred_by_url, "+
				"title, user_nickname, user_urls.url_id FROM user_urls NATURAL JOIN "+
				"url WHERE user_urls.project_id='"+projectID+"';");
		while (dbresult.next()) {
			webtrace.add(dbresult.getString("address"));
			int parent = dbresult.getInt("referred_by_url");
			if (parent == 0) {
				webtrace.add("");
			} else {
				buffer = connection.createStatement().executeQuery(
						"SELECT address FROM url WHERE url_id='"+parent+
						"';");
				if (buffer.next()) {
					webtrace.add(buffer.getString("address"));
				} else {
					throw new SQLException("URL nicht vorhanden!");
				}			
			}
			webtrace.add(dbresult.getString("title"));
			String nickname =dbresult.getString("user_nickname"); 
			webtrace.add(nickname);
			int urlID = dbresult.getInt("user_urls.url_id");
			//der Anwender muss in den WHERE-Teil hinein-
			buffer = connection.createStatement().executeQuery(
					"SELECT rating, rating_notes FROM ratings WHERE "+
					"project_id='"+projectID+"' AND user_nickname='"+nickname+
					"' AND url_id='"+urlID+"';");
			if (buffer.next()) {
				webtrace.add(String.valueOf(buffer.getInt("rating")));
				String rating_notes = buffer.getString("rating_notes");
				if ((rating_notes != null)&&(!rating_notes.equals(""))) {
					webtrace.add(String.valueOf(1));
				} else {
					webtrace.add(String.valueOf(0));
				}
			} else {
				webtrace.add(String.valueOf(-1));
				webtrace.add(String.valueOf(0));
			}
		}
		connection.commit();
		return webtrace;
	}

	/**
	 * Fügt eine Bewertung mit Bewertungsnotiz zu einer URL in der
	 * Datenbank hinzu.
	 * @param user Benutzer der die Notiz und die Bewertung abgegeben
	 * hat
	 * @param projectID Projekt-ID des aktuellen Projektes
	 * @param url URL, an die die Notiz und Bewertung angehängt wird
	 * @param rating Bewertung
	 * @param ratingNote Bewertungsnotiz
	 * @return gibt die ID der neu eingefügten Bewertung zurück; falls
	 * es schon eine Bewertung gab, wird 0 zurückgegeben
	 * @throws SQLException
	 */
	public int addNote(String user, String projectID, String url, 
			String rating, String ratingNote) throws SQLException {
		int id = 0;
		String timeStamp = getDatetime();
		// Erstmal die ID zur URL holen
		dbresult = statement.executeQuery("SELECT url_id FROM url "+
				"WHERE address='"+url+"';");
		if (dbresult.next()) {
			int urlID = dbresult.getInt("url_id");
			// Schauen, ob es schon einen Eintrag gibt.
			dbresult = statement.executeQuery("SELECT url_id,project_id,user_nickname FROM ratings "+
					"WHERE url_id='"+urlID+"' AND project_id='"+projectID+"' AND user_nickname='"+user+"';");
			if (dbresult.next()) {
				// Wenn ja, UPDATE
				System.out.println("Ich muss ein UPDATE machen!");
				statement.executeUpdate("UPDATE ratings "+
						"SET rating_notes='"+ratingNote+"', rating='"+rating+"', "+
						"rating_timestamp='"+timeStamp+"' WHERE project_id='"+projectID+"' AND url_id='"+urlID+"' "+
						"AND user_nickname='"+user+"';");
			} else {
				// sonst INSERT
				statement.executeUpdate("INSERT INTO ratings SET "+
						"project_id='"+projectID+"', user_nickname='"+user+
						"', rating='"+rating+"', rating_notes='"+ratingNote+
						"', url_id='"+urlID+"', "+
						"rating_timestamp='"+timeStamp+"';");
				id = getLatestGeneratedID("ratings", "ratings_id");	
			}
		} else {
			throw new SQLException("URL ist nicht in der DB enthalten!");
		}
		connection.commit();
		return id;
	}
	
	/**
	 * Löscht eine Bewertung aus der Datenbank
	 * @param id ID der Bewertung, die gelöscht werden soll
	 * @throws SQLException
	 */
	public void deleteNote(String id) throws SQLException {
		statement.executeUpdate("DELETE FROM ratings WHERE ratings_id='"+
				id+"';");
		connection.commit();
	}

	/**
	 * Liefert alle Bewertungsnotizen zu einer bestimmten URL
	 * @param url URL, zu der die Bewertungsnotizen ausgegeben werden 
	 * sollen
	 * @return Vector, der die Bewertungsnotizen enthält
	 * @throws SQLException
	 */
	public Vector<String> getNotes(String url) throws SQLException {
		Vector<String> notes = new Vector<String>();
	    dbresult = statement.executeQuery("SELECT user_nickname, "+
	    		"rating_notes FROM ratings WHERE url_id = (SELECT "+
	    		"url_id FROM url WHERE address='"+url+"');");
	    while (dbresult.next()) {
	    	String note = dbresult.getString("rating_notes");
	    	if ((note != null)&&(!note.equals(""))) {
	    		notes.add(dbresult.getString("user_nickname"));
	    		notes.add(note);
	    	}
	    }
	    connection.commit();
		return notes;
	}

	/**
	 * Fügt einen neuen Ordner in der Datenbank ein
	 * @param projectID Projekt-ID des Projektes, in dem der Ordner an-
	 * gelegt wird
	 * @param name Name des Ordners
	 * @param parentFolderID ID des Elternordners
	 * @return true, falls der Elternordner schon gelöscht wurde, false
	 * sonst
	 * @throws SQLException
	 */
	public boolean addFolder(String projectID, String name, 
			String parentFolderID) throws SQLException {
		boolean folderAlreadyDeleted = false;
		if (!parentFolderID.equals("")) {
			dbresult = statement.executeQuery("SELECT * FROM folders "+
					"WHERE folder_id='"+parentFolderID+"';");
			if (dbresult.next()) {
				statement.executeUpdate("INSERT INTO folders SET "+
						"project_id='"+projectID+"', name='"+name+
						"', parent_folder='"+parentFolderID+"', "+
						"unsorted_folder='0';");
			} else {
				folderAlreadyDeleted = true;
			}
		} else {
			//TODO parentFolder auf 0 setzen
			statement.executeUpdate("INSERT INTO folders SET "+
					"project_id='"+projectID+"', name='"+name+
					"', parent_folder='"+0+ "', unsorted_folder='0';");
		}
		latestFolderID = getLatestGeneratedID("folders", "folder_id");
		connection.commit();
		return folderAlreadyDeleted;
	}

	/**
	 * Löscht einen Ordner aus der Datenbank
	 * @param folderID Ordner-ID des Ordners, der gelöscht werden soll
	 * @param withCommit gibt an, ob die Methode als Transaktion gesehen
	 * werden soll
	 * @return true, falls der Ordner schon gelöscht wurde, false sonst
	 * @throws SQLException
	 */
	public boolean  deleteFolder(String folderID, boolean withCommit) throws SQLException {
		boolean folderAlreadyDeleted = false;
		statement.executeUpdate("DELETE FROM folder_urls WHERE folder_id='"+folderID+
				"';");
		dbupdate = statement.executeUpdate("DELETE FROM folders WHERE "+
				"folder_id='"+folderID+"';");
		if (dbupdate != 0) {
			dbresult = statement.executeQuery("SELECT folder_id FROM "+
					"folders WHERE parent_folder='"+folderID+"';");
		    deleteFolder(dbresult);
		} else {
			folderAlreadyDeleted = true;
		}
		if (withCommit) {
			connection.commit();
		}
		return folderAlreadyDeleted;
	}
	
	protected void deleteTable(String name) throws SQLException {
		statement.executeUpdate("DELETE FROM "+ name
				+";");
	}

	/**
	 * Hilfsmethode, die alle Ordner löscht, die in einem gegebeben
	 * ResultSet enthalten sind.
	 * @param children ResultSet, das die Ordner enthält, die gelöscht 
	 * werden sollen
	 * @throws SQLException
	 */
	private void deleteFolder(ResultSet children) throws SQLException {
		Statement st = connection.createStatement();
		while (children.next()) {
			int id = children.getInt("folder_id");
			st.executeUpdate("DELETE FROM folder_urls WHERE folder_id='"+id+
			"';");
			st.executeUpdate("DELETE FROM folders WHERE folder_id='"+id+
					"';");
			dbresult = st.executeQuery("SELECT folder_id FROM "+
					"folders WHERE parent_folder='"+id+"';");
			deleteFolder(dbresult);		
		}
	}

	/**
	 * Verschiebt einen Ordner, so dass er in der Datenbank einen anderen
	 * Elternordner erhält.
	 * @param folderID ID des Ordners, der verschoben werden soll
	 * @param newParentFolderID ID des Ordners, der der neue Elternordner
	 * wird
	 * @return true, falls der neue Elternordner schon gelöscht wurde,
	 * false sonst
	 * @throws SQLException
	 */
	public boolean moveFolder(String folderID, String newParentFolderID) throws SQLException {
		boolean folderAlreadyDeleted = false;
		if (!newParentFolderID.equals("0")) {
			dbresult = statement.executeQuery("SELECT * FROM folders "+
					"WHERE folder_id='"+newParentFolderID+"';");
			if (dbresult.next()) {
				statement.executeUpdate("UPDATE folders SET "+
						"parent_folder='"+newParentFolderID+
						"' WHERE folder_id='"+folderID+"';");
			} else {
				deleteFolder(folderID, false);
				folderAlreadyDeleted = true;
			}
		} else {
			//TODO auf 0 umstellen
			statement.executeUpdate("UPDATE folders SET "+
					"parent_folder='0' WHERE folder_id='"+folderID+"';");
		}	
		connection.commit();
		return folderAlreadyDeleted;
	}
	
	/**
	 * Liefert die zuletzt generierte Ordner-ID
	 * @return die zuletzt generierte Ordner-ID
	 */
	public int getLatestFolderID() {
		return latestFolderID;
	}

	/**
	 * Ändert den Ordner, in der eine URL gespeichert ist
	 * @param address Adresse der URL, deren Ordner geändert werden soll
	 * @param folderID Ordner-ID des Ordners in der die URL eingefügt
	 * werden soll
	 * @return true, falls der Ordner schon gelöscht wurde, false sonst
	 * @throws SQLException
	 */
	public boolean changeParentfolder(String address, String folderID) throws SQLException {
		boolean folderAlreadyDeleted = false;
		dbresult = statement.executeQuery("SELECT url_id FROM url WHERE address='"+
				address+"';");
		if (dbresult.next()) {
			int urlID = dbresult.getInt("url_id");
			if (folderID.equals("")) {
				// Wenn kein Ordner angegeben wird, wird die URL gelöscht
				statement.executeUpdate("DELETE FROM folder_urls WHERE url_id='"+urlID+"';");
			} else {
				dbresult = statement.executeQuery("SELECT * FROM folders WHERE "+
						"folder_id='"+folderID+"';");
				if (dbresult.next()) {
					dbupdate = statement.executeUpdate("UPDATE folder_urls SET folder_id='"+
							folderID+"' WHERE url_id='"+urlID+"';");
					if (dbupdate == 0) {
						statement.executeUpdate("INSERT INTO folder_urls SET folder_id='"+
								folderID+"', url_id='"+urlID+"';");
					}
				} else {
					folderAlreadyDeleted = true;
				}		
			}
		} else {
			throw new SQLException("URL mit der Adresse "+address+" ist nicht vorhanden!");
		}
		connection.commit();
		return folderAlreadyDeleted;
	}

	/**
	 * Liefert zu einem gegebenen Projekt die Ordner-und URL-Informationen
	 * (siehe RFC0815 GET_FOLDERSYSTEM)
	 * @param projectID ID des Projektes, zu der die Informationen
	 * geliefert werden sollen
	 * @return Vector, der die Ordner- und URL-Informationen enthält
	 * @throws SQLException
	 */
	public Vector<String> getFoldersWithURLs(String projectID) throws SQLException {
		Vector<String> foldersWithURLs = new Vector<String>();
		foldersWithURLs.add("");//Platzhalter für die Länge
		foldersWithURLs.add("");
		Vector<String> couples = new Vector<String>();
		ResultSet buffer;
		dbresult = statement.executeQuery("SELECT folder_id, name, parent_folder "+
				"FROM folders WHERE project_id='"+projectID+"';");
		while (dbresult.next()) {
			int folderID = dbresult.getInt("folder_id");
			foldersWithURLs.add(folderID+"");
			foldersWithURLs.add(dbresult.getString("name"));
			foldersWithURLs.add(dbresult.getInt("parent_folder")+"");
			buffer = connection.createStatement().executeQuery("SELECT address FROM "+
					"folder_urls NATURAL JOIN url WHERE folder_id='"+folderID+"';");
			while (buffer.next()) {
				couples.add(folderID+"");
				couples.add(buffer.getString("address")+"");
			}
		}
		int foldersSize = foldersWithURLs.size()-2;
		int couplesSize = couples.size();
		foldersWithURLs.set(1, couplesSize+"");
		foldersWithURLs.set(0, foldersSize+"");
		foldersWithURLs.addAll(couples);
		return foldersWithURLs;
	}

	
	public String getFolder(String folderID) throws SQLException {
		String name = null;
		dbresult = statement.executeQuery("SELECT name FROM "+
				"folders WHERE folder_id='"+folderID+"';");
		if (dbresult.next()) {
			name = dbresult.getString("name");
		}
		connection.commit();
		return name;
	}

	public void log(String logText, String projectID) throws SQLException {
		statement.executeUpdate("INSERT INTO log SET project_id='"+projectID+
				"', text='"+logText+"', timestamp='"+getDatetime()+"';");
		connection.commit();
	}
}
