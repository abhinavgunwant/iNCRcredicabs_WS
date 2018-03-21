package ncab.webservice;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import ncab.dao.impl.ReportServiceImpl;


@Path("/ReportService")
public class ReportService {

	public ReportService() {
		// TODO Auto-generated constructor stub
		
	}
	
	//service for getting the details of the employee in reports
	//employee code starts
	@GET
	@Path("/employeereport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employee() throws ClassNotFoundException, SQLException
	{
		ReportServiceImpl empdao=new ReportServiceImpl();
		JSONArray jsonarray=empdao.employeeDAO();
		Response response=Response.status(200).type("application/json").entity(jsonarray.toString()).build();
		System.out.println(jsonarray);
		return response;
}
	//employee code ends
	
	
	//manager code starts
	@GET
	@Path("/managerreport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response manager() throws ClassNotFoundException, SQLException, ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray =reportserviceimpl.getRequest1();
		Response response=Response.status(200).type("application/json").entity(jsonarray.toString()).build();
		System.out.println(jsonarray);
		return response;
		
	}
	//manager code ends
	
	
	//vendor code starts
	@GET
	@Path("/vendorreport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendor() throws ClassNotFoundException, SQLException, ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray =reportserviceimpl.getRequest2();
		Response response=Response.status(200).type("application/json").entity(jsonarray.toString()).build();
		System.out.println(jsonarray);
		return response;
		
	}
	
	//vendor code ends	
	
}
