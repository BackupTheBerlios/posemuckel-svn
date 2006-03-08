/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

/**
 * Regelt die Zugriffe auf die Database, die einen Bezug zu den Anwenderlisten haben.
 * Konkret werden die folgenden Nachrichten (nach RFC0815) bearbeitet:
 * 
 * <ul>
 * <li>SEARCH_USERS</li>
 * <li>GET_PROFILE</li>
 * </ul>
 * 
 * Außerdem kann eine Liste mit allen Anwendern, die in der Database gespeichert
 * sind, geladen werden.
 * 
 * @author Posemuckel Team
 *
 */
class MemberTask extends TaskAdapter{
	
	static final int LOAD_ALL = 400;

	static final int GET_PROFILE = 500;

	static final int SEARCH = 600;
	
	private MemberList list;
	private String[] nicknames;
	private int task;

	private PersonsData person;

	private String text;
	
	/**
	 * Diese MemberTask kann alle Anwender von der Database laden.
	 * @param list , die die Antwort erwartet
	 */
	MemberTask(MemberList list) {
		this.list = list;
	}
	
	/**
	 * Diese MemberTask kann das Profil von mehreren Anwendern aus der Database laden.
	 * @param list , die die Antwort erwartet
	 * @param nicknames der Anwender, deren Profile geladen werden soll
	 */
	MemberTask(MemberList list, String[] nicknames) {
		this(list);
		this.nicknames = nicknames;
	}
	
	/**
	 * Diese MemberTask kann nach Anwendern in der Database suchen.
	 * @param pool , der die Antwort erwartet
	 * @param data , nach denen gesucht werden soll
	 * @param text Freitext für die Suche
	 */
	MemberTask(UsersPool pool, PersonsData data, String text) {
		this(pool);
		this.person = data;
		this.text = text;
	}
	
	/**
	 * Gibt die Liste, die die Antwort erwartet, aus.
	 * @return Liste für die Antworten
	 */
	protected MemberList getList() {
		return list;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#work(int)
	 */
	@Override
	protected void work(int task) {
		this.task = task;
		switch (task) {
		case LOAD_ALL:
			DatabaseFactory.getRegistry().getAllUsers(this);
			break;
		case GET_PROFILE:
			DatabaseFactory.getRegistry().getProfile(nicknames, this);
			break;
		case SEARCH:
			DatabaseFactory.getRegistry().search(person, text, this);
		default:
			break;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(int)
	 */
	@Override
	public void update(int answer) {
		switch (answer) {
		case Database.ACCESS_DENIED:
			break;
		case Database.ERROR:
			break;
		default:
			break;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(java.util.ArrayList)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(ArrayList answer) {
		switch (task) {
		case LOAD_ALL:
			getList().confirmLoad(answer);	
			break;
		case GET_PROFILE:
			getList().confirmLoadPersonsData(answer);	
			break;
		case SEARCH:
			((UsersPool)getList()).searchResults(answer);
			break;
		default:
			break;
		}	
	}
}
