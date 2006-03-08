/**
 * 
 */
package posemuckel.client.model.event;

/**
 * Ein Projektlistener wird informiert, wenn sich die Zusammensetzung der Projektliste
 * oder ein Element der Projektliste ver&auml;ndert hat. Der Projektlistener wird
 * immer in einer Projektliste als Listener eingetragen. Das Event in der Benachrichtigung
 * enth&auml;t sowohl eine Referenz auf die Liste als auch auf das betroffene Projekt, 
 * falls ein solches existiert.
 * 
 * @author Posemuckel Team
 *
 */
public interface ProjectListener extends PosemuckelListener {
	
	/**
	 * Wird aufgerufen, wenn ein neues Projekt in der Database erzeugt wurde.
	 * 
	 * @param event Event mit dem neuen Projekt
	 */
	public abstract void newProject(ProjectEvent event);
	
	/**
	 * Wird aufgerufen, wenn ein Projekt verlassen wurde.
	 * @param event Event mit der Projektliste des Users und dem betroffenen Projekt
	 */
	public abstract void deleteProject(ProjectEvent event);
	
	/**
	 * Wird aufgerufen, wenn die Projektliste geladen wurde.
	 * 
	 * @param event Event mit der Liste, die geladen wurde
	 */
	public abstract void listLoaded(ProjectEvent event);
	
	public abstract void accessDenied();
	
	/**
	 * Wird aufgerufen, wenn ein neues Projekt gestartet wurde.
	 * 
	 * @param event Event mit der Projektliste
	 */
	public abstract void confirmStartProject(ProjectEvent event);
	
	/**
	 * Wird aufgerufen, wenn ein Projekt ge&ouml;ffnet wurde.
	 * 
	 * @param event Event mit dem ge&ouml;ffneten Projekt
	 */
	public abstract void openProject(ProjectEvent event);
	
	/**
	 * Wird aufgerufen, wenn sich die Daten eines Projektes &auml;ndern.
	 * 
	 * @param event Event mit dem ge&auml;nderten Element
	 */
	public abstract void elementChanged(ProjectEvent event);

}
