package posemuckel.client.gui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import posemuckel.client.gui.ImageManagment;

public class CreateStaticBrowser {
	
	private Browser browser;
	private Display display;
	private int counter = 0;
	private boolean browsingEnabled = false;
	
	public CreateStaticBrowser(final String url, final String title) {
		display = Display.getDefault();
		initBrowser(url, title);
	}
	
	public CreateStaticBrowser(final Composite parent, final String url,
			final String title) {
		display = parent.getDisplay();
		initBrowser(url, title);
	}
	
	private void initBrowser(final String url, final String title) {
		Shell shell = new Shell(display, SWT.TOP);
	    shell.setSize(800,600);
	    shell.setText(title);
	    shell.setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
	    shell.setLayout(new FillLayout());
	    browser = new Browser(shell, SWT.NONE);	    
	    browser.setUrl(url);
	    
//	  	Listener zur Erkennung des Sprungs auf eine neue Webseite
		browser.addLocationListener(new LocationListener() {
			public void changing(LocationEvent event) {
				if ((counter > 0) && (!browsingEnabled)) {
					event.doit = false;
				}
			}
			public void changed(LocationEvent event) {
				if ((counter > 0) && (!browsingEnabled)) {
					event.doit = false;
				}
			}
		});
	     browser.addProgressListener(new ProgressListener() {

			public void changed(ProgressEvent event) {}

			public void completed(ProgressEvent event) {
				counter++;
			}
	    	 
	     });
		//verhindern, dass ein neuese Browserfenster geöffnet wird
		browser.addOpenWindowListener(new OpenWindowListener() {
			public void open(WindowEvent event) {
				event.browser = browser;
			}
		});
		
		shell.open();  
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	}

	/**
	 * Damit kann der statische Browser optional zum Browsen genutzt
	 * werden
	 * @param enabled gibt an, ob das Browsen möglich ist oder nicht
	 */
    public void enableBrowsing(boolean enabled) {
    	browsingEnabled = enabled;
    }

}
