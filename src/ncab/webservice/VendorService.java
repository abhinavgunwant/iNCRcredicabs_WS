package ncab.webservice;

import javax.ws.rs.Path;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;

import ncab.dao.DBConnectionUpd;
import ncab.dao.impl.VendorServiceImpl;


@Path("/VendorService")
public class VendorService {

	public VendorService() {
		// TODO Auto-generated constructor stub
	}

	//Add methods here

	//adding cab

	@POST
	@Path("/Addcab")
	@Consumes(MediaType.APPLICATION_JSON)

	public Response addcab(String params) throws Exception {

		JSONObject jsonPboject = new JSONObject(params);

		int vendor_id = jsonPboject.getInt("vendor_id");
		String onrshp = jsonPboject.getString("onrshp");
		String license_plate_number = jsonPboject.getString("license_plate_number");
		String model = jsonPboject.getString("model");
		String fuel = jsonPboject.getString("fuel");
		String type = jsonPboject.getString("type");
		int occupancy = jsonPboject.getInt("occupancy");
		int cab_rate = jsonPboject.getInt("cab_rate");
		String Mdate = jsonPboject.getString("Mdate");
		String Rcert = jsonPboject.getString("Rcert");
		String Pcert = jsonPboject.getString("Pcert");
		String Fcert = jsonPboject.getString("Fcert");
		String icert = jsonPboject.getString("icert");
		String entry_tax_haryana_certi = jsonPboject.getString("entry_tax_haryana_certi");
		String entry_tax_delhi_certi = jsonPboject.getString("entry_tax_delhi_certi");
		String entry_tax_up_certi = jsonPboject.getString("entry_tax_up_certi");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = (Date) df.parse(Mdate); 
		String Pdate = jsonPboject.getString("Pdate");
		Date date2 = (Date)  df.parse(Pdate); 
		String Fdate = jsonPboject.getString("Fdate");
		Date date3 = (Date)  df.parse(Fdate);
		String idate = jsonPboject.getString("idate");
		Date date4 = (Date) df.parse(idate);
		String entry_tax_haryana_exp_date = jsonPboject.getString("entry_tax_haryana_exp_date");
		Date date5 = (Date) df.parse(entry_tax_haryana_exp_date);
		String entry_tax_delhi_exp_date = jsonPboject.getString("entry_tax_delhi_exp_date");
		Date date6 = (Date) df.parse(entry_tax_delhi_exp_date);
		String entry_tax_up_exp_date = jsonPboject.getString("entry_tax_up_exp_date");
		Date date7 = (Date) df.parse(entry_tax_up_exp_date);

		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();

		try {
			String query = "select * from ncab_cab_master_tbl Where cab_license_plate_no='" + license_plate_number + "' ";

			Statement stmt = connection.createStatement();
			ResultSet result = null;           
			result = stmt.executeQuery(query);

			if(result.isBeforeFirst()){
				connection.close();
				return Response.status(200).entity("Data Found").build();

			}
			else{
				connection.close();
				System.out.println("No Data Found"); //data not exist
				VendorServiceImpl daoimpl=new VendorServiceImpl(); 

				return daoimpl.AddCab(vendor_id, onrshp, license_plate_number, model, fuel,type, occupancy,cab_rate, Rcert,Pcert,Fcert,icert,entry_tax_haryana_certi,entry_tax_delhi_certi,entry_tax_up_certi, date1,date2,date3,date4,date5,date6,date7);
				// data exist
			}   


		} catch (Exception e) {
			throw e;
		}


	}
	//adding driver
	@POST
	@Path("/AddDriver")
	@Consumes(MediaType.APPLICATION_JSON)

