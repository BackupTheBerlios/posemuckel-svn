package posemuckel.client.net;

import java.util.Vector;

import posemuckel.common.Message;

/**
 * Implementiert die vom Client erzeugten Nachrichten nach RFC 0815.
 * 
 * Diese Klasse dient ausschließlich dem Erzeugen von Nachrichten. Formatiert werden
 * diese dann noch in der Klasse posemuckel.common.Message. 
 * Die Nachricht wird hier direkt in die Warteschlange eingereiht, die in Form
 * eines Vectors dem Konstruktor übergeben werden muss. Das Client-Objekt als Konstruktor-
 * Parameter wird benötigt, um zum Beispiel den Hash-Wert des Client senden zu können.
 * 
 * @author Posemuckel Team
 * @see posemuckel.common.Message
 *
 */
// TODO TestCase schreiben!
public class ClientMessage {
	private Vector<String> sendqueue;
	
	/**
	 * Erzeugt ein Objekt diese Klasse. :-)
	 * @param sendqueue ist die Warteschlange, in die die zu sendenden
	 * Nachricht reingeschrieben werden.
	 */
	public ClientMessage(Vector<String> sendqueue) {
		this.sendqueue = sendqueue;
	}


	
	
	/**
	 * Dies ist die richtige Schnittstelle, um eine Registrierung
	 * durchzuführen.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param name Der Vorname des Benutzers
	 * @param surname Der Nachname des Benutzers
	 * @param email Die eMail Adresse des Benutzers
	 * @param nickname Der vom Bennutzer gewünschte Spitzname.
	 * @param password Das Passwort.
	 * @param language Die Sprache des Benutzers, angegeben in der zweibuchstabigen
	 * 			Sprachkennung, also de für deutsch, en für englisch.
	 * @param gender Das Geschlecht des Benutzers, möglich sind MALE oder FEMALE.
	 * @param location Der Wohnort des Benutzers, sofern angegeben.
	 * @param comments Kommentare des Benutzers, sofern angegeben. 
	 */
	  public void register(String id, String name,
			  				 String surname,
			  				 String email,
			  				 String nickname,
			  				 String password,
			  				 String language,
			  				 String gender,
			  				 String location,
			  				 String comments) {
		  //@author Posemuckel Team
		  String[] regData = new String[9];
		  regData[0] = name;
		  regData[1] = surname;
		  regData[2] = email;
		  regData[3] = nickname;
		  regData[4] = password;
		  regData[5] = language;
		  regData[6] = gender;
		  if ( location != null )
			  regData[7] = location;
		  else
			  regData[7] = "";
		  if ( comments != null )
			  regData[8] = comments;
		  else
			  regData[8] = "";
		  
		  String message = Message.format("", id, "REGISTER", regData);
	      add2sendqueue(message);
	  }
	  
	  /**
	   * Ruft die Profile der angegebenen Anwender ab.
	   * @param id die Clientweit eindeutige ID der Nachricht
	   * @param nicknames die Benutzernamen der Anwender
	   */
	  public void profile(String id, String[] nicknames) {			  
		  String message = Message.format(Client.getClientHash(), id, "GET_PROFILE", nicknames);
		  add2sendqueue(message);
	  }
	  
	  /**
	   * Dies ist die richtige Schnittstelle, um einen Login
	   * durchzuführen.
	   * 
	   * @param id die Clientweit eindeutige ID der Nachricht
	   * @param nickname Der Spitzname des Benutzers.
	   * @param password Das Passwort des Benutzers.
	   */
	  public void login(String id, String nickname, String password) {
		  //@author Posemuckel Team
		  String[] loginData = { nickname, password };
	      String message = Message.format("", id, "LOGIN", loginData);
	      add2sendqueue(message);
	  } 
	/**
	 * Einen neuen Buddy der aktuellen Buddy-Liste hinzufügen.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param buddy Der Spitzname des anderen Benutzers.
	 */	    
	  protected void addBuddy(String id, String buddy) {
		  String[] data = new String[1];
		  data[0] = buddy;
	      String message = Message.format(Client.getClientHash(), id, "ADD_BUDDY", data);
	      add2sendqueue(message);
	  }
	  
