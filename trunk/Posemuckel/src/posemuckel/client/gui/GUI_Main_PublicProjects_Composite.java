package posemuckel.client.gui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.gui.actions.AcceptInvitationAction;
import posemuckel.client.gui.actions.JoinProjectAction;
import posemuckel.client.gui.actions.LeaveProjectAction;
import posemuckel.client.gui.actions.OpenProjectAction;
import posemuckel.client.gui.actions.RejectInvitationAction;
import posemuckel.client.model.Project;
import posemuckel.client.model.ProjectComparator;
import posemuckel.client.model.ProjectList;
import posemuckel.common.GetText;

/**
 * Stellt eine Tabelle mit einer Projektübersicht sowie die Tabelle mit den 
 * Projektteilnehmern und den Text zur Darstellung der Projektbeschreibung dar.
 * Die Tabelle mit den Projektteilnehmern und der Text beziehen sich immer
 * auf das Projekt, das der Anwender zuletzt selektiert hat. Das Kontextmenü 
 * der Projektübersicht hängt vom Typ der Projektliste ab.
 * 
 * @author Posemuckel Team
 *
 */
public class GUI_Main_PublicProjects_Composite extends Composite {

	private TableViewer viewer;

	private Table table;
	
	/**
	 * die Projektliste, die dargestellt werden soll
	 */
	private ProjectList input;

	private Text description_widget;

	private GUI_MemberList_Composite membersComp;

	private OpenProjectAction openAction;
	
	/**
	 * neues Composite zur Darstellung der Projektliste
	 * @param parent , in dem dieses Composite eingebettet werden soll
	 * @param input Projektliste mit den darzustellenden Daten
	 */
	public GUI_Main_PublicProjects_Composite(Composite parent, ProjectList input) {
		super(parent, SWT.NULL);
		this.input = input;
		buildControls();
	}
	
	/**
	 * Baut die Tabelle zusammen und erstellt das Kontextmenü.
	 *
	 */
	protected void buildControls() {
		setLayout(MyLayoutFactory.createGrid(1, false, 2));

		table = new Table(this, SWT.FULL_SELECTION | SWT.BORDER);
		table.addSelectionListener(createSelctionListener());
		viewer = buildAndLayoutTable(table);
		attachContentProvider(viewer);
		attachLabelProvider(viewer);
		/*
		 * die Zusammenstellung des Menüs hängt von dem Typ der Liste ab
		 */
		MenuManager tableMenu = new MenuManager();
		if (input.getType().equals(ProjectList.ALL_PROJECTS)) {
			viewer.addFilter(new AllProjectsFilter());
			IAction joinAction = new JoinProjectAction(table);
			IAction newAction = new NewProjectAction();
			tableMenu.add(joinAction);
			tableMenu.add(newAction);
		} else if (input.getType().equals(ProjectList.OPEN_INVITATIONS)) {
			IAction joinAction = new AcceptInvitationAction(table);
			tableMenu.add(joinAction);
			IAction rejectAction = new RejectInvitationAction(table);
			tableMenu.add(rejectAction);
		} else if (input.getType().equals(ProjectList.MY_PROJECTS)) {
			openAction = new OpenProjectAction(table);
			IAction leaveAction = new LeaveProjectAction(table);
			tableMenu.add(openAction);
			tableMenu.add(leaveAction);
		}
		Menu table_menu = tableMenu.createContextMenu(table);
		table.setMenu(table_menu);
		viewer.setInput(input);

		GridData data2 = new GridData(GridData.FILL_BOTH);
		data2.verticalSpan = 1;
		data2.horizontalSpan = 1;
		viewer.getControl().setLayoutData(data2);

	}
	
	/**
	 * Setzt den Text, der eine Projektbeschreibung darstellen soll.
	 * @param text Widget zur Textdarstellung
	 */
	protected void setDescriptionWidget(Text text) {
		description_widget = text;
	}
	
	/**
	 * Setzt das Widget, das die Teilnehmer eines Projektes darstellen soll.
	 * @param widget zur Darstellung einer Teilnehmerliste
	 */
	protected void setParticipantsWidget(GUI_MemberList_Composite widget) {
		membersComp = widget;
	}
	
