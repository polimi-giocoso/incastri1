package com.example.legodigitalsonoro1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 * @author chiara
 *
 */
public class Mail extends javax.mail.Authenticator{
	 private String mailhost = "smtp.gmail.com";   
	    private String user;   
	    private String password;   
	    private Session session;
		private String sender;
		private String subject;
		private String recipients;
		private String body;   

	    static {   
	        Security.addProvider(new JSSEProvider());   
	    }  

	    public Mail() { 
	    	//"legodigitalsonoro@gmail.com"
	        this.user = "legodigitalsonoro@gmail.com"; 
	        //"legodigitalsonoro123"
	        this.password = "legodigitalsonoro123";   

	        Properties props = new Properties();   
	        props.setProperty("mail.transport.protocol", "smtp");   
	        props.setProperty("mail.host", mailhost);   
	        props.put("mail.smtp.auth", "true");   
	        props.put("mail.smtp.port", "465");   
	        props.put("mail.smtp.socketFactory.port", "465");   
	        props.put("mail.smtp.socketFactory.class",   
	                "javax.net.ssl.SSLSocketFactory");   
	        props.put("mail.smtp.socketFactory.fallback", "false");   
	        props.setProperty("mail.smtp.quitwait", "false");   

	        session = Session.getDefaultInstance(props, this);   
	    }   

	    protected PasswordAuthentication getPasswordAuthentication() {   
	        return new PasswordAuthentication(user, password);   
	    }   

	    public synchronized void sendMail() throws Exception {   
	        
	        MimeMessage message = new MimeMessage(session);   
	        DataHandler handler = new DataHandler(new ByteArrayDataSource((body.getBytes()), "text/plain"));   
	        message.setSender(new InternetAddress(sender));   
	        message.setSubject(subject);   
	        message.setDataHandler(handler);   
	        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));   
	        Transport.send(message);   
	    }   

	    public class ByteArrayDataSource implements javax.activation.DataSource {   
	        private byte[] data;   
	        private String type;   

	        public ByteArrayDataSource(byte[] data, String type) {   
	            super();   
	            this.data = data;   
	            this.type = type;   
	        }   

	        public ByteArrayDataSource(byte[] data) {   
	            super();   
	            this.data = data;   
	        }   

	        public void setType(String type) {   
	            this.type = type;   
	        }   

	        public String getContentType() {   
	            if (type == null)   
	                return "application/octet-stream";   
	            else  
	                return type;   
	        }   

	        public InputStream getInputStream() throws IOException {   
	            return new ByteArrayInputStream(data);   
	        }   

	        public String getName() {   
	            return "ByteArrayDataSource";   
	        }   

	        public OutputStream getOutputStream() throws IOException {   
	            throw new IOException("Not Supported");   
	        }   
	    }

		public void setTo(String toArr) {
			this.recipients=toArr;
		}

		public void setFrom() {
			this.sender="legodigitalsonoro@gmail.com";
		}

		public void setSubject() {
			this.subject="Email from LEGODigitalSound1";
		}

		public void setBody(String string) {
			this.body=string;			
		}

		public String getTo() {
			return recipients;
		}   
}
