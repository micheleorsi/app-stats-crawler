/**
 * 
 */
package it.micheleorsi.utils;

import it.micheleorsi.model.StoreAuth;
import it.micheleorsi.model.StoreType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author micheleorsi
 *
 */
public class Configurator {
	
	Logger log = Logger.getLogger(Configurator.class.getName());
	
	private List<StoreAuth> appleAuths = new ArrayList<StoreAuth>();
	private List<StoreAuth> googleAuths = new ArrayList<StoreAuth>();
	
	private static final String APPLE_ID = "apple.id";
	private static final String APPLE_USERNAME = "apple.username";
	private static final String APPLE_PASSWORD = "apple.password";
	
	private static final String GOOGLE_USERNAME = "google.username";
	private static final String GOOGLE_PASSWORD = "google.password";
	
	public static final String ROOT_PATH = "/appstats";
	
	public Configurator(String fileName) throws ConfigurationException {
		Configuration config = new PropertiesConfiguration(fileName);
		
		int numberOfAppleAccounts = config.getStringArray(APPLE_ID).length;
		log.info("number of Apple account set "+numberOfAppleAccounts);
		for(int i=0; i<numberOfAppleAccounts; i++) {
			StoreAuth localAccount = new StoreAuth(config.getStringArray(APPLE_ID)[i], 
					config.getStringArray(APPLE_USERNAME)[i], 
					config.getStringArray(APPLE_PASSWORD)[i], 
					StoreType.APPLE);
			appleAuths.add(localAccount);
		}
		
		int numberOfGoogleAccounts = config.getStringArray(GOOGLE_USERNAME).length;
		log.info("number of Google account set "+numberOfGoogleAccounts);
		for(int i=0; i<numberOfGoogleAccounts; i++) {
			StoreAuth localAccount = new StoreAuth(config.getStringArray(GOOGLE_USERNAME)[i], 
					config.getStringArray(GOOGLE_USERNAME)[i], 
					config.getStringArray(GOOGLE_PASSWORD)[i], 
					StoreType.GOOGLE);
			googleAuths.add(localAccount);
		}
	}

	public List<StoreAuth> getGoogleAuths() {
		return googleAuths;
	}

	public List<StoreAuth> getAppleAuths() {
		return appleAuths;
	}


}
