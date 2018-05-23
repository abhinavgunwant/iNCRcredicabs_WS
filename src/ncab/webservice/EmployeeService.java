package ncab.webservice;

import ncab.beans.*;


//import javax.servlet.http.HttpS;
//import javax.servlet.http.Cookie;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ncab.beans.EmployeeBean;
import ncab.dao.DBConnectionUpd;
import ncab.dao.impl.EmployeeServiceImpl;
import ncab.dao.impl.RosterServiceImpl;
import ncab.dao.impl.UtilServiceImpl;
import ncab.dao.impl.VendorServiceImpl;

@JsonIgnoreProperties(ignoreUnknown = true)
@Path("/EmployeeService")
public class EmployeeService {	
//	private static final String BASE_URL = "http://localhost:4200";
//	private static final String BASE_URL = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080";
	
	private static final String BASE_URL = "http://incredicabs.in";

	
	private static final String FORGOT_PASSWORD_SET_URL = BASE_URL+"/forgot-password/set-password";
	private static final String NEW_ACC_SETUP_SET_PASSWORD_URL = BASE_URL+"/new-acc-setup/set-password";
	private static final String URL_ALLOWED_ON_NO_SESSION_PATTERN = "login|logout|forgot-password|new-acc-setup|ndroid";
	
	private String _qlid;
	private String _token;
	
	public EmployeeService() {
		_qlid = EmployeeServiceImpl.DEFAULT_QLID;
		_token = EmployeeServiceImpl.DEFAULT_TOKEN;		
	}
	
	/**
	 * checksBeforePath()
	 * 		Executes everytime before a "Path" is hit.
	 * 	Used to verify login and other info....
	 */
	@Context
	public void checksBeforePath(
			@Context HttpServletRequest req
			) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String token = (String)sess.getAttribute("token");
		
		if(qlid != null && !qlid.equals(EmployeeServiceImpl.DEFAULT_QLID)
				&& token != null && !token.equals(EmployeeServiceImpl.DEFAULT_TOKEN)
				&& EmployeeServiceImpl.isLoginValid(req)){
			
			//// TODO things to do when user is logged in...
		}else {
			System.out.println("qlid: " + qlid + " token: "+ token);
			String url = req.getRequestURL().toString();
			if(!url.matches("upload-file")) {
//			if(url.matches(URL_ALLOWED_ON_NO_SESSION_PATTERN)) {
				sess.setAttribute("qlid", EmployeeServiceImpl.DEFAULT_QLID);
				sess.setAttribute("token", EmployeeServiceImpl.DEFAULT_TOKEN);
				System.out.println("No session found, assigning a dummy session....");
			}
//			}else {
//				System.out.println("No session for you!");
//			}
		}
	}
	
	public Response returnWithError() {
		return Response.ok("You are not logged in!").build();
	}

	@Path("/text")
	@POST	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String text(String str) {
		System.out.println(str);
		return "!!!!LOL";
	}

	@POST
	@Path("/activate-employee")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String activateEmployee(
						EmployeeBean		employeeBean,
			@Context	HttpServletRequest	req		
			){
//		HttpSession sess = req.getSession();
//		String qlid = (String)sess.getAttribute("qlid");
//		String sessRole = (String)sess.getAttribute("role");
		
//		if(qlid == null || qlid == EmployeeServiceImpl.DEFAULT_QLID ||
//				sessRole.toUpperCase() != "ADMIN") {
//			return EmployeeServiceImpl.noLoginMessage();
//		}		
		
		
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		if(empSrvImpl.activateEmployee(employeeBean)) {
			System.out.println("Activate Success!!");
		}else {
			System.out.println("Activate Failed!!");
			return (new JSONObject()).put("success", false).toString();
		}
		
		System.out.println("\nSuccess?\n");
		
		return (new JSONObject()).put("success", true).toString();
	}

	@POST
	@Path("/add-employee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addEmployeePost(
					EmployeeBean		employeeBean,
		@Context	HttpServletRequest	req,
		@Context	HttpServletResponse	res
	) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
		
