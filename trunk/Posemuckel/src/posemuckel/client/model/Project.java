/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

import posemuckel.client.model.event.ListenerManagment;
import posemuckel.client.model.event.NotifyEvent;
import posemuckel.client.model.event.NotifyListener;


/**
 * Die Klasse modeliert &ouml;ffentliche und private Projekte. Für jedes Projekt
 * wird eine Liste der aktuellen Mitglieder und der wichtigsten Daten
 * eines Projektes verwaltet. Der Anwender kann
 * einem Projekt beitreten bzw. es verlassen. Wenn das Projekt vom Anwender geöffnet wurde, 
 * stehen Funktionen zur Benachrichtigung ("Hochhalten") 
 * der anderen Projektmitglieder, zur Verwaltung der aktuellen URL und ein Zugriff
 * auf den Webtrace des Projektes zur Verfügung.
 * 
 * @author Posemuckel Team
 *
 */
public class Project {
	//TODO den Teil zum geöffneten Projekt in eine separate Klasse verschieben?
	public static final String PUBLIC_TYPE = "1";
	public static final String PRIVATE_TYPE = "0";
	
	public static final String TOPIC = "topic";
	public static final String OWNER = "owner";
	public static final String TYPE = "type";
	public static final String DATE = "date";
	public static final String FREE = "freeSeats";
	public static final String NO = "number"; 
	
	private String id;
	private String topic;
	private String description;
	private String owner;
	private String ispublic;
	private MemberList members;
	private Model model;
	private String freeSeats;
	private String seats;
	private String chatID;
	private String date;
	private boolean open;
	
	/**
	 * nur für geöffnete Projekte relevant
	 */
	private OpenProjectExtension openExt;
	
	private ListenerManagment<NotifyListener> listenerManagment;
	
	/**
	 * Erstellt ein neues Projekt. Diese Methode sollte nur verwendet werden, 
	 * wenn eine Referenz auf das zugeh&ouml;rige Model vorhanden ist.
	 * 
	 * @param model das Model
	 */
	public Project(Model model) {
		this.model = model;
		owner = "nobody";
		listenerManagment = new ListenerManagment<NotifyListener>();
	}
	
	/**
	 * Erstellt ein neues Projekt. Da kein Model angegeben wird, muss dieses &uuml;ber
	 * den Setter sp&auml;ter gesetzt werden.
	 *
	 */
	public Project() {
		this(null);
	}

	/**
	 * Holt die Daten für die Natifikation.
	 * @return Die Notifikationsdaten als String-Array.
	 */
	public String[] getNotify() {
		return openExt.getNotify();
	}
	
	/**
	 * Teilt dem Projekt mit, zu welchem Model es geh&ouml;rt. Diese Methode
	 * wird beim Laden des Projektes von der Datenbank verwendet, da 
	 * die Datenbank selber das Model nicht kennt.
	 * 
	 * @param model das Model
	 */
	protected void setModel(Model model) {
		this.model = model;
	}
	
	/**
	 * Gibt den Webtrace des Projektes aus. Idealerweise sollte nur f&uuml;r 
	 * das geöffnete Projekt ein Webtrace vorhanden sein.
	 * 
	 * @return der Webtrace des Projektes
	 */
	public Webtrace getWebtrace() {
		return openExt.getWebtrace();
	}
	
	/**
	 * Gibt den FolderTree des Projektes aus. Es ist nur für ein geöffnetes 
	 * Projekt ein FolderTree vorhanden.
	 * @return der FolderTree des Projektes
	 */
	public FolderTree getFolderTree() {
		return getWebtrace().getFolderTree();
	}
	
