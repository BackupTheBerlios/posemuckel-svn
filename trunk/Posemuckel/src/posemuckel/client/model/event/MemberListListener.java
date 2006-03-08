/**
 * 
 */
package posemuckel.client.model.event;

import java.util.ArrayList;

import posemuckel.client.model.Person;
import posemuckel.client.model.UsersPool;

/**
 * &Uuml;ber MemberListListener wird eine View &uuml;ber &Auml;nderungen der
 * Mitgliederliste benachrichtigt.
 * 
 * @author Posemuckel Team
 *
 */
public interface MemberListListener extends PosemuckelListener {
	
	/**
	 * Wird aufgerufen, wenn die Liste neu geladen wurde. Eine Liste kann 
	 * mehrmals geladen werden!
	 * 
	 * @param event Quelle des Events
	 */
	public abstract void listLoaded(MemberListEvent event);
	
	/**
	 * Wird aufgerufen, wenn ein neues Mitglied in die Liste eingef&uuml;gt wurde
	 * 
	 * @param event Zugang zur Quelle des Events
	 */
	public abstract void memberAdded(MemberListEvent event);
	
	/**
	 * Wird aufgerufen, wenn ein Mitglied aus der Liste entfernt wird.
	 * 
	 * @param event Zugang zur Quelle des Events
	 */
	public abstract void buddyDeleted(MemberListEvent event);
	
	/**
	 * Wird aufgerufen, wenn neue Daten zu einer Person geladen wurden.
	 * 
	 * @param event Zugang zur Quelle des Event
	 */
	public abstract void personsDataLoaded(PersonsEvent event);
	
	/**
	 * Wird aufgerufen, wenn sich die Eigenschaften eines Mitglieds der 
	 * Liste ver&auml;ndert haben.
	 * 
	 * @param person Person, deren Eigenschaften sich ver&auml;ndert haben
	 */
	public abstract void personChanged(PersonsEvent person);
	
	/**
	 * Wird beim Auftreten eines Fehlers aufgerufen. 
	 * 
	 * @param string Nachricht
	 */
	public abstract void error(String string);
	
	/**
	 * Wird aufgerufen, wenn die Suchergebnisse f&uuml;r eine Suchanfrage
	 * da sind.
	 * @param answer Liste mit den gefundenen Personen
	 * @param pool Liste mit allen im Model vorhandenen Personen
	 */
	public abstract void searchResults(ArrayList<Person> answer, UsersPool pool);

	
}