//		if(qlid == null || qlid == EmployeeServiceImpl.DEFAULT_QLID ||
//				sessRole.toUpperCase() != "ADMIN") {
//			return EmployeeServiceImpl.noLoginMessage();
//		}		
		
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		JSONObject jsObj = new JSONObject();
		JSONObject jsAddResp = new JSONObject();
		
		jsAddResp = empSrvImpl.addEmployee(employeeBean);
		boolean addSuccess = Boolean.parseBoolean(jsAddResp.getString("success"));
		// TODO: add more options to check the errors and report it to user
		if(addSuccess) {
			jsObj.put("success", true)
				.put("message", "Employee has been added to the Database!")
				.put("errorlog", jsAddResp);
		}else {
			jsObj.put("success", false)
				.put(
				"message",
				"Some error occured while adding employee to the Database!\n"
				+ jsAddResp.getString("message")
			);			
		}
		
		return jsObj.toString();
	}

	@POST
	@Path("/check-login-status")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkLoginStatus(
		@Context				HttpServletRequest	req,
//			@Context				HttpServletResponse	res,
		@CookieParam("qlid")	Cookie				cookie,
		@CookieParam("token")	Cookie				tokenCookie
	) {
//		System.out.println("------------checkLoginStatus----------");
		HttpSession sess = req.getSession(true);
		
		if(cookie == null || tokenCookie == null) {
			return Response.ok(
						(new JSONObject())
						.put("login", false)
						.toString()
					).build();
		}
		System.out.println("--------------checkLoginStatus---------------------"
			+ "\n\tSession:"
			+ "\n\t\tQLID: " + sess.getAttribute("qlid")
			+ "\n\t\tToken: " + sess.getAttribute("token")
			+ "\n\tCookie:"
			+ "\n\t\tQLID: " + cookie.getValue()
			+ "\n\t\tToken: " + tokenCookie.getValue());
//		System.out.println("\tSession Context: " + sess.getServletContext().getContextPath());
		if(sess.getAttribute("qlid") == null || cookie.getValue() == null || tokenCookie.getValue() == null) {
			System.out.println("Session or cookie values are null!!");
			return Response.ok(
					(new JSONObject())
						.put("login", false)
						.toString()
			).build();
		}
		
		if(sess.getAttribute("token").equals(tokenCookie.getValue())
				&& sess.getAttribute("qlid").equals(cookie.getValue())
				&& !sess.getAttribute("token").equals(EmployeeServiceImpl.DEFAULT_TOKEN)) {
			System.out.println("Status: Logged-In!");
			return Response.ok(
					(new JSONObject())
						.put("login", true)
						.toString()
					).build();
		}
	
		System.out.println("Session and Cookie token do not match!"
			+ " Cookie Value: " + tokenCookie.getValue()
			+ " Session Value: " + sess.getAttribute("token"));
		
		return Response.ok(
				(new JSONObject())
					.put("login", false)
					.toString()
				).build();
	}
	
	@Path("/deactivate-employee")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String deactivateEmployee(
						EmployeeBean employeeBean,
			@Context	HttpServletRequest	req
			) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
		
