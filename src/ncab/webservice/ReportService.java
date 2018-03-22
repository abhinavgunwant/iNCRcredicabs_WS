package ncab.webservice;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

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


	//Unscheduled summary report by manager starts

	@GET
	@Path("/managerreportDefaultSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response managerUnscheduledSummary() throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.managerSummary();
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}
	//Unscheduled summary report by manager ends


	//Unscheduled detailed report by vendor starts
	@GET
	@Path("/vendorreportDefaultDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendorUnscheduledDetailed() throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.vendorDetailed();
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}
	//Unscheduled detailed report by vendor ends


	//Unscheduled summary report by vendor starts
	@GET
	@Path("/vendorreportDefaultSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendorUnscheduledSummary() throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.vendorSummary();
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}

	//Unscheduled summary report by vendor ends

	//Unscheduled summary report by employee starts
	@GET
	@Path("/employeereportDefaultSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employeeUnscheduledSummary() throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.employeeSummary();
		System.out.print(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}

	//Unscheduled summary report by employee ends


	//Unscheduled detailed report by employee starts
	@GET
	@Path("/employeereportDefaultDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employeeUnscheduledDetailed() throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.employeeDetailed();
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}

	//Unscheduled detailed report by employee ends


	//overloading

	@GET
	@Path("/employeereportParamDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employeeUnscheduledDetailedOverload(JSONObject jsonrequest) throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.employeeDetailed(jsonrequest);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}


	@GET
	@Path("/employeereportParamSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employeeUnscheduledSummaryOverload(JSONObject jsonrequest) throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.employeeSummary(jsonrequest);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}




	@GET
	@Path("/vendorreportParamSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendorUnscheduledSummaryOverload(JSONObject jsonrequest) throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.vendorSummary(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}


	@GET
	@Path("/vendorreportParamDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendorUnscheduledDetailedOverload(JSONObject jsonrequest) throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.vendorDetailed(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}


	@GET
	@Path("/managerreportParamSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response managerUnscheduledSummary(JSONObject jsonrequest) throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.managerSummary(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}


	//overloading
	//transport code starts
	@GET
	@Path("/transportbillingreport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response transportbilling(/*String jsonrequest*/) throws ClassNotFoundException, SQLException, ParseException, java.text.ParseException
	{   JSONObject jsonreq = new JSONObject();
	String start_date ;
	String end_date;
	ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
	try {
		///jsonreq = new JSONObject(jsonrequest);                   
		//month = jsonreq.getString("month");
		//     year = jsonreq.getString("year");
		//month = jsonreq.getString("month");
		//     year = jsonreq.getString("year");
		//month = jsonreq.getString("month");
		//     year = jsonreq.getString("year");
		//month = jsonreq.getString("month");
		//     year = jsonreq.getString("year");
		//month = jsonreq.getString("month");
		//     year = jsonreq.getString("year");
		//month = jsonreq.getString("month");
		//     year = jsonreq.getString("year");
		//month = jsonreq.getString("month");
		//     year = jsonreq.getString("year");
		//month = jsonreq.getString("month");
		//     year = jsonreq.getString("year");
	} catch (ParseException e) {
		e.printStackTrace();
	}


	JSONArray jsonarray=new JSONArray();
	jsonarray =reportserviceimpl.gettransportbilling(/*month,year,*/);
	Response response=Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	System.out.println(jsonarray);
	return response;

	}

	//transport code ends     


	@POST
	@Path("/vendorwisebillreport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendorbill(String jsonrequest) throws ClassNotFoundException, SQLException, ParseException, java.text.ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();

		JSONObject jsonreq = new JSONObject();


		try {
			jsonreq = new JSONObject(jsonrequest);              
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String shift_id=jsonreq.getString("shift_id");
		String vendor_name=jsonreq.getString("vendor_name");
		String FromDate = jsonreq.getString("FromDate");
		String ToDate = jsonreq.getString("ToDate");
		jsonarray =reportserviceimpl.getVendorwisebillReport(shift_id,vendor_name,FromDate,ToDate);
		Response response=Response.status(200).type("application/json").entity(jsonarray.toString()).build();
		return response;

	}

	@GET
	@Path("/VendorNames")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVendorNames(){
		ReportServiceImpl demodaoimpl = new ReportServiceImpl();
		JSONArray jsonresponse = new JSONArray();
		jsonresponse = demodaoimpl.getVendorNames();

		Response response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();

		return response;
	}





}
