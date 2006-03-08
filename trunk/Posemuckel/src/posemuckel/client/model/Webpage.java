/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;
import java.util.Set;

/**
 * Webpage enthält alle Daten und Funktionalitäten rund um eine Webseite im Webtrace.
 * 
 * @author Posemuckel Team
 *
 */
public class Webpage extends Root {
	
	/**
	 * Der Typ einer Webpage-Instanz.
	 */
	public static final String WEBPAGE = "WEBPAGE";
	
	/**
	 * Konstanten, die zum Vergleich von Webseiten mittels eines Komparators dienen
	 * können. Die Konstanten bezeichnen die wichtigsten Eigenschaften einer Webseite.
	 */
	
	public static final String RATING = "RATING";
	
	public static final String TITLE = "TITLE";
	
	public static final String URL = "URL";
	
	//public static final String MARKED = "MARKED";
	
	private String title;
	private boolean marked;
	private float rating;
	private ArrayList<String> fathers;
	private ArrayList<Visitor> visitors;
	private int parentFolderID;
	private Folder parentFolder;

	private boolean hasNote = false;
	
	/**
	 * Erstellt eine neue Webpage mit der angegebenen URL und dem angegebenen Title. 
	 * @param url die url der Webseite
	 * @param title der Titel der Webseite
	 * @param webtrace der Webtrace, zu dem die Webpage gehört
	 */
	public Webpage(String url, String title, Webtrace webtrace) {
		super(url, WEBPAGE, webtrace);	
		this.title = title;
		fathers = new ArrayList<String>();
		//es existiert mind. ein Visitor
		visitors = new ArrayList<Visitor>(1);
		//negative Werte stehen für fehlende Bewertung
		rating = -1f;
	}
	

	/**
	 * Gibt die URL der Webseite zur&uuml;ck.
	 * 
	 * @return URL der Webseite
	 */
	public String getURL() {
		return getName();
	}
	
	/**
	 * Gibt den Titel der Webseite aus.
	 * 
	 * @return Titel der Webseite
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Gibt an, ob der Anwender die Webseite markiert hat.
	 * 
	 * @return true, falls die Webseite markiert ist
	 */
	public boolean isMarked() {
		return marked;
	}
	
	/**
	 * Setzt die Markierung der Webseite.
	 * 
	 * @param marked true, falls die Webseite als markiert gelten soll.
	 */
	public void markWebpage(boolean marked) {
		this.marked = marked;
	}
	
	/**
	 * Gibt die Besucher der Webseite als Array aus.
	 * 
	 * @return Besucher der Webseite
	 */
	public Visitor[] getVisitors() {
		Visitor[] visitor = new Visitor[visitors.size()];
		return visitors.toArray(visitor);
	}
	
	/**
	 * Gibt die Webseiten, von denen diese Webseite aufgesucht wurde, aus.
	 * 
	 * @return ein Array mit Webseiten, von denen diese Webseite aus aufgesucht 
	 * wurde
	 */
	public Webpage[] getFathers() {	
		Webpage[] array = new Webpage[fathers.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = getWebpage(fathers.get(i));
		}
		return array;
	}
	
	/**
	 * Liefert zu einem Benutzernamen alle Webpages, die der Benutzer besucht
	 * hat und die eine Vaterurl dieser Webpage enthalten.
	 * @param user Beutzername
	 * @return alle Vaterpages dieser Webpage, die der Benutzer besucht hat
	 */
	public Webpage[] getFathers(String user) {
		ArrayList<Webpage> pages = new ArrayList<Webpage>();
		Webpage[] fathers = getFathers();
		for (int i = 0; i < fathers.length; i++) {
			if(fathers[i].getVisitorByName(user) != null) {
				pages.add(fathers[i]);
			}
		}
		Webpage[] result = new Webpage[pages.size()];
		return pages.toArray(result);
	}
	
