package posemuckel.server;

import java.net.*;
import java.util.Vector;

import posemuckel.common.ClientHash;

/**
 * Diese Klasse enthält alle Informationen zu einem Client.
 * Dazu gehört der Socket, der Nickname des Benutzers, der Client-Hash,
 * der Online-Status und die aktuellen Projekte des Benutzers sowie
 * das Projekt, an dem aktuell gearbeitet wird. Zusätzlich wird noch
 * die aktuelle URL verwaltet, die der Benutzer gerade besucht.
 */
public class ClientInfo {
	
	/**
	 * Socket des Clients
	 */
	private Socket socket;
	
	/**
	 * Benutzername (Nickname) des Clients
	 */
	private String userName;
	
	/**
	 * Nachname des Benutzers
	 */
	private String name;
	
	/**
	 * Vorname des Benutzers
	 */
	private String surname;
	
	/**
	 * E-Mailadresse des Benutzers
	 */
	private String email;
	
	/**
	 * gewünschte Sprache des Benutzers
	 */
	private String lang;
	
	/**
	 * identifizierender Hash des Benutzers
	 */
	private String hash;
	
	/**
	 * das aktuelle Projekt des Benutzers
	 */
	private ProjectInfo curr_proj;
	
	/**
	 * die aktuell besuchte URL des Benutzers
	 */
	private String curr_url;
	
	/**
	 * Projekte, an denen der Benutzer teilnimmt
	 */
	private Vector<ProjectInfo> projects;
	
	/**
	 * Chats, an denen der Benutzer teilnimmt
	 */
	private Vector<ChatInfo> chats;
	  
	/**
	 *  Dieser Typ beschreibt den Status eines Clients: 
	 *	ONLINE,	    eingeladen und bestätigt aber offline
	 *	OFFLINE,    eingeladen, bestätigt und online
	 *	UNREACHABLE eingeladen, bestätigt aber irgendwie nicht erreichbar
	 */
	public enum ClientStatus { 
		ONLINE,	 // eingeladen und bestätigt aber offline
		OFFLINE, // eingeladen, bestätigt und online
		UNREACHABLE }; // eingeladen, bestätigt aber irgendwie nicht erreichbar

	/**
	 * Status des Benutzers
	 */
	private ClientStatus status;
	
	/**
	 * Konstruktor für einen bestimmten Socket und einen bestimmten
	 * Benutzernamen
	 * @param s Socket
	 * @param u Benutzername
	 */
	public ClientInfo(Socket s, String u) {
	    socket = s;
	    userName = u;
	    projects = new Vector<ProjectInfo>();
	    chats = new Vector<ChatInfo>();
	    hash = ClientHash.getClientHash(u);
	    status = ClientStatus.OFFLINE;
	}

	/**
	 * Setzt den Status des Benutzers
	 * @param stat neuer Status
	 */
	public void setStatus(ClientStatus stat){
		status = stat;
	}
	
	/**
	 * Fügt den Chats des Benutzers einen neuen hinzu
	 * @param chat der neu hinzuzufügende Chat
	 */
	public void addChat(ChatInfo chat) {
		chats.add(chat);
	}
	
	/**
	 * Entfernt einen Chat vom Benutzer
	 * @param chat Chat, der entfernt werden soll
	 */
	public void removeChat(ChatInfo chat) {
		chats.remove(chat);
	}
	
	/**
	 * Liefert alle Chats, an denen der Benutzer teilnimmt, in einem
	 * Array von ChatInfo-Objekten.
	 * @return Array von ChatInfo-Objekten
	 */
	public ChatInfo[] getChats() {
		ChatInfo[] infos = new ChatInfo[chats.size()];
		return chats.toArray(infos);
	}
	
	/**
	 * Liefert das aktuelle Projekt, an dem der Benutzer teilnimmt
	 * @return das aktuelle Projekt
	 */
	public ProjectInfo getCurrentProject() {
		return curr_proj;
	}
	
	/**
	 * Setzt das aktuelle Projekt des Benutzers
	 * @param project das aktuelle Projekt
	 */
	public void setCurrentProject(ProjectInfo project) {
		curr_proj = project;
	}
	
	/**
	 * Liefert den Status des Benutzers
	 * @return Status des Benutzers
	 */
	public ClientStatus getStatus(){
		return status;
	}

	/**
	 * Fügt ein Projekt dem Benutzer hinzu
	 * @param proj das hinzuzufügende Projekt
	 */
	public void addProject(ProjectInfo proj){
		projects.addElement(proj);
	}
	
	/**
	 * Liefert alle Projekte, an denen der Benutzer teilnimmt, in einem
	 * Vector
	 * @return Vector mit allen Projekten, an denen der Benutzer teil-
	 * nimmt
	 */
	public Vector getProjects(){
		return projects;
	}
	
	/**
	 * Setzt den Hash des Benutzers
	 * @param hash Hash
	 */
	public void setHash(String hash){
		this.hash = hash;
	}
	
	/**
	 * Liefert den Hash des Benutzers
	 * @return Hash als String
	 */
	public String getHash(){
		return hash;
	}

	/**
	 * Setzt den Nachnamen
	 * @param name Nachname
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Liefert den Nachnamen
	 * @return Nachname als String
	 */
	public String getName(){
		return name;
	}

	/**
	 * Setzt den Vornamen des Benutzers
	 * @param name Vorname
	 */
	public void setSurname(String name){
		this.surname = name;
	}
	  
	/**
	 * Liefert den Vornamen des Benutzers
	 * @return Vorname als String
	 */
	public String getSurname(){
		return surname;
	}
	 
	/**
	 * Setzt die e-Mailadresse des Benutzers
	 * @param email e-Mailadresse
	 */
	public void seteMail(String email){
		this.email = email;
	}
	 
	/**
	 * Liefert die e-Mailadresse des Benutzers
	 * @return e-Mailadresse als String
	 */
	public String geteMail(){
		return email;
	}
	  
	/**
	 * Setzt die Sprache des Benutzers
	 * @param lang Sprache(bis jetzt: EN oder DE)
	 */
	public void setLanguage(String lang){
		this.lang = lang;
	}
	  
	/**
	 * Liefert die Sprache des Benutzers
	 * @return Sprache (DE oder EN)
	 */
	public String getLanguage(){
		return lang;
	} 
	
	/**
	 * Setzt den Socket des Benutzers
	 * @param s Socket
	 */
	public void setSocket(Socket s) {
	    this.socket = s;
	}

	/**
	 * Setzt den Benutzernamen
	 * @param u Benutzername
	 */
	public void setUserName(String u) {
	    this.userName = u;
	}

	/**
	 * Liefert den Socket
	 * @return Socket
	 */
	public Socket getSocket() {
	    return this.socket;
	}

	/**
	 * Liefert den Benutzernamen
	 * @return Benutzername
	 */
	public String getUserName() {
	    return this.userName;
	}
	 
	/**
	 * Liefert eine String-Repräsentation des ClientInfo-Objekts
	 */
	public String toString() {
		return hash + " " + userName;
	}
	
	/**
	 * Setzt die aktuelle URL des Benutzers
	 * @param url aktuelle URL
	 */
	public void setCurrentURL(String url) {
		curr_url = url;
	}
	 
	/**
	 * Liefert die aktuelle URL, falls schon eine gesetzt ist, sonst
	 * "unknown"
	 * @return aktuelle URL oder den String "unknown"
	 */
	public String getCurrentURL() {
		if (curr_url == null) {
			return "unknown";
		} else {
			return curr_url;
		}
    }
}