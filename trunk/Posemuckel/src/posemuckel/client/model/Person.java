/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

/**
 * Eine Person kann jede Person sein, die in Posemuckel als User registriert ist
 * oder die sich gerade registriert. Es wird also angenommen, dass eine Person
 * irgendwo in der Database existiert. Deshalb ist eine Person eindeutig durch
 * ihren Benutzernamen identifizierbar. Innerhalb eines Models darf es eine Person
 * nur einmal geben.
 * 
 * @author Posemuckel Team
 * @see posemuckel.client.model.User
 * @see posemuckel.client.model.PersonsData
 *
 */
public class Person {

	/**
	 * Zeigt an, dass eine Person online ist. Dieses Flag ist nur f&uuml;r 
	 * Buddys relevant.
	 */
	public static String ONLINE = "ONLINE";
	
	/**
	 * Zeigt an, dass eine Person offline ist. Dieses Flag ist nur f&uuml;r
	 * Buddys relevant.
	 */
	public static String OFFLINE = "OFFLINE";
	
	/**
	 * Zeigt an, dass der Online-Status einer Person nicht bekannt ist. Dieses Flag
	 * gilt f&uuml;r alle Personen, die keine Buddys sind. 
	 */
	public static String UNKNOWN = "UNKNOWN";
	
	/**
	 * Zeigt an, dass die Person das gleiche Projekt ge&ouml;ffnet hat wie der
	 * User.
	 */
	public static String BROWSING = "BROWSING";
	
	private String nickname;
	private String state;
	private String oldState;
	private boolean buddy;
	private PersonsData data;
	//wird nur für die Projektmitglieder im Status BROWSING benötigt
	//enthält die zuletzt registrierte URL
	private String url;
	//alle MemberLists, in denen die Person enthalten ist
	private ArrayList<MemberList> clubs;
	
	
	/**
	 * Erstellt eine neue Person mit dem angegebenen Benutzernamen und dem
	 * angegebenen Online-Status.
	 * 
	 * @param nickname Benutzername der Person
	 * @param state Online-Status der Person
	 */
	public Person(String nickname, String state) {
		this.nickname = nickname;
		this.state = state;
		buddy = !state.equals(Person.UNKNOWN);
		clubs = new ArrayList<MemberList>();
	}
	
	/**
	 * Erstellt eine neue Person mit dem angegebenen Benutzernamen, deren 
	 * Online-Status nicht bekannt ist.
	 * 
	 * @param nickname Benutzername der Person
	 */
	public Person(String nickname) {
		this(nickname, Person.UNKNOWN);
	}
	
	/**
	 * Setzt die personenbezogenen Daten. In der Regel sind die personenbezogenen
	 * Daten im Model nicht bekannt und m&uuml;ssen von der <code>Database</code>
	 * geladen werden.
	 * 
	 * @param firstname Vorname
	 * @param surname Nachname
	 * @param email Email-Adresse
	 * @param comment Kommentar
	 * @param lang Sprache
	 * @param gender Geschlecht
	 * @param location Wohnort
	 */
	public void setData(String firstname, String surname, String email, String comment, String lang, String gender, String location) {
		data = new PersonsData();
		data.setLocation(location);
		data.setComment(comment);
		data.setEmail(email);
		data.setFirstName(firstname);
		data.setGender(gender);
		data.setLang(lang);
		data.setSurname(surname);
		data.setNickname(this.nickname);
	}
	
	/**
	 * Gibt die personenbezogenen Daten in einem <code>PersonsData</code>-Objekt
	 * aus. 
	 * 
	 * @see Person#setData
	 * @return die personenbezogenen Daten
	 */
	public PersonsData getData() {
		if(data == null){
			data = new PersonsData();
			data.setNickname(nickname);
		}
		return data;
	}
	
	/**
	 * Gibt den Benutzernamen zur&uuml;ck.
	 * 
	 * @return Gibt den Benutzernamen zur&uuml;ck.
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Setzt den Benutzernamen
	 * 
	 * @param name Setzt den Benutzernamen
	 */
	protected void setNickname(String name) {
		this.nickname = name;
	}
	
	/**
	 * Setzt die URL der Webseite, die die Person zuletzt besucht hat. Diese Methode
	 * ist nur für Projektmitglieder von Bedeutung.
	 * @param url der Webseite, die die Person zuletzt besucht hat
	 */
	protected void setURL(String url) {
		this.url = url;
	}
	
