package posemuckel.common;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Diese Klasse stellt eine Reihe statischer Methoden f�r die Verarbeitung
 * von Texten bereit. Sie holt Strings zu Schl�sselw�rtern aus der angegebenen
 * Textressource, kann Teilstrings ersetzen und f�gt Zeilenumbr�che ab einer
 * angegebenen Zeilenl�nge ein.
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
	 * Gibt die gew�nschte Sprache an
	 */
	private static Locale lang = Locale.getDefault();
	
	/**
	 * Standardkonstruktor
	 */
	public GetText() {
	}
	
	/**
	 * Diese Methode liefert zu einem String-Array von Schl�sselw�rtern
	 * einen String-Array der entsprechenden Texte aus der gesetzten Text-
	 * Ressource. Wenn keine Ressource gesetzt ist, wird die 
	 * Eingabe geliefert.
	 * 
	 * @param names Die Namen (Schl�sselw�rter) der Texte.
	 * @return Liefert ein String-Array der Texte, sonst die Eingabe wieder zur�ck.
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
	 * Diese Methode liefert zu einem Schl�sselwort
	 * den entsprechenden Text aus der gesetzten Text-
	 * Ressource. Wenn keine Ressource gesetzt ist, wird die Eingabe
	 * geliefert.
	 * 
	 * @param name Der Name (Schl�sselwort) des Textes.
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
	 * @param text Der zu �ndernde Text.
	 * @param replacethis Das Makro, welches als Platzhalter dient.
	 * @param replacewith Der Text, der statt des Makros eingesetzt werden soll.
	 * @return Der ge�nderte Text bzw. text, falls das Makro nicht vorkommt.
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
	 * @param text Der zu �ndernde Text.
	 * @param replacethis Das Makro, welches als Platzhalter dient.
	 * @param replacewith Der Text, der statt des Makros eingesetzt werden soll.
	 * @return Der ge�nderte Text bzw. text, falls das Makro nicht vorkommt.
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
	 * Faltet einen Text. Bei dem als tofold �bergebenen String werden Zeilenumbr�che
	 * eingef�gt, wenn eine Zeile l�nger als width ist. Dabei wird das erste Leerzeichen
	 * nach der angegebenen Breite durch einen Zeilenumbruch ersetzt. Es gibt also keine
	 * Garantie f�r eine maximale Zeilenl�nge, da dies von der L�nge der vorhandenen
	 * W�rter abh�ngt. 
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
				// Suche das n�chste Leerzeichen ab
				// lastindex+width
				newindex = buf.indexOf(" ",lastindex+width);
				// und ersetze es durch einen Zeilenumbruch:
				buf.replace(newindex,newindex+1,"\n");
				// Der neue String ist ein Zeichen l�nger:
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
	 * Dieser Setter liefert die alte Resource, die �berschrieben wird. 
	 * 
	 * @param resname der neue Ressourcenname
	 * @return Der �berschriebene Ressourcenname.
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
	 * @param label Sprachkennung (unterst�tzt wird im Moment DE und EN)
	 */
	public static void setLanguage(String label) {		
		boolean b = false;
		int i = 0;
		//Pr�fung, ob die Sprachkennung unterst�tzt wird
		while ((!b)&&(i<EnumsAndConstants.LANG.length)) {
			if (label.compareTo(EnumsAndConstants.LANG[i])==0) {
				b = true;
			}
			i++;
		}
		//Falls die Sprachkennung unterst�tzt wird, wird diese geladen
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
	 * @return den ge�nderten Text als String, in dem alle Ersetzungen
	 * durchgef�hrt wurden
	 */
	public static String replace(String text, String replaceThis, String replaceWith) {
		while(text.indexOf(replaceThis) > -1) {
			text = GetText.macroreplace(text, replaceThis, replaceWith);
		}
		return text;
	}
	
	/**
	 * Ersetzt Zeilenumbr�che durch Leerzeichen
	 * 
	 * @param input String, in dem die Ersetzungen durchgef�hrt werden
	 * @return den ver�nderten String
	 */
	public static String replaceRN(String input) {
		input = macroreplace_recursive(input, "\r\n", " ");
		input = macroreplace_recursive(input, "\r", " ");
		input = macroreplace_recursive(input, "\n", " ");
		return input;
	}
	
	/**
	 * F�gt vor kritischen Zeichen ein Escape-Symbol ein.
	 * 
	 * @param input Eingabetext
	 * @param escape_these Array von Strings, die als kritisch angesehen
	 * werden
	 * @param escape_with Escape-Symbol
	 * @return den ver�nderten Eingabetext als String
	 */
	public static String escape(String input, String[] escape_these, String escape_with) {
		for ( String s: escape_these ) {
			input = macroreplace_recursive(input, s, escape_with+s);
		}			
		return input;
	}
	
	/**
	 * Ruft ecsape speziell mit Argumenten f�r SQL auf.
	 * 
	 * @param input Eingabetext
	 * @return ver�nderter Eingabetext
	 */
	public static String escape_sql_write(String input) {
		if(input==null)
			return null;
		String[] escape_these = { "'" };
		return escape(input,escape_these,"\\");
	}
	
	/**
	 * Ruft ecsape speziell mit Argumenten f�r SQL auf.
	 * 
	 * @param input Eingabetext
	 * @return ver�nderter Eingabetext
	 */
	public static String escape_sql_read(String input) {
		if(input==null)
			return null;
		String[] escape_these = { "'", "%" };
		return escape(input,escape_these,"\\");
	}

	/**
	 * Ruft ecsape speziell mit Argumenten f�r SQL auf.
	 * 
	 * @param values Eingabetext
	 * @return ver�nderter Eingabetext
	 */
	public static String[] escape_sql_write(String[] values) {
		for ( int i=0; i<values.length; i++ )
			values[i]=escape_sql_write(values[i]);
		return values;
	}

	/**
	 * Ruft ecsape speziell mit Argumenten f�r SQL auf.
	 * 
	 * @param values Eingabetext
	 * @return ver�nderter Eingabetext
	 */
	public static String[] escape_sql_read(String[] values) {
		for ( int i=0; i<values.length; i++ )
			values[i]=escape_sql_read(values[i]);
		return values;
	}	
}
