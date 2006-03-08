/**
 * 
 */
package posemuckel.client.gui.browser;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.gui.Colors;
import posemuckel.client.gui.GUI_Main_Window;
import posemuckel.client.gui.ImageManagment;
import posemuckel.client.gui.MyLayoutFactory;
import posemuckel.client.gui.MyTabFolder;
import posemuckel.client.gui.NotificationDialog;
import posemuckel.client.gui.actions.OpenResultsAction;
import posemuckel.client.model.Chat;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.Project;
import posemuckel.client.model.event.NotifyEvent;
import posemuckel.client.model.event.NotifyListener;
import posemuckel.client.net.Client;
import posemuckel.common.GetText;
/**
 * @author Posemuckel Team
 * Bildet das Gerüst zur Darstellung des Browsers und des Webtracers
 *
 */

public class LabBrowserVariant2 extends ApplicationWindow {
	String url = "www.google.de";
	MyTabFolder tabFolder;
	Text textLocation;
	Label labelStatus;
	ToolBarManager manager;
	SashForm sash;
	SashForm webSash;
	
	Action actionMaxLeft;
	Action actionCenter;
	Action actionMaxRight;
	Action actionShowChat;
	Action actionHideChat;
	Action actionHelp;
	Action actionLeave;
	OpenResultsAction openResults;
	private Shell shell;
	private BrowserMenu browserMenu;
	/**
	 * Constructor
	 */
	public LabBrowserVariant2(Shell shell) {
		super(shell);
		tabFolder = new MyTabFolder(true);
		createActions();
		
		//addStatusLine();
		addToolBar(SWT.FLAT);
		//addMenuBar();
		addListener();
		
	}
	
