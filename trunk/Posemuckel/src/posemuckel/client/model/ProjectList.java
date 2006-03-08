/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import posemuckel.client.model.event.ListenerManagment;
import posemuckel.client.model.event.ProjectEvent;
import posemuckel.client.model.event.ProjectListener;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.GetText;

/**
 * Im Model kommen Projektlisten an folgenden Stellen vor: <br/>
 * 
 * das Model besitzt eine Liste aller Projekte<br/>
 * der User besitzt eine Liste seiner Projekte<br>
 * der User besitzt eine Liste aller offenen Einladungen<br>
 * 
 * Die angegebenen Listen unterscheiden sich durch ihren Typ. F&uuml;r Zugriffe
 * auf die <code>Database</code> wird eine <code>Task</code> verwendet, die 
 * die Antwort der <code>Database</code> entgegennehmen kann und f&uuml; ein 
 * Update der Projektliste sorgt.
 * 
 * @author Posemuckel Team
 *
 */
public class ProjectList {

	/**
	 * eine Projektliste diesen Typs enth&auml;lt alle Projekte, denen der Anwender
	 * beitreten kann oder denen er bereits beigetreten ist
	 */
	public static final String ALL_PROJECTS = "ALL_PROJECTS";
	
	/**
	 * eine Projektliste diesen Typs enth&auml;lt alle Projekte, denen ein Anwender
	 * beigetreten ist
	 */
	public static final String MY_PROJECTS = "MY_PROJECTS";
	
	/**
	 * eine Projektliste diesen Typs enth&auml;lt alle Projekte mit Einladungen, 
	 * die der Anwender noch nicht angenommen hat
	 */
	public static final String OPEN_INVITATIONS = "INVITATIONS";
	
	private Map<String, Project> list;
	private Model model;
	private String type;
	private ListenerManagment<ProjectListener> listenerManagment;
	
	/**
	 * Erstellt eine Projektliste mit dem angegebenen Typ. Das Model bietet Zugriff
	 * auf den Anwender, zu dem die Projektliste geh&ouml;rt.<br>
	 * 
	 * Als Typen sind <code>MY_PROJECTS</code> und <code>ALL_PROJECTS</code>
	 * zul&auml;ssig.
	 * 
	 * @param model das Model, welche Zugriff auf den Anwender bietet
	 * @param type der Typ des Projektes
	 */
	protected ProjectList(Model model, String type) {
		initMap();
		this.model = model;
		this.type = type;
		listenerManagment = new ListenerManagment<ProjectListener>();
	}
	
	/**
	 * Initialisiert die Verwaltung der Projekte.
	 *
	 */
	private void initMap() {
		/* die Anzahl der konkurierenden Zugriffe wird gleich der Anzahl der Threads
		 * im Programm gesetzt. Die anderen Werte sind die Standardwerte einer 
		 * ConcurrentHashMap.
		 */
		list = new ConcurrentHashMap<String, Project>(16, 0.75f, 3);
	}
	
	/**
	 * Gibt an, ob die Liste leer ist.
	 * @return true, falls die Liste leer ist
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	/**
	 * Gibt an, wie viele Projekte in der Liste enthalten sind.
	 * @return Anzahl der Projekte in der Liste
	 */
	public int size() {
		return list.size();
	}
	
	/**
	 * Gibt den Typ dieser Projekliste aus.
	 * Als Typen sind <code>MY_PROJECTS</code> und <code>ALL_PROJECTS</code>
	 * zul&auml;ssig.
	 * @return Typ des Projektes
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sucht das Projekt mit der ID und gibt es aus.
	 * @param id ID des gesuchten Projektes
	 * @return das gesuchte Projekt
	 */
	public Project getProject(String id) {
		return list.get(id);
	}
	
	/**
	 * Gibt alle Projekte dieser Projektliste als Array aus.
	 * 
	 * @return alle Projekte dieser Liste
	 */
	public Project[] getProjects() {
		Project[] projects = new Project[list.size()];
		Collection<Project> c = list.values();
		//erm&ouml;glicht sp&auml;ter ein Typecast, was mit c.toArray() nicht m&ouml;glich ist
		c.toArray(projects);
		return projects;
	}
	
	/**
	 * Wird verwendet, um die <code>Database</code> zum Erstellen und Einf&uuml;gen 
	 * eines neuen Projektes aufzufordern. Alle Daten zum Erstellen
	 * des Projektes befinden sich in der <code>Project</code>-Instanz.
	 * 
	 * @param project das neue Projekt
	 */
	public void startProject(Project project) {
		ProjectTask task = new ProjectTask(this,project);
		task.execute(ProjectTask.START_PROJECT);
	}
	
