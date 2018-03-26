package ncab.webservice;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import ncab.dao.impl.RosterServiceImpl;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/RosterService")
public class RosterService {
	
	@POST
	@Path("/showRosterInfo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
		public Response getRoster(String json){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Start :: "+sdf.format(cal.getTime()));
		
			JSONObject jsonobj;
			Response response=null;
			try {
				jsonobj = new JSONObject(json);
				JSONArray jsonArray =new JSONArray();
				
				RosterServiceImpl frd=new RosterServiceImpl();
				jsonArray =frd.showRosterInfo(jsonobj);
				
				System.out.println("End :: "+sdf.format(cal.getTime()));
				
				response = Response.status(200).type("application/json").entity(jsonArray.toString()).build(); 		

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		return response;
	}


	@POST
	@Path("/UploadFileData")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("uploadFile") InputStream fileInputStream,
			@FormDataParam("uploadFile") FormDataContentDisposition fileFormDataContentDisposition) throws Exception {
		Response response;
		JSONObject jsobj=new JSONObject();
		System.out.println("TestCheck");
		RosterServiceImpl frd=new RosterServiceImpl();
		jsobj=frd.insertIntoDB(fileInputStream, fileFormDataContentDisposition);
		System.out.println("Success import excel to mysql table");
		response = Response.status(200).type("application/json").entity(jsobj.toString()).build(); 		
		return response;
	}




	@POST
	@Path("/AddEmpToDb")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
		public Response addEmp(String json){
			Response response=null;
			try {
				JSONObject js=new JSONObject(json);
				RosterServiceImpl frd=new RosterServiceImpl();
				JSONObject status=frd.addEmpToDb(js);
				response = Response.status(200).type("application/json").entity(status.toString()).build(); 		

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		return response;
	}

	@POST
	@Path("/getAddQlid")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
		public Response getAddData(String json){
			Response response=null;
			try {
				JSONObject js=new JSONObject(json);
				RosterServiceImpl frd=new RosterServiceImpl();
				JSONArray status=frd.getAddData(js);
				response = Response.status(200).type("application/json").entity(status.toString()).build(); 		

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		return response;
	}

	@POST
	@Path("/getcablist")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
		public Response getCabList(String s){
			Response response=null;
			try {
				RosterServiceImpl frd=new RosterServiceImpl();
				JSONArray status=frd.getcablist(s);
				response = Response.status(200).type("application/json").entity(status.toString()).build(); 		

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		return response;
	}


	@POST
	@Path("/inactiveqlid")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response InactiveEmp(String str){
		Response response=null;
		try {
				JSONObject jobj=new JSONObject(str);
						
						RosterServiceImpl frd=new RosterServiceImpl();
						JSONObject status =frd.inactiveqlid(jobj);
						response = Response.status(200).type("application/json").entity(status.toString()).build(); 
						
		} catch (Exception e) {
				// TODO Auto-generated catch block
						
		}
		return response;
				
		
	}


	//Jaspreet
	@POST
	@Path("/driver")
	@Produces(MediaType.APPLICATION_JSON)
	public Response driver() {
		Response response = null;
		RosterServiceImpl frd=new RosterServiceImpl();
		JSONArray jarr = new JSONArray();
		System.out.println("Inside display empdetails");
		try {
			System.out.println("inside try before call");
			jarr = frd.getDriver();
			response = Response.status(200).type("application/json").entity(jarr.toString()).build();

		} catch (Exception e) {

		}
		return response;
	}
	
	@POST
	@Path("/vendorDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response showVendor() {
		RosterServiceImpl frd=new RosterServiceImpl();
		Response response = null;

		try {
			JSONArray jsonArray = new JSONArray();
			jsonArray = frd.showVendor();
			response = Response.status(200).type("application/json").entity(jsonArray.toString()).build();
		} catch (Exception e) {
			System.out.println("Error in display");
			e.printStackTrace();
		}
		return response;
	}

	@POST
	@Path("/empDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response empDetail(String jsonstring) {
		Response response = null;
		JSONObject json;
		JSONObject json1 = new JSONObject();
		RosterServiceImpl frd=new RosterServiceImpl();
		System.out.println("Inside display empdetails");
		try {
			json = new JSONObject(jsonstring);
			System.out.println("inside try before call");
			json1 = frd.getEmpDetails(json);

			response = Response.status(200).type("application/json").entity(json1.toString()).build();

		} catch (Exception e) {

		}
		return response;
	}

	@POST
	@Path("/empDeactive")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response empDeactive(String qlid) {
		int flag = 0;
		Response resp = null;
		RosterServiceImpl frd=new RosterServiceImpl();
		try {
			JSONObject json = new JSONObject(qlid);
			flag = frd.empdeact(json);
			System.out.println("Flag: " + flag);
		} catch (ParseException e) {

		}
		JSONObject json1 = new JSONObject();
		json1.put("response", flag);
		resp = Response.status(200).type("application/json").entity(json1.toString()).build();
		return resp;
	}

	@POST
	@Path("/empqlid")
	@Produces(MediaType.APPLICATION_JSON)
	public Response empQlid() {
		Response response = null;
		RosterServiceImpl frd=new RosterServiceImpl();
		JSONArray jarr = new JSONArray();
		System.out.println("Inside display empdetails");
		try {
			System.out.println("inside try before call");
			jarr = frd.getQlid();
			response = Response.status(200).type("application/json").entity(jarr.toString()).build();

		} catch (Exception e) {

		}
		return response;
	}

	@POST
	@Path("/getCab")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response cabNo(String jsn) {
		Response response = null;
		RosterServiceImpl frd=new RosterServiceImpl();

		JSONArray jsonarr = new JSONArray();
		try {
			JSONObject json = new JSONObject(jsn);
			jsonarr = frd.showCabs(json);
			response = Response.status(200).type("application/json").entity(jsonarr.toString()).build();
		} catch (ParseException e) {
			System.out.println("Error: " + e.getMessage());
		}
		return response;
	}


	@POST
	@Path("/insertRouteSCH")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response newRoute(String jsonarr) {
		int flag = 0;
		Response resp = null;
		RosterServiceImpl frd=new RosterServiceImpl();
		try {
			JSONArray json = new JSONArray(jsonarr);
			System.out.println("Before function call");
			flag = frd.setNewRouteSCH(json);
		} catch (ParseException e) {

		}
		JSONObject json1 = new JSONObject();
		json1.put("response", flag);
		resp = Response.status(200).type("application/json").entity(json1.toString()).build();
		return resp;
	}

	@POST
	@Path("/insertRouteUnSCH")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response newRouteUnSCH(String jsonarr) {
		int flag = 0;
		Response resp = null;
		RosterServiceImpl frd=new RosterServiceImpl();
		try {
			JSONArray json = new JSONArray(jsonarr);
			System.out.println("Before function call");
			flag = frd.setNewRouteUnSCH(json);
		} catch (ParseException e) {

		}
		JSONObject json1 = new JSONObject();
		json1.put("response", flag);
		resp = Response.status(200).type("application/json").entity(json1.toString()).build();
		return resp;
	}


	//saurav


	@POST
	@Path("/editd")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editinfo(String str2) {
		Response respo=null;
		try {
			JSONObject obj=new JSONObject(str2);
			RosterServiceImpl frd=new RosterServiceImpl();
			JSONObject status=frd.sauravkaeditmethod(obj);
			respo=Response.status(200).type("application/json").entity(status.toString()).build();
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		
		return respo;
	}

	//richa
	@POST
	@Path("/getRoute")
	@Produces(MediaType.APPLICATION_JSON)
		public Response getAllData(){
		
			Response response=null;
			try {
				JSONArray jsonArray =new JSONArray();
				RosterServiceImpl frd=new RosterServiceImpl();
				jsonArray =frd.getAllRoute();
				response = Response.status(200).type("application/json").entity(jsonArray.toString()).build(); 		

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		return response;
	}	



	@POST
	@Path("/getCabNo")
	@Produces(MediaType.APPLICATION_JSON)
		public Response getCab(){
		Response response=null;	
			try {
				JSONArray jsonArray =new JSONArray();
				RosterServiceImpl frd=new RosterServiceImpl();
				jsonArray =frd.getAllCab();
				response = Response.status(200).type("application/json").entity(jsonArray.toString()).build(); 		

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return response;
	}	

	@POST
	@Path("/getVendor")
	@Produces(MediaType.APPLICATION_JSON)
		public Response getVendor(){
		
			Response response=null;
			try {
				JSONArray jsonArray =new JSONArray();
				RosterServiceImpl frd=new RosterServiceImpl();
				jsonArray =frd.getAllVendor();
				response = Response.status(200).type("application/json").entity(jsonArray.toString()).build(); 		

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return response;
	}	




	@POST
	@Path("/UpdateRoute")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response route_update(String route_no ){
		Response response=null;
		
		JSONObject json=null;
		try {
			json = new JSONObject(route_no);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("implementation class starts");
		RosterServiceImpl frd=new RosterServiceImpl();
	JSONObject status=frd.updatedRoute(json);		
	response = Response.status(200).type("application/json").entity(status.toString()).build(); 		
		return response;
	}
	
	
	
	
	@POST
	@Path("/downloadexcel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response write_excel(String s){
		Response response=null;
		System.out.println("implementation class starts");
		RosterServiceImpl frd=new RosterServiceImpl();
	    JSONObject status=frd.download_data(s);		
	response = Response.status(200).type("application/json").entity(status.toString()).build(); 		
		return response;
	}
	

@POST
@Path("/getRouteDetails")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
	public Response getRouteData(String json){
          Response response=null;
		try {
			JSONObject js=new JSONObject(json);
			RosterServiceImpl frd=new RosterServiceImpl();
	     	  JSONArray status=frd.getRouteDatas(js);
	     	  response = Response.status(200).type("application/json").entity(status.toString()).build(); 		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	return response;
}

@POST
@Path("/getVendorForFilter")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
	public Response getVendorData(String json){
          Response response=null;
		try {
			RosterServiceImpl frd=new RosterServiceImpl();
	     	  JSONArray status=frd.getVendorForFilter();
	     	  response = Response.status(200).type("application/json").entity(status.toString()).build(); 		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

	return response;
}
	

@POST
@Path("/getStartEndDate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
	public Response getStartandEnd(String strdiv){
	
		Response response=null;
		try {
			JSONArray jsonArray =new JSONArray();
			RosterServiceImpl frd=new RosterServiceImpl();
			jsonArray =frd.getStartandEndDate(strdiv);
			response = Response.status(200).type("application/json").entity(jsonArray.toString()).build(); 		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	return response;
}

@POST
@Path("/getCabShift")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public Response getShiftCab(String data){
	JSONArray jsonarr=new JSONArray();
	RosterServiceImpl frd=new RosterServiceImpl();
	Response resp=null;
	try{
	JSONArray json=new JSONArray(data);
	System.out.println("---Entering getcabshift---");
	jsonarr=frd.getCabShift(json);
	System.out.println("---Exiting getcabshift---");
	resp = Response.status(200).type("application/json").entity(jsonarr.toString()).build();
	}
	catch(ParseException e){
		System.out.println("Error in data:---"+e.getMessage());
	}
	return resp;
}

@POST
@Path("/complaint")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public Response complaint(String data){
	int flag=0;
	String mailresp="";
	
	RosterServiceImpl frd=new RosterServiceImpl();
	Response resp=null;
	try{
	JSONObject json=new JSONObject(data);
	System.out.println("--Entering feedback--");
	flag=frd.feedback(json);
	System.out.println("--Exiting feedback--");
	System.out.println("--Entering sendmail--");
	mailresp=frd.sendMail(json);
	System.out.println("--Exiting sendmail--");
	JSONObject json1 = new JSONObject();
	json1.put("response from DB:", flag);
	json1.put("response from Mail:", mailresp);
	resp = Response.status(200).type("application/json").entity(json1.toString()).build();
	}
	catch(ParseException e){
		System.out.println("Error in data:---"+e.getMessage());
	}
	return resp;
}



	}


	
	

