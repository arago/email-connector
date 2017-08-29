package de.arago.ews_exchange;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

import de.arago.ews_exchange.server.ServiceFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.ItemView;

public class ExchangeChecker {
	private String password; 
	private String email; 
	private ExchangeService service; //
	
	public void closeService(){
		service.close();
	}
	
	public ExchangeService getService(){
		return service; 
	}
	
	public  ExchangeChecker(String email, String password, String uri, String exchangeVersion) throws URISyntaxException {
		ExchangeVersion eV = ExchangeVersion.Exchange2010_SP2; 
			eV = ExchangeVersion.valueOf(exchangeVersion);
		if(eV==null){
			eV = ExchangeVersion.Exchange2010_SP2; 
		}	
		 this.password = password;
		 this.email = email; 
		 this.service = ServiceFactory.getService();
		this.service.setUrl(new java.net.URI(uri)); 

	}
	
	public Folder getFolder(String folderName) throws Exception{
		FindFoldersResults findResults = service.findFolders(WellKnownFolderName.Inbox, new FolderView(Integer.MAX_VALUE));
		Folder f = null ; 
	    for (Folder folder : findResults.getFolders()) {
	    	if(folder.getDisplayName().equals(folderName)){
	    		 f = folder;  
	    	}
	     
	       
	    }
		  return f; 
	}
	public Folder getInbox(){
		Folder inbox = null; 
		try {
			inbox=  Folder.bind(service, WellKnownFolderName.Inbox);
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}; 
		return inbox; 
	}
	
	public int getNumEmailsInFolder(Folder f) {
		int num = -1; 
		if(f!=null)
		try {
			num = f.getTotalCount();
		} catch (NumberFormatException e) {
			System.err.println(e.getMessage());
		} catch (ServiceLocalException e) {
			System.err.println(e.getMessage());
		} 		
		return  num; 
	}
	
	private FindItemsResults<Item> getItems(Folder f){
		FindItemsResults<Item> findResults = null; 
		 int numMails = getNumEmailsInFolder(f);
		 if(numMails>0){
			 ItemView view = new ItemView(numMails);
			 try {
				findResults = service.findItems(f.getId(), view);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		 }
		return findResults; 
	}
	
	public ArrayList<EmailMessage> getEmailList(Folder f){
		FindItemsResults<Item> findResults= getItems(f); 	
		ArrayList<EmailMessage> eList = new ArrayList<EmailMessage>(); 

		 if(findResults!=null)
			 for (Item item: findResults){
				 EmailMessage em = null; 
					 em = (EmailMessage) item; 
					 if(em !=null){
						eList.add(em); 
			        	}
					 }
		return eList;
	}
	
	
	
	public Set<String> listSendersMonth(ArrayList<EmailMessage> eList, Date refDate,String attributes ) throws Exception{
		Set<String> senderSet = new HashSet<String>();
			 for (EmailMessage em: eList){
						 Date date = em.getDateTimeReceived();
						 int monthToday = refDate.getMonth(); 
						 int monthNum = date.getMonth(); 
						 int yearToday = refDate.getYear(); 
						 int yearNum = date.getYear(); 
			        	if(monthToday == monthNum && yearToday == yearNum){
			        		String senderName =  getDetails(em, attributes).toString(); 
			        		senderSet.add(senderName); 
			        	}
	        }
		return senderSet; 
	}
	
	public JSONObject getDetails(EmailMessage em, String attributes){
		JSONObject jo = new JSONObject(); try {
			
		if(attributes.contains("Sendername")){
				jo.put("Sendername", em.getFrom().getName());
		}
		if(attributes.contains("Senderaddress")){
			jo.put("Senderaddress", em.getFrom().getAddress());
		}
		if(attributes.contains("Body")){
			try {
				em.load();
				MessageBody body = em.getBody(); 
				jo.put("Body", body.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		if(attributes.contains("Subject")){
			jo.put("Subject", em.getSubject());
		}
		if(attributes.contains("DateRecieved")){
			jo.put("DateRecieved", em.getDateTimeReceived());
		}
		if(attributes.contains("DateCreated")){
			jo.put("DateCreated", em.getDateTimeCreated());
		}
		if(attributes.contains("DateSend")){
			jo.put("DateSend", em.getDateTimeSent());
		}
		if(attributes.contains("Importance")){
			jo.put("Importance", em.getImportance());
		}
	
		
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ServiceLocalException e) {
			e.printStackTrace();
		}
		return jo; 
	}
	
	
	public Set<String> listSendersCurrentMonth(ArrayList<EmailMessage> eList, String attributes) throws Exception{
		Date today = new Date(); 
		return  listSendersMonth(eList,today,  attributes);
	
		
	}
	
	public Set<String> listSendersDay(ArrayList<EmailMessage> eList, Date refDate, String attributes) throws Exception{
		Set<String> senderSet = new HashSet<String>();
			 for (EmailMessage em: eList){
						 Date date = em.getDateTimeReceived();
						 
						 int monthToday = refDate.getMonth(); 
						 int monthNum = date.getMonth(); 
						 int yearToday = refDate.getYear(); 
						 int yearNum = date.getYear();
						 int dayToday = refDate.getDate(); 
						 int dayNum = date.getDate();
						 
			        	if(monthToday == monthNum && yearToday == yearNum && dayToday==dayNum){
						 
			        		String senderName =  getDetails(em, attributes).toString();
			        		senderSet.add(senderName); 
			        	}
	        }
		return senderSet; 
	}
	
	public Set<String> listSendersYear(ArrayList<EmailMessage> eList, Date refDate, String attributes) throws Exception{
		Set<String> senderSet = new HashSet<String>();
			 for (EmailMessage em: eList){
						 Date date = em.getDateTimeReceived();
						 int yearToday = refDate.getYear(); 
						 int yearNum = date.getYear(); 
			        	if(yearToday == yearNum){
			        		String senderName =  getDetails(em, attributes).toString(); 
			        		senderSet.add(senderName); 
			        	}
	        }
		return senderSet; 
	}
	

	public Set<String> listSendersCurrentYear(ArrayList<EmailMessage> eList, String attributes) throws Exception{
		Date today = new Date(); 
		return  listSendersYear(eList,today,  attributes);
	}
	
	
	public void checkSenderUp(String mailaddress){
		try {
			EmailMessage msg= new EmailMessage(service);
			msg.setSubject("Exchange Server ");
			msg.setBody(MessageBody.getMessageBodyFromText("Exchange Server is up"));
			msg.getToRecipients().add(mailaddress);
			msg.send();
		} catch (Exception e) {
			e.printStackTrace();
		}

	
	
}

	public Set listSendersCurrentDay(ArrayList<EmailMessage> eList, String attributes) throws Exception {
		 Date today = new Date(); 
		return  listSendersDay(eList,today, attributes );
	}
}