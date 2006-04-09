/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

/**
 * Regelt die Zugriffe auf die Database, die einen Bezug zu einer Projektliste haben.
 * Konkret werden die folgenden Nachrichten (nach RFC0815) bearbeitet:
 * 
 * <ul>
 * <li>MY_PROJECTS</li>
 * <li>GET_PROJECTS</li>
 * <li>JOIN_PROJECT</li>
 * <li>LEAVE_PROJECT</li>
 * <li>START_PROJECT</li>
 * <li>GET_ACTIVE_USERS (Projektmitglieder laden)</li>
 * <li>OPEN_PROJECT</li>
 * <li>VISITING</li>
 * <li>GET_INVITATIONS</li>
 * <li>DONT_ACCEPT_PROJECT</li>
 * <li>NOTIFY</li>
 * </ul>
 *
 * @author Posemuckel Team
 */
class ProjectTask extends TaskAdapter {
	
	static final int MY_PROJECTS = 100;
	static final int GET_PROJECTS = 200;
	static final int JOIN = 300;
	static final int LEAVE = 400;
	static final int START_PROJECT = 500;
	static final int LOAD_PROJECT_MEMBERS = 600;
	static final int OPEN = 700;
	
	static final int VISITING = 800;
	static final int GET_INVITATIONS = 900;
	static final int DONT_JOIN = 1000;
	static final int NOTIFY = 1100;
	
	private int task;
	private ProjectList projects;
	private Project project;
	
	/**
	 * Diese ProjectTask kann alle Aufgaben bearbeiten. Wenn eine Projektliste
	 * oder ein Projekt für die Aufgabe nicht benötigt werden, kann auch null 
	 * übergeben werden.
	 * @param projects Projektliste
	 * @param project das betroffene Projekt
	 */
	ProjectTask(ProjectList projects, Project project) {
		this.projects = projects;
		this.project = project;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#work(int)
	 */
	protected void work(int task) {
		this.task = task;
		switch (task) {
		case GET_PROJECTS:
			DatabaseFactory.getRegistry().getAllProjects(this);
			break;
		case MY_PROJECTS:
			DatabaseFactory.getRegistry().getMyProjects(this);
			break;
		case JOIN:
			DatabaseFactory.getRegistry().joinProject(project.getID(), this);
			break;
		case LEAVE:
			DatabaseFactory.getRegistry().leaveProject(project.getID(), this);
			break;
		case START_PROJECT:
			DatabaseFactory.getRegistry().startProject(project,this);
			break;
		case LOAD_PROJECT_MEMBERS:
			DatabaseFactory.getRegistry().getProjectMembers(project.getID(), this);
			break;
		case OPEN:
			DatabaseFactory.getRegistry().openProject(project,this);
			break;
		case VISITING:
			DatabaseFactory.getRegistry().visiting(project.getCurrentURL(), project.getUrlTitle(), 
			project.getPreviousURL(), this);	
			break;	
		case GET_INVITATIONS:
			DatabaseFactory.getRegistry().getInvitations(this);
			break;
		case DONT_JOIN:
			DatabaseFactory.getRegistry().rejectInvitation(project.getID(), this);
			break;
		case NOTIFY:
			DatabaseFactory.getRegistry().notify(project, this);
			break;
		default:
			break;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(int)
	 */
	public void update(int answer) {
		switch (answer + task) {
		case Database.ACK + JOIN:
			projects.confirmAddProject(project);
			//falls einer Einladung gefolgt wurde, das Element löschen
			project.getModel().getUser().getInvitations().confirmDelProject(project);
			break;
		case Database.ACCESS_DENIED + JOIN:
			projects.fireAccessDenied();
			break;
		case Database.ACK + LEAVE:
			projects.confirmDelProject(project);
			break;
		case Database.ACCESS_DENIED + LEAVE:
			projects.fireAccessDenied();
			break;
		case Database.ACK + START_PROJECT:
			projects.fireStartProject(project, true);
			break;
		case Database.ERROR + START_PROJECT:
			projects.fireStartProject(project, false);
			break;
		case Database.ACCESS_DENIED + START_PROJECT:
			projects.fireAccessDenied();
			break;
		case Database.ACCESS_DENIED + LOAD_PROJECT_MEMBERS:
			projects.fireAccessDenied();
			break;
		case Database.ERROR + LOAD_PROJECT_MEMBERS:
			project.getMemberList().error(LOAD_PROJECT_MEMBERS);
			break;
		case Database.ERROR + OPEN:
			projects.fireProjectOpened(project, false);
			break;
		case Database.ACK + DONT_JOIN:
			projects.confirmDelProject(project);
			break;
		case Database.ACK + NOTIFY:
			project.fireNotifyConfirmation();
			break;
		default:
			break;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(java.util.ArrayList)
	 */
	@SuppressWarnings("unchecked")
	public void update(ArrayList list) {
		switch (task) {
		case MY_PROJECTS:
			//wird genauso wie GET_PROJECTS behandelt, also durchfallen lassen
		case GET_INVITATIONS:
			//wird genauso wie GET_PROJECTS behandelt, also durchfallen lassen
		case GET_PROJECTS:
			projects.confirmLoad(list);
			break;
		case LOAD_PROJECT_MEMBERS:
			project.getMemberList().confirmLoad(list);
			break;
		default:
			break;
		}
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(java.lang.String)
	 */
	@Override
	public void update(String message) {
		if(task == OPEN) {
			projects.confirmOpen(project, message);
		}
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.Task#relayACCESS_DENIED()
	 */
	@Override
	public boolean relayACCESS_DENIED() {
		return task == JOIN;
	}

	
}
