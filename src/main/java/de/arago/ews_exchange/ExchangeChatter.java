package de.arago.ews_exchange;

import java.util.UUID;

import de.arago.ews_exchange.server.ServiceFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.enumeration.service.ResponseMessageType;
import microsoft.exchange.webservices.data.core.service.item.Conversation;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.response.ResponseMessage;
import microsoft.exchange.webservices.data.property.complex.ConversationId;
import microsoft.exchange.webservices.data.property.complex.ExtendedProperty;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;

public class ExchangeChatter {
	
	
	public String sendChatMessage() throws Exception{
		

		ExchangeService service = ServiceFactory.getService(); 
		
		Conversation con = new Conversation(service);
		; 
		EmailMessage emailMessage = new EmailMessage(service);
		emailMessage.setSubject("Question ");
		MessageBody mB = MessageBody.getMessageBodyFromText("hi");
		mB.setBodyType(BodyType.Text);
		emailMessage.setBody(mB);

	//	emailMessage.getToRecipients().add("@arago.de");
		
	
		
		
	//	
		
		ItemId id = emailMessage.getId(); 

		emailMessage.setIsResponseRequested(true);
		emailMessage.save();

		

		emailMessage.load();
		
		id = emailMessage.getId(); 
		emailMessage.send();
		
		

		ConversationId cID = emailMessage.getConversationId(); 
		
		for ( ExtendedProperty ep : emailMessage.getExtendedProperties()){
			System.out.println("ep: " + ep.getValue());
		}
		
		System.out.println("original message has the id " + id + " cID" + cID );
		
		return null; 
	}

}
