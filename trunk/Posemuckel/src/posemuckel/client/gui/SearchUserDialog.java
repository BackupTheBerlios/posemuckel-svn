package posemuckel.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.PersonsData;
import posemuckel.client.model.User;
import posemuckel.client.model.UsersPool;
import posemuckel.client.model.event.MemberListAdapter;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.GetText;

/**
 * Dies ist der Dialog zur Suche nach Benutzern.
 * Kriterien sind. Achtung: Dieser Dialog befindet sich
 * in der Entwicklung.
 * @author Posemuckel Team
 *
 */
public class SearchUserDialog extends Dialog {

	private String username;
	private String firstname;
	private String surname;
	private String language;
	private String close;
	private String buddyadd;
	private Label label_username;
	private Text text_username;
	private Label label_found;
	private Label label_name;
	private Text text_name;
	private Label label_free;
	private Text text_free;
	private Label label_surname;
	private Text text_surname;
	private Table table;
	private TableViewer viewer;
	private Label label_feedback;
	private HashMap<String,String> langs = new HashMap<String,String>();
	
	private MemberListAdapter listener;
	private String male;
	private String female;
	private String gender;
	private Label label_gender;
	private Combo combo_gender;
	private String all;
	private String title;
	private String users_found;
	private String freetext;
	private String show_profile;
	private String search_user;
	private String nothing_selected;
	private String no_self_buddy;
	private String no_users_found;
	private String isbuddy;
	
	private Label label_language;
	private Combo combo_language;
	
	private static final int SHOW_PROFILE_BUTTON = IDialogConstants.NO_TO_ALL_ID +1;
	private static final int ADD_BUDDY_BUTTON = IDialogConstants.NO_TO_ALL_ID +2;
	private static final int SEARCH_BUTTON = IDialogConstants.NO_TO_ALL_ID +3;
	
	User myself = Model.getModel().getUser();

	public SearchUserDialog(Shell shell) {
		super(shell);
		getDescriptions();
		listener = new MyMemberListAdapter();
		Model.getModel().getAllPersons().addListener(listener);
	}
	
	private void getDescriptions() {
		username = GetText.gettext("USERNAME");
		firstname = GetText.gettext("FIRSTNAME");
		surname = GetText.gettext("SURNAME");
		language = GetText.gettext("LANG");
		close = GetText.gettext("CLOSE");
		buddyadd = GetText.gettext("ADD_BUDDY");
		male = GetText.gettext("MALE");
		female = GetText.gettext("FEMALE");
		gender = GetText.gettext("GENDER");
		isbuddy = GetText.gettext("ALREADY_A_BUDDY");
		title = GetText.gettext("FIND_USER");
		all = GetText.gettext("ANY");
		users_found = GetText.gettext("USERS_FOUND");
		freetext = GetText.gettext("FREETEXT_SEARCH");
		show_profile = GetText.gettext("SHOW_PROFILE");
		search_user = GetText.gettext("START_SEARCH");
		nothing_selected = GetText.gettext("NOTHING_SELECTED");
		no_self_buddy = GetText.gettext("NO_SELF_BUDDY");
		no_users_found = GetText.gettext("NO_USERS_FOUND");
		// Für jede unterstützte Sprache eine vernünftige
		// Benennung holen: Also Deutsch statt DE bzw. german statt DE.
 		for (int i = 0 ; i < EnumsAndConstants.LANG.length; i++ ) {
			langs.put(GetText.gettext(EnumsAndConstants.LANG[i]), EnumsAndConstants.LANG[i]);
		}
	}
	
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(title);
		getShell().setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout gridlayout = new GridLayout(4,false);
		gridlayout.marginHeight = 20;
		gridlayout.marginWidth = 20;
		comp.setLayout(gridlayout);
		
		// Username field
		label_username = new Label(comp, SWT.SHADOW_NONE);
		label_username.setText(username+":");
		
