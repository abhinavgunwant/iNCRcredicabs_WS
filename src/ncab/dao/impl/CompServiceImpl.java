package ncab.dao.impl;

import java.awt.HeadlessException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONObject;

public class CompServiceImpl {

	
public boolean sendEmailMessage(String from,String recepient1,String recepient2 ,String recepient3,String subject,String messageAttribute) {
		
		//String host = "localhost";
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
		    	System.out.println(subject);
		    	System.out.println(messageAttribute);
		    	
		    	
		    	message.setSubject(subject);
		    	message.setContent(messageAttribute, "text/html; charset=utf-8");
		    	
		    	//message.setText(messageAttribute);
		    	
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
