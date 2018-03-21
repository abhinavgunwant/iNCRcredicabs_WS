package ncab.beans;

public class ContactBean {
	
	private int contactId;
	private String contactNbr;
	private String contactName;
	private String contactSos;
	private String contactRole;
	private int contactSosPriority;
	private String contactStatus;
	
	
	public String getContactStatus() {
		return contactStatus;
	}
	public void setContactStatus(String contactSosStatus) {
		this.contactStatus = contactSosStatus;
	}
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public String getContactNbr() {
		return contactNbr;
	}
	public void setContactNbr(String contactNbr) {
		this.contactNbr = contactNbr;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactSos() {
		return contactSos;
	}
	public void setContactSos(String contactSos) {
		this.contactSos = contactSos;
	}
	public String getContactRole() {
		return contactRole;
	}
	public void setContactRole(String contactRole) {
		this.contactRole = contactRole;
	}
	public int getContactSosPriority() {
		return contactSosPriority;
	}
	public void setContactSosPriority(int contactSosPriority) {
		this.contactSosPriority = contactSosPriority;
	}
	
	
	
	
}
