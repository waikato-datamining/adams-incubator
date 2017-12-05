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
 * AbstractInfoToolSupplier.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.infotool;

import adams.core.CleanUpHandler;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.db.DatabaseConnectionUser;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.gui.visualization.maps.MapDisplayPanel;
import org.geotools.swing.tool.CursorTool;

/**
 * Ancestor for classes that provide {@link CursorTool} instances used in the
 * {@link MapDisplayPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInfoToolSupplier
  extends AbstractOptionHandler
  implements InfoToolSupplier, DatabaseConnectionUser {

  /** for serialization. */
  private static final long serialVersionUID = -8529811448306458643L;

  /** the database connection in use. */
  protected transient adams.db.AbstractDatabaseConnection m_DatabaseConnection;
  
  /** the info tool in use. */
  protected transient CursorTool m_CurrentTool;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentTool = null;
  }
  
  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();

    if ((m_CurrentTool != null) && (m_CurrentTool instanceof CleanUpHandler))
      ((CleanUpHandler) m_CurrentTool).cleanUp();
    m_CurrentTool = null;
  }
  
  /**
   * Returns a new {@link CursorTool} instance.
   * 
   * @return		the tool
   */
  protected abstract CursorTool newInfoTool();
  
  /**
   * Returns the {@link CursorTool} instance to use.
   * 
   * @return		the tool
   */
  @Override
  public CursorTool getInfoTool() {
    if (m_CurrentTool == null)
      m_CurrentTool = newInfoTool();
    return m_CurrentTool;
  }
  
  /**
   * Returns whether a database connection is required.
   * 
   * @return		true if connection required
   */
  public abstract boolean requiresDatabaseConnection();

  /**
   * Determines the database connection in the flow.
   * <br><br>
   * Derived classes can override this method if different database
   * connection objects need to be located.
   *
   * @param actor	the actor to use for lookin up database connection
   * @return		the database connection to use
   */
  protected adams.db.AbstractDatabaseConnection getDatabaseConnection(Actor actor) {
    return ActorUtils.getDatabaseConnection(
	  actor,
	  adams.flow.standalone.DatabaseConnectionProvider.class,
	  adams.db.DatabaseConnection.getSingleton());
  }

  /**
   * Updates, if necessary, its database connection using the specified 
   * actor as starting point.
   * 
   * @param actor	the actor to use for lookin up database connection
   * @see		#getDatabaseConnection(Actor)
   */
  public void updateDatabaseConnection(Actor actor) {
    m_DatabaseConnection = getDatabaseConnection(actor);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the shallow copy
   */
  @Override
  public AbstractInfoToolSupplier shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  @Override
  public AbstractInfoToolSupplier shallowCopy(boolean expand) {
    return (AbstractInfoToolSupplier) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_CurrentTool != null) {
      if (m_CurrentTool instanceof CleanUpHandler)
	((CleanUpHandler) m_CurrentTool).cleanUp();
      m_CurrentTool = null;
    }
  }
}
