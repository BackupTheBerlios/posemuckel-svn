package posemuckel.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import posemuckel.common.ClientHash;
import posemuckel.common.Config;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.GetText;
import posemuckel.common.InvalidMessageException;
import posemuckel.common.Message_Handler;

/**
 * Stellt die serverseitige Erweiterung der Klasse MessageHandler dar und
 * dient der Verarbeitung von Nachrichten die von Clients an den Server
 * geschickt werden.
 */
public class ClientMessage extends Message_Handler {

	/**
	 * Enthält zu jeder URL die Benutzer, die sie gerade besuchen
	 */
    private static HashMap<String,HashSet<String>> currentUserURLs = new HashMap<String,HashSet<String>>();
    
    /**
     * Dient zum Senden der Servernachrichten an die Clients
     */
	private ServerMessage servermess;
	
	/**
	 * Informationen über den Client, der gerade behandelt wird
	 */
	private ClientInfo clientInfo;
	
	/**
	 * Dient zum Senden an den Client, der die Anfrage an den Server
	 * gestellt hat
	 */
	private Vector<ClientInfo> thisclient;
	
	/**
	 * Datenbank
	 */
	private DB	data;
	
	/**
	 * Model
	 */
	private Model model;
	
	/**
	 * Nachrichten-ID
	 */
	private String messageID;
	
	/**
	 * Konfiguration
	 */
	private Config config = Config.getInstance();
	
    /**
     * Konstruktor der Klasse ClientMessage
     * @param sproc Serverprozess, der die Anfrage verarbeitet
     * @param clinf Information zu dem Client, der die Anfrage stellt
     */
    public ClientMessage(ServerProcess sproc, ClientInfo clinf) {
	  super();
	  data = new DB();
	  model = Model.getInstance();
	  servermess = ServerMessage.getInstance();
	  clientInfo = clinf;
	  thisclient = new Vector<ClientInfo>();
	  thisclient.add(clientInfo);
    }
    
    /**
     * Hilfsmethode, die die URL eines Benutzers aktualisiert
     * @param user Benutzer
     * @param url neue URL
     */
    private void updateUserURL(String user, String url) {
    	synchronized (getClass()) {
    		HashSet<String> users;
    		if (currentUserURLs.containsKey(url)) {
    			users = currentUserURLs.remove(url);
    		} else {
    			users = new HashSet<String>();
    		}
    		users.add(user);
			currentUserURLs.put(url, users);
			clientInfo.setCurrentURL(url);
    	}   	
    }
    
    /**
     * Hilfsmethode, die zu einer gegebenen URL alle Benutzer ausgibt,
     * die diese gerade besuchen
     * @param url URL
     * @return Vector, der alle Benutzer enthält, die die URL gerade
     * besuchen
     */
    private Vector<String> getCurrentUsers(String url) {
    	Vector<String> currentUsers = new Vector<String>();
    	String user = clientInfo.getUserName();
    	if (currentUserURLs.containsKey(url)) {
			HashSet<String> users = currentUserURLs.get(url);
			Iterator it = users.iterator();
			while (it.hasNext()) {
				String st = (String)it.next();
				if (!st.equals(user)) {
					currentUsers.add(st);
				}
			}
		} 	
    	return currentUsers;   	
    }
  
    /**
     * Diese Methode gibt es nur solange, bis eine bessere 
     * Lösung gefunden wurde!
     * 
     * Sie wird von ServerProcess aufgerufen, um alle Mitglieder des
     * globalen Chats über das Verlassen eines Benutzers zu informieren.
     * Inzwischen wird sie auch von ClientMessage#login verwendet, um über
     * den Zutritt zum globalen Chat zu informieren.
     * 
     * @param chatid
     * @throws IOException
     */
    public void sendChatMembers(String chatid) throws IOException {
	    // Vorläufig:
	    // Wenn der Login erfolgreich war, gibt es eine
	    // Notifikation an alle Clients im Chat mit
	    // ID 0, dass ein neuer Client dabei ist.
	    String[] members = getOnlineUsers(model.chatmembers(chatid));
	    if(members != null && members.length > 0) {
		    servermess.chat_members(model.chatmembers(chatid),chatid,members);
	    }
    }
    
    /**
     * Wird aufgerufen, wenn sich der Status eines Benutzers geändert hat
     * @param user Benutzer
     * @param status neuer Status des Benutzers
     * @throws IOException
     * @throws SQLException
     */
    public void sendStatusChange(String user, String status) throws IOException, SQLException {
    	Vector<String> interestedUsers = data.getUsersForStatusChange(user);
		Vector<String> userHashes = model.getHashForNames(interestedUsers);
		servermess.userStatus(model.selectedClients(userHashes),user, "-1", status);
    }
    
    /**
     * Sendet eine Fehler-Nachricht an den Client der Anfrage zurück
     *
     */
    public void sendError() {
    	System.out.println("_____Send an error " + messageID);
    	
    	servermess.error(thisclient,"", messageID);
    }

