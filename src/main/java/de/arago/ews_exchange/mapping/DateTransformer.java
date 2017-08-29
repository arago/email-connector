package de.arago.ews_exchange.mapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTransformer {
	private static String inputFormat; 
	private static String inTimeZone="UTC"; 
	private static String outTimeZone="UTC"; 
	private static String outputFormat; 
	private static final Logger log = LoggerFactory.getLogger(DateTransformer.class); 
	
	public static void init(String inFormat, String outFormat, String inTZ, String outTZ){
		inputFormat = inFormat; 
		outputFormat = outFormat; 
		if(inTZ!=null)
			inTimeZone = inTZ; 
		if(outTZ!=null)
			outTimeZone = outTZ; 
	}

	public static String toOutput(String inDate){
		if(inputFormat == null || outputFormat == null)
			log.debug("initiate date Transformer before usage");
		
			
			SimpleDateFormat sdfIn = new SimpleDateFormat(inputFormat);
			sdfIn.setTimeZone(TimeZone.getTimeZone(inTimeZone));
		    Date inptdate = null;

		    
		    try {
		        inptdate = sdfIn.parse(inDate);
		    } catch (ParseException e) {
		    	e.printStackTrace();
		    	log.error(inDate);
		    	log.error(inputFormat);
		    	log.error(inTimeZone);
		    }
		    
		    SimpleDateFormat sdfOut = new SimpleDateFormat(outputFormat);
		    sdfOut.setTimeZone(TimeZone.getTimeZone(outTimeZone));
		    if(inptdate!=null){
		    	String out = sdfOut.format(inptdate);
				return out; 
		    	
		    }
		    return null; 
	}
	

	public static String toInput(String outDate){
		if(inputFormat == null || outputFormat == null)
			log.debug("initiate date Transformer before usage");
		

		SimpleDateFormat sdfOut = new SimpleDateFormat(outputFormat);
		sdfOut.setTimeZone(TimeZone.getTimeZone(outTimeZone));
	    Date outDateDate = null;
	    try {
	    	outDateDate = sdfOut.parse(outDate);
	    } catch (ParseException e) {e.printStackTrace();
	    log.error(outDate);
    	log.error(outputFormat);
    	log.error(outTimeZone);}
	    
	    SimpleDateFormat sdfIn = new SimpleDateFormat(inputFormat);
	    sdfIn.setTimeZone(TimeZone.getTimeZone(inTimeZone));
	
	    if(outDateDate!=null){
	   String out = sdfIn.format(outDateDate);
		return out; 
		}
	    return null; 
	}
	
}
