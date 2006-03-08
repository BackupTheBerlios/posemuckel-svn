package posemuckel.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import posemuckel.server.ClientInfo.ClientStatus;

/**
 * Enthält alle relevanten Daten, auf die der Server oft zugreifen muss,
 * um Zugriffe auf die Datenbank zu minimieren.
 * @author Posemuckel Team
 *
 */
public class Model {

	/**
	 * die einzige Instanz
	 */
	private static Model instance = null;
	
	/**
	 * Enthält die Informationen zu allen Clients, die online sind
	 */
	private HashMap<String,ClientInfo> clients;
	
	/**
	 * Enthält die Informationen zu allen Chats
	 */
	private HashMap<String,ChatInfo> chats;
	
	/**
	 * enthält die Projekte, in denen mind. ein Teilnehmer gerade 
	 * aktiv ist
	 */
	private HashMap<String,ProjectInfo> projects;
	
	/**
	 * die Sendewarteschlange
	 */
	private Vector<QueueItem> sendqueue;
	
	/**
	 * Stellt die Objekte der Sendewarteschlange dar
	 * @author Posemuckel Team
	 *
	 */
	public class QueueItem {
		
		/**
		 * enthält die Empfänger der Nachricht
		 */
		public Vector<ClientInfo> recievers;
		
		/**
		 * Nachricht
		 */
		public String message;
		
		/**
		 * Standardkonstruktor
		 *
		 */
		public QueueItem() {};
	}
	
	/**
	 * erstellt die einzige Instanz der Klasse Model
	 *
	 */
	private Model() {
		/*
		 * Datenstrukturen des Models werden initialisiert
		 */
		clients = new HashMap<String,ClientInfo>();
		chats = new HashMap<String,ChatInfo>();
		projects = new HashMap<String,ProjectInfo>();
		sendqueue = new Vector<QueueItem>();
		/*
		 * Allgemeiner Chat wird erstellt
		 */
		ChatInfo chat = new ChatInfo("0", false);
		addChat(chat);
	}

	/**
	 * Liefert die einzige Instanz der Klasse Model
	 * @return die einzige Instanz der Klasse Model
	 */
	public static Model getInstance() {
		if ( instance == null )
			instance = new Model();
		return instance;
	}
	
  /**
   * Gibt die Sendewarteschlange zurück.
   * @return Sendewarteschlange
   */	
	public Vector<QueueItem> getSendqueue() {
		return sendqueue;
	}
	
	/**
	 * Fügt einen Client hinzu. Dieser ist dann automatisch im Chat "0" 
	 * dabei.
	 *  
	 * @param ci Eine Referenz auf den entsprechenden Client.
	 * @return true, wenn alles geklappt hat, false, wenn das Projekt 
	 * schon voll ist.
	 */
	public boolean addClient(ClientInfo ci){	
		clients.put(ci.getHash(),ci);
		add2Chat(ci,"0");
		return true;
	}
	
	/**
	 * Löscht den Client aus dem Modell und aus allen Chats.
	 * 
	 * @param ci Der zu entfernende Client.
	 */
	public void delClient(ClientInfo ci){
		clients.remove(ci.getHash());
		if(ci.getCurrentProject() != null) {
			ci.getCurrentProject().remove_client(ci);
		}
		ChatInfo[] chats = ci.getChats();
		for (ChatInfo chat : chats) {
			removeFromChat(ci, chat);
		}
	}


	/**
	 * Fügt einen Chat hinzu. 
	 *  
	 * @param ci Eine Referenz auf den entsprechenden Client.
	 * @return true, wenn alles geklappt hat, false, wenn das Projekt schon voll ist.
	 */
	public boolean addChat(ChatInfo ci){	
		chats.put(ci.getID(),ci);
		return true;
	}
	
	/**
	 * Fügt einen Chat hinzu, wenn nicht bereits ein Chat mit dieser ID im Model
	 * eingetragen wurde.
	 * @param chatID ID des Chat
	 */
	public void addChat(String chatID, boolean isProjectChat) {
		if(!chats.containsKey(chatID)) {
			chats.put(chatID, new ChatInfo(chatID, isProjectChat));
		}
	}
	
	/**
	 * Löscht den Chat aus dem Modell
	 * 
	 * @param ci Der zu entfernende Client.
	 */
	public void delChat(ChatInfo ci){
		chats.remove(ci.getID());
	}
	