	/**
	 * Gibt an, ob die Webseite von dem angegebenen Anwender bereits besucht wurde.
	 * @param name Name des Anwenders
	 * @return true, falls der Anwender die Seite bereits besucht hat
	 */
	public boolean hasVisitor(String name) {
		return isVisitedBy(name);
	}
	
	/**
	 * Sucht die URL im von dieser Webpage aus erreichbaren Teilbaum. Bei
	 * der Suche werden nur URLs berücksichtigt, die der Anwender besucht
	 * hat. Wenn die URL gefunden wurde, wird die Suche abgebrochen und
	 * true ausgegeben. Wenn die URL nicht gefunden wird, wird jede besuchte
	 * URL in das Set eingetragen, um Zyklen zu verhindern.
	 * @param url URL, die gesucht wird
	 * @param user Anwendername
	 * @param visited Set mit bereits besuchten Knoten des Graphen
	 * @return true, falls die URL gefunden wurde
	 */
	protected boolean hasURL(String url, String user, Set<String> visited) {
		if(url.equals(getURL())) return true;
		visited.add(getURL());
		Webpage[] children = getChildren();
		for (Webpage webpage : children) {
			if((!visited.contains(webpage.getURL())) && //kein Zyklus
					webpage.hasVisitor(user) && //die Webpage prüfen
					webpage.hasURL(url, user, visited)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sucht die URL im von dieser Webpage aus erreichbaren Teilbaum. Bei
	 * der Suche werden alle URLs berücksichtigt. Wenn die URL gefunden wurde, 
	 * wird die Suche abgebrochen und
	 * true ausgegeben. Wenn die URL nicht gefunden wird, wird jede besuchte
	 * URL in das Set eingetragen, um Zyklen zu verhindern.
	 * @param url URL, die gesucht wird
	 * @param visited Set mit bereits besuchten Knoten des Graphen
	 * @return true, falls die URL gefunden wurde
	 */
	protected boolean hasURL(String url, Set<String> visited) {
		//wegen der Verwendung von "" siehe auch hasVisitor(name) bzw. isVisitedBy(name)
		return hasURL(url, "", visited);
	}
	
	/**
	 * Teilt der Datenbank mit, dass der Anwender eine Bewertung der Webseite 
	 * vorgenommen hat.
	 * @param rating die Bewertung der Webseite
	 */
	public void vote(int rating) {
		new WebTraceTask(this, String.valueOf(rating)).execute(WebTraceTask.VOTE);
	}
	
	/**
	 * F&uuml;gt einen Besucher zu den Besuchern dieser Webseite hinzu. Wenn die 
	 * Seite bereits von dem Anwender besucht wurde, wird keine &Auml;nderung 
	 * vorgenommen.
	 * @param name der Name des Anwenders
	 * @param rating die Bewertung, die der Anwender vorgenommen hat
	 */
	protected void addVisitor(String name, short rating) {
		if(!isVisitedBy(name)) {
			//recalculate wird in der aufrufenden Methode gesetzt
			visitors.add(new Visitor(name, rating));
			visitors.trimToSize();
		}
	}
	
	/**
	 * Aktualisiert die Daten des Besuchers. Der Besucher muss die Webseite 
	 * bereits besucht haben.
	 * @param name des Besuchers
	 * @param rating Bewertung
	 * @param hasNotes 1, falls Berwertungen in der Database vorhanden sind
	 */
	protected void updateVisitor(String name, String rating, String hasNotes) {
		//von Webtrace aus verwendet
		updateVisitor(name, Integer.parseInt(rating), null, hasNotes.equals("1"));
	}
	
	/**
	 * Aktualisiert die Daten des Besuchers. Der Besucher muss die Webseite
	 * bereits besucht haben.
	 * @param name des Besuchers
	 * @param note Anmerkung des Besuchers
	 * @param rating Bewertung des Besuchers
	 */
	public void updateVisitor(String name, String note, int rating) {
		//von MockupModel aus
		updateVisitor(name, rating, note, true);
	}
	
	/**
	 * Aktualisiert die Daten des Besuchers. Der Besucher muss die Webseite bereits
	 * besucht haben.
	 * @param name des Besuchers
	 * @param note die Notiz
	 */
	protected void updateVisitor(String name, String note) {
		//zum Laden der Notizen
		updateVisitor(name, getVisitorByName(name).getRating(), note, true);
	}
	
	/**
	 * Diese Methode ist für die eigentliche Aktualisierung zuständig.
	 * @param name des Besuchers
	 * @param rating Bewertung
	 * @param note Notiz
	 * @param hasNote ist eine Notiz in der Database vorhanden?
	 */
	private void updateVisitor(String name, int rating, String note, boolean hasNote) {
		Visitor visitor = getVisitorByName(name);
		if(visitor != null) {
			visitor.setRating((short)rating);
			calculateRating();
			visitor.setComment(hasNote);
			visitor.setComment(note);
		}
	}
	
	/**
	 * Fügt einen neuen Besucher ohne Bewertung zu der Webpage hinzu.
	 * @param name Name des Besuchers
	 */
	public void addVisitor(String name) {
		if(!isVisitedBy(name)) {
			visitors.add(new Visitor(name));
			visitors.trimToSize();
		}
	}
	
	/**
	 * &Auml;ndert die Bewertung des Besuchers.
	 * @param name Name des Besuchers
	 * @param newRating die neue Bewertung
	 */
	public void changeRating(String name, short newRating) {
		Visitor visitor = getVisitorByName(name);
		boolean recalculate = true;
		if(visitor != null) {
			if(visitor.getRating() == newRating) {
				recalculate = false;
			} else {
				visitor.setRating(newRating);
			}
		} else {
			addVisitor(name, newRating);
		}
		if(recalculate) {
			calculateRating();
		}
	}
	
	/**
	 * Hilfsmethode, die die Gesamtbewertung anhand der Einzelbewertungen
	 * berechnet und in rating speichert.
	 *
	 */
	private void calculateRating() {
		Visitor[] visitors = getVisitors();
		int max = 0;
		int act = 0;
		for (Visitor visitor : visitors) {
			if(validRating(visitor)) {
				max += 5;
				act += visitor.getRating();
			}
		}
		if(max != 0) {
			rating = (1f*act)/(1f*max);
		}
	}
	
	/**
	 * Gibt an, ob die Bewertung eines Anwenders in die Gesamtbewertung einbezogen
	 * werden soll. Zur Zeit wird die Bewertung ausgenommen, wenn sie kleiner 0 ist, 
	 * da dies als Wert für eine nicht 
	 * @param visitor der Besucher
	 * @return true, falls die Bewertung einbezogen werden soll
	 */
	private boolean validRating(Visitor visitor) {
		return (visitor.getRating() >= 0);
	}
	
	/**
	 * F&uuml;gt eine Webseite zur Ansammlung der V&auml;ter hinzu.
	 * 
	 * @param url URL der Webseite, die auf diese Webseite verweist
	 */
	protected void addFather(String url) {
		if(!fathers.contains(url)) 
			fathers.add(url);
	}
	
	/**
	 * Gibt an, ob die Webseite von dem angegebenen Anwender bereits besucht wurde.
	 * @param name Name des Anwenders
	 * @return true, falls der Anwender die Seite bereits besucht hat
	 */
	public boolean isVisitedBy(String name) {
		if(name == null) return false;
		//Sonderfall: wird als interner code für alle Anwender verwendet
		if(name.equals("")) return true;
		for (Visitor visitor : visitors) {
			if(visitor.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sucht den Besucher mit dem angegebenen Namen heraus.
	 * 
	 * @param name Name des Besuchers
	 * @return den Besucher oder <code>null</code>, falls der Besucher nicht gefunden
	 * wurde
	 */
	public Visitor getVisitorByName(String name) {
		for (Visitor visitor : visitors) {
			if(visitor.getName().equals(name)) {
				return visitor;
			}
		}
		return null;
	}
		
	/**
	 * Fuuml;gt eine neue Notiz in der Database zu der Webpage hinzu.
	 * 
	 * @param text die Notiz
	 * @param rating die Bewertung; wenn der Anwender keine Bewertung abgeben 
	 * 	m&ouml;chte, dann kann -1 als Wert eingegeben werden.
	 */
	public void addNote(String text, int rating) {
		String data = String.valueOf(rating) + "\r\n" + text;
		new WebTraceTask(this, data).execute(WebTraceTask.ADD_NOTE);
	}
	
	/**
	 * Gibt an, ob es eine Webpage gibt, von der aus diese Webseite aufgesucht 
	 * wurde.
	 * @return true, falls eine Vaterurl gibt
	 */
	public boolean hasFather() {
		return !fathers.isEmpty();
	}
	
	/**
	 * Gibt an, ob die URL eine Webpage referenziert, von der aus diese Webseite aufgesucht 
	 * wurde.
	 * @param url des möglichen Vaters
	 * @return true, falls die URL eine Vaterurl ist
	 */
	public boolean hasFather(String url) {
		return fathers.contains(url);
	}
	
	/**
	 * Gibt die Gesamtbewertung der Webseite aus.
	 * @return die Gesamtbewertung
	 */
	public float getRating() {
		return rating;
	}
	
	/**
	 * Setzt den Titel der Webpage. Der Wert wird nicht in der Database gespeichert.
	 * @param title der Webseite
	 */
	public void setTitle(String title) {
		this.title = title; 
	}
	
	/**
	 * Setzt die Bewertung der Webseite. Die Bewertung wird nicht in der Database
	 * gespeichert.
	 * @param r Rating als Wert zwischen 0 und 1
	 */
	public void setRating(float r) {
		rating = r;
	}
	
	/**
	 * Gibt einen String, der alle Kommentare enthält, aus.
	 * @return String mit allen Kommentaren
	 */
	public String getComment() {
		StringBuffer buffer = new StringBuffer();
		for (Visitor visitor : visitors) {
			if(visitor.hasComment()) {
				buffer.append(visitor.getName());
				buffer.append(": ");
				buffer.append(visitor.getComment());
				buffer.append(" - ");
			}
		}
		if(buffer.length() > 0) {
			return buffer.toString();
		}
		return null;
	}
	
	/**
	 * Teilt der Webpage mit, dass Notizen von der Database geladen wurden.
	 * @param list mit den Daten gemäß RFC0815
	 */
	public void notesLoaded(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i+=2) {
			//Name, Note
			updateVisitor(list.get(i), list.get(i+1));
		}		
	}

	/**
	 * Liefert false, wenn kein einziger Besucher
	 * eine Notiz angehängt hat.
	 * @return false, falls keine Notizen vorhanden sind
	 */
	public boolean hasNote() {
		if( hasNote ) {
			return true;
		}
		for (Visitor visitor : visitors) {
			if(visitor.hasComment()) {
				this.hasNote = true;
				return true;
			}
		}
		return false;
	}

	//TODO wieso wird das gebraucht???
	protected void setHasNote(boolean b) {
		this.hasNote = true;
	}
	
	/**
	 * Gibt die ID des Folders aus, in dem die Webpage im FolderTree eingeordnet
	 * ist
	 * @return ID des ParentFolders
	 */
	public int getParentFolderID() {
		return parentFolderID;
	}
	
	/**
	 * Gibt eine Referenz auf den ParentFolder aus.
	 * @return Referenz auf den ParentFolder.
	 */
	public Folder getParentFolder() {
		return parentFolder;
	}
	
	/**
	 * Teilt der Webpage den ParentFolder im FolderTree mit.
	 * @param f der Folder, in den die Webpage eingeordnet wird
	 */
	protected void setParentFolder(Folder f) {
		parentFolder = f;
		if(f!= null) {
			parentFolderID = f.getMyID();
		} else {
			parentFolderID = 0;
		}
	}


	/*
	 * die Methode addChild ist in der Klasse Root implementiert
	 */
	

}
