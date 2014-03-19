/**
 * 
 */
package it.micheleorsi.model;

/**
 * Represents the triple of information needed to authenticate on a Store
 * 
 * @author micheleorsi
 *
 */
public class StoreAuth {
	private final String id;
	private final String username;
	private final String password;
	private final StoreType type;
	
	public StoreAuth(String id, String username, String password, StoreType type) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public StoreType getType() {
		return type;
	}
}
