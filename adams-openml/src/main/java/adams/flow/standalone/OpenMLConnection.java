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
 * OpenMLConnection.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import java.util.Date;

import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.xml.Authenticate;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.net.OpenMLHelper;

/**
 <!-- globalinfo-start -->
 * Provides access to the OpenML.org experiment database.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: OpenMLConnection
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The user to use for connecting to OpenML.org.
 * &nbsp;&nbsp;&nbsp;default: fracpete&#64;waikato.ac.nz
 * </pre>
 * 
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The OpenML password.
 * &nbsp;&nbsp;&nbsp;default: {Tm9qcmVyazA=}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7116 $
 */
public class OpenMLConnection
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the OpenML user. */
  protected String m_User;

  /** the OpenML password. */
  protected BasePassword m_Password;

  /** the OpenML URL. */
  protected String m_URL;
  
  /** the connector instance. */
  protected ApiConnector m_Connector;

  /** the session object. */
  protected Authenticate m_Session;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Provides access to the OpenML.org experiment database.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "user", "user",
	    OpenMLHelper.getUser());

    m_OptionManager.add(
	    "password", "password",
	    OpenMLHelper.getPassword());

    m_OptionManager.add(
	    "url", "URL",
	    OpenMLHelper.getURL());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Connector = null;
  }
  
  /**
   * Sets the OpenML user.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the OpenML user.
   *
   * @return		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The user to use for connecting to OpenML.org.";
  }

  /**
   * Sets the OpenML password.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the OpenML password.
   *
   * @return		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The OpenML password.";
  }

  /**
   * Sets the OpenML URL.
   *
   * @param value	the URL
   */
  public void setURL(String value) {
    m_URL = value;
    reset();
  }

  /**
   * Returns the OpenML URL.
   *
   * @return		the URL
   */
  public String getURL() {
    return m_URL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String URLTipText() {
    return "The URL to connect to.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "user", m_User, "user: ");
  }

  /**
   * Performs the authentication.
   * 
   * @return		null if OK, otherwise error message
   */
  protected String performAuthentication() {
    String	result;
    
    result = null;
    
    try {
      if (m_Connector == null)
	m_Connector = new ApiConnector(m_URL);
      m_Session = m_Connector.openmlAuthenticate(m_User, m_Password.getValue());
      if (isLoggingEnabled())
	getLogger().info("sessionHash=" + m_Session.getSessionHash() + ", validUntil=" + m_Session.getValidUntil());
    }
    catch (Exception e) {
      result = handleException("Authentication failed!", e);
    }
    
    return result;
  }
  
  /**
   * Returns the connector object.
   * 
   * @return		the connector, null if not available
   */
  public ApiConnector getConnector() {
    return m_Connector;
  }
  
  /**
   * Returns the session object.
   * 
   * @return		the session, null if failed to authenticate
   */
  public Authenticate getSession() {
    String	msg;
    DateFormat	format;
    
    // expired?
    if (m_Session != null) {
      format = DateUtils.getTimestampFormatter();
      if (format.format(new Date()).compareTo(m_Session.getValidUntil()) > 0)
	m_Session = null;
    }

    // re-authenticate?
    if (m_Session == null) {
      msg = performAuthentication();
      if (msg != null)
	return null;
    }
    
    return m_Session;
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String doExecute() {
    return performAuthentication();
  }
}
