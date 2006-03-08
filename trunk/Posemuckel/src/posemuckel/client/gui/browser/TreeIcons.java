/**
 * 
 */
package posemuckel.client.gui.browser;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import posemuckel.client.gui.Colors;

/**
 * Übernimmt die Verwaltung der TreeIcons incl. der Entsorgung beim Schließen
 * des Fensters. Der Farbverlauf der Icons ist von rot über gelb zu grün. Wenn nach
 * einem 'rating' als Parameter gefragt wird, so ist der Prozentsatz als
 * Wert im Intervall 0 bis 1 anzugeben.
 * 
 * @author Posemuckel Team
 *
 */

public class TreeIcons {

//	zur Zeit liegt bei Key -1f der graue Kreis
	private HashMap<String,Image> images;
	
	private static int imageWidth = 16;
	
	private static TreeIcons icons;
	
	//gibt an, ob die Farbe entsorgt werden muss
	private boolean disposeColor;
	
	public static void disposeIcons() {
		if(icons != null) {
			icons.dispose();
		}
		icons = null;
	}
	
	public static TreeIcons getIcons() {
		if(icons == null) icons = new TreeIcons();
		return icons;
	}
	
	public TreeIcons() {
		images = new HashMap<String, Image>();
		images.put(String.valueOf(-1f),getCircle(getColor(-1), false));
	}
	
	public Image getImage(float rating) {
			//den Schlüssel berechnen
			String key = getKey(rating);
			if(!images.containsKey(key)) {
				//das Image existiert noch nicht
				Color color = getColor(rating);
				Image image = getCircle(color, false);
				if(disposeColor) color.dispose();
				images.put(key, image);
				return image;
			} else {
				//image aus der HashMap holen
				return images.get(key);
			}
	}
	
	/**
	 * Gibt einen Kreis mit einem Durchmesser von 12px und einem Char in der Mitte
	 * aus. Über den Balance-Wert kann gesteuert werden, wie weit der Char von der
	 * Mitte weg gezeichnet werden soll. Das Image wird in einer HashMap gepuffert,
	 * wobei als Schlüssel das Rating und der Char verwendet wird. Der Balance-Wert
	 * kann nach dem ersten Zeichnen also nicht mehr verändert werden.
	 * @param rating
	 * @param c
	 * @param balance
	 * @return Liefert ein Image-Objekt mit dem entsprechenden Bild.
	 */
	public Image getImage(float rating, char c, int balance) {
		//den Schlüssel berechnen
		//TODO den Sonderfall grau behandeln
		String key = String.valueOf(c);
		key += getKey(rating);
		if(!images.containsKey(key)) {
			//das Image existiert noch nicht
			Image image = getCircle(rating, c, balance);
			images.put(key, image);
			return image;
		} else {
			//image aus der HashMap holen
			return images.get(key);
		}		
	}
	
	public Image getImageWithBackground(float rating) {
		String key = String.valueOf(rating) + "back";
		if(!images.containsKey(key)) {
			//das Image existiert noch nicht
			Color color = getColor(rating);
			Image image = getCircle(color, true);
			if(disposeColor) color.dispose();
			images.put(key, image);
			return image;
		} else {
			//image aus der HashMap holen
			return images.get(key);
		}		
	}
	
	public Image getMarkedCircle(float rating) {
		return getImage(rating, '!', 1);
	}
	
	public void dispose() {
		Collection<Image> collection = images.values();
		for (Image image : collection) {
			image.dispose();
		}
	}
	
	/**
	 * Dies ist eine spezielle Methode zum Setzen von Antialiasing.
	 * Hier wird der Fall abgefangen, dass der Aufruf von
	 * gc.setAntialias(SWT.ON);
	 * unter Linux Probleme bereitet. Unter Linux wird diese
	 * Methode von gc daher nicht aufgerufen.
	 * @param gc
	 * @author Posemuckel Team
	 */
	private static void setAntialiasing(GC gc) {
		if(!System.getProperty("os.name").equals("Linux"))
			gc.setAntialias(SWT.ON);
	}
	
	/*
	 * berechnet aus dem Rating den String-Schlüssel. der Sonderfall grauer Kreis
	 * wird mit abgehandelt.
	 */
	private String getKey(float rating) {
		if(rating < 0) {
			rating = -1f;
		} 
		return String.valueOf(rating);
	}
	
	/*
	 * Zeichnet einen Kreis mit einem Char.
	 */
	private Image getCircle(float percentage, char c, int balance) {
		Color color = getColor(percentage);
		Image circle = getCircle(color, false);
		if(disposeColor) color.dispose();
		circle = drawChar(circle, c, balance);
		return circle;
	}
		
	/*
	 * Zeichnet einen Char auf das Image. Die Hintergrundfarbe sollte die
	 * Farbe des Kreises sein, da sie zum Unterlegen des Chars verwendet wird.
	 */
	private Image drawChar(Image image, char c, int balance) {
		GC gc = new GC(image);
		setAntialiasing(gc);
		//wie setze ich die Farbe?
		int a = imageWidth;
		a = a/2 - balance;
		//die Farbe für das Ausrufezeichen
		gc.setForeground(Colors.getBlack());
		gc.setFont(new Font(Display.getCurrent(), new FontData[]{new FontData("arial", 7, SWT.BOLD)}));
		gc.drawString(String.valueOf(c), a, 3, true);
		gc.dispose();
		return image;
	}
	
	/**
	 * Berechnet aus den Prozentwerten die Farbe
	 * @param percentage Prozentwert zwischen 0 und 1
	 * @return die entsprechende Farbe aus dem Bereich rot-grün
	 */
	private Color getColor(float percentage) {
		if(percentage < 0) return Colors.getGrey();
		/*
		 * die Farben werden so umgesetzt, dass gilt
		 * percentage red  green blue
		 * 0f		  255  0     0		(rot)
		 * 0.5f 	  255  255   0		(gelb)
		 * 1f		  0    255   0      (grün)
		 */
		int red = (int)(510 * (1-percentage));
		if(red > 255) red = 255;
		int green = (int)(510 * percentage);
		if(green > 255) green = 255;
		int blue = 0;
		disposeColor = true;
		return new Color(Display.getCurrent(),red, green, blue);
	}
	
	/*
	 * es wird der Kreis gezeichnet
	 */
	private Image getCircle(Color rgb, boolean yellowBack) {
		Image image = new Image(Display.getCurrent(), new Rectangle(0, 0, imageWidth, imageWidth));
		GC gc = new GC(image);
		if(yellowBack) {
			gc.setBackground(Colors.getYellow());
			gc.fillRectangle(0, 0, imageWidth, imageWidth);
		}
		//die Hintergrundfarbe wird zum Zeichnen verwendet
		gc.setBackground(rgb);
		setAntialiasing(gc);
		int a = imageWidth - 5;
		gc.fillOval(2, 2, a, a);
		//Schwarzen Rand zeichnen
		gc.setBackground(Colors.getBlack());
		gc.drawOval(2, 2, a, a);
		gc.dispose();
		return image;	
	}	
	
	/*
	 * hier sind ein paar alte Ideen gelagert
	 */
	
//	public static Image getSquare(int red, int green, int blue) {
//	ImageData data = new ImageData(imageWidth, imageWidth,
//            1, new PaletteData(new RGB[] { new RGB(red, green, blue) }));
////	new Image(Display.getCurrent(), new Rectangle(0, 0, 10, 10));
//	Image image = new Image(Display.getCurrent(), data);
//	return image;		
//}

}