//		if(qlid == null || qlid == EmployeeServiceImpl.DEFAULT_QLID ||
//				sessRole.toUpperCase() != "ADMIN") {
//			return EmployeeServiceImpl.noLoginMessage();
//		}		
		
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		if(empSrvImpl.deactivateEmployee(employeeBean)) {
			System.out.println("Deactivate Success!!");
		}else {
			System.out.println("Deactivate Failed!!");
			return (new JSONObject()).put("success", false).toString();
		}
		
		return (new JSONObject()).put("success", true).toString();
	}
	
	@Path("/edit-employee")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String editEmployee(
						EmployeeBean		employeeBean,
			@Context	HttpServletRequest	req
			) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
		
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		
		JSONObject editJSON = empSrvImpl.editEmployee(employeeBean);
		
		return editJSON.toString();
	}
	
	@Path("/forgot-password")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String forgotpasswordEmployeePost(
					UserCredBean		ucb,
		@Context	HttpServletRequest	req,
		@Context	HttpServletResponse	res
	) {
		System.out.println(ucb.getQlid());
		JSONObject			jsonResponse	= new JSONObject();
		JSONObject			jsObj			= new JSONObject();
//		SendMailService		sms				= new SendMailService();
		EmployeeServiceImpl	empSrvImpl		= new EmployeeServiceImpl();		
		UtilServiceImpl utilServiceImpl     = new UtilServiceImpl();
		if (empSrvImpl.validateUser(ucb.getQlid())) {
			
			System.out.println(" forgot testing");
			
			
			
			//// IMPORTANT STEP: Set all the old tokens invalid!
			empSrvImpl.setOldTokensInvalid(ucb.getQlid());
			String from="incredicabs@ncr.com";
			String token = empSrvImpl.buildRandomToken(128);
			String url= BASE_URL+"/#/forgot-password/set-password/"+ucb.getQlid()+"/"+token;
			String recepient = ucb.getQlid()+"@ncr.com";
			String subject="iNCRediCabs: Link to reset password";
			String message = 
				"<html><head><style>#link-box{padding: 10px 0px 10px 0px;margin-top: 10px; background: #dddddd;}" + 
				"code{margin: 0px 10px 0px 10px;}</style></head><body><div class=\"col-sm-8 col-sm-offset-2\">" + 
				"<h1 class=\"text-center\">iNCRediCabs</h1><p>Visit the url below to reset your password:</p>" + 
				"<strong>Note:</strong>Copy the url below and paste in supported browser: Chrome(ver: 50+) or Firefox(ver: 40+)" + 
				"<div id=\"link-box\"><code>"+url+"</code></div></div></body></html> ";
			

			if(utilServiceImpl.sendEmailMessage(from, recepient, "", "", "", subject, message)) {
				
				jsObj.put("success", true)
				    	.put("message", 
						" An Email has been sent to you along with a link to reset your password."
						+ "You have 10 minutes to set your password before this link expires!"
				);
				if(!empSrvImpl.setNewPasswordToken(ucb.getQlid(), token)) {
					jsObj.put("success", false)
						.put("message", "Internal Error....");
					return jsObj.toString();
				}
				System.out.println("Message sent");
				return jsObj.toString();				
				
			}
		
			/*	if(empSrvImpl.sendMessage(recepient, subject, message)) {
				jsObj.put("success", true)
					.put("message", 
					"An Email has been sent to you along with a link to reset your password."
					+ "You have 10 minutes to set your password before this link expires!"
				);
				if(!empSrvImpl.setNewPasswordToken(ucb.getQlid(), token)) {
					jsObj.put("success", false)
						.put("message", "Internal Error....");
					return jsObj.toString();
				}
				System.out.println("Message sent");
				return jsObj.toString();				
			}
*/
			jsObj.put("success", false)
				.put("message", "An error occured!, please try after some time....");

			empSrvImpl.insertforgotpasswordDetails(ucb.getQlid(), token);
//			System.out.println(jsonResponse.get("status"));

			return jsObj.toString();
		}
		
		jsObj.put("success", false).put("message", "Incorrect qlid!");
		System.out.println("User doesn't exist");

		return jsObj.toString();
	}
	
	@Path("/forgot-password/set-password")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)	
	public String forgotPasswordChangeEmployee(UserCredBean ucb){		
		JSONObject jsObj = new JSONObject();
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		
//		System.out.println("\n\nForgot Password------------");
//		System.out.println(ucb.getPassword().toUpperCase());
//		System.out.println(ucb.getQlid().toUpperCase().trim());
//		System.out.println(ucb.getPassword().toUpperCase().matches(ucb.getQlid().toUpperCase().trim()));
		if(ucb.getPassword().toUpperCase().matches(ucb.getQlid().toUpperCase().trim())) {
			return (new JSONObject())
					.put("success", false)
					.put("message", "Password cannot have QLID!")
					.toString();
		}

		if(empSrvImpl.isPwdTokenValid(ucb.getQlid(), ucb.getToken())) {
			if(empSrvImpl.updatePassword(ucb.getQlid(), ucb.getPassword())) {
				empSrvImpl.setOldTokensInvalid(ucb.getQlid());
//				System.out.println("Edit Done!");
				return (new JSONObject())
						.put("success", true)
						.put("message", "Password has been set successfully!")
						.toString();
			}
		}
		System.out.println("Edit Failed!");
		jsObj.put("success", false).put("message", "Edit Failed!");
		return jsObj.toString();
	}
	
	@Path("/employee-dash/reset-password")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)	
	public String passwordChangeEmployee(
						UserCredBean		ucb,
			@Context	HttpServletRequest	req
			){
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
	
		if(ucb.getPassword().toUpperCase().matches(ucb.getQlid().toUpperCase())) {
			return (new JSONObject())
					.put("success", false)
					.put("message", "Password cannot have QLID!")
					.toString();
		}
//		
//		if(qlid == null || qlid == EmployeeServiceImpl.DEFAULT_QLID) {
//			return EmployeeServiceImpl.noLoginMessage();
//		}		
//		
		JSONObject jsObj = new JSONObject();
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();

		//// TODO: verify that user entered his correct old password and confirmed password
		if(empSrvImpl.updatePassword(ucb.getQlid(), ucb.getPassword())) {
			return (new JSONObject())
					.put("success", true)
					.put("message", "Password has been reset successfully!")
					.toString();
		}
		System.out.println("Edit Failed!");
		jsObj.put("success", false).put("message", "Edit Failed!");
		return jsObj.toString();
	}

	@Path("/get-all-managers")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getAllManagers(
		@Context	HttpServletRequest	req,
		@Context	HttpServletResponse	res
	) {
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		JSONArray jsArr = empSrvImpl.getAllManagers();
		
		return jsArr.toString();		
	}
	
	@Path("/get-all-roles")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getAllRoles() {
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		System.out.println(empSrvImpl.getAllRoles().toString());
		return empSrvImpl.getAllRoles().toString();
	}
	
	@Path("/get-manager-by-name")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getManagerByName(
		@Context	HttpServletRequest	req,
		@Context	HttpServletResponse	res,
					ManagerRequestBean	managerRequestBean
	) {
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		JSONArray jsArr = empSrvImpl.getManager(managerRequestBean);
		
		return jsArr.toString();		
	}
	
	@Path("/login")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(
								LoginFormInputBean	loginFormInputBean, 
		@Context				HttpServletRequest	req,
		@Context				HttpServletResponse	res,
		@CookieParam("qlid")	Cookie				qlidCookie,
		@CookieParam("token")	Cookie				tokenCookie			
	) {
		System.out.println("--------------getLoginPost---------------------");
		
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		JSONObject grecaptchaResponseJSON =
				empSrvImpl.grecaptchaVerify(loginFormInputBean.getGrecaptchaResponse());
		
//		if(grecaptchaResponseJSON != null && grecaptchaResponseJSON.getBoolean("success")) {
		if(true) {
		
			HttpSession session = req.getSession(true);

			String qlid = loginFormInputBean.getQlid().trim();
			String password = loginFormInputBean.getPassword();
			String token = empSrvImpl.buildRandomToken(64);
			
			//// if session is old and if qlid is set return "You are already
			//// signed in!" message, or create a fresh session!		
			if(session.getAttribute("qlid") != null
				&& qlidCookie != null
				&& qlidCookie.getValue().equals(session.getAttribute("qlid"))) {
				return Response.ok(
						(new JSONObject())
						.put("login", true)
						.put("message", "You are already signed in!")
						.put("message-type", "error")
						.toString()
				).build();
			}
			
			JSONObject jsObj = empSrvImpl.getLoginAuthJSON(qlid, password);		
			
			//// When login is successful
			if((boolean)jsObj.get("login") == true) {
				_qlid = qlid;
				_token = token;
				System.out.println("_qlid: " + _qlid);
				System.out.println("Login Successful!\nsetting session....");
				session.setAttribute("qlid", qlid);
				session.setAttribute("token", token);
				session.setAttribute("role", jsObj.get("role"));
				
				//// execute sendmail if user is admin....
				if(jsObj.getString("role").toUpperCase().equals("ADMIN")) {
					VendorServiceImpl demodaoimpl = new VendorServiceImpl();	
					
					if(demodaoimpl.sendnotification()){
						System.out.println("Success");
					}else{
						System.out.println("Failed");
					}
				}
	
				System.out.println(
					"\tSession----------\n\t\tqlid: "
					+ session.getAttribute("qlid") + "\n\t\ttoken: "
					+ session.getAttribute("token"));
			}else {
				System.out.println("Login Failed!");
			}
			
			empSrvImpl.addLoginToDB(req, loginFormInputBean.getQlid(), 'W');
			
			return Response.ok(
						jsObj.toString()
					).cookie(
						new NewCookie("qlid", qlid, "/", "", "", 10800, false),
						new NewCookie("token", (String)session.getAttribute("token"), "/", "", "", 7200, false)
					).build();
		}
		
		//// If there was an exception in the validation of the recaptcha
		if(grecaptchaResponseJSON != null && grecaptchaResponseJSON.has("exception")
				&& grecaptchaResponseJSON.getBoolean("exception")) {
			return Response.ok(grecaptchaResponseJSON).build();
		}
		
		return Response.ok(
				(new JSONObject())
					.put("success", false)
					.put("message", "You Must solve the Captcha!")
					.toString()
				).build();
	}
	
	@Path("/get-role")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getRole(
			@Context	HttpServletRequest	req
	) {
		HttpSession sess = req.getSession();
//		String role = sess.getAttribute("role");
		String qlid = (String)sess.getAttribute("qlid");
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		
		EmployeeBean empBean = empSrvImpl.getEmployeeFromQLID(qlid);
		
		System.out.println("Employee FName: " + empBean.getEmpFName());
		
		String role = empSrvImpl.getRoleNameFromRoleID(empBean.getRolesId());
				
		System.out.println(role);
		return (new JSONObject())
					.put("roleName", role)
					.put("empFName", empBean.getEmpFName())
					.toString();
	}
	
	
	@Path("/logout")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(
		@Context				HttpServletRequest	req,
		@Context				HttpServletResponse	res,
		@CookieParam("qlid")	Cookie				qlidCookie,
		@CookieParam("token")	Cookie				tokenCookie
		) {
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		empSrvImpl.logout(req, res);
				
		return Response.ok((new JSONObject())
				.put("logout", true)
				.put("message", "You have been logged out of iNCRediCabs")
				.toString())
				.cookie((new NewCookie("qlid", "")), new NewCookie("token", ""))
				.build();
	}
	
	@Path("/new-acc-setup/set-password")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String newAccSetPassword(UserCredBean ucb) {
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		if(ucb.getPassword().toUpperCase().matches(ucb.getQlid().toUpperCase())) {
			return (new JSONObject())
					.put("success", false)
					.put("message", "Password cannot have QLID!")
					.toString();
		}
		if(empSrvImpl.hasSetPassword(ucb.getQlid())) {
			return (new JSONObject())
					.put("success", false)
					.put("message",
						"Password already set, use \"Forgot Password\" in "
						+ "case you have forgotten your password..."
					).toString();
		}
		
		if(empSrvImpl.isPwdTokenValid(ucb.getQlid(), ucb.getToken())) {
			if(empSrvImpl.setPassword(ucb.getQlid(), ucb.getPassword())) {
				empSrvImpl.setOldTokensInvalid(ucb.getQlid());
				return (new JSONObject())
						.put("success", true)
						.put("message", "Password has been set successfully!")
						.toString();
			}
		}
		
		return (new JSONObject())
				.put("success", false)
				.put("message", "Password could not be set due to an error,"
						+ " please try after some time....")
				.toString();
	}
	
	@Path("/new-acc-setup")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String newAccSetup(UserCredBean	userCredBean) {
		System.out.println("New account setup testing");
		JSONObject jsObj = new JSONObject();
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		UtilServiceImpl utilServiceImpl     = new UtilServiceImpl();
		
		String qlid = userCredBean.getQlid();
		
		if(empSrvImpl.validateUser(userCredBean.getQlid())) {

		//// TODO: check if session is active and valid, return a message,
		////	notifying the user that they are logged in if they have an
		////	active session....
		
		////---------------------------------------------------------------
		//// If user has set password (i.e. there is a record in the login
		//// 	credentials table), return a message telling them so....
			if(empSrvImpl.hasSetPassword(qlid)) {
				jsObj.put("success", false)
					.put("message",
							"Password already set, use \"Forgot Password\" in "
							+ "case you have forgotten your password..."
						);
				return jsObj.toString();
			}
			
			//// IMPORTANT STEP: Set all the old tokens invalid!
			empSrvImpl.setOldTokensInvalid(qlid);
			
			String pwdToken = empSrvImpl.buildRandomToken(128);
			
			if(!empSrvImpl.setNewPasswordToken(qlid, pwdToken)) {
				jsObj.put("success", false)
					.put("message", "Internal Error....");
				return jsObj.toString();
			}
			
//			SendMailService sms = new SendMailService();
			String from ="incredicabs@ncr.com";
			String url = BASE_URL + "/#/new-acc-setup/set-password/"+qlid+"/"+pwdToken;
			String recepient = qlid+"@ncr.com";
			String subject = "iNCRediCabs: Link to set password.....";
			String messageBody = 
				"<html><head><style>#link-box{padding: 10px 0px 10px 0px;margin-top: 10px; background: #dddddd;}" + 
				"code{margin: 0px 10px 0px 10px;}</style></head><body><div class=\"col-sm-8 col-sm-offset-2\">" + 
				"<h1 class=\"text-center\">iNCRediCabs</h1><p>Visit the url below to set your password:</p>" + 
				"<strong>Note:</strong>Copy the url below and paste in supported browser: Chrome(ver: 50+) or Firefox(ver: 40+)" + 
				"<div id=\"link-box\"><code>"+url+"</code></div></div></body></html>";
	
			if(utilServiceImpl.sendEmailMessage(from, recepient, "", "", "", subject, messageBody)) {
				jsObj.put("success", true)
				.put("message",
					"An Email has been sent to you along with a link to set your password."
					+ "You have 10 minutes to set your password before this link expires!"
					);
			}
			
			
			/* if(empSrvImpl.sendMessage(recepient, subject, messageBody)) {
				jsObj.put("success", true)
					.put("message",
						"An Email has been sent to you along with a link to set your password."
						+ "You have 10 minutes to set your password before this link expires!"
						);
			*/
		   else {
				jsObj.put("success", false)
					.put("message", "An error occured!, please try after some time....");
			}
			
			return jsObj.toString();
		}
		
		return (new JSONObject())
				.put("success", false)
				.put("message", "Qlid is invalid!")
				.toString();
	}
	
	@Path("/verify-pwd-token")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String verifyPwdToken(PasswordTokenBean pwdTokenBean) {
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
//		JSONObject jsObj = new JSONObject();
		System.out.println("received qlid: "+pwdTokenBean.getQlid()
				+" token : "+pwdTokenBean.getToken());
		if(empSrvImpl.isPwdTokenValid(pwdTokenBean.getQlid(), pwdTokenBean.getToken())) {
			System.out.println("valid token");
			return (new JSONObject())
					.put("valid", true)
					.toString();
		}else {
			System.out.println("invalid token");
			return (new JSONObject())
					.put("valid", false)
					.toString();
		}
	}
	
	@Path("/view-employee")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String viewEmp(
					EmployeeFilterBean	employeeFilterBean,
		@Context	HttpServletRequest	req
	) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
