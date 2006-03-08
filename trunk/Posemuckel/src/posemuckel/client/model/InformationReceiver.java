/**
 * 
 */
package posemuckel.client.model;

/**
 * Ein InformationReceiver nimmt Nachrichten vom Server entgegen, die zum Update
 * vorhandener Informationen an mehrere Clients gesendet werden. In der Regel wird
 * vom Client keine Nachricht an den Server gesendet, um die Informationen zu
 * erhalten.<br>
 * 
 * Das Interface wird zur Zeit von zwei Klassen implementiert:
 * <ul>
 * <li>Model</li>
 * <li>Netbase</li>
 * </ul>
 * 
 * @author Posemuckel Team
 *
 */
public interface InformationReceiver {
	
	/**
	 * Informiert den Receiver &uuml;ber ein neues Projekt. Der Aufruf dieser
	 * Methode wird vom Server durch die Nachricht NEW_PROJECT getriggert.
	 * 
	 * @param project das neue Projekt
	 */
	public abstract void informAboutNewProject(Project project);
	
	/**
	 * Informiert den Receiver &uuml;ber einen Anwender, der angefangen hat zu
	 * tippen. Der Aufruf dieser Methode wird vom Server durch die Nachricht 
	 * TYPING getriggert.
	 * 
	 * @param user der Anwender, der tippt
	 * @param chatID der Chat, in dem getippt wird
	 */
	public abstract void typing(String user, String chatID);
	
	/**
	 * Informiert den Receiver &uuml;ber einen Anwender, der angefangen hat zu
	 * lesen. Der Aufruf dieser Methode wird vom Server durch die Nachricht 
	 * READING getriggert.
	 * 
	 * @param user der Anwender, der wieder liest
	 * @param chatID der Chat, in dem getippt wird
	 */
	public abstract void reading(String user, String chatID);
	
	/**
	 * Informiert den Receiver &uuml;ber eine &Auml;nderung der Mitgliederliste
	 * eines Chat.
	 * 
	 * @param message Index 0: die ChatID, Rest: die Anwendernamen
	 */
	public abstract void updateChatMembers(String[] message);
	
	/**
	 * Informiert den Receiver &uuml;ber eine neue Chatnachricht. Der Aufruf dieser
	 * Methode wird vom Server durch die Nachricht CHATTING getriggert.
	 * 
	 * @param user der Autor der Nachricht
	 * @param chatID die ChatID
	 * @param message die Nachricht
	 */
	public abstract void chatting(String user, String chatID, String message);
	
	/**
	 * Informiert über eine Änderung der freien Plätze in einem Projekt.
	 * @param change Differenz der freien Plätze, also -1, wenn es einen freien 
	 * Platz weniger gibt
	 * @param projectID ID des betroffenen Projektes
	 */
	public abstract void freeSeatsChanged(int change, String projectID);
	
	/**
	 * Informiert &uuml;ber die &Auml;nderung des Zustandes eines Anwenders. Diese
	 * Nachricht wird jedesmal vom Server gesendet, wenn sich ein Buddy ein- oder
	 * ausloggt.
	 * 
	 * @param name Name des Buddys
	 * @param status neuer Zustand des Buddys
	 */
	public abstract void userStatusChanged(String name, String status);
	
	/**
	 * Informiert &uuml;ber einen neuen Chat. Die Nachricht wird vom Server an
	 * alle eingeladenen Teilnehmer und den Owner versendet.
	 * @param chatID ID des Chat
	 * @param owner der Owner des Chat
	 * @param invited ein Array mit den eingeladenen Anwendern
	 */
	public abstract void informAboutNewChat(String chatID, String owner, String[] invited);
	

	/**
	 * Informiert &uuml;ber eine neue Seite, die aufgerufen wurde. Die Nachricht wird vom Server an
	 * alle alle Chat-Teilnehmer versendet.
	 * @param newurl die neue URL
	 * @param title der Titel der neuen URL
	 * @param oldurl die vorherige URL
	 */
	public abstract void informAboutVisiting(String user, String newurl, String title, String oldurl);
	
	/**
	 * Informiert &uuml;ber eine Seite, die angesehen wird. Diese Info wird in 
	 * der Regel nur gesendet, wenn keine passende VISITING-Nachricht gesendet
	 * wurde.
	 * 
	 * @param user der Benutzername
	 * @param url die besuchte URL
	 */
	public abstract void informAboutViewing(String user, String url);
	
	/**
	 * Informiert &uuml;ber den Eintritt oder das Verlassen eines Projektmitgliedes im geöffneten Projekt. 
	 * @param user
	 * @param projectID zur Sicherheit: erm&ouml;glicht den Vergleich der ProjektIDs
	 * @param joining true, falls das Mitglied eintritt.
	 */
	public abstract void informAboutProjectMemberChange(String user, String projectID, boolean joining);
	
	/**
	 * Informiert &uuml;ber eine Einladung zu einer Projektteilnahme.
	 * @param projectID ID des Projektes
	 */
	public abstract void newInvitation(String projectID);

	/**
	
	/**
	 * Informiert &uuml;ber das Hochhalten einer URL.
	 * @param user
	 * @param url
	 * @param title
	 * @param comment
	 * @param pic_data_length
	 * @param pic
	 */
	public abstract void notify(String user, String url, String title, String comment, String pic_data_length, String pic);
	
	/**
	 * Informiert über eine neue Bewertung im Client.
	 * @param user der Anwender, der die Bewertung abgegeben hat.
	 * @param url der besuchten Webseite
	 * @param vote die Bewertung
	 */
	public abstract void voting(String user, String url, String vote);
	
	/**
	 * Informiert &uuml;ber eine neue Notiz.
	 * @param user der Anwender, der die Notiz erstellt hat.
	 * @param url die betroffene URL
	 * @param rating das neue Rating
	 * @param withNote gibt an, ob eine Notiz dabei ist
	 */
	public abstract void newNote(String user, String url, String rating, String withNote);
	
	/**
	 * Informiert &uuml;ber das Erstellen eines neuen Ordners
	 * @param title Titel des Ordners
	 * @param id ID des Ordners
	 * @param parentID ID des ParentOrdnersa
	 */
	public abstract void informAboutNewFolder(String title, String id, String parentID);
	
	/**
	 * Informiert über einen Folder, in den die URL eingeordnet wurde. 
	 * @param url die URL
	 * @param parentFolder die ID des Folders oder der leere String, wenn die URL aus
	 * 	einem Folder entfernt wurde
	 */
	public abstract void informAboutParentFolderForUrl(String url, String parentFolder);	
	/**
	 * Informiert über den Wechsel des ParentFolders. Der Folder mit der <code>id</code>
	 * hat einen neuen ParentFolder erhalten.
	 * @param id des Folders
	 * @param parentFolderID des parentFolders
	 */
	public abstract void informAboutParentFolderChanged(String id, String parentFolderID);
	
	/**
	 * Informiert über das Löschen des Folders im Server.
	 * 
	 * @param folderID des Folders, der gelöscht werden soll
	 */
	public abstract void informAboutFolderRemoval(String folderID);

}
