/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

/**
 * Stellt leere Implementierungen f&uuml;r die abstrakten Methoden von
 * <code>Task</code> zur Verf&uuml;gung.
 * 
 * @author Posemuckel Team
 *
 */
public class TaskAdapter extends Task {

	/* (non-Javadoc)
	 * @see posemuckel.client.model.Task#work(int)
	 */
	@Override
	protected void work(int task) {

	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(int)
	 */
	@Override
	public void update(int answer) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(java.util.ArrayList)
	 */
	@Override
	public void update(ArrayList<?> list) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(java.lang.String)
	 */
	@Override
	public void update(String message) {
	}

}
