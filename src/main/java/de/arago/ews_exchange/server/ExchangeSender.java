package de.arago.ews_exchange.server;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.WebProxy;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

public class ExchangeSender implements MessageBackSender {
	
	public ExchangeSender(Map<String,String> confMap){
		
		this.confMap = confMap; 
		if(confMap!=null && confMap.containsKey("notificationTitle")){
			returnMessage = confMap.get("notificationTitle"); 
		}
		if(confMap!= null && confMap.containsKey("notificationEmailAddress")){
			
			notificationMailAddress = confMap.get("notificationEmailAddress"); 
			log.trace("return messages will go to : " +notificationMailAddress);
		}
	}
	
	
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeSender.class); 
	
	private 	Map<String,String> confMap; 
	private 	ExchangeService service; 
	private 	EmailAddress mailaddress;
	private 	String returnMessage = "RetrunMessage"; 
	private 	String notificationMailAddress; 
	


	public boolean sendMessage(String message) {
		 System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
		
		service = ServiceFactory.getService(); 
		
		
		try {
			
			
			
			EmailMessage msg= new EmailMessage(service);
			
			msg.setSubject(returnMessage);
	
			msg.setBody(MessageBody.getMessageBodyFromText(message));
			msg.getToRecipients().add(notificationMailAddress);
			log.debug("Sending back "+returnMessage + " to " + notificationMailAddress);
			log.debug(message);
			msg.send();
			return true; 
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	

}