	/**
	 * Diese Methode liefert alle Clients
	 * die einen bestimmten Status haben. 
	 * Der Status wird in Form eines 
	 * ClientInfo.ClientStatus angegeben.
	 * 
	 * @param status der Status, den die Clients haben sollen
	 * @return Vector der Clients mit gefordertem Status.
	 */
	public Vector<ClientInfo> getClients(ClientInfo.ClientStatus status){
		ClientInfo ci;
		Vector<ClientInfo> vect = new Vector<ClientInfo>();
		Iterator it = clients.entrySet().iterator();
		while( it.hasNext() ) {
			Map.Entry entry = (Map.Entry)it.next();
			ci = (ClientInfo)entry.getValue();
			if ( ci.getStatus() == status ) {
				vect.addElement(ci);
			}
		}
		return vect;
	}
	
	/**
	 * Liefert zu den Hashwerten die passenden ClientInfos
	 * 
	 * @param hash Vector mit den HashWerten
	 * @return Vector mit den ClientInfos
	 */
	public Vector<ClientInfo> getClients(Vector<String> hash) {
		Vector<ClientInfo> vec = new Vector<ClientInfo>();
		for (String clientHash : hash) {
			vec.add((ClientInfo) clients.get(clientHash));
		}
		return vec;
	}

   /**
    * Liefert zu einer chatid den passenden Chat.
    * @param chatid ID des Chats, zu dem die Information geliefert werden
    * sollen
    * @return ChatInfo zum gewünschten Chat
    */	
	public ChatInfo getChat(String chatid) {
		return (ChatInfo) chats.get(chatid);
	}

	/**
	 * Prüft, ob der angegebene Benutzer angemeldet ist,
	 * also in der Liste der Clients und auch ONLINE
	 * @param hash Hash des Benutzers
	 * @return true, falls der Benutzer eingeloggt ist, false sonst
	 */
	public boolean isLoggedIn(String hash) {
		ClientInfo client = (ClientInfo)clients.get(hash);
		if( client == null )
			return false; // Client gar nicht vorhanden
		if ( client.getStatus() == ClientInfo.ClientStatus.ONLINE )
			return true; // Client ist ONLINE
		else
			return false; // Client ist nicht ONLINE
	}
	
	/**
	 * Gibt zu einem Hash-Wert eines eingeloggten
	 * Benutzers den passenden Benutzernamen zurück.
	 * @param hash Hash des Benutzers
	 * @return null, wenn der Client nicht existiert, Benutzername sonst
	 */
	public String getUser(String hash) {
		ClientInfo ci = (ClientInfo)clients.get(hash);
		if ( ci == null )
			return null;
		return ci.getUserName();
	}	
	
	/**
	 * Sucht zu den Benutzernamen die HashWerte heraus. Wenn der Anwender nicht
	 * eingeloggt ist, wird kein HashWert in den Vector eingefügt.
	 * @param usernames Benutzernamen
	 * @return Vector mit den Hashwerten
	 */
	protected Vector<String> getHashForNames(Vector<String> usernames) {
		Vector<String> hash = new Vector<String>();
		ClientInfo cl;
		Iterator it = clients.entrySet().iterator();
		while( it.hasNext() ) {
			Map.Entry entry = (Map.Entry)it.next();
			cl = (ClientInfo)entry.getValue();
			if (cl.getStatus() == ClientInfo.ClientStatus.ONLINE
			 && usernames.contains(cl.getUserName())) {
				hash.add(cl.getHash());
			}
		}
		return hash;
	}

	
	/**
	 * Liefert den Status eines Benutzers nach Angabe seines Namens
	 * @param name Name des Benutzers
	 * @return Status des Benutzers
	 */
	public ClientStatus statusByUsername(String name) {
		ClientInfo cl;
		Iterator it = clients.entrySet().iterator();
		while( it.hasNext() ) {
			Map.Entry entry = (Map.Entry)it.next();
			cl = (ClientInfo)entry.getValue();
			if (cl.getUserName().equals(name)) {
				return cl.getStatus();
			}
		}
		return ClientInfo.ClientStatus.OFFLINE;
	}

	/**
	 * Liefert alle Clients, die an diesem Chat beteiligt sind.
	 * @param chatid ID des Chats, dessen Teilnehmer gesucht werden
	 * @return alle Informationen der Clients, die an diesem Chat
	 * teilnehmen
	 */
	public Vector<ClientInfo> chatmembers(String chatid) {
		ChatInfo chat = chats.get(chatid);
		if ( chat == null )
			return null;
		Vector<ClientInfo> clients = chat.getClients(ClientInfo.ClientStatus.ONLINE);
		return clients;
	}

