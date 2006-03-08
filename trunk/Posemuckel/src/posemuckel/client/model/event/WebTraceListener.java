package posemuckel.client.model.event;

import posemuckel.client.model.Webtrace;

/**
 * Ein Listener, der dieses Interface implementiert, ist an
 * Ereignisse rund um Webtraces interessiert.
 * @author Posemuckel Team
 */
public interface WebTraceListener extends PosemuckelListener {
	
	/**
	 * Informiert alle Listener, dass ein Anwender eine neue Webseite besucht hat.
	 * @param event mit den Details des Ereignisses
	 */
	public abstract void visiting(WebTraceEvent event);
	
	/**
	 * Informiert alle Listener, dass sich die Kindelemente der im Event enthaltenen
	 * Root ge�ndert haben. 
	 * @param event mit der ge�nderten Wurzel
	 */
	public abstract void rootChanged(WebTraceEvent event);
	
	/**
	 * Informiert die Listener, dass sich die im Event enthaltene Webpage
	 * ge�ndert hat.
	 * 
	 * @param event event mit der Webpage
	 */
	public abstract void elementChanged(WebTraceEvent event);
	
	/**
	 * Informiert die Listener, dass ein Anwender jetzt eine andere Webseite ansieht.
	 * @param event event mit der Webpage und dem Anwendernamen
	 */
	public abstract void viewing(WebTraceEvent event);
	
	/**
	 * Teilt den Listenern mit, dass der Trace von der Database geladen wurde.
	 * @param webtrace 
	 */
	public abstract void traceLoaded(Webtrace webtrace);
	
	/**
	 * Teilt den Listenern mit, dass eine neue Notiz eingetroffen ist. Mit der
	 * neuen Notiz kann sich auch die Bewertung ge�ndert haben. In diesem Fall wird
	 * separat eine elementChanged-Nachricht versendet.
	 * @param event mit der Webpage und dem Anwendernamen
	 */
	public abstract void newNote(WebTraceEvent event);
	
	/**
	 * Teilt den Listenern mit, dass die Notizen zu einer Webseite geladen wurden.
	 * 
	 * @param event mit der Webpage
	 */
	public abstract void notes(WebTraceEvent event);
	
	/**
	 * Informiert alle Listener, dass sich der Folder der im Event enthaltenen
	 * Webpage ge�ndert hat.
	 * 
	 * @param event mit der Webpage
	 */
	public abstract void parentFolderChanged(WebTraceEvent event);
	
}
