package de.arago.ews_exchange.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.exception.service.remote.ServiceResponseException;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.FolderId;

public class ExchangeMessageHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeMessageHandler.class); 
	
	private FolderId invalidMassageFolderId; 
	private FolderId validMessageFolderId; 
	
	public ExchangeMessageHandler ( FolderId invalidMassageFolderId, FolderId validMessageFolderId){
		this.invalidMassageFolderId = invalidMassageFolderId; 
		this.validMessageFolderId = validMessageFolderId;
	}
	
	public  void handleSendMessage(EmailMessage message, int sendRc) throws Exception, ServiceResponseException {
		log.trace("message has Status "+ sendRc );
		
		if(sendRc>=200&&sendRc<300){
			
			
			try {
				log.debug("setting message to read");
				message.setIsRead(true);
				message.update(ConflictResolutionMode.AutoResolve);
				log.trace("moving message");
				message.move(validMessageFolderId); 
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(sendRc==400){
			log.trace("moving invalid message with status "+ sendRc + "");
			message.move(invalidMassageFolderId); 
		}
	}

}
