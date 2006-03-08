/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.swt.widgets.Display;

import posemuckel.client.model.Model;
import posemuckel.client.model.event.ProjectEvent;
import posemuckel.client.model.event.ProjectListenerAdapter;

/**
 * 
 * @author Posemuckel Team
 *
 */

public class InvitationInfo extends ProjectListenerAdapter {
	
	public InvitationInfo() {
		Model.getModel().getUser().getInvitations().addListener(this);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.ProjectListenerAdapter#newProject(posemuckel.client.model.event.ProjectEvent)
	 */
	@Override
	public void newProject(final ProjectEvent event) {
		Runnable run = new Runnable() {
			public void run() { 
				String addressee = Model.getModel().getUser().getNickname();
				String project = event.getProjectSource().getTopic();
				String description = event.getProjectSource().getDescription();
				String owner = event.getProjectSource().getOwner();
				//TODO welcher Typ?
				//Messages.showInfo(message, title);
				//new MessageWindow(Display.getDefault().getActiveShell(), project, owner);
				
				InvitationDialog idiag = new InvitationDialog(Display.getDefault().getActiveShell(), addressee, project, description, owner);
				idiag.open();
			}
		};				
		Display.getDefault().asyncExec(run);
	}
}
