package ncab.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.Statement;

import ncab.dao.DBConnectionRo;
import ncab.dao.DBConnectionUpd;

import com.mysql.jdbc.Connection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONObject;  
import java.util.TimeZone;

public class RequestServiceImpl {



	List<JSONObject> jsonArray;
	JSONArray jsonresponse;
	DBConnectionRo dbconnectionRo = new DBConnectionRo();
	DBConnectionUpd dbconnectionUpd=new DBConnectionUpd();
	Connection con = null;

	public String getCurrentDate()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Calcutta"));
		Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str=df.format(date);
		return str;

	}

	public int saveRequest(String emp_QLID, String shift_ID, String Start_Date_Time, String End_Date_Time,String source ,String destination , String other_addr,String reason) {
		// TODO Auto-generated method stub
		int result=0;

		try {	
			con=(Connection) dbconnectionUpd.getConnection();

			PreparedStatement ps = con.prepareStatement("insert into NCAB_UNSCHEDULE_RQST_TBL (Emp_Qlid,Shift_ID,Rqst_Date_Time,Start_Date_Time,End_Date_Time,Source,Destination, Other_Addr ,Reason) values (?,?,?,?,?,?,?,?,?) "
					,Statement.RETURN_GENERATED_KEYS);

			ps.setString(1,emp_QLID);
			ps.setString(2,shift_ID);
			ps.setString(3,getCurrentDate());
			ps.setString(4,Start_Date_Time);
			ps.setString(5,End_Date_Time);
			ps.setString(6,source);
			ps.setString(7,destination);
			ps.setString(8,other_addr);
			ps.setString(9,reason);
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();

			if(rs.next()){

				result = rs.getInt(1);


			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		

		return result;

	}

	public String getDate(String dateTime) {

		return dateTime.substring(8,10)+"-"+dateTime.substring(5,7)+"-"+dateTime.substring(0,4);

	}

	public String getTime(String dateTime) {

		String resultTime="";
		Calendar time = Calendar.getInstance();

		//Calendar.HOUR_OF_DAY is in 24-hour format
		time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateTime.substring(11,13)));

		time.set(Calendar.MINUTE, Integer.parseInt(dateTime.substring(14,16)));
		int am_pm = time.get(GregorianCalendar.AM_PM);
		String zone ;
		switch (am_pm) {
		case  Calendar.AM:
			zone="AM";
			//System.out.println("AM");
			break;
		default:
			zone="PM";
			//System.out.println("PM");
			break;
		}

		resultTime+=time.get(Calendar.HOUR) + ":" + time.get(Calendar.MINUTE) + " " +zone;

		return resultTime;
	}


	public ArrayList<ArrayList<String>> getUnscheduledRequestByIdImpl(String requestIds,String allocatedFlag) throws SQLException {

		ArrayList<ArrayList<String>> excelBody = new ArrayList<ArrayList<String>>();	

		//		System.out.println("in getUnscheduledRequestByIdImpl() >>requestIds"+requestIds+" >>allocatedFlag"+allocatedFlag);

		int i=0;
		try {
			con = (Connection) dbconnectionRo.getConnection();

			PreparedStatement ps=(PreparedStatement) con.prepareStatement("SELECT a.Rqst_Date_Time,a.Request_ID,a.Emp_Qlid AS Emp_ID ,a.Emp_Fname AS Employee_First_Name,a.Emp_Lname AS \r\n" + 
					"Employee_Last_Name,a.Emp_Gender,a.Emp_Mob_Nbr,b.Emp_Qlid AS Manager_Qlid,b.Emp_Fname AS Manager_First_Name,\r\n" + 
					" b.Emp_LName AS Manager_Last_Name,a.Start_Date_Time,a.End_Date_Time,a.Allocated,a.Emp_Pickup_Area, a.Other_Address , a.Source ,  a.Destination , a.Approval FROM NCAB_UNSCHEDULE_RQST_VIEW a, ncab_master_employee_tbl b   \r\n" + 
					"WHERE a.Emp_Mgr_Qlid1 = b.Emp_Qlid  AND a.Request_ID IN ("+requestIds+") AND Allocated=? ORDER BY a.Rqst_Date_Time DESC \r\n" + 
					" ");

			//			ps.setString(1,requestIds);
			ps.setString(1,allocatedFlag);
			ResultSet rs=ps.executeQuery();

			//			System.out.println("Prepared Statement after bind variables set:\n\t" + ps.toString());

			while (rs.next())

			{
				ArrayList<String> excelRow = new ArrayList<String>();
				String Request_ID=rs.getString("Request_ID");
				String Emp_ID=rs.getString("Emp_ID");
				String Employee_First_Name=rs.getString("Employee_First_Name");
				String Employee_Last_Name=rs.getString("Employee_Last_Name");
				String Employee_Name=Employee_First_Name+" "+Employee_Last_Name;
				String Emp_Gender=rs.getString("Emp_Gender");
				String Emp_Mob_Nbr=rs.getString("Emp_Mob_Nbr");
				String Manager_Qlid=rs.getString("Manager_Qlid");
				String Manager_First_Name=rs.getString("Manager_First_Name");
				String Manager_Last_Name=rs.getString("Employee_Last_Name");
				String Manager_Name=Manager_First_Name+" "+Manager_Last_Name;
				String Rqst_Date_Time=rs.getString("Rqst_Date_Time");
				String Start_Date_Time=rs.getString("Start_Date_Time");
				String End_Date_Time=rs.getString("End_Date_Time");
				String Allocated=rs.getString("Allocated");
				String Emp_Pickup_Area=rs.getString("Emp_Pickup_Area");
				String Other_Address=rs.getString("Other_Address");
				String Approval=rs.getString("Approval");
				String Source=rs.getString("Source");
				String Destination=rs.getString("Destination");

				excelRow.add(this.getDate(Rqst_Date_Time)+" "+this.getTime(Rqst_Date_Time)); //Request Date
				excelRow.add(Request_ID);	//Request ID
				excelRow.add(Emp_ID);		//Emp ID
				excelRow.add(Employee_Name);	//Emp name
				excelRow.add(Emp_Gender);	//gender
				excelRow.add(Emp_Mob_Nbr);	//emp no
				excelRow.add(Manager_Qlid);	//manager id
				excelRow.add(Manager_Name);	//manager name
				excelRow.add(Source);	//pickup loc
				excelRow.add(this.getTime(Start_Date_Time));	//pickup time
				excelRow.add(Destination); //drop loc
				excelRow.add(Other_Address);	//Trip Type
				excelRow.add(Approval);			//Request Status		

				//				System.out.println(excelRow.toString());

				excelBody.add(excelRow);
			}

			return  excelBody;
		}

		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error:RequestServiceImpl::getUnscheduledRequestByIdImpl()");
		}
		finally {
			con.close();
		}
		return excelBody;

	}

	public List<JSONObject> getUnscheduledRequest(String requestJson) throws SQLException {
		jsonArray = new ArrayList<JSONObject>();	
		int i=0;
		try {
			con = (Connection) dbconnectionRo.getConnection();

			PreparedStatement ps=(PreparedStatement) con.prepareStatement("SELECT a.Request_ID,a.Emp_Qlid AS Emp_ID ,a.Emp_Fname AS Employee_First_Name,a.Emp_Lname AS \r\n" + 
					"Employee_Last_Name,a.Emp_Gender,a.Emp_Mob_Nbr,b.Emp_Qlid AS Manager_Qlid,b.Emp_Fname AS Manager_First_Name,\r\n" + 
					" b.Emp_LName AS Manager_Last_Name,a.Rqst_Date_Time,a.Start_Date_Time,a.End_Date_Time,a.Allocated,a.Emp_Pickup_Area, a.Other_Address , a.Source ,  a.Destination , a.Approval FROM NCAB_UNSCHEDULE_RQST_VIEW a, ncab_master_employee_tbl b  \r\n" + 
					"WHERE a.Emp_Mgr_Qlid1 = b.Emp_Qlid  AND Allocated=? ORDER BY a.Rqst_Date_Time DESC \r\n" + 
					" ");
			ps.setString(1,requestJson);
			ResultSet rs=ps.executeQuery();

			while (rs.next())

			{
				JSONObject jsonobj=new JSONObject();
				String Request_ID=rs.getString("Request_ID");
				String Emp_ID=rs.getString("Emp_ID");
				String Employee_First_Name=rs.getString("Employee_First_Name");
				String Employee_Last_Name=rs.getString("Employee_Last_Name");
				String Employee_Name=Employee_First_Name+" "+Employee_Last_Name;
				String Emp_Gender=rs.getString("Emp_Gender");
				String Emp_Mob_Nbr=rs.getString("Emp_Mob_Nbr");
				String Manager_Qlid=rs.getString("Manager_Qlid");
				String Manager_First_Name=rs.getString("Manager_First_Name");
				String Manager_Last_Name=rs.getString("Manager_Last_Name");
				String Manager_Name=Manager_First_Name+" "+Manager_Last_Name;
				String Rqst_Date_Time=rs.getString("Rqst_Date_Time");
				String Start_Date_Time=rs.getString("Start_Date_Time");
				String End_Date_Time=rs.getString("End_Date_Time");
				String Allocated=rs.getString("Allocated");
				String Emp_Pickup_Area=rs.getString("Emp_Pickup_Area");
				String Other_Address=rs.getString("Other_Address");
				String Approval=rs.getString("Approval");
				String Source=rs.getString("Source");
				String Destination=rs.getString("Destination");

				jsonobj.put("Request_ID",Request_ID);
				jsonobj.put("Emp_ID", Emp_ID);
				jsonobj.put("Employee_Name",Employee_Name);
				jsonobj.put("Emp_Gender",Emp_Gender);
				jsonobj.put("Emp_Mob_Nb",Emp_Mob_Nbr);
				jsonobj.put("Manager_Qlid",Manager_Qlid);
				jsonobj.put("Manager_Name",Manager_Name);
				jsonobj.put("Rqst_Date_Time",this.getDate(Rqst_Date_Time)+" "+this.getTime(Rqst_Date_Time));
				jsonobj.put("Start_Date",this.getDate(Start_Date_Time));
				jsonobj.put("Start_Time",this.getTime(Start_Date_Time));
				jsonobj.put("End_Date",this.getDate(End_Date_Time));
				jsonobj.put("Allocated",Allocated);
				jsonobj.put("Emp_Pickup_Area",Emp_Pickup_Area);
				jsonobj.put("Other_Address",Other_Address);
				jsonobj.put("Approval",Approval);
				jsonobj.put("Source",Source);
				jsonobj.put("Destination",Destination);

				jsonArray.add(jsonobj);
			}
			//			System.out.print(jsonArray);
			return jsonArray; 
		}

		catch (SQLException e) {
			// TODO Auto-generated catch block
			JSONObject jsonobj=new JSONObject();
			e.printStackTrace();
			jsonobj.put("status", "0");
			jsonobj.put("message", e.toString());
			jsonArray.add(jsonobj);
			return jsonArray;
		}
		finally {
			con.close();
		}

	}

	public JSONArray getRequest(JSONObject requestJson)  {
		RequestServiceImpl requestserviceimpl=new RequestServiceImpl();

		try {
			jsonresponse = new JSONArray(requestserviceimpl.getUnscheduledRequest(requestJson.getString("Allocated")));
		} catch (NoSuchElementException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonresponse;
	}


	public boolean onApprovedService(JSONArray requestIdArr){

		//		System.out.println("In onApprovedService  request_id->"+ request.getString("Allocate"));
		//		System.out.println("In onApprovedService  request_id->"+requestIdArr.toString());
		return new RequestServiceImpl().onApprovedDao(requestIdArr);

	} 


	public boolean onApprovedDao(JSONArray requestIdArr) 
	{
		int i=0;
		con=(Connection) dbconnectionUpd.getConnection();
		try {

			for(int count=0;count<requestIdArr.length();count++) {

				//				System.out.println("id in onApprovedDao"+requestIdArr.get(count));
				PreparedStatement ps = (PreparedStatement) con.prepareStatement("UPDATE  NCAB_UNSCHEDULE_RQST_TBL SET Allocated=1 WHERE Request_ID=?");
				ps.setString(1,String.valueOf(requestIdArr.get(count)));
				i= ps.executeUpdate();

			}
			if(i>0)
				return true;
			else 
				return false;

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}


	} 


}

