/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

/**
 * F�hrt f�r die Verwaltung der BuddyListe relevante Operationen auf der Datenbank
 * aus. Als Operationen stehen zur Verf�gung:
 * 
 * <ul>
 * <li>hinzuf�gen</li>
 * <li>entfernen</li>
 * <li>die Liste laden</li>
 * </ul>
 * 
 * Das Hinzuf�gen bzw. das Entfernen der Buddys wird von der Database im Erfolgsfalle
 * mit ACK best�tigt.
 * 
 * @author Posemuckel Team
 *
 */
class BuddyTask extends MemberTask {
	
	static final int ADD = 100;
	static final int DELETE = 200;
	static final int LOAD = 300;
	
	private String buddy;
	private int task;
	
	/**
	 * Diese BuddyTask kann die Liste der Buddys laden
	 * @param list , die die Antwort erh�lt
	 */
	BuddyTask(MemberList list) {
		super(list);
	}
	
	/**
	 * Diese BuddyTask kann Buddys hinzuf�gen und l�schen
	 * @param list , die die Antwort erh�lt
	 * @param buddy der hinzugef�gt oder gel�scht werden soll
	 */
	BuddyTask(MemberList list, String buddy) {
		super(list);
		this.buddy = buddy;
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.Task#work(int)
	 */
	@Override
	protected void work(int task) {
		this.task = task;
		switch (task) {
		case ADD:
			DatabaseFactory.getRegistry().addBuddy(buddy, this);
			break;
		case DELETE:
			DatabaseFactory.getRegistry().deleteBuddy(buddy, this);
			break;
		case LOAD:
			DatabaseFactory.getRegistry().getBuddys(this);
		default:
			break;
		}

	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(int)
	 */
	@Override
	public void update(int answer) {
		switch (task + answer) {
		case ADD + Database.ERROR:
			getList().error(ADD);
			break;
		case DELETE + Database.ACK:
			getList().confirmDelMember(buddy);
		case DELETE + Database.ERROR:
			getList().error(DELETE);
			break;
		case LOAD + Database.ERROR:
			getList().error(LOAD);
			break;
		default:
			super.update(answer);
		}
	}
	
	/**
	 *  
	 * Erwartet in der ArrayList eine Lister der Buddys oder den Status 
	 * des neuen Buddys
	 * @see posemuckel.client.model.Task#update(java.util.ArrayList)
	 */
	@SuppressWarnings("unchecked")
	public void update(ArrayList answer) {
		switch (task) {
		case LOAD:
			getList().confirmLoad(answer);
		default:
			break;
		}
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(java.lang.String)
	 */
	@Override
	public void update(String message) {
		if (task == ADD) {
			getList().confirmAddMember(new Person(buddy, message.toUpperCase()));
		}
	}

}
