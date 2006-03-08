/**
 * 
 */
package posemuckel.client.gui.browser;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import posemuckel.client.model.Model;
import posemuckel.client.model.Root;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.Webtrace;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;
import posemuckel.client.model.event.WebTraceListener;

/**
 * Liefert die Elemente für die Baumdarstellung und sorgt für das Update des
 * Baumes bei Änderungen des Models.
 * @author Posemuckel Team
 *
 */

public class TraceContentProvider extends WebTraceAdapter implements ITreeContentProvider, WebTraceListener {
	
	private Root root;
	private TreeViewer viewer;
	private String userName;
	private String type;
	private boolean disposed;
	private Webpage previous;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return ((Root)parentElement).getChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		Root root = (Root)element;
		if(root.getType().equals(Webpage.WEBPAGE)) {
			//Webpage page = (Webpage)element;
			//Webpage[] fathers = page.getFathers();
			//if(fathers == null || fathers.length == 0)
				//return root;
			return null;
		} else if(root.getType().equals(Root.MASTER)) {
			return null;
		} else if(root.getType().equals(Root.USER_TYPE)){
			return null;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		//Webpage ist eine Unterklasse von Root
		return ((Root)element).hasChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		//diese Methode wird nur für die Wurzel aufgerufen
		return ((Root)inputElement).getChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		disposed = true;
		Model.getModel().getOpenProject().getWebtrace().removeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//TODO überarbeiten
		root = (Root)newInput;
		this.viewer = (TreeViewer)viewer;
		if(root != null && root.getType().equals(Root.USER_TYPE)) {
			userName = root.getName();
			type = root.getType();
			Model.getModel().getOpenProject().getWebtrace().addListener(this);
		} else if (root != null && root.getType().equals(Root.MASTER)) {
			type = root.getType();
			Model.getModel().getOpenProject().getWebtrace().addListener(this);
		} else if(root!= null) {
			Model.getModel().getOpenProject().getWebtrace().addListener(this);
		}
	}
	

	public void visiting(final WebTraceEvent event) {
		Runnable run = new Runnable() {

			public void run() {
				if(disposed)return;
				if(userName != null && userName.equals(event.getUser())) {
					if(previous != null) {
						viewer.refresh(previous);
					}
					previous = event.getPrevious();
					viewer.refresh(event.getPrevious());
					viewer.expandToLevel(event.getPrevious(), 1);
				} else if(userName == null && type != null && type.equals(Root.MASTER) ){
					viewer.refresh(event.getPrevious());
					viewer.expandToLevel(event.getPrevious(), 1);
				} else if(userName == null) {
					viewer.refresh();
				}
			}			
		};
		if(Display.getDefault().isDisposed()) {
			Model.getModel().getOpenProject().getWebtrace().removeListener(this);
		} else {
			Display.getDefault().asyncExec(run);
		}
	}

	public void rootChanged(final WebTraceEvent event) {
		Runnable run = new Runnable() {

			public void run() {
				if(disposed)return;
				if(type != null && type.equals(Root.MASTER))
					viewer.refresh(root);
			}			
		};
		if(Display.getDefault().isDisposed()) {
			Model.getModel().getOpenProject().getWebtrace().removeListener(this);
		} else {
			Display.getDefault().asyncExec(run);
		}
	}

	public void elementChanged(final WebTraceEvent event) {
		Runnable run = new Runnable() {
			public void run() {
				if(disposed)return;
				viewer.refresh(event.getPrevious());
			}			
		};
		if(Display.getDefault().isDisposed()) {
			Model.getModel().getOpenProject().getWebtrace().removeListener(this);
		} else {
			Display.getDefault().asyncExec(run);
		}
	}

	public void viewing(final WebTraceEvent event) {
		Runnable run = new Runnable() {

			public void run() {
				if(disposed)return;
				if(userName != null && userName.equals(event.getUser())) {
					if(previous != null) {
						viewer.refresh(previous);
					}
					previous = (Webpage)event.getRoot();
					viewer.refresh(event.getRoot());
				}
			}			
		};
		if(Display.getDefault().isDisposed()) {
			Model.getModel().getOpenProject().getWebtrace().removeListener(this);
		} else {
			Display.getDefault().asyncExec(run);
		}
	}

	public void traceLoaded(Webtrace webtrace) {
		Runnable run = new Runnable() {

			public void run() {
				if(disposed)return;
				if(root != null)
					viewer.refresh(root);
			}			
		};
		if(Display.getDefault().isDisposed()) {
			Model.getModel().getOpenProject().getWebtrace().removeListener(this);
		} else {
			Display.getDefault().asyncExec(run);
		}
	}
}
