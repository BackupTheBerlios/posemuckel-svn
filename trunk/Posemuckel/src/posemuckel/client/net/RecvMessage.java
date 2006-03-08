package posemuckel.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import posemuckel.common.InvalidMessageException;
import posemuckel.common.Message_Handler;
import posemuckel.common.VerboseClientReader;

/**
 * Liest die vom Server kommenden Nachrichten und reicht sie an den zuständigen
 * Message_Handler weiter. Hierzu muss eine Referenz auf den Socket übergeben werden.
 *
 */
public class RecvMessage extends Thread {
	
	private Socket s; // socket of connection

	private Message_Handler msgHandler;

	private static Logger logger = Logger.getLogger(RecvMessage.class);
	
	/**
	 * Empfängt Nachrichten vom Server und leitet sie an den Message_Handler zur
	 * Verarbeitung weiter.
	 * @param handler der Message_Handler zur Verarbeitung der Nachrichten
	 */
	public RecvMessage(Message_Handler handler) {
		this.msgHandler = handler;
	}
	
	/**
	 * Setzt den Socket, der zum Lesen verwendet werden soll.
	 * @param socket Socket zum Lesen der Nachrichten
	 */
	protected void setSocket(Socket socket) {
		this.s = socket;
	}

	/**
	 * runs until read from server ends
	 * <p>
	 * - init input stream
	 * <br>
	 * - read strings from server
	 * <br>
	 * - write new strings into output window
	 * <br>
	 * - end program if read from server fails
	 * @throws IOException: server has disconnected
	 * @throws SQLException 
	 */
	private void working() throws IOException, SQLException {
		logger.info("starting read from server");

		// in: BufferedReader used to read from ServerSocket
		//BufferedReader in = new BufferedReader(
		//new InputStreamReader(s.getInputStream()));
		VerboseClientReader in = new VerboseClientReader(new BufferedReader(
				new InputStreamReader(s.getInputStream())));
		//kann in Client.main wieder eingeschaltet werden, falls über die GUI getestet wird
		//in.isVerbose(true);
		boolean alive = true;

		while (alive) {
			try {
				if (!msgHandler.eat_up_ServerPacket(in)) {
					logger.info("Ich bin aus der Empfangsschleife gefallen");
					break;
				}
			} catch (InvalidMessageException e) {
				// TODO es muss jemandem mitgeteilt werden, dass die Verbindung weg ist
				//TODO es muss zwischen unbekanntem Header und leerem Header unterschieden werden!
				if (s.isInputShutdown()) {
					//es wurde eine null gelesen
					logger.debug("InputStream was closed.");
				} else {
					e.printStackTrace();
				}
				s.close();
				alive = false;
			}
		}
		/*
		 * wenn der Server die Verbindung unterbricht, liest der MessageHandler eine
		 * null und wir werden aus der while-Schleife geschmissen; 
		 */
		Client.reopenConnection();
	}

	/**
	 * run until server disconnects
	 */
	public void run() {
		try {
			try {
				this.working();
			} catch (SQLException e) {//wegen der Kompatibilität zum Server leider nötig
				e.printStackTrace();
			} // do that until server disconnects
		} catch (IOException e) {
			logger.debug("InputStream was closed");
		} catch (NullPointerException e) {
			logger.error("NullPointerException in RescMessage", e);
		} finally {
			try {
				sleep(3);
			} catch (InterruptedException ex) {
			}
		}
	}
}