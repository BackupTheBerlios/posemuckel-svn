package posemuckel.client.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import org.apache.log4j.Logger;

import posemuckel.common.ForkedWriter;

/**
 * Dies ist diejenige Klasse, die Nachrichten aus einem Vector,
 * der als Sendewarteschlange dient, entnimmt und diese dann 
 * sendet. Hierzu muss eine Referenz auf den Vektor übergeben
 * werden. Diese Klasse implementiert ein Runnable, weil das
 * Senden von Nachrichten in einem separaten Thread erfolgen
 * soll. Besser ist das.
 * 
 * @author Posemuckel Team
 */
public class SendMessage implements Runnable
{
  private Socket socket;
  private Vector sendqueue;
  private static Logger logger = Logger.getLogger(SendMessage.class);
  
  /**
   * Konstruktor - du weißt schon.  ;-)
   * @param sendqueue Nachrichtenwarteschlange
   */
  public SendMessage(Vector sendqueue) {
	  this.sendqueue = sendqueue;
  }
  
	/**
	 * Setzt den Socket, der zum Senden verwendet werden soll.
	 * @param socket Socket zum Senden der Nachrichten
	 */
  protected void setSocket(Socket socket) {
	  this.socket = socket;
  }
    
  /**
   * Sendet eine Nachricht über den Socket.
   * @param str Die zu sendende Nachricht.
   */
  private String sendToServer(String str)
  {
    //AbstractOutput.output("sending to server: " + str, AbstractOutput.MISC_MSG);
    try
    {
      PrintWriter out = new PrintWriter(
            new BufferedWriter(
              new OutputStreamWriter(
                socket.getOutputStream())), true);
      ForkedWriter writer = new ForkedWriter(out);
      //in den Unit-Tests wird mit print gearbeitet, hier also auch
      logger.info("sending message: " + str);
      writer.print(str);
      //sonst wartet der Writer auf weitere ausgaben
      writer.flush();
      return "true";
    } catch (IOException e) {
      logger.warn("cannot open socket " + socket);
      return e.getMessage();
    }
  }
  
  
/**
 * Die run-Methode dieses Threads. HIer wird konkurrierend mit anderen
 * Threads auf die Warteschlange zugegriffen.
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
				sendToServer((String) sendqueue.elementAt(0));
				sendqueue.removeElementAt(0);

			}
		}
	}
}