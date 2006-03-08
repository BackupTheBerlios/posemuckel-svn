/**
 * 
 */
package posemuckel.client.model.event;

/**
 * Der UserListener empfängt Benachrichtigungen zum 
 * <ul>
 * <li>login</li>
 * <li>logout</li>
 * <li>registieren</li>
 * <li>ändern des Profils</li>
 * <li>neuen Chat</li>
 * </ul>
 * des Anwenders.
 * @author Posemuckel Team
 *
 */
public interface UserListener extends PosemuckelListener {
	
	/**
	 * Das Resultat des Versuches, sich einzuloggen, ist angekommen.
	 * @param event mit Referenz auf den User und weiteren Informationen
	 */
	public void login(UserEvent event);
	
	/**
	 * Das Resultat des Versuches, sich auszuloggen, ist angekommen.
	 * @param event mit Referenz auf den User und weiteren Informationen
	 */
	public void register(UserEvent event);
	
	/**
	 * Das Resultat des Versuches, sich einzuloggen, ist angekommen.
	 * @param event mit Referenz auf den User und weiteren Informationen
	 */
	public void logout(UserEvent event);

	/**
	 * Das Resultat des Versuches, das Profil zu ändern, ist angekommen.
	 * @param event mit Referenz auf den User und weiteren Informationen
	 */
	public void profileChanged(UserEvent event);
	
	/**
	 * Wird aufgerufen, wenn der User zu einem Chat eingeladen wurde
	 * oder der Owner eines neuen Chats ist.
	 * 
	 * @param event Event mit dem neuen Chat
	 */
	public void newChat(ChatEvent event);

}
