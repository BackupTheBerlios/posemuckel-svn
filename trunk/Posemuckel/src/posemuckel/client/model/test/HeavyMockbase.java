/**
 * 
 */
package posemuckel.client.model.test;

/**
 * Erm�glicht es, die GUI gegen Threads zu testen: die einzelnen Task
 * werden in einem separaten Thread ausgef�hrt. Die Daten werden
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
		//die Tasks werden im Thread ausgef�hrt
		return true;
	}

}
