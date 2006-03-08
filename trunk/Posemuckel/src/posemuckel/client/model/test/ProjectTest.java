package posemuckel.client.model.test;

import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.client.model.ProjectList;
import junit.framework.TestCase;

/**
 * Testet das Starten und das Laden von Projekten. Es wird Mockbase
 * verwendet, um die Kommunikation zwischen Client und Database zu entwickeln.
 * 
 * @author Posemuckel Team
 *
 */
public class ProjectTest extends TestCase {
	
	private Project p1;
	private Mockbase registry;
	private Model model;
	private ProjectList projects;

	protected void setUp() throws Exception {
		DatabaseFactory.createRegistry(DatabaseFactory.USE_MOCKBASE);
		model = new Model();
		registry = (Mockbase)DatabaseFactory.getRegistry();
		p1 = new Project(model);
		p1.setData("1", "a topic", "niko", Project.PUBLIC_TYPE, "2", "6", "a description", "2005-12-29");
		projects = model.getAllProjects();
	}
	
	/**
	 * Testet die Daten des Projektes, das von Mockbase geladen wurde.
	 *
	 */
	public void testProjectInit() {
		assertEquals("1", p1.getID());
		assertEquals("a topic", p1.getTopic());
		assertEquals("a description", p1.getDescription());
		assertTrue(p1.isPublic());
	}
	
	/**
	 * Wenn ein Projekt in Mockbase eingefügt wird, sollte es dort auch gespeichert
	 * werden.
	 *
	 */
	public void testAddToMockbase() {
		assertFalse(registry.hasProjects());
		registry.addProject(p1);
		assertTrue(registry.hasProjects());
	}
	
	/**
	 * Wenn der Projektliste mitgeteilt wird, dass sie ein neues Projekt enthält,
	 * dann sollte die Liste das Projekt auch speichern.
	 *
	 */
	public void testProjectList() {
		assertTrue(projects.isEmpty());
		projects.confirmAddProject(p1);
		assertFalse(projects.isEmpty());
	}
	
	/**
	 * Wenn ein neues Projekt gestartet wird, ist es anschließend in Mockbase
	 * wiederzufinden.
	 *
	 */
	public void testStartProject() {
		Project p2 = new Project(model);
		p2.setData("2", "a topic", "wiede", Project.PUBLIC_TYPE, "3", "4", "a description", "2005-12-29");
		assertTrue(model.getAllProjects().isEmpty());
		assertFalse(registry.hasProjects());
		projects.startProject(p2);
		assertTrue(registry.hasProjects());
	}
}