	/**
	 * Stellt einen Listener, der auf die Auswahl eines Tabelleneintrages vom 
	 * Anwender reagiert, her. Der Listener sorgt für das Update der 
	 * Projektbeschreibung und der Teilnehmertabelle.
	 * @return SelectionListener für die Tabelle
	 */
	private SelectionListener createSelctionListener() {
		SelectionListener listener = new SelectionAdapter() {
			
			/*
			 *  (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				if ((table.getSelection() != null)
						&& table.getSelection().length > 0) {
					Project p = (Project) table.getSelection()[0].getData();
					description_widget.setText(p.getDescription());
					membersComp.setInput(p.getMemberList());
					//TODO es muss überprüft werden, ob das Laden überhaut notwendig ist
					//if(p.getMemberList().isEmpty()) {
					//der Owner sollte immer Mitglied sein, daher wird die Liste nur
					//einmal geladen
					p.getMemberList().load();
					//}
				}
			}

		};
		return listener;
	}
	
	/**
	 * Initialisiert die Action mit der Tabelle, in der die Projekte enthalten sind.
	 * @param action die initialisiert werden soll
	 */
	void initOpenAction(OpenProjectAction action) {
		action.setTable(table);
	}
	
	/**
	 * Der LabelProvider versorgt die Tabelle mit den Strings und Icons für
	 * die einzelnen Zellen.
	 * @param viewer , der einen LabelProvider benötigt
	 */
	private void attachLabelProvider(TableViewer viewer) {
		viewer.setLabelProvider(new ITableLabelProvider() {
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return ((Project) element).getTopic();
				case 1:
					return ((Project) element).getOwner();
				case 2:
					return ((Project) element).isPublic() ? GetText
							.gettext("NO") : GetText.gettext("YES");
				case 3:
					return ((Project)element).getFreeSeats()
					+ "/" + ((Project)element).getMaxNumber();
//				case 4:
//					return ((Project)element).getMaxNumber();
				case 4:
					return ((Project) element).getDate();
				default:
					return GetText.gettext("INVALID_COLUMN") + " :"
							+ columnIndex;
				}
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener lpl) {
			}
		});
	}
	
	/**
	 * Der ContentProvider versorgt die Tabelle mit den einzelnen Projekten.
	 * @param viewer der einen ContentProvider benötigt
	 */
	private void attachContentProvider(TableViewer viewer) {
		viewer.setContentProvider(new ProjectListProvider(input));
	}
	
	/**
	 * Baut die Tabelle zur Projektdarstellung zusammen: es werden die 
	 * Spaltenzahl, die Spaltenbreite, die Spaltenüberschrift und 
	 * die Listener für die Selektion der Spaltenköpfe festgelegt.
	 * @param table ,die die Spalten enthalten soll
	 * @return tableViewer für die Tabelle
	 */
	private TableViewer buildAndLayoutTable(final Table table) {
		final TableViewer tableViewer = new TableViewer(table);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(40, 75, true));
		layout.addColumnData(new ColumnWeightData(15, 75, true));
		layout.addColumnData(new ColumnWeightData(15, 75, true));
		//layout.addColumnData(new ColumnWeightData(15, 75, true));
		layout.addColumnData(new ColumnWeightData(15, 75, true));
		layout.addColumnData(new ColumnWeightData(15, 75, true));
		table.setLayout(layout);
		TableColumn titleColumn = new TableColumn(table, SWT.LEFT);
		titleColumn.setText(GetText.gettext("TOPIC"));
		titleColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new ProjectSorter(Project.TOPIC));
			}

		});
		TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText(GetText.gettext("OWNER"));
		typeColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new ProjectSorter(Project.OWNER));
			}

		});
		TableColumn startdateColumn = new TableColumn(table, SWT.LEFT);
		startdateColumn.setText(GetText.gettext("PRIVATE") + "?");
		startdateColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new ProjectSorter(Project.TYPE));
			}

		});
		TableColumn freeColumn = new TableColumn(table, SWT.CENTER);
		freeColumn.setText(GetText.gettext("FREE_SPACES"));
		freeColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new ProjectSorter(Project.FREE));
			}

		});
//		TableColumn membersColumn = new TableColumn(table, SWT.CENTER);
//		membersColumn.setText(GetText.gettext("ALL_SPACES"));
//		membersColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				tableViewer.setSorter(new ProjectSorter(Project.NO));
//			}
//		});
		TableColumn dateColumn = new TableColumn(table, SWT.CENTER);
		dateColumn.setText(GetText.gettext("DATE"));
		dateColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new ProjectSorter(Project.DATE));
			}

		});
		table.setHeaderVisible(true);
		return tableViewer;
	}
	
	/**
	 * Sortiert die Projekte der Tabelle unter Zuhilfenahme eine 
	 * <code>ProjectComparator</code> nach einem vorgegebenen Kriterium.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class ProjectSorter extends ViewerSorter {
		
		/**
		 * Sortiert die Einträge in der Tabellendarstellung der Projekte.
		 */
		private ProjectComparator comp;
		
		/**
		 * Sortiert die Einträge in der Tabellendarstellung der Projekte nach
		 * dem Sortierkriterium type.
		 * @param type Sortierkriterium
		 */
		ProjectSorter(String type) {
			comp = new ProjectComparator(type);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return comp.compare((Project) e1, (Project) e2);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerSorter#isSorterProperty(java.lang.Object, java.lang.String)
		 */
		@Override
		public boolean isSorterProperty(Object element, String property) {
			return true;
		}

	}
	
	/**
	 * (De-)Aktiviert das Öffnen von Projekten.
	 * @param enabled true, wenn Projekte geöffnet werden können
	 */
	protected void setEnabledOpenProject(boolean enabled) {
		if (openAction != null) {
			openAction.setEnabled(enabled);
		}
	}

}
