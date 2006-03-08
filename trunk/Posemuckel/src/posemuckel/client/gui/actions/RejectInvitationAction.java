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
 * 
 * @author Posemuckel Team
 *
 */

public class RejectInvitationAction extends Action {

	private Table projects;
	
	public RejectInvitationAction(Table projects) {
		super(GetText.gettext("REJECT_INVITATION"), AS_PUSH_BUTTON);
		//setToolTipText("leave a project");
//		setImageDescriptor(ImageDescriptor.createFromFile
//				(this.getClass(),"run.gif"));
		this.projects = projects;		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (!Model.getModel().getUser().isLoggedIn()) {
			// Eine entsprechende Meldung anzeigen...
			Messages.showInfo(GetText.gettext("NOT_LOGGED_IN_MESSAGE"),GetText.gettext("NOT_LOGGED_IN_TITLE"));
			// LoginDialog andrehen
			new LoginDialog(Display.getDefault().getActiveShell());
			// und abbrechen:
			return;
		}
		TableItem[] items = (TableItem[])projects.getSelection();
		if(items.length > 0) {
			TableItem item = items[0];
			if(item != null && item.getData() != null) {
				Project p = (Project)item.getData();
				p.rejectInvitation();
			}
		}
	}
	

}
