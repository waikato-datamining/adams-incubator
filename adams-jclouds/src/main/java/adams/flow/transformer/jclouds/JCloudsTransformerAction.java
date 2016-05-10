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
 * JCloudsTransformerAction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.jclouds;

import adams.flow.JCloudsAction;

/**
 * Interface for actions that transform data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface JCloudsTransformerAction
  extends JCloudsAction {

  /**
   * Returns the accepted data types.
   *
   * @return		the data types
   */
  public Class[] accepts();

  /**
   * Sets the data to consume.
   *
   * @param data	the data
   */
  public void input(Object data);

  /**
   * Returns the data types being generated.
   *
   * @return		the data types
   */
  public Class[] generates();

  /**
   * Returns whether any data was generated.
   *
   * @return		true if data available to be collected
   * @see		#output()
   */
  public boolean hasPendingOutput();

  /**
   * Returns the generated data.
   *
   * @return		the generated data
   */
  public Object output();
}
