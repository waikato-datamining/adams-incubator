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
 * DL4JEvaluationValue.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import org.deeplearning4j.eval.Evaluation;

import adams.core.QuickInfoHelper;
import adams.data.ml.DBNBuilder;
import adams.flow.core.DL4JEvaluationStatistic;
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
public class DL4JEvaluationValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -7213972179879592883L;

  /** the value to pick. */
  protected DL4JEvaluationStatistic m_Statistic;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Retrieves the specified evaluation statistic.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "builder", "builder",
	    new DBNBuilder());
  }

  /**
   * Sets the statistic.
   *
   * @param value	the statistic
   */
  public void setStatistic(DL4JEvaluationStatistic value) {
    m_Statistic = value;
    reset();
  }

  /**
   * Returns the statistic.
   *
   * @return		the statistic
   */
  public DL4JEvaluationStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticTipText() {
    return "The statistic to retrieve.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "statistic", m_Statistic);
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Evaluation.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_Statistic) {
      case STATS:
	return new Class[]{String.class};
      case ACCURACY:
      case F1:
      case FALSE_POSITIVE:
      case TRUE_NEGATIVES:
      case NEGATIVE:
      case POSITIVE:
      case PRECISION:
      case RECALL:
	return new Class[]{Double.class};
      default:
	throw new IllegalStateException("Unhandled statistic: " + m_Statistic);
    }
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    Evaluation	eval;
    
    eval = (Evaluation) m_InputToken.getPayload();

    switch (m_Statistic) {
      case STATS:
	m_OutputToken = new Token(eval.stats());
	break;
      case ACCURACY:
	m_OutputToken = new Token(eval.accuracy());
	break;
      case F1:
	m_OutputToken = new Token(eval.f1());
	break;
      case FALSE_POSITIVE:
	m_OutputToken = new Token(eval.falsePositive());
	break;
      case TRUE_NEGATIVES:
	m_OutputToken = new Token(eval.trueNegatives());
	break;
      case NEGATIVE:
	m_OutputToken = new Token(eval.negative());
	break;
      case POSITIVE:
	m_OutputToken = new Token(eval.positive());
	break;
      case PRECISION:
	m_OutputToken = new Token(eval.precision());
	break;
      case RECALL:
	m_OutputToken = new Token(eval.recall());
	break;
      default:
	throw new IllegalStateException("Unhandled statistic: " + m_Statistic);
    }
    
    return null;
  }
}
