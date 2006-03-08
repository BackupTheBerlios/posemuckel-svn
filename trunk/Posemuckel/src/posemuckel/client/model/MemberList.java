/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

import posemuckel.client.model.event.ListenerManagment;
import posemuckel.client.model.event.MemberListEvent;
import posemuckel.client.model.event.MemberListListener;
import posemuckel.client.model.event.PersonsEvent;

/**
 * <code>MemberList</code> ist wie die Mitgliedsliste eines Clubs. Im Model können 
 * verschiedene Clubs auftauchen:
 * <ul>
 * <li>das Model ist auch ein Club</li>
 * <li>ein Projekt</li>
 * <li>ein Chat</li>
 * <li>die Buddyliste des Anwenders</li>
 * </ul>
 * Allerdings sind noch nicht alle Clubs implementiert.
 * 
 * Wie bei Clubs &uuml;blich, wei&szlig; ein Mitglied, zu welchen Clubs es geh&ouml;rt.
 * Die Aufnahme in den Club bzw. das Verlassen des Clubs k&ouml;nnen &uuml;ber 
 * den Benutzernamen eines Anwenders beantragt werden. Voraussetzung ist allerdings,
 * dass der Benutzername bereits im Model bekannt ist.
 * 
 * @author Posemuckel Team
 *
 */
public class MemberList {
	//TODO fire

	
	/**
	 * ein Liste der Buddys des Users
	 */
	public static final String BUDDY_TYPE = "buddys";
	
	/**
	 * eine Liste mit allen Personen, die von der Datenbank geladen wurden
	 */
	public static final String ALL_USERS_TYPE = "all users";
	
	/**
	 * eine Liste mit den Teilnehmern eines Projektes
	 */
	public static final String PROJECT = "project";
	
	/**
	 * eine Liste mit den Teilnehmern eines Chat
	 */
	public static final String CHAT = "chat";
	
	/**
	 * Enthält die Benutzernamen der Clubmitglieder. Über den 
	 * <code>UsersPool</code> kann auf die zugehörige 
	 * Person zugegriffen werden.
	 *
	 */
	private ArrayList<String> memberKeys;
	
	/**
	 * Lock f&uuml;r die Synchronisation der memberKeys. Es werden nur schreibende
	 * Zugriffe synchronisiert.
	 */
	private Object lock = new Object();
	
	/*
	 * gibt an, ob es den Anwendern erlaubt ist, die Liste zu editieren
	 */
	private boolean editable;
	private String type;
	private Model model;
	private ListenerManagment<MemberListListener> listenerManagment;
	
	/*
	 * das zugeförige Projekt, falls es sich um eine Mitgliederliste für ein Projekt
	 * handelt
	 */
	private Project project;
	
	/**
	 * Erstellt eine leere Mitgliedsliste.
	 * @param editable true, wenn ein Antrag auf Aufnahme in den Club gestellt werden
	 * 			kann
	 * @param type Typ des Clubs
	 * @param model das Model, in dem alle Mitglieder des Clubs bekannt sind
	 */
	protected MemberList(boolean editable, String type, Model model) {
		this.editable = editable;
		this.type = type;
		this.model = model;
		listenerManagment = new ListenerManagment<MemberListListener>();
		initCollections();
	}
	
	/**
	 * Erstellt eine leere Mitgliederliste. Diese Liste kann als Platzhalter
	 * dienen, wenn die tatsächliche Liste noch nicht von der Database geladen
	 * wurde.
	 * @param type der Liste
	 * @param model Model mit Referenz auf alle Personen 
	 * @return leere Mitgliederliste
	 *
	 */
	public static MemberList getDummyList(String type, Model model) {
		return new MemberList(false, type, model);
	}
	
	/**
	 * Initialisiert die Collection, die die Mitglieder speichert. Standardm&auml;ßig
	 * wird eine ArrayList mit den Benutzernamen der Mitglieder verwendet.
	 *
	 */
	protected void initCollections() {
		memberKeys = new ArrayList<String>();
	}
	
