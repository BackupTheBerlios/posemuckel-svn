package posemuckel.client.model;

/**
 * Eine <code>Database</code> enth&auml;lt die Informationen, die f&uuml;r die
 * Anwendung von Bedeutung sind. Die Informationen werden in der Database so gespeichert,
 * dass mehrere Anwender darauf zugreifen k&ounml;nnen. <br>
 * Der Anwender wird durch die mitgegebene <code>Task</code> &uuml;ber das Ergebnis der Operation
 * informiert. Welche Antworten gegeben werden, wird im RFC0815 spezifiziert, wobei
 * die Database manche Datensätze schon vorverarbeitet hat.<br>  
 * Manche &Auml;nderungen 
 * machen es notwendig, dass auch andere Anwender informiert werden. In diesem
 * Fall wird ein <code>InformationReceiver</code> eingesetzt, um &uuml;ber die
 * &Auml;nderungen zu informieren. Eine Task erhält in diesem Fall keine Antwort
 * von der Database (außer, es ist im RFC0815 anders spezifiziert).
 * 
 * @author Posemuckel Team
 *
 */
public interface Database {
	
	/**
	 * Die möglichen Antworten der Database. Es werden int im Bereich von
	 * 0 bis 99 verwendet.
	 */
	
	/**
	 * Bestätigt das Einloggen eines Anwenders.
	 */
	public static final int ACCESS_GRANTED = 0;
	
	/**
	 * Diese Antwort wird von der Database immer dann an die Task weitergereicht, 
	 * wenn der Anwender nicht eingeloggt ist. Im Zusammenhang mit einem Login kann
	 * diese Antwort auch bedeuten, dass Passwort und Benutzername nicht zusammenpassen.
	 */
	public static final int ACCESS_DENIED = 1;

	/**
	 * Bestätigt den Erfolg einer Operation, bei der keine Daten zurückgegeben werden.
	 */
	public static final int ACK = 2;
	
	/**
	 * Gibt an, dass ein Anwender bereits existiert. Diese Antwort wird bei
	 * der Registrierung verwendet, da ein Benutzername eindeutig sein muss.
	 */
	public static final int USER_EXITS = 3;
	
	/**
	 * Gibt an, dass die Operation nicht ausgeführt werden konnte.
	 */
	public static final int ERROR = 4;

	/**
	 * Gibt an, ob die ausgelösten Operationen sehr lange dauern können. In diesem Fall wird
	 * die Aufgabe vom Model aus in einem eigenen Thread ausgeführt.
	 * 
	 * @return ob die Operationen sehr lange dauern können
	 */
	public abstract boolean isHeavyTask();
	
	/**
	 * Gibt den String zurück, der den Anwender eindeutig identifiziert.
	 * @return ClientHash
	 */
	public String getClientHash();

	/**
	 * Fügt einen User in die Database ein. Alle Felder bis auf location und
	 * comment sind Pflichtfelder.<br>
	 * Die Database liefert ACK im Erfolgsfalle und USER_EXISTS, falls der Anwender
	 * bereits existiert. Jeder Anwender kann nur einmal in der Datenbank enthalten
	 * sein. Als Schlüssel wird dabei der Anwendername verwendet.
	 * 
	 * @param name
	 *            Vorname des Users
	 * @param pwd
	 *            Passwort des Users
	 * @param surname
	 *            Nachname des Users
	 * @param nickname
	 *            Nickname des Users
	 * @param lang
	 *            Sprache des Users
	 * @param gender
	 *            Geschlecht des Users
	 * @param email
	 *            Email-Adresse des Users
	 * @param location
	 *            Wohnort des Users, es darf auch ein leerer String übergeben
	 *            werden
	 * @param comments
	 *            Kommentare, es darf auch ein leerer String übergeben werden
	 * @param task
	 *            die Task, die die Antwort über einen Erfolg der Operation
	 *            erhält
	 */
	public abstract void addUser(String name, String pwd, String surname,
			String nickname, String lang, String gender, String email,
			String location, String comments, Task task);

	/**
	 * Logt den User mit dem Nickname name ein.<br>
	 * Die Antworten der Database sind ACCESS_GRANTED im Erfolgsfalle und
	 * ACCESS_DENIED, wenn das Passwort falsch ist oder der Anwender bereits
	 * eingeloggt ist.
	 * @param name Nickname des Users
	 * @param pwd Passwort des Users
	 * @param task die Task, die die Antwort über einen Erfolg der Operation erhält
	 */
	public abstract void login(String name, String pwd, Task task);

