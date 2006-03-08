/**
 * 
 */
package posemuckel.client.model.event;

/**
 * Ein Listener für die Hochhalten-Funktion.
 * @author Posemuckel Team
 *
 */
public interface NotifyListener extends PosemuckelListener {
	// Empfang einer Notifikationsnachricht.
	public abstract void notify(NotifyEvent event);
	// Reaktion auf die ACK-Nachricht vom Server.
	public abstract void ack();
	// Wird zum Setzen einer neuen URL im (Haupt-)
	// Browser benötigt.
	public abstract void newurl(String url);
	
}
