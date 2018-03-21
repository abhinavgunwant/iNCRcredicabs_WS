package ncab.dao.impl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import ncab.dao.DBConnectionUpd;



public class VendorServiceImpl {

	public JSONObject getVendorDetails(){
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarray = new JSONArray();
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement("SELECT * FROM ncab_vendor_master_tbl ORDER BY vendor_status , vendor_name ASC");
			ResultSet rs = ps.executeQuery();
			System.out.println(rs);
			String id;
			String name="";
			String bussAddr="";
			String bussType="";
			String venContact="";
			String venEmail="";
			String website;	
			String pan="";
			String gstNum;
			int cabs_prov;
			String supervisorName="";
			String supContact="";
			String supEmail;
			String manName;
			String manContact;
			String manEmail;
			String ownerName;
			String ownerContact;
			String ownerEmail;
			int vendorStatus;
			Date agreementExpiry;

			//int markDeleted=0;


			while(rs.next()){
				JSONObject jsonresponse = new JSONObject();
				id=rs.getString(1);
				name=rs.getString(2);
				bussAddr=rs.getString(3);
				bussType=rs.getString(4);
				venContact=rs.getString(5);
				cabs_prov=rs.getInt(6);
				venEmail=rs.getString(7);
				website=rs.getString(8);
				pan=rs.getString(9);
				gstNum=rs.getString(10);
				vendorStatus=rs.getInt(11);
				agreementExpiry=rs.getDate(12);
				System.out.println(rs.getDate(12));
				System.out.println(agreementExpiry.toString());
				supervisorName=rs.getString(13);
				supContact=rs.getString(14);
				supEmail=rs.getString(15);
				manName=rs.getString(16);
				manContact=rs.getString(17);
				manEmail=rs.getString(18);
				ownerName=rs.getString(19);
				ownerContact=rs.getString(20);
				ownerEmail=rs.getString(21);

				jsonresponse.put("id", id);
				jsonresponse.put("name", name );
				jsonresponse.put("bussAddr", bussAddr);
				jsonresponse.put("bussType", bussType);
				jsonresponse.put("venContact", venContact);
				jsonresponse.put("website", website);
				jsonresponse.put("pan", pan);
				jsonresponse.put("cabs_prov",cabs_prov);
				//jsonresponse.put("idProof", imgId);
				jsonresponse.put("gstnum", gstNum);
				jsonresponse.put("supervisorName", supervisorName);
				jsonresponse.put("venEmail", venEmail);
				jsonresponse.put("supContact", supContact);
				jsonresponse.put("supEmail", supEmail);
				jsonresponse.put("manName", manName);
				jsonresponse.put("manContact", manContact);
				jsonresponse.put("manEmail", manEmail);
				jsonresponse.put("ownerName", ownerName);
				jsonresponse.put("ownerContact", ownerContact);
				jsonresponse.put("ownerEmail", ownerEmail);
				jsonresponse.put("status",vendorStatus);
				//System.out.println(jsonresponse);
				//System.out.println(agreementExpiry);
				jsonresponse.put("agreementExpiry",agreementExpiry.toString());
				//System.out.println(jsonresponse);
				jsonarray.put(jsonresponse);
				//System.out.println(jsonarray);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return  new JSONObject().put("result", jsonarray);
	}


public JSONObject deleteVendorDetailsByVendorID(JSONObject jsonrequest){
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
	    int i = 0;
		JSONObject jsonreq = new JSONObject();
		
        try 
         {
	      jsonreq = jsonrequest.getJSONObject("request");
	      System.out.println(jsonreq.toString());
	      String vid = jsonreq.getString("vendor_id");
	      System.out.println(vid);
	      PreparedStatement ps1 = connection.prepareStatement("update ncab_driver_master_tbl set driver_status=1 where vendor_id = ?");
	      ps1.setString(1,  vid);
	    	 i=ps1.executeUpdate();
	    	 PreparedStatement ps2 = connection.prepareStatement("update ncab_cab_master_tbl set cab_status=1 where vendor_id = ?");
		      ps2.setString(1,  vid);
		    	 i=ps2.executeUpdate();
	      PreparedStatement ps = connection.prepareStatement("update ncab_vendor_master_tbl set vendor_status=1 where vendor_id = ?");
	      ps.setString(1,  vid);
	    	 i=ps.executeUpdate();
	    	 System.out.println(i);
	
         } catch (SQLException e)
        {
        	 // TODO Auto-generated catch block
        	 e.printStackTrace();
        }
		
        finally {
        	if (connection != null) {
        		try
        		{
        			connection.close();
        		}
        		catch (SQLException e) 
        		{
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }		
		if(i>0)
		{
		return new JSONObject().put("result", "true");		
		
		}
		else
			return new JSONObject().put("result", "false");
	}

	public JSONObject getVendorDetailsByKey(JSONObject jsonrequest) {
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarray = new JSONArray();
		JSONObject jsonreq = new JSONObject();

		try {
			jsonreq = jsonrequest.getJSONObject("request");
			Iterator<String> keys = jsonreq.keys();
			String key = "title";
			if (keys.hasNext()) {
				key = (String) keys.next(); // First key
			}

			String value = jsonreq.getString(key);
			System.out.println(key + ":" + value);
			String sql="";
			if(!key.equals("vendor_status")){
				sql = "select * from ncab_vendor_master_tbl where " + key + " like '%" + value + "%' order by vendor_status, vendor_name ";
			}
			else{
				int v=Integer.parseInt(value);
				System.out.println(v);
				sql = "select * from ncab_vendor_master_tbl where " + key + " = "+v+" order by vendor_status, vendor_name ";
			}
			System.out.println(sql);
			PreparedStatement ps = connection.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			String id;
			String name="";
			String bussAddr="";
			String bussType="";
			String venContact="";
			String venEmail="";
			String website;	
			String pan="";
			String gstNum;
			String supervisorName="";
			String supContact="";
			String supEmail;
			int cabs_prov;
			String manName;
			String manContact;
			String manEmail;
			String ownerName;
			String ownerContact;
			String ownerEmail;
			int vendorStatus;
			Date agreementExpiry;
			while (rs.next()) {

				JSONObject jsonresponse = new JSONObject();
				id=rs.getString(1);
				name=rs.getString(2);
				bussAddr=rs.getString(3);
				bussType=rs.getString(4);
				venContact=rs.getString(5);
				cabs_prov=rs.getInt(6);
				venEmail=rs.getString(7);
				website=rs.getString(8);
				pan=rs.getString(9);
				gstNum=rs.getString(10);
				vendorStatus=rs.getInt(11);
				agreementExpiry=rs.getDate(12);
				supervisorName=rs.getString(13);
				supContact=rs.getString(14);
				supEmail=rs.getString(15);
				manName=rs.getString(16);
				manContact=rs.getString(17);
				manEmail=rs.getString(18);
				ownerName=rs.getString(19);
				ownerContact=rs.getString(20);
				ownerEmail=rs.getString(21);

				jsonresponse.put("id", id);
				jsonresponse.put("name", name );
				jsonresponse.put("bussAddr", bussAddr);
				jsonresponse.put("bussType", bussType);
				jsonresponse.put("venContact", venContact);
				jsonresponse.put("website", website);
				jsonresponse.put("pan", pan);
				//jsonresponse.put("idProof", imgId);
				jsonresponse.put("gstnum", gstNum);
				jsonresponse.put("supervisorName", supervisorName);

				jsonresponse.put("supContact", supContact);
				jsonresponse.put("supEmail", supEmail);
				jsonresponse.put("manName", manName);
				jsonresponse.put("manContact", manContact);
				jsonresponse.put("manEmail", manEmail);
				jsonresponse.put("ownerName", ownerName);
				jsonresponse.put("ownerContact", ownerContact);
				jsonresponse.put("ownerEmail", ownerEmail);
				jsonresponse.put("status",vendorStatus);
				jsonresponse.put("agreementExpiry", agreementExpiry.toString());
				jsonarray.put(jsonresponse);
			}


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return new JSONObject().put("result", jsonarray);
	}

	public String addVendorList(String vendor_name, String business_type, String vendor_contact_num, String vendor_mail_id, String website, String pan_id, String gst_num, int vendor_status, java.util.Date date1, int cabs_provided, String business_address, String supervisor_name, String sup_contact_num, String sup_mail_id, String manager_name, String manager_contact_num, String manager_mail_id, String owner_name, String owner_contact_num, String owner_mail_id )throws Exception 
	{
		int x=0;
		Date d1 = new Date(date1.getTime());
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();
		try {

			String query = " insert into ncab_vendor_master_tbl (vendor_name, business_address, business_type, vendor_contact_num, cabs_provided, vendor_mail_id, website, pan_id, gst_num, vendor_status, agreement_expiry_date, supervisor_name, sup_contact_num, sup_mail_id, manager_name, manager_contact_num, manager_mail_id, owner_name, owner_contact_num, owner_mail_id)" + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = connection.prepareStatement(query);

			ps.setString(1,vendor_name);
			ps.setString(2,business_address);
			ps.setString(3,business_type);
			ps.setString(4,vendor_contact_num);
			ps.setInt(5,cabs_provided);
			ps.setString(6,vendor_mail_id);
			ps.setString(7,website);
			ps.setString(8,pan_id);
			ps.setString(9,gst_num);
			ps.setInt(10,vendor_status);
			ps.setDate(11,d1);
			ps.setString(12,supervisor_name);
			ps.setString(13,sup_contact_num);
			ps.setString(14,sup_mail_id);
			ps.setString(15,manager_name);
			ps.setString(16,manager_contact_num);
			ps.setString(17,manager_mail_id);
			ps.setString(18,owner_name);
			ps.setString(19,owner_contact_num);
			ps.setString(20,owner_mail_id);


			x = ps.executeUpdate();
			if(x==1){
				return "Insert Data success";
			}

			connection.close();

		} catch (Exception e) {
			throw e;
		}
		return "Insert Data success";
	}


	public JSONObject updatevendor(int vendor_id,String vendor_name, String business_type, String vendor_contact_num, String vendor_mail_id, String website, String pan_id, String gst_num,int vendor_status, Date date1, String business_address, String supervisor_name, String sup_contact_num, String sup_mail_id, String manager_name, String manager_contact_num, String manager_mail_id, String owner_name, String owner_contact_num, String owner_mail_id )throws Exception 
	{      
		//java.sql.Date d1 = new Date(date1.getTime());

		System.out.println("working1");
		//     int x=0;
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();
		try {
			System.out.println("working5");


			String query = " update ncab_vendor_master_tbl set vendor_name =?,business_address=?, vendor_contact_num=?, vendor_mail_id=?, website = ?, pan_id = ?, gst_num = ?,vendor_status = ?, agreement_expiry_date = ?, supervisor_name = ?, sup_contact_num = ?, sup_mail_id= ? , manager_name = ?,manager_contact_num= ?,manager_mail_id= ?,owner_name= ?,owner_contact_num= ?,owner_mail_id= ? where vendor_id = ?" ;
			PreparedStatement ps = connection.prepareStatement(query);
			System.out.println(vendor_id);
			//ps.setInt(1,vendor_id);
			ps.setString(1,vendor_name);
			ps.setString(2,business_address);
			ps.setString(3,vendor_contact_num);
			ps.setString(4,vendor_mail_id);
			ps.setString(5,website);
			ps.setString(6,pan_id);
			ps.setString(7,gst_num);
			ps.setInt(8,vendor_status);
			ps.setDate(9,date1);
			ps.setString(10,supervisor_name);
			ps.setString(11,sup_contact_num);
			ps.setString(12,sup_mail_id);
			ps.setString(13,manager_name);
			ps.setString(14,manager_contact_num);
			ps.setString(15,manager_mail_id);
			ps.setString(16,owner_name);
			ps.setString(17,owner_contact_num);
			ps.setString(18,owner_mail_id);
			ps.setInt(19,vendor_id);

			int a= ps.executeUpdate();
			System.out.println(a);
			System.out.println("working8");

			return new JSONObject().put("result","works");


		} catch (Exception e){
			throw e;
		}

	}

	public JSONObject enableVendorDetailsById(JSONObject jsonrequest){

		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		int i = 0;
		JSONObject jsonreq = new JSONObject();

		try 
		{
			jsonreq = jsonrequest.getJSONObject("request");
			System.out.println(jsonreq.toString());
			String vid = jsonreq.getString("vendor_id");
			System.out.println(vid);
			PreparedStatement ps = connection.prepareStatement("update ncab_vendor_master_tbl set vendor_status=0 where vendor_id = ?");
			ps.setString(1,  vid);
			i=ps.executeUpdate();
			System.out.println(i);

		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (connection != null) {
				try
				{
					connection.close();
				}
				catch (SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		if(i>0)
		{
			return new JSONObject().put("result", "true");		

		}
		else
			return new JSONObject().put("result", "false");
	}


	public JSONObject getCabDetails()
	{
		String query="SELECT ncab_cab_master_tbl.vendor_id,ncab_cab_master_tbl.cab_license_plate_no,model,fuel_type,contracted_or_owned,cab_type,cab_rate,manufacture_date,reg_certi,pollution_certi,fitness_certi,insurance_certi,entry_tax_haryana_certi,entry_tax_delhi_certi,entry_tax_up_certi,poll_certi_exp_date,fit_certi_exp_date,insur_certi_exp_date,entry_tax_haryana_exp_date,entry_tax_delhi_exp_date,entry_tax_up_exp_date,driver_name,d_contact_num,vendor_name,cab_status,driver_type FROM ncab_cab_master_tbl,ncab_driver_master_tbl,ncab_vendor_master_tbl WHERE ncab_cab_master_tbl. cab_license_plate_no=ncab_driver_master_tbl.cab_license_plate_no AND ncab_vendor_master_tbl.vendor_id=ncab_cab_master_tbl.vendor_id order by cab_status";

		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarray = new JSONArray();
		PreparedStatement ps;
		try{
			//		ps = connection.prepareStatement("SELECT * FROM cab_tbl,driver_tbl WHERE cab_status = 0 AND driver_status=0 AND driver_tbl.cab_license_plate_no=cab_tbl.cab_license_plate_no" );
			ps= connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			String vendor_id="";
			String cab_no="";
			String model="";
			String fuel="";
			String cab_type="";
			int cab_rate;
			String contract_owned;
			String reg_certi;
			String poll_certi;
			String fit_certi;
			String insur_certi;
			String tax_haryana_certi;
			String tax_up_certi;
			String tax_delhi_certi;

			String manufacture_date="";
			String poll_exp;
			String fit_exp;
			String insur_exp;
			String tax_haryana_exp;
			String tax_up_exp;
			String tax_delhi_exp;
			String driver_name;
			String driver_contact_num;
			String vendor_name;
			int cab_status;
			String driver_type;


			while(rs.next())
			{
				JSONObject jsonresponse = new JSONObject();

				vendor_id=rs.getString(1);
				cab_no=rs.getString(2);
				model=rs.getString(3);
				fuel=rs.getString(4);
				contract_owned=rs.getString(5);
				cab_type=rs.getString(6);
				if(cab_type.equals("BIG"))
					cab_type="6";
				else
					cab_type="4";
				cab_rate=rs.getInt(7);
				manufacture_date=rs.getString(8);

				reg_certi=rs.getString(9);
				poll_certi=rs.getString(10);
				fit_certi=rs.getString(11);
				insur_certi=rs.getString(12);
				tax_haryana_certi=rs.getString(13);
				tax_delhi_certi=rs.getString(14);
				tax_up_certi=rs.getString(15);
				poll_exp=rs.getString(16);
				fit_exp=rs.getString(17);
				insur_exp=rs.getString(18);
				tax_haryana_exp=rs.getString(19);

				tax_delhi_exp=rs.getString(20);
				tax_up_exp=rs.getString(21);
				driver_name=rs.getString(22);
				driver_contact_num=rs.getString(23);
				vendor_name=rs.getString(24);
				cab_status=rs.getInt(25);
				driver_type=rs.getString(26);
				System.out.println(cab_status);
				jsonresponse.put("vendor_id", vendor_id);
				jsonresponse.put("cab_no", cab_no);
				jsonresponse.put("model", model);
				jsonresponse.put("fuel", fuel);
				jsonresponse.put("cab_type", cab_type);
				jsonresponse.put("cab_rate", cab_rate);
				jsonresponse.put("contract_owned", contract_owned);
				jsonresponse.put("manufacture_date", manufacture_date);
				jsonresponse.put("poll_exp", poll_exp);
				jsonresponse.put("fit_exp", fit_exp);
				jsonresponse.put("insur_exp", insur_exp);
				jsonresponse.put("tax_haryana_exp", tax_haryana_exp);
				jsonresponse.put("tax_up_exp", tax_up_exp);
				jsonresponse.put("tax_delhi_exp", tax_delhi_exp);
				jsonresponse.put("driver_name", driver_name);
				jsonresponse.put("driver_contact_num", driver_contact_num);
				jsonresponse.put("vendor_name", vendor_name);
				jsonresponse.put("reg_certi", reg_certi);
				jsonresponse.put("poll_certi",poll_certi);
				jsonresponse.put("fit_certi", fit_certi);
				jsonresponse.put("insur_certi",insur_certi);
				jsonresponse.put("tax_haryana_certi", tax_haryana_certi);
				jsonresponse.put("tax_delhi_certi", tax_delhi_certi);
				jsonresponse.put("tax_up_certi",tax_up_certi);
				jsonresponse.put("status", cab_status);
				jsonresponse.put("driver_type", driver_type);



				jsonarray.put(jsonresponse);


			}

		} catch (SQLException e) {
			//		 TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONObject().put("result", jsonarray);

	}
public JSONObject enableCabDetailsById(JSONObject jsonrequest){
		
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
	    int i = 0;
		JSONObject jsonreq = new JSONObject();
		int vendor_status=-1;
		
        try 
         {
	      jsonreq = jsonrequest.getJSONObject("request");
	      System.out.println(jsonreq.toString());
	      String vid = jsonreq.getString("vendor_id");
	      System.out.println(vid);
	      String sql="Select vendor_status from ncab_vendor_master_tbl where vendor_id = ?";
	      System.out.println("sql"+sql);
	      PreparedStatement ps1 = connection.prepareStatement(sql);
	      ps1.setString(1, vid);
	      ResultSet rs = ps1.executeQuery();
	      while(rs.next()){
	    	  vendor_status=rs.getInt(1);
	      }
	      System.out.println("ven"+vendor_status);
	      if(vendor_status==0){
	      PreparedStatement ps = connection.prepareStatement("update ncab_cab_master_tbl set cab_status=0 where vendor_id = ?");
	      ps.setString(1,  vid);
	    	 i=ps.executeUpdate();
	    	 System.out.println(i);
	      }
         } catch (SQLException e)
        {
        	 // TODO Auto-generated catch block
        	 e.printStackTrace();
        }
		
        finally {
        	if (connection != null) {
        		try
        		{
        			connection.close();
        		}
        		catch (SQLException e) 
        		{
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }		
		if(i>0)
		{
		return new JSONObject().put("result", "true");		
		
		}
		else
			return new JSONObject().put("result", "false");
	}

public JSONObject deleteCabDetailsByVendorID(JSONObject jsonrequest){
	DBConnectionUpd dbconnection = new DBConnectionUpd();
	Connection connection = dbconnection.getConnection();
    int i = 0;
    ResultSet rs=null;
	JSONObject jsonreq = new JSONObject();
	int j=0;
    try 
     {
      jsonreq = jsonrequest.getJSONObject("request");
      System.out.println(jsonreq.toString());
      int vid = jsonreq.getInt("vendor_id");
      System.out.println(vid);

//      PreparedStatement ps2=connection.prepareStatement("select * from roastertable where cab_no=?");
//      ps2.setString(1,  vid);
//      rs=ps2.executeQuery();
//      
      PreparedStatement ps1=connection.prepareStatement("update ncab_driver_master_tbl set driver_status=1 where vendor_id = ?");
      ps1.setInt(1,  vid);
      j=ps1.executeUpdate();
    //  System.out.println(j);
      PreparedStatement ps = connection.prepareStatement("update ncab_cab_master_tbl set cab_status=1 where vendor_id = ?");
      ps.setInt(1,  vid);
          i=ps.executeUpdate();
    	 System.out.println(i);

     } catch (SQLException e)
    {
    	 // TODO Auto-generated catch block
    	 e.printStackTrace();
    }
	
    finally {
    	if (connection != null) {
    		try
    		{
    			connection.close();
    		}
    		catch (SQLException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }		
	if(i>0)
	{
	return new JSONObject().put("result", "true");		
	
	}
	else
		return new JSONObject().put("result", "false");
}

	public Response getDriverType(String cabno)
	{
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		//JSONArray jsonarray = new JSONArray();
		//JSONObject jsonreq = new JSONObject();

		try {

			//System.out.println("hello"+cabno);
			String sql="SELECT  COUNT(driver_type) FROM ncab_driver_master_tbl,ncab_cab_master_tbl WHERE ncab_cab_master_tbl.cab_license_plate_no=ncab_driver_master_tbl.cab_license_plate_no AND ncab_driver_master_tbl.cab_license_plate_no = '" + cabno + "' ";
			int count=0;
			String message="";
			PreparedStatement ps = connection.prepareStatement(sql);

			//System.out.println(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				count=rs.getInt(1);
			}

			System.out.println(count);
			if(count==0)
				message="hell";
			if(count==2)
				message="2";
			if(count==1)
			{
				//message="1";
				sql="SELECT  driver_type FROM ncab_driver_master_tbl,ncab_cab_master_tbl WHERE ncab_cab_master_tbl.cab_license_plate_no=ncab_driver_master_tbl.cab_license_plate_no AND ncab_driver_master_tbl.cab_license_plate_no = '" + cabno + "' ";
				ps = connection.prepareStatement(sql);
				rs = ps.executeQuery();

				//message="hello";
				while(rs.next())
				{
					message=rs.getString(1);
				}

			}
			System.out.println(message);

			//	int count=(SELECT COUNT(*) FROM ncab_driver_master_tbl,ncab_cab_master_tbl WHERE ncab_cab_master_tbl.cab_license_plate_no=ncab_driver_master_tbl.cab_license_plate_no AND ncab_driver_master_tbl.cab_license_plate_no='HR6D3456');
			return Response.status(200).entity(message)	.build();
			//return new JSONObject().put("result", message);
		}

		catch (SQLException e) {
			//	 TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					//		 TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


		//return new JSONObject().put("result", jsonarray);
		return Response.status(200).entity("error")	.build();



	}

	public JSONObject getDriverDetails()
	{

		String query="SELECT ncab_vendor_master_tbl.vendor_id,driver_name,d_contact_num,driver_type,driver_local_address,driver_permanent_address,driver_photo,police_verification,driver_local_address_proof,driver_permanent_address_proof,driving_license,ncab_driver_master_tbl.cab_license_plate_no,vendor_name,license_exp_date,driver_status,driver_id FROM ncab_cab_master_tbl,ncab_driver_master_tbl,ncab_vendor_master_tbl WHERE ncab_cab_master_tbl. cab_license_plate_no=ncab_driver_master_tbl. cab_license_plate_no AND ncab_vendor_master_tbl.vendor_id=ncab_cab_master_tbl.vendor_id";

		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarray = new JSONArray();

		PreparedStatement ps;

		try{
			//ps = connection.prepareStatement("SELECT driver_name,d_contact_num,driver_local_address,driver_permanent_address,driver_photo,police_verification,driver_local_address_proof,driver_permanent_address_proof,driving_license,driver_tbl.cab_license_plate_no,vendor_name,license_exp_date FROM cab_tbl,driver_tbl,vendor_tbl WHERE cab_status = 0 AND driver_status=0 AND vendor_status=0 AND cab_tbl. cab_license_plate_no=driver_tbl. cab_license_plate_no AND vendor_tbl.vendor_id=cab_tbl.vendor_id");
			ps=connection.prepareStatement(query);

			//System.out.println("query"+query);

			ResultSet rs = ps.executeQuery();

			int vendor_id;
			String d_name;
			String d_contact_num;
			String driver_type;
			String d_local_add;
			String d_permanent_add;
			String d_license;
			String license_exp_date;
			String driver_photo;
			String police_verification;
			String d_local_add_proof;
			String d_permanent_add_proof;
			String cab_no;
			String vendor_name;
			int driver_status;
			int driver_id;
			


			while(rs.next())
			{

				JSONObject jsonresponse = new JSONObject();
				vendor_id=rs.getInt(1);
				d_name=rs.getString(2);
				d_contact_num=rs.getString(3);
				driver_type=rs.getString(4);
				d_local_add=rs.getString(5);
				d_permanent_add=rs.getString(6);
				driver_photo=rs.getString(7);
				police_verification=rs.getString(8);
				d_local_add_proof=rs.getString(9);
				d_permanent_add_proof=rs.getString(10);
				d_license=rs.getString(11);
				cab_no=rs.getString(12);
				vendor_name=rs.getString(13);
				license_exp_date=rs.getString(14);
				driver_status=rs.getInt(15);
				driver_id=rs.getInt(16);
				
				jsonresponse.put("vendor_id", vendor_id);
				jsonresponse.put("d_name", d_name);
				jsonresponse.put("d_contact_num", d_contact_num);
				jsonresponse.put("driver_type", driver_type);
				jsonresponse.put("d_local_add", d_local_add);
				jsonresponse.put("d_permanent_add", d_permanent_add);
				jsonresponse.put("driver_photo", driver_photo);
				jsonresponse.put("police_verification", police_verification);
				jsonresponse.put("d_local_add_proof", d_local_add_proof);
				jsonresponse.put("d_permanent_add_proof", d_permanent_add_proof);
				jsonresponse.put("d_license", d_license);
				jsonresponse.put("cab_no", cab_no);
				jsonresponse.put("vendor_name", vendor_name);
				jsonresponse.put("license_exp_date", license_exp_date);
				jsonresponse.put("status", driver_status);
				jsonresponse.put("driver_id", driver_id);
				
				jsonarray.put(jsonresponse);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONObject().put("result", jsonarray);
	}


	public Response AddCab(int vendor_id, String onrshp, String license_plate_number, String model,  String fuel,String type, int occupancy, int cab_rate , String Rcert,String Pcert,String Fcert,String icert,String entry_tax_haryana_certi,String entry_tax_delhi_certi,String entry_tax_up_certi, java.util.Date date1, java.util.Date date2, java.util.Date date3, java.util.Date date4, java.util.Date date5, java.util.Date date6, java.util.Date date7 ) throws Exception
	{
		int cab_status=0;
		java.sql.Date d1 = new java.sql.Date(date1.getTime());
		java.sql.Date  d2 = new java.sql.Date (date2.getTime());
		java.sql.Date  d3 = new java.sql.Date (date3.getTime());
		java.sql.Date d4 = new java.sql.Date(date4.getTime());
		java.sql.Date  d5 = new java.sql.Date (date5.getTime());
		java.sql.Date  d6 = new java.sql.Date (date6.getTime());
		java.sql.Date d7 = new java.sql.Date(date7.getTime());
		int x=0;


		try {
			DBConnectionUpd database= new DBConnectionUpd();
			Connection connection = database.getConnection();
			String query = " insert into ncab_cab_master_tbl (vendor_id, cab_license_plate_no, model, fuel_type, contracted_or_owned, cab_type,cab_capacity, cab_rate, reg_certi ,pollution_certi,fitness_certi,insurance_certi,entry_tax_haryana_certi ,entry_tax_delhi_certi,entry_tax_up_certi ,  manufacture_date, cab_status, poll_certi_exp_date, fit_certi_exp_date, insur_certi_exp_date, entry_tax_haryana_exp_date, entry_tax_delhi_exp_date, entry_tax_up_exp_date)" + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1,vendor_id);
			ps.setString(2,license_plate_number);
			ps.setString(3,model);
			ps.setString(4,fuel);
			ps.setString(5,onrshp);
			ps.setString(6,type);
			ps.setInt(7, occupancy);
			ps.setInt(8,cab_rate);
			ps.setString(9, Rcert);
			ps.setString(10, Pcert);
			ps.setString(11, Fcert);
			ps.setString(12, icert);
			ps.setString(13, entry_tax_haryana_certi);
			ps.setString(14, entry_tax_delhi_certi);
			ps.setString(15, entry_tax_up_certi);
			ps.setDate(16,d1);
			ps.setInt(17, cab_status);
			ps.setDate(18,d2);
			ps.setDate(19,d3);
			ps.setDate(20,d4);
			ps.setDate(21,d5);
			ps.setDate(22,d6);
			ps.setDate(23,d7);



			x = ps.executeUpdate();
			if(x==1){
				connection.close();
				return Response.status(200).entity("Insert Data success").build();
				//return "Insert Data success";
			}else{
				connection.close();

				return Response.status(500).entity("Error").build();
			}



		} catch (Exception e) {
			throw e;
		}

	}


	public Response AddDriver(int vendor_id, String d_type,String first_Name, String dPhone_Nbr, String local_Address, String permanent_Address,String c_Plate_Nbr,
			java.util.Date license,String d_comercial_liscence,String d_police_verification,String d_local_Address_proof,String d_permanent_address_proof,String d_photo)throws Exception 
	{
		int driver_status=0;
		int x=0;
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();
		try {

			String query = " insert into ncab_driver_master_tbl (driver_type, vendor_id, driver_name, d_contact_num, driver_local_address, driver_permanent_address, cab_license_plate_no, license_exp_date, driving_license, police_verification, driver_local_address_proof, driver_permanent_address_proof, driver_photo, driver_status)" + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1,d_type);
			ps.setInt(2,vendor_id);
			ps.setString(3,first_Name);
			ps.setString(4,dPhone_Nbr);
			ps.setString(5,local_Address);
			ps.setString(6,permanent_Address);
			ps.setString(7,c_Plate_Nbr);
			java.sql.Date date=new  java.sql.Date(license.getTime());
			ps.setDate(8,date);

			ps.setString(9,d_comercial_liscence);
			ps.setString(10,d_police_verification);
			ps.setString(11,d_local_Address_proof);
			ps.setString(12,d_permanent_address_proof);
			ps.setString(13,d_photo);
			ps.setInt(14,driver_status);
			x = ps.executeUpdate();
			System.out.println("work ho raha hai");
			if(x==1){
				connection.close();

				return Response.status(200).entity("Insert Data success").build();

			}else
			{
				connection.close();
				return Response.status(200).entity("Failed").build();
			}


		} catch (Exception e) {
			throw e;
		}
	}

	public JSONObject getCabDetailsByKey(JSONObject jsonrequest)
	{

		String query="SELECT ncab_cab_master_tbl.vendor_id,ncab_cab_master_tbl.cab_license_plate_no,model,fuel_type,contracted_or_owned,cab_type,cab_rate,manufacture_date,reg_certi,pollution_certi,fitness_certi,insurance_certi,entry_tax_haryana_certi,entry_tax_delhi_certi,entry_tax_up_certi,poll_certi_exp_date,fit_certi_exp_date,insur_certi_exp_date,entry_tax_haryana_exp_date,entry_tax_delhi_exp_date,entry_tax_up_exp_date,driver_name,d_contact_num,vendor_name,cab_status,driver_type FROM ncab_cab_master_tbl,ncab_driver_master_tbl,ncab_vendor_master_tbl WHERE ncab_cab_master_tbl. cab_license_plate_no=ncab_driver_master_tbl.cab_license_plate_no AND ncab_vendor_master_tbl.vendor_id=ncab_cab_master_tbl.vendor_id";

		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarray = new JSONArray();
		JSONObject jsonreq = new JSONObject();

		try {
			jsonreq = jsonrequest.getJSONObject("request");
			Iterator<String> keys = jsonreq.keys();
			String key = "title";
			if (keys.hasNext()) {
				key = (String) keys.next();  //First key
			}
			String value = jsonreq.getString(key);
			System.out.println(key + ":" + value);
			String sql="";



			if(key.equals("cab_type")){
				if(value.equals("6"))
				{
					value="BIG";
				}
				if(value.equals("4"))
				{
					value="SMALL";
				}
			}

			if(key.equals("cab_license_plate_no"))
			{
				key="ncab_cab_master_tbl."+key;
			}
			//System.out.println(key);
			sql=query+" AND "+key+" like '%"+value+"%'";
			//	System.out.println(query+" AND "+key+"='"+value+"'");
			PreparedStatement ps = connection.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			String vendor_id="";
			String cab_no="";
			String model="";
			String fuel="";
			String cab_type="";
			int cab_rate;
			String contract_owned;
			String reg_certi;
			String poll_certi;
			String fit_certi;
			String insur_certi;
			String tax_haryana_certi;
			String tax_up_certi;
			String tax_delhi_certi;

			String manufacture_date="";
			String poll_exp;
			String fit_exp;
			String insur_exp;
			String tax_haryana_exp;
			String tax_up_exp;
			String tax_delhi_exp;
			String driver_name;
			String driver_contact_num;
			String vendor_name;
			int cab_status;
			String driver_type;


			while(rs.next())
			{
				JSONObject jsonresponse = new JSONObject();

				vendor_id=rs.getString(1);
				cab_no=rs.getString(2);
				model=rs.getString(3);
				fuel=rs.getString(4);
				contract_owned=rs.getString(5);
				cab_type=rs.getString(6);
				if(cab_type.equals("BIG"))
					cab_type="6";
				else
					cab_type="4";
				cab_rate=rs.getInt(7);
				manufacture_date=rs.getString(8);

				reg_certi=rs.getString(9);
				poll_certi=rs.getString(10);
				fit_certi=rs.getString(11);
				insur_certi=rs.getString(12);
				tax_haryana_certi=rs.getString(13);
				tax_delhi_certi=rs.getString(14);
				tax_up_certi=rs.getString(15);
				poll_exp=rs.getString(16);
				fit_exp=rs.getString(17);
				insur_exp=rs.getString(18);
				tax_haryana_exp=rs.getString(19);

				tax_delhi_exp=rs.getString(20);
				tax_up_exp=rs.getString(21);
				driver_name=rs.getString(22);
				driver_contact_num=rs.getString(23);
				vendor_name=rs.getString(24);
				cab_status=rs.getInt(25);
				driver_type=rs.getString(26);
				jsonresponse.put("vendor_id", vendor_id);
				jsonresponse.put("cab_no", cab_no);
				jsonresponse.put("model", model);
				jsonresponse.put("fuel", fuel);
				jsonresponse.put("cab_type", cab_type);
				jsonresponse.put("cab_rate", cab_rate);
				jsonresponse.put("contract_owned", contract_owned);
				jsonresponse.put("manufacture_date", manufacture_date);
				jsonresponse.put("poll_exp", poll_exp);
				jsonresponse.put("fit_exp", fit_exp);
				jsonresponse.put("insur_exp", insur_exp);
				jsonresponse.put("tax_haryana_exp", tax_haryana_exp);
				jsonresponse.put("tax_up_exp", tax_up_exp);
				jsonresponse.put("tax_delhi_exp", tax_delhi_exp);
				jsonresponse.put("driver_name", driver_name);
				jsonresponse.put("driver_contact_num", driver_contact_num);
				jsonresponse.put("vendor_name", vendor_name);
				jsonresponse.put("reg_certi", reg_certi);
				jsonresponse.put("poll_certi",poll_certi);
				jsonresponse.put("fit_certi", fit_certi);
				jsonresponse.put("insur_certi",insur_certi);
				jsonresponse.put("tax_haryana_certi", tax_haryana_certi);
				jsonresponse.put("tax_delhi_certi", tax_delhi_certi);
				jsonresponse.put("tax_up_certi",tax_up_certi);
				jsonresponse.put("status", cab_status);
				jsonresponse.put("driver_type", driver_type);


				jsonarray.put(jsonresponse);


			}
		}
		catch (SQLException e) {
			//	 TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					//		 TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


		return new JSONObject().put("result", jsonarray);

	}

	public JSONObject getDriverDetailsByKey(JSONObject jsonrequest)
	{

		String query="SELECT ncab_vendor_master_tbl.vendor_id,driver_name,d_contact_num,driver_type,driver_local_address,driver_permanent_address,driver_photo,police_verification,driver_local_address_proof,driver_permanent_address_proof,driving_license,ncab_driver_master_tbl.cab_license_plate_no,vendor_name,license_exp_date,driver_status,driver_id FROM ncab_cab_master_tbl,ncab_driver_master_tbl,ncab_vendor_master_tbl WHERE ncab_cab_master_tbl. cab_license_plate_no=ncab_driver_master_tbl. cab_license_plate_no AND ncab_vendor_master_tbl.vendor_id=ncab_cab_master_tbl.vendor_id";

		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		JSONArray jsonarray = new JSONArray();
		JSONObject jsonreq = new JSONObject();
		PreparedStatement ps;

		try {
			jsonreq = jsonrequest.getJSONObject("request");
			Iterator<String> keys = jsonreq.keys();
			String key = "title";
			if (keys.hasNext()) {
				key = (String) keys.next(); // First key
			}
			String value = jsonreq.getString(key);
			System.out.println(key + ":" + value);
			String sql="";
			sql=query+" AND "+key+" like '%"+value+"%'";
			System.out.println(sql);
			ps=connection.prepareStatement(sql);

			//System.out.println("query"+query);

			ResultSet rs = ps.executeQuery();
			int vendor_id;
			String d_name;
			String d_contact_num;
			String driver_type;
			String d_local_add;
			String d_permanent_add;
			String d_license;
			String license_exp_date;
			String driver_photo;
			String police_verification;
			String d_local_add_proof;
			String d_permanent_add_proof;
			String cab_no;
			String vendor_name;
			int driver_status;
			int driver_id;


			while(rs.next())
			{

				JSONObject jsonresponse = new JSONObject();
				vendor_id=rs.getInt(1);
				d_name=rs.getString(2);
				d_contact_num=rs.getString(3);
				driver_type=rs.getString(4);
				d_local_add=rs.getString(5);
				d_permanent_add=rs.getString(6);
				driver_photo=rs.getString(7);
				police_verification=rs.getString(8);
				d_local_add_proof=rs.getString(9);
				d_permanent_add_proof=rs.getString(10);
				d_license=rs.getString(11);
				cab_no=rs.getString(12);
				vendor_name=rs.getString(13);
				license_exp_date=rs.getString(14);
				driver_status=rs.getInt(15);
				driver_id=rs.getInt(16);
				
				jsonresponse.put("vendor_id", vendor_id);
				jsonresponse.put("d_name", d_name);
				jsonresponse.put("d_contact_num", d_contact_num);
				jsonresponse.put("driver_type", driver_type);
				jsonresponse.put("d_local_add", d_local_add);
				jsonresponse.put("d_permanent_add", d_permanent_add);
				jsonresponse.put("driver_photo", driver_photo);
				jsonresponse.put("police_verification", police_verification);
				jsonresponse.put("d_local_add_proof", d_local_add_proof);
				jsonresponse.put("d_permanent_add_proof", d_permanent_add_proof);
				jsonresponse.put("d_license", d_license);
				jsonresponse.put("cab_no", cab_no);
				jsonresponse.put("vendor_name", vendor_name);
				jsonresponse.put("license_exp_date", license_exp_date);
				jsonresponse.put("status", driver_status);
				jsonresponse.put("driver_id", driver_id);
				
				jsonarray.put(jsonresponse);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONObject().put("result", jsonarray);

	}

	public void saveToFile(InputStream inStream, String target)
			throws IOException {
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		out = new FileOutputStream(new File(target));
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}

	public void createFolderIfNotExists(String dirName)
			throws SecurityException {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
			theDir.mkdir();
		}
	}

	public Response updateCab(String onrshp, String cab_license_plate_number, String model,  String fuel_type,String type,int occupancy, int cab_rate , String Rcert,String Pcert,String Fcert,String icert,String entry_tax_haryana_certi,String entry_tax_delhi_certi,String entry_tax_up_certi, Date date1, Date date2, Date date3, Date date4, Date date5, Date date6, Date date7 )throws Exception 
	{
		int cab_status=0;
		//int vendor_id = 98898;

		int x=0;
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();

		try {

			String query = " update ncab_cab_master_tbl set model=?, fuel_type=?, contracted_or_owned=?, cab_type=?,cab_capacity=?, cab_rate=?,  manufacture_date=?,cab_status=?,reg_certi=? ,pollution_certi=?,fitness_certi=?,insurance_certi=?,entry_tax_haryana_certi=? ,entry_tax_delhi_certi=?,entry_tax_up_certi=? ,   poll_certi_exp_date=?, fit_certi_exp_date=?, insur_certi_exp_date=?, entry_tax_haryana_exp_date=?, entry_tax_delhi_exp_date=?, entry_tax_up_exp_date=? where cab_license_plate_no= ?";
			PreparedStatement ps = connection.prepareStatement(query);
			//ps.setInt(1,vendor_id);

			ps.setString(1,model);
			ps.setString(2,fuel_type);
			ps.setString(3,onrshp);
			ps.setString(4,type);
			ps.setInt(5, occupancy);
			ps.setInt(6,cab_rate);
			ps.setDate(7,date1);
			ps.setInt(8, cab_status);
			ps.setString(9, Rcert);
			ps.setString(10, Pcert);
			ps.setString(11, Fcert);
			ps.setString(12, icert);
			ps.setString(13, entry_tax_haryana_certi);
			ps.setString(14, entry_tax_delhi_certi);
			ps.setString(15, entry_tax_up_certi);


			ps.setDate(16,date2);
			ps.setDate(17,date3);
			ps.setDate(18,date4);
			ps.setDate(19,date5);
			ps.setDate(20,date6);
			ps.setDate(21,date7);
			ps.setString(22,cab_license_plate_number);


			x = ps.executeUpdate();
			System.out.println(x);
			if(x==1){
				return Response.status(200).type("application/json").entity(new JSONObject().put("result","Insert Data success").toString()).build();
				//return "Insert Data success";
			}

			connection.close();

		} catch (Exception e) {
			throw e;
		}
		return Response.status(200).type("application/json").entity(new JSONObject().put("result","Insert Data success").toString()).build();
		//return "Insert Data success";
	}

	public Response getAllDriverList(String cab_license_plate_number, String driver_name,String d_contact_num,String driver_type,String driver_local_address,String driver_permanent_address,String driving_license,String driver_local_address_proof,String driver_permanent_address_proof,String driver_photo,String police_verification, Date date1)throws Exception 
	{
		int driver_status=0;
		//int vendor_ id = 98898;
		//java.sql.Date d1 = new java.sql.Date(date1.getTime());
		//java.sql.Date  d2 = new java.sql.Date (date2.getTime());
		//java.sql.Date  d3 = new java.sql.Date (date3.getTime());
		//java.sql.Date d4 = new java.sql.Date(date4.getTime());
		//java.sql.Date  d5 = new java.sql.Date (date5.getTime());
		//java.sql.Date  d6 = new java.sql.Date (date6.getTime());
		//java.sql.Date d7 = new java.sql.Date(date7.getTime());
		int x=0;
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();

		try {

			String query = " update ncab_driver_master_tbl set driver_name=?,d_contact_num=?,driver_type=?,driver_local_address=?, driver_permanent_address=?,license_exp_date=?,driver_status=?,driving_license=?,driver_local_address_proof=?,driver_permanent_address_proof=?,driver_photo=?,police_verification=? where cab_license_plate_no=? ";
			PreparedStatement ps = connection.prepareStatement(query);
			//ps.setInt(1,driver_id);
			//	ps.setString(2,cab_license_plate_number);
			ps.setString(1,driver_name);
			ps.setString(2,d_contact_num);
			ps.setString(3,driver_type);
			ps.setString(4,driver_local_address);
			ps.setString(5, driver_permanent_address);
			ps.setDate(6,date1);
			ps.setInt(7,driver_status);
			ps.setString(8, driving_license);
			ps.setString(9,driver_local_address_proof);
			//ps.setString(12, icert);
			//	ps.setString(13, entry_tax_haryana_certi);
			//	ps.setString(14, entry_tax_delhi_certi);
			ps.setString(10,driver_permanent_address_proof);
			ps.setString(11, driver_photo);
			ps.setString(12,police_verification);
			ps.setString(13, cab_license_plate_number);
			//ps.setDate(13,d1);
			//ps.setInt(17, cab_status);
			//ps.setDate(18,d2);
			//ps.setDate(19,d3);
			//ps.setDate(20,d4);
			//ps.setDate(21,d5);
			//ps.setDate(22,d6);
			//ps.setDate(23,d7);



			x = ps.executeUpdate();
			if(x==1){
				return Response.status(200).type("application/json").entity(new JSONObject().put("result","Insert Data success").toString()).build();
				//return "Insert Data success";
			}

			connection.close();

		} catch (Exception e) {
			throw e;
		}
		return Response.status(200).type("application/json").entity(new JSONObject().put("result","Insert Data success").toString()).build();
		//return "Insert Data success";
	}
	

public JSONObject enableDriverDetailsById(JSONObject jsonrequest){
		
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
	    int i = 0;
		JSONObject jsonreq = new JSONObject();
		int vid=-1, driverid=-1;
		
	    try 
	     {
	      jsonreq = jsonrequest.getJSONObject("request");
	      System.out.println(jsonreq.toString());
	      String did = jsonreq.getString("driver_id");
	      System.out.println(did);
	      
	      PreparedStatement ps1 = connection.prepareStatement("select vendor_id from ncab_driver_master_tbl where driver_id = ?");
	      ps1.setString(1, did);
	      ResultSet rs = ps1.executeQuery();
	      while(rs.next()){
	    	   vid=rs.getInt(1);
	      }
	      
	      PreparedStatement ps2 = connection.prepareStatement("select cab_status from ncab_cab_master_tbl where vendor_id = ?");
	      ps2.setInt(1, vid);
	      ResultSet rs1 = ps2.executeQuery();
	      while(rs1.next()){
	    	  driverid=rs1.getInt(1);
	      }
	      System.out.println("driveriddd"+driverid);
	      
	      if(driverid==0){
	      PreparedStatement ps = connection.prepareStatement("update ncab_driver_master_tbl set driver_status=0 where driver_id = ?");
	      ps.setString(1,  did);
	    	 i=ps.executeUpdate();
	    	 System.out.println(i);
	      }
	     } catch (SQLException e)
	    {
	    	 // TODO Auto-generated catch block
	    	 e.printStackTrace();
	    }
		
	    finally {
	    	if (connection != null) {
	    		try
	    		{
	    			connection.close();
	    		}
	    		catch (SQLException e) 
	    		{
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
	    }		
		if(i>0)
		{
		return new JSONObject().put("result", "true");		
		
		}
		else
			return new JSONObject().put("result", "false");
	}


public JSONObject deleteDriverDetailsByDriverID(JSONObject jsonrequest){
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
	    int i = 0;
		JSONObject jsonreq = new JSONObject();
		
        try 
         {
	      jsonreq = jsonrequest.getJSONObject("request");
	      System.out.println(jsonreq.toString());
	      String did = jsonreq.getString("driver_id");
	      System.out.println(did);
	      PreparedStatement ps = connection.prepareStatement("update ncab_driver_master_tbl set driver_status=1 where driver_id = ?");
	      ps.setString(1,  did);
	    	 i=ps.executeUpdate();
	    	 System.out.println(i);
	
         } catch (SQLException e)
        {
        	 // TODO Auto-generated catch block
        	 e.printStackTrace();
        }
		
        finally {
        	if (connection != null) {
        		try
        		{
        			connection.close();
        		}
        		catch (SQLException e) 
        		{
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }		
		if(i>0)
		{
		return new JSONObject().put("result", "true");		
		
		}
		else
			return new JSONObject().put("result", "false");
	}


}
