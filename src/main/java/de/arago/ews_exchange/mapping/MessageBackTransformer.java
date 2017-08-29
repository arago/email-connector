package de.arago.ews_exchange.mapping;

import org.json.JSONObject;

public interface MessageBackTransformer {
	public String transform(JSONObject updateMessage); 
}
