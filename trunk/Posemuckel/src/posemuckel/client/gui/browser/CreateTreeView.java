package posemuckel.client.gui.browser;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TreeItem;

import posemuckel.client.gui.NoteDialog;
import posemuckel.client.model.Chat;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.Webtrace;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;
import posemuckel.common.GetText;

/**
 * 
 * @author Posemuckel Team
 *
 */
public class CreateTreeView {

	private Menu menu;
	private TreeViewer traceTree;
	//der Browser wird gebraucht, um die URL beim Doppelklick setzen zu können
	private CreateBrowser createBrowser;
	//Name des Anwenders
	private String name;
	private Shell shell;
	private Webpage selected=null;
	private String myname;

	
	private class adapter extends WebTraceAdapter  {
		private Webtrace trace;
		private boolean activatedDialog;
		
		public adapter(Webtrace trace) {
			this.trace = trace;
		}
		
		public void activate() {
			activatedDialog = true;
		}
		
		public void newNote(WebTraceEvent event) {
			//Webpage wp = (Webpage)event.getRoot();
		}
		
		public void notes(final WebTraceEvent event) {
			Runnable run = new Runnable() {
				public void run() {
					if(shell.isDisposed()) return;
					NoteDialog note = new NoteDialog(shell,(Webpage)event.getRoot());
					note.open();		
				}
			};		
			if(shell.isDisposed()) {
				trace.removeListener(this);
			} else if(activatedDialog){
				activatedDialog = false;
				Display.getDefault().asyncExec(run);
			}
			}
	};

	private static adapter myadapter = null;
	
	
	/*
	 * Zeigt den Trace eines Anwenders.
	 */
	public CreateTreeView(final Composite parent, TabItem tabItem,String name, CreateBrowser browser) {
		this.name = name;
		this.myname = Model.getModel().getUser().getNickname();
		initComposite(parent, tabItem);
		this.shell = parent.getShell();
		initTraceTree(name);
		createBrowser = browser;
		//Konstruktorspezifischer Code
		traceTree.setInput(Model.getModel().getOpenProject().getWebtrace().getRootForName(name));
		traceTree.addFilter(new TreeFilterByName(name));
		addTraceListener();
	};
	
	/*
	 * Konstruktor zur Anzeige des gemeinsamen Trace aller Anwender 
	 */
	public CreateTreeView(final Composite parent, TabItem tabItem, CreateBrowser browser) {
		initComposite(parent, tabItem);
		initTraceTree(null);
		
		createBrowser = browser;
		//Konstruktorspezifischer Code
		traceTree.setInput(Model.getModel().getOpenProject().getWebtrace().getRootElementForWholeTrace());
		//Filter wird nicht gebraucht
	};
	
	private void initTraceTree(String userName) {
		traceTree.setContentProvider(new TraceContentProvider());
		traceTree.setLabelProvider(new TreeLabelProvider(TreeIcons.getIcons(), userName));
		addTreeListener();
		traceTree.setUseHashlookup(true);
		//initContextMenu();
		//menu = new Menu(traceTree.getControl());
		//traceTree.getControl().setMenu(menu);
		//MenuItem test = new MenuItem(menu, SWT.POP_UP);
		//test.setText("Hallo");

	}
		