	public Response driver(String params) throws Exception {
		JSONObject jsonPboject = new JSONObject(params);

		int vendor_id = jsonPboject.getInt("vendor_id");
		String d_type = jsonPboject.getString("d_type");
		String first_Name = jsonPboject.getString("first_Name");
		String dPhone_Nbr = jsonPboject.getString("dPhone_Nbr");
		String local_Address = jsonPboject.getString("local_Address");
		String permanent_Address = jsonPboject.getString("permanent_Address");
		String c_Plate_Nbr = jsonPboject.getString("c_Plate_Nbr");
		String license_exp_date = jsonPboject.getString("license_exp_date");
		String d_comercial_liscence = jsonPboject.getString("d_comercial_liscence");
		String d_police_verification = jsonPboject.getString("d_police_verification");
		String d_local_Address_proof = jsonPboject.getString("d_local_Address_proof");
		String d_permanent_address_proof = jsonPboject.getString("d_permanent_address_proof");
		String d_photo = jsonPboject.getString("d_photo");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date license = (Date) df.parse(license_exp_date);

		DBConnectionUpd database= new DBConnectionUpd();
		Connection connection = database.getConnection();
		try {

			int count=0;
			String query = "select count(cab_license_plate_no) from ncab_driver_master_tbl where cab_license_plate_no = '" + c_Plate_Nbr + "' ";
			Statement stmt = connection.createStatement();
			ResultSet result = null;           
			result = stmt.executeQuery(query);
			//System.out.println(result.next());
			while(result.next())
			{
				count=result.getInt(1);
			}
			System.out.println(count);
			if(count>=2){
				connection.close();
				return Response.status(200).entity("Data Found").build();                   
			}
			else{
				connection.close();
				System.out.println("No Data Found"); 
				VendorServiceImpl daoimpl=new VendorServiceImpl(); 

				return daoimpl.AddDriver(vendor_id,d_type, first_Name, dPhone_Nbr, local_Address, permanent_Address, c_Plate_Nbr, license, d_comercial_liscence, d_police_verification, d_local_Address_proof, d_permanent_address_proof, d_photo);

			}   

		} catch (Exception e) {
			throw e;
		}
	}

	//adding image

	private static final String UPLOAD_FOLDER = System.getProperty("user.dir")+"/image/";


	@POST
	@Path("/AddImage")
	@Consumes(MediaType.MULTIPART_FORM_DATA)

	public Response addimage(@FormDataParam("file_upload")  InputStream uploadedInputStream,
			@FormDataParam("file_upload") FormDataContentDisposition fileDetail) throws Exception {
		VendorServiceImpl daoimpl=new VendorServiceImpl();
		if (uploadedInputStream == null || fileDetail == null)
			return Response.status(400).entity("Invalid form data").build();

		//System.out.println("Check2");

		// create our destination folder, if it not exists
		try {
			daoimpl.createFolderIfNotExists(UPLOAD_FOLDER);
		} catch (SecurityException se) {
			return Response.status(500).entity("Can not create destination folder on server").build();
		}

		String uploadedFileLocation = UPLOAD_FOLDER + fileDetail.getFileName();;
		try {
			daoimpl.saveToFile(uploadedInputStream, uploadedFileLocation);
		} catch (IOException e) {
			return Response.status(500).entity("Can not save file").build();
		}
		return Response.status(200).entity("File saved to " + uploadedFileLocation).build();
	}

	//adding vendor
	@POST
	@Path("/AddVendor")
	@Consumes(MediaType.APPLICATION_JSON)

	public String addVendor(String params) throws Exception {
		JSONObject jsonPboject = new JSONObject(params);


		String vendor_name = jsonPboject.getString("vendor_name");
		String business_type = jsonPboject.getString("business_type");
		String vendor_contact_num = jsonPboject.getString("vendor_contact_num");
		String vendor_mail_id = jsonPboject.getString("vendor_mail_id");
		String website = jsonPboject.getString("website");
		String pan_id = jsonPboject.getString("pan_id");
		String gst_num = jsonPboject.getString("gst_num");
		int vendor_status = 0;
		String agreement_expiry_date = jsonPboject.getString("agreement_expiry_date");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = (Date) df.parse(agreement_expiry_date);
		int cabs_provided = jsonPboject.getInt("cabs_provided");
		String business_address = jsonPboject.getString("business_address");
		String supervisor_name = jsonPboject.getString("supervisor_name");
		String sup_contact_num = jsonPboject.getString("sup_contact_num");
		String sup_mail_id= jsonPboject.getString("sup_mail_id");
		String manager_name = jsonPboject.getString("manager_name");
		String manager_contact_num = jsonPboject.getString("manager_contact_num");
		String manager_mail_id= jsonPboject.getString("manager_mail_id");
		String owner_name = jsonPboject.getString("owner_name");
		String owner_contact_num = jsonPboject.getString("owner_contact_num");
		String owner_mail_id= jsonPboject.getString("owner_mail_id");
		VendorServiceImpl daoimpl=new VendorServiceImpl(); 
		return daoimpl.addVendorList( vendor_name, business_type, vendor_contact_num, vendor_mail_id, website, pan_id, gst_num, vendor_status, date1, cabs_provided, business_address, supervisor_name, sup_contact_num, sup_mail_id, manager_name, manager_contact_num, manager_mail_id, owner_name, owner_contact_num, owner_mail_id);



	}
	//getting cabdetails

