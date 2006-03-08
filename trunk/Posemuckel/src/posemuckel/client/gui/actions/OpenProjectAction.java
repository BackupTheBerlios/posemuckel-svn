/**
 * 
 */
package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import posemuckel.client.gui.GUI_Main_Window;
import posemuckel.client.gui.LoginDialog;
import posemuckel.client.gui.Messages;
import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.client.model.ProjectList;
import posemuckel.client.model.event.ProjectEvent;
import posemuckel.client.model.event.ProjectListenerAdapter;
import posemuckel.client.net.Client;
import posemuckel.common.GetText;

/**
 * Triggert das &Ouml;ffnen eines Projektes.
 * 
 * @author Posemuckel Team
 *
 */

public class OpenProjectAction extends Action {

	private Table table;
	
	private boolean close=false;

	// Diese Constructors stimmen so nicht!
	public OpenProjectAction() {
		super(GetText.gettext("OPEN_PROJECT"), AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("OPEN_PROJECT"));
		setImageDescriptor(ImageDescriptor.createFromFile(GUI_Main_Window.class,
				"icons/dir.gif"));

	}
	
	public OpenProjectAction(boolean c) {
		super(GetText.gettext("OPEN_PROJECT"), AS_PUSH_BUTTON);
		close=c;
		setToolTipText(GetText.gettext("OPEN_PROJECT"));
		setImageDescriptor(ImageDescriptor.createFromFile(GUI_Main_Window.class,
				"icons/dir.gif"));

	}

	public OpenProjectAction(Table t) {
		super(GetText.gettext("OPEN_PROJECT"), AS_PUSH_BUTTON);
		table = t;
		setToolTipText(GetText.gettext("OPEN_PROJECT"));
		setImageDescriptor(ImageDescriptor.createFromFile(GUI_Main_Window.class,
				"icons/dir.gif"));
	}

	
	public OpenProjectAction(Table t, boolean c) {
		super(GetText.gettext("OPEN_PROJECT"), AS_PUSH_BUTTON);
		table = t;
		close=c;
		setToolTipText(GetText.gettext("OPEN_PROJECT"));
		setImageDescriptor(ImageDescriptor.createFromFile(GUI_Main_Window.class,
				"icons/dir.gif"));	
	}
	
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
		if (table != null && (table.getSelection() != null)
				&& table.getSelection().length > 0) {
			addListener();
			((Project)table.getSelection()[0].getData()).open();
		}
	}

	public void setTable(Table table) {
		this.table = table;
	}
	
	private void addListener() {
		final ProjectList list = Model.getModel().getUser().getProjects();
		ProjectListenerAdapter adapter = new ProjectListenerAdapter() {

			/* (non-Javadoc)
			 * @see posemuckel.client.model.event.ProjectListenerAdapter#openProject(posemuckel.client.model.event.ProjectEvent)
			 */
			@Override
			public void openProject(ProjectEvent event) {
				Runnable run = new Runnable() {
					public void run() {
						Client.goToBrowser();
						if (close==true) {
							table.getShell().close();
						};
					}
				};				
				Display.getDefault().asyncExec(run);
				list.removeListener(this);
			}
			
		};
		list.addListener(adapter);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}
	
	

}
