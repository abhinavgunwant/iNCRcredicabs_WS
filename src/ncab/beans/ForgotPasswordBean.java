package ncab.beans;

/**
 *  TODO: Replace this class with UserCredBean instead,
 *  	since the name 'UserCredBean' solves the purpose
 *  	for forgot password, set password and login beans
 *  	and no separate beans shall be required for this....
 *  
 * @author ag250497
 *
 */

public class ForgotPasswordBean {
	private String qlid;

	public String getQlid() {
		return qlid;
	}

	public void setQlid(String qlid) {
		this.qlid = qlid;
	}

}
