/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import posemuckel.client.model.MemberList;
import posemuckel.client.model.event.MemberListAdapter;
import posemuckel.client.model.event.MemberListEvent;
import posemuckel.client.model.event.PersonsEvent;

/**
 * Ein <code>MemberListProvider</code> ist die Br&uuml;cke zwischen einer
 * <code>MemberList</code> und der Tabelle, in der die Liste dargestellt 
 * wird.<br>
 * Der <code>MemberListProvider</code> ruft Informationen aus der 
 * <code>MemberList</code> ab und leitet sie an die Tabelle weiter. Wenn
 * in der <code>MemberList</code> ein Ereignist auftritt, dient der
 * <code>MemberListProvider</code> als Listener und f&uuml;hrt das Update 
 * der Tabelle durch.
 * 
 * @author Posemuckel Team
 *
 */
class MemberListProvider extends MemberListAdapter 
						 implements IStructuredContentProvider {
	
	private MemberList list;
	private TableViewer viewer;
	private boolean disposed;
	
	/**
	 * Diese Instanz bereitet die MemberList so auf, dass sie in einer Tabelle
	 * dargestellt werden kann.
	 * 
	 * @param list MemberList
	 */
	MemberListProvider(MemberList list) {
		this.list = list;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return list.getMembers().toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		disposed = true;
		list.removeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(oldInput != null) {
			((MemberList)oldInput).removeListener(this);
		} 
		if(newInput != null) {
			list = (MemberList)newInput;
			list.addListener(this);
		}
		if(viewer != null) {
			this.viewer = (TableViewer) viewer;
		}
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#listLoaded(posemuckel.client.model.event.MemberListEvent)
	 */
	public void listLoaded(final MemberListEvent event) {
		/*
		 * löscht die alte Liste und läd die neue
		 */
		Runnable run = new Runnable() {
			public void run() {
				if(!disposed) viewer.refresh(false);
			}			
		};
		Display.getDefault().asyncExec(run);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#buddyAdded(posemuckel.client.model.event.MemberListEvent)
	 */
	public void memberAdded(final MemberListEvent event) {
		Runnable run = new Runnable() {
			public void run() {
				if(!disposed)viewer.add(event.getPerson());
			}			
		};
		Display.getDefault().asyncExec(run);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#buddyDeleted(posemuckel.client.model.event.MemberListEvent)
	 */
	public void buddyDeleted(final MemberListEvent event) {
		Runnable run = new Runnable() {

			public void run() {
				if(!disposed)viewer.remove(event.getPerson());
			}			
		};
		Display.getDefault().asyncExec(run);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#error(java.lang.String)
	 */
	public void error(String string) {
		System.out.println("Error in MemberListProvider received");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.MemberListListener#personChanged(posemuckel.client.model.event.PersonsEvent)
	 */
	public void personChanged(final PersonsEvent person) {
		Runnable run = new Runnable() {

			public void run() {
				if(!disposed)viewer.update(person.getSource(), null);
			}			
		};
		Display.getDefault().asyncExec(run);
	}
	
}
