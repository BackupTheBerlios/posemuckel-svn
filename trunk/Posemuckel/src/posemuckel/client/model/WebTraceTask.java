/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

/**
 * Regelt die Zugriffe auf die Database, die einen Bezug zum Webtrace haben.
 * Konkret werden die folgenden Nachrichten (nach RFC0815) bearbeitet:
 * 
 * <ul>
 * <li>GET_WEBTRACE</li>
 * <li>ADD_NOTE</li>
 * <li>GET_NOTES</li>
 * <li>VOTING</li>
 * <li>VIEWING</li>
 * </ul>
 * @author Posemuckel Team
 *
 */
//TODO Visiting verschieben
class WebTraceTask extends TaskAdapter {
	
	private int task;
	
	static final int VOTE = 100;

	static final int VIEWING = 200;
	
	static final int LOAD = 300;

	static final int GET_NOTES = 400;

	static final int ADD_NOTE = 500;
	
	private Webpage page;
	private Webtrace trace;
	private String data;
	
	/**
	 * Instanz, die die VOTING- und die ADD_NOTES-Nachricht bearbeiten kann.
	 * @param page die beurteilt wird
	 * @param data Rating oder Rating-Notiz-Kombination als String
	 */
	WebTraceTask(Webpage page, String data) {
		this.page = page;
		this.data = data;
	}
	
	/**
	 * Instanz zum Laden des gesamten Webtrace von der Database sowie zur Bearbeitung
	 * der VIEWING-Nachricht
	 * @param webtrace der bearbeitet werden soll
	 */
	WebTraceTask(Webtrace webtrace) {
		trace = webtrace;
	}
	
	/**
	 * Instanz zur Bearbeitung der GET_NOTES-Nachricht
	 * @param webtrace der über den Empfang der Notizen benachrichtigt wird
	 * @param url , zu der die Notizen geladen werden sollen
	 */
	WebTraceTask(Webtrace webtrace, String url) {
		trace = webtrace;
		data = url;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#work(int)
	 */
	@Override
	protected void work(int task) {
		this.task = task;
		switch (task) {
		case VOTE:
			DatabaseFactory.getRegistry().vote(page.getURL(), data);
			break;
		case VIEWING:
			DatabaseFactory.getRegistry().viewing(trace.getProject().getCurrentURL());
			break;
		case LOAD:
			DatabaseFactory.getRegistry().getWebtrace(this);
			break;
		case ADD_NOTE:
			DatabaseFactory.getRegistry().addNote(page.getURL(), data);
			break;
		case GET_NOTES:
			DatabaseFactory.getRegistry().getNotes(data, this);
			break;
		default:
			throw new IllegalArgumentException("unknown task");
		}
	}

	/**
	 * Bearbeitet die Daten, die von der Database geladen werden. Für die 
	 * Nachrichten GET_WEBTRACE und GET_NOTES wird eine ArrayList(String)
	 * als Antwort erwartet.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(ArrayList list) {
		switch (task) {
		case LOAD:
			//erwartet wird eine ArrayList mit Strings: der Webtrace dekodiert
			//das selber
			trace.confirmLoad(list);
			break;
		case GET_NOTES:
			trace.notesLoaded(list, data);
			break;
		default:
			break;
		}
	}
		
}
