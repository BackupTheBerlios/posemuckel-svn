/**
 * 
 */
package posemuckel.client.model.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


/**
 * Simuliert eine Tabelle in einer Datenbank.
 * 
 * @author Posemuckel Team
 *
 */
class Table {
	
	/**
	 * Daten werden in einer HashMap gespeichert.
	 */
	private HashMap<String, ArrayList> table;
	
	/**
	 * Eine neue Tabelle zur Datenspeicherung.
	 *
	 */
	Table() {
		table = new HashMap<String, ArrayList>();
	}
	
	/**
	 * Gibt den Datensatz, der zu dem Schl�ssel passt, aus. Wenn kein solcher
	 * Datensatz existiert, wird <code>null</code> ausgegeben.
	 * @param key Schl�ssel
	 * @return Datensatz in einer ArrayList
	 */
	ArrayList lookUp(String key) {
		return table.get(key);
	}
	
	/**
	 * F�gt einen Datensatz in die Tabelle ein. Wenn unter dem Schl�ssel bereits
	 * ein Datensatz gespeichert ist, wird dieser �berschrieben.
	 * @param key Schl�ssel
	 * @param list Datensatz
	 */
	void put(String key, ArrayList list) {
		table.put(key, list);
	}
	
	/**
	 * Sucht den Inhalt der Zelle, die in dem zum Schl�ssel geh�renden Datensatz
	 * an der Stelle index steht.
	 * @param key Schl�ssel
	 * @param index Positionsangabe
	 * @return Inhalt der Zelle
	 */
	Object lookUp(String key, int index) {
		ArrayList list = lookUp(key);
		return (list==null) ? "" : list.get(index);
	}
	
	/**
	 * Entfernt den zum Schl�ssel geh�renden Datensatz aus der Tabelle.
	 * 
	 * @param key Schl�ssel
	 */
	void remove(String key) {
		table.remove(key);
	}
	
	/**
	 * Ist die Tabelle leer?
	 * @return true , falls die Tabelle leer ist
	 */
	boolean isEmpty() {
		return table.isEmpty();
	}
	
	/**
	 * �ndert den Eintrag in einer Zelle der Tabelle. Die Zelle geh�rt zum Datensatz
	 * mit dem angegebenen Schl�ssel und sie liegt in der durch den Index gekennzeichneten
	 * Spalte. (Der Schl�ssel wird bei der Indexangabe nicht mitgerechnet)
	 * @param key Schl�ssel
	 * @param index Index
	 * @param value neuer Wert
	 */
	@SuppressWarnings("unchecked") void set(String key, int index, Object value) {
		ArrayList list = lookUp(key);
		if(list != null) {
			list.set(index, value);
		}
	}
	
	/**
	 * Sucht die erste Zeile (bzw. den Datensatz), die in der Spalte index den
	 * Wert value eingetragen hat.
	 * 
	 * @param value Wert des gesuchten Objects
	 * @param index Spalte in der Tabelle
	 * @return die gesuchte Zeile
	 */
	ArrayList search(Object value, int index) {
		ArrayList values = (ArrayList) table.values();
		boolean found = false;
		ArrayList row = null;
		int i = 0;
		while(!found && i < values.size()) {
			row = (ArrayList)values.get(i++);
			if(row.get(index).equals(value)) found = true;
		}
		return found ? row : null;
	}
	
	/**
	 * Gibt die Datens�tze in einer Collection aus.
	 * @return Datens�tze aus der Tabelle
	 */
	Collection<ArrayList> getAsList() {
		return table.values();
	}


}
