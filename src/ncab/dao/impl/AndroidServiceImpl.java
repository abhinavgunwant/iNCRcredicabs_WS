
package ncab.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;


import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import ncab.dao.DBConnectionRo;
import ncab.dao.DBConnectionUpd;
import ncab.webservice.RequestService;

public class AndroidServiceImpl {

	List<JSONObject> jsonArray;
	JSONArray jsonresponse;
	DBConnectionRo dbconnectionRo = new DBConnectionRo();
	DBConnectionUpd dbconnectionUpd=new DBConnectionUpd();
	Connection con = null;
	RequestServiceImpl requestserviceimpl=new RequestServiceImpl();
	String push_token="";

	public int saveApproval(String Approval,String req_id) {
		// TODO Auto-generated method stub
		System.out.println("Implementetion");
		//System.out.println(req_id);
		//int r=Integer.parseInt(req_id);

		int result=0;
		String act=null,Emp_Qlid=null,to=null,from=null,source=null,destination=null,time=null,reason=null,pushmsg=null;

		try {  
			con=(Connection) dbconnectionUpd.getConnection();

			PreparedStatement ps1 =con.prepareStatement("SELECT Approval,Emp_Qlid,Start_Date_Time,End_Date_time,Source,Destination,Rqst_Date_Time,Reason FROM NCAB_UNSCHEDULE_RQST_TBL WHERE Request_ID=?");
			int r=Integer.parseInt(req_id);

			ps1.setInt(1, r);

			ResultSet rs = ps1.executeQuery();

			System.out.println("Query");
			while(rs.next()){
				act=rs.getString(1);
				Emp_Qlid=rs.getString(2);
				from=rs.getString(3);
				to=rs.getString(4);
				source=rs.getString(5);
				destination=rs.getString(6);
				time=rs.getString(7);
				reason=rs.getString(8);

				System.out.println(to);
				System.out.println(act);
			}

			if(act.equalsIgnoreCase("UNAPPROVED")){

				PreparedStatement ps = con.prepareStatement("UPDATE NCAB_UNSCHEDULE_RQST_TBL SET Approval=? WHERE Request_ID=?");

				ps.setString(1,Approval);
				ps.setString(2, req_id);

				result=ps.executeUpdate();
				System.out.println("tez"+result);
				System.out.println("SQL update executed succesfully!");
				if(Approval.equalsIgnoreCase("Approved") || Approval.equalsIgnoreCase("Rejected")){

					//UtilServiceImpl revertMailService =new UtilServiceImpl();

					//String msg = 
					//revertMailService.sendEmailMessage( Mgr_QLID+"@ncr.com",Emp_QLID+"@ncr.com",transport_manager,null,msg);                                                             //from

					UtilServiceImpl sendPushService = new UtilServiceImpl();

					push_token=sendtoken(r);

					pushmsg = "Status: "+"Request has been "+Approval+"\n"+
							"Cab Request Id:"+r+"\n"+
							"Employee QLId:"+Emp_Qlid+"\n"+
							"From: "+from+"\n"+
							"To: "+ to+"\n"+
							"Source: "+source+"\n"+
							"Destination: "+destination+"\n"+
							"Time: "+time+"\n"+
                            "*Reason*: " +reason+"\n";

					System.out.println(pushmsg);

					sendPushService.sendPushMessage(push_token,"Cab Request Status",pushmsg);



				}

			}


			else{  result=7;
			System.out.println("Already approved/rejected");
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



	public String sendtoken(int request_id) throws SQLException {
		// TODO Auto-generated method stub
		String emp_id=null;
		System.out.println("In method");
		con=(Connection) dbconnectionUpd.getConnection();
		PreparedStatement ps3=con.prepareStatement("SELECT Emp_Qlid FROM NCAB_UNSCHEDULE_RQST_TBL WHERE Request_ID=?;");
		ps3.setInt(1, request_id);
		ResultSet rs3 = ps3.executeQuery();

		while(rs3.next()){
			emp_id=rs3.getString(1);

		}
		PreparedStatement ps2=con.prepareStatement("SELECT Login_Push_Token FROM ncab_login_credentials_tbl WHERE Emp_Qlid=?");
		ps2.setString(1,emp_id);


		ResultSet rs2 = ps2.executeQuery();
		System.out.println("Query2");
		while(rs2.next()){
			push_token=rs2.getString(1);

		}

		System.out.println(push_token);
		return push_token;

	}

	/*              public JSONObject postCheckInDetails(String Emp_Qlid,String Route_No,String date,String Check_in_Time,String Trip_Type,String Cab_Type) {
                  // TODO Auto-generated method stub
                  int x=0;
                  DBConnectionUpd database= new DBConnectionUpd();
                  JSONObject ob=new JSONObject();
                  Connection connection = (Connection) database.getConnection();
                  try {
                String query_check="Select * from ncab_emp_checkin_tbl where Emp_Qlid=? AND Trip_Date=? AND Trip_Type=?";
                PreparedStatement ps_check = connection.prepareStatement(query_check);
                         ps_check.setString(1,Emp_Qlid);
                         ps_check.setString(2,date);
                         ps_check.setString(3,Trip_Type );
                         ResultSet rs=ps_check.executeQuery();
                         if(rs.next())
                         { 

                               ob.put("Check_In","ALREADY");
                               return new JSONObject().put("result",ob);
                         }
                         else
                         {
                               String query = "INSERT INTO ncab_emp_checkin_tbl(Emp_Qlid,Route_No,Trip_Date,Check_in_Time,Trip_Type,Cab_Type) VALUES(?,?,?,?,?,?);";
                               PreparedStatement ps = connection.prepareStatement(query);
                               ps.setString(1,Emp_Qlid);
                               ps.setString(2,Route_No);
                               ps.setString(3,date);
                               ps.setString(4,Check_in_Time);
                               ps.setString(5,Trip_Type);
                               ps.setString(6,Cab_Type);
                               //     ps.setString(5, Pickup_Time );
                               x = ps.executeUpdate();
                               if(x==1){
                                      ob.put("Check_In","Done");
                                      return new JSONObject().put("result",ob);
                               }

                         }
                         connection.close();

                  } catch (Exception e) {
                         try {
                               throw e;
                         } catch (Exception e1) {
                               // TODO Auto-generated catch block
                               e1.printStackTrace();
                         }
                  }

                  return null;
           }
	 */              

	public JSONObject postCheckInDetails(String Emp_Qlid,String Route_No,String date,String Check_in_Time,String Trip_Type,String Cab_Type,String QRcode) 
	{
		// TODO Auto-generated method stub
		int x=0;
		DBConnectionUpd database= new DBConnectionUpd();
		JSONObject ob=new JSONObject();
		System.out.println("Check1");
		System.out.println("Hello");
		Connection connection = (Connection) database.getConnection();        
		try {
			System.out.println("Hello");
			if(QRcode.equals("NULL")||QRcode.equals("")||QRcode.equals("null"))
			{
				System.out.println("Check2");
				String query_check="Select * from ncab_emp_checkin_tbl where Emp_Qlid=? AND Trip_Date=? AND Trip_Type=?";
				PreparedStatement ps_check = connection.prepareStatement(query_check);
				ps_check.setString(1,Emp_Qlid);
				ps_check.setString(2,date);
				ps_check.setString(3,Trip_Type );
				ResultSet rs=ps_check.executeQuery();
				System.out.println(rs);
				if(rs.next() )
				{
					System.out.println("Check3");
					ob.put("Check_In","ALREADY");
					return new JSONObject().put("result",ob);
				}
				else
				{
					String query = "INSERT INTO ncab_emp_checkin_tbl(Emp_Qlid,Route_No,Trip_Date,Check_in_Time,Trip_Type,Cab_Type) VALUES(?,?,?,?,?,?);";
					PreparedStatement ps = connection.prepareStatement(query);
					ps.setString(1,Emp_Qlid);
					ps.setString(2,Route_No);
					ps.setString(3,date);
					ps.setString(4,Check_in_Time);
					ps.setString(5,Trip_Type);
					ps.setString(6,Cab_Type);
					//     ps.setString(5, Pickup_Time );
					x = ps.executeUpdate();
					if(x==1){
						ob.put("Check_In","Done");
						return new JSONObject().put("result",ob);
					}
				}


			} 
			else
			{
				String query_check_QRcode="SELECT * FROM  ncab_roster_tbl WHERE Cab_No=? AND Emp_Qlid=?;";
				PreparedStatement ps_check_QRcode = connection.prepareStatement(query_check_QRcode);
				ps_check_QRcode.setString(1, QRcode);
				ps_check_QRcode.setString(2,Emp_Qlid);
				System.out.println(QRcode);
				ResultSet rscheck_QRcode=ps_check_QRcode.executeQuery();
				Boolean check_QR=rscheck_QRcode.next();
				System.out.println(check_QR);     
				if(check_QR)
				{

					System.out.println("Check2");
					String query_check="Select * from ncab_emp_checkin_tbl where Emp_Qlid=? AND Trip_Date=? AND Trip_Type=?";
					PreparedStatement ps_check = connection.prepareStatement(query_check);
					ps_check.setString(1,Emp_Qlid);
					ps_check.setString(2,date);
					ps_check.setString(3,Trip_Type );
					ResultSet rs=ps_check.executeQuery();
					System.out.println(rs);
					if(rs.next() )
					{
						System.out.println("Check3");
						ob.put("Check_In","ALREADY");
						return new JSONObject().put("result",ob);
					}
					else
					{
						String query = "INSERT INTO ncab_emp_checkin_tbl(Emp_Qlid,Route_No,Trip_Date,Check_in_Time,Trip_Type,Cab_Type) VALUES(?,?,?,?,?,?);";
						PreparedStatement ps = connection.prepareStatement(query);
						ps.setString(1,Emp_Qlid);
						ps.setString(2,Route_No);
						ps.setString(3,date);
						ps.setString(4,Check_in_Time);
						ps.setString(5,Trip_Type);
						ps.setString(6,Cab_Type);
						//     ps.setString(5, Pickup_Time );
						x = ps.executeUpdate();
						if(x==1){
							ob.put("Check_In","Done");
							return new JSONObject().put("result",ob);
						}
					} 
				}       
				else
				{
					System.out.println("Check4");
					ob.put("Check_In","INCORRECT QRcode");
					return new JSONObject().put("result",ob);

				}


			}
			connection.close();

		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return null;
	}
	public JSONObject postCheckOutDetails(String Emp_Qlid,String date,String Check_out_Time,String Trip_Type,String QRcode) {
		// TODO Auto-generated method stub
		int x=0;
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = (Connection) database.getConnection();
		JSONObject ob=new JSONObject();    	 
		try {
			if(QRcode.equals("NULL")||QRcode.equals("")||QRcode.equals("null"))
			{
				String query_check="Select Check_out_time from ncab_emp_checkin_tbl where Emp_Qlid=? AND Trip_Date=? AND Trip_Type=?";
				PreparedStatement ps_check = connection.prepareStatement(query_check);
				ps_check.setString(1,Emp_Qlid);
				ps_check.setString(2,date);
				ps_check.setString(3,Trip_Type );
				ResultSet rs=ps_check.executeQuery();
				// Boolean check_record=rs.next();
				String temp="";
				while(rs.next())
				{
					temp=rs.getString(1);	
				}
				System.out.println("temp"+temp);
				//System.out.println(check_record);                   
				if(temp!=null)
				{
					System.out.println("Check3");
					ob.put("Check_out","ALREADY");
					return new JSONObject().put("result",ob);
				}
				else
				{

					String query = "UPDATE ncab_emp_checkin_tbl SET Check_out_Time=? WHERE Emp_Qlid=? AND Trip_Date=? AND Trip_Type=?;";
					PreparedStatement ps = connection.prepareStatement(query);
					ps.setString(1,Check_out_Time);
					ps.setString(2,Emp_Qlid);
					ps.setString(3,date);
					ps.setString(4,Trip_Type);
					x = ps.executeUpdate();
					System.out.println("Check1");
					if(x==1){
						System.out.println("Check2");

					}
					ob.put("Check_out","Done");
					connection.close();
					return new JSONObject().put("result",ob);

				}

			}
			else
			{
				String query_check_QRcode="SELECT * FROM  ncab_roster_tbl WHERE Cab_No=? AND Emp_Qlid=?;";
				PreparedStatement ps_check_QRcode = connection.prepareStatement(query_check_QRcode);
				ps_check_QRcode.setString(1, QRcode);
				ps_check_QRcode.setString(2,Emp_Qlid);
				System.out.println(QRcode);
				ResultSet rscheck_QRcode=ps_check_QRcode.executeQuery();
				Boolean check_QR=rscheck_QRcode.next();
				System.out.println(check_QR);     
				if(check_QR)
				{
					String query_check="Select Check_out_time from ncab_emp_checkin_tbl where Emp_Qlid=? AND Trip_Date=? AND Trip_Type=?";
					PreparedStatement ps_check = connection.prepareStatement(query_check);
					ps_check.setString(1,Emp_Qlid);
					ps_check.setString(2,date);
					ps_check.setString(3,Trip_Type );
					ResultSet rs=ps_check.executeQuery();
					//Boolean check_record=rs.next();
					String temp1="null";
					while(rs.next())
					{  
						temp1=rs.getString(1);	

					}
					System.out.println("temp"+temp1+"temp");
					//    System.out.println(check_record);                   
					if(temp1!=null )
					{
						System.out.println("Check3");
						ob.put("Check_out","ALREADY");
						return new JSONObject().put("result",ob);
					}
					else
					{

						String query = "UPDATE ncab_emp_checkin_tbl SET Check_out_Time=? WHERE Emp_Qlid=? AND Trip_Date=? AND Trip_Type=?;";
						PreparedStatement ps = connection.prepareStatement(query);
						ps.setString(1,Check_out_Time);
						ps.setString(2,Emp_Qlid);
						ps.setString(3,date);
						ps.setString(4,Trip_Type);
						x = ps.executeUpdate();
						System.out.println("Check1");
						if(x==1){
							System.out.println("Check2");

						}
						ob.put("Check_out","Done");
						connection.close();
						return new JSONObject().put("result",ob);

					}
				}
				else
				{
					System.out.println("Check4");
					ob.put("Check_out","INCORRECT QRcode");
					return new JSONObject().put("result",ob);

				}
			}
		} 
		catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return null;
	}

	public JSONObject getRoasterDetailsByEmpID(String emp_Qlid) {
		// TODO Auto-generated method stub
		DBConnectionRo dbconnection = new DBConnectionRo();
		Connection connection = (Connection) dbconnection.getConnection();
		String sql = "select Route_No,Pickup_Time,Shift_Id from ncab_roster_tbl where Emp_Qlid=?";
		PreparedStatement ps;
		PreparedStatement ps1;
		JSONObject jsonresponse=null;
		try {
			ps = connection.prepareStatement(sql);
			ps.setString(1,emp_Qlid);
			ResultSet rs = ps.executeQuery();
			String Route_No=null;
			String Pickup_Time=null;
			jsonresponse= new JSONObject();
			String Shift_Id=null;

			while (rs.next()) {

				Route_No=rs.getString(1);
				Pickup_Time=rs.getString(2);
				jsonresponse.put("Route_No", Route_No);
				System.out.println(Pickup_Time);
				jsonresponse.put("Pickup_Time", Pickup_Time);
				Shift_Id=rs.getString(3);
				System.out.println(Shift_Id);
				String sql1 = "select Start_Time from NCAB_SHIFT_MASTER_TBL where Shift_ID=?";
				ps1 = connection.prepareStatement(sql1);
				System.out.println(Shift_Id);
				ps1.setString(1,Shift_Id);
				ResultSet rs1=ps1.executeQuery();
				while(rs1.next())
				{
					String Start_Time = rs1.getString(1);
					jsonresponse.put("Start_Time",Start_Time);
					System.out.println(Start_Time);
				}

			}


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONObject().put("result", jsonresponse);

	}

	//              public JSONObject getRoasterDetailsByEmpID(String emp_Qlid) {
	//                  // TODO Auto-generated method stub
	//                  DBConnectionRo dbconnection = new DBConnectionRo();
	//                  Connection connection = (Connection) dbconnection.getConnection();
	//                                String sql = "select Pickup_Time from ncab_roster_tbl where Emp_Qlid=?";
	//                         PreparedStatement ps;
	//                         JSONObject jsonresponse=null;
	//                         try {
	//                                ps = connection.prepareStatement(sql);
	//                                ps.setString(1,emp_Qlid);
	//                                ResultSet rs = ps.executeQuery();
	//                                String Pickup_Time=null;
	//                                   jsonresponse= new JSONObject();
	//                                while (rs.next()) {
	//
	//                                        Pickup_Time=rs.getString(1);
	//                                      jsonresponse.put("Pickup_Time", Pickup_Time); 
	//                                      jsonresponse.put("status", "success"); 
	//                                                    }
	//
	//
	//                         } catch (SQLException e) {
	//                                // TODO Auto-generated catch block
	//                                e.printStackTrace();
	//                         }
	//                         return jsonresponse;
	//
	//           }
	
	public JSONObject getRequestsbyme(String Emp_Qlid){
        
        JSONArray jsonarray=new JSONArray();
        DBConnectionRo dbconnection = new DBConnectionRo();
        Connection connection = (Connection) dbconnection.getConnection();
        String sql = "SELECT (SELECT CONCAT(Emp_FName,' ',Emp_LName) FROM ncab_master_employee_tbl WHERE Emp_Qlid=?) AS Emp_Name,Rqst_Date_Time,Start_Date_Time,End_Date_Time,Approval,Source,Destination,Reason FROM NCAB_UNSCHEDULE_RQST_TBL WHERE Emp_Qlid=?";
        PreparedStatement ps;
 
        try {
               ps = connection.prepareStatement(sql);
               ps.setString(1,Emp_Qlid);
               ps.setString(2,Emp_Qlid);
               
               ResultSet rs = ps.executeQuery();
               if (!rs.next())
                     return (new JSONObject()).put("result","fail");
                     
               while (rs.next()) {
                     JSONObject jsonresponse=new JSONObject();

                     jsonresponse.put("Emp_Name",rs.getString(1));
                     jsonresponse.put("Rqst_Date_Time",rs.getString(2));
                     jsonresponse.put("Start_Date_Time",rs.getString(3));
                     jsonresponse.put("End_Date_Time",rs.getString(4));
                     jsonresponse.put("Approval",rs.getString(5));
                     jsonresponse.put("Source",rs.getString(6));
                     jsonresponse.put("Destination",rs.getString(7));
                     jsonresponse.put("Reason",rs.getString(8));
                      
                     jsonarray.put(jsonresponse);
        
               }


        } catch (SQLException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
        }
        return (new JSONObject()).put("result",jsonarray).put("status","success");

        
 }
 
 public JSONObject getRequestsforme(String Emp_Qlid){
        
        JSONArray jsonarray=new JSONArray();
        DBConnectionRo dbconnection = new DBConnectionRo();
        Connection connection = (Connection) dbconnection.getConnection();
        String sql = "SELECT a.Rqst_Date_Time,a.Start_Date_Time,a.End_Date_Time,a.Approval,a.Source,a.Destination,a.Reason,b.Emp_FName,b.Emp_LName FROM NCAB_UNSCHEDULE_RQST_TBL AS a, ncab_master_employee_tbl AS b  WHERE a.Emp_Qlid=b.Emp_Qlid AND a.Approvers like '%"+Emp_Qlid+"%' order by a.Approval desc";
        PreparedStatement ps;
 
        try {
               ps = connection.prepareStatement(sql);
               
               ResultSet rs = ps.executeQuery();
               
               if (!rs.next())
                     return (new JSONObject()).put("result","fail");
               
               while (rs.next()) {
                     JSONObject jsonresponse=new JSONObject();

                     jsonresponse.put("Emp_Name",rs.getString(8)+" "+rs.getString(9));
                     jsonresponse.put("Rqst_Date_Time",rs.getString(1));
                     jsonresponse.put("Start_Date_Time",rs.getString(2));
                     jsonresponse.put("End_Date_Time",rs.getString(3));
                      jsonresponse.put("Approval",rs.getString(4));
                     jsonresponse.put("Source",rs.getString(5));
                     jsonresponse.put("Destination",rs.getString(6));
                     jsonresponse.put("Reason",rs.getString(7));
                     
                     jsonarray.put(jsonresponse);
        
               }


        } catch (SQLException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
        }
        return (new JSONObject()).put("result",jsonarray).put("status","success");
        
 }


}
