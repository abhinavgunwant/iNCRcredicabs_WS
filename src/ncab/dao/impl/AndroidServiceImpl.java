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

                                           if(act.equalsIgnoreCase("UNSEEN")){

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
                                                                        
                     pushmsg = "Cab Request Id:"+r+"\n"+
                                           "Employee QLId:"+Emp_Qlid+"\n"+
                                                          "From: "+from+"\n"+
                                                          "To: "+ to+"\n"+
                                                          "Source: "+source+"\n"+
                                                          "Destination: "+destination+"\n"+
                                                          "Time: "+time+"\n"+
                                                          "Status: "+"Request has been "+Approval+"\n"+
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









}

