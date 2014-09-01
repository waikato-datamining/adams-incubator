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
 * OptimizationAlgorithm.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.ml;

/**
 * Wrapper for the {@link org.deeplearning4j.nn.NeuralNetwork.OptimizationAlgorithm} enum.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9552 $
 */
public enum OptimizationAlgorithm {

  NONE(null),
  CONJUGATE_GRADIENT(org.deeplearning4j.nn.NeuralNetwork.OptimizationAlgorithm.CONJUGATE_GRADIENT),
  GRADIENT_DESCENT(org.deeplearning4j.nn.NeuralNetwork.OptimizationAlgorithm.GRADIENT_DESCENT),
  HESSIAN_FREE(org.deeplearning4j.nn.NeuralNetwork.OptimizationAlgorithm.HESSIAN_FREE);

  /** the corresponding OptimizationAlgorithm value. */
  private org.deeplearning4j.nn.NeuralNetwork.OptimizationAlgorithm m_OptimizationAlgorithm;

  /**
   * Initializes the enum.
   *
   * @param wi		the associated {@link org.deeplearning4j.nn.NeuralNetwork.OptimizationAlgorithm} value
   */
  private OptimizationAlgorithm(org.deeplearning4j.nn.NeuralNetwork.OptimizationAlgorithm wi) {
    m_OptimizationAlgorithm = wi;
  }

  /**
   * Returns the associated {@link org.deeplearning4j.nn.NeuralNetwork.OptimizationAlgorithm} value.
   *
   * @return		the initialization
   */
  public org.deeplearning4j.nn.NeuralNetwork.OptimizationAlgorithm getOptimizationAlgorithm() {
    return m_OptimizationAlgorithm;
  }
}
