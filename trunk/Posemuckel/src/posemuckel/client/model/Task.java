/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

/**
 * Eine Task f&uuml;hrt eine Aufgabe aus, die mit der &Auml;nderung einer 
 * <code>Database</code> verbunden ist. Die Task ruft eine Methode der
 * <code>Database</code> auf und wird von der <code>Database</code> benachrichtigt,
 * sobald die Aufbabe erledigt ist.
 * 
 * @author Posemuckel Team
 *
 */
public abstract class Task {
	
	/**
	 * Erstellt einen Thread, in dem die Aufgabe dann ausgef&uuml;hrt wird. Diese
	 * Art der Ausf&uuml;hrung wird verwendet, wenn der Zugriff auf die <code>Database</code>
	 * sehr lange dauern kann.
	 * 
	 * @param task Aufgabenbeschreibung
	 */
	protected void launchInThread(final int task) {
		Thread worker = new Thread() {
			@Override
			public void run() {
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				work(task);
			}

		};
		worker.start();
	}
	
	/**
	 * In dieser Methode wird der Zugriff auf die <code>Database</code> ausgef&uuml;hrt.
	 * 
	 * @param task Aufgabenbeschreibung
	 */
	protected abstract void work(int task);
	
	/**
	 * Wird von der <code>Database</code> aufgerufen, um die Task &uuml;ber das Ergebnis
	 * eines Auftrags zu informieren. Die m&ouml;glichen Antworten sind in 
	 * <code>Database</code> zu finden.
	 * 
	 * @param answer Code der Antwort
	 */
	public abstract void update(int answer);
	
	/**
	 * Wird von der <code>Database</code> aufgerufen, um die Task &uuml;ber das Ergebnis
	 * eines Auftrages in Form einer Liste mit Ergebnisobjekten zu benachrichtigen.
	 * 
	 * @param list Liste mit den von der <code>Database</code> zur&uuml;ckgegebenen 
	 * 			Objekten.
	 */
	public abstract void update(ArrayList<?> list);
	
	/**
	 * Wird von der <code>Database</code> aufgerufen, um die Task &uuml;ber das Ergebnis
	 * eines Auftrages in Form eines Strings mit Ergebnisdaten zu benachrichtigen.
	 * 
	 * @param message Antwort der <code>Database</code>.
	 */
	public abstract void update(String message);
	
	/**
	 * Gibt an, ob die Aufgabe bei einem <code>ACCESS_DENIED</code> noch einmal
	 * ausgef&uuml;hrt werden soll oder ob der Auftraggeber benachrichtigt werden soll.
	 * 
	 * @return true, wenn der Auftraggeber von einem <code>ACCESS_DENIED</code>
	 * 			benachrichtigt werden soll
	 */
	public boolean relayACCESS_DENIED() {
		return false;
	}
	
	/**
	 * F&uuml;hrt die Aufgabe mit der angegebenen ID aus.
	 * 
	 * @param task Aufgabenbeschreibung, also die ID des Aufgabentyps.
	 */
	public void execute(int task) {
		if(DatabaseFactory.getRegistry() == null) return;
		if(DatabaseFactory.getRegistry().isHeavyTask()) {
			launchInThread(task);
		} else {
			work(task);
		}
	}

}
