package de.arago.ews_exchange.listener;




import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import de.arago.ews_exchange.ExchangeChecker;
import de.arago.ews_exchange.server.ServiceFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.notification.EventType;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.IAsyncResult;
import microsoft.exchange.webservices.data.notification.GetEventsResults;
import microsoft.exchange.webservices.data.notification.PullSubscription;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;

public class ExchangeListener {
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeListener.class); 
	
	private ExchangeService service; 
	private Map<String,String> confMap; 
	private MessageSender messageSender; 
	private MessageTransformer messageTransformer; 
	private List<String> incommingFolderList; 
	private String invalidMessageFolder; 
	private String validMessageFolder; 
	private String email; 
	private String password; 
	private String uri; 
	private String exchangeVersion;
	private	Map<String, FolderId> inFolderId; 
	private FolderId validMessageFolderId; 
	private FolderId invalidMessageFolderId; 
	private int refreshInterval=30000; 
	private String listenMode; 
	
	private Map<String,FolderId> getFolderId(List<String> folderNames){
		Map idMap = new HashMap<String,FolderId>(); 
		
		
		FindFoldersResults findResults= null;
		try {
			findResults = service.findFolders(WellKnownFolderName.Inbox, new FolderView(Integer.MAX_VALUE));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Folder f = null ; 
		FolderId id = null; 
		if(findResults!=null){
			
	    for (Folder folder : findResults.getFolders()) {
	    	try {
	    		for(String folderName : folderNames){
				if(folder.getDisplayName().equals(folderName)){
					 f = folder;  
					 id = f.getId(); 
					 idMap.put(folderName, id); 
				}
	    		}
			} catch (ServiceLocalException e) {
				e.printStackTrace();
			}
	    	
	    }
	       
	    }
	    
		  return idMap; 
	
}

	
	private FolderId getFolderId(String folderName){
			FindFoldersResults findResults= null;
			try {
				findResults = service.findFolders(WellKnownFolderName.Inbox, new FolderView(Integer.MAX_VALUE));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Folder f = null ; 
			FolderId id = null; 
			if(findResults!=null){
				
		    for (Folder folder : findResults.getFolders()) {
		    	try {
					if(folder.getDisplayName().equals(folderName)){
						 f = folder;  
						 id = f.getId(); 
					}
				} catch (ServiceLocalException e) {
					e.printStackTrace();
				}
		     
		       
		    }
		    }
			  return id; 
		
	}
	
	public ExchangeListener(String email, String password, String uri, String exchangeVersion, Map<String,String> confMap, MessageSender messageSender, MessageTransformer messageTransformer){
		ExchangeVersion eV= null; 
		this.password = password; 
		this.email = email; 
		this.uri = uri; 
		this.exchangeVersion  = exchangeVersion; 
		this.confMap = confMap; 
		this.messageSender = messageSender; 
		String inList = confMap.get("incommingFolder"); 
		String[] inFols = inList.split(","); 
		List<String> inFoList = new ArrayList<String>(); 
		log.trace(""+inList + " "+  inFols + " " + inFols.length);
		for (int i = 0; i < inFols.length;  i++){
			String fna = inFols[i]; 
			inFoList.add(fna); 
		}
		this.incommingFolderList = inFoList; 
		this.invalidMessageFolder = confMap.get("invalidTicketsFolder"); 
		this.validMessageFolder = confMap.get("validTicketFolder"); 
		this.messageTransformer = messageTransformer; 
		
		if(confMap.containsKey("listenMode")){
			listenMode = confMap.get("listenMode"); 
		}
		if(confMap.containsKey("refreshInterval")){
			refreshInterval = Integer.parseInt(confMap.get("refreshInterval")); 
			log.trace("Setting refresh interval to: " + confMap.get("refreshInterval") );
			
		}
	

		this.service = ServiceFactory.getService();
		 ExchangeCredentials credentials = new WebCredentials(email, password);
		 this.service.setCredentials(credentials);
		try {
			this.service.setUrl(new java.net.URI(uri));
		} catch (URISyntaxException e) {
			System.err.println(e.getMessage());
		} 
		 
		
		 
	}
	
	
	
	
	public ExchangeListener( ExchangeService service){
		this.service = service; 
	}
	
	public IAsyncResult startListening() throws Exception{
		IAsyncResult asyncresult = service.beginSubscribeToPullNotificationsOnAllFolders(null, null, 10, null, EventType.NewMail);
	return asyncresult; 
	}
	
	

	public void stopListning(IAsyncResult asyncresult) throws Exception {
		PullSubscription subscription = service.endSubscribeToPullNotifications(asyncresult);
		GetEventsResults events = subscription.getEvents();
		subscription.unsubscribe();
		if(inFolderId== null)
			inFolderId  = getFolderId(incommingFolderList);
		if(invalidMessageFolderId == null)
			invalidMessageFolderId = getFolderId(invalidMessageFolder); 
		if(validMessageFolderId == null)
			validMessageFolderId = getFolderId(validMessageFolder); 
		
		for(String incommingFolder : incommingFolderList){
			FolderId iFId = inFolderId.get(incommingFolder); 
		
		
		HandlerThread eH = new SimpleEventHandlerThread(service, events,confMap,  messageSender, messageTransformer, iFId, invalidMessageFolderId, validMessageFolderId, incommingFolder); 
		eH.start();
		}
	
	}
	
	public void initialSend(){
		ExchangeChecker exChecker=null;
		try {
			exChecker = new ExchangeChecker(email, password, uri, exchangeVersion);
			for(String folderName : incommingFolderList){
		
			
			Folder incFolder = exChecker.getFolder(folderName); 
			ArrayList<EmailMessage> messages = exChecker.getEmailList(incFolder); 
			for(EmailMessage message : messages){
				message.load();
					if(invalidMessageFolderId == null)
						invalidMessageFolderId = getFolderId(invalidMessageFolder); 
					if(validMessageFolderId == null)
						validMessageFolderId = getFolderId(validMessageFolder); 
					 MessageBody body = message.getBody();
					BodyType messageType = body.getBodyType(); 
					
					JSONObject transformedMessage = messageTransformer.transform(body.toString(), messageType, folderName);
					if(transformedMessage !=null && messageSender!=null) {
						int sendRc = messageSender.sendMessage(transformedMessage.toString());
					  	ExchangeMessageHandler exchangeMessageHandler = new ExchangeMessageHandler(invalidMessageFolderId, validMessageFolderId) ;
					  	exchangeMessageHandler .handleSendMessage(message, sendRc);
					}
			}
			
		}
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(exChecker!=null){
				log.trace("closing service");
				exChecker.closeService();
			}
		}
	}
	
	
	private void listen(){
		if(listenMode.equals("PULL")){
			 listenPull(); 
		}else if (listenMode.equals("ASYNCPULL")){
			listenAsyncPull(); 
		}else if (listenMode.equals("ASYNCPUSH")){
			
		}
	}
	
	public void start() {
		initialSend(); 
		while(true){
			listen();
		}
		
	}
	
	
	private void listenPull() {
		try {
			log.info("waiting "+refreshInterval/1000 +"s for new mails");
			Thread.sleep(refreshInterval);
			initialSend(); 
	
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void listenAsyncPull() {
		try {
			IAsyncResult asyncresult = startListening();
			log.info("waiting "+refreshInterval/1000 +"s for new mails");
			Thread.sleep(refreshInterval);
			
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
