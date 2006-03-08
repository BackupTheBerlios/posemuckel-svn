/**
 * 
 */
package posemuckel.client.gui;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;

import posemuckel.client.gui.actions.OpenProjectAction;
import posemuckel.client.model.Model;
import posemuckel.client.model.ProjectList;
import posemuckel.client.model.event.ChatEvent;
import posemuckel.client.model.event.UserListenerAdapter;

/**
 * 
 * MyTabFolder enth&auml;lt eine Menge von Modelviews in Tabs. Die Views k&ouml;nnen
 * im laufenden Betrieb hinzugef&uuml;gt werden. Wenn ein neuer Chat eingef&uuml;gt
 * wird, erh&auml;lt er den Fokus.
 * 
 * @author Posemuckel Team
 *
 */

public class MyTabFolder {
	
	private TabFolder tab_folder;
	private MyUserListener listener;
	private Vector<ProjectTab> projectTabs;
	
	/**
	 * Erstellt einen neuen MyTabFolder. Der Inhalt wird durch die Methode
	 * <code>createContent</code> erstellt.
	 * 
	 * @param showChats wenn das Flag auf true gesetzt wird, wird ein Listener
	 * 				eingef&uuml;gt, der ein neues ChatTab setzt, sobald einer erzeugt
	 * 				wird
	 */
	public MyTabFolder(boolean showChats) {
		if(showChats) {
			listener = new MyUserListener();
			Model.getModel().getUser().addListener(listener);
		}
		projectTabs = new Vector<ProjectTab>(0);
	}
	
	/**
	 * Erstellt den Inhalt des TabFolders.
	 * @param parent Composite, in den der TabFolder eingebettet werden soll
	 */
	public void createContent(Composite parent) {
		tab_folder = new TabFolder(parent, SWT.NONE);
		tab_folder.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				//System.out.println(tab_folder.getSelectionIndex());
			}
		});
	}
	
	/**
	 * F&uuml;gt einen neuen Tab f&uuml;r den angegebenen Chat
	 * in den TabFolder ein. 
	 * @param id chatID
	 */
	public void addChat(String id) {
		ChatTab tab = new ChatTab(id);
		tab.createContent(tab_folder);
		//selektiert das zuletzt eingefügte Element
		tab_folder.setSelection(tab_folder.getItemCount() -1);
	}
	
	public void addLogTab() {
		LogTab tab = new LogTab();
		tab.createContent(tab_folder);
	}
	
	/**
	 * F&uuml;gt einen neuen Tab f&uuml;r die angegebene Projektliste
	 * in den TabFolder ein. 
	 * @param list Projektliste
	 */
	public void addProjectTab(ProjectList list) {
		ProjectTab tab = new ProjectTab(list);
		tab.createContent(tab_folder);
		projectTabs.add(tab);
		projectTabs.trimToSize();
	}
	
	/**
	 * F&uuml;gt einen neuen Tab f&uuml;r die angegebene Projektliste
	 * in den TabFolder ein. Die mitgegebene OpenProjectAction wird
	 * initialisiert.
	 * @param list Projektliste
	 * @param action Action, die initialisiert werden soll
	 */
	public void addProjectTab(ProjectList list, OpenProjectAction action) {
		ProjectTab tab = new ProjectTab(list);
		tab.createContent(tab_folder);
		tab.initOpenAction(action);
		projectTabs.add(tab);
		projectTabs.trimToSize();
	}
	
	protected void setEnabledOpenProject(boolean enabled) {
		for (ProjectTab tab : projectTabs) {
			tab.setEnabledOpenProject(enabled);
		}
	}
	
	private class MyUserListener extends UserListenerAdapter {

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.UserListenerAdapter#newChat(posemuckel.client.model.event.ChatEvent)
		 */
		@Override
		public void newChat(final ChatEvent event) {
			Runnable run = new Runnable() {
				public void run() {
					if((!tab_folder.isDisposed()) && tab_folder.getShell()!= null && !tab_folder.getShell().isDisposed()) {
						addChat(event.getSource().getID());
					} else {
						Model.getModel().getUser().removeListener(listener);
						listener = null;
					}
				}
			};
			Display.getDefault().asyncExec(run);	
		}
	}

}
