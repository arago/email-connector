package de.arago.ews_exchange;

import java.util.Map;

import de.arago.ews_exchange.listener.HandlerThread;
import de.arago.ews_exchange.listener.SimpleEventHandlerThread;
import de.arago.ews_exchange.server.ServiceFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.notification.EventType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.response.ResponseMessage;
import microsoft.exchange.webservices.data.misc.IAsyncResult;
import microsoft.exchange.webservices.data.notification.GetEventsResults;
import microsoft.exchange.webservices.data.notification.ItemEvent;
import microsoft.exchange.webservices.data.notification.PullSubscription;
import microsoft.exchange.webservices.data.property.complex.ExtendedProperty;
import microsoft.exchange.webservices.data.property.complex.ExtendedPropertyCollection;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.definition.ComplexPropertyDefinition;

public class ExchangeChatReciever {

	private static ExchangeService service; 
	
	public static void main(String[] args ){
		ServiceFactory sF = new ServiceFactory(); 
		Map<String,String> confMap = PropertiesReader.getConfMap(null); 
		sF.init(confMap);
		service = ServiceFactory.getService(); 
		run(); 
		
	}
	public static IAsyncResult startListening() throws Exception{
		IAsyncResult asyncresult = service.beginSubscribeToPullNotificationsOnAllFolders(null, null, 10, null, EventType.NewMail);
		return asyncresult; 
		}
		
		public static void stopListning(IAsyncResult asyncresult) throws Exception {
			PullSubscription subscription = service.endSubscribeToPullNotifications(asyncresult);
			GetEventsResults events = subscription.getEvents();
			subscription.unsubscribe();
			for(ItemEvent event : events.getItemEvents()){
				EmailMessage message;  //= (EmailMessage)  event.getItemId(); ; 
				ItemId id = event.getItemId(); 
				message = EmailMessage.bind(service, id);
				; 
				System.out.println(message.getSubject() + "  ID" + id.toString() + "  CID " + message.getConversationId());
				System.out.println(message.getBody());

				
				
			}
		}
		
		public static void run(){
			while(true){
				try {
					IAsyncResult asyncresult = startListening();
					Thread.sleep(30000);
					stopListning(asyncresult);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				catch (Exception e1) {
					e1.printStackTrace();
				} 
				
			}
		}
}
