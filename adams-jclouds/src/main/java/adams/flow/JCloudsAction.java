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
 * JCloudsAction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow;

import adams.core.CleanUpHandler;
import adams.core.option.OptionHandler;
import adams.flow.standalone.JCloudsConnection;

/**
 * Interface for JClouds actions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface JCloudsAction
  extends OptionHandler, CleanUpHandler {

  /**
   * Sets the JClouds connection to use.
   *
   * @param conn	the connection
   */
  public void setConnection(JCloudsConnection conn);

  /**
   * Returns the JClouds connection in use.
   *
   * @return		the connection, null if none set
   */
  public JCloudsConnection getConnection();

  /**
   * Returns the provider that this action requires.
   *
   * @return		the provider
   */
  public String getProvider();

  /**
   * Performs the action.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute();
}
