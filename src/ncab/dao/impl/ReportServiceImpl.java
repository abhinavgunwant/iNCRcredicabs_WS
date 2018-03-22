package ncab.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.PreparedStatement;
import ncab.dao.DBConnectionRo;

public class ReportServiceImpl {

	//Getting information for the employee according to the ui filter
	//employee code starts
	public JSONArray employeeDAO() throws ClassNotFoundException, SQLException
	{
		DBConnectionRo dbconnection=new DBConnectionRo();
		Connection connection=dbconnection.getConnection();
		JSONArray jsonarray=new JSONArray();
		String query="Select b.Emp_Qlid AS Emp_Qlid,c.Emp_FName AS Emp_FName,c.Emp_Mgr_Qlid1 AS Emp_Mgr_Qlid1 ,b.Request_ID AS Request_ID,b.Start_Date_Time AS Start_Date_Time,a.Cab_Cost AS Cab_Cost,b.Shift_ID AS Shift_ID,a.Cab_No AS Cab_No from ncab_roster_tbl a,NCAB_UNSCHEDULE_RQST_TBL  b ,ncab_master_employee_tbl c where b.Shift_ID=a.Shift_Id AND b.Emp_Qlid=c.Emp_Qlid AND a.Emp_Qlid=b.Emp_Qlid;";
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement(query);
		ResultSet rs=ps.executeQuery();

		while(rs.next())
		{
			JSONObject jsonobj=new JSONObject();
			String Emp_Qlid=rs.getString("Emp_Qlid");
			String Emp_FName=rs.getString("Emp_FName");
			String Emp_Mgr_Qlid1=rs.getString("Emp_Mgr_Qlid1");
			String Request_ID=rs.getString("Request_ID");
			String Start_Date_Time=rs.getString("Start_Date_Time");
			String Cab_Cost=rs.getString("Cab_Cost");
			String Shift_ID=rs.getString("Shift_ID");
			String Cab_No=rs.getString("Cab_No");

			jsonobj.put("Emp_Qlid", Emp_Qlid);
			jsonobj.put("Emp_FName", Emp_FName);
			jsonobj.put("Emp_Mgr_Qlid1", Emp_Mgr_Qlid1);
			jsonobj.put("Request_ID", Request_ID);
			jsonobj.put("Start_Date_Time", Start_Date_Time);
			jsonobj.put("Cab_Cost", Cab_Cost);
			jsonobj.put("Shift_ID", Shift_ID);
			jsonobj.put("Cab_No", Cab_No);

			jsonarray.put(jsonobj);
		}
		return jsonarray;

	}
	//employee code ends


	//manager code
	public JSONArray getManagerReport() throws SQLException, ClassNotFoundException {
		Connection connection=null;
		DBConnectionRo dbconnection=new DBConnectionRo();
		JSONArray jsonarray=new JSONArray();
		connection=(Connection) dbconnection.getConnection();						
		PreparedStatement ps2=(PreparedStatement) connection.prepareStatement(" SELECT Manager,Emp_FName AS ManagerName,NE,NC,Cost FROM view5,ncab_master_employee_tbl\r\n" + 
				" WHERE Emp_Qlid=Manager;");
		ResultSet rs2=ps2.executeQuery();

		while (rs2.next())	
		{
			JSONObject jsonresponse = new JSONObject();
			String Manager_Name=rs2.getString("ManagerName");
			String No_Of_Employees=rs2.getString("NE");
			String No_Of_Cabs=rs2.getString("NC");
			String Total_Cost=rs2.getString("Cost");

			jsonresponse.put("Manager_Name", Manager_Name);
			jsonresponse.put("No_Of_Employees", No_Of_Employees);
			jsonresponse.put("No_Of_Cabs",No_Of_Cabs);
			jsonresponse.put("Total_Cost",Total_Cost);

			jsonarray.put(jsonresponse);

		}
		return jsonarray; 

	}

	public JSONArray getRequest1() throws SQLException, ClassNotFoundException {
		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();
		JSONArray jsonarray=new JSONArray();
		jsonarray = reportserviceimpl.getManagerReport();
		return jsonarray;
	}
	//manager code ends


