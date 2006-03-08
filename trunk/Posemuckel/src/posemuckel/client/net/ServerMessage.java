/**
 * 
 */
package posemuckel.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import posemuckel.client.model.Database;
import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.InformationReceiver;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.PersonsData;
import posemuckel.client.model.Project;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.InvalidMessageException;
import posemuckel.common.Message_Handler;
import posemuckel.common.VerboseClientReader;

/**
 * Liest die vom Server kommenden Nachrichten und reicht sie an Netbase bzw.
 * den InformationReceiver weiter. Hierzu muss der InformationReceiver
 * gesetzt werden. 
 * 
 * @see posemuckel.client.model.InformationReceiver
 * @author Posemuckel Team
 *
 */
public class ServerMessage extends Message_Handler {
	
	private InformationReceiver receiver;
	
	/**
	 * Liest die vom Server kommenden Nachrichten.
	 *
	 */
	public ServerMessage() {
	}
	
	/**
	 * Hilfsmethode, die aus dem Reader die angegebene Zeilenzahl liest.
	 * 
	 * @param in Reader
	 * @param count Zeilenzahl
	 * @return String-Array mit den gelesenen Zeilen
	 * @throws IOException
	 */
	private String[] parse(BufferedReader in, int count) throws IOException {
		String[] result = new String[count];
		for(int i = 0; i < count; i++) {
			result[i] = in.readLine();
		}
		return result;
	}
	
