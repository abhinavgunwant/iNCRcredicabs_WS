/**
 * Not in use! LoginFormInputBean to be used instead
 * 
 * To be used only as a Base Class....
 */
package ncab.beans;

public class FormInputBean {
	protected String qlid;
	protected String password;
	protected String grecaptchaResponse;		//// google reCaptcha Response string
	
	public void setQlid(String qlid) {
		this.qlid = qlid;
	}
	
	public String getQlid() {		
		return this.qlid;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {		
		return this.password;
	}

	/**
	 * @return the grecaptchaResponse
	 */
	public String getGrecaptchaResponse() {
		return grecaptchaResponse;
	}

	/**
	 * @param grecaptchaResponse the grecaptchaResponse to set
	 */
	public void setGrecaptchaResponse(String grecaptchaResponse) {
		this.grecaptchaResponse = grecaptchaResponse;
	}
}
