/**
 * 
 */
package posemuckel.client.model.event;

import posemuckel.client.model.Chat;

/**
 * Wird ausgelöst, wenn sich der Zustand des Chat ändert.
 * Enthält den Autor und die Nachricht eines Chatbeitrages sowie eine Referenz
 * auf den betroffenen Chat.
 * 
 * @author Posemuckel Team
 *
 */
public class ChatEvent {
	
	private String message;
	private String author;
	private Chat chat;
	private boolean userIsOwner;
	
	/**
	 * Erzeugt ein neues ChatEvent für eine chatting-Nachricht.
	 * 
	 * @param message Nachricht
	 * @param author Autor der Nachricht
	 * @param chat der betroffene Chat
	 */
	public ChatEvent(String message, String author, Chat chat) {
		this.message = message;
		this.author = author;
		this.chat = chat;
	}
	
	/**
	 * Erzeugt ein neues ChatEvent für eine typing/reading-Nachricht.
	 * 
	 * @param author Anwender, der von Tippend zu Lesend oder umgekehrt gewechselt hat.
	 * @param chat der betroffene Chat
	 */
	public ChatEvent(String author, Chat chat) {
		this("", author, chat);
	}
	
	/**
	 * Erzeugt ein neues ChatEvent, dass die Listener über einen neuen Chat informiert, 
	 * an dem der Anwender teilnehmen kann.
	 * @param chat der neue Chat
	 * @param userIsOwner true, wenn der Anwender der Owner des Chat ist
	 */
	public ChatEvent(Chat chat, boolean userIsOwner) {
		this("", "", chat);
		this.userIsOwner = userIsOwner;
	}

	/**
	 * Gibt den Autor einer Nachricht oder den Anwender, der gerade tippt,
	 * zur&uuml;ck.
	 * 
	 * @return der Autor einer Nachricht oder der Anwender, der gerade tippt
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Gibt die neue Nachricht zur&uuml;ck, falls eine gesendet wurde.
	 * 
	 * @return die neue Nachricht, falls eine &uuml;bertragen wurde
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Gibt den betroffenen Chat aus.
	 * 
	 * @return chat
	 */
	public Chat getSource() {
		return chat;
	}
}
