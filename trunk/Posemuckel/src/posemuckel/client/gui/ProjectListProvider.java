/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import posemuckel.client.model.Project;
import posemuckel.client.model.ProjectList;
import posemuckel.client.model.event.ProjectEvent;
import posemuckel.client.model.event.ProjectListener;
import posemuckel.client.model.event.ProjectListenerAdapter;

/**
 * 
 * @author Posemuckel Team
 *
 */

public class ProjectListProvider extends ProjectListenerAdapter implements IStructuredContentProvider,
		ProjectListener {
	
	private ProjectList projects;
	private TableViewer viewer;
	
	ProjectListProvider(ProjectList projects) {
		this.projects = projects;
	}

	public Object[] getElements(Object inputElement) {
		return projects.getProjects();
	}

	public void dispose() {
		projects.removeListener(this);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(oldInput != null) {
			((ProjectList)oldInput).removeListener(this);
		} 
		if(newInput != null) {
			projects = (ProjectList)newInput;
			projects.addListener(this);
		}
		if(viewer != null) {
			this.viewer = (TableViewer) viewer;
		}
	}

	public void newProject(final ProjectEvent event) {
		Runnable run = new Runnable() {
			public void run() {
				viewer.add(event.getProjectSource());
			}			
		};
		Display.getDefault().asyncExec(run);
	}

	public void deleteProject(final ProjectEvent event) {
		Runnable run = new Runnable() {
			public void run() {
				viewer.remove(event.getProjectSource());
			}			
		};
		Display.getDefault().asyncExec(run);		
	}

	public void listLoaded(final ProjectEvent event) {
		//geht implizit davon aus, dass die Liste nur einmal geladen wird
		Runnable run = new Runnable() {
			public void run() {
				viewer.refresh();
			}			
		};
		Display.getDefault().asyncExec(run);
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.event.ProjectListenerAdapter#elementChanged(posemuckel.client.model.event.ProjectEvent)
	 */
	@Override
	public void elementChanged(final ProjectEvent event) {
		
		Runnable run = new Runnable() {
			public void run() {
				Project project = event.getProjectSource();
				if (projects.equals(project.getModel().getAllProjects())) {
					viewer.refresh();
				} else {
					viewer.update(project, null);
				}			
			}			
		};
		Display.getDefault().asyncExec(run);
	}
	
	
}
