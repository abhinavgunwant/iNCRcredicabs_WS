package ncab.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.PreparedStatement;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

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


	// MANAGER IMPL'S
	// working
	public JSONArray managerSummary() throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		Connection connection =dbconnectionro.getConnection();
		PreparedStatement ps=(PreparedStatement)connection.prepareStatement("SELECT Manager,Emp_FName AS ManagerName,NE,NC,Cost FROM view5,ncab_master_employee_tbl\n" + 
				"WHERE Emp_Qlid=Manager;\n" + 
				";");
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

	// working
	public JSONArray managerSummary(JSONObject jsonrequest) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		String toDate=jsonrequest.getString("toDate");
		String fromDate=jsonrequest.getString("fromDate");
		PreparedStatement ps=null;
		ps=(PreparedStatement)connection.prepareStatement("SELECT Manager,Emp_FName AS ManagerName,NE,NC,Cost FROM view5,ncab_master_employee_tbl\n" + 
				"WHERE Emp_Qlid=Manager AND Start_Date BETWEEN ? AND ? ;");
		ps.setString(1, fromDate);
		ps.setString(2, toDate);
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

	// working          
	public JSONArray mangerDetailed(JSONObject jsonreq) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		Connection connection=dbconnectionro.getConnection();
		String toDate=jsonreq.getString("toDate");
		String fromDate=jsonreq.getString("fromDate");
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT a.Employee_Name AS Emp_Name,a.Emp_Qlid AS Emp_Qlid, a.Manager_id AS Manager_Id,CONCAT(b.Emp_FName,' ',b.Emp_LName) AS Manager_Name,\n" + 
				"a.StartDate AS Start_Date,a.Vendor_Name AS Vendor_Name,a.trip_type AS Trip_Type,a.Source AS Source,a.Destination AS Destination,\n" + 
				"a.Emp_Zone AS Emp_Zone,a.Cab_Cost AS Cab_Cost\n" + 
				"FROM ManagerDetailView AS a,ncab_master_employee_tbl AS b\n" + 
				"WHERE a.Manager_id = b.Emp_Qlid AND a.StartDate BETWEEN ? AND ?;");
		ps.setString(1, fromDate);
		ps.setString(2, toDate);
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Emp_Name", rs.getString(1));
			jsonresponse.put("Emp_Qlid", rs.getString(2));
			jsonresponse.put("Manager_Id", rs.getString(3));
			jsonresponse.put("Manager_Name", rs.getString(4));
			jsonresponse.put("Start_Date", rs.getString(5));
			jsonresponse.put("Vendor_Name", rs.getString(6));
			jsonresponse.put("Trip_Type", rs.getString(7));
			jsonresponse.put("Source", rs.getString(8));
			jsonresponse.put("Destination", rs.getString(9));
			jsonresponse.put("Emp_Zone", rs.getString(10));
			jsonresponse.put("Cab_Cost", rs.getString(11));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;

	}

	// working
	public JSONArray managerDetailed() throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		Connection connection=dbconnectionro.getConnection();
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT a.Employee_Name AS Emp_Name ,a.Emp_Qlid AS Emp_Qlid, a.Manager_id AS Manager_Id,CONCAT(b.Emp_FName,' ',b.Emp_LName) AS Manager_Name,a.StartDate AS Start_Date,\n" + 
				"a.Vendor_Name AS Vendor_Name,a.trip_type AS Trip_Type,\n" + 
				"a.Source AS Source,a.Destination AS Destination,a.Emp_Zone AS Emp_Zone,a.Cab_Cost AS Cab_Cost\n" + 
				"FROM ManagerDetailView AS a,ncab_master_employee_tbl AS b\n" + 
				"WHERE a.Manager_id = b.Emp_Qlid;");
		ResultSet rs=ps.executeQuery();

		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Emp_Name", rs.getString(1));
			jsonresponse.put("Emp_Qlid",rs.getString(2));
			jsonresponse.put("Manager_Id",rs.getString(3));
			jsonresponse.put("Manager_Name",rs.getString(4));
			jsonresponse.put("Start_Date",rs.getString(5));
			jsonresponse.put("Vendor_Name",rs.getString(6));
			jsonresponse.put("Trip_Type",rs.getString(7));
			jsonresponse.put("Source",rs.getString(8));
			jsonresponse.put("Destination",rs.getString(9));
			jsonresponse.put("Emp_Zone",rs.getString(10));
			jsonresponse.put("Cab_Cost",rs.getString(11));


			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}

	// VENDOR IMPL'S
	// working
	public JSONArray vendorDetailed() throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		Connection connection=dbconnectionro.getConnection();
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT R.vendor_name AS Vendor_Name,N.Emp_Qlid AS Emp_Qlid,S.Emp_FName AS Emp_FName,R.Cab_Cost AS Cab_Cost,\n" + 
				"R.Start_Date AS DATE,R.Cab_No AS Cab_No,N.Source AS Source,N.Destination AS Destination,N.Other_Addr AS Other_Addr,\n" + 
				"S.Emp_Zone AS Emp_Zone  FROM  NCAB_UNSCHEDULE_RQST_TBL N INNER JOIN ncab_roster_tbl R,\n" + 
				"ncab_master_employee_tbl AS S WHERE N.Emp_Qlid=R.Emp_Qlid  AND N.Emp_Qlid=S.Emp_Qlid;"); 
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Vendor_Name", rs.getString(1));
			jsonresponse.put("Emp_Qlid", rs.getString(2));
			jsonresponse.put("Emp_FName", rs.getString(3));
			jsonresponse.put("Cab_Cost", rs.getString(4));
			jsonresponse.put("Date", rs.getString(5));
			jsonresponse.put("Cab_No", rs.getString(6));
			jsonresponse.put("Source", rs.getString(7));
			jsonresponse.put("Destination", rs.getString(8));
			jsonresponse.put("Other_Addr", rs.getString(9));
			jsonresponse.put("Emp_Zone", rs.getString(10));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}

