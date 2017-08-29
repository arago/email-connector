package de.arago.ews_exchange;

import java.net.URISyntaxException;
import java.util.Map;

public class AppExchange 
{
    public static void main( String[] args ) throws Exception 
    {	
    	//TODO check prop file name
    	Map<String,String> conMap = PropertiesReader.getConfMap("exlistenerJSON.properties"); 
    	String email = conMap.get("email");
		String pw = conMap.get("password");
		String uri = conMap.get("uri");
		String exV = conMap.get("exchangeVersion"); 
    	
		
		
    	if(pw!=null && email!=null && uri!=null){
    		ExchangeChecker eListener = null;
    		ExchangePrinter ePrint = null; 
		try {
			 
			eListener = new ExchangeChecker(email, pw, uri, exV);
			ePrint = new ExchangePrinter(eListener); 
			
		} catch (URISyntaxException e) {
			System.err.println("invalid uri");
		} 
		if(eListener!=null){
		
			ePrint.print(args);
		}
		
    	}

	
    }
}
