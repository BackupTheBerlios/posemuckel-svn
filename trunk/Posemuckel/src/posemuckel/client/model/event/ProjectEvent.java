/**
 * 
 */
package posemuckel.client.model.event;

import posemuckel.client.model.Project;
import posemuckel.client.model.ProjectList;

/**
 * Wird ausgel�st, wenn sich eine Projektliste oder ein Projekt ge�ndert hat oder
 * eine �nderung auf der Database fehlgeschlagen ist.
 * @author Posemuckel Team
 *
 */
public class ProjectEvent {
	
	private ProjectList list;
	private Project project;
	private boolean succeeded;
	
	/**
	 * Wird verwendet, wenn sich ein Projekt ver�ndert hat oder eine �nderung
	 * auf der Database fehlgeschlagen ist.
	 * @param source das ver�nderte Projekt
	 * @param succeeded true , wenn die �nderung in der Database durchgef�hrt werden 
	 * konnte
	 */
	public ProjectEvent(Project source, boolean succeeded) {
		this(null, source, succeeded);
	}
	
	/**
	 * Wird verwendet, wenn sich eine Projektliste ver�ndert hat.
	 * @param source die ver�nderte Projektliste
	 * @param project das betroffene Projekt
	 * @param succeeded true , wenn die �nderung in der Database durchgef�hrt werden 
	 * konnte
	 */
	public ProjectEvent(ProjectList source, Project project, boolean succeeded) {
		this.project = project;
		list = source;
		this.succeeded = succeeded;
	}
	
	/**
	 * Gibt das Projekt aus, das von der �nderung betroffen ist, falls ein solches
	 * existiert.
	 * @return das betroffene Projekt oder null
	 */
	public Project getProjectSource() {
		return project;
	}
	
	/**
	 * Gibt die Projektliste aus, die von der �nderung betroffen ist, falls eine
	 * solche Liste existiert.
	 * @return die betroffene Projektliste oder null
	 */
	public ProjectList getListSource() {
		return list;
	}
	
	/**
	 * Gibt an, ob die �nderung in der Database erfolgreich war.
	 * @return true , falls die �nderung in der Database erfolgreich war
	 */
	public boolean isSucceeded() {
		return succeeded;
	}

}
