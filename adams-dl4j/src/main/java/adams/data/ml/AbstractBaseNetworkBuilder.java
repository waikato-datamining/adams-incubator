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
 * AbstractNeuralNetworkBuilder.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.ml;

import org.deeplearning4j.datasets.DataSet;
import org.deeplearning4j.nn.BaseNeuralNetwork;
import org.deeplearning4j.nn.BaseNeuralNetwork.Builder;

/**
 * Ancestor for neural network builder setups.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of builder to configure
 */
public abstract class AbstractBaseNetworkBuilder<T extends BaseNeuralNetwork>
  extends AbstractNetworkBuilder<Builder<T>, T> {

  /** for serialization. */
  private static final long serialVersionUID = 3279279215595856579L;

  /** the number of hidden neurons. */
  protected int m_NumHidden;

  /** whether to apply sparsity. */
  protected boolean m_ApplySparsity;

  /** the sparsity. */
  protected double m_Sparsity;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-hidden", "numHidden",
	    1, 1, null);

    m_OptionManager.add(
	    "apply-sparsity", "applySparsity",
	    false);

    m_OptionManager.add(
	    "sparsity", "sparsity",
	    0);
  }

  /**
   * Returns the default momentum.
   *
   * @return		the default
   */
  @Override
  protected double getDefaultMomentum() {
    return 0.5;
  }

  /**
   * Sets the number of hidden neurons.
   *
   * @param value	the number
   */
  public void setNumHidden(int value) {
    m_NumHidden = value;
    reset();
  }

  /**
   * Returns the number of hidden neurons.
   *
   * @return		the number
   */
  public int getNumHidden() {
    return m_NumHidden;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numHiddenTipText() {
    return "The number of hidden neurons.";
  }

  /**
   * Sets whether to apply sparsity.
   *
   * @param value	true if to apply sparsity
   */
  public void setApplySparsity(boolean value) {
    m_ApplySparsity = value;
    reset();
  }

  /**
   * Returns whether to apply sparsity.
   *
   * @return		true if to apply sparsity
   */
  public boolean getApplySparsity() {
    return m_ApplySparsity;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String applySparsityTipText() {
    return "Whether to apply sparsity.";
  }

  /**
   * Sets the sparsity.
   *
   * @param value	the sparsity
   */
  public void setSparsity(double value) {
    m_Sparsity = value;
    reset();
  }

  /**
   * Returns the sparsity.
   *
   * @return		the sparsity
   */
  public double getSparsity() {
    return m_Sparsity;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sparsityTipText() {
    return "The sparsity.";
  }

  /**
   * Performs the actual configuration.
   *
   * @param data	the data to train with
   * @return		the configured builder
   */
  @Override
  protected Builder<T> doConfigureNetwork(DataSet data) {
    Builder<T>	result;

    result = newBuilder();

    // general
    if (!(m_RandomNumberGenerator instanceof adams.data.random.Null))
      result.withRandom(m_RandomNumberGenerator.getRandomGenerator());
    result.applySparsity(m_ApplySparsity);
    result.withSparsity(m_Sparsity);
    result.withDistribution(m_Distribution.getRealDistribution());

    // input
    result.numberOfVisible(data.getFirst().getColumns());

    // hidden
    result.numHidden(m_NumHidden);


    // TODO parameters

    return result;
  }

  /**
   * Configures the builder.
   *
   * @param data	the data to train with
   * @return		the builder
   */
  @Override
  public Builder<T> configureNetwork(DataSet data) {
    check(data);
    return doConfigureNetwork(data);
  }

  /**
   * Performs the actual generation of the network.
   *
   * @param builder	the builder to use
   * @param data	the data to train with
   * @return		the generated network
   */
  @Override
  protected T doGenerateNetwork(Builder<T> builder, DataSet data) {
    return builder.build();
  }
}
