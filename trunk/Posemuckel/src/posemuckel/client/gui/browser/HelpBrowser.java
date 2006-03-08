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

/**
 * Dies ist ein Hilfe-Browser. Er hat keine Adresszeile zur Eingabe einer
 * URL und (noch) keine Vorwärts-/Rückwärts-Buttons.
 * @author Posemuckel Team
 */
public class HelpBrowser {
	
	private Browser browser;
	private Display display;
	private int counter = 0;
	
	public HelpBrowser(final String url, final String title) {
		display = Display.getDefault();
		initBrowser(url, title);
	}
	
	public HelpBrowser(final Composite parent, final String url,
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
	    
		browser.addLocationListener(new LocationListener() {
			public void changing(LocationEvent event) {
			}
			public void changed(LocationEvent event) {
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


}
