/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

/**
 * 
 * Eine Instanz von <code>Root</code> stellt einen Einstiegspunkt f&uuml;r eine
 * Baumdarstellung des Webtrace dar. Als Kinder sind nur Webseiten, aber keine
 * Folder, erlaubt. Es gibt verschiedene Typen von Root:
 * <ul>
 * <li>USER_TYPE</li>
 * <li>CATEGORY_TYPE </li>
 * <li>MASTER</li>
 * </ul>
 * 
 * @author Posemuckel Team
 *
 */
public class Root {

	/**
	 * Eine Root vom Typ <code>USER_TYPE</code> enthält die Wurzelelemente f&uuml;r
	 * den Webtrace eines einzelnen Anwenders.
	 * 
	 */
	public static final String USER_TYPE = "USER";
	
	/**
	 * Eine Root vom Typ <code>CATEGORY_TYPE</code> enth&auml;lt die URLs eines 
	 * bestimmten Folders.
	 */
	public static final String CATEGORY_TYPE = "CAT";
	
	/**
	 * Das Wurzelelement für alle URLs.
	 */
	public static final String MASTER = "MASTER";
		
	private ArrayList<String> children;
	private String type;
	private String name;
	private Webtrace trace;
	
	/**
	 * Erstellt ein neues Wurzelelement des angegebenen Typs. Der Name des
	 * Wurzelelementes richtet sich nach dem Typ: f&uuml:r eine Kategorie wird
	 * der Titel der Kategorie erwartet, f&uuml;r den <code>USER_TYPE</code> wird
	 * der Name des Anwenders erwartet. Der Name des Wurzelelementes sollte Applikationsweit
	 * sein, damit eine Verwendung in HashMaps möglich ist.
	 * 
	 * @param name der Titel der Kategorie oder der Name des Anwenders
	 * @param type der Typ des Wurzelelementes
	 * @param webtrace zu dem der Wurzelknoten geh&ouml;rt
	 */
	protected Root(String name, String type, Webtrace webtrace) {
		children = new ArrayList<String>();
		this.type = type;
		this.name = name;
		trace = webtrace;
	}
	
	/**
	 * Konstruktor für die Benutzung in ResultsViewer.java. 
	 * @param name Bezeichner der Wurzel
	 */
	@Deprecated //der Folder bekommt keine Initialisierung für den Webtrace, was zu NullPointern führen kann
	public Root(String name) {
		//@author Posemuckel Team
		this(name, CATEGORY_TYPE, null);		
		//this.name = "ERROR, no name";
		
	}
	
	/**
	 * Gibt den Webtrace zu dem FolderTree aus. Der Webtrace ermöglicht die 
	 * Zuordnung zwischen den URLs und den Webseiten.
	 * @return Webtrace zu dem FolderTree
	 */
	protected Webtrace getTrace() {
		return trace;
	}
	
	/**
	 * Konstruktor für Folder.java und RatedWebpage.java. 
	 * 
	 * 
	 * @param id 
	 * @param url
	 * @param pfid
	 */
	protected Root(int id, String url, int pfid) {
		//@author Posemuckel Team
		this(url);		
	}
	
	/**
	 * Setzt die Kinder des Wurzelknotens. Es werden die Stringrepr&auml;sentationen
	 * der URLs erwartet. Es wird überprüft, ob das Kindelement bereits
	 * enthalten ist.
	 * @param urls urls der Kindelemente
	 */
	protected void addChildren(String[] urls) {
		for (String url : urls) {
			children.add(url);
		}
		children.trimToSize();
	}
	
	/**
	 * Fügt ein Kind zu dem Wurzelknoten hinzu. Es wird die Stringrepr&auml;sentationen
	 * der URL erwartet. Es wird überprüft, ob das Kindelement bereits
	 * enthalten ist.
	 * @param url das Kindelement
	 */
	protected void addChild(String url) {
		if(!children.contains(url)) {
//			if(type.equals(MASTER)) {
//				System.out.println("++: " + url);
//			}
			children.add(url);
		}
	}
	
	/**
	 * Gibt an, ob der Webtrace ein Kind mit der angegebenen URL hat.
	 * Für Webseiten wird die url erwartet.
	 * @param url des Kindes.
	 * @return true, falls ein Kind mit der angegebenen ID vorhanden ist
	 */
	public boolean hasChild(String url) {		
		return children.contains(url);
	}
	
	/**
	 * Entfernt die URL aus dieser Wurzel.
	 * @param url , die entfernt werden soll
	 */
	protected void removeChild(String url) {
//		if(type.equals(MASTER)) {
//			System.out.println("--: " + url);
//		}
		children.remove(url);
	}
	
	/**
	 * Entfernt alle Kindelemente dieser Wurzel.
	 *
	 */
	protected void removeAllChildren() {
		children = new ArrayList<String>();
	}
	
	/**
	 * Gibt eine Referenz auf das Projekt, zu dem dieses Wurzelelement gehört, aus.
	 * 
	 * @return das zugehörige Projekt
	 */
	protected Project getProject() {
		return trace.getProject();
	}
	
	/**
	 * Gibt den Typ des Wurzelelementes zur&uuml;ck.
	 * 
	 * @return der Typ des Wurzelelementes
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Der Name des
	 * Wurzelelementes richtet sich nach dem Typ: f&uuml:r einen Folder wird
	 * der Titel des Folders ausgegeben, f&uuml;r den <code>USER_TYPE</code> wird
	 * der Name des Anwenders ausgegeben und für eine URL wird die Stringrepräsentation
	 * der URL ausgegeben.
	 * 
	 * @return Name der Wurzel
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Setzt den Namen des Wurzelelementes. 
	 * @see posemuckel.client.model.Root#getName()
	 * @param name der Name des Wurzelelementes
	 */
	protected void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gibt die URLs der direkten Kindelemente des Wurzelelementes aus.
	 * 
	 * @return gibt die direkten Kindelemente der Wurzel aus.
	 */
	public Webpage[] getChildren() {
		Webpage[] pages = new Webpage[children.size()];
		for (int i = 0; i < pages.length; i++) {
			pages[i] = getWebpage(children.get(i));
		}
		return pages;
	}
	
	/**
	 * Gibt an, ob dieses Wurzelelement Kindelemente hat. 
	 * @return true, falls dieses Wurzelelement Kinder hat.
	 */
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	/**
	 * Übersetzt die gegebenene URL in eine Webpage.
	 * @param url Schlüssel zur Identifizierung der Webpage
	 * @return die Webpage zu der URL
	 */
	protected Webpage getWebpage(String url) {
		return trace.getPageForUrl(url);
	}

}
