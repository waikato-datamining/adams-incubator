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
 * InfoToolSupplier.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.infotool;

import adams.core.CleanUpHandler;
import adams.core.ShallowCopySupporter;
import adams.core.option.OptionHandler;
import adams.flow.core.Actor;
import adams.gui.visualization.maps.MapDisplayPanel;
import org.geotools.swing.tool.CursorTool;

/**
 * Ancestor for classes that provide {@link CursorTool} instances used in the
 * {@link MapDisplayPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface InfoToolSupplier
  extends OptionHandler, ShallowCopySupporter<InfoToolSupplier>, CleanUpHandler {

  /**
   * Returns the {@link CursorTool} instance to use.
   * 
   * @return		the info tool
   */
  public abstract CursorTool getInfoTool();
  
  /**
   * Returns whether a database connection is required.
   * 
   * @return		true if connection required
   */
  public boolean requiresDatabaseConnection();
  
  /**
   * Updates, if necessary, its database connection using the specified 
   * actor as starting point.
   * 
   * @param actor	the actor to use for lookin up database connection
   */
  public void updateDatabaseConnection(Actor actor);

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp();
}
