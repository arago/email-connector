package de.arago.ews_exchange.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.arago.ews_exchange.PropertiesReader;

public class  SubSDFType  extends DynamicEnum{
	private static Map<String,SubSDFType> valueList; 

	public SubSDFType(String val){
		this.val = val; 	
	}
	
	public static void init(String values){
		valueList = new HashMap<String,SubSDFType>(); 
		String[] vals = values.split(","); 
		for (int i = 0; i < vals.length ; i++){
			String val = vals[i]; 
			val = val.replaceAll(" ", ""); 
			SubSDFType sT= new SubSDFType(val); 
			valueList.put(val,sT); 
		}
		
	}
	
	public static SubSDFType valueOf(String name){
		return valueList.get(name);
	}
	
	public static List<SubSDFType> values(){
	
		
		List<SubSDFType> vals = new ArrayList<SubSDFType>(); 
		for(String key : valueList.keySet()){
				vals.add(valueList.get(key)); 
		}
		return vals; 
	}

}

