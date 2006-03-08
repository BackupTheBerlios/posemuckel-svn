package posemuckel.client.gui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import posemuckel.client.model.Model;
import posemuckel.client.model.Project;

/**
 * Filtert die Projekte, die in dem Projekt-Tab Sonstige Projekte (Other
 * Projects) dargestellt werden.
 * @author Posemuckel Team
 *
 */
public class AllProjectsFilter extends ViewerFilter {

	public AllProjectsFilter() {
		super();
	}

	/**
	 * Gibt an, ob das übergebene Objekt element gefiltert werden soll.
	 */
	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		Project project = (Project)element;
		Model model = project.getModel();
		String id = project.getID();
		return (!model.getUser().getProjects().contains(id) &&
				!model.getUser().getInvitations().contains(id));
	}

}
