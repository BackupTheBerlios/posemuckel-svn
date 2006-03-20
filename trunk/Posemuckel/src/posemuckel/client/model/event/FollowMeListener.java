package posemuckel.client.model.event;

import posemuckel.client.model.FollowMeManager;

/**
 * Beobachtet den FollowMeManager. Wenn sich der Anwender, dem gefolgt wird, 
 * verändert, wird dieser Listener benachrichtigt.
 * 
 * @author Tanja Buttler
 *
 */
public interface FollowMeListener extends PosemuckelListener {
	
	/**
	 * Wird aufgerufen, wenn sich der Anwender, dem gefolgt wird, ändert.
	 * @param name Benutzername des Anwenders, dem gefolgt wird
	 * @param manager Quelle des Event
	 */
	public abstract void following(String name, FollowMeManager manager);
	
	/**
	 * Wird aufgerufen, wenn niemandem mehr gefolgt wird, der FollowMeModus 
	 * also deaktiviert ist.
	 *
	 */
	public abstract void deactivation(FollowMeManager manager);

}
