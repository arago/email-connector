package de.arago.ews_exchange.listener;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.notification.GetEventsResults;
import microsoft.exchange.webservices.data.property.complex.FolderId;

public abstract class HandlerThread extends Thread{
	private static final Logger log = LoggerFactory.getLogger(HandlerThread.class); 
	
	protected 	ExchangeService service;
	protected GetEventsResults events; 
	protected Map<String,String> confMap;
	protected String connectURL ;
	protected int connectPort; 
	protected MessageSender messageSender; 
	protected MessageTransformer messageTransformer; 
	protected  FolderId inboundFolderID; 
	protected FolderId validMessageFolderId; 
	protected FolderId invalidMassageFolderId; 
	protected ExchangeMessageHandler exchangeMessageHandler; 
	protected String incommingFolder; 
	
	public HandlerThread(ExchangeService service, GetEventsResults events, Map<String,String> confMap, MessageSender messageSender, MessageTransformer messageTransformer, FolderId inboundFolderID, FolderId invalidMassageFolderId, FolderId validMessageFolderId, String incommingFolder){
		this.confMap = confMap; 
		this.service = service; 
		this.events = events; 
		this.connectPort = Integer.valueOf(confMap.get("connectPort")); 
		this.connectURL = confMap.get("connectURL"); 
		this.messageSender = messageSender; 
		this.messageTransformer = messageTransformer; 
		this.inboundFolderID = inboundFolderID; 
		this.invalidMassageFolderId = inboundFolderID; 
		this.validMessageFolderId = validMessageFolderId; 
		this.exchangeMessageHandler = new ExchangeMessageHandler(invalidMassageFolderId, validMessageFolderId); 
		this.incommingFolder = incommingFolder; 
	}
}
