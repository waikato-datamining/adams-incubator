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
 * AbstractMongoDbBooleanCondition.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.condition.bool;

import adams.db.MongoDbConnection;
import adams.flow.core.Actor;
import adams.flow.core.MongoDbActorUtils;

/**
 * Ancestor for MongoDB conditions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMongoDbBooleanCondition
  extends AbstractBooleanCondition {

  private static final long serialVersionUID = -2563430699960393494L;

  /** the database connection. */
  protected MongoDbConnection m_DatabaseConnection;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = null;
  }

  /**
   * Resets the converter.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DatabaseConnection = null;
  }

  /**
   * Returns the default database connection.
   *
   * @return 		the default database connection
   */
  protected MongoDbConnection getDefaultDatabaseConnection() {
    return MongoDbConnection.getSingleton();
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  public String setUp(Actor owner) {
    String	result;

    result = setUp(owner);

    if (result == null) {
      m_DatabaseConnection = MongoDbActorUtils.getDatabaseConnection(
	owner, adams.flow.standalone.DatabaseConnectionProvider.class, getDefaultDatabaseConnection());
    }

    return result;
  }
}
