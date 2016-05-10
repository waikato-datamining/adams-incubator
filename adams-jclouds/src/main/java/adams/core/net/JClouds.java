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

/**
 * JClouds.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.net;

import adams.core.Properties;
import adams.core.base.BasePassword;
import adams.core.base.BaseURL;
import adams.env.Environment;

import java.io.File;

/**
 * Handles the JClouds setup.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JClouds {

  /** the properties file. */
  public final static String FILENAME = "adams/core/net/JClouds.props";

  /** the key for the identity (= tenantName:userName). */
  public final static String IDENTITY = "Identity";

  /** the key for the credential (= password). */
  public final static String CREDENTIAL = "Credential";

  /** the key for the endpoint (= host with API). */
  public final static String ENDPOINT = "Endpoint";

  /** the properties. */
  protected static Properties m_Properties;
  
  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    Properties	result;

    if (m_Properties == null) {
      try {
	result = Properties.read(FILENAME);
      }
      catch (Exception e) {
	result = new Properties();
      }
      m_Properties = result;
    }

    return m_Properties;
  }

  /**
   * Writes the properties to disk.
   *
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
    String	filename;

    filename = Environment.getInstance().createPropertiesFilename(new File(FILENAME).getName());
    result   = props.save(filename);
    // require reload
    m_Properties = null;

    return result;
  }

  /**
   * Returns the identity (tenantName:userName).
   *
   * @return		the identity
   */
  public static String getIdentity() {
    return getProperties().getProperty(IDENTITY);
  }

  /**
   * Returns the credential.
   *
   * @return		the password
   */
  public static BasePassword getCredential() {
    return new BasePassword(getProperties().getProperty(CREDENTIAL));
  }

  /**
   * Returns the API endpoint.
   * 
   * @return		the URL
   */
  public static BaseURL getEndpoint() {
    return new BaseURL(getProperties().getProperty(ENDPOINT));
  }
}
