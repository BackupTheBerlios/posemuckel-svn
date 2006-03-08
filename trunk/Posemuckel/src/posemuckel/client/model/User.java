/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

import posemuckel.client.model.event.ChatEvent;
import posemuckel.client.model.event.ListenerManagment;
import posemuckel.client.model.event.UserEvent;
import posemuckel.client.model.event.UserListener;

/**
 * Der User simuliert den Anwender, der sich im Server einloggen kann. Es stehen
 * Methoden zum Registrieren, Einloggen, Ausloggen und Ändern des eigenen Profils
 * zur Verfügung. Für den User gibt eine Liste mit seinen Buddys sowie zwei Projektlisten:
 * offene Einladungen und Projekte, an denen der User teilnimmt.<br>
 * Der User kann andere Anwender zu einem gemeinsamen Chat einladen.
 * 
 * @author Posemuckel Team
 *
 */
public class User extends Person {
	
	private UserData data;
	private Model model;
	
	private MemberList buddys;
	private ProjectList projects;
	private ProjectList invitations;	
	private ListenerManagment<UserListener> listenerManagment;
	
	/**
	 * Erstellt einen neuen Anwender für den Client.
	 * @param nickname Benutzername
	 * @param model mit dem Pool für alle Anwender
	 */
	public User(String nickname, Model model) {
		super(nickname, Person.OFFLINE);
		this.model = model;
		data = new UserData(this);
		buddys = new MemberList(true, MemberList.BUDDY_TYPE, model);
		projects = new ProjectList(model, ProjectList.MY_PROJECTS);
		invitations = new ProjectList(model, ProjectList.OPEN_INVITATIONS);
		listenerManagment = new ListenerManagment<UserListener>();
	}
	
	/**
	 * Erstellt einen neuen Anwender für den Client ohne Benutzernamen
	 * @param model mit dem Pool für alle Anwender
	 */
	public User(Model model) {
		this("", model);
	}
	
	/**
	 * Gibt an, ob der Anwender eingeloggt ist. Dabei wird angenommen,
	 * dass Benutzer, die gerade im Netz surfen, angemeldet sind.
	 * @return true, falls der Anwender eingeloggt ist
	 */
	public boolean isLoggedIn() {
		if ( getState().equals(Person.ONLINE) || getState().equals(Person.BROWSING) )
			return true;
		else
			return false;
	}
	
	/**
	 * Gibt die Buddyliste des Anwenders aus. Wenn der Anwender nicht eingeloggt 
	 * ist, ist die Buddyliste leer.
	 * @return Buddyliste
	 */
	public MemberList getBuddyList() {		
		return buddys;
	}	
	
	/**
	 * Gibt die Liste mit den Projekten, an denen der Anwender teilnimmt, aus. Wenn
	 * der Anwender nicht eingeloggt ist, ist die Projektliste leer.
	 * @return Liste mit den Projekten, an denen der Anwender teilnimmt
	 */
	public ProjectList getProjects() {
		return projects;
	}
	
	/**
	 * F&uuml;gt den Anwender in den Anwenderpool des Models ein. Der <code>nickname</code>
	 * muss bereits gesetzt sein. Die Operation wird im Moment bei der Best&auml;tigung
	 * des Login durchgef&uuml;hrt.
	 *
	 */
	private void addToPool() {
		model.putUserIntoPool();
	}
	
	/**
	 * Gibt die Liste mit den Projekten, zu denen der Anwender eingeladen ist, aus. 
	 * Es werden nur offene Einladungen aufgeführt. Wenn
	 * der Anwender nicht eingeloggt ist, ist die Projektliste leer.
	 * @return Liste mit den Projekten, zu denen der Anwender eingeladen ist.
	 */
	public ProjectList getInvitations() {
		return invitations;
	}
	
	/**
	 * Führt das Login des Users durch. Der Benutzername muss bereits bekannt sein.
	 * @param pwd Passwort
	 * @throws IllegalArgumentException falls der Anwendername leer ist
	 */
	public void login(String pwd) {
		//es gibt auch eine Methode login(pwd, nickname), bei der der Nickname mit übergeben werden kann
		if(getNickname().equalsIgnoreCase("")) throw new IllegalArgumentException("no valid nickname");
		//passwort wird für diese Session gemerkt
		data.setPassword(pwd);
		login();
	}
	
	/**
	 * Führt das Login des Users durch. Das Passwort und der Benutzername 
	 * müssen bereits bekannt sein.
	 *
	 */
	public void login() {
		new UserTask(this).execute(UserTask.LOGIN);
	}
	
	/**
	 * Führt das Login aus.
	 * @param name Anwendername
	 * @param pwd Passwort
	 */
	public void login(String name, String pwd) {
		setNickname(name);
		login(pwd);
	}
	
	/**
	 * Überprüft, ob das Passwort mit dem gespeicherten Passwort übereinstimmt.
	 * @param pwd Passwort
	 * @return true , falls es mit dem gespeicherten Passwort übereinstimmt
	 */
	public boolean checkPassword(String pwd) {
		return pwd.equals(data.getPassword());
	}
	
	/**
	 * Der Anwender startet einen neuen Chat mit den angegebenen Leuten.
	 * @param nicknames Anwendernamen der Chatteilnehmer
	 */
	public void startChatWith(ArrayList<String> nicknames) {
		nicknames.remove(getNickname());
		String[] names = new String[nicknames.size()];
		new ChatTask(nicknames.toArray(names)).execute(ChatTask.START);
	}
	
