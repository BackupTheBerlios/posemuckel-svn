/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import posemuckel.client.model.event.ListenerManagment;
import posemuckel.client.model.event.WebTraceEvent;
import posemuckel.client.model.event.WebTraceListener;

/**
 * Ein Webtrace enthält alle Webseiten, die innerhalb eines Projektes besucht wurden.
 * Zu jeder Webseite wird gespeichert, wer sie besucht hat (Visitor) und welche 
 * Webseiten auf diese Webseite verweisen. <br>
 * Pro Projekt gibt es nur einen Webtrace. Die Verläufe der einzelnen Anwender
 * werden durch verschiedene Wurzeln dargestellt. Bei den Kindern muss gefiltert
 * werden, ob die Seiten auch von dem Anwender besucht wurden.
 * 
 * @see posemuckel.client.model.Visitor
 * @author Posemuckel Team
 *
 */
public class Webtrace {
	
	private HashMap<String, Webpage> pages;
	private HashMap<String, Root> userRoots;
	private ListenerManagment<WebTraceListener> listenerManagment;
	private FolderTree folderTree;
	private Root masterRoot;
	private Project project;
	
	//wenn auf false gesetzt, werden die Listener bei addWebpage() nicht benachrichtigt
	private boolean notifyListener = true;
	private boolean loaded;
	
	/**
	 * Erstellt einen Webtrace zu dem Projekt. Pro Projekt sollte es nur einen 
	 * Webtrace geben.
	 * 
	 * @param project zu dem Webtrace
	 */
	public Webtrace(Project project) {
		pages = new HashMap<String, Webpage>();
		userRoots = new HashMap<String, Root>();
		masterRoot = new Root("", Root.MASTER, this);
		folderTree = new FolderTree(this);
		listenerManagment = new ListenerManagment<WebTraceListener>();
		this.project = project;
	}
	
	/**
	 * Der Webtrace enth&auml;lt f&uuml;r jeden Anwender ein eigenes Wurzelelement.
	 * 
	 * @param name des Anwenders
	 */
	protected void addUser(String name) {
		if(!userRoots.containsKey(name)) {
			userRoots.put(name, new Root(name, Root.USER_TYPE, this));
		}
	}
	
	/**
	 * L&auml;dt den gesamten Webtrace von der Database.
	 *
	 */
	public void load() {
		new WebTraceTask(this).execute(WebTraceTask.LOAD);
	}
	
	/**
	 * Gibt das Wurzelelement für den Verlauf eines Anwenders aus.
	 * @param userName Anwender
	 * @return Wurzel
	 */
	public Root getRootForName(String userName) {
		return userRoots.get(userName);
	}
	
	/**
	 * Gibt alle Wurzeln von Webtraces der Anwender aus. Wenn jemand das Projekt
	 * verlassen hat, nachdem er schon mit dem Sammeln von URLs begonnen hat, 
	 * wird er nicht mehr bei den Projektmitgliedern aufgeführt; es existiert aber
	 * noch ein Wurzelelement für ihn.
	 * 
	 * @return alle Wurzelelemente
	 */
	public Root[] getUserRoots() {
		Collection<Root> roots = userRoots.values();
		Root[] array = new Root[roots.size()];
		return roots.toArray(array);
	}
	
	/**
	 * Gibt die Wurzel des Trace für den Anwender aus.
	 * @param name des Anwenders
	 * @return Wurzel des Webtrace für diesen Anwender
	 */
	public Root getUserRoot(String name) {
		return userRoots.get(name);
	}
	
