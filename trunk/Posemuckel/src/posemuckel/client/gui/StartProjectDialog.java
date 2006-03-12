package posemuckel.client.gui;



import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.Project;
import posemuckel.client.model.User;
import posemuckel.client.model.event.ProjectEvent;
import posemuckel.client.model.event.ProjectListener;
import posemuckel.client.model.event.ProjectListenerAdapter;
import posemuckel.common.GetText;

/**
 * Dieser Dialog dient dem Anlegen eines neuen Projektes.
 * Es werden alle relevanten Informationen vom Benutzer abgefragt.
 * @author Posemuckel Team
 *
 */
public class StartProjectDialog extends Dialog {
	
	// TODO Die Aktion der Buttons zwischen den Userlisten implementieren.
	
	private List BuddyList;
	private Button button_invite;
	private Button button_deinvite;
	private List InvitedList;
	private GridData data;
	private Label label_buddies;
	private Label label_invited;
	private Label label_topic;
	private Text text_topic;
	private Button checkbox_public;
	private Label label_maxusers;
	private Combo combo_maxusers;
	private Label label_description;
	private Text text_description;

	private String title;
	private String topic;
	private String isprivate;
	private String description;
	private String buddies;
	private String inviters;
	private String maxusers;
	private String close;
	private String create_project;
	private User user;
	private String myname;
	private String input_error;
	private String give_me_a_topic;
	private String give_me_maxusers;
	private String give_me_a_description;
	private String give_me_a_number;
	private ProjectListener listener;
	private Label result;
	private String access_denied;
	private String success;
	private String fail;
	
	private boolean active = true;
	
	private static final int START_PROJECT_BUTTON = IDialogConstants.NO_TO_ALL_ID +1;
	
	/**
	 * @param arg0
	 */
	public StartProjectDialog(Shell arg0) {
		super(arg0);
		getDescriptions();
		user = Model.getModel().getUser();
		myname = user.getNickname();
	}

	
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(title);
		getShell().setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout gridlayout = new GridLayout();
		gridlayout.numColumns = 6;
		comp.setLayout(gridlayout);
		

		label_topic = new Label(comp, SWT.SHADOW_NONE);
		label_topic.setText(topic+":");
		
		text_topic = new Text(comp, SWT.SINGLE | SWT.BORDER);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan = 2;
		data.minimumWidth = 200;
		text_topic.setLayoutData(data);
		text_topic.setTextLimit(50);
		
		label_buddies = new Label(comp, SWT.SHADOW_NONE);
		label_buddies.setText(buddies+":");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label_buddies.setLayoutData(data);
		
		@SuppressWarnings("unused") Label do_not_delete = new Label(comp, SWT.SHADOW_NONE);
		
		label_invited = new Label(comp, SWT.SHADOW_NONE);
		label_invited.setText(inviters+":");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label_invited.setLayoutData(data);

