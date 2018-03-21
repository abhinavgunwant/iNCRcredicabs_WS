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

public class AndroidServiceImpl {

       List<JSONObject> jsonArray;
       JSONArray jsonresponse;
    DBConnectionRo dbconnectionRo = new DBConnectionRo();
    DBConnectionUpd dbconnectionUpd=new DBConnectionUpd();
       Connection con = null;
       

       public int saveApproval(String action,String req_id) {
             // TODO Auto-generated method stub
             int result=0;
             
             try {  
                    con=(Connection) dbconnectionUpd.getConnection();

                    PreparedStatement ps = con.prepareStatement("UPDATE NCAB_UNSCHEDULE_RQST_TBL SET Approval=? WHERE Request_ID=?");
             
                    ps.setString(1,action);
                    ps.setString(2, req_id);
                    
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


       
       public JSONArray getRequest(JSONObject requestJson) throws SQLException {
             RequestServiceImpl requestserviceimpl=new RequestServiceImpl();
             
                    try {
                          jsonresponse = new JSONArray(requestserviceimpl.getUnscheduledRequest(requestJson.getString("Allocated")));
                    } catch (NoSuchElementException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                    }
                    return jsonresponse;
             }
             

       
       
       
              
       


}



