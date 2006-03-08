/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

import posemuckel.client.model.event.ChatEvent;
import posemuckel.client.model.event.ChatListener;
import posemuckel.client.model.event.ListenerManagment;
import posemuckel.common.GetText;

/**
 * &Uuml;ber einen Chat werden die Informationen
 * wie neue Nachrichten und Aktivit&auml;ten des Benutzers an die Database
 * weitergeleitet. Informationen über die Mitglieder eines Chat können 
 * über die Mitgliederliste bezogen werden. 
 * 
 * @author Posemuckel Team
 *
 */
public class Chat {
	//TODO LeaveChat implementieren
	
	private String id;
	private MemberList members;
	private String lastMessage;
	private boolean isOpen;
	private ListenerManagment<ChatListener> listenerManagment;
	
	//wird noch nicht verwendet
	private String title;
	
	/**
	 * Erstellt einen neuen Chat im Model.
	 * 
	 * @param id id des Chat
	 * @param model das Model
	 */
	public Chat(String id, Model model) {
		this.id = id;
		members = new MemberList(false, MemberList.CHAT, model);
		model.addChat(this);
		listenerManagment = new ListenerManagment<ChatListener>();
		isOpen = true;
		title = GetText.gettext("CHAT_PUBLIC");
	}
	
	/**
	 * Ändert den Titel des Chat
	 * @param title der neue Titel
	 */
	protected void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Gibt den Titel des Chat aus.
	 * @return Titel
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Teilt dem Chat mit, dass der Anwender am tippen ist. Die Information 
	 * wird an die Database weitergeleitet.
	 *
	 */
	public void userIsTyping() {
		new ChatTask(this).execute(ChatTask.TYPING);
	}
	
	/**
	 * Gibt an, ob der User dem Chat bereits beigetreten ist. Wenn der User der
	 * Owner des Chat ist, gilt er als beigetreten.
	 * 
	 * @return true, falls der User dem Chat bereits beigetreten ist.
	 */
	public boolean isOpen() {
		return isOpen;
	}
	
	/**
	 * Teilt dem Chat mit, dass der User dem Chat beigetreten ist. 
	 * 
	 * @param open true, wenn der User dem Chat beigetreten ist
	 */
	protected void setOpen(boolean open) {
		isOpen = open;
	}
	
	/**
	 * Teilt dem Chat mit, dass der Anwender wieder am Lesen ist. Die
	 * Information wird an die Database weitergeleitet.
	 *
	 */
	public void userIsReading() {
		new ChatTask(this).execute(ChatTask.READING);
	}
	
	/**
	 * Gibt die Mitgliederlist f&uuml;r die Chatteilnehmer aus.
	 * @return Mitgliederliste
	 */
	public MemberList getChatMembers() {
		return members;
	}
	
	/**
	 * Gibt an, ob jemand ein Mitglied des Chat ist. Wenn die Mitgliederliste
	 * noch nicht geladen wurde, wird <code>false</code> ausgegeben.
	 * 
	 * @param nickname Benutzername
	 * @return true, falls der Benutzer Mitglied im Chat ist
	 */
	public boolean isChatMember(String nickname) {
		if(members == null) return false;
		return members.hasMember(nickname);
	}
	
	/**
	 * Setzt die Mitgliederliste. Damit kann dem Chat eine Mitgliederliste
	 * zugeteilt werden, die nicht &uuml;ber CHAT-MEMBERS geladen wurde. Zur Zeit
	 * wird dies vor allem bei der Projektliste gebraucht.
	 * 
	 * @param members die Mitgliederliste f&uuml;r den Chat
	 */
	protected void setMemberList(MemberList members) {
		this.members = members;
	}
	
	/**
	 * Gibt die ID des Chat aus. Im <code>Model</code> gibt es einen Chatpool, 
	 * in dem dieser Chat anhand der ID herausgeholt werden kann.
	 * 
	 * @return ID des Chat
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gibt die letzte Nachricht, die vom Anwender geschrieben wurde, wieder aus.
	 * 
	 * @return letzte Nachricht des Anwenders
	 */
	protected String getLastUserMessage() {
		return lastMessage;
	}
	
