/**
 * 
 */
package posemuckel.client.gui.resultsviewer;

import java.util.Iterator;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.gui.browser.TreeIcons;
import posemuckel.client.model.Model;
import posemuckel.client.model.Root;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.WebpageComparator;
import posemuckel.client.model.Webtrace;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;
import posemuckel.common.GetText;

/**
 * Ein WebtraceTable zeigt die gesammelten URLs in einer Tabelle an. Die Einträge
 * können nach den verschiedenen Headern sortiert werden. Details werden
 * in Anzeigen unter der Tabelle angezeigt. Die Komponente kann problemlos 
 * an anderen Stellen in der GUI eingebaut werden.
 * @author Posemuckel Team
 *
 */
public class WebtraceTable extends Composite {
	
	//TODO die Spalte muss aktuell gehalten werden, sonst funktioniert DND nicht
	private static final int urlColumn = 3;
	
	private TableViewer viewer;
	private Table table;
	private Webtrace webtrace;
	private RatingTable singleRating;
	private MyListener listener;
	private Model model;
	
	/**
	 * 
	 * @param parent in dem der Webtrace eingebaut werden soll
	 * @param webtrace Webtrace, aus dem die Daten angezeigt werden sollen
	 * @param model das Model, zu dem der Webtrace gehört
	 */
	public WebtraceTable(Composite parent, Webtrace webtrace, Model model) {
		super(parent, SWT.NULL);	
		this.model = model;
		this.webtrace = webtrace;
		buildControls();
		listener = new MyListener();
		webtrace.addListener(listener);
	}
	
	/*
	 * Baut die Tabelle und die Anzeigen unter der Tabelle zusammen. 
	 */
	private void buildControls() {
		setLayout(new FillLayout());
		SashForm child = new SashForm(this, SWT.VERTICAL);
		table = new Table(child, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER );
		table.addSelectionListener(createSelctionListener());
		viewer = buildAndLayoutTable(table);
		addDNDSupport(viewer);
		attachContentProvider(viewer);
		attachLabelProvider(viewer);
		singleRating = new RatingTable(child, null, SWT.HORIZONTAL, model);
		//Zum Debuggen ist das Textfeld sehr nützlich
		//Text target = new Text(child, SWT.NONE);
		//initDropTest(target);
		//child.setWeights(new int[] {2, 1, 1});
		child.setWeights(new int[]{2, 1});
		addFilter(viewer);
		viewer.setInput(webtrace);
	}
	
