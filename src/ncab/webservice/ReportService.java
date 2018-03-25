package ncab.webservice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.PreparedStatement;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import ncab.dao.DBConnectionRo;
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

	// working
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

	// working
	@POST
	@Path("/managerreportParamSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response managerUnscheduledSummary(String jsonreq) throws SQLException, java.text.ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONObject jsonrequest=new JSONObject(jsonreq);
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.managerSummary(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}
	// working
	@GET
	@Path("/managerreportDefaultDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response managerUnscheduledDetailed() throws SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.managerDetailed();
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}


	// working
	@POST
	@Path("/managerreportParamDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response managerUnscheduledDetailedOverload(String jsonreq) throws java.text.ParseException, SQLException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONObject jsonrequest=new JSONObject(jsonreq);
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.mangerDetailed(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();


	}

	// VENDOR API'S
	// working
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

	// working
	@POST
	@Path("/vendorreportParamDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendorUnscheduledDetailedOverload(String jsonreq) throws SQLException, java.text.ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONObject jsonrequest=new JSONObject(jsonreq);
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.vendorDetailed(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}

	// working
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

	// working
	@POST
	@Path("/vendorreportParamSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendorUnscheduledSummaryOverload(String jsonreq) throws SQLException, java.text.ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONObject jsonrequest=new JSONObject(jsonreq);
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.vendorSummary(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}


	// EMPLOYEE API's   
	// working
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

	// working
	@POST
	@Path("/employeereportParamSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employeeUnscheduledSummaryOverload(String jsonreq) throws SQLException, java.text.ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		JSONObject jsonrequest=new JSONObject(jsonreq); 
		jsonarray=reportserviceimpl.employeeSummary(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}

	// working
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


	// working
	@POST
	@Path("/employeereportParamDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employeeUnscheduledDetailedOverload(String jsonreq) throws SQLException, java.text.ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		JSONObject jsonrequest=new JSONObject(jsonreq);
		jsonarray=reportserviceimpl.employeeDetailed(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}

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


	@POST
	@Path("/RouteNos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRouteNos(String VendorName){
		ReportServiceImpl demodaoimpl = new ReportServiceImpl();
		JSONArray jsonresponse = new JSONArray();
		jsonresponse = demodaoimpl.getRouteNos(VendorName);

		Response response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();

		return response;
	}


	@POST
	@Path("/getCabNobyVendorandRoute")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCabNobyVendorandRoute(String jsonRequest) throws java.text.ParseException, ClassNotFoundException, SQLException{

		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();

		JSONObject jsonreq = new JSONObject();

		try {
			jsonreq = new JSONObject(jsonRequest);              
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String VendorName=jsonreq.getString("VendorName");
		String RouteNo= jsonreq.getString("RouteNo");
		jsonarray =reportserviceimpl.getCabNobyVendorandRoute(VendorName,RouteNo);
		Response response=Response.status(200).type("application/json").entity(jsonarray.toString()).build();
		return response;



	}


	@POST
	@Path("/checkinoutreport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkinout(String jsonrequest) throws ClassNotFoundException, SQLException, ParseException, java.text.ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();

		JSONObject jsonreq = new JSONObject();


		try {
			jsonreq = new JSONObject(jsonrequest);              
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String route_no=jsonreq.getString("route_no");
		String from_date=jsonreq.getString("from_date");
		String to_date=jsonreq.getString("to_date");

		String cab_no=jsonreq.getString("cab_no");
		String emp_fname=jsonreq.getString("emp_fname");

		String emp_lname=jsonreq.getString("emp_lname");
		String vendor_name=jsonreq.getString("vendor_name");
		jsonarray =reportserviceimpl.checkinoutReport(route_no,from_date,to_date,cab_no,emp_fname,emp_lname,vendor_name);

		Response response=Response.status(200).type("application/json").entity(jsonarray.toString()).build();
		System.out.println(jsonarray);
		return response;

	}


	//transport billing report start
	@POST
	@Path("/transportbillingreport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response transportbilling(String jsonrequest) throws ClassNotFoundException, SQLException, ParseException, java.text.ParseException
	{   JSONObject jsonreq = new JSONObject();
	String month="" ;
	String year="";
	String hrtax_regular_cab="";
	String uptax_regular_cab="";
	String emp_contrib_regular="";
	String gps_regular_cab="";
	String gstTax_regular_cab="";
	String emp_contrib_shift="";
	String gps_shift_cab="";
	String gstTax_shift_cab="";
	String  hrtax_shift_cab="";
	String uptax_shift_cab="";
	String toll_shift_cab="";
	String toll_unscheduled_cab="";
	String gstTax_unscheduled="";
	String standByCab_extraKms="";
	String ratePerKm="";
	String extraMileageCost="";
	String standByCost="";
	String standByTax="";
	String otherCabCost="";
	String otherCabGST="";
	String escortGuardCost="";
	String escortGuardDropDutyCost="";
	String escortGuardTaxes="";
	String tptMobCost="";
	String overallUPtax="";
	String overallHRtax="";
	String overallTaxes="";
	String overallToll="";
	String overallGPS="";
	String foreignExPrice="";
	String toll_regular_cab ="";

	ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
	try {
		jsonreq = new JSONObject(jsonrequest);                                               
		month = jsonreq.getString("month");
		year = jsonreq.getString("year");
		toll_regular_cab  = jsonreq.getString("toll_regular_cab");
		hrtax_regular_cab = jsonreq.getString("hrtax_regular_cab");
		uptax_regular_cab = jsonreq.getString("uptax_regular_cab");
		emp_contrib_regular = jsonreq.getString("emp_contrib_regular");
		gps_regular_cab = jsonreq.getString("gps_regular_cab");
		gstTax_regular_cab = jsonreq.getString("gstTax_regular_cab");
		emp_contrib_shift = jsonreq.getString("emp_contrib_shift");
		gps_shift_cab = jsonreq.getString("gps_shift_cab");
		gstTax_shift_cab = jsonreq.getString("gstTax_shift_cab");
		hrtax_shift_cab = jsonreq.getString("hrtax_shift_cab");
		uptax_shift_cab = jsonreq.getString("uptax_shift_cab");
		toll_shift_cab = jsonreq.getString("toll_shift_cab");
		toll_unscheduled_cab = jsonreq.getString("toll_unscheduled_cab");
		gstTax_unscheduled = jsonreq.getString("gstTax_unscheduled");
		standByCab_extraKms = jsonreq.getString("standByCab_extraKms");
		ratePerKm = jsonreq.getString("ratePerKm");
		extraMileageCost = jsonreq.getString("extraMileageCost");
		standByCost = jsonreq.getString("standByCost");
		standByTax = jsonreq.getString("standByTax");
		otherCabCost = jsonreq.getString("otherCabCost");
		otherCabGST = jsonreq.getString("otherCabGST");
		escortGuardCost = jsonreq.getString("escortGuardCost");
		escortGuardDropDutyCost = jsonreq.getString("escortGuardDropDutyCost");
		escortGuardTaxes = jsonreq.getString("escortGuardTaxes");
		tptMobCost = jsonreq.getString("tptMobCost");
		overallUPtax = jsonreq.getString("overallUPtax");
		overallHRtax = jsonreq.getString("overallHRtax");
		overallTaxes = jsonreq.getString("overallTaxes");
		overallToll = jsonreq.getString("overallToll");
		overallGPS = jsonreq.getString("overallGPS");
		foreignExPrice = jsonreq.getString("foreignExPrice");
	} catch (ParseException e) {
		e.printStackTrace();
	}
	JSONArray jsonarray=new JSONArray();
	jsonarray =reportserviceimpl.gettransportbilling(
			month,
			year,
			hrtax_regular_cab,
			uptax_regular_cab,
			emp_contrib_regular ,
			gps_regular_cab,
			gstTax_regular_cab,
			emp_contrib_shift,
			gps_shift_cab,
			gstTax_shift_cab,
			hrtax_shift_cab,
			uptax_shift_cab,
			toll_shift_cab,
			toll_unscheduled_cab,
			gstTax_unscheduled,
			standByCab_extraKms,
			ratePerKm,
			extraMileageCost,
			standByCost,
			standByTax,
			otherCabCost,
			otherCabGST,
			escortGuardCost,
			escortGuardDropDutyCost,
			escortGuardTaxes,
			tptMobCost,
			overallUPtax,
			overallHRtax,
			overallTaxes,
			overallToll,overallGPS,
			foreignExPrice,
			toll_regular_cab);
	Response response=Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	System.out.println(jsonarray);
	return response;

	}




}
