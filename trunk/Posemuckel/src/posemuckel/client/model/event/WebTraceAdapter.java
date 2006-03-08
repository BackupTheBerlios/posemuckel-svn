/**
 * 
 */
package posemuckel.client.model.event;

import posemuckel.client.model.Webtrace;

/**
 * Ist für Leute, die keine leeren Methoden in ihren Listenern mögen.
 * @author Posemuckel Team
 *
 */
public class WebTraceAdapter implements WebTraceListener {

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.WebTraceListener#visiting(posemuckel.client.model.event.WebTraceEvent)
	 */
	public void visiting(WebTraceEvent event) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.WebTraceListener#rootChanged(posemuckel.client.model.event.WebTraceEvent)
	 */
	public void rootChanged(WebTraceEvent event) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.WebTraceListener#elementChanged(posemuckel.client.model.event.WebTraceEvent)
	 */
	public void elementChanged(WebTraceEvent event) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.WebTraceListener#viewing(posemuckel.client.model.event.WebTraceEvent)
	 */
	public void viewing(WebTraceEvent event) {
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.WebTraceListener#traceLoaded(posemuckel.client.model.Webtrace)
	 */
	public void traceLoaded(Webtrace webtrace) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.WebTraceListener#newNote(posemuckel.client.model.event.WebTraceEvent)
	 */
	public void newNote(WebTraceEvent event) {
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.WebTraceListener#notes(posemuckel.client.model.event.WebTraceEvent)
	 */
	public void notes(WebTraceEvent event) {
	}
	
	/*
	 * @see posemuckel.client.model.event.WebTraceListener#parentFolderChanged(posemuckel.client.model.event.WebTraceEvent)
	 */
	public void parentFolderChanged(WebTraceEvent event) {}

}
