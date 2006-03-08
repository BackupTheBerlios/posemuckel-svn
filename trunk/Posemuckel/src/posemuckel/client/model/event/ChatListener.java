/**
 * 
 */
package posemuckel.client.model.event;

/**
 * Der ChatListener empf&auml;ngt Nachrichten &uuml;ber die Ver&auml;nderungen 
 * des Chat. Wenn die Ver&auml;nderungen die Mitglieder des Chat betreffen, so
 * wird der <code>MemberListListener</code> der Mitgliedsliste des Chat benachrichtigt
 * und nicht der <code>ChatListener</code> des Chat.
 * @author Posemuckel Team
 *
 */
public interface ChatListener extends PosemuckelListener {
	
	/**
	 * Der Listener wird mit dieser Methode benachrichtigt, wenn ein User anf&auml;ngt zu tippen.
	 * 
	 * @param event ChatEvent, welches den Autor enth&auml;lt
	 */
	public abstract void typing(ChatEvent event);
	
	/**
	 * Der Listener wird mit dieser Methode benachrichtigt, wenn ein User wieder liest.
	 * 
	 * @param event ChatEvent, welches den Autor enth&auml;lt
	 */
	public abstract void reading(ChatEvent event);
	
	/**
	 * Der Listener wird mit dieser Methode &uuml;ber das Eintreffen einer neuen Nachricht
	 * informiert
	 * 
	 * @param event ChatEvent mit Autor und Nachricht
	 */
	public abstract void chatting(ChatEvent event);
	
}
