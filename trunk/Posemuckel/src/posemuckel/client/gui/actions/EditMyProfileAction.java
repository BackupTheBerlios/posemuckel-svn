/**
 * 
 */
package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import posemuckel.client.gui.GUI_Main_Window;
import posemuckel.client.gui.LoginDialog;
import posemuckel.client.gui.Messages;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import posemuckel.common.GetText;

/**
 * Diese Action dient der Anzeige des eigenen Benutzerprofiles.
 * Sie wurde implementiert, um die Klasse ShowProfileAction
 * schlanker und übersichtlicher gestalten zu können und um
 * dieser Action einen sinnvolleren Namen in den Menüs der GUI
 * geben zu können.
 * 
 * @author Posemuckel Team
 *
 */
public class EditMyProfileAction extends Action {

	private User user = Model.getModel().getUser();

	private String name;
	
	public EditMyProfileAction() {
		super(GetText.gettext("SHOW_REG_DATA"), AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("SHOW_REG_DATA"));
		setImageDescriptor(ImageDescriptor.createFromFile(GUI_Main_Window.class,
				"icons/text.gif"));
//		Listener für die Anzeige von Benutzerprofilen
		ShowProfileAction.initModelListeners();
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
		name = user.getNickname();
		// Vertraut darauf, dass es irgendwo einen Listener gibt,
		// der dann den richtigen Dialog aufmacht.
		Model.getModel().getAllPersons().loadPersonsData(new String[] { name });
		
	}
	
}