	private void addTreeListener() {
		traceTree.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				Webpage page = (Webpage)selection.getFirstElement();
				if(createBrowser != null) {
					createBrowser.setURL(page.getURL());
				} else {
					new CreateStaticBrowser(traceTree.getTree().getShell(), page.getURL(), page.getTitle());
				}

			}
			
		}) ;
		
		//Listener um im Tree einen rechten Mausklick abzufangen
		traceTree.getTree().addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Point point = new Point(event.x, event.y);
				if (traceTree.getTree().getMenu()!=null) {
					traceTree.getTree().getMenu().dispose();
				}
				final TreeItem item= traceTree.getTree().getItem(point);
				if (item==null) {
					//traceTree.getTree().getMenu().dispose();
					return;
				}
				//final IStructuredSelection selection = (IStructuredSelection)item;
				if (event.button==3) {
					//System.out.println("Rechtsklick auf folgenden Knoten");
					//System.out.println(item.getText());
					//System.out.println(item.getData());
					//MenuManager menuT = new MenuManager();
					//menuT.add(new TreeAction(traceTree));
					//Menu menu = menuT.createContextMenu(traceTree.getTree());
					menu = new Menu(traceTree.getTree());
					traceTree.getTree().setMenu(menu);
					MenuItem preview = new MenuItem(menu, SWT.POP_UP);
					preview.setText("Vorschau");
					preview.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							Webpage page = (Webpage) item.getData();
							//Webpage page = (Webpage)((IStructuredSelection) item).getFirstElement();
							new CreateStaticBrowser(traceTree.getTree().getShell(), page.getURL(), page.getTitle());
						}
					});
					MenuItem toBrowser = new MenuItem(menu, SWT.POP_UP);
					toBrowser.setText("Seite im Browser");
					toBrowser.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							Webpage page = (Webpage) item.getData();
							//Webpage page = (Webpage)((IStructuredSelection) item).getFirstElement();
							if(createBrowser != null) {
								createBrowser.setURL(page.getURL());
							}
						}
					});
					Webpage page = (Webpage) item.getData();
					// Zeige den Menüpunkt nur, wenn entweder andere
					// eine Notiz geschrieben haben oder ich die Seite
					// besucht habe.
					if ( page.hasNote() || page.isVisitedBy(myname) ) {
						MenuItem addNote = new MenuItem(menu, SWT.POP_UP);
						if( page.isVisitedBy(myname) )
							addNote.setText("Eigenschaften bearbeiten");
						else
							// Leute, die die Seite noch nicht
							// gesehen haben, dürfen Notizen
							// nur lesen!
							addNote.setText("Eigenschaften ansehen");
						
						addNote.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event event) {
								myadapter.activate();
								selected = (Webpage) item.getData();
								Model.getModel().getOpenProject().getWebtrace().loadNotes(selected.getURL());
							}
						});
					}
					menu.setVisible(true);
				}
				
			}
			
		});		
		
	}
	
	
	
  

	private void initComposite(final Composite parent, final TabItem tabItem){
		
		Composite child = new Composite(parent, SWT.NONE);
		child.setLayout(new GridLayout(1,false));
		
		Composite tbComp = new Composite(child, SWT.NONE);
		tbComp.setLayout(new FillLayout());	
		ToolBar tb = new ToolBar(tbComp, SWT.WRAP);
		ToolBarManager mngr= new ToolBarManager(tb);

		//die Actions hängen davon ab, was für ein Webtrace angezeigt wird
		if((name != null) && (!name.equals(Model.getModel().getUser().getNickname()))){
			//System.out.println("add " + name);
			Action actionFollowMe = new Action (GetText.gettext("FOLLOW_ME")) {
				private boolean active = true;
				public void run() {
					Model mod = Model.getModel();
					User user = mod.getUser();
					String myself = user.getNickname();
					Chat chat = mod.getLogchat();
					String msg;
					if(active)
						msg = "Benutzer "+myself+" folgt "+name+".";
					else
						msg = "Benutzer "+myself+" folgt "+name+" nicht mehr.";
					if (chat != null)
						chat.userIsChatting(msg);
					createBrowser.activateFollowMe(name, active);
					//zum deaktivieren den boolschen Wert auf false setzen
					//der Name ist dabei egal
					createBrowser.activateFollowMe(name, active);
					active = !active;
				}
			};
			actionFollowMe.setChecked(false);
			mngr.add(actionFollowMe);
		}
		mngr.update(true);
		
		GridData gridData= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace=true;
		gridData.grabExcessVerticalSpace=true;
		Composite tvComp = new Composite(child, SWT.NONE);
		tvComp.setLayout(new GridLayout(1,false));
		tvComp.setLayoutData(gridData);
		traceTree=new TreeViewer(tvComp, SWT.BORDER);
		traceTree.getControl().setLayoutData(gridData);

		tabItem.setControl(child);	
	}
		
	
	
//	public void addNode(String title, String url, String previous) {
//		boolean root=previous.equals(""); 
//		boolean found=false;
//		System.out.println("CreateTreeView:");
//		System.out.println("ArraySize: " + itemList.size());
//		CreateTreeNode node = new CreateTreeNode();
//		if (itemList.isEmpty()==false){
//			System.out.println("Uebergebene URL:");
//			System.out.println(url);
//			for (int i=0; i<itemList.size(); i++) {
//				System.out.println(itemList.get(i).getURL());
//				if (url.equals(itemList.get(i).getURL())) {
//					//lastIndex=i;
//					System.out.println("match");
//					found=true;;
//					lastIndex=i;
//					itemList.get(i).getThisItem().setText(title + " (" + url + ")");
//				}
//				else {
//					System.out.println("no match");
//				}
//			}
//			if (found==true) {
//				itemList.add(itemList.get(lastIndex));
//			}
//		}
//		if ((found==false) && (root==false) && (itemList.isEmpty()==false)) {
//			node.url=url;
//			lastItem=new TreeItem(itemList.get(itemList.size()-1).getThisItem(), SWT.NULL);
//			lastItem.setText(title + "(" + url + ")");
//			lastItem.setImage(Colors.getCircle(-1));
//			lastItem.setData(url);
//			node.thisItem=lastItem;
//			itemList.add(node);
//			//lastIndex=itemList.size();			
//		}
//	
//		if ((found==false) && ((root==true) || (itemList.isEmpty()))) {
//			node.url=url;
//			lastItem=new TreeItem(traceTree, SWT.NULL);
//			lastItem.setText(title + " (" + url + ")");
//			lastItem.setImage(Colors.getCircle(-1));
//			lastItem.setData(url);
//			node.thisItem=lastItem;
//			itemList.add(node);
//			//lastIndex=itemList.size();					
//		}
//	}	

	
	private void addTraceListener() {
		if( myadapter == null ) {
			Webtrace trace = Model.getModel().getOpenProject().getWebtrace();
			myadapter = new adapter(trace);
			trace.addListener(myadapter);
		}
	}
	
}
