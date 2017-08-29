package de.arago.ews_exchange.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDFAttributeType extends DynamicEnum{
	//STRING, DATE, TIME, SDF, COMMENT
	
	private static Map<String,SDFAttributeType> valueList  ; 

	public static  SDFAttributeType STRING = new SDFAttributeType("STRING"); 
	public static  SDFAttributeType DATE = new SDFAttributeType("DATE"); 
	public static  SDFAttributeType TIME = new SDFAttributeType("TIME"); 
	
	public SDFAttributeType(String val){
		this.val = val; 	
	}
	
	public static void init(String values){
		valueList= new HashMap<String,SDFAttributeType>(); ; 
		String[] vals = values.split(","); 
		for (int i = 0; i < vals.length ; i++){
			String val = vals[i]; 
			val = val.replaceAll(" ", ""); 
			SDFAttributeType sT= new SDFAttributeType(val); 
			valueList.put(val,sT); 
			
		}
		
	}
	
	public static SDFAttributeType valueOf(String name){
		
		
		return valueList.get(name);
	}
	
	public static List<SDFAttributeType> values(){
	
		
		List<SDFAttributeType> vals = new ArrayList<SDFAttributeType>(); 
		for(String key : valueList.keySet()){
				vals.add(valueList.get(key)); 
		}
		return vals; 
	}


}
