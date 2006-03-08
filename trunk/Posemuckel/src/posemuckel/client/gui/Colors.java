/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * Gibt einen Zugriff aus alle Farben, die verwendet werden. Damit lassen sich die
 * verwendeten Farben zentral verwalten. Zum von SWT verwendeten Farbmodel siehe
 * http://www.eclipse.org/articles/Article-SWT-Color-Model/swt-color-model.htm
 * 
 * @author Posemuckel Team
 *
 */
public class Colors {

	
	private static Color darkBlue1;
	private static Color darkViolett1;
	private static Color yellow1;
			
	/**
	 * Initialisiert das Farbschema. Ein Display muss bereits durch das Erstellen 
	 * eines Fensters initialisiert worden sein.
	 *
	 */
	public static void initColors() {
		if(yellow1 == null || yellow1.isDisposed()) {
//			Colors.green = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
//			Colors.darkGrey = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
			/*
			 * diese Farben sollten mit dispose() entsorgt werden
			 */
			Colors.darkBlue1 = new Color(Display.getCurrent(),50, 50, 200);
			Colors.darkViolett1 = new Color(Display.getCurrent(),51, 0, 102);
			Colors.yellow1 = new Color(Display.getCurrent(),255, 190, 0);
		}
	}
			
	/**
	 * Gibt die erzeugten Systemresourcen wieder frei.
	 *
	 */
	public static void dispose() {
		darkBlue1.dispose();
		darkViolett1.dispose();
		yellow1.dispose();
	}
	
	/**
	 * Gibt die Farbe, die als Hintergrund für die SashForms verwendet werden sollen,
	 * aus.
	 * @return Hintergrundfarbe für die SashForms
	 */
	public static Color getSashBackground() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	}
	
	/**
	 * Hintergrundfarbe der Widgets in SWT
	 * @return Hintergrundfarbe der Widgets in SWT
	 */
	public static Color getWidgetBackground() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	}

	/**
	 * Hintergrundfarbe für die Texte im Chat
	 * @return Hintergrundfarbe für die Texte im Chat
	 */
	public static Color getChatTextBackground() {
		return getWhite();
	}

	/**
	 * Farbe, in der die Nachrichten im Chat dargestellt werden sollen
	 * @return Farbe für die Nachrichten im Chat
	 */
	public static Color getChatTextColor() {
		return getBlack();
	}

	/**
	 * Farbe, mit der die Anwendernamen im Chat hinterlegt werden sollen
	 * @return Hintergrundfarbe für die Autoren von Chatnachrichten
	 */
	public static Color getChatUserBackground() {
		return yellow1;
	}
	
	/**
	 * Hintergrundfarbe, mit dem der nicht beschriebene Teil des Zeile, die den
	 * Anwendernamen im Chat darstellt, eingefärbt wird
	 * @return ???
	 */
	public static Color getChatUserLine() {
		return yellow1;
	}

	/**
	 * Farbe, in der die Namen der Autoren von Chatnachrichten geschrieben werden.
	 * @return Farbe, in der die Anwendernamen geschrieben werden
	 */
	public static Color getChatUserColor() {
		return darkViolett1;
	}
	
	/**
	 * @return Gibt info zur&uuml;ck.
	 */
	public static Color getInfo() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	}

	/**
	 * Farbe, in der Fehlermeldungen und Warnungen geschrieben werden können.
	 * @return Warnfarbe
	 */
	public static Color getWarning() {
		return getRed();
	}
	
	/**
	 * Farbe für eine Erfolgsmeldung
	 * @return Farbe für eine Erfolgsmeldung
	 */
	public static Color getSuccess() {
		return darkBlue1;
	}
	
	/**
	 * weiße Farbe
	 * @return weiß
	 */
	public static Color getWhite() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}
	
	/**
	 * Scharze Farbe
	 * @return schwarz
	 */
	public static Color getBlack() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	}
	
	/**
	 * rote Farbe
	 * @return rot
	 */
	public static Color getRed() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	}
	
	/**
	 * hellgraue Farbe
	 * @return grau
	 */
	public static Color getGrey() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	}
	
	/**
	 * blaue Farbe 
	 * @return blau
	 */
	public static Color getBlue() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	}
	
	/**
	 * gelbe Farbe
	 * @return gelb
	 */
	public static Color getYellow() {
		return yellow1;
	}
}
