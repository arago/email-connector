package de.arago.ews_exchange.server;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.WebProxy;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;

public class ServiceFactory {
	private static final Logger log = LoggerFactory.getLogger(ServiceFactory.class); 
	private static Map<String,String> confMap; 
	private static ExchangeService service = null ;

	 private static String email ; 
	 private static	String password ; 
	 private static	String uri ; 
	private	static String exV ; 
	private static	String proxyHost = null; 
	private	static int proxyPort = -1; 
	private	static ExchangeVersion eV = ExchangeVersion.Exchange2010_SP2; 
	private	static String domain = null; 
		
	 
	public static ExchangeService getService(){
		WebProxy proxy = null; 
		
		 if(proxyHost!=null && proxyPort !=-1)
			 proxy = new WebProxy(proxyHost, proxyPort);
		
		if(proxy!=null)
			log.trace("uri:" + uri + " proxy:" + proxy.getHost() +":"+ proxy.getPort());
		
		
			service = new ExchangeService(eV);

			service.setWebProxy(proxy );
		 
		 ExchangeCredentials credentials; 
		 if (domain == null){
			 credentials = new WebCredentials(email, password);}
		 else{
			 credentials = new WebCredentials(email, password, domain);
				 
			 }
		 
		service.setCredentials(credentials);
		
		try {
			service.setUrl(new java.net.URI(uri));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} 
		return service; 
	}
	
	public  void init(Map<String,String> confMap){
		this.confMap = confMap; 
		 email = confMap.get("email");
		 password = confMap.get("password");
		 uri = confMap.get("uri");
		 exV = confMap.get("exchangeVersion"); 
		 proxyHost = null; 

		 eV = ExchangeVersion.Exchange2010_SP2; 

		if(confMap.containsKey("domain"))
			domain = confMap.get("domain"); 
		if(confMap.containsKey("proxyHost"))
			proxyHost = confMap.get("proxyHost"); 
		if(confMap.containsKey("proxyPort"))
			proxyPort = Integer.parseInt(confMap.get("proxyPort")); 
		
		eV = ExchangeVersion.valueOf(exV);
		if(eV==null){
			eV = ExchangeVersion.Exchange2010_SP2; 
		}	
		 System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
		 
		
		
	}
}
