package posemuckel.client.model;

/**
 * Vergleicht Projekte nach einer der wichtigsten Eigenschaften. Als 
 * Vergleichskriterium können
 * <ul>
 * <li>Project.TOPIC</li>
 * <li>Project.OWNER</li>
 * <li>Project.TYPE</li>
 * <li>Project.DATE</li>
 * <li>Project.FREE</li>
 * <li>Project.NO</li>
 * </ul>
 * 
 * dienen.
 * @author Posemuckel Team
 *
 */
public class ProjectComparator extends Comparator {
	
	private String type;
	
	/**
	 * Erstellt einen Vergleicher für Projekte. Als 
	 * Vergleichskriterium können
	 * <ul>
	 * <li>Project.TOPIC</li>
	 * <li>Project.OWNER</li>
	 * <li>Project.TYPE</li>
	 * <li>Project.DATE</li>
	 * <li>Project.FREE</li>
	 * <li>Project.NO</li>
	 * </ul>
	 * 
	 * dienen.
	 * @param property Eigenschaft, nach der verglichen werden soll
	 */
	public ProjectComparator(String property) {
		if(property.equals(Project.TOPIC) || property.equals(Project.OWNER) || 
				property.equals(Project.TYPE)|| property.equals(Project.DATE)
				|| property.equals(Project.FREE)|| property.equals(Project.NO)) {
			this.type = property;
		} else {
			throw new IllegalArgumentException("I dont know how to deal with " + property);
		}
	}
	
	/**
	 * Vergleicht die Projekte nach dem angegebenen Kriterium. Wenn nach 
	 * Project.TOPIC oder Project.OWNER verglichen werden soll, wird auf der entsprechenden
	 * Eigenschaft die lexikalische Ordnung unter Ignorierung von Groß-und Kleinschreibung 
	 * verwendet. Wenn als Vergleichskriterium Project.DATE verwendet wird, werden
	 * die Projekte nach Erzeugungsdatum aussteigend sortiert. Bei Verwendung
	 * von Project.FREE werden die Projekte nach absteigener Zahl der freien
	 * Plätze sortiert, während bei Verwendung von Project.NO die Projekte nach
	 * aufsteigender Folge der Gesamtplätze sortiert werden.
	 * Wenn als Vergleichskriterium Project.property verwendet wird, kommen öffentliche vor
	 * privaten Projekten.
	 * 
	 * @param one das erste Projekt
	 * @param two das zweite Projekt
	 * @return ein Wert kleiner 0, wenn das erste Projekt vor dem zweiten Projekt
	 * angeordnet werden soll
	 */
	public int compare(Project one, Project two) {
		if(type.equals(Project.TOPIC)) 
			return compareIgnoreCase(one.getTopic(), two.getTopic());
		if(type.equals(Project.OWNER)) 
			return compareIgnoreCase(one.getOwner(), two.getOwner());
		if(type.equals(Project.TYPE)) 
			return compareBoolean(two.isPublic(), one.isPublic());
		if(type.equals(Project.DATE)) 
			return compareInteger(one.getID(), two.getID());
		if(type.equals(Project.FREE)) 
			//viele freie Plätze zuerst anzeigen
			return compareInteger(two.getFreeSeats(), one.getFreeSeats());
		//(type.equals(Project.NO))
			return compareInteger(one.getMaxNumber(), two.getMaxNumber());
	}
	
}
