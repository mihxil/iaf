/*
   Copyright 2013 Nationale-Nederlanden

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package nl.nn.adapterframework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Convenience functions for logging.
 * Enables a separate log4j configuartion for each Ibis-instance.
 * Searches first for log4j4ibis.properties on the classpath. If not found, then searches for log4j.properties.
 *
 * @author  Gerrit van Brakel
 * @author  Jaco de Groot (***@dynasol.nl)
 */
public class LogUtil {
	public static final String DEBUG_LOG_PREFIX = "Ibis LogUtil class ";
	public static final String DEBUG_LOG_SUFFIX = "";
	public static final String WARN_LOG_PREFIX = DEBUG_LOG_PREFIX;
	public static final String WARN_LOG_SUFFIX = ", leaving it up to log4j's default initialization procedure: http://logging.apache.org/log4j/docs/manual.html#defaultInit";
	public static final String LOG4J_XML_FILE = "log4j4ibis.xml";
	public static final String LOG4J_PROPS_FILE = "log4j4ibis.properties";

	private static Hierarchy hierarchy=null;

	static {
		String l4jxml;		URL url = LogUtil.class.getClassLoader().getResource(LOG4J_XML_FILE);
		if (url == null) {
			l4jxml = null;
			System.out.println(DEBUG_LOG_PREFIX + "did not find " + LOG4J_XML_FILE + ", will try " + LOG4J_PROPS_FILE + " instead" + DEBUG_LOG_SUFFIX);
		} else {
			try {
				l4jxml = resourceToString(url);
			} catch (IOException e) {
				l4jxml = null;
				System.out.println(DEBUG_LOG_PREFIX + "could not read " + url + " (" + e.getClass().getName() + ": " + e.getMessage() + "), will try " + LOG4J_PROPS_FILE + " instead" + DEBUG_LOG_SUFFIX);
			}
		}

		Properties log4jProperties = getProperties(LOG4J_PROPS_FILE);
		if (log4jProperties != null) {
			Properties dsProperties = getProperties("DeploymentSpecifics.properties");
			if (dsProperties != null) {
				String instanceNameLowerCase = dsProperties.getProperty("instance.name");
				if (instanceNameLowerCase != null) {
					instanceNameLowerCase = instanceNameLowerCase.toLowerCase();
				} else {
					instanceNameLowerCase = "ibis4unknown";
				}
				log4jProperties.put("instance.name.lc", instanceNameLowerCase);
				log4jProperties.putAll(dsProperties);
				hierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
				if (l4jxml==null) {
					new PropertyConfigurator().doConfigure(log4jProperties, hierarchy);
				} else {
					log4jProperties.putAll(System.getProperties());
					l4jxml = StringResolver.substVars(l4jxml, log4jProperties);
					Reader reader = new StringReader(l4jxml);
					new DOMConfigurator().doConfigure(reader, hierarchy);
				}
			}
		}
	}

	public static Logger getRootLogger() {
		if (hierarchy == null) {
			return Logger.getRootLogger();
		} else {
			return hierarchy.getRootLogger();
		}
	}

	public static Logger getLogger(String name) {
		Logger logger = null;
		if (hierarchy == null) {
			logger = Logger.getLogger(name);
		} else {
			logger = hierarchy.getLogger(name);
		}
		return logger;
	}

	public static Logger getLogger(Class clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(Object owner) {
		return getLogger(owner.getClass());
	}


	private static Properties getProperties(String resourceName) {
		Properties properties = null;
		URL url = LogUtil.class.getClassLoader().getResource(resourceName);
		if (url == null) {
			System.out.println(WARN_LOG_PREFIX + "did not find " + resourceName + WARN_LOG_SUFFIX);
		} else {
			properties = getProperties(url);
		}
		return properties;
	}

	private static Properties getProperties(URL url) {
		Properties properties = new Properties();
		try {
			properties.load(url.openStream());
			if (System.getProperty("log4j.debug") != null) {
				System.out.println(DEBUG_LOG_PREFIX + "loaded properties from " + url.toString() + DEBUG_LOG_SUFFIX);
			}
		} catch (IOException e) {
			properties = null;
			System.out.println(WARN_LOG_PREFIX + "could not read " + url + " (" + e.getClass().getName() + ": " + e.getMessage() + ")" + WARN_LOG_SUFFIX);
		}
		return properties;
	}

	private static String resourceToString(URL url) throws IOException {
		char[] buff = new char[1024];
		Writer stringWriter = new StringWriter();
		InputStream stream = url.openStream();
		try {
			Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			int n;
			while ((n = reader.read(buff))!=-1) {
				stringWriter.write(buff, 0, n);
			}
			return stringWriter.toString();
		}
		finally {
			stringWriter.close();
			stream.close();
		}
	}
}
