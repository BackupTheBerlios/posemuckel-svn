/**
 * 
 */
package posemuckel.client.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import posemuckel.client.net.Client;
import posemuckel.common.EnumsAndConstants;

/**
 * Das Model ist eine logische Klammer, welche alle Daten, die f&uuml;r den
 * Anwender interessant sein k&ouml;nnten, enth&auml;lt.
 * 
 * Das Model dient auch als Empf&auml;nger und Verteiler von Informationen, 
 * die von der Datenbank gesendet werden.
 * 
 * @author Posemuckel Team
 *
 */
public class Model implements InformationReceiver {
	
	private static Model model;
	private static boolean dataLoaded;
	
	private User user;
	private Project openProject;
	private ProjectList allProjects;
	
	private UsersPool allUsers;
	private HashMap<String, Chat> chatPool;
	
	//speichert Einladungen zu Projekten, für die noch keine newProject-nachricht
	//gekommen ist
	private Vector<String> pendingInvitations;
	private static Logger logger = Logger.getLogger(Model.class);
	/**
	 * Gibt ein Model aus. Wenn noch kein Model &uuml;ber diese Methode instantiiert 
	 * wurde, wird ein neues Model erzeugt. Diese Methode ist nur f&uuml;r die GUI
	 * gedacht, damit keine Instanz eines Models durchgereicht werden muss. 
	 * @return ein Model
	 */
	public static Model getModel() {
		if(model == null) {
			model = new Model();
		}
		// Die Verbindung kann auch später aufgebaut werden:
		if(Client.hasConnection()) {
			DatabaseFactory.getRegistry().setReceiver(model);
		}
		return model;
	}
	
	/**
	 * Erstellt ein Model mit einem dazugeh&ouml;renden Anwender.
	 *
	 */
	public Model() {
		user = new User(this);	
		allUsers = new UsersPool(this);
		chatPool = new HashMap<String, Chat>();
		new Chat("0", this);
	}
	
	/**
	 * Gibt den Anwender, der zu dem Model geh&ouml;rt, aus.
	 * 
	 * @return Anwender des Models
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Sucht alle User mit den angegebenen Daten.
	 * @param data die Daten der Person
	 * @param text Freitext
	 */
	public void searchUsers(PersonsData data, String text) {
		allUsers.search(data, text);
	}
	
	/**
	 * Gibt die Zahl der Chats aus.
	 * 
	 * @return Zahl der Chats
	 */
	public int getChatCount() {
		return chatPool.size();
	}
	
	/**
	 * Gibt alle Chats in einer Collection aus.
	 * @return alle Chats
	 */
	public Collection<Chat> getAllChats() {
		return chatPool.values();
	}
	
	/**
	 * Pr&uuml;ft, ob alle Anwender im Model bekannt sind.
	 * @param nicknames Benutzernamen
	 * @throws IllegalArgumentException wenn einer der Namen nicht bekannt ist
	 */
	public void hasUsers(String[] nicknames) {
		for (String name : nicknames) {
			if(!getAllPersons().hasMember(name))
				throw new IllegalArgumentException("unknown user: " + name);
		}
	}
	
	/**
	 * Gibt das Projekt zur&uuml;ck, welches zuletzt ge&ouml;ffnet wurde. Wenn ein
	 * solches Projekt nicht existiert, wird <code>null</code> zur&uuml;ckgegeben.
	 * 
	 * @return das ge&ouml;ffnete Projekt
	 */
	public Project getOpenProject() {
		return openProject;
	}
	
	/**
	 * Gibt den Chat mit der angegebenen ID zur&uuml;ck. Wenn der Chat nicht
	 * existiert, wird <code>null</code> zur&uuml;ckgegeben.
	 * @param id ID des Chat
	 * @return Chat
	 */
	public Chat getChat(String id) {
		return chatPool.get(id);
	}
	
	/**
	 * Liefert den Logchat
	 * @return
	 */
	public Chat getLogchat() {
		return chatPool.get(EnumsAndConstants.LOG_CHAT_ID);
	}

	
	/**
	 * Gibt eine Instanz von <code>MemberList</code> mit allen im Model vorkommenden
	 * Personen aus.
	 * 
	 * @return alle Personen, die irgendwo im Model vorkommen
	 */
	public MemberList getAllPersons() {
		return allUsers;
	}
	
	/**
	 * F&uuml;gt den Anwender in den PersonenPool ein. Der Anwender kann erst
	 * eingef&uuml;gt werden, wenn sein Nickname bekannt ist.
	 *
	 */
	protected void putUserIntoPool() {
		allUsers.addMember(user);
	}
	
	/**
	 * Gibt eine Instanz von <code>ProjectList</code> mit allen im Model vorkommenden
	 * Projekten aus. In diese Liste wird ein neues Projekt eingef&uuml;gt, wenn
	 * in der Database ein neues Projekt erstellt wird.
	 * 
	 * @return alle Projekte, die irgendwo im Model vorkommen
	 */
	public ProjectList getAllProjects() {
		if(allProjects == null) {
			allProjects = new ProjectList(this, ProjectList.ALL_PROJECTS);
		}
		return allProjects;
	}
	