//		
//		if(qlid == null || qlid == EmployeeServiceImpl.DEFAULT_QLID ||
//				sessRole.toUpperCase() != "ADMIN") {
//			return EmployeeServiceImpl.noLoginMessage();
//		}		
		
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		JSONArray jsArr = new JSONArray();
		
		//// TODO: uncomment after session issues are fixed
//		System.out.println("View: qlid: " + req.getSession().getAttribute("qlid"));
//		LoginAuth loginAuth = new LoginAuth();
//		if(!loginAuth.isLoginValid(req)) {
//			return (new JSONObject())
//					.put("login", false)
//					.put("message", "This session is not valid!")
//					.toString();
//		}
//		System.out.println();
//-------------------------------------------------------------------------------------
		EmployeeBean employeeBean[] = empSrvImpl.getEmployeeArray(employeeFilterBean);
		
		jsArr = empSrvImpl.getEmployeeJSONArray(employeeBean);
		return jsArr.toString();
	}
	
	@Path("/view-employee-by-qlid")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String viewEmpByQlid(
						UserCredBean		ucb,
			@Context	HttpServletRequest	req
			) {
		EmployeeServiceImpl esImpl = new EmployeeServiceImpl();
		if(esImpl.validateUser(ucb.getQlid())) {
			EmployeeFilterBean efb = new EmployeeFilterBean();
			efb.setFilterType("EMPQLID");
			efb.setFilterValue(ucb.getQlid());
			
			return esImpl.createJSON(esImpl.getEmployeeArray(efb)[0])
					.put("success", true)
					.toString();
		}
		
		return (new JSONObject())
				.put("success", false)
				.put("message", "No user found with qlid: "+ucb.getQlid())
				.toString();
	}
	
	@Path("/view-employee/{urlToken}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String viewEmpDetails(
								EmployeeFilterBean	employeeFilterBean,
		@PathParam("urlToken")	String				urlToken,
		@Context				HttpServletRequest	req
	) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
