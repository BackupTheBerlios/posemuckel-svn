/**
 * 
 */
package posemuckel.client.model.event;

import posemuckel.client.model.MemberList;
import posemuckel.client.model.Person;

/**
 * Wird ausgelöst, wenn sich der Zustand einer Mitgliederliste geändert hat oder
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
	 * @param source Mitgliederliste, die sich geändert hat
	 * @param succeeded true , wenn die Änderung in der Database durchgeführt werden konnte
	 * @param person Person, die von der Änderung betroffen ist (falls diese existiert)
	 */
	public MemberListEvent(MemberList source, boolean succeeded, Person person) {
		this.person = person;
		this.source = source;
		this.succeeded = succeeded;
	}

	/**
	 * Wenn eine einzelne Person hinzugefügt oder gelöscht wurde, wird der Name 
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