	/**
	 * Legt die Daten des Projektes fest. Diese Methode wird von der
	 * Datenbank verwendet, wenn das Projekt geladen wird.
	 * 
	 * @param id ID des Projektes
	 * @param topic Thema
	 * @param owner Besitzer
	 * @param isPublic ist das Projekt &ouml;ffentlich?
	 * @param freeSeats Zahl der freien Pl&auml;tze
	 * @param spaces Zahl der Pl&auml;tze
	 * @param description Beschreibung
	 */
	public void setData(String id, String topic, String owner, 
			String isPublic, String freeSeats, String spaces, String description, String date) {
		this.id = id;
		this.topic = topic;
		this.owner = owner;
		this.freeSeats = freeSeats;
		this.seats = spaces;
		this.description = description;
		this.ispublic = isPublic;
		this.date = date;
	}
	
	/**
	 * Gibt die ID des Projektchats zur&uuml;ck. Wenn das Projekt nicht ge&ouml;ffnet
	 * ist, wird <code>null</code> ausgegeben.
	 * 
	 * @return ID des Projektchat oder <code>null</code>
	 */
	public String getChatID() {
		if(!isOpen()) return null;
		return chatID;
	}
	
	/**
	 * Gibt an, ob das Projekt ge&ouml;ffnet ist.
	 * 
	 * @return true, falls das Projekt ge&ouml;ffnet ist
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Dies ist eine Kopie der obigen Methode, wobei lediglich der Parameter
	 * zur Übergabe der ID fehlt. Diese ist n&auml;mlich beim Neuanlegen eines Projektes
	 * noch nicht bekannt.
	 * 
	 * @param topic Thema
	 * @param owner Besitzer
	 * @param isPublic ist das Projekt &ouml;ffentlich?
	 * @param freeSeats Zahl der freien Pl&auml;tze
	 * @param spaces Zahl der Pl&auml;tze
	 * @param description Beschreibung
	 */
	public void setData(String topic, String owner, String isPublic, 
			String freeSeats, String spaces, String description) {
		this.topic = topic;
		this.owner = owner;
		this.freeSeats = freeSeats;
		this.seats = spaces;
		this.description = description;
		this.ispublic = isPublic;		
	}
	
	/**
	 * Setzt die maximale Teilnehmerzahl.
	 * 
	 * @param max maximale Teilnehmerzahl
	 */
	public void setMaxNumber(int max) {
		this.seats = String.valueOf(max);
	}
	
	/**
	 * Gibt den Besitzer des Projektes an.
	 * 
	 * @return Besitzer des Projektes
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * f&uuml;gt dem Projekt ein Mitglied hinzu; wird zur Konstruktion des Projektes ben&ouml;tigt
	 * @param nickname
	 * @throws IllegalArgumentException wenn keine Person mit dem Nickname im Model bekannt ist
	 */
	public void addMember(String nickname) throws IllegalArgumentException {
		getMemberList().addMemberToClub(nickname);
	}
	
	/**
	 * f&uuml;gt dem Projekt eine Mitgliederliste hinzu; wird zur Konstruktion des Projektes ben&ouml;tigt
	 * @param nicknames
	 * @throws IllegalArgumentException wenn ein unbekannter Nickname in der Liste bekannt ist
	 */
	public void addMembers(String[] nicknames) throws IllegalArgumentException {
		for (String name : nicknames) {
			addMember(name);
		}
	}
	
	/**
	 * Gibt die Mitgliederliste des Projektes zur&uuml;ck.
	 * @return Mitgliederliste
	 */
	public MemberList getMemberList() {
		if(members == null) {
			members = new MemberList(true, MemberList.PROJECT, model);
			members.setProject(this);
		}
		return members;
	}
	
	/**
	 * Gibt die Benutzernamen aller Anwender des Projektes zur&uuml;ck.
	 * @return Benutzernamen der Projektmitglieder
	 */
	public String[] getMembers() {
		return getMemberList().getNicknames();
	}
	
	/**
	 * Gibt die Beschreibung des Projektes zur&uuml;ck.
	 * @return die Beschreibung des Projektes
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gibt die Zahl der Pl&auml;tze im Projekt an.
	 * 
	 * @return Zahl der Pl&auml;tze
	 */
	public String getMaxNumber() {
		return seats;
	}

