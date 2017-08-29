package de.arago.ews_exchange.listener;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RestMessageSender implements MessageSender{
	
	private OkHttpClient client;
	private String connectURL; 
	private String connectPort; 
	private String connectSegment; 
	private static final Logger log = LoggerFactory.getLogger(RestMessageSender.class); 
			
			
	public RestMessageSender(OkHttpClient client, Map<String,String> confMap) {
		this.client = client; 


		connectURL = confMap.get("connectURL"); 
		connectPort = confMap.get("connectPort");
		connectSegment = confMap.get("connectAPIPath");
	}

	public int sendMessage(String message) {
		 int successfullySend = 0; 
		 log.trace("sending the message: "+ message);
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody bodyAsJson = RequestBody.create(mediaType, message);
		Request request = new Request.Builder()
		  .url(connectURL+":"+connectPort+connectSegment)
		  .post(bodyAsJson)
		  .addHeader("accept", "application/json")
		  .addHeader("content-type", "application/json")
		  .addHeader("Connection", "close")
		  .addHeader("cache-control", "no-cache")
		  .build();

		Response response = null ;
		try {
		
			
			log.trace("sending request:" + request);
			response = client.newCall(request).execute();
			
			
			String responseMessage = response.body().string() ; 
			log.trace("Response from Connectit: body:"+  responseMessage+ " message:" + response.message() + " code:" + response.code());
			int sendRc = response.code(); 
		
			
			if(sendRc == 200){
				//correct rc 
				if(responseMessage.contains("invalid sdf format: SDF format error ") ){
					sendRc = 400; 
				}
				if(responseMessage.contains("\\\"status\\\":\\\"400\\\"}\",\"status\":\"200\"}")){
					sendRc = 400; 
				}
				if(!responseMessage.endsWith(",\"status\":\"200\"}")){
					sendRc = -1; 
				}
			}

			successfullySend = sendRc; 
			
		} catch (IOException e) {
			if(response!=null)
				successfullySend = response.code();
			log.error(successfullySend + " "+ e.getMessage());
			
		}
		finally{
			if(response!=null){
				response.body().close();
			}
		}

		return successfullySend;
	}
	
}
