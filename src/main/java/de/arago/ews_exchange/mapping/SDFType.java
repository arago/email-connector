package de.arago.ews_exchange.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public enum SDFType {
//	INCIDENT, CHANGE, COMMENT, EVENT, ATTACHMENT, SUBTASK
//}

public class SDFType extends DynamicEnum{
	private static Map<String,SDFType> valueList; 

	public SDFType(String val){
		this.val = val; 	
	}
	
	public static void init(String values){
		valueList = new HashMap<String,SDFType>(); 
		String[] vals = values.split(","); 
		for (int i = 0; i < vals.length ; i++){
			String val = vals[i]; 
			val = val.replaceAll(" ", ""); 
			SDFType sT= new SDFType(val); 
			valueList.put(val,sT); 
		}
		
	}
	
	public static SDFType valueOf(String name){
		return valueList.get(name);
	}
	
	
}
