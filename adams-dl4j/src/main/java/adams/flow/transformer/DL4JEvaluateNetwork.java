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
 * DL4JEvaluateNetwork.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import org.deeplearning4j.datasets.DataSet;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.BaseMultiLayerNetwork;
import org.deeplearning4j.nn.BaseNeuralNetwork;
import org.jblas.DoubleMatrix;

import adams.flow.container.DL4JModelContainer;
import adams.flow.core.Token;

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
public class DL4JEvaluateNetwork
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -7213972179879592883L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Evaluates the model from the container on the dataset in the container.";
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
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Evaluation.class};
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    DL4JModelContainer	cont;
    Evaluation		eval;
    DoubleMatrix 	predict;
    Object		model;
    DataSet		data;

    result  = null;
    cont    = (DL4JModelContainer) m_InputToken.getPayload();
    model   = cont.getValue(DL4JModelContainer.VALUE_MODEL);
    data    = (DataSet) cont.getValue(DL4JModelContainer.VALUE_DATASET);
    eval    = new Evaluation();
    predict = null;
    if (model instanceof BaseNeuralNetwork)
      predict = ((BaseNeuralNetwork) model).output(data.getFirst());
    else if (model instanceof BaseMultiLayerNetwork)
      predict = ((BaseMultiLayerNetwork) model).output(data.getFirst());
    else
      result = "Unhandled network type: " + model.getClass().getName();
    
    if (result == null) {
      eval.eval(predict, data.getSecond());
      m_OutputToken = new Token(eval);
    }
    
    return null;
  }
}