	/**
	 * Erzeugung der GUI-Elemente
	 */
	protected Control createContents(Composite parent) {
//		Composite composite = new Composite(parent, SWT.NULL);	
		this.shell=parent.getShell();
		String title = Model.getModel().getUser().getNickname() + ": " + 
						Model.getModel().getOpenProject().getTopic();
		shell.setText(title);
		shell.setImage(ImageManagment.getRegistry().get(
				ImageManagment.SHELL_ICON));
		shell.setLayout(MyLayoutFactory.createGrid(1, false));
		Colors.initColors();
		
		/**
		Menu menu=new Menu(this.shell, SWT.BAR);
		
		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("&Datei");
		Menu fileMenu= new Menu(this.shell, SWT.DROP_DOWN);
		
		MenuItem exit = new MenuItem (fileMenu, SWT.PUSH);
		exit.setText("&Verlassen\tCtrl+V");
		exit.setAccelerator (SWT.CTRL + 'V');
		exit.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Client.goToMain();
				shell.close();
			}
		});
		
		fileMenuItem.setMenu(fileMenu);
		
		this.shell.setMenuBar(menu);
		**/
		
		//bei der Initialisierung der Actions war die Shell noch nicht existent
		//backToMain.setShell(shell);
		openResults.setShell(shell);
		/*
		 * SashForm erstellen und füllen
		 */
		sash = new SashForm(shell, SWT.NONE | SWT.HORIZONTAL);
		sash.setBackground(Colors.getSashBackground());
		sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite browseComp = new Composite(sash, SWT.BORDER);
		webSash = new SashForm(sash, SWT.BORDER | SWT.VERTICAL);
		Composite traceComp = new Composite(webSash, SWT.NONE);
		Composite chatComp = new Composite(webSash, SWT.NONE);

		//CreateBrowser createBrowser = new CreateBrowser(browseComp);
	
		//sash.setWeights(new int[]{1, 1});
		//webSash.setWeights(new int[]{2, 1});		
		
		CreateBrowser browser = new CreateBrowser(browseComp);
		browser.setMenu(browserMenu);
		traceComp.setLayout(new FillLayout());
		new CreateWebtracer(traceComp, browser);
		//TODO falls der browser eine Referenz auf den Webtrace braucht, kommt hier ein setter hin
		chatComp.setLayout(new FillLayout());		
		tabFolder.createContent(chatComp);
		tabFolder.addChat(Model.getModel().getOpenProject().getChatID());
		
		//TODO LogTab einfügen
		tabFolder.addLogTab();
		sash.setWeights(new int[]{4, 3});
		webSash.setWeights(new int[]{1, 1});
		parent.setSize(950,700);
		
		return parent;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.ApplicationWindow#close()
	 */
	@Override
	public boolean close() {
		Client.enableOpenProject(true);
		return super.close();
	}

	/**
	 * ToolBarManager zur Darstellung der Toolbar im Fenster
	 */
	protected ToolBarManager createToolBarManager(int style) {
		
		
		
		
		manager = super.createToolBarManager(style);
		browserMenu = new BrowserMenu();
		IAction[] actions = browserMenu.createMenuItems();
		for (int i = 0; i < actions.length; i++) {
			addAction(manager, actions[i], true);
		}
		manager.add(new Separator());
		addAction(manager, actionMaxLeft, true);
		addAction(manager, actionCenter, true);
		addAction(manager, actionMaxRight, true);
		manager.add(new Separator());
		addAction(manager, actionShowChat, true);
		addAction(manager, actionHideChat, true);
		manager.add(new Separator());
		//addAction(manager, backToMain, true);
		addAction(manager, openResults, true);
		manager.add(new Separator());
		addAction(manager, actionHelp, true);
		manager.add(new Separator());
		addAction(manager, actionLeave, true);
		manager.update(true);
		
		return manager;
	}
	
	/**
	 * Fuegt dem ToolBarManager eine Aktion hinzu
	 * @param manager
	 * @param action
	 * @param displayText
	 */
	public static void addAction(ToolBarManager manager, IAction action, boolean displayText) {
		if (!displayText) {
			manager.add(action);
			return;
		}
		else {
			ActionContributionItem item = new ActionContributionItem(action);
			item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
			manager.add(item);
		}
	}
	
	/**
	 * Aktionen in der ToolBar (Buttons und hinterlegte Befehle)
	 *
	 */
	private void createActions() {
		actionMaxLeft =
			new Action(
				"&"+GetText.gettext("BROWSER"),
				ImageDescriptor.createFromFile(
					this.getClass(),
					"maxleft.gif")) {
			public void run() {
				sash.setWeights(new int[]{1, 0});
			}
		};

		actionCenter =
			new Action(
				"&"+GetText.gettext("SHARED"),
				ImageDescriptor.createFromFile(
					this.getClass(),
					"center.gif")) {
			public void run() {
				sash.setWeights(new int[]{1, 1});
			}
		};

		actionMaxRight =
			new Action(
				"&"+GetText.gettext("WEBTRACER"),
				ImageDescriptor.createFromFile(this.getClass(), "maxright.gif")) {
			public void run() {
				sash.setWeights(new int[]{0, 1});
			}
		};

		actionShowChat =
			new Action(
				"&"+GetText.gettext("SHOW_CHAT"),
				ImageDescriptor.createFromFile(this.getClass(), "tabfolder_obj.gif")) {
			public void run() {
				webSash.setWeights(new int[]{2, 1});
			}
		};
		
		actionHideChat =
			new Action(
				"&"+GetText.gettext("HIDE_CHAT"),
				ImageDescriptor.createFromFile(this.getClass(), "ctabfolder_obj.gif")) {
			public void run() {
				webSash.setWeights(new int[]{1, 0});
			}
		};
		//backToMain = new BackToMainAction(getParentShell());
		ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"jtree16.gif");
		openResults = new OpenResultsAction(getParentShell(), id);
		
		actionHelp =
			new Action(
				"&"+GetText.gettext("HELP"),
				ImageDescriptor.createFromFile(this.getClass(), "smartmode_co.gif")) {
			public void run() {
				new HelpBrowser(System.getProperty("user.dir")+"/doc/userdoc/projectView.htm", "Browser Hilfe");
			}
		};
		
		
		actionLeave =
			new Action(
				"&"+GetText.gettext("LEAVE"),
				ImageDescriptor.createFromFile(this.getClass(), "error_obj.gif")) {
			public void run() {
				//das Hauptfenster ist noch offen, also muss es nicht noch mal geöffnet 
				//werden
				//Client.goToMain();
				Model.getModel().getUser().setState(Person.ONLINE);
				Chat chat = Model.getModel().getLogchat();
				chat.userIsChatting(Model.getModel().getUser().getNickname()+" hat den Browser geschlossen");
				shell.close();
			}
		};
	}

	private void addListener() {
		
		/**
		 * Dieser Listener hört auf DecodeImage
		 */
		final Project proj = Model.getModel().getOpenProject();
		if (proj == null)
			return;
		
		NotifyListener adapter = new NotifyListener() {

			public void notify(final NotifyEvent event) {
				Runnable run = new Runnable() {
					public void run() {
						if(shell.isDisposed())return;
						NotificationDialog notify = new NotificationDialog(shell,event.getImageData(), event.getUser(), event.getURL(), event.getComment());
						notify.open();
					}
				};				
				if(shell.isDisposed()) {
					proj.removeListener(this);
				} else {
					Display.getDefault().asyncExec(run);
				}
				
			}

			public void ack() {}

			public void newurl(String url) {}
			
		};
		proj.addListener(adapter);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) { 
		LabBrowserVariant2 window = new LabBrowserVariant2(null);
		window.setBlockOnOpen(true);
		window.open();
	}

}
