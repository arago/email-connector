package de.arago.ews_exchange.mapping;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.arago.ews_exchange.PropertiesReader;
import de.arago.ews_exchange.listener.MessageTransformer;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;

public class GenericTransformer implements MessageTransformer{
	
	
	private static final Logger log = LoggerFactory.getLogger(GenericTransformer.class); 
	private boolean mappingRead = false; 
	private Map<String,MappingObject>  mappingMap; 
	private Map<SDFType,Map<String,MappingObject>> sdfMappingMap; 
	private Map<String,String> confMap; 
	private ToolFormat toolFormat; 
	private String incomingMessageValueSepeartor; 
	private String incomingMessageSeperator = "\\r?\\n"; 
	private 	Map<SDFType, MappingObject> structureMap; 
	
	
	
	public GenericTransformer(String propFileName) {
		if(mappingRead ==false){
			Map<String,String> confMap = PropertiesReader.getConfMap(propFileName); 
			DefaultSetter.setDefaults(confMap);
			toolFormat = ToolFormat.valueOf(confMap.get("incommingMessageFormat")); 
			MappingObject.setToolFormat(toolFormat);
			
			if(confMap.containsKey("mappingPropName")){
				String mappingPropName = confMap.get("mappingPropName"); 
				MappingReader.init(mappingPropName);
			}
			else{
				MappingReader.init("mapping.properties");
			}
			
			
			mappingMap = MappingReader.getMappingTool2SDF(); 
			sdfMappingMap = MappingReader.getSDFMappingTool2SDF();
			structureMap = MappingReader.getStructureMap(); 
			
			if(confMap.containsKey("incomingMessageValueSepeartor")){
				incomingMessageValueSepeartor = confMap.get("incomingMessageValueSepeartor"); 
			}
			if(confMap.containsKey("incomingMessageSeperator")){
				incomingMessageSeperator = confMap.get(incomingMessageSeperator); 
			}
			
		
			this.confMap = confMap; 
			this.mappingRead= true; 
		}
	}

	
	public MappingObject findMappingObject(String line){
		String[] lineSplit = null;
		if(mappingMap!=null)
			lineSplit = line.split(incomingMessageValueSepeartor); 
			if(lineSplit!=null && lineSplit.length >0){
				String attName = lineSplit[0];
				attName = attName.replaceAll("\\s+",""); //Remove all whitespaces 
				if(mappingMap.containsKey(attName)){
					MappingObject mo = mappingMap.get(attName);
					return mo; 
				}
			}
			
		return null; 
	}
	
