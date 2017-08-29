package de.arago.ews_exchange.listener;

import org.json.JSONObject;

import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;

public interface MessageTransformer {
public JSONObject transform(String incommingMessage, BodyType messageType, String incommingFolder); 
}
