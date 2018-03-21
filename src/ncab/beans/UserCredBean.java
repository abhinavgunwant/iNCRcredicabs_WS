package ncab.beans;

public class UserCredBean {
	private String qlid;
	private String password;
	private String token;
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the qlid
	 */
	public String getQlid() {
		return qlid;
	}
	/**
	 * @param qlid the qlid to set
	 */
	public void setQlid(String qlid) {
		this.qlid = qlid;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
