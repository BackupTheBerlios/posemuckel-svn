/**
 * 
 */
package posemuckel.client.model.event;

/**
 * Ist für Leute, die keine leeren Methoden in ihren Listenern mögen.
 * @author Posemuckel Team
 *
 */
public class UserListenerAdapter implements UserListener {

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.UserListener#login(posemuckel.client.model.event.UserEvent)
	 */
	public void login(UserEvent event) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.UserListener#register(posemuckel.client.model.event.UserEvent)
	 */
	public void register(UserEvent event) {}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.UserListener#logout(posemuckel.client.model.event.UserEvent)
	 */
	public void logout(UserEvent event) {		
	}

	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.UserListener#profileChanged(posemuckel.client.model.event.UserEvent)
	 */
	public void profileChanged(UserEvent event) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.UserListener#newChat(posemuckel.client.model.event.ChatEvent)
	 */
	public void newChat(ChatEvent event) {
	}

}