	/**
	 * Dies ist die richtige Schnittstelle, um einen Buddy aus der eigenen Liste
	 * zu entfernen.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht 
	 * @param buddy Der Buddy, der aus der Liste gel&ouml;scht werden soll
	 */
	  protected void delBuddy(String id, String buddy) {
		  String[] data = new String[1];
		  data[0] = buddy;
	      String message = Message.format(Client.getClientHash(), id, "DEL_BUDDY", data);
	      add2sendqueue(message);
	  }
	  	
	  /**
	   * Ruft das Foldersystem vom Server ab.
	   * @param id die Clientweit eindeutige ID der Nachricht
	   */
		protected void getFoldersystem(String id) {
		      String message = Message.format(Client.getClientHash(), id, "GET_FOLDERSYSTEM", null);
		      add2sendqueue(message);
		}

	/**
	 * Anfrage nach den aktuellen Buddies des Benutzers. Hier gibt es keinerlei
	 * Parameter, da der Client-Hash den Client eindeutig identifiziert.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 */
	  protected void myBuddies(String id) {
	      String message = Message.format(Client.getClientHash(), id, "MY_BUDDIES", null);
	      add2sendqueue(message);
	  }
	  
	/**
	 * Startet ein neues Projekt.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param topic Das Thema, worum es hier geht.
	 * @param is_public ...ist true, wenn das Projekt öffentlich ist.
	 * @param max_users Die maximale Anzahl von Benutzern, die man zulassen will.
	 * @param users_to_invite Ein Array mit Spitznamen von Benutzern, die umgehend per eMail eingeladen werden sollen.
	 */
	 protected void startProject(String id, String topic, String description, boolean is_public, String max_users, String[] users_to_invite) {
		  int len = 4 + users_to_invite.length;
		  String[] projData = new String[len];
		  projData[0] = topic;
		  if (is_public)
			  projData[1] = "1";
		  else
			  projData[1] = "0";
		  projData[2] = max_users;
		  projData[3] = description;
		  int cnt = 0;
		  for ( String arg : users_to_invite ) {
			  projData[4+cnt] = arg;
			  cnt++;
		  }	  
		  String message = Message.format(Client.getClientHash(), id, "START_PROJECT", projData);
	      add2sendqueue(message);
	  }

	/**
	 * Mit dieser Methode tritt der Client einem Projekt bei.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param project_id das Projekt, dem der Nutzer beitreten m&ouml;chte
	 */
	  protected void joinProject(String id, String project_id) {
		  //die String-ID ist für das Model durchaus geeignet, solange der Server dafür sorgt, 
		  //das die ID eindeutig ist
		  String[] data = new String[1];
		  data[0] = project_id;
	      String message = Message.format(Client.getClientHash(), id, "JOIN_PROJECT", data);
	      add2sendqueue(message);
	  }

	/**
	 * Mit dieser Methode verlässt der Client das angegebene Projekt.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param project_id das Projekt, das der Nutzer verlassen m&ouml;chte
	 */
	  protected void leaveProject(String id, String project_id) {
		  //die String-ID ist für das Model durchaus geeignet, solange der Server dafür sorgt, 
		  //das die ID eindeutig ist
		  String[] data = new String[1];
		  data[0] = project_id;
	      String message = Message.format(Client.getClientHash(), id, "LEAVE_PROJECT", data);
	      add2sendqueue(message);
	  }

    /**
     * Anfrage nach den aktuellen Projekten des Benutzers. Hier gibt es keinerlei
     * Parameter, da der Client-Hash den Client eindeutig identifiziert.
     * 
	 * @param id die Clientweit eindeutige ID der Nachricht
     */
	  protected void myProjects(String id) {
		  String message = Message.format(Client.getClientHash(), id, "MY_PROJECTS", null);
		  add2sendqueue(message);
	  }
	  
	/**
	 * Senden einer Chat-Nachricht.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param chat_id Die ID des Chats.
	 * @param message die Chat-Botschaft, die an die Gruppe gehen soll.
	 */
	  public void chat(String id, String chat_id, String message) {
		  //die String-ID ist für das Model durchaus geeignet, solange der Server dafür sorgt, 
		  //das die ID eindeutig ist
		  String[] data = new String[2];
		  data[0] = chat_id;
		  data[1] = message;
	      String msg = Message.format(Client.getClientHash(), id, "CHAT", data);
	      add2sendqueue(msg);
	  }
	  
