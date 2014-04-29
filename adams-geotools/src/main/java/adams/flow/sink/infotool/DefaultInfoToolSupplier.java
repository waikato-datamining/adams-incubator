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
 * DefaultInfoToolSupplier.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.infotool;

import org.geotools.swing.tool.CursorTool;
import org.geotools.swing.tool.InfoTool;

/**
 * Default GeoTools {@link InfoTool}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultInfoToolSupplier
  extends AbstractInfoToolSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -4002971853300712434L;

  /** the InfoTool instance in use. */
  protected InfoTool m_InfoTool;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Provides the default GeoTools InfoTool.";
  }
  
  /**
   * Returns whether a database connection is required.
   * 
   * @return		always false
   */
  @Override
  public boolean requiresDatabaseConnection() {
    return false;
  }

  /**
   * Returns a new {@link CursorTool} instance.
   * 
   * @return		the tool
   */
  @Override
  protected CursorTool newInfoTool() {
    return new InfoTool();
  }
}
