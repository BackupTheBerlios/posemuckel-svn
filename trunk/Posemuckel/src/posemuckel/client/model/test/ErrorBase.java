package posemuckel.client.model.test;

import posemuckel.client.model.Database;
import posemuckel.client.model.Task;

/**
 * ErrorBase ist eine Variante von Mockbase, mit der das Verhalten von 
 * Objekten aus dem Model des Client bei Fehlermeldungen aus der Database
 * getestet werden kann.
 * 
 * @author Posemuckel Team
 *
 */
public class ErrorBase extends Mockbase {
	
	/**
	 * Gibt der Task die Fehlermeldung zurück.
	 * @param task Task, die eine Anfrage/Änderung der Database durchführen sollte
	 */
	private void error(Task task) {
		task.update(Database.ERROR);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.test.Mockbase#getBuddys(posemuckel.client.model.Task)
	 */
	@Override
	public void getBuddys(Task task) {
		error(task);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.test.Mockbase#addBuddy(java.lang.String, posemuckel.client.model.Task)
	 */
	@Override
	public void addBuddy(String name, Task task) {
		error(task);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.test.Mockbase#deleteBuddy(java.lang.String, posemuckel.client.model.Task)
	 */
	@Override
	public void deleteBuddy(String buddy, Task task) {
		error(task);
	}

}