	/**
	 * Gibt den Typ der Liste aus.
	 * @return Typ
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Gibt an, ob der Club schon Mitglieder hat.
	 * 
	 * @return true, falls der Club noch keine Mitglieder hat.
	 */
	public boolean isEmpty() {
		return memberKeys.isEmpty();
	}
	
	/**
	 * Gibt die Anzahl der Mitglieder an.
	 * 
	 * @return Anzahl der Clubmitglieder
	 */
	public int size() {
		return memberKeys.size();
	}
	
	/**
	 * F&uuml;gt ein neues Mitglied &uuml;ber die Datenbank in die Liste ein, falls
	 * die Liste editierbar ist.
	 * 
	 * @param name Benutzername des neuen Mitglieds
	 */
	public void addBuddy(String name) {
		//TODO allgemeiner formulieren
		if(editable) {
			new BuddyTask(this, name).execute(BuddyTask.ADD);
		}
	}
	
	/**
	 * Entfernt ein Mitglied &uuml;ber die Datenbank 
	 * aus der Liste, falls die Liste editierbar ist.
	 * @param name Benutzername des Mitglieds, das entfernt werden soll
	 */
	public void deleteMember(String name) {
		if(editable) {
			new BuddyTask(this, name).execute(BuddyTask.DELETE);		
		}
	}
	
	/**
	 * Gibt eine Liste aller Mitglieder des Clubs aus.
	 *
	 * @return alle Mitglieder des Clubs.
	 */
	public ArrayList<Person> getMembers() {
		ArrayList<Person> list = new ArrayList<Person>(memberKeys.size());
		MemberList allUsers = model.getAllPersons();
		for (String key : memberKeys) {
			list.add(allUsers.getMember(key));
		}
		return list;
	}
	
	/**
	 * Gibt an, ob der Club ein bestimmtes Mitglied hat.
	 * @param nickname Benutzername
	 * @return true, falls der Benutzername in der Liste ist
	 */
	public boolean hasMember(String nickname) {
		return getMember(nickname) != null;
	}
	
	/**
	 * Gibt die Benutzernamen aller Clubmitglieder aus.
	 * 
	 * @return die Benutzernamen aller Clubmitglieder
	 */
	public String[] getNicknames() {
		String[] names = new String[memberKeys.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = memberKeys.get(i);
		}
		return names;
	}
	
	/**
	 * Sucht die Person mit dem Benutzernamen aus der Mitgliederliste heraus. 
	 * Wenn der Benutzername in dieser Mitgliedsliste
	 * nicht bekannt ist, wird <code>null</code> zur&uuml;ckgegeben.
	 * 
	 * @param nickname Benutzername der gesuchten Person
	 * @return gesuchte Person
	 */
	public Person getMember(String nickname) {
		Person searched = null;
		if (memberKeys.contains(nickname)) {
			MemberList allUsers = model.getAllPersons();
			searched = allUsers.getMember(nickname);
		}
		return searched; 
	}
	
	/**
	 * L&auml;dt die Mitgliederliste von der Datenbank. <br>
	 * Wenn die Liste zu einem
	 * Chat geh&ouml;rt, muss das Laden &uuml;ber die Instanz des Chat erfolgen.
	 * Ein Aufruf dieser Methode hat in diesem Falle keinen Effekt.
	 *
	 */
	public void load() {
		if(type.equals(BUDDY_TYPE)) {
			new BuddyTask(this).execute(BuddyTask.LOAD);
		} else if (type.equals(PROJECT)){
			new ProjectTask(null, getProject()).execute(ProjectTask.LOAD_PROJECT_MEMBERS);
		}
	}
	
	/**
	 * Wenn der Typ dieser Liste <code>PROJECT</code> ist, wird
	 * das Projekt, auf das sich die Liste bezieht, ausgegeben.
	 * @return Projekt zu der Mitgliederliste
	 */
	private Project getProject() {
		return project;
	}
	
	/**
	 * Wenn der Typ dieser Liste <code>PROJECT</code> ist, wird
	 * das Projekt, auf das sich die Liste bezieht, gesetzt. Diese Angabe
	 * ist für das Laden wichtig!
	 * @param p das Projekt zu der Mitgliederliste
	 */
	protected void setProject(Project p) {
		project = p;
	}
	
