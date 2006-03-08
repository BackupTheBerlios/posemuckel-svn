/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.swt.layout.GridLayout;

/**
 * Erstellt ein GridLayout mit sehr kleiner Randbreite. Damit lassen sich die verschiedenen
 * Elemente ineinander schachteln, ohne dass der Rand zu breit wird.
 * 
 * Wenn ein <code>Composite</code> das Layout <code>FillLayout</code> verwendet, 
 * so ist die Randbreite bereits 0.
 * 
 * @author Posemuckel Team
 *
 */
public class MyLayoutFactory {
	
	/**
	 * Erzeugt ein GridLayout, welches als Margin in der Breite den Wert 1 und
	 * in der Höhe den Wert 0 hat.
	 * @param columns die Zahl der Spalten
	 * @param equalWidth sollen die Spalten alle die gleiche Breite haben?
	 * @return GridLayout ohne nennenswerten Rand
	 */
	public static GridLayout createGrid(int columns, boolean equalWidth) {
		return createGrid(columns, equalWidth, 0, 1);
	}
	
	public static GridLayout createGrid(int columns, boolean equalWidth, int margin) {
		return createGrid(columns, equalWidth, margin, margin);
	}
	
	public static GridLayout createGrid(int columns, boolean equalWidth, int heightM, int widthM) {
		GridLayout layout = new GridLayout(columns, equalWidth);
		layout.marginHeight = heightM;
		layout.marginWidth = widthM;
		return layout;
	}
	
}
