/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * OpenMLHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net;

import java.io.StringReader;

import adams.core.Properties;
import adams.core.base.BasePassword;
import adams.env.Environment;
import adams.env.OpenMLDefinition;

/**
 * A helper class for the OpenML setup.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8675 $
 */
public class OpenMLHelper {

  /** the name of the props file. */
  public final static String FILENAME = "OpenML.props";

  /** the user key. */
  public final static String USER = "User";

  /** the password. */
  public final static String PASSWORD = "Password";

  /** the URL. */
  public final static String URL = "URL";
  
  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns the underlying properties.
   *
   * @return		the properties
   */
  public synchronized static Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Environment.getInstance().read(OpenMLDefinition.KEY);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }

  /**
   * Writes the specified properties to disk.
   *
   * @param props	the properties to write to disk
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties() {
    return writeProperties(getProperties());
  }

  /**
   * Writes the specified properties to disk.
   *
   * @param props	the properties to write to disk
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties(Properties props) {
    boolean	result;

    result = Environment.getInstance().write(OpenMLDefinition.KEY, props);
    // require reload
    m_Properties = null;

    return result;
  }

  /**
   * Returns the user.
   *
   * @return		the user
   */
  public static String getUser() {
    return getProperties().getPath(USER, "");
  }

  /**
   * Returns the password.
   *
   * @return		the password
   */
  public static BasePassword getPassword() {
    return new BasePassword(getProperties().getPath(PASSWORD, ""));
  }

  /**
   * Returns the URL.
   *
   * @return		the URL
   */
  public static String getURL() {
    return getProperties().getPath(URL, "http://openml.liacs.nl/");
  }
  
  /**
   * Extracts the status from the JSON result.
   * 
   * @param json	the OpenML JSON object to use
   * @return		null if no status, otherwise status message
   */
  public static String extractStatus(org.json.JSONObject json) {
    String	result;
    
    result = null;
    
    try {
      if (json.has("status"))
	result = json.getString("status");
    }
    catch (Exception e) {
      System.err.println("Failed to retrieve status:");
      e.printStackTrace();
    }
    
    return result;
  }
  
  /**
   * Extracts the status from the JSON result.
   * 
   * @param json	the OpenML JSON object to use
   * @return		null if no status, otherwise status message
   */
  public static String extractStatus(net.minidev.json.JSONAware json) {
    String			result;
    net.minidev.json.JSONObject	obj;
    
    result = null;
    
    try {
      if (json instanceof net.minidev.json.JSONObject) {
	obj = (net.minidev.json.JSONObject) json;
	if (obj.containsKey("status"))
	  result = "" + obj.get("status");
      }
    }
    catch (Exception e) {
      System.err.println("Failed to retrieve status:");
      e.printStackTrace();
    }
    
    return result;
  }
  
  /**
   * Extracts any error message from the JSON result (status).
   * 
   * @param json	the OpenML JSON object to use
   * @return		null if no error message, otherwise error message
   */
  public static String extractErrorMessage(org.json.JSONObject json) {
    String	result;
    
    result = extractStatus(json);

    if (result != null) {
      if ((result.indexOf("Error") == -1) && (result.indexOf("error") == -1))
	result = null;
    }
    
    return result;
  }
  
  /**
   * Extracts any error message from the JSON result (status).
   * 
   * @param json	the OpenML JSON object to use
   * @return		null if no error message, otherwise error message
   */
  public static String extractErrorMessage(net.minidev.json.JSONAware json) {
    String	result;
    
    result = extractStatus(json);

    if (result != null) {
      if ((result.indexOf("Error") == -1) && (result.indexOf("error") == -1))
	result = null;
    }
    
    return result;
  }
  
  /**
   * Converts the OpenML JSON object into an ADAMS one.
   * 
   * @param json	the OpenML JSON object to convert
   * @return		the ADAMS JSON object
   * @throws Exception	if parsing of JSON fails
   */
  public static net.minidev.json.JSONAware convertJson(org.json.JSONObject json) throws Exception {
    StringReader			reader;
    net.minidev.json.parser.JSONParser	parser;
    Object				obj;
    
    reader = new StringReader(json.toString());
    parser = new net.minidev.json.parser.JSONParser(net.minidev.json.parser.JSONParser.MODE_JSON_SIMPLE);
    obj    = parser.parse(reader);
    reader.close();

    return (net.minidev.json.JSONAware) obj;
  }
}
