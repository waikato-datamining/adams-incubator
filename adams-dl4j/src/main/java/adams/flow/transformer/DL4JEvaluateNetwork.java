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

import adams.core.QuickInfoHelper;
import adams.flow.container.DL4JModelContainer;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.OutputProducer;
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

  /** the callable actor for obtaining the test set. */
  protected CallableActorReference m_Test;

  /** the helper class. */
  protected CallableActorHelper m_Helper;
  
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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "test", "test",
	    new CallableActorReference("test"));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Sets the name of the callable actor providing the test data.
   *
   * @param value	the name
   */
  public void setTest(CallableActorReference value) {
    m_Test = value;
    reset();
  }

  /**
   * Returns the name of the callable actor providing the test data.
   *
   * @return		the name
   */
  public CallableActorReference getTest() {
    return m_Test;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testTipText() {
    return 
	"The name of the callable actor for retrieving the test data; if "
	+ "not available, the training data in the received model container is used.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "test", m_Test);
    
    return result;
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
    AbstractActor	source;

    result  = null;
    cont    = (DL4JModelContainer) m_InputToken.getPayload();
    model   = cont.getValue(DL4JModelContainer.VALUE_MODEL);
    data    = (DataSet) cont.getValue(DL4JModelContainer.VALUE_DATASET);
    source  = m_Helper.findCallableActorRecursive(this, m_Test);
    if (source != null) {
      if (isLoggingEnabled())
	getLogger().info("Callable actor '" + m_Test + "' found, trying to obtain test data");
      result  = source.execute();
      if (result == null) {
	data = ((DataSet) ((OutputProducer) source).output().getPayload());
	if (data == null)
	  result = "Failed to obtain training data from '" + m_Test + "'!";
	else if (isLoggingEnabled())
	  getLogger().info("Using test data from '" + m_Test + "'");
      }
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Callable actor '" + m_Test + "' not found, using training data for evaluation");
    }
    
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
