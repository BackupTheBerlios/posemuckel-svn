/**
 * 
 */
package posemuckel.client.model.event;

import java.util.ArrayList;


/**
 * Generische Klasse zur Verwaltung einer Menge von PosemuckelListener.
 *  
 * @author Posemuckel Team
 *
 */
public class ListenerManagment<L extends PosemuckelListener> {
	
	private ArrayList<L> list;
	
	/**
	 * Erstellt eine neue Listenerverwaltung.
	 *
	 */
	public ListenerManagment() {
		list = new ArrayList<L>(0);
	}
	
	/**
	 * Fügt einen Listener in die Verwaltung ein. Wenn der Listener bereits
	 * vorhanden ist, wird er nicht noch einmal eingefügt. 
	 * @param listener der neue Listener
	 */
	public synchronized void addListener(L listener) {
		if(listener == null) {
			throw new NullPointerException();
		}
		if(!list.contains(listener)) {
			list.add(listener);
		}
	}
	
	/**
	 * Entfernt den Listener aus der Verwaltung.
	 * @param listener der entfernt werden soll
	 */
	public synchronized void removeListener(L listener) {
		if (listener==null) throw new NullPointerException();
		list.remove(listener);
	}
		
	/**
	 * Gibt eine <i>Kopie</i> der Liste mit den Listenern aus.
	 * @return Kopie der Liste mit den Listenern
	 */
	public synchronized ArrayList<L> getListener() {
		ArrayList<L> copy = new ArrayList<L>(list.size());
		for (L listener : list) {
			copy.add(listener);
		}
		return copy;
	}
	
	/**
	 * Sind Listener in der Verwaltung registriert?
	 * @return true, falls Listener vorhanden sind
	 */
	boolean isEmpty() {
		return list.size() == 0;
	}
	
}
