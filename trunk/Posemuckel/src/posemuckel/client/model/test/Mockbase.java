/**
 * 
 */
package posemuckel.client.model.test;

import java.util.ArrayList;
import java.util.Collection;

import posemuckel.client.model.Database;
import posemuckel.client.model.InformationReceiver;
import posemuckel.client.model.Person;
import posemuckel.client.model.PersonsData;
import posemuckel.client.model.Project;
import posemuckel.client.model.Task;

/**
 * Enthält eine Tabelle mit allen Anwenderdaten.
 * Soll die entsprechende Tabelle in der DB auf dem Server simulieren und dient nur zu
 * Testzwecken. Es können Anwenderlisten, Buddys und Projektlisten verwaltet werden.
 * 
 * Nicht kommentierte Methoden wurden nicht mehr als Mockup implementiert.
 * 
 * @author Posemuckel Team
 *
 */
public class Mockbase implements Database {
	
	private static final int pwdIndex = 0;
	private static final int loggedIn = 1;
	
	private Table userTable;
	private Table buddys;
	private Table projects;
	private InformationReceiver receiver;
			
	/**
	 * Simuliert die Datenbank.
	 *
	 */
	public Mockbase(){
		userTable = new Table();
		buddys = new Table();
		projects = new Table();
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.client.model.test.Database#isHeavyTask()
	 */
	public boolean isHeavyTask() {
		return false;
	}


	/* (non-Javadoc)
	 * @see posemuckel.client.model.test.Database#addUser(java.lang.String, java.lang.String, posemuckel.client.model.UserTask)
	 */
	public void addUser(String name, String pwd, Task task) {
		if(exists(name)) {
			task.update(USER_EXITS);
		} else {
			overwriteUser(name, pwd);
			task.update(ACK);
		}
	}
	
	/**
	 * Hilfsmethode zum Hinzufügen eines Anwenders in der Anwendertabelle. Wenn schon
	 * ein Anwender mit dem gleichen Benutzernamen gespeichert ist, wird er 
	 * überschrieben. Der Anwender wird mit dem Onlinestatus OFFLINE gespeichert.
	 * @param name Benutzername
	 * @param pwd Passwort
	 */
	void overwriteUser(String name, String pwd) {
		ArrayList<String> list = new ArrayList<String>(3);
		list.add(pwd);
		list.add("false");
		list.add(name);
		userTable.put(name, list);
	}
	
	/**
	 * Löscht den Anwender aus der Tabelle mit allen Anwender, wenn das Passwort
	 * mit dem übergebenen Passwort übereinstimmt.
	 * @param name Benutzername
	 * @param pwd Passwort
	 */
	void deleteUser(String name, String pwd) {
		String real = (String)userTable.lookUp(name, pwdIndex);
		if(real.equals(pwd)) {
			userTable.remove(name);
		}
	}
	
	/**
	 * Prüft, ob das Passwort mit dem gespeicherten Passwort des Anwenders 
	 * übereinstimmt.
	 * @param name Benutzername
	 * @param pwd Passwort
	 * @return true, falls das Passwort mit dem gespeicherten Passwort übereinstimmt
	 */
	boolean checkPwd(String name, String pwd) {
		return pwd.equals(userTable.lookUp(name, pwdIndex));
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.test.Database#login(java.lang.String, java.lang.String, posemuckel.client.model.UserTask)
	 */
	public void login(String name, String pwd, Task task) {
		if(checkPwd(name, pwd)) {
			userTable.set(name, loggedIn, "true");
			task.update(ACCESS_GRANTED);
		} else {
			task.update(ACCESS_DENIED);
		}
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.client.model.test.Database#logout(java.lang.String, posemuckel.client.model.UserTask)
	 */
	public void logout(String name, Task task) {
		if(isLoggedIn(name)) {
			userTable.set(name, loggedIn, "false");
			task.update(ACK);
		} else {
			task.update(ACCESS_DENIED);
		}
	}
	
	/**
	 * Gibt an, ob der Anwender eingeloggt ist.
	 * @param name Benutzername
	 * @return true, falls der Anwender eingeloggt ist
	 */
	boolean isLoggedIn(String name) {
		return (userTable.lookUp(name, loggedIn)=="true");
	}
	
	/**
	 * Gibt an, ob der Benutzername in Mockbase vorkommt.
	 * @param name Benutzername
	 * @return true, falls der Benutzername in Mockbase existiert
	 */
	boolean exists(String name) {
		return (userTable.lookUp(name)!= null);
	}
	
	/**
	 * Gibt an, ob Anwender in Mockbase gespeichert sind.
	 * @return true, falls Anwender in Mockbase gespeichert sind
	 */
	boolean hasNoUsers() {
		return userTable.isEmpty();
	}

	/**
	 * Gibt an, ob Buddys in Mockbase gespeichert sind.
	 * @return true, falls Buddys in Mockbase gespeichert sind
	 */
	boolean hasBuddy() {
		return !buddys.isEmpty();
	}
	
	/**
	 * Fügt einen Buddy in die Tabellen ein.
	 * @param userHash des Anwenders, zu dem ein Buddy hinzugefügt werden soll
	 * @param bud der Buddy
	 */
	@SuppressWarnings("unchecked")
	void addBuddy(String userHash, Person bud) {
		ArrayList list = buddys.lookUp(userHash);
		if(list == null) {
			list = new ArrayList();
		}
		list.add(bud);
		buddys.put(userHash, list);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#addBuddy(java.lang.String, posemuckel.client.model.Task)
	 */
	public void addBuddy(String name, Task task) {
		addBuddy(getClientHash(),new Person(name, Person.ONLINE));
		task.update("online");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#deleteBuddy(java.lang.String, posemuckel.client.model.Task)
	 */
	public void deleteBuddy(String buddy, Task task) {
		buddys.remove(getClientHash());
		task.update(ACK);		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getBuddys(posemuckel.client.model.Task)
	 */
	public void getBuddys(Task task) {
		ArrayList budd = buddys.lookUp(getClientHash());
		task.update(budd);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#addUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, posemuckel.client.model.Task)
	 */
	public void addUser(String name, String pwd, String surname, String nickname, String lang, String gender, String email, String location, String comments, Task task) {
		this.addUser(nickname, pwd, task);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getClientHash()
	 */
	public String getClientHash() {
		return null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getAllUsers(posemuckel.client.model.Task)
	 */
	@SuppressWarnings("unchecked")
	public void getAllUsers(Task task) {
		Collection data = userTable.getAsList();
		ArrayList users = new ArrayList();
		for (Object o : data) {
			ArrayList<String> person = (ArrayList<String>)o;
			//der Index 2 enthält den Namen
			//der OnlineStatus wird nicht mit ausgegeben
			Person p = new Person(person.get(2));
			users.add(p);
		};
		task.update(users);
	}
	
	/**
	 * Fügt ein neues Projekt in die Projekttabelle ein.
	 * @param project das neue Projekt
	 */
	@SuppressWarnings("unchecked")
	public void addProject(Project project) {
		ArrayList list = new ArrayList();
		list.add(String.valueOf(project.isPublic()));
		list.add(project.getTopic());
		list.add(project.getDescription());
		projects.put(project.getID(), list);
	}
	
	/**
	 * Gibt an, ob Projekte in Mockbase gespeichert sind.
	 * @return true, falls Projekte in Mockbase gespeichert sind
	 */
	public boolean hasProjects() {
		return !projects.isEmpty();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#startProject(posemuckel.client.model.Project, posemuckel.client.model.Task)
	 */
	public void startProject(Project project, Task task) {
		addProject(project);
		if(receiver != null) {
			receiver.informAboutNewProject(project);			
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#setReceiver(posemuckel.client.model.InformationReceiver)
	 */
	public void setReceiver(InformationReceiver receiver) {
		this.receiver = receiver;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getAllProjects(posemuckel.client.model.Task)
	 */
	public void getAllProjects(Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getMyProjects(posemuckel.client.model.Task)
	 */
	public void getMyProjects(Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#joinProject(java.lang.String, posemuckel.client.model.Task)
	 */
	public void joinProject(String projectID, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#leaveProject(java.lang.String, posemuckel.client.model.Task)
	 */
	public void leaveProject(String projectID, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getProfile(java.lang.String[], posemuckel.client.model.Task)
	 */
	public void getProfile(String[] nicknames, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#setProfile(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, posemuckel.client.model.Task)
	 */
	public void setProfile(String firstname, String surname, String pwd, String email, String lang, String gender, String location, String comment, Task task) {}
	
	/*
	 * 
	 */
	public void loadChatMembers(String id, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#loadChatMembers(java.lang.String)
	 */
	public void loadChatMembers(String id) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#chatting(java.lang.String, java.lang.String)
	 */
	public void chatting(String id, String message) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#typing(java.lang.String)
	 */
	public void typing(String id) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#reading(java.lang.String)
	 */
	public void reading(String id) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getProjectMembers(java.lang.String, posemuckel.client.model.Task)
	 */
	public void getProjectMembers(String id, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#openProject(posemuckel.client.model.Project, posemuckel.client.model.Task)
	 */
	public void openProject(Project project, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#joinChat(java.lang.String, posemuckel.client.model.Task)
	 */
	public void joinChat(String id, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#startChat(java.lang.String[])
	 */
	public void startChat(String[] userToInvite) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#search(posemuckel.client.model.PersonsData, java.lang.String, posemuckel.client.model.Task)
	 */
	public void search(PersonsData person, String text, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#visiting(java.lang.String, java.lang.String, java.lang.String, posemuckel.client.model.Task)
	 */
	public void visiting(String newurl, String title, String oldurl, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getInvitations(posemuckel.client.model.Task)
	 */
	public void getInvitations(Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#rejectInvitation(java.lang.String, posemuckel.client.model.Task)
	 */
	public void rejectInvitation(String id, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#notify(posemuckel.client.model.Project, posemuckel.client.model.Task)
	 */
	public void notify(Project project, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#vote(java.lang.String, java.lang.String)
	 */
	public void vote(String url, String rating) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#viewing(java.lang.String)
	 */
	public void viewing(String URL) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getWebtrace(posemuckel.client.model.Task)
	 */
	public void getWebtrace(Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#addNote(java.lang.String, java.lang.String)
	 */
	public void addNote(String url, String data) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#getNotes(java.lang.String, posemuckel.client.model.Task)
	 */
	public void getNotes(String url, Task task) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#addFolder(java.lang.String, java.lang.String)
	 */
	public void addFolder(String title, String parentID) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#changeParentFolder(java.lang.String, java.lang.String)
	 */
	public void changeParentFolder(String folderID, String parentID) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#deleteFolder(java.lang.String)
	 */
	public void deleteFolder(String folderID) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#changeParentFolderForURL(java.lang.String, java.lang.String)
	 */
	public void changeParentFolderForURL(String url, String parentID) {}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Database#loadFolderStructure(posemuckel.client.model.Task)
	 */
	public void loadFolderStructure(Task task) {}
}
;