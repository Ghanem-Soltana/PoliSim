package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	static Properties prop = new Properties();

	
	
	public static String getProperty(String name, String FileName)
	{
		Properties prop = new Properties();
    	InputStream input = null;
    	try {
    		 
    		input = new FileInputStream("Config/"+FileName+".properties");
     
    		// load a properties file
    		prop.load(input);
     
    		// get the property value and print it out
    		return prop.getProperty(name);
     
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
		return null;
		
	}
	
	


}