	@POST
	@Path("/CabDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCabDetails(){
		VendorServiceImpl demodaoimpl = new VendorServiceImpl();
		JSONObject jsonresponse = new JSONObject();
		jsonresponse = demodaoimpl.getCabDetails();

		Response response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();



		return response;
	}

	//updating cab

	@POST
	@Path("/Updatecab")
	@Consumes(MediaType.APPLICATION_JSON)

	public Response Updatecab(String params) throws Exception {
		int occupancy=0;
		VendorServiceImpl daoimpl=new VendorServiceImpl();
		JSONObject jsonPboject = new JSONObject(params);
		String onrshp = jsonPboject.getString("contract_owned");
		System.out.println("11");
		System.out.println(onrshp);
		String cab_license_plate_number = jsonPboject.getString("cab_no");
		String model = jsonPboject.getString("model");
		String fuel_type = jsonPboject.getString("fuel");
		String type = jsonPboject.getString("cab_type");
		if(type=="Big")
		{
			occupancy = 6;
		}
		else
		{
			occupancy=4;
		}
		int cab_rate = jsonPboject.getInt("cab_rate");
		String Mdate = jsonPboject.getString("manufacture_date");
		String Rcert = jsonPboject.getString("reg_certi");
		String Pcert = jsonPboject.getString("poll_certi");
		String Fcert = jsonPboject.getString("fit_certi");
		String icert = jsonPboject.getString("insur_certi");
		String entry_tax_haryana_certi = jsonPboject.getString("tax_haryana_certi");
		String entry_tax_delhi_certi = jsonPboject.getString("tax_delhi_certi");
		String entry_tax_up_certi = jsonPboject.getString("tax_up_certi");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date date1 =new  java.sql.Date( df.parse(Mdate).getTime()); 
		String Pdate = jsonPboject.getString("poll_exp");
		Date date2 =new  java.sql.Date(df.parse(Pdate).getTime()); 
		String Fdate = jsonPboject.getString("fit_exp");
		Date date3 =new java.sql.Date(df.parse(Fdate).getTime());
		String idate = jsonPboject.getString("insur_exp");
		Date date4 = new java.sql.Date(df.parse(idate).getTime());
		String entry_tax_haryana_exp_date = jsonPboject.getString("tax_haryana_exp");
		Date date5 = new java.sql.Date(df.parse(entry_tax_haryana_exp_date).getTime());
		String entry_tax_delhi_exp_date = jsonPboject.getString("tax_delhi_exp");
		Date date6 = new java.sql.Date(df.parse(entry_tax_delhi_exp_date).getTime());
		String entry_tax_up_exp_date = jsonPboject.getString("tax_up_exp");
		Date date7 = new java.sql.Date(df.parse(entry_tax_up_exp_date).getTime());




		return daoimpl.updateCab(onrshp, cab_license_plate_number, model, fuel_type, type, occupancy,cab_rate, Rcert,Pcert,Fcert,icert,entry_tax_haryana_certi,entry_tax_delhi_certi,entry_tax_up_certi, date1,date2,date3,date4,date5,date6,date7);

	}

