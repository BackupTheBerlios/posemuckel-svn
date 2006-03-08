/**
 * 
 */
package posemuckel.client.net;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;

import posemuckel.client.model.Database;
import posemuckel.client.model.InformationReceiver;
import posemuckel.client.model.PersonsData;
import posemuckel.client.model.Project;
import posemuckel.client.model.Task;
import posemuckel.client.model.TaskAdapter;

/**
 * Netbase ist die Implementierung des DatabaseInterface f&uuml;r die &uuml;ber 
 * das Netzwerk erreichbare Datenbank. Der gesamte Netzwerkverkehr wird durch Netbase
 * von dem Model verborgen.
 * 
 * @author Posemuckel Team
 * 
 */
public class Netbase implements Database, InformationReceiver {

	private ClientMessage sender;

	private Vector tasks;
	private Vector messageIDs;
	private InformationReceiver receiver;
	private static Logger logger = Logger.getLogger(Netbase.class);
	
	/**
	 * Erstellt eine neue Netzdatenbank.
	 * @param sender der Sender der Nachrichten zum Server
	 * @param reader der Empfänger der Nachrichten vom Server
	 */
	protected Netbase(ClientMessage sender, ServerMessage reader) {
		this.sender = sender;
		tasks = new Vector();
		messageIDs = new Vector();
		reader.addReceiver(this);
	}
	
	/**
	 * Erstellt eine neue Netzdatenbank. Die Verbindung zum Server muss 
	 * initialisiert sein.
	 *
	 */
	public Netbase() {
		this(Client.getConnection().getMessages(), Client.getConnection().getMessageHandler());
	}
	
	/**
	 * F&uuml;gt die Task zu den in Arbeit befindlichen Tasks hinzu. Die Task
	 * kann anhand der ID sp&auml;ter wieder entfernt werden.
	 * 
	 * @param task neue Task
	 * @param id ID der Nachricht
	 */
	@SuppressWarnings("unchecked")
	private void addTask(Task task, String id) {
		// ans Ende einfügen
		synchronized (tasks) {
			tasks.add(task);
			messageIDs.add(id);
		}
	}
	