//		
//		if(qlid == null || qlid == EmployeeServiceImpl.DEFAULT_QLID ||
//				sessRole.toUpperCase() != "ADMIN") {
//			return EmployeeServiceImpl.noLoginMessage();
//		}		
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		JSONArray jsArr = new JSONArray();
		EmployeeBean employeeBean[] = empSrvImpl.getEmployeeArray(
				employeeFilterBean);
		
		jsArr = empSrvImpl.getEmployeeJSONArray(employeeBean);
		return jsArr.toString();
	}
	
	/**
	 * employeeManagerDetails()
	 * 
	 * 		Currently being used for requesting unscheduled cab....
	 * @return
	 */
	@Path("/employee-manager-details")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String employeeManagerDetails(
			@Context	HttpServletRequest	req
		) {
		//// TODO: verify that user is logged in, use user qlid
		////	return "success": false if not logged in....
		JSONObject jsObj = new JSONObject();
		JSONObject tmp = new JSONObject();
		EmployeeBean empBean = new EmployeeBean();
		EmployeeServiceImpl empSrvImpl  = new EmployeeServiceImpl();
		
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		
		String employeeName = "";
		String shiftId = null;
		
		empBean = empSrvImpl.getEmployeeFromQLID(qlid);
		jsObj = empSrvImpl.createJSON(empBean);
		EmployeeBean mgrArr[] = empSrvImpl.getManagerDetailsEmployee(qlid);
		
		RosterServiceImpl rstrImpl = new RosterServiceImpl();
		JSONArray rstrArray = rstrImpl.showRosterInfo(
				(new JSONObject())
				.put("qlid", qlid)
				.put("c_n", "")
				.put("s_i", "")
				.put("e_n", "")
				.put("vname", "")
			);
		for(int i=0; i<rstrArray.length(); ++i) {
			tmp = (JSONObject) rstrArray.get(i);
			if(tmp.has("shift_id")) {
				shiftId = tmp.getString("shift_id");
			}
		}
		
		if(shiftId == null) {
			shiftId = "0";
		}
		
		if(empBean.getEmpMName() == null) {
			if(empBean.getEmpLName() == null) {
				employeeName = empBean.getEmpFName();
			}else {
				employeeName = empBean.getEmpFName() + " " + empBean.getEmpLName();
			}
		}else {
			employeeName = empBean.getEmpFName() + " " + empBean.getEmpMName() + " "
					+ empBean.getEmpLName();
		}
		
		jsObj.put("shiftId", shiftId);
		jsObj.put("empName", employeeName);
		jsObj.put("mgr1Name", mgrArr[0].getEmpFName()
								+" "+mgrArr[0].getEmpMName()
								+" "+mgrArr[0].getEmpLName());
		jsObj.put("mgr1Qlid", mgrArr[0].getEmpQlid());
		jsObj.put("mgr2Name", mgrArr[1].getEmpFName()
								+" "+mgrArr[1].getEmpMName()
								+" "+mgrArr[1].getEmpLName());
		jsObj.put("mgr2Qlid", mgrArr[1].getEmpQlid());		
		jsObj.put("success", true);
				
		return jsObj.toString();
	}
	
	@Path("/employee-dashboard")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String employeeDash(
			        EmployeeBean		employeeBean,
		@Context	HttpServletRequest	req,
		@Context	HttpServletResponse	res
	) {
		HttpSession sess = req.getSession();
		String qlid;
		String sessRole;
		if(employeeBean.getEmpQlid() == null) {
			qlid = (String)sess.getAttribute("qlid");
			sessRole = (String)sess.getAttribute("role");	
		}else {
			qlid = employeeBean.getEmpQlid();
		}
		
		JSONObject			jsObj			= new JSONObject();
		EmployeeServiceImpl	empSrvImpl		= new EmployeeServiceImpl();
		
		System.out.println("EmployeeDash for: " + qlid);
		
     	jsObj = empSrvImpl.employeeDash(qlid);
     	System.out.println("data Retrive");
     	System.out.println(jsObj);
     	
		//	jsObj = empSrvImpl.employeeDash(jsonRequest.getString("qlid"));
		//Response response=Response.status(200).type("application/json").entity(jsObj.toString()).build();
		if(jsObj != null) {
			jsObj.put("success", true);
		}
		
		return jsObj.toString();
    }
	
	@Path("/change-password")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)	
	public String changePassword(
			@Context	HttpServletRequest	req,
						SetPasswordBean		spb
			) {
		HttpSession sess = req.getSession();
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		String qlid = (String)sess.getAttribute("qlid");
//		JSONObject jsObj = new JSONObject();
		System.out.println("changing password....");
		
		return (empSrvImpl.changePassword(qlid, spb)).toString();
	}
	
	
	
	@Path("/login-android")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String loginAndroid(
					UserCredBean		ucb
	) {
//		HttpSession sess = req.getSession();
		JSONObject jsObj = new JSONObject();
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		EmployeeBean empBean = new EmployeeBean();
		
		JSONObject loginAuth = empSrvImpl.getLoginAuthJSON(ucb.getQlid(), ucb.getPassword());
		if(Boolean.parseBoolean(loginAuth.getString("login"))) {
			jsObj = empSrvImpl.employeeDash(ucb.getQlid());
			jsObj.put("success", true);
		}else {
			jsObj.put("success", false);
		}
		
		return jsObj.toString();
	}
	
	
	
	@Path("/get-qlid")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getQlid(
			@Context	HttpServletRequest	req
	) {
		HttpSession sess = req.getSession();
		String qlid;
		if(sess.getAttribute("qlid") == null) {
			return (new JSONObject())
						.put("success", false)
						.put("message", "You are not logged in!")
						.toString();
		}
		
		qlid = (String)sess.getAttribute("qlid");
		return (new JSONObject())
					.put("success", true)
					.put("qlid", qlid)
					.toString();
	}
	
	@Path("/set-push-token-android")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String setPushTokenAndroid(
			PushTokenBean ptb
			) {
		System.out.println("Setting Push Token! qlid:" + ptb.getQlid());
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		if(empSrvImpl.setPushTokenAndroid(ptb)) {
			return (new JSONObject())
						.put("success", true)
						.put("message", "Push token successfully updated!")
						.toString();
		}
		
		return (new JSONObject())
				.put("success", false)
				.put("message", "Push token could not be successfully updated!")
				.toString();	
	}
	
	@Path("/sos-trigger-android")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String sosTriggerAndroid(
			SOSRequestBean sos
			) {
		//// TODO: add entry to sos table
		////		send mails to transport admins and managers
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		UtilServiceImpl us = new UtilServiceImpl();
		empSrvImpl.addEntryToSOSTable(sos);
		
		//// send SOS mail
		EmployeeBean emp = empSrvImpl.getEmployeeFromQLID(sos.getEmpQlid());
		
		JSONArray sosArr = empSrvImpl.getContactJSONArray();
		int len = sosArr.length();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));
		String now = sdf.format(new Date());
		
		String mail[] = new String[3];
		String from		= "incredicabs@ncr.com";
		String subject	= "SOS received from "+emp.getEmpFName()+" "+emp.getEmpMName()+" "
							+emp.getEmpLName()+" - iNCRediCabs";
		String message	= "An SOS alert has been triggered by "+emp.getEmpFName()+" "+emp.getEmpMName()+" "
				+emp.getEmpLName() + "(" +emp.getEmpQlid() + ") on "+now;
		String receivers = "";
		
		JSONObject jsObj;
		
		for(int i=0, s=0; i<3; ++i) {
			jsObj = sosArr.getJSONObject(i);
			if(jsObj == null) {
				mail[i] = "";
			}else {				
				mail[i] = jsObj.getString("contactEmail");
			}
		}
		
		empSrvImpl.addEntryToSOSTable(sos);
		
		us.sendEmailMessage(from, mail[0], mail[1], mail[2], "", subject, message);