	/**
	 * Gibt die ID des Projektes an. Die ID ist nur dann vergeben, wenn
	 * das Projekt von der Datenbank geladen wurde.
	 * 
	 * @return die ID des Projektes, falls vorhanden; ansonsten null
	 */
	public String getID() {
		return id;
	}

	/**
	 * Gibt das Thema des Projektes an.
	 * @return Thema des Projektes
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Gibt an, ob dieses Projekt &ouml;ffentlich ist.
	 * 
	 * @return true, falls das Projekt &ouml;ffentlich ist
	 */
	public boolean isPublic() {
		return ispublic.equals(Project.PUBLIC_TYPE);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "project no. " + getID() + " about " + getTopic();
	}
	
	/**
	 * Der User des Models tritt dem Projekt bei. Es wird eine entsprechende
	 * Nachricht an die Datenbank geschickt.
	 *
	 */
	public void join() {
		if(model.getUser().getProjects().getProject(this.getID()) == null) {
			new ProjectTask(model.getUser().getProjects(), this).execute(ProjectTask.JOIN);
		}
	}
	
	/**
	 * Der User des Models lehnt die Einladung ab. Es wird eine entsprechende
	 * Nachricht an die Datenbank geschickt.
	 *
	 */
	public void rejectInvitation() {
		if(model.getUser().getInvitations().getProject(getID()) != null) {
			//nur ausführen, wenn der Anwender auch eingeladen ist
			new ProjectTask(model.getUser().getInvitations(), this).execute(ProjectTask.DONT_JOIN);
		}
	}
	
	/**
	 * Der User des Models verl&auml;&szlig;t das Projekt. Es wird eine entsprechende 
	 * Nachricht an die Datenbank geschickt.
	 *
	 */
	public void leave() {
		new ProjectTask(model.getUser().getProjects(), this).execute(ProjectTask.LEAVE);
	}
	
	/**
	 * Der User öffnet das Projekt. Es wird eine entsprechende Nachricht an die
	 * Database geschickt.
	 *
	 */
	public void open() {
		new ProjectTask(model.getUser().getProjects(), this).execute(ProjectTask.OPEN);
	}
	
	/**
	 * Gibt die Anzahl der noch freien Pl&auml;tze in dem Projekt aus.
	 * 
	 * @return Zahl der freien Pl&auml;tze
	 */
	public String getFreeSeats() {
		return freeSeats;
	}
	
	/**
	 * &Auml;ndert die Zahl der freien Pl&auml;tze im Projekt.
	 * @param change Anstieg oder Verringerung der Zahl der freien Plätze
	 */
	protected void changeFreeSeats(int change) {
		int oldValue = new Integer(freeSeats).intValue();
		oldValue = oldValue + change;
		freeSeats = String.valueOf(oldValue);
	}
	
	/**
	 * Teilt dem Projekt mit, dass es ge&ouml;ffnet wurde.
	 * @param chatID des Projektes
	 */
	void setOpen(String chatID) {
		open = true;
		openExt = new OpenProjectExtension(this);
		this.chatID = chatID;
	}
	
	/**
	 * Teilt dem Projekt mit, dass es wieder geschlossen wurde.
	 *
	 */
	void resetOpen() {
		open = false;
	}
	
	/**
	 * Gibt das Erstellungsdatum des Projektes aus.
	 * @return Erstellungsdatum
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * Gibt das Model des Projektes aus.
	 * @return das Model des Projektes
	 */
	public Model getModel() {
		return this.model;		
	}
	
	/**
	 * Setzt die URL, die der Anwender gerade besucht.
	 * @param url , die der Anwender gerade besucht
	 */
	public void setCurrentURL(String url){
		openExt.setCurrentURL(url);
	}
	