	//vendor code starts
	public JSONArray getVendorReport() throws SQLException, ClassNotFoundException {
		Connection connection=null;
		DBConnectionRo dbconnection=new DBConnectionRo();
		JSONArray jsonarray=new JSONArray();
		connection=dbconnection.getConnection();	
		PreparedStatement ps= (PreparedStatement) connection.prepareStatement("SELECT V.vendor_name,R.Cab_Cost,N.Rqst_Date_Time,R.Cab_No,N.Emp_Qlid,N.Request_ID FROM  NCAB_UNSCHEDULE_RQST_TBL N INNER JOIN ncab_roster_tbl R ON N.Emp_Qlid=R.Emp_Qlid INNER JOIN ncab_vendor_master_tbl V ON R.vendor_id=V.vendor_id;");
		ResultSet rs=ps.executeQuery();
		while (rs.next())
		{
			JSONObject jsonresponse = new JSONObject();
			String vendor_name =rs.getString(1);
			String Cab_Cost=rs.getString(2);
			String Rqst_Date_Time=rs.getString(3);
			String Cab_No=rs.getString(4);
			String Emp_Qlid=rs.getString(5);
			String Request_ID=rs.getString(6);

			jsonresponse.put("vendor_name",vendor_name);
			jsonresponse.put("Cab_Cost",Cab_Cost );
			jsonresponse.put("Rqst_Date_Time",Rqst_Date_Time);
			jsonresponse.put("Cab_No",Cab_No);
			jsonresponse.put("Emp_Qlid",Emp_Qlid);
			jsonresponse.put("Request_ID",Request_ID);

			jsonarray.put(jsonresponse);

		}
		return jsonarray; 

	}


	public JSONArray getRequest2()throws SQLException,  ClassNotFoundException
	{

		ReportServiceImpl reportserviceimpl=new ReportServiceImpl();		
		JSONArray jsonarray=new JSONArray();

		jsonarray = reportserviceimpl.getVendorReport();
		return jsonarray;
	}

	//vendor code ends

	//Unscheduled summary report by manager starts

