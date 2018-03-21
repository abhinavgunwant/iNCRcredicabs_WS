package ncab.dao.impl;

import java.sql.Connection;
import java.util.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.sql.Timestamp;
import java.sql.Types;

import java.util.HashMap;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
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
		int count=0;
		RosterModel rm=new RosterModel(); 
		Connection con=db.getConnection();
		String qlid=jsn.getString("qlid");
		String cab_number=jsn.getString("c_n");
		String shift_id=jsn.getString("s_i");
		String emp_name=jsn.getString("e_n");
		System.out.println(shift_id);	
		String current_roster_month="MAR";
		String current_roster_year="2018";
		int rn,shift;
		ResultSet rs1,rs2;
		String qlid_cab="";//cab count
		String query="",subquery1="",subquery2="",subquery3="";
		HashMap<String,String> occu=new HashMap<>();
		HashMap<String,String> occunch=new HashMap<>();
	System.out.println("Start showRosterInfo 2 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
	
		String occu_query="select cab_license_plate_no,cab_capacity from ncab_cab_master_tbl";
		String occ_q="select distinct Cab_No from ncab_roster_tbl where Shift_Id='4' and Roster_Month='"+current_roster_month+"' and Roster_Year='"+current_roster_year+"' and Route_Status='active'";
		try {
			PreparedStatement ps3=con.prepareStatement(occu_query);
			PreparedStatement ps4=con.prepareStatement(occ_q);
			ResultSet rs3=ps3.executeQuery();
			ResultSet rs4=ps4.executeQuery();
			while(rs3.next()){
				occu.put(rs3.getString(1),rs3.getString(2));
				System.out.println("Item added :-  "+rs3.getString(1)+"  "+rs3.getString(2));
			}
			while(rs4.next()){
					occunch.put(rs4.getString(1),"4");
				System.out.println("Item added Un:-  "+rs4.getString(1));
			}
			String pick_qlid="";
			String pick_shift="";
			String pick_cab_number="";
			query=selectFilterQuery(qlid,cab_number,shift_id,emp_name,current_roster_month,current_roster_year);
			qlid_cab="";
			PreparedStatement ps=con.prepareStatement(query);
			ResultSet rs=ps.executeQuery();

			while(rs.next()){
				count++;
				pick_qlid=rs.getString(1);
				pick_shift=rs.getString(2);
				pick_cab_number=rs.getString(3);
				subquery3="select Count(Emp_Qlid) from ncab_roster_tbl where Roster_Month='"+current_roster_month+"' and Shift_Id='"+pick_shift+"' and Cab_No='"+pick_cab_number+"' and Roster_Year='"+current_roster_year+"' and Emp_Status = 'active' ";
				PreparedStatement ps5=con.prepareStatement(subquery3);	
				ResultSet rs5=ps5.executeQuery();
				while(rs5.next()){
				 qlid_cab=rs5.getString(1);	
				}
				JSONObject jsonObj=new JSONObject();
				 
	              subquery1="select * from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"' and Shift_Id='"+pick_shift+"' and Emp_Status = 'active' and Roster_Month='"+current_roster_month+"' and Roster_Year='"+current_roster_year+"' ";
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
		                 rm.setPickup_time(rs1.getString(4));
		                 rm.setVendor_name(rs1.getString(16));
	            	 jsonObj.put("Qlid",rm.getQlid());
	 		         jsonObj.put("Cab_number",rm.getCab_number());
	 		         jsonObj.put("Route_number",rm.getRoot_number());
	 		         jsonObj.put("shift_id", rm.getShift_id());
	 		         jsonObj.put("pickup_time", rm.getPickup_time());
	 		         jsonObj.put("vendor_name",rm.getVendor_name());
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
	              if(Integer.parseInt(rm.getShift_id()) != 4){
						 jsonObj.put("occu_left",( Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
						 System.out.println(" put :- "+(Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));			 
					  }
					  else{
						  jsonObj.put("occu_left",( Integer.parseInt((occunch.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
							 System.out.println(" put :- "+(Integer.parseInt((occunch.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));	
					  }
  	              System.out.println(jsonObj.get("Qlid")+" "+jsonObj.get("Cab_number")+" "+jsonObj.get("Route_number")+" "+jsonObj.get("shift_id"));

	              jsonArr.put(jsonObj);	
	              
			}
			if(count == 0){
     				JSONObject js=new JSONObject();
					js.put("error","no data");
					jsonArr.put(js);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonArr;
		}
	
	
	public static String selectFilterQuery(String emp_qlid,String cab_no,String s_id,String name,String c_r_m,String c_r_y){
		String query="";
		String qlid=emp_qlid;
		String emp_name=name;
		String shift_id=s_id;
		String cab_number=cab_no;
		String current_roster_month=c_r_m;
		String current_roster_year=c_r_y;
		System.out.println("call me");
		if(!(cab_number.equals(""))){
			if(!(emp_name.equals(""))){
				if(!(qlid.equals(""))){
					if(!(shift_id.equals(""))){  // if all fields are given
						query="select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl WHERE Shift_Id='"+shift_id+"' AND Cab_No LIKE '%"+cab_number+"%' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Route_Status='active' AND Emp_Status='active' ORDER BY Route_No";
					}
					else{  // if cab_number, qlid, emp_name are given
						System.out.println(current_roster_month);
						System.out.println(current_roster_year);
						System.out.println(qlid);
						System.out.println(cab_number);
						System.out.println(shift_id);

						query = "SELECT Emp_Qlid, Shift_Id, Cab_No FROM ncab_roster_tbl WHERE Emp_Status = 'active' AND Route_Status = 'active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Cab_No LIKE '%"+cab_number+"%' AND Shift_Id IN (SELECT Shift_Id FROM ncab_roster_tbl WHERE Emp_Qlid = '"+qlid+"' AND Cab_No LIKE '%"+cab_number+"%' AND Emp_Status='active' AND Route_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"') ORDER BY Route_No;";
					}
				}
				else{					
					if(!(shift_id.equals(""))){ //if cab_number, emp_name, shift_id is given
						query="select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl WHERE Shift_Id='"+shift_id+"' AND Cab_No LIKE '%"+cab_number+"%' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Route_Status='active' AND Emp_Status='active' ORDER BY Route_No";

					}
					else{ //if cab_number, emp_name is given
						query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where Emp_Status = 'active' AND Route_Status = 'active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Cab_No LIKE '%"+cab_number+"%' AND Shift_Id IN (select Shift_id from ncab_roster_tbl where Emp_Qlid IN (SELECT Emp_Qlid FROM ncab_master_employee_tbl WHERE (Emp_FName LIKE '%"+emp_name+"%')||(Emp_MName LIKE '%"+emp_name+"%')||(Emp_LName LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_MName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')) AND Emp_Status = 'active' AND Route_Status = 'active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"')";
					}	       
				} 
			}	
			else
			{
				if(!(qlid.equals(""))){
					if(!(shift_id.equals(""))){ //if cab_number, qlid, shift_id is given
						//					 	query="SELECT Emp_Qlid,Shift_Id,Cab_No FROM ncab_roster_tbl WHERE Roster_Month='"+current_roster_month+"' and Roster_Year='"+current_roster_year+"' and Emp_Status = 'active' and Emp_Qlid='"+qlid+"' AND Shift_Id='"+shift_id+"' AND Cab_No='"+cab_number+"' AND Emp_Status='active' AND Route_Status='active'";
						query="select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl WHERE Shift_Id='"+shift_id+"' AND Cab_No LIKE '%"+cab_number+"%' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Route_Status='active' AND Emp_Status='active' ORDER BY Route_No";

					}
					else{ //if cab_number, qlid is given
						query = "SELECT Emp_Qlid, Shift_Id, Cab_No FROM ncab_roster_tbl WHERE Emp_Status = 'active' AND Route_Status = 'active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Cab_No LIKE '%"+cab_number+"%' AND Shift_Id IN (SELECT Shift_Id FROM ncab_roster_tbl WHERE Emp_Qlid = '"+qlid+"' AND Cab_No LIKE '%"+cab_number+"%' AND Emp_Status='active' AND Route_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"') ORDER BY Route_No;";

					}
				}
				else{
					if(!(shift_id.equals(""))){ //if cab_number, shift_id is given
						//						 query="SELECT Emp_Qlid,Shift_Id,Cab_No FROM ncab_roster_tbl WHERE Roster_Month='"+current_roster_month+"' and Roster_Year='"+current_roster_year+"' and Emp_Status = 'active' and Shift_Id='"+shift_id+"' AND Cab_No='"+cab_number+"' AND Route_Status='active'";
						query="select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl WHERE Shift_Id='"+shift_id+"' AND Cab_No LIKE '%"+cab_number+"%' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Route_Status='active' AND Emp_Status='active' ORDER BY Route_No";

					}
					else{ //if cab_number is given
						query="SELECT Emp_Qlid,Shift_Id,Cab_No FROM ncab_roster_tbl WHERE Roster_Month='"+current_roster_month+"' and Roster_Year='"+current_roster_year+"' and Emp_Status = 'active' and Cab_No LIKE '%"+cab_number+"%'";	 
					}					
				}
			}
		}
		else{
			if(!(emp_name.equals(""))){
				if(!(qlid.equals(""))){
					if(!(shift_id.equals(""))){ //if emp_name, qlid, shift_id is given 
						query="select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl where Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Cab_No in (select Cab_No from ncab_roster_tbl WHERE Shift_Id='"+shift_id+"' AND Emp_Qlid LIKE '"+qlid+"' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Route_Status='active' AND Emp_Status='active')";
					}
					else{ //if emp_name, qlid is given 
						query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where (Cab_No, Shift_Id) In (Select Cab_No, Shift_Id from ncab_roster_tbl where Emp_Qlid = '"+qlid+"' and Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"') and Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' ";

					}
				}
				else{					
					if(!(shift_id.equals(""))){ //if emp_name, shift_id is given 
						query="select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Shift_Id = '"+shift_id+"' AND Cab_No IN (select Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' and Emp_Qlid IN (SELECT Emp_Qlid FROM ncab_master_employee_tbl WHERE (Emp_FName LIKE '%"+emp_name+"%')||(Emp_MName LIKE '%"+emp_name+"%')||(Emp_LName LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_MName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')))";
					}
					else{ 
						//if emp_name is given
						query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where (Cab_No, Shift_Id) In (Select Cab_No, Shift_Id from ncab_roster_tbl where Emp_Qlid IN (SELECT Emp_Qlid FROM ncab_master_employee_tbl WHERE (Emp_FName LIKE '%"+emp_name+"%')||(Emp_MName LIKE '%"+emp_name+"%')||(Emp_LName LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_MName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')||(CONCAT(Emp_FName,' ',Emp_LName,' ') LIKE '%"+emp_name+"%')) and Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"') and Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' ";
					}
				}	       
			} 
			else
			{
				if(!(qlid.equals(""))){ 
					if(!(shift_id.equals(""))){ //if  qlid, shift_id is given 
						query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Shift_Id = '"+shift_id+"' and Cab_No IN (select Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' and Emp_Qlid = '"+qlid+"')";
					}
					else{ 
						//if  qlid is given
						query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where (Cab_No, Shift_Id) In (Select Cab_No, Shift_Id from ncab_roster_tbl where Emp_Qlid = '"+qlid+"' and Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"') and Route_Status = 'active' and Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' ";
					}
				}
				else{
					if(!(shift_id.equals(""))){ 
						// if shift_id is given
						System.out.println(shift_id);
						query="select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl where Emp_Status='active' AND Shift_Id = '"+shift_id+"' and Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Cab_No IN (select Cab_No from ncab_roster_tbl where Shift_Id='"+shift_id+"')";

					}
					else{ 
						// if all fields are empty
						long millis=System.currentTimeMillis();  
						java.sql.Date date=new java.sql.Date(millis);  
						System.out.println("Date: "+date); 
						
						
						
						System.out.println("all filter fields are empty");
						query="select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl where Emp_Status='active' AND Roster_Month='"+current_roster_month+"' AND Roster_Year='"+current_roster_year+"' AND Route_Status='active'";
					}
				}
			} 
		}

		return query;
	}
	
	
	public JSONArray insertIntoDB(InputStream fileInputStream, FormDataContentDisposition fileFormDataContentDisposition)
			throws IOException {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONObject jsobj=new JSONObject();
		JSONArray jsarr=new JSONArray();
		RowCheck rowcheck = new RowCheck();
		FileWriter f0 = new FileWriter("C:\\Users\\DB250491\\Desktop\\output.txt");
		String[] route_no_arr = null;
		String[] empid_arr = null;
		String[] cab_arr = null;
		String route_no = null;
		int i = 0, last_row_valid = 0, index = 1;
		int ct = 0;
		// PreparedStatement counter = connection.prepareStatement("select
		// max(Route_No) from ncab_roster_tbl;");
		PreparedStatement psc;
		try {
			psc = connection.prepareStatement("select max(Route_No) from ncab_roster_tbl;");
			ResultSet rscounter = psc.executeQuery();
			rscounter.next();
			if (rscounter.getString(1) == null)
				ct = 0;
			else
				ct = Integer.parseInt(rscounter.getString(1));
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		;
		HashMap<String, String> sr = null; // shift id and route number link
		HashMap<String, HashMap<String, String>> hm = new HashMap<String, HashMap<String, String>>(); 
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(
					"select Cab_No,Shift_Id,Route_No from ncab_roster_tbl where Shift_Id <> 4 order by Route_No");
			ResultSet rs = ps.executeQuery();
			int ct1 = 0;
			while (rs.next()) {
				String cabno = rs.getString(1);
				String sid = rs.getString(2);
				String rn = rs.getString(3);
				ct1++;
				System.out.println(ct1 + ": cabNO: " + cabno + "  " + " sid: " + sid + " route: " + rn);
				if (hm.get(cabno) == null) {
					hm.put(cabno, new HashMap<String, String>());
					hm.get(cabno).put(sid, rn);
				} else if (hm.get(cabno).get(sid) == null)
					hm.get(cabno).put(sid, rn);

			}
			System.out.println("Initial hashmap: " + hm);
			// hash map is ready
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
				String empid = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				empid_arr[i - 1] = empid;
				System.out.println("id: " + empid);
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

				// instead of roster month and year, accept dates

				String roster_month = row.getCell(14).getStringCellValue();
				System.out.println("Roster Month " + roster_month);

				int roster_yr = (int) row.getCell(15).getNumericCellValue();
				String roster_year = Integer.toString(roster_yr);
				System.out.println("Roster Year " + roster_year);

				String remarks = row.getCell(7).getStringCellValue();
				System.out.println("Remarks are " + remarks);

				String Route_No = "";

				String cabno;
				PreparedStatement cabno_pre = connection.prepareStatement(
						"select COUNT(cab_license_plate_no) from ncab_cab_master_tbl where cab_license_plate_no = '"
								+ cab_from_excel + "'");
				ResultSet res_cab = cabno_pre.executeQuery();
				res_cab.next();
				String cabno_flag = res_cab.getString(1);

				if (cabno_flag.equals("0"))
					cabno = "invalid_cab";
				else
					cabno = cab_from_excel;

				if (cabno.equals("invalid_cab"))
					Route_No = "Errorofcab";

				else if (hm.get(cabno) == null || (hm.get(cabno) != null && hm.get(cabno).get(shift_id) == null)) {
					ct++;
					Route_No = String.format("%03d", ct);
					// change the code for this as per the above hashmap data
					// filling
					if (hm.get(cabno) == null)
						sr = new HashMap<String, String>();
					sr.put(shift_id, Route_No);
					hm.put(cabno, sr);
				} else if (hm.get(cabno) != null && hm.get(cabno).get(shift_id) != null)
					Route_No = hm.get(cabno).get(shift_id);

				System.out.println("Big hm: " + hm);
				
				String rn_from_excel = row.getCell(8, MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				if (!rn_from_excel.equals("") && !rn_from_excel.equals(Route_No)) {
					System.out.println("Error in route number");/// wrong route
												/// number from
					Route_No = "Errorofcab";											/// excel
				}
				System.out.println("Import rows " + i);
				connection.setAutoCommit(false);

				if (rn_from_excel.equals(Route_No)) // undo this update if the
													// insert is a failure
					try {
						PreparedStatement update = connection.prepareStatement(
								"update ncab_roster_tbl set Emp_Status = 'inactive' where Emp_Qlid = '" + empid
										+ "' and Route_No = '" + rn_from_excel + "';");
						update.executeUpdate();
					} catch (Exception e) {
						System.out.println(e);
					}

				// function call for insertion

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
				System.out.println(retValue + "Point");
				String[] flag = { "FAILURE", "NO", "NO", "NO", "No","No" };
				String[] quote = { "FAILURE", "QLID", "Shift_Timing", "Cab_No", "Route No has no Vacancy","Route Number doesn't match" };
				String[] retValue_token = retValue.split("\\s+");
				String final_push = "";
				System.out.println(retValue_token[0] + "SagaCheck");
				if (retValue_token[0].compareTo("FAILURE") == 0) {

					try {
						PreparedStatement update = connection
								.prepareStatement("update ncab_roster_tbl set Emp_Status = 'active' where Emp_Qlid = '"
										+ empid + "' and Route_No = '" + rn_from_excel + "';");
						update.executeUpdate();
						System.out.println("Update undone");
					} catch (Exception e) {
						System.out.println(e);
					}
					PreparedStatement occupancy = connection.prepareStatement(
							"select cab_capacity from ncab_cab_master_tbl where cab_license_plate_no = '" + cabno
									+ "';");
					ResultSet occupancy_no = occupancy.executeQuery();
					// ResultSet will be returning null if Validation fails onto
					// given Cab_No.
					occupancy_no.next();
					String occ_no = occupancy_no.getString(1);
					PreparedStatement vacancy = connection
							.prepareStatement("select COUNT(Emp_Qlid) from ncab_roster_tbl where Cab_No = '" + cabno
									+ "' and Shift_Id = '" + shift_id
									+ "' and Emp_Status = 'active' and Roster_Month='MAR' and Roster_Year = '2018'");
					ResultSet vacancy_num = vacancy.executeQuery();
					vacancy_num.next();
					String vacan_num = vacancy_num.getString(1);
					int idiot = Integer.parseInt(occ_no) - Integer.parseInt(vacan_num);
					System.out.println("This is the idiot" + idiot);
					String idiot_value = Integer.toString(idiot);
					System.out.println("occu: " + occ_no);
					System.out.println("vacancy: " + vacan_num);

					if (occ_no.compareTo(idiot_value) == 0) {
						System.out.println("Before Inside error:" + hm);

						if (hm.get(cabno).size() == 1)
							hm.remove(cabno);
						else
							hm.get(cabno).remove(shift_id);
						System.out.println("After Inside error:" + hm);
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
					if(retValue_token[5].compareTo("-1")==0){
						System.out.println("Route number mismatch");
						flag[5] = "Yes";
					}
					int counter = 0;
					for (int y = 1; y < 6; y++) {
						if (flag[y].compareTo("Yes") == 0) {
							final_push = final_push.concat(quote[y]) + " ";
							counter++;
						}
					}

					System.out.println(final_push);
					empid_arr[i - 1] = "Error";
					cab_arr[i - 1] = "Error";
					jsobj.put("tr",i-1 );
					jsobj.put("eo", counter);
					jsarr.put(jsobj);
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
//			UtilServiceImpl usi=new UtilServiceImpl();
//			String from="deepakbisht55979@gmail.com";
//			String recepient1="db250491@ncr.com";
//			String recepient2="dp250369@ncr.com";
//			String recepient3="am250914@ncr.com";
//			String subject="Roster Error Log";
			// Create the message part
//	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Now set the actual message
//	         messageBodyPart.setText("This is message body");

	         // Create a multipar message
//	         Multipart multipart = new MimeMultipart();

	         // Set text message part
//	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
//	         messageBodyPart = new MimeBodyPart();
//	         String filename = "/home/manisha/file.txt";
//	         FileDataSource source = new FileDataSource("C:\\Users\\DB250491\\Desktop\\output.txt");
//	         messageBodyPart.setDataHandler(new DataHandler(source));
//	         messageBodyPart.setFileName("C:\\Users\\DB250491\\Desktop\\output.txt");
//	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
//	         message.setContent(multipart);
//			Path fileLocation = Paths.get("C:\\Users\\DB250491\\Desktop\\output.txt");
//			String messageAttribute=Files.readAllBytes(fileLocation).toString();
//			usi.sendEmailMessage(from, recepient1, recepient2, recepient3, subject, multipart.toString());

     
			// System.out.println("Success import excel to mysql table");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("Jsarr :- " +jsarr);
		return jsarr;
	}
	
	

	public JSONObject addEmpToDb(JSONObject json){
		JSONObject js=new JSONObject();
		try {  
			DBConnectionUpd db=new DBConnectionUpd();
			Connection con=db.getConnection();
			String cab=json.getString("c_n");
			String qlid=json.getString("qlid");
			String sid=json.getString("s_i");
			System.out.println("The sid is "+sid);
			
			long millis=System.currentTimeMillis();  
			java.sql.Date startdate=new java.sql.Date(millis);  
			System.out.println("Start Date: "+startdate); 
			
			int count=0;
			String enddate = null;
			//String pick=json.getString("p_time");
			System.out.println(cab+"   "+qlid);
			String r_n="",shift_time="",month="",year="",status="active";
			PreparedStatement ps1=con.prepareStatement("select Route_No,Roster_Month,Roster_Year,End_Date from ncab_roster_tbl where Cab_No=? and Shift_Id=?");
			ps1.setString(1, cab);
			ps1.setString(2, sid);
			ResultSet rs2=ps1.executeQuery();
			while(rs2.next()){
				r_n=rs2.getString(1);
				//  shift_time=rs2.getString(2);
				month=rs2.getString(2);
				year=rs2.getString(3);
				enddate = rs2.getString(4);
				//				PreparedStatement ps2=con.prepareStatement("insert into roster_tbl(Route_No,Emp_Qlid,Shift_Time,Pickup_time,Cab_No) values(?,?,?,?,?)");
				PreparedStatement ps2=con.prepareStatement("insert into ncab_roster_tbl(Route_No,Emp_Qlid,Shift_Id,Cab_No,Roster_Month,Roster_Year,Emp_Status,Start_Date,End_Date) values(?,?,?,?,?,?,?,?,?)");

				ps2.setString(1, r_n);
				ps2.setString(2, qlid);
				ps2.setString(3, sid);
				ps2.setString(4, cab);
				ps2.setString(5, month);
				ps2.setString(6, year);
				ps2.setString(7, status);
				ps2.setString(8, startdate.toString());
				ps2.setString(9, enddate);
				
				int i=ps2.executeUpdate();
				if(i>1){
					js.put("msg", "success");
					System.out.println("success in inserting");
				}
				else{
					js.put("msg", "fail");
					System.out.println("fail in inserting");

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
    
    public JSONArray getDriver(){
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarr = new JSONArray();
		String name = "",ph="";
		try {
			System.out.println("Inside try before query");
			PreparedStatement ps = connection.prepareStatement("select driver_name , d_contact_num from ncab_driver_master_tbl");
			ResultSet rs = ps.executeQuery();
			System.out.println("Inside try after query");
			while (rs.next()) {
				JSONObject json = new JSONObject();
				name = rs.getString(1);
				ph=rs.getString(2);
				json.put("name", name);
				json.put("ph", ph);
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
    
public int empdeact(JSONObject json) {
		int flag = 0;
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		String qlid = json.getString("qlid");
		String month = json.getString("startdate");
		
		try {
			PreparedStatement ps = connection.prepareStatement(
					"Update ncab_roster_tbl set Emp_Status='Inactive' where Emp_Qlid=? and  ? between start_date and end_date");
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
public int setNewRouteSCH(JSONArray jsonarr) {
		int flag = 0;
		DBConnectionUpd db = new DBConnectionUpd();
		Connection connection = db.getConnection();
		String qlid, guard, picktime, cabno,start, end, vendor, Status = "Active";
		int shiftid = 0,driver=0;
		try {
			String routeno = "";
			int route = 0,cost=0;
			PreparedStatement ps = connection.prepareStatement("select Max(Route_No) from ncab_roster_tbl");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				routeno = rs.getString(1);
			}
			route = Integer.parseInt(routeno);
		
			route++;
			JSONObject jj=jsonarr.getJSONObject(0);
			String drivername=jj.optString("dname");
			String driverph=jj.optString("dph");
			PreparedStatement ps2 = connection.prepareStatement("select driver_id from ncab_driver_master_tbl where driver_name = ? and d_contact_num = ?");
			ps2.setString(1,drivername);
			ps2.setString(2, driverph);
			System.out.println("Driver name:"+drivername+driverph);
			ResultSet rs2 = ps2.executeQuery();
			while(rs2.next()){
				driver=rs2.getInt(1);
			}
			System.out.println("Driver id:"+driver);

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
				picktime = json.optString("picktime");
				cabno = json.optString("cabno");
				start = json.optString("start");
				end = json.optString("end");
				cost = json.optInt("cost");
				vendor = json.optString("vendor");
				
				System.out.println("Object Created" + qlid);
				System.out.println("----Query ready" + qlid);
				PreparedStatement ps1 = connection.prepareStatement(
						"insert into ncab_roster_tbl (Route_no,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Start_Date,End_Date,Vendor_Id,Route_Status,Emp_Status,Cab_Cost,Driver_Id) values (?,?,?,?,?,?,?,?,?,?,?,?,?)");

				ps1.setString(1, routeno);
				ps1.setString(2, qlid);
				ps1.setInt(3, shiftid);
				ps1.setString(4, picktime);
				ps1.setString(5, cabno);
				ps1.setString(6, guard);
				ps1.setString(7, start);
				ps1.setString(8, end);
				ps1.setString(9, vendor);
				ps1.setString(10, Status);
				ps1.setString(11, Status);
				ps1.setInt(12, cost);
				ps1.setInt(13, driver);
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
		String qlid, guard, picktime, cabno, start, end, vendor, Status = "Active",pickup="nan",drop="nan";
		int shiftid = 4, cost;
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
				pickup=json.optString("pickup");
				drop=json.optString("drop");
				picktime = json.optString("picktime");
				cabno = json.optString("cabno");
				start = json.optString("start");
				end = json.optString("end");
				vendor = json.optString("vendor");
				cost = json.optInt("cost");
				System.out.println("Object Created" + qlid);
				System.out.println("----Query ready" + qlid);
				PreparedStatement ps1 = connection.prepareStatement(
						"insert into ncab_roster_tbl (Route_no,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Start_Date,End_Date,Vendor_Id,Emp_Status,Cab_Cost,Route_Status,Drop_type,Pickup_Area) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				ps1.setString(1, routeno);
				ps1.setString(2, qlid);
				ps1.setInt(3, shiftid);
				ps1.setString(4, picktime);
				ps1.setString(5, cabno);
				ps1.setString(6, guard);
				ps1.setString(7, start);
				ps1.setString(8, end);
				ps1.setString(9, vendor);
				ps1.setString(10, Status);
				ps1.setInt(11, cost);
				ps1.setString(12, Status);
				ps1.setString(14, pickup);
				ps1.setString(13, drop);
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



//saurav
	

	public String sauravkaeditmethod(JSONObject obj) {
	
		try {
			System.out.println("edit json:" + obj);
			String cab_and_shift = obj.getString("cabno");
			String arr[] = cab_and_shift.split(" ");
			String cabno = arr[0];
			String sid = arr[1];
			System.out.println("CAB NUMBER: "+cabno);
			System.out.println("SHIFT ID: "+sid);
			
			String picktime = obj.getString("picktime");
//			String shifttime="";
			String qlid = obj.getString("qlid");
			String sdate = obj.getString("sdate");
			String edate = obj.getString("edate");

			System.out.println(cabno);
			System.out.println(picktime);
			System.out.println(qlid);
			System.out.println(sdate);
			System.out.println(edate);
			System.out.println(sdate.length());
			/*System.out.println(edate.length());
			String sd=sdate.substring(8);
			System.out.println(sd);
			String ed=edate.substring(8);
			System.out.println(ed);
*/


			DBConnectionUpd db=new DBConnectionUpd();
			Connection con=db.getConnection();
			String v_n="";
			String r_n="";
			String query2="select Route_No from ncab_roster_tbl where Cab_No='"+cabno+"' and Shift_Id = '"+sid+"' and Roster_Month='Mar' and Roster_Year='2018' and Emp_Status='active' and Route_Status = 'active'";
			PreparedStatement ps2=con.prepareStatement(query2);
			ResultSet rs=ps2.executeQuery();
			rs.next();
			r_n = rs.getString(1);
			
			String query4="update ncab_roster_tbl set Emp_Status='inactive' where Emp_Qlid='"+qlid+"' and Roster_Month='Mar' and Roster_Year='2018'";
			PreparedStatement ps4=con.prepareStatement(query4);
			ps4.executeUpdate();
			System.out.println("RN: "+r_n);
			System.out.println("qlid: "+qlid);
			System.out.println("Cab_No: "+cabno);
			System.out.println("PT: "+picktime);
			System.out.println("SD: "+sdate);
			System.out.println("ED: "+edate);
			String query1="insert into ncab_roster_tbl (Route_No,Emp_Qlid,Cab_No,Pickup_Time,Shift_Id,Start_Date,End_Date,Roster_Month,Roster_Year) values(?,?,?,?,?,?,?,?,?) ";
			
			PreparedStatement ps=con.prepareStatement(query1);
			
			ps.setString(1, r_n);
			ps.setString(2, qlid);
			ps.setString(3, cabno);
			ps.setString(4, picktime);
			ps.setString(5, sid);
			ps.setString(6, sdate);
			ps.setString(7, edate);
			ps.setString(8, "Mar");
			ps.setString(9, "2018");

			ps.executeUpdate();
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
	
	public JSONArray getAllVendor()
	{
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jarr = new JSONArray();
		
		try{
			PreparedStatement ps = connection.prepareStatement("select vendor_name from ncab_vendor_master_tbl") ;
			
			ResultSet rs= ps.executeQuery();
			while(rs.next()){
				JSONObject json=new JSONObject();
				json.put("ven", rs.getString(1));
			
				jarr.put(json);
			}
			
		}
		catch(Exception e){
			System.out.println("error in imp :- "+e.getMessage());
		}
		return jarr;
	}
	
	
	
	public JSONArray getAllCab()
	{
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jarr = new JSONArray();
		
		try{
			PreparedStatement ps = connection.prepareStatement("select cab_license_plate_no from ncab_cab_master_tbl") ;
			
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
	String vendor=json.getString("ven");
	int s1=0;
	System.out.println(routeno);
	System.out.println(cabno);
	System.out.println(shiftid);
	
	try{
		if(vendor==""){
			PreparedStatement p=con.prepareStatement("select Vendor_Name from ncab_roster_tbl where Route_No='"+routeno+"'");
			ResultSet rr=p.executeQuery();
			while(rr.next()){
				 vendor=rr.getString(1);
			}
		}
		if(cabno==""){
			PreparedStatement p=con.prepareStatement("select Cab_No from ncab_roster_tbl where Route_No='"+routeno+"'");
			ResultSet rr=p.executeQuery();
			while(rr.next()){
				 cabno=rr.getString(1);
			}
		}
		if(shiftid==0){
			PreparedStatement p=con.prepareStatement("select Shift_Id from ncab_roster_tbl where Route_No='"+routeno+"'");
			ResultSet rr=p.executeQuery();
			while(rr.next()){
				 shiftid=rr.getInt(1);
			}
		}
	PreparedStatement ps = con.prepareStatement("UPDATE ncab_roster_tbl SET Route_Status='inactive' WHERE Route_No ='"+routeno+"'");
	int i=ps.executeUpdate();
	PreparedStatement pss=con.prepareStatement("select Route_No,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Roster_Month,Roster_Year,Cab_Cost,Vendor_Name,Start_Date,End_Date from ncab_roster_tbl WHERE Route_No ='"+routeno+"'");
	ResultSet rs=pss.executeQuery();
    while(rs.next()){
    	PreparedStatement fs=con.prepareStatement("insert into ncab_roster_tbl(Route_No,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Roster_Month,Roster_Year,Cab_Cost,Vendor_Name,Start_Date,End_Date) values(?,?,?,?,?,?,?,?,?,?,?)");
    	fs.setString(1,routeno);
    	System.out.println("The employee id is"+rs.getString(2));
    	fs.setString(2,rs.getString(2));
    	fs.setInt(3,shiftid);
    	fs.setString(4,rs.getString(4));
    	fs.setString(5,cabno);
    	fs.setString(6,rs.getString(6));
    	fs.setString(7,rs.getString(7));
    	fs.setInt(8,rs.getInt(9));
    	fs.setString(9,vendor);
    	fs.setString(10,rs.getString(10) );
    	fs.setString(11,rs.getString(11));
    	s1=fs.executeUpdate();
    	System.out.println("data inserted");
    }
	
	
	
	if(s1>0){
		return("success");
	}
	System.out.println("Success mysql table");
} catch (Exception e) {
	// TODO: handle exception
	e.printStackTrace();
}
	return("failure");
}


	
	public JSONArray getcablist(String s){
		DBConnectionUpd db=new DBConnectionUpd();
		Connection connection=db.getConnection();
		JSONArray jsarr=new JSONArray();
		try {
			JSONObject json = new JSONObject(s);
//			String cabno = json.getString("cabno");
			String sid = json.getString("shiftid");
			
			PreparedStatement ps=connection.prepareStatement("select distinct Cab_No,Shift_Id from ncab_roster_tbl where Shift_Id <> '"+sid+"' and Shift_Id <> 4 and Roster_Month='MAR' and Roster_Year='2018' order by Shift_Id");
			//				PreparedStatement ps=connection.prepareStatement("select Emp_Qlid from master_employee ");

			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				PreparedStatement emp_ct1 = connection.prepareStatement("select count(Emp_Qlid) from ncab_roster_tbl where Cab_No = '"+rs.getString(1)+"' and Shift_Id = '"+rs.getString(2)+"' and Emp_Status = 'active' and Route_Status = 'active'");
				ResultSet ctrs1 = emp_ct1.executeQuery();
				ctrs1.next();
				
				PreparedStatement emp_ct2 = connection.prepareStatement("select cab_capacity from ncab_cab_master_tbl where cab_license_plate_no='"+rs.getString(1)+"'");
				ResultSet ctrs2 = emp_ct2.executeQuery();
				ctrs2.next();
				int vacancy = Integer.parseInt(ctrs2.getString(1)) - Integer.parseInt(ctrs1.getString(1));
				if(vacancy >= 1)
				{
					JSONObject js=new JSONObject();
					js.put("s_id",rs.getString(2));
					js.put("c_n",rs.getString(1));
					jsarr.put(js);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsarr;
		 
	}
	
	
	
	public JSONArray getVendorForFilter(){
		DBConnectionUpd db=new DBConnectionUpd();
		Connection connection=db.getConnection();
		JSONArray jsarr=new JSONArray();
		try {
			PreparedStatement ps=connection.prepareStatement("select distinct vendor_name from ncab_vendor_master_tbl");
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				JSONObject js=new JSONObject();
					js.put("v_n",rs.getString(1));
					jsarr.put(js);
				}
			System.out.println(jsarr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsarr;
		 
	}
	
	
	public JSONArray getRouteDatas(JSONObject js) {
		// TODO Auto-generated method stub
		DBConnectionUpd db=new DBConnectionUpd();
		Connection con=db.getConnection();
		JSONArray jsarr=new JSONArray();
		JSONObject jo=new JSONObject();
		String qli="";
		String driverid="";
		String vendorid="",fname="",lname="",mgr="",drivern="",drivercont="",managername="",managername1="";
		
		
		try {
			
			
			String cabno=js.getString("c_n");
			String picktime=js.getString("s_n");
			//int shifttime=Integer.parseInt(obj.getString("shiftime"));

			System.out.println(cabno);
			System.out.println(picktime);
			
	
			String query6="select * from ncab_roster_tbl where Cab_No='"+cabno+"' and Shift_Id='"+picktime+"' and Roster_Month='MAR' and Roster_Year='2018' and Emp_Status='active'";
			PreparedStatement ps7=con.prepareStatement(query6);
			ResultSet rs6=ps7.executeQuery();
			while(rs6.next()){
			
//				jo.put("qlid", rs6.getString(2));
//				jo.put("did", rs6.getString(19));
//				jo.put("vid", rs6.getString(16));
			
				driverid=rs6.getString(19);
				
				 qli=rs6.getString(2);
				 System.out.println("This is the qlid in the cab " +qli);
			
				String query8="select Emp_FName,Emp_LName,Emp_Mgr_Qlid1 from ncab_master_employee_tbl where Emp_Qlid='"+qli+"'";
				
				PreparedStatement ps9=con.prepareStatement(query8);
				ResultSet rs9=ps9.executeQuery();
				while(rs9.next()){
						jo=new JSONObject();
//						jo.put("name1", rs9.getString(1));
//						jo.put("name2", rs9.getString(2));
//						jo.put("mgr",   rs9.getString(3));
						fname=rs9.getString(1);
						lname=rs9.getString(2);
						mgr=rs9.getString(3);
					
				
				}
				String query11="select Emp_FName,Emp_LName from ncab_master_employee_tbl where Emp_Qlid='"+mgr+"'";
				PreparedStatement ps11=con.prepareStatement(query11);
				ResultSet rs11=ps11.executeQuery();
				while(rs11.next()){
					jo=new JSONObject();
					managername=rs11.getString(1);
					managername1=rs11.getString(2);
				}
				
			
				
				String query7="select * from ncab_driver_master_tbl where driver_id='"+driverid+"'";
				PreparedStatement ps8=con.prepareStatement(query7);
				ResultSet rs8=ps8.executeQuery();
				
				while(rs8.next()){
				
//						jo.put("dname", rs8.getString(2));
//						jo.put("dcont", rs8.getString(3));
						drivern=rs8.getString(2);
						drivercont=rs8.getString(3);
					
				}
//				jsarr.put(jo);
				jo.put("qd", qli);
				jo.put("did",driverid);
			//	jo.put("vid", rs6.getString(16));
				jo.put("name1", fname);
				jo.put("name2", lname);
				jo.put("mgr",  mgr);
				jo.put("dname", drivern);
				jo.put("dcont", drivercont);
				jo.put("managr", mgr);
				jo.put("mname1",managername);
				jo.put("mname2", managername1);
			
				jsarr.put(jo);
			
			
			
			
			
		}
			System.out.println("This is jsarray" +jsarr);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		return jsarr;



		// TODO Auto-generated method stub
		

	
	}
	
	public String writeExcel(String s) {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		RosterServiceImpl rsi=new RosterServiceImpl();
		JSONObject jsobj=null;
		try {
			jsobj = new JSONObject(s);
			JSONArray jsarr=rsi.showRosterInfo(jsobj);

			  HSSFWorkbook hssfWorkbook = null;
			  HSSFRow row = null;
			  HSSFSheet hssfSheet = null;
			  FileOutputStream fileOutputStream = null;
			  Properties properties = null;
			   String filename = "C:/roster.xls";
			   hssfWorkbook = new HSSFWorkbook();
			   hssfSheet = hssfWorkbook.createSheet("new sheet");

			   HSSFRow rowhead = hssfSheet.createRow(0); // Header
			   
			 		
			   rowhead.createCell(0).setCellValue("Route No");	
			   rowhead.createCell(1).setCellValue("Qlid");	
//			   rowhead.createCell(2).setCellValue("Car Model");	
			   rowhead.createCell(2).setCellValue("Employee Name");
			   rowhead.createCell(3).setCellValue("Shift Id");	
//			   rowhead.createCell(5).setCellValue("Drop At");	
			   rowhead.createCell(4).setCellValue("Pick-up Area");	
//			   rowhead.createCell(5).setCellValue("Pick Time");
//			   rowhead.createCell(8).setCellValue("Driver Number");	
			   rowhead.createCell(5).setCellValue("Cab Number");	
//			   rowhead.createCell(7).setCellValue("Vendor Name");	
//			   rowhead.createCell(11).setCellValue("Roster Month");
//			   rowhead.createCell(12).setCellValue("Roster year");
//			   rowhead.createCell(13).setCellValue("Guard Needed");
//			   rowhead.createCell(14).setCellValue("Route Start Date");
//			   rowhead.createCell(15).setCellValue("Route End Date");
          
		  for(int i=0;i<jsarr.length();i++){
			  JSONObject jsonObject1 = jsarr.getJSONObject(i);
			  row = hssfSheet.createRow((short) i+1);
			    row.createCell((short) 0).setCellValue(Integer.parseInt(jsonObject1.getString("Route_number")));
			    row.createCell((short) 1).setCellValue(jsonObject1.getString("Qlid"));
			    String mname="";
				    row.createCell((short) 2).setCellValue(jsonObject1.getString("f_name")+" "+jsonObject1.getString("l_name") );

			    
			    row.createCell((short) 3).setCellValue(Integer.parseInt(jsonObject1.getString("shift_id")));
			    row.createCell((short) 4).setCellValue(jsonObject1.getString("p_a"));
//			    row.createCell((short) 5).setCellValue(jsonObject1.getString("pickup_time"));
			    row.createCell((short) 5).setCellValue(jsonObject1.getString("Cab_number"));
//			    row.createCell((short) 7).setCellValue(jsonObject1.getString("vendor_name"));

		  }

			  
		/*	   for (RosterModel rostermodel : jsonInfo.getRecords()) {
			    properties = programInfo.getProperties();
			    row = hssfSheet.createRow((short) counter);
			    row.createCell((short) 0).setCellValue(counter);
			    row.createCell((short) 1).setCellValue(
			      properties.getRegisteredForActualService());
			    row.createCell((short) 2).setCellValue(
			      properties.getEmsCreationTime());
			    row.createCell((short) 3).setCellValue(
			      properties.getRequestsOffering());
			    row.createCell((short) 4).setCellValue(
			      programInfo.getRecord_type());
			    row.createCell((short) 5).setCellValue("");// This is blank
			               // object without
			               // any property
			    counter++;
			   }
			   counter += 5;
			   row = hssfSheet.createRow((short) counter);
			   row.createCell((short) 0).setCellValue("Completion Status : "+
			     jsonInfo.getMeta().getCompletion_status());
			   row.createCell((short) 1).setCellValue("Total Count : "+
			     jsonInfo.getMeta().getTotal_count());
	 */
			   fileOutputStream = new FileOutputStream(filename);
			   hssfWorkbook.write(fileOutputStream);
			   fileOutputStream.close();
			  
			   System.out.println("JSON data successfully exported to excel!");
			  } catch (Throwable throwable) {
			   System.out.println("Exception in writting data to excel : "
			     + throwable);
			  }
			  return "success";
			 }


//download data
	
	@SuppressWarnings("unused")
	public JSONArray download_data(JSONObject jsn){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Start showRosterInfo :: "+ new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
		JSONArray jsonArr =new JSONArray();
		DBConnectionUpd db=new DBConnectionUpd();
		int count=0;
		RosterModel rm=new RosterModel(); 
		Connection con=db.getConnection();
		String qlid=jsn.getString("qlid");
		String cab_number=jsn.getString("c_n");
		String shift_id=jsn.getString("s_i");
		String emp_name=jsn.getString("e_n");
		System.out.println(shift_id);	
		String current_roster_month="MAR";
		String current_roster_year="2018";
		int rn,shift;
		ResultSet rs1,rs2;
		String qlid_cab="";//cab count
		String query="",subquery1="",subquery2="",subquery3="";
		HashMap<String,String> occu=new HashMap<>();
		HashMap<String,String> occunch=new HashMap<>();
	System.out.println("Start showRosterInfo 2 :: "+new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
	
		String occu_query="select cab_license_plate_no,cab_capacity from ncab_cab_master_tbl";
		String occ_q="select distinct Cab_No from ncab_roster_tbl where Shift_Id='4' and Roster_Month='"+current_roster_month+"' and Roster_Year='"+current_roster_year+"' and Route_Status='active'";
		try {
			PreparedStatement ps3=con.prepareStatement(occu_query);
			PreparedStatement ps4=con.prepareStatement(occ_q);
			ResultSet rs3=ps3.executeQuery();
			ResultSet rs4=ps4.executeQuery();
			while(rs3.next()){
				occu.put(rs3.getString(1),rs3.getString(2));
				System.out.println("Item added :-  "+rs3.getString(1)+"  "+rs3.getString(2));
			}
			while(rs4.next()){
					occunch.put(rs4.getString(1),"4");
				System.out.println("Item added Un:-  "+rs4.getString(1));
			}
			String pick_qlid="";
			String pick_shift="";
			String pick_cab_number="";
			query=selectFilterQuery(qlid,cab_number,shift_id,emp_name,current_roster_month,current_roster_year);
			qlid_cab="";
			PreparedStatement ps=con.prepareStatement(query);
			ResultSet rs=ps.executeQuery();

			while(rs.next()){
				count++;
				pick_qlid=rs.getString(1);
				pick_shift=rs.getString(2);
				pick_cab_number=rs.getString(3);
				subquery3="select Count(Emp_Qlid) from ncab_roster_tbl where Roster_Month='"+current_roster_month+"' and Shift_Id='"+pick_shift+"' and Cab_No='"+pick_cab_number+"' and Roster_Year='"+current_roster_year+"' and Emp_Status = 'active' ";
				PreparedStatement ps5=con.prepareStatement(subquery3);	
				ResultSet rs5=ps5.executeQuery();
				while(rs5.next()){
				 qlid_cab=rs5.getString(1);	
				}
				JSONObject jsonObj=new JSONObject();
				 
	              subquery1="select * from ncab_roster_tbl where Emp_Qlid='"+pick_qlid+"' and Shift_Id='"+pick_shift+"' and Emp_Status = 'active' and Roster_Month='"+current_roster_month+"' and Roster_Year='"+current_roster_year+"' ";
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
		                 rm.setPickup_time(rs1.getString(4));
		                 rm.setVendor_name(rs1.getString(16));
	            	 jsonObj.put("Qlid",rm.getQlid());
	 		         jsonObj.put("Cab_number",rm.getCab_number());
	 		         jsonObj.put("Route_number",rm.getRoot_number());
	 		         jsonObj.put("shift_id", rm.getShift_id());
	 		         jsonObj.put("pickup_time", rm.getPickup_time());
	 		         jsonObj.put("vendor_name",rm.getVendor_name());
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
	              if(Integer.parseInt(rm.getShift_id()) != 4){
						 jsonObj.put("occu_left",( Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
						 System.out.println(" put :- "+(Integer.parseInt((occu.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));			 
					  }
					  else{
						  jsonObj.put("occu_left",( Integer.parseInt((occunch.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));
							 System.out.println(" put :- "+(Integer.parseInt((occunch.get(rm.getCab_number())))-Integer.parseInt(qlid_cab)));	
					  }
  	              System.out.println(jsonObj.get("Qlid")+" "+jsonObj.get("Cab_number")+" "+jsonObj.get("Route_number")+" "+jsonObj.get("shift_id"));

	              jsonArr.put(jsonObj);	
	              
			}
			if(count == 0){
     				JSONObject js=new JSONObject();
					js.put("error","no data");
					jsonArr.put(js);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonArr;
		}
	
	
}