/*	// working
	public JSONArray vendorDetailed(JSONObject jsonrequest) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		String toDate=jsonrequest.getString("toDate");
		String fromDate=jsonrequest.getString("fromDate");
		PreparedStatement ps=null;
		ps=(PreparedStatement)connection.prepareStatement("SELECT R.vendor_name AS Vendor_Name,N.Emp_Qlid AS Emp_Qlid,S.Emp_FName AS Emp_FName,R.Cab_Cost AS Cab_Cost,R.Start_Date AS Start_Date,\n" + 
				"R.Cab_No AS Cab_No,N.Source AS Source,N.Destination AS Destination,N.Other_Addr AS Other_Addr,S.Emp_Zone AS Emp_Zone \n" + 
				"FROM  NCAB_UNSCHEDULE_RQST_TBL N INNER JOIN ncab_roster_tbl R,ncab_master_employee_tbl AS S \n" + 
				"WHERE N.Emp_Qlid=R.Emp_Qlid  AND N.Emp_Qlid=S.Emp_Qlid\n" + 
				"AND R.Start_Date BETWEEN ? AND ?;");  
		ps.setString(1, fromDate);
		ps.setString(2, toDate);
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Vendor_Name", rs.getString(1));
			jsonresponse.put("Emp_Qlid", rs.getString(2));
			jsonresponse.put("Emp_FName", rs.getString(3));
			jsonresponse.put("Cab_Cost", rs.getString(4));
			jsonresponse.put("Start_Date", rs.getString(5));
			jsonresponse.put("Cab_No", rs.getString(6));
			jsonresponse.put("Source", rs.getString(7));
			jsonresponse.put("Destination", rs.getString(8));
			jsonresponse.put("Other_Addr", rs.getString(9));
			jsonresponse.put("Emp_Zone", rs.getString(10));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}
*/

	public JSONArray vendorDetailed(JSONObject jsonrequest) throws SQLException
    {
           JSONArray jsonarray=new JSONArray();
           Connection connection=new DBConnectionRo().getConnection();
           String toDate=jsonrequest.getString("toDate");
           String fromDate=jsonrequest.getString("fromDate");
           PreparedStatement ps=null;
           ps=(PreparedStatement)connection.prepareStatement("SELECT R.vendor_name AS Vendor_Name,N.Emp_Qlid AS Emp_Qlid,S.Emp_FName AS Emp_FName,R.Cab_Cost AS Cab_Cost,R.Start_Date AS Date,\n" + 
                        "R.Cab_No AS Cab_No,N.Source AS Source,N.Destination AS Destination,N.Other_Addr AS Other_Addr,S.Emp_Zone AS Emp_Zone \n" + 
                        "FROM  NCAB_UNSCHEDULE_RQST_TBL N INNER JOIN ncab_roster_tbl R,ncab_master_employee_tbl AS S \n" + 
                        "WHERE N.Emp_Qlid=R.Emp_Qlid  AND N.Emp_Qlid=S.Emp_Qlid\n" + 
                        "AND R.Start_Date BETWEEN ? AND ?;");  
           ps.setString(1, fromDate);
           ps.setString(2, toDate);
           ResultSet rs=ps.executeQuery();
           while(rs.next())
           {
                 JSONObject jsonresponse=new JSONObject();
                 jsonresponse.put("Vendor_Name", rs.getString(1));
                 jsonresponse.put("Emp_Qlid", rs.getString(2));
                 jsonresponse.put("Emp_FName", rs.getString(3));
                 jsonresponse.put("Cab_Cost", rs.getString(4));
                 jsonresponse.put("Date", rs.getString(5));
                 jsonresponse.put("Cab_No", rs.getString(6));
                 jsonresponse.put("Source", rs.getString(7));
                 jsonresponse.put("Destination", rs.getString(8));
                 jsonresponse.put("Other_Addr", rs.getString(9));
                 jsonresponse.put("Emp_Zone", rs.getString(10));
                 
                 jsonarray.put(jsonresponse);
           }
           return jsonarray;
    }

	// working
	public JSONArray vendorSummary() throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		Connection connection=dbconnectionro.getConnection();
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT a.vendor_name AS Vendor_Name,SUM(a.Cab_Cost) AS Cab_Cost,COUNT(DISTINCT(a.Cab_No)) AS Cab_Count \n" + 
				"FROM ncab_roster_tbl AS a WHERE a.Shift_Id=4  GROUP BY a.Vendor_name;");
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			//jsonresponse.put("Vendor_id", rs.getString(1));
			jsonresponse.put("Vendor_Name", rs.getString(1));
			jsonresponse.put("Cab_Cost", rs.getString(2));
			jsonresponse.put("Cabs_Count", rs.getString(3));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}