	/**
	 * Führt die Registrierung des Anwenders aus.
	 * @param nickname Benutzername
	 * @param pwd Passwort
	 * @param firstname Vorname
	 * @param surname Nachname
	 * @param email Email
	 * @param lang Sprache
	 * @param gender Geschlecht
	 * @param location Ort
	 * @param comment Kommentar
	 */
	public void register(String nickname, String pwd, String firstname,
			String surname, String email, String lang, String gender,
			String location, String comment) {
		data.register(firstname, surname, pwd, nickname, email, lang, gender, location,
				comment);
	}
	
	/**
	 * Ändert die personenbezogenen Daten des Anwenders.
	 * @param pwd Passwort
	 * @param firstname Vorname
	 * @param surname Nachname
	 * @param email email
	 * @param lang Sprachkürzel
	 * @param gender Geschlecht
	 * @param location Ort 
	 * @param comment Kommentar
	 */
	public void setProfile(String pwd, String firstname,
			String surname, String email, String lang, String gender,
			String location, String comment) {
		data.setProfile(firstname, surname, pwd, email, lang, gender, location,
				comment);
	}
	
	/**
	 * Führt das Logout durch.
	 *
	 */
	public void logout() {
		new UserTask(this).execute(UserTask.LOGOUT);
	}
	
	/**
	 * Gibt die personenbezogenen Daten des Anwenders aus.
	 * @return personenbezogene Daten
	 */
	protected UserData getUserData() {
		return data;
	}	
	
	/* (non-Javadoc)
	 * @see posemuckel.client.model.Person#update(posemuckel.client.model.PersonsData)
	 */
	@Override
	public void updateProfile(PersonsData pData) {
		data.setLocation(pData.getLocation());
		data.setComment(pData.getComment());
		data.setEmail(pData.getEmail());
		data.setFirstName(pData.getFirstName());
		data.setGender(pData.getGender());
		data.setLang(pData.getLang());
		data.setSurname(pData.getSurname());
		super.updateProfile(data);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Person#getData()
	 */
	@Override
	public PersonsData getData() {
		return data;
	}
	
	/**
	 * Teilt dem Anwender die Antwort auf den Loginversuch mit.
	 * Die UserListener werden über den Erfolg benachrichtigt.
	 *
	 */
	protected void confirmLogin(boolean succeeded) {
		if(succeeded) {
			setState(Person.ONLINE);
			addToPool();
		} else {
			setState(Person.OFFLINE);
			data.setPassword("");
		}
		fireLogin(succeeded);
	}
			
	/**
	 * Teilt dem Anwender die Antwort auf den Logoutversuch mit.
	 * Die UserListener werden über das Ergebnis benachrichtigt.
	 *
	 */
	protected void confirmLogout(boolean succeeded) {
		if(succeeded) {
			setState(Person.OFFLINE);
		} //sonst Status nicht ändern
		fireLogout(succeeded);
	}
	
	/**
	 * Gibt das Passwort aus.
	 * @return Passwort
	 */
	public String getPassword() {
		return data.getPassword();
	}
	
	/**
	 * Teilt den Listenern den Ausgang der Profiländerung mit.
	 * @param success true, falls das Profil erfolgreich geändert werden konnte
	 */
	protected void fireProfileChanged(boolean success) {
		ArrayList<UserListener> listener = getListener();
		for (UserListener userListener : listener) {
			userListener.profileChanged(new UserEvent(success, this));
		}
	}
	
	/**
	 * Teilt den Listenern den Ausgang eines Logoutversuches mit. 
	 * @param succeeded true, falls das Logout erfolgreich verlief
	 */
	protected void fireLogout(boolean succeeded) {
		ArrayList<UserListener> listener = getListener();
		for (UserListener userListener : listener) {
			userListener.logout(new UserEvent(succeeded, this));
		}
	}
	
	/**
	 * Teilt den Listenern den Ausgang eines Loginversuches mit. 
	 * @param success true, falls das Login erfolgreich verlief
	 */
	protected void fireLogin(boolean success) {
		ArrayList<UserListener> listener = getListener();
		for (UserListener userListener : listener) {
			userListener.login(new UserEvent(success, this));
		}
	}
	
	/**
	 * Teilt den Listenern, dass der Anwender
	 * zu einem Chat eingeladen oder der Owner eines neuen Chat ist.
	 * @param chat der neue Chat
	 * @param isOwner true, wenn der User der Owner ist
	 */
	protected void fireNewChat(Chat chat, boolean isOwner) {
		ArrayList<UserListener> listener = getListener();
		for (UserListener userListener : listener) {
			userListener.newChat(new ChatEvent(chat, isOwner));
		}
	}
	/**
	 * Teilt dem User mit, dass er zu einem Chat eingeladen oder der Owner eines neuen
	 * Chat ist.
	 * @param chat der neue Chat
	 * @param isOwner true, wenn der User der Owner ist
	 */
	protected void informAboutNewChat(Chat chat, boolean isOwner) {
		fireNewChat(chat, isOwner);
		chat.join();
	}
	
	/**
	 * Registriert einen UserListener, der über Ereignisse, die diese Instanz
	 * betreffen, informiert werden möchte. 
	 * @param listener der zu registrierende UserListener
	 */
	public void addListener(UserListener listener) {
		listenerManagment.addListener(listener);
	}

	/**
	 * Gibt eine Kopie der Liste mit allen UserListenern aus.
	 * @return Kopie der Liste mit allen UserListenern
	 */
	public ArrayList<UserListener> getListener() {
		return listenerManagment.getListener();
	}

	/**
	 * Entfernt den UserListener aus der Liste der UserListener.
	 * @param listener der zu entfernende UserListener
	 */
	public void removeListener(UserListener listener) {
		listenerManagment.removeListener(listener);
	}
	
}