	/**
	 * L&auml;d die Liste aus der <code>Database</code>. Die Liste ist durch den Typ zusammen mit
	 * dem Anwender eindeutig identifiziert.
	 *
	 */
	public void load() {
		if(!isEmpty())initMap();
		if(type.equals(ProjectList.ALL_PROJECTS)) {
			new ProjectTask(this, null).execute(ProjectTask.GET_PROJECTS);
		} else if (type.equals(ProjectList.MY_PROJECTS)) {
			new ProjectTask(this, null).execute(ProjectTask.MY_PROJECTS);
		} else if(type.equals(ProjectList.OPEN_INVITATIONS)) {
			new ProjectTask(this, null).execute(ProjectTask.GET_INVITATIONS);
		}
	}
	
	/**
	 * Gibt das erste Projekt mit dem angegebenen Thema aus.
	 * 
	 * @param topic Thema
	 * @return das erste Projekt mit dem angegebenen Thema
	 */
	public Project searchByTopic(String topic) {
		Project result = null;
		Collection<Project> col = list.values();
		for (Project project : col) {
			if(project.getTopic().equals(topic)) {
				result = project;
				break;
			}			
		}
		return result;
	}
	
	/**
	 * Wird von einer <code>Task</code> verwendet, um ein neues Projekt in die
	 * Liste einzuf&uuml;gen. Diese Methode wird auf einer Liste des Typs 
	 * <code>MY_PROJECTS</code> aufgerufen, wenn ein 
	 * Aufruf von <code>startProject()</code> mit Erfolg verlaufen ist. Sie wird
	 * auf einer Liste des Typs <code>ALL_PROJECTS</code> aufgerufen, wenn ein
	 * neues Projekt in die <code>Database</code> eingef&uuml;gt wurde, zu dem 
	 * der Anwender Zutritt hat. 
	 * 
	 * @param project das Projekt, das neu erstellt wurde
	 */
	public void confirmAddProject(Project project) {
		list.put(project.getID(), project);
		fireAddProject(project, true);
	}
		
	/**
	 * Wird von einer <code>Task</code> verwendet, um das Laden der Projektliste
	 * mitzuteilen. Diese Methode wird aufgerufen, wenn ein 
	 * Aufruf von <code>load()</code> mit Erfolg verlaufen ist.
	 * 
	 * @param projects die Projekte, die von der Datenbank geladen wurden
	 */
	public void confirmLoad(ArrayList<Project> projects) {
		for (Project proj : projects) {
			proj.setModel(model);
			list.put(proj.getID(), proj);
		}
		fireLoaded(true);
	}
	
	/**
	 * Wird von einer <code>Task</code> verwendet, um ein Projekt aus der
	 * Liste zu l&ouml;schen. Diese Methode wird auf einer Liste des Typs 
	 * <code>MY_PROJECT</code> aufgerufen, wenn ein 
	 * Aufruf von <code>Project#leave</code> mit Erfolg verlaufen ist.
	 * 
	 * @param project das Projekt, das neu erstellt wurde
	 */
	public void confirmDelProject(Project project) {
		list.remove(project.getID());	
		fireDelProject(project, true);
	}
	
	/**
	 * Wird von einer <code>Task</code> verwendet, mitzuteilen, dass
	 * eine Operation fehlgeschlagen ist. Diese Methode wird auf einer Liste des Typs 
	 * <code>MY_PROJECT</code> aufgerufen, wenn ein 
	 * Aufruf von <code>Project#leave</code> ohne Erfolg verlaufen ist.
	 */
	public void fireAccessDenied() {
		ArrayList<ProjectListener>  listener = getListener();
		for (ProjectListener projectListener : listener) {
			((ProjectListener)projectListener).accessDenied();
		}
	}
	
	/**
	 * Gibt den Typ und alle Projekte in einem String aus.
	 */
	@Override
	public String toString() {
		String str = type + "\n";
		Collection<Project> col = list.values();
		for (Project project : col) {
			str+= project.toString() + "\n";
		}
		return str;
	}
		
	/**
	 * Informiert die Projektliste &uuml;ber die &Auml;nderung der Zahl der 
	 * freien Pl&auml;tze. Die Information wird an das Projekt und an alle
	 * Listener der Liste weitergeleitet.
	 * 
	 * @param change Anstieg oder Verringerung der Zahl der freien Plätze
	 * @param projectID Id des ge&auml;nderten Projektes
	 */
//	public void informAboutNewProjectMember(int change, String projectID) {
//	confirmElementChanged(list.get(projectID));
//	}
	
	/**
	 * Informiert die Projektliste &uuml;ber ein Projekt, das ge&ouml;ffnet wurde.
	 * Das Model wird &uuml;ber einen neuen Chat sowie &uuml;ber das 
	 * ge&ouml;ffnete Projekt informiert. 
	 * 
	 * @param project das ge&ouml;ffnete Projekte
	 * @param chatID die ID des Chat
	 */
	public void confirmOpen(Project project, String chatID) {
		if( model.getLogchat() != null ) {
			model.removeLogchat();
		}
		new Chat(EnumsAndConstants.LOG_CHAT_ID, model).setTitle(GetText.gettext("LOG_CHAT"));
		model.setOpenProject(project);
		Chat chat = new Chat(chatID, model);
		project.setOpen(chatID);
		if(!project.getMemberList().hasMember(model.getUser().getNickname())) {
			project.getMemberList().confirmAddMember(model.getUser());
		}
		chat.setMemberList(project.getMemberList());
		project.getWebtrace().load();
		fireProjectOpened(project, true);
	}
	
