/**
 * 
 */
package posemuckel.client.model.event;

import posemuckel.client.model.Person;

/**
 * Ein PersonsEvent tritt ein, wenn sich eine Person oder das Wissen, das
 * im Model &uuml;ber die Person vorhanden ist, &auml;ndert. Dies kann bei
 * Status&auml;nderungen oder bei &Auml;nderungen der Personendaten der Fall
 * sein.
 * 
 * @author Posemuckel Team
 *
 */
public class PersonsEvent {
	
	private Person source;
	
	/**
	 * Event zur Änderung des Zustands einer Person.
	 * @param source Person, deren Zustand sich geändert hat
	 */
	public PersonsEvent(Person source) {
		this.source = source;
	}
	
	/**
	 * @return Gibt die Quelle des Event zur&uuml;ck.
	 */
	public Person getSource() {
		return source;
	}



}