		checkbox_public = new Button(comp, SWT.CHECK);
		checkbox_public.setText(isprivate);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		checkbox_public.setLayoutData(data);
		checkbox_public.addSelectionListener(
				// da bei privaten Projekten die MaxUser ignoriert werden,
				// deaktivieren wir bei privaten Projekten die Combobox
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event){
						if(checkbox_public.getSelection() == true)
							combo_maxusers.setEnabled(false);
						else combo_maxusers.setEnabled(true);
					}
				}
		);
		
		BuddyList = new List(comp, SWT.BORDER |SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		data.verticalSpan = 4;
		BuddyList.setLayoutData(data);
		ArrayList<Person> Buddies = Model.getModel().getUser().getBuddyList().getMembers();
		
		for( Person p : Buddies ) {
			String buddyname = p.getNickname();
			if(!myname.equals(buddyname))
				BuddyList.add(buddyname);
		}
		
		button_invite = new Button(comp, SWT.ARROW | SWT.RIGHT);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		data.horizontalSpan = 1;
		button_invite.setLayoutData(data);
				
		InvitedList = new List(comp, SWT.BORDER |SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		data.verticalSpan = 4;
		InvitedList.setLayoutData(data);

		label_maxusers = new Label(comp, SWT.SHADOW_NONE);
		label_maxusers.setText(maxusers+":");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		label_maxusers.setLayoutData(data);
		
		combo_maxusers = new Combo(comp,SWT.DROP_DOWN);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.minimumWidth = 50;
		combo_maxusers.setLayoutData(data);
		combo_maxusers.add("5");
		combo_maxusers.add("10");
		combo_maxusers.add("20");
		combo_maxusers.setText("10");
		combo_maxusers.setTextLimit(3);
		combo_maxusers.addModifyListener(
				// Dieser Listener soll "falsche" Einagben verhindern
				new ModifyListener()
				{
					public void modifyText(ModifyEvent event) {
						Combo combo = (Combo)event.getSource();
						String txt = combo.getText();
						if (!txt.equals("") ) {
							try {
								int num_users = Integer.valueOf(txt);
								if ( num_users == 0 ) {
									combo.setText("");
								}
							} catch (NumberFormatException e) {
								MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK );
								mb.setText(input_error);
								mb.setMessage(give_me_a_number);
								mb.open();
								combo.setText("");
							}
						}
					}
				}
		);
		
		button_deinvite = new Button(comp, SWT.ARROW | SWT.LEFT);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		data.horizontalSpan = 1;
		button_deinvite.setLayoutData(data);

		// Hier ist das Verhalten des Buttons zum Enteinladen
		// implementiert.
		button_deinvite.addListener( SWT.Selection,
				 new Listener() {
					public void handleEvent(Event arg0) {
						String[] selected = InvitedList.getSelection();
						if ( selected.length != 0 ) {
							for ( String buddy : selected ) {
								InvitedList.remove(buddy);
								BuddyList.add(buddy);
							}
						}
					}
				 }
		);
		
		// Hier ist das Verhalten des Buttons zum Einladen implementiert.
		button_invite.addListener( SWT.Selection,
				 new Listener() {
					public void handleEvent(Event arg0) {
						String[] selected = BuddyList.getSelection();
						if ( selected.length != 0 ) {
							for ( String buddy : selected ) {
								BuddyList.remove(buddy);
								InvitedList.add(buddy);
							}
						}
					}
				 }
		);
		
		label_description = new Label(comp, SWT.SHADOW_NONE);
		label_description.setText(description+":");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		label_description.setLayoutData(data);
		
		text_description = new Text(comp, SWT.MULTI |SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData( GridData.FILL_BOTH );
		data.horizontalSpan = 3;
		data.heightHint = 200;
		data.widthHint = 200;
		text_description.setLayoutData(data);
		text_description.setEditable(true);
		text_description.setTextLimit(255); // MySQL: TinyText
		
		
		result = new Label(comp, SWT.SHADOW_NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 6;
		result.setLayoutData(gridData);
		
		return comp;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent,START_PROJECT_BUTTON,create_project,true);
		createButton(parent,IDialogConstants.CLOSE_ID,close,true);
	}
	
	private void getDescriptions() {
		title  = GetText.gettext("NEW_PROJECT");
		topic  = GetText.gettext("START_PROJECT_TOPIC");
		isprivate  = GetText.gettext("PRIVATE");
		description = GetText.gettext("PROJECT_DESCRIPTION");
		buddies = GetText.gettext("BUDDIES_NOT_TO_INVITE");
		inviters = GetText.gettext("INVITE_BUDDIES");
		maxusers = GetText.gettext("PROJECT_MAXUSERS");
		close = GetText.gettext("CLOSE");
		create_project = GetText.gettext("NEW_PROJECT_CREATE");
		input_error = GetText.gettext("ERROR_ON_INPUT_TITLE");
		give_me_a_topic = GetText.gettext("GIVE_ME_TOPIC");
		give_me_maxusers = GetText.gettext("GIVE_ME_MAXUSERS");
		give_me_a_description = GetText.gettext("GIVE_ME_DESCRIPTION");
		give_me_a_number = GetText.gettext("GIVE_ME_NUMBER");
		access_denied = GetText.gettext("ACCESS_DENIED");
		success = GetText.gettext("START_PROJECT_SUCCESS");
		fail = GetText.gettext("START_PROJECT_FAIL");
	}
	
	protected void buttonPressed(int buttonID) {
		switch (buttonID) {
		case IDialogConstants.CLOSE_ID: {
			close();
			break;
		}
		case START_PROJECT_BUTTON: {
			if(!active)
				return;
			else
				active = false;
			
			addListener();
			String project_topic = text_topic.getText();
			String max_users = combo_maxusers.getText();
			String description = text_description.getText();
			// TODO Lieber Zeilenumbrüche zulassen (Änderung des RFC)			
			description = GetText.replaceRN(description);
			boolean isprivate = checkbox_public.getSelection();
			int max = Integer.valueOf(combo_maxusers.getText());
			System.out.println("1. max = " + max);
			int free = max;
			String ispublic;
			if(isprivate) {
				ispublic = Project.PRIVATE_TYPE;
				//TODO
				//die Anzahl ist nicht variabel, oder?
				max = InvitedList.getItemCount() + 1;
				System.out.println("2. max = " + max);
				free = 0;
			} else {
				ispublic = Project.PUBLIC_TYPE;
				//die Anzahl der Anwender nach oben verschieben
				max = (max >= (InvitedList.getItemCount() + 1) ? max : (InvitedList.getItemCount() + 1));
				System.out.println("2. max = " + max);
				free = max - (InvitedList.getItemCount() + 1);
			}
			if ( project_topic.equals("") ) {
				MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK );
	            mb.setText(input_error);
	            mb.setMessage(give_me_a_topic);
	            mb.open();
	            active = true;
				return;
			}
			if ( max_users.equals("") ) {
				MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK );
	            mb.setText(input_error);
	            mb.setMessage(give_me_maxusers);
	            mb.open();
	            active = true;
				return;
			}
			if ( description.equals("") ) {
				MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK );
	            mb.setText(input_error);
	            mb.setMessage(give_me_a_description);
	            mb.open();
	            active = true;
				return;
			}
			
			String free_seats = new String(Integer.toString(free));
			System.out.println("Free = "+ free_seats);
			Project newProject = new Project(Model.getModel());
			newProject.setData(project_topic,myname,ispublic,free_seats,
					max_users,description);
			newProject.setMaxNumber(max);
			newProject.addMembers(InvitedList.getItems());
			// TODO Prüfen, ob die Liste der eingeladenen Benutzer leer ist.
			Model.getModel().getAllProjects().startProject(newProject);
			break;
		}
		}
	}

	private void addListener() {
		listener = new ProjectListenerAdapter() {

			public void accessDenied() {
				Runnable run = new Runnable() {
					public void run() {
						result.setText(access_denied);
					}					
				};
				/*
				 * das Runnable darf nur ausgeführt werden, wenn der Dialog
				 * noch existiert: ein Dialog, der bereits geschlossen ist, 
				 * führt zu unerklärlichen Fehlern
				 */
				active = true;
				if(isDisposed()) {
					//TODO wieso kann der User einen ProjectListener erhalten???
					//user.removeListener(listener);
				} else {
					Display.getDefault().asyncExec(run);
				}				
			}

			public void confirmStartProject(final ProjectEvent event) {
				Runnable run = new Runnable() {
					public void run() {
						if (event.isSucceeded()) {
							// funktioniert nicht: event.getProjectSource().join();
							result.setText(success);
						}
						else {
							active = true;
							result.setText(fail);
						}
					}
					
				};
				/*
				 * das Runnable darf nur ausgeführt werden, wenn der Dialog
				 * noch existiert: ein Dialog, der bereits geschlossen ist, 
				 * führt zu unerklärlichen Fehlern
				 */
				if(isDisposed()) {
					//TODO user hat keine ProjectListener
					//user.removeListener(listener);
				} else {
					Display.getDefault().asyncExec(run);
				}
			}

			public void openProject(ProjectEvent event) {
			}
		};
		Model.getModel().getAllProjects().addListener(listener);
	}

	/**
	 * Stellt fest, ob der Dialog schon geschlossen wurde.
	 * @return true, falls der Dialog schon geschlossen wurde
	 */
	private boolean isDisposed() {
		return (getShell() == null) || this.getShell().isDisposed();
	}
	
}