	/**
	 * Gibt an, ob diese Projektliste das Projekt mit der ID id enthällt.
	 * @param id des gesuchten Projektes
	 * @return true, falls das Projekt in dieser Liste enthalten ist
	 */
	public boolean contains(String id) {
		return list.containsKey(id);
	}
	
	/**
	 * Teilt allen Listenern mit, ob ein neues Projekt in die Liste eingefügt wurde.
	 * 
	 * @param project das neue Projekt
	 * @param succeeded true , wenn ein Projekt eingefügt wurde
	 */
	protected void fireAddProject(Project project, boolean succeeded) {
		ArrayList<ProjectListener> listener = getListener();
		for (ProjectListener projectListener : listener) {
			((ProjectListener)projectListener).newProject(new ProjectEvent(this, project, succeeded));
		}
	}
	
	/**
	 * Teilt allen Listenern mit, ob ein Versuch, das Projekt aus der Liste zu
	 * entfernen, erfolgreich war.
	 * @param project das entfernte Projekt
	 * @param succeeded true, falls das Projekt entfernt wurde
	 */
	protected void fireDelProject(Project project, boolean succeeded) {
		ArrayList<ProjectListener> listener = getListener();
		for (ProjectListener projectListener : listener) {
			((ProjectListener)projectListener).deleteProject(new ProjectEvent(this, project, succeeded));
		}
	}
	
	/**
	 * Informiert alle Listener, ob die List von der Database geladen werden
	 * konnte.
	 * @param succeeded true, falls die Liste geladen werden konnte
	 */
	protected void fireLoaded(boolean succeeded) {
		ArrayList<ProjectListener> listener = getListener();
		for (ProjectListener projectListener : listener) {
			((ProjectListener)projectListener).listLoaded(new ProjectEvent(this, null, succeeded));
		}
	}
	
	/**
	 * Wird verwendet, um die Listener &uuml;ber eine &Auml;nderung eines Projektes
	 * zu informieren. Als Argument wird die Referenz auf die ge&auml;nderte
	 * Projektinstanz erwartet, die auch in dieser Liste vorhanden ist.
	 * Es sollte keine Kopie (aus einer anderen Projektliste) verwendet werden. 
	 * 
	 * @param project eine Referenz auf das ge&auml;nderte Projekt
	 * @param succeeded true, falls das Projekt geändert werden konnte
	 */
	protected void fireElementChanged(Project project, boolean succeeded) {
		if(project != null) {
			ArrayList<ProjectListener> listener = getListener();
			for (ProjectListener projectListener : listener) {
				((ProjectListener)projectListener).elementChanged(new ProjectEvent(this, project, succeeded));
			}
		}
	}
	
	/**
	 * Informiert alle Listener über das Öffnen eines Projektes. 
	 * @param opened das geöffnete Projekt
	 * @param succeeded true, falls das Projekt geöffnet werden konnte
	 */
	protected void fireProjectOpened(Project opened, boolean succeeded) {
		ArrayList<ProjectListener>  listener = getListener();
		for (ProjectListener projectListener : listener) {
			((ProjectListener)projectListener).openProject(new ProjectEvent(this, opened, succeeded));
		}
	}
	
	/**
	 * Teilt allen Listenern mit, dass der Anwender ein neues Projekt 
	 * erfolgreich gestartet hat. 
	 * @param project das neue Projekt
	 * @param succeeded true, falls das Projekt gestartet werden konnte
	 */
	protected void fireStartProject(Project project, boolean succeeded) {
		//das Einfügen in die Projektlisten wird vom InformationReceiver geregelt:
		//alle Projekte werden gleich behandelt
		ArrayList<ProjectListener>  listener = getListener();
		for (ProjectListener projectListener : listener) {
			((ProjectListener)projectListener).confirmStartProject(new ProjectEvent(this, project, succeeded));
		}		
	}
	
	/**
	 * Registriert einen ProjectListener, der über Ereignisse, die diese Instanz
	 * betreffen, informiert werden möchte. 
	 * @param listener der zu registrierende ProjectListener
	 */
	public void addListener(ProjectListener listener) {
		listenerManagment.addListener(listener);
	}

	/**
	 * Gibt eine Kopie der Liste mit allen ProjectListenern aus.
	 * @return Kopie der Liste mit allen ProjectListenern
	 */
	public ArrayList<ProjectListener> getListener() {
		return listenerManagment.getListener();
	}

	/**
	 * Entfernt den ProjectListener aus der Liste der ProjectListener.
	 * @param listener der zu entfernende ProjectListener
	 */
	public void removeListener(ProjectListener listener) {
		listenerManagment.removeListener(listener);
	}
}
