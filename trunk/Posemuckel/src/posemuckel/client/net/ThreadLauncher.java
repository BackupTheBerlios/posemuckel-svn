package posemuckel.client.net;

import java.util.Vector;

/**
 * Bietet die Möglichkeit, Threads an beliebiger Stelle
 * zu starten und dann von ganz anderen Programmteilen
 * aus zu beenden.
 * @author Posemuckel Team
 */
public class ThreadLauncher {

	private static ThreadLauncher Instance = null;
	private static Vector<Thread> threads;
	
	private ThreadLauncher() {
		threads = new Vector<Thread>();
	}

	public static ThreadLauncher getInstance(){
		if( Instance == null ) {
			Instance = new ThreadLauncher();
		}
		return Instance;
	}
	
	/**
	 * Startet einen Thread und speichert eine Referenz darauf,
	 * so dass dieser später wieder beendet werden kann.
	 * @param thread Der Thread, der gestartet werden soll.
	 */
	public void startThread(Thread thread){
		threads.add(thread);
		thread.start();
	}
	
	/**
	 * Unterbricht alle Threads, die über diese Klasse
	 * gestartet wurden.
	 */
	public static void interruptAll() {
		//wenn keine Verbindung hergestellt werden konnte, ist die Liste nicht
		//initialisiert!
		if(threads == null) return;
		for (Thread t : threads) {
			t.interrupt();
		}
	}
	
}
