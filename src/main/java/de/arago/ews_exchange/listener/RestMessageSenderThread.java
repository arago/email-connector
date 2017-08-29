package de.arago.ews_exchange.listener;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

public class RestMessageSenderThread implements MessageSenderThread{
	private static final Logger log = LoggerFactory.getLogger(RestMessageSenderThread.class); 
	private String connectURL; 
	private int  connectPortNumber; 
	private String sendMessage; 
	private EmailMessage message; 
	private OkHttpClient client; 
	private boolean successfullySend; 
	

	public RestMessageSenderThread(String connectorUrl, int connectPortNumber, String sendMessage, EmailMessage message, OkHttpClient client) {
		this.connectPortNumber = connectPortNumber; 
		this.connectURL = connectorUrl;
		this.message = message; 
		this.client = client; 
		this.sendMessage = sendMessage; 
	}
	
	public boolean sendMessage() {
		
		 boolean successfullySend = false; 
		 System.out.println("sending the message: "+ sendMessage);
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody bodyAsJson = RequestBody.create(mediaType, sendMessage);
		Request request = new Request.Builder()
		  .url(connectURL+":"+connectPortNumber)
		  .post(bodyAsJson)
		  .addHeader("accept", "application/json")
		  .addHeader("content-type", "application/json")
		  .addHeader("cache-control", "no-cache")
		  .build();

		Response response;
		try {
			response = client.newCall(request).execute();

			int sendRc = response.code(); 
			
			if(sendRc == 200){
				successfullySend = true; 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return successfullySend;
		
	}

	public void run() {
		successfullySend = sendMessage();
		if(successfullySend){
			try {
				message.setIsRead(true);
				message.update(ConflictResolutionMode.AutoResolve);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}



	

}
