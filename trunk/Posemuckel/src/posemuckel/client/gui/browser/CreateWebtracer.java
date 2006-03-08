package posemuckel.client.gui.browser;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import posemuckel.client.gui.MyLayoutFactory;
import posemuckel.client.model.MemberList;
import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.client.model.event.MemberListAdapter;
import posemuckel.client.model.event.MemberListEvent;
import posemuckel.common.GetText;

/**
 * Klasse zur Darstellung des Webtracers 
 * @author Posemuckel Team
 *
 */
public class CreateWebtracer extends MemberListAdapter {
	
	private TabFolder tabFolder;
	private CreateBrowser browser;
		
	public CreateWebtracer(final Composite parent, CreateBrowser browser){
		Model.getModel().getOpenProject().getMemberList().addListener(this);
		final Shell shell=parent.getShell();
		this.browser = browser;
		shell.setLayout(MyLayoutFactory.createGrid(1, false));
		
/**
	
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace=true;
		gridData.horizontalAlignment= GridData.FILL;
		
		parent.setLayoutData(gridData);
		shell.setLayoutData(gridData);
**/		
		//die Referenz auf das Projekt holen
		Project project = Model.getModel().getOpenProject();
		//die Referenz auf die Mitgliederliste holen
		MemberList members = project.getMemberList();
		//TabItem[] tabItem = new TabItem[members.size()];
		String[] name= new String[members.size()];
		// Erstellen der User-Tabs
		tabFolder = new TabFolder(parent, SWT.CLOSE);
		int userIndex = 0;
		String userName = Model.getModel().getUser().getNickname();
		for (int i=0; i<members.size(); i++) {
			name[i]=members.getMembers().get(i).getNickname();
			if(name[i].equals(userName)) {
				userIndex = i;
			}
			addTab(name[i]);
		}
		//enthält alle Webtraces
		TabItem master = new TabItem(tabFolder, SWT.NULL);
		master.setText(GetText.gettext("MASTER"));
		new CreateTreeView(tabFolder, master, browser);
		//TabItem tableItem = new TabItem(tabFolder, SWT.NULL);
		//tableItem.setText("Tabelle");
		//addTable(tabFolder, tableItem);
		//selektiere den eigenen Webtrace
		tabFolder.setSelection(userIndex);
	}	
	
//	protected void addTable(TabFolder folder, TabItem tab) {
//		Composite child = new Composite(folder, SWT.NONE);
//		child.setLayout(MyLayoutFactory.createGrid(1, true));
//		Composite tvComp = new Composite(child, SWT.NONE);
//		tvComp.setLayout(new FillLayout());	
//		tvComp.setLayoutData(getGridData());
//		new WebtraceTable(tvComp, Model.getModel().getOpenProject().getWebtrace());
//		tab.setControl(child);	
//	}
	
//	private GridData getGridData() {
//		GridData gridData= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
//		gridData.grabExcessHorizontalSpace=true;
//		gridData.grabExcessVerticalSpace=true;
//		return gridData;
//	}
	
	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListAdapter#buddyDeleted(posemuckel.client.model.event.MemberListEvent)
	 */
	@Override
	public void buddyDeleted(final MemberListEvent event) {
		Runnable run = new Runnable() {

			public void run() {
				if(tabFolder.isDisposed())return;
				int size = tabFolder.getItemCount();
				for (int i = 0; i < size; i++) {
					TabItem item = tabFolder.getItem(i);
					if(item.getText().equals(event.getPerson().getNickname())) {
						item.dispose();
						break;
					}
				}
			}			
		};
		if(!tabFolder.isDisposed()) {
			Display.getDefault().asyncExec(run);
		} else {
			Model.getModel().getOpenProject().getMemberList().removeListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListAdapter#memberAdded(posemuckel.client.model.event.MemberListEvent)
	 */
	@Override
	public void memberAdded(final MemberListEvent event) {
		Runnable run = new Runnable() {

			public void run() {
				if(tabFolder.isDisposed())return;
				addTab(event.getPerson().getNickname());
			}			
		};
		if(!tabFolder.isDisposed()) {
			Display.getDefault().asyncExec(run);
		} else {
			Model.getModel().getOpenProject().getMemberList().removeListener(this);
		}
	}

	private TabItem addTab(String user) {
		TabItem tab = new TabItem(tabFolder, SWT.CLOSE);
		tab.setText(user);
		new CreateTreeView(tabFolder,tab,user, browser);
		return tab;
	}

	
	
}