		text_username = new Text(comp, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData( GridData.FILL_HORIZONTAL );
		data.minimumWidth = 200;
		text_username.setLayoutData(data);
		text_username.setTextLimit(20);
		
		// Vorname
		label_name = new Label(comp, SWT.SHADOW_NONE);
		label_name.setText(firstname+":");
		
		text_name = new Text(comp, SWT.SINGLE | SWT.BORDER);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.minimumWidth = 200;
		text_name.setLayoutData(data);
		text_name.setTextLimit(20);

		// Freitext
		label_free = new Label(comp, SWT.SHADOW_NONE);
		label_free.setText(freetext+":");
		
		text_free = new Text(comp, SWT.SINGLE | SWT.BORDER);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.minimumWidth = 200;
		text_free.setLayoutData(data);


		// Nachname
		label_surname = new Label(comp, SWT.SHADOW_NONE);
		label_surname.setText(surname+":");
		
		text_surname = new Text(comp, SWT.SINGLE | SWT.BORDER);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.minimumWidth = 200;
		text_surname.setLayoutData(data);
		text_surname.setTextLimit(20);

		// Gender combo box
		label_gender = new Label(comp, SWT.SHADOW_NONE);
		label_gender.setText(gender+":");
		
		combo_gender = new Combo(comp, SWT.READ_ONLY);
		data = new GridData( GridData.FILL_HORIZONTAL );
		combo_gender.setLayoutData(data);
		combo_gender.setItems(new String[] {male, female, all});
		combo_gender.setText(all);
		
		
		// Language field
		label_language = new Label(comp, SWT.SHADOW_NONE);
		label_language.setText(language + ":");

		combo_language = new Combo(comp, SWT.READ_ONLY);
		Set keys = langs.keySet();
		Object[] arr = keys.toArray();
		for ( Object lang : arr ) {
			combo_language.add((String)lang);
		}
		combo_language.add(all);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.minimumWidth = 200;
		combo_language.setLayoutData(data);
		combo_language.setText(all);
			
		// Textfeld zur Liste der gefundenen Benutzer
		label_found = new Label(comp, SWT.SHADOW_NONE);
		label_found.setText("Gefundene Benutzer:");
		data = new GridData( GridData.BEGINNING );
		data.horizontalSpan = 4;
		label_found.setLayoutData(data);
				
		// Die Tabelle der gefundenen Benutzer:
		table = new Table(comp, SWT.FULL_SELECTION | SWT.BORDER);
		viewer = buildAndLayoutTable(table);
		
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.heightHint = 100;
		data.horizontalSpan = 4;
		table.setLayoutData(data);
		
		//Content und LabelProvider einfügen
		attachContentProvider();
		attachLabelProvider();

		// Feedback-Label
		label_feedback = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.LEFT, true, false);
		data.horizontalSpan = 4;
		label_feedback.setLayoutData(data);
		
