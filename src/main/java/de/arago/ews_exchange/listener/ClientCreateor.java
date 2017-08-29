package de.arago.ews_exchange.listener;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class ClientCreateor  implements Authenticator {
	private Map<String,String> confMap; 
	private String email; 
	private String pw; 
	private String uri ; 
	private String exV ; 
	private String basicUser; 
	private String basicPW; 
	private int writeTimeout = 30; 
	private int connectTimeout = 30; 
	
	private static final Logger log = LoggerFactory.getLogger(ClientCreateor.class); 
	
	public ClientCreateor(Map<String,String> confMap){
		this.confMap = confMap; 

		 email = confMap.get("email");
		 pw = confMap.get("password");
		 uri = confMap.get("uri");
		 exV = confMap.get("exchangeVersion"); 
		 if(confMap.containsKey("connectTimeout")){
			 connectTimeout = Integer.parseInt(confMap.get("connectTimeout"));
		 }
		 if(confMap.containsKey("writeTimeout")){
			 writeTimeout = Integer.parseInt(confMap.get("writeTimeout"));
		 }
		 if (confMap.containsKey("basicAuthUser")){
			 basicUser = confMap.get("basicAuthUser");
		 }
		 if (confMap.containsKey("basicAuthPassword")){
			 basicPW = confMap.get("basicAuthPassword");
		 }

	}
	
		
	public OkHttpClient getClient(){
		OkHttpClient client = null;
		
    	if(pw!=null && email!=null && uri!=null){
    		Builder okBuilder = new Builder(); 
    		okBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
    		okBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS);  
    		
    		//TODO: build in proxy
    		
    		okBuilder.retryOnConnectionFailure(true); 
    		
    		if(basicPW!=null && basicUser!=null){
    			okBuilder.authenticator(this); 
    			log.info("basic auth is used");
    		}
    		client = okBuilder.build(); 
	}
    	return client; 
}


	@Override
	public Request authenticate(Route route, Response response) throws IOException {
		String credential = Credentials.basic(basicUser, basicPW); 
			return response.request().newBuilder().header("Authorization", credential).build();
		
	}
	
}
