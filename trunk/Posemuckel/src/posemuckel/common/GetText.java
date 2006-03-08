package posemuckel.common;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Diese Klasse stellt eine Reihe statischer Methoden für die Verarbeitung
 * von Texten bereit. Sie holt Strings zu Schlüsselwörtern aus der angegebenen
 * Textressource, kann Teilstrings ersetzen und fügt Zeilenumbrüche ab einer
 * angegebenen Zeilenlänge ein.
 */
public class GetText {

	/**
	 * Dient dem Laden von verschiedenen Sprachressourcen
	 */
	private static ResourceBundle messages;
	
	/**
	 * Name der Ressource
	 */
	private static String resourceName = null;
	
	/**
	 * Gibt die gewünschte Sprache an
	 */
	private static Locale lang = Locale.getDefault();
	
	/**
	 * Standardkonstruktor
	 */
	public GetText() {
	}
	
	/**
	 * Diese Methode liefert zu einem String-Array von Schlüsselwörtern
	 * einen String-Array der entsprechenden Texte aus der gesetzten Text-
	 * Ressource. Wenn keine Ressource gesetzt ist, wird die 
	 * Eingabe geliefert.
	 * 
	 * @param names Die Namen (Schlüsselwörter) der Texte.
	 * @return Liefert ein String-Array der Texte, sonst die Eingabe wieder zurück.
	 */
	public static String[] gettext(String[] names) {

		if ( resourceName == null ) {
			return names;
		}
		
		int length = names.length;
		String[] res = new String[length];
		try {
			messages = PropertyResourceBundle.getBundle(resourceName,lang);
			if ( messages != null ) {
				int i=0;
				for (String name : names) {
					res[i] = messages.getString(name);
					i++;
				}
			} else
				throw new MissingResourceException("Ressource not found.", resourceName, names[0]);
		} catch (MissingResourceException e) {			
			Logger.getLogger(GetText.class).warn("Problem to find Strings in i18n files.");
		}
		return res;
	}
	
	/**
	 * Diese Methode liefert zu einem Schlüsselwort
	 * den entsprechenden Text aus der gesetzten Text-
	 * Ressource. Wenn keine Ressource gesetzt ist, wird die Eingabe
	 * geliefert.
	 * 
	 * @param name Der Name (Schlüsselwort) des Textes.
	 * @return Der Text, null sonst.
	 */	
	public static String gettext(String name) {

		if ( resourceName == null ) {
			return name;
		}

		String res;
		try {
			messages = PropertyResourceBundle.getBundle(resourceName,lang);
			if ( messages != null ) {
				res = messages.getString(name);
			} else
				throw new MissingResourceException("Ressource not found.", resourceName, name);
		} catch (MissingResourceException e) {
			Logger.getLogger(GetText.class).warn("The Resource could not be found: " + name + " in " + resourceName);
			res = name;
		}
		return res;
	}

	/**
	 * Ersetzt das erste Vorkommen von replacethis in dem Text text durch
	 * den String replacewith.
	 * 
	 * @param text Der zu ändernde Text.
	 * @param replacethis Das Makro, welches als Platzhalter dient.
	 * @param replacewith Der Text, der statt des Makros eingesetzt werden soll.
	 * @return Der geänderte Text bzw. text, falls das Makro nicht vorkommt.
	 */	
	public static String macroreplace(String text, String replacethis, String replacewith) {
		StringBuffer buf = new StringBuffer(text);
		int startindex = buf.indexOf(replacethis);
		if ( startindex == -1 ) // replacethis konnte nicht gefunden werden 
			return text;
		buf.replace(startindex,startindex+replacethis.length(),replacewith);
		return buf.toString();
	}

	
	/**
	 * Ersetzt das jedes Vorkommen von replacethis in dem Text text durch
	 * den String replacewith. Dies ist die rekursive Variante von
	 * macroreplace.
	 * 
	 * @param text Der zu ändernde Text.
	 * @param replacethis Das Makro, welches als Platzhalter dient.
	 * @param replacewith Der Text, der statt des Makros eingesetzt werden soll.
	 * @return Der geänderte Text bzw. text, falls das Makro nicht vorkommt.
	 */	
	public static String macroreplace_recursive(String text, String replacethis, String replacewith) {
		StringBuffer buf = new StringBuffer(text);
		int index = buf.indexOf(replacethis);
		int end = 0;
		while (true) {
			index = buf.indexOf(replacethis,index);
			if( index == -1 ) break;
			end = index+replacethis.length();
			buf.replace(index,end,replacewith);
			index = end+1;
		}
		return buf.toString();
	}
	
