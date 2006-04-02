package posemuckel.client.gui.resultsviewer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import posemuckel.client.gui.ImageManagment;
import posemuckel.client.gui.MyLayoutFactory;
import posemuckel.client.model.Folder;
import posemuckel.client.model.Model;
import posemuckel.client.model.Root;
import posemuckel.client.model.Webpage;
import posemuckel.common.GetText;

/**
 * Baut das Ergebnisfenster zusammen und ermöglicht es dem Anwender, 
 * die einzelnen Webseiten aus der Ergebnistabelle per Drag and Drop
 * in das Foldersystem einzusortieren.
 * 
 * @author Posemuckel Team
 *
 */
public class ResultDialog extends Dialog {

	private ResultTree resultTree;
	private RatingTable singleRating;
	
	public ResultDialog(Shell shell) {
		super(shell);
	}

	protected Control createDialogArea(Composite parent)
	{
		//Colors.initColors();
		GridLayout layout = new GridLayout();
		Composite comp = (Composite)super.createDialogArea(parent);
		layout.numColumns = 1;
		//layout.verticalSpacing = 2;
		//layout.marginWidth = 0;
		//layout.marginHeight = 2;
		comp.setLayout(layout);
		GridData layoutData = new GridData();
		// Create the tree viewer as a child of the composite comp
		SashForm sash = new SashForm(comp, SWT.HORIZONTAL);
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumHeight=500;
		layoutData.minimumWidth=800;
		sash.setLayoutData(layoutData);
		//die Komponenten einsetzen
		singleRating = addTable(sash);
		resultTree = new ResultTree(sash);
		addListeners();
		resultTree.setInput(getInitialInput());
		// Create menu, toolbars, filters, sorters.
		//createFiltersAndSorters();
		//createActions();
		//createMenus();
		//createToolbar();
		resultTree.enableDND();
		
		getShell().setText(GetText.macroreplace(GetText.gettext("PROJECT_RESULTS"),"PROJ",Model.getModel().getOpenProject().getTopic()));
		getShell().setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
		return comp;
	}
		
	/**
	 * Erzeugt die Tabelle zur Anzeige der URLs, die eine Mindestbewertung erhalten
	 * haben. 
	 * @param parent Composite, in dem die Tabelle angezeigt werden soll
	 * @return RatingTable, in dem die Einzelwertungen angezeigt werden
	 */
		protected RatingTable addTable(Composite parent) {
			Composite child = new Composite(parent, SWT.NONE);
			child.setLayout(MyLayoutFactory.createGrid(1, true));
			Composite tvComp = new Composite(child, SWT.NONE);
			tvComp.setLayout(new FillLayout());	
			tvComp.setLayoutData(getGridData());
			Model model = Model.getModel();
			return new WebtraceTable(tvComp, 
					model.getOpenProject().getWebtrace(), model).getRatingTable();
			//die Instanz von RatingTable abfragen und zurückgeben			
	}
		
		private GridData getGridData() {
			GridData gridData= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
			gridData.grabExcessHorizontalSpace=true;
			gridData.grabExcessVerticalSpace=true;
			return gridData;
		}

		protected void addListeners() {
			resultTree.addSelectionChangedListener(new ISelectionChangedListener() {
				//TODO an die Kommentare anschliessen
				public void selectionChanged(SelectionChangedEvent event) {
					// if the selection is empty do nothing
					if(event.getSelection().isEmpty()) {
						return;
					}
					if(event.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection)event.getSelection();
						Object selected = (Root) selection.getFirstElement();
						if(selected instanceof Webpage) {
							singleRating.setInput((Webpage)selected);
						}
						//text_selection.setText(labelProvider.getText(selected, RATING));
					}
				}
			});
		}
	
	
	public Folder getInitialInput() 
	{
		return Model.getModel().getOpenProject().getFolderTree().getFolderRoot();
	}
	
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent,IDialogConstants.CLOSE_ID,GetText.gettext("CLOSE"),true);
	}
	
	/**
	 * Implementiert die Button-Aktionen.
	 */
	protected void buttonPressed(int buttonID) {
		switch (buttonID) {
		case IDialogConstants.CLOSE_ID: {
			close();
			break;
		}
		}
	}
	
}
