package de.arago.ews_exchange.mapping;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.arago.ews_exchange.PropertiesReader;

public class GenericBackTransformer implements MessageBackTransformer{
	
	private static final Logger log = LoggerFactory.getLogger(GenericBackTransformer.class); 
	private boolean mappingRead = false; 
	private Map<SDFType,Map<String,MappingObject>>  mappingMap; //TODO: 1 Mapping Map per SDF Type
	private Map<SDFType,Map<String, List<String>>> generalMessageMap; 
	private Map<String,String> confMap; 
	private ToolFormat toolFormat; 
	private String incomingMessageValueSepeartor; 
	private String incomingMessageSeperator = "\\r?\\n"; 
	private String outgoingMessageLineSeperator = "<br/>"; 
	private Map<Integer,MappingObject> orderMap; 
	private Map<SDFType,List<JSONObject>> subSDFlist = new HashMap<SDFType,List<JSONObject>>();  ; 
	
	private String mapValue(MappingObject mo, String value){
		String mappedValue = value; 
		if(mo.getSdfAttributetype().toString().startsWith("ENUM-")){

			Map<SDFType, Map<String, Map<String, String>>> enumSDFMap = MappingReader.getEnumSDFMap(); 
			if(enumSDFMap!=null){
				Map<String, Map<String, String>> enumNamesMap = enumSDFMap.get(mo.getSdfType()); 
				
				if(enumNamesMap!=null && enumNamesMap.containsKey(mo.getSdfAttributetype().toString())){}
					Map<String, String> enumValueMap = enumNamesMap.get(mo.getSdfAttributetype().toString()); 
					 mappedValue = enumValueMap.get(value); 
					
					
					
				}
		}
		return mappedValue; 
	}
	
	public GenericBackTransformer(String propFileName) {
		if(mappingRead ==false){
			String toolDateTimeZone = null; ;
			String hiroDateTimeZone = null ;
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
			
		
			
			mappingMap = MappingReader.getMappingSDF2Tool(); 
		
			if(confMap.containsKey("incomingMessageValueSepeartor")){
				incomingMessageValueSepeartor = confMap.get("incomingMessageValueSepeartor"); 
			}
			 if(confMap.containsKey("incomingMessageSeperator")){
				incomingMessageSeperator = confMap.get("incomingMessageSeperator"); 
			}
			 if(confMap.containsKey("outgoingMessageLineSeperator")){
				log.trace("line seperateor outgoing is : " +confMap.get("outgoingMessageLineSeperator"));
				outgoingMessageLineSeperator = confMap.get("outgoingMessageLineSeperator"); 
			}
			 
			
			 
			this.mappingRead= true; 
		}
	}
	
	
	private void transform2Text(String value, String sdfName, Map<String,List<String>> messageMap, SDFType sdfType){
		Map <String, MappingObject> sdfMappingMap = mappingMap.get(sdfType); 
		MappingObject mo = sdfMappingMap.get(sdfName);
		if(mo!=null){
			String key = mo.getSdfAttributeName();
			SDFAttributeClass sac= mo.getSdfAttributeClass(); 
			if(!sac.equals(SDFAttributeClass.SDFDEFAULT) && !sac.equals(SDFAttributeClass.TOOLDEFAULT)){
				if(messageMap.containsKey(key)){
					List<String> l = messageMap.get(key); 
					if(mo.getSdfAttributetype().toString().startsWith("ENUM-")){
						l.add(mapValue(mo,value)); 

					}
					else{
						
						l.add(mapValue(mo,value));
					}
				}
				else{
					List<String> l = new ArrayList(); 
					l.add(mapValue(mo,value));
					messageMap.put(key, l);	
				}
			}
		}
	}
	
