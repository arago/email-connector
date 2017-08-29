package de.arago.ews_exchange.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.BasicConfigurator;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.arago.ews_exchange.PropertiesReader;

public class RestletServer implements Runnable{
	private static final Logger log = LoggerFactory.getLogger(RestletServer.class); 
	private String path; 
	public RestletServer(String path){
		this.path = path; 
	}
	
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		ServiceFactory sF = new ServiceFactory(); 
		String path = null; 
    	if(args != null && args.length>0){
    		path = args[0]; 
    		log.trace("pro File: " + path);
    		RestletResource.setPropFileName(path);
    		log.debug("using properties from " + path);
    	}
		Map<String,String> confMap = PropertiesReader.getConfMap(path); 
		sF.init(confMap);
		int portNum = 5000; 
		Protocol proto = Protocol.HTTP; 
		
		
		
		
		try{
			portNum = Integer.parseInt(confMap.get("connectRestPort")) ; 
			proto = Protocol.valueOf(confMap.get("connectRestProtocol")); 
		}
		catch(Exception e){
			
		}
		log.trace("create server with: " + proto + ", " + portNum );

		
		Server server = new Server(proto, portNum, RestletResource.class);
		
	    server.start();
	}

	@Override
	public void run() {
		ServiceFactory sF = new ServiceFactory(); 
    	log.trace("pro File: " + path);
    	RestletResource.setPropFileName(path);
    	log.debug("using properties from " + path);
		Map<String,String> confMap = PropertiesReader.getConfMap(path); 
		sF.init(confMap);
		int portNum = 5000; 
		Protocol proto = Protocol.HTTP; 
		try{
			portNum = Integer.parseInt(confMap.get("connectRestPort")) ; 
			proto = Protocol.valueOf(confMap.get("connectRestProtocol")); 
		}
		catch(Exception e){
			
		}
		log.trace("create server with: " + proto + ", " + portNum );

		
		Server server = new Server(proto, portNum, RestletResource.class);
		
	    try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
