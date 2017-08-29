package de.arago.ews_exchange;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

public class ExchangePrinter {

	private ExchangeChecker exchangeChecker; 
	
	
	private String setToString(Set<String> s){
		 String setString = ""; 
		 int numSendersInList = 0; 
		 for (String sn : s){        	
			 numSendersInList ++; 
			 if(numSendersInList == s.size()){
				 setString += sn; 
        	}
			 else{
				 setString += sn +"\n"; 
			 }
			 
			
       } 
		 return setString;
	}
		 
	 

	
public ExchangePrinter(ExchangeChecker ec){
	exchangeChecker = ec; 
	}

public void printSenderCurrentMonth(String attributes)  {
	
	try {
		Folder f = exchangeChecker.getInbox(); 
		ArrayList<EmailMessage> eList = exchangeChecker.getEmailList(f); 
		Set s = exchangeChecker.listSendersCurrentMonth(eList, attributes); 
		System.out.println(setToString(s));
	} catch (Exception e) {
		e.printStackTrace();
	}

}

public void printSenderMonth(String attributes, Date refDate)  {
	
	try {
		Folder f = exchangeChecker.getInbox(); 
		ArrayList<EmailMessage> eList = exchangeChecker.getEmailList(f); 
	
		Set s = exchangeChecker.listSendersMonth(eList, refDate, attributes); 
		System.out.println(setToString(s));
	} catch (Exception e) {
		e.printStackTrace();
	}

}

public void printSenderCurrentYear(String attributes)  {
	
	try {
		Folder f = exchangeChecker.getInbox(); 
		ArrayList<EmailMessage> eList = exchangeChecker.getEmailList(f); 
		Set s = exchangeChecker.listSendersCurrentYear(eList,  attributes); 
		System.out.println(setToString(s));
	} catch (Exception e) {
		e.printStackTrace();
	}

}


public void printSenderCurrentDay(String attributes)  {
	
	try {
		Folder f = exchangeChecker.getInbox(); 
		ArrayList<EmailMessage> eList = exchangeChecker.getEmailList(f); 
		Set s = exchangeChecker.listSendersCurrentDay(eList, attributes); 
		System.out.println(setToString(s));
	} catch (Exception e) {
		e.printStackTrace();
	}

}



public void printNumberEmailsInInbox(){
	Folder f = exchangeChecker.getInbox(); 
	System.out.println(exchangeChecker.getNumEmailsInFolder(f));;

}




public void print(String[] args) {
	String mode = null;
	String dateMode = null; 
	String dateAsString = null; 
	String attributesList = null; 
	if(args.length>0)
		mode= args[0];
	
	if(args.length == 4){
		dateMode = args[1]; 
		dateAsString = args[2]; 
		DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Date date= null;
		
		
		attributesList = args[3];
		
		if(mode.equals("Get")){
			if(dateAsString.equals("today")){
				if(dateMode.equals("Day")){
					printSenderCurrentDay(attributesList);
				}
				if(dateMode.equals("Month")){
					printSenderCurrentMonth(attributesList);
				}
				if(dateMode.equals("Year")){
					printSenderCurrentYear(attributesList);
				}
			}
			else{
				try {
					date = format.parse(dateAsString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(date!=null){
					if(dateMode.equals("Day")){
						printSenderDay(attributesList, date);
					}
					if(dateMode.equals("Month")){
						printSenderMonth(attributesList, date);
					}
					if(dateMode.equals("Year")){
						printSenderYear(attributesList, date);
					}
				}
			}
		}
	}
	else if(mode!=null && mode.equals("NumberOfMails")){	
			  if(args.length > 1){
				String folderName = args[1]; 
				printNumberEmailsInFolder(folderName); 
			}
			else{
			printNumberEmailsInInbox();}
		}
	else if(mode!=null && mode.equals("CheckExchangeUp")&& args.length>1){
		String mailaddress = args[1];
		exchangeChecker.checkSenderUp(mailaddress);
		System.out.println("Mail was send to " +mailaddress);
	}
		else{
			System.err.println("no mode for " +mode);
		}
		
	
if(mode == null ){
	System.err.println("empty mode ");
}

}




private void printSenderYear(String attributesList, Date date) {
	try {
		Folder f = exchangeChecker.getInbox(); 
		ArrayList<EmailMessage> eList = exchangeChecker.getEmailList(f); 
	
		Set s = exchangeChecker.listSendersYear(eList, date, attributesList); 
		System.out.println(setToString(s));
	} catch (Exception e) {
		e.printStackTrace();
	}	
}




private void printSenderDay(String attributesList, Date date) {
	try {
		Folder f = exchangeChecker.getInbox(); 
		ArrayList<EmailMessage> eList = exchangeChecker.getEmailList(f); 
	
		Set s = exchangeChecker.listSendersDay(eList, date, attributesList); 
		System.out.println(setToString(s));
	} catch (Exception e) {
		e.printStackTrace();
	}	
}




public void printNumberEmailsInFolder(String folderName)  {

	try {
		System.out.println(exchangeChecker.getNumEmailsInFolder(exchangeChecker.getFolder(folderName)));
	} catch (Exception e) {
		System.err.println(e.getMessage());
	}
	
}


}