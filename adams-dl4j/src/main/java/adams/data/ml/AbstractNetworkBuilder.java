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
 * AbstractNetworkBuilder.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.ml;

import org.deeplearning4j.datasets.DataSet;

import adams.core.option.AbstractOptionHandler;
import adams.data.distribution.AbstractRealDistribution;
import adams.data.distribution.Normal;
import adams.data.random.CommonsRandomNumberGenerator;
import adams.data.random.MersenneTwister;

/**
 * Ancestor for network builder setups.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <B> the type of builder to configure
 * @param <N> the generated network
 */
public abstract class AbstractNetworkBuilder<B, N>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3279279215595856579L;

  /** how to initialize the weights. */
  protected WeightInitialization m_WeightInit;

  /** the random number generator. */
  protected CommonsRandomNumberGenerator m_RandomNumberGenerator;

  /** the momentum. */
  protected double m_Momentum;

  /** the distribution. */
  protected AbstractRealDistribution m_Distribution;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "random-number-generator", "randomNumberGenerator",
	    new MersenneTwister());

    m_OptionManager.add(
	    "weight-init", "weightInit",
	    WeightInitialization.NONE);

    m_OptionManager.add(
	    "momentum", "momentum",
	    getDefaultMomentum());

    m_OptionManager.add(
	    "distribution", "distribution",
	    new Normal());
  }

  /**
   * Sets the random number generator.
   *
   * @param value	the generator
   */
  public void setRandomNumberGenerator(CommonsRandomNumberGenerator value) {
    m_RandomNumberGenerator = value;
    reset();
  }

  /**
   * Returns the random number generator.
   *
   * @return		the generator
   */
  public CommonsRandomNumberGenerator getRandomNumberGenerator() {
    return m_RandomNumberGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String randomNumberGeneratorTipText() {
    return "The random number generator to use.";
  }

  /**
   * Sets the weight initialization.
   *
   * @param value	the weight initialization
   */
  public void setWeightInit(WeightInitialization value) {
    m_WeightInit = value;
    reset();
  }

  /**
   * Returns the weight initialization.
   *
   * @return		the weight initialization
   */
  public WeightInitialization getWeightInit() {
    return m_WeightInit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String weightInitTipText() {
    return "The weight initialization.";
  }

  /**
   * Returns the default momentum.
   *
   * @return		the default
   */
  protected abstract double getDefaultMomentum();

  /**
   * Sets the momentum.
   *
   * @param value	the momentum
   */
  public void setMomentum(double value) {
    m_Momentum = value;
    reset();
  }

  /**
   * Returns the momentum.
   *
   * @return		the momentum
   */
  public double getMomentum() {
    return m_Momentum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String momentumTipText() {
    return "The momentum.";
  }

  /**
   * Sets the distribution.
   *
   * @param value	the distribution
   */
  public void setDistribution(AbstractRealDistribution value) {
    m_Distribution = value;
    reset();
  }

  /**
   * Returns the distribution.
   *
   * @return		the distribution
   */
  public AbstractRealDistribution getDistribution() {
    return m_Distribution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String distributionTipText() {
    return "The distribution to use.";
  }

  /**
   * Performs checks before configuring the builder.
   * <p/>
   * Default implementation does nothing.
   *
   * @param data	the data to train with
   */
  protected void check(DataSet data) {
  }

  /**
   * Generates a new builder instance.
   *
   * @return		the builder instance
   */
  protected abstract B newBuilder();

  /**
   * Performs the actual configuration.
   *
   * @param data	the data to train with
   * @return		the configured builder
   */
  protected abstract B doConfigureNetwork(DataSet data);

  /**
   * Configures the builder.
   *
   * @param data	the data to train with
   * @return		the builder
   */
  public B configureNetwork(DataSet data) {
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
  protected abstract N doGenerateNetwork(B builder, DataSet data);

  /**
   * Generates the network.
   *
   * @param data	the data to train with
   * @return		the generated network
   */
  public N generateNetwork(DataSet data) {
    return doGenerateNetwork(configureNetwork(data), data);
  }

  /**
   * Performs the actual training of the network.
   *
   * @param network	the network to train
   * @param data	the data to train with
   * @return		the trained network
   */
  protected abstract N doTrainNetwork(N network, DataSet data);

  /**
   * Trains the network.
   *
   * @param data	the data to train with
   * @return		the trained network
   */
  public N trainNetwork(DataSet data) {
    return doTrainNetwork(generateNetwork(data), data);
  }

  /**
   * Returns the class of network that gets generated by the builder.
   *
   * @return		the network class
   */
  public abstract Class generates();
}
