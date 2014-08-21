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
 * AbstractMultiLayerNetworkBuilder.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.nnbuilder;

import org.deeplearning4j.nn.BaseMultiLayerNetwork;
import org.deeplearning4j.nn.BaseMultiLayerNetwork.Builder;
import org.deeplearning4j.nn.activation.ActivationFunction;
import org.deeplearning4j.nn.activation.Null;

import adams.core.base.BaseInteger;

/**
 * Ancestor for multi-layer neural network builder setups.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of builder to configure
 */
public abstract class AbstractMultiLayerNetworkBuilder<T extends BaseMultiLayerNetwork>
  extends AbstractNetworkBuilder<Builder<T>, T> {

  /** for serialization. */
  private static final long serialVersionUID = 3279279215595856579L;

  /** the sizes of the hidden layers. */
  protected BaseInteger[] m_HiddenLayerSizes;
  
  /** the activation function. */
  protected ActivationFunction m_ActivationFunction;
  
  /** the output activation function. */
  protected ActivationFunction m_OutputActivationFunction;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "hidden-layer-sizes", "hiddenLayerSizes",
	    new BaseInteger[]{new BaseInteger(3)});

    m_OptionManager.add(
	    "activation-function", "activationFunction",
	    new Null());

    m_OptionManager.add(
	    "output-activation-function", "outputActivationFunction",
	    new Null());
  }

  /**
   * Sets the hidden layer sizes.
   *
   * @param value	the layers
   */
  public void setHiddenLayerSizes(BaseInteger[] value) {
    m_HiddenLayerSizes = value;
    reset();
  }

  /**
   * Returns the hidden layer sizes.
   *
   * @return		the layers
   */
  public BaseInteger[] getHiddenLayerSizes() {
    return m_HiddenLayerSizes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hiddenLayerSizesTipText() {
    return "The hidden layer sizes; at least one layer and each layer must have at least 1 node.";
  }

  /**
   * Sets the activation function.
   *
   * @param value	the activation function
   */
  public void setActivationFunction(ActivationFunction value) {
    m_ActivationFunction = value;
    reset();
  }

  /**
   * Returns the activation function.
   *
   * @return		the activation function
   */
  public ActivationFunction getActivationFunction() {
    return m_ActivationFunction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String activationFunctionTipText() {
    return "The activation function to use.";
  }

  /**
   * Sets the output activation function.
   *
   * @param value	the output activation function
   */
  public void setOutputActivationFunction(ActivationFunction value) {
    m_OutputActivationFunction = value;
    reset();
  }

  /**
   * Returns the output activation function.
   *
   * @return		the output activation function
   */
  public ActivationFunction getOutputActivationFunction() {
    return m_OutputActivationFunction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputActivationFunctionTipText() {
    return "The output activation function to use.";
  }
  
  /**
   * Convencience method for turning the hidden layer sizes into an integer array.
   * 
   * @return		the hidden layer sizes as int array
   */
  protected int[] hiddenLayerSizes() {
    int[]	result;
    int		i;
    
    result = new int[m_HiddenLayerSizes.length];
    for (i = 0; i < result.length; i++)
      result[i] = m_HiddenLayerSizes[i].intValue();
    
    return result;
  }

  /**
   * Performs checks before configuring the builder.
   * <p/>
   * Default implementation ensures that at least one hidden layer is defined
   * and that the hidden layers have at least 1 node each.
   */
  @Override
  protected void check() {
    int		i;
    
    if (m_HiddenLayerSizes.length == 0)
      throw new IllegalStateException("No hidden layers defined");
    
    for (i = 0; i < m_HiddenLayerSizes.length; i++) {
      if (m_HiddenLayerSizes[i].intValue() < 1)
	throw new IllegalStateException("Hidden layer #" + (i+1) + " has less than 1 node!");
    }
  }

  /**
   * Performs the actual configuration.
   * 
   * @return		the configured builder
   */
  @Override
  protected Builder<T> doConfigureNetwork() {
    Builder<T>	result;
    
    result = newBuilder();

    result.hiddenLayerSizes(hiddenLayerSizes());
    
    if (!(m_ActivationFunction instanceof org.deeplearning4j.nn.activation.Null))
      result.withActivation(m_ActivationFunction);
    
    if (!(m_OutputActivationFunction instanceof org.deeplearning4j.nn.activation.Null))
      result.withOutputActivationFunction(m_OutputActivationFunction);
    
    if (!(m_RandomNumberGenerator instanceof adams.data.random.Null))
      result.withRng(m_RandomNumberGenerator.getRandomGenerator());
    
    // TODO parameters
    
    return result;
  }
  
  /**
   * Configures the builder.
   * 
   * @return		the builder
   */
  @Override
  public Builder<T> configureNetwork() {
    check();
    return doConfigureNetwork();
  }
  
  /**
   * Performs the actual generation of the network.
   * 
   * @param builder	the builder to use
   * @return		the generated network
   */
  @Override
  protected T doGenerateNetwork(Builder<T> builder) {
    return builder.build();
  }
}
