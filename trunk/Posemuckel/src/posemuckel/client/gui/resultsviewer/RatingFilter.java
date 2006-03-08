/**
 * 
 */
package posemuckel.client.gui.resultsviewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import posemuckel.client.model.Webpage;

/**
 * Filtert die Webpages nach ihrer Bewertung. 
 * @author Posemuckel Team
 *
 */

public class RatingFilter extends ViewerFilter {
	
	/*
	 * die minimale Bewertung, die noch akzeptabel ist
	 */
	private float acceptable;
	
	/**
	 * 
	 * @param acceptable die minimale akzeptable Bewertung
	 */
	public RatingFilter(float acceptable) {
		this.acceptable = acceptable;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#isFilterProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isFilterProperty(Object element, String property) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean ok = ((Webpage)element).getRating() >= acceptable;
		if(ok) {
			ok = (((Webpage)element).getParentFolder() == null);
		}
		return ok;
	}

}
