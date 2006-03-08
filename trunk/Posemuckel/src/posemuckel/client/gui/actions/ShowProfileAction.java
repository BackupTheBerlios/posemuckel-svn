/**
 * 
 */
package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;

import posemuckel.client.gui.GUI_Main_Window;
import posemuckel.client.gui.LoginDialog;
import posemuckel.client.gui.Messages;
import posemuckel.client.gui.ProfileDialog;
import posemuckel.client.model.Model;
import posemuckel.client.model.event.MemberListAdapter;
import posemuckel.client.model.event.MemberListListener;
import posemuckel.client.model.event.PersonsEvent;
import posemuckel.client.net.Client;
import posemuckel.common.GetText;

public class ShowProfileAction extends Action {

	private List list;

	private Table table;
	
	private boolean loadData;
	
	private static MemberListListener memberListener;

	public ShowProfileAction() {
		super(GetText.gettext("SHOW_PROFILE"), AS_PUSH_BUTTON);
		loadData = true;
		setToolTipText(GetText.gettext("SHOW_PROFILE"));
		setImageDescriptor(ImageDescriptor.createFromFile(GUI_Main_Window.class,
				"icons/text.gif"));
		initModelListeners();
	}

	public ShowProfileAction(List list) {
		this();
		this.list = list;
	}

	public ShowProfileAction(Table table) {
		this();
		this.table = table;
	}
	
	/**
	 * Verhindert, dass bei ShowProfile die Daten vom Server geladen werden.
	 *
	 */
	public void dontLoad() {
		loadData = false;
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
		//es muss der Name der Person, deren Profile ermittelt werden soll, gelesene 
		//werde
		String name = "";
		if (list != null && list.getSelectionCount() > 0) {
			name = (list.getSelection())[0];

		} else if (table != null && table.getSelectionCount() > 0) {
			name = (table.getSelection())[0].getText(1);
		} 
		if (loadData && name != "" && Client.hasConnection()) {
			Model.getModel().getAllPersons().loadPersonsData(
					new String[] { name });
		} else if(!loadData && name != "") {
			//TODO hoffentlich funktioniert das
			new ProfileDialog(Display.getCurrent().getActiveShell(), 
					Model.getModel().getAllPersons().getMember(name).getData()).open();
		} 
	}
	
	protected static  void initModelListeners() {
		if(memberListener == null) {
			addListener();
		}
	}
	
	private static void addListener() {
		memberListener = new MemberListAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see posemuckel.client.model.event.MemberListAdapter#personsDataLoaded(posemuckel.client.model.event.PersonsEvent)
			 */
			@Override
			public void personsDataLoaded(final PersonsEvent event) {
				//hier werden die geladenen Daten angezeigt
				Runnable run = new Runnable() {
					public void run() {
						ProfileDialog dialog = new ProfileDialog(Display
								.getCurrent().getActiveShell(), event
								.getSource().getData());
						dialog.open();
					}
				};				
				Display.getDefault().asyncExec(run);
			}
		};
		Model.getModel().getAllPersons().addListener(memberListener);		
	}
	
}
