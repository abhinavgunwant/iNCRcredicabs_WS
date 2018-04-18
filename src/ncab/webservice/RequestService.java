package ncab.webservice;


import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import ncab.dao.DBConnectionRo;
import ncab.dao.impl.RequestServiceImpl;
import ncab.dao.impl.UtilServiceImpl;
import java.util.Calendar;

@Path("/RequestService")
public class RequestService {
	UtilServiceImpl  sendMailService;
	UtilServiceImpl  sendPushService;
	RequestServiceImpl requestServiceImpl;
	Response response;


	public ArrayList<String> getExcelColumnNames() {

		ArrayList<String> excelColNames =new ArrayList<String>();
		excelColNames.add("Request Date");
		excelColNames.add("Request Id");
		excelColNames.add("Employee Qlid");
		excelColNames.add("Employee Name");
		excelColNames.add("Gender");
		excelColNames.add("Mobile No.");
		excelColNames.add("Manager Qlid");
		excelColNames.add("Manager Name");
		excelColNames.add("Pickup Location");					
		excelColNames.add("Pickup Time");
		excelColNames.add("Drop Location");
		excelColNames.add("Trip Type");
		excelColNames.add("Request Status");

		return excelColNames;

	}


	public ArrayList<ArrayList<String>> getExcelBody(String requestIds,String Allocated) {

		requestServiceImpl = new RequestServiceImpl();
		//		System.out.println("in getExcelBody() >>requestIds"+requestIds+" >>Allocated"+Allocated);
		ArrayList<ArrayList<String>> myList = new ArrayList<>();
		try {
			myList=requestServiceImpl.getUnscheduledRequestByIdImpl(requestIds,Allocated);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error:RequestService::getExcelBody()");
		}

		//		System.out.println("exiting getExcelBody()"+myList.toString());

		return myList;
	}

	@GET  
	@Path("/pullExcel/{flp}")  
	@Produces("application/vnd.ms-excel")
	public Response pullExcelFile(@Context HttpServletRequest req,@PathParam("flp") String filename) {

		JSONObject jsonResponse =new JSONObject();
		String webappPath = req.getServletContext().getRealPath("/");

		File file =new File(webappPath+"WebContent"+File.separator+"tempDir"+File.separator+filename);

		if(file.exists())
		{
			//			System.out.println("File found");
			ResponseBuilder rb = Response.ok(file);  
			rb.header("content-disposition", "attachment; filename=\"File.xlsx\"");  
			return rb.build();  
		}else {
			//			System.out.println("File not found");

			jsonResponse.put("status", "fail" );
			jsonResponse.put("message", "No file exists");
			response = Response.status(200).type("application/json").entity(jsonResponse.toString()).build();       
			return response;
		}

	}  

	public File createTempFileWithDir(@Context HttpServletRequest req) throws IOException {

		String webappPath = req.getServletContext().getRealPath("/");

		File dir =new File(webappPath+"WebContent"+File.separator+"tempDir");		
		if(!dir.exists())
		{
			if(dir.mkdirs()) {
				System.out.println("Directory created");
			}else {
				System.out.println("Directory not created");
			}
		}

		File file = File.createTempFile("ExcelFile", ".xlsx", dir);	

		return file;
	}

