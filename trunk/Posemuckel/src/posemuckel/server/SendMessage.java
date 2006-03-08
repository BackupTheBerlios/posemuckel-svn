package posemuckel.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

import posemuckel.server.Model.QueueItem;

/**
 * Objekte dieser Klasse bilden einen Hintergrundthread, der Nachrichten
 * aus einer Warteschlange holt und diese an die angegebenen Clients
 * überträgt.
 * 
 * @author Posemuckel Team
 *
 */
public class SendMessage implements Runnable {

	/**
	 * Sendewarteschlange
	 */
	private Vector<Model.QueueItem> sendqueue;
	
	/**
	 * Model
	 */
	private Model model;
	
	/**
	 * Konstruktor über die Warteschlange des Models
	 *
	 */
	public SendMessage() {
		super();
		model = Model.getInstance();
		this.sendqueue = model.getSendqueue();
	}

	/**
	 * Schaut, ob ein Element in der Warteschlange ist. Falls ja, wird
	 * die enthaltene Nachricht an die angegebenen Clients übertragen
	 * und das Element aus der Warteschlange entfernt.
	 */
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			synchronized (sendqueue) {
				if (sendqueue.size() < 1) {
					try {
						sendqueue.wait();
					} catch (InterruptedException e) {
						break;
					}
				}					
				sendToClient((Model.QueueItem)sendqueue.elementAt(0));
				sendqueue.removeElementAt(0);

			}
		}		
	}
	
	/**
	 * Sendet eine Nachricht an alle angegebenen Empfänger
	 * @param item Element der Sendewarteschlange
	 */
	private void sendToClient(QueueItem item) {
		if (item.recievers == null)
			return;
		for( ClientInfo ci : item.recievers ) {
			PrintWriter out;
			try {
				out = new PrintWriter(
				    new BufferedWriter(
				      new OutputStreamWriter(
				        ci.getSocket().getOutputStream())), true);
	            out.print(item.message);
	            out.flush();
			} catch (IOException e) {
				System.err.println("An den Client "+ci.getSocket()+" von Benutzer "+ci.getUserName()+" kann nicht gesendet werden!");
				e.printStackTrace();
			}
		}
	}
}