	/**
	 * F&uuml;gt eine neue Webpage in den Webtrace ein. Es wird davon ausgegangen, dass
	 * die vorhergehende URL bereits im Webtrace bekannt ist.
	 * 
	 * @param url der Webpage
	 * @param previousUrl der vorhergehenden Webseite
	 * @param title Titel der Webpage
	 * @param userName Anwender, der die Verbindung verfolgt hat
	 * @return die neu hinzugefügte Webseite
	 */
	protected Webpage addWebpage(String url, String previousUrl, String title, String userName) {
		if(previousUrl != null && previousUrl.equals(url)) {
			//das gibt sonst sehr kurze Zyklen!
			previousUrl = "";
		}
//		if(userName.equals("stephan")) {
//			System.out.println("add " + url);
//			System.out.println("prev "+ previousUrl);
//		}
		Webpage page = getPageForUrl(url);
		if(page == null) {
			page = addNewWebpage(url, previousUrl, title, userName);
		} else if(previousUrl == null || previousUrl.equals("")) {
			//Wurzelelement f&uuml;r den Anwender holen
			Root root = checkRoot(userName);
			if(!userTraceContainsURL(url, userName)) {
				root.addChild(url);
				//die master-Root wurde bereits beim ersten Eintrag manipuliert
			}
			page.addVisitor(userName);
			//den Titel aktualisieren
			page.setTitle(title);
		} else {
			//seite als besucht markieren
			page.addVisitor(userName);
			//den Titel aktualisieren
			page.setTitle(title);
			//Vaterknoten aktualisieren
			Webpage father = getPageForUrl(previousUrl);
			if(father == null) {
				father = createFather(previousUrl, userName);
			} else {
				father.addVisitor(userName);
			}			
			father.addChild(url);
			page.addFather(previousUrl);
			//die url hat jetzt einen Vaterknoten
			Root root = checkRoot(userName);
			if(root.hasChild(url)) {
				//hier muss sich eine Suche anschließen: wenn der Vater
				//nicht von der Wurzel des Anwenders aus erreichbar ist, wird
				//er in die Wurzel eingefügt
				root.removeChild(url);
				if(!userTraceContainsURL(previousUrl, userName)) {
					root.addChild(previousUrl);
				}
			}
		}
		checkMaster(url, previousUrl);
		return page;
	}
	
