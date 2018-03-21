package ncab.webservice;

//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.Writer;
import java.io.*; 

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import ncab.dao.impl.RequestServiceImpl;
import ncab.dao.impl.UtilServiceImpl;


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
		excelColNames.add("Drop Time");
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


	@POST
	@Path("/getUExcel")
//	@Produces("application/octet-stream")
	@Produces("application/vnd.ms-excel")
	//	@Produces(MediaType.APPLICATION_JSON)

	public Response downloadUnscheduledRequestExcel(String request) {

//		System.out.println("In Request Service"+request);
		//		response = Response.status(200).type("application/json").entity(request).build(); 		
		//
		//		return response;

		JSONObject jsonRequest=null;
		java.nio.file.Path tempFile=null;
		ResponseBuilder response =null;
		String requestIds=null;		
		try {
			jsonRequest = new JSONObject(request);
			requestIds=jsonRequest.getString("Request_ids");
			//			System.out.println("JSON Array>>>"+requestIds);
//			System.out.println("Trimmed Array>>>"+requestIds.substring(1,requestIds.length()-1));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		ArrayList<String> excelColNames = this.getExcelColumnNames();
		ArrayList<ArrayList<String>> excelBody =this.getExcelBody(requestIds.substring(1,requestIds.length()-1),jsonRequest.getString("Allocated"));

		try {
			tempFile =this.createTempFileWithDir();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(jsonRequest.getString("Allocated").equals("1")) {
			this.createExcel(tempFile.toString(),excelColNames,excelBody,"Allocated Unscheduled Request");
		}

		this.createExcel(tempFile.toString(),excelColNames,excelBody,"Pending Unscheduled Request");


		File file = new File(tempFile.toString());

		if(file.exists()) {
			System.out.println("File created");
		}else {
			System.out.println("File not created");
		}
		
		byte[] bFile =null;
		 try {
		 bFile = Files.readAllBytes(Paths.get(tempFile.toString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

		 response = Response.ok((Object)bFile);

//		response = Response.ok((Object)file);
//		System.out.println("Response>>"+response.toString());
		response.header("Content-Disposition", "attachment; filename='ExcelFile.xls'");
		response.header("Content-Type","application/vnd.ms-excel");
//		response.header("Content-Type","application/octet-stream");

		return response.build();

	}

	private XSSFWorkbook createExcel(String filePath,ArrayList<String> excelColNames,ArrayList<ArrayList<String>> excelBody,String excelTitle) {

		FileOutputStream outputStream;
		XSSFWorkbook workbook = new XSSFWorkbook();
		int rowNum = 0;
		int colNum = 0;
		XSSFSheet sheet = workbook.createSheet(excelTitle);
		//		Object[][] datatypes = {
		//				{"Datatype", "Type", "Size(in bytes)"},
		//				{"int", "Primitive", 2},
		//				{"float", "Primitive", 4},
		//				{"double", "Primitive", 8},
		//				{"char", "Primitive", 1},
		//				{"String", "Non-Primitive", "No fixed size"}
		//		};

//		System.out.println("Adding Col names");

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

//		System.out.println("Adding excel body");

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

		System.out.println("Generated excel");
		return workbook;

	}


	private java.nio.file.Path createTempFileWithDir() throws IOException {
		java.nio.file.Path tempDir = Files.createTempDirectory("tempfiles");	     
		java.nio.file.Path tempFile = Files.createTempFile(tempDir, "ExcelFile", ".xls");
		return tempFile;
	}


/*	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sendRequest")
	public Response sendRequest(String jsonrequest){

		JSONObject jsonreq = new JSONObject();
		JSONObject jsonres = new JSONObject();

		try {
			jsonreq = new JSONObject(jsonrequest);			
		} catch (ParseException e) {
			e.printStackTrace();
		}

		JSONObject jsonresponse = new JSONObject();

		int result=0;

		requestServiceImpl = new RequestServiceImpl();

		String Emp_QLID = jsonreq.getString("Emp_QLID");
		String Shift_ID = jsonreq.getString("Shift_ID");
		String Mgr_QLID = jsonreq.getString("Mgr_QLID");
		//String source = jsonreq.getString("Source");
		//String destination = jsonreq.getString("Destination");
		String other = jsonreq.getString("Other_Addr");
		String Reason = jsonreq.getString("Reason");
		String Start_Date_Time = jsonreq.getString("Start_Date_Time");
		String End_Date_Time = jsonreq.getString("End_Date_Time");
		String Destination= jsonreq.getString("Destination");
		String Source= jsonreq.getString("Source");
		String Level2_mgr = jsonreq.getString("Level2_mgr");
		int Counter = jsonreq.getInt("Counter");
		String date=Start_Date_Time.substring(0,10);;
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

		if (result>0) {sendMailService =new UtilServiceImpl();
		if (Counter == 1){
			sendMailService.sendEmailMessage(
					"donotreply@ncr.com",                                                              //from
					"guppul@gmail.com",                   //to  Transport Manger ID
					Mgr_QLID+"@ncr.com","",                                                              //cc
					"NCR cabs | Request generated for "+Shift_Name+" by "+Emp_QLID, //subject
					"<center>" +
					"<table class='MsoNormalTable' border='0' cellspacing='0' cellpadding='0' width='40%' style='width:40.0%;mso-cellspacing:0in;background:white;mso-yfti-tbllook:1184;" +
					"mso-padding-alt:0in 0in 0in 0in'>" +
					"<tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:44.4pt'>" +
					" <td colspan='2' valign='top' style='padding:1.8pt 1.8pt 1.8pt 1.8pt;height:44.4pt'>" +
					" <p class='MsoNormal'><!--[if gte vml 1]><v:shape id='_x0000_i1025' type='#_x0000_t75'" +
					" alt='Are you ready to experience a new world of interaction?' style='width:450pt;" +
					"height:55.5pt'>" +
					"<img src='http://pulkit604.esy.es/image003.jpg'" +
					" o:href='cid:image005.jpg@01D3AB32.E8728490'/>" +
					" </v:shape><![endif]--><!--[if !vml]--><img border='0' width='600' height='74' src='http://pulkit604.esy.es/image003.jpg' style='height:.766in;width:6.25in' alt='Are you ready to experience a new world of interaction?' v:shapes='_x0000_i1025'><!--[endif]--></p>" +
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
					"   <p><span class='bodytext1'><span style='font-size:8.5pt'>The cab request by <b>"+Emp_QLID+"</b> for &quot;Cab Request Id:&quot; <b>"+result+ " </b>has been forwarded to the Approver: <b>"+Mgr_QLID+"</b>  for approval. Once the request is approved, it will be forwarded to the" +
					"   concerned person.</span></span><span style='font-size:8.5pt;font-family:&quot;Verdana&quot;,sans-serif;" +
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
					"   <span class='mousetype1'>© 2010 NCR Corporation. All rights reserved.</span></span></p>" +
					"   </td>" +
					"  </tr>" +
					" </tbody></table></center>");}

		else{

			sendMailService.sendEmailMessage(
					"donotreply@ncr.com",                                                              //from
					"guppul@gmail.com",                   //to  Transport Manger ID
					Mgr_QLID+"@ncr.com",Level2_mgr+"@ncr.com",                                                        //cc
					"NCR cabs | Request generated for "+Shift_Name+" by "+Emp_QLID, //subject
					"<center>" +
					"<table class='MsoNormalTable' border='0' cellspacing='0' cellpadding='0' width='40%' style='width:40.0%;mso-cellspacing:0in;background:white;mso-yfti-tbllook:1184;" +
					"mso-padding-alt:0in 0in 0in 0in'>" +
					"<tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:44.4pt'>" +
					" <td colspan='2' valign='top' style='padding:1.8pt 1.8pt 1.8pt 1.8pt;height:44.4pt'>" +
					" <p class='MsoNormal'><!--[if gte vml 1]><v:shape id='_x0000_i1025' type='#_x0000_t75'" +
					" alt='Are you ready to experience a new world of interaction?' style='width:450pt;" +
					"height:55.5pt'>" +
					"<img src='http://pulkit604.esy.es/image003.jpg'" +
					" o:href='cid:image005.jpg@01D3AB32.E8728490'/>" +
					" </v:shape><![endif]--><!--[if !vml]--><img border='0' width='600' height='74' src='http://pulkit604.esy.es/image003.jpg' style='height:.766in;width:6.25in' alt='Are you ready to experience a new world of interaction?' v:shapes='_x0000_i1025'><!--[endif]--></p>" +
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
					"   <p><span class='bodytext1'><span style='font-size:8.5pt'>The cab request by <b>"+Emp_QLID+"</b> for &quot;Cab Request Id:&quot; <b>"+result+ " </b>has been forwarded to the Approver: <b>"+Mgr_QLID+"</b>  for approval. Once the request is approved, it will be forwarded to the" +
					"   concerned person.</span></span><span style='font-size:8.5pt;font-family:&quot;Verdana&quot;,sans-serif;" +
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
					"   <span class='mousetype1'>© 2010 NCR Corporation. All rights reserved.</span></span></p>" +
					"   </td>" +
					"  </tr>" +
					" </tbody></table></center>");
		}
		sendPushService = new UtilServiceImpl();
		String pushmsg = "Cab Request Id:"+result+"\n"+
				"Employee QLId:"+Emp_QLID+"\n"+
				"From: "+date+"\n"+
				"To: "+ End_Date_Time+"\n"+
				"Source: "+Source+"\n"+
				"Destination: "+Destination+"\n"+
				"Time: "+time+"\n"+
				"*Reason*: " +Reason+"\n";


		sendPushService.sendPushMessage("eqgNLkCK2JU:APA91bFKydOWHS-PVWcme-Dqit3VoGNpCEklJRIl3oiDgkCLO0RmRwzTXeaCH8IYhyELOsHi_Fa651300g3DDn5lYNVzo6zglMd5qPOLoDp1lSKEOH_UnXFQHYk_u-PKIusPpb_acEU-","Cab Request",pushmsg);


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
*/
	
	@POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sendRequest")
    public Response sendRequest(String jsonrequest) throws ParseException{
        
        JSONObject jsonreq = new JSONObject(jsonrequest);
        JSONObject jsonres = new JSONObject();
        
            System.out.println(jsonreq);
        
        JSONObject jsonresponse = new JSONObject();
    
        int result=0;
        
        requestServiceImpl = new RequestServiceImpl();
        
            String Emp_QLID = jsonreq.getString("Emp_QLID");
            String Shift_ID = jsonreq.getString("Shift_ID");
            String Mgr_QLID = jsonreq.getString("Mgr_QLID");
            //String source = jsonreq.getString("Source");
            //String destination = jsonreq.getString("Destination");
            String other = jsonreq.getString("Other_Addr");
            String Reason = jsonreq.getString("Reason");
            String Start_Date_Time = jsonreq.getString("Start_Date_Time");
            String End_Date_Time = jsonreq.getString("End_Date_Time");
            String Destination= jsonreq.getString("Destination");
            String Source= jsonreq.getString("Source");
            String Level2_mgr = jsonreq.getString("Level2_mgr");
            int Counter = jsonreq.getInt("Counter");
            String date=Start_Date_Time.substring(0,10);;
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
            
                if (result>0) {sendMailService =new UtilServiceImpl();
                    if (Counter == 1){
                    sendMailService.sendEmailMessage(
                            "donotreply@ncr.com",                                                              //from
                            "guppul@gmail.com",                   //to  Transport Manger ID
                            Mgr_QLID+"@ncr.com","",                                                              //cc
                            "NCR cabs | Request generated for "+Shift_Name+" by "+Emp_QLID, //subject
                            "<center>" +
                            "<table class='MsoNormalTable' border='0' cellspacing='0' cellpadding='0' width='40%' style='width:40.0%;mso-cellspacing:0in;background:white;mso-yfti-tbllook:1184;" +
                            "mso-padding-alt:0in 0in 0in 0in'>" +
                            "<tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:44.4pt'>" +
                            " <td colspan='2' valign='top' style='padding:1.8pt 1.8pt 1.8pt 1.8pt;height:44.4pt'>" +
                            " <p class='MsoNormal'><!--[if gte vml 1]><v:shape id='_x0000_i1025' type='#_x0000_t75'" +
                            " alt='Are you ready to experience a new world of interaction?' style='width:450pt;" +
                            "height:55.5pt'>" +
                            "<img src='http://pulkit604.esy.es/image003.jpg'" +
                            " o:href='cid:image005.jpg@01D3AB32.E8728490'/>" +
                            " </v:shape><![endif]--><!--[if !vml]--><img border='0' width='600' height='74' src='http://pulkit604.esy.es/image003.jpg' style='height:.766in;width:6.25in' alt='Are you ready to experience a new world of interaction?' v:shapes='_x0000_i1025'><!--[endif]--></p>" +
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
                            "   <p><span class='bodytext1'><span style='font-size:8.5pt'>The cab request by <b>"+Emp_QLID+"</b> for &quot;Cab Request Id:&quot; <b>"+result+ " </b>has been forwarded to the Approver: <b>"+Mgr_QLID+"</b>  for approval. Once the request is approved, it will be forwarded to the" +
                            "   concerned person.</span></span><span style='font-size:8.5pt;font-family:&quot;Verdana&quot;,sans-serif;" +
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
                            "   <span class='mousetype1'>© 2010 NCR Corporation. All rights reserved.</span></span></p>" +
                            "   </td>" +
                            "  </tr>" +
                            " </tbody></table></center>");}
                      
                else{
                    
                    sendMailService.sendEmailMessage(
                                                    "donotreply@ncr.com",                                                              //from
                                                    "guppul@gmail.com",                   //to  Transport Manger ID
                                                    Mgr_QLID+"@ncr.com",Level2_mgr+"@ncr.com",                                                        //cc
                                                    "NCR cabs | Request generated for "+Shift_Name+" by "+Emp_QLID, //subject
                                                    "<center>" +
                                                    "<table class='MsoNormalTable' border='0' cellspacing='0' cellpadding='0' width='40%' style='width:40.0%;mso-cellspacing:0in;background:white;mso-yfti-tbllook:1184;" +
                                                    "mso-padding-alt:0in 0in 0in 0in'>" +
                                                    "<tbody><tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;height:44.4pt'>" +
                                                    " <td colspan='2' valign='top' style='padding:1.8pt 1.8pt 1.8pt 1.8pt;height:44.4pt'>" +
                                                    " <p class='MsoNormal'><!--[if gte vml 1]><v:shape id='_x0000_i1025' type='#_x0000_t75'" +
                                                    " alt='Are you ready to experience a new world of interaction?' style='width:450pt;" +
                                                    "height:55.5pt'>" +
                                                    "<img src='http://pulkit604.esy.es/image003.jpg'" +
                                                    " o:href='cid:image005.jpg@01D3AB32.E8728490'/>" +
                                                    " </v:shape><![endif]--><!--[if !vml]--><img border='0' width='600' height='74' src='http://pulkit604.esy.es/image003.jpg' style='height:.766in;width:6.25in' alt='Are you ready to experience a new world of interaction?' v:shapes='_x0000_i1025'><!--[endif]--></p>" +
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
                                                    "   <p><span class='bodytext1'><span style='font-size:8.5pt'>The cab request by <b>"+Emp_QLID+"</b> for &quot;Cab Request Id:&quot; <b>"+result+ " </b>has been forwarded to the Approver: <b>"+Mgr_QLID+"</b>  for approval. Once the request is approved, it will be forwarded to the" +
                                                    "   concerned person.</span></span><span style='font-size:8.5pt;font-family:&quot;Verdana&quot;,sans-serif;" +
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
                                                    "   <span class='mousetype1'>© 2010 NCR Corporation. All rights reserved.</span></span></p>" +
                                                    "   </td>" +
                                                    "  </tr>" +
                                                    " </tbody></table></center>");
                }
                    sendPushService = new UtilServiceImpl();
                    String pushmsg = "Cab Request Id:"+result+"\n"+
                            "Employee QLId:"+Emp_QLID+"\n"+
                            "From: "+date+"\n"+
                            "To: "+ End_Date_Time+"\n"+
                            "Source: "+Source+"\n"+
                            "Destination: "+Destination+"\n"+
                            "Time: "+time+"\n"+
                            "*Reason*: " +Reason+"\n";
                    
                    
                    sendPushService.sendPushMessage("eqgNLkCK2JU:APA91bFKydOWHS-PVWcme-Dqit3VoGNpCEklJRIl3oiDgkCLO0RmRwzTXeaCH8IYhyELOsHi_Fa651300g3DDn5lYNVzo6zglMd5qPOLoDp1lSKEOH_UnXFQHYk_u-PKIusPpb_acEU-","Cab Request",pushmsg);
                    
                    
                    
                    
                    
                    
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
			//			System.out.println("request received"+jsonRequest.toString());
			JSONArray jsonRequestArr=jsonRequest.getJSONArray("Request_ids");
			JSONObject allocation;
			if(new RequestServiceImpl().onApprovedService(jsonRequestArr))
			{
				System.out.print("cab alloted");
				allocation=new JSONObject();
				allocation.put("Allocated", "0");
				jsonResponse =new RequestServiceImpl().getRequest(allocation);
			}
			else {
				System.out.print("cab unalloted");
				jsonResponse.put(false);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response = Response.status(200).type("application/json").entity(jsonResponse.toString()).build(); 	
		return response;

	} 

	//	@GET  
	//	@Path("/excel")  
	////	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	//	@Produces("application/vnd.ms-excel")
	//	public Response test2() {  
	//	    File file = new File("C:/Users/kg250190/Documents/Leaves.xlsx");  
	//	    ResponseBuilder rb = Response.ok(file);  
	//	    rb.header("content-disposition", "attachment; filename=\"Leaves.xlsx\"");  
	//	    return rb.build();  
	//	}  



}
