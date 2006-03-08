package posemuckel.server;

import java.util.Vector;

import posemuckel.common.Message;

/**
 * Erzeugt die Pakete, die vom Server verschickt werden und legt sie
 * in der Sendewarteschlange ab, die als Vector übergeben wurde.
 * Das Nachrichtenformat entspricht RFC 0815.
 * 
 * @author Posemuckel Team
 */
public class ServerMessage {
	
	/**
	 * Sendewarteschlange
	 */
	private Vector<Model.QueueItem> sendqueue;
	
	/**
	 * einzige Instanz der Klasse
	 */
	private static ServerMessage instance = null;
	
	/**
	 * Model
	 */
	private Model model = Model.getInstance();
	
	/**
	 * Konstruktor über der Warteschlange des Models
	 *
	 */
	public ServerMessage() {
		this.sendqueue = model.getSendqueue();
	}

	/**
	 * Liefert die einzige Instanz der Klasse ServerMessage
	 * @return einzige Instanz der Klasse ServerMessage
	 */
	public static ServerMessage getInstance() {
		if ( instance == null )
			instance = new ServerMessage();
		return instance;
	}
	
	/**
	 * Erzeugt eine ACK-Nachricht.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param hash Der zu übergebende Client-Hash.
	 * @param id Die Nachrichten-ID.
	 */
	public void ack(Vector<ClientInfo> recievers, String hash, String id) {
		String message = Message.format(hash, id, "ACK", null);
		add2sendqueue(recievers,message);
	}

	/**
	 * Erzeugt eine USER_EXISTS-Nachricht.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param id Die Nachrichten-ID.
	 */
	public void user_exists(Vector<ClientInfo> recievers, String id) {
		String message = Message.format("", id, "USER_EXISTS", null);
		add2sendqueue(recievers,message);
	}

	/**
	 * Erzeugt eine ACCESS_DENIED-Nachricht.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param hash Der zu übergebende Client-Hash.
	 * @param id Die Nachrichten-ID.
	 */
	public void access_denied(Vector<ClientInfo> recievers, String hash, String id) {
		String message = Message.format(hash, id, "ACCESS_DENIED", null);
		add2sendqueue(recievers,message);
	}

	/**
	 * Erzeugt eine ACCESS_GRANTED-Nachricht.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param hash Der zu übergebende Client-Hash.
	 * @param id Die Nachrichten-ID.
	 */
	public void access_granted(Vector<ClientInfo> recievers, String hash, String id) {
		String message = Message.format(hash, id, "ACCESS_GRANTED", null);
		add2sendqueue(recievers,message);
	}
	
	/**
	 * Erzeugt eine ERROR-Mitteilung.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param hash Der zu übergebende Client-Hash.
	 * @param id Die Nachrichten-ID.
	 */
	public void error(Vector<ClientInfo> recievers, String hash, String id) {
		String message = Message.format(hash, id, "ERROR", null);
		add2sendqueue(recievers,message);
	}
	
	/**
	 * Senden einer Chat-Nachricht.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param nickname Der Nickname des Benutzers, der die Chat-Nachricht verschickt hat.
	 * @param id Die Clientweit eindeutige ID der Nachricht
	 * @param chat_id Die ID des Chats.
	 * @param message die Chat-Botschaft, die an die Gruppe gehen soll.
	 */
	public void chat(Vector<ClientInfo> recievers, String nickname, String id, String chat_id, String message) {
		String[] data = new String[2];
		data[0] = chat_id;
		data[1] = message;
	    String msg = Message.format(nickname, id, "CHAT", data);
	    add2sendqueue(recievers,msg);
	}

	/**
	 * Benachrichtigt die anderen Benutzer des Chats über die Tatsache, dass dieser Benutzer
	 * gerade schreibt. Hier gibt es nur die Chat-ID als Parameter, da der Client-Hash den Client
	 * eindeutig identifiziert.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param nickname Der Nickname des Benutzers, von dem die Nachricht stammt.
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param chat_id Die ID des Chats.
	 */
	public void typing(Vector<ClientInfo> recievers, String nickname, String id, String chat_id) {
		//die String-ID ist für das Model durchaus geeignet, solange der Server dafür sorgt, 
		//das die ID eindeutig ist
		String[] data = { chat_id };		  
		String message = Message.format(nickname, id, "TYPING", data);
	    add2sendqueue(recievers,message);
    }

