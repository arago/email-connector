package de.arago.ews_exchange.mapping;

import org.json.JSONException;
import org.json.JSONObject;

public class IssueData {
	String formalJSONRep = "{sresult paren lamflmasdlf}";
	private String mode_sla =""; 
		
	private void createFromString(JSONObject input){
		if(input.has("mode_sla")){
			try {
				mode_sla = input.getString("mode_sla");
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
	}

}
