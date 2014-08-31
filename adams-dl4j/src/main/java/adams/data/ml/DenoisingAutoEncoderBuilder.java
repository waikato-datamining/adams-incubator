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
 * DenoisingAutoEncoderBuilder.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.ml;

import org.deeplearning4j.da.DenoisingAutoEncoder;
import org.deeplearning4j.da.DenoisingAutoEncoder.Builder;
import org.deeplearning4j.datasets.DataSet;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DenoisingAutoEncoderBuilder
  extends AbstractBaseNetworkBuilder<DenoisingAutoEncoder> {

  /** for serialization. */
  private static final long serialVersionUID = 8804661387146021377L;

  /** the corruption level. */
  protected double m_CorruptionLevel;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Builder for denoising Autoencoder.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "corruption-level", "corruptionLevel",
	    0.01);
  }

  /**
   * Sets the corruption level.
   *
   * @param value	the level
   */
  public void setCorruptionLevel(double value) {
    m_CorruptionLevel = value;
    reset();
  }

  /**
   * Returns the corruption level.
   *
   * @return		the level
   */
  public double getCorruptionLevel() {
    return m_CorruptionLevel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String corruptionLevelTipText() {
    return "The corruption level.";
  }

  /**
   * Generates a new builder instance.
   * 
   * @return		the builder instance
   */
  @Override
  protected Builder newBuilder() {
    return new Builder();
  }
  
  /**
   * Performs the actual training of the network.
   * 
   * @param network	the network to train
   * @param data	the data to train with
   * @return		the trained network
   */
  @Override
  protected DenoisingAutoEncoder doTrainNetwork(DenoisingAutoEncoder network, DataSet data) {
    network.setInput(data.getFeatureMatrix());
    network.trainTillConvergence(data.getFeatureMatrix(), m_LearningRate, new Object[]{m_CorruptionLevel, m_LearningRate, m_NumEpochs});
    
    return network;
  }
  
  /**
   * Returns the class of network that gets generated by the builder.
   * 
   * @return		the network class
   */
  @Override
  public Class generates() {
    return DenoisingAutoEncoder.class;
  }
}