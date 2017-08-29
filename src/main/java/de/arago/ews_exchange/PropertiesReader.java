package de.arago.ews_exchange;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesReader {
	
	
	private static final Logger log = LoggerFactory.getLogger(PropertiesReader.class); 
	
	public static Map<String,String> getConfMap(String propFileName){
		Properties prop = new Properties(); 
     	InputStream in = null; 
     	
		
		Map<String,String> confMap = new HashMap<String,String> (); 
		
    	try {
    		if(propFileName==null){
    			in = new FileInputStream("exlistener.properties");
			}
    		else{
    			in = new FileInputStream(propFileName);
    		}
			prop.load(in);
			for(Object keyOb : prop.keySet()){
				confMap.put(keyOb.toString(), prop.get(keyOb).toString()) ; 
			}
    	
    	} catch (FileNotFoundException e) {
			System.err.println("no properties file found");
			
		} catch (IOException e) {
			System.err.println("no correct input");
		} 
    	log.trace("Found "+ confMap.size()+" entries in log file "+ propFileName );
    	return confMap; 
	}
}