	/**
	 * Benachrichtigt die anderen Benutzer des Chats über die Tatsache, dass dieser Benutzer
	 * eine gewisse Zeit lang nicht mehr in die Tasten gehauen hat. Hier gibt es nur die Chat-ID
	 * als Parameter, da der Client-Hash den Client eindeutig identifiziert.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param nickname Der Nickname des Benutzers, von dem die Nachricht stammt.
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param chat_id Die ID des Chats.
	 */
	  public void reading(Vector<ClientInfo> recievers, String nickname, String id, String chat_id) {
		  //die String-ID ist für das Model durchaus geeignet, solange der Server dafür sorgt, 
		  //das die ID eindeutig ist
		  String[] data = { chat_id };		  
		  String message = Message.format(nickname, id, "READING", data);
	      add2sendqueue(recievers,message);
	  }
	  
	  /**
	   * Benachrichtigt die interessierten Nutzer, dass sich der Status des 
	   * Users ge&auml;ndert hat.
	   * 
	   * @param recievers Vector der Empfänger
	   * @param user der User, dessen Status sich ge&auml;ndert hat
	   * @param id der Nachricht
	   * @param status neuer Status des Users
	   */
	  public void userStatus(Vector<ClientInfo> recievers, String user, String id, String status) {
		  String[] data = {user, status};
		  String message = Message.format("", id, "USER_STATUS", data);
		  add2sendqueue(recievers,message);
	  }

	 /**
	  * Versenden der CHAT_MEMBERS Nachricht.
	  * 
	  * @param recievers Vector der Empfänger
	  * @param chat_id Die ID des Chats.
	  * @param members String-Array der Mitglieder
	  */
	  public void chat_members(Vector<ClientInfo> recievers, String chat_id, String[] members) {
		  // Die id ist die Nachrichten-ID.
		  String[] data = new String[members.length+1];
		  data[0] = chat_id;
		  for ( int i=1; i < data.length; i++ ) {
			  data[i]=members[i-1];
		  }
		  String message = Message.format("", "-1", "CHAT_MEMBERS", data);
	      add2sendqueue(recievers,message);
	  }

	/**
	 * Sendet eine NEW_PROJECT-Nachricht als Antwort auf eine START_PROJECT-
	 * Anfrage eines Client.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param hash Der zu übergebende Client-Hash.
	 * @param id die Clientweit eindeutige ID der Nachricht
	 * @param projId ID des Projektes
	 * @param topic der Titel des Themas
	 * @param pub Projekt ist öffentlich/privat
	 * @param desc die Beschreibung des Projektes
	 * @param maxUsers maximale Teilnehmerzahl
	 */
	 public void new_project(Vector<ClientInfo> recievers, String hash, String id, int projId, String topic, String owner, String pub, String maxUsers, String desc, String date) {
		  String[] data = new String[8];
		  data[0] = String.valueOf(projId);
		  data[1] = topic;
		  data[2] = owner;
		  data[3] = pub;
		  data[4] = maxUsers;
		  data[5] = maxUsers;
		  data[6] = desc;
		  data[7] = date;
		  String message = Message.format(hash, id, "8", "NEW_PROJECT", data);
	      add2sendqueue(recievers,message);
	  }
	 
	 /**
	  * Diese Methode ist universell einsetzbar für alle
	  * Nachrichten mit beliebigem Rattenschwanz an Parametern.
	  *
	  * @param recievers Vector der Empfänger
	  * @param id Die Nachrichten-ID
	  * @param msgname Der Nachrichten-Name nach RFC
	  * @param data Ein String-Array der anzuhängenden Parameter.
	  */
	 public void generic(Vector<ClientInfo> recievers, String id, String msgname, String[] data) {
		 String message;
		 if( data == null )
			 message = Message.format("", id, "0", msgname, null);
		 else
			 message = Message.format("", id, String.valueOf(data.length), msgname, data);
		 add2sendqueue(recievers,message);
	 }	
	
    /**
     * Diese Methode fügt den zu sendenden String in die Warteschlange für
     * ausgehende Nachrichten ein.
     * 
	 * @param recievers Vector der Empfänger
     * @param message
     */
	private void add2sendqueue(Vector<ClientInfo> recievers, String message) {
		synchronized (sendqueue) {
			Model.QueueItem qi = model.new QueueItem();
			qi.message = message;
			qi.recievers = recievers;
			sendqueue.addElement(qi);
			sendqueue.notify();
		}
	}

	/** 
	 * Diese Methode sendet eine NEW_CHAT Nachricht.
	 * Das Argument data enthält neben den Mitgliedern auch die ID, was ein Relikt aus
	 * ClientMessage ist.
	 * 
	 * @param recievers Vector der Empfänger
	 * @param id Nachrichten-ID
	 * @param data String-Array der Nachrichten-Parameter.
	 */
	public void newchat(Vector<ClientInfo> recievers, String id, String[] data) {
		 String message = Message.format("", id, String.valueOf(data.length), "NEW_CHAT", data);
		 add2sendqueue(recievers,message);
	}

