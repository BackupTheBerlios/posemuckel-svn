package posemuckel.client.gui.browser;


import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.gui.Colors;
import posemuckel.client.gui.MyLayoutFactory;
import posemuckel.client.gui.actions.GoogleAction;
import posemuckel.client.model.FollowMeManager;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.Project;
import posemuckel.client.model.Visitor;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.event.FollowMeListener;
import posemuckel.client.model.event.NotifyEvent;
import posemuckel.client.model.event.NotifyListener;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;
import posemuckel.common.GetText;

/**
 * Klasse zur Darstellung des Browsers 
 * @author Posemuckel Team
 *
 */
public class CreateBrowser {

	private static Browser browser;	
	private Composite browserComp;
	private static Text textLocation;
	private Label labelStatus;
	private Button voteButton;
	private StyledText textVisitors;
	
	private String actualTitle;
	//private String actualURL;
	private boolean send = false;
	private boolean titleset = false;
	
	private FollowMeManager followMe;
	
	
	
	private static String lastURL;
	private Shell shell;
	
	private BrowserMenu menu;
	
	/**
	 * Erstellt den Browser im, durch parent, vorgegebenen Composite
	 * @param parent Composite in dem der Browser erstellt werden soll
	 */
	public CreateBrowser(final Composite parent){
		addListener();
		followMe = Model.getModel().getOpenProject().getFollowMeManager();
		followMe.addListener(new FollowMeListener() {

			public void following(String name, FollowMeManager manager) {
				if(browserComp.isDisposed()) {
					manager.removeListener(this);
					return;
				}
				activateFollowMe(name);
			}

			public void deactivation(FollowMeManager manager) {}
			
		});
		shell=parent.getShell();
		parent.setLayout(MyLayoutFactory.createGrid(1, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"go.gif");		
		final Image goIcon = id.createImage(Display.getCurrent());
		
		//Adresszeile
		Composite adrComp = new Composite(parent, SWT.NULL);
		adrComp.setLayout(MyLayoutFactory.createGrid(4, false));
		adrComp.setLayoutData(createGridData());
		Label labelAddress = new Label(adrComp, SWT.NULL);
		labelAddress.setText(GetText.gettext("ADDRESS"));
		textLocation = new Text(adrComp, SWT.SINGLE | SWT.BORDER);
		textLocation.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		Button buttonGo = new Button(adrComp, SWT.NULL);
		buttonGo.setImage(goIcon);
		buttonGo.setText(GetText.gettext("GO"));
		//Suchen
		GoogleAction google = new GoogleAction();
		ActionContributionItem googleButton = new ActionContributionItem(google);
		googleButton.fill(adrComp);
		google.setInputField(textLocation);
		//VoteBar
		Composite voteBarComp = new Composite(parent, SWT.NONE);		
		voteBarComp.setLayout(MyLayoutFactory.createGrid(5, false));
		voteBarComp.setLayoutData(createVoteBarData(true));
		
		final Spinner voteWidget = new Spinner(voteBarComp, SWT.NONE);
		voteWidget.setMaximum(5);
		voteWidget.setMinimum(0);
		voteWidget.setSelection(3);
		final Label voteIconLabel = new Label(voteBarComp, SWT.NULL);
		voteIconLabel.setImage(TreeIcons.getIcons().getImage(0.6f));
		
		voteButton= new Button(voteBarComp, SWT.NULL);
		voteButton.setText(GetText.gettext("SEND"));
		voteWidget.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				float vote = (voteWidget.getSelection()*1.0f)/5f;
				voteIconLabel.setImage(TreeIcons.getIcons().getImage(vote));
			}			
		});
		voteButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				addVote(voteWidget.getSelection());
			}
		});
		Label labelVoters = new Label(voteBarComp, SWT.NULL);
		labelVoters.setText(GetText.gettext("VISITED_BY")+": ");
		textVisitors = new StyledText(voteBarComp, SWT.READ_ONLY);
		textVisitors.setLayoutData(createGridData());
		textVisitors.setBackground(Colors.getWidgetBackground());
		
		//Browser
		GridData browserGridData = new GridData();
		browserGridData.grabExcessHorizontalSpace=true;
		browserGridData.horizontalAlignment = GridData.FILL;
		browserGridData.grabExcessVerticalSpace= true;
		browserGridData.verticalAlignment = GridData.FILL;
		
		
		browserComp = new Composite(parent, SWT.NULL);
		browserComp.setLayout(MyLayoutFactory.createGrid(1, false));
		browserComp.setLayoutData(browserGridData);
		
		browser = new Browser(browserComp, SWT.FILL);
		google.setBrowser(this);
		//browser.setLayout(new GridLayout());
		browser.setLayoutData(browserGridData);
		if(menu != null) {
			menu.setBrowser(browser);
			menu.setBrowserComposite(browserComp);
		}
		// Statuszeile
		/*
		 * Trennung von Progressbar und Statuszeile des Browsers wegen Lesbarkeit
		 * Browserstatus direkt unterhalnb des Browsers
		 */				
		Composite compositeStatus = new Composite(parent, SWT.NULL);
		compositeStatus.setLayoutData(createGridData());
		//compositeStatus.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * ProgressBar und StatusBar nebeneinander nehmen weniger Platz ein
		 */
		compositeStatus.setLayout(MyLayoutFactory.createGrid(2, false));
		
		labelStatus = new Label(compositeStatus, SWT.LEFT);
		labelStatus.setText(GetText.gettext("READY"));
		labelStatus.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		final ProgressBar progressBar =
			new ProgressBar(compositeStatus, SWT.RIGHT);
		progressBar.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
		

		
		Listener openURLListener = new Listener() {
			public void handleEvent(Event event) {
				Project project = Model.getModel().getOpenProject();
				if ( project != null ) {
					project.jumpToURL(textLocation.getText());
				}
				send = false;
				titleset = false;
				browser.setUrl(textLocation.getText());
			}
		};


		buttonGo.addListener(SWT.Selection, openURLListener);
		textLocation.addListener(SWT.DefaultSelection, openURLListener);
		
		// Listener zur erkennung des Sprungs auf eine neuw Webseite
		browser.addLocationListener(new LocationListener() {
			public void changing(LocationEvent event) {
				// Zeigt die neue Adresse im Testfeld.
				textLocation.setText(event.location);
				//textLocation.setText(browser.getUrl());
				//actualURL=browser.getUrl();
			}
			public void changed(LocationEvent event) {
				if (browser.isBackEnabled()==true) {
					menu.setBackEnabled(true);
				}
				else {
					menu.setBackEnabled(false);
				}
				if (browser.isForwardEnabled()==true) {
					menu.setForwardEnabled(true);
				}
				else {
					menu.setForwardEnabled(false);
				}
				//tracer.addNode(Model.getModel().getUser().getNickname(), event.location);
				// Implementation der Vor/Zürück-Buttons in der ToolBar.
				Project project = Model.getModel().getOpenProject();
				if ( project != null ) {
					project.setPreviousURL(project.getCurrentURL());
					project.setCurrentURL(browser.getUrl());
				}
				if ( titleset ) {
					//tracer.addNode(Model.getModel().getUser().getNickname(), project.getUrlTitle(), project.getCurrentURL(), project.getPreviousURL());
					lastURL = event.location;
					textLocation.setText(lastURL);
					project.visiting();
					replaceVisitors();
				} else
					send = true;
			}
		});
		
		
		// definieren der Listener
		

		browser.addProgressListener(new ProgressListener() {
			public void changed(ProgressEvent event) {
				progressBar.setMaximum(event.total);
				progressBar.setSelection(event.current);
			}

			public void completed(ProgressEvent event) {
				progressBar.setSelection(0);
			}
		});

		browser.addStatusTextListener(new StatusTextListener() {
			public void changed(StatusTextEvent event) {
				labelStatus.setText(event.text);
			}
		});

		browser.addTitleListener(new TitleListener() {
			public void changed(TitleEvent event) {
				actualTitle=event.title;				
				shell.setText(actualTitle + " - " + GetText.gettext("PRESENTED_BY_POSEMUCKEL"));
				Project project = Model.getModel().getOpenProject();
				
				if ( project != null ) {
					project.setUrlTitle(actualTitle);
				}
				// test des Tracers
				if ( send ) {
					project.visiting();
					replaceVisitors();
				} else
					titleset = true;
			}
		});
		
		addWebTraceListener();
		
		
		
		
		//verhindern, dass ein neuese Browserfenster geöffnet wird
		/**
		browser.addOpenWindowListener(new OpenWindowListener() {
			public void open(WindowEvent event) {
				if (event.addressBar || event.menuBar || event.toolBar) {
					System.out.println(event.data.toString());
				}
				event.browser = browser;
			}
		});

		
		browser.addVisibilityWindowListener(new VisibilityWindowListener() {
			public void hide(WindowEvent event) {
				Browser testBrowser = (Browser)event.widget;
				Shell testShell = testBrowser.getShell();
				testShell.setVisible(false);
			}
			public void show(WindowEvent event) {
				Browser testBrowser = (Browser)event.widget;
				final Shell testShell = testBrowser.getShell();
		
				if (!event.addressBar && !event.menuBar && !event.statusBar && !event.toolBar) {
					System.out.println("Popup blocked.");
					event.display.asyncExec(new Runnable() {
						public void run() {
							testShell.close();
						}
					});	
					return;
				}
				browser.setUrl(testBrowser.getUrl());
				textLocation.setText(testBrowser.getUrl());
				testShell.close();
			}
		}); 
		**/
		
		initialize(Display.getCurrent(), browser);

	}
	
	/**
	 * Fuegt das Menu Hinzu
	 * @param menu
	 */
	void setMenu(BrowserMenu menu) {
		this.menu = menu;
		menu.setBrowser(browser);
		menu.setBrowserComposite(browserComp);
	}
	
	/**
	 * Erzeugt das Grid zur Darstellung der Vote-Bar
	 * @param grabHorizontal
	 * @return GridData
	 */
	private GridData createVoteBarData(boolean grabHorizontal) {
		GridData voteBarGridData = new GridData();
		voteBarGridData.grabExcessHorizontalSpace=grabHorizontal;
		voteBarGridData.horizontalAlignment = SWT.FILL;
		voteBarGridData.grabExcessVerticalSpace=false;
		voteBarGridData.verticalAlignment=SWT.CENTER;
		return voteBarGridData;
	}
	
	/**
	 * Erzeugt das allgemeine Grid
	 * @return GridData
	 */
	private GridData createGridData() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace=true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment= GridData.FILL;
		return gridData;
	}
	
	/**
	 * Der Browser wurd die unter "url" angegeben Webseite aufrufen
	 * @param url
	 */
	public void setURL(String url) {
		if(url != null) {
			Model.getModel().getOpenProject().jumpToURL(url);
			//System.out.println("set url " + url);
			browser.setUrl(url);
		}
	}

	
	/**
	 * Überträgt die abgegebene Bewertung
	 * @param vote: Wert der Bewertung
	 */
	void addVote(int vote) {
		//die aktuelle Webseite holen
		Project project =Model.getModel().getOpenProject(); 
		String url = project.getCurrentURL();
		if(url != null && (!url.equals(""))) {
			Webpage page = project.getWebtrace().getPageForUrl(url);
			if(page != null)page.vote(vote);
		}
	}
	
	
	/**
	 * Fügt den Namen eines Besuchers hinzu.
	 * @param name
	 */
	public void _addPreviousVotes(String name) {
		//TODO entfernen
		//alreadyVotedCombo.add(name);
	}
	
	/**
	 * Ersetzt die Liste der Besucher durch die Liste der Besucher, die unter
	 * der aktuellen URL gespeichert sind
	 *
	 */
	public void replaceVisitors() {
		String url = Model.getModel().getOpenProject().getCurrentURL();
		Webpage page = Model.getModel().getOpenProject().getWebtrace().getPageForUrl(url);
		if(page == null) return;
		Visitor[] visitors = page.getVisitors();
		if(!textVisitors.isDisposed()) {
			textVisitors.setText("");
		} else {
			return;
		}
		int endOfText = 0;
		if(visitors != null && visitors.length != 0) {
			//alreadyVotedCombo.removeAll();
			for (Visitor visitor : visitors) {
				String text = null;
				if(visitor.getRating() <= 0) {
					//addPreviousVotes(visitor.getName());
					text = visitor.getName() + "  ";
				} else {
					//addPreviousVotes(visitor.getName(), visitor.getRating());
					text = visitor.getName() + " (" + visitor.getRatingAsString() + ") ";
				}
				textVisitors.append(text);
				//ganz normaler Text
				StyleRange style = new StyleRange(endOfText, text.length(),
						Colors.getBlack(), Colors.getWidgetBackground());
				Person person =Model.getModel().getAllPersons().getMember(visitor.getName()); 
				if(url.equals(person.getURL())) {
					//die Person sieht sich gerade die Seite an
					style.fontStyle = SWT.BOLD;
				}
				textVisitors.setStyleRange(style);
				endOfText += text.length();
			}
			//alreadyVotedCombo.select(0);
		}
	}
	
	/**
	 * Fuegt den Listener zur Bestueckung des Webtracers hinzu
	 *
	 */
	private void addWebTraceListener() {
		//aktualisiert die Liste der abgegebenen Bewertungen
		Model.getModel().getOpenProject().getWebtrace().addListener(new WebTraceAdapter() {

			public void visiting(WebTraceEvent event) {
				follow(event.getURL(), event.getUser());
				updateVisitorLabel(event.getURL());
			}
			
			private void updateVisitorLabel(String eventURL) {
				String url = Model.getModel().getOpenProject().getCurrentURL();
				if(eventURL.equals(url)) {
					
					Runnable run = new Runnable() {
						public void run() {
							if(!browser.isDisposed())replaceVisitors();
						}
						
					};
					if(Display.getDefault().isDisposed()) {
						Model.getModel().getOpenProject().getWebtrace().removeListener(this);
					} else {
						Display.getDefault().asyncExec(run);
					}
				}
			}
			
			private void follow(final String url, final String name) {
				Runnable run = new Runnable() {
					public void run() {
						if(followMe.isFollowing(name) && !browser.isDisposed()) {
							if(!url.equals(Model.getModel().getUser().getURL()))
								browser.setUrl(url);
						}
					}					
				};
				if(Display.getDefault().isDisposed()) {
					Model.getModel().getOpenProject().getWebtrace().removeListener(this);
				} else {
					Display.getDefault().asyncExec(run);
				}
			}
			
			public void elementChanged(WebTraceEvent event) {
				updateVisitorLabel(event.getURL());
			}

			public void viewing(WebTraceEvent event) {
				//TODO falls der aktuellen URL entsprechend, anzeigen
				follow(event.getRoot().getName(), event.getUser());
				updateVisitorLabel(event.getURL());
			}
			
		});
	}
		
	/**
	 * Initialisiert den Browser
	 * @param display
	 * @param brow
	 */
	static void initialize(final Display display, final Browser brow) {
		brow.addOpenWindowListener(new OpenWindowListener() {
			public void open(WindowEvent event) {
				/*
				 * einen Dummy erstellen: es wird kein IEFenster geöffnet, und die 
				 * Shell kann geschlossen werden, bevor irgendetwas nach draußen dringt
				 */
				Shell shell = new Shell(display);
				shell.setText(GetText.gettext("NEW_WINDOW"));
				shell.setLayout(new FillLayout());
				Browser browser = new Browser(shell, SWT.NONE);
				initialize(display, browser);
				event.browser = browser;
			}
		});
		brow.addVisibilityWindowListener(new VisibilityWindowListener() {
			public void hide(WindowEvent event) {
				Browser browser = (Browser)event.widget;
				Shell shell = browser.getShell();
				shell.setVisible(false);
			}
			public void show(WindowEvent event) {
				final Browser b = (Browser)event.widget;
				final Shell shell = b.getShell();
				/* popup blocker - ignore windows with no style */
				if (!event.addressBar && !event.menuBar && !event.statusBar && !event.toolBar) {
					System.out.println("Popup blocked.");
				} else {
					final String url = lastURL;
					event.display.asyncExec(new Runnable() {
						public void run() {
							browser.setUrl(url);
							textLocation.setText(url);
							
						}
					});
				}
				/*
				 * der dummy muss auf alle Fälle entsorgt werden
				 */
				event.display.asyncExec(new Runnable() {
					public void run() {
						if ( shell != browser.getShell() )
							shell.close();
					}
				});
			}
		});
		//merkt sich die URL, auf die der Anwender geklickt hat
		brow.addStatusTextListener(new StatusTextListener() {
			public void changed(StatusTextEvent event) {
				String url = event.text;
				//die URL liegt direkt unter dem Mauszeiger
				if(url.startsWith("http://")){
					lastURL = url;
				}			
			}		
		});
	}

	/**
	 * Listener zum Erkennen wenn der Browser auf eine neue URL springt
	 *
	 */
	private void addListener() {
		
		/**
		 * Dieser Listener hört auf das Setzen einer neuen URL
		 */
		final Project proj = Model.getModel().getOpenProject();
		if (proj == null)
			return;
		
		NotifyListener adapter = new NotifyListener() {

			public void notify(final NotifyEvent event) {}

			public void ack() {}

			public void newurl(final String url) {
				Runnable run = new Runnable() {
					public void run() {
						if(!shell.isDisposed()) setURL(url);
					}
				};				
				if(shell.isDisposed()) {
					proj.removeListener(this);
				} else {
					Display.getDefault().asyncExec(run);
				}
				
			}
			
		};
		proj.addListener(adapter);
	}

	/**
	 * Versetzt den Browser in den "Follow-Me"-Modus
	 * @param name
	 * @param active
	 */
	public void activateFollowMe(String name) {
			//setzt den Browser auf die URL des Anwenders, dem gefolgt wird
			String url = Model.getModel().getUser().getURL();
			String otherURL = Model.getModel().getAllPersons().getMember(name).getURL();
			if(url != null && !url.equals(otherURL)) {
				setURL(otherURL);
			}
	}
	
}
