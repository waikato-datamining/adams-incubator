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
 * AbstractOpenMLSource.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import java.util.logging.Level;

import net.minidev.json.JSONAware;

import org.json.JSONObject;

import adams.core.net.OpenMLHelper;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.OpenMLConnection;

/**
 * Ancestor for OpenML source actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOpenMLSource
  extends AbstractSimpleSource {
  
  /** for serialization. */
  private static final long serialVersionUID = -5561129671356371714L;
  
  /** the connection. */
  protected OpenMLConnection m_Connection;

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Connection = null;
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected OpenMLConnection getConnection() {
    return (OpenMLConnection) ActorUtils.findClosestType(this, OpenMLConnection.class, true);
  }

  /**
   * Checks the JSONObject for errors.
   * 
   * @param json	the JSON object to check
   */
  protected String checkJSON(JSONObject json) {
    String	result;
    
    result = OpenMLHelper.extractErrorMessage(json);
    getLogger().log(Level.SEVERE, result);
    
    return result;
  }

  /**
   * Checks the JSONObject for errors.
   * 
   * @param json	the JSON object to check
   */
  protected String checkJSON(JSONAware json) {
    String	result;
    
    result = OpenMLHelper.extractErrorMessage(json);
    getLogger().log(Level.SEVERE, result);
    
    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Connection = getConnection();
      if (m_Connection == null)
	result = "No " + OpenMLConnection.class.getName() + " actor found!";
    }

    return result;
  }
}
