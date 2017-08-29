package de.arago.ews_exchange.server;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.security.MapVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.arago.ews_exchange.PropertiesReader;
import de.arago.ews_exchange.mapping.GenericBackTransformer;
import de.arago.ews_exchange.mapping.MessageBackTransformer;
import de.arago.ews_exchange.mapping.SDFType;
import de.arago.ews_exchange.mapping.SimpleBackTransformer;

public class RestletResource extends ServerResource {
	private static final Logger log = LoggerFactory.getLogger(RestletResource.class); 
	private static MessageBackSender messageBackSender = null ; 
	private static MessageBackTransformer  messageTransformer = null; ;
	private static String  propFileName = "exlistener.properties"; 
	private static Map<String,String> confMap = null; 
	
	public static void setPropFileName(String propFile){
		propFileName = propFile; 
		
		

		
	}
	public RestletResource(){
		super(); 
		
		if(messageTransformer ==null){
			 messageTransformer =  new GenericBackTransformer(propFileName); ;
		}
		if(confMap== null){
			confMap = PropertiesReader.getConfMap(propFileName); 
		}
		if(messageBackSender == null){
			messageBackSender = new ExchangeSender(confMap); 
		}
	}
	
	
	
	@Get 
	public String hello() 
	{ return "up and running"; } 
	
	
	

	
	@Post()
	public void send(Representation jR) {
		JSONObject jO = null; 
		String text = null; 
		
		if(jR==null){
			log.error("no representation");
		}else{
		MediaType mediaType = jR.getMediaType(); 
		if(mediaType.equals(MediaType.APPLICATION_JSON)){
			try {
				text = jR.getText();
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
			if(text != null )
			try {
				jO = new JSONObject(text);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
		}
		if(jO!=null){
			String backMessage = messageTransformer.transform(jO); 
			boolean successfullySend = messageBackSender.sendMessage(backMessage); 	
			System.out.println("got message " +jO.toString());
		}
		
	}
	
	}