	/**
	 * Gibt die URL der Webseite, die die Person zuletzt besucht hat, aus.
	 * Diese Methode ist nur für Projektmitglieder von Bedeutung.
	 * @return URL der zuletzt besuchten Webseite
	 */
	public String getURL() {
		return url;
	}
	
	/**
	 * Gibt den Online-Status der Person aus.
	 * 
	 * @return Online-Status der Person 
	 */
	public String getState() {
		return state;
	}
	
	/**
	 * Setzt den Online-Status der Person.
	 * 
	 * @param state Online-Status der Person
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * Setzt den Zustand dieser Person auf Browsing. Der Zustand Browsing kann nur 
	 * von Projektmitgliedern, die im gleichen Projekt wie der User aktiv sind, 
	 * eingenommen werden.
	 * @param b true, wenn der Anwender am browsen ist
	 */
	protected void browsing(boolean b) {
		if(b && (!getState().equals(BROWSING))) {
			//zum Status browsing wechseln
			//der alte Zustand muss gemerkt werden, weil wir sonst nicht zwischen
			//ONLINE und UNKNOWN unterscheiden können
			oldState = state;
			notifyOfStateChange(BROWSING);
		} else if ((!b) && getState().equals(BROWSING)){
			notifyOfStateChange(oldState);
		}
	}
	
	/**
	 * Informiert die Instanz, dass sich der Onlinestatus der Person geändert 
	 * hat. Es werden alle Mitgliederlisten, zu denen diese Person geh&ouml;rt,
	 * &uuml;ber das Update informiert. 
	 * 
	 * <br/>Die Mitgliederlisten informieren ihrerseits
	 * ihre Listener &uuml;ber die &Auml;nderung.
	 * Diese Methode sollte nur von Database aus aufgerufen werden!
	 *  
	 * @param state der neue Zustand
	 */
	protected void notifyOfStateChange(String state) {
		setState(state);
		notifyClubsAboutChange();
	}
	
	/**
	 * Ersetzt die personenbezogenen Daten dieser Person durch die Daten des
	 * Argumentes. 
	 * @param data die neuen Daten
	 */
	protected void updateProfile(PersonsData data) {
		this.data = data;
		data.setNickname(nickname);
	}
	
	/**
	 * Teilt der <code>Person</code> mit, dass sie einem Club beigetreten ist.
	 * Ein Club ist eine Instanz einer <code>MemberList</code>.
	 * 
	 * @param club der neue Club
	 */
	protected void joinClub(MemberList club) {
		this.clubs.add(club);
		if(club.getType().equals(MemberList.BUDDY_TYPE)) {
			buddy = true;
		}
		this.clubs.trimToSize();
	}
	
	/**
	 * Teilt der <code>Person</code> mit, dass sie aus einem Club ausgetreten ist.
	 * Ein Club ist eine Instanz einer <code>MemberList</code>.
	 * 
	 * @param club der Club
	 */
	protected void leaveClub(MemberList club) {
		this.clubs.remove(club);
		if(club.getType().equals(MemberList.BUDDY_TYPE)) {
			buddy = false;
		}
		this.clubs.trimToSize();
	}
	
	/**
	 * Teilt allen Clubs, in denen die Person Mitglied ist, mit, dass sich der
	 * Zustand der Person ge&auml;ndert hat.
	 *
	 */
	protected void notifyClubsAboutChange() {
		for (MemberList club : clubs) {
			club.confirmMemberChanged(this);
		}
	}

	/**
	 * Gibt den Benutzernamen der Person, den Online-Status sowie die 
	 * personenbezogenen Daten in einem String aus.
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Person:\t" + getNickname());
		buffer.append("\nstate:\t" + getState());
		if(data != null) {
			buffer.append("\nnickname:\t" + data.getNickname());
			buffer.append("\nfirst nickname:\t" + data.getFirstName());
			buffer.append("\nsurname:\t" + data.getSurname());
			buffer.append("\nemail:\t" + data.getEmail());
			buffer.append("\nlocation:\t" + data.getLocation());
			buffer.append("\ncomment:\t" + data.getComment());
			buffer.append("\ngender:\t" + data.getGender());
			buffer.append("\nlanguage:\t" + data.getLang());
		}
		return buffer.toString();
	}
	
	/**
	 * Gibt an, ob es sich bei dieser Person um einen Buddy des Users handelt.
	 * @return true, falls dieser Anwender ein Buddy des Users ist.
	 */
	public boolean isBuddy() {
		return buddy;
	}

}