	/**
	 * Liest den Eingabestrom und gibt die gelesenen Zeilen in einer ArrayList
	 * aus.
	 * @param in Eingabestrom
	 * @param count Zeilenzahl
	 * @return ArrayList mit den gelesenen Zeilen
	 * @throws IOException
	 */
	private ArrayList<String> parseIntoList(BufferedReader in, int count) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < count; i++) {
			list.add(in.readLine());
		}
		return list;
	}

		
	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#access_denied(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean access_denied(String user, String id, String count, BufferedReader in) throws InvalidMessageException {
		if(!count.equals("0"))throw new InvalidMessageException("access_denied is to long!");
		((Netbase)DatabaseFactory.getRegistry()).update(Database.ACCESS_DENIED, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#access_granted(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean access_granted(String user, String id, String count, BufferedReader in) throws InvalidMessageException {
		if(!count.equals("0"))throw new InvalidMessageException("access_granted is to long!");
		Client.setClientHash(user);
		((Netbase)DatabaseFactory.getRegistry()).update(Database.ACCESS_GRANTED, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#ack(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean ack(String user, String id, String count, BufferedReader in) throws InvalidMessageException {
		if(!count.equals("0"))throw new InvalidMessageException("ack is to long!");
		((Netbase)DatabaseFactory.getRegistry()).update(Database.ACK, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#chat(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean chat(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		/*
		 * enth&auml;lt CHAT_ID und MESSAGE
		 */
		String[] message = parse(in, Integer.valueOf(count));
		// Kopiere mehrere Chat Zeilen in eine Nachricht:
		if( message.length > 2 )
			for (int i=2; i<message.length; i++)
				message[1]+=EnumsAndConstants.LS+message[i];
		receiver.chatting(user, message[0], message[1]);
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#newInvitation(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean newInvitation(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] projectID = parse(in, Integer.valueOf(count));
		receiver.newInvitation(projectID[0]);
		return true;
	}	

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#newNote(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean newNote(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] data = parse(in, Integer.valueOf(count));
		//URL, Bewertung
		receiver.newNote(user, data[0], data[1], data[2]);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#notes(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean notes(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		ArrayList<String> messageData = new ArrayList<String>();
		int lines = Integer.valueOf(count);
		for(int i = 0; i < lines; i++) {
			messageData.add(in.readLine());
		}
		((Netbase)DatabaseFactory.getRegistry()).update(messageData, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#newMember(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean memberChange(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] data = parse(in, Integer.valueOf(count));
		if(data[0].equals("PROJECT")) {
			receiver.informAboutProjectMemberChange(user, data[1], data[2].equals("1"));
		}
		return true;
	}

	/** Wenn sich die Zusammensetzung der Chat-Gruppe ändert (z.B. bei einem Login)
	 * versendet der Server eine CHAT_MEMBERS-Nachricht
	 * die bisherige Liste mit den Teilnehmern wird dann gelöscht und die neue Liste
	 * aufgebaut
	 * @param user da bei dieser Nachricht kein spezieller Benutzer angesprochen wird,
	 * 		ist der String in diesem Fall leer
	 * @param id die Nachrichten-ID
	 * @param count die Anzahl der Parameter dieser Nachricht
	 * @param in der Reader, der pro Zeile einen Chat-Member enthält
	 * @return true
	 * @see posemuckel.common.Message_Handler#chatMembers(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean chatMembers(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] members = parse(in, Integer.valueOf(count));
		receiver.updateChatMembers(members);
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#newChat(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean newChat(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		if(Integer.valueOf(count) < 2) throw new InvalidMessageException("newChat is to short!");
		String chatID = in.readLine();
		String owner = in.readLine();
		String[] invited = parse(in, Integer.valueOf(count)-2);
		receiver.informAboutNewChat(chatID, owner, invited);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#error(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean error(String user, String id, String count, BufferedReader in) throws InvalidMessageException {
		if(!count.equals("0"))throw new InvalidMessageException("error is to long!");
		((Netbase)DatabaseFactory.getRegistry()).update(Database.ERROR, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#reading(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean reading(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		/*
		 * enth&auml;lt CHAT_ID
		 */
		String[] message = parse(in, Integer.valueOf(count));
		receiver.reading(user, message[0]);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#typing(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean typing(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		/*
		 * enth&auml;lt CHAT_ID
		 */
		String[] message = parse(in, Integer.valueOf(count));
		receiver.typing(user, message[0]);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#voting(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean voting(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		String[] message = parse(in, Integer.valueOf(count));
		//user, url, vote
		receiver.voting(user, message[0], message[1]);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#userExists(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean userExists(String user, String id, String count, BufferedReader in) throws InvalidMessageException {
		if(!count.equals("0"))throw new InvalidMessageException("userExists is to long!");
		((Netbase)DatabaseFactory.getRegistry()).update(Database.USER_EXITS, id);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#users(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean users(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] users = parse(in, Integer.valueOf(count));
		ArrayList<Person> list = new ArrayList<Person>();
		for(int i = 0; i < users.length; i+= 8) {
			//Benutzername
			Person p = new Person(users[i]);
			p.setData(users[i+1], users[i+2], users[i+3], users[i+7], users[i+4] , users[i+5], users[i+6]);
			list.add(p);
		}
		((Netbase)DatabaseFactory.getRegistry()).update(list, id);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#userStatus(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean userStatus(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] message = parse(in, Integer.valueOf(count));
		//Benutzername, OnlineStatus
		receiver.userStatusChanged(message[0], message[1]);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#activeUsers(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean activeUsers(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] members = parse(in, Integer.valueOf(count));
		ArrayList<Person> list = new ArrayList<Person>();
		
		for(int i = 0; i < members.length; i++) {
			Person p = new Person(members[i]);
			list.add(p);
		}
		((Netbase)DatabaseFactory.getRegistry()).update(list, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#projectOpened(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean projectOpened(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] data = parse(in, Integer.valueOf(count));
		//data[0] ist die ChatID
		((Netbase)DatabaseFactory.getRegistry()).update(data[0], id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#profile(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean profile(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] data = parse(in, Integer.valueOf(count));
		ArrayList<PersonsData> list = new ArrayList<PersonsData>();
		for(int i = 0; i < data.length; i+= 8) {
			PersonsData person = new PersonsData();
			person.setFirstName(data[i + 0]);
			person.setSurname(data[i + 1]);
			person.setEmail(data[i + 2]);
			person.setNickname(data[i + 3]);
			person.setLang(data[i + 4]);
			person.setGender(data[i + 5]);
			person.setLocation(data[i + 6]);
			person.setComment(data[i + 7]);
			list.add(person);
		}
		((Netbase)DatabaseFactory.getRegistry()).update(list, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#newBuddy(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean newBuddy(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		if(!count.equals("1"))throw new InvalidMessageException("newBuddy is to long!");
		String status = in.readLine();
		((Netbase)DatabaseFactory.getRegistry()).update(status, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#yourBuddies(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean yourBuddies(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] buddys = parse(in, Integer.valueOf(count));
		ArrayList<Person> list = new ArrayList<Person>();
		/*
		 * pro Buddy enth&auml;lt das Array zwei Zeilen:
		 * Nickname und Status
		 */
		for(int i = 0; i < buddys.length; i+= 2) {
			Person p = new Person(buddys[i], buddys[i+1].toUpperCase());
			list.add(p);
		}
		((Netbase)DatabaseFactory.getRegistry()).update(list, id);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#webtrace(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean webtrace(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		ArrayList<String> messageData = new ArrayList<String>();
		int lines = Integer.valueOf(count);
		for(int i = 0; i < lines; i++) {
			messageData.add(in.readLine());
		}
		((Netbase)DatabaseFactory.getRegistry()).update(messageData, id);
		return true;
	}
	
	/**
	 * Setzt den Receiver für Nachrichten, die vom Server ohne vorherige Anfrage
	 * gesendet werden.
	 * @param receiver
	 */
	public void addReceiver(InformationReceiver receiver) {
		this.receiver = receiver;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#newProject(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean newProject(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] infos = parse(in, Integer.valueOf(count));
		if(!count.equals("8"))throw new IllegalArgumentException("message to long");
		Project project = new Project();
		//id, topic, owner, isPublic, Zahl freier Plätze, Zahl Plätze gesamt, Beschreibung, Datum
		project.setData(infos[0], infos[1], infos[2], infos[3], infos[4], 
				infos[5], infos[6], infos[7]);
		receiver.informAboutNewProject(project);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#yourProjects(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean yourProjects(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] infos = parse(in, Integer.valueOf(count));
		ArrayList<Project> list = new ArrayList<Project>();
	    for(int i = 0; i < infos.length; i+=8) {
			Project project = new Project();
			//id, topic, owner, , isPublic, freeSpaces, spaces, description, date
			project.setData(infos[i], infos[i + 1], infos[i + 2], 
					infos[i + 3], infos[i + 4], infos[i + 5], infos[i + 6], infos[i + 7]);
			list.add(project);
		}
		((Netbase)DatabaseFactory.getRegistry()).update(list, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#projects(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean projects(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		//wird genauso verarbeitet wie your_projects
		yourProjects(hash, id, count, in);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#visiting(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	protected boolean visiting(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		//wird genauso verarbeitet wie your_projects
		String user = hash;
		String newurl = in.readLine();
		String title = in.readLine();
		String oldurl = in.readLine();
		receiver.informAboutVisiting(user, newurl, title, oldurl);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#viewing(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean viewing(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		String[] infos = parse(in, Integer.valueOf(count));
		//an der Stelle 0 steht die URL
		receiver.informAboutViewing(user, infos[0]);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#deleteFolder(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean deleteFolder(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		String[] infos = parse(in, Integer.valueOf(count));
		receiver.informAboutFolderRemoval(infos[0]);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#moveFolder(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean moveFolder(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		String[] infos = parse(in, Integer.valueOf(count));
		receiver.informAboutParentFolderChanged(infos[0], infos[1]);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#newFolder(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean newFolder(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		String[] infos = parse(in, Integer.valueOf(count));
		//title, id, parentID
		receiver.informAboutNewFolder(infos[1], infos[0], infos[2]);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#foldersystem(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean foldersystem(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		int folderSize = Integer.parseInt(in.readLine());
		int coupleSize = Integer.parseInt(in.readLine());
		ArrayList<String> folderStructure = parseIntoList(in, folderSize);
		ArrayList<String> couples = parseIntoList(in, coupleSize);
		((Netbase)DatabaseFactory.getRegistry()).updateFolders(folderStructure, couples, id);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#parentfolderChanged(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	@Override
	protected boolean parentfolderChanged(String user, String id, String count, BufferedReader in) throws InvalidMessageException, IOException, SQLException {
		String[] data = parse(in, Integer.parseInt(count));
		//URL, FolderID
		receiver.informAboutParentFolderForUrl(data[0], data[1]);
		return true;
	}

	/* (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#notify(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	protected boolean notify(String hash, String id, String count, BufferedReader in) throws InvalidMessageException, IOException {
		//wird genauso verarbeitet wie your_projects
		String user = hash;
		String url = in.readLine();
		String title = in.readLine();
		String comment = in.readLine();
		String pic_data_length = in.readLine();
		//durch die switch-Anweisungen bleibt die alte DebugEinstellung erhalten
		VerboseClientReader.switchOff();
		String pic = in.readLine();
		VerboseClientReader.switchOn();
		// Leite die Nachricht nur weiter,
		// wenn der Benutzer gerade
		// im Internet surft. 
		if(Model.getModel().getUser().getState().equals(Person.BROWSING))
			receiver.notify(user, url, title, comment, pic_data_length, pic);
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.common.Message_Handler#freeSpacesChanged(java.lang.String, java.lang.String, java.lang.String, java.io.BufferedReader)
	 */
	protected boolean freeSpacesChanged(String user, String id, 
			String count, BufferedReader in) throws 
			InvalidMessageException, IOException {
		String[] infos = parse(in, Integer.valueOf(count));
		if(!count.equals("2"))throw new IllegalArgumentException("message to long");
		receiver.freeSeatsChanged(new Integer(infos[1]).intValue(),
				infos[0]);
		return true;
	}

		
}