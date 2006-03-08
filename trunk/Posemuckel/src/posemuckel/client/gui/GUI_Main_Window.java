	package posemuckel.client.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import posemuckel.client.gui.actions.AboutPosemuckelAction;
import posemuckel.client.gui.actions.EditMyProfileAction;
import posemuckel.client.gui.actions.HelpContentsAction;
import posemuckel.client.gui.actions.OpenProjectAction;
import posemuckel.client.model.Model;
import posemuckel.client.model.event.UserEvent;
import posemuckel.client.model.event.UserListenerAdapter;
import posemuckel.client.net.Client;
import posemuckel.common.GetText;


public class GUI_Main_Window extends ApplicationWindow
{
	private MyTabFolder chats;
	private MyTabFolder projects;
	private SashForm all;	
	
	
	OpenProjectAction openProjectAction = new OpenProjectAction();
	LogoffAction logoffAction = new LogoffAction();
	LoginAction loginAction = new LoginAction();
	ExitAction exitAction = new ExitAction(this);
	FindUserAction findUserAction = new FindUserAction();
	AddBuddyAction addBuddyAction = new AddBuddyAction();
	EditMyProfileAction editMyProfileAction = new EditMyProfileAction();
	NewProjectAction newProjectAction = new NewProjectAction();
	FindProjectAction findProjectAction = new FindProjectAction();
	HelpContentsAction helpContentsAction = new HelpContentsAction();
	AboutPosemuckelAction aboutPosemuckelAction = new AboutPosemuckelAction();
	//GoToLastProjectAction goBackAction;
	StatusLineManager slm = new StatusLineManager();
	ActionContributionItem aci = new ActionContributionItem(newProjectAction);
	
	public GUI_Main_Window()
	{
		super(null);
		Client.setMainWindow(this);
		chats = new MyTabFolder(true);
		addStatusLine();
		addMenuBar();
		//addCoolBar(SWT.WRAP);
		//TODO entfernen
		//initModelListeners();
		this.setBlockOnOpen(true);
		this.open();
	}
	