     /**
      * Mit dieser Methode wird ein neuer Chat gestartet.
      * 
 	  * @param id die Clientweit eindeutige ID der Nachricht
      * @param is_public boolescher Wert, ob der Chat "öffentlich" sein soll.
      * @param users_to_invite String-Array mit den einzuladenden Benutzern.
      */
	  public void startChat(String id, boolean is_public, String[] users_to_invite) {
		  int len = 1 + users_to_invite.length;
		  String[] chatData = new String[len];
		  if (!is_public) {
			  chatData[0] = "0";
			  int cnt = 0;
			  for ( String arg : users_to_invite ) {
				  chatData[1+cnt] = arg;
				  cnt++;
			  }	  		  
		  } else {
			  chatData[0] = "1";
		  }
	      String message = Message.format(Client.getClientHash(), id, "START_CHAT", chatData);
	      add2sendqueue(message);
	  }

	  /**
	   * Diese Methode verschickt eine Nachricht, um die Mitglieder eines
	   * Chats anzufordern.
	   * 
	   * @param msgid Nachrichten-ID
	   * @param chatid Chat-ID
	   */
	  public void getChatMembers(String msgid, String chatid) {
		  String[] data = { chatid };
		  String message = Message.format(Client.getClientHash(), msgid, "GET_CHAT_MEMBERS", data);
		  add2sendqueue(message);	
	  }
	  
	/**
	 * Verschickt eine Nachricht, um an einem Chat teilzunehmen, zu dem man eingeladen wurde.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param chat_id Die ID des Chats.
	 */
	  protected void joinChat(String id, String chat_id) {
		  String[] data = { chat_id };		  
		  String message = Message.format(Client.getClientHash(), id, "JOIN_CHAT", data);
		  add2sendqueue(message);
	  }
  
	  
	/**
	 * Benachrichtigt die anderen Benutzer des Chats über die Tatsache, dass dieser Benutzer
	 * gerade schreibt. Hier gibt es nur die Chat-ID als Parameter, da der Client-Hash den Client
	 * eindeutig identifiziert.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param chat_id Die ID des Chats.
	 */
	  public void typing(String id, String chat_id) {
		  //die String-ID ist für das Model durchaus geeignet, solange der Server dafür sorgt, 
		  //das die ID eindeutig ist
		  String[] data = { chat_id };		  
		  String message = Message.format(Client.getClientHash(), id, "TYPING", data);
	      add2sendqueue(message);
	  }
	  
	    /**
	     * Fordert den gesamten Webtrace vom Server an. 
	     * @param id der Nachricht
	     */
	  	protected void getWebtrace(String id) {
			String message = Message.format(Client.getClientHash(), id, "GET_WEBTRACE", null);
			add2sendqueue(message);
		}



	/**
	 * Benachrichtigt die anderen Benutzer des Chats über die Tatsache, dass dieser Benutzer
	 * eine gewisse Zeit lang nicht mehr in die Tasten gehauen hat. Hier gibt es nur die Chat-ID
	 * als Parameter, da der Client-Hash den Client eindeutig identifiziert.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param chat_id Die ID des Chats.
	 */
	  public void reading(String id, String chat_id) {
		  //die String-ID ist für das Model durchaus geeignet, solange der Server dafür sorgt, 
		  //das die ID eindeutig ist
		  String[] data = { chat_id };		  
		  String message = Message.format(Client.getClientHash(), id, "READING", data);
	      add2sendqueue(message);
	  }
	  
	/**
	 * Verschickt eine Nachricht, um den Benutzer abzumelden.
	 * 
	 * @param id  die Clientweit eindeutige ID der Nachricht
	 */
	protected void logout(String id) {
		String message = Message.format(Client.getClientHash(), id, "LOGOUT",
				null);
		add2sendqueue(message);
	}

