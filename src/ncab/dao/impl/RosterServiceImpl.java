package ncab.dao.impl;

import java.sql.Connection;
import java.io.File;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart; 
import java.util.*;
import java.io.InputStream;
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

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
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
	public JSONArray showRosterInfo(JSONObject jsn) {
		Calendar cal = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Start showRosterInfo :: " + new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));
		JSONArray jsonArr = new JSONArray();
		DBConnectionUpd db = new DBConnectionUpd();
		RosterModel rm = new RosterModel();
		Connection con = db.getConnection();
		String qlid = jsn.getString("qlid");
		String cab_number = jsn.getString("c_n");
		String shift_id = jsn.getString("s_i");
		String emp_name = jsn.getString("e_n");
		// String vname = "";
		String vname = jsn.getString("vname");
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();

		System.out.println(shift_id);
		// String current_roster_month = "MAR";
		// String current_roster_year = "2018";
		int rn, shift;
		ResultSet rs1, rs2;
		String qlid_cab = "";// cab count
		String query = "", subquery1 = "", subquery2 = "", subquery3 = "";
		HashMap<String, String> occu = new HashMap<>();
		HashMap<String, String> occunch = new HashMap<>();
		System.out.println("Start showRosterInfo 2 :: " + new SimpleDateFormat("HH:mm:ss").format(cal.getTime()));

		String occu_query = "select cab_license_plate_no,cab_capacity from ncab_cab_master_tbl";
		String occ_q = "select distinct Cab_No from ncab_roster_tbl where Shift_Id='4' and '" + current_date
				+ "' between Start_Date and End_Date and Route_Status='active'";
		try {
			PreparedStatement ps3 = con.prepareStatement(occu_query);
			PreparedStatement ps4 = con.prepareStatement(occ_q);
			ResultSet rs3 = ps3.executeQuery();
			ResultSet rs4 = ps4.executeQuery();
			while (rs3.next()) {
				occu.put(rs3.getString(1), rs3.getString(2));
				System.out.println("Item added :-  " + rs3.getString(1) + "  " + rs3.getString(2));
			}
			while (rs4.next()) {
				occunch.put(rs4.getString(1), "4");
				System.out.println("Item added Un:-  " + rs4.getString(1));
			}
			String pick_qlid = "";
			String pick_shift = "";
			String pick_cab_number = "";
			query = selectFilterQuery(qlid, cab_number, shift_id, emp_name, vname);
			qlid_cab = "";
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			int qsize = 0;
			while (rs.next()) {
				qsize++;

				pick_qlid = rs.getString(1);
				pick_shift = rs.getString(2);
				pick_cab_number = rs.getString(3);
				subquery3 = "select Count(Emp_Qlid) from ncab_roster_tbl where '" + current_date
						+ "' between Start_Date and End_Date" + " and Shift_Id='" + pick_shift + "' and Cab_No='"
						+ pick_cab_number + "' and Emp_Status = 'active' and Route_Status = 'active' ";
				PreparedStatement ps5 = con.prepareStatement(subquery3);
				ResultSet rs5 = ps5.executeQuery();
				while (rs5.next()) {
					qlid_cab = rs5.getString(1);
				}
				JSONObject jsonObj = new JSONObject();

				subquery1 = "select * from ncab_roster_tbl where Emp_Qlid='" + pick_qlid + "' and Shift_Id='"
						+ pick_shift + "' and Emp_Status = 'active' and '" + current_date
						+ "' between Start_Date and End_Date ";
				subquery2 = "select Emp_FName,Emp_MName,Emp_LName,Emp_Pickup_Area,Emp_Mob_Nbr from ncab_master_employee_tbl where Emp_Qlid='"
						+ pick_qlid + "'";
				PreparedStatement ps1 = con.prepareStatement(subquery1);
				PreparedStatement ps2 = con.prepareStatement(subquery2);
				rs1 = ps1.executeQuery();
				rs2 = ps2.executeQuery();
				while (rs1.next()) {
					rm.setQlid(rs1.getString(2));
					rm.setCab_number(rs1.getString(6));
					rm.setRoot_number(rs1.getString(1));
					rm.setShift_id(rs1.getString(3));
					rm.setPickup_time(rs1.getString(4));
					rm.setVendor_name(rs1.getString(16));
					jsonObj.put("Qlid", rm.getQlid());
					jsonObj.put("Cab_number", rm.getCab_number());
					jsonObj.put("Route_number", rm.getRoot_number());
					jsonObj.put("shift_id", rm.getShift_id());
					jsonObj.put("pickup_time", rm.getPickup_time());
					System.out.println("vendor :- " + rm.getVendor_name());
					String setVendor = "";
					if(rm.getVendor_name().equals(" ")){
						setVendor="";
					}
					else{
					String splitVendor[] = rm.getVendor_name().toString().split(" ");
					
					if (splitVendor.length > 3) {
						setVendor = new String(splitVendor[0] + " " + splitVendor[1] + " " + splitVendor[2]);
					} else if (splitVendor.length == 2) {
						setVendor = new String(splitVendor[0] + " " + splitVendor[1]);
					} else {
						setVendor = new String(splitVendor[0]);
					}
					}
					jsonObj.put("vendor_name", setVendor);

				}
				while (rs2.next()) {
					rm.setFname(rs2.getString(1));
					rm.setMname(rs2.getString(2));
					rm.setLname(rs2.getString(3));
					rm.setPickup_area(rs2.getString(4));
					rm.setEmp_Mob(rs2.getString(5));
					jsonObj.put("f_name", rm.getFname());
					jsonObj.put("m_name", rm.getMname());
					jsonObj.put("l_name", rm.getLname());
					
					jsonObj.put("e_mob", rm.getEmp_Mob());
					if(!(rm.getShift_id().equals("4"))){
					jsonObj.put("p_a", rm.getPickup_area());
					}
					else{
						PreparedStatement ps6 = con.prepareStatement("select Pickup_Area from ncab_roster_tbl where Emp_Qlid='"+rm.getQlid()+"' and Cab_No='"+rm.getCab_number()+"' and Shift_Id='"+rm.getShift_id()+"' and Emp_Status='active' and '" + current_date
						+ "' between Start_Date and End_Date");
						ResultSet rs6=ps6.executeQuery();
						rs6.next();
						jsonObj.put("p_a", rs6.getString(1).toString());
					}
				}
				if (Integer.parseInt(rm.getShift_id()) != 4) {
					jsonObj.put("occu_left",
							(Integer.parseInt((occu.get(rm.getCab_number()))) - Integer.parseInt(qlid_cab)));
					System.out.println(" put :- "
							+ (Integer.parseInt((occu.get(rm.getCab_number()))) - Integer.parseInt(qlid_cab)));
				} else {
					jsonObj.put("occu_left",
							(Integer.parseInt((occunch.get(rm.getCab_number()))) - Integer.parseInt(qlid_cab)));
					System.out.println(" put :- "
							+ (Integer.parseInt((occunch.get(rm.getCab_number()))) - Integer.parseInt(qlid_cab)));
				}
				System.out.println(jsonObj.get("Qlid") + " " + jsonObj.get("Cab_number") + " "
						+ jsonObj.get("Route_number") + " " + jsonObj.get("shift_id"));

				jsonArr.put(jsonObj);

			}
			if (qsize == 0) {
				JSONObject js = new JSONObject();
				js.put("error", "no data");
				jsonArr.put(js);
			}
			System.out.println("query result size: " + qsize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArr;
	}

	public static String selectFilterQuery(String emp_qlid, String cab_no, String s_id, String name, String vname) {

		String query = "";
		String qlid = emp_qlid;
		String emp_name = name;
		String shift_id = s_id;
		String cab_number = cab_no;
		// String current_roster_month = c_r_m;
		// String current_roster_year = c_r_y;
		System.out.println("call me");

		System.out.println("qlid: " + emp_qlid);
		System.out.println("emp_name: " + name);
		System.out.println("Shift_Id: " + s_id);
		System.out.println("Cab_number: " + cab_no);

		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();
		System.out.println("CURRENT DATE: " + current_date);
		if (!(vname.equals(""))) {
			if (!(shift_id.equals("")))
				query = "select Emp_Qlid,Shift_Id, Cab_No from ncab_roster_tbl where Vendor_Name = '" + vname
						+ "' and Shift_Id = '" + shift_id
						+ "' and Route_Status = 'active' and Emp_Status = 'active' and '" + current_date
						+ "' between Start_Date and End_Date ";
			else
				query = "select Emp_Qlid,Shift_Id, Cab_No from ncab_roster_tbl where Vendor_Name = '" + vname
						+ "' and Route_Status = 'active' and Emp_Status = 'active' and '" + current_date
						+ "' between Start_Date and End_Date ";
		} else {
			if (!(cab_number.equals(""))) {
				if (!(emp_name.equals(""))) {
					if (!(qlid.equals(""))) {
						if (!(shift_id.equals(""))) { // if all fields are given
							query = "select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl WHERE Shift_Id='" + shift_id
									+ "' AND Cab_No LIKE '%" + cab_number + "%' and '" + current_date
									+ "' between Start_Date and End_Date AND Route_Status='active' AND Emp_Status='active' ORDER BY Route_No";
						} else { // if cab_number, qlid, emp_name are given
							// System.out.println(current_roster_month);
							// System.out.println(current_roster_year);
							System.out.println(qlid);
							System.out.println(cab_number);
							System.out.println(shift_id);

							query = "SELECT Emp_Qlid, Shift_Id, Cab_No FROM ncab_roster_tbl WHERE Emp_Status = 'active' AND Route_Status = 'active' and '"
									+ current_date + "' between Start_Date and End_Date" + " AND Cab_No LIKE '%"
									+ cab_number
									+ "%' AND Shift_Id IN (SELECT Shift_Id FROM ncab_roster_tbl WHERE Emp_Qlid = '"
									+ qlid + "' AND Cab_No LIKE '%" + cab_number
									+ "%' AND Emp_Status='active' AND Route_Status='active' and '" + current_date
									+ "' between Start_Date and End_Date) order by Route_No;";
						}
					} else {
						if (!(shift_id.equals(""))) { // if cab_number,
														// emp_name,
							// shift_id is given
							query = "select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl WHERE Shift_Id='" + shift_id
									+ "' AND Cab_No LIKE '%" + cab_number + "%' and '" + current_date
									+ "' between Start_Date and End_Date"
									+ " AND Route_Status='active' AND Emp_Status='active' ORDER BY Route_No";

						} else { // if cab_number, emp_name is given
							query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where Emp_Status = 'active' AND Route_Status = 'active' and '"
									+ current_date + "' between Start_Date and End_Date" + " AND Cab_No LIKE '%"
									+ cab_number
									+ "%' AND Shift_Id IN (select Shift_id from ncab_roster_tbl where Emp_Qlid IN (SELECT Emp_Qlid FROM ncab_master_employee_tbl WHERE (Emp_FName LIKE '%"
									+ emp_name + "%')||(Emp_MName LIKE '%" + emp_name + "%')||(Emp_LName LIKE '%"
									+ emp_name + "%')||(CONCAT(Emp_FName,' ',Emp_MName,' ',Emp_LName,' ') LIKE '%"
									+ emp_name + "%')||(CONCAT(Emp_FName,' ',Emp_LName,' ') LIKE '%" + emp_name
									+ "%')) AND Emp_Status = 'active' AND Route_Status = 'active' and '" + current_date
									+ "' between Start_Date and End_Date)";
						}
					}
				} else {
					if (!(qlid.equals(""))) {
						if (!(shift_id.equals(""))) { // if cab_number, qlid,
							// shift_id is given
							query = "select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl WHERE Shift_Id='" + shift_id
									+ "' AND Cab_No LIKE '%" + cab_number + "%' and '" + current_date
									+ "' between Start_Date and End_Date"
									+ " AND Route_Status='active' AND Emp_Status='active' ORDER BY Route_No";

						} else { // if cab_number, qlid is given
							query = "SELECT Emp_Qlid, Shift_Id, Cab_No FROM ncab_roster_tbl WHERE Emp_Status = 'active' AND Route_Status = 'active' and '"
									+ current_date + "' between Start_Date and End_Date" + " AND Cab_No LIKE '%"
									+ cab_number
									+ "%' AND Shift_Id IN (SELECT Shift_Id FROM ncab_roster_tbl WHERE Emp_Qlid = '"
									+ qlid + "' AND Cab_No LIKE '%" + cab_number
									+ "%' AND Emp_Status='active' AND Route_Status='active' and '" + current_date
									+ "' between Start_Date and End_Date" + ") ORDER BY Route_No;";

						}
					} else {
						if (!(shift_id.equals(""))) { // if cab_number, shift_id
														// is
							// given
							query = "select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl WHERE Shift_Id='" + shift_id
									+ "' AND Cab_No LIKE '%" + cab_number + "%' and '" + current_date
									+ "' between Start_Date and End_Date"
									+ " AND Route_Status='active' AND Emp_Status='active' ORDER BY Route_No";

						} else { // if cab_number is given
							query = "SELECT Emp_Qlid,Shift_Id,Cab_No FROM ncab_roster_tbl WHERE '" + current_date
									+ "' between Start_Date and End_Date"
									+ " and Emp_Status = 'active' and Route_Status = 'active' and Cab_No LIKE '%"
									+ cab_number + "%'";
						}
					}
				}
			} else {
				if (!(emp_name.equals(""))) {
					if (!(qlid.equals(""))) {
						if (!(shift_id.equals(""))) { // if emp_name, qlid,
														// shift_id
							// is given
							query = "select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' and '"
									+ current_date + "' between Start_Date and End_Date"
									+ " AND Cab_No in (select Cab_No from ncab_roster_tbl WHERE Shift_Id='" + shift_id
									+ "' AND Emp_Qlid LIKE '" + qlid + "' and '" + current_date
									+ "' between Start_Date and End_Date"
									+ " AND Route_Status='active' AND Emp_Status='active')";
						} else { // if emp_name, qlid is given
							query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where (Cab_No, Shift_Id) In (Select Cab_No, Shift_Id from ncab_roster_tbl where Emp_Qlid = '"
									+ qlid + "' and Route_Status = 'active' and Emp_Status='active' and '"
									+ current_date + "' between Start_Date and End_Date"
									+ ") and Route_Status = 'active' and Emp_Status='active' and '" + current_date
									+ "' between Start_Date and End_Date ";

						}
					} else {
						if (!(shift_id.equals(""))) { // if emp_name, shift_id
														// is
							// given
							query = "select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' and '"
									+ current_date + "' between Start_Date and End_Date" + " AND Shift_Id = '"
									+ shift_id
									+ "' AND Cab_No IN (select Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' and '"
									+ current_date + "' between Start_Date and End_Date"
									+ " and Emp_Qlid IN (SELECT Emp_Qlid FROM ncab_master_employee_tbl WHERE (Emp_FName LIKE '%"
									+ emp_name + "%')||(Emp_MName LIKE '%" + emp_name + "%')||(Emp_LName LIKE '%"
									+ emp_name + "%')||(CONCAT(Emp_FName,' ',Emp_MName,' ',Emp_LName,' ') LIKE '%"
									+ emp_name + "%')||(CONCAT(Emp_FName,' ',Emp_LName,' ') LIKE '%" + emp_name
									+ "%')))";
						} else {
							// if emp_name is given
							query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where (Cab_No, Shift_Id) In (Select Cab_No, Shift_Id from ncab_roster_tbl where Emp_Qlid IN (SELECT Emp_Qlid FROM ncab_master_employee_tbl WHERE (Emp_FName LIKE '%"
									+ emp_name + "%')||(Emp_MName LIKE '%" + emp_name + "%')||(Emp_LName LIKE '%"
									+ emp_name + "%')||(CONCAT(Emp_FName,' ',Emp_MName,' ',Emp_LName,' ') LIKE '%"
									+ emp_name + "%')||(CONCAT(Emp_FName,' ',Emp_LName,' ') LIKE '%" + emp_name
									+ "%')) and Route_Status = 'active' and Emp_Status='active' and '" + current_date
									+ "' between Start_Date and End_Date"
									+ ") and Route_Status = 'active' and Emp_Status='active' and '" + current_date
									+ "' between Start_Date and End_Date ";
						}
					}
				} else {
					if (!(qlid.equals(""))) {
						if (!(shift_id.equals(""))) { // if qlid, shift_id is
														// given
							query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' and '"
									+ current_date + "' between Start_Date and End_Date" + " AND Shift_Id = '"
									+ shift_id
									+ "' and Cab_No IN (select Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' and '"
									+ current_date + "' between Start_Date and End_Date" + " and Emp_Qlid = '" + qlid
									+ "')";
						} else {
							// if qlid is given
							query = "select Emp_Qlid, Shift_Id, Cab_No from ncab_roster_tbl where (Cab_No, Shift_Id) In (Select Cab_No, Shift_Id from ncab_roster_tbl where Emp_Qlid = '"
									+ qlid + "' and Route_Status = 'active' and Emp_Status='active' and '"
									+ current_date + "' between Start_Date and End_Date"
									+ ") and Route_Status = 'active' and Emp_Status='active' and '" + current_date
									+ "' between Start_Date and End_Date ";
						}
					} else {
						if (!(shift_id.equals(""))) {
							// if shift_id is given
							System.out.println(shift_id);
							query = "select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl where Route_Status = 'active' and Emp_Status='active' AND Shift_Id = '"
									+ shift_id + "' and '" + current_date + "' between Start_Date and End_Date"
									+ " AND Cab_No IN (select Cab_No from ncab_roster_tbl where '" + current_date
									+ "' between Start_Date and End_Date and Emp_Status = 'active' and Route_Status = 'active' and Shift_Id='"
									+ shift_id + "')";

						} else {
							// if all fields are empty

							System.out.println("all filter fields are empty");
							query = "select Emp_Qlid,Shift_Id,Cab_No from ncab_roster_tbl where Emp_Status='active' AND Route_Status='active'  AND '"
									+ current_date + "' between Start_Date AND End_Date order by Route_No";
						}
					}
				}
			}

		}

		return query;
	}

	public JSONObject insertIntoDB(InputStream fileInputStream,
			FormDataContentDisposition fileFormDataContentDisposition) throws IOException {
		int counter = 0;
		JSONObject jsobj = new JSONObject();
		JSONArray jsarr = new JSONArray();
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		RowCheck rowcheck = new RowCheck();
	//	String LOGFILE_DIR = "C:\\Users\\DB250491\\Desktop\\ncab_logs";
		String LOGFILE_DIR = "/tmp/ncab_logs";
		String LOGFILE_PREFIX = "iNCRediCabs_Roster_MASS_UPLOAD_LOG_";
//		String path = new String(System.getProperty("user.home") + "/Desktop/output.txt");
		String logFileName = LOGFILE_DIR + "/" + LOGFILE_PREFIX+ ".txt";
		File logDir = new File(LOGFILE_DIR);
		if(!logDir.exists()) {
			logDir.mkdir();
		}
		File file = new File(logFileName);
		if(!file.exists()) {
						file.createNewFile();
		}

		//String path = new String("/tmp/ncab_logs/RosterUploadError_log.txt");
		FileWriter f0 = new FileWriter(file); 
//		FileWriter f0 = new FileWriter(path);
		String[] route_no_arr = null;
		String[] empid_arr = null;
		String[] cab_arr = null;
		String route_no = null;
		int error = 0;
		int i = 0, last_row_valid = 0, index = 1;
		int ct = 0;
		// PreparedStatement counter = connection.prepareStatement("select
		// max(Route_No) from ncab_roster_tbl;");
		PreparedStatement psc;
		try {
			psc = connection.prepareStatement("select max(Route_No) from ncab_roster_tbl where '" + current_date
					+ "' between Start_Date and End_Date;");
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
		HashMap<String, HashMap<String, String>> hm = new HashMap<String, HashMap<String, String>>(); // cab

		PreparedStatement ps;
		try {
			ps = connection
					.prepareStatement("select Cab_No,Shift_Id,Route_No from ncab_roster_tbl where Shift_Id <> 4 and '"
							+ current_date + "' between Start_Date and End_Date and Route_Status='active' order by Route_No");
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
			System.out.println("HashMap after filling the data from db: " + hm);
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
				Row row_check_test = sheet.getRow(i);
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
				row = sheet.getRow(i);
				String shift_id = null;
				String empid = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				empid_arr[i - 1] = empid;
				System.out.println("id: " + empid);
				String shift_time = row.getCell(4).getStringCellValue(); // get
																			// from
																			// table
				if (shift_time.equals("07:00 - 04:00")) {
					shift_id = "1";
				}
				if (shift_time.equals("10:00 - 07:00")) {
					shift_id = "2";
				}
				if (shift_time.equals("12:00 - 09:00")) {
					shift_id = "3";
				}
				if (shift_time.equals("02:00 - 11:00")) {
					shift_id = "5";
				}
				System.out.println("Shift: " + shift_id);

				/*
				 * String pickhrs = "" +
				 * row.getCell(6).getDateCellValue().getHours(); String pickmin
				 * = "" + row.getCell(6).getDateCellValue().getMinutes(); if
				 * (pickmin.compareTo("0") == 0) pickmin = "00"; String picktime
				 * = pickhrs + ":" + pickmin;
				 */
				String picktime = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				System.out.println("pick time: " + picktime);

				String cab_from_excel = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
						.getStringCellValue();
				cab_arr[i - 1] = cab_from_excel;
				System.out.println("Cab: " + cab_from_excel);

				// instead of roster month and year, accept dates

				String start_date = row.getCell(9).getStringCellValue();
				System.out.println("Start Date: " + start_date);

				String end_date = row.getCell(10).getStringCellValue();
				System.out.println("Start Date: " + end_date);

				String vname = row.getCell(8).getStringCellValue();
				System.out.println("Vendor name:" + vname);

				String dname = row.getCell(12).getStringCellValue();
				System.out.println("Driver Name: " + dname);

//				String dnumber = row.getCell(13).getStringCellValue();
				 String dnumber = "1234567890";
				System.out.println("Driver Phone Number: " + dnumber);

				String remarks = row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				System.out.println("Remarks are " + remarks);

				String Route_No = "";

				// working date conversion

				/*
				 * String dfe = "21-03-2018"; // get dates from excel
				 * System.out.println("Input date: "+dfe);
				 */ SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date parsed;
				String sdate = " ", edate = " ";
				try {
					parsed = format.parse(start_date);
					java.sql.Date sdate_sql = new java.sql.Date(parsed.getTime());
					parsed = format.parse(end_date);
					java.sql.Date edate_sql = new java.sql.Date(parsed.getTime());
					sdate = sdate_sql.toString();
					edate = edate_sql.toString();
					System.out.println(sdate + " " + edate);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
					System.out.println("r no :- "+Route_No);
					System.out.println("shift :- "+shift_id);
					
					
					if (hm.get(cabno) == null) {
						hm.put(cabno, new HashMap<String, String>());
						hm.get(cabno).put(shift_id, Route_No);
					} else if (hm.get(cabno).get(shift_id) == null)
						hm.get(cabno).put(shift_id, Route_No);
										
					
					
					// change the code for this as per the above hashmap data
					// filling
					/*if (hm.get(cabno) == null)
						sr = new HashMap<String, String>();
					sr.put(shift_id, Route_No);
					hm.put(cabno, sr);
				} else if (hm.get(cabno) != null && hm.get(cabno).get(shift_id) != null)
					Route_No = hm.get(cabno).get(shift_id);*/
				}
				else if (hm.get(cabno) != null && hm.get(cabno).get(shift_id) != null)
					Route_No = hm.get(cabno).get(shift_id);
				

				System.out.println("Big hm: " + hm);

				String rn_from_excel = row.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
				System.out.println("Route from excel:" + rn_from_excel);

				if (!rn_from_excel.equals("") && !rn_from_excel.equals(Route_No)) {
					System.out.println("Error in route number");/// wrong route
					/// number from
					Route_No = "Errorofcab"; /// excel
				}
				System.out.println("Import rows " + i);
				connection.setAutoCommit(false);

				if (rn_from_excel.equals(Route_No)) // undo this update if the
					// insert is a failure
					try {
						PreparedStatement update = connection.prepareStatement(
								"update ncab_roster_tbl set Emp_Status = 'inactive' where Emp_Qlid = '" + empid
										+ "' and Route_No = '" + rn_from_excel + "' and '" + current_date
										+ "' between Start_Date and End_Date;");
						update.executeUpdate();
					} catch (Exception e) {
						System.out.println(e);
					}

				// function call for insertion

				CallableStatement cs = (CallableStatement) connection
						.prepareCall("{? = call ncab_add_excel_row_3_fnc(?,?,?,?,?,?,?,?,?,?,?)}");
				cs.registerOutParameter(1, Types.VARCHAR);
				cs.setString(2, empid);
				cs.setString(3, shift_id);
				cs.setString(4, picktime);
				cs.setString(5, cabno);
				cs.setString(6, remarks);
				cs.setString(7, Route_No);
				cs.setString(8, sdate);
				cs.setString(9, edate);
				cs.setString(10, dname);
				cs.setString(11, dnumber);
				cs.setString(12, vname);

				cs.execute();
				String retValue = cs.getString(1);

				System.out.println(retValue + "Point");
				String[] flag = { "FAILURE", "NO", "NO", "NO", "No", "No", "No", "No" ,"No"};
				String[] quote = { "FAILURE", "QLID", "Shift_Timing", "Cab_No", "Route No has no Vacancy",
						"Route Number doesn't match", "Wrong Driver Name", "Wrong Vendor Name" ,"Duplicate Record Found"};
				String[] retValue_token = retValue.split("\\s+");
				String final_push = "";
				System.out.println(retValue_token[0] + "SagaCheck");
				if (retValue_token[0].compareTo("FAILURE") == 0) {

					try {
						PreparedStatement update = connection
								.prepareStatement("update ncab_roster_tbl set Emp_Status = 'active' where Emp_Qlid = '"
										+ empid + "' and Route_No = '" + rn_from_excel + "' and '" + current_date
										+ "' between Start_Date and End_Date;");
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
									+ "' and Shift_Id = '" + shift_id + "' and Emp_Status = 'active' and '"
									+ current_date + "' between Start_Date and End_Date");
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
					if (retValue_token[5].compareTo("-1") == 0) {
						System.out.println("Route number mismatch");
						flag[5] = "Yes";
					}
					if (retValue_token[6].compareTo("-1") == 0) {
						System.out.println("Driver Name wrong");
						flag[6] = "Yes";
					}
					if (retValue_token[7].compareTo("-1") == 0) {
						System.out.println("Vendor Name wrong");
						flag[7] = "Yes";
					}
					if (retValue_token[8].compareTo("-1") != 0) {
						System.out.println("Duplicate Record");
						flag[8] = "Yes";
					}
					
					for (int y = 1; y < 9; y++) {
						if (flag[y].compareTo("Yes") == 0) {
							final_push = final_push.concat(quote[y]) + " ";
							counter++;
						}
					}

					System.out.println(final_push);
					error++;

					empid_arr[i - 1] = "Error";
					cab_arr[i - 1] = "Error";
					f0.write("Error for " + i + " record" + " Reason for Error(" + counter + "):- " + final_push
							+ " Invalid @ " + new Timestamp(System.currentTimeMillis()) + newLine);
				}
			}
			jsobj.put("tr", i - 1);
			jsobj.put("eo", counter);
			
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
		
		System.out.println("Jsarr :- " + jsobj);
		this.SendAttachmentInEmail();
		return jsobj;
	}

	public void SendAttachmentInEmail() {

		String to = "sj250305@ncr.com";

		String from = "sauravjoshi123@gmail.com";

		final String username = "sauravjoshi123";// change accordingly
		final String password = "jhzktcgxbthatpta";// change accordingly

		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "25");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));

			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			
			message.setSubject("Upload Error Log");

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			Multipart multipart = new MimeMultipart();

			multipart.addBodyPart(messageBodyPart);
			String LOGFILE_DIR = "/tmp/ncab_logs";
			String LOGFILE_PREFIX = "iNCRediCabs_Roster_MASS_UPLOAD_LOG_";
//			String path = new String(System.getProperty("user.home") + "/Desktop/output.txt");
			String logFileName = LOGFILE_DIR + "/" + LOGFILE_PREFIX+ ".txt";
			messageBodyPart = new MimeBodyPart();
			//String filename = new String(System.getProperty("user.home") + "/Desktop/output.txt");
			DataSource source = new FileDataSource(logFileName);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(logFileName);
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);

			Transport.send(message);

			System.out.println("Sent message successfully....");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	} 
	
	public JSONObject addEmpToDb(JSONObject json) {
		JSONObject js = new JSONObject();
		long millis = System.currentTimeMillis();  
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();

		try {
			DBConnectionUpd db = new DBConnectionUpd();
			Connection con = db.getConnection();
			String cab = json.getString("c_n");
			String qlid = json.getString("qlid");
			String sid = json.getString("s_i");
			System.out.println("The sid is " + sid);

			//			long millis = System.currentTimeMillis();
			java.sql.Date startdate = new java.sql.Date(millis);
			System.out.println("Start Date: " + startdate);

			int count = 0;
			String enddate = null;
			// String pick=json.getString("p_time");
			System.out.println(cab + "   " + qlid);
			String r_n = "";
			PreparedStatement ps = con.prepareStatement("select count(Emp_Qlid) from ncab_roster_tbl where Cab_No=? and Shift_Id=? and Emp_Qlid=? and '"+current_date+"' between Start_Date and End_Date");
			ps.setString(1,cab);
			ps.setString(2,sid);
			ps.setString(3,qlid);
			ResultSet rs=ps.executeQuery();
			rs.next();
			int count1=Integer.parseInt(rs.getString(1));
			if(count1 >0){
				js.put("error_type", "exist");
			}
			else{
			PreparedStatement ps1 = con.prepareStatement("select Route_No,max(End_Date),Vendor_Name, Driver_Id from ncab_roster_tbl where Cab_No=? and Shift_Id=? and '"+current_date+"' between Start_Date and End_Date");
			ps1.setString(1, cab);
			ps1.setString(2, sid);
			ResultSet rs2 = ps1.executeQuery();
			String vname="",did="";
			while (rs2.next()) {
				r_n = rs2.getString(1);
				enddate = rs2.getString(2);
				vname = rs2.getString(3);
				did = rs2.getString(4);
				PreparedStatement ps2 = con.prepareStatement("insert into ncab_roster_tbl(Route_No,Emp_Qlid,Shift_Id,Cab_No,Start_Date,End_Date,Vendor_Name,Driver_Id) values(?,?,?,?,?,?,?,?)");

				ps2.setString(1, r_n);
				ps2.setString(2, qlid);
				ps2.setString(3, sid);
				ps2.setString(4, cab);
				ps2.setString(5, startdate.toString());
				ps2.setString(6, enddate);
				ps2.setString(7, vname);
				ps2.setString(8, did);


				int i = ps2.executeUpdate();
				System.out.println("Return value update: "+i);
				if (i > 0) {
					js.put("error_type", "success");
					System.out.println("success in inserting");
				} else {
					js.put("error_type", "fail");
				}

			}
		 }	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return js;

	}

	public JSONArray getAddData(JSONObject json) {
		String c_no = json.getString("c_n");
		String s_id = json.getString("s_id");
		
		DBConnectionUpd db = new DBConnectionUpd();
		Connection connection = db.getConnection();
		JSONArray jsarr = new JSONArray();
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();

		try {
			PreparedStatement ps = null;
			if (s_id.equals("4")) {
				ps = connection.prepareStatement(
						"select Emp_Qlid,Emp_FName,Emp_LName from ncab_master_employee_tbl where Emp_Qlid not in (select Emp_Qlid from ncab_roster_tbl where Shift_Id = '"+ s_id + "' and Emp_Status='active' and  '" + current_date+ "' between Start_Date and End_Date)");
			} else {
				ps = connection.prepareStatement(
						"select Emp_Qlid,Emp_FName,Emp_LName from ncab_master_employee_tbl where Emp_Qlid not in (select Emp_Qlid from ncab_roster_tbl where Shift_Id <> '4' and Emp_Status='active' and  '"+ current_date + "' between Start_Date and End_Date)");

			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject js = new JSONObject();
				// PreparedStatement ps1=connection.prepareStatement("select
				// Emp_FName,Emp_LName from ncab_master_employee_tbl where
				// Emp_Qlid not in (select Emp_Qlid from ncab_roster_tbl where
				// Emp_Status='active' and Roster_Month='MAR' and
				// Roster_Year='2018')");

				js.put("Qlid", rs.getString(1));
				js.put("EFname", rs.getString(2));
				js.put("ELname", rs.getString(3));
				jsarr.put(js);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsarr;

	}

	public JSONObject inactiveqlid(JSONObject jobj) {

		JSONObject js = new JSONObject();

		try {
			String qlid = jobj.getString("emp_qlid");
			String s_id =jobj.getString("s_id");
			System.out.println("Delete :- " + qlid);
			DBConnectionUpd db = new DBConnectionUpd();
			Connection con = db.getConnection();
			long millis = System.currentTimeMillis();  
			java.sql.Date date = new java.sql.Date(millis);
			String current_date = date.toString();
			String query = "UPDATE ncab_roster_tbl SET Emp_Status='inactive' WHERE Emp_Qlid = '" + qlid + "' and Shift_Id='"+s_id+"' and '"+current_date+"' between Start_Date and End_Date";
			PreparedStatement ps = con.prepareStatement(query);
			int i = ps.executeUpdate();
			if (i > 0){
				js.put("error_msg", "success");
			}
			else{
				js.put("error_msg", "fail");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return js;
	}

	// Jaspreet

	public JSONArray getDriver() {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarr = new JSONArray();
		String name = "", ph = "";
		try {
			System.out.println("Inside try before query");
			PreparedStatement ps = connection
					.prepareStatement("select driver_name , d_contact_num from ncab_driver_master_tbl");
			ResultSet rs = ps.executeQuery();
			System.out.println("Inside try after query");
			while (rs.next()) {
				JSONObject json = new JSONObject();
				name = rs.getString(1);
				ph = rs.getString(2);
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
		String qlid, guard, picktime, cabno, start, end, vendor, Status = "Active";
		int shiftid = 0, driver = 0, vendorid=0;;
		try {
			String routeno = "";
			int route = 0, cost = 0;
			PreparedStatement ps = connection.prepareStatement("select Max(Route_No) from ncab_roster_tbl");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				routeno = rs.getString(1);
			}
			route = Integer.parseInt(routeno);

			route++;
			JSONObject jj = jsonarr.getJSONObject(0);
			String drivername = jj.optString("dname");
			String driverph = jj.optString("dph");
			PreparedStatement ps2 = connection.prepareStatement(
					"select driver_id from ncab_driver_master_tbl where driver_name = ? and d_contact_num = ?");
			ps2.setString(1, drivername);
			ps2.setString(2, driverph);
			System.out.println("Driver name:" + drivername + driverph);
			ResultSet rs2 = ps2.executeQuery();
			while (rs2.next()) {
				driver = rs2.getInt(1);
			}
			System.out.println("Driver id:" + driver);

			System.out.println(route);
			if (route < 10)
				routeno = "00" + route;
			else if (route >= 10 && route < 100)
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
				vendorid=json.optInt("vid");
				System.out.println("Object Created" + qlid);
				System.out.println("----Query ready" + qlid);
				PreparedStatement ps1 = connection.prepareStatement(
						"insert into ncab_roster_tbl (Route_no,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Start_Date,End_Date,Vendor_Name,Route_Status,Emp_Status,Cab_Cost,Driver_Id,Vendor_Id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

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
				ps1.setInt(14, vendorid);
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
		String qlid, guard, picktime, cabno, start, end, vendor, Status = "Active", pickup = "nan", drop = "nan";
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
				pickup = json.optString("pickup");
				drop = json.optString("drop");
				picktime = json.optString("picktime");
				cabno = json.optString("cabno");
				start = json.optString("start");
				end = json.optString("end");
				vendor = json.optString("vendor");
				cost = json.optInt("cost");
				System.out.println("Object Created" + qlid+" :drop "+drop);
				System.out.println("----Query ready" + qlid+" :pickup "+pickup);
				PreparedStatement ps1 = connection.prepareStatement(
						"insert into ncab_roster_tbl (Route_no,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Start_Date,End_Date,Vendor_Name,Emp_Status,Cab_Cost,Route_Status,Drop_type,Pickup_Area) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

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
				ps1.setString(13, drop);
				ps1.setString(14, pickup);
				
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
		String month = json.getString("date");

		JSONObject json1 = new JSONObject();
		System.out.println("inside getEmpDetails");
		try {
			System.out.println("inside try before query");
			PreparedStatement ps1 = connection.prepareStatement(
					"select Route_No from ncab_roster_tbl where Emp_Qlid = ? and  ? Between Start_Date and End_Date and Emp_Status='active'");
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
				json1.put("qlid", qlid);
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
		String fname="",mname="",lname="",name="";

		try {
			System.out.println("Inside try before query");
			PreparedStatement ps = connection.prepareStatement("select Emp_Qlid,Emp_FName, Emp_MName, Emp_LName from ncab_master_employee_tbl");
			ResultSet rs = ps.executeQuery();
			System.out.println("Inside try after query");
			while (rs.next()) {
				JSONObject json = new JSONObject();
				qlid = rs.getString(1);
				fname=rs.getString(2);
				mname=rs.getString(3);
				lname=rs.getString(4);
				name=fname+" "+mname+" "+lname;
				json.put("qlid", qlid);
				json.put("name", name);	jsonarr.put(json);
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
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
		String cabno = "";
		int cap = 0;
		try {
			PreparedStatement ps = connection.prepareStatement(
					"SELECT cab_license_plate_no, cab_capacity FROM ncab_cab_master_tbl WHERE cab_license_plate_no NOT IN ( SELECT Cab_No FROM ncab_roster_tbl WHERE Shift_Id = ? )");
			ps.setInt(1, shiftid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				cabno = rs.getString(1);
				cap = rs.getInt(2);
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
	}	// saurav

	public JSONObject sauravkaeditmethod(JSONObject obj) {
		JSONObject jobjtodis=new JSONObject();
	try {
		System.out.println("edit json:" + obj);
		String cab_and_shift = obj.getString("cabno");
		String arr[] = cab_and_shift.split(" ");
		String cabno = arr[0];
		String sid = arr[1];
		System.out.println("CAB NUMBER: " + cabno);
		System.out.println("SHIFT ID: " + sid);

		String picktime = obj.getString("picktime");
		// String shifttime="";
		String qlid = obj.getString("qlid");
		String sdate = obj.getString("sdate");
		String edate = obj.getString("edate");

		System.out.println(cabno);
		System.out.println(picktime);
		System.out.println(qlid);
		System.out.println(sdate);
		System.out.println(edate);
		System.out.println(sdate.length());
		/*
		 * System.out.println(edate.length()); String sd=sdate.substring(8);
		 * System.out.println(sd); String ed=edate.substring(8);
		 * System.out.println(ed);
		 */

		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();

		DBConnectionUpd db = new DBConnectionUpd();
		Connection con = db.getConnection();
		String v_n = "";
		String r_n = "";
		String query2 = "select Route_No,Vendor_Name,Driver_Id from ncab_roster_tbl where Cab_No='" + cabno
				+ "' and Shift_Id = '" + sid + "' and '" + current_date
				+ "' between Start_Date and End_Date and Emp_Status='active' and Route_Status = 'active'";
		PreparedStatement ps2 = con.prepareStatement(query2);
		ResultSet rs = ps2.executeQuery();
		rs.next();
		r_n = rs.getString(1);
		String vname = rs.getString(2);
		String did = rs.getString(3);

		String query4 = "update ncab_roster_tbl set Emp_Status='inactive' where Emp_Qlid='" + qlid + "' and '"
				+ current_date + "' between Start_Date and End_Date";
		PreparedStatement ps4 = con.prepareStatement(query4);
		ps4.executeUpdate();
		System.out.println("RN: " + r_n);
		System.out.println("qlid: " + qlid);
		System.out.println("Cab_No: " + cabno);
		System.out.println("PT: " + picktime);
		System.out.println("SD: " + sdate);
		System.out.println("ED: " + edate);
		String query1 = "insert into ncab_roster_tbl (Route_No,Emp_Qlid,Cab_No,Pickup_Time,Shift_Id,Start_Date,End_Date,Vendor_Name,Driver_Id) values(?,?,?,?,?,?,?,?,?) ";

		PreparedStatement ps = con.prepareStatement(query1);

		ps.setString(1, r_n);
		ps.setString(2, qlid);
		ps.setString(3, cabno);
		ps.setString(4, picktime);
		ps.setString(5, sid);
		ps.setString(6, sdate);
		ps.setString(7, edate);
		ps.setString(8, vname);
		ps.setString(9, did);

		int i=ps.executeUpdate();
		if(i>=1){
			jobjtodis.put("error_type", "success");
		}
		else{
			jobjtodis.put("error_type", "fail");
		}
		System.out.println(jobjtodis);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		jobjtodis.put("error_type", "fail");
		e.printStackTrace();
	}

	// TODO Auto-generated method stub
	return (jobjtodis);
}

	// richa
	public JSONArray getAllRoute() {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jarr = new JSONArray();
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();

		try {
			PreparedStatement ps = connection
					.prepareStatement("select distinct Route_No from ncab_roster_tbl where  Route_Status='active' and '"
							+ current_date + "' between Start_Date and End_Date and Shift_Id <>'4' order by Route_No");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject json = new JSONObject();
				json.put("r_n", rs.getString(1));
				System.out.println(rs.getString(1));
				jarr.put(json);
			}
		} catch (Exception e) {
			System.out.println("error in imp" + e.getMessage());
		}
		return jarr;
	}

	public JSONArray getAllVendor() {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jarr = new JSONArray();

		try {
			PreparedStatement ps = connection.prepareStatement("select vendor_name from ncab_vendor_master_tbl");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject json = new JSONObject();
				json.put("ven", rs.getString(1));

				jarr.put(json);
			}

		} catch (Exception e) {
			System.out.println("error in imp :- " + e.getMessage());
		}
		return jarr;
	}

	public JSONArray getAllCab() {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jarr = new JSONArray();

		try {
			PreparedStatement ps = connection.prepareStatement("select cab_license_plate_no from ncab_cab_master_tbl");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject json = new JSONObject();
				json.put("c_n", rs.getString(1));

				jarr.put(json);
			}

		} catch (Exception e) {
			System.out.println("error in imp" + e.getMessage());
		}
		return jarr;
	}

	public JSONObject updatedRoute(JSONObject json) {
		System.out.println("inside fetchroster");
		JSONObject jsobj=new JSONObject();
		DBConnectionUpd db = new DBConnectionUpd();
		Connection con = db.getConnection();
		String routeno = json.getString("r_n");
		String cabno = json.getString("c_n");
		int shiftid = json.getInt("s_i");
		String vendor = json.getString("ven");
		String s_date = json.getString("s_date");
		String e_date = json.getString("e_date");
		System.out.println("SD: "+s_date
				);
		int s1 = 0;
		System.out.println(routeno);
		System.out.println(cabno);
		System.out.println(shiftid);

		try {
			if (vendor.equals("")) {
				PreparedStatement p = con.prepareStatement("select Vendor_Name from ncab_roster_tbl where Route_No='"
						+ routeno + "' and Route_Status='active'");
				ResultSet rr = p.executeQuery();
				while (rr.next()) {
					vendor = rr.getString(1);
				}
			}
			if (cabno.equals("")) {
				PreparedStatement p = con.prepareStatement("select Cab_No from ncab_roster_tbl where Route_No='"
						+ routeno + "' and Route_Status='active'");
				ResultSet rr = p.executeQuery();
				while (rr.next()) {
					cabno = rr.getString(1);
				}
			}
			if (shiftid == 0) {
				PreparedStatement p = con.prepareStatement("select Shift_Id from ncab_roster_tbl where Route_No='"
						+ routeno + "' and Route_Status='active'");
				ResultSet rr = p.executeQuery();
				while (rr.next()) {
					shiftid = rr.getInt(1);
				}
			}
			if (s_date.equals("")) {
				PreparedStatement p = con.prepareStatement("select Start_Date from ncab_roster_tbl where Route_No='"
						+ routeno + "' and Route_Status='active'");
				ResultSet rr = p.executeQuery();
				while (rr.next()) {
					s_date = rr.getString(1);
				}
			}
			if (e_date.equals("")) {
				PreparedStatement p = con.prepareStatement("select End_Date from ncab_roster_tbl where Route_No='"
						+ routeno + "' and Route_Status='active'");
				ResultSet rr = p.executeQuery();
				while (rr.next()) {
					e_date = rr.getString(1);
				}
			}
			
			
			long millis = System.currentTimeMillis();
			java.sql.Date date = new java.sql.Date(millis);
			String current_date = date.toString();
			
			System.out.println("Before If: cabno: "+cabno);
			System.out.println("Before If: sid: "+shiftid);
			
			if(!cabno.equals("") && shiftid != 0)
			{
				PreparedStatement rfe1 = con.prepareStatement("select Route_No,count(Route_No) from ncab_roster_tbl where Cab_No = '"+cabno+"' and Shift_Id = '"+shiftid+"' and '"+current_date+"' between Start_Date and End_Date");
				ResultSet rsrfe = rfe1.executeQuery();
				rsrfe.next();
				
				if(!routeno.equals(rsrfe.getString(1)) && !rsrfe.getString(2).equals("0"))
				{
					System.out.println("Already there is a route of this cab and shift");
					//throw an error
					jsobj.put("err_msg", "exist");
					return jsobj;
				}
			}
			
			PreparedStatement getcab = con.prepareStatement("select Cab_No from ncab_roster_tbl where Route_No = '"+routeno+"' and Route_Status = 'active'");
			ResultSet rsgc = getcab.executeQuery();
			rsgc.next();
			String cabnoupd = rsgc.getString(1); 
			System.out.println("Old cab no: "+cabnoupd);
			PreparedStatement pss = con.prepareStatement(
					"select Emp_Qlid,Pickup_Time,Cab_Cost,Guard_Needed,Remarks,Driver_Id,Drop_Type from ncab_roster_tbl WHERE Route_No ='"
							+ routeno + "' and Cab_No = '"+cabnoupd+"' and Emp_Status = 'active' and '"+current_date+"' between Start_Date and End_Date");
			ResultSet rs = pss.executeQuery();
			PreparedStatement ps = con
					.prepareStatement("UPDATE ncab_roster_tbl SET Emp_Status = 'inactive' , Route_Status='inactive' WHERE Route_No ='" + routeno
							+ "' and Route_Status='active' and Cab_No = '"+cabnoupd+"'");
			ps.executeUpdate();
			while (rs.next()) {
				PreparedStatement fs = con.prepareStatement(
						"insert into ncab_roster_tbl(Route_No,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Remarks,Start_Date,End_Date,Cab_Cost,Vendor_Name,Driver_Id,Drop_Type,Emp_Status,Route_Status) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				fs.setString(1, routeno);
				fs.setString(2, rs.getString(1));
				fs.setInt(3, shiftid);
				fs.setString(4, rs.getString(2));
				fs.setString(5, cabno);
				fs.setString(6, rs.getString(4));
				fs.setString(7, rs.getString(5));
				fs.setString(8, s_date);
				fs.setString(9, e_date);
				fs.setString(10, rs.getString(3));
				fs.setString(11, vendor);
				fs.setString(12, rs.getString(6));
				fs.setString(13, rs.getString(7));
				fs.setString(14, "active");
				fs.setString(15, "active");
				s1 = fs.executeUpdate();
				System.out.println("data inserted");
			}
			System.out.println("New rows inserted");

			System.out.println("Old rows made inactive");

			if (s1 > 0) {
				jsobj.put("err_msg", "success");
			}
			else{
				jsobj.put("err_msg", "fail");
			}
			System.out.println("Success mysql table");
		} catch (Exception e) {
			// TODO: handle exception				
			jsobj.put("err_msg", "fail");
			e.printStackTrace();
		}
		return jsobj;
	}

	public JSONArray getcablist(String s) {
		DBConnectionUpd db = new DBConnectionUpd();
		Connection connection = db.getConnection();
		JSONArray jsarr = new JSONArray();
		long millis = System.currentTimeMillis();  
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();
		System.out.println("String s: "+s);
		try {
			JSONObject json = new JSONObject(s);
			System.out.println("emp edit json: "+json);
			 String cabno = json.getString("cabno");
			String sid = json.getString("shiftid");
			PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT Cab_No,Shift_Id FROM ncab_roster_tbl WHERE (Cab_No,Shift_Id) NOT IN (SELECT cab_no,shift_id FROM ncab_roster_tbl WHERE cab_no = '"+cabno+"' AND shift_id = '"+sid+"' and '"+current_date+"' BETWEEN Start_Date AND End_Date) AND Shift_Id <> 4 AND '"+current_date+"' BETWEEN Start_Date AND End_Date ORDER BY Shift_Id;");
			// PreparedStatement ps=connection.prepareStatement("select Emp_Qlid
			// from master_employee ");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PreparedStatement emp_ct1 = connection
						.prepareStatement("select count(Emp_Qlid) from ncab_roster_tbl where Cab_No = '"
								+ rs.getString(1) + "' and Shift_Id = '" + rs.getString(2)
								+ "' and Emp_Status = 'active' and Route_Status = 'active'");
				ResultSet ctrs1 = emp_ct1.executeQuery();
				ctrs1.next();

				PreparedStatement emp_ct2 = connection
						.prepareStatement("select cab_capacity from ncab_cab_master_tbl where cab_license_plate_no='"
								+ rs.getString(1) + "'");
				ResultSet ctrs2 = emp_ct2.executeQuery();
				ctrs2.next();
				int vacancy = Integer.parseInt(ctrs2.getString(1)) - Integer.parseInt(ctrs1.getString(1));
				if (vacancy >= 1) {
					JSONObject js = new JSONObject();
					js.put("s_id", rs.getString(2));
					js.put("c_n", rs.getString(1));
					jsarr.put(js);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsarr;

	}

	public JSONArray getVendorForFilter() {
		DBConnectionUpd db = new DBConnectionUpd();
		Connection connection = db.getConnection();
		JSONArray jsarr = new JSONArray();
		try {
			PreparedStatement ps = connection
					.prepareStatement("select distinct vendor_name from ncab_vendor_master_tbl");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject js = new JSONObject();
				js.put("v_n", rs.getString(1));
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
		DBConnectionUpd db = new DBConnectionUpd();
		Connection con = db.getConnection();
		JSONArray jsarr = new JSONArray();
		String qli = "";
		String driverid = "";
		String vendorid = "", fname = "", lname = "", mgr = "", drivern = "", drivercont = "", managername = "",
				managername1 = "";
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();
		try {

			String cabno = js.getString("c_n");
			String shift = js.getString("s_n");
			System.out.println(cabno);
			System.out.println(shift);

			String query6 = "select Driver_Id,Emp_Qlid from ncab_roster_tbl where Cab_No='" + cabno + "' and Shift_Id='"
					+ shift + "' and  '" + current_date + "' between Start_Date and End_Date  and Emp_Status='active'";
			PreparedStatement ps7 = con.prepareStatement(query6);
			ResultSet rs6 = ps7.executeQuery();
			while (rs6.next()) {
				JSONObject jo = new JSONObject();
				driverid = rs6.getString(1);
				qli = rs6.getString(2);
				System.out.println("This is the qlid in the cab " + qli);

				String query8 = "select Emp_FName,Emp_LName,Emp_Mgr_Qlid1 from ncab_master_employee_tbl where Emp_Qlid='"
						+ qli + "'";

				PreparedStatement ps9 = con.prepareStatement(query8);
				ResultSet rs9 = ps9.executeQuery();
				while (rs9.next()) {
					fname = rs9.getString(1);
					lname = rs9.getString(2);
					mgr = rs9.getString(3);
					System.out.println("mgr :- " + mgr);
				}

				String query11 = "select Emp_FName,Emp_LName from ncab_master_employee_tbl where Emp_Qlid='" + mgr
						+ "'";
				PreparedStatement ps11 = con.prepareStatement(query11);
				ResultSet rs11 = ps11.executeQuery();
				System.out.println("mgr out :- " + mgr);

				while (rs11.next()) {
					System.out.println("mgr in :- " + mgr);
					managername = rs11.getString(1);
					managername1 = rs11.getString(2);
					System.out.println(managername + " " + managername1);
				}

				String query7 = "select driver_name,d_contact_num from ncab_driver_master_tbl where driver_id='"
						+ driverid + "'";
				PreparedStatement ps8 = con.prepareStatement(query7);
				ResultSet rs8 = ps8.executeQuery();
				while (rs8.next()) {
					drivern = rs8.getString(1);
					drivercont = rs8.getString(2);
				}

				jo.put("qd", qli);
				jo.put("did", driverid);
				jo.put("name1", fname);
				jo.put("name2", lname);
				jo.put("mgr", mgr);
				jo.put("dname", drivern);
				jo.put("dcont", drivercont);
				jo.put("managr", mgr);
				jo.put("mname1", managername);
				jo.put("mname2", managername1);

				jsarr.put(jo);
			}
			System.out.println("This is jsarray" + jsarr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsarr;

		// TODO Auto-generated method stub

	}
	
	// download data

	@SuppressWarnings("unused")
	public JSONObject download_data(String s) {
		JSONObject msg=new JSONObject();

		try {
			
			JSONObject jsn = new JSONObject(s);
			DBConnectionUpd db = new DBConnectionUpd();
			RosterModel rm = new RosterModel();
			Connection con = db.getConnection();
			String qlid = jsn.getString("qlid");
			String cab_number = jsn.getString("c_n");
			String shift_id = jsn.getString("s_i");
			String emp_name = jsn.getString("e_n");
			String vendor_name = jsn.getString("vname");
			String query = selectFilterQuery(qlid, cab_number, shift_id, emp_name, vendor_name);
			// Excel code
			int counter = 1;
			HSSFWorkbook hssfWorkbook = null;
			HSSFRow row = null;
			HSSFSheet hssfSheet = null;
			FileOutputStream fileOutputStream = null;
			Properties properties = null;
			String LOGFILE_DIR = "/tmp/ncab_logs";
			String LOGFILE_PREFIX = "Roster_Excel";
	//		String path = new String(System.getProperty("user.home") + "/Desktop/output.txt");
			String filename = LOGFILE_DIR + "/" + LOGFILE_PREFIX+ ".xls";
	//		String filename = new String(System.getProperty("user.home") + "/Desktop/roster.xls");

			hssfWorkbook = new HSSFWorkbook();
			hssfSheet = hssfWorkbook.createSheet("new sheet");
			DataFormat fmt = hssfWorkbook.createDataFormat();
			CellStyle textStyle = hssfWorkbook.createCellStyle();
			textStyle.setDataFormat(fmt.getFormat("@"));
			hssfSheet.setDefaultColumnStyle(6, textStyle); 
			
			
			HSSFRow rowhead = hssfSheet.createRow(0); // Header

			rowhead.createCell(0).setCellValue("S.N");
			rowhead.createCell(1).setCellValue("Route No");
			rowhead.createCell(2).setCellValue("QLID");
			rowhead.createCell(3).setCellValue("Employee Name");
			rowhead.createCell(4).setCellValue("Shift Time");
			rowhead.createCell(5).setCellValue("Pick-up Area");
			rowhead.createCell(6).setCellValue("Pick Time");
			rowhead.createCell(7).setCellValue("Drop at");
			rowhead.createCell(8).setCellValue("Vendor Name");
			rowhead.createCell(9).setCellValue("Start Date");
			rowhead.createCell(10).setCellValue("End Date");
			rowhead.createCell(11).setCellValue("Cab Number");
			rowhead.createCell(12).setCellValue("Driver Name");
			rowhead.createCell(13).setCellValue("Driver Number");
			rowhead.createCell(14).setCellValue("Guard Needed");
			rowhead.createCell(15).setCellValue("Remarks");

			// excel header created
			JSONObject jsobj = new JSONObject();
			PreparedStatement ps = con.prepareStatement(query);
			PreparedStatement ps1 = con.prepareStatement(
					"select Route_No,Emp_Qlid,Shift_Id,Pickup_Time,Cab_No,Guard_Needed,Start_Date,End_Date,Vendor_Name,Driver_Id from ncab_roster_tbl where Emp_Qlid=? and Shift_Id=? and Cab_No=?");
			PreparedStatement ps2 = con
					.prepareStatement("select driver_name,d_contact_num from ncab_driver_master_tbl where driver_id=?");
			PreparedStatement ps3 = con.prepareStatement(
					"select Emp_FName,Emp_MName,Emp_LName,Emp_Pickup_Area from ncab_master_employee_tbl where Emp_Qlid=?");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ps1.setString(1, rs.getString(1));
				ps1.setString(2, rs.getString(2));
				ps1.setString(3, rs.getString(3));
				ResultSet rs1 = ps1.executeQuery();
				while (rs1.next()) {
					rm.setRoute_no(rs1.getString(1));
					rm.setQlid(rs1.getString(2));
					rm.setShift_id(rs1.getString(3));
					rm.setPickup_time(rs1.getString(4));
					rm.setCab_number(rs1.getString(5));
					rm.setGuard_required(rs1.getString(6));
					rm.setStart_time(rs1.getString(7));
					rm.setEnd_time(rs1.getString(8));
					rm.setVendor_name(rs1.getString(9));
					rm.setDriver_id(rs1.getString(10));
				}
				// fetching driver data
				ps2.setInt(1, Integer.parseInt(rm.getDriver_id().toString()));
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					rm.setDriver_name(rs2.getString(1));
					rm.setDriver_phone_number(rs2.getString(2));
				}
				ps3.setString(1, rs.getString(1));

				// fetching emp data
				ResultSet rs3 = ps3.executeQuery();
				rs3.next();
				rm.setFname(rs3.getString(1));
				rm.setMname(rs3.getString(2));
				rm.setLname(rs3.getString(3));
				rm.setPickup_area(rs3.getString(4));
				if (!(rm.getRoute_no().equals("000"))) {
					row = hssfSheet.createRow( counter);
					row.createCell( 0).setCellValue(counter);
					int i = Integer.parseInt(rm.getRoute_no());
					row.createCell( 1).setCellValue(String.format("%03d", i));
					row.createCell( 2).setCellValue(rm.getQlid());
					if ((rm.getMname()).equals("")) {
						row.createCell( 3).setCellValue(rm.getFname() + " " + rm.getLname());
					} else {
						row.createCell( 3)
								.setCellValue(rm.getFname() + " " + rm.getMname() + " " + rm.getLname());

					}
					int shift = Integer.parseInt(rm.getShift_id());
					if (shift == 1) {
						row.createCell( 4).setCellValue("07:00 - 04:00");
					} else if (shift == 2) {
						row.createCell( 4).setCellValue("10:00 - 07:00");
					} else if (shift == 3) {
						row.createCell( 4).setCellValue("12:00 - 09:00");
					} else if (shift == 5) {
						row.createCell( 4).setCellValue("02:00 - 11:00");
					} else if (shift > 5) {
						row.createCell( 4).setCellValue("00:00 - 00:00");
					} else {
						row.createCell( 4).setCellValue("00:00 - 00:00");

					}
					row.createCell( 5).setCellValue(rm.getPickup_area());
					Cell cell_pickup = row.createCell(6);
					cell_pickup.setCellStyle(textStyle);
					cell_pickup.setCellValue(rm.getPickup_time());
					row.createCell( 7).setCellValue(rm.getDrop_time());
					row.createCell( 8).setCellValue(rm.getVendor_name());
					row.createCell( 9).setCellValue(rm.getStart_time());
					row.createCell( 10).setCellValue(rm.getEnd_time());
					row.createCell( 11).setCellValue(rm.getCab_number());
					row.createCell( 12).setCellValue(rm.getDriver_name());
					row.createCell( 13).setCellValue(rm.getDriver_phone_number());
					row.createCell( 14).setCellValue(rm.getGuard_required());
					row.createCell( 15).setCellValue(rm.getRemarks());
					counter++;
					System.out.println("row Added :- " + counter);
				}
			}
			fileOutputStream = new FileOutputStream(filename);
			hssfWorkbook.write(fileOutputStream);
			fileOutputStream.close();
//			msg.put("err_msg","success");
//			msg.put("err_msg","success");
//			System.out.println("Excel Sucessfully Downloaded.");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			msg.put("err_msg", "error_exist");
			msg.put("err_type","success");
			System.out.println("Excel Sucessfully Downloaded.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msg.put("err_type", "fail");
			e.printStackTrace();
		}
System.out.println("error msg download data:- "+msg);
		return msg;
	}
	
public JSONArray getStartandEndDate(String strdiv) {
		
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String current_date = date.toString();
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();

		JSONArray jarr = new JSONArray();

		try {
			JSONObject jsobj=new JSONObject(strdiv);
			String eqlid=jsobj.getString("e_qlid");
			String ecab=jsobj.getString("e_cab");
			String eshift=jsobj.getString("e_sid");
			PreparedStatement ps = connection.prepareStatement("select Start_Date,End_Date,Pickup_Time from ncab_roster_tbl where Cab_No='"+ecab+"'and Shift_Id='"+eshift+"' and Emp_Qlid='"+eqlid+"' and Emp_Status='active' and current_date between Start_Date and End_Date");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject json = new JSONObject();
				json.put("sdate", rs.getString(1));
				json.put("edate", rs.getString(2));
				json.put("pickt", rs.getString(3));

				jarr.put(json);
			}
				System.out.println(jarr);
		} catch (Exception e) {
			System.out.println("error in imp :- " + e.getMessage());
		}
		return jarr;
	}
		

}
