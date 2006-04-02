package posemuckel.client.gui.resultsviewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import posemuckel.client.gui.browser.TreeIcons;
import posemuckel.client.model.Folder;
import posemuckel.client.model.Webpage;

public class ResultsViewerLabelProvider extends LabelProvider
{
	final ImageRegistry imageRegistry = new ImageRegistry();
	private String iconPath = ""; 
	private TreeIcons icons;
	
	public ResultsViewerLabelProvider(TreeIcons icons) {
		this.icons = icons;
		imageRegistry.put("Folder", ImageDescriptor.createFromFile(
				this.getClass(),
				iconPath + "folder.gif")
		);
	}
	
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof Folder)
			return imageRegistry.get("Folder");
		if (element instanceof Webpage)
			//return imageRegistry.get("URL");
			return icons.getImage(((Webpage)element).getRating());
		return null;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof Folder) {
			if(((Folder)element).getName() == null) {
				return "no name";
			} else {
				return ((Folder)element).getName();
			}
		} else if (element instanceof Webpage) {
			if(((Webpage)element).getTitle() == null) {
				return "no title";
			} else {
				return ((Webpage)element).getTitle(); 
				//+" (" + ((Webpage)element).getName() + ")";
			}
		} else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

}