	/**
	 * Entfernt die Task zu der MessageID aus der Menge der in Arbeit befindlichen
	 * Tasks und gibt die Task zur&uuml;ck.
	 * 
	 * @param id MessageID
	 * @return Task zu der id
	 */
	private Task removeTask(String id) {
		Task t = null;
		// das erste Element entfernen
		synchronized (tasks) {
			 if(tasks.size() < 1) {
				 t = new TaskAdapter();
				 logger.info("dequeue has no more tasks");
			 } else if (messageIDs.indexOf(id) == -1){
				 throw new IllegalArgumentException("ID is not known in netbase");
			 } else {
				 int i = messageIDs.indexOf(id);
				 messageIDs.remove(i);
				 t = (Task) tasks.remove(i);
			 }
		}
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see posemuckel.client.model.Database#isHeavyTask()
	 */
	public boolean isHeavyTask() {
		// bis der Client so läuft, wie wir ihn haben wollen
		return true;
	}
	
	/**
	 * Informiert die Task, die der Nachricht mit der angegebenen ID zugeordnet
	 * ist, &uuml;ber die Ankunft der Antwort. Die möglichen Antworten sind
	 * in posemuckel.client.model.Database definiert.
	 *
	 * @param answer Antwort
	 * @param id ID der Nachricht
	 */
	void update(int answer, String id) {
		Task task = removeTask(id);
		if(answer != Database.ACCESS_DENIED || task.relayACCESS_DENIED()) {
			task.update(answer);
		}
	}
	
	/**
	 * Informiert die Task, die der Nachricht mit der angegebenen ID zugeordnet
	 * ist, &uuml;ber die Ankunft der Antwort.
	 * 
	 * @param objects Liste der Objekte in der Antwort
	 * @param id ID der Nachricht
	 */
	protected void update(ArrayList objects, String id) {
		Task task = removeTask(id);
		task.update(objects);
	}
	
	/**
	 * Informiert die Task, die der Nachricht mit der angegebenen ID zugeordnet
	 * ist, &uuml;ber die Ankunft der Antwort.
	 * 
	 * @param message einzeilige Antwort des Servers
	 * @param id ID der Nachricht
	 */
	protected void update(String message, String id) {
		Task task = removeTask(id);
		task.update(message);
	}
	
	/**
	 * Informiert die Netbase über das Laden des FolderSystems über das Netzwerk.
	 * Die Antwort des Servers ist in zwei Teile zerlegt: die Folderstruktur und
	 * die Einordnung der URLs in die Folder
	 * @param folderStructure 
	 * @param couples Einordung der URLs in die Folder
	 * @param id ID der Nachricht
	 */
	protected void updateFolders(ArrayList<String> folderStructure, ArrayList<String> couples, String id) {
		Task task = removeTask(id);
		task.update(folderStructure);
		task.update(couples);	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see posemuckel.client.model.Database#login(java.lang.String,
	 *      java.lang.String, posemuckel.client.model.Task)
	 */
	public void login(String name, String pwd, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.login(id, name, pwd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see posemuckel.client.model.Database#logout(java.lang.String,
	 *      posemuckel.client.model.Task)
	 */
	public void logout(String name, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		// für das Logout wird nur der Clienthash benötigt, der über
		// eine Clientvariable zugänglich ist
		sender.logout(id);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see posemuckel.client.model.Database#addBuddy(java.lang.String,
	 *      java.lang.String, posemuckel.client.model.Task)
	 */
	public void addBuddy(String name, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.addBuddy(id, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see posemuckel.client.model.Database#deleteBuddy(java.lang.String,
	 *      java.lang.String, posemuckel.client.model.Task)
	 */
	public void deleteBuddy(String buddy, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.delBuddy(id, buddy);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#loadFolderStructure(posemuckel.client.model.Task)
	 */
	public void loadFolderStructure(Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.getFoldersystem(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see posemuckel.client.model.Database#getBuddys(java.lang.String,
	 *      posemuckel.client.model.Task)
	 */
	public void getBuddys(Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.myBuddies(id);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getWebtrace(posemuckel.client.model.Task)
	 */
	public void getWebtrace(Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.getWebtrace(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see posemuckel.client.model.Database#addUser(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, posemuckel.client.model.Task)
	 */
	public void addUser(String firstname, String pwd, String surname,
			String nickname, String lang, String gender, String email,
			String location, String comments, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);

		sender.register(id, firstname, surname, email, nickname, pwd, lang, gender,
				location, comments);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getClientHash()
	 */
	public String getClientHash() {
		//wird noch von der Klasse Client gespeichert
		return Client.getClientHash();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getAllUsers(posemuckel.client.model.Task)
	 */
	public void getAllUsers(Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.searchUsers(id, "", "", "", "", "", "");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#search(posemuckel.client.model.PersonsData, java.lang.String, posemuckel.client.model.Task)
	 */
	public void search(PersonsData data, String text, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.searchUsers(id, data.getNickname(), data.getFirstName(), 
				data.getSurname(), data.getLang(), data.getGender(), text);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#startProject(posemuckel.client.model.Project)
	 */
	public void startProject(Project project, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task,id);
		sender.startProject(id, project.getTopic(), project.getDescription(),
				project.isPublic(), project.getMaxNumber(), project.getMembers() );
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#informAboutNewProject(posemuckel.client.model.InformationReceiver)
	 */
	public void setReceiver(InformationReceiver receiver) {
		this.receiver = receiver;
	}

	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutNewProject(posemuckel.client.model.Project)
	 */
	public void informAboutNewProject(Project project) {
		if(receiver != null) {
			receiver.informAboutNewProject(project);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#freeSeatsChanged(int, java.lang.String)
	 */
	public void freeSeatsChanged(int change, String projectID) {
		if(receiver != null) {
			receiver.freeSeatsChanged(change, projectID);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getAllProjects(posemuckel.client.model.Task)
	 */
	public void getAllProjects(Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.getProjects(id);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#addNote(java.lang.String, java.lang.String)
	 */
	public void addNote(String url, String data) {
		String id = MessageID.getNextIDAsString();
		sender.addNote(id, url, data);
	}

	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getNotes(java.lang.String, posemuckel.client.model.Task)
	 */
	public void getNotes(String url, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.getNotes(id, url);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getInvitations(posemuckel.client.model.Task)
	 */
	public void getInvitations(Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.getInvitations(id);
	}

	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getMyProjects(posemuckel.client.model.Task)
	 */
	public void getMyProjects(Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.myProjects(id);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#joinProject(java.lang.String, posemuckel.client.model.Task)
	 */
	public void joinProject(String projectID, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.joinProject(id, projectID);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#leaveProject(java.lang.String, posemuckel.client.model.Task)
	 */
	public void leaveProject(String projectID, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.leaveProject(id, projectID);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getProfile(java.lang.String[], posemuckel.client.model.Task)
	 */
	public void getProfile(String[] nicknames, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.profile(id, nicknames);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#setProfile(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, posemuckel.client.model.Task)
	 */
	public void setProfile(String firstname, String surname, String pwd, String email, String lang, String gender, String location, String comment, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.setProfile(id, firstname, surname, pwd, email, lang, gender, location, comment );		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#visiting(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public void visiting(String newurl, String title, String oldurl, Task task) {
		String id = MessageID.getNextIDAsString();
		sender.visiting(id, newurl , title, oldurl);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getProjectMembers(java.lang.String, posemuckel.client.model.Task)
	 */
	public void getProjectMembers(String projectId, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.getProjectMembers(id, projectId);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#openProject(posemuckel.client.model.Project, posemuckel.client.model.Task)
	 */
	public void openProject(Project project, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.openProject(id, project.getID());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#joinChat(java.lang.String, posemuckel.client.model.Task)
	 */
	public void joinChat(String chatId, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.joinChat(id, chatId);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#rejectInvitation(java.lang.String, posemuckel.client.model.Task)
	 */
	public void rejectInvitation(String projectID, Task task) {
		String id = MessageID.getNextIDAsString();
		addTask(task, id);
		sender.rejectInvitation(id, projectID);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#startChat(java.lang.String[])
	 */
	public void startChat(String[] userToInvite) {
		String id = MessageID.getNextIDAsString();
		sender.startChat(id, false, userToInvite);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#loadChatMembers(java.lang.String, posemuckel.client.model.Task)
	 */
	public void loadChatMembers(String chatId) {
		String id = MessageID.getNextIDAsString();
		sender.getChatMembers(id, chatId);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#chatting(java.lang.String, java.lang.String)
	 */
	public void chatting(String chatId, String message) {
		String id = MessageID.getNextIDAsString();
		sender.chat(id, chatId, message);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#addFolder(java.lang.String, java.lang.String)
	 */
	public void addFolder(String title, String parentID) {
		String id = MessageID.getNextIDAsString();
		sender.addFolder(id, title, parentID);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#changeParentFolder(java.lang.String, java.lang.String)
	 */
	public void changeParentFolder(String folderID, String parentID) {
		String id = MessageID.getNextIDAsString();
		sender.changeParent(id, folderID, parentID);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#deleteFolder(java.lang.String)
	 */
	public void deleteFolder(String folderID) {
		String id = MessageID.getNextIDAsString();
		sender.deleteFolder(id, folderID);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#changeParentFolderForURL(java.lang.String, java.lang.String)
	 */
	public void changeParentFolderForURL(String url, String parentID) {
		String id = MessageID.getNextIDAsString();
		sender.changeParentFolderForURL(id, url, parentID);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#typing(java.lang.String)
	 */
	public void typing(String chatId) {
		String id = MessageID.getNextIDAsString();
		sender.typing(id, chatId);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#reading(java.lang.String)
	 */
	public void reading(String chatId) {
		String id = MessageID.getNextIDAsString();
		sender.reading(id, chatId);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#typing(java.lang.String, java.lang.String)
	 */
	public void typing(String user, String chatID) {
		if(receiver != null) {
			receiver.typing(user, chatID);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#reading(java.lang.String, java.lang.String)
	 */
	public void reading(String user, String chatID) {
		if(receiver != null) {
			receiver.reading(user, chatID);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#updateChatMembers(java.lang.String[])
	 */
	public void updateChatMembers(String[] message) {
		if(receiver != null) {
			receiver.updateChatMembers(message);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#chatting(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void chatting(String user, String chatID, String message) {
		if(receiver != null) {
			receiver.chatting(user, chatID, message);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#userStatusChanged(java.lang.String, java.lang.String)
	 */
	public void userStatusChanged(String name, String status) {
		if(receiver != null) {
			receiver.userStatusChanged(name, status);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutNewChat(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public void informAboutNewChat(String chatID, String owner, String[] invited) {
		if(receiver != null) {
			receiver.informAboutNewChat(chatID ,owner, invited);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutVisiting(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void informAboutVisiting(String user, String newurl, String title, String oldurl) {
		if(receiver != null) {
			receiver.informAboutVisiting(user, newurl , title, oldurl);
		}		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutViewing(java.lang.String, java.lang.String)
	 */
	public void informAboutViewing(String user, String url) {
		if(receiver != null) {
			receiver.informAboutViewing(user, url);
		}		

	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#newInvitation(java.lang.String)
	 */
	public void newInvitation(String projectID) {
		if(receiver != null) {
			receiver.newInvitation(projectID);
		}		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutProjectMemberChange(java.lang.String, java.lang.String, boolean)
	 */
	public void informAboutProjectMemberChange(String user, String projectID, boolean joining) {
		if(receiver != null) {
			receiver.informAboutProjectMemberChange(user, projectID, joining);
		}		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#notify(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void notify(String user, String url, String title, String comment, String pic_data_length, String pic) {
		if(receiver != null) {
			receiver.notify(user, url, title, comment, pic_data_length, pic);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#voting(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void voting(String user, String url, String vote) {
		if(receiver != null) {
			receiver.voting(user, url, vote);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#notify(posemuckel.client.model.Project, posemuckel.client.model.Task)
	 */
	public void notify(Project project, Task task) {
		String id = MessageID.getNextIDAsString();
		String[] data = project.getNotify();
		addTask(task, id);
		sender.notify(id,data);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#vote(java.lang.String, java.lang.String)
	 */
	public void vote(String url, String rating) {
		String id = MessageID.getNextIDAsString();
		sender.vote(id, url, rating);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#viewing(java.lang.String)
	 */
	public void viewing(String URL) {
		String id = MessageID.getNextIDAsString();
		sender.viewing(URL, id);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#newNote(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void newNote(String user, String url, String rating, String withNote) {
		if(receiver != null) {
			receiver.newNote(user,url,rating, withNote);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutNewFolder(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void informAboutNewFolder(String title, String id, String parentID) {
		if(receiver != null) {
			receiver.informAboutNewFolder(title, id, parentID);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutParentFolderChanged(java.lang.String, java.lang.String)
	 */
	public void informAboutParentFolderChanged(String id, String parentFolderID) {
		if(receiver != null) {
			receiver.informAboutParentFolderChanged(id, parentFolderID);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutFolderRemoval(java.lang.String)
	 */
	public void informAboutFolderRemoval(String folderID) {
		if(receiver != null) {
			receiver.informAboutFolderRemoval(folderID);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.InformationReceiver#informAboutParentFolderForUrl(java.lang.String, java.lang.String)
	 */
	public void informAboutParentFolderForUrl(String url, String parentFolder) {
		if(receiver != null) {
			receiver.informAboutParentFolderForUrl(url, parentFolder);
		}
	}
}