	/**
	 * Liefert zu den im übergebenen Vector angegebenen Hashes die
	 * passenden ClientInfos wiederum in einem Vector.
	 * @param userHashes Vector der Hashes
	 * @return Einen Vector mit den entsprechenden ClientInfos.
	 */
	public Vector<ClientInfo> selectedClients(Vector<String> userHashes) {
		Vector<ClientInfo> result = new Vector<ClientInfo>();
		for( String hash : userHashes ) {
			ClientInfo cl = clients.get(hash);
			if ( cl != null)
				result.add(cl);
		}
		return result;
	}

	/**
	 * Fügt den Benutzer dem Chat hinzu.
	 * @param client Client-Informationen des Benutzers
	 * @param chatid ID des Chats, zu dem der Benutzer hinzugefügt wird
	 * @return true, wenn der Client hinzugefügt wurde und false, wenn
	 * der Client schon am Chat beteiligt ist oder der Chat nicht existiert.
	 */
	public boolean add2Chat(ClientInfo client, String chatid) {
		ChatInfo chat = getChat(chatid);
		// Der Chat muss auf null geprüft werden:
		if ( chat == null )
			return false;
		if( chat.isParticipant(client) )
			return false;
		else {
			chat.addClient(client);
			client.addChat(chat);
			return true;
		}
	}
	
	/**
	 * Entfernt den Client aus einem Chat.
	 * @param client Client, der entfernt werden soll
	 * @param chatid ID des Chats, aus dem der Client entfernt werden soll
	 * @return true, wenn der Client erfolgreich entfernt wurde und 
	 * false, wenn der Client gar nicht am Chat beteiligt ist.
	 */
	public boolean removeFromChat(ClientInfo client, String chatid) {
		ChatInfo chat = getChat(chatid);
		return removeFromChat(client, chat);
	}
	
	/**
	 * Hilfsmethode, die einen Client aus einem Chat löscht
	 * @param client Client
	 * @param chat Chat, aus dem der Client gelöscht wird
	 * @return true, falls der Client an dem Chat teilgenommen hat, false
	 * sonst
	 */
	protected boolean removeFromChat(ClientInfo client, ChatInfo chat) {
		if( !chat.isParticipant(client) )
			return false;
		else {
			chat.delClient(client);
			client.removeChat(chat);
			return true;
		}
	}

	/**
	 * Fügt einen Client einem Projekt hinzu.
	 * @param client Client
	 * @param project Projekt
	 */
	public void add2Project(ClientInfo client, ProjectInfo project) {
		//erst mal aus dem alten Projekt entfernen
		ProjectInfo oldProject = client.getCurrentProject();
		if(oldProject != null) {
			oldProject.remove_client(client);
			removeFromChat(client, oldProject.getChatID());
		}
		project.add_client(client);
		add2Chat(client, project.getChatID());
	}
	
	/**
	 * Gibt das Projekt mit der ID zurück. Wenn das Projekt noch nicht in der
	 * HashMap eingetragen ist, wird ein neues ProjectInfo erzeugt, dessen Daten
	 * noch initialisiert werden müssen.
	 * 
	 * @param projectID ID des Projektes, zu dem Informationen benötigt 
	 * werden
	 * @return Projektinformationen zu dem gwünschten Projekt
	 */
	public ProjectInfo getProject(String projectID) {
		if(projects.containsKey(projectID)) return projects.get(projectID);
		ProjectInfo project = new ProjectInfo(projectID);
		projects.put(projectID, project);
		return project;
	}

	/**
	 * Gibt zu einem Hash-Wert alle Clients zurück, die im gleichen
	 * Projekt aktiv sind, wobei null gelifert wird, wenn es keinen
	 * passenden Client gibt oder dieser Client kein aktives Projekt
	 * hat.
	 * @param hash Der Hash-Wert des Clients
	 * @return Die passende ClientInfo und null, falls der Client
	 * nicht existiert oder dessen aktuelles Projekt null ist.
	 */	
	public Vector<ClientInfo> getCurrentProjectMembers(String hash) {
		ClientInfo client = clients.get(hash);
		// Wenn kein Client gefunden wurde, gib null zurück:
		if ( client == null )
			return null;
		
		ProjectInfo currentProject = client.getCurrentProject();
		// Wenn kein Projekt gefunden wurde, gib null zurück:
		if ( currentProject == null )
			return null;
		
		Vector<ClientInfo> participants = currentProject.get_clients(ClientInfo.ClientStatus.ONLINE);
		return participants;
	}	
}
