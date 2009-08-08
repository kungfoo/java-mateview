package ch.mollusca.plist;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.configuration.plist.PropertyListConfiguration;

public class Dict {
	private Configuration configuration;

	public Dict(String filename) {
		tryParsingFile(filename);
	}

	private void tryParsingFile(String filename) {
		try {
			configuration = new XMLConfiguration(new File(filename));
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public String getString(String key){
		return configuration.getString(key);
	}
	
	public int getInt(String key){
		return configuration.getInt(key);
	}
}
