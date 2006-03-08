/**
 * 
 */
package posemuckel.client.model;


/**
 * F&uuml;rt die Zugriffe auf die Database, die zum Funktionieren des Chat 
 * notwendig sind, aus.
 * Dazu geh&ouml;ren
 * <ul>
 * <li>Laden der Teilnehmer</li>
 * <li>Chatnachrichten senden</li>
 * <li>typing senden</li>
 * <li>reading senden</li>
 * <li>privaten Chat starten</li>
 * </ul>
 * 
 * In Zukunft soll noch hinzukommen:
 * <ul>
 * <li>einem Chat beitreten</li>
 * <li>einen Chat verlassen</li>
 * </ul>
 * 
 * @author Posemuckel Team
 *
 */
class ChatTask extends TaskAdapter {
	
	static final int LOAD_MEMBERS = 100;

	static final int CHATTING = 200;

	static final int TYPING = 300;

	static final int READING = 400;

	static final int JOIN = 500;
	
	static final int START = 600;
	
	private Chat chat;
	private String[] userToInvite;
	
	/**
	 * Diese ChatTask kann alle Aufgaben bis auf das Starten eines neuen Chat
	 * erledigen.
	 * @param chat auf den sich die Aufgabe bezieht
	 */
	ChatTask(Chat chat) {
		this.chat = chat;
	}
	
	/**
	 * Diese ChatTask kann einen neuen Chat starten.
	 * @param userToInvite Liste der Teilnehmer
	 */
	ChatTask(String[] userToInvite) {
		this.userToInvite = userToInvite;
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.TaskAdapter#work(int)
	 */
	@Override
	protected void work(int task) {
		switch(task) {
		case LOAD_MEMBERS:
			DatabaseFactory.getRegistry().loadChatMembers(chat.getID());
			break;
		case CHATTING:
			DatabaseFactory.getRegistry().chatting(chat.getID(), chat.getLastUserMessage());
			break;
		case TYPING:
			DatabaseFactory.getRegistry().typing(chat.getID());
			break;
		case READING:
			DatabaseFactory.getRegistry().reading(chat.getID());
			break;
		case JOIN:
			DatabaseFactory.getRegistry().joinChat(chat.getID(), this);
			break;
		case START:
			DatabaseFactory.getRegistry().startChat(userToInvite);
			break;
		default:
			break;
		}
	}	
	

}
