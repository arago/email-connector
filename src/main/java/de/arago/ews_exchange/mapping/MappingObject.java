package de.arago.ews_exchange.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingObject {
	private static final Logger log = LoggerFactory.getLogger(MappingObject.class); 
	public SDFType getSdfType() {
		return sdfType;
	}


	public void setSdfType(SDFType sdfType) {
		this.sdfType = sdfType;
	}

	

	public int getToolOrder() {
		return toolOrder;
	}


	public void setToolOrder(int toolOrder) {
		this.toolOrder = toolOrder;
	}


	public SDFAttributeClass getSdfAttributeClass() {
		return sdfAttributeClass;
	}


	public void setSdfAttributeClass(SDFAttributeClass sdfAttributeClass) {
		this.sdfAttributeClass = sdfAttributeClass;
	}
	private String sdfAttributeName; 
	private AttributeFormat sdfAttibuteFormat; 
	private SDFAttributeType sdfAttributetype; 
	private SDFAttributeClass sdfAttributeClass; 
	private static ToolFormat toolFormat; 
	private String toolAttributeName;
	private int toolOrder; 
	private String combinedKey; 
	private String sdfDefaultValue; 
	private String toolDefaultValue; 
	private SDFType sdfType; 
	private SDFType subSDF; 
	private boolean isStartOfSubSDF; 
	private boolean isSDFStructureInformation = false; 
	public SDFType getSubSDF() {
		return subSDF;
	}


	public void setSubSDF(SDFType subSDF) {
		this.subSDF = subSDF;
	}
	private int maxHirachyOrder; 
	private Map<Integer, JsonHirachy> hirachyMap; 
	
	
	public MappingObject(String sdfAttributeName, AttributeFormat sdfAttibuteFormat,
		SDFAttributeType sdfAttributetype, SDFAttributeClass sdfAttributeClass, String toolAttributeName) {
		this.sdfAttributeName = sdfAttributeName;
		this.sdfAttibuteFormat = sdfAttibuteFormat;
		this.sdfAttributetype = sdfAttributetype;
		this.toolAttributeName = toolAttributeName;
		this.sdfAttributeClass = sdfAttributeClass; 
	}
	
	
	


	public String getToolDefaultValue() {
		return toolDefaultValue;
	}


	public void setToolDefaultValue(String toolDefaultValue) {
		this.toolDefaultValue = toolDefaultValue;
	}


	@Override
	public String toString() {
		return "MappingObject [sdfAttributeName=" + sdfAttributeName + ", SDFType=" +sdfType+ ", sdfAttibuteFormat=" + sdfAttibuteFormat
				+ ", sdfAttributetype=" + sdfAttributetype + ", sdfAttributeClass=" + sdfAttributeClass
				+ ", toolAttributeName=" + toolAttributeName + ", toolOrder=" + toolOrder + ", combinedKey="
				+ combinedKey + ", sdfDefaultValue=" + sdfDefaultValue + ", toolDefaultValue=" + toolDefaultValue
				+ ", maxHirachyOrder=" + maxHirachyOrder + ", hirachyMap=" + hirachyMap + "]";
	}


	public MappingObject(String sdfAttributeName, String mappString) {
		this.sdfAttributeName = sdfAttributeName;
		String[] splittedMapping = mappString.split(","); 
		this.sdfType = SDFType.valueOf(splittedMapping[0]);
		this.sdfAttibuteFormat = AttributeFormat.valueOf(splittedMapping[1]);
		
		this.sdfAttributetype = SDFAttributeType.valueOf(splittedMapping[2]) ; 
		if(sdfAttributetype!=null&&!sdfAttributetype.equals(SDFAttributeType.DATE) && ! sdfAttributetype.equals(SDFAttributeType.TIME)&& ! sdfAttributetype.equals(SDFAttributeType.STRING)){
			this.subSDF =SDFType.valueOf(splittedMapping[2]); 
		}
		this.sdfAttributeClass = SDFAttributeClass.valueOf(splittedMapping[3]) ; 
		
		
		
		if(sdfAttributeClass==SDFAttributeClass.SDFDEFAULT){
			try{
			this.sdfDefaultValue = splittedMapping[4]; }
			catch (ArrayIndexOutOfBoundsException e){
				this.sdfDefaultValue = ""; 
			}
		} else if(sdfAttributeClass==SDFAttributeClass.TOOLDEFAULT){
			this.toolDefaultValue= splittedMapping[4]; 
			this.toolAttributeName = sdfAttributeName;
			switch(toolFormat){
			case PLAINTEXT: 
				this.toolOrder = Integer.valueOf(splittedMapping[5]); 
			break;
			}
		}
		
		else{
			
			
			switch(toolFormat){
			case PLAINTEXT: 
			//	String subType = null; 
				SubSDFType subType = null; 
				try{
					if(sdfAttributetype!=null)
						subType = SubSDFType.valueOf(sdfAttributetype.toString()); 
				}catch(IllegalArgumentException e){
					
				}
				if(subType !=null){
					setSDFStructureInformation(true); 
				}else{
				
					this.toolAttributeName = splittedMapping[4];
					this.combinedKey = this.toolAttributeName; 
					this.toolOrder = Integer.valueOf(splittedMapping[5]); 
					SubSDFType sT = null; 
					//String sT = null;
					this.isStartOfSubSDF = false; 
					String startMarker = null; 
					try{
						startMarker = splittedMapping[6];
					}catch (Exception e){
						
					}
					if(startMarker!=null)
						this.isStartOfSubSDF=true; 
					
					try{
						if(sdfAttributetype!=null)
						sT = SubSDFType.valueOf(sdfAttributetype.toString()); 
					}catch(IllegalArgumentException ie){
						
					}
				}
			break; 
			case JSON: 
				this.toolAttributeName = splittedMapping[4];
				maxHirachyOrder  = -1; 
				hirachyMap = new HashMap<Integer,JsonHirachy>(); 
				int currentOrder = 0; 
				String combinedKey = ""; 
				for (int i = 4; i < splittedMapping.length-1; i+=2){
					currentOrder++;
					JsonHirachy jh = new JsonHirachy(currentOrder, AttributeFormat.valueOf(splittedMapping[i+1]), splittedMapping[i]);
					hirachyMap.put(currentOrder, jh);
					if(combinedKey=="")
						combinedKey += splittedMapping[i]; 
					else
						combinedKey+= "-"+splittedMapping[i];
					
					this.combinedKey = combinedKey;
					
					
					if(currentOrder>maxHirachyOrder)
						maxHirachyOrder= currentOrder; 
				}
				break; 
			}
			
			}
	}
	
	
	public String getSdfDefaultValue() {
		return sdfDefaultValue;
	}


	public void setSdfDefaultValue(String sdfDefaultValue) {
		this.sdfDefaultValue = sdfDefaultValue;
	}


	public String getCombinedKey() {
		return combinedKey;
	}


	public void setCombinedKey(String combinedKey) {
		this.combinedKey = combinedKey;
	}


	public int getMaxHirachyOrder() {
		return maxHirachyOrder;
	}


	public void setMaxHirachyOrder(int maxHirachyOrder) {
		this.maxHirachyOrder = maxHirachyOrder;
	}


	public Map<Integer, JsonHirachy> getHirachyMap() {
		return hirachyMap;
	}


	public void setHirachyMap(Map<Integer, JsonHirachy> hirachyMap) {
		this.hirachyMap = hirachyMap;
	}


	public String getSdfAttributeName() {
		return sdfAttributeName;
	}
	public void setSdfAttributeName(String sdfAttributeName) {
		this.sdfAttributeName = sdfAttributeName;
	}
	public AttributeFormat getSdfAttibuteFormat() {
		return sdfAttibuteFormat;
	}
	public void setSdfAttibuteFormat(AttributeFormat sdfAttibuteFormat) {
		this.sdfAttibuteFormat = sdfAttibuteFormat;
	}
	public SDFAttributeType getSdfAttributetype() {
		return sdfAttributetype;
	}
	public void setSdfAttributetype(SDFAttributeType sdfAttributetype) {
		this.sdfAttributetype = sdfAttributetype;
	}
	public ToolFormat getToolFormat() {
		return toolFormat;
	}
	public static void setToolFormat(ToolFormat tF) {
		toolFormat = tF;
	}
	public String getToolAttributeName() {
		return toolAttributeName;
	}
	public void setToolAttributeName(String toolAttributeName) {
		this.toolAttributeName = toolAttributeName;
	}


	public boolean isStartOfSubSDF() {
		return isStartOfSubSDF;
	}


	public void setStartOfSubSDF(boolean isStartOfSubSDF) {
		this.isStartOfSubSDF = isStartOfSubSDF;
	}


	public boolean isSDFStructureInformation() {
		return isSDFStructureInformation;
	}


	public void setSDFStructureInformation(boolean isSDFStructureInformation) {
		this.isSDFStructureInformation = isSDFStructureInformation;
	} 
	 
	

}
