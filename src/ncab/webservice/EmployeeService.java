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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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
import ncab.dao.impl.EmployeeServiceImpl;
import ncab.dao.impl.RosterServiceImpl;

@JsonIgnoreProperties(ignoreUnknown = true)
@Path("/EmployeeService")
public class EmployeeService {
	public EmployeeService() {}
	
	/**
	 * checksBeforePath()
	 * 		Executes everytime before a "Path" is hit.
	 * 	Used to verify login and other info....
	 */
	@Context
	public void checksBeforePath(
			@Context HttpServletRequest req
			) {
//		System.out.println("Hello!");
		if(EmployeeServiceImpl.isLoginValid(req)){
//			System.out.println(req.getSession().getAttribute("role"));
			//// TODO things to do when user is logged in...
		}else {
			HttpSession sess = req.getSession();
			sess.setAttribute("qlid", EmployeeServiceImpl.DEFAULT_QLID);
			sess.setAttribute("token", EmployeeServiceImpl.DEFAULT_TOKEN);
			System.out.println("No session found, assigning a dummy session....");
//			return;
		}
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
	public String activateEmployee(EmployeeBean employeeBean){
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		if(empSrvImpl.activateEmployee(employeeBean)) {
			System.out.println("Activate Success!!");
		}else {
			System.out.println("Activate Failed!!");
			return (new JSONObject()).put("success", false).toString();
		}
		
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
		EmployeeServiceImpl empSrvImpl = new EmployeeServiceImpl();
		JSONObject jsObj = new JSONObject();
		JSONObject jsAddResp = new JSONObject();
		
		jsAddResp = empSrvImpl.addEmployee(employeeBean);
		boolean addSuccess = Boolean.parseBoolean(jsAddResp.getString("success"));
		// TODO: add more options to check the errors and report it to user
		if(addSuccess) {
			jsObj.put("success", true)
				.put("message", "Employee has been added to the Database!");
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
			+ "\n\tSession---------"
			+ "\n\t\tToken: " + sess.getAttribute("token")
			+ "\n\tCookie----------"
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
				&& !sess.getAttribute("token").equals(EmployeeServiceImpl.DEFAULT_TOKEN)) {
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
	public String deactivateEmployee(EmployeeBean employeeBean) {
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
	public String editEmployee(EmployeeBean	employeeBean) {
		System.out.println("!!");
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
		
		if (empSrvImpl.validateUser(ucb.getQlid())) {
			//// IMPORTANT STEP: Set all the old tokens invalid!
			empSrvImpl.setOldTokensInvalid(ucb.getQlid());

			String token = empSrvImpl.buildRandomToken(128);
			String url="http://localhost:4200/forgot-password/set-password/"+ucb.getQlid()+"/"+token;
			String recepient = ucb.getQlid()+"@ncr.com";
			String subject="iNCRediCabs: Link to reset password";
			String message = 
				"<html>"
				+ "	<head>"
				+ "		<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">" 
				+ "  	<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>"
				+ "  	<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>"
				+ "	</head>"
				+ "	<body>"
				+ "		<div class=\"container col-sm-6 col-sm-ofset-3\">"
				+ "			<h1 class=\"text-center\">iNCRediCabs</h1>"
				+ "			<p>Visit the url below to reset your password:</p><br />"
				+ "			<a href=\""+url+"\">"+url+"</a>"
				+ "		</div>"
				+ "	</body>"
				+ "</html>";
			if(empSrvImpl.sendMessage(recepient, subject, message)) {
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

			jsObj.put("success", false)
				.put("message", "An error occured!, please try after some time....");

			empSrvImpl.insertforgotpasswordDetails(ucb.getQlid(), token);
			System.out.println(jsonResponse.get("status"));

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
//		System.out.println("!!");
		JSONObject jsObj = new JSONObject();
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();

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
	public String passwordChangeEmployee(UserCredBean ucb){
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
				System.out.println("Login Successful!\nsetting session....");
				session.setAttribute("qlid", loginFormInputBean.getQlid());
				session.setAttribute("token", token);
				session.setAttribute("role", jsObj.get("role"));
	
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
						new NewCookie("qlid", qlid, "/", "localhost", "", 7200, false),
						new NewCookie("token", (String)session.getAttribute("token"), "/", "localhost", "", 3600, false)
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
		System.out.println(sess.getAttribute("role"));
		return (new JSONObject())
					.put("roleName", sess.getAttribute("role"))
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
		JSONObject jsObj = new JSONObject();
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
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
			String url = "http://localhost:4200/new-acc-setup/set-password/"+qlid+"/"+pwdToken;
			String recepient = qlid+"@ncr.com";
			String subject = "iNCRediCabs: Link to set password.....";
			String messageBody = 
				"<html>"
				+ "	<head>"
				+ "		<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">" 
				+ "  	<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>"
				+ "  	<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>"
				+ "	</head>"
				+ "	<body>"
				+ "		<div class=\"container col-sm-6 col-sm-ofset-3\">"
				+ "			<h1 class=\"text-center\">iNCRediCabs</h1>"
				+ "			<p>Visit the url below to set your password:</p><br />"
				+ "			<a href=\""+url+"\">Click Here</a>"
				+ "		</div>"
				+ "	</body>"
				+ "</html>";
	
			if(empSrvImpl.sendMessage(recepient, subject, messageBody)) {
				jsObj.put("success", true)
					.put("message",
						"An Email has been sent to you along with a link to set your password."
						+ "You have 10 minutes to set your password before this link expires!"
						);
			}else {
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
	public String viewEmpByQlid(UserCredBean ucb) {
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
	public String employeeManagerDetails() {
		//// TODO: verify that user is logged in, use user qlid
		////	return "success": false if not logged in....
		JSONObject jsObj = new JSONObject();
		EmployeeBean empBean = new EmployeeBean();
		EmployeeServiceImpl empSrvImpl  = new EmployeeServiceImpl();
		String qlid = "AP250624";
		
//		empBean = empSrvImpl.getEmployeeArray()
		empBean = empSrvImpl.getEmployeeFromQLID(qlid);
		jsObj = empSrvImpl.createJSON(empBean);
		EmployeeBean mgrArr[] = empSrvImpl.getManagerDetailsEmployee();
		
		jsObj.put("mgr1Name", mgrArr[0].getEmpFName()
								+" "+mgrArr[0].getEmpMName()
								+" "+mgrArr[0].getEmpLName());

		jsObj.put("mgr2Name", mgrArr[1].getEmpFName()
								+" "+mgrArr[1].getEmpMName()
								+" "+mgrArr[1].getEmpLName());
		
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
//		System.out.println(employeeBean.getEmpQlid());
		HttpSession sess = req.getSession();
		String qlid = (String)sess.getAttribute("qlid");
//		String qlid = employeeBean.getEmpQlid();
		
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
	public String sosTriggerAndroid() {
		//// TODO: add entry to sos table
		////		send mails to transport admins and managers
		
		
		
		return "";
	}


	@Path("/UploadFileData")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadFile(
			@FormDataParam("uploadFile") InputStream				fileInputStream,
			@FormDataParam("uploadFile") FormDataContentDisposition fileFormDataContentDisposition
		) throws Exception {
		System.out.println("TestCheck");
		EmployeeServiceImpl frd = new EmployeeServiceImpl();
		JSONObject sUpl = frd.insertIntoDatabase(fileInputStream, fileFormDataContentDisposition);
		System.out.println("Success import excel to mysql table");
		return (new JSONObject())
					.put("success", true)
					.put("message", "File successfully uploaded!")
					.put("SuccessfullUpload", sUpl.getJSONArray("successfulUpload"))
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
		
		
		JSONObject			jsObj			= new JSONObject();
		EmployeeServiceImpl	empSrvImpl		= new EmployeeServiceImpl();	
		if(empSrvImpl.validateContactId(contactBean))
			{
			jsObj=empSrvImpl.editContact(contactBean);
			}
		else
		{
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
		EmployeeServiceImpl	empSrvImpl = new EmployeeServiceImpl();
		JSONArray jsArr = new JSONArray();
		
			jsArr = empSrvImpl.getContactJSONArray();
		
		return jsArr.toString();	
     }
}