/*	//working
	public JSONArray vendorSummary(JSONObject jsonrequest) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		String fromDate=jsonrequest.getString("fromDate");
		String toDate=jsonrequest.getString("toDate");
		PreparedStatement ps=null;

		ps=(PreparedStatement)connection.prepareStatement("Select a.vendor_name As Vendor_Name,Sum(a.Cab_Cost) As Cab_Cost,Count(distinct(a.Cab_No)) As Cab_Count \n" + 
				"from ncab_roster_tbl AS a\n" + 
				"Where a.Shift_Id=4 AND a.Start_Date BETWEEN ? AND ?\n" + 
				"GROUP BY a.Vendor_name\n" + 
				";");  
		ps.setString(1, fromDate);
		ps.setString(2, toDate);
		ResultSet rs=ps.executeQuery();

		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Vendor_Name", rs.getString(1));
			jsonresponse.put("Cab_Cost", rs.getString(2));
			jsonresponse.put("Cab_Count", rs.getString(3));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}
*/

	public JSONArray vendorSummary(JSONObject jsonrequest) throws SQLException
    {
           JSONArray jsonarray=new JSONArray();
           Connection connection=new DBConnectionRo().getConnection();
           String fromDate=jsonrequest.getString("fromDate");
           String toDate=jsonrequest.getString("toDate");
           PreparedStatement ps=null;
           
                 ps=(PreparedStatement)connection.prepareStatement("Select a.vendor_name As Vendor_Name,Sum(a.Cab_Cost) As Cab_Cost,Count(distinct(a.Cab_No)) As Cabs_Count \n" + 
                               "from ncab_roster_tbl AS a\n" + 
                               "Where a.Shift_Id=4 AND a.Start_Date BETWEEN ? AND ?\n" + 
                               "GROUP BY a.Vendor_name\n" + 
                               ";");  
                 ps.setString(1, fromDate);
                 ps.setString(2, toDate);
           ResultSet rs=ps.executeQuery();
           
           while(rs.next())
           {
                 JSONObject jsonresponse=new JSONObject();
                 jsonresponse.put("Vendor_Name", rs.getString(1));
                 jsonresponse.put("Cab_Cost", rs.getString(2));
                 jsonresponse.put("Cabs_Count", rs.getString(3));
                 
                 jsonarray.put(jsonresponse);
           }
           return jsonarray;
    }

	
	// EMPLOYEE IMPL'S  
	// working
	public JSONArray employeeSummary() throws SQLException
	{
		DBConnectionRo dbconnectionro=new DBConnectionRo();
		JSONArray jsonarray=new JSONArray();
		Connection connection=dbconnectionro.getConnection();
		PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT a.Emp_Qlid,a.Emp_FName,a.Emp_Mgr_Qlid1,COUNT(b.Cab_No) AS Cab_Count,SUM(b.Cab_Cost) AS Cab_Cost \n" + 
				" FROM ncab_master_employee_tbl AS a , ncab_roster_tbl AS b WHERE b.Shift_Id=4 AND a.Emp_Qlid=b.Emp_Qlid GROUP BY b.Emp_Qlid;");
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

