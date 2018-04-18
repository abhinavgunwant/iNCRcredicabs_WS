package ncab.dao.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.util.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.awt.HeadlessException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONObject;

import ncab.dao.DBConnectionRo;
import ncab.dao.DBConnectionUpd;



public class VendorServiceImpl {

	public JSONObject getVendorDetails(){
        DBConnectionRo dbconnection = new DBConnectionRo();
        Connection connection = dbconnection.getConnection();
        JSONArray jsonarray = new JSONArray();
        PreparedStatement ps;
        try {
               ps = connection.prepareStatement("SELECT * FROM ncab_vendor_master_tbl ORDER BY vendor_name ASC");
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
               
               String agreementExpiry;

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
                     System.out.println(gstNum);
                     
                     agreementExpiry=rs.getString(11);
                     ownerName=rs.getString(12);
                     ownerContact=rs.getString(13);
                     ownerEmail=rs.getString(14);
                     supervisorName=rs.getString(15);
                     supContact=rs.getString(16);
                     supEmail=rs.getString(17);
                     manName=rs.getString(18);
                     manContact=rs.getString(19);
                     manEmail=rs.getString(20);
                     

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
                     
                     //System.out.println(jsonresponse);
                     //System.out.println(agreementExpiry);
                     jsonresponse.put("agreementExpiry",agreementExpiry);
                     //System.out.println(jsonresponse);
                     jsonarray.put(jsonresponse);
                     System.out.println(jsonarray);
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
//get vendor details by key

	public JSONObject getVendorDetailsByKey(JSONObject jsonrequest) {
		DBConnectionRo dbconnection = new DBConnectionRo();
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
			
			/*if(!key.equals("vendor_status")){
				sql = "select * from ncab_vendor_master_tbl where " + key + " like '%" + value + "%' order by vendor_status, vendor_name ";
			}
			else{
				int v=Integer.parseInt(value);
				System.out.println(v);
				sql = "select * from ncab_vendor_master_tbl where " + key + " = "+v+" order by vendor_status, vendor_name ";
			}*/
			if(key.equals("vendor_id"))
			{
				String query = "select * from ncab_vendor_master_tbl where";
				sql=query+" "+key+" = "+Integer.parseInt(value);
			}
			else{
			sql="select * from ncab_vendor_master_tbl where "+key+" like '"+value+"%' order by vendor_name";
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
            
            String agreementExpiry;

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
                  System.out.println(gstNum);
                  
                  agreementExpiry=rs.getString(11);
                  ownerName=rs.getString(12);
                  ownerContact=rs.getString(13);
                  ownerEmail=rs.getString(14);
                  supervisorName=rs.getString(15);
                  supContact=rs.getString(16);
                  supEmail=rs.getString(17);
                  manName=rs.getString(18);
                  manContact=rs.getString(19);
                  manEmail=rs.getString(20);

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
                  
                  //System.out.println(jsonresponse);
                  //System.out.println(agreementExpiry);
                  jsonresponse.put("agreementExpiry",agreementExpiry);
                  //System.out.println(jsonresponse);
                  jsonarray.put(jsonresponse);
                  System.out.println(jsonarray);
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

	public String addVendorList(String vendor_name, String business_type, String vendor_contact_num, String vendor_mail_id, String website, String pan_id, String gst_num, Date date, int cabs_provided, String business_address, String supervisor_name, String sup_contact_num, String sup_mail_id, String manager_name, String manager_contact_num, String manager_mail_id, String owner_name, String owner_contact_num, String owner_mail_id )throws Exception 
	{
		int x=0;
		//Date d1 = new Date(date1.getTime());
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();
		try {

			String query = " insert into ncab_vendor_master_tbl (vendor_name, business_address, business_type, vendor_contact_num, cabs_provided, vendor_mail_id, website, pan_id, gst_num, agreement_expiry_date, supervisor_name, sup_contact_num, sup_mail_id, manager_name, manager_contact_num, manager_mail_id, owner_name, owner_contact_num, owner_mail_id)" + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
			ps.setDate(10,date);
			ps.setString(11,supervisor_name);
			ps.setString(12,sup_contact_num);
			ps.setString(13,sup_mail_id);
			ps.setString(14,manager_name);
			ps.setString(15,manager_contact_num);
			ps.setString(16,manager_mail_id);
			ps.setString(17,owner_name);
			ps.setString(18,owner_contact_num);
			ps.setString(19,owner_mail_id);


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


	public JSONObject updatevendor(int vendor_id,String vendor_name, String business_type, String vendor_contact_num, String vendor_mail_id, String website, String pan_id, String gst_num, Date date1, String business_address, String supervisor_name, String sup_contact_num, String sup_mail_id, String manager_name, String manager_contact_num, String manager_mail_id, String owner_name, String owner_contact_num, String owner_mail_id )throws Exception 
	{      
		//java.sql.Date d1 = new Date(date1.getTime());

		System.out.println("working1");
		//     int x=0;
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();
		try {
			System.out.println("working5");


			String query = " update ncab_vendor_master_tbl set vendor_name =?,business_address=?, vendor_contact_num=?, vendor_mail_id=?, website = ?, pan_id = ?, gst_num = ?, agreement_expiry_date = ?, supervisor_name = ?, sup_contact_num = ?, sup_mail_id= ? , manager_name = ?,manager_contact_num= ?,manager_mail_id= ?,owner_name= ?,owner_contact_num= ?,owner_mail_id= ?, business_type=? where vendor_id = ?" ;
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
			
			ps.setDate(8,date1);
			ps.setString(9,supervisor_name);
			ps.setString(10,sup_contact_num);
			ps.setString(11,sup_mail_id);
			ps.setString(12,manager_name);
			ps.setString(13,manager_contact_num);
			ps.setString(14,manager_mail_id);
			ps.setString(15,owner_name);
			ps.setString(16,owner_contact_num);
			ps.setString(17,owner_mail_id);
			ps.setString(18, business_type);
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
           //String query="SELECT ncab_cab_master_tbl.cab_license_plate_no,model,fuel_type,contracted_or_owned,cab_type,cab_rate,manufacture_date,reg_certi,pollution_certi,fitness_certi,insurance_certi,entry_tax_haryana_certi,entry_tax_delhi_certi,entry_tax_up_certi,poll_certi_exp_date,fit_certi_exp_date,insur_certi_exp_date,entry_tax_haryana_exp_date,entry_tax_delhi_exp_date,entry_tax_up_exp_date,driver_name,d_contact_num,vendor_name,cab_status,driver_type FROM ncab_cab_master_tbl,ncab_driver_master_tbl,ncab_vendor_master_tbl WHERE ncab_cab_master_tbl. cab_license_plate_no=ncab_driver_master_tbl.cab_license_plate_no AND ncab_vendor_master_tbl.vendor_id=ncab_cab_master_tbl.vendor_id order by cab_status";
           //String query="SELECT * FROM ncab_cab_master_tbl WHERE ncab_relation_tbl.cab_license_plate_no=ncab_cab_master_tbl.cab_license_plate_no AND vendor_id=?";
		String query="select * from ncab_cab_master_tbl";
           
           DBConnectionUpd dbconnection = new DBConnectionUpd();
           Connection connection = dbconnection.getConnection();
           JSONArray jsonarray = new JSONArray();
           PreparedStatement ps;
           
           try{
                  
                  
                  ps= connection.prepareStatement(query);
                 //ps.setInt(1, vendor_id);
                  ResultSet rs = ps.executeQuery();
                  System.out.println(query);
                  
                  //int vendor_id;
                  String cab_no="";
                  String model="";
                  String fuel="";
                  String cab_type="";
                  int cab_rate;
                  String cab_id= "";
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
                  int cab_compliances;


                  while(rs.next())
                  {
                        JSONObject jsonresponse = new JSONObject();
                        
                        //vendor_id=rs.getString(1);
                        cab_id = rs.getString(1);
                        cab_no=rs.getString(2);
                        model=rs.getString(3);
                        fuel=rs.getString(4);
                        //cab_id=rs.getString(4);
                        cab_type=rs.getString(5);
                        if(cab_type.equalsIgnoreCase("BIG"))
                               cab_type="7";
                        else
                               cab_type="4";
                        cab_rate=rs.getInt(7);
                        manufacture_date=rs.getString(8);
                        cab_status=rs.getInt(9);
                        reg_certi=rs.getString(10);
                      poll_certi=rs.getString(11);
                      fit_certi=rs.getString(12);
                   insur_certi=rs.getString(13);
                   tax_haryana_certi=rs.getString(14);
                   tax_delhi_certi=rs.getString(15);
                   tax_up_certi=rs.getString(16);
                        poll_exp=rs.getString(17);
                        fit_exp=rs.getString(18);
                        insur_exp=rs.getString(19);
                        tax_haryana_exp=rs.getString(20);
                        tax_delhi_exp=rs.getString(21);
                        tax_up_exp=rs.getString(22);
                        cab_compliances=rs.getInt(23);
//                      driver_name=rs.getString(22);
//                      driver_contact_num=rs.getString(23);
//                      vendor_name=rs.getString(24);
                        
                        
                  //     jsonresponse.put("vendor_id", vendor_id);
                        jsonresponse.put("cab_id", cab_id);
                        jsonresponse.put("cab_no", cab_no);
                        jsonresponse.put("model", model);
                        jsonresponse.put("fuel", fuel);
                        jsonresponse.put("cab_type", cab_type);
                        jsonresponse.put("cab_rate", cab_rate);
                        jsonresponse.put("manufacture_date", manufacture_date);
                        jsonresponse.put("poll_exp", poll_exp);
                        jsonresponse.put("fit_exp", fit_exp);
                        jsonresponse.put("insur_exp", insur_exp);
                        jsonresponse.put("tax_haryana_exp", tax_haryana_exp);
                        jsonresponse.put("tax_up_exp", tax_up_exp);
                        jsonresponse.put("tax_delhi_exp", tax_delhi_exp);
//                      jsonresponse.put("driver_name", driver_name);
//                      jsonresponse.put("driver_contact_num", driver_contact_num);
//                      jsonresponse.put("vendor_name", vendor_name);
                        jsonresponse.put("reg_certi", reg_certi);
                        jsonresponse.put("poll_certi",poll_certi);
                        jsonresponse.put("fit_certi", fit_certi);
                      jsonresponse.put("insur_certi",insur_certi);
                        jsonresponse.put("tax_haryana_certi", tax_haryana_certi);
                        jsonresponse.put("tax_delhi_certi", tax_delhi_certi);
                        jsonresponse.put("tax_up_certi",tax_up_certi);
                        jsonresponse.put("status", cab_status);
                        jsonresponse.put("compliance", cab_compliances);
                                 
                        
                        
                        jsonarray.put(jsonresponse);
                        
                        
                  }


           } catch (SQLException e) {
                  //            TODO Auto-generated catch block
                  e.printStackTrace();
           }
           return new JSONObject().put("result", jsonarray);

    }


	public JSONObject enableCabDetailsById(JSONObject jsonrequest){

        DBConnectionUpd dbconnection = new DBConnectionUpd();
        Connection connection = dbconnection.getConnection();
        int i = 0;
        System.out.println(jsonrequest);

        String cab_no=jsonrequest.getJSONObject("request").getString("cab_license_plate_no");
        System.out.println(cab_no);
        try 
        {

               PreparedStatement ps = connection.prepareStatement("update ncab_cab_master_tbl set cab_status=1 where cab_license_plate_no = ?");
               ps.setString(1, cab_no);
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
               System.out.println("true");
               return new JSONObject().put("result", "true");              

        }
        else
               return new JSONObject().put("result", "false");
 }


public JSONObject deleteCabDetailsByCabID(JSONObject jsonrequest){
    DBConnectionUpd dbconnection = new DBConnectionUpd();
    Connection connection = dbconnection.getConnection();
    int i = 0;
    String result="";
    JSONObject jsonres=new JSONObject();
    ResultSet rs=null;
    JSONObject jsonreq = new JSONObject();
    int j=0;
    try 
    {
           jsonreq = jsonrequest.getJSONObject("request");
           System.out.println(jsonreq.toString());
           String cab_no = jsonreq.getString("cab_license_plate_no");
           System.out.println(cab_no);

           //      PreparedStatement ps2=connection.prepareStatement("select * from roastertable where cab_no=?");
           //      ps2.setString(1,  vid);
           //      rs=ps2.executeQuery();
           //      
           PreparedStatement ps1=connection.prepareStatement("select Route_Status from ncab_roster_tbl where Cab_No = ?");
           ps1.setString(1,  cab_no);
           rs=ps1.executeQuery();
           while(rs.next()) {
                 String status=rs.getString(1);
                 if(status.equalsIgnoreCase("active")) {
                        result="active";
                 }

           }
           if(result.equals("active")) {
                 jsonres.put("result",result);
           }
           else {
                 PreparedStatement ps=connection.prepareStatement("update ncab_cab_master_tbl set cab_status=0 where cab_license_plate_no = ?");
                 ps.setString(1, cab_no);
                 int res=ps.executeUpdate();
                 jsonres.put("result","inactive");
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

    return jsonres;
}


	
public JSONObject getDriverDetails()
{

       //String query="SELECT ncab_vendor_master_tbl.vendor_id,driver_name,d_contact_num,driver_type,driver_local_address,driver_permanent_address,driver_photo,police_verification,driver_local_address_proof,driver_permanent_address_proof,driving_license,ncab_driver_master_tbl.cab_license_plate_no,vendor_name,license_exp_date,driver_status,driver_id FROM ncab_cab_master_tbl,ncab_driver_master_tbl,ncab_vendor_master_tbl WHERE ncab_cab_master_tbl. cab_license_plate_no=ncab_driver_master_tbl. cab_license_plate_no AND ncab_vendor_master_tbl.vendor_id=ncab_cab_master_tbl.vendor_id";
       //String query="SELECT * FROM ncab_driver_master_tbl,ncab_relation_tbl WHERE ncab_driver_master_tbl.driver_id=ncab_relation_tbl.driver_id AND vendor_id=? AND cab_license_plate_no=?";
	String query="SELECT * FROM ncab_driver_master_tbl";
       DBConnectionUpd dbconnection = new DBConnectionUpd();
       Connection connection = dbconnection.getConnection();
       JSONArray jsonarray = new JSONArray();

       PreparedStatement ps;

       try{
              //ps = connection.prepareStatement("SELECT driver_name,d_contact_num,driver_local_address,driver_permanent_address,driver_photo,police_verification,driver_local_address_proof,driver_permanent_address_proof,driving_license,driver_tbl.cab_license_plate_no,vendor_name,license_exp_date FROM cab_tbl,driver_tbl,vendor_tbl WHERE cab_status = 0 AND driver_status=0 AND vendor_status=0 AND cab_tbl. cab_license_plate_no=driver_tbl. cab_license_plate_no AND vendor_tbl.vendor_id=cab_tbl.vendor_id");
              ps=connection.prepareStatement(query);
              //ps.setInt(1, vendor_id);
              //ps.setString(2, cab_no);

              //System.out.println("query"+query);

              ResultSet rs = ps.executeQuery();
              int driver_id;
              //int vendor_id;
              String d_name;
              String d_contact_num;
              String d_local_add;
              String d_permanent_add;
              int driver_status;
              String d_license;
             
              String d_license_num;
              String license_exp_date;
              String driver_photo;
              String police_verification;
              String d_local_add_proof;
              String d_permanent_add_proof;
              int compliance;

              while(rs.next())
              {
                    
                    JSONObject jsonresponse = new JSONObject();
                    //vendor_id=rs.getInt(1);
                    driver_id=rs.getInt(1);
                    d_name=rs.getString(2);
                    d_contact_num=rs.getString(3);
                    d_local_add=rs.getString(4);
                    d_permanent_add=rs.getString(5);
                    driver_status=rs.getInt(6);
                    d_license=rs.getString(7);
                    d_license_num=rs.getString(8);
                    license_exp_date=rs.getString(9);
                    d_local_add_proof=rs.getString(10);
                    d_permanent_add_proof=rs.getString(11);
                    driver_photo=rs.getString(12);
                    police_verification=rs.getString(13);
                    System.out.println(driver_id);
                    compliance=rs.getInt(15);
//                  
//                  cab_no=rs.getString(12);
//                  vendor_name=rs.getString(13);
                    
                    
                    
                    //jsonresponse.put("vendor_id", vendor_id);
                    jsonresponse.put("driver_id", driver_id);
                    jsonresponse.put("d_name", d_name);
                    jsonresponse.put("d_contact_num", d_contact_num);
                    //jsonresponse.put("driver_type", driver_type);
                    jsonresponse.put("d_local_add", d_local_add);
                    jsonresponse.put("d_permanent_add", d_permanent_add);
                    jsonresponse.put("status", driver_status);
                    jsonresponse.put("d_license", d_license);
                    jsonresponse.put("license_num", d_license_num);
                    jsonresponse.put("license_exp_date", license_exp_date);
                    jsonresponse.put("d_local_add_proof", d_local_add_proof);
                    jsonresponse.put("d_permanent_add_proof", d_permanent_add_proof);
                    jsonresponse.put("driver_photo", driver_photo);
                    jsonresponse.put("police_verification", police_verification);  
                    jsonresponse.put("compliance", compliance);
//                  jsonresponse.put("cab_no", cab_no);
//                  jsonresponse.put("vendor_name", vendor_name);
//                  
//                  

                    jsonarray.put(jsonresponse);
              }


       } catch (SQLException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
       }
       return new JSONObject().put("result", jsonarray);
}



	

	public Response AddCab(String license_plate_number, String model,  String fuel,String type, int occupancy, int cab_rate , String Rcert,String Pcert,String Fcert,String icert,String entry_tax_haryana_certi,String entry_tax_delhi_certi,String entry_tax_up_certi, Date date1, Date date2, Date date3, Date date4, String entry_tax_haryana_exp_date, String entry_tax_delhi_exp_date, String entry_tax_up_exp_date, int compliance ) throws Exception
	{
		int cab_status=1;
		int x=0;


		try {
			DBConnectionUpd database= new DBConnectionUpd();
			Connection connection = database.getConnection();
			String query = " insert into ncab_cab_master_tbl (cab_license_plate_no, model, fuel_type, cab_type,cab_capacity, cab_rate, reg_certi ,pollution_certi,fitness_certi,insurance_certi,entry_tax_haryana_certi ,entry_tax_delhi_certi,entry_tax_up_certi ,  manufacture_date, cab_status, poll_certi_exp_date, fit_certi_exp_date, insur_certi_exp_date, entry_tax_haryana_exp_date, entry_tax_delhi_exp_date, entry_tax_up_exp_date, cab_compliance)" + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1,license_plate_number);
			ps.setString(2,model);
			ps.setString(3,fuel);
			ps.setString(4,type);
			ps.setInt(5, occupancy);
			ps.setInt(6,cab_rate);
			ps.setString(7, Rcert);
			ps.setString(8, Pcert);
			ps.setString(9, Fcert);
			ps.setString(10, icert);
			ps.setString(11, entry_tax_haryana_certi);
			ps.setString(12, entry_tax_delhi_certi);
			ps.setString(13, entry_tax_up_certi);
			ps.setDate(14,date1);
			ps.setInt(15, cab_status);
			ps.setDate(16,date2);
			ps.setDate(17,date3);
			ps.setDate(18,date4);
			ps.setString(19,entry_tax_haryana_exp_date);
			ps.setString(20,entry_tax_delhi_exp_date);
			ps.setString(21,entry_tax_up_exp_date);
			ps.setInt(22, compliance);



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



	public Response AddDriver(String driver_license_num,String Name, String dPhone_Nbr, String local_Address, String permanent_Address,Date license,String d_comercial_liscence,String d_police_verification,String d_local_Address_proof,String d_permanent_address_proof,String d_photo)throws Exception 
    {
          int driver_status=1;
          int x=0;
          DBConnectionUpd database= new DBConnectionUpd();
          Connection connection = database.getConnection();
          try {

                 String query = " insert into ncab_driver_master_tbl (driver_license_num, driver_name, d_contact_num, driver_local_address, driver_permanent_address, license_exp_date, driving_license, police_verification, driver_local_address_proof, driver_permanent_address_proof, driver_photo, driver_status, driver_compliance)" + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
                 PreparedStatement ps = connection.prepareStatement(query);
                 ps.setString(1,driver_license_num);
                 ps.setString(2,Name);
                 ps.setString(3,dPhone_Nbr);
                 ps.setString(4,local_Address);
                 ps.setString(5,permanent_Address);
                 ps.setDate(6,license);

                 ps.setString(7,d_comercial_liscence);
                 ps.setString(8,d_police_verification);
                 ps.setString(9,d_local_Address_proof);
                 ps.setString(10,d_permanent_address_proof);
                 ps.setString(11,d_photo);
                 ps.setInt(12,driver_status);
                 if(d_local_Address_proof.equals(""))
                 {
                	 ps.setInt(13, 0);
                 }
                 else
                 {
                	 ps.setInt(13, 1);
                 }
                 x = ps.executeUpdate();

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

	//	String query="SELECT cab_license_plate_no,model,fuel_type,contracted_or_owned,cab_type,cab_rate,manufacture_date,reg_certi,pollution_certi,fitness_certi,insurance_certi,entry_tax_haryana_certi,entry_tax_delhi_certi,entry_tax_up_certi,poll_certi_exp_date,fit_certi_exp_date,insur_certi_exp_date,entry_tax_haryana_exp_date,entry_tax_delhi_exp_date,entry_tax_up_exp_date,driver_name,d_contact_num,vendor_name,cab_status,driver_type FROM ncab_cab_master_tbl,ncab_driver_master_tbl,ncab_vendor_master_tbl WHERE ncab_cab_master_tbl. cab_license_plate_no=ncab_driver_master_tbl.cab_license_plate_no AND ncab_vendor_master_tbl.vendor_id=ncab_cab_master_tbl.vendor_id";
		String query="select * from ncab_cab_master_tbl where ";
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
				if(value.equals("7"))
				{
					value="BIG";
				}
				if(value.equals("4"))
				{
					value="SMALL";
				}
			}

			/*if(key.equals("cab_license_plate_no"))
			{
				key="ncab_cab_master_tbl."+key;
			}*/
			//System.out.println(key);
			sql=query+""+key+" = '"+value+"'";
			if(key.equals("cab_id")){
				sql=query+""+key+" = "+Integer.parseInt(value);
			}
			
			System.out.println(sql);
			//	System.out.println(query+" AND "+key+"='"+value+"'");
			PreparedStatement ps = connection.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			int cab_id; 
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
            int cab_compliances;


            while(rs.next())
            {
                  JSONObject jsonresponse = new JSONObject();
                  
                  //vendor_id=rs.getString(1);
                  cab_id = rs.getInt(1);
                  cab_no=rs.getString(2);
                  model=rs.getString(3);
                  fuel=rs.getString(4);
                  contract_owned=rs.getString(5);
                  cab_type=rs.getString(6);
                  cab_rate=rs.getInt(7);
                  manufacture_date=rs.getString(8);
                  cab_status=rs.getInt(9);
                  reg_certi=rs.getString(10);
                poll_certi=rs.getString(11);
                fit_certi=rs.getString(12);
             insur_certi=rs.getString(13);
             tax_haryana_certi=rs.getString(14);
             tax_delhi_certi=rs.getString(15);
             tax_up_certi=rs.getString(16);
                  poll_exp=rs.getString(17);
                  fit_exp=rs.getString(18);
                  insur_exp=rs.getString(19);
                  tax_haryana_exp=rs.getString(20);
                  tax_delhi_exp=rs.getString(21);
                  tax_up_exp=rs.getString(22);
                  cab_compliances=rs.getInt(23);
//                driver_name=rs.getString(22);
//                driver_contact_num=rs.getString(23);
//                vendor_name=rs.getString(24);
                  
                  
            //     jsonresponse.put("vendor_id", vendor_id);
                  jsonresponse.put("cab_id",cab_id);
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
//                jsonresponse.put("driver_name", driver_name);
//                jsonresponse.put("driver_contact_num", driver_contact_num);
//                jsonresponse.put("vendor_name", vendor_name);
                  jsonresponse.put("reg_certi", reg_certi);
                  jsonresponse.put("poll_certi",poll_certi);
                  jsonresponse.put("fit_certi", fit_certi);
                jsonresponse.put("insur_certi",insur_certi);
                  jsonresponse.put("tax_haryana_certi", tax_haryana_certi);
                  jsonresponse.put("tax_delhi_certi", tax_delhi_certi);
                  jsonresponse.put("tax_up_certi",tax_up_certi);
                  jsonresponse.put("status", cab_status);
                  jsonresponse.put("compliance", cab_compliances);
                           
                  System.out.println(jsonresponse);
                  
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

		String query="SELECT * FROM ncab_driver_master_tbl";

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
			sql=query+" where "+key+" like '%"+value+"%'";
			System.out.println(sql);
			ps=connection.prepareStatement(sql);

			//System.out.println("query"+query);

			ResultSet rs = ps.executeQuery();
			int driver_id;
            //int vendor_id;
            String d_name;
            String d_contact_num;
            String d_local_add;
            String d_permanent_add;
            int driver_status;
            String d_license;
           
            String d_license_num;
            String license_exp_date;
            String driver_photo;
            String police_verification;
            String d_local_add_proof;
            String d_permanent_add_proof;
            int compliance;
			

			while(rs.next())
			{

				JSONObject jsonresponse = new JSONObject();
//				vendor_id=rs.getInt(1);
				driver_id=rs.getInt(1);
                d_name=rs.getString(2);
                d_contact_num=rs.getString(3);
                d_local_add=rs.getString(4);
                d_permanent_add=rs.getString(5);
                driver_status=rs.getInt(6);
                d_license=rs.getString(7);
                d_license_num=rs.getString(8);
                license_exp_date=rs.getString(9);
                d_local_add_proof=rs.getString(10);
                d_permanent_add_proof=rs.getString(11);
                driver_photo=rs.getString(12);
                police_verification=rs.getString(13);
                System.out.println(driver_id);
                compliance=rs.getInt(15);
				
//				jsonresponse.put("vendor_id", vendor_id);
				jsonresponse.put("d_name", d_name);
				jsonresponse.put("d_contact_num", d_contact_num);
//				jsonresponse.put("driver_type", driver_type);
				jsonresponse.put("d_local_add", d_local_add);
				jsonresponse.put("d_permanent_add", d_permanent_add);
				jsonresponse.put("driver_photo", driver_photo);
				jsonresponse.put("police_verification", police_verification);
				jsonresponse.put("d_local_add_proof", d_local_add_proof);
				jsonresponse.put("d_permanent_add_proof", d_permanent_add_proof);
				jsonresponse.put("d_license", d_license);
				jsonresponse.put("d_license_num", d_license_num);
//				jsonresponse.put("cab_no", cab_no);
//				jsonresponse.put("vendor_name", vendor_name);
				jsonresponse.put("license_exp_date", license_exp_date);
				jsonresponse.put("status", driver_status);
				jsonresponse.put("driver_id", driver_id);
				jsonresponse.put("compliance",compliance);
				
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
     FTPClient client = new FTPClient();

     try {
            client.connect("ec2-18-219-151-75.us-east-2.compute.amazonaws.com", 21);

            // Try to login and return the respective boolean value
            boolean login = client.login("imagestore", "Gr33n@1234");

            // If login is true notify user
            if (login) {
         System.out.println("Connection established...");
         FileInputStream fis = null;
       client.setFileType(FTP.BINARY_FILE_TYPE);
         
        
         boolean done = client.storeFile(target, inStream);
         
         if(done)
         {
            System.out.println("Image Uploaded");
         }
         else
         {
            System.out.println("uploading failed");
         }
         // Try to logout and return the respective boolean value
         boolean logout = client.logout();
         // If logout is true notify user
         if (logout) { 
            System.out.println("Connection close...");
         }
         //  Notify user for failure 

     } else {
         System.out.println("Connection fail...");
     }
       } catch (IOException e) {
                   e.printStackTrace();
       } finally {
     try {
         // close connection
         client.disconnect();
     } catch (IOException e) {
         e.printStackTrace();
     }
       }
     
}


public void downloadImage(OutputStream outputStream, String image)throws IOException{
    FTPClient client = new FTPClient();

    try {
           //InetAddress host = InetAddress.getByName("13.127.147.167");
           client.connect("ec2-18-219-151-75.us-east-2.compute.amazonaws.com", 21);

           // Try to login and return the respective boolean value
           boolean login = client.login("imagestore", "Gr33n@1234");

           // If login is true notify user
           if (login) {
        System.out.println("Connection established...");
        client.setFileType(FTP.BINARY_FILE_TYPE);
        boolean success = client.retrieveFile(image, outputStream);
        	outputStream.close();

        if(success)
        {
           System.out.println("Image Downloaded");
        }
        else
        {
           System.out.println("Downloading failed");
        }
        // Try to logout and return the respective boolean value
        boolean logout = client.logout();
        // If logout is true notify user
        if (logout) { 
           System.out.println("Connection close...");
        }
        //  Notify user for failure 

    } else {
        System.out.println("Connection fail...");
    }
      } catch (IOException e) {
                  e.printStackTrace();
      } finally {
    try {
        // close connection
        client.disconnect();
    } catch (IOException e) {
        e.printStackTrace();
    }
      }

	}

	// ApiIMPL for getting singular driver details
	public JSONObject getdriverdetailsbyid(int driver_id)
	{
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();
		JSONArray jsonarray = new JSONArray();

		try{
			String query = "SELECT * FROM ncab_driver_master_tbl where driver_id =?";
			PreparedStatement ps = connection.prepareStatement(query);
			
			ps.setInt(1, driver_id);

			//System.out.println("query"+query);

			ResultSet rs = ps.executeQuery();
			
            //int vendor_id;
            String d_name;
            String d_contact_num;
            String d_local_add;
            String d_permanent_add;
            int driver_status;
            String d_license;
           
            String d_license_num;
            String license_exp_date;
            String driver_photo;
            String police_verification;
            String d_local_add_proof;
            String d_permanent_add_proof;
            int compliance;
			

			while(rs.next())
			{

				JSONObject jsonresponse = new JSONObject();
//				vendor_id=rs.getInt(1);
				driver_id=rs.getInt(1);
                d_name=rs.getString(2);
                d_contact_num=rs.getString(3);
                d_local_add=rs.getString(4);
                d_permanent_add=rs.getString(5);
                driver_status=rs.getInt(6);
                d_license=rs.getString(7);
                d_license_num=rs.getString(8);
                license_exp_date=rs.getString(9);
                d_local_add_proof=rs.getString(10);
                d_permanent_add_proof=rs.getString(11);
                driver_photo=rs.getString(12);
                police_verification=rs.getString(13);
                System.out.println(driver_id);
                compliance=rs.getInt(15);
				
//				jsonresponse.put("vendor_id", vendor_id);
				jsonresponse.put("d_name", d_name);
				jsonresponse.put("d_contact_num", d_contact_num);
//				jsonresponse.put("driver_type", driver_type);
				jsonresponse.put("d_local_add", d_local_add);
				jsonresponse.put("d_permanent_add", d_permanent_add);
				jsonresponse.put("driver_photo", driver_photo);
				jsonresponse.put("police_verification", police_verification);
				jsonresponse.put("d_local_add_proof", d_local_add_proof);
				jsonresponse.put("d_permanent_add_proof", d_permanent_add_proof);
				jsonresponse.put("d_license", d_license);
				jsonresponse.put("d_license_num", d_license_num);
//				jsonresponse.put("cab_no", cab_no);
//				jsonresponse.put("vendor_name", vendor_name);
				jsonresponse.put("license_exp_date", license_exp_date);
				jsonresponse.put("status", driver_status);
				jsonresponse.put("driver_id", driver_id);
				jsonresponse.put("compliance",compliance);
				
				 jsonarray.put(jsonresponse);
			}
			}
			 catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		return new JSONObject().put("result", jsonarray);
					
		}
	
	//ApiIMPL for updating cab
	

	public Response updateCab(String cab_id,String cab_license_plate_number, String model,  String fuel_type,String type,int occupancy, int cab_rate , String Rcert,String Pcert,String Fcert,String icert,String entry_tax_haryana_certi,String entry_tax_delhi_certi,String entry_tax_up_certi, Date date1, Date date2, Date date3, Date date4, String entry_tax_haryana_exp_date, String entry_tax_delhi_exp_date, String entry_tax_up_exp_date )throws Exception 
	{
		//int cab_status=0;
		//int vendor_id = 98898;

		int x=0;
		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();

		try {

			String query = " update ncab_cab_master_tbl set model=?, fuel_type=?, cab_type=?,cab_capacity=?, cab_rate=?,  manufacture_date=?,reg_certi=? ,pollution_certi=?,fitness_certi=?,insurance_certi=?,entry_tax_haryana_certi=? ,entry_tax_delhi_certi=?,entry_tax_up_certi=? ,   poll_certi_exp_date=?, fit_certi_exp_date=?, insur_certi_exp_date=?, entry_tax_haryana_exp_date=?, entry_tax_delhi_exp_date=?, entry_tax_up_exp_date=?, cab_compliance=?, cab_license_plate_no= ? where cab_id =?";
			PreparedStatement ps = connection.prepareStatement(query);
			//ps.setInt(1,vendor_id);

			ps.setString(1,model);
			ps.setString(2,fuel_type);
			//ps.setString(3,onrshp);
			ps.setString(3,type);
			ps.setInt(4, occupancy);
			ps.setInt(5,cab_rate);
			ps.setDate(6,date1);
			//ps.setInt(8, cab_status);
			ps.setString(7, Rcert);
			ps.setString(8, Pcert);
			ps.setString(9, Fcert);
			ps.setString(10, icert);
			ps.setString(11, entry_tax_haryana_certi);
			ps.setString(12, entry_tax_delhi_certi);
			ps.setString(13, entry_tax_up_certi);


			ps.setDate(14,date2);
			ps.setDate(15,date3);
			ps.setDate(16,date4);
			ps.setString(17,entry_tax_haryana_exp_date);
			ps.setString(18,entry_tax_delhi_exp_date);
			ps.setString(19,entry_tax_up_exp_date);
			if(entry_tax_haryana_certi.equals("") || entry_tax_delhi_certi.equals("") || entry_tax_up_certi.equals(""))
			{
				ps.setInt(20, 0);
			}
			else
			{
				ps.setInt(20, 1);
			}
			ps.setString(21,cab_license_plate_number);
			ps.setString(22, cab_id);
			


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

	public Response getAllDriverList(String driver_name,String d_contact_num,String driver_local_address,String driver_permanent_address,String driving_license,String driver_local_address_proof,String driver_permanent_address_proof,String driver_photo,String police_verification, Date date1,int driver_id,int driver_status, String license_num)throws Exception 
	{
		//int driver_status=0;
		
		
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

			String query = " update ncab_driver_master_tbl set driver_name=?,d_contact_num=?,driver_local_address=?, driver_permanent_address=?,license_exp_date=?,driver_compliance=?,driving_license=?,driver_local_address_proof=?,driver_permanent_address_proof=?,driver_photo=?,police_verification=?,driver_license_num=? where driver_id= ? ";
			PreparedStatement ps = connection.prepareStatement(query);
			//ps.setInt(1,driver_id);
			//	ps.setString(2,cab_license_plate_number);
			ps.setString(1,driver_name);
			ps.setString(2,d_contact_num);
			//ps.setString(3,driver_type);
			ps.setString(3,driver_local_address);
			ps.setString(4, driver_permanent_address);
			ps.setDate(5,date1);
			if(driver_local_address_proof.equals(""))
			{
				ps.setInt(6, 0);
			}
			else
			{
				ps.setInt(6,1);
			}
			
			ps.setString(7, driving_license);
			ps.setString(8,driver_local_address_proof);
			//ps.setString(12, icert);
			//	ps.setString(13, entry_tax_haryana_certi);
			//	ps.setString(14, entry_tax_delhi_certi);
			ps.setString(9,driver_permanent_address_proof);
			ps.setString(10, driver_photo);
			ps.setString(11,police_verification);
			ps.setString(12, license_num);
			ps.setInt(13, driver_id);
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
        //int vid=-1, driverid=-1;

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




	public JSONObject deleteDriverDetailsByDriverID(JSONObject jsonrequest){
        DBConnectionUpd dbconnection = new DBConnectionUpd();
        Connection connection = dbconnection.getConnection();
        int i = 0;
        JSONObject jsonreq = new JSONObject();
        JSONObject jsonres = new JSONObject();
        String result="";
        try 
        {
               jsonreq = jsonrequest.getJSONObject("request");
               System.out.println(jsonreq.toString());
               String did = jsonreq.getString("driver_id");
               System.out.println(did);


               PreparedStatement ps1=connection.prepareStatement("select Route_Status from ncab_roster_tbl where Driver_Id = ?");
               ps1.setString(1,  did);
               ResultSet rs=ps1.executeQuery();
               while(rs.next()) {
                     String status=rs.getString(1);
                     if(status.equalsIgnoreCase("active")) {
                            result="active";
                     }

               }
               if(result.equals("active")) {
                     jsonres.put("result",result);
               }

               else {

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
        return jsonres;
 }
	
	public boolean sendnotification(){
		DBConnectionUpd dbconnection = new DBConnectionUpd();
		Connection connection = dbconnection.getConnection();
		int count =0;
		PreparedStatement ps;
		int flag = 0;
		try {
			ps = connection.prepareStatement("SELECT count(*) FROM ncab_expiry_notification_tbl where flag_sent = '" + flag + "' ");
			ResultSet rs1 = ps.executeQuery();
			while(rs1.next())
			{
				count = rs1.getInt(1);
			}
			ps = connection.prepareStatement("SELECT * FROM ncab_expiry_notification_tbl where flag_sent = '" + flag + "' ");
			ResultSet rs = ps.executeQuery();

			String[] document_name= new String[count];
			String[] entity_name= new String[count];
			String[] entity_identifier= new String[count];
			String[] contact_number= new String[count];
			String[] expiry_date= new String[count];
			int[] days_left = new int[count];
			int i = 0;

			while(rs.next()){
				document_name[i]=rs.getString(1);
				entity_name[i]=rs.getString(2);
				entity_identifier[i]=rs.getString(3);
				contact_number[i]=rs.getString(4);
				expiry_date[i]=rs.getString(5);
				days_left[i]=rs.getInt(6);
				i++;
			}                  

			if(count == 0)
			{
				return false;
			}
			else{
				// TODO add email id for Expiry document
					String from = "";
					String recepient1 = "Hanif.Mohd@ncr.com";
					String recepient2 = "sk250865@ncr.com";
					String recepient3 = "";
					String recepient4 = "";
					
					String subject = "Expiry Notification";

					String messageAttribute = "Hi Transport Team, <br><br>"
							+ "<table width='100%' border='1' align='center'>"
							+ "<tr align='center'>"
							+ "<td><b>Document Name<b></td>"
							+ "<td><b>Entity Name<b></td>"
							+ "<td><b>Entity Identifier<b></td>"
							+ "<td><b>Contact Number<b></td>"
							+ "<td><b>Expiry Date<b></td>"
							+ "<td><b>Days Left<b></td>"
							+ "</tr>";
					for(int j=0;j<count;j++)
					{
						messageAttribute += "<tr>"
								+ "<td>" + document_name[j] + "</td>"
								+ "<td>" + entity_name[j] + "</td>"
								+ "<td>" + entity_identifier[j] + "</td>"
								+ "<td>" + contact_number[j] + "</td>"
								+ "<td>" + expiry_date[j] + "</td>"
								+ "<td>" + days_left[j] + "</td>"
								+ "</tr>";
					}
					messageAttribute += "</table><br>";

					//message.setContent(messageAttribute, "text/html");
					
					UtilServiceImpl obj = new UtilServiceImpl();
					 if(obj.sendEmailMessage(from, recepient1, recepient2, recepient3, recepient4, subject, messageAttribute)) 
					 {
						 System.out.println("AppEngine: Message Sent");
						 
						 	ps = connection.prepareStatement("update ncab_expiry_notification_tbl set flag_sent = 1 where flag_sent = 0");
							ps.executeUpdate();
							System.out.println("Flag Updated");                 
						
							return true;
					 }
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}



}