	//disable cab

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/DisableCab")	
	public Response deleteCabDetailsByName(String jsonreqest){		

		VendorServiceImpl demodaoimpl = new VendorServiceImpl();
		JSONObject jsonreq = new JSONObject();
		JSONObject jsonres=new JSONObject();	
		try {
			jsonreq = new JSONObject(jsonreqest);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonres = demodaoimpl.deleteCabDetailsByVendorID(jsonreq);
		System.out.println(jsonres);
		Response response = Response.status(200).type("application/json").entity(jsonres.toString()).build();
		return response;
	}

	//disable vendor

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/DisableVendor")	
	public Response deleteVendorDetailsByName(String jsonreqest){		

		VendorServiceImpl demodaoimpl = new VendorServiceImpl();
		JSONObject jsonreq = new JSONObject();
		JSONObject jsonres=new JSONObject();	
		try {
			jsonreq = new JSONObject(jsonreqest);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonres = demodaoimpl.deleteVendorDetailsByVendorID(jsonreq);
		System.out.println(jsonres);
		Response response = Response.status(200).type("application/json").entity(jsonres.toString()).build();
		return response;
	}

	//enable vendor

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/EnableVendor")
	public Response enableVendorDetailsByName(String jsonrequest){
		VendorServiceImpl daoimpl=new VendorServiceImpl();
		JSONObject jsonreq=new JSONObject();
		JSONObject jsonres=new JSONObject();
		try {
			jsonreq=new JSONObject(jsonrequest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonres = daoimpl.enableVendorDetailsById(jsonreq);
		Response response = Response.status(200).type("application/json").entity(jsonres.toString()).build();
		return response;
	}

	//getting driver details

	@POST
	@Path("/DriverDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDriverDetails(){
		VendorServiceImpl demodaoimpldriver = new VendorServiceImpl();
		JSONObject jsonresponse = new JSONObject();
		jsonresponse = demodaoimpldriver.getDriverDetails();
		Response response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();
		return response;

	}

	//updating driver details

	@POST
	@Path("/UpdateDriver")
	@Consumes(MediaType.APPLICATION_JSON)

	public Response updatedriver(String params) throws Exception {

		JSONObject jsonPboject = new JSONObject(params);
		//JSONObject jsonPboject = new JSONObject(params);
		//	int driver_id = jsonPboject.getInt("driver_id");
		String cab_license_plate_number = jsonPboject.getString("cab_no");
		String driver_name = jsonPboject.getString("d_name");
		String d_contact_num = jsonPboject.getString("d_contact_num");
		String driver_type = jsonPboject.getString("driver_type");
		String driver_local_address = jsonPboject.getString("d_local_add");
		String driver_permanent_address= jsonPboject.getString("d_permanent_add");
		String license_exp_date = jsonPboject.getString("license_exp_date");
		//String Rcert = jsonPboject.getString("Rcert");
		//	String Pcert = jsonPboject.getString("Pcert");
		//String Fcert = jsonPboject.getString("Fcert");
		//String icert = jsonPboject.getString("icert");
		//int driver_status = jsonPboject.getInt("driver_status");
		String driving_license = jsonPboject.getString("d_license");
		String driver_local_address_proof = jsonPboject.getString("d_local_add_proof");
		String driver_permanent_address_proof= jsonPboject.getString("d_permanent_add_proof");
		String driver_photo=jsonPboject.getString("driver_photo");
		String police_verification=jsonPboject.getString("police_verification");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = new java.sql.Date(df.parse(license_exp_date).getTime()); 
		VendorServiceImpl daoimpl = new VendorServiceImpl();
		return daoimpl.getAllDriverList( cab_license_plate_number, driver_name,d_contact_num,driver_type, driver_local_address,driver_permanent_address,driving_license,driver_local_address_proof,driver_permanent_address_proof,driver_photo,police_verification, date1);

	}

	//fetching driver type

	@POST
	@Path("/DriverType")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDriverType(String params) throws ParseException{
		VendorServiceImpl demodaoimpl = new VendorServiceImpl();
		JSONObject jsonreq=new JSONObject(params);
		//String cabno = "UP4F3287";
		String cabno = jsonreq.getString("cab_no");
		//JSONObject jsonresponse = new JSONObject();
		//jsonreq=new JSONObject(jsonrequest);
		return demodaoimpl.getDriverType(cabno);
		//Response response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();
		//return response;
	}

	//filter cabs

	@Path("/SearchCab")
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response getCabDetailsByKey(String jsonrequest){
		VendorServiceImpl daoimpl=new VendorServiceImpl();
		JSONObject jsonreq = new JSONObject();
		JSONObject jsonres = new JSONObject();
		try {
			jsonreq=new JSONObject(jsonrequest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonres=daoimpl.getCabDetailsByKey(jsonreq);
		Response response = Response.status(200).type("application/json").entity(jsonres.toString()).build(); 		
		return response;
	}

	//filter driver

	@Path("/SearchDriver")
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response getDriverDetailsByKey(String jsonrequest){
		VendorServiceImpl daoimpldriver=new VendorServiceImpl();
		JSONObject jsonreq = new JSONObject();
		JSONObject jsonres = new JSONObject();
		try {
			jsonreq=new JSONObject(jsonrequest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonres=daoimpldriver.getDriverDetailsByKey(jsonreq);
		Response response = Response.status(200).type("application/json").entity(jsonres.toString()).build(); 		
		return response;
	}

	//filter vendor

	@Path("/SearchVendor")
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response getVendorDetailsByKey(String jsonrequest){
		VendorServiceImpl daoimpl=new VendorServiceImpl();
		JSONObject jsonreq = new JSONObject();
		JSONObject jsonres = new JSONObject();
		try {
			jsonreq=new JSONObject(jsonrequest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonres=daoimpl.getVendorDetailsByKey(jsonreq);
		Response response = Response.status(200).type("application/json").entity(jsonres.toString()).build(); 		
		return response;
	}

	//updating vendor

	@POST
	@Path("/UpdateVendor")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateVendor(String params) throws Exception {
		JSONObject jsonPboject = new JSONObject(params);
		System.out.println(jsonPboject);
		int vendor_id = jsonPboject.getInt("id");
		String vendor_name = jsonPboject.getString("name");
		String business_type = jsonPboject.getString("bussType");
		String vendor_contact_num = jsonPboject.getString("venContact");
		String vendor_mail_id = jsonPboject.getString("venEmail");
		String website = jsonPboject.getString("website");
		String pan_id = jsonPboject.getString("pan");
		String gst_num = jsonPboject.getString("gstnum");

		int vendor_status = 0;
		String agreement_expiry_date = jsonPboject.getString("agreementExpiry");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = new java.sql.Date(df.parse(agreement_expiry_date).getTime());
		String business_address = jsonPboject.getString("bussAddr");
		String supervisor_name = jsonPboject.getString("supervisorName");
		String sup_contact_num = jsonPboject.getString("supContact");
		String sup_mail_id= jsonPboject.getString("supEmail");
		String manager_name = jsonPboject.getString("manName");
		String manager_contact_num = jsonPboject.getString("manContact");
		String manager_mail_id= jsonPboject.getString("manEmail");
		String owner_name = jsonPboject.getString("ownerName");
		String owner_contact_num = jsonPboject.getString("ownerContact");
		String owner_mail_id= jsonPboject.getString("ownerEmail");

		VendorServiceImpl Dao = new VendorServiceImpl();
		JSONObject jsonresponse= Dao.updatevendor( vendor_id,vendor_name, business_type, vendor_contact_num, vendor_mail_id, website, pan_id, gst_num,vendor_status,date1, business_address, supervisor_name, sup_contact_num, sup_mail_id, manager_name, manager_contact_num, manager_mail_id, owner_name, owner_contact_num, owner_mail_id);


		Response response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();
		return response;

	}

	//getting vendor details

	@POST
	@Path("/VendorDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVendorDetails(){
		VendorServiceImpl demodaoimpl = new VendorServiceImpl();
		JSONObject jsonresponse = new JSONObject();
		jsonresponse = demodaoimpl.getVendorDetails();
		Response response = Response.status(200).type("application/json").entity(jsonresponse.toString()).build();
		return response;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/EnableDriver")
	public Response enableCabDetailsByName(String jsonrequest){
		VendorServiceImpl daoimpl=new VendorServiceImpl();
		JSONObject jsonreq=new JSONObject();
		JSONObject jsonres=new JSONObject();
		try {
			jsonreq=new JSONObject(jsonrequest);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonres = daoimpl.enableDriverDetailsById(jsonreq);
		Response response = Response.status(200).type("application/json").entity(jsonres.toString()).build();
		return response;
	}
	

@POST

	@Produces(MediaType.APPLICATION_JSON)
	@Path("/DisableDriver")	
	public Response deleteDriverDetailsByName(String jsonreqest){		

		VendorServiceImpl demodaoimpl = new VendorServiceImpl();
		JSONObject jsonreq = new JSONObject();
		JSONObject jsonres=new JSONObject();	
		try {
			jsonreq = new JSONObject(jsonreqest);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonres = demodaoimpl.deleteDriverDetailsByDriverID(jsonreq);
		System.out.println(jsonres);
		Response response = Response.status(200).type("application/json").entity(jsonres.toString()).build();
		return response;
	}



}