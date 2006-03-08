package posemuckel.client.model.test;

import junit.framework.TestCase;
import posemuckel.client.model.MemberList;
import posemuckel.client.model.Model;
import posemuckel.client.model.ProjectList;

/**
 * Testet die Verfügbarkeit von Buddyliste und den Projektlisten
 * für einen Benutzer, der nicht eingeloggt ist.
 * 
 * @author Posemuckel Team
 *
 */
public class TestNoLogin extends TestCase {
	
	private Model model;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		model = new Model();
	}
	
	/**
	 * Auf die Buddyliste kann zugegriffen werden, wenn der Anwender
	 * nicht eingeloggt ist. Allerdings ist sie dann leer.
	 *
	 */
	public void testEmptyBuddyList() {
		MemberList buddys = model.getUser().getBuddyList();
		assertTrue(buddys.isEmpty());
		assertEquals(MemberList.BUDDY_TYPE, buddys.getType());
	}
	
	/**
	 * Auf die Projektlisten kann zugegriffen werden, wenn der Anwender
	 * nicht eingeloggt ist. Allerdings sind sie dann leer.
	 *
	 */
	public void testEmptyProjectLists() {
		ProjectList projects = model.getAllProjects();
		assertTrue(projects.isEmpty());
		assertEquals(ProjectList.ALL_PROJECTS, projects.getType());
		projects = model.getUser().getInvitations();
		assertTrue(projects.isEmpty());
		assertEquals(ProjectList.OPEN_INVITATIONS, projects.getType());
		projects = model.getUser().getProjects();
		assertTrue(projects.isEmpty());
		assertEquals(ProjectList.MY_PROJECTS, projects.getType());
	}
	
	

}