	public JSONArray managerSummary() throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		Connection connection =dbconnectionro.getConnection();
		PreparedStatement ps=(PreparedStatement)connection.prepareStatement("SELECT Manager,Emp_FName AS ManagerName,NE,NC,Cost FROM view5,ncab_master_employee_tbl WHERE Emp_Qlid=Manager;");
		ResultSet rs=ps.executeQuery();

		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Manager", rs.getString(1));
			jsonresponse.put("ManagerName", rs.getString(2));
			jsonresponse.put("NE", rs.getString(3));
			jsonresponse.put("NC", rs.getString(4));
			jsonresponse.put("Cost",rs.getString(5));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}

	//Unscheduled summary report by manager ends

	//Unscheduled detailed report by vendor starts
	public JSONArray vendorDetailed() throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		Connection connection=dbconnectionro.getConnection();
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT V.vendor_name,R.Cab_Cost,N.Rqst_Date_Time,R.Cab_No,N.Emp_Qlid,N.Request_ID FROM  NCAB_UNSCHEDULE_RQST_TBL N INNER JOIN ncab_roster_tbl R ON N.Emp_Qlid=R.Emp_Qlid INNER JOIN ncab_vendor_master_tbl V ON R.vendor_id=V.vendor_id;");	
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("vendoe_name", rs.getString(1));
			jsonresponse.put("Cab_Cost", rs.getString(2));
			jsonresponse.put("Rqst_Date_Time", rs.getString(3));
			jsonresponse.put("Cab_No", rs.getString(4));
			jsonresponse.put("Emp_Qlid", rs.getString(5));
			jsonresponse.put("Request_ID", rs.getString(6));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}

	//Unscheduled detailed report by vendor ends


	//Unscheduled summary report by vendor starts

	public JSONArray vendorSummary() throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		Connection connection=dbconnectionro.getConnection();
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT a.Vendor_id,b.vendor_name,SUM(a.Cab_Cost) AS Amount,COUNT(DISTINCT(a.Cab_No)) AS Cabs FROM ncab_roster_tbl AS a, ncab_vendor_master_tbl AS b WHERE a.Shift_Id=4 AND a.Vendor_Id=b.vendor_id GROUP BY a.Vendor_id;\n" + 
				"");
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Vendor_id", rs.getString(1));
			jsonresponse.put("vendor_name", rs.getString(2));
			jsonresponse.put("Amount", rs.getString(3));
			jsonresponse.put("Cabs", rs.getString(4));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}
	//Unscheduled summary report by vendor ends


	//Unscheduled summary report by employee starts
	public JSONArray employeeSummary() throws SQLException
	{
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		JSONArray jsonarray=new JSONArray();
		Connection connection=dbconnectionro.getConnection();
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT a.Emp_Qlid,a.Emp_FName,a.Emp_Mgr_Qlid1,COUNT(b.Cab_No) AS Cab_Count,SUM(b.Cab_Cost) AS Cab_Cost  FROM ncab_master_employee_tbl AS a , ncab_roster_tbl AS b WHERE b.Shift_Id=4 AND a.Emp_Qlid=b.Emp_Qlid GROUP BY b.Emp_Qlid;");
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Emp_Qlid", rs.getString(1));
			jsonresponse.put("Emp_FName", rs.getString(2));
			jsonresponse.put("Emp_Mgr_Qlid1", rs.getString(3));
			jsonresponse.put("Cab_Count", rs.getString(4));
			jsonresponse.put("Cab_Cost", rs.getString(5));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}
	//Unscheduled summary report by employee ends

	//Unscheduled detailed report by employee starts
	public JSONArray employeeDetailed() throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnection=new DBConnectionRo();
		Connection connection=dbconnection.getConnection();
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement("Select b.Emp_Qlid AS Emp_Qlid,c.Emp_FName AS Emp_FName,c.Emp_Mgr_Qlid1 AS Emp_Mgr_Qlid1 ,b.Start_Date_Time AS Start_Date_Time,a.Cab_Cost AS Cab_Cost,b.Shift_ID AS Shift_ID,a.Cab_No AS Cab_No from ncab_roster_tbl a,NCAB_UNSCHEDULE_RQST_TBL  b ,ncab_master_employee_tbl c where b.Shift_ID=a.Shift_Id AND b.Emp_Qlid=c.Emp_Qlid AND a.Emp_Qlid=b.Emp_Qlid;");
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Emp_Qlid", rs.getString(1));
			jsonresponse.put("Emp_FName", rs.getString(2));
			jsonresponse.put("Emp_Mgr_Qlid1", rs.getString(3));
			jsonresponse.put("Start_Date_Time", rs.getString(4));
			jsonresponse.put("Cab_Cost", rs.getString(5));
			jsonresponse.put("Shift_ID", rs.getString(6));
			jsonresponse.put("Cab_No", rs.getString(7));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}
	//Unscheduled detailed report by employee ends


	//filter reports starts
	public JSONArray employeeDetailed(JSONObject jsonrequest) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		PreparedStatement ps=null;
		if(jsonrequest.getString("managername").equals(null))
		{
			//null
			ps=(PreparedStatement)connection.prepareStatement("");	
		}
		else
		{
			//manager name
			ps=(PreparedStatement)connection.prepareStatement("");
		}
		ResultSet rs=ps.executeQuery();

		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Emp_Qlid", rs.getString(1));
			jsonresponse.put("Emp_FName", rs.getString(2));
			jsonresponse.put("Emp_Mgr_Qlid1", rs.getString(3));
			jsonresponse.put("Start_Date_Time", rs.getString(4));
			jsonresponse.put("Cab_Cost", rs.getString(5));
			jsonresponse.put("Shift_ID", rs.getString(6));
			jsonresponse.put("Cab_No", rs.getString(7));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}


	public JSONArray employeeSummary(JSONObject jsonrequest) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		PreparedStatement ps=null;
		if(jsonrequest.getString("managername").equals(null))
		{
			//null
			ps=(PreparedStatement)connection.prepareStatement("");	
		}
		else
		{
			//manager name
			ps=(PreparedStatement)connection.prepareStatement("");
		}
		ResultSet rs=ps.executeQuery();

		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Emp_Qlid", rs.getString(1));
			jsonresponse.put("Emp_FName", rs.getString(2));
			jsonresponse.put("Emp_Mgr_Qlid1", rs.getString(3));
			jsonresponse.put("Cab_Count", rs.getString(4));
			jsonresponse.put("Cab_Cost", rs.getString(5));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}


	public JSONArray vendorSummary(JSONObject jsonrequest) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		PreparedStatement ps=null;
		if(jsonrequest.getString("vendorname").equals(null))
		{
			//null
			ps=(PreparedStatement)connection.prepareStatement("");	
		}
		else
		{
			//vendor name
			ps=(PreparedStatement)connection.prepareStatement("");
		}
		ResultSet rs=ps.executeQuery();

		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Vendor_id", rs.getString(1));
			jsonresponse.put("vendor_name", rs.getString(2));
			jsonresponse.put("Amount", rs.getString(3));
			jsonresponse.put("Cabs", rs.getString(4));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}


	public JSONArray vendorDetailed(JSONObject jsonrequest) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		PreparedStatement ps=null;
		if(jsonrequest.getString("vendorname").equals(null))
		{
			//null
			ps=(PreparedStatement)connection.prepareStatement("");	
		}
		else
		{
			//vendor name
			ps=(PreparedStatement)connection.prepareStatement("");
		}
		ResultSet rs=ps.executeQuery();

		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("vendoe_name", rs.getString(1));
			jsonresponse.put("Cab_Cost", rs.getString(2));
			jsonresponse.put("Rqst_Date_Time", rs.getString(3));
			jsonresponse.put("Cab_No", rs.getString(4));
			jsonresponse.put("Emp_Qlid", rs.getString(5));
			jsonresponse.put("Request_ID", rs.getString(6));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}




	public JSONArray managerSummary(JSONObject jsonrequest) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		PreparedStatement ps=null;
		if(jsonrequest.getString("managername").equals(null))
		{
			//null
			ps=(PreparedStatement)connection.prepareStatement("");	
		}
		else
		{
			//manager name
			ps=(PreparedStatement)connection.prepareStatement("");
		}
		ResultSet rs=ps.executeQuery();

		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Manager", rs.getString(1));
			jsonresponse.put("ManagerName", rs.getString(2));
			jsonresponse.put("NE", rs.getString(3));
			jsonresponse.put("NC", rs.getString(4));
			jsonresponse.put("Cost",rs.getString(5));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}
	//filter reports ends

	//transport billing report start
	public JSONArray gettransportbilling(/*String start_date,String end_date*/ ) throws ClassNotFoundException, SQLException
	{ 
		int total_no_regular_cab_small=0,total_no_regular_cab_big = 0;
		DBConnectionRo dbconnection=new DBConnectionRo();
		Connection connection=dbconnection.getConnection();
		JSONArray jsonarray=new JSONArray();
		JSONObject jsonobjfinal=new JSONObject();
		//vendor cost


		JSONArray jsonarray0=new JSONArray();
		connection=dbconnection.getConnection();        
		PreparedStatement ps= (PreparedStatement) connection.prepareStatement("SELECT vendor_id,Roster_Month,Roster_Year,vendor_name,SUM(cab_cost) AS vendor_total_cost FROM A GROUP BY vendor_id,Roster_Month,Roster_Year ");
		ResultSet rs=ps.executeQuery();
		while (rs.next())
		{if(rs.getString(2).equalsIgnoreCase("3") && rs.getString(3).equals("2018")) {
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("vendor_id",rs.getString(1));
			jsonresponse.put("Roster_Month",rs.getString(2));
			jsonresponse.put("Roster_Year",rs.getString(3));
			jsonresponse.put("vendor_name",rs.getString(4));
			jsonresponse.put("vendor_total_cost",rs.getString(5));
			jsonarray0.put(jsonresponse);}
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
		//String unscheduled_cost=rs1.getString("cost");
		if(Roster_Month.equals("3") && Roster_Year.equals("2018"))
		{
			jsonobjfinal.put("total_no_of_unscheduled_employees",unscheduled_Total_Employees_No);
			jsonobjfinal.put("total_no_of_unscheduled_cabs",unscheduled_no_of_cabs);
			//jsonobjfinal.put("total_unscheduled_cost",unscheduled_cost);
		}

		}


		//         String query5="SELECT SUM(Cab_Cost) AS total_cost_of_Unscheduled_cabs FROM sum_of_Unscheduled_Cabs ;";
		//         PreparedStatement ps5=(PreparedStatement) connection.prepareStatement(query5);
		//         ResultSet rs5=ps5.executeQuery();
		//         
		//         while(rs5.next())
		//         {int i=0;
		//               JSONObject jsonobj=new JSONObject();
		//         
		//                      String Roster_Month=rs1.getString("Roster_Month");
		//                      String Roster_Year=rs1.getString("Roster_Year");
		//                      String unscheduled_cost=rs1.getString("total_cost_of_Unscheduled_cabs");
		//                      if(Roster_Month.equals("3") && Roster_Year.equals("2018"))
		//                      {
		//                             jsonobjfinal.put("total_unscheduled_cost",unscheduled_cost);
		//                      }
		//                      
		//                      
		//                      
		//
		//         }




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
		{      System.out.println("in");

		jsonobjfinal.put("total_no_regular_cab_small",Total_Cabs);
		//jsonobjfinal.put("total_cost_regular_cab_small",Total_Cost);
		jsonobjfinal.put("total_no_of_regular_employees_small",Total_Employees);


		}
		if( cab_type.equalsIgnoreCase("Big"))
		{      System.out.println("in big regular");  
		//     int sumcab=0 , sumcost=0;
		//                             sumcab=sumcab+Integer.parseInt(Total_Cabs);
		//                             sumcost=sumcost+Integer.parseInt(Total_Cost);
		//                             total_no_regular_cab_big=sumcab;
		jsonobjfinal.put("total_no_regular_cab_big",Total_Cabs);
		//jsonobjfinal.put("total_cost_regular_cab_small",Total_Cost);
		jsonobjfinal.put("total_no_of_regular_employees_big",Total_Employees);


		}


		}






		//jsonresponse.put(""+i++,jsonobj);
		}
		//     jsonarray.put(jsonobjfinal);

		//         
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
			{      System.out.println("in");
			sumcosts=sumcosts+Integer.parseInt(Total_Cost);
			//jsonobjfinal.put("total_no_regular_cab_small",sumcosts);
			jsonobjfinal.put("total_cost_regular_cab_small",""+sumcosts);
			//jsonobjfinal.put("total_no_of_regular_employees_small",Total_Employees);


			}
			if( cab_type.equalsIgnoreCase("Big"))
			{      System.out.println("in big regular");  
			//     

			sumcostb=sumcostb+Integer.parseInt(Total_Cost);

			//jsonobjfinal.put("total_no_regular_cab_big",Total_Cabs);
			jsonobjfinal.put("total_cost_regular_cab_big",""+sumcostb);
			//       jsonobjfinal.put("total_no_of_regular_employees_big",Total_Employees);


			}
			//                                
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
				{      System.out.println("in");

				sumcabs=sumcabs+Integer.parseInt(Total_Cabs);


				jsonobjfinal.put("total_no_shift_cab_small",""+sumcabs);
				//jsonobjfinal.put("total_cost_regular_cab_small",Total_Cost);
				jsonobjfinal.put("total_no_of_shift_employees_small",Total_Employees);


				}
				if( cab_type.equalsIgnoreCase("Big"))
				{      System.out.println("in big shift");    

				sumcab=sumcab+Integer.parseInt(Total_Cabs);
				//     int sumcab=0 , sumcost=0;
				//                                           sumcab=sumcab+Integer.parseInt(Total_Cabs);
				//                                           sumcost=sumcost+Integer.parseInt(Total_Cost);
				//                                           total_no_regular_cab_big=sumcab;
				jsonobjfinal.put("total_no_shift_cab_big",""+sumcab);
				//jsonobjfinal.put("total_cost_regular_cab_small",Total_Cost);
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
			{      System.out.println("in");
			sumcostss=sumcostss+Integer.parseInt(Total_Cost);
			//jsonobjfinal.put("total_no_regular_cab_small",sumcosts);
			jsonobjfinal.put("total_cost_shift_cab_small",""+sumcostss);
			//jsonobjfinal.put("total_no_of_regular_employees_small",Total_Employees);


			}
			if( cab_type.equalsIgnoreCase("Big"))
			{      System.out.println("in big shift");     
			//     

			sumcostbs=sumcostbs+Integer.parseInt(Total_Cost);

			//jsonobjfinal.put("total_no_regular_cab_big",Total_Cabs);
			jsonobjfinal.put("total_cost_shift_cab_big",""+sumcostbs);
			//       jsonobjfinal.put("total_no_of_regular_employees_big",Total_Employees);


			}
			//                                
		}}































		jsonobjfinal.put("total_no_regular_cab",Integer.parseInt((String) jsonobjfinal.get("total_no_regular_cab_big"))+Integer.parseInt((String) jsonobjfinal.get("total_no_regular_cab_small")));
		jsonobjfinal.put("total_no_of_regular_employees",Integer.parseInt((String) jsonobjfinal.get("total_no_of_regular_employees_small"))+Integer.parseInt((String) jsonobjfinal.get("total_no_of_regular_employees_big")));
		//jsonobjfinal.remove("total_no_of_regular_employees_small");
		//jsonobjfinal.remove("total_no_of_regular_employees_big");
		jsonobjfinal.put("total_cost__regular_cab",Integer.parseInt((String) jsonobjfinal.get("total_cost_regular_cab_small"))+Integer.parseInt((String) jsonobjfinal.get("total_cost_regular_cab_big")));
		jsonobjfinal.put("total_no_shift_cab",Integer.parseInt((String) jsonobjfinal.get("total_no_shift_cab_small"))+Integer.parseInt((String) jsonobjfinal.get("total_no_shift_cab_big")));
		jsonobjfinal.put("total_no_of_shift_employees",Integer.parseInt((String) jsonobjfinal.get("total_no_of_shift_employees_small"))+Integer.parseInt((String) jsonobjfinal.get("total_no_of_shift_employees_big")));
		//jsonobjfinal.put("total_no_re_shift_cab",Integer.parseInt((String) jsonobjfinal.get("total_no_regular_cab_small"))+Integer.parseInt((String) jsonobjfinal.get("total_no_regular_cab_small"))+Integer.parseInt((String) jsonobjfinal.get("total_no_shift_cab_small"))+Integer.parseInt((String) jsonobjfinal.get("total_no_shift_cab_small")));
		jsonobjfinal.put("total_cost_shift_cab",Integer.parseInt((String) jsonobjfinal.get("total_cost_shift_cab_small"))+Integer.parseInt((String) jsonobjfinal.get("total_cost_shift_cab_big")));
		//jsonobjfinal.remove("total_no_of_regular_employees_small");
		//jsonobjfinal.remove("total_no_of_regular_employees_big");
		jsonarray.put(jsonobjfinal);
		jsonarray.put(jsonarray0);
		return jsonarray;

		//         Integer.parseInt((String) jsonobjfinal.get("total_no_regular_cab_small"))+

	}
	//transport billing report  ends



	public JSONArray getVendorwisebillReport(String shift_id,String vendor_name, String FromDate, String ToDate) throws SQLException, ClassNotFoundException {

		Connection connection=null;
		DBConnectionRo dbconnection=new DBConnectionRo();
		JSONArray jsonarray=new JSONArray();
		connection=dbconnection.getConnection();       
		System.out.println("Connected");
		PreparedStatement ps3= (PreparedStatement) connection.prepareStatement("SELECT CONCAT(B.start_date,' - ',B.end_date) AS Period,B.Cab_no, CONCAT(D.start_time,' - ',D.end_time) AS Shift_Time,emp_pickup_area AS Source,Model,Cab_Cost AS Rate FROM ncab_cab_master_tbl A,ncab_roster_tbl B,ncab_master_employee_tbl C,NCAB_SHIFT_MASTER_TBL D,ncab_vendor_master_tbl E WHERE (D.shift_id = ? && E.vendor_name = ? && MONTH(start_date) BETWEEN ? AND ?) AND (A.cab_license_plate_no = B.cab_no && C.emp_qlid = B.emp_qlid && D.shift_id = B.shift_id) GROUP BY (B.cab_no) ");
		ps3.setString(1,shift_id);
		ps3.setString(2,vendor_name);
		ps3.setString(3,FromDate);
		ps3.setString(4,ToDate);

		int i=0;
		ResultSet rs3=ps3.executeQuery();
		while (rs3.next())
		{
			JSONObject jsonresponse = new JSONObject();

			String Period =rs3.getString(1);
			String Cab_no=rs3.getString(2);
			String Shift_Time=rs3.getString(3);
			String Source=rs3.getString(4);
			String Model=rs3.getString(5);
			String Rate=rs3.getString(6);
			System.out.println(Model);

			jsonresponse.put("Period",Period);
			jsonresponse.put("Cab_No",Cab_no);
			jsonresponse.put("Shift_Time",Shift_Time);
			jsonresponse.put("Source",Source );
			jsonresponse.put("Model",Model);
			jsonresponse.put("Rate",Rate);

			jsonarray.put(jsonresponse);      

		}

		return jsonarray; 

	}

	public JSONArray getVendorNames(){
		DBConnectionRo dbconnection = new DBConnectionRo();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarray = new JSONArray();
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) connection.prepareStatement("SELECT vendor_name FROM ncab_vendor_master_tbl");
			ResultSet rs = ps.executeQuery();
			String name="";


			while(rs.next()){
				JSONObject jsonresponse = new JSONObject();

				name=rs.getString(1);

				jsonresponse.put("name", name );
				jsonarray.put(jsonresponse);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return  jsonarray;
	}



}
