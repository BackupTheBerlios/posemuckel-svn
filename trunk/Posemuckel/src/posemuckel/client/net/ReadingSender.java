package posemuckel.client.net;

import posemuckel.client.gui.Chat_InputComposite.TypingSemaphor;
import posemuckel.client.model.Chat;

/**
 * Dieser Thread dient dem Verzögerten Senden einer READING-Nachricht.
 * Die Kommunikation mit der GUI erfolgt über ein Objekt vom Typ
 * posemuckel.client.gui.Chat_InputComposite.TypingSemaphor
 * Es wird eine READING-Nachricht verschickt, wenn sich dieses
 * Objekt im Zustand "schreibend" befindet und der darin
 * hinterlegte Absendezeitpunkt erreicht bzw. überschritten ist.
 *  
 * @author Posemuckel Team
 */
public class ReadingSender extends Thread {

	private TypingSemaphor typing;
	private Chat chat;
	public ReadingSender(TypingSemaphor typing, Chat chat) {
		this.typing = typing;
		this.chat = chat;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	// Hier wird gearbeitet...
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {		
			synchronized(typing) {
				if(typing.isTyping()) {
					// Schreibe schon
					long diff = System.currentTimeMillis() - typing.getSendTime();
					if ( diff > 0 ) {
						// Zeit ist abgelaufen
						typing.setTyping(false);
						chat.userIsReading();
					} else {
						// Zeit noch nicht abgelaufen
						diff = -diff;
						try {
							typing.wait(diff);
						} catch (InterruptedException e) {
							break;
						}
					}
				} else
					// Der Benutzer liest gerade. Deshalb muss hier
					// auf unbestimmte Zeit gewartet werden.
					try {
						typing.wait();
					} catch (InterruptedException e) {
						break;
					}
			}
		}
	}

}