	/**
	 * Bestätigt wird das Logout mit ACK. Wenn der Anwender bereits ausgeloggt ist, 
	 * wird ein ACCESS_DENIED versendet.
	 * @param name Nickname des Users
	 * @param task die Task, die die Antwort über einen Erfolg der Operation erhält
	 */
	public abstract void logout(String name, Task task);

	/**
	 * F&uuml;gt einen neuen Buddy in die Buddyliste ein. Bei Erfolg wird der 
	 * OnlineStatus des Buddys an die Task weitergeleitet.
	 * 
	 * @param name Nickname des Buddys
	 * @param task die Task, die die Antwort über einen Erfolg der Operation erhält
	 */
	public abstract void addBuddy(String name, Task task);

	/**
	 * Entfernt einen Buddy aus der Buddyliste. Bestätigt wird mit ACK.
	 * @param buddy Nickname des Buddys
	 * @param task die Task, die die Antwort über einen Erfolg der Operation erhält
	 */
	public abstract void deleteBuddy(String buddy, Task task);
	
	/**
	 * L&auml;dt alle Buddys aus der Datenbank. Bei Erfolg wird eine Liste mit
	 * den Buddys an die Task übergeben. Die Buddys enthalten ihren Online-Status.
	 * 
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void getBuddys(Task task);
	
	/**
	 * Ruft alle Anwender mit ihren Profilen von der Datenbank ab. Bei Erfolg
	 * wird eine ArrayList mit allen Personen an die Task übergeben.
	 * 
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void getAllUsers(Task task);
	
	/**
	 * Ruft den gesamten Webtrace von der Datenbank ab. Die Daten werden als eine
	 * ArrayListe, bestehend aus Strings, an die Task übergeben. Die Daten haben die
	 * im RFC0815 beschriebene Reihenfolge.
	 * 
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void getWebtrace(Task task);

	/**
	 * Startet das angegebene Projekt. Die Datenbank gibt auf diese Anforderung
	 * keine Antwort
	 * 
	 * @param project das zu startende Projekt
	 * @param task Der enhtsprechende Task.
	 */
	public abstract void startProject(Project project, Task task);
	
	/**
	 * Ruft die Daten aller Projekte von der Datenbank ab.
	 * 
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void getAllProjects(Task task);
	
	/**
	 * Ruft alle offenen Einladungen von der Datenbank ab.
	 * 
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void getInvitations(Task task);
	
	/**
	 * Ruft die Daten der Projekte, an denen der Anwender beteiligt ist, von 
	 * der Datenbank ab.
	 * 
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void getMyProjects(Task task);
	
	/**
	 * Informiert den <code>InformationReceiver</code> &uuml;ber &Auml;nderungen
	 * der Datenbank, die von anderen Anwendern ausgel&ouml;st wurden
	 * 
	 * @param receiver InformationReceiver
	 */
	public abstract void setReceiver(InformationReceiver receiver);
	
	/**
	 * Teilt der Datenbank mit, dass der Anwender dem Projekt mit der ID projectID
	 * beitreten m&ouml;te.
	 * @param projectID ID des Projektes
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void joinProject(String projectID, Task task);

	/**
	 * Teilt der Datenbank mit, dass der Anwender das Projekt mit der ID projectID
	 * verlassen m&ouml;te.
	 * @param projectID ID des Projektes
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void leaveProject(String projectID, Task task);
	
	/**
	 * Ruft die Profile der Anwender mit den angegebenen Benutzernamen von der
	 * Datenbank ab.
	 * 
	 * @param nicknames Benutzernamen von den Anwendern, deren Profile gefragt sind
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void getProfile(String[] nicknames, Task task);
	
	/**
	 * Ändert das Profil des Anwenders.
	 * 
	 * @param firstname Vorname
	 * @param surname Nachname
	 * @param pwd Passwort
	 * @param email email-Adresse
	 * @param lang Sprache
	 * @param gender Geschlecht
	 * @param location Wohnort
	 * @param comment Kommentar
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void setProfile(String firstname, String surname, String pwd, String email, String lang, String gender, String location, String comment, Task task);
	
	/**
	 * Teilt der Database den verfolgten Link, den der Anwender zum ersten Mal verfolgt, mit.
	 * Der Link wird dann zum ersten Mal verfolgt, wenn die Webseite zum ersten Mal
	 * vom Anwender besucht wird oder wenn der Link das erste Mal von der Webseite, 
	 * die den Link enthält(oldurl), verfolgt wird. 
	 * @param newurl URL, auf die der Link verweist
	 * @param title Titel der Webseite
	 * @param oldurl URL der Webseite, auf der der Link gefunden wurde
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void visiting(String newurl, String title, String oldurl, Task task);
	
	/**
	 * Läd die Teilnehmer eines Chat von der Datenbank.
	 * 
	 * @param id des Chat
	 */
	public abstract void loadChatMembers(String id);
	
