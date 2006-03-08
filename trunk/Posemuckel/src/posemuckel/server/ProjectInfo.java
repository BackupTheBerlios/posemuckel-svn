package posemuckel.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Die Klasse ProjectInfo enthält die Daten zu einem Projekt.
 * Dazu gehören Thema, max. Anzahl der User, die eingeladenen Benutzer,
 * der Chat des Projektes, sowie alle Benutzer, die zu diesem Projekt 
 * gehören und der "Besitzer" des Projektes.
 * 
 * @author Posemuckel Team
 *
 */
public class ProjectInfo {
	
	/**
	 * Eigentümer des Projektes
	 */
	private ClientInfo owner;
	
	/**
	 * Enthält die Mitglieder des Projektes
	 */
	private HashMap<String, ClientInfo> clients;
	
	/**
	 * Thema des Projektes
	 */
	private String topic;
	
	/**
	 * Beschreibung des Projektes
	 */
	private String description;
	
	/**
	 * Maximale Anzahl an Teilnehmern
	 */
	private int max_users;
	
	/**
	 * Chat-ID des Projektchats
	 */
	private String chatID;
	
	/**
	 * ID des Projektes
	 */
	private String projectID;
	
	/**
	 * Dies ist der Konstruktor. Es wird davon ausgegangen, dass hier einfach
	 * die Parameter aus dem Netzwerkpaket übergeben werden.
	 * Diese werden dann in Member-Variablen gespeichert.
	 * 
	 * @param id des Projektes
	 */
	public ProjectInfo(String id) {
		super();
		max_users = 10;
		projectID = id;
		clients = new HashMap<String, ClientInfo>();
		
	}
	
	/**
	 * Fügt einen Client diesem Projekt hinzu. Dabei wird auch
	 * das Projekt beim Client registriert.
	 * 
	 * @param ci Eine Referenz auf den entsprechenden Client.
	 * @return true, wenn alles geklappt hat, false, wenn das Projekt schon voll ist.
	 */
	public boolean add_client(ClientInfo ci){	
		//wenn unter myProjects eingetragen, dann habe ich auch Zutritt
		clients.put(ci.getHash(),ci);
		//ci.addProject(this);
		ci.setCurrentProject(this);
		return true;
	}
	
	/**
	 * Löscht den Client aus dem Projekt
	 * 
	 * @param ci Der zu entfernende Client.
	 */
	public void remove_client(ClientInfo ci){
		clients.remove(ci.getHash());
	}
	
	
	/**
	 * Diese Methode liefert alle Clients des Projektes,
	 * die einen bestimmten Status haben. Der Status wird in Form
	 * eines ClientInfo.ClientStatus angegeben.
	 * 
	 * @param status
	 * @return Vector der Clients mit gefordertem Status.
	 */
	public Vector<ClientInfo> get_clients(ClientInfo.ClientStatus status){
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
	 * Gibt die ID des ProjektChats zurück.
	 * 
	 * @return ID des ProjektChats
	 */
	public String getChatID() {
		return chatID;
	}
	
	/**
	 * Setzt die ID des ProjektChats.
	 * 
	 * @param id des ProjektChats
	 */
	public void setChatID(String id) {
		chatID = id;
	}
	
	/**
	 * Gibt die ID des Projekts zurück.
	 * 
	 * @return ID des Projekts
	 */
	protected String getProjectID() {
		return projectID;
	}

	/**
	 * Setzt die ID des Projektes.
	 * 
	 * @param projectID des Projektes
	 */
	protected void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	/**
	 * Liefert zu einem gegebenen Hash-Wert den passenden Client,
	 * der in diesem Projekt ist. Wenn es keinen solchen gibt, ist das Ergebnis
	 * null.
	 * 
	 * @param hash Hash-Wert zu einem Client.
	 * @return ClientInfo Das zum Hash-Wert passende ClientInfo-Objekt.
	 */
	public ClientInfo get_client_by_hash(String hash) {
		return (ClientInfo)clients.get(hash);
	}
	
	/**
	 * Setzt die maximale Teilnehmerzahl
	 * @param max maximale Anzahl an Teilnehmern
	 */
	public void setMaxUsers(int max) {
		max_users = max;
	}
	
	/**
	 * Liefert die maximale Anzahl an Teilnehmern
	 * @return maximale Anzahl an Teilnehmern
	 */
	public int getMaxUsers() {
		return max_users;
	}	

	/**
	 * Setzt den Eigentümer des Projektes
	 * @param owner Eigentümer
	 */
	public void setOwner(ClientInfo owner) {
		this.owner = owner;
	}
	
	/**
	 * Liefert den Eigentümer des Projektes
	 * @return Eigentümer
	 */
	public ClientInfo getOwner() {
		return owner;
	}
	
	/**
	 * Setzt das Thema des Projektes
	 * @param tpc Thema
	 */
	public void setTopic(String tpc) {
		topic = tpc;
	}
	
	/**
	 * Liefert das Thema des Projektes
	 * @return Thema
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Setzt die Beschreibung des Projektes
	 * @param description Beschreibung
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Liefert die Beschreibung des Projektes
	 * @return Beschreibung
	 */
	public String getDescription() {
		return description;
	}	
}