	/**
	 * Fordert den Chat, eine Mitgliederliste von der Datenbank zu laden.
	 *
	 */
	public void loadChatMembers() {
		new ChatTask(this).execute(ChatTask.LOAD_MEMBERS);
	}
	
	/**
	 * Fordert die Database auf, den User in die Mitgliederliste des Chat einzutragen.
	 * Es wird nicht &uuml;berpr&uuml;ft, ob der User schon Mitglied des Chat ist.
	 *
	 */
	public void join() {
		new ChatTask(this).execute(ChatTask.JOIN);
	}
	
	/**
	 * Fordert den Chat auf, eine Chatnachricht an die Datenbank zu schicken. 
	 * Die Nachricht kann &uuml;ber <code>getLastUserMessage</code> vom Chat
	 * abgefragt werden.
	 * 
	 * @param message Nachricht
	 */
	public void userIsChatting(String message) {
		lastMessage = message;
		new ChatTask(this).execute(ChatTask.CHATTING);
	}
				
	/**
	 * Teilt dem Chat mit, dass sich die Mitgliederliste des Chat ver&auml;ndert
	 * hat. Der Chat leitet die Mitteilung an seine Mitgliederliste weiter, die
	 * ihre Listener &uuml;ber das Ereignis informiert.
	 * 
	 * @param users die Liste der Mitglieder: an der Position 0 ist zur Zeit die
	 * ChatID zu finden!
	 */
	protected void informAboutUserUpdate(String[] users) {
		members.confirmChatMembersReceived(users);
	}
	
	//die Listener benachrichtigen
	/**
	 * Teilt dem Chat mit, dass eine neue Nachricht von einem Chatmitglied 
	 * eingetroffen ist. Wenn das Chatmitglied vorher am Tippen war, so ist
	 * er/sie jetzt wieder als Lesend einzustufen. Die Listener werden &uuml;ber
	 * die neue Nachricht informiert.
	 * 
	 * @param author Autor der Nachricht
	 * @param message neue Nachricht
	 */
	protected void fireNewMessage(String author, String message) {
		ArrayList<ChatListener> listener = getListener();
		for (ChatListener listListener : listener) {
			listListener.chatting(new ChatEvent(message, author, this));
		}
	}
	
	/**
	 * Informiert den Chat &uuml;ber einen Chatteilnehmer, der gerade wieder zu
	 * lesen angefangen hat. Diese Information wird an die Listener des Chat 
	 * weitergegeben.
	 * 
	 * @param user Chatteilnehmer, der wieder am Lesen ist
	 */
	protected void fireReading(String user) {
		ArrayList<ChatListener> listener = getListener();
		for (ChatListener listListener : listener) {
			listListener.reading(new ChatEvent(user, this));
		}
	}
	
	/**
	 * Informiert den Chat &uuml;ber einen Chatteilnehmer, der gerade am Tippen
	 * ist. Diese Information wird an die Listener des Chats weitergegeben.
	 * 
	 * @param user Chatteilnehmer, der tippt
	 */
	protected void fireTyping(String user) {
		ArrayList<ChatListener> listener = getListener();
		for (ChatListener listListener : listener) {
			listListener.typing(new ChatEvent(user, this));
		}
	}
	
	/**
	 * Registriert einen ChatListener, der über Ereignisse, die diese Instanz
	 * betreffen, informiert werden möchte. 
	 * @param listener der zu registrierende ChatListener
	 */
	public void addListener(ChatListener listener) {
		listenerManagment.addListener(listener);
	}

	/**
	 * Gibt eine Kopie der Liste mit allen ChatListenern aus.
	 * @return Kopie der Liste mit allen ChatListenern
	 */
	public ArrayList<ChatListener> getListener() {
		return listenerManagment.getListener();
	}

	/**
	 * Entfernt den ChatListener aus der Liste der ChatListener.
	 * @param listener der zu entfernende ChatListener
	 */
	public void removeListener(ChatListener listener) {
		listenerManagment.removeListener(listener);
	}


}
