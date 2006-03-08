/**
 * 
 */
package posemuckel.client.gui.browser;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import posemuckel.client.model.Root;
import posemuckel.client.model.Webpage;

/**
 * Filtert die Webseiten heraus, die nicht vom Anwender besucht werden.
 * @author Posemuckel Team
 *
 */

public class TreeFilterByName extends ViewerFilter {
	
	private String userName;
	
	public TreeFilterByName(String userName) {
		this.userName = userName;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		Root root =(Root)element;
		boolean result = false;
		if(root.getName().equals(userName)) {
			result =  true;
		} else if(root.getType().equals(Webpage.WEBPAGE)){
			result = ((Webpage)element).isVisitedBy(userName);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#isFilterProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isFilterProperty(Object element, String property) {
		//damit funktioniert das Filtern auch bei Updates
		return true;
	}

}
