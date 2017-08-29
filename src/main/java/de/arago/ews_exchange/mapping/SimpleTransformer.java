package de.arago.ews_exchange.mapping;

import org.json.JSONException;
import org.json.JSONObject;

import de.arago.ews_exchange.listener.MessageTransformer;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;

public class SimpleTransformer implements MessageTransformer {

	public JSONObject transform(String incommingMessage, BodyType messageType, String incommingFolder) {
		
		int numBeginning = incommingMessage.indexOf("{") ;
		int numEnding = incommingMessage.lastIndexOf("}")+1; 
		String reducedMessage = incommingMessage.substring(numBeginning, numEnding); 
	
		JSONObject jO = null; 
		try {
			if(reducedMessage!=null && reducedMessage !="" )
				jO= new JSONObject(reducedMessage);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return jO;
	}

}