	/**
	 * L&auml;d die Daten der angegebenen Personen. Es werden alle Listener dieser
	 * Liste benachrichtigt, wenn die Operation Erfolg hat.
	 *  
	 * @param nicknames Benutzernamen der Personen
	 * @throws IllegalArgumentException wenn einer der Benutzername nicht in dieser Liste
	 * 			vertreten ist
	 */
	public void loadPersonsData(String[] nicknames) {
		for (String name : nicknames) {
			if(!memberKeys.contains(name)) 
				throw new IllegalArgumentException("unknown nickname " + name);
		}
		new MemberTask(this, nicknames).execute(MemberTask.GET_PROFILE);
	}
	
	/**
	 * F&uuml;gt eine Person in die Mitgliederliste ein und teilt der Person mit,
	 * dass sie jetzt Clubmitglied ist. Die Methode sollte nur aufgerufen werden, 
	 * wenn die Person schon in der Datenbank als Mitglied registriert ist.
	 * 
	 * @param nickname Benutzername der Person
	 */
	protected void addMemberToClub(String nickname) {
		//eine Person sollte in einer Liste nur einmal enthalten sein
		if(!memberKeys.contains(nickname)) {
			MemberList allUsers = model.getAllPersons();
			Person person = allUsers.getMember(nickname);
			synchronized (lock) {
				memberKeys.add(nickname);
				//jede Person kennt die Vereine, in denen sie Mitglied ist
				person.joinClub(this);
			}
		}
	}
	
	/**
	 * Entfernt eine <code>Person</code> aus der Mitgliederliste und teilt der
	 * Person mit, dass sie nicht l&auml;nger Mitglied ist. Die Methode sollte 
	 * nur aufgerufen werden, wenn die Person schon in der Datenbank aus der
	 * Mitgliedsliste entfernt wurde.
	 * 
	 * @param nickname Benutzername der Person
	 */
	protected void removeMember(String nickname) {
		if(memberKeys.contains(nickname)) {
			MemberList allUsers = model.getAllPersons();
			Person person = allUsers.getMember(nickname);
			synchronized (lock) {
				memberKeys.remove(nickname);
				//jede Person kennt die Vereine, in denen sie Mitglied ist
				person.leaveClub(this);
			}
		}
	}
	
	/**
	 * Best&auml;tigt das Hinzuf&uuml;gen des Anwenders zu der Liste in
	 * der Datenbank. Die Listener werden &uuml;ber <code>memberAdded</code> 
	 * von dem Ereignis benachrichtigt.
	 * 
	 * @param member das neue Mitglied
	 */
	protected void confirmAddMember(Person member) {
		member = addPerson(member);			
		ArrayList<MemberListListener> listener = getListener();
		for (MemberListListener MemberListListener : listener) {
			MemberListListener.memberAdded(new MemberListEvent(this, true, member));
		}
	}
	
	/**
	 * Best&auml;tigt das Entfernen des Anwenders aus der Liste in
	 * der Datenbank. Die Listener werden &uuml;ber <code>buddyDeleted</code> 
	 * von dem Ereignis benachrichtigt.
	 * 
	 * @param member das neue Mitglied
	 */
	protected void confirmDelMember(String member) {
		removeMember(member);
		MemberList allUsers = model.getAllPersons();
		Person deleted = allUsers.getMember(member);
		if(type.equals(MemberList.BUDDY_TYPE)) {
			deleted.setState(Person.UNKNOWN);
		}
		ArrayList<MemberListListener> listener = getListener();
		for (MemberListListener memberListener : listener) {
			memberListener.buddyDeleted(new MemberListEvent(this, true, deleted));
		}
	}
	
	/**
	 * Best&auml;tigt das Laden der Liste von der Datenbank. Die bereits in der
	 * Liste aufgef&uuml;hrten Personen werden nicht gel&ouml;scht.
	 * Die Listener werden &uuml;ber <code>listLoaded</code> von dem Ereignis
	 * benachrichtigt. Diese Methode kann auch zum Erstellen eines Mockups verwendet
	 * werden.
	 * 
	 * @param memberList Liste mit den neuen Mitgliedern
	 */
	public void confirmLoad(ArrayList<Person> memberList) {
		for (Person member : memberList) {
			addPerson(member);			
		}
		ArrayList<MemberListListener> listener = getListener();
		for (MemberListListener buddyListener : listener) {
			buddyListener.listLoaded(new MemberListEvent(this, true, null));
		}
	}
	
