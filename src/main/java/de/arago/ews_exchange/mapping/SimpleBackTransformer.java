package de.arago.ews_exchange.mapping;

import org.json.JSONObject;

public  class SimpleBackTransformer implements MessageBackTransformer {
	
	public String transform(JSONObject updateMessage) {
		if(updateMessage!=null)
			return updateMessage.toString();
		return null; 
	}

}