	/**
	 * Faltet einen Text. Bei dem als tofold übergebenen String werden Zeilenumbrüche
	 * eingefügt, wenn eine Zeile länger als width ist. Dabei wird das erste Leerzeichen
	 * nach der angegebenen Breite durch einen Zeilenumbruch ersetzt. Es gibt also keine
	 * Garantie für eine maximale Zeilenlänge, da dies von der Länge der vorhandenen
	 * Wörter abhängt. 
	 * 
	 * @param tofold Der zu faltende Text.
	 * @param width Die "Wunschbreite".
	 * @return Der gefaltete Text.
	 */	
	public static String foldtext(String tofold, int width) {
		int lastindex = -1;
		int newindex = 0;
		StringBuffer buf = new StringBuffer(tofold);
		while ( newindex != -1 ) {
			newindex = buf.indexOf("\n",lastindex+1);
			// Wenn eine Zeile zu lang ist, dann muss sie zwischendrin
			// umgebrochen werden.
			if ( newindex - lastindex > width ) {
				// Suche das nächste Leerzeichen ab
				// lastindex+width
				newindex = buf.indexOf(" ",lastindex+width);
				// und ersetze es durch einen Zeilenumbruch:
				buf.replace(newindex,newindex+1,"\n");
				// Der neue String ist ein Zeichen länger:
				newindex++;
			}
			lastindex = newindex;
		}
		return buf.toString();
	}
	
	/**
	 * Hiermit kann in einem Programm die aktuell gesetzte Ressource
	 * gesichert werden, um kurzzeitig auf eine neue umzuschalten.
	 * 
	 * @return Die aktuelle Textressource.
	 */	
	public static String getResourceName() {
		return resourceName;
	}
	
	/**
	 * Dieser Setter liefert die alte Resource, die überschrieben wird. 
	 * 
	 * @param resname der neue Ressourcenname
	 * @return Der überschriebene Ressourcenname.
	 */
	public static String setResourceName(String resname) {
		String old = resourceName;
		resourceName = resname;
		return old;
	}
	
	/**
	 * Setzt die Sprache, in der Elemente der Benutzerschnittstelle
	 * angezeigt werden.
	 * 
	 * @param label Sprachkennung (unterstützt wird im Moment DE und EN)
	 */
	public static void setLanguage(String label) {		
		boolean b = false;
		int i = 0;
		//Prüfung, ob die Sprachkennung unterstützt wird
		while ((!b)&&(i<EnumsAndConstants.LANG.length)) {
			if (label.compareTo(EnumsAndConstants.LANG[i])==0) {
				b = true;
			}
			i++;
		}
		//Falls die Sprachkennung unterstützt wird, wird diese geladen
		if (b) {
			lang = new Locale(label.toLowerCase());
		}
	}	
		
	/**
	 * Ersetzt mehrfache Vorkommen eines Strings in einem Text.
	 * 
	 * @param text Text, in dem die Ersetzungen vorgenommen werden
	 * @param replaceThis diese Vorkommen sollen ersetzt werden
	 * @param replaceWith durch diesen String sollen die Vorkommen er-
	 * setzt werden
	 * @return den geänderten Text als String, in dem alle Ersetzungen
	 * durchgeführt wurden
	 */
	public static String replace(String text, String replaceThis, String replaceWith) {
		while(text.indexOf(replaceThis) > -1) {
			text = GetText.macroreplace(text, replaceThis, replaceWith);
		}
		return text;
	}
	
	/**
	 * Ersetzt Zeilenumbrüche durch Leerzeichen
	 * 
	 * @param input String, in dem die Ersetzungen durchgeführt werden
	 * @return den veränderten String
	 */
	public static String replaceRN(String input) {
		input = macroreplace_recursive(input, "\r\n", " ");
		input = macroreplace_recursive(input, "\r", " ");
		input = macroreplace_recursive(input, "\n", " ");
		return input;
	}
	
	/**
	 * Fügt vor kritischen Zeichen ein Escape-Symbol ein.
	 * 
	 * @param input Eingabetext
	 * @param escape_these Array von Strings, die als kritisch angesehen
	 * werden
	 * @param escape_with Escape-Symbol
	 * @return den veränderten Eingabetext als String
	 */
	public static String escape(String input, String[] escape_these, String escape_with) {
		for ( String s: escape_these ) {
			input = macroreplace_recursive(input, s, escape_with+s);
		}			
		return input;
	}
	
	/**
	 * Ruft ecsape speziell mit Argumenten für SQL auf.
	 * 
	 * @param input Eingabetext
	 * @return veränderter Eingabetext
	 */
	public static String escape_sql_write(String input) {
		if(input==null)
			return null;
		String[] escape_these = { "'" };
		return escape(input,escape_these,"\\");
	}
	
	/**
	 * Ruft ecsape speziell mit Argumenten für SQL auf.
	 * 
	 * @param input Eingabetext
	 * @return veränderter Eingabetext
	 */
	public static String escape_sql_read(String input) {
		if(input==null)
			return null;
		String[] escape_these = { "'", "%" };
		return escape(input,escape_these,"\\");
	}

	/**
	 * Ruft ecsape speziell mit Argumenten für SQL auf.
	 * 
	 * @param values Eingabetext
	 * @return veränderter Eingabetext
	 */
	public static String[] escape_sql_write(String[] values) {
		for ( int i=0; i<values.length; i++ )
			values[i]=escape_sql_write(values[i]);
		return values;
	}

	/**
	 * Ruft ecsape speziell mit Argumenten für SQL auf.
	 * 
	 * @param values Eingabetext
	 * @return veränderter Eingabetext
	 */
	public static String[] escape_sql_read(String[] values) {
		for ( int i=0; i<values.length; i++ )
			values[i]=escape_sql_read(values[i]);
		return values;
	}	
}
