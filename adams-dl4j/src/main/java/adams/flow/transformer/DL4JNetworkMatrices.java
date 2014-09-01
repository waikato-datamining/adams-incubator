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
 * DL4JNetworkMatrices.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import org.deeplearning4j.nn.NeuralNetwork;
import org.deeplearning4j.nn.gradient.NeuralNetworkGradient;

import adams.data.ml.AbstractBaseNetworkBuilder;
import adams.data.ml.DL4JHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.DL4JModelContainer;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JNetworkMatrices
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -5699857965056336060L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Extracts the matrices (W, hBias, vBias and associated gradients) "
	+ "from the trained network.\n"
	+ "NB: the network must be a " + NeuralNetwork.class.getName() + ".";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Output the matrices as array rather than one-by-one";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{DL4JModelContainer.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return SpreadSheet.class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    DL4JModelContainer		cont;
    NeuralNetwork		network;
    AbstractBaseNetworkBuilder	builder;
    NeuralNetworkGradient	gradient;
    
    result  = null;

    cont = (DL4JModelContainer) m_InputToken.getPayload();
    if (cont.getValue(DL4JModelContainer.VALUE_MODEL) instanceof NeuralNetwork) {
      network  = (NeuralNetwork) cont.getValue(DL4JModelContainer.VALUE_MODEL);
      builder  = (AbstractBaseNetworkBuilder) cont.getValue(DL4JModelContainer.VALUE_BUILDER);
      m_Queue.add(DL4JHelper.doubleMatrixToSpreadSheet(network.getW(), "W"));
      m_Queue.add(DL4JHelper.doubleMatrixToSpreadSheet(network.gethBias(), "hBias"));
      m_Queue.add(DL4JHelper.doubleMatrixToSpreadSheet(network.getvBias(), "vBias"));
      if (builder != null) {
	gradient = builder.getGradient(network);
	m_Queue.add(DL4JHelper.doubleMatrixToSpreadSheet(gradient.getwGradient(), "W-Gradient"));
	m_Queue.add(DL4JHelper.doubleMatrixToSpreadSheet(gradient.gethBiasGradient(), "hBias-Gradient"));
	m_Queue.add(DL4JHelper.doubleMatrixToSpreadSheet(gradient.getvBiasGradient(), "vBias-Gradient"));
      }
    }

    return result;
  }
}