	/**
	 * Verschickt eine Nachricht, um nach allen Projekten zu fragen.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 */
	protected void getProjects(String id) {
		String message = Message.format(Client.getClientHash(), id,
				"GET_PROJECTS", null);
		add2sendqueue(message);
	}
	
	/**
	 * Verschickt eine Nachricht, um nach allen offenen Einladungen zu
	 * fragen.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 */
	protected void getInvitations(String id) {
		String message = Message.format(Client.getClientHash(), id,
				"GET_INVITATIONS", null);
		add2sendqueue(message);
	}

	
	/**
	 * Verschickt eine Nachricht, um den Server nach Benutzern suchen
	 * zu lassen.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param username Benutzername
	 * @param name Vorname
	 * @param surname Nachname
	 * @param language Sprache
	 * @param gender Geschlecht
	 * @param freetext Freitext - wird in allen Feldern gesucht
	 */
	protected void searchUsers(String id, String username, String name, String surname, String language, String gender, String freetext) {
		String[] data = { username,
						  name,
						  surname,
						  language,
						  gender,
						  freetext };		  
		String message = Message.format(Client.getClientHash(), id,
				"SEARCH_USERS", data);
		add2sendqueue(message);
	}
	
	/**
	 * Verschickt eine Nachricht, um nach den Mitgliedern eines Projektes
	 * zu fragen.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param projectId die eindeutige ID des Projektes
	 */
	protected void getProjectMembers(String id, String projectId) {
		String[] data = { projectId };		  
		String message = Message.format(Client.getClientHash(), id,
				"GET_ACTIVE_USERS", data);
		add2sendqueue(message);
	}
	
	/**
	 * Verschickt eine Nachricht, um das &Ouml;ffnen eines Projektes 
	 * zu veranlassen.
	 * 
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param projectId die eindeutige ID des Projektes
	 */
	public void openProject(String id, String projectId) {
		String[] data = { projectId };		  
		String message = Message.format(Client.getClientHash(), id,
				"OPEN_PROJECT", data);
		add2sendqueue(message);
	}
	
	/**
	 * Lehnt die Teilnahme an einem Projekt ab.
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param projectID die eindeutige ID des Projektes
	 */
	public void rejectInvitation(String id, String projectID) {
		String[] data = { projectID };		  
		String message = Message.format(Client.getClientHash(), id,
				"DONT_ACCEPT_PROJECT", data);
		add2sendqueue(message);
	}


	  /**
		 * Verschickt eine SET_PROFILE Nachricht, um das Profil des Benutzers zu
		 * ändern.
		 * 
		 * @param id
		 * @param firstname
		 * @param surname
		 * @param pwd
		 * @param email
		 * @param lang
		 * @param gender
		 * @param location
		 * @param comment
		 */  
	  public void setProfile(String id, String firstname, String surname, String pwd, String email, String lang, String gender, String location, String comment ) {
		  String[] data = { firstname,
				  			surname,
				  			email,
				  			pwd,
				  			lang,
				  			gender,
				  			location,
				  			comment };
		  String message = Message.format(Client.getClientHash(), id, "SET_PROFILE", data);
		  add2sendqueue(message);
	  }
	  
		/**
		 * Verschickt eine VISITING-Nachricht nach Definition im RFC 0815
		 * @param id ID der Nachricht
		 * @param newurl Die neue URL
	 	 * @param newtitle Der Titel der neuen URL
		 * @param oldurl Die alte URL, von der die neue besucht wurde
		 */
		public void visiting(String id, String newurl, String newtitle, String oldurl) {
			String[] data = { newurl, newtitle, oldurl };
			String message = Message.format(Client.getClientHash(), id, "VISITING", data);
			add2sendqueue(message);
		}
		
		/**
		 * Fügt einen neuen Folder in das Foldersystem des geöffneten Projektes
		 * ein.
		 * @param id die Clientweit eindeutige ID der Nachricht
		 * @param title des Folders
		 * @param parentID ID des ParentFolders
		 */
		public void addFolder(String id, String title, String parentID) {
			String[] data = { title, parentID };
			String message = Message.format(Client.getClientHash(), id, "NEW_FOLDER", data);
			add2sendqueue(message);
		}
		
