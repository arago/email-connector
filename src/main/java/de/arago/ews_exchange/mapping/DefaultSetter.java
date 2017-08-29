package de.arago.ews_exchange.mapping;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSetter {
	private static final Logger log = LoggerFactory.getLogger(DefaultSetter.class); 
	public static void setDefaults(Map<String,String> confMap){
		if(confMap.containsKey("SDFTypes")){
			SDFType.init(confMap.get("SDFTypes"));
			log.trace("Setting SDFTypes");
		}
		if(confMap.containsKey("SubSDFTypes")){
			SubSDFType.init(confMap.get("SubSDFTypes"));
			log.trace("Setting SDFSubTypes");
		}
		if(confMap.containsKey("SDFAttributeType")){
			SDFAttributeType.init(confMap.get("SDFAttributeType"));
			log.trace("Setting SDFAttributeTypes");
		}
	}
}