	/**
	 * Wird aufgerufen, wenn eine START_PROJECT-Nachricht eingetroffen
	 * ist.
	 */
    protected boolean startProject(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
	    boolean b = true;
	    if (model.isLoggedIn(hash)) {
	    	// Erstmal alles einlesen:
	    	String user = model.getUser(hash);
	    	String topic = in.readLine();
	    	topic = GetText.escape_sql_write(topic);
		    String isPrivate = in.readLine();
		    String date = data.getDate();
		    if(!(isPrivate.equals("0") | isPrivate.equals("1")))
		    		new InvalidMessageException("Die Angabe ob der Chat privat(öffentlich ist, ist fehlerhaft " +
		    				"erwarteter Wert: 0/1, empfangener Wert: " +isPrivate);
		    String maxUsers = in.readLine();
		    String description = in.readLine();
	    	description = GetText.escape_sql_write(description);
	   
	    	// So, jetzt müssen wir schauen, ob es so ein
	    	// Projekt mit diesem Namen schon gibt.
	    	try{
	    		if(data.projectExists(topic)){
	    			// Wenn ja, wird die Abarbeitung der Anfrage beendet.
	    			servermess.error(thisclient,"", id);
	    			return true;
	    		}
	    	} catch(SQLException e) {
	    		// Dieser catch-Block ist hier notwendig,
	    		// weil nur hier die aktuelle Nachtichten-ID
	    		// zur Verfügung steht.
    			servermess.error(thisclient,"", id);
    			return true;
	    	}
	    	
	    	// Alles klar, das Projekt kann angelegt werden:
		    Integer c = new Integer(count);
		    String[] userToInvite = GetText.escape_sql_write(parse(in,c.intValue()-4, "START_PROJECT"));
		    int projectID = data.addProject(user, topic, isPrivate,
		    		maxUsers, description, date, userToInvite);
		    int freeSpaces = (new Integer(maxUsers).intValue()) - userToInvite.length;
		    servermess.ack(thisclient,"", id);
		    servermess.new_project(model.getClients(ClientInfo.ClientStatus.ONLINE),"", "-1", projectID, topic, user, 
		    		isPrivate, String.valueOf(freeSpaces), description, data.convertDate(date));
		    // Jetzt müssen Vektoren aufgebaut werden, die nur die
		    // online/offline-Users enthalten:
		    Vector<String> online = new Vector<String>();
		    Vector<String> offline = new Vector<String>();
		    for ( String username : userToInvite ) {
		    	if( model.statusByUsername(username) == ClientInfo.ClientStatus.ONLINE )
		    		online.add(username);
		    	else
		    		offline.add(username);
		    }
		    //die Nachricht sollte nach new_Project verschickt werden
		    // Diese wird an alle ONLINE-Users verschickt.
		    Vector<ClientInfo> clients = model.getClients(model.getHashForNames(online));
		    if( !clients.isEmpty() )
		    	servermess.invite(clients, projectID);
			    // Aktualisiere die Daten im ClientInfo des Clients, denn
			    // er könnte ja z.B. seine Mailadresse geändert haben.
		    data.getClientData(clientInfo);
	    	Vector<ClientInfo> clients2 = data.getClients(offline);
		    ProjectInfo pi = new ProjectInfo("0");
    		pi.setTopic(topic);
    		pi.setDescription(description);
    		pi.setOwner(clientInfo);
    		for ( ClientInfo cl : clients2 ) {
    			if( cl != null && (config.getconfig("DO_SENDMAIL")).equals("yes") ) {
    				SendMail.send_invitation(cl,pi);	
    			}
    		}	
	    } else {
	    	servermess.access_denied(thisclient,"",id);
		    b = false;
	    }	  	  
	    return b;
    }

    /**
     * Wird aufgerufen, wenn eine JOIN_PROJECT-Nachricht eintrifft
     */
	protected boolean joinProject(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;

    	if (model.isLoggedIn(hash)) {   		
	    	String user = model.getUser(hash);
    		String projectID = in.readLine();
    		boolean isInvited = data.isInvited(user, projectID);
    		boolean hasAnswered = data.hasAnsweredInvitation(projectID, user);
			if ((data.isPrivateProject(projectID) &&
		        !(isInvited || data.isOwner(
		        		user, projectID))) | data.isFull(projectID) |
		        		data.isMember(user, projectID)) {
				servermess.access_denied(thisclient,"",id);
			} else {
				data.addUserToProject(user, projectID);
				String[] projectData = {projectID, String.valueOf(-1)};
				servermess.ack(thisclient,"",id);
				ProjectInfo project = model.getProject(projectID);
				if(project != null) {
					Vector<ClientInfo> recievers = project.get_clients(ClientInfo.ClientStatus.ONLINE);
					servermess.memberChange(recievers, "PROJECT", projectID, 
							clientInfo.getUserName(), "1");
				}
				if ((!isInvited) || (hasAnswered)) {
					servermess.generic(model.getClients(ClientInfo.ClientStatus.ONLINE),id,
							"FREESPACES_CHANGED",projectData);
				}
			}   				
    	} else {
			servermess.access_denied(thisclient,"",id);
		    b = false;
    	}
    	return b;
    }

	/**
	 * Wird aufgerufen, wenn eine JOIN_CHAT-Nachricht eintrifft
	 */
	protected boolean joinChat(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;

    	if (model.isLoggedIn(hash)) { 		
	    	String user = model.getUser(hash);
    		String chatID = in.readLine();
    		//Sonderfall: der Chat 0
    		if(chatID.equals("0")) {
    			model.getChat("0").addClient(clientInfo);
    		}
			if ((data.isPrivateChat(chatID)) &&
		        (!data.isInvited(user, chatID))) {
				servermess.access_denied(thisclient,"",id);
			} else {
				data.addUserToChat(user, chatID);
				//über joinChat kann man keinem Projektchat beitreten
				//der Beitritt wird bei openProject erledigt
				model.addChat(chatID, false);
				model.add2Chat(clientInfo, chatID);
		    	servermess.ack(thisclient,"",id);
		    	sendChatMembers(chatID);
			}   				
    	} else {
			servermess.access_denied(thisclient,"",id);
		    b = false;
    	}
    	return b;
    }

