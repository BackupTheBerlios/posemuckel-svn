/**
 * 
 */
package posemuckel.client.model.event;

import posemuckel.client.model.User;

/**
 * Wird ausgelöst, wenn sich der Zustand des Anwenders ändert: wenn er sich einloggt
 * oder ausloggt oder registriert.
 * @author Posemuckel Team
 *
 */
public class UserEvent {
	
	private boolean succeeded;
	private User source;
	
	/**
	 * Ein Event, mit dem die Listener über eine Zustandsänderung des Users
	 * informiert werden.
	 * @param succeeded true , falls die Operation erfolgreich war
	 * @param source der Anwender
	 */
	public UserEvent(boolean succeeded, User source) {
		this.succeeded = succeeded;
		this.source = source;
	}
	/**
	 * @return Gibt die Quelle des Events zur&uuml;ck.
	 */
	public User getSource() {
		return source;
	}
	/**
	 * @return Gibt an, ob die Operation erfolgreich war.
	 */
	public boolean isSucceeded() {
		return succeeded;
	}
	
	
	

}