	public void setEnabledOpenProject(boolean enabled) {
		openProjectAction.setEnabled(enabled);
		projects.setEnabledOpenProject(enabled);
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.ApplicationWindow#close()
	 */
	@Override
	public boolean close() {
		Client.closeBrowser();
		return super.close();
	}

	protected Control createContents(Composite parent)
	{	Colors.initColors();
//		goBackAction.setShell(this.getShell());
		all = new SashForm(parent, SWT.VERTICAL);
		SashForm sash_main = new SashForm(all, SWT.HORIZONTAL | SWT.NULL);
		Color color_sash = Colors.getSashBackground();
		all.setBackground(color_sash);
		sash_main.setBackground(color_sash);
		
		chats.createContent(all);
		chats.addChat("0");
		// TODO: Verschicke die Anforderung zur Anfrage der Beteiligten Bennutzer an Chat 0
		
		new GUI_MemberList_Composite(sash_main, Model.getModel().getUser().getBuddyList());

		SashForm sash_inner = new SashForm(sash_main, SWT.VERTICAL | SWT.NULL);
		sash_inner.setBackground(color_sash);
		
		projects = new MyTabFolder(false);
		projects.createContent(sash_inner);
		projects.addProjectTab(Model.getModel().getUser().getProjects(), openProjectAction);
		projects.addProjectTab(Model.getModel().getUser().getInvitations());
		projects.addProjectTab(Model.getModel().getAllProjects());
		
		
//		GUI_Main_PublicProjects_Composite publicProjectList =
//			new GUI_Main_PublicProjects_Composite(sash_inner, Model.getModel().getAllProjects());
		
		int[] weights = new int[] {1,2};
		sash_main.setWeights(weights);
		
		//setStatus(GetText.gettext("STATUSLINE"));

		//Listener für die Anzeige von Einladungen
		new InvitationInfo();
		setEnabledOpenProject(true);
		
		/*
		 * Ein Versuch, das Loginfenster beim Öffnen des Hauptfensters
		 * automatisch anzuzeigen. Wenn das Probleme macht, einfach
		 * entfernen
		 */
		// If user not logged in, show LoginDialog
		if (!Model.getModel().getUser().isLoggedIn()) {
			// LoginDialog andrehen
			new LoginDialog(Display.getDefault().getActiveShell());
		}
		ImageRegistry images = ImageManagment.getRegistry();
		this.getShell().setImage(images.get(ImageManagment.SHELL_ICON));
		updateMainWindow();
		//getShell().setText(GetText.gettext("MAIN_WINDOW_TITLE"));

		return sash_main;
	}
	
	public boolean isDisposed() {
		return (all == null) || all.isDisposed();
	}
	
	protected void updateMainWindow() {
		if (Model.getModel().getUser().isLoggedIn()) {
			getShell().setText(GetText.gettext("MAIN_WINDOW_TITLE_USER") + " " + Model.getModel().getUser().getNickname());	
		} else {
			getShell().setText(GetText.gettext("MAIN_WINDOW_TITLE"));
		}
	}
		
	protected MenuManager createMenuManager()
	{
		MenuManager main_menu = new MenuManager(null);
		
		MenuManager fileMenu = new MenuManager(GetText.gettext("FILE"));
		main_menu.add(fileMenu);
		loginAction.setEnabled(!Model.getModel().getUser().isLoggedIn());
		logoffAction.setEnabled(Model.getModel().getUser().isLoggedIn());
		exitAction.setEnabled(!Model.getModel().getUser().isLoggedIn());
		fileMenu.add(loginAction);
		fileMenu.add(logoffAction);
		fileMenu.add(exitAction);
		
		MenuManager usersMenu = new MenuManager(GetText.gettext("USERS"));
		main_menu.add(usersMenu);
		usersMenu.add(findUserAction);
		usersMenu.add(editMyProfileAction);

		MenuManager projectMenu = new MenuManager(GetText.gettext(
				"PROJECT"));
		main_menu.add(projectMenu);
		projectMenu.add(newProjectAction);
//		projectMenu.add(findProjectAction);
		
		MenuManager helpMenu = new MenuManager(GetText.gettext("HELP"));
		main_menu.add(helpMenu);
		helpMenu.add(helpContentsAction);
		helpMenu.add(aboutPosemuckelAction);
		
		//Listener initialisieren
		Model.getModel().getUser().addListener(new LoginListener());
		return main_menu;
	}
	
	protected CoolBarManager createCoolBarManager(int style)
	{
		CoolBarManager coolBarManager = new CoolBarManager(style);
		
		ToolBarManager projectToolBarManager = new ToolBarManager(style);
		projectToolBarManager.add(newProjectAction);
		projectToolBarManager.add(openProjectAction);
		
		ToolBarManager userToolBarManager = new ToolBarManager(style);
		userToolBarManager.add(findUserAction);
		userToolBarManager.add(addBuddyAction);
		
//		ToolBarManager backToolBarManager = new ToolBarManager(style);
//		goBackAction = new GoToLastProjectAction(this.getShell());
//		goBackAction.setEnabled(Model.getModel().getOpenProject() != null);
//		backToolBarManager.add(goBackAction);
		
		coolBarManager.add(projectToolBarManager);
		coolBarManager.add(userToolBarManager);
//		coolBarManager.add(backToolBarManager);
		
		return coolBarManager;
	}

	protected StatusLineManager createStatusLineManager()
	{
		return slm;
	}
	
	public static void main(String[] args)
	{
		new GUI_Main_Window();
	}
	
	private class LoginListener extends UserListenerAdapter {

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.UserListenerAdapter#login(posemuckel.client.model.event.UserEvent)
		 */
		@Override
		public void login(UserEvent event) {
			Runnable run = new Runnable() {

				public void run() {
					if(!isDisposed()) {
						loginAction.setEnabled(false);
						logoffAction.setEnabled(true);
						exitAction.setEnabled(true);
					}
				}
				
			};
			Display.getDefault().asyncExec(run);
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.UserListenerAdapter#logout(posemuckel.client.model.event.UserEvent)
		 */
		@Override
		public void logout(UserEvent event) {
			Runnable run = new Runnable() {

				public void run() {
					if(!isDisposed()) {
						loginAction.setEnabled(true);
						logoffAction.setEnabled(false);
						exitAction.setEnabled(true);
					}
				}
				
			};
			Display.getDefault().asyncExec(run);
		}
		
	}
	
}

class LogoffAction extends Action
{
	
	public LogoffAction()
	{
		super("&"+GetText.gettext("LOG_OFF")+"@Ctrl+L", AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("LOG_OFF"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"icons/run.gif"));
	}
	public void run()
	{
		Client.closeBrowser();
		Model.getModel().getUser().logout();
	}
}

class NewProjectAction extends Action
{
	
	public NewProjectAction()
	{
		super(GetText.gettext("NEW_PROJECT"), AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("NEW_PROJECT"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"icons/generic.gif"));
	}
	public void run()
	{
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
		StartProjectDialog newProject = new StartProjectDialog(Display.getCurrent().getActiveShell());
		newProject.open();
	}
}

class FindProjectAction extends Action
{
	
	public FindProjectAction()
	{
		super("&"+GetText.gettext("FIND_PROJECT"), AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("FIND_PROJECT"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"icons/index.gif"));
	}
	public void run()
	{
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
		// TODO: Implement Find Project
		System.out.println("Find Project Action");
	}
}

class AddBuddyAction extends Action
{
	
	public AddBuddyAction()
	{
		super("&"+GetText.gettext("ADD_BUDDY")+"@Ctrl+A", AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("ADD_BUDDY"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"icons/add.gif"));
	}
	public void run()
	{
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
		System.out.println("Action: Add Buddy");
		// TODO: implement add  buddy
	}
}

class FindUserAction extends Action
{
	
	public FindUserAction()
	{
		super("&"+GetText.gettext("FIND_USER")+"@Ctrl+U", AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("FIND_USER"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"icons/index.gif"));
	}
	public void run()
	{
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
		SearchUserDialog findUser = new SearchUserDialog(Display.getCurrent().getActiveShell());
		findUser.open();
	}
}

class DeleteBuddyAction extends Action
{
	private Table table;
	
	public DeleteBuddyAction(Table table) {
		super("&"+GetText.gettext("DELETE_BUDDY"), AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("DELETE_BUDDY"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"icons/close.gif"));
		this.table = table;
	}
	
	
	public void run()
	{
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
		TableItem[] items = (TableItem[])table.getSelection();
		if(items.length > 0) {
			TableItem item = items[0];
			if(item != null && item.getData() != null) {
				Model.getModel().getUser().getBuddyList().deleteMember(item.getText(1));
			}
		}
	}
}

class LoginAction extends Action
{
	public LoginAction() {
		super("Login");
		setToolTipText("MELD! DICH! AN!");
	}
	
	
	public void run()
	{
		if(!Model.getModel().getUser().isLoggedIn())
			new LoginDialog(Display.getDefault().getActiveShell());		
	}
}