/*	// working
	public JSONArray employeeDetailed() throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		DBConnectionRo dbconnection=new DBConnectionRo();
		Connection connection=dbconnection.getConnection();
        PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT b.Emp_Qlid AS Emp_Qlid,a.vendor_name AS Vendor_Name,c.Emp_FName AS Emp_FName,c.Emp_Mgr_Qlid1 AS Emp_Mgr_Qlid1 ,\n" + 
                "a.Start_Date AS Start_Date_Time,a.Cab_Cost AS Cab_Cost,a.Cab_No AS Cab_No,d.model AS Cab_Type,\n" + 
                "b.Source,b.Destination,b.Other_addr AS TripType,c.Emp_Zone AS Zone\n" + 
                "FROM ncab_roster_tbl  a,NCAB_UNSCHEDULE_RQST_TBL  b ,ncab_master_employee_tbl c ,ncab_cab_master_tbl d\n" + 
                "WHERE b.Shift_ID=a.Shift_Id AND b.Emp_Qlid=c.Emp_Qlid AND a.Emp_Qlid=b.Emp_Qlid AND d.cab_license_plate_no=a.Cab_No;");

		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
            jsonresponse.put("Emp_Qlid", rs.getString(1));
            jsonresponse.put("Vendor_Name", rs.getString(2));
            jsonresponse.put("Emp_FName", rs.getString(3));
            jsonresponse.put("Emp_Mgr_Qlid1", rs.getString(4));
            jsonresponse.put("Start_Date_Time", rs.getString(5));
            jsonresponse.put("Cab_Cost", rs.getString(6));
            //jsonresponse.put("Shift_ID", rs.getString(7));
            jsonresponse.put("Cab_No", rs.getString(7));
            jsonresponse.put("Cab_Type", rs.getString(8));
            jsonresponse.put("Zone", rs.getString(9));
            jsonresponse.put("Destination", rs.getString(10));
            jsonresponse.put("Source", rs.getString(11));
            jsonresponse.put("TripType", rs.getString(12));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}
*/
	public JSONArray employeeDetailed() throws SQLException
    {
           JSONArray jsonarray=new JSONArray();
           DBConnectionRo dbconnection=new DBConnectionRo();
           Connection connection=dbconnection.getConnection();
           PreparedStatement ps=(PreparedStatement) connection.prepareStatement("SELECT b.Emp_Qlid AS Emp_Qlid,a.vendor_name AS Vendor_Name,c.Emp_FName AS Emp_FName,c.Emp_Mgr_Qlid1 AS Emp_Mgr_Qlid1 , \r\n" + 
           		"a.Start_Date AS Start_Date_Time,a.Cab_Cost AS Cab_Cost,a.Cab_No AS Cab_No,d.model AS Cab_Type,\r\n" + 
           		"b.Source AS Source,b.Destination AS Destination,b.Other_addr AS Trip_Type,c.Emp_Zone AS Zone\r\n" + 
           		"FROM ncab_roster_tbl  a,NCAB_UNSCHEDULE_RQST_TBL  b ,ncab_master_employee_tbl c ,ncab_cab_master_tbl d\r\n" + 
           		"WHERE b.Shift_ID=4 AND b.Emp_Qlid=c.Emp_Qlid AND a.Emp_Qlid=b.Emp_Qlid;");
           ResultSet rs=ps.executeQuery();
           while(rs.next())
           {
                  JSONObject jsonresponse=new JSONObject();
                  jsonresponse.put("Emp_Qlid", rs.getString(1));
                  jsonresponse.put("Vendor_Name", rs.getString(2));
                  jsonresponse.put("Emp_FName", rs.getString(3));
                  jsonresponse.put("Emp_Mgr_Qlid1", rs.getString(4));
                  jsonresponse.put("Start_Date_Time", rs.getString(5));
                  jsonresponse.put("Cab_Cost", rs.getString(6));
                  //jsonresponse.put("Shift_ID", rs.getString(7));
                  jsonresponse.put("Cab_No", rs.getString(7));
                  jsonresponse.put("Cab_Type", rs.getString(8));
                  jsonresponse.put("Source", rs.getString(9));
                  jsonresponse.put("Destination", rs.getString(10));
                  jsonresponse.put("Trip_Type", rs.getString(11));
                  jsonresponse.put("Zone", rs.getString(12));
                  
                  jsonarray.put(jsonresponse);
           }
           return jsonarray;
    }

	
