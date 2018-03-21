package ncab.webservice;

import java.text.ParseException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import ncab.dao.impl.UtilServiceImpl;

@Path("/UtilityService")
public class UtilityService {

	UtilServiceImpl utilServiceImpl;
	Response response;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sendPush")
	public Response sendPush(String jsonrequest){
		
		JSONObject jsonreq = new JSONObject();
		JSONObject jsonres = new JSONObject();
		
		try {
			jsonreq = new JSONObject(jsonrequest);			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject jsonresponse = new JSONObject();
	
		int result=0;
		
		utilServiceImpl = new UtilServiceImpl();
		
			String to = jsonreq.getString("to");
			String title = jsonreq.getString("title");
			String message = jsonreq.getString("message");
			
			
			result=utilServiceImpl.sendPushMessage(to,title,message);	
			
				if (result>0) {
						jsonresponse.put("status", "success");
						jsonres = jsonresponse;
				}else {
					return Response.status(200).type("application/json").entity(new JSONObject().put("result", "fail").toString()).build();
			}
				 response = Response.status(200).type("application/json").entity(jsonres.toString()).build(); 		
		
		return response;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sendEmail")
	
	public JSONObject sendEmail(JSONObject jsonRequest) {
		JSONObject jsonResponse =new JSONObject();
		
		utilServiceImpl = new UtilServiceImpl();
		
		if(utilServiceImpl.sendEmailMessage(jsonRequest.getString("from"),jsonRequest.getString("recepient1"),jsonRequest.getString("recepient2"),jsonRequest.getString("recepient3"),jsonRequest.getString("subject"),jsonRequest.getString("message")))
				jsonResponse.put("status", "success");
			else
				jsonResponse.put("status", "fail");
		return jsonResponse;
	}
	
	
	
	
}
