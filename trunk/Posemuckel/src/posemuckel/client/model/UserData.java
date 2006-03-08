/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

import posemuckel.client.model.event.UserEvent;
import posemuckel.client.model.event.UserListener;
import posemuckel.common.GetText;

/**
 * UserData enthält die personenbezogenen Daten des Anwenders und die damit
 * verbundenen Methoden <code>register</code> und <code>setProfile</code>.
 * 
 * @author Posemuckel Team
 *
 */
public class UserData extends PersonsData {

	private String password;
	private User user;
	//die Daten, die jede Person hat
	private PersonsData myData;
	
	/**
	 * Datenbehälter mit den Daten des Anwenders.
	 * @param user Referenz auf den Anwender
	 */
	UserData(User user) {
		super();
		password = "";
		this.user = user;
	}
	
	/**
	 * Gibt den Datenbehälter <code>PersonsData</code> für den Anwender aus.
	 * @return PersonsData für den User
	 */
	private PersonsData getPersonsData() {
		if(myData == null) {
			myData = new PersonsData();
		}
		return myData;
	}
	
	/**
	 * das Passwort des Anwenders
	 * @return gibt das Passwort aus
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * der Benutzername des Anwenders
	 * @return gibt den Nickname aus
	 */
	public String getNickname() {
		return user.getNickname();
	}
	
	
	/**
	 * Führt die Registrierung des Anwenders durch. 
	 * 
	 * @param firstname Vorname
	 * @param surname Nachname
	 * @param pwd Passwort
	 * @param nickname Benutzername
	 * @param email e-mail-Adresse
	 * @param lang Spracheinstellung
	 * @param gender Geschlecht
	 * @param location Ort
	 * @param comment Kommentar
	 */
	public void register(String firstname, String surname, String pwd, String nickname,
			String email, String lang, String gender, String location,
			String comment) {
		setFirstName(firstname);
		setSurname(surname);
		setGender(gender);
		setLang(lang);
		setEmail(email);
		setLocation(location);
		setComment(GetText.replaceRN(comment));
		this.password = pwd;
		user.setNickname(nickname);
		new UserTask(user).execute(UserTask.REGISTER);
	}
	
	/**
	 * Teilt den Listenern mit, ob die Registrierung erfolgreich verlaufen ist.
	 *
	 * @param successful true, wenn die Registrierung erfolgreich verlaufen ist
	 */
	protected void fireRegister(boolean successful) {
		//tue nichts
		//die Listener benachrichtigen
		ArrayList<UserListener> listener = user.getListener();
		for (UserListener userListener : listener) {
			userListener.register(new UserEvent(successful, user));
		}
	}
	
	/**
	 * Teilt den Listenern mit, dass bereits ein Anwender mit dem gewünschten 
	 * Benutzernamen existiert.
	 *
	 */
	protected void userExists() {
		//nickname und pwd zurücksetzen
		user.setNickname("");
		password = "";
		//Listener benachrichtigen
		fireRegister(false);
	}
	
	/**
	 * Setzt das Passwort.
	 * @param pwd Passwort
	 */
	protected void setPassword(String pwd) {
		this.password = pwd;		
	}
	
	/**
	 * Setzt das Profil des Anwenders.
	 * @param firstname Vorname
	 * @param surname Nachname
	 * @param pwd Passwort
	 * @param email e-mail-Adresse
	 * @param lang Spracheinstellung
	 * @param gender Geschlecht
	 * @param location Ort
	 * @param comment Kommentar
	 */
	public void setProfile(String firstname, String surname, String pwd, String email, String lang, String gender, String location, String comment) {
		setFirstName(firstname);
		setSurname(surname);
		setGender(gender);
		setLang(lang);
		setEmail(email);
		setLocation(location);
		setComment(GetText.replaceRN(comment));
		this.password = pwd;
		new UserTask(user).execute(UserTask.SET_PROFILE);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#getComment()
	 */
	public String getComment() {
		return getPersonsData().getComment();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#getEmail()
	 */
	public String getEmail() {
		return getPersonsData().getEmail();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#getFirstName()
	 */
	public String getFirstName() {
		return getPersonsData().getFirstName();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#getGender()
	 */
	public String getGender() {
		return getPersonsData().getGender();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#getLang()
	 */
	public String getLang() {
		return getPersonsData().getLang();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#getLocation()
	 */
	public String getLocation() {
		return getPersonsData().getLocation();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#getSurname()
	 */
	public String getSurname() {
		return getPersonsData().getSurname();
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#setComment(java.lang.String)
	 */
	public void setComment(String comment) {
		getPersonsData().setComment(comment);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#setEmail(java.lang.String)
	 */
	public void setEmail(String email) {
		getPersonsData().setEmail(email);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#setFirstName(java.lang.String)
	 */
	public void setFirstName(String firstName) {
		getPersonsData().setFirstName(firstName);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#setGender(java.lang.String)
	 */
	public void setGender(String gender) {
		getPersonsData().setGender(gender);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#setLang(java.lang.String)
	 */
	public void setLang(String lang) {
		getPersonsData().setLang(lang);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#setLocation(java.lang.String)
	 */
	public void setLocation(String location) {
		getPersonsData().setLocation(location);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#setNickname(java.lang.String)
	 */
	public void setNickname(String nickname) {
		getPersonsData().setNickname(nickname);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.PersonsData#setSurname(java.lang.String)
	 */
	public void setSurname(String surname) {
		getPersonsData().setSurname(surname);
	}
	
	
	
}
