/**
 * 
 */
package posemuckel.client.model.event;

import posemuckel.client.model.Project;
import posemuckel.client.model.ProjectList;

/**
 * Wird ausgelöst, wenn sich eine Projektliste oder ein Projekt geändert hat oder
 * eine Änderung auf der Database fehlgeschlagen ist.
 * @author Posemuckel Team
 *
 */
public class ProjectEvent {
	
	private ProjectList list;
	private Project project;
	private boolean succeeded;
	
	/**
	 * Wird verwendet, wenn sich ein Projekt verändert hat oder eine Änderung
	 * auf der Database fehlgeschlagen ist.
	 * @param source das veränderte Projekt
	 * @param succeeded true , wenn die Änderung in der Database durchgeführt werden 
	 * konnte
	 */
	public ProjectEvent(Project source, boolean succeeded) {
		this(null, source, succeeded);
	}
	
	/**
	 * Wird verwendet, wenn sich eine Projektliste verändert hat.
	 * @param source die veränderte Projektliste
	 * @param project das betroffene Projekt
	 * @param succeeded true , wenn die Änderung in der Database durchgeführt werden 
	 * konnte
	 */
	public ProjectEvent(ProjectList source, Project project, boolean succeeded) {
		this.project = project;
		list = source;
		this.succeeded = succeeded;
	}
	
	/**
	 * Gibt das Projekt aus, das von der Änderung betroffen ist, falls ein solches
	 * existiert.
	 * @return das betroffene Projekt oder null
	 */
	public Project getProjectSource() {
		return project;
	}
	
	/**
	 * Gibt die Projektliste aus, die von der Änderung betroffen ist, falls eine
	 * solche Liste existiert.
	 * @return die betroffene Projektliste oder null
	 */
	public ProjectList getListSource() {
		return list;
	}
	
	/**
	 * Gibt an, ob die Änderung in der Database erfolgreich war.
	 * @return true , falls die Änderung in der Database erfolgreich war
	 */
	public boolean isSucceeded() {
		return succeeded;
	}

}
