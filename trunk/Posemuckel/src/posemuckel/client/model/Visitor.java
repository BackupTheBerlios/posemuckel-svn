/**
 * 
 */
package posemuckel.client.model;


/**
 * Ein Vistor repräsentiert einen Anwender, der eine Webseite besucht und 
 * bewertet hat.
 * 
 * @author Posemuckel Team
 *
 */
public class Visitor {
	
	/**
	 * Standardwert für das Fehlen einer Bewertung. Entweder die Bewertung ist
	 * nicht bekannt oder sie wurde nicht abgegeben. 
	 */
	public static short NO_RATING = -1;
	
	private String nickname;
	private String comment;
	private short rating;
	private boolean hasComment;
	
	/**
	 * Erstellt einen Visitor mit dem angegebenen Benutzernamen.
	 * @param nickname Benutzername
	 */
	public Visitor(String nickname) {
		this.nickname = nickname;
		rating = NO_RATING;
	}
	
	/**
	 * Erstellt einen Visitor, von dem bereits eine Bewertung bekannt ist.
	 * @param nickname Benutzername
	 * @param rating Bewertung
	 */
	public Visitor(String nickname, short rating) {
		this.nickname = nickname;
		this.rating = rating;
	}
	
	/**
	 * Gibt den Namen des Anwenders zur&uuml;ck.
	 * @return Name des Besuchers
	 */
	public String getName() {
		return nickname;
	}
	
	/**
	 * Gibt einen leeren String zurück, wenn es noch kein
	 * Kommentar gibt. Ansonsten bekommt man hier den Kommentar zur
	 * entsprechenden URL.
	 * @return Kommentar zur URL
	 */
	public String getComment() {
		if(comment == null) 
			return "";
		return comment;
	}
	
	/**
	 * Hat der Anwender die Seite bewertet?
	 * @return true, falls der Anwender bereits eine Bewertung abgegeben hat
	 */
	public boolean hasComment() {
		return hasComment;
	}
	
	/**
	 * Informiert den Visitor, dass ein Kommentar vorhanden ist. Der Kommentar 
	 * muss nicht in dieser Instanz geladen sein.
	 * @param b true, falls in der Database ein Kommentar vorhanden ist
	 */
	protected void setComment(boolean b) {
		hasComment = b;
	}
	
	/**
	 * Setzt den Kommentar des Anwenders.
	 * @param comment Kommentar
	 */
	protected void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Setzt die Bewertung f&uuml;r eine Webseite.
	 * 
	 * @param rating Bewertung
	 */
	void setRating(short rating) {
		this.rating = rating;
	}
	
	/**
	 * Gibt die Bewertung zur&uuml;ck.
	 * 
	 * @return die Bewertung
	 */
	public short getRating() {
		return rating;
	}
	
	/**
	 * Gibt die Bewertung als String aus. Es wird der Zahlenwert direkt in den 
	 * passenden String &uuml;bersetzt.
	 * 
	 * @return Bewertung im String
	 */
	public String getRatingAsString() {
		return String.valueOf(rating);
	}
	

}
