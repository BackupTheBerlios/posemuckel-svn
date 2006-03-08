/**
 * 
 */
package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import posemuckel.client.gui.resultsviewer.ResultDialog;
import posemuckel.client.model.Model;
import posemuckel.common.GetText;

/**
 * �ffnet den Dialog mit der Ergebnisansicht
 * 
 * @author Posemuckel Team
 *
 */

public class OpenResultsAction extends Action {
	
	private Shell shell;
	
	/**
	 * Wenn f�r die Shell null �bergeben wird, muss sie sp�ter mit
	 * <code>setShell</code> gesetzt werden.
	 * 
	 * @param shell die geschlossen werden soll
	 */
	public OpenResultsAction(Shell shell, ImageDescriptor id ) {
		super(GetText.gettext("SHOW_RESULTS") + "@Ctrl+R", id);
		this.shell = shell;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		ResultDialog rd = new ResultDialog(shell);
		//die Struktur laden falls n�tig
		if(!Model.getModel().getOpenProject().getFolderTree().isLoaded()) {
			Model.getModel().getOpenProject().getFolderTree().loadFolderStructure();
		}
		rd.open();
	}
	
	/**
	 * Die Shell kann auch nachtr�glich gesetzt werden ,wenn sie bei der
	 * Initialisierung der Action noch nicht bekannt ist.
	 * 
	 * @param shell die geschlossen werden soll
	 */
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	
	

}
