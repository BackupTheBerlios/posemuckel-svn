/**
 * 
 */
package posemuckel.client.model;


/**
 * Nimmt Aufträge vom User entgegen und greift auf die Database zu, um sie
 * auszuführen. 
 * Konkret werden die folgenden Nachrichten (nach RFC0815) bearbeitet:
 * 
 * <ul>
 * <li>LOGIN</li>
 * <li>LOGOUT</li>
 * <li>REGISTER</li>
 * <li>SET_PROFILE</li>
 * </ul>
 * @author Posemuckel Team
 * 
 */
class UserTask extends TaskAdapter {

	static final int LOGIN = 100;

	static final int LOGOUT = 200;

	static final int REGISTER = 300;

	static final int SET_PROFILE = 400;

	private int task;

	private User user;
	
	/**
	 * Erstellt eine neue UserTask. Die Referenz auf den Anwender darf nicht null
	 * sein!
	 * @param user Anwender
	 */
	UserTask(User user) {
		this.user = user;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#work(int)
	 */
	protected void work(final int task) {
		this.task = task;
		switch (task) {
		case LOGIN:
			DatabaseFactory.getRegistry().login(user.getNickname(),
					user.getUserData().getPassword(), this);
			break;
		case LOGOUT:
			DatabaseFactory.getRegistry().logout(user.getNickname(), this);
			break;
		case REGISTER:
			UserData dat = user.getUserData();
			DatabaseFactory.getRegistry().addUser(dat.getFirstName(),
					dat.getPassword(), dat.getSurname(), dat.getNickname(),
					dat.getLang(), dat.getGender(), dat.getEmail(),
					dat.getLocation(), dat.getComment(), this);
			break;
		case SET_PROFILE:
			UserData dat2 = user.getUserData();
			DatabaseFactory.getRegistry().setProfile(dat2.getFirstName(),dat2.getSurname(),
					dat2.getPassword(),dat2.getEmail(),dat2.getLang(),
					dat2.getGender(),dat2.getLocation(),dat2.getComment(),this);	
			break;
		default:
			throw new IllegalArgumentException("unknown task");
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see posemuckel.client.model.Task#update(int)
	 */
	public void update(int answer) {
		int key = task + answer;
		switch (key) {
		case LOGIN + Database.ACCESS_GRANTED:
			user.confirmLogin(true);
			break;
		case LOGIN + Database.ACCESS_DENIED:
			user.confirmLogin(false);
			break;
		case LOGOUT + Database.ACK:
			user.confirmLogout(true);
			break;
		case LOGOUT + Database.ACCESS_DENIED:
			user.fireLogout(false);
			break;
		case REGISTER + Database.ACK:
			user.getUserData().fireRegister(true);
			break;
		case REGISTER + Database.USER_EXITS:
			user.getUserData().userExists();
		case SET_PROFILE + Database.ACK:
			user.fireProfileChanged(true);
			break;
		case SET_PROFILE + Database.ERROR:
			user.fireProfileChanged(false);
			break;
		default:
			break;
		}
	}

	/**
	 * bei fehlschlagendem Login muss das Resultat vom User
	 * behandelt werden, damit er das Passwort und den Nickname neu eingeben
	 * kann</ br>
	 */
	@Override
	public boolean relayACCESS_DENIED() {
		return task == LOGIN;
	}
}
