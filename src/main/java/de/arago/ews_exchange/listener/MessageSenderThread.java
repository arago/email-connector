package de.arago.ews_exchange.listener;


public interface MessageSenderThread extends Runnable{
	public boolean sendMessage();
}