	private void addValueToAttributeValueInJson(JSONObject jo, MappingObject mo, Object value, String listName){
		if(jo.has(listName)){
			try {
				JSONObject jMand = (JSONObject) jo.get(listName);
				
				if(value.getClass().toString().equals(String.class.toString())){
					mappValue(jMand, mo, (String) value); 
					
					}
				else
					jMand.put(mo.getSdfAttributeName(), value); 
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}else{
			
			try {
				JSONObject jMand = new JSONObject(); 
				if(value.getClass().toString().equals(String.class.toString()))
					mappValue(jMand, mo, (String) value); 
				else
					jMand.put(mo.getSdfAttributeName(), value); 
				
				jo.put(listName, jMand); 
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			
		}
	}
	
	private void mappValue(JSONObject jo, MappingObject mo, String value){
		try {
			if (mo.getSdfAttributetype()!=null && mo.getSdfAttributetype().equals(SDFAttributeType.STRING)) {
				jo.put(mo.getSdfAttributeName(), value);
			}
			else if (mo.getSdfAttributetype()!=null && mo.getSdfAttributetype().equals(SDFAttributeType.DATE)) {
				
				String transformedDate = DateTransformer.toOutput(value); 
				jo.put(mo.getSdfAttributeName(), transformedDate);
				}
			else if (mo.getSdfAttributetype()!=null && mo.getSdfAttributetype().equals(SDFAttributeType.TIME)) {
				jo.put(mo.getSdfAttributeName(), value);
			}
			else if(mo.getSdfAttributetype()!=null && mo.getSdfAttributetype().toString().startsWith("ENUM-")){
			Map<SDFType, Map<String, Map<String, String>>> enumSDFMap = MappingReader.getEnumTool();
			if(enumSDFMap!=null){
				Map<String, Map<String, String>> enumNamesMap = enumSDFMap.get(mo.getSdfType()); 
				
				if(enumNamesMap!=null && enumNamesMap.containsKey(mo.getSdfAttributetype().toString())){}
					Map<String, String> enumValueMap = enumNamesMap.get(mo.getSdfAttributetype().toString()); 
					String mappedValue = enumValueMap.get(value); 
					jo.put(mo.getSdfAttributeName(),mappedValue);
					
				}
			}
				
			
			
			else {
				jo.put(mo.getSdfAttributeName(), value);
			}
		
			
			
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void addValueToAttributeListInJson(JSONObject jo, MappingObject mo, Object value, String listName){
		
		if(jo.has(listName)){
			
			try {
				JSONObject jMand = (JSONObject) jo.get(listName);
				if(jMand.has(mo.getSdfAttributeName())){
					JSONArray jA = (JSONArray) jMand.get(mo.getSdfAttributeName()); 
					jA.put(value); 
				}else{
					JSONArray jA = new JSONArray(); 
					jA.put(value); 
					jMand.put(mo.getSdfAttributeName(), jA); 
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}else{
			try {
				JSONObject jMand = new JSONObject(); 
				JSONArray jA = new JSONArray(); 
				jA.put(value); 
				jMand.put(mo.getSdfAttributeName(), jA); 
				jo.put(listName, jMand); 
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			
		}
	}
	
	private void addToSDF(JSONObject jo, MappingObject mo, String value){
		if(mo.getSdfAttibuteFormat().equals(AttributeFormat.VALUE)){
			if(!mo.getSdfAttributeClass().equals(SDFAttributeClass.DEFINITION)&&!mo.getSdfAttributeClass().equals(SDFAttributeClass.SDFDEFAULT)&&!mo.getSdfAttributeClass().equals(SDFAttributeClass.TOOLDEFAULT)){
				addValueToAttributeValueInJson(jo,mo,value,mo.getSdfAttributeClass().toString().toLowerCase()); 
			}else if(mo.getSdfAttributeClass().equals(SDFAttributeClass.DEFINITION)){
			
					mappValue(jo, mo,value); 
					
				
			}
		}
		else if (mo.getSdfAttibuteFormat().equals(AttributeFormat.LIST)){
			if(!mo.getSdfAttributeClass().equals(SDFAttributeClass.DEFINITION)&&!mo.getSdfAttributeClass().equals(SDFAttributeClass.SDFDEFAULT)&&!mo.getSdfAttributeClass().equals(SDFAttributeClass.TOOLDEFAULT)){
				addValueToAttributeListInJson(jo,mo,value,mo.getSdfAttributeClass().toString().toLowerCase()); 
			}
		}
		
	}
	
private void addSubSdfToSDF(JSONObject jo, MappingObject mo, JSONObject value){
		log.trace("adding Sub SDF:" + value + " using " + mo);
		if(mo.getSdfAttibuteFormat().equals(AttributeFormat.VALUE)){
			if(!mo.getSdfAttributeClass().equals(SDFAttributeClass.DEFINITION)&&!mo.getSdfAttributeClass().equals(SDFAttributeClass.SDFDEFAULT)&&!mo.getSdfAttributeClass().equals(SDFAttributeClass.TOOLDEFAULT)){
				addValueToAttributeValueInJson(jo,mo,value,mo.getSdfAttributeClass().toString().toLowerCase()); 
			}else if(mo.getSdfAttributeClass().equals(SDFAttributeClass.DEFINITION)){
				
				try {
					
					jo.put(mo.getSdfAttributeName(), value);
				} catch (JSONException e) {
					e.printStackTrace();
				} 
			}
		}
		else if (mo.getSdfAttibuteFormat().equals(AttributeFormat.LIST)){
			if(!mo.getSdfAttributeClass().equals(SDFAttributeClass.DEFINITION)&&!mo.getSdfAttributeClass().equals(SDFAttributeClass.SDFDEFAULT)&&!mo.getSdfAttributeClass().equals(SDFAttributeClass.TOOLDEFAULT)){
				addValueToAttributeListInJson(jo,mo,value,mo.getSdfAttributeClass().toString().toLowerCase()); 
			}
		}
		
	}
	
	
	
	private SDFType getSDFTypeFromIncommingFolder(String incommingFolder){
		String sdfTypeName = confMap.get(incommingFolder); 
		
		return SDFType.valueOf(sdfTypeName); 
	}
	
	public JSONObject transform(String incommingMessage, BodyType messageType, String incommingFolder) {
		
		SDFType sdfType = getSDFTypeFromIncommingFolder(incommingFolder); 
		
		
		if(mappingRead==true){
			switch (toolFormat){
			case PLAINTEXT: 
				JSONObject JOP = transformPlainText2Json(incommingMessage, messageType, sdfType);
				log.trace("message transformed to " + JOP); 
				return JOP ;
			case JSON: 
				JSONObject JO = transformJson2Json(incommingMessage, sdfType); 
				log.trace("message transormed to " + JO); 
				return JO ;
			}
		}
		
		return null; 
	}


	private JSONObject transformJson2Json(String incommingMessage,SDFType sdfType ) {
		JSONObject jo = new JSONObject(); 
		SetDefaultLists(jo);
		JSONObject joIn = null; 
		int numEndOfHead = 0; 
		 numEndOfHead = incommingMessage.indexOf("</head>"); 
		 if(numEndOfHead > 1)
			 incommingMessage = incommingMessage.substring(numEndOfHead, incommingMessage.length()); 
		 
		int numBeginning = incommingMessage.indexOf("{") ;
		int numEnding = incommingMessage.lastIndexOf("}")+1;
		String reducedMessage = incommingMessage.substring(numBeginning, numEnding); 
		
		reducedMessage = StringEscapeUtils.unescapeHtml4(reducedMessage);
		
		reducedMessage = reducedMessage.replaceAll("\r\n", ""); 
		
		log.trace("Reduced to core message: " + reducedMessage);
		try {
		
			 joIn = new JSONObject(reducedMessage);
			 log.trace("Reducded message as Json: " + joIn);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		
		addDefaults( jo,sdfType);
		addJsonValue2Json(jo, joIn, ""); 
			
			
			
		return jo;
	}


	private void SetDefaultLists(JSONObject jo){
		try {
			if(!jo.has(SDFObjects.mand)){
				JSONObject joi = new JSONObject(); 
					jo.put(SDFObjects.mand, joi);
			}
			if(!jo.has(SDFObjects.opt)){
				JSONObject joi = new JSONObject(); 
					jo.put(SDFObjects.opt, joi);
			}
			if(!jo.has(SDFObjects.free)){
				JSONObject joi = new JSONObject(); 
					jo.put(SDFObjects.free, joi);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	private void addJsonValue2Json(JSONObject jo, JSONObject joIn, String combinedKey ) {
		if(joIn!=null){
		Iterator inIterator = joIn.keys(); 
		
		while(inIterator.hasNext()){
			String k1 = (String) inIterator.next(); 
			
			try {
				Object o1 = joIn.get(k1);
				String o1Class = o1.getClass().getName(); 
				if(o1Class.equals(String.class.getName())){
					MappingObject mo = mappingMap.get(k1+combinedKey); 
	
					if(mo!=null){
						
						addToSDF(jo,mo,(String) o1); 
					}
				}
				else if(o1Class.equals(JSONArray.class.getName())){
					MappingObject mo = mappingMap.get(k1+combinedKey); 
					JSONArray ja = (JSONArray) o1; 
					if(mo!=null ){
					
						for(int i = 0; i < ja.length(); i++){
							String joInJaName = ja.get(i).getClass().getName(); 
							if(joInJaName.equals(String.class.getName())){
								addToSDF(jo,mo, ja.getString(i)); 
							}
							else if (joInJaName.equals(JSONObject.class.getName())&&mo.getSubSDF()==null){
							
								JSONObject newJo =(JSONObject) ja.getJSONObject(i);
								
							
								addJsonValue2Json(jo,newJo, "-"+k1+combinedKey ); 
							}else if (joInJaName.equals(JSONObject.class.getName())&&mo.getSubSDF()!=null){
								JSONObject newJo =(JSONObject) ja.getJSONObject(i);
								log.trace("found subSDF: ("+ k1 + ") " + newJo );
								mapSubSDF(jo, "-" +k1+combinedKey , ja, i);
							}
						}
						
					}else{
						for(int i = 0; i < ja.length(); i++){
							String joInJaName = ja.get(i).getClass().getName(); 
							 if (joInJaName.equals(JSONObject.class.getName())){
									mapSubSDF(jo, combinedKey, ja, i);
							}
						}
						
					}
				}
				else if (o1Class.equals(JSONObject.class.getName())){
					JSONObject newJo =(JSONObject) o1;
					addJsonValue2Json(jo,newJo, "-"+k1+combinedKey ); 
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	}


	private void mapSubSDF(JSONObject jo, String combinedKey, JSONArray ja, int i) throws JSONException {
		JSONObject newJo =(JSONObject) ja.getJSONObject(i);
		//TODO: sure this must be a subsdf? 
		if(mappingMap.containsKey(combinedKey.substring(1))){
			MappingObject smo = mappingMap.get(combinedKey.substring(1));
			JSONObject subSDFJO = new JSONObject(); 
			
			addJsonValue2Json(subSDFJO,newJo, combinedKey );
			
			
			addDefaults(subSDFJO, smo.getSubSDF()); 
			String listname = smo.getSdfAttributeClass().toString().toLowerCase();
			String sdfName = smo.getSdfAttributeName(); 
			AttributeFormat attForm = smo.getSdfAttibuteFormat(); 
			JSONObject jList ; 
			if(jo.has(listname)){
				jList= jo.getJSONObject(listname);
			}else{
				jList = new JSONObject(); 
				jo.put(listname, jList);
			}
			if(attForm.equals(AttributeFormat.LIST)){
				if(jList.has(sdfName) && jList.get(sdfName).getClass().getName().equals(JSONArray.class.getName())){
					JSONArray sdfJa = jList.getJSONArray(sdfName);
					sdfJa.put(subSDFJO);
				}else{
					JSONArray sdfJa = new JSONArray(); 
					jList.put(sdfName, sdfJa);
					sdfJa.put(subSDFJO);
				}
			}else if(attForm.equals(AttributeFormat.VALUE)){
				if(jList.has(sdfName) && ! jList.get(sdfName).getClass().getName().equals(JSONObject.class.getName())){
					jList.put(sdfName, subSDFJO);
				}
			}
			
			
			
			//TODO: add subsdf 
			
			
		}else{
			//TODO: is this ever happening??? 
		}
	}

	private void addDefaults(JSONObject jo){
		for(String defKey : mappingMap.keySet()){
			MappingObject dmo = mappingMap.get(defKey); 
			if(dmo.getSdfAttributeClass().equals(SDFAttributeClass.SDFDEFAULT)){
				String dVal = dmo.getSdfDefaultValue();
				String key = dmo.getSdfAttributeName(); 
				
				if(dVal!=null){
					       //try {
					        	mappValue(jo,dmo,dVal); 
							//	jo.put(key, dVal);
							//} catch (JSONException e) {
							//	e.printStackTrace();
							//} 
				}
			}
		}
		
	}
	
	private void addDefaults(JSONObject jo, SDFType sdftype){
		 Map<String, MappingObject> mappingMapSDF = sdfMappingMap.get(sdftype); 
		for(String defKey : mappingMapSDF.keySet()){
			MappingObject dmo = mappingMapSDF.get(defKey); 
			if(dmo.getSdfAttributeClass().equals(SDFAttributeClass.SDFDEFAULT)){
				String dVal = dmo.getSdfDefaultValue();
				String key = dmo.getSdfAttributeName(); 
				if(dVal!=null){
					        	mappValue(jo,dmo,dVal); 
				}
			}
		}
		
	}
	
	private JSONObject transformPlainText2Json(String incommingMessage, BodyType messageType, SDFType sdfType) {
		JSONObject jo = new JSONObject(); 		
		JSONObject newJO = new JSONObject(); 
		List<JSONObject> SDFList = new ArrayList<JSONObject>(); 
		List<SDFType> sdfTypeList = new ArrayList<SDFType>(); 
		if(incommingMessage!=null && messageType.equals(BodyType.Text)){
			String[] lines  = incommingMessage.split(incomingMessageSeperator); 
			for(String line : lines){
				MappingObject mo = findMappingObject(line); 
				if(mo!=null){
					if(mo.isStartOfSubSDF()){
						newJO = new JSONObject(); 
						SDFList.add(newJO); 
						sdfTypeList.add(mo.getSdfType()); 
					}
					String attributeName = mo.getToolAttributeName() + incomingMessageValueSepeartor;
					Pattern p = Pattern.compile(""+attributeName+"[ ]*(.*)");
					Matcher m = p.matcher(line); 
					m.find(); 
					String attValue = m.group(1); 
					addToSDF(newJO,mo,attValue); 
				}
			}
			
			for(int i = 0; i < SDFList.size(); i++){
				 addDefaults(SDFList.get(i), sdfTypeList.get(i));
			}
			 jo = combineSDFs(SDFList, sdfTypeList, sdfType);
			return jo; 
		}
		
		return null; 
	}


	private JSONObject findParentSDF(List<JSONObject> sDFList, List<SDFType> sdfTypeList, SDFType sdfType){
		JSONObject jjo = new JSONObject(); 
		for(int i = 0; i < sdfTypeList.size() ; i ++ ){
			if(sdfType.equals(sdfTypeList.get(i))){
				return jjo = sDFList.get(i); 
			}
		}
		return jjo; 
		
	}
	private JSONObject combineSDFs(List<JSONObject> sDFList, List<SDFType> sdfTypeList, SDFType sdfType) {
		JSONObject jjo = findParentSDF(sDFList, sdfTypeList, sdfType); 
		
		for(int i = 0; i < sdfTypeList.size() ; i ++ ){
			SDFType sdType = sdfTypeList.get(i); 
			if(!sdfType.equals(sdType)){
				MappingObject mo = structureMap.get(sdType); 
				addSubSdfToSDF(jjo, mo, sDFList.get(i)); 
			}
		}
		
		return jjo;
	}

}