	/**
	 * Verschickt eine VISITING-Nachricht nach Definition im RFC 0815
	 * 
	 * @param recievers Vector der Empfänger
	 * @param id ID der Nachricht
	 * @param nickname Der Benutzername des Benutzers, der die Nachricht geschickt hat.
	 * @param newurl Die neue URL
 	 * @param newtitle Der Titel der neuen URL
	 * @param oldurl Die alte URL, von der die neue besucht wurde
	 */
	public void visiting(Vector<ClientInfo> recievers, String id, String nickname, String newurl, String newtitle, String oldurl) {
		String[] data = { newurl, newtitle, oldurl };
		String message = Message.format(nickname, id, String.valueOf(data.length), "VISITING", data);
		add2sendqueue(recievers,message);
	}
	
	/**
	 * Verschickt eine VIEWING-Nachricht nach Definition im RFC 0815
	 * 
	 * @param recievers Vector der Empfänger
	 * @param name Der Benutzername des Benutzers, der die Nachricht geschickt hat.
	 * @param url Die besuchte URL
	 */
	public void viewing(Vector<ClientInfo> recievers, String name, String url) {
		String[] data = { url };
		String message = Message.format(name, "-1", String.valueOf(data.length), "VIEWING", data);
		add2sendqueue(recievers,message);
	}
	
	/**
	 * Sendet eine VOTING-Nchricht.
	 * 
	 * @param recievers
	 * @param id
	 * @param nickname
	 * @param url
	 * @param vote
	 */
	public void voting(Vector<ClientInfo> recievers, String id, String nickname,
			String url, String vote) {
		String[] data = {url, vote};
		String message = Message.format(nickname, id, "VOTING", data);
		add2sendqueue(recievers,message);		
	}	
	
	/**
	 * Sendet die MEMBER_CHANGE Nachricht.
	 * 
	 * @param recievers Der Vector der Empfänger.
	 * @param type Zusätzliches Schlüsselwort für diese Nachricht. Spezifiziert Varianten der Nachricht.
	 * @param id Projekt-ID des Projektes auf das sich die Nachricht bezieht.
	 * @param nickname Benutzername des betreffenden Benutzers
	 * @param joining Enthält den String
	 */
	public void memberChange(Vector<ClientInfo> recievers, String type, String id,
			String nickname, String joining) {
		if(recievers != null && recievers.size() > 0) {
			String[] data = {type, id, joining};
			String message = Message.format(nickname, "-1", "MEMBER_CHANGE", data);
			add2sendqueue(recievers,message); 
		}
	}
	
	/**
	 * Verschickt eine NEW_INVITATION-Nachricht nach Definition im RFC 0815
	 * 
	 * @param clients Vector der Empfänger
	 * @param projectID ID des Projektes
	 */
	public void invite(Vector<ClientInfo> clients, int projectID) {
		String message = Message.format("", "-1", "NEW_INVITATION", new String[] {(projectID + "")});
		add2sendqueue(clients,message);
	}

	/**
	 * Verschickt eine NOTIFY Nachricht mit den entsprechenden Bilddaten.
	 * 
	 * @param recievers Die Empfänger
	 * @param user Der hochhaltende Benutzer
	 * @param url Die URL der Seite
	 * @param title Der Titel der Seite
	 * @param comment Der Kommentar von user
	 * @param datalen Die Länge der Bilddaten
	 * @param imagedata Die Bilddaten
	 */
	public void notify(Vector<ClientInfo> recievers, String user, String url, String title, String comment, String datalen, String imagedata) {
		String[] data = {url, title, comment, datalen, imagedata};
		String message = Message.format(user, "-1", String.valueOf(data.length), "NOTIFY", data);
		add2sendqueue(recievers,message);
	}

	/**
	 * Verschickt eine NEW_NOTE Nachricht.
	 * 
	 * @param recievers Empfänger
	 * @param url Die betreffende URL
	 * @param rating Die Bewertung zur URL
	 * @param withNote gibt an, ob eine Notiz dabei ist ("0" = keine Notiz,
	 * "1" = Notiz dabei)
	 */
	public void new_note(Vector<ClientInfo> recievers, String user, String url, String rating, String withNote) {
		String[] data = {url,rating,withNote};
		String message = Message.format(user, "-1", String.valueOf(data.length), "NEW_NOTE", data);
		add2sendqueue(recievers,message);
	}
}