	/**
	 * Teilt der Datenbank eine neue Nachricht f&uuml;r den Chat mit.
	 * 
	 * @param id des Chat
	 * @param message die neue Nachricht
	 */
	public abstract void chatting(String id, String message);
	
	/**
	 * Teilt der Datenbank mit, dass der Anwender am tippen ist.
	 * 
	 * @param id des Chat
	 */
	public abstract void typing(String id);
	
	/**
	 * Teilt der Datenbank mit, dass der Anwender am lesen ist.
	 * @param id des Chat
	 */
	public abstract void reading(String id);

	/**
	 * L&auml;dt die Mitglieder einer Projektliste aus der Datenbank.
	 * 
	 * @param id des Projektes
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void getProjectMembers(String id, Task task);
	
	/**
	 * &Ouml;ffnet ein Projekt.
	 * 
	 * @param project das ge&ouml;ffnet werden soll
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void openProject(Project project, Task task);
	
	/**
	 * Teilt der Datenbank mit, dass der Anwender dem Chat beitreten m&ouml;chte.
	 * @param id ID des Chat
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void joinChat(String id, Task task);
	
	/**
	 * Teilt der Datenbank mit, dass ein privater Chat gestartet werden soll.
	 * 
	 * @param userToInvite Anwender, die eingeladen werden
	 */
	public abstract void startChat(String[] userToInvite);
	
	/**
	 * Sucht nach Personen mit den angegebenen Parametern.
	 * 
	 * @param person Datenkontainer
	 * @param text Freitext
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void search(PersonsData person, String text, Task task);
	
	/**
	 * Teilt der Database mit, dass der Anwender die Einladung ablehnt.
	 * 
	 * @param id des Projektes
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void rejectInvitation(String id, Task task);

	
	/**
	 * Teilt der Database mit, dass der Anwender eine URL hochh&auml;lt.
	 * 
	 * @param task Task, die die Antwort erwartet
	 */
	public abstract void notify(Project project, Task task);
	
	/**
	 * Teilt der Database mit, dass der Anwender eine Bewertung f&uuml;r eine
	 * Webseite abgegeben hat.
	 * @param url der Webseite
	 * @param rating die Bewertung als String
	 */
	public abstract void vote(String url, String rating);
	
	/**
	 * Teilt der Database mit, dass der Anwender eine neue Webseite betrachtet.
	 * @param URL der Webseite
	 */
	public abstract void viewing(String URL);
	
	/**
	 * Teilt der Database mit, dass der Anwender eine Notiz zu einer Webseite 
	 * hinzugefügt hat.
	 * @param url die URL der Webseite
	 * @param data das Rating gefolgt von der Notiz; getrennt durch \r\n
	 */
	public abstract void addNote(String url, String data);
	
	/**
	 * L&auml;d die Notizen zu der Webseite von der Datenbank
	 * @param url die URL der Webseite
	 * @param task die Task, die die Antwort entgegennimmt
	 */
	public abstract void getNotes(String url, Task task);
	
	/**
	 * Fügt einen neuen Folder in die Datenbank ein.
	 * @param title des Folders
	 * @param parentID die ParentID oder der leere String
	 */
	public abstract void addFolder(String title, String parentID);
	
	/**
	 * Ändert den Parent des Folders. 
	 * @param folderID des Folders
	 * @param parentID des neuen Parents
	 */
	public abstract void changeParentFolder(String folderID, String parentID);
	
	/**
	 * Löscht den Folder aus der Datenbank.
	 * @param folderID des zu löschenden Folders
	 */
	public abstract void deleteFolder(String folderID);
	
	/**
	 * Ändert den ParentFolder für die URL. Wenn der ParentFolder der leere String
	 * ist, wird die URL aus ihrem alten Folder entfernt.
	 * @param url die URL
	 * @param parentID die ID des neuen ParentFolders oder der leere String
	 */
	public abstract void changeParentFolderForURL(String url, String parentID);
	
	/**
	 * Lädt die gesamte Folderstruktur von der Datenbank.
	 * @param task Task, die die Antwort entgegennimmt
	 */
	public abstract void loadFolderStructure(Task task);


}