package posemuckel.client.gui.resultsviewer;

import java.util.Iterator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import posemuckel.client.model.Folder;
import posemuckel.client.model.FolderTree;
import posemuckel.client.model.Model;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.event.FolderEvent;
import posemuckel.client.model.event.FolderTreeListener;

/**
 * Der Content Provider für ResultsViewer.java
 * 
 * @author Posemuckel Team
 *
 */

public class ResultsViewerContentProvider implements ITreeContentProvider, 
	FolderTreeListener {
	
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	private FolderTree folderTree;
	private boolean disposed;

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Folder) {
			Folder folder = (Folder)parentElement;
			return concat(folder.getChildren(), folder.getSubfolders().toArray());
		}
		return EMPTY_ARRAY;
	}
	
	protected Object[] concat(Object[] o1, Object[] o2) {
		if(o1 != null && o2 != null) {
			Object[] both = new Object[o1.length + o2.length];
			System.arraycopy(o1, 0, both, 0, o1.length);
			System.arraycopy(o2, 0, both, o1.length, o2.length);
			return both;
		} else if(o2 != null) {
			return o2;
		} else if(o1 != null) {
			return o1;
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		if(element instanceof Folder) {
			return ((Folder)element).getParentFolder();
		}
		if(element instanceof Webpage) {
			return ((Webpage)element).getParentFolder();
		}
		return null;
	}


	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}


	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	* Notifies this content provider that the given viewer's folderTree
	* has been switched to a different element.
	* <p>
	* A typical use for this method is registering the content provider as a listener
	* to changes on the new folderTree (using model-specific means), and deregistering the viewer 
	* from the old folderTree. In response to these change notifications, the content provider
	* propagates the changes to the viewer.
	* </p>
	*
	* @param viewer the viewer
	* @param oldInput the old folderTree element, or <code>null</code> if the viewer
	*   did not previously have an folderTree
	* @param newInput the new folderTree element, or <code>null</code> if the viewer
	*   does not have an folderTree
	*/
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null && oldInput instanceof Folder) {
			removeListenerFrom((Folder)oldInput);
			folderTree.removeListener(this);
		}
		if(newInput != null && newInput instanceof Folder) {
			addListenerTo((Folder)newInput);
			folderTree = Model.getModel().getOpenProject().getFolderTree();
			folderTree.addListener(this);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child of the given root-Folder. */
	protected void removeListenerFrom(Folder treeItem) {
		//treeItem.removeListener(this);
		for (Iterator iterator = treeItem.getSubfolders().iterator(); iterator.hasNext();) {
			Folder folder = (Folder) iterator.next();
			removeListenerFrom(folder);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child box of the given box. */
	protected void addListenerTo(Folder treeItem) {
		//treeItem.addListener(this);
		for (Iterator iterator = treeItem.getSubfolders().iterator(); iterator.hasNext();) {
			Folder folder = (Folder) iterator.next();
			addListenerTo(folder);
		}
	}
	
	
	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
	
	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {
		disposed = true;
		folderTree.removeListener(this);
	}

	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.FolderTreeListener#foldersLoaded(posemuckel.client.model.event.FolderEvent)
	 */
	public void foldersLoaded(FolderEvent event) {
		refresh(null);
	}

	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.FolderTreeListener#urlsLoaded(posemuckel.client.model.event.FolderEvent)
	 */
	public void urlsLoaded(FolderEvent event) {
		refresh(null);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.FolderTreeListener#urlChangedParent(posemuckel.client.model.event.FolderEvent)
	 */
	public void urlChangedParent(FolderEvent event) {
		if(event.getOldParent() != null) refresh(event.getOldParent());
		if(event.getCurrentParent() != null) refresh(event.getCurrentParent());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.FolderTreeListener#folderChangedParent(posemuckel.client.model.event.FolderEvent)
	 */
	public void folderChangedParent(FolderEvent event) {
		refresh(event.getOldParent());
		refresh(event.getCurrentParent());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.FolderTreeListener#newFolder(posemuckel.client.model.event.FolderEvent)
	 */
	public void newFolder(FolderEvent event) {
		refresh(event.getCurrentParent());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.event.FolderTreeListener#deletedFolder(posemuckel.client.model.event.FolderEvent)
	 */
	public void deletedFolder(FolderEvent event) {
		refresh(event.getOldParent());
	}
	
	/*
	 * Zeichnet den Baum ab dem angegebenen Folder neu. Wenn als Folder null
	 * angegeben wird, wird der ganze Baum neu gezeichnet.
	 */
	private void refresh(final Folder folder) {
		Runnable run = new Runnable() {

			public void run() {
				if(disposed)return;
				if(folder != null) {
					//nur einen Teilbaum neu zeichnen
					viewer.refresh(folder, false);
					viewer.expandToLevel(folder, 2);
				} else {
					//den ganzen Baum neu zeichnen
					viewer.refresh();
					viewer.expandAll();
				}
			}
		};	
		Display.getDefault().asyncExec(run);
		
	}

}
