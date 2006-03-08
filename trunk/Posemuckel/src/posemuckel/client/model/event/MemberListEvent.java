/**
 * 
 */
package posemuckel.client.model.event;

import posemuckel.client.model.MemberList;
import posemuckel.client.model.Person;

/**
 * Wird ausgel�st, wenn sich der Zustand einer Mitgliederliste ge�ndert hat oder
 * ein Fehler von der Database gemeldet wurde.
 * 
 * @author Posemuckel Team
 *
 */
public class MemberListEvent {
	
	private MemberList source;
	private boolean succeeded;
	private Person person;
	
	/**
	 * 
	 * @param source Mitgliederliste, die sich ge�ndert hat
	 * @param succeeded true , wenn die �nderung in der Database durchgef�hrt werden konnte
	 * @param person Person, die von der �nderung betroffen ist (falls diese existiert)
	 */
	public MemberListEvent(MemberList source, boolean succeeded, Person person) {
		this.person = person;
		this.source = source;
		this.succeeded = succeeded;
	}

	/**
	 * Wenn eine einzelne Person hinzugef�gt oder gel�scht wurde, wird der Name 
	 * zur&uuml;ckgegeben. Ansonsten wird null zur&uuml;ckgegeben.
	 * @return Gibt person zur&uuml;ck.
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @return Gibt die betroffene MemberListe zur&uuml;ck.
	 */
	public MemberList getSource() {
		return source;
	}

	/**
	 * @return Gibt succeeded zur&uuml;ck.
	 */
	public boolean isSucceeded() {
		return succeeded;
	}
	
	

}
