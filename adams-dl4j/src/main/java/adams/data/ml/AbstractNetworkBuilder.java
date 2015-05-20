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
import org.deeplearning4j.nn.NeuralNetwork.LossFunction;

import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
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
  extends AbstractOptionHandler
  implements ShallowCopySupporter<AbstractNetworkBuilder<B, N>> {

  /** for serialization. */
  private static final long serialVersionUID = 3279279215595856579L;

  /** how to initialize the weights. */
  protected WeightInitialization m_WeightInit;

  /** the random number generator. */
  protected CommonsRandomNumberGenerator m_RandomNumberGenerator;

  /** the momentum. */
  protected double m_Momentum;

  /** the learning rate. */
  protected double m_LearningRate;

  /** the number of eqochs. */
  protected int m_NumEpochs;

  /** the distribution. */
  protected AbstractRealDistribution m_Distribution;

  /** the loss function. */
  protected LossFunction m_LossFunction;
  
  /** the optimization algorithm. */
  protected OptimizationAlgorithm m_OptimizationAlgorithm;

  /** the l2. */
  protected double m_L2;

  /** whether to use regularization. */
  protected boolean m_UseRegularization;

  /** the sparsity. */
  protected double m_Sparsity;

  /** the drop out. */
  protected double m_DropOut;
  
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
	    "learning-rate", "learningRate",
	    getDefaultLearningRate());

    m_OptionManager.add(
	    "num-epochs", "numEpochs",
	    getDefaultNumEpochs());

    m_OptionManager.add(
	    "distribution", "distribution",
	    new Normal());

    m_OptionManager.add(
	    "loss-function", "lossFunction",
	    getDefaultLossFunction());

    m_OptionManager.add(
	    "optimization-algorithm", "optimizationAlgorithm",
	    OptimizationAlgorithm.NONE);

    m_OptionManager.add(
	    "l2", "L2",
	    getDefaultL2());

    m_OptionManager.add(
	    "use-regularization", "useRegularization",
	    false);

    m_OptionManager.add(
	    "sparsity", "sparsity",
	    0.0);

    m_OptionManager.add(
	    "drop-out", "dropOut",
	    0.0);
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
   * Returns the default learning rate.
   *
   * @return		the default
   */
  protected abstract double getDefaultLearningRate();

  /**
   * Sets the learning rate.
   *
   * @param value	the learning rate
   */
  public void setLearningRate(double value) {
    m_LearningRate = value;
    reset();
  }

  /**
   * Returns the learning rate.
   *
   * @return		the learning rate
   */
  public double getLearningRate() {
    return m_LearningRate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String learningRateTipText() {
    return "The learning rate.";
  }

  /**
   * Returns the default number of iterations.
   *
   * @return		the default
   */
  protected abstract int getDefaultNumEpochs();

  /**
   * Sets the number of iterations.
   *
   * @param value	the number of iterations
   */
  public void setNumEpochs(int value) {
    m_NumEpochs = value;
    reset();
  }

  /**
   * Returns the number of iterations.
   *
   * @return		the number of iterations
   */
  public int getNumEpochs() {
    return m_NumEpochs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numEpochsTipText() {
    return "The number of iterations to perform.";
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
   * Returns the default loss function.
   * 
   * @return		the loss function
   */
  protected abstract LossFunction getDefaultLossFunction();
  
  /**
   * Sets the loss function.
   *
   * @param value	the loss function
   */
  public void setLossFunction(LossFunction value) {
    m_LossFunction = value;
    reset();
  }

  /**
   * Returns the loss function.
   *
   * @return		the loss function
   */
  public LossFunction getLossFunction() {
    return m_LossFunction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lossFunctionTipText() {
    return "The loss function.";
  }

  /**
   * Sets the optimization algorithm.
   *
   * @param value	the optimization algorithm
   */
  public void setOptimizationAlgorithm(OptimizationAlgorithm value) {
    m_OptimizationAlgorithm = value;
    reset();
  }

  /**
   * Returns the optimization algorithm.
   *
   * @return		the algorithm
   */
  public OptimizationAlgorithm getOptimizationAlgorithm() {
    return m_OptimizationAlgorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optimizationAlgorithmTipText() {
    return "The optimization algorithm.";
  }

  /**
   * Returns the default l2.
   *
   * @return		the default
   */
  protected abstract double getDefaultL2();

  /**
   * Sets the l2.
   *
   * @param value	the l2
   */
  public void setL2(double value) {
    m_L2 = value;
    reset();
  }

  /**
   * Returns the l2.
   *
   * @return		the l2
   */
  public double getL2() {
    return m_L2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String L2TipText() {
    return "The l2 parameter.";
  }

  /**
   * Sets whether to use regularization.
   *
   * @param value	true if to use regularization
   */
  public void setUseRegularization(boolean value) {
    m_UseRegularization = value;
    reset();
  }

  /**
   * Returns whether to use regularization.
   *
   * @return		true if to use regularization
   */
  public boolean getUseRegularization() {
    return m_UseRegularization;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useRegularizationTipText() {
    return "Whether to use regularization.";
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
   * Sets the drop out.
   *
   * @param value	the drop out
   */
  public void setDropOut(double value) {
    m_DropOut = value;
    reset();
  }

  /**
   * Returns the drop out.
   *
   * @return		the drop out
   */
  public double getDropOut() {
    return m_DropOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dropOutTipText() {
    return "The drop out.";
  }

  /**
   * Performs checks before configuring the builder.
   * <br><br>
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

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractNetworkBuilder<B, N> shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractNetworkBuilder<B, N> shallowCopy(boolean expand) {
    return (AbstractNetworkBuilder<B, N>) OptionUtils.shallowCopy(this, expand);
  }
}