	/**
	 * Setzt die URL, die der Anwender vor der aktuellen URL besucht hat.
	 * @param url 
	 */
	public void setPreviousURL(String url){
		openExt.setPreviousURL(url);
	}
	
	/**
	 * Teilt dem Projekt mit, dass der Browser vom Anwender gezwungen wird, zu einer 
	 * URL zu springen. Das kann durch Setzen einer URL in der Adresszeile, über den
	 * Webtrace oder durch andere Buttons geschehen.
	 * @param url , zu der gesprungen wird
	 */
	public void jumpToURL(String url) {
		openExt.jumpToURL(url);
	}
	
	/**
	 * Gibt die zuletzt besuchte URL aus.
	 * @return zuletzt besuchte URL
	 */
	public String getCurrentURL(){
		return openExt.getCurrentURL();
	}
	
	/**
	 * Gibt die vorletzte besuchte URL von diesem Projekt aus.
	 * @return vorletzte besuchte URL
	 */
	public String getPreviousURL(){
		return openExt.getPreviousURL();
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.client.model.OpenProjectExtension#getUrlTitle()
	 */
	public String getUrlTitle() {
		return openExt.getUrlTitle();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.OpenProjectExtension#setUrlTitle(java.lang.String)
	 */
	public void setUrlTitle(String title) {
		openExt.setUrlTitle(title);
	}

	/**
	 * Sendet eine Visiting- oder eine Viewing-Nachricht an die Database.
	 *
	 */
	public void visiting() {
		openExt.visiting();
	}
	
	/**
	 * Teilt dem Projekt mit, dass nach Möglichkeit eine Viewing-Nachricht anstelle
	 * einer Visiting-Nachricht verwendet werden soll, wenn die nächste URL an
	 * die Database versendet werden soll. Es ist nicht möglich, eine
	 * Viewing-Nachricht zu verwenden, wenn der Anwender die URL noch nicht
	 * besucht hat.
	 *
	 */
	public void useViewing() {
		openExt.useViewing();
	}

	public void notify(String url, String title, String comment, String datalen, String imagedata, String[] user) {
		openExt.notify(url, title, comment, datalen, imagedata, user);
	}
	
	/**
	 * Informiert die Listener, dass eine Notify-Nachricht von der Database an die
	 * Empfänger weitergeleitet wurde.
	 *
	 */
	protected void fireNotifyConfirmation() {
		openExt.fireNotifyConfirmation();
	}
	
	/**
	 * Informiert die Listener, dass der Anwender eine neue URL angeklickt hat.
	 * @param url die neue URL
	 */
	public void fireNewURL(String url) {
		openExt.fireNewURL(url);
	}
	
	/**
	 * Informiert die Listener über ein neues NotifyEvent.
	 * @param event das NotifyEvent
	 */
	public void fireNotifyEvent(NotifyEvent event) {
		event.fireNotifyEvent(this);
	}
	
	/**
	 * Gibt den FollowMeManager des Projektes aus. Der FollowMeManager sollte nur 
	 * für geöffnete Projekte verwendet werden.
	 * @return FollowMeManager
	 */
	public FollowMeManager getFollowMeManager()  {
		return openExt.getFollowMeManager();
	}
	
	/**
	 * Registriert einen NotifyListener, der über Ereignisse, die diese Instanz
	 * betreffen, informiert werden möchte. 
	 * @param listener der zu registrierende NotifyListener
	 */
	public void addListener(NotifyListener listener) {
		listenerManagment.addListener(listener);
	}

	/**
	 * Gibt eine Kopie der Liste mit allen NotifyListenern aus.
	 * @return Kopie der Liste mit allen NotifyListenern
	 */
	public ArrayList<NotifyListener> getListener() {
		return listenerManagment.getListener();
	}

	/**
	 * Entfernt den NotifyListener aus der Liste der NotifyListener.
	 * @param listener der zu entfernende NotifyListener
	 */
	public void removeListener(NotifyListener listener) {
		listenerManagment.removeListener(listener);
	}
	
}