	/**
	 * Best&auml;tigt, dass die komplette Liste erneut von der Datenbank geladen
	 * wurde. Die Listener werden &uuml;ber <code>listLoaded</code> von dem Ereignis
	 * benachrichtigt. Wenn die Liste vom Typ PROJECT ist, dann wird der Status
	 * der Mitglieder ge&auml;ndert. Wenn die Liste vom Typ CHAT ist,
	 * werden die Mitgliedernamen neu gesetzt. Diese Methode sollte nur
	 * aufgerufen werden, wenn sich die ChatMembers geändert haben.
	 * 
	 * @param names Benutzernamen der Anwender, die auf der Liste stehen sollen
	 * 				an der Stelle 0 kann noch eine ID oder ähnliches stehen und diese
	 * 				sollte daher nicht mit ausgewertet werden
	 */
	protected void confirmChatMembersReceived(String[] names) {
		if(type.equals(CHAT)) {
			removeAll();
			for (int i = 1; i < names.length; i++) {
				addPerson(names[i]);
			}
		} else if(type.equals(PROJECT)) {
			ArrayList<Person> members = getMembers();
			//TODO vereinfachen?
			//in eine ArrayList kopieren, damit das Suchen der Namen vereinfacht wird
			ArrayList<String> browsing = new ArrayList<String>(names.length);
			for (String name : names) {
				browsing.add(name);
			}
			for (Person person : members) {
				person.browsing(browsing.contains(person.getNickname())); 
			}
		} else throw new IllegalArgumentException("this method works currently only for CHAT and PROJECT type");
		ArrayList<MemberListListener> listener = getListener();
		for (MemberListListener buddyListener : listener) {
			buddyListener.listLoaded(new MemberListEvent(this, true, null));
		}
	}
	
	/**
	 * Best&auml;tigt das Laden der Personendaten von der Datenbank. Die Listener
	 * der Mitgliederliste werden &uuml;ber das Laden der Daten benachrichtigt.
	 * <br> 
	 * Bei der 
	 * Verarbeitung der Personendaten muss ber&uuml;cksichtigt werden, dass
	 * jede Person nur einmal im Model vorkommen darf.
	 * 
	 * @param persons die Personen mit den neuen Daten
	 */
	protected void confirmLoadPersonsData(ArrayList<PersonsData> persons) {
		MemberList allUsers = model.getAllPersons();
		for (PersonsData data : persons) {
			Person person = allUsers.getMember(data.getNickname());
			person.updateProfile(data);
			//die Listener benachrichtigen
			ArrayList<MemberListListener> listener = getListener();
			for (MemberListListener listListener : listener) {
				listListener.personsDataLoaded(new PersonsEvent(person));
			}
		}
	}
	
	/**
	 * Best&auml;tigt die Ver&auml;nderung der Personendaten. Die &Auml;nderungen
	 * beziehen sich zur Zeit vor allem auf den OnlineStatus. Die Listener
	 * der Mitgliederliste werden &uuml;ber die Ver&auml;nderung der Daten benachrichtigt.
	 * 
	 * @param person die Person mit den neuen Daten
	 */
	protected void confirmMemberChanged(Person person) {
		ArrayList<MemberListListener> listener = getListener();
		for (MemberListListener listListener : listener) {
			listListener.personChanged(new PersonsEvent(person));
		}
	}
	
