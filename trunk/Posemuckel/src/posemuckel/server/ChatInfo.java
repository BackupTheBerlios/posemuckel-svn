/**
 * 
 */
package posemuckel.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Die Klasse ChatInfo verwaltet einen Chat.
 * Dazu geh�rt eine eindeutige ChatID und eine Liste von
 * Usern, die aus Sicht des Servers als eine Liste von
 * Clients verwaltet wird.
 *
 */
public class ChatInfo {

	/**
	 * ID des Chats
	 */
	private String chatid;
	
	/**
	 * enth�lt die Clients, die an dem Chat teilnehmen
	 */
	private HashMap<String,ClientInfo> clients;
	
	/**
	 * Gibt an, ob es sich um einen Projektchat handelt
	 */
	private boolean projectChat;
	
	/**
	 * Diesem Kontruktor muss auf jeden Fall die ChatID
	 * �bergeben werden.
	 */	
	public ChatInfo(String chatid, boolean isProjectChat) {
		this.chatid = chatid;
		clients = new HashMap<String,ClientInfo>(2);
		projectChat = isProjectChat;
	}
	
	/**
	 * Diese Methode liefert die ChatID zur�ck.
	 * @return ChatID
	 */
	public String getID() {
		return chatid;
	}
	
	/**
	 * Gibt an, ob es sich um einen Projektchat handelt
	 * @return true, falls es sich um einen Projektchat handelt, false
	 * sonst
	 */
	public boolean isProjectChat() {
		return projectChat;
	}

	/**
	 * F�gt den �bergebenen Client diesem Chat hinzu.
	 * @param ci Informationen �ber Client
	 */
	public void addClient(ClientInfo ci) {
		clients.put(ci.getHash(),ci);
	}

	/**
	 * Liefert true, falls der angegebene Client an diesem
	 * Chat beteiligt ist.
	 * 
	 * @param ci ClientInfo, von dem ger�ft werden soll,
	 * ob dieser Client am Chat teilnimmt.
	 * @return true, wenn der Client am Chat teilnimmt, sonst false.
	 */
	public boolean isParticipant(ClientInfo ci) {
		if( clients.containsKey(ci.getHash()) )
			return true;
		return false;
	}

	/**
	 * Entfernt den angegebenen Client aus dem Chat.
	 * @param ci Client
	 */
	public void delClient(ClientInfo ci) {
		clients.remove(ci.getHash());
	}

	/**
	 * Liefert alle Clients des Chats in eine
	 * HashMap.
	 * @return Vector der beteiligten Clients als ClientInfos
	 */
	public Vector<ClientInfo> getClients(ClientInfo.ClientStatus status) {
		ClientInfo ci;
		Vector<ClientInfo> vect = new Vector<ClientInfo>();
		Iterator it = clients.entrySet().iterator();
		while( it.hasNext() ) {	
			Map.Entry entry = (Map.Entry)it.next();
			ci = (ClientInfo)entry.getValue();
			if ( ci.getStatus() == status ) {
				vect.addElement(ci);
			}
		}
		return vect;
	}	
	
}
