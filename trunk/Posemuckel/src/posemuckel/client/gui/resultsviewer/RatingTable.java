/**
 * 
 */
package posemuckel.client.gui.resultsviewer;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.gui.MyLayoutFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.Visitor;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;
import posemuckel.common.GetText;

/**
 * In einem RatingTable werden die einzelnen Bewertungen der Anwender zu einer 
 * Webseite tabellarisch dargestellt. Für jeden Anwender wird aufgeführt, ob Notizen
 * zu dieser Webseite vorhanden sind. 
 * 
 * @author Posemuckel Team
 *
 */
public class RatingTable extends Composite {
	
	private Table table;
	private int style;
	private TableViewer tableViewer;
	private Webpage input;
	private Text description_widget;
	private Label label_rating;
	private MyListener listener;
	private Model model;
	
	public RatingTable(Composite parent, Webpage input, int style, Model model) {
		super(parent, SWT.NULL);
		this.model = model;
		if(style != SWT.VERTICAL && style != SWT.HORIZONTAL) 
			throw new IllegalArgumentException("unsupported style");
		this.style = style;
		this.input = input;
		buildControls();
		listener = new MyListener();
		model.getOpenProject().getWebtrace().addListener(listener);
	}
	
	protected void setInput(Webpage page) {
		if((page != null) && (tableViewer != null)) {
			System.out.println("setLabel " + label_rating.getText());
			tableViewer.setInput(page);
			input = page;
			label_rating.setText(
					GetText.gettext("SINGLE_RATING") + ": " + page.getTitle());
			model.getOpenProject().getWebtrace().loadNotes(page.getURL());
		}
	}
		
	protected void buildControls() {
		setLayout(new FillLayout());
		SashForm sash = new SashForm(this, style);
		Composite ratingComp = new Composite(sash, SWT.NONE);
		ratingComp.setLayout(MyLayoutFactory.createGrid(1, false));
		Composite notesComp = new Composite(sash, SWT.NONE);
		notesComp.setLayout(MyLayoutFactory.createGrid(1, false));
		
		label_rating = new Label(ratingComp, SWT.SHADOW_NONE);
		label_rating.setText(GetText.gettext("SINGLE_RATING"));	
		GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
		data1.verticalSpan = 1;
		//data1.grabExcessHorizontalSpace = true;
		data1.horizontalSpan = 1;
		label_rating.setLayoutData(data1);
		
		Label label_notes = new Label(notesComp, SWT.SHADOW_NONE);
		label_notes.setText(GetText.gettext("COMMENT"));	
		GridData data2 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data2.verticalSpan = 1;
		data2.horizontalSpan = 1;
		label_notes.setLayoutData(data2);
		
		table = new Table(ratingComp, SWT.FULL_SELECTION | SWT.BORDER);
		table.addSelectionListener(createSelctionListener());
		tableViewer = buildAndLayoutTable(table);
		attachContentProvider(tableViewer);
		attachLabelProvider(tableViewer);
		if(input != null) {
			tableViewer.setInput(input);
		}
		
		GridData data3 = new GridData(GridData.FILL_BOTH);
		data3.verticalSpan = 1;
		data3.horizontalSpan = 1;
		tableViewer.getControl().setLayoutData(data3);
		
		description_widget = new Text(notesComp, SWT.MULTI |SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		GridData data = new GridData( GridData.FILL_BOTH );
		description_widget.setLayoutData(data);
		description_widget.setEditable(false);
	}
	
	private SelectionListener createSelctionListener() {
		SelectionListener listener = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if((table.getSelection()!= null) && table.getSelection().length > 0) {
					Visitor visitor = (Visitor)table.getSelection()[0].getData();
					if(visitor.hasComment()) {
						description_widget.setText(visitor.getComment());
					} else {
						description_widget.setText("no comment");
					}
				}
			}
			
		};
		return listener;
	}
	
	private TableViewer buildAndLayoutTable(final Table table) {
		TableViewer tableViewer = new TableViewer(table);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1, 16, false));
		layout.addColumnData(new ColumnWeightData(10, 45, true));
		table.setLayout(layout);
		new TableColumn(table, SWT.LEFT);
		new TableColumn(table, SWT.LEFT);
		table.setHeaderVisible(false);
		return tableViewer;
	}
	
	private void attachContentProvider(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if(inputElement == null) return null;
				if(inputElement instanceof Webpage) {
					return ((Webpage)inputElement).getVisitors();
				}
				return null;
			}

			public void dispose() {
				model.getOpenProject().getWebtrace().removeListener(listener);
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				tableViewer = (TableViewer)viewer;
			}
			
		});
	}

	private void attachLabelProvider(TableViewer viewer) {
			
		viewer.setLabelProvider(new ITableLabelProvider() {			
			/**
			 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
			 */
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
			/**
			 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
			 */
			public String getColumnText(Object element,
					int columnIndex) {
				Visitor visitor = (Visitor) element;
				switch(columnIndex)	{
				case 0:
					if(visitor.getRating() < 0) return "--";
					return visitor.getRatingAsString();
				case 1:
					return visitor.getName();
				default:
					return "Invalid column: " + columnIndex;
				}
			}
			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose(){
			}
			public boolean isLabelProperty(Object element,
					String property){
				return false;
			}
			public void removeListener(ILabelProviderListener lpl) {
			}
			
		});
	}
	
	private class MyListener extends WebTraceAdapter {

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#notes(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void notes(WebTraceEvent event) {
			Runnable run = new Runnable() {
				public void run() {
					if((!table.isDisposed()) && table.getSelectionCount() > 0) {
						TableItem item = table.getSelection()[0];
						Visitor v = (Visitor)item.getData();
						description_widget.setText(v.getComment());
					}
				}			
			};
			Display.getDefault().asyncExec(run);

		}
		
	}

}