	/**
	 * Wird aufgerufen, wenn eine LEAVE_PROJECT-Nachricht eintrifft.
	 */
    protected boolean leaveProject(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;

    	if (model.isLoggedIn(hash)) {
    		String projectID = in.readLine();
    		String user = model.getUser(hash);
			data.removeUserFromProject(user, projectID);
			String[] projectData = {projectID, String.valueOf(+1)};
			servermess.ack(thisclient,"",id);
			ProjectInfo project = model.getProject(projectID);
			if(project != null) {
				Vector<ClientInfo> recievers = project.get_clients(ClientInfo.ClientStatus.ONLINE);
				servermess.memberChange(recievers, "PROJECT", projectID, 
						clientInfo.getUserName(), "0");
			}
			servermess.generic(model.getClients(ClientInfo.ClientStatus.ONLINE),id,
					"FREESPACES_CHANGED",projectData);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }
    
    /**
     * Wird aufgerufen, wenn eine OPEN_PROJECT-Nachricht eintrifft
     */
	protected boolean openProject(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
    	boolean b = true;

    	if (model.isLoggedIn(hash)) {
    		//der Chat bleibt weiter offen!
    		//ChatInfo chat = model.getChat("0");
            //chat.delClient(clientInfo);            
    		//die Antwort erstellen
    		String projectID = in.readLine();
    		ProjectInfo project = model.getProject(projectID);
    		if(project.getChatID() == null) {
    			//erst mal das ChatInfo erzeugen
    			String chat = data.getChatID(Integer.valueOf(projectID));
    			model.addChat(chat, true);
    			project.setChatID(chat);
    		}    
    		String oldProjectChat = null;
    		if(clientInfo.getCurrentProject() != null) {
    			oldProjectChat = clientInfo.getCurrentProject().getChatID();
    		}
    		//fügt den Client in das Projekt und in den Projektchat ein
    		model.add2Project(clientInfo, project);
    		String chatID = project.getChatID();
    		log(hash, id, "Benutzer "+model.getUser(hash)+" hat den Browser geöffnet.");
	    	servermess.generic(thisclient, id, "PROJECT_CHAT", new String[] {chatID});
	    	if(oldProjectChat != null && !oldProjectChat.equals("")) {
	    		sendChatMembers(oldProjectChat);
	    	}
	    	sendChatMembers(chatID);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
	}

	/**
     * Wird aufgerufen, wenn eine MY_PROJECTS-Nachricht eintrifft
     */
	protected boolean myProjects(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;
    	if (model.isLoggedIn(hash)) {
       		String user = model.getUser(hash);
    		Vector projects = data.getProjects(user);
    		String[] proj = getProjectsArray(projects);
    		servermess.generic(thisclient,id,"YOUR_PROJECTS",proj);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }
	
	/**
     * Wird aufgerufen, wenn eine GET_INVITATIONS-Nachricht eintrifft
     */
	protected boolean getInvitations(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;
    	if (model.isLoggedIn(hash)) {
    		Vector projects = data.getInvitations(clientInfo.getUserName());
    		String[] proj = getProjectsArray(projects);
    		servermess.generic(thisclient,id,"PROJECTS",proj);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }
	
	/**
     * Wird aufgerufen, wenn eine GET_PROJECTS-Nachricht eintrifft
     */
    protected boolean getProjects(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;
    	if (model.isLoggedIn(hash)) {
    		Vector projects = data.getProjects();
    		String[] proj = getProjectsArray(projects);
    		servermess.generic(thisclient,id,"PROJECTS",proj);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }
    
    /**
     * Hilfsmethode, die die Elemente des übergebenen Vectors in einen
     * Array von Strings einfügt.
     * @param projects Vector mit Projektinformationen
     * @return Array mit den Elementen des Vectors
     */
    private String[] getProjectsArray(Vector projects) {
    	String[] proj = new String[projects.size()];
		Iterator it = projects.iterator();
		int i = 0;
		while (it.hasNext()) {
			proj[i] = (String)it.next();
			i++;
		}
		return proj;
	}

    /**
     * Wird aufgerufen, wenn eine START_CHAT-Nachricht eintrifft
     */
	protected boolean startChat(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;

    	if (model.isLoggedIn(hash)) {
       		String user = model.getUser(hash);
    		String isPrivate = in.readLine();
    		Vector<String> users = new Vector<String>();
    		Integer c = new Integer(count);
    		int chatID;
    		if (isPrivate.compareTo("0") == 0) {
    			users = getStrings(c.intValue()-1, in);
    			chatID = data.addChat(true, user, users);
    		} else {
    			chatID = data.addChat(false, user, users);
    		}
    		String[] chat = getChatArray(String.valueOf(chatID), user, users);
    		Vector<ClientInfo> infos = model.getClients(model.getHashForNames(users));
    		infos.add(clientInfo);
    		servermess.newchat(infos, id, chat);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }

	/**
	 * Hilfsmethode, die die übergebenenen Parameter in einen Array
	 * einfügt.
	 * @param chatID ID des Chats
	 * @param user Benutzer
	 * @param users Vector mit Benutzern
	 * @return Array, der alle Datenelemente der Chat-Nachricht enthält
	 */
    private String[] getChatArray(String chatID, String user, Vector users) {
		String[] st = new String[users.size()+2];
		st[0] = chatID;
		st[1] = user;
		Iterator it = users.iterator();
		int i = 2;
		while (it.hasNext()) {
			st[i] = (String)it.next();
			i++;
		}
		return st;
	}

    /**
     * Wird aufgerufen, wenn eine CHAT-Nachricht im Server eintrifft.
     * Der Text der Chatnachricht wird an alle Chatteilnehmer verteilt.
     * @throws SQLException 
     */
	protected boolean chat(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;

    	if (model.isLoggedIn(hash)) {
       		String user = model.getUser(hash);
    		String chatID = in.readLine();
    		if (chatID == null)
    			throw new InvalidMessageException("ChatID is null!");
    		String text = readChatText(count, in);
    		if (chatID.equals(EnumsAndConstants.LOG_CHAT_ID)) {
    			log(hash, id, text);
    		} else {
    			if (clientInfo.getCurrentProject() != null)
    			data.addText(text, user, chatID, 
    					clientInfo.getCurrentProject().getProjectID());
    			servermess.chat(model.chatmembers(chatID),user,id,chatID,text);
    		}			
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }
    
	/**
	 * Hilfsmethode, die den übertragenen Chattext einliest und in einem
	 * String zurückgibt.
	 * @param count Anzahl der Wörter des Textes
	 * @param in BufferedReader
	 * @return eingelesenen Text als String
	 * @throws IOException
	 * @throws InvalidMessageException
	 */
    private String readChatText(String count, BufferedReader in) 
    throws IOException, InvalidMessageException {
    	String chatText = new String();
		Integer c = new Integer(count);
		int counter = c.intValue()-2;
		for (int i=0; i<counter; i++) {
			chatText += in.readLine();
			chatText += "\r\n";
		}
		chatText += in.readLine();
		return chatText;
	}

    /**
     * Wird aufgerufen, wenn eine TYPING-Nachricht im Server eintrifft.
     * Die Nachricht wird an alle Chatteilnehmer verteilt.
     */
	protected boolean typing(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException {
    	boolean b = true;
    	if (model.isLoggedIn(hash)) {
       		String user = model.getUser(hash);
    		String chatID = in.readLine();
    		if (chatID == null)
    			throw new InvalidMessageException("ChatID is null!");
			servermess.typing(model.chatmembers(chatID),user,id,chatID);

    	} else {
			servermess.access_denied(thisclient,"",id);
	    	b = false;
    	}
    	return b;
    }
    
	/**
	 * Wird aufgerufen, wenn eine READING-Nachricht im Server eintrifft.
	 * Diese Nachricht wird an alle Chatmitglieder verteilt.
	 */
    protected boolean reading(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException {
    	boolean b = true;
    	if (model.isLoggedIn(hash)) {
       		String user = model.getUser(hash);
    		String chatID = in.readLine();
    		if (chatID == null)
    			throw new InvalidMessageException("ChatID is null!");
			servermess.reading(model.chatmembers(chatID),user,id,chatID);

    	} else {
			servermess.access_denied(thisclient,"",id);
	    	b = false;
    	}
    	return b;
    }
    
    /**
     * Wird aufgerufen, wenn eine ADD_BUDDY-Nachricht im Server eintrifft.
     * Der Client, der die Nachricht verschickt hat, bekommt eine NEW_BUDDY-
     * Nachricht zurückgeschickt.
     */
    protected boolean addBuddy(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;
    	messageID = id;
    	if (model.isLoggedIn(hash)) {
    		String user = model.getUser(hash);
    		String status;
    		String buddy = in.readLine();
    		data.addBuddy(user, buddy);
    	    if (model.statusByUsername(buddy) == ClientInfo.ClientStatus.ONLINE) {
    	    	status = "ONLINE";
    	    } else {
    	    	status = "OFFLINE";
    	    }
    	    String[] buddyData = {status};
    	    servermess.generic(thisclient,id,"NEW_BUDDY",buddyData);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }
    
    /**
     * Wird aufgerufen, wenn eine DEL_BUDDY-Nachricht im Server eintrifft.
     * Der Client, der die Nachricht verschickt hat, bekommt im Erfolgs-
     * fall eine ACK-Nachricht zurückgeschickt.
     */
    protected boolean delBuddy(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;

    	if (model.isLoggedIn(hash)) {
    		String user = model.getUser(hash);
    		String buddy = in.readLine();
    		data.deleteBuddy(user, buddy);
	    	servermess.ack(thisclient,"",id);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }
    
    /**
     * Wird aufgerufen, wenn eine MY_BUDDIES-Nachricht im Server eintrifft.
     * Der Client, der die Nachricht verschickt hat, bekommt im Erfolgs-
     * fall eine YOUR_BUDDIES-Nachricht zurückgeschickt.
     */
    protected boolean myBuddies(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;

    	if (model.isLoggedIn(hash)) {
    		Vector buddies = data.getBuddies(model.getUser(hash));
    		String[] buddyData = getBuddyArray(buddies);
    		servermess.generic(thisclient,id,"YOUR_BUDDIES",buddyData);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }
  
    /**
     * Hilfsmethode, die die Elemente des übergebenen Vectors in einen
     * String-Array einfügt.
     * @param buddies Vector mit Benutzernamen
     * @return Elemente des Vectors in einem String-Array
     */
    private String[] getBuddyArray(Vector buddies) {
		String[] buddyData = new String[(2*buddies.size())];
		Iterator it = buddies.iterator();
		String buddy;
		int i = 0;
		while (it.hasNext()) {
			buddy = (String)it.next();
			buddyData[i] = buddy;
			i++;
			if (model.statusByUsername(buddy) == ClientInfo.ClientStatus.ONLINE) {
				buddyData[i] = "ONLINE";
			} else {
				buddyData[i] = "OFFLINE";
			}
			i++;
		}
		return buddyData;
	}
    
    /**
     * Hier wird eine Suche nach Benutzern gestartet und dann deren
     * Profile zurückgegeben.
     */
    protected boolean searchUsers(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;

    	String[] query = parse(in, Integer.valueOf(count),"SEARCH_USERS");
    	query = GetText.escape_sql_read(query);
    	if (model.isLoggedIn(hash)) {
    		String[] userdata = data.searchUsers(query);
    		servermess.generic(thisclient, id, "USERS", userdata);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }
    
    /**
     * Diese Methode verarbeitet eine VISITING-Nachricht.
     * Die Nachricht wird an alle Clients des Projekts weitergeleitet.
     */
    protected boolean visiting(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	boolean b = true;
    	String[] data = parse(in, Integer.valueOf(count),"VISITING");    	

    	if (model.isLoggedIn(hash)) {
    		String user = model.getUser(hash);
    		updateUserURL(user, data[0]);
    		this.data.addURL(user,clientInfo.getCurrentProject().getProjectID(),
    				data[0],data[1],data[2]);
    		log(hash, id, "Seite "+data[0]+" wird besucht");
    		Vector<ClientInfo> recievers = model.getCurrentProjectMembers(hash);
    		servermess.visiting(recievers,"-1",user,data[0],data[1],data[2]);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}    	
    	return b;
    }
    
    /**
     * Wird aufgerufen, wenn eine VIEWING-Nachricht im Server eintrifft.
     * Im Erfolgsfall wird diese Nachricht an alle Projektmitglieder
     * verteilt.
     * @throws SQLException 
     */
	protected boolean viewing(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
    	boolean b = true;
    	String[] data = parse(in, Integer.valueOf(count),"VISITING");    	

    	if (model.isLoggedIn(user)) {
    		String name = clientInfo.getUserName();
    		updateUserURL(name, data[0]);
    		log(user, id, "Seite "+data[0]+" wird besucht");
    		Vector<ClientInfo> recievers = model.getCurrentProjectMembers(user);
    		servermess.viewing(recievers,name, data[0]);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}    	
    	return b;
	}

	/**
	 * Wird aufgerufen, wenn eine VOTING-Nachricht im Server eintrifft.
	 * Im Erfogsfall wird diese Nachricht an alle Projektteilnehmer
	 * verteilt.
	 */
	protected boolean voting(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
    	boolean b = true;
    	String[] values = parse(in, Integer.valueOf(count),"VOTING");    	

    	if (model.isLoggedIn(hash)) {
    		String user = model.getUser(hash);
    		data.addVote(user,clientInfo.getCurrentProject().getProjectID(), 
    				values[0], values[1]);
    		log(hash, id, "Seite "+values[0]+" wurde mit "+values[1]+" bewertet");
    		Vector<ClientInfo> recievers = model.getCurrentProjectMembers(hash);
    		servermess.voting(recievers,"-1",user,values[0], values[1]);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}    	
    	return b;
	}
	
	/**
	 * Wird aufgerufen, wenn eine GET_ACTIVE_USERS-Nachricht im Server
	 * eintrifft. Im Erfolgsfall bekommt der Client der Anfrage eine
	 * ACTIVE_USERS-Nachricht zurück.
	 */
	protected boolean getActiveUsers(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
    	String projectID = in.readLine();

    	if (model.isLoggedIn(hash)) {
    		Vector users = data.getProjectMembers(Integer.parseInt(projectID));
    		String[] usersData = vectorToStringArray(users);
    		servermess.generic(thisclient, id,"ACTIVE_USERS", usersData);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
    }

	/**
	 * Wird aufgerufen, wenn eine GET_PROFILE-Nachricht im Server ein-
	 * trifft. Im Erfolgsfall bekommt der Client der Anfrage eine 
	 * PROFILE-Nachricht zurück.
	 */
	protected boolean getProfile(String hash, String id, String count, BufferedReader in) 
	  throws InvalidMessageException, IOException, SQLException {
    	boolean b = true;

    	if (model.isLoggedIn(hash)) {
    		String[] users = new String[Integer.valueOf(count)];
    		for(int i = 0; i < users.length; i++) {
    			users[i] = in.readLine();
    		}
    		try {
        		String[] profileData = vectorToStringArray(data.getProfile(users));
        		servermess.generic(thisclient, id,"PROFILE", profileData);
    		} catch(IllegalArgumentException e) {
        		servermess.error(thisclient,"", id);
    		}
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}
    	return b;
	}

	/**
	 * Hier wird vom Client eine Änderung des
	 * Benutzerprofils angefordert.
	 * 
	 * Die Übergebenen Werte entsprechen den üblichen
	 * Bedeutungen wie bei allen Nachrichten.
	 * 
	 * Die Parameter werden noch von der Eingabe
	 * gelesen.
	 */
	protected boolean setProfile(String hash, String id, String count, 
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		int numargs = Integer.valueOf(count);
		if (numargs != 8)
			throw new InvalidMessageException("Bei SET_PROFILE stimmt die Anzhal der Argumente nicht!");
		String[] values = parse(in,Integer.valueOf(count),"SET_PROFILE");
		values = GetText.escape_sql_write(values);
    	if (model.isLoggedIn(hash)) {
    		String user = model.getUser(hash);
    		if (! data.setProfile(user,values[0],values[1],values[2],values[3],values[4],values[5],values[6],values[7]) ) {
    			servermess.error(thisclient,"",id);
    	    	return false;
    		} else {
    			servermess.ack(thisclient,"",id);
    	    	return true;
    		}
    	} else {
			servermess.access_denied(thisclient,"",id);
    		return false;
    	}
	}
	
	/**
	 * Hilfsmethode, die die Elemente des übergebenen Vectors in einen
	 * String-Array einfügt.
	 * @param strings Vector, gefüllt mit Strings
	 * @return String-Array, der die Elemente des Vectors enthält
	 */
	private String[] vectorToStringArray(Vector strings) {
		String[] array = new String[strings.size()];
		Iterator it = strings.iterator();
		int i = 0;
		while (it.hasNext()) {
			array[i] = (String)it.next();
			i++;
		}
		return array;
	}

	/**
     * Hier wird ein neuer User registriert. Dies geschieht, indem
     * sein Name und die weiteren Werte aus der REGISTER-Nachricht in 
     * die Datenbank eingetragen werden.
     */
    protected boolean register(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	// nur ein Serverprozess darf zu einem Zeitpunkt die Methode
    	// register aufrufen; damit wird verhindert, dass sich zwei
    	// Benutzer mit den gleichen Daten registrieren können
    	synchronized (getClass()) {
    			
    		    String name = GetText.escape_sql_write(in.readLine());
    		    String surname = GetText.escape_sql_write(in.readLine());
    		    String email = GetText.escape_sql_write(in.readLine());
    		    String nickname = GetText.escape_sql_write(in.readLine());
    		    String password = GetText.escape_sql_write(in.readLine());
    		    String lang = GetText.escape_sql_write(in.readLine());
    		    String gender = GetText.escape_sql_write(in.readLine());
    		    String location = GetText.escape_sql_write(in.readLine());
    		    String comments = GetText.escape_sql_write(in.readLine());
    		    
    		    if(!data.existsUser(nickname)){
    		        String ip = clientInfo.getSocket().getInetAddress().getHostAddress();
    		        data.addUser(name, surname, email, nickname,
    					    password, lang, gender, location, comments, ip);
    		        clientInfo.setUserName(nickname);	
    		        servermess.ack(thisclient,"",id);
    		    } else {
    		        servermess.user_exists(thisclient,id);
    		    }
    	    return true;    		
    	}	    
    }

    /**
     * Wird aufgerufen, wenn eine LOGOUT-Nachricht im Server eintrifft.
     * Alle Benutzer, die über Statusänderungen des ausgeloggten Be-
     * nutzers informiert werden müssen (Buddies des Benutzers) 
     * erhalten eine USER_STATUS-Nachricht.
     */
    protected boolean logout(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
    	if ( !model.isLoggedIn(hash) ) {
			servermess.access_denied(thisclient,"",id);
    	} else {
    		String user = model.getUser(hash);
    		model.delClient(clientInfo);
    		data.logout(user);
	    	try {
				Vector<String> interestedUsers = data.getUsersForStatusChange(user);
				Vector<String> userHashes = model.getHashForNames(interestedUsers);
				servermess.userStatus(model.selectedClients(userHashes),user, "0", "OFFLINE");
			} catch (SQLException e) {
				e.printStackTrace();
			}    	

    		servermess.ack(thisclient,"", id);
    	}
    	return true;
    }

    /**
     * Wird aufgerufen, wenn eine LOGIN-Nachricht im Server eintrifft.
     * Alle Buddies des eingeloggten Benutzers werden über dessen
     * Statusänderung informiert.
     */
    protected boolean login(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException,
    		IOException, SQLException {
	    String user, pass;
	  
	    user = in.readLine();
	    if( user == null ) throw new InvalidMessageException("Fehler im Login-Paket beim Lesen der Nickname!");
	    pass = in.readLine();
	    if( pass == null ) throw new InvalidMessageException("Fehler im Login-Paket beim Lesen des Passwortes!");
	    
	    user = GetText.escape_sql_read(user);
	    pass = GetText.escape_sql_read(pass);
	    
	    if ( model.statusByUsername(user) == ClientInfo.ClientStatus.ONLINE ) {
	    	// Der Benutzer ist schon eingeloggt!
	    	servermess.access_denied(thisclient,"",id);
	    	// Da mach ich jetzt einfach mal nicht weiter :-)
	    	return true;
	    }
	    
	    String hashToSend = ClientHash.getClientHash(user+pass);
	    if ( data.correct_login(user, pass)) {
	    	// Erstmal alle wichtigen Daten in die
	    	// ClientInfo eintragen.
	    	Thread.currentThread().setName(user);
	    	clientInfo.setStatus(ClientInfo.ClientStatus.ONLINE);
		    clientInfo.setUserName(user);
		    clientInfo.setHash(hashToSend);
		    try {
				data.getClientData(clientInfo);
			} catch (SQLException e) {
				servermess.error(thisclient,hashToSend,id);
				return false;
			}
	    	// Diesen Client in der DB als eingelogt markieren.
	    	data.login(user, hashToSend);
			servermess.access_granted(thisclient,hashToSend,id);
		    // Überall-Chat holen und diesen Client hinzufügen.
		    // Dies geschieht erst jetzt, weil dabei der
		    // Client automtisch auch im Chat "0" ist.
	    	model.addClient(clientInfo);
	    	sendChatMembers("0");
	    	// alle Anwender, die an einer Statusänderung interessiert sind, benachrichtigen
	    	sendStatusChange(user, "ONLINE");
	    } else {
			servermess.access_denied(thisclient,"",id);		
	    }    
	    return true;
    }

    /**
     * Besorgt zu einem ClientInfo-Vector ein String-Array
     * der Mitglieder.
     * 
     * @param mems Vector, der die Clientinformationen der Mitglieder
     * enthält.
     * @return String-Array der Mitglieder
     */
	private String[] getOnlineUsers(Vector<ClientInfo> mems) {
		String[] members = new String[mems.size()];
		int cnt = 0;
		for ( Object cl : mems ) {
			members[cnt] = ((ClientInfo)cl).getUserName();
			cnt++;	
		}
		return members;
	}

	/**
	 * Diese Methode verschickt CHAT_MEMBERS Nachrichten an die Clients eines
	 * Chats.
	 */    
    protected boolean getChatMembers(String user, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
    	String chatid = in.readLine();
    	if ( chatid == null )
    		throw new InvalidMessageException("Fehler im GET_CHAT_MEMBERS-Paket beim Lesen der Chatid!");
   	    servermess.chat_members(model.chatmembers("0"),chatid,getOnlineUsers(model.chatmembers(chatid)));
    	return true;
	}
    
    /**
     * Diese Methode wird beim Empfang einer NOTIFY-Nachricht aufgerufen.
     * Der Server sendet daraufhin weitere NOTIFY Nachrichten an die entsprechenden
     * Empfänger.
     * @throws SQLException 
     */
    protected boolean notify(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
    	boolean b = true;
    	// erstmal die ersten vier Parameter einlesen...
    	String[] data = parse(in,Integer.valueOf(count),"NOTIFY");    	
    	
    	if (model.isLoggedIn(hash)) {
    		String user = model.getUser(hash);
    		
    		Vector<String> users = new Vector<String>();
    		for (int i=5; i < data.length; i++ )
    			users.add(data[i]);
    		log(hash, id, "Seite "+data[0]+" wurde hochgehalten");
			Vector<ClientInfo> recievers = model.getClients(model.getHashForNames(users));
    		servermess.notify(recievers,user,data[0],data[1],data[2],data[3],data[4]);
			servermess.ack(thisclient,"",id);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}    	
    	return b;
    }
    
    
    /**
     * Wird von den obigen Methoden aufgerufen, wenn eine variable Anzahl
     * von Strings einzulesen ist. Die eingelesenen Strings werden in einem
     * Vector zurückgegeben.
     * @param count Anzahl der Parameter der Nachricht, die noch zu
     * lesen sind.
     * 
     * @param in BufferedReader
     * @return die eingelesenen Strings in einem Vector
     * @throws IOException
     */
    protected Vector<String> getStrings(int count, BufferedReader in)
    throws IOException {
	    Vector<String> v = new Vector<String>();
	    for (int i = 0; i < count; i++) {
	    	v.add(in.readLine());
	    }
	    return v;
    }

	/**
	 * Hilfsmethode, die aus dem Reader die angegebene Zeilenzahl liest.
	 * (aus ServerMessage vom Client geklaut)
	 * @param in Reader
	 * @param count Zeilenzahl
	 * @return String-Array mit den gelesenen Zeilen
	 * @throws IOException
	 * @throws InvalidMessageException 
	 */
	private String[] parse(BufferedReader in, int count, String paketname) throws IOException, InvalidMessageException {
		String[] result = new String[count];
		for(int i = 0; i < count; i++) {
			result[i] = in.readLine();
			if ( result[i] == null )
				throw new InvalidMessageException("Fehler beim Parsen der "+paketname+"-Nachricht!");				
		}
		return result;
	}
    
	/**
	 * Liefert die Datenbank
	 * @return Datenbank
	 */
	public DB getDB() {
		return this.data;
	}
		
	/**
	 * Wird aufgerufen, wenn eine ADD_NOTE-Nachricht im Server eintrifft.
	 * Im Erfolgsfall erhalten alle Projektmitglieder eine NEW_NOTE-
	 * Nachricht.
	 */
	protected boolean addNote(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
		// erstmal die ersten vier Parameter einlesen...
    	String[] input = parse(in,Integer.valueOf(count),"ADD_NOTE");    	
    	if ( input.length != 3 )
    		throw new InvalidMessageException("Bei ADD_NOTE stimmt die Zahl der Parameter nicht.");
    	if (model.isLoggedIn(hash)) {
    		String user = model.getUser(hash);
    		data.addNote(user, 
    				clientInfo.getCurrentProject().getProjectID(), 
    				input[0],input[1],input[2]);
    		Vector<ClientInfo> recievers = model.getCurrentProjectMembers(hash);
    		if (input[2].equals("")) {
    			log(hash, id, "Seite "+input[0]+" wurde mit "+input[1]+" bewertet");
    			servermess.new_note(recievers,user,input[0],input[1], "0");
    		} else {
    			log(hash, id, "Seite "+input[0]+" wurde mit "+
    					input[1]+" bewertet und eine Notiz angeheftet");
    			servermess.new_note(recievers,user,input[0],input[1], "1");
    		}   		
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}    	
    	return b;
	}
	
	/**
	 * Wird aufgerufen, wenn eine GET_NOTES-Nachricht im Server eintrifft.
	 * Im Erfolgsfall bekommt der anfragende Client eine NOTES-Nachricht
	 * zurückgeschickt.
	 */
	protected boolean getNotes(String hash, String id, String count,
    		BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
		// erstmal die ersten vier Parameter einlesen...
    	String[] input = parse(in,Integer.valueOf(count),"GET_NOTES");    	
    	if ( input.length != 1 )
    		throw new InvalidMessageException("Bei GET_NOTES stimmt die Zahl der Parameter nicht.");
    	if (model.isLoggedIn(hash)) {
    		String user = model.getUser(hash);
    		if ( user == null )
    			throw new InvalidMessageException("Fehler beim Lesen von Notizen.");
    		String[] notes = vectorToStringArray(data.getNotes(input[0]));
    		servermess.generic(thisclient,id,"NOTES",notes);
    	} else {
			servermess.access_denied(thisclient,"",id);
    		b = false;
    	}    	
    	return b;
	}
	
	/**
	 * Wird aufgerufen, wenn eine DONT_ACCEPT_PROJECT-Nachricht im Server
	 * eintrifft. Im Erfolgsfall erhält der anfragende Client eine ACK-
	 * Nachricht zurück.
	 */
	protected boolean projectNotAccepted(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
    	String projectID = in.readLine();
    	if ( projectID == null )
    		throw new InvalidMessageException("Fehler im DONT_ACCEPT_PROJECT-Paket beim Lesen der Projektid!");
   	    if (model.isLoggedIn(hash)) {
   	    	String user = model.getUser(hash);
   	    	data.answerInvitation(user, projectID, false);
   	    	String[] projectData = {projectID, String.valueOf(+1)};
   	    	servermess.ack(thisclient,"",id);
			servermess.generic(model.getClients(ClientInfo.ClientStatus.ONLINE),id,
					"FREESPACES_CHANGED",projectData);
   	    } else {
   	    	servermess.access_denied(thisclient,"",id);
		    b = false;
   	    }
    	return b;
	}
	
	/**
	 * Wird aufgerufen, wenn eine GET_WEBTRACE-Nachricht im Server
	 * eintrifft. Im Erfolgsfall erhält der anfragende Client eine
	 * WEBTRACE-Nachricht.
	 */
	protected boolean webtrace(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
		if (model.isLoggedIn(hash)) {
			Vector webtrace = data.getWebtrace(
					clientInfo.getCurrentProject().getProjectID());
			String[] webtraceData = vectorToStringArray(webtrace);
			servermess.generic(thisclient, id, "WEBTRACE", webtraceData);
		} else {
			servermess.access_denied(thisclient,"",id);
		    b = false;
		}
		return b;
	}
	
	/**
	 * Wird aufgerufen wenn eine GET_SAME_URL_VIEWERS-Nachricht im Server
	 * eintrifft. Wenn der Benutzer eingeloggt ist, bekommt er eine
	 * SAME_URL_VIEWERS-Nachricht zurück.
	 */
	protected boolean getSameURLViewers(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException {
		boolean b = true;
		if (model.isLoggedIn(hash)) {
			Vector<String> viewers = getCurrentUsers(clientInfo.getCurrentURL());
			String[] viewersData = vectorToStringArray(viewers);
			servermess.generic(thisclient, id, "SAME_URL_VIEWERS", viewersData);
		} else {
			servermess.access_denied(thisclient,"",id);
		    b = false;
		}
		return b;
	}
	
	/**
	 * Wird aufgerufen, wenn eine NEW_FOLDER-Nachricht im Server eintrifft.
	 * Wenn der Elternordner des neuen Ordners noch nicht gelöscht wurde,
	 * erhalten alle Clients der Projektmitglieder eine NEW_FOLDER-Nachricht.
	 */
	protected boolean newFolder(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
		String[] input = parse(in,Integer.valueOf(count),"NEW_FOLDER");    	
    	if ( input.length != 2 )
    		throw new InvalidMessageException("Bei NEW_FOLDER stimmt die Zahl der Parameter nicht.");
		if (model.isLoggedIn(hash)) {
			String projectID = clientInfo.getCurrentProject().getProjectID();
			boolean folderAlreadyDeleted = data.addFolder(projectID, input[0], input[1]);
			if (!folderAlreadyDeleted) {
				log(hash, id, "Der Ordner "+input[0]+" wurde mit dem "+
						"Elternordner "+data.getFolder(input[1])+" erzeugt");
				String[] folderData = {data.getLatestFolderID()+"", input[0], input[1]};
				servermess.generic(model.getCurrentProjectMembers(hash), 
						id, "NEW_FOLDER", folderData);
			}
		} else {
			servermess.access_denied(thisclient,"",id);
		    b = false;
		}
		return b;
	}
	
	/**
	 * Wird aufgerufen, wenn eine DELETE_FOLDER-Nachricht im Server eintrifft.
	 * Wenn der zu löschende Ordner noch nicht gelöscht wurde,
	 * erhalten alle Clients der Projektmitglieder eine DELETE_FOLDER-Nachricht.
	 */
	protected boolean deleteFolder(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
		String[] input = parse(in,Integer.valueOf(count),"DELETE_FOLDER");    	
    	if ( input.length != 1 )
    		throw new InvalidMessageException("Bei DELETE_FOLDER stimmt die Zahl der Parameter nicht.");
		if (model.isLoggedIn(hash)) {
			boolean folderAlreadyDeleted = data.deleteFolder(input[0], true);
			if (!folderAlreadyDeleted) {
				log(hash, id, "Der Ordner "+data.getFolder(input[0])+
						"wurde gelöscht");
				String[] folderData = {input[0]};
				servermess.generic(model.getCurrentProjectMembers(hash), 
						id, "DELETE_FOLDER", folderData);
			}
		} else {
			servermess.access_denied(thisclient,"",id);
		    b = false;
		}
		return b;
	}
	
	/**
	 * Wird aufgerufen, wenn eine MOVE_FOLDER-Nachricht im Server
	 * eintrifft. Falls der neue Elternordner noch nicht gelöscht wurde,
	 * erhalten alle Clients der Projektmitglieder eine MOVE_FOLDER-
	 * Nachricht.
	 */
	protected boolean moveFolder(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
		String[] input = parse(in,Integer.valueOf(count),"MOVE_FOLDER");    	
    	if ( input.length != 2 )
    		throw new InvalidMessageException("Bei MOVE_FOLDER stimmt die Zahl der Parameter nicht.");
		if (model.isLoggedIn(hash)) {
			boolean folderAlreadyDeleted = data.moveFolder(input[0], input[1]);
			if (!folderAlreadyDeleted) {
				log(hash, id, "Der Ordner "+data.getFolder(input[0])+" wurde zum "+
						"Ordner "+data.getFolder(input[1])+" verschoben");
				String[] folderData = {input[0], input[1]};
				servermess.generic(model.getCurrentProjectMembers(hash), 
						id, "MOVE_FOLDER", folderData);
			} else {
				String[] folderData = {input[0]};
				servermess.generic(model.getCurrentProjectMembers(hash), 
						id, "DELETE_FOLDER", folderData);
			}		
		} else {
			servermess.access_denied(thisclient,"",id);
		    b = false;
		}
		return b;
	}
	
	/**
	 * Wird aufgerufen, wenn eine PARENTFOLDER_CHANGED-Nachricht im 
	 * Server eintrifft. Falls der neue Elternordner der URL noch nicht
	 * gelöscht wurde, erhalten alle Clients der Projektmitglieder eine
	 * PARENTFOLDER_CHANGED-Nachricht.
	 */
	protected boolean parentfolderChanged(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
		String[] input = parse(in,Integer.valueOf(count),"PARENTFOLDER_CHANGED");    	
    	if ( input.length != 2 )
    		throw new InvalidMessageException("Bei PARENTFOLDER_CHANGED stimmt die Zahl der Parameter nicht.");
		if (model.isLoggedIn(hash)) {
			boolean folderAlreadyDeleted = data.changeParentfolder(input[0], input[1]);
			if (!folderAlreadyDeleted) {
				//Folder ist noch nicht gelöscht
				if(input[1].equals("")) {
					log(hash, id, "Die Url " + input[0] + " wurde in die Tabelle verschoben.");
				} else {
					String folder = data.getFolder(input[1]);
					log(hash, id, "Die Url " + input[0] + "wurde in den Folder " + folder + " verschoben");
				}
				String[] folderData = {input[0], input[1]};
				servermess.generic(model.getCurrentProjectMembers(hash), 
						id, "PARENTFOLDER_CHANGED", folderData);
			} else {
				String[] folderData = {input[0], ""};
				log(hash, id, "Die Url " + input[0] + " wurde in die Tabelle verschoben.");
				servermess.generic(model.getCurrentProjectMembers(hash), 
						id, "PARENTFOLDER_CHANGED", folderData);
			}		
		} else {
			servermess.access_denied(thisclient,"",id);
		    b = false;
		}
		return b;
	}
	
	/**
	 * Wird aufgerufen, wenn eine GET_FOLDERSYSTEM-Nachricht im Server
	 * eintrifft. Wenn der Benutzer eingeloggt ist, erhält sein Client
	 * eine FOLDERSYSTEM-Nachricht.
	 */
	protected boolean getFoldersystem(String hash, String id, String count,
			BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		boolean b = true;
		if (model.isLoggedIn(hash)) {
			Vector foldersWithURLs = data.getFoldersWithURLs(
					clientInfo.getCurrentProject().getProjectID());
			String[] data = vectorToStringArray(foldersWithURLs);
			servermess.generic(thisclient, id, "FOLDERSYSTEM", data);
		} else {
			servermess.access_denied(thisclient,"",id);
		    b = false;
		}
		return b;
	}
	
	/**
	 * Wird aufgerufen, um eine Log-Nachricht an alle Projekt-
	 * teilnehmer zu schicken.
	 * @param hash Hash
	 * @param id Nachrichten-ID
	 * @param logText Text
	 * @throws SQLException 
	 */
	private void log(String hash, String id, String logText) throws SQLException {
        String user = model.getUser(hash);
        String date = data.getDatetime();
		String text = user+": "+logText;
		data.log(logText, clientInfo.getCurrentProject().getProjectID());
		servermess.chat(model.getCurrentProjectMembers(hash), 
				date, id, EnumsAndConstants.LOG_CHAT_ID, text);
	}
}
 