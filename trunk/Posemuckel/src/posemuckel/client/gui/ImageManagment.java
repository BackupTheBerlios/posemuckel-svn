/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

/**
 * Verwaltet die Images, die selbst erzeugt werden. Die Images werden in 
 * einer statischen ImageRegistry verwaltet, die nach dem Erzeugen des 
 * ersten Displays initialisiert wird. Die Images werden entsorgt (dispose),
 * sobald das Display entsorgt wird.
 * @author Tanja Buttler
 *
 */
public class ImageManagment {
	
	public static final String iconPath = "/icons";
	public static final String SHELL_ICON = "shell_icon";
	
	private static ImageRegistry registry;
	
	/**
	 * Gibt die ImageRegistry für die Applikation aus. Es muss ein Display
	 * erzeugt worden sein, bevor diese Methode aufgerufen werden kann.
	 * 
	 * @return ImageRegistry für die Applikation
	 */
	public static ImageRegistry getRegistry() {
		if(registry == null) {
			initRegistry();
		} 
		return registry;
	}
	
	/**
	 * Initialisiert die ImageRegistry und legt die wichtigsten Bilder in 
	 * ihr ab.
	 *
	 */
	private static void initRegistry() {
		registry = new ImageRegistry();
		//hier können die Schlüssel für die verschiedenen Bilder abgelegt werden
		registry.put(SHELL_ICON, ImageDescriptor.createFromFile(GUI_Main_Window.class,
			"icons/posemuckel-32x32.ico"));

	}

}