	/**
	 * Gibt an, ob der Trace des Users - ausgehend von der Wurzel - die URL enthält. 
	 * Es wird der gesamte
	 * von der UserRoot aus erreichbare Trace abgesucht. Diese Methode testet also, 
	 * ob eine URL im Trace eines Anwendenders von der Wurzel aus erreichbar ist.
	 * @param url gesuchte URL
	 * @param user Benutzername
	 * @return true, falls der Trace die URL enthält
	 */
	private boolean userTraceContainsURL(String url, String user) {
	//	System.out.println("::search for " + url);
		if( getPageForUrl(url) == null) {
			return false; //der Anwender hat die Webseite nicht besucht; sollte nicht vorkommen
		}
		if(getPageForUrl(url).hasVisitor(user)) {
			//System.out.println("** start search for " + url);
			Set<String> visited = new HashSet<String>();
			Webpage[] rootElements = getUserRoot(user).getChildren();
			for (Webpage page : rootElements) {
				if(page.hasURL(url, user, visited)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Prüft, ob zu einem Benutzernamen schon eine Wurzel existiert. Wenn dies nicht
	 * der Fall ist, wird eine Wurzel erzeugt. 
	 * @param name Benutzername
	 * @return Wurzelelement zu dem Benutzernamen
	 */
	private Root checkRoot(String name) {
		Root root = userRoots.get(name);
		if(root == null) {
			addUser(name);
			//die Wurzel einfügen, falls nötig
			root = userRoots.get(name);
		}
		return root;
	}
	
	/**
	 * Berechnet die Zusammensetzung der Root vom Typ MASTER, wenn eine
	 * neue URL in den Webtrace eingefügt wurde. Wenn sich die
	 * Zusammensetzung ändert, werden die Listener benachrichtigt.
	 * 
	 * @param url , die einen neuen Vater hat oder neue hinzugekommen ist
	 */
	private void checkMaster(String url) {
		if((!masterRoot.hasChild(url)) && (!getPageForUrl(url).hasFather())) {
			masterRoot.addChild(url);
			if(notifyListener)informAboutRootChange(masterRoot);
		}
	}
	
	/**
	 * Berechnet die Zusammensetzung der Root vom Typ MASTER, nachdem
	 * ein Kind der Wurzel einen neuen Vater bekommen hat. Wenn sich die
	 * Zusammensetzung ändert, werden die Listener benachrichtigt.
	 * 
	 * @param url , die einen neuen Vater hat oder neue hinzugekommen ist
	 */
	private void checkMaster(String url, String father) {
		//hat sich die Masterroot geändert?
		//neuer Vater
		if(masterRoot.hasChild(url) && father != null && getPageForUrl(url).hasFather()) {
			//prüfen, ob der Vater von den anderen Wurzelknoten aus erreichbar ist
			//if(!userTraceContainsURL())
			masterRoot.removeChild(url);
			Set<String> visited = new HashSet<String>();
			Webpage[] rootElements = masterRoot.getChildren();
			for (Webpage page : rootElements) {
				if(page.hasURL(url, visited)) {
					masterRoot.addChild(father);
					if(notifyListener)informAboutRootChange(masterRoot);
					break;
				}
			}

		}
	}
	
	/**
	 * Erstellt eine Webpage mit der angegebenen Url. Diese Situation kann dann eintreten, 
	 * wenn der Webtrace nicht vom Server geladen wurde.
	 * @param url des Vaters
	 * @param userName des Besuchers
	 * @return die erstellte Webpage
	 */
	private Webpage createFather(String url, String userName) {
		addNewWebpage(url, null, "", userName);
		if(notifyListener)informAboutNewVisit(url, null, "", userName);
		return getPageForUrl(url);
	}
	
	/**
	 * Baut den Webtrace aus den Daten auf. Die Zusammensetzung und die Reihenfolge
	 * der Daten ist im RFC0815 beschrieben. Die ArrayList enthält die übertragenen
	 * Daten ohne die Zeilenenden.
	 * @param data Liste mit den einzelnen Daten: aus jeder Zeile wird ein Datum 
	 * erstellt
	 */
	private void buildTrace(ArrayList<String> data) {
		notifyListener = false;
		Webpage page = null;
		for (int i = 0; i < data.size(); i+=6) {
			//if(data.get(i+3).equals("stephan"))
				//System.out.println("add " + data.get(i) + " for " + data.get(i+3));
			//url, previousUrl, title, visitor
			page = addWebpage(data.get(i), data.get(i+1), data.get(i+2), data.get(i+3));
			//visitor, rating, hasNotes
			page.updateVisitor(data.get(i+3), data.get(i+4), data.get(i+5));
		}
		notifyListener = true;
	}
	
	/**
	 * Berechnet die Wurzelelemente für den gesamten Webtrace. Es werden die
	 * Wurzelelemente der einzelnen Anwender genommen und die Elemente, die
	 * einen Vater haben, herausgefiltert.
	 *  
	 * @return Webpages, die als Wurzelelemente des gesamten Webtrace dienen
	 */
	public Root getRootElementForWholeTrace() {
		//die einzelnen Kinder der RootElemente holen
		//und prüfen, ob sie einen Vater haben
		ArrayList<Webpage> found = new ArrayList<Webpage>();
		Collection<Root> roots = userRoots.values();
		for (Root userRoot : roots) {
			Webpage[] children = userRoot.getChildren();
			for (Webpage webpage : children) {
				if((!webpage.hasFather()) && (!found.contains(webpage))) {
					found.add(webpage);
				}
			}
		}
		for (Webpage page : found) {
			masterRoot.addChild(page.getURL());
		}
		return masterRoot;
	}
	
	/**
	 * Fügt eine neue Webpage in den Trace ein. Die URL darf nicht bereits im Webtrace
	 * bekannt sein.
	 * @param url der Webpage
	 * @param previousUrl der vorhergehenden Webseite
	 * @param title Titel der Webpage
	 * @param userName Anwender, der die Verbindung verfolgt hat
	 */
	private Webpage addNewWebpage(String url, String previousUrl, String title, String userName) {
		Webpage page = new Webpage(url, title, this);
		page.addVisitor(userName);
		pages.put(url, page);
		//das Vaterelement aktualisieren
		if(previousUrl == null || previousUrl.equals("") ) {
//			Wurzelelemente aktualisieren
			Root root = checkRoot(userName);
			if(!userTraceContainsURL(url, userName)) {
				root.addChild(url);
				checkMaster(url);
			}
		} else {
			//Vaterknoten aktualisieren
			Webpage father = getPageForUrl(previousUrl);
			if(father == null) { //der Fall sollte eigentlich nicht auftreten
				father = createFather(previousUrl, userName);
			} else {
				father.addVisitor(userName);
			}
			father.addChild(url);
			page.addFather(previousUrl);
		}
		return page;
	}
	
	/**
	 * Gibt an, ob sowohl die Url als auch die Vorg&auml;ngerUrl von dem Anwender
	 * bereits besucht wurde und der Vorgänger Vater der Url ist.
	 * @param userName Anwendername
	 * @param url Url der Webseite
	 * @param previous Url der vorhergehenden Webseite
	 * @return true, falls beide Urls bereits bereits besucht wurden und eine 
	 * 		Vater-Kind-Beziehung zwischen beiden besteht
	 */
	public boolean hasURL(String userName, String url, String previous) {
		boolean known = false;
		Webpage from = getPageForUrl(url, userName);
		Webpage to = getPageForUrl(previous, userName);
		if((from != null) && (to != null)) {
			known = from.hasChild(previous);
			if(!known) {//Verbindung nicht bekannt
				known = to.hasChild(url);
			}
		}
		return known;
	}
	
	/**
	 * Gibt die Webpage zu der Url aus.
	 * @param url der Webpage
	 * @return die Webpage
	 */
	public Webpage getPageForUrl(String url) {
		return pages.get(url);
	}
	
	/**
	 * Gibt alle Webpages, die in diesem Webtrace enthalten sind, aus.
	 * @return alle Webpages in diesem Webtrace
	 */
	public Webpage[] getURLs() {
		Webpage[] array = new Webpage[pages.size()];
		return pages.values().toArray(array);
	}
	
	/**
	 * Gibt das Projekt zu diesem Webtrace aus.
	 * @return das Projekt zu diesem Webtrace
	 */
	protected Project getProject() {
		return project;
	}
	
	/**
	 * Gibt an, ob der Webtrace bereits von der Datenbank geladen wurde. 
	 * @return true, falls der Webtrace bereits geladen wurde
	 */
	public boolean isLoaded() {
		return loaded;
	}
	
	/**
	 * Gibt die Webpage zu der Url aus, falls die Url von dem angegebenen
	 * Anwender besucht wurde.
	 * @param url der Webpage
	 * @param userName des Anwenders
	 * @return die Webpage
	 */
	protected Webpage getPageForUrl(String url, String userName) {
		Webpage page = getPageForUrl(url);
		if((page != null) && (!page.isVisitedBy(userName))) {
			//Seite wurde von 'userName' nicht besucht
			page = null;
		}
		return page;
	}
	
	/**
	 * L&auml;dt die Notizen zu der URL vom Server.
	 * 
	 * @param url die URL, zu der die Notizen gefragt sind
	 *
	 */
	public void loadNotes(String url) {
		new WebTraceTask(this, url).execute(WebTraceTask.GET_NOTES);
	}
	
	/**
	 * Informiere alle Listener über eine neu aufgesuchte Webseite.
	 * @param newurl Url der neuen Seite
	 * @param oldurl Url der Seite, die einen Link auf die neue Seite hat oder der
	 * leere String
	 * @param title Titel der neuen Seite
	 * @param user Anwender, der dem Link gefolgt ist.
	 */
	protected void informAboutNewVisit(String newurl, String oldurl, String title, String user) {
		Webpage page = null;
		if(oldurl != null) page = getPageForUrl(oldurl);
		Person person = project.getMemberList().getMember(user);
		if(person != null)person.setURL(newurl);
		ArrayList<WebTraceListener> listener = getListener();
		for (WebTraceListener listListener : listener) {
			listListener.visiting(new WebTraceEvent(newurl, oldurl, title, user, page));
		}
	}
	
	/**
	 * Informiert alle Listener, dass ein Anwender eine Seite erneut besucht hat.
	 * @param user Anwender, der die Seite besucht
	 * @param url der Webseite
	 */
	protected void informAboutViewing(String user, String url) {
		Webpage page = getPageForUrl(url);
		Person person = project.getMemberList().getMember(user);
		person.setURL(url);
		ArrayList<WebTraceListener> listener = getListener();
		for (WebTraceListener listListener : listener) {
			listListener.viewing(new WebTraceEvent(page, user));
		}
	}
	
	/**
	 * Informiert den Webtrace über eine neue Notiz.
	 * @param user Verfasser der Notiz
	 * @param url auf die sich die Notiz bezieht
	 * @param rating Bewertung der Webseite
	 * @param withNote gibt an, ob eine Notiz dabei ist
	 */
	protected void informAboutNewNote(String user, String url, String rating, String withNote) {
		Webpage page = getPageForUrl(url);
		boolean ratingChanged = !rating.equals(page.getVisitorByName(user).getRatingAsString());
		page.updateVisitor(user, rating, withNote);
		if (withNote.equals("1")) {
			page.setHasNote(true);
		}
		ArrayList<WebTraceListener> listener = getListener();
		WebTraceEvent event = new WebTraceEvent(page, user);
		for (WebTraceListener listListener : listener) {
			listListener.newNote(event);
		}
		if(ratingChanged)informAboutChange(page);
	}
	
	/**
	 * Informiert alle Listener, dass sich die Zusammensetzung der Wurzel geändert hat.
	 * Diese Methode wird nur für Wurzeln des Typs MASTER gebraucht.
	 * @param root die betroffene Wurzel
	 */
	protected void informAboutRootChange(Root root) {
		ArrayList<WebTraceListener> listener = getListener();
		for (WebTraceListener listListener : listener) {
			listListener.rootChanged(new WebTraceEvent(root));
		}
	}
	
	/**
	 * Teilt den Listenern mit, dass sich die Daten der Webseite geändert haben.
	 * @param page die geänderte Webseite
	 */
	protected void informAboutChange(Webpage page) {
		ArrayList<WebTraceListener> listener = getListener();
		for (WebTraceListener listListener : listener) {
			listListener.elementChanged(new WebTraceEvent(page));
		}
	}
	
	/**
	 * Teilt dem Webtrace mit, dass die Daten vom Server geladen wurden. Die Listener
	 * des Webtrace werden benachrichtigt, dass die Daten geladen wurden.
	 * @param list mit den Daten als Strings
	 */
	protected void confirmLoad(ArrayList<String> list) {
		buildTrace(list);
		//System.out.println(getRootElementForWholeTrace().getChildren().length);
		loaded = true;
		ArrayList<WebTraceListener> listener = getListener();
		for (WebTraceListener listListener : listener) {
			listListener.traceLoaded(this);
		}
	}
	
	/**
	 * Teilt allen Listenern mit, dass die Anmerkungen zu der URL von der
	 * Datenbank geladen wurden.
	 * @param list Liste mit den geladenen Anmerkungen und Anwendernamen
	 * @param url URL, auf die sich die Notizen beziehen
	 */
	protected void notesLoaded(ArrayList<String> list, String url) {
		Webpage page = getPageForUrl(url);
		page.notesLoaded(list);
		ArrayList<WebTraceListener> listener = getListener();
		for (WebTraceListener listListener : listener) {
			listListener.notes(new WebTraceEvent(page));
		}
	}
	
	/**
	 * Gibt die Ordnerstruktur zu dem Webtrace aus.
	 * @return Ordnerstruktur
	 */
	protected FolderTree getFolderTree() {
		return folderTree;
	}
	
	/**
	 * Informiert alle Listener, dass sich die Ordnerzuordnung einer Webpage
	 * geändert hat.
	 * @param page die betroffene Webpage
	 */
	void fireFolderChanged(Webpage page) {
		ArrayList<WebTraceListener> listener = getListener();
		for (WebTraceListener listListener : listener) {
			listListener.parentFolderChanged(new WebTraceEvent(page));
		}
	}
	
	/**
	 * Registriert einen WebTraceListener, der über Ereignisse, die diese Instanz
	 * betreffen, informiert werden möchte. 
	 * @param listener der zu registrierende WebTraceListener
	 */
	public void addListener(WebTraceListener listener) {
		listenerManagment.addListener(listener);
	}

	/**
	 * Entfernt den WebTraceListener aus der Liste der WebTraceListener.
	 * @param listener der zu entfernende WebTraceListener
	 */
	public void removeListener(WebTraceListener listener) {
		listenerManagment.removeListener(listener);
	}
	
	/**
	 * Gibt eine Kopie der Liste mit allen WebTraceListenern aus.
	 * @return Kopie der Liste mit allen WebTraceListenern
	 */
	public ArrayList<WebTraceListener> getListener() {
		return listenerManagment.getListener();
	}
	
}
