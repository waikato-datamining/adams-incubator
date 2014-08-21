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
package adams.flow.source.nnbuilder;

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

  /**
   * Performs the actual configuration.
   * 
   * @return		the configured builder
   */
  @Override
  protected Builder<T> doConfigureNetwork() {
    Builder<T>	result;
    
    result = newBuilder();
    
    if (!(m_RandomNumberGenerator instanceof adams.data.random.Null))
      result.withRandom(m_RandomNumberGenerator.getRandomGenerator());
    
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
