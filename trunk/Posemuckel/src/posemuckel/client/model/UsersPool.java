/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import posemuckel.client.model.event.MemberListEvent;
import posemuckel.client.model.event.MemberListListener;
import posemuckel.client.model.event.PersonsEvent;

/**
 * 
 * Der UserPool enth&auml;lt alle Personen, die jemals von der 
 * Database geladen wurden. Der UsersPool kann jede Person nur
 * einmal enthalten, so dass eine Person im gesamten Model des Client
 * <b>eindeutig</b> ist.
 * 
 * @author Posemuckel Team
 *
 */
public class UsersPool extends MemberList {
	
	/**
	 * Diese Map enthält alle Anwender. Als Schlüssel wird der Benutzername verwendet, 
	 * da dieser eindeutig ist.
	 */
	private Map<String,Person> personsPool;
	
	/**
	 * Erstellt einen neuen UserPool. Pro Model darf es nur einen UserPool geben.
	 * 
	 * @param model das Model
	 */
	protected UsersPool(Model model) {
		super(false, MemberList.ALL_USERS_TYPE ,model);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#initCollections()
	 */
	@Override
	protected void initCollections() {
		/* die Anzahl der konkurierenden Zugriffe wird gleich der Anzahl der Threads
		 * im Programm gesetzt. Die anderen Werte sind die Standardwerte einer 
		 * ConcurrentHashMap.
		 */
		personsPool = new ConcurrentHashMap<String, Person>(16, 0.75f, 3);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return personsPool.isEmpty();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#size()
	 */
	@Override
	public int size() {
		return personsPool.size();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#getMember(java.lang.String)
	 */
	@Override
	public Person getMember(String nickname) {
		return personsPool.get(nickname);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#getMembers()
	 */
	@Override
	public ArrayList<Person> getMembers() {
		Collection<Person> persons = personsPool.values();
		ArrayList<Person> list = new ArrayList<Person>(size());
		for (Person person : persons) {
			list.add(person);
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#getNicknames()
	 */
	@Override
	public String[] getNicknames() {
		Collection<Person> persons = personsPool.values();
		String[] list = new String[size()];
		int i = 0;
		for (Person person : persons) {
			list[i] = person.getNickname();
			i++;
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#load()
	 */
	@Override
	public void load() {
		new MemberTask(this).execute(MemberTask.LOAD_ALL);
	}
	
	/**
	 * Sucht in der Database nach Personen mit den angegebenen Daten und dem
	 * Freitext.
	 * PersonsData dient als Datenkontainer f&uuml;r die Parameter.
	 * 
	 * @param person Datenkontainer mit den Suchparametern
	 * @param text Freitext oder leerer String ""
	 */
	public void search(PersonsData person, String text) {
		new MemberTask(this, person, text).execute(MemberTask.SEARCH);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#loadPersonsData(java.lang.String[])
	 */
	@Override
	public void loadPersonsData(String[] nicknames) {
		for (String name : nicknames) {
			if(!personsPool.containsKey(name)) 
				throw new IllegalArgumentException("unknown nickname " + name);
		}
		new MemberTask(this, nicknames).execute(MemberTask.GET_PROFILE);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#addMember(java.lang.String)
	 */
	@Override
	protected void addMemberToClub(String nickname) {
		if(!personsPool.containsKey(nickname)) {
			Person person = new Person(nickname, Person.UNKNOWN);
			personsPool.put(nickname, person);
			person.joinClub(this);
		}
	}
	
	/**
	 * F&uuml;gt eine neue Person in den Pool ein, wenn sie nicht bereits enthalten
	 * ist. 
	 * 
	 * @param member neue Person
	 */
	protected void addMember(Person member) {
		if(!personsPool.containsKey(member.getNickname())) {
			personsPool.put(member.getNickname(), member);
			member.joinClub(this);
		}
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#confirmLoad(java.util.ArrayList)
	 */
	@Override
	public void confirmLoad(ArrayList<Person> memberList) {
		for (Person member : memberList) {
			addMember(member);	
		}
		ArrayList<MemberListListener> listener = getListener();
		for (MemberListListener MemberListListener : listener) {
			MemberListListener.listLoaded(new MemberListEvent(this, true, null));
		}
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.client.model.MemberList#confirmLoadPersonsData(java.util.ArrayList)
	 */
	@Override
	public void confirmLoadPersonsData(ArrayList<PersonsData> persons) {
		for (PersonsData data : persons) {
			Person person = personsPool.get(data.getNickname());
			person.updateProfile(data);
			//die Listener benachrichtigen
			ArrayList<MemberListListener> listener = getListener();
			for (MemberListListener listListener : listener) {
				listListener.personsDataLoaded(new PersonsEvent(person));
			}
		}
	}
	
	/**
	 * Informiert den UserPool &uuml;ber Suchergebnisse aus der Datenbank.
	 * @param memberList ArrayList mit den Personen, die gefunden wurden
	 */
	public void searchResults(ArrayList<Person> memberList) {
		// Personen in die Liste einfügen
		for (Person member : memberList) {
			addMember(member);	
			getMember(member.getNickname()).updateProfile(member.getData());
		}
		//die Listener mit der Personenliste benachrichtigen
		ArrayList<MemberListListener> listener = getListener();
		for (MemberListListener listListener : listener) {
			listListener.searchResults(memberList, this);
		}
	}
	
}
