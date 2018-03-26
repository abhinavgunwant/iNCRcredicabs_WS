package ncab.dao.impl;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.mysql.jdbc.CallableStatement;

//import com.ncr.iNCRediCabs.dao.DBConnection;
//import com.ncr.iNCRediCabs.dao.DbTables;
 import ncab.dao.impl.*;
import ncab.beans.*;
import ncab.dao.DBConnectionRo;
import ncab.dao.DBConnectionUpd;

public class EmployeeServiceImpl {
	//// The set of characters a pwdToken is allowed to have
	private final String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private final int allowedLen = allowedChars.length();
	
	//// required information for Google Recaptcha
	private final String USER_AGENT = "Mozilla/5.0";
	private final String GRECAPTCHA_API_URL = "https://www.google.com/recaptcha/api/siteverify";
	private final String GRECAPTCHA_SECRET = "6LfobEoUAAAAAMzl6qoNPTKv561siLjKlvFVAMIO";
//	private final String LOGFILE_DIR = "C:\\Users\\AG250497\\Desktop\\ncab_logs";
	private final String LOGFILE_DIR = "/tmp/ncab_logs";
	private final String LOGFILE_PREFIX = "iNCRediCabs_EMP_MASS_UPLOAD_LOG_";
	
	public static final String DEFAULT_QLID = "NO_QLID";
	public static final String DEFAULT_TOKEN = "NO_TOKEN";
	
	public static String noLoginMessage() {
		return (new JSONObject())
				.put("login", false)
				.put("message", "You must be logged-in!")
				.toString();
	}
	
	public static Response noLoginMessageResponse() {
		return Response.ok((new JSONObject())
				.put("login", false)
				.put("message", "You must be logged-in!")
				.toString()).build();
	}
	
	public JSONObject grecaptchaVerify(String grecaptchaResponse) {
		JSONObject grecaptchaResponseJSON = new JSONObject();
		try{
			URL url = new URL(GRECAPTCHA_API_URL);
			HttpsURLConnection conUrl = (HttpsURLConnection) url.openConnection();

			conUrl.setRequestMethod("POST");
			conUrl.setRequestProperty("User-Agent", USER_AGENT);
			conUrl.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String postParams = "secret="+GRECAPTCHA_SECRET+"&response="+grecaptchaResponse;

			conUrl.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conUrl.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(conUrl.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			grecaptchaResponseJSON = new JSONObject(response.toString());
		}catch(Exception e){
			e.printStackTrace();
			grecaptchaResponseJSON
				.put("success", false)
				.put("exception", true)
				.put("message", "Error connecting to reCaptcha server.... Please try again later!");
		}
		
		return grecaptchaResponseJSON;
	}
	
	/**
	 * addEmployee()
	 * 		Adds an employee to the DataBase
	 * 
	 * @param employeeBean: Bean containing the employee to be added to the
	 * 		database
	 * 
	 * 		An important thing to consider: The password in editEmployee and
	 * 		addEmployee methods contains "plain-text" of the password in
	 * 		employeeBean, rest of the methods contain the "Hash" of password!
	 * 		
	 * 		On Success, returns JSON:
	 * 				{"success": true, "message": "Employee successfully added!"}
	 * 		On SQL Error, returns JSON:
	 * 				{"success": false, "message": "Error in fetching result!"}
	 */
	public JSONObject addEmployee(EmployeeBean employeeBean) {
		System.out.println("in addEmployee");
		boolean managerFound = false;
//		System.out.println("!!");
		JSONObject validateJSON = validateAddEmployeeFormData(employeeBean);
		if(validateJSON.has("success")) {
//			System.out.println("!!1, success: " + validateJSON.getString("success"));
			if(!Boolean.parseBoolean(validateJSON.getString("success"))) {
				return validateJSON;
			}
		}else {
			return (new JSONObject())
					.put("success", false)
					.put("message", "An error occured!");
		}
//		System.out.println("!!2");
		
		try {
			Connection connection = (new DBConnectionUpd()).getConnection();
			PreparedStatement psMgr = connection.prepareStatement(
				"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE emp_qlid = ? AND roles_id = 2"
			);
			psMgr.setString(1, employeeBean.getEmpMgrQlid1());
			ResultSet rs = psMgr.executeQuery();
			while(rs.next()) {				
				managerFound = true;	
			}
			if(!managerFound) {
				return (new JSONObject())
						.put("success", false)
						.put("message", "The Manager QLID is invalid!");
			}
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO "+ DBTables.EMPLOYEE +"("
				+ "emp_qlid, emp_mgr_qlid1, emp_mgr_qlid2, emp_fname, emp_mname, emp_lname, emp_gender,"
				+ "Emp_Mob_Nbr, emp_home_nbr, emp_emerg_nbr, emp_add_line1, emp_add_line2, emp_pin,"
				+ "emp_pickup_area, emp_status, emp_bloodgrp, emp_created_by, emp_creation_date,"
				+ "emp_last_updated_by, emp_last_update_date, roles_id, emp_zone, emp_org_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE(), ?, CURDATE(), ?, ?, 'ITS')"
			);
			
			ps.setString(1, employeeBean.getEmpQlid());
			ps.setString(2, employeeBean.getEmpMgrQlid1());
			ps.setString(3, employeeBean.getEmpMgrQlid2());
			ps.setString(4, employeeBean.getEmpFName());
			ps.setString(5, employeeBean.getEmpMName());
			ps.setString(6, employeeBean.getEmpLName());
			ps.setString(7, employeeBean.getEmpGender());
//			ps.setString(8, employeeBean.getEmpDOB());
			ps.setString(8, employeeBean.getEmpMobNbr());
			ps.setString(9, employeeBean.getEmpHomeNbr());
			ps.setString(10, employeeBean.getEmpEmergNbr());
			ps.setString(11, employeeBean.getEmpAddLine1());
			ps.setString(12, employeeBean.getEmpAddLine2());
			ps.setInt(13, employeeBean.getEmpPin());
			ps.setString(14, employeeBean.getEmpPickupArea());
			ps.setString(15, "A");
			ps.setString(16, employeeBean.getEmpBloodGrp());
			ps.setString(17, "SYSTEM");
			ps.setString(18, "SYSTEM");
			ps.setString(19, employeeBean.getRolesId());
			ps.setString(20, employeeBean.getEmpZone());
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
			return (new JSONObject())
					.put("success", false)
					.put("message", "Error in fetching result!");
		}
		return (new JSONObject())
				.put("success", true)
				.put("message", "Employee successfully added!");
	}
	
	/**
	 * editEmployee()
	 * 		Edits the employee details
	 * @param employeeBean: the bean containing edited employee info
	 * @return JSONObject with key "success" set to true in case of
	 * 		success and false in case of any failure....
	 */
	public JSONObject editEmployee(EmployeeBean employeeBean) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date today = (Calendar.getInstance()).getTime();
		System.out.println("Starting validation....");
		JSONObject validateJSON = validateAddEmployeeFormData(employeeBean);
		System.out.println(validateJSON.toString());
		System.out.println(validateJSON.getString("success"));
		if(!Boolean.parseBoolean(validateJSON.getString("success"))) {
			return validateJSON;
		}
		System.out.println("Before sql");
		try {
			Connection connection = (new DBConnectionUpd()).getConnection();			
			PreparedStatement ps = connection.prepareStatement( 
				"UPDATE ncab_master_employee_tbl SET "
					+ "emp_mgr_qlid1 = ?, emp_mgr_qlid2 = ?, emp_fname = ?, emp_mname = ?, "
					+ "emp_lname = ?, emp_gender = ?, emp_mob_nbr = ?, "
					+ "emp_home_nbr = ?, emp_emerg_nbr = ?, emp_add_line1 = ?, "
					+ "emp_add_line2 = ?, emp_pin = ?, emp_pickup_area = ?, "
					+ "emp_bloodgrp = ?, emp_last_updated_by = ?, "
					+ "emp_last_update_date = ?, emp_status = ?, roles_id = ?, emp_zone = ? "
					+ "WHERE emp_qlid = ?"
			);
			 
			ps.setString(1, employeeBean.getEmpMgrQlid1());
			ps.setString(2, employeeBean.getEmpMgrQlid2());
			ps.setString(3, employeeBean.getEmpFName());
			ps.setString(4, employeeBean.getEmpMName());
			ps.setString(5, employeeBean.getEmpLName());
			ps.setString(6, employeeBean.getEmpGender());
			ps.setString(7, employeeBean.getEmpMobNbr());
			ps.setString(8, employeeBean.getEmpHomeNbr());
			ps.setString(9, employeeBean.getEmpEmergNbr());
			ps.setString(10, employeeBean.getEmpAddLine1());
			ps.setString(11, employeeBean.getEmpAddLine2());
			ps.setInt(12, employeeBean.getEmpPin());
			ps.setString(13, employeeBean.getEmpPickupArea());
			ps.setString(14, employeeBean.getEmpBloodGrp());
			ps.setString(15, employeeBean.getEmpLastUpdatedBy());
			ps.setString(16, df.format(today));
			ps.setString(17, employeeBean.getEmpStatus());
			ps.setString(18, employeeBean.getRolesId());
			ps.setString(19, employeeBean.getEmpZone());
			ps.setString(20, employeeBean.getEmpQlid());

			ps.executeUpdate();
			System.out.println("SQL update executed succesfully!");
		} catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Inside Catch");
			return (new JSONObject())
					.put("success", false)
					.put("message", "Error while editing employee!");
		}
		
