/**
 * 
 */
package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import posemuckel.client.gui.LoginDialog;
import posemuckel.client.gui.Messages;
import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.common.GetText;

/**
 * Diese Aktion triggert das verlassen eines Projektes. Das Projekt muss in 
 * MyProjects bekannt sein.
 * 
 * @author Posemuckel Team
 *
 */

public class LeaveProjectAction extends Action {

	private Table projects;
	
	public LeaveProjectAction(Table projects) {
		super(GetText.gettext("LEAVE_PROJECT"), AS_PUSH_BUTTON);
		setToolTipText("leave a project");
//		setImageDescriptor(ImageDescriptor.createFromFile
//				(this.getClass(),"run.gif"));
		this.projects = projects;		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		// Erstmal prüfen, ob der B-Nutzer überhaupt eingelogt
		// ist:
		if (!Model.getModel().getUser().isLoggedIn()) {
			// Eine entsprechende Meldung anzeigen...
			Messages.showInfo(GetText.gettext("NOT_LOGGED_IN_MESSAGE"),GetText.gettext("NOT_LOGGED_IN_TITLE"));
			// LoginDialog andrehen
			new LoginDialog(Display.getDefault().getActiveShell());
			// und abbrechen:
			return;
		}
		TableItem item = (TableItem)projects.getSelection()[0];
		if(item != null && item.getData() != null) {
			Project p = (Project)item.getData();
			p.leave();
		}
	}
	
	

}