	/**
	 * Informiert das Model dar&uuml;ber, dass in der <code>Database</code> ein
	 * neues Projekt eingef&uuml;gt wurde.
	 */
	public void informAboutNewProject(Project project) {
		project.setModel(this);
		getAllProjects().confirmAddProject(project);
		//das Projekt in die Liste der Einladungen einfügen
		//falls als noch zu bearbeiten gespeichert
		checkPendingInvitations(project.getID());
	}
		
	/**
	 * Gibt die Liste mit den offenen Einladungen zu einem Projekt zurück.
	 * @return Liste mit den offenen Einladungen
	 */
	private Vector<String> getPendingInvitations() {
		if(pendingInvitations ==null) {
			pendingInvitations = new Vector<String>();
		}
		return pendingInvitations;
	}
	
	/**
	 * Prüft, ob das Projekt bei den offenen Einladungen eingetragen ist und noch
	 * nicht als neues Projekt eingetragen wurde.
	 * @param projectID 
	 */
	private void checkPendingInvitations(String projectID) {
		if(pendingInvitations != null && pendingInvitations.contains(projectID)) {
			pendingInvitations.remove(projectID);
			newInvitation(projectID);
		}
		//pendingInvitations wieder aufräumen
		if(pendingInvitations != null && pendingInvitations.size() == 0) {
			pendingInvitations = null;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#freeSpacesChanged(int, java.lang.String)
	 */
	public void freeSeatsChanged(int change, String projectID) {
		//leider kann ein Projekt doppelt vorkommen, also hier prüfen
		//TODO braucht das aktuelle Projekt auch eine Aktualisierung
		//Projekte holen
		Project project = getAllProjects().getProject(projectID);
		Project userProject = user.getProjects().getProject(projectID);
		Project invitation = user.getInvitations().getProject(projectID);
		//Projektdaten ändern
		if(project != null) {
			project.changeFreeSeats(change);
		}		
		if(userProject != null && userProject != project) {
			userProject.changeFreeSeats(change);
		}		
		if(invitation != null && invitation != project && invitation != userProject) {
			invitation.changeFreeSeats(change);
		}
		//Listener benachrichtigen
		getAllProjects().fireElementChanged(project, true);
		user.getProjects().fireElementChanged(userProject, true);
		user.getInvitations().fireElementChanged(invitation, true);
	}
	
	/**
	 * F&uuml;gt den Chat in den Pool ein.
	 * 
	 * @param chat der neue Chat
	 */
	protected void addChat(Chat chat) {
		/*
		 * die Methode wird im Konstruktor von Chat aufgerufen
		 */
		chatPool.put(chat.getID(), chat);
	}
	
	/**
	 * das erste Argument des Arrays enth&auml;lt die ChatID, die anderen Elemente
	 * enthalten die Nicknames der Chatteilnehmer
	 */
	public void updateChatMembers(String[] message) {
		Chat chat = getChat(message[0]);
		if(chat != null) {
			chat.informAboutUserUpdate(message);
		}
	}
		
	/**
	 * Teilt dem Model mit, welches Projekt ge&ouml;ffnet wurde.
	 * @param project das neue geöffnete Projekt
	 */
	void setOpenProject(Project project) {
		closeOpenProject();
		openProject = project;
	}
	
	/**
	 * Schließt das gerade geöffnete Projekt. Der Server wird nicht 
	 * davon unterrrichtet.
	 *
	 */
	public void closeOpenProject() {
		if(openProject != null) {
			openProject.resetOpen();
			openProject = null;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutNewChat(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public void informAboutNewChat(String chatID, String owner, String[] invited) {
		Chat chat = new Chat(chatID, this);		
		chat.getChatMembers().addMember(invited);
		chat.getChatMembers().addMember(new String[] {owner});
		chat.setTitle(owner);
		user.informAboutNewChat(chat, true);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#typing(java.lang.String, java.lang.String)
	 */
	public void typing(String user, String chatID) {
		Chat chat = getChat(chatID);
		if(chat != null) {
			chat.fireTyping(user);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#reading(java.lang.String, java.lang.String)
	 */
	public void reading(String user, String chatID) {
		Chat chat = getChat(chatID);
		if(chat != null) {
			chat.fireReading(user);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutVisiting(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void informAboutVisiting(String user, String newurl, String title, String oldurl) {
		if(getOpenProject() != null) {
			getOpenProject().getWebtrace().addWebpage(newurl, oldurl, title, user);
			getOpenProject().getWebtrace().informAboutNewVisit(newurl, oldurl, title, user);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#chatting(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void chatting(String user, String chatID, String message) {
		logger.debug("Chat-Message \""+message+"\" has been recieved.");
		Chat chat = getChat(chatID);
		if(chat!= null) {
			logger.debug("fireing new massage event");
			chat.fireNewMessage(user, message);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#userStatusChanged(java.lang.String, java.lang.String)
	 */
	public void userStatusChanged(String name, String status) {
		Person person = getAllPersons().getMember(name);
		if(person != null) {
			person.notifyOfStateChange(status);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutNewFolder(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void informAboutNewFolder(String title, String id, String parentID) {
		if(getOpenProject() != null) {
			getOpenProject().getWebtrace().getFolderTree().addFolder(title, id, parentID);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutParentFolderForUrl(java.lang.String, java.lang.String)
	 */
	public void informAboutParentFolderForUrl(String url, String parentFolder) {
		if(getOpenProject() != null && getOpenProject().getFolderTree().isLoaded()) {
			getOpenProject().getWebtrace().getFolderTree().addChildURLToFolder(url, parentFolder);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutParentFolderChanged(java.lang.String, java.lang.String)
	 */
	public void informAboutParentFolderChanged(String id, String parentFolderID) {
		if(getOpenProject() != null) {
			getOpenProject().getWebtrace().getFolderTree().changeParentFolder(id, parentFolderID);
		}		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutFolderRemoval(java.lang.String)
	 */
	public void informAboutFolderRemoval(String folderID) {
		if(getOpenProject() != null) {
			getOpenProject().getWebtrace().getFolderTree().deleteFolder(folderID);
		}		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutViewing(java.lang.String, java.lang.String)
	 */
	public void informAboutViewing(String user, String url) {
		if(getOpenProject() != null) {
			getOpenProject().getWebtrace().informAboutViewing(user, url);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#newNote(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void newNote(String user, String url, String rating, String withNote) {
		if(getOpenProject() != null) {
			getOpenProject().getWebtrace().informAboutNewNote(user, url, rating, withNote);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#voting(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void voting(String user, String url, String vote) {
		if(getOpenProject() != null) {
			Webpage page = getOpenProject().getWebtrace().getPageForUrl(url);
			if(page != null) {
				page.changeRating(user, Short.valueOf(vote));
			}
			getOpenProject().getWebtrace().informAboutChange(page);
		}		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#notify(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void notify(String user, String url, String title, String comment, String pic_data_length, String pic) {
		DecodeImage decoder = new DecodeImage(user, url, title, comment, pic_data_length, pic);
		Thread t = new Thread(decoder);
		t.start();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutProjectMemberChange(java.lang.String, java.lang.String, boolean)
	 */
	public void informAboutProjectMemberChange(String user, String projectID, boolean joining) {
		if(getOpenProject() != null && getOpenProject().getID().equals(projectID)) {
			MemberList members = getOpenProject().getMemberList();
			Person person = getAllPersons().getMember(user);
			if(person == null) {
				getAllPersons().addMemberToClub(user);
				person = getAllPersons().getMember(user);
			}
			if(joining) {
				getOpenProject().getWebtrace().addUser(user);
				members.confirmAddMember(person);
			} else {
				members.confirmDelMember(user);
				if(person.isBuddy()) {
					person.setState(Person.ONLINE);
				} else {
					person.setState(Person.UNKNOWN);
				}
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#newInvitation(java.lang.String)
	 */
	public void newInvitation(String projectID) {
		Project project = getAllProjects().getProject(projectID);
		if(project != null) {
			user.getInvitations().confirmAddProject(project);
		} else {
			//später bearbeiten: auf newProject warten
			//dieser Fall sollte die Ausnahme und nicht die Regel sein
			getPendingInvitations().add(projectID);
		}
	}
	
	/**
	 * Läd die Daten vom Server, die für das Projektfenster gebraucht werden.
	 * @param async wenn auf true gesetzt, wird das Laden zu einem späteren Zeitpunkt
	 * ausgeführt
	 */
	public static void load(boolean async) {
		if(async && !dataLoaded) {
			dataLoaded = true;
			Display.getDefault().asyncExec(getWorker());
		} else if(!dataLoaded) {
			getWorker().run();
		}
	}
	
	/**
	 * Erstellt das Runnable, welches die Daten läd.
	 * @return Runnable zum Laden der Daten
	 */
	private static Runnable getWorker() {
		Runnable run = new Runnable() {
			public void run() {
				if (Client.hasConnection()) {
					Model.getModel().getUser().getBuddyList().load();
					Model.getModel().getChat("0").loadChatMembers();
					Model.getModel().getUser().getProjects().load();
					Model.getModel().getAllProjects().load();
					Model.getModel().getUser().getInvitations().load();
				}
			}
		};
		return run;
	}

	/**
	 * Löscht den Chat mit der LOG_CHAT_ID aus dem Chat-Pool
	 */
	public void removeLogchat() {
		chatPool.remove(EnumsAndConstants.LOG_CHAT_ID);
	}
		
}
