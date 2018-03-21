/**
 * EmployeeFilterBean.java
 * @version 1.00
 * 
 * 		Represents the JSON that is used to filter users in the 
 * 	view-employee component in the front-end.....
 */

package ncab.beans;

public class EmployeeFilterBean {
	private String filterType;
	private String filterValue;
	/**
	 * @return the filterType
	 */
	public String getFilterType() {
		return filterType;
	}
	/**
	 * @param filterType the filterType to set
	 */
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}
	/**
	 * @return the filterValue
	 */
	public String getFilterValue() {
		return filterValue;
	}
	/**
	 * @param filterValue the filterValue to set
	 */
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	
}
