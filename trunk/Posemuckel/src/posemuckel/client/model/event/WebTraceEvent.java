package posemuckel.client.model.event;

import posemuckel.client.model.Root;
import posemuckel.client.model.Webpage;

/**
 * Dies ist eine Event-Klasse f�r Ereignisse, die mit
 * dem Webtrace zu tun haben. Auf diese Weise k�nnen
 * recht einfach neue Webseiten �bergeben werden.
 * 
 * @author Posemuckel Team
 *
 */
public class WebTraceEvent {
//TODO �berarbeiten
	private String newurl;
	private String oldurl;
	private String newtitle;
	private String user;
	private Webpage previous;
	private Root root;
	
	/**
	 * Wird verwendet, wenn ein VISITING_Event auftritt.
	 * @param newurl die besuchte Webseite
	 * @param oldurl die ParentUrl
	 * @param newtitle der Titel der Webseite
	 * @param user der Anwender, der die Webseite besucht hat
	 * @param previous die Webpage der ParentUrl
	 */
	public WebTraceEvent(String newurl, String oldurl, String newtitle, String user, Webpage previous) {
		this.newurl = newurl;
		this.oldurl = oldurl;
		this.newtitle = newtitle;
		this.user = user;
		this.previous = previous;
	}
	
	/**
	 * Wird verwendet, um �ber die �nderung der Daten einer Webseite oder der Ver�nderung
	 * eines Wurzelelementes zu berichten. Zu den Daten einer Webseite geh�rt
	 * auch dessen ParentFolder
	 * @param root die betroffene Webseite oder Root
	 */
	public WebTraceEvent(Root root) {
		this.root = root;
	}
	
	/**
	 * Wird verwendet, um �ber eine neue Notiz oder eine VIEWING-Nachricht zu 
	 * berichten. 
	 * @param root die betroffene Webseite
	 * @param user der Anwender, der f�r die �nderung verantwortlich ist
	 */
	public WebTraceEvent(Webpage root, String user) {
		this.user = user;
		this.root = root;
	}
	
	/**
	 * Gibt die von der �nderung betroffene Root aus.
	 * @return betroffene Root
	 */
	public Root getRoot() {
		return root;
	}
	
	/**
	 * Gibt den Titel der besuchten Webseite aus.
	 * @return Titel der besuchten Webseite
	 */
	public String getTitle() {
		return newtitle;
	}
	
	/**
	 * Gibt die URL der betroffenen Webpage aus, falls diese vorhanden ist.
	 * @return die URL der betroffenen Webpage
	 */
	public String getURL() {
		if(newurl != null) return newurl;
		if(root!=null && root instanceof Webpage)return root.getName();
		return null;
	}
	
	/**
	 * Gibt die URL der Webseite aus, von der die neue Webseite besucht worden
	 * war.
	 * @return die URL der "Vaterseite"
	 */
	public String getPreviousURL() {
		return oldurl;
	}
	
	/**
	 * Gibt die Webseite, die als Vater betroffenen Webseite registriert ist, aus.
	 * @return die "Vaterseite"
	 */
	public Webpage getPrevious() {
		return previous;
	}
	
	/**
	 * Gibt den Anwendernamen des Users, der f�r die �nderungen verantwortlich
	 * ist, aus.
	 * @return der Nickname des Anwenders
	 */
	public String getUser() {
		return user;
	}

}