	private void transform2MessageMap(JSONObject updateMessage, 	Map<String,List<String>> messageMap, SDFType sdfType ){
	
		Iterator keyIterator = updateMessage.keys(); 
		
		while ( keyIterator.hasNext()){
			String key = (String) keyIterator.next(); 
			try {
				 Object o = updateMessage.get(key);
				 String oClass = o.getClass().getName(); 
				 
				 if(oClass.equals(String.class.getName())){
				
					 transform2Text((String) o,key, messageMap, sdfType); 
				 }
				 else if(oClass.equals(JSONArray.class.getName())){
					 
					 JSONArray jA = (JSONArray) o; 
					 for(int i = 0; i < jA.length(); i++){
						Object innerOb = jA.get(i);
						 if(innerOb.getClass().getName().equals(String.class.getName())){
							 transform2Text((String) innerOb,key, messageMap, sdfType); 
						 }
						 else if(innerOb.getClass().getName().equals(JSONObject.class.getName())){
							 log.trace("found sub sdf: " + innerOb);
							 mappInnerJSON(innerOb, messageMap);
						 }
						 
					 }
				 }
				 else if(key.equals(SDFObjects.opt) || key.equals(SDFObjects.free) || key.equals(SDFObjects.mand) ){
					 transform2MessageMap( (JSONObject) updateMessage.get(key), messageMap, sdfType); 
				 }
				 else if(oClass.equals(JSONObject.class.getName())){
					 mappInnerJSON(o);
				 }
				 
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
		
		
	}


	private void mappInnerJSON(Object o) throws JSONException {
		JSONObject innerJo = (JSONObject) o; 
		String innerSDF = null; 
		 if(innerJo.has(SDFObjects.SDFType)){
			 SDFType innerType = null; 
			innerType  = SDFType.valueOf(innerJo.getString(SDFObjects.SDFType)); 
			Map<String,List<String>> messageMap = new HashMap<String, List<String>>(); 
			transform2MessageMap(innerJo, messageMap, innerType); 
			JSONObject rs = transform2Json(messageMap, innerType);
			 List<JSONObject> subSDFs = null; 
			 if(subSDFlist.containsKey(innerType)){
				 subSDFs = subSDFlist.get(innerType); 
			 }
			 else{
				 subSDFs = new ArrayList<JSONObject>(); 
				 subSDFlist.put(innerType, subSDFs);
			 }
			 subSDFs.add(rs); 
		 }
	}
	
	private void mappInnerJSON(Object o, Map<String,List<String>> messageMap) throws JSONException {
		
		
		JSONObject innerJo = (JSONObject) o; 
		String innerSDF = null; 
		 if(innerJo.has(SDFObjects.SDFType)){
			
			 SDFType innerType = null; 
			innerType  = SDFType.valueOf(innerJo.getString(SDFObjects.SDFType)); 
			if(generalMessageMap== null)
				generalMessageMap = new HashMap<>(); 
			if(generalMessageMap.containsKey(innerType)) 
				messageMap = generalMessageMap.get(innerType); 
			else{
				 messageMap  = new HashMap<>(); 
				generalMessageMap.put(innerType, messageMap); 
			}
			 
			transform2MessageMap(innerJo, messageMap, innerType); 
			
				JSONObject rs = transform2Json(messageMap, innerType);
			 List<JSONObject> subSDFs = null;
			 
			 if(subSDFlist.containsKey(innerType)){
				 subSDFs = subSDFlist.get(innerType); 
			 }
			 else{
				 subSDFs = new ArrayList<JSONObject>(); 
				 subSDFlist.put(innerType, subSDFs);
			 }
			
			 subSDFs.add(rs); 
		 }
	}
	
	public String transform(JSONObject updateMessage) {
		
		Map<String,List<String>> messageMap = new HashMap<String, List<String>>(); 
		log.debug("got Message" + updateMessage);
		updateMessage.has(SDFObjects.SDFType); 
		SDFType sdfType= null;
		try {
			String sdfName = updateMessage.getString(SDFObjects.SDFType);
			if(sdfName!=null )
				sdfType = SDFType.valueOf(sdfName);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		if(sdfType!=null){
			 
			
			switch (toolFormat){
				case PLAINTEXT :
					if(generalMessageMap== null){
						generalMessageMap = new HashMap<>(); 
						generalMessageMap.put(sdfType, messageMap);
					}else{
						if(generalMessageMap.containsKey(sdfType)){
							messageMap = generalMessageMap.get(sdfType); 
						}
						else{
						generalMessageMap.put(sdfType, messageMap);
						}
					}
					transform2MessageMap(updateMessage, messageMap, sdfType); 
					String answerPlain = transform2PlainText(messageMap); 
					log.debug("Mapped to " + answerPlain);
					return answerPlain; 
				case JSON :
					transform2MessageMap(updateMessage, messageMap, sdfType); 
					String answerJSON = transform2JsonString(messageMap, sdfType); 
					log.debug("Mapped to " + answerJSON);
					return answerJSON; 
	//			case XML :
	//				return transform2XML(messageMap); 
			}
			
		}
		return null;
	}

	private String transform2PlainText(Map<String,List<String>> mM){
		String message=""; ;
		
		
		
		orderMap = MappingReader.getOrderMap(); 
		for(int ordNum = 0; ordNum <= MappingReader.getMaxOrderNum(); ordNum++){
			if(orderMap.containsKey(ordNum)){
				String tkey = orderMap.get(ordNum).getToolAttributeName();
				String sdfKey = orderMap.get(ordNum).getSdfAttributeName();
				MappingObject mo = orderMap.get(ordNum); 
				
				SDFAttributeClass sac = mo.getSdfAttributeClass(); 
				mM = generalMessageMap.get(mo.getSdfType()); 
				
				if(mM!=null&& sac.equals(SDFAttributeClass.TOOLDEFAULT)){
					
					String v = mo.getToolDefaultValue(); 
					if(message!=""){
						message += outgoingMessageLineSeperator+tkey+incomingMessageValueSepeartor+" "+v; 
					}else{
						message += tkey+incomingMessageValueSepeartor+" "+v; 
					}
				}
			
				if(mM != null && mM.containsKey(sdfKey)){
					List<String> l = mM.get(sdfKey); 
					
					for(String v : l){
						if(message!=""){
							System.out.println();
							message += outgoingMessageLineSeperator+tkey+incomingMessageValueSepeartor+" "+v; 
						}else{
							message += tkey+incomingMessageValueSepeartor+" "+v; 
						}
					}
				}
			}

		}
		
		
		return message; 
	}
	
	
	private String transform2XML(Map<String, List<String>> mM) {
		return null;
	}

	private JSONObject setValueToInnerstJSON(JSONObject jRet, MappingObject mo, int cLvl, String sdfValue ){
		Map<Integer, JsonHirachy> hiMap = mo.getHirachyMap(); 
		JSONObject jj = null; 
	
		if(hiMap!=null && hiMap.containsKey(cLvl)){
			String name = hiMap.get(cLvl).getName();
			AttributeFormat format = hiMap.get(cLvl).getFormat(); 
			if(format.equals(AttributeFormat.VALUE)){
				if(cLvl>1){
					if(jRet.has(name)){
						try {
								return setValueToInnerstJSON(jRet.getJSONObject(name), mo, cLvl-1, sdfValue);
						} catch (JSONException e) {
							e.printStackTrace();
						} 
						
					}else{
						JSONObject nJO = new JSONObject(); 
						try {
							jRet.put(name, nJO);
						} catch (JSONException e) {
							e.printStackTrace();
						} 
					}
			}
				else{
						mappValue(name, jRet, mo, sdfValue);
					 
					return jRet; 
				}
			}else if(format.equals(AttributeFormat.LIST)){
					setInnerList(jRet, mo, cLvl, sdfValue, hiMap, name);
			}
		}
		return jj; 
	}

	private void mappValue(String name, JSONObject jRet, MappingObject mo, JSONObject sdfValue ){
		try {
			jRet.put(name, sdfValue);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void mappValue(String name, JSONObject jRet, MappingObject mo, String sdfValue ){
		try {
			if (mo.getSdfAttributetype().equals(SDFAttributeType.STRING)) {
				jRet.put(name, sdfValue);
			}
			else if (mo.getSdfAttributetype().equals(SDFAttributeType.DATE)) {
				String transformedDate = null; 
				 transformedDate = DateTransformer.toInput(sdfValue); 
				if(transformedDate!=null)
					jRet.put(name,transformedDate );
				}
			else if (mo.getSdfAttributetype().equals(SDFAttributeType.TIME)) {
				jRet.put(name, sdfValue);
			}
			else if(mo.getSdfAttributetype().toString().startsWith("ENUM-")){
				
				Map<SDFType, Map<String, Map<String, String>>> enumSDFMap = MappingReader.getEnumSDFMap(); 
				if(enumSDFMap!=null){
					Map<String, Map<String, String>> enumNamesMap = enumSDFMap.get(mo.getSdfType()); 
					
					if(enumNamesMap!=null && enumNamesMap.containsKey(mo.getSdfAttributetype().toString())){}
						Map<String, String> enumValueMap = enumNamesMap.get(mo.getSdfAttributetype().toString()); 
						String mappedValue = enumValueMap.get(sdfValue); 
						jRet.put(mo.getToolAttributeName(),mappedValue);
						
					}
			}
			else {
				jRet.put(name, sdfValue);
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	private JSONObject setValueToInnerstJSON(JSONObject jRet, MappingObject mo, int cLvl, JSONObject sdfValue ){
		Map<Integer, JsonHirachy> hiMap = mo.getHirachyMap(); 
		JSONObject jj = null; 
		if(hiMap.containsKey(cLvl)){
			String name = hiMap.get(cLvl).getName();
			AttributeFormat format = hiMap.get(cLvl).getFormat(); 
			if(format.equals(AttributeFormat.VALUE)){
				if(cLvl>1){
					if(jRet.has(name)){
						try {
								return setValueToInnerstJSON(jRet.getJSONObject(name), mo, cLvl-1, sdfValue);
						} catch (JSONException e) {
							e.printStackTrace();
						} 
						
					}else{
						JSONObject nJO = new JSONObject(); 
						try {
							jRet.put(name, nJO);
						} catch (JSONException e) {
							e.printStackTrace();
						} 
					}
			}
				else{
					
						mappValue(name,jRet,mo,sdfValue); 
					
					return jRet; 
				}
			}else if(format.equals(AttributeFormat.LIST)){
					setInnerList(jRet, mo, cLvl, sdfValue, hiMap, name);
			}
		}
		return jj; 
	}

	private void setInnerList(JSONObject jRet, MappingObject mo, int cLvl, String sdfValue,
			Map<Integer, JsonHirachy> hiMap, String name) {
		JSONArray nJA = new JSONArray(); 
		if(jRet.has(name)){
			try {
				nJA = jRet.getJSONArray(name);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			
		}else{
			try {
				jRet.put(name, nJA);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
		if(cLvl>1){
			String innerName = hiMap.get(cLvl-1).getName();
			AttributeFormat innerFormat = hiMap.get(cLvl-1).getFormat(); 
			if(innerFormat.equals(AttributeFormat.VALUE)){
				JSONObject joo;
				if(nJA.length() > 0){
					try {
						 joo = nJA.getJSONObject(0);
						 setValueToInnerstJSON(joo, mo, cLvl-1, sdfValue);
					} catch (JSONException e) {
					} 
					
				}else{
					 joo = new JSONObject(); 
					 nJA.put(joo); 
					 setValueToInnerstJSON(joo, mo, cLvl-1, sdfValue);
				}
			}
		}else{
			nJA.put(sdfValue); 
		}
	}
	
	private void setInnerList(JSONObject jRet, MappingObject mo, int cLvl, JSONObject sdfValue,
			Map<Integer, JsonHirachy> hiMap, String name) {
		JSONArray nJA = new JSONArray(); 
		if(jRet.has(name)){
			try {
				nJA = jRet.getJSONArray(name);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			
		}else{
			try {
				jRet.put(name, nJA);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
		if(cLvl>1){
			String innerName = hiMap.get(cLvl-1).getName();
			AttributeFormat innerFormat = hiMap.get(cLvl-1).getFormat(); 
			if(innerFormat.equals(AttributeFormat.VALUE)){
				JSONObject joo;
				if(nJA.length() > 0){
					try {
						 joo = nJA.getJSONObject(0);
						 setValueToInnerstJSON(joo, mo, cLvl-1, sdfValue);
					} catch (JSONException e) {
					} 
					
				}else{
					 joo = new JSONObject(); 
					 nJA.put(joo); 
					 setValueToInnerstJSON(joo, mo, cLvl-1, sdfValue);
				}
			}
		}else{
			nJA.put(sdfValue); 
		}
	}
	
	private void addSDFValueToJson(JSONObject jRet, MappingObject mo, String sdfValue){
		Map<Integer, JsonHirachy> hiMap = mo.getHirachyMap(); 
		int maxHO = mo.getMaxHirachyOrder(); 
		for(int cLvl =maxHO ; cLvl >0 ; cLvl--){
			if(hiMap.containsKey(cLvl)){
				hiMap.get(cLvl).getName();
				setValueToInnerstJSON(jRet,mo,maxHO,sdfValue); 
			}
			
		}
		if(maxHO == 0){
			try {
				jRet.put(mo.getSdfAttributeName(), sdfValue);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
		
		
	}
	
	private void addToolDefaults(JSONObject jo, SDFType sdfType){
		Map<String, MappingObject> sdfMappingMap = mappingMap.get(sdfType);
		
		for(String defKey : sdfMappingMap.keySet()){
			MappingObject dmo = sdfMappingMap.get(defKey); 
  			if(dmo!=null && dmo.getSdfAttributeClass() != null && dmo.getSdfAttributeClass().equals(SDFAttributeClass.TOOLDEFAULT)){
				String dVal = dmo.getToolDefaultValue();
				String dKey = dmo.getToolAttributeName(); 
				if(dVal!=null && dKey!=null){
					        try {
								jo.put(dKey, dVal);
							} catch (JSONException e) {
								e.printStackTrace();
							} 
				}
			}
		}
		
	}
	
	private String transform2JsonString(Map<String, List<String>> mM, SDFType sdfType) {
		
		transform2Json(mM,sdfType); 
		
		return transform2Json(mM,sdfType).toString();
	}
	private JSONObject transform2Json(Map<String, List<String>> mM, SDFType sdfType) {
		JSONObject jRet = new JSONObject(); 
		
		Map<String, MappingObject> sdfMappingMap = mappingMap.get(sdfType); 
		for(String sdfkey : mM.keySet()){
			
			MappingObject mo = sdfMappingMap.get(sdfkey); 
		
			if(mo!=null){
				List<String> sdfValueList = mM.get(sdfkey); 
				
				for(String sdfValue: sdfValueList){
					addSDFValueToJson(jRet, mo, sdfValue); 
				}
			}
		}
		
			//TODO: Check 
		
		for(String defKey : sdfMappingMap.keySet()){
			addSubSDFEntry(jRet, sdfMappingMap, defKey);
		}
		
		addToolDefaults(jRet, sdfType); 
		
		
		return jRet;
	}


	

	private void addSubSDFEntry(JSONObject jRet, Map<String, MappingObject> sdfMappingMap, String defKey) {
		MappingObject dmo = sdfMappingMap.get(defKey); 
		if(dmo!=null  && dmo.getSubSDF()!=null){
			
			List<JSONObject> ll = subSDFlist.get( dmo.getSubSDF()); 
			log.trace("addingSubSDFs");
			if(ll!=null)
			for(JSONObject s : ll){
				setValueToInnerstJSON(jRet, dmo, 	dmo.getMaxHirachyOrder(), s);
			}
		}
	}

}
