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
 * WeightInitialization.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.ml;

import org.deeplearning4j.nn.WeightInit;

/**
 * Wrapper for the {@link WeightInit} enum.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum WeightInitialization {

  /** none. */
  NONE(null),
  /** variance normalized. */
  VARIANCE_NORMALIZED(WeightInit.VI),
  /** sparse. */
  SPARSE(WeightInit.SI);

  /** the corresponding WeightInit value. */
  private WeightInit m_WeightInit;

  /**
   * Initializes the enum.
   *
   * @param wi		the associated {@link WeightInit} value
   */
  private WeightInitialization(WeightInit wi) {
    m_WeightInit = wi;
  }

  /**
   * Returns the associated {@link WeightInit} value.
   *
   * @return		the initialization
   */
  public WeightInit getWeightInit() {
    return m_WeightInit;
  }
}