	@POST
	@Path("/getUExcel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response downloadUnscheduledRequestExcel(@Context HttpServletRequest req,String request) {


		JSONObject jsonRequest=null;
		JSONObject jsonResponse = new JSONObject();
		File tempFile=null;
		String requestIds=null;		
		try {
			jsonRequest = new JSONObject(request);
			requestIds=jsonRequest.getString("Request_ids");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		ArrayList<String> excelColNames = this.getExcelColumnNames();
		ArrayList<ArrayList<String>> excelBody =this.getExcelBody(requestIds.substring(1,requestIds.length()-1),jsonRequest.getString("Allocated"));

		try {
			tempFile =this.createTempFileWithDir(req);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(tempFile==null)
		{
			jsonResponse.put("status","fail");
			response = Response.status(200).type("application/json").entity(jsonResponse.toString()).build(); 	
			return response;
		}


		if(jsonRequest.getString("Allocated").equals("1")) {
			this.createExcel(tempFile.toString(),excelColNames,excelBody,"Allocated Unscheduled Request");
		}

		this.createExcel(tempFile.toString(),excelColNames,excelBody,"Pending Unscheduled Request");

		if(tempFile.exists()) {
			jsonResponse.put("status","success");
			jsonResponse.put("fileName",tempFile.getName().toString());
		}else {
			jsonResponse.put("status", "fail");
		}

		response = Response.status(200).type("application/json").entity(jsonResponse.toString()).build(); 	
		return response;

	}

	private XSSFWorkbook createExcel(String filePath,ArrayList<String> excelColNames,ArrayList<ArrayList<String>> excelBody,String excelTitle) {

		FileOutputStream outputStream;
		XSSFWorkbook workbook = new XSSFWorkbook();
		int rowNum = 0;
		int colNum = 0;
		XSSFSheet sheet = workbook.createSheet(excelTitle);

		Row row = sheet.createRow(rowNum++);
		colNum=0;
		for(Object colName: excelColNames)
		{
			Cell cell = row.createCell(colNum++);
			if (colName instanceof String) {
				cell.setCellValue((String) colName);
			} else if (colName instanceof Integer) {
				cell.setCellValue((Integer) colName);
			}
		}

		rowNum=1;

		for (ArrayList<String> excelRow : excelBody) {
			//			System.out.println(excelRow.toString());
			row = sheet.createRow(rowNum++);
			colNum = 0;
			for (Object field : excelRow) {
				Cell cell = row.createCell(colNum++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Integer) {
					cell.setCellValue((Integer) field);
				}
			}
		}

		try {
			outputStream = new FileOutputStream(filePath);
			workbook.write(outputStream);
			workbook.close();	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		System.out.println("Generated excel");
		return workbook;

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sendRequest")
	public Response sendRequest(String jsonrequest) throws ParseException, SQLException{

		JSONObject jsonreq = new JSONObject(jsonrequest);
		JSONObject jsonres = new JSONObject();

		System.out.println(jsonreq);

		JSONObject jsonresponse = new JSONObject();

		int result=0;
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);

		String push_Level1_token = "";
		String push_Level2_token="";

		requestServiceImpl = new RequestServiceImpl();

		String Emp_QLID = jsonreq.getString("Emp_QLID");
		String Employee_Name = jsonreq.getString("Employee_Name");
		String Shift_ID = jsonreq.getString("Shift_ID");
		String Mgr_QLID = jsonreq.getString("Mgr_QLID");
		String Employee_Manager_1_Name = jsonreq.getString("Employee_Manager_1_Name");
		//String source = jsonreq.getString("Source");
		//String destination = jsonreq.getString("Destination");
		String other = jsonreq.getString("Other_Addr");
		String Reason = jsonreq.getString("Reason");
		String Start_Date_Time = jsonreq.getString("Start_Date_Time");
		String End_Date_Time = jsonreq.getString("End_Date_Time");
		String Destination= jsonreq.getString("Destination");
		String Source= jsonreq.getString("Source");
		String Level2_mgr = jsonreq.getString("Mgr_QLID_Level2");
		String Employee_Manager_2_Name = jsonreq.getString("Employee_Manager_2_Name");
		int Counter = jsonreq.getInt("Counter");
		String date=Start_Date_Time.substring(0,10);
		String time=Start_Date_Time.substring(11,19);

		String Shift_Name = "";
		switch(Shift_ID){

		case "1": Shift_Name=""; 
		break;
		case "2": Shift_Name="";
		break;
		case "3": Shift_Name="";
		break;
		case "4": Shift_Name="Unscheduled";
		break;

		}

		result=requestServiceImpl.saveRequest(Emp_QLID,Shift_ID,Start_Date_Time,End_Date_Time,Source , Destination , other,Reason);	

		if (result>0) {sendPushService = new UtilServiceImpl();
		sendMailService =new UtilServiceImpl();


		Connection connection = (new DBConnectionRo()).getConnection();			
		PreparedStatement ps = connection.prepareStatement( "Select Login_Push_Token from ncab_login_credentials_tbl where Emp_Qlid = ? ");
		ps.setString(1,Mgr_QLID);
		ResultSet rs = ps.executeQuery();

		if(rs.next())
		{
			push_Level1_token=rs.getString("Login_Push_Token");
		}


		PreparedStatement ps1 = connection.prepareStatement( "Select Login_Push_Token from ncab_login_credentials_tbl where Emp_Qlid = ? ");
		ps1.setString(1,Level2_mgr);
		ResultSet rs1 = ps1.executeQuery();
		if(rs1.next())
		{
			push_Level2_token=rs1.getString("Login_Push_Token");
		}


		System.out.println("out1"+push_Level1_token);
		System.out.println("out2"+push_Level2_token);


		if (Counter == 1){



			String pushmsg = "Cab Request Id:"+result+"\n"+
					"Employee :"+Employee_Name+"("+Emp_QLID+")"+"\n"+
					"From: "+date+"\n"+
					"To: "+ End_Date_Time+"\n"+
					"Trip Type: "+other+"\n"+ 
					"Source: "+Source+"\n"+
					"Destination: "+Destination+"\n"+
					"Time: "+time+"\n"+
					"*Reason*: " +Reason+"\n";
			// System.out.println(pushmsg);
			System.out.println("in"+push_Level1_token);
			int val= sendPushService.sendPushMessage(push_Level1_token,"Cab Request",pushmsg);
			System.out.println("notify res"+val);
			System.out.println("out"+push_Level1_token);
			sendMailService.sendEmailMessage(
					"incredicabs@ncr.com",                 //from
					"hanif.mohd@ncr.com",                   //to  Transport Manger ID
					Mgr_QLID+"@ncr.com",
					Emp_QLID+"@ncr.com",
					"",//cc
					"NCR cabs | Request generated for "+Shift_Name+" by "+Employee_Name+"("+Emp_QLID+")", //subject
					"<center>" +
					"<table class='MsoNormalTable' border='0' cellspacing='0' cellpadding='0' width='40%' style='width:40.0%;mso-cellspacing:0in;background:white;mso-yfti-tbllook:1184;" +
					"mso-padding-alt:0in 0in 0in 0in'>" +
					"<tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:44.4pt'>" +
					" <td colspan='2' valign='top' style='padding:1.8pt 1.8pt 1.8pt 1.8pt;height:44.4pt'>" +
					" <p class='MsoNormal'><!--[if gte vml 1]><v:shape id='_x0000_i1025' type='#_x0000_t75'" +
					" alt='Are you ready to experience a new world of interaction?' style='width:450pt;" +
					"height:55.5pt'>" +
					"<img src='https://digitalbridge.000webhostapp.com/images/ncr.jpg'" +
					" o:href='cid:image005.jpg@01D3AB32.E8728490'/>" +
					" </v:shape><![endif]--><!--[if !vml]--><!--[endif]--></p>" +
					" </td>" +
					" </tr>" +
					" <tr style='mso-yfti-irow:1;height:26.4pt'>" +
					"  <td colspan='2' style='padding:1.8pt 1.8pt 1.8pt 1.8pt;height:26.4pt'>" +
					" <table class='MsoNormalTable' border='0' cellspacing='0' cellpadding='0' width='100%' style='width:100.0%;mso-cellspacing:0in;mso-yfti-tbllook:1184;mso-padding-alt:" +
					" 0in 0in 0in 0in'>" +
					"<tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;mso-yfti-lastrow:yes'>" +
					"<td style='background:#E3E3E3;padding:3.0pt 3.0pt 3.0pt 3.0pt'>" +
					"<table class='MsoNormalTable' border='0' cellspacing='0' cellfpadding='0' width='100%' style='width:100.0%;mso-cellspacing:0in;mso-yfti-tbllook:" +
					" 1184;mso-padding-alt:0in 0in 0in 0in'>" +
					" <tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;mso-yfti-lastrow:yes'>" +
					"  <td style='padding:0in 0in 0in 0in'></td>" +
					" </tr>" +
					"</tbody></table>" +
					"</td>" +
					"</tr>" +
					"</tbody></table>" +
					" </td>" +
					" </tr>" +
					" <tr style='mso-yfti-irow:2'>" +
					"  <td style='padding:1.8pt 1.8pt 1.8pt 1.8pt'></td>" +
					"  <td style='padding:1.8pt 1.8pt 1.8pt 1.8pt'></td>" +
					"  </tr>" +
					" <tr style='mso-yfti-irow:3'>" +
					"   <td width='1%' valign='top' style='width:1.0%;padding:1.8pt 1.8pt 1.8pt 1.8pt'></td>" +
					"   <td width='67%' valign='top' style='width:67.0%;padding:1.8pt 1.8pt 1.8pt 1.8pt'>" +
					"   <p><span class='bodytext1'><span style='font-size:8.5pt'>The cab request by <b>"+Employee_Name+"("+Emp_QLID+")"+"</b> for &quot;Cab Request Id:&quot; <b>"+result+ " </b>has been forwarded to the Approver: <b>"+Employee_Manager_1_Name+"("+Mgr_QLID+")"+"</b>  for approval. Once the request is approved, you will recieve a mail." +
					"   </span></span><span style='font-size:8.5pt;font-family:&quot;Verdana&quot;,sans-serif;" +
					"   color:black'><br>" +
					"   <span class='bodytext1'>Details about the cab request are below:</span><br>" +
					"   <span class='bodytext1'>From: <b>"+date+"</b></span><br>" +
					"   <span class='bodytext1'>To: <b>"+End_Date_Time+"</b></span><br>" +
					"   <span class='bodytext1'>Source: <b>"+Source+"</b></span><br>" +
					"   <span class='bodytext1'>Destination: <b>"+Destination+"</b></span><br>" +
					"   <span class='bodytext1'>Time: <b>"+time+"</b></span><br>" +
					"   <span class='bodytext1'>Purpose Of Travel: <b>"+Reason+"</b></span><br>" +
					"   <span class='bodytext1'>Availing Cab at Weekend/s: <b>None</b></span><br>" +
					"   <span class='bodytext1'>For: <b>1 Days</b></span><br>" +
					"   <br>  <span class='bodytext1'><a href='http://idcportal.ncr.com/myidc/index.php/unscheduled-cab?view=unschedulecab&amp;id=16378&amp;mail=1'>" +
					" </a><o:p></o:p></span></span></p>" +
					"  </td>" +
					" </tr>" +
					" <tr style='mso-yfti-irow:4;mso-yfti-lastrow:yes;height:.25in'>" +
					"  <td colspan='2' style='background:#E3E3E3;padding:1.8pt 1.8pt 1.8pt 1.8pt;" +
					"  height:.25in'>" +
					"  <p class='MsoNormal' align='center' style='text-align:center'><span class='mousetype1'><span style='font-size:7.5pt'>NCR Confidential: FOR INTERNAL" +
					"  USE ONLY</span></span><span style='font-size:7.5pt;font-family:&quot;Verdana&quot;,sans-serif;" +
					"  color:black'><br>" +
					"   <span class='mousetype1'>"+year+" NCR Corporation. All rights reserved.</span></span></p>" +
					"   </td>" +
					"  </tr>" +
					" </tbody></table></center>");


		}

		else{

			sendMailService.sendEmailMessage(
					"incredicabs@ncr.com",                      //from
					"hanif.mohd@ncr.com",                   //to  Transport Manger ID
					Mgr_QLID+"@ncr.com",
					Level2_mgr+"@ncr.com",							//cc
					Emp_QLID+"@ncr.com",							//cc
					"NCR cabs | Request generated for "+Shift_Name+" by "+Employee_Name+"("+Emp_QLID+")", //subject
					"<center>" +
					"<table class='MsoNormalTable' border='0' cellspacing='0' cellpadding='0' width='40%' style='width:40.0%;mso-cellspacing:0in;background:white;mso-yfti-tbllook:1184;" +
					"mso-padding-alt:0in 0in 0in 0in'>" +
					"<tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:44.4pt'>" +
					" <td colspan='2' valign='top' style='padding:1.8pt 1.8pt 1.8pt 1.8pt;height:44.4pt'>" +
					" <p class='MsoNormal'><!--[if gte vml 1]><v:shape id='_x0000_i1025' type='#_x0000_t75'" +
					" alt='Are you ready to experience a new world of interaction?' style='width:450pt;" +
					"height:55.5pt'>" +
					"<img src='https://digitalbridge.000webhostapp.com/images/ncr.jpg'" +
					" o:href='cid:image005.jpg@01D3AB32.E8728490'/>" +
					" </v:shape><![endif]--><!--[if !vml]--><!--[endif]--></p>" +
					" </td>" +
					" </tr>" +
					" <tr style='mso-yfti-irow:1;height:26.4pt'>" +
					"  <td colspan='2' style='padding:1.8pt 1.8pt 1.8pt 1.8pt;height:26.4pt'>" +
					" <table class='MsoNormalTable' border='0' cellspacing='0' cellpadding='0' width='100%' style='width:100.0%;mso-cellspacing:0in;mso-yfti-tbllook:1184;mso-padding-alt:" +
					" 0in 0in 0in 0in'>" +
					"<tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;mso-yfti-lastrow:yes'>" +
					"<td style='background:#E3E3E3;padding:3.0pt 3.0pt 3.0pt 3.0pt'>" +
					"<table class='MsoNormalTable' border='0' cellspacing='0' cellpadding='0' width='100%' style='width:100.0%;mso-cellspacing:0in;mso-yfti-tbllook:" +
					" 1184;mso-padding-alt:0in 0in 0in 0in'>" +
					" <tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;mso-yfti-lastrow:yes'>" +
					"  <td style='padding:0in 0in 0in 0in'></td>" +
					" </tr>" +
					"</tbody></table>" +
					"</td>" +
					"</tr>" +
					"</tbody></table>" +
					" </td>" +
					" </tr>" +
					" <tr style='mso-yfti-irow:2'>" +
					"  <td style='padding:1.8pt 1.8pt 1.8pt 1.8pt'></td>" +
					"  <td style='padding:1.8pt 1.8pt 1.8pt 1.8pt'></td>" +
					"  </tr>" +
					" <tr style='mso-yfti-irow:3'>" +
					"   <td width='1%' valign='top' style='width:1.0%;padding:1.8pt 1.8pt 1.8pt 1.8pt'></td>" +
					"   <td width='67%' valign='top' style='width:67.0%;padding:1.8pt 1.8pt 1.8pt 1.8pt'>" +
					"   <p><span class='bodytext1'><span style='font-size:8.5pt'>The cab request by <b>"+Employee_Name+"("+Emp_QLID+")"+"</b> for &quot;Cab Request Id:&quot; <b>"+result+ " </b>has been forwarded to the Approvers: <b>"+Employee_Manager_1_Name+"("+Mgr_QLID+")"+" and "+Employee_Manager_2_Name+"("+Level2_mgr+")"+"</b>  for approval. Once the request is approved, you will recieve a mail." +
					"   </span></span><span style='font-size:8.5pt;font-family:&quot;Verdana&quot;,sans-serif;" +
					"   color:black'><br>" +
					"   <span class='bodytext1'>Details about the cab request are below:</span><br>" +
					"   <span class='bodytext1'>From: <b>"+date+"</b></span><br>" +
					"   <span class='bodytext1'>To: <b>"+End_Date_Time+"</b></span><br>" +
					"   <span class='bodytext1'>Source: <b>"+Source+"</b></span><br>" +
					"   <span class='bodytext1'>Destination: <b>"+Destination+"</b></span><br>" +
					"   <span class='bodytext1'>Time: <b>"+time+"</b></span><br>" +
					"   <span class='bodytext1'>Purpose Of Travel: <b>"+Reason+"</b></span><br>" +
					"   <span class='bodytext1'>Availing Cab at Weekend/s: <b>None</b></span><br>" +
					"   <span class='bodytext1'>For: <b>1 Days</b></span><br>" +
					"   <br>  <span class='bodytext1'><a href='http://idcportal.ncr.com/myidc/index.php/unscheduled-cab?view=unschedulecab&amp;id=16378&amp;mail=1'>" +
					" </a><o:p></o:p></span></span></p>" +
					"  </td>" +
					" </tr>" +
					" <tr style='mso-yfti-irow:4;mso-yfti-lastrow:yes;height:.25in'>" +
					"  <td colspan='2' style='background:#E3E3E3;padding:1.8pt 1.8pt 1.8pt 1.8pt;" +
					"  height:.25in'>" +
					"  <p class='MsoNormal' align='center' style='text-align:center'><span class='mousetype1'><span style='font-size:7.5pt'>NCR Confidential: FOR INTERNAL" +
					"  USE ONLY</span></span><span style='font-size:7.5pt;font-family:&quot;Verdana&quot;,sans-serif;" +
					"  color:black'><br>" +
					"   <span class='mousetype1'>"+year+" NCR Corporation. All rights reserved.</span></span></p>" +
					"   </td>" +
					"  </tr>" +
					" </tbody></table></center>");




			sendPushService = new UtilServiceImpl();
			String pushmsg = "Cab Request Id:"+result+"\n"+
					"Employee :"+Employee_Name+"("+Emp_QLID+")"+"\n"+
					"From: "+date+"\n"+
					"To: "+ End_Date_Time+"\n"+
					"Trip Type: "+other+"\n"+ 
					"Source: "+Source+"\n"+
					"Destination: "+Destination+"\n"+
					"Time: "+time+"\n"+
					"*Reason*: " +Reason+"\n";

			sendPushService.sendPushMessage(push_Level1_token,"Cab Request",pushmsg);


			sendPushService = new UtilServiceImpl();
			String pushmsg1 = "Cab Request Id:"+result+"\n"+
					"Employee QLId:"+Emp_QLID+"\n"+
					"From: "+date+"\n"+
					"To: "+ End_Date_Time+"\n"+
					"Trip Type: "+other+"\n"+ 
					"Source: "+Source+"\n"+
					"Destination: "+Destination+"\n"+
					"Time: "+time+"\n"+
					"*Reason*: " +Reason+"\n";

			sendPushService.sendPushMessage(push_Level2_token,"Cab Request",pushmsg1);
		}


		jsonresponse.put("Shift_Name", Shift_Name);
		jsonresponse.put("Request_Id", result );
		jsonresponse.put("status", "success");

		jsonres = jsonresponse;
		}else {
			return Response.status(200).type("application/json").entity(new JSONObject().put("result", "fail").toString()).build();
		}

		response = Response.status(200).type("application/json").entity(jsonres.toString()).build(); 		

		return response;
	}


	@POST
	@Path("/getrequest")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRequestNew(String requestJson) throws ClassNotFoundException, SQLException, ParseException
	{

		requestServiceImpl=new RequestServiceImpl();
		JSONArray jsonResponse=new JSONArray();
		JSONObject jsonRequest=new JSONObject(requestJson);

		jsonResponse =requestServiceImpl.getRequest(jsonRequest);
		Response response = Response.status(200).type("application/json").entity(jsonResponse.toString()).build(); 	
		return response;

	}


	@POST
	@Path("/onApproved")
	@Produces(MediaType.APPLICATION_JSON)
	public Response onApproved(String request){

		JSONArray jsonResponse=new JSONArray();
		JSONObject jsonRequest;
		try {
			jsonRequest = new JSONObject(request);
			JSONArray jsonRequestArr=jsonRequest.getJSONArray("Request_ids");
			JSONObject allocation;
			if(new RequestServiceImpl().onApprovedService(jsonRequestArr))
			{
				//				System.out.print("cab alloted");
				allocation=new JSONObject();
				allocation.put("Allocated", "0");
				jsonResponse =new RequestServiceImpl().getRequest(allocation);
			}
			else {
				//				System.out.print("cab unalloted");
				jsonResponse.put(false);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response = Response.status(200).type("application/json").entity(jsonResponse.toString()).build(); 	
		return response;

	} 


}
