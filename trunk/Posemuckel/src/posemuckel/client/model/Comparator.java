/**
 * 
 */
package posemuckel.client.model;


/**
 * Vergleicht zwei gleiche Objekte oder Attribute nach einem bestimmten Schema.
 * Diese Klasse enth‰lt Hilfsmethoden zum Vergleich von Integer, Float, Boolschen
 * Werten und Strings.
 * @author Posemuckel Team
 *
 */
public class Comparator {
	
	/**
	 * Nutzt die gleiche Semantik wie ein Stringvergleich (also lexikalische Anordnung).
	 * Der Vergleich wird in eine zweistellige Funktion umgewandelt.
	 * @see java.lang.String#compareTo(java.lang.String)
	 * @param one der erste String
	 * @param two der zweite String
	 * @return ein Wert kleiner 0, falls der erste String lexikalisch
	 * 			vor dem zweiten String angeordnet wird
	 */
	protected int compareStrings(String one, String two) {
		return one.compareTo(two);
	}
	
	/**
	 * Nutzt die gleiche Semantik, wie ein Stringvergleich, bei dem beide Strings
	 * nur aus Kleinbuchstaben bestehen.
	 * @see java.lang.String#compareToIgnoreCase(java.lang.String)
	 * @param one der erste String
	 * @param two der zweite String
	 * @return ein Wert kleiner 0, falls der erste String lexikalisch vor dem zweiten String
	 *		angeordnet wird; Groﬂ-und Kleinschreibung wird ignoriert
	 */
	protected int compareIgnoreCase(String one, String two) {
		return one.compareToIgnoreCase(two);
	}
	
	/**
	 * Vergleicht die Integer-Werte, die durch die Strings repr‰sentiert werden.
	 * @see java.lang.Integer#compareTo(java.lang.Integer)
	 * @see java.lang.Integer#parseInt(java.lang.String)
	 * @param one der erste Integer-Wert
	 * @param two der zweite Integer-Wert
	 * @throws NumberFormatException wenn einer der Werte nicht in ein Integer 
	 * umgewandelt werden kann
	 * @return -1, falls der erste Wert kleiner wie der zweite Werte ist
	 */
	protected int compareInteger(String one, String two) {
		return compareInteger(Integer.parseInt(one), Integer.parseInt(two));
	}
	
	/**
	 * Vergleicht die Float-Werte, die durch die Strings repr‰sentiert werden.
	 * @see java.lang.Float#parseFloat(java.lang.String)
	 * @see java.lang.Float#compare(float, float)
	 * @param one
	 * @param two
	 * @return -1, falls der erste Wert kleiner wie der zweite Wert ist
	 */
	protected int compareFloat(String one, String two) {
		return Float.compare(Float.parseFloat(one), Float.parseFloat(two));
	}
	
	/**
	 * Vergleicht die boolschen Werte. False ist kleiner wie true.
	 * @param one der erste boolesche Wert
	 * @param two der zweite boolsche Wert
	 * @return -1, falls der erste Wert kleiner wie der zweite Wert ist
	 */
	protected int compareBoolean(boolean one, boolean two) {
		int result = 0;
		if(one && !two) {
			result = 1;
		} else if((!one) && two) {
			result = -1;
		}
		return result;
	}
	
	/**
	 * Vergleicht die zwei float-Werte.
	 * @see java.lang.Float#compare(float, float)
	 * @param one der erste Wert
	 * @param two der zweite Wert
	 * @return ein Wert kleiner 0, falls der erste Wert kleiner wie der zweite Wert ist
	 */
	protected int compareFloat(float one, float two) {
		return Float.compare(one, two);
	}
	
	/**
	 * Vergleicht zwei integer-Werte.
	 * @see java.lang.Integer#compareTo(java.lang.Integer)
	 * @param one der erste Wert
	 * @param two der zweite Wert
	 * @return -1, falls der erste Wert kleiner wie der zweite Wert ist
	 */
	protected int compareInteger(int one, int two) {
		int result = 0;
		if(one < two) {
			result = -1;
		} else if(two < one) {
			result = 1;
		}
		return result;
	}

}
