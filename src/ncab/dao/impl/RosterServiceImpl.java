package ncab.dao.impl;

import java.sql.Connection;
import java.util.*;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.IOException;

import java.sql.Timestamp;
import java.sql.Types;

import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.mysql.jdbc.CallableStatement;

import org.json.JSONArray;
import org.json.JSONObject;

import ncab.beans.RosterModel;
import ncab.dao.DBConnectionUpd;

public class RosterServiceImpl {
@SuppressWarnings("unused")
	public JSONArray showRosterInfo(JSONObject jsn){
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        System.out.println("Start showRosterInfo :: "+ new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
        
		JSONArray jsonArr =new JSONArray();
		DBConnectionUpd db=new DBConnectionUpd();
		RosterModel rm=new RosterModel(); 
		Connection con=db.getConnection();
		String qlid=jsn.getString("qlid");
		String cab_number=jsn.getString("c_n");
		String shift_time=jsn.getString("s_i");
		String emp_name=jsn.getString("e_n");
		System.out.println(shift_time);
		String month="MAR";
		String year="2018";
		int rn,shift;
		ResultSet rs1,rs2;
		String qlid_cab="";//cab count
		String query="",subquery1="",subquery2="",subquery3="";
		HashMap<String,String> occu=new HashMap<>();
		System.out.println("Start showRosterInfo 2 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
		
		//getting occupancy from vendor
		
		String occu_query="select cab_license_plate_no,cab_capacity from ncab_cab_master_tbl";
		try {
			PreparedStatement ps3=con.prepareStatement(occu_query);
			ResultSet rs3=ps3.executeQuery();
			while(rs3.next()){
			  occu.put(rs3.getString(1),rs3.getString(2));
			  System.out.println("Item added :-  "+rs3.getString(1)+"  "+rs3.getString(2));;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	//getting occupancy from vendor ends	
		
		System.out.println("Start showRosterInfo 3 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
		
		// if cab number is not null
		
		if(!(cab_number.equals(""))){
			String pick_qlid="";
			query="select Emp_Qlid from ncab_roster_tbl where Cab_No LIKE '%"+cab_number+"%' and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"' order by Route_No";
			subquery3="select Count(Emp_Qlid) from ncab_roster_tbl where Cab_No='"+cab_number+"' and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"'";
			try {
				
				//get count
				PreparedStatement ps4=con.prepareStatement(subquery3);
				ResultSet rs4=ps4.executeQuery();
				while(rs4.next()){
				 qlid_cab=rs4.getString(1);	
				}
				System.out.println("Cab count:- "+qlid_cab);
				//get count ends

				System.out.println("Start showRosterInfo 4 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
				
				PreparedStatement ps=con.prepareStatement(query);
				ResultSet rs=ps.executeQuery();
				while(rs.next()){
					JSONObject jsonObj=new JSONObject();
					pick_qlid=rs.getString(1);
		              subquery1="select * from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"'and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"'";
		              subquery2="select Emp_FName,Emp_MName,Emp_LName,Emp_Pickup_Area,Emp_Mob_Nbr from ncab_master_employee_tbl where Emp_Qlid='"+pick_qlid+"'";
		              PreparedStatement ps1=con.prepareStatement(subquery1);
		              PreparedStatement ps2=con.prepareStatement(subquery2);
		              rs1=ps1.executeQuery();
		              rs2=ps2.executeQuery();
		              while(rs1.next()){
		                 rm.setQlid(rs1.getString(2));
		                 rm.setCab_number(rs1.getString(6));
		                 rm.setRoot_number(rs1.getString(1));
		                 rm.setShift_id(rs1.getString(3));
		            	 jsonObj.put("Qlid",rm.getQlid());
		 		         jsonObj.put("Cab_number",rm.getCab_number());
		 		         jsonObj.put("Route_number",rm.getRoot_number());
		 		         jsonObj.put("shift_id", rm.getShift_id());
		            	  }
		              while(rs2.next()){
		            	  rm.setFname(rs2.getString(1));
		            	  rm.setMname(rs2.getString(2));
		            	  rm.setLname(rs2.getString(3));
		            	  rm.setPickup_area(rs2.getString(4));
		            	  rm.setEmp_Mob(rs2.getString(5));
		            	  jsonObj.put("f_name",rm.getFname());
		            	  jsonObj.put("m_name",rm.getMname());
		            	  jsonObj.put("l_name",rm.getLname());
		            	  jsonObj.put("p_a",rm.getPickup_area());
		            	  jsonObj.put("e_mob",rm.getEmp_Mob());

		              }
		              String cab_exist=occu.get(rm.getCab_number());
                      if(Integer.parseInt(rm.getShift_id())>3){
                          jsonObj.put("occu_left","0");

                      }
                      else{if(cab_exist == null){
                             jsonObj.put("occu_left","0");
                      }
                      else
                      {
                        jsonObj.put("occu_left",( Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
                        System.out.println(" put :- "+(Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
                      } 
                      }

		              jsonArr.put(jsonObj);
					 }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		
		//if cab number is null
		else{
			System.out.println("Start showRosterInfo 5 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
			// if qlid is not null 
			if(!(qlid.equals(""))){
				try {
					String pick_qlid="";
					query="select Emp_Qlid from ncab_roster_tbl where Cab_No IN (select Cab_No from ncab_roster_tbl where Emp_Qlid='"+qlid+"') and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"' order by Route_No";
					subquery3="select Count(Emp_Qlid) from ncab_roster_tbl where Roster_Month='"+month+"' and Roster_Year='"+year+"' and Emp_Status = 'active' and Cab_no IN(select Cab_No from ncab_roster_tbl where Emp_Qlid='"+qlid+"' and Roster_Month='"+month+"' and Roster_Year='"+year+"')";
                     qlid_cab="";
                    
                     //get count 
                     PreparedStatement ps4=con.prepareStatement(subquery3);
						ResultSet rs4=ps4.executeQuery();
						while(rs4.next()){
						 qlid_cab=rs4.getString(1);	
						}
						System.out.println("Cab count:- "+qlid_cab);
					  //get count ends
					
					PreparedStatement ps=con.prepareStatement(query);
					ResultSet rs=ps.executeQuery();
					while(rs.next()){

						JSONObject jsonObj=new JSONObject();
						 pick_qlid=rs.getString(1);
			              subquery1="select * from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"' and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"'";
			              subquery2="select Emp_FName,Emp_MName,Emp_LName,Emp_Pickup_Area,Emp_Mob_Nbr from ncab_master_employee_tbl where Emp_Qlid='"+pick_qlid+"'";
			              PreparedStatement ps1=con.prepareStatement(subquery1);
			              PreparedStatement ps2=con.prepareStatement(subquery2);
			              rs1=ps1.executeQuery();
			              rs2=ps2.executeQuery();
			              while(rs1.next()){
				                 rm.setQlid(rs1.getString(2));
				                 rm.setCab_number(rs1.getString(6));
				                 rm.setRoot_number(rs1.getString(1));
				                 rm.setShift_id(rs1.getString(3));
			            	 jsonObj.put("Qlid",rm.getQlid());
			 		         jsonObj.put("Cab_number",rm.getCab_number());
			 		         jsonObj.put("Route_number",rm.getRoot_number());
			 		         jsonObj.put("shift_id", rm.getShift_id());
			            	  }
			              while(rs2.next()){
			            	  rm.setFname(rs2.getString(1));
			            	  rm.setMname(rs2.getString(2));
			            	  rm.setLname(rs2.getString(3));
			            	  rm.setPickup_area(rs2.getString(4));
			            	  rm.setEmp_Mob(rs2.getString(5));
			            	  jsonObj.put("f_name",rm.getFname());
			            	  jsonObj.put("m_name",rm.getMname());
			            	  jsonObj.put("l_name",rm.getLname());
			            	  jsonObj.put("p_a",rm.getPickup_area());
			            	  jsonObj.put("e_mob",rm.getEmp_Mob());

			              }
			              String cab_exist=occu.get(rm.getCab_number());
			              if(Integer.parseInt(rm.getShift_id())>3){
	                          jsonObj.put("occu_left","0");

	                      }
	                      else{if(cab_exist == null){
	                             jsonObj.put("occu_left","0");
	                      }
	                      else
	                      {
	                        jsonObj.put("occu_left",( Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
	                        System.out.println(" put :- "+(Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
	                      } 
	                      }


			              jsonArr.put(jsonObj);	
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// if qlid is null
			
			/////////////////////////////////////////////
			
			else{
				System.out.println("Start showRosterInfo 7 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
				//if name is not null
				if(!(emp_name.equals(""))){
					
					try {
						String pick_qlid="";
						if(shift_time.equals(""))
						query="SELECT Emp_Qlid FROM ncab_roster_tbl WHERE Roster_Month='"+month+"' and Roster_Year='"+year+"' and Emp_Status = 'active' and Cab_No IN (SELECT Cab_No FROM ncab_roster_tbl WHERE Roster_Month='"+month+"' and Roster_Year='"+year+"' and Emp_Qlid IN (SELECT Emp_Qlid FROM ncab_master_employee_tbl WHERE (Emp_FName LIKE '%"+emp_name+"%')||(Emp_MName LIKE '%"+emp_name+"%')||(Emp_LName LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_MName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%'))) order by Route_No";
						else
						query="SELECT Emp_Qlid FROM ncab_roster_tbl WHERE Roster_Month='"+month+"' and Roster_Year='"+year+"' and Emp_Status = 'active' and Cab_No IN (SELECT Cab_No FROM ncab_roster_tbl WHERE Roster_Month='"+month+"' and Roster_Year='"+year+"' and Emp_Qlid IN (SELECT Emp_Qlid FROM ncab_master_employee_tbl WHERE (Emp_FName LIKE '%"+emp_name+"%')||(Emp_MName LIKE '%"+emp_name+"%')||(Emp_LName LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_MName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')))  and Shift_Id='"+shift_time+"' order by Route_No";

						qlid_cab="";
						
						PreparedStatement ps=con.prepareStatement(query);
						ResultSet rs=ps.executeQuery();
						while(rs.next()){
							pick_qlid=rs.getString(1);
							subquery3="select Count(Emp_Qlid) from ncab_roster_tbl where Roster_Month='"+month+"' and Roster_Year='"+year+"' and Emp_Status = 'active' and Cab_no IN(select Cab_No from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"' and Roster_Month='"+month+"' and Roster_Year='"+year+"')";
							PreparedStatement ps4=con.prepareStatement(subquery3);
							ResultSet rs4=ps4.executeQuery();
							while(rs4.next()){
							 qlid_cab=rs4.getString(1);	
							}
							JSONObject jsonObj=new JSONObject();
				              subquery1="select * from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"' and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"'";
				              subquery2="select Emp_FName,Emp_MName,Emp_LName,Emp_Pickup_Area,Emp_Mob_Nbr from ncab_master_employee_tbl where Emp_Qlid='"+pick_qlid+"' ";
				              PreparedStatement ps1=con.prepareStatement(subquery1);
				              PreparedStatement ps2=con.prepareStatement(subquery2);
				              rs1=ps1.executeQuery();
				              rs2=ps2.executeQuery();
				              while(rs1.next()){
					                 rm.setQlid(rs1.getString(2));
					                 rm.setCab_number(rs1.getString(6));
					                 rm.setRoot_number(rs1.getString(1));
					                 rm.setShift_id(rs1.getString(3));
				            	 jsonObj.put("Qlid",rm.getQlid());
				 		         jsonObj.put("Cab_number",rm.getCab_number());
				 		         jsonObj.put("Route_number",rm.getRoot_number());
				 		         jsonObj.put("shift_id", rm.getShift_id());
				            	  }
				              while(rs2.next()){
				            	  rm.setFname(rs2.getString(1));
				            	  rm.setMname(rs2.getString(2));
				            	  rm.setLname(rs2.getString(3));
				            	  rm.setPickup_area(rs2.getString(4));
				            	  rm.setEmp_Mob(rs2.getString(5));
				            	  jsonObj.put("f_name",rm.getFname());
				            	  jsonObj.put("m_name",rm.getMname());
				            	  jsonObj.put("l_name",rm.getLname());
				            	  jsonObj.put("p_a",rm.getPickup_area());
				            	  jsonObj.put("e_mob",rm.getEmp_Mob());

				              }
				              String cab_exist=occu.get(rm.getCab_number());
				              if(Integer.parseInt(rm.getShift_id())>3){
		                          jsonObj.put("occu_left","NA");

		                      }
		                      else{if(cab_exist == null){
		                             jsonObj.put("occu_left","0");
		                      }
		                      else
		                      {
		                        jsonObj.put("occu_left",( Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
		                        System.out.println(" put :- "+(Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
		                      } 
		                      }


				              jsonArr.put(jsonObj);	
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				//////////////////////////////////// if name is null
			
			
			
			else
			{
				System.out.println("Start showRosterInfo 6 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
				// if shift id is not null
				if(!(shift_time.equals(""))){
					try {
						String pick_qlid="";
						if(emp_name.equals(""))
						query="select Emp_Qlid from ncab_roster_tbl where Shift_Id='"+shift_time+"' and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"' order by Route_No";
						else
						query="select Emp_Qlid from ncab_roster_tbl where Shift_Id='"+shift_time+"' and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"' and ((Emp_FName LIKE '%"+emp_name+"%')||(Emp_MName LIKE '%"+emp_name+"%')||(Emp_LName LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_MName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')) order by Route_No";
					
						qlid_cab="";
				
						PreparedStatement ps=con.prepareStatement(query);
						ResultSet rs=ps.executeQuery();
						int ct = 0;
						while(rs.next()){
							pick_qlid=rs.getString(1);
							subquery3="select Count(Emp_Qlid) from ncab_roster_tbl where Roster_Month='"+month+"' and Roster_Year='"+year+"' and Emp_Status = 'active' and Cab_no IN(select Cab_No from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"' and Roster_Month='"+month+"' and Roster_Year='"+year+"')";
							PreparedStatement ps4=con.prepareStatement(subquery3);
							ResultSet rs4=ps4.executeQuery();
							while(rs4.next()){
							 qlid_cab=rs4.getString(1);	
							}
							JSONObject jsonObj=new JSONObject();
							
				              subquery1="select * from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"' and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"'";
				              subquery2="select Emp_FName,Emp_MName,Emp_LName,Emp_Pickup_Area,Emp_Mob_Nbr from ncab_master_employee_tbl where Emp_Qlid='"+pick_qlid+"' ";
				              PreparedStatement ps1=con.prepareStatement(subquery1);
				              PreparedStatement ps2=con.prepareStatement(subquery2);
				              rs1=ps1.executeQuery();
				              rs2=ps2.executeQuery();
				              while(rs1.next()){
					                 rm.setQlid(rs1.getString(2));
					                 rm.setCab_number(rs1.getString(6));
					                 rm.setRoot_number(rs1.getString(1));
					                 rm.setShift_id(rs1.getString(3));
				            	 jsonObj.put("Qlid",rm.getQlid());
				 		         jsonObj.put("Cab_number",rm.getCab_number());
				 		         jsonObj.put("Route_number",rm.getRoot_number());
				 		         jsonObj.put("shift_id", rm.getShift_id());
				            	  }
				              while(rs2.next()){
				            	  rm.setFname(rs2.getString(1));
				            	  rm.setMname(rs2.getString(2));
				            	  rm.setLname(rs2.getString(3));
				            	  rm.setPickup_area(rs2.getString(4));
				            	  rm.setEmp_Mob(rs2.getString(5));
				            	  jsonObj.put("f_name",rm.getFname());
				            	  jsonObj.put("m_name",rm.getMname());
				            	  jsonObj.put("l_name",rm.getLname());
				            	  jsonObj.put("p_a",rm.getPickup_area());
				            	  jsonObj.put("e_mob",rm.getEmp_Mob());

				              }
				              String cab_exist=occu.get(rm.getCab_number());
				              if(Integer.parseInt(rm.getShift_id())>3){
		                          jsonObj.put("occu_left","0");

		                      }
		                      else{if(cab_exist == null){
		                             jsonObj.put("occu_left","0");
		                      }
		                      else
		                      {
		                        jsonObj.put("occu_left",( Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
		                        System.out.println(" put :- "+(Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
		                      } 
		                      }

								ct++;
								jsonArr.put(jsonObj);	
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
else{
						System.out.println("Start showRosterInfo 8 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
						//if all is null
						try {
							String pick_qlid="";
							query="select Emp_Qlid from ncab_roster_tbl where Emp_Status='active' and Roster_Month='"+month+"' and Roster_Year='"+year+"' order by Route_No";

							qlid_cab="";
							PreparedStatement ps=con.prepareStatement(query);
							ResultSet rs=ps.executeQuery();
							while(rs.next()){
								pick_qlid=rs.getString(1);
								subquery3="select Count(Emp_Qlid) from ncab_roster_tbl where Roster_Month='"+month+"' and Roster_Year='"+year+"' and Emp_Status = 'active' and Cab_No IN(select Cab_No from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"' and Roster_Month='"+month+"' and Roster_Year='"+year+"')";
								PreparedStatement ps4=con.prepareStatement(subquery3);
								
								ResultSet rs4=ps4.executeQuery();
								while(rs4.next()){
								 qlid_cab=rs4.getString(1);	
								}
								JSONObject jsonObj=new JSONObject();
								 
					              subquery1="select * from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"' and Emp_Status = 'active' and Roster_Month='"+month+"' and Roster_Year='"+year+"' ";
					              subquery2="select Emp_FName,Emp_MName,Emp_LName,Emp_Pickup_Area,Emp_Mob_Nbr from ncab_master_employee_tbl where Emp_Qlid='"+pick_qlid+"'";
					              PreparedStatement ps1=con.prepareStatement(subquery1);
					              PreparedStatement ps2=con.prepareStatement(subquery2);
					              rs1=ps1.executeQuery();

					              rs2=ps2.executeQuery();

										while(rs1.next()){
						                 rm.setQlid(rs1.getString(2));
						                 rm.setCab_number(rs1.getString(6));
						                 rm.setRoot_number(rs1.getString(1));
						                 rm.setShift_id(rs1.getString(3));
					            	 jsonObj.put("Qlid",rm.getQlid());
					 		         jsonObj.put("Cab_number",rm.getCab_number());
					 		         jsonObj.put("Route_number",rm.getRoot_number());
					 		         jsonObj.put("shift_id", rm.getShift_id());
					            	  }
					              while(rs2.next()){
					            	  rm.setFname(rs2.getString(1));
					            	  rm.setMname(rs2.getString(2));
					            	  rm.setLname(rs2.getString(3));
					            	  rm.setPickup_area(rs2.getString(4));
					            	  rm.setEmp_Mob(rs2.getString(5));
					            	  jsonObj.put("f_name",rm.getFname());
					            	  jsonObj.put("m_name",rm.getMname());
					            	  jsonObj.put("l_name",rm.getLname());
					            	  jsonObj.put("p_a",rm.getPickup_area());
					            	  jsonObj.put("e_mob",rm.getEmp_Mob());
					              }
					              String cab_exist=occu.get(rm.getCab_number());
					              if(Integer.parseInt(rm.getShift_id())>3){
			                          jsonObj.put("occu_left","0");

			                      }
			                      else{if(cab_exist == null){
			                             jsonObj.put("occu_left","0");
			                      }
			                      else
			                      {
			                        jsonObj.put("occu_left",( Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
			                        System.out.println(" put :- "+(Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
			                      } 
			                      } 


					              jsonArr.put(jsonObj);	
					              
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		System.out.println("Start showRosterInfo 9 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
		return jsonArr;
	}
	
	
	
	

	
	public void insertIntoDB(InputStream fileInputStream, FormDataContentDisposition fileFormDataContentDisposition)
			throws IOException {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		RowCheck rowcheck = new RowCheck();
		FileWriter f0 = new FileWriter("C:\\Users\\AG250497\\Desktop\\output.txt");
		String[] route_no_arr = null;
		String[] empid_arr = null;
		String[] cab_arr = null;
		String route_no = null;
		int i = 0, last_row_valid = 0, index = 1;
		HashMap<String, String> link = new HashMap<>();
		int ct = 0;

		try {
			Workbook workbook = null;
			if (fileFormDataContentDisposition.getFileName().endsWith("xlsx")) {
				System.out.println("Yes check succeed");
				workbook = new XSSFWorkbook(fileInputStream);
			} else {
				workbook = new HSSFWorkbook(fileInputStream);
				System.out.println("Yes check succeed for other file type");
			}
			Sheet sheet = workbook.getSheetAt(0);

			System.out.println(sheet.getLastRowNum());
			// Generating right LastRowNum

			for (i = sheet.getLastRowNum(); i > 0; i--) {
				Row row_check_test = (Row) sheet.getRow(i);
				boolean flag = RowCheck.isRowEmpty(row_check_test);
				if (flag == true) {
					System.out.println("Empty row Existed, Iterating Backwards");
					continue;
				} else {
					System.out.println("The Index is " + i);
					last_row_valid = i;
					break;

				}
			}
			route_no_arr = new String[last_row_valid];
			empid_arr = new String[last_row_valid];
			cab_arr = new String[last_row_valid];

			Row row;
			String newLine = System.getProperty("line.separator");
			for (i = 1; i <= last_row_valid; i++) {
				row = (Row) sheet.getRow(i);
				String shift_id = null;
				// String routeno = row.getCell(8).getStringCellValue();
				// route_no_arr[i - 1] = routeno;
				// System.out.println("route: " + routeno + route_no_arr[i -
				// 1]);
				String empid = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				empid_arr[i - 1] = empid;
				System.out.println("id: " + empid);

				// String pickarea = row.getCell(4).getStringCellValue();
				// pickup_arr[i - 1] = pickarea;
				// System.out.println("pick area: " + pickarea);

				String shift_time = row.getCell(11).getStringCellValue();
				if (shift_time.equals(" 07:00 - 04:00")) {
					shift_id = "1";
				}
				if (shift_time.equals(" 10:00 - 07:00")) {
					shift_id = "2";
				}
				if (shift_time.equals(" 12:00 - 09:00")) {
					shift_id = "3";
				}
				System.out.println("Shift: " + shift_id);

				String pickhrs = "" + row.getCell(6).getDateCellValue().getHours();
				String pickmin = "" + row.getCell(6).getDateCellValue().getMinutes();
				if (pickmin.compareTo("0") == 0)
					pickmin = "00";
				String picktime = pickhrs + ":" + pickmin;
				System.out.println("pick time: " + picktime);

				String cab_from_excel = row.getCell(13).getStringCellValue();
				cab_arr[i - 1] = cab_from_excel;
				System.out.println("Cab: " + cab_from_excel);

				String roster_month = row.getCell(14).getStringCellValue();
				System.out.println("Roster Month " + roster_month);

				int roster_yr = (int) row.getCell(15).getNumericCellValue();
				String roster_year = Integer.toString(roster_yr);
				System.out.println("Roster Year " + roster_year);

				String remarks = row.getCell(7).getStringCellValue();
				System.out.println("Remarks are " + remarks);

				String Route_No = "";
				String cabno;
				PreparedStatement cabno_pre = connection
						.prepareStatement("select COUNT(cab_license_plate_no) from ncab_cab_master_tbl where cab_license_plate_no = '" + cab_from_excel + "';");
				ResultSet res_cab = cabno_pre.executeQuery();
				res_cab.next();
				String cabno_flag = res_cab.getString(1);
				if (cabno_flag.equals("0")) {
					cabno = "invalid_cab";
				} else {
					cabno = cab_from_excel;
				}
				if (cabno.equals("invalid_cab")) {
					Route_No = "Errorofcab";
				} else if (link.get(cabno) == null) {
					ct++;
					Route_No = String.format("%03d", ct);
					link.put(cabno, Route_No);
				} else {
					// make db connection and proper usage of prepared statement
					// and result sets
					// PreparedStatement route = connection.prepareStatement(
					// "select DISTINCT (Route_No) from roster_tbl where Cab_No
					// = '" + cabno + "';");
					// ResultSet rs_route = route.executeQuery();
					// rs_route.next();
					// String rn = rs_route.getString(1);
					// int var = Integer.parseInt(rn); // (@@)
					Route_No = link.get(cabno);

					// or directly, Route_No = query;
				}

				System.out.println("Import rows " + i);
				connection.setAutoCommit(false);
				// PreparedStatement ps = connection.prepareStatement(
				// "INSERT INTO roster_tbl(Route_No, Emp_Qlid, Pickup_Area,
				// Time_Stamp) VALUES (?, ?, ?, ?)");
				


				//function call for insertion

				CallableStatement cs = (CallableStatement) connection
						.prepareCall("{? = call ncab_add_excel_row_3_fnc(?,?,?,?,?,?,?,?)}");
				cs.registerOutParameter(1, Types.VARCHAR);
				cs.setString(2, empid);
				cs.setString(3, shift_id);
				cs.setString(4, picktime);
				cs.setString(5, cabno);
				cs.setString(6, roster_month);
				cs.setString(7, roster_year);
				cs.setString(8, remarks);
				cs.setString(9, Route_No);
				cs.execute();
				String retValue = cs.getString(1);
				System.out.println(retValue +"Point" );
				String[] flag = { "FAILURE", "NO", "NO", "NO", "No" };
				String[] quote = { "FAILURE", "QLID", "Shift_Timing", "Cab_No",
						"Route No has no Vacancy" };
				String[] retValue_token = retValue.split("\\s+");
				String final_push = "";
				System.out.println(retValue_token[0] + "SagaCheck");
				if (retValue_token[0].compareTo("FAILURE") == 0) {

					PreparedStatement occupancy = connection
							.prepareStatement("select COUNT(cab_capacity) from ncab_cab_master_tbl where cab_license_plate_no = '" + cabno + "';");
					ResultSet occupancy_no = occupancy.executeQuery();
					// ResultSet will be returning null if Validation fails onto
					// given Cab_No.
					occupancy_no.next();
					String occ_no = occupancy_no.getString(1);
					PreparedStatement vacancy = connection
							.prepareStatement("select COUNT(Emp_Qlid) from ncab_roster_tbl where Cab_No = '" + cabno + "';");
					ResultSet vacancy_num = vacancy.executeQuery();
					vacancy_num.next();
					String vacan_num = vacancy_num.getString(1);
					if (occ_no.compareTo(vacan_num) == 0) {
						link.remove(cabno);
						ct--;
					}

					if (retValue_token[1].compareTo("1") != 0) {
						System.out.println("Check Inside QLID IF");
						flag[1] = "Yes";
					}
					if (retValue_token[2].compareTo("1") != 0) {
						System.out.println("Check Inside ShiftID IF");
						flag[2] = "Yes";
					}
					if (retValue_token[3].compareTo("1") != 0) {
						System.out.println("Check Inside CabNo IF");
						flag[3] = "Yes";
					}
					if (retValue_token[4].compareTo("0") == 0) {
						System.out.println("Check inside Route No vacancy");
						flag[4] = "Yes";
					}
					int counter = 0;
					for (int y = 1; y < 5; y++) {
						if (flag[y].compareTo("Yes") == 0) {
							final_push = final_push.concat(quote[y]) + " ";
							counter++;
						}
					}

					System.out.println(final_push);
					empid_arr[i - 1] = "Error";
					cab_arr[i - 1] = "Error";
					f0.write("Error for " + i + " record" + " Reason for Error(" + counter + "):- " + final_push
							+ " Invalid @ " + new Timestamp(System.currentTimeMillis()) + newLine);
				}
			}
			//
			f0.write("XXXXXXXXXXXXXXXXXX " + (i - 1) + " Records Processed @  "
					+ new Timestamp(System.currentTimeMillis()) + "  XXXXXXXXXXXXXXXXXXX");
			f0.close();
			workbook.close();
			connection.commit();
			connection.close();

			// System.out.println("Success import excel to mysql table");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	

	public JSONObject addEmpToDb(JSONObject json){
  	  JSONObject js=new JSONObject();
	  js=null;

		try {  
        	 DBConnectionUpd db=new DBConnectionUpd();
     		 Connection con=db.getConnection();
			String cab=json.getString("c_n");
			String qlid=json.getString("qlid");
			int count=0;
			//String pick=json.getString("p_time");
			System.out.println(cab+"   "+qlid);
			String r_n="",shift_time="",month="",year="",status="active";
				PreparedStatement ps1=con.prepareStatement("select Route_No,Shift_Id,Roster_Month,Roster_Year from ncab_roster_tbl where Cab_No=?");
			    ps1.setString(1, cab);
			    ResultSet rs2=ps1.executeQuery();
			    while(rs2.next()){
			    r_n=rs2.getString(1);
			    shift_time=rs2.getString(2);
			    month=rs2.getString(3);
			    year=rs2.getString(4);
			    
//				PreparedStatement ps2=con.prepareStatement("insert into roster_tbl(Route_No,Emp_Qlid,Shift_Time,Pickup_time,Cab_No) values(?,?,?,?,?)");
				PreparedStatement ps2=con.prepareStatement("insert into ncab_roster_tbl(Route_No,Emp_Qlid,Shift_Id,Cab_No,Roster_Month,Roster_Year,Emp_Status) values(?,?,?,?,?,?,?)");

			    ps2.setString(1, r_n);
                ps2.setString(2, qlid);
                ps2.setString(3,shift_time);
                ps2.setString(4,cab);
                ps2.setString(5,month);
                ps2.setString(6, year);
                ps2.setString(7, status);
                boolean i=ps2.execute();
                if(i){
                	js.put("msg", "success");
                	}
                else{
                	js.put("msg", "fail");
                	}
                			
                	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
		return js;
	} 
	
	
	public JSONArray getAddData(JSONObject json){
		String c_no=json.getString("c_n");
		DBConnectionUpd db=new DBConnectionUpd();
		 Connection connection=db.getConnection();
		 JSONArray jsarr=new JSONArray();
		 try {
			PreparedStatement ps=connection.prepareStatement("select Emp_Qlid from ncab_master_employee_tbl where Emp_Qlid not in (select Emp_Qlid from ncab_roster_tbl where Emp_Status='active' and Roster_Month='MAR' and Roster_Year='2018')");
//				PreparedStatement ps=connection.prepareStatement("select Emp_Qlid from master_employee ");

			 ResultSet rs=ps.executeQuery();
		    while(rs.next()){
		    	JSONObject js=new JSONObject();
		    	js.put("Qlid",rs.getString(1));
		    	jsarr.put(js);
		    }
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return jsarr;
		 
	}
	

    public JSONObject inactiveqlid(JSONObject jobj) {
           
                  
//         String qlid=jobj.getString("e_qlid");
           // TODO Auto-generated method stub
    	JSONObject js=new JSONObject();
           
           try {
                  String qlid=jobj.getString("emp_qlid");
                  System.out.println("Delete :- "+qlid);
                  DBConnectionUpd db=new DBConnectionUpd();
                  Connection con=db.getConnection();
                  String query="UPDATE ncab_roster_tbl SET Emp_Status='inactive' WHERE Emp_Qlid = '"+qlid+"'";
                  PreparedStatement ps=con.prepareStatement(query);
                  int i=ps.executeUpdate();
                  if(i>0)
                       js.put("msg","success");
                  else
                	  js.put("msg","fail");
                  
                  
           } catch (Exception e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
           }
           
           return js;
    }

	//Jaspreet
    
    
    public JSONArray showVendor() {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jarr = new JSONArray();
		String vname = "", vid = "";
		try {
			// PreparedStatement ps = connection.prepareStatement("select
			// vendor_id, vendor_name from ncab_vendor_tbl");
			PreparedStatement ps = connection
					.prepareStatement("select vendor_id, vendor_name from ncab_vendor_master_tbl");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject json = new JSONObject();
				vid = "" + rs.getInt(1);
				vname = rs.getString(2);
				json.put("vid", vid);
				json.put("vname", vname);
				jarr.put(json);
			}

		} catch (Exception e) {
			System.out.println("error in imp" + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return jarr;

	}

	public JSONObject getEmpDetails(JSONObject json) {
		JSONArray jarr = new JSONArray();
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		String Fname = "", Mname = "", Lname = "", parea = "", ph = "", route = "";
		String qlid = json.getString("qlid");
		String month = "MAR";
		JSONObject json1 = new JSONObject();
		System.out.println("inside getEmpDetails");
		try {
			System.out.println("inside try before query");
			PreparedStatement ps1 = connection
					.prepareStatement("select Route_No from ncab_roster_tbl where Emp_Qlid = ? and Roster_Month = ? and Emp_Status='active'");
			ps1.setString(1, qlid);
			ps1.setString(2, month);
			ResultSet rs1 = ps1.executeQuery();
			while (rs1.next()) {
				route = "" + rs1.getString(1);
				if (route.isEmpty()) {
					route = " ";
				} else {
					route = "RN" + route;
				}

			}
			
			json1.put("route", route);
			jarr.put(json1);
			System.out.println(json1.getString("route"));
			PreparedStatement ps = connection.prepareStatement(
					"select Emp_FName, Emp_MName, Emp_LName, Emp_Pickup_Area, Emp_Mob_Nbr  from ncab_master_employee_tbl where Emp_Qlid = ?");
			ps.setString(1, qlid);
			ResultSet rs = ps.executeQuery();
			System.out.println("Inside try after query");
			while (rs.next()) {
				// JSONObject json1 = new JSONObject();
				Fname = rs.getString(1);
				Mname = rs.getString(2);
				Lname = rs.getString(3);
				parea = rs.getString(4);
				ph = rs.getString(5);
				json1.put("qlid",qlid);
				json1.put("fname", Fname);
				json1.put("mname", Mname);
				json1.put("lname", Lname);
				json1.put("parea", parea);
				json1.put("ph", ph);
				jarr.put(json1);
			}
			System.out.println(json1.getString("route"));

		} catch (Exception e) {
			System.out.println("error in imp" + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return json1;

	}

	public int empdeact(JSONObject json) {
		int flag = 0;
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		String qlid = json.getString("qlid");
		String month = null;
		int m = json.getInt("month");
		switch (m) {
		case 01:
			month = "JAN";
			break;
		case 02:
			month = "FEB";
			break;
		case 03:
			month = "MAR";
			break;
		case 04:
			month = "APR";
			break;
		case 05:
			month = "MAY";
			break;
		case 06:
			month = "JUN";
			break;
		case 07:
			month = "JUL";
			break;
		case 8:
			month = "AUG";
			break;
		case 9:
			month = "SEP";
			break;
		case 10:
			month = "OCT";
			break;
		case 11:
			month = "NOV";
			break;
		case 12:
			month = "DEC";
			break;
		}
		try {
			PreparedStatement ps = connection.prepareStatement(
					"Update ncab_roster_tbl set Emp_Status='Inactive' where Emp_Qlid=? and Roster_Month = ?");
			ps.setString(1, qlid);
			ps.setString(2, month);
			flag = ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return flag;
	}

	public JSONArray getQlid() {

		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarr = new JSONArray();
		String qlid = "";
		try {
			System.out.println("Inside try before query");
			PreparedStatement ps = connection.prepareStatement("select Emp_Qlid from ncab_master_employee_tbl");
			ResultSet rs = ps.executeQuery();
			System.out.println("Inside try after query");
			while (rs.next()) {
				JSONObject json = new JSONObject();
				qlid = rs.getString(1);
				json.put("qlid", qlid);
				jsonarr.put(json);
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return jsonarr;
	}

	public JSONArray showCabs(JSONObject shift) {
		JSONArray jsonarr = new JSONArray();
		DBConnectionUpd db = new DBConnectionUpd();
		Connection connection = db.getConnection();
		int shiftid = shift.getInt("shift");
		int vendorid = shift.getInt("vendor");
		String cabno = "";
		int cap=0;
		try {
			// PreparedStatement ps = connection.prepareStatement("SELECT
			// cab_license_plate_no FROM ncab_cab_tbl WHERE cab_license_plate_no
			// NOT IN ( SELECT Cab_No FROM ncab_roster_tbl WHERE Shift_Id = ? )
			// ");
			PreparedStatement ps = connection.prepareStatement(
					"SELECT cab_license_plate_no, cab_capacity FROM ncab_cab_master_tbl WHERE cab_license_plate_no NOT IN ( SELECT Cab_No FROM ncab_roster_tbl WHERE Shift_Id = ? ) and vendor_id =? ");
			ps.setInt(1, shiftid);
			ps.setInt(2, vendorid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				cabno = rs.getString(1);
				cap=rs.getInt(2);
				jobj.put("cabno", cabno);
				jobj.put("cap", cap);
				jsonarr.put(jobj);
			}

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return jsonarr;
	}

	public int setNewRouteSCH(JSONArray jsonarr) {
		int flag = 0;
		DBConnectionUpd db = new DBConnectionUpd();
		Connection connection = db.getConnection();
		String qlid, guard, picktime, cabno, month = "", year, start, end, vendor, Status = "Active";
		int shiftid = 0;
		try {
			String routeno = "";
			int route = 0, m = 0;
			PreparedStatement ps = connection.prepareStatement("select Max(Route_No) from ncab_roster_tbl");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				routeno = rs.getString(1);
			}
			route = Integer.parseInt(routeno);
			route++;
			System.out.println(route);
			if(route<10)
				routeno = "00" + route;
			else if(route >= 10 && route<100)
				routeno = "0" + route;
			System.out.println("Before loop: " + jsonarr.length());
			for (int i = 0; i < jsonarr.length(); i++) {

				JSONObject json = jsonarr.getJSONObject(i);
				qlid = json.optString("qlid");
				shiftid = json.optInt("shift");
				guard = json.optString("guard");
				if (guard.equalsIgnoreCase("true")) {
					guard = "Yes";
				} else {
					guard = "No";
				}
				// month=json.optString("month");
				m = json.optInt("month");
				switch (m) {
				case 01:
					month = "JAN";
					break;
				case 02:
					month = "FEB";
					break;
				case 03:
					month = "MAR";
					break;
				case 04:
					month = "APR";
					break;
				case 05:
					month = "MAY";
					break;
				case 06:
					month = "JUN";
					break;
				case 07:
					month = "JUL";
					break;
				case 8:
					month = "AUG";
					break;
				case 9:
					month = "SEP";
					break;
				case 10:
					month = "OCT";
					break;
				case 11:
					month = "NOV";
					break;
				case 12:
					month = "DEC";
					break;
				}
				year = json.optString("year");
				picktime = json.optString("picktime");
				cabno = json.optString("cabno");
				start = json.optString("start");
				end = json.optString("end");
				vendor = json.optString("vendor");
				System.out.println("Object Created" + qlid);
				System.out.println("----Query ready" + qlid);
				// PreparedStatement ps1 = connection.prepareStatement("insert
				// into ncab_roster_tbl
				// (Route_no,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Start_Date,End_Date,Month,Year,Vendor_Id,Status)
				// values (?,?,?,?,?,?,?,?,?,?,?,?)");
				PreparedStatement ps1 = connection.prepareStatement(
						"insert into ncab_roster_tbl (Route_no,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Start_Date,End_Date,Roster_Month,Roster_Year,Vendor_Id,Route_Status,Emp_Status) values (?,?,?,?,?,?,?,?,?,?,?,?,?)");

				ps1.setString(1, routeno);
				ps1.setString(2, qlid);
				ps1.setInt(3, shiftid);
				ps1.setString(4, picktime);
				ps1.setString(5, cabno);
				ps1.setString(6, guard);
				ps1.setString(7, start);
				ps1.setString(8, end);
				ps1.setString(9, month);
				ps1.setString(10, year);
				ps1.setString(11, vendor);
				ps1.setString(12, Status);
				ps1.setString(13, Status);
				flag = ps1.executeUpdate();
				System.out.println("------query fired");
			}
		} catch (Exception e) {
			System.out.println("Error" + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	public int setNewRouteUnSCH(JSONArray jsonarr) {
		int flag = 0;
		DBConnectionUpd db = new DBConnectionUpd();
		Connection connection = db.getConnection();
		String qlid, guard, picktime, cabno, month = "", year, start, end, vendor, Status = "Active";
		int shiftid = 4, cost, m = 0;
		try {
			String routeno = "000";
			System.out.println("Before loop: " + jsonarr.length());
			for (int i = 0; i < jsonarr.length(); i++) {

				JSONObject json = jsonarr.getJSONObject(i);
				qlid = json.optString("qlid");
				guard = json.optString("guard");
				if (guard.equalsIgnoreCase("true")) {
					guard = "Yes";
				} else {
					guard = "No";
				}
				// month=json.optString("month");
				m = json.optInt("month");
				switch (m) {
				case 01:
					month = "JAN";
					break;
				case 02:
					month = "FEB";
					break;
				case 03:
					month = "MAR";
					break;
				case 04:
					month = "APR";
					break;
				case 05:
					month = "MAY";
					break;
				case 06:
					month = "JUN";
					break;
				case 07:
					month = "JUL";
					break;
				case 8:
					month = "AUG";
					break;
				case 9:
					month = "SEP";
					break;
				case 10:
					month = "OCT";
					break;
				case 11:
					month = "NOV";
					break;
				case 12:
					month = "DEC";
					break;
				}
				year = json.optString("year");
				picktime = json.optString("picktime");
				cabno = json.optString("cabno");
				start = json.optString("start");
				end = json.optString("end");
				vendor = json.optString("vendor");
				cost = json.optInt("cost");
				System.out.println("Object Created" + qlid);
				System.out.println("----Query ready" + qlid);
				// PreparedStatement ps1 = connection.prepareStatement("insert
				// into ncab_roster_tbl
				// (Route_no,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Start_Date,End_Date,Month,Year,Vendor_Id,Status,Cab_Cost)
				// values (?,?,?,?,?,?,?,?,?,?,?,?,?)");

				PreparedStatement ps1 = connection.prepareStatement(
						"insert into ncab_roster_tbl (Route_no,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Start_Date,End_Date,Roster_Month,Roster_Year,Vendor_Id,Emp_Status,Cab_Cost,Route_Status) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				ps1.setString(1, routeno);
				ps1.setString(2, qlid);
				ps1.setInt(3, shiftid);
				ps1.setString(4, picktime);
				ps1.setString(5, cabno);
				ps1.setString(6, guard);
				ps1.setString(7, start);
				ps1.setString(8, end);
				ps1.setString(9, month);
				ps1.setString(10, year);
				ps1.setString(11, vendor);
				ps1.setString(12, Status);
				ps1.setInt(13, cost);
				ps1.setString(14, Status);
				flag += ps1.executeUpdate();
				System.out.println("------query fired");

			}

		} catch (Exception e) {
			System.out.println("Error" + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

//saurav
	

	public String sauravkaeditmethod(JSONObject obj) {
	
		try {
			String cabno=obj.getString("cabno");
			String picktime=obj.getString("picktime");
			//int shifttime=Integer.parseInt(obj.getString("shiftime"));
			String shifttime="";
			String qlid = obj.getString("qlid");
			String sdate=obj.getString("sdate");
			String edate=obj.getString("edate");
			
            System.out.println(cabno);
			System.out.println(picktime);
			System.out.println(qlid);
			System.out.println(sdate);
			System.out.println(edate);
			System.out.println(sdate.length());
			System.out.println(edate.length());
			String sd=sdate.substring(8);
			System.out.println(sd);
			String ed=edate.substring(8);
			System.out.println(ed);
			

				    
			DBConnectionUpd db=new DBConnectionUpd();
			Connection con=db.getConnection();
			String v_n="";
			String r_n="";
			int capacity=0;
			String query6="select count(Emp_Qlid) from ncab_roster_tbl where Cab_No='"+cabno+"' and Roster_Month='Mar' and Roster_Year='2018' and Emp_Status='active'";
			PreparedStatement ps7=con.prepareStatement(query6);
			ResultSet rs6=ps7.executeQuery();
			rs6.next();
			String query7="select cab_capacity from ncab_cab_master_tbl where cab_license_plate_no='"+cabno+"'";
			PreparedStatement ps8=con.prepareStatement(query7);
			ResultSet rs8=ps8.executeQuery();
			rs8.next();
			capacity=Integer.parseInt((rs8.getString(1)))-Integer.parseInt(rs6.getString(1));
            System.out.println("capacity::: "+capacity);
			if(capacity>0){
//			String query1="update ncab_roster_tbl set Emp_Status='inactive' where Emp_Qlid='"+qlid+"'";
			String query2="select Route_No from ncab_roster_tbl where Cab_No='"+cabno+"' and Roster_Month='Mar' and Roster_Year='2018' and Emp_Status='active'";
			PreparedStatement ps2=con.prepareStatement(query2);
			ResultSet rs=ps2.executeQuery();
			while(rs.next()){
			r_n=rs.getString(1);
			}
			String query5="select Shift_Id from ncab_roster_tbl where Cab_No='"+cabno+"' and Roster_Month='Mar' and Roster_Year='2018' and Emp_Status='active'";
			PreparedStatement ps5=con.prepareStatement(query5);
			ResultSet rs2=ps5.executeQuery();
			while(rs2.next()){
			shifttime=rs2.getString(1);
			}
			String query3="select vendor_id from ncab_cab_master_tbl where cab_license_plate_no='"+cabno+"'";
			PreparedStatement ps3=con.prepareStatement(query3);
			ResultSet rs1=ps3.executeQuery();
			while(rs1.next()){
			v_n=rs1.getString(1);
			}
			String query4="update ncab_roster_tbl set Emp_Status='inactive' where Emp_Qlid='"+qlid+"' and Roster_Month='Mar' and Roster_Year='2018'";
			PreparedStatement ps4=con.prepareStatement(query4);
			ps4.executeUpdate();
			//			String query1="update ncab_roster_tbl set Emp_Status='inactive' where Emp_Qlid='"+qlid+"' and Emp_Month='Mar' and Emp_Year='2018'";
			String query1="insert into ncab_roster_tbl(Route_No,Emp_Qlid,Cab_No,Pickup_Time,Shift_Id,Start_Date,End_Date,Roster_Month,Roster_Year) values(?,?,?,?,?,?,?,?,?) ";
			PreparedStatement ps=con.prepareStatement(query1);
			ps.setString(1, r_n);
			ps.setString(2, qlid);
			ps.setString(3, cabno);
			ps.setString(4, picktime);
			ps.setString(5, shifttime);
			ps.setString(6, sd);
			ps.setString(7, ed);
			ps.setString(8, "Mar");
			ps.setString(9, "2018");
			
			ps.executeUpdate();
//			String query="UPDATE ncab_roster_tbl SET Cab_No='"+cabno+"' , Pickup_Time ='"+picktime+"', Shift_Id='"+shifttime+"' ,Start_Date='"+sd+"' , End_Date='"+ed+"'  WHERE Emp_Qlid ='"+qlid+"' ";
//			String query="UPDATE roster_tbl SET Cab_No='"+cabno+"' , Pickup_Time ='"+picktime+"', Shift_Time='"+shifttime+"' ,Start_Date='"+sd+"' , End_Date='"+ed+"'  WHERE Emp_Qlid ='"+qlid+"' ";
			
			//PreparedStatement ps1=con.prepareStatement(query3);
			//int i=ps.executeUpdate();
			}
			else{
				return "fail";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		// TODO Auto-generated method stub
		return("success");
	}
	
	//richa
	public JSONArray getAllRoute()
	{
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jarr = new JSONArray();
		
		try{
			PreparedStatement ps = connection.prepareStatement("select distinct Route_No from ncab_roster_tbl where Roster_Month='MAR' and Roster_Year='2018' and Route_Status='active' and Shift_Id <>'4'");
			
			ResultSet rs= ps.executeQuery();
			while(rs.next()){
				JSONObject json=new JSONObject();
				json.put("r_n", rs.getString(1));
				System.out.println(rs.getString(1));
				jarr.put(json);
			}	
		}
		catch(Exception e){
			System.out.println("error in imp"+e.getMessage());
		}
		return jarr;
	}
	
	public JSONArray getAllCab()
	{
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jarr = new JSONArray();
		
		try{
			PreparedStatement ps = connection.prepareStatement("select cab_license_plate_no from ncab_cab_master_tbl where cab_license_plate_no not in(select Cab_No from ncab_roster_tbl)") ;
			
			ResultSet rs= ps.executeQuery();
			while(rs.next()){
				JSONObject json=new JSONObject();
				json.put("c_n", rs.getString(1));
			
				jarr.put(json);
			}
			
		}
		catch(Exception e){
			System.out.println("error in imp"+e.getMessage());
		}
		return jarr;
	}
	public String updatedRoute(JSONObject json){
		System.out.println("inside fetchroster");
	DBConnectionUpd db=new DBConnectionUpd();
	Connection con=db.getConnection();
	String routeno=json.getString("r_n");
	String cabno=json.getString("c_n");
	int shiftid=json.getInt("s_i");
	
	System.out.println(routeno);
	System.out.println(cabno);
	System.out.println(shiftid);
	
	try{
		if(cabno==""){
			PreparedStatement p=con.prepareStatement("select cabno from ncab_roster_tbl where Route_No='"+routeno+"'");
			ResultSet rr=p.executeQuery();
			while(rr.next()){
				 cabno=rr.getString(1);
			}
		}
		if(shiftid==0){
			PreparedStatement p=con.prepareStatement("select shift_id from ncab_roster_tbl where Route_No='"+routeno+"'");
			ResultSet rr=p.executeQuery();
			while(rr.next()){
				 shiftid=rr.getInt(1);
			}
		}
	PreparedStatement ps = con.prepareStatement("UPDATE ncab_roster_tbl SET Cab_No='"+cabno+"', Shift_Id='"+shiftid+"' WHERE Route_No ='"+routeno+"'");

	System.out.println("data inserted");
	int i=ps.executeUpdate();
	if(i>0){
		return("success");
	}
	System.out.println("Success mysql table");
} catch (Exception e) {
	// TODO: handle exception
	e.printStackTrace();
}
	return("failure");
}

	
	public JSONArray getcablist(){
		DBConnectionUpd db=new DBConnectionUpd();
		 Connection connection=db.getConnection();
		 JSONArray jsarr=new JSONArray();
		 try {
			PreparedStatement ps=connection.prepareStatement("select distinct Cab_No from ncab_roster_tbl where Roster_Month='MAR' and Roster_Year='2018'");
//				PreparedStatement ps=connection.prepareStatement("select Emp_Qlid from master_employee ");

			 ResultSet rs=ps.executeQuery();
		    while(rs.next()){
		    	String hh=rs.getString(1);
		    	System.out.println(hh);
		    	JSONObject js=new JSONObject();
				PreparedStatement ps1=connection.prepareStatement("select Shift_Id from ncab_roster_tbl where Cab_No='"+rs.getString(1)+"' and Roster_Month='MAR' and Roster_Year='2018'");
				ResultSet rs1=ps1.executeQuery();
				rs1.next();
			    	if((Integer.parseInt(rs1.getString(1))) > 3){
			    		
			    	}
			    	else{
			    	js.put("s_id",rs1.getString(1));
			    	js.put("c_n",hh);
			    	jsarr.put(js);
			    	}
		    	
		    }
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return jsarr;
		 
	}
}