//		empSrvImpl.sendMessage(mail[0]+","+mail[1]+","+mail[2], subject, message);
		
		
		return "";
	}


	/**
	 * An important issue in this method is that while uploading,
	 * jersey finds no session... 
	 */
	
	@Path("/upload-file-data")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadFile(
			@Context						HttpServletRequest			req,
			@FormDataParam("uploadFile") 	InputStream					fileInputStream,
			@FormDataParam("uploadFile") 	FormDataContentDisposition	fileFormDataContentDisposition
		) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
		
//		if(qlid == null || qlid.equals(EmployeeServiceImpl.DEFAULT_QLID) ||
//				!sessRole.toUpperCase().equals("ADMIN")) {
//			System.out.println("!! " + qlid);
//			return EmployeeServiceImpl.noLoginMessage();
//		}
		System.out.println("TestCheck");
		EmployeeServiceImpl frd = new EmployeeServiceImpl();
		JSONObject sUpl = null;
		try {
			sUpl = frd.insertIntoDatabase(fileInputStream, fileFormDataContentDisposition, _qlid);
//			UtilServiceImpl us = new UtilServiceImpl();
//			us.sendEmailMessage("system@ncr.com", recepient1, recepient2, recepient3, recepient4, subject, messageAttribute)
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(sUpl == null) {
			return (new JSONObject())
						.put("success", false)
						.put("message", "An error occured, please try again later!")
						.toString();
		}
		return (new JSONObject())
					.put("success", Boolean.parseBoolean(sUpl.getString("success")))
					.put("totalRows", sUpl.getString("totalRows"))
					.put("totalSuccessful", sUpl.getString("totalSuccessful"))
					.put("totalFailed", sUpl.getString("totalFailed"))
//					.put("message", "File successfully uploaded!")
					.put("successfullUpload", sUpl.getJSONArray("successfulUpload"))
					.toString();
	}
	
	//Contact-Delete
	@Path("/contacts/delete")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String contactDelete(
					ContactBean	contactBean,
		@Context	HttpServletRequest	req,
		@Context	HttpServletResponse	res
	) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
		
		if(qlid == null || qlid == EmployeeServiceImpl.DEFAULT_QLID ||
				sessRole.toUpperCase() != "ADMIN") {
			return EmployeeServiceImpl.noLoginMessage();
		}
		
		JSONObject			jsObj			= new JSONObject();
		EmployeeServiceImpl	empSrvImpl		= new EmployeeServiceImpl();
		jsObj = empSrvImpl.disableContact(contactBean);
		System.out.println(jsObj.toString());
		return jsObj.toString();
     }
	
	//Contact-update	
	@Path("/contacts/update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String contactEdit(
					ContactBean	contactBean,			        
		@Context	HttpServletRequest	req,
		@Context	HttpServletResponse	res
	) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
		
//		if(qlid == null || qlid == EmployeeServiceImpl.DEFAULT_QLID ||
//				sessRole.toUpperCase() != "ADMIN") {
//			return EmployeeServiceImpl.noLoginMessage();
//		}		
		
		JSONObject			jsObj			= new JSONObject();
		EmployeeServiceImpl	empSrvImpl		= new EmployeeServiceImpl();	
		if(empSrvImpl.validateContactId(contactBean)){
			jsObj=empSrvImpl.editContact(contactBean);
		}else{
			jsObj=empSrvImpl.addContact(contactBean);
		}
		
		return jsObj.toString();
    }
	
	//Contact-View
	
	@Path("/contacts/view")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String contactView(		        
		@Context	HttpServletRequest	req,
		@Context	HttpServletResponse	res
	) {
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
		String sessRole = (String)sess.getAttribute("role");
		
//		if(qlid == null || qlid == EmployeeServiceImpl.DEFAULT_QLID ||
//				sessRole.toUpperCase() != "ADMIN") {
//			return EmployeeServiceImpl.noLoginMessage();
//		}		
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		JSONArray jsArr = new JSONArray();
		
			jsArr = empSrvImpl.getContactJSONArray();
		
		return jsArr.toString();	
     }
}