/*	// working
	public JSONArray employeeDetailed(JSONObject jsonrequest) throws SQLException, ParseException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		String toDate=jsonrequest.getString("toDate");
		String fromDate=jsonrequest.getString("fromDate");
		PreparedStatement ps=null;
		ps=(PreparedStatement)connection.prepareStatement("SELECT b.Emp_Qlid AS Emp_Qlid,a.vendor_name AS Vendor_Name,c.Emp_FName AS Emp_FName,c.Emp_Mgr_Qlid1 AS Emp_Mgr_Qlid1 ,a.Start_Date AS Start_Date_Time,\n" + 
				"a.Cab_Cost AS Cab_Cost,a.Cab_No AS Cab_No,d.model AS Cab_Model,\n" + 
				"b.Source AS Source,b.Destination AS Destination,b.Other_addr AS TripType,c.Emp_Zone AS Zone\n" + 
				"FROM ncab_roster_tbl a,NCAB_UNSCHEDULE_RQST_TBL  b ,ncab_master_employee_tbl c ,ncab_cab_master_tbl d\n" + 
				"WHERE b.Shift_ID=a.Shift_Id AND b.Emp_Qlid=c.Emp_Qlid AND a.Emp_Qlid=b.Emp_Qlid AND d.cab_license_plate_no=a.Cab_No \n" + 
				"AND a.Start_Date BETWEEN ? AND ?;");
		ps.setString(1, fromDate);
		ps.setString(2, toDate);
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Emp_Qlid", rs.getString(1));
			jsonresponse.put("Vendor_Name", rs.getString(2));
			jsonresponse.put("Emp_FName", rs.getString(3));
			jsonresponse.put("Emp_Mgr_Qlid1", rs.getString(4));
			jsonresponse.put("Start_Date_Time", rs.getString(5));
			jsonresponse.put("Cab_Cost", rs.getString(6));
			jsonresponse.put("Cab_No", rs.getString(7));
			jsonresponse.put("Cab_Model",rs.getString(8));
			jsonresponse.put("Source", rs.getString(9));
			jsonresponse.put("Destination", rs.getString(10));
			jsonresponse.put("Trip_Type", rs.getString(11));
			jsonresponse.put("Zone", rs.getString(12));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}
*/

	public JSONArray employeeDetailed(JSONObject jsonrequest) throws SQLException, ParseException
    {
           JSONArray jsonarray=new JSONArray();
           Connection connection=new DBConnectionRo().getConnection();
           String toDate=jsonrequest.getString("toDate");
           String fromDate=jsonrequest.getString("fromDate");
           PreparedStatement ps=null;
           ps=(PreparedStatement)connection.prepareStatement("SELECT b.Emp_Qlid AS Emp_Qlid,a.vendor_name AS Vendor_Name,c.Emp_FName AS Emp_FName,c.Emp_Mgr_Qlid1 AS Emp_Mgr_Qlid1 ,a.Start_Date AS Start_Date_Time,\n" + 
                        "a.Cab_Cost AS Cab_Cost,a.Cab_No AS Cab_No,d.model AS Cab_Type,\n" + 
                        "b.Source AS Source,b.Destination AS Destination,b.Other_addr AS TripType,c.Emp_Zone AS Zone\n" + 
                        "FROM ncab_roster_tbl a,NCAB_UNSCHEDULE_RQST_TBL  b ,ncab_master_employee_tbl c ,ncab_cab_master_tbl d\n" + 
                        "WHERE b.Shift_ID=4 AND b.Emp_Qlid=c.Emp_Qlid AND a.Emp_Qlid=b.Emp_Qlid AND d.cab_license_plate_no=a.Cab_No \n" + 
                        "AND a.Start_Date BETWEEN ? AND ?;");
           ps.setString(1, fromDate);
           ps.setString(2, toDate);
           ResultSet rs=ps.executeQuery();
           while(rs.next())
           {
                  JSONObject jsonresponse=new JSONObject();
                  jsonresponse.put("Emp_Qlid", rs.getString(1));
                  jsonresponse.put("Vendor_Name", rs.getString(2));
                  jsonresponse.put("Emp_FName", rs.getString(3));
                  jsonresponse.put("Emp_Mgr_Qlid1", rs.getString(4));
                  jsonresponse.put("Start_Date_Time", rs.getString(5));
                  jsonresponse.put("Cab_Cost", rs.getString(6));
                  jsonresponse.put("Cab_No", rs.getString(7));
                  jsonresponse.put("Cab_Type", rs.getString(8));
                  jsonresponse.put("Source", rs.getString(9));
                  jsonresponse.put("Destination", rs.getString(10));
                  jsonresponse.put("TripType", rs.getString(11));
                  jsonresponse.put("Zone", rs.getString(12));
           
                  jsonarray.put(jsonresponse);
           }
           return jsonarray;
    }
	
	
	// working
	public JSONArray employeeSummary(JSONObject jsonrequest) throws SQLException
	{
		JSONArray jsonarray=new JSONArray();
		Connection connection=new DBConnectionRo().getConnection();
		PreparedStatement ps=null;
		String toDate= jsonrequest.getString("toDate");
		String fromDate= jsonrequest.getString("fromDate");
		ps=(PreparedStatement)connection.prepareStatement("SELECT  f.Emp_Qlid AS Emp_Qlid,f.Emp_FName AS Emp_FName,f.Emp_Mgr_Qlid1 AS Emp_Mgr_Qlid1,CONCAT(b.Emp_FName,' ',b.Emp_LName) AS Manager_Name,f.No_cab AS Cab_Count,f.Cost AS Cab_Cost\n" + 
				"FROM employee_summary AS f,ncab_master_employee_tbl AS b \n" + 
				"WHERE b.Emp_Qlid=f.Emp_Mgr_Qlid1 AND f.Start_Date BETWEEN ? AND ?;");
		ps.setString(1, fromDate);
		ps.setString(2, toDate);

		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			JSONObject jsonresponse=new JSONObject();
			jsonresponse.put("Emp_Qlid", rs.getString(1));
			jsonresponse.put("Emp_FName", rs.getString(2));
			jsonresponse.put("Emp_Mgr_Qlid1", rs.getString(3));
			jsonresponse.put("Manager_Name", rs.getString(4));
			jsonresponse.put("Cab_Count", rs.getString(5));
			jsonresponse.put("Cab_Cost", rs.getString(6));

			jsonarray.put(jsonresponse);
		}
		return jsonarray;
	}





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


	public JSONArray getRouteNos(String VendorName){
		DBConnectionRo dbconnection = new DBConnectionRo();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarray = new JSONArray();
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) connection.prepareStatement("SELECT distinct Route_No FROM ncab_roster_tbl where Vendor_Name = ?");
			ps.setString(1, VendorName);
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


	public JSONArray getCabNobyVendorandRoute(String VendorName,String RouteNo) throws SQLException, ClassNotFoundException {

		Connection connection=null;
		DBConnectionRo dbconnection=new DBConnectionRo();
		JSONArray jsonarray=new JSONArray();
		connection=dbconnection.getConnection();       

		PreparedStatement ps3= (PreparedStatement) connection.prepareStatement("SELECT distinct Cab_No FROM ncab_roster_tbl where Vendor_Name = ? and Route_No = ? ");
		ps3.setString(1,VendorName);
		ps3.setString(2, RouteNo);


		ResultSet rs3=ps3.executeQuery();
		while (rs3.next())
		{
			JSONObject jsonresponse = new JSONObject();

			String Cab_no=rs3.getString(1);                     
			jsonresponse.put("Cab_No",Cab_no);
			jsonarray.put(jsonresponse);      

		}

		return jsonarray; 

	}


	//checkin out code starts

	public JSONArray checkinoutReport(String route_no,String from_date,String to_date,String cab_no,String emp_fname,String emp_lname,String vendor_name) throws SQLException, ClassNotFoundException {
		String stmt1="";
		int count=0;
		String[] arr=new String[7];
		String[] parameter={route_no,from_date,to_date,cab_no,emp_fname,emp_lname,vendor_name};

		for(int i=0,j=-1;i<parameter.length;i++)
		{   System.out.println(parameter[i]);
		if(parameter[i].length()>1)
		{

			if(i==0){ stmt1=stmt1+"a.route_no = ? ";
			arr[++j]=parameter[i]; count++;  }
			else if(i==1){  
				if(count>0)
				{
					stmt1=stmt1+"&&"+" a.trip_date BETWEEN ? ";

				}
				else
					stmt1=stmt1+" a.trip_date BETWEEN ? ";
				arr[++j]=parameter[i]; count++;
			}
			else if(i==2){  stmt1=stmt1+" AND ? ";
			arr[++j]=parameter[i];count++;}
			else if(i==3){  
				if(count>0){
					stmt1=stmt1+"&&"+" b.cab_no = ?  ";
				}
				else stmt1=stmt1+" b.cab_no = ?  ";
				arr[++j]=parameter[i];count++;
			}
			else if(i==4){  

				if(count>0){

					stmt1=stmt1+"&&"+" d.emp_fname = ?";
				}
				else stmt1=stmt1+" d.emp_fname = ?";
				arr[++j]=parameter[i];count++;
			}
			else if(i==5){  stmt1=stmt1+" && d.emp_lname = ?  ";
			arr[++j]=parameter[i];count++;}
			else if(i==6){  
				if(count>0){
					stmt1=stmt1+"&&"+" c.vendor_name = ? ";
				}
				else stmt1=stmt1+" c.vendor_name = ? ";
				arr[++j]=parameter[i];count++;
			}



		}

		}

		System.out.println(stmt1);
		/*for(int i=0;i<count;i++)
       {
              System.out.println(arr[i]);
       }*/
		Connection connection=null;
		DBConnectionRo dbconnection=new DBConnectionRo();
		JSONArray jsonarray=new JSONArray();
		connection=dbconnection.getConnection();      
		System.out.println("Connected");
		String query="SELECT a.trip_date AS DATE, b.route_no,b.cab_no,c.vendor_name,a.trip_type,CONCAT(d.emp_fname,' ',d.emp_mname,' ',d.emp_lname) AS Employee_Name,CONCAT(e.start_time,' - ',e.end_time) AS Shift_Time,a.check_in_time,a.check_out_time,(CASE WHEN a.trip_type = 'Pick' THEN TIMEDIFF(a.check_out_time, e.start_time) END) AS late_arrival,(CASE WHEN a.trip_type = 'Drop' THEN TIMEDIFF(a.check_in_time,e.end_time) END) AS late_departure FROM ncab_emp_checkin_tbl a,ncab_roster_tbl b,ncab_vendor_master_tbl c,ncab_master_employee_tbl d,NCAB_SHIFT_MASTER_TBL e WHERE (" +stmt1+ ")  AND  (a.route_no=b.route_no && d.emp_qlid=a.emp_qlid) GROUP BY route_no";
		System.out.println(query);
		PreparedStatement ps4= (PreparedStatement) connection.prepareStatement(query);
		for(int i=0;i<count;i++){
			String s=arr[i];
			int j=i+1;
			ps4.setString(j,s);


		}


		ResultSet rs4=ps4.executeQuery();
		while (rs4.next())
		{   JSONObject jsonresponse = new JSONObject();
		String Date =rs4.getString(1);
		String Route_no =rs4.getString(2);
		String Cab_no=rs4.getString(3);
		String Vendor_name=rs4.getString(4);
		String trip_type=rs4.getString(5);
		String Employee_name=rs4.getString(6);
		String Shift_time=rs4.getString(7);
		String check_in_time=rs4.getString(8);
		String check_out_time=rs4.getString(9);
		String late_arrival=rs4.getString(10);
		String late_departure=rs4.getString(11);
		System.out.println(Employee_name);

		jsonresponse.put("DATE",Date);
		jsonresponse.put("route_no",Route_no);
		jsonresponse.put("cab_No",Cab_no);
		jsonresponse.put("vendor_name",Vendor_name);
		jsonresponse.put("trip_type",trip_type );
		jsonresponse.put("Employee_Name",Employee_name);

		jsonresponse.put("Shift_Time",Shift_time);
		jsonresponse.put("check_in_time",check_in_time);
		jsonresponse.put("check_out_time",check_out_time);
		jsonresponse.put("late_arrival",late_arrival);
		jsonresponse.put("late_departure",late_departure);


		jsonarray.put(jsonresponse);

		}
		return jsonarray; 

	}

	//checkin out code ends



	//transport billing report start
	public JSONArray gettransportbilling(
			String month, String year, String hrtax_regular_cab, String uptax_regular_cab,
			String emp_contrib_regular, String gps_regular_cab, String gstTax_regular_cab, String emp_contrib_shift,
			String gps_shift_cab, String gstTax_shift_cab, String hrtax_shift_cab, String uptax_shift_cab,
			String toll_shift_cab, String toll_unscheduled_cab, String gstTax_unscheduled, String standByCab_extraKms,
			String ratePerKm, String extraMileageCost, String standByCost, String standByTax, String otherCabCost,
			String otherCabGST, String escortGuardCost, String escortGuardDropDutyCost, String escortGuardTaxes,
			String tptMobCost, String overallUPtax, String overallHRtax, String overallTaxes, String overallToll,
			String overallGPS, String foreignExPrice, String toll_regular_cab  ) throws ClassNotFoundException, SQLException
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
			JSONObject jsonobj=new JSONObject();      
			if(rs.getString(3).equalsIgnoreCase(month) && rs.getString(4).equalsIgnoreCase(year)) {
				jsonobj.put("vendor_name",rs.getString(1));
				jsonobj.put("vendor_total_cost",rs.getString(2));

				jsonarray0.put(jsonobj);
				}
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

		if(Roster_Month.equals(month) && Roster_Year.equalsIgnoreCase(year))
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
		if(Roster_Month.equals(month) && Roster_Year.equalsIgnoreCase(year))
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
		if(Roster_Month.equalsIgnoreCase(month) && Roster_Year.equalsIgnoreCase(year))
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
		if(Roster_Month.equalsIgnoreCase(month) && Roster_Year.equalsIgnoreCase(year))
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

			if(Roster_Month.equalsIgnoreCase(month) && Roster_Year.equalsIgnoreCase(year))
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

		String query7="SELECT YEAR(Start_Date) as Roster_Year, MONTH(Start_Date) as Roster_Month,cab_type,SUM(Cab_Cost) as Total_Cost FROM sum_of_shift_cabs GROUP BY YEAR(Start_Date), MONTH(Start_Date),cab_type;";
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

		if(Roster_Month.equalsIgnoreCase(month) && Roster_Year.equalsIgnoreCase(year))
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
		jsonobjfinalnew.put("toll_regular_cab", toll_regular_cab );
		jsonobjfinalnew.put("gps_regular_cab", gps_regular_cab);
		jsonobjfinalnew.put("uptax_regular_cab", uptax_regular_cab);
		jsonobjfinalnew.put("hrtax_regular_cab", hrtax_regular_cab);
		jsonobjfinalnew.put("gstTax_regular_cab", gstTax_regular_cab);
		int t1= Integer.parseInt(jsonobjfinal.get("total_cost__regular_cab").toString())+Integer.parseInt(toll_regular_cab )+Integer.parseInt(gps_regular_cab)+Integer.parseInt(uptax_regular_cab)+Integer.parseInt(hrtax_regular_cab)+Integer.parseInt(gstTax_regular_cab);
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
		jsonobjfinalnew.put("Shift_Cab_Cost",t2);
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
		jsonobjfinalnew.put("Total_Transportation_Cost_for_the_Month", t1+t2+(Integer.parseInt(jsonobjfinalnew.get("Unscheduled_Cab_Costs").toString()))+(Integer.parseInt(jsonobjfinalnew.get("Standby_Cab_Cost").toString()))+(Integer.parseInt(jsonobjfinalnew.get("Cabs_for_Other_Purposes_Cost").toString())));
		jsonobjfinalnew.put("escortGuardCost", escortGuardCost);
		jsonobjfinalnew.put("escortGuardTaxes", escortGuardTaxes);
		jsonobjfinalnew.put("Total_Amount_Security_Guard", Integer.parseInt(escortGuardCost)+Integer.parseInt(escortGuardTaxes));
		jsonobjfinalnew.put("escortGuardDropDutyCost", Integer.parseInt(escortGuardDropDutyCost));   
		jsonobjfinalnew.put("Escort_Security_Guards", Integer.parseInt(escortGuardDropDutyCost)+(Integer.parseInt(jsonobjfinalnew.get("Total_Amount_Security_Guard").toString())));
		jsonobjfinalnew.put("tptMobCost", tptMobCost);
		jsonobjfinalnew.put("Transport_plus_Escort_Security", Integer.parseInt(tptMobCost)+(Integer.parseInt(jsonobjfinalnew.get("Total_Transportation_Cost_for_the_Month").toString()))+(Integer.parseInt(jsonobjfinalnew.get("Escort_Security_Guards").toString())));
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
		jsonobjfinalnew.put("foreignExPrice",foreignExPrice);
		jsonobjfinalnew.put("total_cost_regular_cab_small", jsonobjfinal.get("total_cost_regular_cab_small")); 
		jsonobjfinalnew.put("total_cost_regular_cab_big", jsonobjfinal.get("total_cost_regular_cab_big")); 
		jsonobjfinalnew.put("total_cost_shift_cab_small", jsonobjfinal.get("total_cost_shift_cab_small")); 
		jsonobjfinalnew.put("total_cost_shift_cab_big", jsonobjfinal.get("total_cost_shift_cab_big")); 
		jsonobjfinalnew.put("total_no_of_unscheduled_employees",jsonobjfinal.get("total_no_of_unscheduled_employees"));  
		jsonobjfinalnew.put("overall_emp_contri",Integer.parseInt(jsonobjfinalnew.get("Scheduled_Cab_Cost_less_Emp_Contribution").toString())+Integer.parseInt(jsonobjfinalnew.get("Shift_Cab_Cost_less_Emp_Contribution").toString()));
        jsonobjfinalnew.put("total_cost", Integer.parseInt(jsonobjfinalnew.get("Scheduled_Cab_Cost_less_Emp_Contribution").toString())+Integer.parseInt(jsonobjfinalnew.get("Shift_Cab_Cost_less_Emp_Contribution").toString())+Integer.parseInt(jsonobjfinal.get("total_unscheduled_cost").toString())); 

		
		jsonarray.put(jsonobjfinalnew);

		return jsonarray;


	}

	//transport billing report  ends


}
