/**
 * 
 */
package posemuckel.client.gui;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import posemuckel.client.model.Person;

/**
 * MyTab enthält ein TabItem, welches mit einem einfachen Blinkermechanismus
 * ausgestattet ist. Das Tab sollte kein Image besitzen, da dieses beim Blinken
 * gelöscht wird.
 * 
 * @author Posemuckel Team
 *
 */
public class MyTab {
	
	private TabItem tab;
	private TabFolder folder;
	//wird nur aus dem GUI-thread heraus maninipuliert
	private boolean blinking;
	private int count;
	private ImageRegistry imageRegistry;
	
	protected TabItem getTab(TabFolder parent, int style) {
		if(tab == null) {
			tab = new TabItem(parent, style);
			folder = parent;
			imageRegistry = new ImageRegistry();
			imageRegistry.put(Person.ONLINE, ImageDescriptor.createFromFile(
					this.getClass(),
					"icons/" + "online.bmp")
			);
		}
		return tab;
	}
	
	private boolean isDisposed() {
		return tab.isDisposed();
	}
	
	protected boolean isSelected() {
		TabItem[] selection = folder.getSelection();
		for (TabItem item : selection) {
			if(item == tab) return true;
		}
		return false;
	}
	
	protected void startBlinking() {
		if(!blinking) {
			blinking = true;
			count = 0;
			//Logger.getLogger(MyTab.class).debug("start blinking");
			blink();
			timeBlinking();
		}
	}
	
	private boolean again() {
		//es wurde schon zweimal geblinkt, wenn das aufgerufen wird
		//Logger.getLogger(MyTab.class).debug("blink again (count: "+ count);
		return count < 10 && count > 0;
	}
	
	private void timeBlinking() {
		if(!isDisposed()) {
			tab.getControl().getDisplay().timerExec(500, new Runnable() {
				public void run() {
					blink();
					if(again()) {
						timeBlinking();
					} else {
						//jetzt erst darf der nächste
						blinking = false;
					}
				}					
			});
		}
	}
	
	private void blink() {		
		//in der Wartezeit kann das Tab entsorgt worden sein
		if(!isDisposed()) {
			int even = count%2;
			if(even == 0) {
				tab.setImage(imageRegistry.get(Person.ONLINE));
			} else {
				tab.setImage(null);
			}
			count++;
		} else {
			//again liefert false
			count = -1;
			Logger.getLogger(MyTab.class).debug("Chat was disposed: no blinking");
		}		
	}
	
	


	
	

}
