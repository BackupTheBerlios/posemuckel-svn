/**
 * 
 */
package posemuckel.client.model;

/**
 * Vergleicht die Webseiten nach ihren wichtigsten Eigenschaften.
 * Es kann nach den Eigenschaften
 * <ul>
 * <li>Webpage.RATING</li>
 * <li>Webpage.TITLE</li>
 * <li>Webpage.URL</li>
 * </ul>
 * verglichen werden.
 * @author Posemuckel Team
 *
 */
public class WebpageComparator extends Comparator {
	
	private String type;
	
	/**
	 * Erstellt einen neuen Vergleicher für Webseiten, der nach dem angegebenen
	 * Kriterium vergleicht.
	 * Es kann nach den Kriterien 
	 * <ul>
	 * <li>Webpage.RATING</li>
	 * <li>Webpage.TITLE</li>
	 * <li>Webpage.URL</li>
	 * </ul>
	 * verglichen werden.
	 * @param type das Vergleichskriterium
	 */
	public WebpageComparator(String type) {
		if(type.equals(Webpage.RATING) || type.equals(Webpage.TITLE) || type.equals(Webpage.URL)) {
			this.type = type;
		} else {
			throw new IllegalArgumentException("I dont know how to deal with " + type);
		}
	}
	
	/**
	 * Vergleicht die zwei Webseiten nach dem angegebenen Kriterium.
	 * Wenn als Vergleichskriterium Webpage.TITLE oder Webpage.URL verwendet wird, 
	 * wird auf dem Vergleichskriterium die lexikalische Ordnung ohne 
	 * Berücksichtigung von Groß-und Kleinschreibung verwendet.
	 * Wenn als Vergleichskriterium Webpage.RATING verwendet wird, wird eine
	 * Webseite mit höherem Rating vor einer Webseite mit niedrigerem Rating
	 * angeordnet.
	 * @param one die erste Webseite
	 * @param two die zweite Webseite
	 * @return ein Wert kleiner 0, wenn die erste Webseite vor der zweiten Webseite angeordnet wird
	 */
	public int compare(Webpage one, Webpage two) {
		if(type.equals(Webpage.RATING)) 
			//absteigend sortieren 
			return compareFloat(two.getRating(), one.getRating());
		if(type.equals(Webpage.TITLE)) 
			return compareIgnoreCase(one.getTitle(), two.getTitle());
		//(type.equals(Webpage.URL)) {
		return compareIgnoreCase(one.getURL(), two.getURL());
	}
	
}
