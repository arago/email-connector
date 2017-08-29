package de.arago.ews_exchange.listener;
import okhttp3.OkHttpClient.Builder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.BasicConfigurator;

import de.arago.ews_exchange.PropertiesReader;
import de.arago.ews_exchange.mapping.DateTransformer;
import de.arago.ews_exchange.mapping.GenericTransformer;
import de.arago.ews_exchange.mapping.SDFObjects;
import de.arago.ews_exchange.server.ServiceFactory;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;


public class ExchangeListenApp  implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(ExchangeListenApp.class);  
		private String path; 
	
		public ExchangeListenApp(String path){
			this.path = path; 
	    	}
			
		
	    public static void main( String[] args ) throws Exception 
	    {	
	    	String path = null; 
	    	if(args != null && args.length>0){
	    		path = args[0]; 
	    		log.debug("using properties from " + path);
	    	}
	    	Map<String,String> confMap = PropertiesReader.getConfMap(path); 
	    	
	    	
	    	String toolDateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX"; 
			   String toolDateTimeZone = null; 
			   String hiroDateTimeZone = null; 
			   if(confMap.containsKey("toolDateFormat")&& confMap.containsKey("toolDateTimeZone") &&  confMap.containsKey("hiroDateTimeZone")){
					
					toolDateFormat = confMap.get("toolDateFormat"); 
				}
				if( confMap.containsKey("toolDateTimeZone")){
		
					toolDateTimeZone = confMap.get("toolDateTimeZone");
				}
				if( confMap.containsKey("hiroDateTimeZone")){
		
					hiroDateTimeZone = confMap.get("hiroDateTimeZone");
				}
					DateTransformer.init(toolDateFormat,SDFObjects.dateFromat,toolDateTimeZone, hiroDateTimeZone );
	    	
	    	
	    	
	    	ClientCreateor cc = new ClientCreateor(confMap);
	    	String email = confMap.get("email");
			String pw = confMap.get("password");
			String uri = confMap.get("uri");
			String exV = confMap.get("exchangeVersion"); 
			 ServiceFactory sF = new ServiceFactory(); 
			 sF.init(confMap);
			
	    		OkHttpClient client = cc.getClient(); 
	    		
	    		
	    		MessageSender  restSender = new  RestMessageSender(client, confMap); 
	    		MessageTransformer transformer = new GenericTransformer(path); 
	    		
	    		ExchangeListener eL = new ExchangeListener(email, pw, uri, exV, confMap,restSender, transformer ); 
	    		if(eL!=null)
	    			eL.start(); 
	    	}


		@Override
		public void run() {
			Map<String,String> confMap = PropertiesReader.getConfMap(path); 
	    	ClientCreateor cc = new ClientCreateor(confMap);
	    	String email = confMap.get("email");
			String pw = confMap.get("password");
			String uri = confMap.get("uri");
			String exV = confMap.get("exchangeVersion"); 
	    	
			
	    		OkHttpClient client = cc.getClient(); 
	    		
	    		
	    		MessageSender  restSender = new  RestMessageSender(client, confMap); 
	    		MessageTransformer transformer = new GenericTransformer(path); 
	    		
	    		ExchangeListener eL = new ExchangeListener(email, pw, uri, exV, confMap,restSender, transformer ); 
	    		if(eL!=null)
	    			eL.start(); 
			
		}

		
	    }
	


