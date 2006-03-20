/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

import posemuckel.client.model.event.FollowMeListener;
import posemuckel.client.model.event.ListenerManagment;

/**
 * Handhabt die FollowMe-Logik. Ein Anwender kann maximal einem anderen
 * Anwender folgen. Wenn der Anwender gewechselt wird, dem der Anwender folgt, 
 * werden alle Listener benachrichtigt. 
 * 
 * Wenn einem Anwender gefolgt wird, so befindet sich der Client im "FollowMeModus".
 * 
 * @author Tanja Buttler
 *
 */
public class FollowMeManager {
	
	/**
	 * Name des Anwenders, dem gerade gefolgt wird. Wenn keinem Anwender gefolgt
	 * wird, ist der Wert <code>null</null>.
	 */
	private String name;
	private String myself;
	private Chat logChat;
	
	private ListenerManagment<FollowMeListener> listenerManagment;
	
	/**
	 * Erstellt einen FollowMeManager.
	 * @param logChat der Chat, in dem die Logs angezeigt werden
	 * @param username Name des Users - der Name wird für das Log benötigt
	 */
	public FollowMeManager(Chat logChat, String username) {
		this.logChat = logChat;
		this.myself = username;
		listenerManagment = new ListenerManagment<FollowMeListener>();
	}
	
	/**
	 * Ändert den Anwender, dem der User gerade folgt.
	 * @param name Benutzername des Verfolgten
	 */
	public void follow(String name) {
		this.name = name;
		logChat.userIsChatting("Benutzer "+myself+" folgt "+name+".");
		fireFollowing(name);
	}
	
	/**
	 * Deaktiviert den FollowMeModus.
	 *
	 */
	public void deactivate() {
		logChat.userIsChatting("Benutzer "+myself+" folgt "+name+" nicht mehr.");
		this.name = null;
		fireDeactivation();
	}
	
	/**
	 * Gibt an, ob der User dem Anwender mit Benutzernamen name folgt.
	 * @param name Benutzername
	 * @return true, falls der Anwender name verfolgt wird
	 */
	public boolean isFollowing(String name) {
		if(this.name == null) return false;
		return this.name.equals(name);
	}
	
	/**
	 * Benachrichtigt alle Listener, dass der FollowMeModus deaktiviert wurde.
	 *
	 */
	protected void fireDeactivation() {
		ArrayList<FollowMeListener>  listener = getListener();
		for (FollowMeListener followMeListener : listener) {
			followMeListener.deactivation(this);
		}
	}
	
	/**
	 * Benachrichtigt alle Listener, dass sich der Anwender, dem gefolgt wird, 
	 * geändert hat.
	 * @param name Benutzername des Anwenders, dem gefolgt wird
	 */
	protected void fireFollowing(String name) {
		ArrayList<FollowMeListener>  listener = getListener();
		for (FollowMeListener followMeListener : listener) {
			followMeListener.following(name, this);
		}
	}
	
	/**
	 * Registriert einen FollowMeListener, der über Ereignisse, die diese Instanz
	 * betreffen, informiert werden möchte. 
	 * @param listener der zu registrierende FollowMeListener
	 */
	public void addListener(FollowMeListener listener) {
		listenerManagment.addListener(listener);
	}

	/**
	 * Gibt eine Kopie der Liste mit allen FollowMeListenern aus.
	 * @return Kopie der Liste mit allen FollowMeListenern
	 */
	public ArrayList<FollowMeListener> getListener() {
		return listenerManagment.getListener();
	}

	/**
	 * Entfernt den FollowMeListener aus der Liste der FollowMeListener.
	 * @param listener der zu entfernende FollowMeListener
	 */
	public void removeListener(FollowMeListener listener) {
		listenerManagment.removeListener(listener);
	}


	
}
