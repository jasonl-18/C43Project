package users;

public class User {
	private static User instance;
	private String sin;
	
	public User(String sin) {
		this.sin = sin;
	}
	public static User getInstance() {
		return instance;
	}
	public static void setInstance(User u) {
		instance = u;
	}
	
	public String getSin() {
		return this.sin;
	}
}
