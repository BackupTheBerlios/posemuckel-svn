package posemuckel.client.model;

/**
 * PersonsData enth&auml;lt die Daten einer Person, die nicht sofort von der
 * Database geladen werden, sondern einer speziellen Aufforderung bed&uuml;rfen.
 * Die Daten k&ouml;nnen auch von einer View (zum Beispiel der GUI) gesetzt
 * werden, wenn sich die Daten des Benutzers &auml;ndern. 
 * 
 * @see Person
 * @author Posemuckel Team
 *
 */
public class PersonsData {
	
	private String gender;
	private String lang;
	private String firstName;
	private String surname;
	private String email;
	private String location;
	private String comment;
	private String nickname;
	
	/**
	 * Gibt den Benutzernamen zur&uuml;ck. Der Benutzername muss identisch
	 * mit dem Benutzernamen der Person sein.
	 * 
	 * @return Gibt den Benutzernamen zur&uuml;ck.
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Setzt den Benutzernamen. Der Benutzername muss identisch mit dem Benutzernamen
	 * der Person sein. Diese Methode vor allem dann wichtig, wenn diese Instanz
	 * von <code>PersonsData</code> als Datenkontainer dienen soll.
	 * 
	 * @param nickname Setzt den Benutzernamen.
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	/**
	 * Erstellt ein PersonsData-Objekt ohne Daten.
	 *
	 */
	public PersonsData() {
		gender = "";
		lang = "";
		firstName = "";
		surname = "";
		email = "";
		location = "";
		comment = "";
		nickname = "";
	}
	
	/**
	 * Gibt das Geschlecht zur&uuml;ck.
	 * @return Gibt das Geschlecht zur&uuml;ck.
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Gibt die Sprache zur&uuml;ck.
	 * @return Gibt die Sprache zur&uuml;ck.
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * Gibt den Vornamen zur&uuml;ck.
	 * @return Gibt den Vornamen zur&uuml;ck.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Gibt den Nachnamen zur&uuml;ck.
	 * 
	 * @return Gibt den Nachnamen zur&uuml;ck.
	 */
	public String getSurname() {
		return surname;
	}
	
	/**
	 * Gibt die Email-Adresse zur&uuml;ck.
	 * 
	 * @return Gibt die Email-Adresse zur&uuml;ck.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Gibt die Adresse zur&uuml;ck.
	 * 
	 * @return Gibt die Adresse zur&uuml;ck.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Gibt den Kommentar des Benutzers zur&uuml;ck.
	 * @return Gibt den Kommentar des Benutzers zur&uuml;ck.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Setzt die Adresse.
	 * @param location Setzt die Adresse.
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Setzt den Kommentar.
	 * @param comment Setzt den Kommentar.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Setzt die Email-Adresse.
	 * @param email Setzt die Email-Adresse.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Setzt den Vornamen.
	 * @param firstName Setzt den Vornamen.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Setzt das Geschlecht.
	 * @param gender Setzt das Geschlecht.
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Setzt die Sprache.
	 * @param lang Setzt die Sprache.
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * Setzt den Nachnamen.
	 * @param surname Setzt den Nachnamen.
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}
	

}