		System.out.println("Last statement");
		return (new JSONObject())
				.put("success", true)
				.put("message", "Employee edited successfully!");
	}
	
	/**
	 * deactivateEmployee()
	 * @param employeeBean
	 * @return boolean: 'true': on success, 'false': on failure
	 * 
	 * 		Deactivates an employee: sets 'emp_status' field in the db table
	 * 	to 'I' (i.e. 'Inactive')
	 */
	public boolean deactivateEmployee(EmployeeBean employeeBean) {
		System.out.println("Deactivating");
		try {
			Connection connection = (new DBConnectionUpd()).getConnection();
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE "+ DBTables.EMPLOYEE +" SET emp_status = 'I' WHERE emp_qlid = ?"
			);
			ps.setString(1, employeeBean.getEmpQlid());
			ps.executeUpdate();
			System.out.println("SQL update executed succesfully!");
		} catch(SQLException e) {
			System.out.println("!!!!!!!!");
			e.printStackTrace();
			return false;
		}		
		return true;
	}
	
	/**
	 * activateEmployee()
	 * @param employeeBean
	 * @return boolean: 'true': on success, 'false': on failure
	 * 
	 * 		Activates an employee: Sets the 'emp_status' field in the db table
	 * 	to 'A' (i.e. 'Active')
	 */
	public boolean activateEmployee(EmployeeBean employeeBean) {
		System.out.println("Activating Employee!!");
		try {
			Connection connection = (new DBConnectionUpd()).getConnection();
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE "+ DBTables.EMPLOYEE +" SET emp_status = 'A' WHERE emp_qlid = ?"
			);
			ps.setString(1, employeeBean.getEmpQlid());
			ps.executeUpdate();
			System.out.println("SQL update executed succesfully!");
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * getEmployeeJSONArray()
	 * @param employeeBeanArr
	 * @return empJSONArray
	 * 
	 * 		Returns the list of all employees in the employeeBeanArr in the form
	 * 	of JSON array.
	 */
	public JSONArray getEmployeeJSONArray(EmployeeBean[] employeeBeanArr) {
		JSONArray empJSONArray = new JSONArray();
		if(employeeBeanArr == null) {
			return empJSONArray;
		}
		
		for(int i=0; i<employeeBeanArr.length; ++i) {
			empJSONArray.put(createJSON(employeeBeanArr[i]));
		}
		
		return empJSONArray;
	}
	
	/**
	 * getEmployeeArray()
	 * @param employeeFilterBean
	 * @return employeeBeanArr[]
	 * 
	 * 		Returns the array of Employees based on the filters(employeeFilterBean)
	 */
	public EmployeeBean[] getEmployeeArray(
			EmployeeFilterBean	employeeFilterBean
	) {
		EmployeeBean employeeBeanArr[] = null;
		String defaultSQL = "SELECT * FROM "+ DBTables.EMPLOYEE;
		
		try {
			Connection connection = (new DBConnectionUpd()).getConnection();
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			//// Check if filter type is not null
			if(employeeFilterBean.getFilterType() != null ||
					employeeFilterBean.getFilterValue() != null) {
				
				//// Do SQL queries based on filter types
				switch(employeeFilterBean.getFilterType().toUpperCase()) {
					case "MGRQLID1":
						ps = connection.prepareStatement(
								"SELECT * FROM "+ DBTables.EMPLOYEE +" WHERE "
								+ "emp_mgr_qlid1 LIKE ? "
								+ "ORDER BY emp_fname, emp_mname, emp_lname, emp_status"
						);
						ps.setString(1, employeeFilterBean.getFilterValue()+"%");
						break;
						
					case "MGRQLID2":
						ps = connection.prepareStatement(
								"SELECT * FROM "+ DBTables.EMPLOYEE +" WHERE "
								+ "emp_mgr_qlid2 LIKE ?"
						);
						ps.setString(1, employeeFilterBean.getFilterValue()+"%");
						break;
						
					case "MGRNAME1":
						String mgrName1[] = employeeFilterBean.getFilterValue().split(" ");
						String mgrFName1, mgrLName1;
						String finalSQL1;
						if(mgrName1.length == 2) {
							mgrFName1 = mgrName1[0];
							mgrLName1 = mgrName1[1];
						}else {
							mgrFName1 = mgrName1[0];
							mgrLName1 = mgrFName1;
						}
						
						PreparedStatement ps2 = connection.prepareStatement(
							//// make sure roles_id is manager's role id
							"SELECT emp_qlid from "+DBTables.EMPLOYEE+" WHERE "
							+ "(emp_fname LIKE ? or emp_lname LIKE ?) "
							+ "AND roles_id = 2" 
						);
						ps2.setString(1, "%"+mgrFName1+"%");
						ps2.setString(2, "%"+mgrLName1+"%");
						
						ResultSet rs2 = ps2.executeQuery();
						
						int rows = rs2.last() ? rs2.getRow() : 0;
						if(rows > 0) {
							finalSQL1 = "SELECT * FROM "+DBTables.EMPLOYEE+" WHERE "
									+ "emp_mgr_qlid1 = ? ";
							for(int i=1; i<rows; ++i) {
								finalSQL1 += "or emp_mgr_qlid1 = ? ";
							}
							
							ps = connection.prepareStatement(finalSQL1);
							rs2.beforeFirst();
							
							int i=0;
							
							while(rs2.next()) {
								ps.setString(++i, rs2.getString("emp_qlid"));							
							}
						}
						
						break;
						
					case "MGRNAME2":
						String mgrName2[] = employeeFilterBean.getFilterValue().split(" ");
						String mgrFName2, mgrLName2;
						String finalSQL2;
						if(mgrName2.length == 2) {
							mgrFName2 = mgrName2[0];
							mgrLName2 = mgrName2[1];
						}else {
							mgrFName2 = mgrName2[0];
							mgrLName2 = mgrFName2;
						}
						
						PreparedStatement ps3 = connection.prepareStatement(
//							"SELECT mgr_qlid from master_manager WHERE "
//								+ "mgr_fname LIKE ? or mgr_lname LIKE ? "
							//// make sure roles_id is manager's role id
							"SELECT emp_qlid from "+DBTables.EMPLOYEE+" WHERE "
							+ "(emp_fname LIKE ? or emp_lname LIKE ?)"
							+ "AND roles_id = 2" 
						);
						ps3.setString(1, "%"+mgrFName2+"%");
						ps3.setString(2, "%"+mgrLName2+"%");
						ResultSet rs3 = ps3.executeQuery();
						
						int rows2 = rs3.last() ? rs3.getRow() : 0;
						if(rows2 > 0) {
							finalSQL2 = "SELECT * FROM "+DBTables.EMPLOYEE+" WHERE "
									+ "emp_mgr_qlid1 = ? ";
							for(int i=1; i<rows2; ++i) {
								finalSQL2 += "or emp_mgr_qlid2 = ? ";
							}
							
							ps = connection.prepareStatement(finalSQL2);
							rs3.beforeFirst();
							
							int i=0;
							
							while(rs3.next()) {
								ps.setString(++i, rs3.getString("emp_qlid"));							
							}
						}
						
						break;
					case "EMPQLID":
						ps = connection.prepareStatement(
							"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE emp_qlid LIKE ?"
						);
						ps.setString(1, employeeFilterBean.getFilterValue()+"%");
						
						break;
					case "EMPNAME":
						String empName[] = employeeFilterBean.getFilterValue().split(" ");	
						
						//// If value supplied for the filter is one word, it could be
						//// 	first name or last, so build a query string to do so....
						//// Similarly for two words and so on....
						switch(empName.length) {
							//// if filter value has one word, search both first and last name
							case 1:
								ps = connection.prepareStatement(
									"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE "
										+ "emp_fname LIKE ? or emp_mname LIKE ? or "
										+ "emp_lname LIKE ? "
								);
								
								if(empName[0].charAt(empName[0].length()-1) == '*') {
									String temp = empName[0].replace('*', '%');
									ps.setString(1, temp);
									ps.setString(2, temp);
									ps.setString(3, temp);
								}else {
									ps.setString(1, "%"+empName[0]+"%");
									ps.setString(2, "%"+empName[0]+"%");
									ps.setString(3, "%"+empName[0]+"%");
								}
								
								System.out.println(ps.toString());
								
								break;
							//// For 2 words, it could be First Name and Last Name,
							////	First name and Middle Name and so on....
							//// 	Build SQL query for all combinations....
							case 2:
								ps = connection.prepareStatement(
									"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE "
										+ "emp_fname LIKE ? or emp_fname LIKE ? or "
										+ "emp_mname LIKE ? or emp_mname LIKE ? or "
										+ "emp_lname LIKE ? or emp_lname LIKE ? "
								);
								
								//// To check only the names starting with the string sequence
								//// before '*' character!

								if(empName[0].charAt(empName[0].length()-1) == '*') {
									String temp = empName[0].replace('*', '%');
									ps.setString(1, temp);	
									ps.setString(3, temp);
									ps.setString(5, temp);								
								}else {
									ps.setString(1, "%"+empName[0]+"%");
									ps.setString(3, "%"+empName[0]+"%");
									ps.setString(5, "%"+empName[0]+"%");
								}								

								if(empName[1].charAt(empName[1].length()-1) == '*') {
									String temp = empName[1].replace('*', '%');
									ps.setString(2, temp);
									ps.setString(4, temp);
									ps.setString(6, temp);
								}else {
									ps.setString(2, "%"+empName[1]+"%");	
									ps.setString(4, "%"+empName[1]+"%");
									ps.setString(6, "%"+empName[1]+"%");								
								}		
								
								break;
								
							//// For 3 words, search for First, Middle and Last Name in
							////	possible permutations....
							case 3:
								ps = connection.prepareStatement(
									"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE "
										+ "emp_fname LIKE ? or emp_fname LIKE ? or "
										+ "emp_fname LIKE ? or emp_mname LIKE ? or "
										+ "emp_mname LIKE ? or emp_mname LIKE ? or "
										+ "emp_lname LIKE ? or emp_lname LIKE ? or "
										+ "emp_lname LIKE ? "
								);
								
								if(empName[0].charAt(empName[0].length()-1) == '*') {
									String temp = empName[0].replace('*', '%');
									ps.setString(1, temp);
									ps.setString(4, temp);
									ps.setString(7, temp);
								}else {
									ps.setString(1, "%"+empName[0]+"%");
								}

								if(empName[1].charAt(empName[1].length()-1) == '*') {
									String temp = empName[1].replace('*', '%');
									ps.setString(2, temp);
									ps.setString(5, temp);
									ps.setString(8, temp);
								}else {
									ps.setString(2, "%"+empName[1]+"%");
									ps.setString(5, "%"+empName[1]+"%");
									ps.setString(8, "%"+empName[1]+"%");
								}

								if(empName[2].charAt(empName[2].length()-1) == '*') {
									String temp = empName[2].replace('*', '%');
									ps.setString(3, temp);
									ps.setString(6, temp);
									ps.setString(9, temp);
								}else {
									ps.setString(3, "%"+empName[2]+"%");
									ps.setString(6, "%"+empName[2]+"%");
									ps.setString(9, "%"+empName[2]+"%");
								}
								
								break;
							
							//// Return an empty set if words are not in range of 1-3
							default:
								ps = connection.prepareStatement(
									"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE 0 = 1"
								);
						}
						
						break;
						
						
					case "EMPPICKUPAREA":
						ps = connection.prepareStatement(
							"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE emp_pickup LIKE ?"
						);
						ps.setString(1, "%"+employeeFilterBean.getFilterValue()+"%");
						
						break;
						
					case "EMPPIN":
						ps = connection.prepareStatement(
								"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE emp_pin LIKE ?"
						);
						ps.setString(1, "%"+employeeFilterBean.getFilterValue()+"%");
						
						break;
						
					case "ROLESID":						
						ps = connection.prepareStatement(
							"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE roles_id = ?"
						);
						ps.setString(1, employeeFilterBean.getFilterValue());
						
						break;
						
					case "EMPZONE":
						ps = connection.prepareStatement(
							"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE emp_zone = ?"
						);
						ps.setString(1, employeeFilterBean.getFilterValue());
						
						break;
						
					case "EMPSTATUS":
						ps = connection.prepareStatement(
							"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE emp_status = ?"
						);
						ps.setString(1, employeeFilterBean.getFilterValue());
						
						break;
						
					default:
						ps = connection.prepareStatement(defaultSQL);					 
				}
			}else {
				ps = connection.prepareStatement(defaultSQL);
			}
			
			if(ps != null) {
				rs = ps.executeQuery();
				
				int rows = rs.last() ? rs.getRow() : 0;
				
				if(rows == 0) {
					System.out.println("SQL SELECT statement did not return anything!");
					return null;
				}
				
				rs.beforeFirst();
				employeeBeanArr =  new EmployeeBean[rows];
				int i = 0;
				
				while(rs.next()) {
					employeeBeanArr[i] = new EmployeeBean();
					employeeBeanArr[i].setEmpQlid(rs.getString("emp_qlid"));
					employeeBeanArr[i].setEmpMgrQlid1(rs.getString("emp_mgr_qlid1"));
					employeeBeanArr[i].setEmpMgrQlid2(rs.getString("emp_mgr_qlid2"));
					employeeBeanArr[i].setEmpFName(rs.getString("emp_fname"));
					employeeBeanArr[i].setEmpMName(rs.getString("emp_mname"));
					employeeBeanArr[i].setEmpLName(rs.getString("emp_lname"));
					employeeBeanArr[i].setEmpGender(rs.getString("emp_gender"));
					employeeBeanArr[i].setEmpMobNbr(rs.getString("emp_mob_nbr"));
					employeeBeanArr[i].setEmpHomeNbr(rs.getString("emp_home_nbr"));
					employeeBeanArr[i].setEmpEmergNbr(rs.getString("emp_emerg_nbr"));
					employeeBeanArr[i].setEmpAddLine1(rs.getString("emp_add_line1"));
					employeeBeanArr[i].setEmpAddLine2(rs.getString("emp_add_line2"));
					employeeBeanArr[i].setEmpPin(rs.getInt("emp_pin"));
					employeeBeanArr[i].setEmpZone(rs.getString("emp_zone"));
					employeeBeanArr[i].setEmpPickupArea(rs.getString("emp_pickup_area"));
					employeeBeanArr[i].setEmpStatus(rs.getString("emp_status"));
					employeeBeanArr[i].setEmpBloodGrp(rs.getString("emp_bloodgrp"));
					employeeBeanArr[i].setEmpCreatedBy(rs.getString("emp_created_by"));
					employeeBeanArr[i].setEmpCreationDate(rs.getString("emp_creation_date"));
					employeeBeanArr[i].setEmpLastUpdatedBy(rs.getString("emp_last_updated_by"));
					employeeBeanArr[i].setEmpLastUpdateDate(rs.getString("emp_last_update_date"));
					employeeBeanArr[i].setRolesId(rs.getString("roles_id"));
					++i;
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return employeeBeanArr;
	}
	
	public EmployeeBean getEmployeeFromQLID(String qlid) {
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		EmployeeBean empBean = null;
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE emp_qlid = ? "
					);
			ps.setString(1, qlid);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				empBean = new EmployeeBean();
				empBean.setEmpQlid(rs.getString("emp_qlid"));
				empBean.setEmpMgrQlid1(rs.getString("emp_mgr_qlid1"));
				empBean.setEmpMgrQlid2(rs.getString("emp_mgr_qlid2"));
				empBean.setEmpFName(rs.getString("emp_fname"));
				empBean.setEmpMName(rs.getString("emp_mname"));
				empBean.setEmpLName(rs.getString("emp_lname"));
				empBean.setEmpGender(rs.getString("emp_gender"));
				empBean.setEmpMobNbr(rs.getString("emp_mob_nbr"));
				empBean.setEmpHomeNbr(rs.getString("emp_home_nbr"));
				empBean.setEmpEmergNbr(rs.getString("emp_emerg_nbr"));
				empBean.setEmpAddLine1(rs.getString("emp_add_line1"));
				empBean.setEmpAddLine2(rs.getString("emp_add_line2"));
				empBean.setEmpPin(rs.getInt("emp_pin"));
				empBean.setEmpZone(rs.getString("emp_zone"));
				empBean.setEmpPickupArea(rs.getString("emp_pickup_area"));
				empBean.setEmpStatus(rs.getString("emp_status"));
				empBean.setEmpBloodGrp(rs.getString("emp_bloodgrp"));
				empBean.setEmpCreatedBy(rs.getString("emp_created_by"));
				empBean.setEmpCreationDate(rs.getString("emp_creation_date"));
				empBean.setEmpLastUpdatedBy(rs.getString("emp_last_updated_by"));
				empBean.setEmpLastUpdateDate(rs.getString("emp_last_update_date"));
				empBean.setRolesId(rs.getString("roles_id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return empBean;
	}
	/**
	 * getAllManagers()
	 * @return JSONArray
	 * 
	 *		Returns the list of all the managers in the form of JSONArray
	 */
	public JSONArray getAllManagers() {
		JSONArray jsArr = new JSONArray();
		
		try {		
			DBConnectionUpd DBConnectionUpd = new DBConnectionUpd();
			Connection connection = DBConnectionUpd.getConnection();
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE roles_id = 2"
			);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				jsArr.put(
					(new JSONObject())
						.put("mgrQlid", rs.getString("emp_qlid"))
						.put("mgrFName", rs.getString("emp_fname"))
						.put("mgrLName", rs.getString("emp_lname"))
				);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return jsArr;		
	}
	
	/**
	 * getManager()
	 * @param managerRequestBean
	 * @return JSONArray
	 * 
	 * 		Sends the list of managers in form of JSONArray based on the
	 * 	manager name specified in the managerRequestBean.
	 */
	public JSONArray getManager(ManagerRequestBean managerRequestBean) {
		JSONArray jsArr = new JSONArray();
		String name[] = managerRequestBean.getManagerName().split(" ");
		if (name == null || name.length == 0) {
			return jsArr;
		}
		
		try {
			DBConnectionUpd DBConnectionUpd = new DBConnectionUpd();
			Connection connection = DBConnectionUpd.getConnection();
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM "+DBTables.EMPLOYEE+" WHERE (mgr_fname LIKE ? "
				+ "or mgr_lname LIKE ? or mgr_fname LIKE ? or mgr_lname LIKE ?)"
				+ "AND roles_id = 2");
			switch(name.length) {
				case 1:
					ps.setString(1, name[0]);
					ps.setString(2, name[0]);
					ps.setString(3, name[0]);
					ps.setString(4, name[0]);
					break;
				case 2:
				case 3:
					ps.setString(1, name[0]);
					ps.setString(2, name[1]);
					ps.setString(3, name[1]);
					ps.setString(4, name[0]);
					break;
				default:
					return jsArr;
			}
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				jsArr.put(
					(new JSONObject())
						.put("mgrQlid", rs.getString("emp_qlid"))
						.put("mgrFName", rs.getString("emp_fname"))
						.put("mgrLName", rs.getString("emp_lname"))
				);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return jsArr;
	}
	
	/**
	 * validateAddEmployeeFormData()
	 * @param employeeBean
	 * @return JSONObject
	 * 		
	 * 		Validates the data entered in the "Add Employee Form".
	 * 	Returns a JSON with error messages for each field....
	 */
	public JSONObject validateAddEmployeeFormData(EmployeeBean employeeBean) {
		JSONObject jsObj = new JSONObject();
		
		boolean addEmpSuccess = true;
		
		String qlidPattern = "^[a-zA-Z]{2}\\d{6}$";
		String mobPattern = "^\\d{10}$";
		String pinPattern = "^\\d{6}$";
		String bloodGrpPattern = "^(A|B|AB|O|a|b|ab|o)[\\\\+\\\\-]$";
		String genderPattern = "^((M|F)|(m|f))$";
		String namePattern = "^\\w{1,15}$";
		
		String message;
		
		//// Employee QLID validation		
		if(employeeBean.getEmpQlid() == "") {
			System.out.println("Error in Employee QLID validation1");
			addEmpSuccess = false;
			jsObj.put("empQlid", 
				(new JSONObject())
					.put("value", employeeBean.getEmpQlid())
					.put("error", true)
					.put("message", "Employee QLID cannot be empty!")
			);
		}else if(!employeeBean.getEmpQlid().matches(qlidPattern)) {
			System.out.println("Error in Employee QLID validation2, qlid: " + employeeBean.getEmpQlid());
			addEmpSuccess = false;
			jsObj.put("empQlid", 
				(new JSONObject())
					.put("value", employeeBean.getEmpQlid())
					.put("error", true)
					.put("message", "Employee QLID format is invalid!")
			);
		}else {
			jsObj.put("empQlid", 
				(new JSONObject())
					.put("value", employeeBean.getEmpQlid())
					.put("error", false)
					.put("message", "")
			);
		}
		
		
		//// Employee FirstName validation		
		if(employeeBean.getEmpFName() == "") {
			System.out.println("Error in Employee FirstName validation1");
			addEmpSuccess = false;
			jsObj.put("empFName", 
				(new JSONObject())
					.put("value", employeeBean.getEmpFName())
					.put("error", true)
					.put("message", "First Name cannot be empty!")
			);
		}else if(!employeeBean.getEmpFName().matches(namePattern)) {
			System.out.println("Error in Employee FirstName validation2");
			addEmpSuccess = false;
			jsObj.put("empFName",
				(new JSONObject())
					.put("value", employeeBean.getEmpFName())
					.put("error", true)
					.put("message", "Firstname length should be between 1 and 16")
			);
		}else {
			jsObj.put("empFName", 
				(new JSONObject())
					.put("value", employeeBean.getEmpFName())
					.put("error", false)
					.put("message", "")
			);
		}
		
		//// Employee Middle Name validation
		if(employeeBean.getEmpMName() != null) {
			if(employeeBean.getEmpMName().length() > 15) {
				System.out.println("Error in  validation");
				addEmpSuccess = false;
				jsObj.put("empMName", 
					(new JSONObject())
						.put("value", employeeBean.getEmpMName())
						.put("error", true)
						.put("message", "Middle name cannot exceed 15 characters")
				);
			}else{
				jsObj.put("empMName", 
					(new JSONObject())
						.put("error", false)
						.put("message", "")
				);
			}
		}else{
				jsObj.put("empMName", 
					(new JSONObject())
						.put("error", false)
						.put("message", "")
				);
		}
		
		//// Employee Last Name validation
		if(employeeBean.getEmpLName() == "") {
			System.out.println("Error in Employee Last Name validation1");
			addEmpSuccess = false;
			jsObj.put("empLName", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Last Name length should be between 1 and 16")
			);
		}else if(!employeeBean.getEmpLName().matches(namePattern)) {
			System.out.println("Error in Employee Last Name validation2");
			addEmpSuccess = false;
			jsObj.put("empLName", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Last Name cannot be empty!")
			);
		}else {
			jsObj.put("empLName", 
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
		//// Employee Gender validation
		if(employeeBean.getEmpGender() == "") {
			System.out.println("Error in Employee Gender validation1");
			addEmpSuccess = false;
			jsObj.put("empGender", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Please Select Gender!")
			);
		}else if(!employeeBean.getEmpGender().matches(genderPattern)) {
			System.out.println("Error in Employee Gender validation2");
			addEmpSuccess = false;
			jsObj.put("empGender", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Please Select Gender!")
			);
		}else {
			jsObj.put("empGender", 
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
		//// Employee Contact(Mob) validation
		if(employeeBean.getEmpMobNbr() == "") {
			System.out.println("Error in Employee Contact(Mob) validation1");
			addEmpSuccess = false;
			jsObj.put("empMobNbr", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Contact No cannot be Empty!")
			);
		}else if(!employeeBean.getEmpMobNbr().matches(mobPattern)) {
			System.out.println("Error in Employee Contact(Mob) validation2");
			addEmpSuccess = false;
			jsObj.put("empMobNbr", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Mobile no format invalid!")
			);
		}else {
			jsObj.put("empMobNbr", 
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
		//// Employee Role validation
		if(employeeBean.getRolesId() == "") {
			System.out.println("Error in Employee Role validation1");
			addEmpSuccess = false;
			jsObj.put("rolesId", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Please select a Role")
			);
		}else if(Integer.parseInt(employeeBean.getRolesId()) > 9
				|| Integer.parseInt(employeeBean.getRolesId()) < 0
				) {
			System.out.println("Error in Employee Role validation2");
			addEmpSuccess = false;
			jsObj.put("rolesId", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Selected role is invalid!")
			);
		}else {
			jsObj.put("rolesId", 
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
	    //// Employee L1 Manager QLID validation
		if(employeeBean.getEmpMgrQlid1() == "") {
			System.out.println("Error in Employee L1 Manager QLID validation1");
			addEmpSuccess = false;
			jsObj.put("empMgrQlid1", 
				(new JSONObject())
					.put("error", true)
					.put("message", "L1 Manager QLID cannot be empty!")
			);
		}else if(!employeeBean.getEmpMgrQlid2().matches(qlidPattern)) {
			System.out.println("Error in Employee L1 Manager QLID validation2");
			addEmpSuccess = false;
			jsObj.put("empMgrQlid1", 
				(new JSONObject())
					.put("error", true)
					.put("message", "L1 Manager QLID format is invalid!")
			);
		}else {
			jsObj.put("empMgrQlid1", 
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
	    //// Employee L2 Manager QLID validation
		if(employeeBean.getEmpMgrQlid2() == "") {
			System.out.println("Error in Employee L2 Manager QLID validation1");
			addEmpSuccess = false;
			jsObj.put("empMgrQlid2", 
				(new JSONObject())
					.put("error", true)
					.put("message", "L2 Manager QLID cannot be empty!")
			);
		}else if(!employeeBean.getEmpMgrQlid2().matches(qlidPattern)) {
			System.out.println("Error in Employee L2 Manager QLID validation2");
			addEmpSuccess = false;
			jsObj.put("empMgrQlid2", 
				(new JSONObject())
					.put("error", true)
					.put("message", "L2 Manager QLID format is invalid!")
			);
		}else {
			jsObj.put("empMgrQlid2", 
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
	    //// Employee Address Line 1 validation
		if(employeeBean.getEmpAddLine1() == "") {
			System.out.println("Error in Employee Address Line 1 validation1");
			addEmpSuccess = false;
			jsObj.put("empAddLine1", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Address Line 1 cannot be empty!")
			);
		}else if(employeeBean.getEmpAddLine1().length() > 100) {
			System.out.println("Error in Employee Address Line 1 validation2");
			addEmpSuccess = false;
			jsObj.put("empAddLine1", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Address Line 1 can have maximum 100 characters!")
			);
		}else {
			jsObj.put("empAddLine1",
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
	    //// Employee Address Line 2 validation
		if(employeeBean.getEmpAddLine2() == "") {
			System.out.println("Error in Employee Address Line 2 validation1");
			addEmpSuccess = false;
			jsObj.put("empAddLine2", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Address Line 2 cannot be empty!")
			);
		}else if(employeeBean.getEmpAddLine2().length() > 100) {
			System.out.println("Error in Employee Address Line 2 validation2");
			addEmpSuccess = false;
			jsObj.put("empAddLine2", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Address Line 2 can have maximum 100 characters!")
			);
		}else {
			jsObj.put("empAddLine2",
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
	    //// Employee Postal Code (PIN) validation
		if(employeeBean.getEmpPin() == 0) {
			System.out.println("Error in Employee Postal Code (PIN) validation1");
			addEmpSuccess = false;
			jsObj.put("empPin", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Postal Code cannot be empty!")
			);
		}else if(!Integer.toString(employeeBean.getEmpPin()).matches(pinPattern)) {
			System.out.println("Error in Employee Postal Code (PIN) validation2");
			addEmpSuccess = false;
			jsObj.put("empPin", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Invalid Postal Code Pattern!")
			);
		}else {
			jsObj.put("empPin",
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
	    //// Employee Pickup Area validation
		if(employeeBean.getEmpPickupArea() == "") {
			System.out.println("Error in Employee Pickup Area validation1");
			addEmpSuccess = false;
			jsObj.put("empPickupArea", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Pickup Area cannot be empty!")
			);
		}else if(employeeBean.getEmpPickupArea().length() > 20) {
			System.out.println("Error in Employee Pickup Area validation2");
			addEmpSuccess = false;
			jsObj.put("empPickupArea", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Pickup Area cannot exceed 20 characters!")
			);
		}else {
			jsObj.put("empPickupArea",
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
	    //// Employee Home contact no validation
		if(employeeBean.getEmpHomeNbr() != null) {
			if(!employeeBean.getEmpHomeNbr().matches(mobPattern)) {
				System.out.println("Error in Employee Home contact no validation");
				addEmpSuccess = false;
				jsObj.put("empHomeNbr", 
					(new JSONObject())
						.put("error", true)
						.put("message", "Please enter a valid home phone no")
				);
			}else {
				jsObj.put("empHomeNbr", 
					(new JSONObject())
						.put("error", false)
						.put("message", "")
				);
			}
		}
		
	    //// Employee Emergency contact no validation
		if(employeeBean.getEmpEmergNbr() == "") {
			System.out.println("Error in Emergency contact no validation1");
			addEmpSuccess = false;
			jsObj.put("empEmergNbr", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Emergency no cannot be empty!")
			);
		}else if(!employeeBean.getEmpEmergNbr().matches(mobPattern)) {
			System.out.println("Error in Emergency contact no validation2");
			addEmpSuccess = false;
			jsObj.put("empEmergNbr", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Please enter a valid emergency no")
			);
		}else {
			jsObj.put("empEmergNbr", 
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
	    //// Employee Blood Group validation
		if(!employeeBean.getEmpBloodGrp().matches(bloodGrpPattern)) {
			addEmpSuccess = false;
			System.out.println("Error in Employee Blood Group validation!");
			jsObj.put("empBloodGrp", 
				(new JSONObject())
					.put("error", true)
					.put("message", "Please enter a valid blood group name")
			);
		}else {
			jsObj.put("empBloodGrp", 
				(new JSONObject())
					.put("error", false)
					.put("message", "")
			);
		}
		
		jsObj.put("success", addEmpSuccess);
		
		if(addEmpSuccess) {
			message = "Employee has been added successfully!";
		}else {
			message = "Error while adding employee!";
		}		

		jsObj.put("message", message);
		
		return jsObj;
	}
	
	/**
	 * isPasswordSet()
	 * @param qlid
	 * @return boolean
	 * 		Checks if the user with supplied qlid has set their password.
	 * 	Returns true if they have, false otherwise....		
	 */
	public boolean isPasswordSet(String qlid) {
		Connection con = (new DBConnectionUpd()).getConnection();
		PreparedStatement ps;
		ResultSet rs;
		if(qlid.matches("^\\w\\w\\d{6}$")) {
			try {
				ps = con.prepareStatement(
					"SELECT * FROM "+DBTables.LOGIN_CREDENTIALS+" WHERE emp_qlid = ?"
				);
				ps.setString(1, qlid);
				System.out.println(ps.toString());
				rs = ps.executeQuery();
				if(rs.next()) {
					if(rs.getString("login_password") != "") {
						rs.close();
						ps.close();
						con.close();
						return true;
					}
				}
				rs.close();
				ps.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	/**
	 * setPassword()
	 * @param qlid
	 * @param password
	 * @return boolean
	 * 
	 * 		Sets the password for employee with the supplied qlid
	 */
	public boolean setPassword(String qlid, String password) {
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		PreparedStatement ps;
		
		String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
		try {
			ps = con.prepareStatement(
				"INSERT INTO "+DBTables.LOGIN_CREDENTIALS+" ("
				+ "emp_qlid, login_password, "
				+ "login_creation_by, login_creation_date, login_last_updated_by,"
				+ "login_last_update_date, login_passwordchangedon) VALUES ("
				+ "?,?, 'SYSTEM', CURDATE(), 'SYSTEM', CURDATE(), CURDATE())"
			);
			
			ps.setString(1, qlid);
			ps.setString(2, passwordHash);
			ps.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * updatePassword()
	 * @param qlid
	 * @param password
	 * @return boolean
	 * 
	 * 		Updates the user password
	 */
	public boolean updatePassword(String qlid, String password) {
		try {
			String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
			Connection connection = (new DBConnectionUpd()).getConnection();
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE "+DBTables.LOGIN_CREDENTIALS+" SET "
				+ "Login_Last_Update_Date = CURDATE(), Login_PasswordChangedOn = CURDATE(), "
				+ "Login_Password = ?, Login_Last_Updated_By = ? WHERE Emp_Qlid = ?"
			);
			ps.setString(1, passwordHash);
			ps.setString(2, qlid);
			ps.setString(3, qlid);
			ps.executeUpdate();
			
			System.out.println("SQL update executed succesfully!");
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;		
	}
	
	/**
	 * validateUser()
	 * @param qlid
	 * @param password
	 * @return boolean
	 * 
	 * 		returns true if user with the given qlid exists....
	 */
	public boolean validateUser(String qlid) {
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		
		//// return false if the qlid string does not match the qlid format
		if(!qlid.matches("^\\w\\w\\d{6}$")) {
			return false;
		}
		
		try{
			PreparedStatement ps = con.prepareStatement(
				"select * from ncab_master_employee_tbl where Emp_Qlid= ? "
			);
			ps.setString(1, qlid);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}			
		} catch (SQLException e) {
			System.out.println("SQL ERROR!");
			e.printStackTrace();
			return false;			
		}
		return false;
	}
	
	/**
	 * createJSON()
	 * @param emp
	 * @return JSONObject
	 * 
	 * 		Creates the JSON object for the given employee bean....
	 */
	public JSONObject createJSON(EmployeeBean emp) {
		JSONObject empJSON = new JSONObject();
		if(emp != null) {
			empJSON.put("empQlid", emp.getEmpQlid())
				.put("empFName", emp.getEmpFName())
				.put("empMName", emp.getEmpMName())
				.put("empLName", emp.getEmpLName())
				.put("empGender", emp.getEmpGender())
				.put("empDOB", emp.getEmpDOB())
				.put("empMobNbr", emp.getEmpMobNbr())
				.put("empHomeNbr", emp.getEmpHomeNbr())
				.put("empEmergNbr", emp.getEmpEmergNbr())
				.put("empAddLine1", emp.getEmpAddLine1())
				.put("empAddLine2", emp.getEmpAddLine2())
				.put("empZone", emp.getEmpZone())
				.put("empPin", emp.getEmpPin())
				.put("empPickupArea", emp.getEmpPickupArea())
				.put("empStatus", emp.getEmpStatus())
				.put("empBloodGrp", emp.getEmpBloodGrp())
				.put("empCreatedBy", emp.getEmpCreatedBy())
				.put("empCreationDate", emp.getEmpCreationDate())
				.put("empLastUpdatedBy", emp.getEmpLastUpdatedBy())
				.put("empLastUpdateDate", emp.getEmpLastUpdateDate())
				.put("rolesId", emp.getRolesId())
				.put("empMgrQlid1", emp.getEmpMgrQlid1())
				.put("empMgrQlid2", emp.getEmpMgrQlid2());
		}
		
		return empJSON;
	}
	
	/**
	 * getLoginAuthJSON()
	 * @param qlid
	 * @param password
	 * @return JSONObject
	 * 
	 * 		Logs the user in and returns a JSON object showing the status of login
	 * request along with a message.........
	 * 	
	 */
	public JSONObject getLoginAuthJSON(String qlid, String password) {
		JSONObject jsObj = new JSONObject();
		boolean userRecordFound = false;
		boolean loginSuccess = false;
		
		String dbPassword = null;
		
		String message = "The username and password combination you entered is incorrect!";
		String messageType = "error";
		String roleId;
		String role = "EMPLOYEE";
		
		try {
//			System.out.println("!!");
			Connection con = (new DBConnectionUpd()).getConnection();
			PreparedStatement ps = con.prepareStatement(
				"select login_password from "+DBTables.LOGIN_CREDENTIALS+", "+
					DBTables.EMPLOYEE+" where "+DBTables.LOGIN_CREDENTIALS+".emp_qlid = ?"
			);
			PreparedStatement psRole = con.prepareStatement(
					"SELECT roles_name FROM `ncab_master_roles_tbl` WHERE roles_id ="
					+ "	(SELECT roles_id FROM `ncab_master_employee_tbl` WHERE emp_qlid = ?)"	
			);
			
			ps.setString(1, qlid);
			psRole.setString(1, qlid);
			
			ResultSet rs = ps.executeQuery();
			ResultSet rs2 = psRole.executeQuery();
			
			while(rs2.next()) {
				role = rs2.getString("roles_name");
				break;
			}
			
			//// TODO: Incorporate Password Hashing.....
			
			while(rs.next()) {
				userRecordFound = true;
				dbPassword = rs.getString("login_password");
//				System.out.println("DB password: " + dbPassword);
				
//				if(dbPassword.equals(password)) {
				//// Verify that the password is correct!
				//// (Uses BCrypt)....
				if(BCrypt.checkpw(password, dbPassword)) {
					loginSuccess = true;
					message = "You have been successfully logged in!";
					messageType = "success";			
				}
				break;
			}
			
			
			if(!userRecordFound) {
				message = "User with the QLID \""+ qlid +"\" is not registered,"
						+ " Please go to \"New Account Setup\" to register yourself...";
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(loginSuccess) {
			jsObj.put("role", role);
		}
		
		jsObj.put("login", loginSuccess)
			.put("message", message)
			.put("message-type", messageType);
		
		return jsObj;
	}
	
	/**
	 * setLoginSession()
	 * @param req
	 * @param res
	 * @param loginFormInputBean
	 * 
	 *		Sets the login session....
	 */
	public void setLoginSession(
			HttpServletRequest req,
			HttpServletResponse res,
			LoginFormInputBean loginFormInputBean
		) {
		HttpSession sess = req.getSession();
		String token = null;
		if(sess.getAttribute("qlid") == null) {
			token = buildRandomToken(64);
			sess.setAttribute("qlid", loginFormInputBean.getQlid());
			sess.setAttribute("token", token);
		}
	}
	
	/**
	 * logout()
	 * @param req
	 * @param res
	 * 
	 *		Logs the user out....
	 */
	public void logout(HttpServletRequest req, HttpServletResponse res) {
		HttpSession sess = req.getSession();
		sess.setAttribute("qlid", null);
		sess.setAttribute("token", null);
//		sess.invalidate();
		System.out.println("Logged Out!");
	}
	
	/**
	 * isLoginValid()
	 * @param req
	 * @return boolean
	 * 
	 * 		Returns true if a user is logged in, false otherwise....
	 */
	public static boolean isLoginValid(HttpServletRequest req) {
		HttpSession sess = req.getSession();
		String loginToken = (String) sess.getAttribute("token");
		
		if(sess.getAttribute("qlid") == null) {
			System.out.println("Checking qlid: " + sess.getAttribute("qlid"));
			System.out.println("Login Invalid!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * buildRandomToken()
	 * @param length
	 * @return String
	 * 
	 * 		Builds a random string of length 'length', used as a password token
	 */
	public String buildRandomToken(int length) {
		StringBuilder strB = new StringBuilder(length);
		int ranIndx;
		Random rand = new Random();
		
		for(int i=0; i<length; ++i) {
			ranIndx = rand.nextInt(allowedLen);
			strB.append(allowedChars.charAt(ranIndx));
		}		
		
		return strB.toString();
	}
	
	/**
	 * addLoginToDB()
	 * @param req
	 * @param qlid
	 * 
	 * 		Add an entry to the db login log table along with mode of login
	 * 	(i.e. Mobile/Web)
	 */
	public void addLoginToDB(HttpServletRequest req, String qlid, char mode) {
		DBConnectionUpd dbConnection = new DBConnectionUpd();
		Connection con = dbConnection.getConnection();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(
				"INSERT INTO "+ DBTables.LOGIN_HISTORY
				+ " (emp_qlid, lh_loginmode, lh_lanid, lh_created_by, lh_last_updated_by,"
				+ "lh_creation_date, lh_last_update_date) VALUES ("
				+ "?, ?, ?, ?, ?, CURDATE(), CURDATE())"
			);
			
			long ip = 0;
			String ipString = req.getRemoteAddr();
//			String ipNums[];
//			if(ipString.matches(":")) {
//				ipNums = ipString.split(":");
//			}else {
//				ipNums = ipString.split("\\.");
//			}
			
			Calendar cal = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			System.out.println("IpNums: " + ipNums);
//			
//			for(int i=0; i<ipNums.length; ++i) {
//				ip += Integer.parseInt(ipNums[i]) << (24 - (8*i));
//			}
			
			//// TODO: add something to really tell the login mode!
			ps.setString(1, qlid);
			ps.setString(2, mode+"");
			//// TODO: this method of gathering IP address does not work with proxy...
			ps.setString(3, ipString);
			ps.setString(4, "SYSTEM");
			ps.setString(5, "SYSTEM");
//			ps.setString(6, df.format(cal));
//			ps.setString(7, df.format(cal));
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * hasSetPassword()
	 * @param qlid
	 * @return boolean
	 * 
	 * 		Returns true if user has ever set password, false otherwise
	 */
	public boolean hasSetPassword(String qlid) {
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = con.prepareStatement(
				"SELECT login_creation_date FROM "+DBTables.LOGIN_CREDENTIALS+" WHERE "
				+ "emp_qlid = ?"
			);
			ps.setString(1, qlid);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	/**
	 * setOldTokensInvalid()
	 * @param qlid
	 * 		
	 * 		Set the status of all password tokens for the given qlid to 'I'
	 * 	(i.e. 'Invalid')
	 */
	public void setOldTokensInvalid(String qlid) {
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		Calendar now = Calendar.getInstance();
		Date today = new Date(now.getTimeInMillis());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			PreparedStatement ps = con.prepareStatement(
				"UPDATE "+DBTables.PASSWORD_TOKEN+" SET "
				+ "pwd_token_status = 'I', pwd_token_last_updated_by = ?,"
				+ "pwd_token_last_update_date = ? "
			);
			ps.setString(1, "SYSTEM");
			ps.setString(2, dateFormat.format(today));
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error while invalidating old tokens....");
			e.printStackTrace();
		}
	}
	
	/**
	 * isPwdTokenValid()
	 * @param qlid
	 * @param token
	 * @return boolean
	 * 
	 * 		Returns true if token for the given qlid exists and is not expired.
	 */
	public boolean isPwdTokenValid(String qlid, String token) {
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = con.prepareStatement(
				"SELECT pwd_token_id FROM "+DBTables.PASSWORD_TOKEN+" "
				+ "WHERE emp_qlid = ? "
				+ "AND pwd_token = ? "
				+ "AND pwd_token_expiry >= NOW() "
				+ "AND pwd_token_status = 'A'"
			);
			ps.setString(1, qlid);
			ps.setString(2, token);
			rs = ps.executeQuery();
			if(rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Exception!!");
			return false;
		}
		
		return false;
	}
	
	/**
	 * setNewPasswordToken()
	 * @param qlid
	 * @param pwdToken
	 * @return boolean
	 * 
	 * 		Set the password token for user to set new password
	 */
	public boolean setNewPasswordToken(String qlid, String pwdToken) {
		String accType = "N";
		
		if(validateUser(qlid)) {
			//// If password is set, tell the user to use "Forgot Password" instead....
			if(isPasswordSet(qlid)) {
				System.out.println("The password is already set!");
//				return false;
				accType = "O";
			}
			
			DBConnectionUpd dbCon = new DBConnectionUpd();
			Connection con;
			PreparedStatement ps;
			Calendar now = Calendar.getInstance();
			long nowMillis = now.getTimeInMillis();
			Date today = new Date(nowMillis);
			//// set expiry time to be 10 minutes from now....
			Date pwdTokenExpiry = new Date(nowMillis + 600000);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			con = dbCon.getConnection();
			try {
				ps = con.prepareStatement(
					"INSERT INTO "+DBTables.PASSWORD_TOKEN+" ("
						+ "emp_qlid, pwd_token, pwd_token_expiry, account_type, pwd_token_created_by, "
						+ "pwd_token_last_updated_by, Pwd_Token_Creation_Date, "
						+ "Pwd_Token_Last_Update_Date) VALUES (?,?,?,?,?,?,CURDATE(),CURDATE())"
				);
				ps.setString(1, qlid);
				ps.setString(2, pwdToken);
				ps.setString(3, df.format(pwdTokenExpiry));
				ps.setString(4, accType);
				ps.setString(5, "SYSTEM");
				ps.setString(6, "SYSTEM");				
				ps.executeUpdate();
				
			} catch (SQLException e) {
				System.out.println("An exception occured...");
				e.printStackTrace();
				return false;
			}	
			
			System.out.println("Success!");			
			return true;
		}
		
		return false;
	}
	
	/**
	 * insertforgotpasswordDetails()
	 * @param qlid
	 * @param token
	 * @return boolean
	 * 
	 * 		Set the password token for user in case of forgot password....
	 */
	public boolean insertforgotpasswordDetails(String qlid, String token) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");				
			Date currentDate = new Date();				
			
			DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd");				
			Date currentDate1 = new Date();
			String curDate=dateFormat1.format(currentDate1);
		
		    Calendar c = Calendar.getInstance();
	        c.setTime(currentDate);
	        c.add(Calendar.MINUTE, 10);
	        Date currentDatePlusTen = c.getTime();
		        
	        String expDate=  dateFormat.format(currentDatePlusTen);
		      
			Connection connection = (new DBConnectionUpd()).getConnection();
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO ncabdb.ncab_password_token_tbl("
				+ "Pwd_Token,Pwd_Token_Expiry, Emp_Qlid, Pwd_Token_Status,Pwd_Token_Created_By,"
				+ "Pwd_Token_Creation_Date,Pwd_Token_Last_Updated_By,Pwd_Token_Last_Update_Date,"
				+ "Account_Type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'O')");
			
			ps.setString(1, token);
			ps.setString(2, expDate);
			ps.setString(3, qlid);
			ps.setString(4, "A");
			ps.setString(5, "System");
			ps.setString(6, curDate);				
			ps.setString(7, "System");
			ps.setString(8, curDate);
			
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	public boolean sendMessage(
			String recepient,
			String subject,
			String messageBody
		) {	
		String host = "localhost";
		Properties props = new Properties();
    	props.put("mail.smtp.host", "smtp.gmail.com");
	//	props.put("mail.smtp.host", "smtp.sendgrid.net");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		    
		Session mySession = Session.getInstance(props, new Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication("javamailsystem1@gmail.com","javamail1");
	    	//	return new PasswordAuthentication(
	    	//			"apikey",
	    	//			"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"
			//	);
		}});		    
	    try{
	    	String from="iNCRediCabs-Admin@ncr.com";
	    	MimeMessage message = new MimeMessage(mySession);
	    	message.setFrom(new InternetAddress(from));
	    	message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recepient));	  
	    	message.setSubject(subject);
	    	message.setContent(messageBody, "text/html; charset=utf-8");
	    	Transport.send(message);
	    	System.out.println("SendMailService: Message Sent!");
	    	return true;
	    }catch( HeadlessException | MessagingException e){
	    	e.printStackTrace();
	    	return false;
	    }
	}
	

	/**
	 * getAllRoles()
	 * @return JSONObject
	 * 
	 *		Returns JSONObject containing all roles in the roles master table.		
	 */
	public JSONObject getAllRoles() {
		JSONObject jsObj = new JSONObject();
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(
				"SELECT roles_id, roles_name FROM "+DBTables.ROLES
			);
			ResultSet rs;
			rs = ps.executeQuery();
			while(rs.next()) {
				jsObj.put(rs.getString("roles_id"), rs.getString("roles_name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsObj;
	}


	public JSONObject insertIntoDatabase(InputStream fileInputStream, FormDataContentDisposition fileFormDataContentDisposition, String qlid)
			throws IOException {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		DateFormat logDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		EmployeeBean empBean = new EmployeeBean();
		
		JSONArray jsArr = new JSONArray();
		JSONArray jsValidateArr = new JSONArray();
		JSONObject validateJSON = new JSONObject();
		JSONObject js2;
		Iterator jsonKeys;
		
		RowCheck rowcheck = new RowCheck();
		Calendar cal = Calendar.getInstance();
		String logStartStr = logDateFormat.format(new Date());
		
		String logFileName = LOGFILE_DIR + "/" + LOGFILE_PREFIX +
						logStartStr.replaceAll("\\s", "_").replaceAll(":", "-") + ".txt";
		
		File logDir = new File(LOGFILE_DIR);
		
		if(!logDir.exists()) {
			logDir.mkdir();
		}
		
		File file = new File(logFileName);
		if(!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter f0 = null;
//		FileWriter fw;
		
	    int last_row_valid = 0;
		int index = 1;
	    int ct = 0;
	    int totalRows = 0;
	    int totalSuccessful = 0;

	    String[] qlid_arr = null;
	
		HashMap<String, String> link = new HashMap<>();	
	    XSSFWorkbook workbook = null;
	    Sheet sheet = null;
	    
		boolean success = true;
		boolean allSuccess = true;
		boolean empAlreadyExists = false;
		
		
		try {
			f0 = new FileWriter(file);
			f0.write("iNCRediCabs EMPLOYEE MASS UPLOAD LOG");
			f0.write("\n---------------------------------------------------");
			f0.write("\nLog recorded on: " + logDateFormat.format(new Date()));
			f0.write("\nAdmin QLID: " + qlid.toUpperCase());
			
			if (fileFormDataContentDisposition.getFileName().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(fileInputStream);
			} else {
				workbook = new XSSFWorkbook(fileInputStream);
				System.out.println("Yes check succeed for other file type");
			}
			
			sheet = workbook.getSheetAt(0);
	
			for (int i = sheet.getLastRowNum(); i >= 0; i--) {
				Row row_check_test = (Row) sheet.getRow(i);
				boolean flag = RowCheck.isRowEmpty(row_check_test);
				if (flag == true) {
					continue;
				} else {
					last_row_valid = i;
					break;
				}
			}
			qlid_arr = new String[last_row_valid];
			
			String newLine = System.getProperty("line.separator");
		}catch(FileNotFoundException c){			//// for new FileWriter(file) statement
			System.out.println("File not found!");
			return (new JSONObject())
						.put("success", false)
						.put("message", "Log file could not be created, aborting...");
		}catch(Exception e) {
			e.printStackTrace();
		}
		

		f0.write("\nFile Name: " + fileFormDataContentDisposition.getFileName());
		f0.write("\nTotal number of rows in file: "+(last_row_valid+1));
		f0.write("\n---------------------------------------------------\n");
		
		Row row;
		for (int i = 0; i <= last_row_valid; i++) {
			success = true;
			empAlreadyExists = false;
			f0.write("\nROW " + (i+1)+ ":["+logDateFormat.format(new Date())+"] >>>> ");
			validateJSON = null;			
			
			try {
				row = (Row) sheet.getRow(i);
				String creation_date = "" + row.getCell(18, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getDateCellValue();
				DateFormat formatter1 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
				Date date2 = (Date)formatter1.parse(creation_date);			 
				
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(date2);
				String formatedDate1 = cal1.get(Calendar.YEAR) + "-" + (cal1.get(Calendar.MONTH) + 1) + "-" + cal1.get(Calendar.DATE);
				String last_updated_date = "" + row.getCell(20, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getDateCellValue();
				DateFormat formatter2 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
	            Date date3 = (Date)formatter1.parse(last_updated_date);
	            
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(date3);
				String formatedDate2 = cal2.get(Calendar.YEAR) + "-" + (cal2.get(Calendar.MONTH) + 1) + "-" + cal2.get(Calendar.DATE);
				
				empBean.setEmpQlid(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpMgrQlid1(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpMgrQlid2(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpOrgId(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpFName(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpMName(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpLName(row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpGender(row.getCell(7,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpMobNbr(((long)row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue()+"").trim());
				empBean.setEmpHomeNbr(((long)row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue()+"").trim());
				empBean.setEmpEmergNbr(((long)row.getCell(10,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue()+"").trim());
				empBean.setEmpAddLine1(row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpAddLine2(row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpPin((int)row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
				empBean.setEmpPickupArea(row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpStatus(row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpBloodGrp(row.getCell(16, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpCreatedBy(row.getCell(17, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim());
				empBean.setEmpCreationDate(formatedDate1);
				empBean.setEmpLastUpdatedBy(row.getCell(19, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
				empBean.setEmpLastUpdateDate(formatedDate1);
				empBean.setRolesId((int)row.getCell(21,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue()+"");
				empBean.setEmpZone(row.getCell(22, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
				
				if(empBean.getEmpAddLine1().length() > 60) {
					empBean.setEmpAddLine1(empBean.getEmpAddLine1().substring(0, 60));
				}				

				if(empBean.getEmpAddLine2().length() > 60) {
					empBean.setEmpAddLine2(empBean.getEmpAddLine2().substring(0, 60));
				}			
				
				if(empBean.getEmpHomeNbr().equals("0")) {
					System.out.println("Home no is 0");
					empBean.setEmpHomeNbr(null);
				}
				
				validateJSON = validateAddEmployeeFormData(empBean);
				System.out.println("Home Number: " + empBean.getEmpHomeNbr());
				jsonKeys = validateJSON.keys();
				
				String key;
				while(jsonKeys.hasNext()) {
					key = jsonKeys.next().toString();
					if(key != "message" && key != "success") {
						js2 = validateJSON.getJSONObject(key);
						if(Boolean.parseBoolean(js2.getString("error"))) {
							success = false;
							f0.write("\n\tERROR: \"" + js2.getString("message") +"\" \n\t\tVALUE: "
									+js2.getString("value"));
						}
					}
				}
				
				String its = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				System.out.println("ITS: " + its);
				
				PreparedStatement psCheck = connection.prepareStatement(
						"SELECT emp_qlid FROM "+DBTables.EMPLOYEE+
							" WHERE emp_qlid = ?"
						);
				psCheck.setString(1, empBean.getEmpQlid());
				ResultSet rsCheck = psCheck.executeQuery();
				while(rsCheck.next()) {
					if(rsCheck.getString("emp_qlid") != null) {
						empAlreadyExists = true;						
					}
				}

				connection.setAutoCommit(false);

				if(success) {
					PreparedStatement ps;
					if(empAlreadyExists) {
						System.out.println("already exists! updating....");
						ps = connection.prepareStatement(
							"UPDATE "+DBTables.EMPLOYEE+" SET "+
								"Emp_Mgr_Qlid1 = ?, " + 
								"Emp_Mgr_Qlid2 = ?, Emp_Org_Id = ?, Emp_Fname = ?, Emp_Mname = ?, Emp_Lname = ?,"+
								"Emp_Gender = ?, Emp_Mob_Nbr = ?, Emp_Home_Nbr = ?, Emp_Emerg_Nbr = ?, Emp_Add_Line1 = ?, "+
								"Emp_Add_Line2 = ?, Emp_PIN = ?, Emp_Pickup_Area = ?, Emp_Status = ?, Emp_BloodGrp = ?,"+
								"Emp_Created_By = ?, Emp_Creation_Date = ?, Emp_Last_Updated_By = ?, Emp_Last_Update_Date = ?,"+
								"Roles_Id = ?, Emp_Zone = ?"
								+ " WHERE emp_qlid = ?"
						);						
						
						ps.setString(1, empBean.getEmpMgrQlid1());
						ps.setString(2, empBean.getEmpMgrQlid1());
						ps.setString(3, its);
						ps.setString(4, empBean.getEmpFName());
						ps.setString(5, empBean.getEmpMName());
						ps.setString(6, empBean.getEmpLName());
						ps.setString(7, empBean.getEmpGender());
						ps.setString(8, empBean.getEmpMobNbr());
						ps.setString(9, empBean.getEmpHomeNbr());
						ps.setString(10, empBean.getEmpEmergNbr());
						ps.setString(11, empBean.getEmpAddLine1());
						ps.setString(12, empBean.getEmpAddLine2());
						ps.setInt(13, empBean.getEmpPin());
						ps.setString(14, empBean.getEmpPickupArea());
						ps.setString(15, empBean.getEmpStatus());
						ps.setString(16, empBean.getEmpBloodGrp());
						ps.setString(17, empBean.getEmpCreatedBy());
						ps.setString(18, empBean.getEmpCreationDate());
						ps.setString(19, empBean.getEmpCreatedBy());
						ps.setString(20, empBean.getEmpLastUpdateDate());
						ps.setString(21, empBean.getRolesId());
						ps.setString(22, empBean.getEmpZone());
						ps.setString(23, empBean.getEmpQlid());
						ps.executeUpdate();
						ps.close();
						
						connection.commit();
					
						System.out.println("Success import excel to mysql table");
						jsArr.put(createJSON(empBean));
						++totalSuccessful;						
					}else {
						System.out.println("Does not exist in db, Inserting....");
						ps = connection.prepareStatement(
							"INSERT into "+DBTables.EMPLOYEE+" (Emp_Qlid, Emp_Mgr_Qlid1,"
								+ "Emp_Mgr_Qlid2, Emp_Org_Id, Emp_Fname, Emp_Mname, Emp_Lname, Emp_Gender,"
								+ "Emp_Mob_Nbr, Emp_Home_Nbr, Emp_Emerg_Nbr, Emp_Add_Line1, Emp_Add_Line2,"
								+ "Emp_PIN, Emp_Pickup_Area, Emp_Status, Emp_BloodGrp, Emp_Created_By,"
								+ "Emp_Creation_Date, Emp_Last_Updated_By, Emp_Last_Update_Date, Roles_Id,"
								+ "Emp_Zone) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
								+ "?, ?, ?, ?, ?, ?)"
						);
			
						ps.setString(1, empBean.getEmpQlid());
						ps.setString(2, empBean.getEmpMgrQlid1());
						ps.setString(3, empBean.getEmpMgrQlid1());
						ps.setString(4, its);
						ps.setString(5, empBean.getEmpFName());
						ps.setString(6, empBean.getEmpMName());
						ps.setString(7, empBean.getEmpLName());
						ps.setString(8, empBean.getEmpGender());
						ps.setString(9, empBean.getEmpMobNbr());
						ps.setString(10, empBean.getEmpHomeNbr());
						ps.setString(11, empBean.getEmpEmergNbr());
						ps.setString(12, empBean.getEmpAddLine1());
						ps.setString(13, empBean.getEmpAddLine2());
						ps.setInt(14, empBean.getEmpPin());
						ps.setString(15, empBean.getEmpPickupArea());
						ps.setString(16, empBean.getEmpStatus());
						ps.setString(17, empBean.getEmpBloodGrp());
						ps.setString(18, empBean.getEmpCreatedBy());
						ps.setString(19, empBean.getEmpCreationDate());
						ps.setString(20, empBean.getEmpCreatedBy());
						ps.setString(21, empBean.getEmpLastUpdateDate());
						ps.setString(22, empBean.getRolesId());
						ps.setString(23, empBean.getEmpZone());
						ps.executeUpdate();
						ps.close();
						
						connection.commit();
					
						System.out.println("Success import excel to mysql table");
						jsArr.put(createJSON(empBean));
						++totalSuccessful;
					}
				}else {
					System.out.println("***** Validation Error!");
				}
			}catch (Exception e) {		
				System.out.println(e.toString());
			    e.printStackTrace();
			    success = false;
		    }
			
			if(success) {				
				f0.write("\n\tROW ADD SUCCEEDED");				
			}else {
				allSuccess = false;
				f0.write("\n\tROW ADD FAILED");
				
			}
		}

		workbook.close();
		
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		f0.close();
		
		System.out.println("totalRows: " + (last_row_valid+1));
		System.out.println("totalSuccessful: " + totalSuccessful);
		System.out.println("totalFailed: " + (last_row_valid - totalSuccessful));
		
//		System.out.println("Sending log to " + qlid + "@ncr.com");
//		if(!qlid.equals(DEFAULT_QLID)) {
//		sendAttachment("ag250497@ncr.com", logFileName);
//		}else {
//			System.out.println("could not send email to: " + qlid);
//		}
		
		return (new JSONObject())
					.put("success", allSuccess)
					.put("totalRows", (last_row_valid+1))
					.put("totalSuccessful", totalSuccessful)
					.put("totalFailed", (last_row_valid + 1 - totalSuccessful))
					.put("successfulUpload", jsArr);
	}
	
	public boolean sendAttachment(String toAddress, String filePath) {
		String host = "localhost";
		Properties props = new Properties();
    	props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		    
		Session mySession = Session.getInstance(props, new Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication("javamailsystem1@gmail.com","javamail1");
		}});		    
	    try{
	    	String from="iNCRediCabs-Admin@ncr.com";
	    	MimeMessage message = new MimeMessage(mySession);
	    	message.setFrom(new InternetAddress(from));
	    	message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));	  
	    	message.setSubject("iNCRediCabs Employee Mass Upload LOG");
	    	
	    	BodyPart bp = new MimeBodyPart();
	    	bp.setText("Please find attached the log file to the mass upload action performed by you!");
	    	
	    	Multipart mp = new MimeMultipart();
	    	mp.addBodyPart(bp);
	    	
	    	bp = new MimeBodyPart();
	    	DataSource ds = new FileDataSource(filePath);
	    	bp.setDataHandler(new DataHandler(ds));
	    	bp.setFileName(filePath);
	    	mp.addBodyPart(bp);  	
	    	
	    	
	    	message.setContent(mp);
	    	
	    	Transport.send(message);
	    	System.out.println("SendMailService: Message Sent!");
	    	return true;
	    }catch( HeadlessException | MessagingException e){
	    	e.printStackTrace();
	    	return false;
	    }
		
	}

	/**
	 * getManagerDetailsEmployee()
	 * 
	 * 		returns level1 and level2 manager names for the signed in user
	 * @return
	 */
	public EmployeeBean[] getManagerDetailsEmployee(String qlid) {		
		EmployeeBean emp = getEmployeeFromQLID(qlid);
		
		EmployeeBean[] mgrArr = new EmployeeBean[2];
		mgrArr[0] = getEmployeeFromQLID(emp.getEmpMgrQlid1());
		mgrArr[1] = getEmployeeFromQLID(emp.getEmpMgrQlid2()); 
		
		return mgrArr;
	}
	
	public JSONObject getDriverInfoForEmployee(String qlid) {
		JSONObject jsObj = new JSONObject();
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		PreparedStatement ps;
		ResultSet rs;
		
		
		try {
			ps = con.prepareStatement(
				"select driver_name, d_contact_num from ncab_driver_master_tbl where driver_id = "
				+ "(select driver_id from ncab_roster_tbl where "
				+ "emp_qlid = ? and emp_status = 'active')"
			);
			
			ps.setString(1, qlid);
			rs = ps.executeQuery();
			if(rs.next()) {
				jsObj.put("driverName", rs.getString("driver_name"))
					.put("driverContact", rs.getString("d_contact_num"));				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsObj;
	}
	
	public JSONObject changePassword(String qlid, SetPasswordBean spb) {
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		PreparedStatement ps, setPassPs;
		ResultSet rs;
		
		System.out.println("current pass: " + spb.getCurrentPassword());
		System.out.println("pass1: " + spb.getPassword1());
		System.out.println("pass2: " + spb.getPassword2());
		
		try {
			ps = con.prepareStatement(
				"SELECT login_password from "+DBTables.LOGIN_CREDENTIALS+" WHERE emp_qlid = ?"
			);
			setPassPs = con.prepareStatement(
				"UPDATE "+DBTables.LOGIN_CREDENTIALS+" SET login_password = ?, "
					+ "login_last_update_date = CURDATE(), login_last_updated_by = ? "
				+ " WHERE emp_qlid = ?"
			);
			setPassPs.setString(2, qlid);
			setPassPs.setString(3, qlid);

		ps.setString(1, qlid);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				if(BCrypt.checkpw(spb.getCurrentPassword(), rs.getString("login_password"))){
					if(spb.getPassword1().equals(spb.getPassword2())) {
						setPassPs.setString(1, BCrypt.hashpw(spb.getPassword1(), BCrypt.gensalt()));
						setPassPs.executeUpdate();
						return (new JSONObject())
									.put("success", true)
									.put("message", "Password has been updated successfully!");
					}else {
						return (new JSONObject())
								.put("success", false)
								.put("message", "Both passwords should be same!");						
					}
				}else {
					return (new JSONObject())
								.put("success", false)
								.put("message", "Current Password is invalid!");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return (new JSONObject())
					.put("success", false)
					.put("message", "Some error occured!");
	}
	
	public JSONArray getShiftJSONArray() {
		System.out.println("Getting Shift Info....");
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		PreparedStatement ps;
		ResultSet rs;
		JSONArray jsArr = new JSONArray();
		
		try {
			ps = con.prepareStatement(
				"SELECT * FROM NCAB_SHIFT_MASTER_TBL"
			);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				jsArr.put(
						(new JSONObject())
							.put("shiftId", rs.getString("shift_id"))
							.put("startTime", rs.getString("start_time"))
							.put("endTime", rs.getString("end_time"))
							.put("shiftName", rs.getString("shift_name"))
				);	
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return jsArr;
	}
	
/// Employee Dashboard

	public JSONObject employeeDash(String qlid) {
		// TODO Auto-generated method stub
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		
		RosterServiceImpl rstrImpl= new RosterServiceImpl();
		JSONObject jsObj = new JSONObject();
		
		EmployeeBean empBean;
		EmployeeBean mgr1;
		EmployeeBean mgr2;
		
		JSONArray rosterInfoTemp = rstrImpl.showRosterInfo(
				(new JSONObject())
				.put("qlid", qlid)
				.put("c_n", "")
				.put("s_i", "")
				.put("e_n", "")
				.put("vname", "")
			);
		JSONArray shiftTemp = getShiftJSONArray();
		
		JSONArray rosterInfo = new JSONArray();
		JSONArray shiftArr = new JSONArray();		
		
//		if(rosterInfo.length() == 0) {
//			rosterInfo.put(
//					(new JSONObject())
//						.put("", value))
//		}
		
		JSONObject tmp;
		
		for(int i=0; i<rosterInfoTemp.length(); ++i) {
			tmp = (JSONObject) rosterInfoTemp.get(i);
			if(tmp == null) {
				tmp = new JSONObject();
			}
			if(!tmp.has("Qlid")) {
				tmp.put("Qlid", "");
			}
			if(!tmp.has("m_name")) {
				tmp.put("m_name", "");
								
			}
			if(!tmp.has("p_a")) {
				tmp.put("p_a", "");				
			}
			if(!tmp.has("Cab_number")) {
				tmp.put("Cab_number", "");				
			}
			if(!tmp.has("shift_id")) {
				tmp.put("shift_id", "");
				
			}
			if(!tmp.has("l_name")) {
				tmp.put("l_name", "");
				
			}
			if(!tmp.has("f_name")) {
				tmp.put("f_name", "");
				
			}
			if(!tmp.has("vendor_name")) {
				tmp.put("vendor_name", "");
				
			}
			if(!tmp.has("e_mob")) {
				tmp.put("e_mob", "");
				
			}
			if(!tmp.has("pickup_time")) {
				tmp.put("pickup_time", "");
				
			}
			if(!tmp.has("Route_number")) {
				tmp.put("Route_number", "");				
			}
			if(!tmp.has("occu_left")) {
				tmp.put("occu_left", "");				
			}
			
			if(!tmp.has("roster_id")) {
				tmp.put("roster_id", "");				
			}
			 
			
			rosterInfo.put(tmp);
		}
		
		for(int i=0; i<shiftTemp.length(); ++i) {
			tmp = shiftTemp.getJSONObject(i);
			if(tmp == null) {
				tmp = new JSONObject();
			}
			if(!tmp.has("shiftId")) {
				tmp.put("shiftId", "");				
			}
			if(!tmp.has("shiftName")) {
				tmp.put("shiftName", "");				
			}
			if(!tmp.has("startTime")) {
				tmp.put("startTime", "");				
			}
			if(!tmp.has("endTime")) {
				tmp.put("endTime", "");				
			} 
			
			shiftArr.put(tmp);
		}
				
		
		empBean = getEmployeeFromQLID(qlid);
		mgr1 = getEmployeeFromQLID(empBean.getEmpMgrQlid1());
		mgr2 = getEmployeeFromQLID(empBean.getEmpMgrQlid2());
		
		
		jsObj = createJSON(empBean);
		jsObj.put("mgr1Name", mgr1.getEmpFName() + " " + mgr1.getEmpMName() + " " + mgr1.getEmpLName())
				.put("mgr1Contact", mgr1.getEmpMobNbr())
				.put("mgr2Contact", mgr2.getEmpMobNbr())
				.put("mgr2Name", mgr2.getEmpFName() + " " + mgr2.getEmpMName() + " " + mgr2.getEmpLName())
				.put("rosterInfo", rosterInfo)
				.put("driverDetails", getDriverInfoForEmployee(qlid))
				.put("contacts", getContactJSONArray())
				.put("shiftInfo", shiftArr);
		
		return jsObj;
	}

	/**
	 * validateUserCurrPassword()
	 * @param qlid
	 * @param current password
	 * @return boolean
	 * 
	 * 		returns true if user with the given qlid and Current Password exists....
	 */
	
	public boolean validateUserCurrPassword(String qlid, String currpassword) {
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		
		//// return false if the qlid string does not match the qlid format
		if(!qlid.matches("^[a-zA-Z]{2}\\d{6}$")) {
			return false;
		}
		
		try{
			PreparedStatement ps = con.prepareStatement(
				"select * from "+DBTables.LOGIN_CREDENTIALS+" where Emp_Qlid= ? and Login_Password=?"
			);
			ps.setString(1, qlid);
			ps.setString(2, currpassword);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}			
		} catch (SQLException e) {
			System.out.println("SQL ERROR!");
			e.printStackTrace();
						
		}		
		return false;
	}
	
	public boolean addEntryToSOSTable(SOSRequestBean srb) {
		//// TODO get cab licenseplate no
		
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		
		try {
			
			PreparedStatement ps = con.prepareStatement(
					"INSERT INTO "+DBTables.SOS+" SET emp_qlid = ?, sos_date_time = CURDATE(),"
						+ "roster_id = ?, cab_license_plate_no = ?, sos_creation_date = CURDATE(),"
						+ "sos_last_update_date = CURDATE(), sos_last_updated_by = 'SYSTEM'"
				);
			
			ps.setString(1, srb.getEmpQlid());
			ps.setString(2, srb.getRosterId());
			ps.setString(3, srb.getCabLicensePlateNo());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	public boolean setPushTokenAndroid(PushTokenBean ptb) {
		System.out.println("push token: qlid: " + ptb.getQlid());
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		PreparedStatement ps, psVerify;
		ResultSet rs, rsVerify;
		boolean loginExists = false;
		boolean pushTokenUpdateSuccessful = false;
		try {
			//// first verify whether the row is present in the db or not
			psVerify = con.prepareStatement(
					"SELECT login_push_token FROM "+DBTables.LOGIN_CREDENTIALS+""
							+ " WHERE emp_qlid = ?"
					);
			psVerify.setString(1, ptb.getQlid());
			
			rsVerify = psVerify.executeQuery();
			
			while(rsVerify.next()) {
				loginExists = true;
			}
			
			if(loginExists) {
				ps = con.prepareStatement(
					"UPDATE "+DBTables.LOGIN_CREDENTIALS+" SET login_push_token = ? "
						+ "WHERE emp_qlid = ?"		
				);
				
				ps.setString(1, ptb.getPushToken());
				ps.setString(2, ptb.getQlid());
				ps.executeUpdate();
				
				pushTokenUpdateSuccessful = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pushTokenUpdateSuccessful;
	}
	
	public String getPushTokenAndroid(PushTokenBean ptb) {
		String loginPushToken = "";
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = con.prepareStatement(
				"SELECT login_push_token FROM "+DBTables.LOGIN_CREDENTIALS+
					" WHERE emp_qlid = ?"
			);
			ps.setString(1, ptb.getQlid());
			
			rs= ps.executeQuery();
			
			while(rs.next()) {
				loginPushToken = rs.getString("login_push_token");
				break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loginPushToken;
	}

	public JSONObject disableContact(ContactBean contactBean) {				
		try {
			Connection connection = (new DBConnectionUpd()).getConnection();
			PreparedStatement ps = connection.prepareStatement(
				"update "+DBTables.CONTACTS +"  set contact_sos_status='I' Where "
				+ "contact_id = ?"
			);
			ps.setInt(1, contactBean.getContactId());			
			ps.executeUpdate();			
			
			System.out.println("Contact disable!");
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e);
			return (new JSONObject())
					.put("success", false)
					.put("message", "Contact can't be disable!");
		}
	 
		return (new JSONObject())
				.put("success", true)
				.put("message", "Contact disable!");
	}
	
	
	public boolean validateContactId(ContactBean contactBean) {		
		DBConnectionUpd dbCon = new DBConnectionUpd();
		Connection con = dbCon.getConnection();
		
		try{
			PreparedStatement ps = con.prepareStatement(
					"UPDATE "+ DBTables.CONTACTS +" SET contact_ WHERE contact_id = ?"	);
			ps.setInt(1, contactBean.getContactId());
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}			
		} catch (SQLException e) {
			System.out.println("SQL ERROR!");
			e.printStackTrace();
						
		}
		return false;	
	}
	
	
	public JSONObject addContact(ContactBean contactBean) {
		try {
			Connection connection = (new DBConnectionUpd()).getConnection();
			PreparedStatement ps = connection.prepareStatement(
			"insert into "+ DBTables.CONTACTS +"(contact_nbr, contact_name,contact_sos,contact_role,contact_sos_priority,contact_sos_status) values(?,?,?,?,?,'A')"
			);	
			System.out.println("SQL update executed succesfully!");
	
			ps.setString(1, contactBean.getContactNbr() );
			ps.setString(2, contactBean.getContactName());
			ps.setString(3, contactBean.getContactSos() );
			ps.setString(4, contactBean.getContactRole());
			ps.setInt(5,    contactBean.getContactSosPriority());			
			
			ps.executeUpdate();
			
			
			System.out.println("SQL insert query executed succesfully!");
			
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e);
			return (new JSONObject())
					.put("success", false)
					.put("message", "Error in inserting Contact!");
		}
		return (new JSONObject())
				.put("success", true)
				.put("message", "Contact Number successfully Inserted!");
		
	}
	
	public JSONObject editContact(ContactBean contactBean) {
		try {
			Connection connection = (new DBConnectionUpd()).getConnection();
			PreparedStatement ps = connection.prepareStatement(
			"UPDATE "+ DBTables.CONTACTS +" SET contact_nbr=?, contact_name=?, contact_sos=?,"
				+ "contact_role=?, contact_sos_priority=?, contact_status=?"
				+ " WHERE contact_id = ?"
			);
			
			System.out.println("SQL update executed succesfully!");			
	
			ps.setString(1, contactBean.getContactNbr() );
			ps.setString(2, contactBean.getContactName());
			ps.setString(3, contactBean.getContactSos() );
			ps.setString(4, contactBean.getContactRole());
			ps.setInt(5, contactBean.getContactSosPriority() );
			ps.setString(6, contactBean.getContactStatus());			
			ps.setInt(7, contactBean.getContactId());			
			ps.executeUpdate();			
			
			System.out.println("SQL update executed succesfully!");
			
		}catch(SQLException e) {
			e.printStackTrace();
			return (new JSONObject())
					.put("success", false)
					.put("message", "Error in Updating Contact!");
		}
		return (new JSONObject())
				.put("success", true)
				.put("message", "Contact Number successfully updated!");
	}

	/**
	 * getContactJSONArray()
	
	 * @return ContactJSONArray
	 * 
	 * 		Returns the list of all SOS Contact Number in the JSON array.
	 */
	public JSONArray getContactJSONArray() {
		JSONArray jsArr = new JSONArray();
		
		try {		
			DBConnectionUpd DBConnectionUpd = new DBConnectionUpd();
			Connection connection = DBConnectionUpd.getConnection();
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM "+DBTables.CONTACTS+" where contact_status='A' "
			);
						
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				jsArr.put(
						(new JSONObject())
						.put("contactId", rs.getString("contact_id"))
						.put("contactNbr", rs.getString("contact_nbr"))
						.put("contactName", rs.getString("contact_name"))
						.put("contactSos", rs.getString("contact_sos"))
						.put("contactRole", rs.getString("contact_role"))
						.put("contactSosPriority", rs.getString("contact_sos_priority"))
						.put("contactEmail", rs.getString("contact_email"))
				);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return jsArr;		
	}
}
