package com.rambris;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Handles configuration
 * 
 * @author Fredrik Rambris <fredrik@rambris.com>
 * @version $Id: Config.java 28 2010-05-23 19:47:55Z boost $
 */
public class Config extends XMLConfiguration
{
	private static File findConfigFile(String appname)
	{
		AbstractConfiguration c = new BaseConfiguration();
		String delimiter = System.getProperty("CONFIG_ARRAY_DELIMITER", ";");
		if (!delimiter.isEmpty()) c.setListDelimiter(delimiter.charAt(0));

		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);
		String filename = appname + ".xml";
		String[] paths = { filename, filename, "/etc/" + filename, "/etc/" + appname + "/" + filename, "~/." + filename, "~/.config/" + filename };
		if (System.getProperty("configfile") != null) paths[0] = System.getProperty("configfile");
		else if (System.getenv("CONFIGFILE") != null) paths[0] = System.getenv("CONFIGFILE");
		for (String path : paths)
		{
			File file = new File(path);
			if (file.exists()) return file;
		}
		return new File(filename);
	}

	public Config(File configFile) throws ConfigurationException
	{
		super(configFile);

		/* Configure logging if possible. Either via external properties-file */
		if (this.containsKey("log_config"))
		{
			PropertyConfigurator.configureAndWatch(getString("log_config"), 5000);
		}
		else
		{
			BasicConfigurator.configure();
		}
	}

	public Config(String appname) throws ConfigurationException
	{
		this(findConfigFile(appname));
	}

	static Properties configToProperties(Configuration cfg, Properties defaults)
	{
		Iterator i = cfg.getKeys();
		Properties props;
		if (defaults == null) props = new Properties();
		else props = new Properties(defaults);
		while (i.hasNext())
		{
			String key = (String) i.next();
			props.put(key, cfg.getString(key));
		}
		return props;
	}

	/**
	 * Convert a section of the config to a Properties object.
	 * 
	 * @param name
	 * @param defaults
	 * @return
	 */
	public Properties toProperties(String name, Properties defaults)
	{
		SubnodeConfiguration subnode = configurationAt(name);
		return configToProperties(subnode, defaults);
	}

	/**
	 * Convert a section of the config to a Properties object.
	 * 
	 * @param name
	 * @return
	 */
	public Properties toProperties(String name)
	{
		return toProperties(name, new Properties());
	}
}
