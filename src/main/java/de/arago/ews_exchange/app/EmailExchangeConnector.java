package de.arago.ews_exchange.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.restlet.ext.slf4j.Slf4jLoggerFacade;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.arago.ews_exchange.PropertiesReader;
import de.arago.ews_exchange.listener.ExchangeListenApp;
import de.arago.ews_exchange.mapping.DateTransformer;
import de.arago.ews_exchange.mapping.SDFObjects;
import de.arago.ews_exchange.server.RestletServer;
import de.arago.ews_exchange.server.ServiceFactory;

public class EmailExchangeConnector {
	private static final Logger log = LoggerFactory.getLogger(EmailExchangeConnector.class); 
	
	
	  public static void main( String[] args ){
	
			String path = null; 
	    	if(args != null && args.length>0){
	    		path = args[0]; 
	    		System.err.println("using properties from " + path);
				System.setProperty("org.restlet.engine.loggerFacadeClass","org.restlet.ext.slf4j.Slf4jLoggerFacade" );
	    
	
	    	Map<String,String> confMap = PropertiesReader.getConfMap(path); 
	    	 String log4JPropertyFile = "log4j.properties";
	    	if(confMap.containsKey("log4jpropFile")){
	    		log4JPropertyFile = confMap.get("log4jpropFile"); 
				String notificationMailAddress = confMap.get("notificationEmailAddress"); 
				log.trace("return messages will go to : " +notificationMailAddress);
	    	}
		 
		  Properties p = new Properties();

		  try {
		      p.load(new FileInputStream(log4JPropertyFile));
		      PropertyConfigurator.configure(p);
		  } catch (IOException e) {
			  System.err.println("no file found "+ e.getMessage());
		  }
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
		   
		  
		    	ServiceFactory sf = new ServiceFactory();
		    	sf.init(confMap);
		    	RestletServer rs = new RestletServer(path); 
		    	ExchangeListenApp exlA = new ExchangeListenApp(path);
		    	

		    	if(confMap.containsKey("BackSycnerEmailState")&& confMap.get("BackSycnerEmailState").equals("active")){
			    	log.info("Start RestletServer");
			    	rs.run(); 
			    	log.info("RestletServer Started");
		    	}
		    	if(confMap.containsKey("EmailListenerState")&& confMap.get("EmailListenerState").equals("active")){
		    	log.info("Start Exchange Email Listener");
		    	exlA.run();
		    	log.info("Exchange Email Listener Started");
		    	}
	  }
	  
	  else{
		  System.err.println("No properties file found");
	  }
	  }
}