		return comp;
	}
	
	
	protected void createButtonsForButtonBar(Composite parent) {		
		createButton(parent,SHOW_PROFILE_BUTTON,show_profile,false);
		createButton(parent,ADD_BUDDY_BUTTON,buddyadd,false);
		createButton(parent,SEARCH_BUTTON,search_user ,true);
		createButton(parent,IDialogConstants.CLOSE_ID,close,false);
	}
	
	private TableViewer buildAndLayoutTable(final Table table)
	{
		TableViewer tableViewer = new TableViewer(table);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1, 75, true));
		layout.addColumnData(new ColumnWeightData(1, 75, true));
		layout.addColumnData(new ColumnWeightData(1, 75, true));
		layout.addColumnData(new ColumnWeightData(1, 75, true));
		table.setLayout(layout);
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText(username);
		col1.setResizable(false);
		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText(firstname);
		col2.setResizable(false);
		TableColumn col3 = new TableColumn(table, SWT.LEFT);
		col3.setText(surname);
		col3.setResizable(false);
		TableColumn col4 = new TableColumn(table, SWT.LEFT);
		col4.setText(language);		
		col4.setResizable(false);
		table.setHeaderVisible(true);
		return tableViewer;
	}

	protected void buttonPressed(int buttonID) {
		switch (buttonID) {
		case IDialogConstants.CLOSE_ID: {
			close();
			break;
		}
		case SHOW_PROFILE_BUTTON: {
			if (table != null && table.getSelectionCount() == 0) {
				label_feedback.setText(nothing_selected );
				return;
			}
			if (table != null && table.getSelectionCount() > 0) {
				String name = (table.getSelection())[0].getText(0);
				new ProfileDialog(Display.getCurrent().getActiveShell(), 
					Model.getModel().getAllPersons().getMember(name).getData()).open();
			}
			break;
		}
		case ADD_BUDDY_BUTTON: {
			if (table != null && table.getSelectionCount() == 0) {
				label_feedback.setText(nothing_selected);
				return;
			}
			if (table != null && table.getSelectionCount() > 0) {
				String name = (table.getSelection())[0].getText(0);
				if(myself.getNickname().equals(name)) {
					label_feedback.setText(no_self_buddy);
					return;
				}
				if(myself.getBuddyList().hasMember(name)) {
					label_feedback.setText(name+" "+isbuddy);
					return;
				}
					myself.getBuddyList().addBuddy(name);
			}
			break;
		}
		case SEARCH_BUTTON: {
			PersonsData data = new PersonsData();
			data.setNickname(text_username.getText());
			data.setFirstName(text_name.getText());
			data.setSurname(text_surname.getText());
			String genderset = combo_gender.getText();
			String langset = combo_language.getText();
			if ( genderset.equals(male) )
				genderset = EnumsAndConstants.GENDER[0];
			else if ( genderset.equals(female) )
				genderset = EnumsAndConstants.GENDER[1];
			else
				genderset = "";

			if (langset.equals(all))
				langset = "";
			else
				langset = (String)langs.get(langset);
			
			data.setLang(langset);
			data.setGender(genderset);
			
			Model.getModel().searchUsers(data, text_free.getText());
			break;
		}
		}
	}
	
	private void attachContentProvider() {
		viewer.setContentProvider(new IStructuredContentProvider() {
			private ArrayList<Person> input;
			
			public void dispose() {
				Model.getModel().getAllPersons().removeListener(listener);
			}

			public Object[] getElements(Object inputElement) {
				return input.toArray();
			}

			@SuppressWarnings("unchecked")
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				//hoffentlich funktioniert das
				input = (ArrayList<Person>) newInput;
			}			
		});
	}
	
	private void attachLabelProvider() {
		viewer.setLabelProvider(new ITableLabelProvider() {

			public void addListener(ILabelProviderListener listener) {}

			public void dispose() {
				Model.getModel().getAllPersons().removeListener(listener);
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				Person person = (Person)element;
				switch (columnIndex) {
				case 0://Benutzername
					return person.getNickname();
				case 1://Vorname
					return person.getData().getFirstName();
				case 2://Nachname
					return person.getData().getSurname();
				case 3://Sprache
					//TODO in unsere Sprachangaben übersetzen
					return person.getData().getLang();
				default:
					break;
				}
				return null;
			}
			
		});
	}

	private class MyMemberListAdapter extends MemberListAdapter {

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.MemberListAdapter#searchResults(java.util.ArrayList, posemuckel.client.model.UsersPool)
		 */
		@Override
		public void searchResults(final ArrayList<Person> answer, UsersPool pool) {
			Runnable run = new Runnable() {


				public void run() {
					if (answer.isEmpty()) {
						label_feedback.setText(no_users_found);
						table.clearAll();
						return;
					}
					label_feedback.setText(answer.size()+" "+users_found);
					viewer.setInput(answer);
					//TODO funktioniert das ohne refresh?
					viewer.refresh(false);
				}			
			};
			Display.getDefault().asyncExec(run);
		}
		
	}

}
