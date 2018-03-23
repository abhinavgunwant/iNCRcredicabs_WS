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
	
	@POST
	@Path("/employeereportParamDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employeeUnscheduledDetailedOverload(String jsonreq) throws SQLException, java.text.ParseException
	{
	ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
	JSONObject jsonrequest= new JSONObject(jsonreq);
	JSONArray jsonarray=new JSONArray();
	jsonarray=reportserviceimpl.employeeDetailed(jsonrequest);
	return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}
	
	
	@POST
	@Path("/employeereportParamSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response employeeUnscheduledSummaryOverload(String jsonreq) throws SQLException, java.text.ParseException
	{
	ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
	JSONObject jsonrequest= new JSONObject(jsonreq);
	JSONArray jsonarray=new JSONArray();
	jsonarray=reportserviceimpl.employeeSummary(jsonrequest);
	return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}
	
	
	
	
	@POST
	@Path("/vendorreportParamSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendorUnscheduledSummaryOverload(String jsonreq) throws SQLException, java.text.ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONObject jsonrequest= new JSONObject(jsonreq);
		JSONArray jsonarray=new JSONArray();
		jsonarray=reportserviceimpl.vendorSummary(jsonrequest);
		System.out.println(jsonarray);
		return Response.status(200).type("application/json").entity(jsonarray.toString()).build();
	}
	
	
	@POST
	@Path("/vendorreportParamDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response vendorUnscheduledDetailedOverload(String jsonreq) throws SQLException, java.text.ParseException
	{
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONObject jsonrequest= new JSONObject(jsonreq);
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

     
   //checkin checkout code starts
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

     //checkin checkout  code ends

   //transport billing report start
                public JSONArray gettransportbilling(
                                                String month, String year, String hrtax_regular_cab, String uptax_regular_cab,
                                                String emp_contrib_regular, String gps_regular_cab, String gstTax_regular_cab, String emp_contrib_shift,
                                                String gps_shift_cab, String gstTax_shift_cab, String hrtax_shift_cab, String uptax_shift_cab,
                                                String toll_shift_cab, String toll_unscheduled_cab, String gstTax_unscheduled, String standByCab_extraKms,
                                                String ratePerKm, String extraMileageCost, String standByCost, String standByTax, String otherCabCost,
                                                String otherCabGST, String escortGuardCost, String escortGuardDropDutyCost, String escortGuardTaxes,
                                                String tptMobCost, String overallUPtax, String overallHRtax, String overallTaxes, String overallToll,
                                                String overallGPS, String foreignExPrice ) throws ClassNotFoundException, SQLException
                { 
                int total_no_regular_cab_small=0,total_no_regular_cab_big = 0;
                                DBConnectionRo dbconnection=new DBConnectionRo();
                                Connection connection=dbconnection.getConnection();
                                JSONArray jsonarray=new JSONArray();
                                JSONObject jsonobjt=new JSONObject();
                                JSONObject jsonobjfinal=new JSONObject();
                                JSONObject jsonobjfinalnew=new JSONObject();
                                jsonobjfinal.put("total_no_of_unscheduled_employees","0");
                                jsonobjfinal.put("total_no_of_unscheduled_cabs","0");
                                jsonobjfinal.put("total_unscheduled_cost","0");
                                jsonobjfinal.put("total_no_regular_cab_small","0");
                                jsonobjfinal.put("total_no_of_regular_employees_small","0");
                                jsonobjfinal.put("total_no_regular_cab_big","0");
                    jsonobjfinal.put("total_no_of_regular_employees_big","0");
                    jsonobjfinal.put("total_cost_regular_cab_small",""+"0");
                    jsonobjfinal.put("total_cost_regular_cab_big",""+"0");
                    jsonobjfinal.put("total_no_shift_cab_small",""+"0");
                                jsonobjfinal.put("total_no_of_shift_employees_small","0");
                                jsonobjfinal.put("total_no_shift_cab_big",""+"0");
                                jsonobjfinal.put("total_no_of_shift_employees_big","0");
                                jsonobjfinal.put("total_cost_shift_cab_small",""+"0");
                    jsonobjfinal.put("total_cost_shift_cab_big",""+"0");
                                
//vendor cost
                                
                                
                                JSONArray jsonarray0=new JSONArray();
        connection=dbconnection.getConnection();        
                                PreparedStatement ps= (PreparedStatement) connection.prepareStatement(" SELECT vendor_name,SUM(Cab_Cost) AS vendor_cost,Roster_Month,Roster_Year FROM B GROUP BY vendor_name ;");
                                ResultSet rs=ps.executeQuery();
                                int in=0;
                                while (rs.next())
                                {
                                JSONObject jsonobj=new JSONObject();                                                if(rs.getString(3).equalsIgnoreCase("3") && rs.getString(4).equals("2018")) {
                                        JSONObject jsonresponse=new JSONObject();
                                        jsonresponse.put("vendor_name",rs.getString(1));
                                        jsonresponse.put("vendor_total_cost",rs.getString(2));

                                        jsonobj.put("vendor"+in++, jsonresponse);
                                        jsonarray0.put(jsonobj);}
                                }

                                //unscheduled report
                                
                                String query1="SELECT  YEAR(Start_Date) as Roster_Year,MONTH(Start_Date) as Roster_Month , COUNT(DISTINCT (Emp_Qlid)) AS Total_Employees_No , COUNT(DISTINCT (cab_no)) AS no_of_cabs FROM ncab_roster_tbl WHERE Shift_Id=4 GROUP BY MONTH(Start_Date) , YEAR(Start_Date) ;";
                                PreparedStatement ps1=(PreparedStatement) connection.prepareStatement(query1);
                                ResultSet rs1=ps1.executeQuery();
                                
                                while(rs1.next())
                                {int i=0;
                                                JSONObject jsonobj=new JSONObject();
                                
                                                                String Roster_Month=rs1.getString("Roster_Month");
                                                                String Roster_Year=rs1.getString("Roster_Year");
                                                                String unscheduled_Total_Employees_No=rs1.getString("Total_Employees_No");
                                                                String unscheduled_no_of_cabs=rs1.getString("no_of_cabs");

                                                                if(Roster_Month.equals("3") && Roster_Year.equals("2018"))
                                                                {
                                                                                jsonobjfinal.put("total_no_of_unscheduled_employees",unscheduled_Total_Employees_No);
                                                                                jsonobjfinal.put("total_no_of_unscheduled_cabs",unscheduled_no_of_cabs);
                                                                                
                                                                }
                                                                
                                }
                                
                                
                                String query5="SELECT YEAR(Start_Date) as Roster_Year, MONTH(Start_Date) as Roster_Month,SUM(Cab_Cost) as total_cost_of_Unscheduled_cabs FROM sum_of_unscheduled_cabs GROUP BY YEAR(Start_Date), MONTH(Start_Date);";
                                PreparedStatement ps5=(PreparedStatement) connection.prepareStatement(query5);
                                ResultSet rs5=ps5.executeQuery();
                                
                                while(rs5.next())
                                {int i=0;
                                                JSONObject jsonobj=new JSONObject();
                                                                String Roster_Month=rs5.getString("Roster_Month");
                                                                String Roster_Year=rs5.getString("Roster_Year");
                                                                String unscheduled_cost=rs5.getString("total_cost_of_Unscheduled_cabs");
                                                                if(Roster_Month.equals("3") && Roster_Year.equals("2018"))
                                                                {
                                                                                jsonobjfinal.put("total_unscheduled_cost",unscheduled_cost);
                                                                }

                                }

                                //regular report
                                
                                String query3="SELECT YEAR(Start_Date) as Roster_Year ,MONTH(Start_Date) as Roster_Month ,cab_type,COUNT(DISTINCT Emp_Qlid) AS Total_Employees,COUNT(DISTINCT Cab_No) AS Total_Cabs FROM ncab_cab_master_tbl INNER JOIN ncab_roster_tbl ON ncab_cab_master_tbl.cab_license_plate_no = ncab_roster_tbl.Cab_No WHERE Shift_Id=1 GROUP BY ncab_cab_master_tbl.cab_type,YEAR(Start_Date),MONTH(Start_Date);";
                                PreparedStatement ps3=(PreparedStatement) connection.prepareStatement(query3);
                                ResultSet rs3=ps3.executeQuery();
                                
                                while(rs3.next())
                                {int i=0;
                                                JSONObject jsonobj=new JSONObject();
                                                                String Roster_Month=rs3.getString("Roster_Month");
                                                                String Roster_Year=rs3.getString("Roster_Year");
                                                                String cab_type=rs3.getString("cab_type");
                                                                String Total_Cabs=rs3.getString("Total_Cabs");
                                                                //String Total_Cost=rs3.getString("Total_Cost");
                                                                String Total_Employees=rs3.getString("Total_Employees");
                                                                System.out.println("in regular ");
                                                                if(Roster_Month.equalsIgnoreCase("3") && Roster_Year.equals("2018"))
                                                                { System.out.println("in regular if1");
                                                                                if(cab_type.equalsIgnoreCase("Small"))
                                                                                {              System.out.println("in");
                                                                                
                                                                                jsonobjfinal.put("total_no_regular_cab_small",Total_Cabs);
                                                                                jsonobjfinal.put("total_no_of_regular_employees_small",Total_Employees);
                                                                                                
                                                                                                
                                                                                }
                                                                                if( cab_type.equalsIgnoreCase("Big"))
                                                                                {              System.out.println("in big regular");        

                                                                                jsonobjfinal.put("total_no_regular_cab_big",Total_Cabs);
                                                                
                                                                                jsonobjfinal.put("total_no_of_regular_employees_big",Total_Employees);
                                                                                                
                                                                                                
                                                                                }
                
                                                                }

                                }
                
                String query4="SELECT YEAR(Start_Date) as Roster_Year, MONTH(Start_Date) as Roster_Month,cab_type,SUM(Cab_Cost) as Total_Cost FROM sum_of_scheduled_cabs GROUP BY YEAR(Start_Date), MONTH(Start_Date),cab_type;";
                                PreparedStatement ps4=(PreparedStatement) connection.prepareStatement(query4);
                                ResultSet rs4=ps4.executeQuery();
                                int sumcosts=0 , sumcostb=0;
                                while(rs4.next())
                                {int i=0;
                                                JSONObject jsonobj=new JSONObject();
                                                String Roster_Month=rs4.getString("Roster_Month");
                                                                String Roster_Year=rs4.getString("Roster_Year");
                                                                String cab_type=rs4.getString("cab_type");
                                                                String Total_Cost=rs4.getString("Total_Cost");
                if(Roster_Month.equalsIgnoreCase("3") && Roster_Year.equals("2018"))
                                                                { 
                                                                                if(cab_type.equalsIgnoreCase("Small"))
                                                                                {              System.out.println("in");
                                                                                sumcosts=sumcosts+Integer.parseInt(Total_Cost);
                    jsonobjfinal.put("total_cost_regular_cab_small",""+sumcosts);
                
                                                                                }
                                                                                if( cab_type.equalsIgnoreCase("Big"))
                                                                                {              System.out.println("in big regular");        

                                                                                sumcostb=sumcostb+Integer.parseInt(Total_Cost);
                    jsonobjfinal.put("total_cost_regular_cab_big",""+sumcostb);
                
                                                                                }
                                                                                                                                
                                                }}

                                                                //shift report
                                                                
                                                                String query6="SELECT YEAR(Start_Date) as Roster_Year,MONTH(Start_Date) as Roster_Month,cab_type,COUNT(DISTINCT Emp_Qlid) AS Total_Employees,COUNT(DISTINCT Cab_No) AS Total_Cabs FROM ncab_cab_master_tbl INNER JOIN ncab_roster_tbl ON ncab_cab_master_tbl.cab_license_plate_no = ncab_roster_tbl.Cab_No WHERE Shift_Id=2 OR Shift_Id=3 GROUP BY ncab_cab_master_tbl.cab_type,YEAR(Start_Date),MONTH(Start_Date);";
                                                                PreparedStatement ps6=(PreparedStatement) connection.prepareStatement(query6);
                                                                ResultSet rs6=ps6.executeQuery();
                                                                int sumcabs=0 ;
                                                                int sumcab=0 ;
                                                                while(rs6.next())
                                                                {
                                                                                
                                                                                                String Roster_Month=rs6.getString("Roster_Month");
                                                                                                String Roster_Year=rs6.getString("Roster_Year");
                                                                                                String cab_type=rs6.getString("cab_type");
                                                                                                String Total_Cabs=rs6.getString("Total_Cabs");
                                                                                                //String Total_Cost=rs3.getString("Total_Cost");
                                                                                                String Total_Employees=rs6.getString("Total_Employees");
                                                                                                
                                                                                                if(Roster_Month.equalsIgnoreCase("3") && Roster_Year.equals("2018"))
                                                                                                { 
                                                                                                                if(cab_type.equalsIgnoreCase("Small"))
                                                                                                                {              System.out.println("in");
                                                                                                                
                                                                                                                sumcabs=sumcabs+Integer.parseInt(Total_Cabs);
                                                                                                    jsonobjfinal.put("total_no_shift_cab_small",""+sumcabs);
                                                                                                                jsonobjfinal.put("total_no_of_shift_employees_small",Total_Employees);
                                                                                                                                
                                                                                                                }
                                                                                                                if( cab_type.equalsIgnoreCase("Big"))
                                                                                                                {              System.out.println("in big shift");              
                                                                                                    sumcab=sumcab+Integer.parseInt(Total_Cabs);
                                                                                                                jsonobjfinal.put("total_no_shift_cab_big",""+sumcab);
                                                                                                    jsonobjfinal.put("total_no_of_shift_employees_big",Total_Employees);
                                                                                                                                
                                                                                                                }
                                                
                                                                                                }
                                                                                                
                                                                                                
                                                                }
                                                                                                
                                                                                                String query7="SELECT YEAR(Start_Date) as Roster_Year, MONTH(Start_Date) as Roster_Month,cab_type,SUM(Cab_Cost) as Total_Cost FROM sum_of_scheduled_cabs GROUP BY YEAR(Start_Date), MONTH(Start_Date),cab_type;";
                                                                                                PreparedStatement ps7=(PreparedStatement) connection.prepareStatement(query7);
                                                                                                ResultSet rs7=ps7.executeQuery();
                                                                                                int sumcostss=0 , sumcostbs=0;
                                                                                                while(rs7.next())
                                                                                                {int i=0;
                                                                                                                JSONObject jsonobj=new JSONObject();
                                                                                                                String Roster_Month=rs7.getString("Roster_Month");
                                                                                                                                String Roster_Year=rs7.getString("Roster_Year");
                                                                                                                                String cab_type=rs7.getString("cab_type");
                                                                                                                                String Total_Cost=rs7.getString("Total_Cost");
                                                                                                                                
                                                                                                                                if(Roster_Month.equalsIgnoreCase("3") && Roster_Year.equals("2018"))
                                                                                                                                { 
                                                                                                                                                if(cab_type.equalsIgnoreCase("Small"))
                                                                                                                                                {              System.out.println("in");
                                                                                                                                                sumcostss=sumcostss+Integer.parseInt(Total_Cost);
                                                                                                                                                jsonobjfinal.put("total_cost_shift_cab_small",""+sumcostss);
                                                                                                                }
                                                                                                                                                if( cab_type.equalsIgnoreCase("Big"))
                                                                                                                                                {              System.out.println("in big shift");                
                                                                                                                                                sumcostbs=sumcostbs+Integer.parseInt(Total_Cost);
                                                                                                                                                jsonobjfinal.put("total_cost_shift_cab_big",""+sumcostbs);
                                                                }
                                                                                                                                                                                
                                                                                                                }}

                                jsonobjfinal.put("total_no_regular_cab",Integer.parseInt((String) jsonobjfinal.get("total_no_regular_cab_big"))+Integer.parseInt((String) jsonobjfinal.get("total_no_regular_cab_small")));
                                jsonobjfinal.put("total_no_of_regular_employees",Integer.parseInt((String) jsonobjfinal.get("total_no_of_regular_employees_small"))+Integer.parseInt((String) jsonobjfinal.get("total_no_of_regular_employees_big")));
                                jsonobjfinal.put("total_cost__regular_cab",Integer.parseInt((String) jsonobjfinal.get("total_cost_regular_cab_small"))+Integer.parseInt((String) jsonobjfinal.get("total_cost_regular_cab_big")));
                                jsonobjfinal.put("total_no_shift_cab",Integer.parseInt((String) jsonobjfinal.get("total_no_shift_cab_small"))+Integer.parseInt((String) jsonobjfinal.get("total_no_shift_cab_big")));
                                jsonobjfinal.put("total_no_of_shift_employees",Integer.parseInt((String) jsonobjfinal.get("total_no_of_shift_employees_small"))+Integer.parseInt((String) jsonobjfinal.get("total_no_of_shift_employees_big")));
                                jsonobjfinal.put("total_cost_shift_cab",Integer.parseInt((String) jsonobjfinal.get("total_cost_shift_cab_small"))+Integer.parseInt((String) jsonobjfinal.get("total_cost_shift_cab_big")));
                                jsonobjfinalnew.put("Cost_of_Scheduled_Cabs", jsonobjfinal.get("total_cost__regular_cab"));
                                jsonobjfinalnew.put("toll_shift_cab", toll_shift_cab);
                                jsonobjfinalnew.put("gps_regular_cab", gps_regular_cab);
                                jsonobjfinalnew.put("uptax_regular_cab", uptax_regular_cab);
                                jsonobjfinalnew.put("hrtax_regular_cab", hrtax_regular_cab);
                                jsonobjfinalnew.put("gstTax_regular_cab", gstTax_regular_cab);
                                int t1= Integer.parseInt(jsonobjfinal.get("total_cost__regular_cab").toString())+Integer.parseInt(toll_shift_cab)+Integer.parseInt(gps_regular_cab)+Integer.parseInt(uptax_regular_cab)+Integer.parseInt(hrtax_regular_cab)+Integer.parseInt(gstTax_regular_cab);
                                jsonobjfinalnew.put("Scheduled_Cab_Cost",t1);
                                jsonobjfinalnew.put("emp_contrib_regular", emp_contrib_regular);
                                jsonobjfinalnew.put("Scheduled_Cab_Cost_less_Emp_Contribution", t1-Integer.parseInt(emp_contrib_regular));
                                jsonobjfinalnew.put("Cost_of_Shift_Cabs", jsonobjfinal.get("total_cost_shift_cab"));
                                jsonobjfinalnew.put("toll_shift_cab", toll_shift_cab);
                                jsonobjfinalnew.put("gps_shift_cab", gps_shift_cab);
                                jsonobjfinalnew.put("uptax_shift_cab", uptax_shift_cab);
                                jsonobjfinalnew.put("hrtax_shift_cab", hrtax_shift_cab);
                                jsonobjfinalnew.put("gstTax_shift_cab", gstTax_shift_cab);
                                int t2=(Integer.parseInt(jsonobjfinal.get("total_cost_shift_cab").toString())+Integer.parseInt(toll_shift_cab)+Integer.parseInt(gps_shift_cab)+Integer.parseInt(uptax_shift_cab)+Integer.parseInt(hrtax_shift_cab)+Integer.parseInt(gstTax_shift_cab));
                                jsonobjfinalnew.put("Shift_Cab_Cost",t1);
                                jsonobjfinalnew.put("emp_contrib_shift", emp_contrib_shift);
                                jsonobjfinalnew.put("Shift_Cab_Cost_less_Emp_Contribution", t2-Integer.parseInt(emp_contrib_shift));
                                jsonobjfinalnew.put("Cost_of_Unscheduled_Cabs", jsonobjfinal.get("total_unscheduled_cost"));
                                jsonobjfinalnew.put("toll_unscheduled_cab", toll_unscheduled_cab);
                                jsonobjfinalnew.put("gstTax_unscheduled", gstTax_unscheduled);
                                jsonobjfinalnew.put("Unscheduled_Cab_Costs", Integer.parseInt(jsonobjfinalnew.get("Cost_of_Unscheduled_Cabs").toString())+Integer.parseInt(toll_unscheduled_cab)+Integer.parseInt(gstTax_unscheduled));
                                jsonobjfinalnew.put("Cost_of_Standby_Cab", standByCost);
                                jsonobjfinalnew.put("standByTax", standByTax);
                                jsonobjfinalnew.put("Extra_Mileage_Cost", extraMileageCost);
                                jsonobjfinalnew.put("Standby_Cab_Cost", Integer.parseInt(extraMileageCost)+Integer.parseInt(standByCost)+Integer.parseInt(standByTax));
                                jsonobjfinalnew.put("Cost_of_Cabs_for_Other_Purposes", otherCabCost);
                                jsonobjfinalnew.put("otherCabGST",otherCabGST);
                                jsonobjfinalnew.put("Cabs_for_Other_Purposes_Cost", Integer.parseInt(otherCabCost)+Integer.parseInt(otherCabGST));
                                jsonobjfinalnew.put("Total_Transortation_Cost_for_the_Month", t1+t2+(Integer.parseInt(jsonobjfinalnew.get("Unscheduled_Cab_Costs").toString()))+(Integer.parseInt(jsonobjfinalnew.get("Standby_Cab_Cost").toString()))+(Integer.parseInt(jsonobjfinalnew.get("Cabs_for_Other_Purposes_Cost").toString())));
                                jsonobjfinalnew.put("escortGuardCost", escortGuardCost);
                                jsonobjfinalnew.put("escortGuardTaxes", escortGuardTaxes);
                                jsonobjfinalnew.put("Total_Amount_Security_Guard", Integer.parseInt(escortGuardCost)+Integer.parseInt(escortGuardTaxes));
                                jsonobjfinalnew.put("escortGuardDropDutyCost", escortGuardDropDutyCost);   
                                jsonobjfinalnew.put("Escort_Security_Guards", Integer.parseInt(escortGuardDropDutyCost)+(Integer.parseInt(jsonobjfinalnew.get("Total_Amount_Security_Guard").toString())));
                                jsonobjfinalnew.put("tptMobCost", tptMobCost);
                                jsonobjfinalnew.put("Transport_plus_Escort Security ", Integer.parseInt(tptMobCost)+Integer.parseInt(otherCabGST)+(Integer.parseInt(jsonobjfinalnew.get("Total_Transortation_Cost_for_the_Month").toString()))+(Integer.parseInt(jsonobjfinalnew.get("Escort_Security_Guards").toString())));
                                jsonobjfinalnew.put("total_no_of_regular_employees", jsonobjfinal.get("total_no_of_regular_employees"));
                                jsonobjfinalnew.put("total_no_of_shift_employees", jsonobjfinal.get("total_no_of_shift_employees"));
                                jsonobjfinalnew.put("total_no_regular_cab_small", jsonobjfinal.get("total_no_regular_cab_small"));
                                jsonobjfinalnew.put("total_no_regular_cab_big", jsonobjfinal.get("total_no_regular_cab_big"));
                                jsonobjfinalnew.put("total_no_regular_cab", jsonobjfinal.get("total_no_regular_cab"));
                                jsonobjfinalnew.put("total_no_shift_cab_small", jsonobjfinal.get("total_no_shift_cab_small"));
                                jsonobjfinalnew.put("total_no_shift_cab_big", jsonobjfinal.get("total_no_shift_cab_big"));
                                jsonobjfinalnew.put("total_no_shift_cab", jsonobjfinal.get("total_no_shift_cab"));
                                jsonobjfinalnew.put("total_no_regular_and_shift_cab", +(Integer.parseInt(jsonobjfinal.get("total_no_regular_cab").toString()))+(Integer.parseInt(jsonobjfinal.get("total_no_shift_cab").toString())));
                                jsonobjfinalnew.put("total_no_of_unscheduled_cabs", jsonobjfinal.get("total_no_of_unscheduled_cabs"));
                                jsonobjfinalnew.put("vendor", jsonarray0);
                                jsonarray.put(jsonobjfinalnew);
                                
                                return jsonarray;

                                
                }
                                //transport billing report  ends
  
     
     
     
}