	/*
	 * Zur Zeit gibt es SelectionListener mit folgenden Aufgaben:
	 * -die Tabelle mit den Einzelwertungen wird an das erste selektierte Element
	 *  gekoppelt.
	 */
	private SelectionListener createSelctionListener() {
		SelectionListener listener = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if((table.getSelection()!= null) && table.getSelection().length > 0) {
					Webpage page = (Webpage)table.getSelection()[0].getData();
					singleRating.setInput(page);
				}
			}
			
		};
		return listener;
	}
	
	protected RatingTable getRatingTable() {
		return singleRating;
	}
	
	/**
	 * Stattet die Tabelle mit einem Filter aus, so dass nur Webseiten mit einer
	 * Durchschnittswertung von mindestens 3 Punkten angezeigt werden.
	 * @param viewer der Tabelle
	 */
	protected void addFilter(TableViewer viewer) {
		//0.6f entspricht einer minimalen durchschnittlichen Bewertung von 3 Punkten
		viewer.addFilter(new RatingFilter(0.6f));
	}
	
	/**
	 * Ermöglicht es der Tabelle, als Quelle oder als Ziel für DND zu dienen.
	 *
	 * @param viewer der als Quelle/Ziel dient
	 */
	protected void addDNDSupport(TableViewer viewer) {
		int ops = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		Transfer[] types = new Transfer[] 
		      {new ResultTransferType(ResultTransferType.URL_TYPE)};
		viewer.addDragSupport(ops, types, new MyDragSourceListener());
		viewer.addDropSupport(ops, types, new ViewerDropAdapter(viewer) {

			@Override
			public boolean performDrop(Object data) {
				String[] urls = (String[]) data;
				Webpage page = null;
				for (int i = 0; i < urls.length; i++) {
					page = webtrace.getPageForUrl(urls[i]);
					if(page != null && page.getParentFolder() != null) {
						model.getOpenProject().
						getFolderTree().requestChangeParentForURL("", urls[i]);
					}
				}
				//System.out.println("data " + data);
				return true;
			}

			@Override
			public boolean validateDrop(Object target, int operation, TransferData transferType) {
				if(!ResultTransferType.supportsURL(transferType))return false;
				return true;				
			}
			
		});
	}
	
	/*
	 * Der LabelProvider bestimmt, welche Einträge in welcher Spalte stehen. 
	 * 
	 */
	private void attachLabelProvider(TableViewer viewer)
	{
		viewer.setLabelProvider(new ITableLabelProvider() {
			public Image getColumnImage(Object element,
					int columnIndex) {
				switch (columnIndex) {
				case 0:
					Webpage page = (Webpage)element;
					return TreeIcons.getIcons().getImage(page.getRating());
				case 1:
					Webpage page2 = (Webpage)element;
					if(page2.hasNote())
						return TreeIcons.getIcons().getImage(0.5f, 'N', 3);
					//ansonsten durchfallen und null ausgeben
				default:
					return null;
				}
			}
			public String getColumnText(Object element,
					int columnIndex) {
				Webpage page = (Webpage)element;
				switch(columnIndex)
				{
				case 0: //Bewertung wird als Icon angezeigt
					//return null;
				case 1: //Bewertung wird als Icon angezeigt
					return null;
				case 2: //Titel
					return page.getTitle();
				case 3: //URL
					return page.getURL();
				default:
					return GetText.gettext("INVALID_COLUMN")
					+ " :" + columnIndex;
				}
			}
			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose(){
			}
			public boolean isLabelProperty(Object element,
					String property){
				return true;
			}
			public void removeListener(ILabelProviderListener lpl) {
			}
		});
	}
	
	private void attachContentProvider(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return webtrace.getURLs();
			}

			public void dispose() {
				webtrace.removeListener(listener);
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
		});
	}
	
	private TableViewer buildAndLayoutTable(final Table table) {
		final TableViewer tableViewer = new TableViewer(table);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(16, 16, true));
		layout.addColumnData(new ColumnWeightData(16, 16, true));
		layout.addColumnData(new ColumnWeightData(40, 40, true));
		layout.addColumnData(new ColumnWeightData(75, 75, true));
		table.setLayout(layout);
		TableColumn ratingColumn = new TableColumn(table, SWT.LEFT);
		ratingColumn.setText(GetText.gettext("POINTS"));
		ratingColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new WebpageSorter(Webpage.RATING));
			}
			
		});
		TableColumn notesColumn = new TableColumn(table, SWT.LEFT);
		notesColumn.setText("");
		notesColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				//tableViewer.setSorter(new WebpageSorter(Webpage.RATING));
			}
			
		});
		TableColumn titleColumn = new TableColumn(table, SWT.LEFT);
		titleColumn.setText(GetText.gettext("TITLE2"));
		titleColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new WebpageSorter(Webpage.TITLE));
			}
			
		});
		TableColumn urlColumn = new TableColumn(table, SWT.LEFT);
		urlColumn.setText(GetText.gettext("URL"));
		urlColumn.addSelectionListener(new SelectionAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new WebpageSorter(Webpage.URL));
			}
			
		});
		table.setHeaderVisible(true);
		return tableViewer;
	}

	/**
	 * Aktualisiert die Tabelle als Reaktion auf verschiedene Ereignisse im Webtrace.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class MyListener extends WebTraceAdapter {
		
		private String[] prop = {"DUMMY"};

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#visiting(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void visiting(WebTraceEvent event) {
			// es könnte eine neue URL hinzugekommen sein:
			//die gesamte Tabelle neu zeichnen
			refresh();
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#traceLoaded(posemuckel.client.model.Webtrace)
		 */
		@Override
		public void traceLoaded(Webtrace webtrace) {
			refresh();
		}
		
		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#elementChanged(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void elementChanged(WebTraceEvent event) {
			update(event.getRoot());	
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#newNote(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void newNote(WebTraceEvent event) {
			update(event.getRoot());
		}
		
		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#parentFolderChanged(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void parentFolderChanged(WebTraceEvent event) {
			Webpage page = (Webpage)event.getRoot();
			if(page.getParentFolder() != null) {
				remove(page);
			} else {
				add(page);
			}
		}

		/**
		 * Zeichnet die ganze Tabelle neu.
		 *
		 */
		private void refresh() {
			Runnable run = new Runnable() {
				public void run() {
					if(table.isDisposed())return;
					viewer.refresh();
				}			
			};
			Display.getDefault().asyncExec(run);
		}
		
		private void add(final Webpage page) {
			Runnable run = new Runnable() {
				public void run() {
					if(table.isDisposed())return;
					viewer.add(page);
				}			
			};
			Display.getDefault().asyncExec(run);
		}
		
		private void remove(final Webpage page) {
			Runnable run = new Runnable() {
				public void run() {
					if(table.isDisposed())return;
					viewer.remove(page);
				}			
			};
			Display.getDefault().asyncExec(run);
		}
		
		/**
		 * Führt ein Update einer Tabellenzeile durch.
		 * @param page Webpage, deren Eigenschaften verändert wurden
		 */
		private void update(final Root page) {
			Runnable run = new Runnable() {
				public void run() {
					if(table.isDisposed())return;
					viewer.update(page, prop);
				}			
			};
			Display.getDefault().asyncExec(run);
		}
		
		
	}
	
	/**
	 * Sortiert die Einträge in der Tabelle nach dem angegebenen Typ. Für die 
	 * erlaubten Typen siehe <code>Webpage</code>. Für das Sortieren wird
	 * ein <code>WebpageComparator</code> verwendet. 
	 * 
	 * @see posemuckel.client.model.Webpage 
	 * @see posemuckel.client.model.WebpageComparator
	 * @author Posemuckel Team
	 *
	 */
	private class WebpageSorter extends ViewerSorter {
		
		private WebpageComparator comp;
		
		WebpageSorter(String type) {
			comp = new WebpageComparator(type);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return comp.compare((Webpage)e1, (Webpage) e2);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerSorter#isSorterProperty(java.lang.Object, java.lang.String)
		 */
		@Override
		public boolean isSorterProperty(Object element, String property) {
			return true;
		}

	}
	
	private class MyDragSourceListener extends DragSourceAdapter {
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.dnd.DragSourceAdapter#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
		 */
		@Override
		public void dragFinished(DragSourceEvent event) {
				if (event.doit) {
					String operation = null;
					switch (event.detail) {
					case DND.DROP_MOVE:
						operation = "moved"; break;
					case DND.DROP_COPY:
						operation = "copied"; break;
					case DND.DROP_LINK:
						operation = "linked"; break;
					case DND.DROP_NONE:
						operation = "disallowed"; break;
					default:
						operation = "unknown"; break;
					}
					System.out.println("Drag Source (data "+operation+")");
				} else {
					System.out.println("Drag Source (drag cancelled)");
				}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.dnd.DragSourceAdapter#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
		 */
		@Override
		public void dragSetData(DragSourceEvent event) {
			if(ResultTransferType.supportsURL(event.dataType)) {
			      // Get the selected items in the drag source
				IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			    String[] data = new String[selection.size() + 1];
			    data[0] = ResultTransferType.URL_TYPE;
				int i = 1;
			    for (Iterator it = selection.iterator(); it.hasNext();) {
					Webpage page = (Webpage) it.next();
					data[i++] = page.getURL();
				}
			      // Put the data into the event
			      event.data = data;
			} else if(TextTransfer.getInstance().isSupportedType(event.dataType)){
				event.data = table.getSelection()[0].getText(urlColumn);
			}
		}		
	}
	
	@SuppressWarnings("unused")
	//zeigt den Inhalt der Zwischenablage an, wird zum Debuggen verwendet
	private void _initDropTest(final Text text) {
		text.setText("Drop Target ");
		DropTarget target = new DropTarget(text, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		target.setTransfer(
				new Transfer[] {
						new ResultTransferType(ResultTransferType.URL_TYPE),
						new ResultTransferType(ResultTransferType.FOLDER_TYPE), 
						TextTransfer.getInstance()});
		target.addDropListener(new DropTargetAdapter() {
			
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.dnd.DropTargetAdapter#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
			 */
			@Override
			public void dragEnter(DropTargetEvent event) {
				//die Operation kann geändert werden
			}

			/* (non-Javadoc)
			 * @see org.eclipse.swt.dnd.DropTargetAdapter#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
			 */
			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				//die Operation kann geändert werden
			}

			public void drop(DropTargetEvent event) {
				String operation = null;
				switch (event.detail) {
				case DND.DROP_MOVE:
					operation = "(moved)"; break;
				case DND.DROP_COPY:
					operation = "(copied)"; break;
				case DND.DROP_LINK:
					operation = "(linked)"; break;
				case DND.DROP_NONE:
					operation = "(disallowed)"; break;
				default:
					operation = "(unknown)"; break;
				}
				text.append("--");
				//unser TransferTyp wird hier eindeutig bevorzugt :-)
				if(ResultTransferType.supports(event.currentDataType)) {
					String[] data = (String[]) event.data;
					String type = data[0];
					type =  " [" + type + "] ";
					text.append(operation+ type);
					for (String string : data) {
						text.append(string + "| ");
					}
				} else if (TextTransfer.getInstance().isSupportedType(event.currentDataType)){ 
					text.append(operation+ " [text] " + (String)event.data);
				}
			}
		});
	}

}
