/**
 * 
 */
package posemuckel.client.model.event;

/**
 * Der Adapter liefer leere Methodenr&uuml;mpfe f&uuml;r die Methoden von 
 * <code>ProjectListener</code>. Das spart eine Menge leerer Methoden in 
 * der GUI ;-)
 * 
 * @author Posemuckel Team
 *
 */
public class ProjectListenerAdapter implements ProjectListener {

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.ProjectListener#newProject(posemuckel.client.model.event.ProjectEvent)
	 */
	public void newProject(ProjectEvent event) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.ProjectListener#leaveProject(posemuckel.client.model.event.ProjectEvent)
	 */
	public void deleteProject(ProjectEvent event) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.ProjectListener#listLoaded(posemuckel.client.model.event.ProjectEvent)
	 */
	public void listLoaded(ProjectEvent event) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.ProjectListener#accessDenied()
	 */
	public void accessDenied() {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.ProjectListener#confirmStartProject(posemuckel.client.model.event.ProjectEvent)
	 */
	public void confirmStartProject(ProjectEvent event) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.ProjectListener#openProject(posemuckel.client.model.event.ProjectEvent)
	 */
	public void openProject(ProjectEvent event) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.ProjectListener#elementChanged(posemuckel.client.model.event.ProjectEvent)
	 */
	public void elementChanged(ProjectEvent event) {		
	}

}
