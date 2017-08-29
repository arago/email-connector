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
		//<html><body><form ><input type='text' name='testVar' value='yes'/>   </form></body></html>
		MessageBody mB = MessageBody.getMessageBodyFromText("hi");
		mB.setBodyType(BodyType.Text);
		emailMessage.setBody(mB);

		emailMessage.getToRecipients().add("ppelchmann@arago.de");
		
	
		
		
	//	
		
		ItemId id = emailMessage.getId(); 
//		UUID yourPropertySetId = UUID.fromString("01638372-9F96-43b2-A403-B504ED14A910");
//		ExtendedPropertyDefinition extendedPropertyDefinition = new ExtendedPropertyDefinition(
//			    yourPropertySetId, "myUIDprop", MapiPropertyType.String);
//
//			// Stamp the extended property on a message.
//		emailMessage.setExtendedProperty(extendedPropertyDefinition, "myUID");
		emailMessage.setIsResponseRequested(true);
//			// Save the message.
		emailMessage.save();

		

//		ResponseMessage responseMessage = emailMessage.createReply(true);
//		
////		ResponseMessage responseMessage = new ResponseMessage(emailMessage, ResponseMessageType.Reply); 
//		MessageBody mB = new MessageBody("response");
//		responseMessage.setSubject("MyAnswer" );
//		responseMessage.setBody(mB);
//		responseMessage.setBodyPrefix(mB);
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
