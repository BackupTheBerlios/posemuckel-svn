package posemuckel.client.model.test;

import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.client.model.ProjectComparator;
import junit.framework.TestCase;

/**
 * Testet den Vergleich von Projekten. Als Vergleichskriterium können
 * 
 * <ul>
 * <li>Project.TOPIC</li>
 * <li>Project.OWNER</li>
 * <li>Project.TYPE</li>
 * <li>Project.DATE</li>
 * <li>Project.FREE</li>
 * <li>Project.NO</li>
 * </ul>
 * 
 * dienen.
 * @author Posemuckel Team
 *
 */
public class ProjectComparatorTest extends TestCase {
	
	private Project one;
	private Project two;
	private ProjectComparator comp;
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		Model model = new Model();
		one = new Project(model);
		two = new Project(model);
		//öffentliches Projekt
		one.setData("2", "Topic", "Owner", "1", "3", "6", "no desc", "2006 10 2");
		//privates Projekt
		two.setData("1", "unknown", "jens", "0", "1", "5", "no desc", "2005 10 2");
	}
	
	/**
	 * Testet den Vergleich von Projekten nach dem Kriterium Project.TOPIC.
	 * Es wird die lexikalische Ordnung ohne Beachtung
	 * von Groß-und Kleinschreibung verwendet.
	 */
	public void testTopic() {
		comp = new ProjectComparator(Project.TOPIC);
		assertTrue(comp.compare(one, two) < 0);
		assertTrue(comp.compare(one, one) == 0);
		assertTrue(comp.compare(two, one) > 0);
	}
	
	/**
	 * Testet den Vergleich von Projekten nach dem Kriterium Project.OWNER.
	 * Es wird die lexikalische Ordnung ohne Beachtung
	 * von Groß-und Kleinschreibung verwendet.
	 */
	public void testOwner() {
		comp = new ProjectComparator(Project.OWNER);
		assertTrue(comp.compare(two, one) < 0);
		assertTrue(comp.compare(one, one) == 0);
		assertTrue(comp.compare(one, two) > 0);
	}
	
	/**
	 * Testet den Vergleich von Projekten nach dem Kriterium Project.TYPE.
	 * Öffentliche Projekte werden vor privaten Projekten angeordnet.
	 *
	 */
	public void testType() {
		comp = new ProjectComparator(Project.TYPE);
		assertTrue(comp.compare(one, two) < 0);
		assertTrue(comp.compare(one, one) == 0);
		assertTrue(comp.compare(two, one) > 0);
	}
	
	/**
	 * Testet den Vergleich von Projekten nach dem Kriterium Project.DATE.
	 * Das Datum wird in aufsteigender Reihenfolge angeordnet.
	 */
	public void testDate() {
		comp = new ProjectComparator(Project.OWNER);
		assertTrue(comp.compare(two, one) < 0);
		assertTrue(comp.compare(one, one) == 0);
		assertTrue(comp.compare(one, two) > 0);
	}
	
	/**
	 * Testet den Vergleich von Projekten nach dem Kriterium Project.FREE.
	 * Die freien Plätze werden in absteigender Folge angeordnet.
	 */
	public void testFreePlaces() {
		comp = new ProjectComparator(Project.FREE);
		assertTrue(comp.compare(one, two) < 0);
		assertTrue(comp.compare(one, one) == 0);
		assertTrue(comp.compare(two, one) > 0);
	}
	
	/**
	 * Testet den Vergleich von Projekten nach dem Kriterium Project.NO.
	 * Die Zahl der Plätze wird in aufsteigender Folge angeordnet.
	 *
	 */
	public void testPlaces() {
		comp = new ProjectComparator(Project.NO);
		assertTrue(comp.compare(two, one) < 0);
		assertTrue(comp.compare(one, one) == 0);
		assertTrue(comp.compare(one, two) > 0);
	}

}
