package ncab.dao.impl;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;


public class UtilServiceImpl {

	
	public int sendPushMessage(String to,String title, String message ){
	    int result = 0;
		
		URL url;
		HttpURLConnection connection = null;
		
	try{
		url = new URL("https://fcm.googleapis.com/fcm/send");
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Authorization","key=AIzaSyCB4SW8EX0m5GTupOJuOXJTC92t6HcgE6U");
		connection.setRequestProperty("Content-Type","application/json");
		connection.setDoOutput(true);
			
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		
		JSONObject req = new JSONObject();
		req.put("to",to);
		JSONObject data = new JSONObject();
		data.put("title",title);
		data.put("message",message);
		
		req.put("data",data);
		
		
		wr.writeBytes(req.toString());
		wr.flush();
		wr.close();
		
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		String line;
		StringBuffer response = new StringBuffer();
		while((line = rd.readLine()) != null){
			response.append(line);
			response.append('\r');
		}
		rd.close();

		response.toString();
		
		
		
		int success_count=response.charAt(response.indexOf("success:")+8);
		
		
		int failure_count=response.charAt(response.indexOf("failure:")+8);
		
		if(success_count==1)
		result =1;
		else if(failure_count==1)
	    result = 0;

		return result;
	}
	catch (Exception e){
		e.printStackTrace();
		return -1;
		
	}	finally{
		if(connection!= null)
			connection.disconnect();
	
	}
		
		}

	public boolean sendEmailMessage(String from,String recepient1,String recepient2 ,String recepient3,String subject,String messageAttribute) {
		
		String host = "localhost";
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		    
		    
		Session mySession = Session.getInstance(props, new Authenticator(){

			protected PasswordAuthentication getPasswordAuthentication()
		    	{
		    		return new PasswordAuthentication("javamailsystem1@gmail.com","javamail1");
			
		    	}
		    });
		    
		    try
		    {
		    	MimeMessage message = new MimeMessage(mySession);
		    	message.setFrom(new InternetAddress(from));
		    	message.addRecipient(Message.RecipientType.TO,new InternetAddress(recepient1));
		    	message.addRecipients(Message.RecipientType.CC, 
	                      InternetAddress.parse(recepient2+","+recepient3));
		    	//message.addRecipient(Message.RecipientType.CC,new InternetAddress(recepient2 + ","+recepient3));
		    //	message.addRecipient(Message.RecipientType.BCC,new InternetAddress(recepient3));

		    	message.setSubject(subject);

		    	message.setContent(messageAttribute, "text/html; charset=utf-8"); 
		    	System.out.println("App Engine: Sending Mail to " + recepient1);
		    	System.out.println("App Engine: Sending CC Mail to " + recepient2);
		    	System.out.println("App Engine: Sending CC Mail to " + recepient3);

		    	Transport.send(message);
		    	System.out.println("AppEngine: Message Sent");
		    	return true;

		    }catch( HeadlessException | MessagingException e)
		    {
		    	e.printStackTrace();
		    	return false;
		    }

	}
	
	
}
		
	

