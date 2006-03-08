/**
 * 
 */
package posemuckel.client.model.event;

import java.util.ArrayList;

import posemuckel.client.model.Person;
import posemuckel.client.model.UsersPool;

/**
 * Diese Klasse implementiert alle Methoden des Interface <code>MemberListListener</code>
 * mit einem leeren Methodenrumpf. 
 * 
 * @author Posemuckel Team
 *
 */

public class MemberListAdapter implements MemberListListener {
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#listLoaded(posemuckel.client.model.event.MemberListEvent)
	 */
	public void listLoaded(MemberListEvent event) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#buddyAdded(posemuckel.client.model.event.MemberListEvent)
	 */
	public void memberAdded(MemberListEvent event) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#buddyDeleted(posemuckel.client.model.event.MemberListEvent)
	 */
	public void buddyDeleted(MemberListEvent event) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#personsDataLoaded(posemuckel.client.model.event.PersonsEvent)
	 */
	public void personsDataLoaded(PersonsEvent event) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#error(java.lang.String)
	 */
	public void error(String string) {		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#personChanged(posemuckel.client.model.event.PersonsEvent)
	 */
	public void personChanged(PersonsEvent person) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#searchResults(java.util.ArrayList, posemuckel.client.model.UsersPool)
	 */
	public void searchResults(ArrayList<Person> answer, UsersPool pool) {
	}

}
