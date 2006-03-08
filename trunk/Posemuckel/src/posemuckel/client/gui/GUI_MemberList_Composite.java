package posemuckel.client.gui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import posemuckel.client.gui.actions.AddBuddyAction;
import posemuckel.client.gui.actions.DeleteBuddyAction;
import posemuckel.client.gui.actions.ShowProfileAction;
import posemuckel.client.gui.actions.StartChatAction;
import posemuckel.client.gui.browser.TreeIcons;
import posemuckel.client.model.MemberList;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.User;
import posemuckel.common.GetText;

public class GUI_MemberList_Composite extends Composite
{
	private String iconPath = "icons/"; 
	private Table table;
	private TableViewer viewer;
	private MemberList input;
	private Menu menu;
	
	public GUI_MemberList_Composite(Composite parent, MemberList input)
	{
		super(parent, SWT.NULL);
		this.input = input;
		buildControls();
	}
		
	protected void buildControls()
	{
		setLayout(MyLayoutFactory.createGrid(1, false, 2));

		Label label_buddyList = new Label(this, SWT.SHADOW_NONE);
		label_buddyList.setText(getMyTitle());	
		GridData data1 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data1.verticalSpan = 1;
		data1.horizontalSpan = 1;
		label_buddyList.setLayoutData(data1);
		
		table = new Table(this, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		viewer = buildAndLayoutTable(table);
		attachContentProvider(viewer);
		attachLabelProvider(viewer);
		/*
		 * das Kontextmenü hängt von der Liste ab, die dargestellt werden soll
		 */
		table.addListener(SWT.MouseDown, new Listener() {
			
			public void handleEvent(Event event) {
				Point point = new Point(event.x, event.y);
				if (table.getMenu()!=null) {
					table.getMenu().dispose();
				}
				final TableItem item= table.getItem(point);
				if (item==null) {
					return;
				}
				//final IStructuredSelection selection = (IStructuredSelection)item;
				if (event.button==3) {
					menu = new Menu(table);
					Person selected = (Person)item.getData();
					User myself = Model.getModel().getUser();
					if(input.getType().equals(MemberList.BUDDY_TYPE)) {
						MenuItem delBuddy = new MenuItem(menu, SWT.POP_UP);
						final DeleteBuddyAction deleteBuddyAction = new DeleteBuddyAction(table);
						delBuddy.setText(GetText.gettext("DELETE_BUDDY"));
						delBuddy.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event event) {
								deleteBuddyAction.run();
							}
						});
					} 

					if ( !selected.getNickname().equals(myself.getNickname())) {
							MenuItem startChat = new MenuItem(menu, SWT.POP_UP);
							final StartChatAction startChatAction = new StartChatAction(table);
							startChat.setText(GetText.gettext("START_CHAT"));
							startChat.addListener(SWT.Selection, new Listener() {
								public void handleEvent(Event event) {
									startChatAction.run();
								}
							});

							if(!myself.getBuddyList().hasMember(selected.getNickname())) {
								MenuItem addBuddy = new MenuItem(menu, SWT.POP_UP);
								final AddBuddyAction addBuddyAction = new AddBuddyAction(table);
								addBuddy.setText(GetText.gettext("ADD_BUDDY"));
								addBuddy.addListener(SWT.Selection, new Listener() {
								public void handleEvent(Event event) {
									addBuddyAction.run();
								}
								});
							}
					}
					MenuItem showProfile = new MenuItem(menu, SWT.POP_UP);
					final ShowProfileAction showProfileAction = new ShowProfileAction(table);
					showProfile.setText(GetText.gettext("SHOW_PROFILE"));
					showProfile.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							showProfileAction.run();
						}
					});
					

					menu.setVisible(true);
				}
			}
		});
		 
		/*
		 * der Input wird direkt aus der MemberList bezogen!
		 */
		viewer.setInput(input);
		
		GridData data2 = new GridData(GridData.FILL_BOTH);
		data2.verticalSpan = 1;
		data2.horizontalSpan = 1;
		viewer.getControl().setLayoutData(data2);
	}
	
	protected void setInput(MemberList input) {
		viewer.setInput(input);
	}
	
	private String getMyTitle() {
		String title = "";
		if(input.getType().equals(MemberList.BUDDY_TYPE)) {
			title = GetText.gettext("MY_BUDDIES");
		} else if (input.getType().equals(MemberList.ALL_USERS_TYPE)){
			title = GetText.gettext("ALL_USERS");
		} else if (input.getType().equals(MemberList.CHAT)){
			title = GetText.gettext("PARTICIPANTS");
		} else if (input.getType().equals(MemberList.PROJECT)){
			title = GetText.gettext("PARTICIPANTS");
		}
		title+= ":";
		return title;
	}
	
	private TableViewer buildAndLayoutTable(final Table table)
	{
		TableViewer tableViewer = new TableViewer(table);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1, 20, false));
		layout.addColumnData(new ColumnWeightData(10, 45, true));
		table.setLayout(layout);
		new TableColumn(table, SWT.LEFT);
		new TableColumn(table, SWT.LEFT);
		table.setHeaderVisible(false);
		return tableViewer;
	}
	
	private void attachContentProvider(TableViewer viewer)
	{
		viewer.setContentProvider(new MemberListProvider(input));
	}

	private void attachLabelProvider(TableViewer viewer)
	{	
		final ImageRegistry imageRegistry = new ImageRegistry();
		imageRegistry.put(Person.ONLINE, ImageDescriptor.createFromFile(
				this.getClass(),
				iconPath + "online.bmp")
		);
		imageRegistry.put(Person.OFFLINE, ImageDescriptor.createFromFile(
				this.getClass(), 
				iconPath + "offline.bmp")
		);		
		imageRegistry.put(Person.UNKNOWN, ImageDescriptor.createFromFile(
				this.getClass(), 
				iconPath + "unknown.bmp")
		);		
			
		viewer.setLabelProvider(new ITableLabelProvider() {			
			/**
			 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
			 */
			public Image getColumnImage(Object element, int columnIndex) {
				if(columnIndex == 0) {
					if(((Person)element).getState().equals(Person.OFFLINE)) {
						return imageRegistry.get(Person.OFFLINE);
					}
					else if(((Person)element).getState().equals(Person.ONLINE)) {
						return imageRegistry.get(Person.ONLINE);
					}
					else if(((Person)element).getState().equals(Person.UNKNOWN)) {
						return imageRegistry.get(Person.UNKNOWN);
					}
					else if(((Person)element).getState().equals(Person.BROWSING)) {
						return TreeIcons.getIcons().getImage(1f, 'P', 3);
					}
				    else  {
						System.err.println("unknown state\n" + element);
						return null;
					}
				} else {
					return null;
				}
			}
			/**
			 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
			 */
			public String getColumnText(Object element,
					int columnIndex) {
				switch(columnIndex)
				{
				case 0:
					return null; // da wir ein icon für den Status benutzen
				case 1:
					return ((Person)element).getNickname();
				default:
					return "Invalid column: " + columnIndex;
				}
			}
			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose(){
			}
			public boolean isLabelProperty(Object element,
					String property){
				return false;
			}
			public void removeListener(ILabelProviderListener lpl) {
			}
			
		});
	}
}