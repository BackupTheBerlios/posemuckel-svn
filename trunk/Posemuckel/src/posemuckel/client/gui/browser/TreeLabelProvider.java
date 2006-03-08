/**
 * 
 */
package posemuckel.client.gui.browser;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import posemuckel.client.model.Model;
import posemuckel.client.model.Root;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;

/**
 * Diese Klasse übersetzt die Werte des Models in vernünftige Ausgaben für die
 * Trees.
 * @author Posemuckel Team
 *
 */

public class TreeLabelProvider extends WebTraceAdapter 
	implements ILabelProvider {
	
	private TreeIcons icons;
	private String name;
	private String currentURL;

	public TreeLabelProvider(TreeIcons icons, String name) {
		this.icons = icons;
		this.name = name;
		Model.getModel().getOpenProject().getWebtrace().addListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		String type = ((Root)element).getType();
		if(type.equals(Root.USER_TYPE))
			return null;
		if(type.equals(Root.CATEGORY_TYPE))
			return null;
		if(type.equals(Webpage.WEBPAGE)) {
			Webpage page = (Webpage)element;
			if(page.getURL().equals(currentURL)) {
				return icons.getImageWithBackground(page.getRating());
			}
			return icons.getImage(page.getRating());
		}
		return null;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		String type = ((Root)element).getType();
		String text = "";
		if(type.equals(Root.USER_TYPE)) {
			text = ((Root)element).getName();
		} else if(type.equals(Root.CATEGORY_TYPE)) {
			text = ((Root)element).getName();
		} else if(type.equals(Webpage.WEBPAGE)) {
			Webpage page = (Webpage)element;
			text += page.getTitle();
			text +="(" + page.getURL() + ")";
		}		
		return text;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		Model.getModel().getOpenProject().getWebtrace().removeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.WebTraceAdapter#viewing(posemuckel.client.model.event.WebTraceEvent)
	 */
	@Override
	public void viewing(WebTraceEvent event) {
		if(name != null && name.equals(event.getUser())) {
			currentURL = event.getRoot().getName();
		}
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.WebTraceAdapter#visiting(posemuckel.client.model.event.WebTraceEvent)
	 */
	@Override
	public void visiting(WebTraceEvent event) {
		if(name != null && name.equals(event.getUser())) {
			currentURL = event.getURL();
		}
	}
	
	

}
