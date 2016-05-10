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
 * AbstractJCloudsSinkAction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.jclouds;

import adams.core.option.AbstractOptionHandler;
import adams.flow.standalone.JCloudsConnection;

/**
 * Ancestor for JClouds actions that only generate data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractJCloudsSourceAction
  extends AbstractOptionHandler
  implements JCloudsSourceAction {

  private static final long serialVersionUID = 3348340100584810763L;

  /** the connection to use. */
  protected JCloudsConnection m_Connection;

  /**
   * Sets the JClouds connection to use.
   *
   * @param conn	the connection
   */
  public void setConnection(JCloudsConnection conn) {
    m_Connection = conn;
  }

  /**
   * Returns the JClouds connection in use.
   *
   * @return		the connection, null if none set
   */
  public JCloudsConnection getConnection() {
    return m_Connection;
  }

  /**
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  public String getAdditionalInformation() {
    return "Provider: " + getProvider();
  }

  /**
   * Returns the data types of the generated data.
   *
   * @return		the data types
   */
  public abstract Class[] generates();

  /**
   * Hook method for checks before the actual execution.
   * <br>
   * Default implementation ensures that connection is present.
   *
   * @return		null if checks passed, otherwise error message
   */
  protected String check() {
    if (m_Connection == null)
      return "No JCloudsConnection set!";
    return null;
  }

  /**
   * Performs the actual action.
   *
   * @return		null if successful, otherwise error message
   */
  protected abstract String doExecute();

  /**
   * Performs the action.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String	result;

    result = check();
    if (result == null)
      result = doExecute();

    return result;
  }

  /**
   * Returns whether any data was generated.
   *
   * @return		true if data available to be collected
   * @see		#output()
   */
  public abstract boolean hasPendingOutput();

  /**
   * Returns the generated data.
   *
   * @return		the generated data
   */
  public abstract Object output();

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
  }
}
