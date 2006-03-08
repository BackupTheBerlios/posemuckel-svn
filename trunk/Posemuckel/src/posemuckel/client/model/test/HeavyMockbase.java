/**
 * 
 */
package posemuckel.client.model.test;

/**
 * Ermöglicht es, die GUI gegen Threads zu testen: die einzelnen Task
 * werden in einem separaten Thread ausgeführt. Die Daten werden
 * aus Mockbase bezogen.
 * 
 * @author Posemuckel Team
 *
 */
public class HeavyMockbase extends Mockbase {

	/* (non-Javadoc)
	 * @see posemuckel.client.model.test.Mockbase#isHeavyTask()
	 */
	@Override
	public boolean isHeavyTask() {
		//die Tasks werden im Thread ausgeführt
		return true;
	}

}
