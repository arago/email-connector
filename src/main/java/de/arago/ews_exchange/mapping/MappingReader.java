package de.arago.ews_exchange.mapping;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingReader {
	private static final Logger log = LoggerFactory.getLogger(MappingReader.class); 
	private static int  maxOrderNum = -1; 
	
	private static String propName; 
	private static Map<SDFType, MappingObject> structureMap = null; 
	private static Map<String,MappingObject> map2toolMap = null; 
	private static Map<SDFType,String> subSDFKeyMap = new HashMap<SDFType, String>(); 
	private static Map<Integer,MappingObject> mapOrderMap  = null; 
	private static Map<SDFType,Map<String,MappingObject>> mapSdfSdf2ToolMap = null; 
	private static Map<SDFType,Map<String,MappingObject>> mapSdfTool2SdfMap = null; 
	private static Map<SDFType,Map<String,Map<String,String>>> enumSDFMap = null; 
	private static Map<SDFType,Map<String,Map<String,String>>> enumToolMap = null; 
	
	public MappingReader(String propName) {
		this.propName = propName; 
		//SubSDFType.init(propName);
	
	}
	public static void init(String pName){
		propName = pName; 
	}
	
	public static int getMaxOrderNum() {
		return maxOrderNum;
	}

	public static void setMaxOrderNum(int maxOrderNum) {
		MappingReader.maxOrderNum = maxOrderNum;
	}

	
	public static  Map<SDFType,Map<String,Map<String,String>>> getEnumTool(){
		if(enumToolMap ==null)
		{
			enumToolMap= new HashMap<SDFType,Map<String,Map<String,String>>>(); 
				String file=propName; 
					try(BufferedReader br = new BufferedReader(new FileReader(file ))) {
					    for(String line; (line = br.readLine()) != null; ) {
					    	if(line.startsWith("ENUM-")){
					    	int keyNum = line.indexOf(","); 
						    	String key = line.substring(0, keyNum);
						    	String val = line.substring(keyNum+1, line.length()); 
						    	String[] splittedMapping = val.split(","); 
						    	SDFType sdfType = SDFType.valueOf(splittedMapping[0]);
						    	Map<String,Map<String,String>> enumMap;
						    	Map<String,String> enumValueMap ; 
						    	if(enumToolMap.containsKey(sdfType)){
						    		enumMap = enumToolMap.get(sdfType);
						    	}
						    	else{
						    		enumMap = new HashMap<String,Map<String,String>>(); 
						    		enumToolMap.put(sdfType, enumMap); 
						    	}
						    	if(enumMap.containsKey(key)){
						    		enumValueMap = enumMap.get(key); 
						    	}
						    	else{
						    		enumValueMap = new HashMap<String,String>(); 
						    		enumMap.put(key, enumValueMap); 
						    	}
						    	for(int i = 1; i < splittedMapping.length; i++){
						    		String[] keyValue = splittedMapping[i].split(";"); 
						    		if(keyValue.length==2){
						    			if(!enumValueMap.containsKey(keyValue[0])){
						    				enumValueMap.put( keyValue[1], keyValue[0]); 
						    			}
						    		}
						    	}
						    		
						 
								
							}
						}
			    	} catch (FileNotFoundException e) {
						System.err.println("no properties mapping file found");
					} catch (IOException e) {
						System.err.println("no correct input");
					} 
				}	
			
			
		return enumToolMap; 
	}
	
	public static  Map<SDFType,Map<String,Map<String,String>>> getEnumSDFMap(){
		if(enumSDFMap ==null)
		{
			log.trace("no Enum Map - Read it from prop : " + propName);
			enumSDFMap= new HashMap<SDFType,Map<String,Map<String,String>>>(); 
				String file=propName; 
					try(BufferedReader br = new BufferedReader(new FileReader(file ))) {
						log.trace("reading from file: " + file);
					    for(String line; (line = br.readLine()) != null; ) {
					    	if(line.startsWith("ENUM-")){
					    	int keyNum = line.indexOf(","); 
						    	String key = line.substring(0, keyNum);
						    	String val = line.substring(keyNum+1, line.length()); 
						    	log.trace("found" + key + " " + val );
						    	String[] splittedMapping = val.split(","); 
						    	log.trace(splittedMapping[0]); 
						    	SDFType sdfType = SDFType.valueOf(splittedMapping[0]);
						    	Map<String,Map<String,String>> enumMap;
						    	Map<String,String> enumValueMap ; 
						    	if(enumSDFMap.containsKey(sdfType)){
						    		enumMap = enumSDFMap.get(sdfType);
						    	}
						    	else{
						    		enumMap = new HashMap<String,Map<String,String>>(); 
						    		enumSDFMap.put(sdfType, enumMap); 
						    	}
						    	if(enumMap.containsKey(key)){
						    		enumValueMap = enumMap.get(key); 
						    	}
						    	else{
						    		enumValueMap = new HashMap<String,String>(); 
						    		enumMap.put(key, enumValueMap); 
						    	}
						    	for(int i = 1; i < splittedMapping.length; i++){
						    		String[] keyValue = splittedMapping[i].split(";"); 
						    		if(keyValue.length==2){
						    			if(!enumValueMap.containsKey(keyValue[0])){
						    				log.trace("adding " + key + " "+ sdfType + " " + keyValue[0] + ":" +keyValue[1]);
						    				enumValueMap.put(keyValue[0], keyValue[1]); 
						    			}
						    		}
						    	}
						    		
						 
								
							}
						}
			    	} catch (FileNotFoundException e) {
						System.err.println("no properties mapping file found");
					} catch (IOException e) {
						System.err.println("no correct input");
					} 
				}	
			
			
		return enumSDFMap; 
	}
	
	public static Map<SDFType, MappingObject> getStructureMap(){
		if(structureMap==null){
			Map<String,MappingObject> mapMap= getMappingTool2SDF(); 
			 structureMap= new HashMap<SDFType, MappingObject> (); 
				String file=propName; 
					try(BufferedReader br = new BufferedReader(new FileReader(file ))) {
					    for(String line; (line = br.readLine()) != null; ) {
					    	if(!line.startsWith("#") && ! line.startsWith("ENUM-")){
					    	int keyNum = line.indexOf(","); 
						    	String key = line.substring(0, keyNum);
						    	String val = line.substring(keyNum+1, line.length()); 
								MappingObject mo = new MappingObject(key , 	val);
								if(mo.isSDFStructureInformation()){
									structureMap.put(mo.getSubSDF(), mo); 
								} 
							}
						}
			    	} catch (FileNotFoundException e) {
						System.err.println("no properties mapping file found");
					} catch (IOException e) {
						System.err.println("no correct input");
					} 
				}	 
		return structureMap; 
	}
	
	public static Map<String,MappingObject> getMappingTool2SDF(){
		String file=propName; 
		if(map2toolMap==null){
			map2toolMap = new HashMap<String,MappingObject> ();
			try(BufferedReader br = new BufferedReader(new FileReader(file ))) {
			    for(String line; (line = br.readLine()) != null; ) {
			    	if(!line.startsWith("#") && ! line.startsWith("ENUM-")){
			    	int keyNum = line.indexOf(","); 
				    	String key = line.substring(0, keyNum);
				    	String val = line.substring(keyNum+1, line.length()); 
						MappingObject mO = new MappingObject(key , 	val);
						if(containsSubSDFType(mO.getSdfType())&&mO.getCombinedKey()!=null){
							String newCombinedKey ; 
							if(mO.getToolOrder()>0)
								newCombinedKey = mO.getCombinedKey(); 
								else	
							 newCombinedKey = mO.getCombinedKey()+"-"+subSDFKeyMap.get(mO.getSdfType()); 
							
							mO.setCombinedKey(newCombinedKey);
					}
					if(mO.getSubSDF()!=null){
						
						subSDFKeyMap.put(mO.getSubSDF(), mO.getCombinedKey());
						log.trace("" + subSDFKeyMap);
					}
					map2toolMap.put(mO.getCombinedKey(), mO);
			    }
				}
	    	} catch (FileNotFoundException e) {
	    		log.error("no properties mapping file found (" + propName+")");
			} catch (IOException e) {
				log.error("invalid input for mapping file found (" + propName+")");
				
			} 
		}
		return map2toolMap; 

	}
	

	
public static Map<Integer,MappingObject> getOrderMap(){
		String file=propName; 
		if(mapOrderMap==null){
			 mapOrderMap = new HashMap<Integer,MappingObject> ();
			try(BufferedReader br = new BufferedReader(new FileReader(file ))) {
			    for(String line; (line = br.readLine()) != null; ) {
			    	if(!line.startsWith("#")&& !line.startsWith("ENUM-")){
		    	int keyNum = line.indexOf(","); 
			    	String key = line.substring(0, keyNum);
			    	String val = line.substring(keyNum+1, line.length()); 
					MappingObject mO = new MappingObject(key , 	val); 
					mapOrderMap.put(mO.getToolOrder(), mO);
					if(mO.getToolOrder()> maxOrderNum){
						maxOrderNum = mO.getToolOrder(); 
					}
			    }
				}
	    	} catch  (FileNotFoundException e) {
	    		log.error("no properties mapping file found (" + propName+")");
			} catch (IOException e) {
				log.error("invalid input for mapping file found (" + propName+")");
				
			} 
		}
		return mapOrderMap; 
	}

	public static Map<SDFType,Map<String, MappingObject>> getMappingSDF2Tool() {
		if(mapSdfSdf2ToolMap==null){
		mapSdfSdf2ToolMap = new HashMap<SDFType,Map<String,MappingObject>> (); 
		try {
			String file = propName; 
			try(BufferedReader br = new BufferedReader(new FileReader(file ))) {
			    for(String line; (line = br.readLine()) != null; ) {
			    	if(!line.startsWith("#") && ! line.startsWith("ENUM-")){
			    		
		    	int keyNum = line.indexOf(","); 
			    	String key = line.substring(0, keyNum);
			    	String val = line.substring(keyNum+1, line.length()); 
			    
		
				MappingObject mO = new MappingObject(key , val); 
				SDFType sdfType = mO.getSdfType(); 
				Map<String,MappingObject> mmMap; 
				if(mapSdfSdf2ToolMap.containsKey(sdfType)){
					mmMap = mapSdfSdf2ToolMap.get(sdfType); 
					
				}else{
					mmMap = new HashMap<String,MappingObject>(); 
					mapSdfSdf2ToolMap.put(sdfType, mmMap);
				}
				
				mmMap.put(key, mO);
			}
			    }
		}
    	} catch (FileNotFoundException e) {
    		log.error("no properties mapping file found (" + propName+")");
		} catch (IOException e) {
			log.error("invalid input for mapping file found (" + propName+")");
			
		} 
		}
		return mapSdfSdf2ToolMap; 
	}

	public static Map<SDFType, Map<String, MappingObject>> getSDFMappingTool2SDF() {
		if(mapSdfTool2SdfMap ==null){
			 mapSdfTool2SdfMap = new HashMap<SDFType,Map<String,MappingObject>> (); 
		try {
			String file = propName; 
			try(BufferedReader br = new BufferedReader(new FileReader(file ))) {
			    for(String line; (line = br.readLine()) != null; ) {
			    	if(!line.startsWith("#") && ! line.startsWith("ENUM-")){
			    		
		    	int keyNum = line.indexOf(","); 
			    	String key = line.substring(0, keyNum);
			    	String val = line.substring(keyNum+1, line.length()); 
				MappingObject mO = new MappingObject(key , val); 
				SDFType sdfType = mO.getSdfType(); 
				Map<String,MappingObject> mmMap; 
				if(mapSdfTool2SdfMap.containsKey(sdfType)){
					mmMap = mapSdfTool2SdfMap.get(sdfType); 
				}else{
					mmMap = new HashMap<String,MappingObject>(); 
					mapSdfTool2SdfMap.put(sdfType, mmMap);
				}
				if(mO.getCombinedKey()!=null){
					mmMap.put(mO.getCombinedKey(), mO);
					}
				else{
					mmMap.put("sdf-"+mO.getSdfAttributeName(), mO); 
				}
				
				if(containsSubSDFType(mO.getSdfType())&&mO.getCombinedKey()!=null){
					String newCombinedKey = mO.getCombinedKey()+"-"+subSDFKeyMap.get(mO.getSdfType()); 
					mO.setCombinedKey(newCombinedKey);
				}
				if(mO.getSubSDF()!=null){
					subSDFKeyMap.put(mO.getSubSDF(), mO.getCombinedKey());
				}
			    }
			}
			    
		}
    	
    	} catch (FileNotFoundException e) {
    		log.error("no properties mapping file found (" + propName+")");
		} catch (IOException e) {
			log.error("invalid input for mapping file found (" + propName+")");
			
		} 
		}

		return mapSdfTool2SdfMap; 
	}
	
	public static boolean containsSubSDFType(SDFType test) {
	    for (SubSDFType c : SubSDFType.values()) {
	        if (c.toString().equals(test.toString())) {
	            return true;
	        }
	    }

	    return false;
	}
}
