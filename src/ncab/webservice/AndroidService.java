package ncab.webservice;

import java.text.ParseException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import ncab.dao.impl.AndroidServiceImpl;


@Path("/AndroidService")
public class AndroidService {

              public AndroidService() {
                             // TODO Auto-generated constructor stub
			  }
			  
              @POST
              @Produces(MediaType.APPLICATION_JSON)
              @Path("/approval")
              public Response sendApproval(String jsonrequest){

                             JSONObject jsonreq = new JSONObject();
                             JSONObject jsonres = new JSONObject();

                             try {
                                           jsonreq = new JSONObject(jsonrequest);              
                             } catch (ParseException e) {
                                           e.printStackTrace();
                             }

                             JSONObject jsonresponse = new JSONObject();

                             

                             AndroidServiceImpl AndroidServiceImpl  = new AndroidServiceImpl();

                             String req_id = jsonreq.getString("request_id");

                             String action = jsonreq.getString("Approval");
                   int result=0;
                             System.out.println("Request_id recieved"+ req_id);           
                             System.out.println("action recieved"+ action);

                             result=AndroidServiceImpl.saveApproval(action,req_id);
                             System.out.println(result);
                                                
                                                      if (result==1) {
                                                                   
                                                                    jsonresponse.put("Request_Id", result );
                                                                    jsonresponse.put("status", "success");
                                                                   
                                                                    jsonres = jsonresponse;
                                                      }else {   if(result==7)
                                                      {
                                                    	  jsonresponse.put("Request_Id", result );
                                                          jsonresponse.put("status", "Already");
                                                         
                                                          jsonres = jsonresponse;
                                                    	  
                                                    	  
                                                    	  
                                                      }else {
                                                             return Response.status(200).type("application/json").entity(new JSONObject().put("result", "fail").toString()).build();
                                                 }}

                             Response   response = Response.status(200).type("application/json").entity(jsonres.toString()).build();              

                             return response;
              }


       @POST
       @Produces(MediaType.APPLICATION_JSON)
       @Path("/checkin")
  	 public Response Employee_CheckIn(String jsonrequest) {
    	  AndroidServiceImpl demodaoimpl=new AndroidServiceImpl();
    	  System.out.println(jsonrequest);
    	  JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonrequest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 System.out.println(jsonObject);
		 String Route_No=jsonObject.getString("Route_No");
		    String date1 = null;
			date1 = jsonObject.getString("Trip_Date");
			String Check_in_Time=jsonObject.getString("Check_in_Time");
		    String Emp_Qlid=jsonObject.getString("Emp_Qlid");
    	  String Trip_Type=jsonObject.getString("Trip_Type");
    	  String Cab_Type=jsonObject.getString("Cab_Type");
    	   JSONObject jsonresponse = new JSONObject();
    	  jsonresponse = demodaoimpl.postCheckInDetails(Emp_Qlid,Route_No,date1,Check_in_Time,Trip_Type,Cab_Type); 
  		  Response   response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();              
          return response;
  	 }
       @POST
       @Produces(MediaType.APPLICATION_JSON)
       @Path("/checkout")
  	 public Response Employee_CheckOut(String jsonrequest) {
    	  AndroidServiceImpl demodaoimpl=new AndroidServiceImpl();
    	  System.out.println(jsonrequest);
    	  JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonrequest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   System.out.println(jsonObject);
		    String date1 = null;
			date1 = jsonObject.getString("Trip_Date");
			String Check_out_Time=jsonObject.getString("Check_out_Time");
		    String Emp_Qlid=jsonObject.getString("Emp_Qlid");
    	   String Trip_Type=jsonObject.getString("Trip_Type");
    	   JSONObject jsonresponse = new JSONObject();
    	  jsonresponse = demodaoimpl.postCheckOutDetails(Emp_Qlid,date1,Check_out_Time,Trip_Type); 
  		 Response   response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();              
          return response;
  	 }
       @POST
       @Produces(MediaType.APPLICATION_JSON)
       @Path("/RoasterDetailsByEmpID")
  	 public Response getRoasterDetailsByEmpID(String jsonrequest) {
    	  AndroidServiceImpl demodaoimpl=new AndroidServiceImpl();
    	  System.out.println(jsonrequest);
    	  JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonrequest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   System.out.println(jsonObject);
		   String Emp_Qlid=jsonObject.getString("Emp_Qlid");
    	   JSONObject jsonresponse = new JSONObject();
    	  jsonresponse = demodaoimpl.getRoasterDetailsByEmpID(Emp_Qlid); 
  		 Response   response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();              
          return response;
  	 }
}