		/**
		 * Ändert den ParentFolder eines Folders.
		 * @param id die Clientweit eindeutige ID der Nachricht
		 * @param folderID ID des Folders
		 * @param parentID ID des neuen ParentFolders
		 */
		public void changeParent(String id, String folderID, String parentID) {
			String[] data = { folderID, parentID };
			String message = Message.format(Client.getClientHash(), id, "MOVE_FOLDER", data);
			add2sendqueue(message);
		}
		
		/**
		 * Löscht einen Folder aus dem Foldersystem des geöffneten Projektes.
		 * @param id die Clientweit eindeutige ID der Nachricht
		 * @param folderID ID des zu löschenden Folders
		 */
		public void deleteFolder(String id, String folderID) {
			String[] data = { folderID };
			String message = Message.format(Client.getClientHash(), id, "DELETE_FOLDER", data);
			add2sendqueue(message);
		}
		
		/**
		 * Ändert die ID des ParentFolders für die URL. Für die Sonderfälle
		 * siehe RFC 0815
		 * @param id die Clientweit eindeutige ID der Nachricht
		 * @param url 
		 * @param parentID ID des ParentFolders
		 */
		protected void changeParentFolderForURL(String id, String url, String parentID) {
			String[] data = {url, parentID};
			String message = Message.format(Client.getClientHash(), id, "PARENTFOLDER_CHANGED", data);
			add2sendqueue(message);
		}
		
		/**
		 * Verschickt eine VIEWING-Nachricht nach Definition im RFC 0815
		 * @param id ID der Nachricht
		 * @param url Die besuchte URL
		 */
		public void viewing(String url, String id) {
			String message = Message.format(Client.getClientHash(), id, 
					"VIEWING", new String[]{url});
			add2sendqueue(message);
		}
		
		/**
		 * Verschickt eine GET_NOTES-Nachricht nach Definition im RFC 0815
		 * @param id ID der Nachricht
		 * @param url Die URL, deren Notizen gefordert sind
		 */
		public void getNotes(String id, String url) {
			String message = Message.format(Client.getClientHash(), id, 
					"GET_NOTES", new String[]{url});
			add2sendqueue(message);
		}
		
		/**
		 * Verschickt eine ADD_NOTE-Nachricht nach Definition im RFC 0815
		 * @param id ID der Nachricht
		 * @param url Die URL, zu der Notizen erstellt wurden
		 * @param data das Rating und der Text, durch \r\n getrennt
		 */
		public void addNote(String id, String url, String data) {
			String message = Message.format(Client.getClientHash(), id, 
					"ADD_NOTE", new String[]{url, data});
			add2sendqueue(message);
		}
		
		/**
		 * Verschickt eine VOTING-Nachricht nach Definition im RFC 0815
		 * @param id die Clientweit eindeutige ID der Nachricht
		 * @param url die bewertete URL
		 * @param rating die abgegebene Bewertung
		 */
		public void vote(String id, String url, String rating) {
			String[] data = new String[]{url, rating};
			String message = Message.format(Client.getClientHash(), id, "VOTING", data);
			//System.out.println("I add vote to sendqueue");
			add2sendqueue(message);
		}
		  
		/**
		 * Verschickt eine NOTIFY-Nachricht nach Definition im RFC 0815
		 * url Die neue URL
	 	 * title Der Titel der URL
		 * pic_data_length L&auml;nge der Bilddaten
		 * pic das Bild codiert in Base64
		 * users die Nutzer, die die Nachricht empfangen sollen
		 * @param id ID der Nachricht
		 * @param data die Daten für die Nachricht
		 */
		public void notify(String id, String[] data) {
			//TODO die Daten in einer Liste aufzählen
			String message = Message.format(Client.getClientHash(), id, "NOTIFY", data);
			add2sendqueue(message);
		}
	  
    /**
     * Diese Methode fügt den zu sendenden String in die Warteschlange für
     * ausgehende Nachrichten ein. 
     * 
     * @param message der zu sendende String
     */
	private void add2sendqueue(String message) {
		//@author Posemuckel Team
		synchronized (sendqueue) {
			sendqueue.addElement(message);
			sendqueue.notify();
		}
	}
}
