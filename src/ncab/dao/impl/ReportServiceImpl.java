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
				//jsonresponse.put(""+i++,jsonobj);
		}
		return jsonarray;
		
	}
		//employee code ends
	
	
	//manager code
	public JSONArray getManagerReport() throws SQLException, ClassNotFoundException {
		
		JSONObject jsonresponse = new JSONObject();
		Connection connection=null;
		DBConnectionRo dbconnection=new DBConnectionRo();
		JSONArray jsonarray=new JSONArray();
					connection=(Connection) dbconnection.getConnection();						
					PreparedStatement ps2=(PreparedStatement) connection.prepareStatement(" SELECT Manager,Emp_FName AS ManagerName,NE,NC,Cost FROM view5,ncab_master_employee_tbl\r\n" + 
							" WHERE Emp_Qlid=Manager;");
					ResultSet rs2=ps2.executeQuery();
					
					while (rs2.next())	
					{
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
		JSONObject jsonresponse = new JSONObject();
		 Connection connection=null;
		 DBConnectionRo dbconnection=new DBConnectionRo();
		 JSONArray jsonarray=new JSONArray();
		 connection=dbconnection.getConnection();	
					PreparedStatement ps= (PreparedStatement) connection.prepareStatement("SELECT V.vendor_name,R.Cab_Cost,N.Rqst_Date_Time,R.Cab_No,N.Emp_Qlid,N.Request_ID FROM  NCAB_UNSCHEDULE_RQST_TBL N INNER JOIN ncab_roster_tbl R ON N.Emp_Qlid=R.Emp_Qlid INNER JOIN ncab_vendor_master_tbl V ON R.vendor_id=V.vendor_id;");
					ResultSet rs=ps.executeQuery();
					while (rs.next())
					{
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
	
	
}
