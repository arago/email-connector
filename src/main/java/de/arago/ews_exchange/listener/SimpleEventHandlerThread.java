package de.arago.ews_exchange.listener;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.notification.GetEventsResults;
import microsoft.exchange.webservices.data.notification.ItemEvent;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

public class SimpleEventHandlerThread extends HandlerThread{
	
	private static final Logger log = LoggerFactory.getLogger(SimpleEventHandlerThread.class); 

	public SimpleEventHandlerThread(ExchangeService service, GetEventsResults events, Map<String,String> confMap, MessageSender messageSender, MessageTransformer messageTransformer,  FolderId inboundFolderID, FolderId invalidMessageFolderId, FolderId validMessageFolderId,String incommingFolder) {
		super(service, events, confMap, messageSender, messageTransformer, inboundFolderID, invalidMessageFolderId,validMessageFolderId,  incommingFolder);
	}

	
	public int sendMessage(String sendMessage) {
		log.trace("mapped message: " + sendMessage);
		return messageSender.sendMessage(sendMessage); 
		
		
		
	}
	
		
		
	public void run(){
		
		
		
		for(ItemEvent event : events.getItemEvents()){
			System.out.println("found event");
		
			EmailMessage message;
			try {
				
				ItemId id = event.getItemId(); 
				message = EmailMessage.bind(service, id);
				FolderId folderID = message.getParentFolderId();
				if(folderID.equals(inboundFolderID)){
					MessageBody body = message.getBody(); 
					log.trace("message has the body: \n" + body);
					BodyType messageType = body.getBodyType(); 
				 
						
					String rawMessage = body.toString();
						JSONObject jO = null; 
						
						jO = messageTransformer.transform(rawMessage,messageType, incommingFolder );
						if(jO != null){
							int sendRc = sendMessage(jO.toString()); 
							exchangeMessageHandler.handleSendMessage(message, sendRc);
						}
			
			}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			 
			 
		}
		
	}


	

}


