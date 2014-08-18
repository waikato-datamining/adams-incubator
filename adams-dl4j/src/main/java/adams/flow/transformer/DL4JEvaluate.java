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
 * DL4JEvaluate.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.deeplearning4j.datasets.DataSet;
import org.deeplearning4j.dbn.DBN;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.activation.Activations;
import org.jblas.DoubleMatrix;

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
public class DL4JEvaluate
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5699857965056336060L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{DataSet.class};
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
    RandomGenerator rng = new MersenneTwister(123);
    int[] hiddenLayerSizes = new int[] {3};
    DataSet data = (DataSet) m_InputToken.getPayload();
    DBN.Builder builder = new DBN.Builder();
    builder.numberOfInputs(data.getFirst().getColumns());
    builder.numberOfOutPuts(data.getSecond().getColumns());
    builder.withOutputActivationFunction(Activations.softMaxRows());
    builder.hiddenLayerSizes(hiddenLayerSizes);
    builder.withRng(rng);
    DBN dbn = builder.build();
    data.shuffle();
    data.scale();
    dbn.setInput(data.getFeatureMatrix());
    dbn.pretrain(data.getFirst(),1,1e-1,100);
    dbn.finetune(data.getSecond(),1e-1,100);
    Evaluation eval = new Evaluation();
    DoubleMatrix predict = dbn.output(data.getFirst());
    eval.eval(predict,data.getSecond());
    m_OutputToken = new Token(eval);
    return null;
  }
}
