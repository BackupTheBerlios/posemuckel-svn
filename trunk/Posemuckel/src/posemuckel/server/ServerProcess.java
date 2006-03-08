package posemuckel.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import posemuckel.common.InvalidMessageException;
import posemuckel.common.VerboseServerReader;

/**
 * Objekte dieser Klasse sind für die Verarbeitung von Nachrichten da,
 * die von einem Client gesendet werden. Jedem Client ist also genau ein
 * ServerProcess-Objekt zugeteilt.
 * @author Posemuckel Team
 *
 */
public class ServerProcess
extends Thread {

	/**
	 * enthält alle relevanten Daten, um Zugriffe auf die Datenbank zu
	 * minimieren.
	 */
	Model model;
	
	/**
	 * enthält alle Daten des verbundenen Clients
	 */
    ClientInfo clientInfo;            

    /**
     * Konstruktor
     * @param c Client, der mit diesem ServerProcess verbunden werden
     * soll
     */
    public ServerProcess(ClientInfo c) {
      	model = Model.getInstance();
      	c.setStatus(ClientInfo.ClientStatus.OFFLINE);
        clientInfo = c;
    }

    /**
     * Fügt dem Model einen neuen Client hinzu
     * @param client Client
     */
    public void addNewClient(ClientInfo client) {
    	model.addClient(clientInfo);
    }
    
    
    /**
     * Wird von der run-Methode des ServerProcess aufgerufen, und arbeitet
     * solange, bis der Client die Verbindung unterbrochen hat oder
     * ein Fehler aufgetreten ist.
     * @throws IOException
     * @throws NullPointerException
     * @throws SQLException
     */
    public void working()
    throws IOException, NullPointerException, SQLException {
        /* Diese Klasse frisst die Nachrichten auf und führt die
	       passenden Aktionen durch. */
	    ClientMessage msgHandler = null;
	    
        try {
            VerboseServerReader in = new VerboseServerReader(new BufferedReader(
                new InputStreamReader(
                		clientInfo.getSocket().getInputStream())));
            VerboseServerReader.isVerbose(true);
      
      
      
            System.out.println("new client on " + 
            		clientInfo.getSocket() + " has connected");

            boolean alive = true;
            msgHandler = new ClientMessage(this, clientInfo);
            while (alive) {
                try {
						if(!msgHandler.eat_up_ClientPacket(in)) {
						    break;
						}
                } catch (InvalidMessageException e) {
                	alive = false;
        	        System.out.println("I got an invalid message!\n"+
        	        		e.toString());
                } catch (SQLException ex) {
                	// Falls eine SQL-Exception auftritt, wird die laufende
                	// Transaktion zurückgesetzt und der Client darüber informiert
                	ex.printStackTrace();
					msgHandler.sendError();
						try {
							msgHandler.getDB().getConnection().rollback();
						} catch (SQLException e) {
							System.out.println("________Rollback failed");
							e.printStackTrace();
						}
					
                }
            }
        } finally {
            // Wir kommen hier an, wenn der Client die Verbindung
        	// beendet.
            System.out.println("Client on " + clientInfo.getSocket() + 
            		" is disconnected");

            // Entfernen des Clients
            ChatInfo[] chats = clientInfo.getChats();
            model.delClient(clientInfo);
            // Sender der CHAT_MENBERS Nachricht an alle Interessenten
             for (ChatInfo chatInfo : chats) {
     				msgHandler.sendChatMembers(chatInfo.getID()); 
			}
            // der Benutzer wird ausgeloggt
            String user = clientInfo.getUserName();
            if (user.compareTo("unknown") != 0) {
      	        msgHandler.getDB().logout(user);
            }
            //die anderen Clients von der Statusänderung informieren
            //TODO wenn sich der Anwender ausloggt, wird die Nachricht dann doppelt
            //gesendet
            msgHandler.sendStatusChange(user, "OFFLINE");
            try {
                clientInfo.getSocket().close(); // Socket wird geschlossen
            } catch (NullPointerException e) {
            	e.printStackTrace();
            }
        }
    }

    /**
     * Ruft die Methode working() auf, die arbeitet bis der Client
     * die Verbindung unterbricht oder ein Fehler auftritt.
     */
    public void run() {
        try {
		    this.working();
        } catch (IOException e) {
        	//e.printStackTrace();
            System.out.println("read/write from socket ended");
        } catch (NullPointerException e) {
        	e.printStackTrace();
        } catch (SQLException e) {
			e.printStackTrace();
		}
    }
}