	/**
	 * Fügt alle Anwender, deren Benutzername im Array enthalten ist, in die
	 * Mitgliederliste ein.
	 * @param names Array mit Benutzernamen
	 */
	protected void addMember(String[] names) {
		for (String name : names) {
			addPerson(name);
		}
	}
	
	
	/**
	 * Die Person wird in die Mitgliedsliste und bei Bedarf auch noch in die
	 * Liste aller bekannten Anwender eingef&uuml;gt. Wenn dort bereits eine
	 * Person mit dem Benutzernamen enthalten ist, wird die Instanz aus dem 
	 * Pool zur&uuml;ckgegeben. Das muss nicht zwingend die gleiche Instanz wie
	 * die als Argument &uuml;bergebene sein!
	 * 
	 * @param person das neue Mitglied
	 * @return die Person, die im Pool des Models enthalten ist
	 */
	private Person addPerson(Person person) {
		Person modelPerson = addPerson(person.getNickname());
		// Logik zum Update des State
		/*
		 * 1.Fall: lokal liegt ein Buddy vor; in dem Fall ist der neueste Status
		 * maßgebend, sofern der Status nicht UNKNOWN ist
		 * 
		 * 2.Fall: lokal liegt eine allgemeine Person vor: in dem Fall darf der
		 * Status überschrieben werden
		 */
		if (!person.getState().equals(Person.UNKNOWN)) {
//			modelPerson.setState(person.getState());
			modelPerson.notifyOfStateChange(person.getState());
		}
		return modelPerson;
	}
	
	/**
	 * F&uuml;gt die Person in die Liste mit allen Anwendern und in diese Instanz
	 * der Mitgliederliste ein. Die Person wird nur dann in die Liste mit allen
	 * Anwendern eingetragen, wenn sie dort noch nicht enthalten ist.
	 * 
	 * Wenn dort bereits eine
	 * Person mit dem Benutzernamen enthalten ist, wird die Instanz aus dem 
	 * Pool zur&uuml;ckgegeben. Das muss nicht zwingend die gleiche Instanz wie
	 * die als Argument &uuml;bergebene sein!
	 * 
	 * @param nickname Benutzername der Person
	 * @return die im Pool enthaltene Instanz der Person 
	 */
	private Person addPerson(String nickname) {
		MemberList allUsers = model.getAllPersons();
		allUsers.addMemberToClub(nickname);
		addMemberToClub(nickname);
		return allUsers.getMember(nickname);
	}
	
	/**
	 * Gibt den Typ der Liste, gefolgt von allen Mitgliedern und der 
	 * Gr&auml;&szlig;e der Liste als String aus.
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("MemberList: " + type);
		ArrayList<Person> list = getMembers();
		for (Person person : list) {
			buffer.append("\n");
			buffer.append(person.toString());
		}
		buffer.append("\nThe list has " + list.size());
		buffer.append(" members.");
		return buffer.toString();
	}
	
	/**
	 * Teilt der Mitgliederliste mit, dass bei einer Operation auf der Datenbank
	 * ein Fehler aufgetreten ist
	 * 
	 * @param code der Aufgabe, die den Fehler ausgelöst hat
	 * @see posemuckel.client.model.MemberTask
	 */
	protected void error(int code) {
		ArrayList<MemberListListener> listener = getListener();
		for (MemberListListener listListener : listener) {
			listListener.error("error");
		}
	}
	
	/**
	 * Entfernt alle Mitglieder aus der Liste und teilt den Mitgliedern mit,
	 * dass sie nicht l&auml;nger Mitglied im Club sind.
	 *
	 */
	protected void removeAll() {
		Object[] members = memberKeys.toArray();
		for (Object name : members) {
			removeMember((String)name);
		}
	}	
	
	/**
	 * Registriert einen MemberListListener, der über Ereignisse, die diese Instanz
	 * betreffen, informiert werden möchte. 
	 * @param listener der zu registrierende MemberListListener
	 */
	public void addListener(MemberListListener listener) {
		listenerManagment.addListener(listener);
	}

	/**
	 * Gibt eine Kopie der Liste mit allen MemberListListenern aus.
	 * @return Kopie der Liste mit allen MemberListListenern
	 */
	public ArrayList<MemberListListener> getListener() {
		return listenerManagment.getListener();
	}

	/**
	 * Entfernt den MemberListListener aus der Liste der MemberListListener.
	 * @param listener der zu entfernende MemberListListener
	 */
	public void removeListener(MemberListListener listener) {
		listenerManagment.removeListener(listener);
	}
}
