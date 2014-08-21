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
 * Null.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import org.apache.commons.math3.random.RandomGenerator;

/**
 * Dummy random number generator, only used in setup to skip random number 
 * generator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Null
  extends AbstractCommonsRandomNumberGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -4423220255926423993L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy random number generator, only used in setup to skip random number generator.";
  }

  /**
   * Returns the next random number. Does the actual computation.
   *
   * @return		the next number
   */
  @Override
  protected Number doNext() {
    if (m_GenerateDoubles)
      return 1.0;
    else
      return 1;
  }

  /**
   * The underlying random number generator.
   * 
   * @return		always null
   */
  @Override
  public RandomGenerator getRandomGenerator() {
    return null;
  }
}
