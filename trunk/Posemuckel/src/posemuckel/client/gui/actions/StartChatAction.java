/**
 * 
 */
package posemuckel.client.gui.actions;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import posemuckel.client.gui.GUI_Main_Window;
import posemuckel.client.gui.LoginDialog;
import posemuckel.client.gui.Messages;
import posemuckel.client.model.Model;
import posemuckel.common.GetText;

/**
 * StartChatAction startet einen privaten Chat mit einem anderen Benutzer.
 * Zur Initialisierung wird zur Zeit die Tabelle, in der der Anwender eingetragen
 * ist, ben&ouml;tigt.
 * 
 * @author Posemuckel Team
 *
 */

public class StartChatAction extends Action {
	
	private Table table;

	public StartChatAction(Table table) {
		super(GetText.gettext("START_CHAT"), AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("START_CHAT"));
		setImageDescriptor(ImageDescriptor.createFromFile(GUI_Main_Window.class,
				"icons/blank.gif"));
		this.table = table;
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
		if(table != null && table.getSelectionCount() > 0) {
			TableItem[] items = table.getSelection();
			ArrayList<String> names = new ArrayList<String>(items.length);
			for (int i = 0; i < items.length; i++) {
				names.add(items[i].getText(1));
			}
			Model.getModel().getUser().startChatWith(names);
		}		
	}
}
