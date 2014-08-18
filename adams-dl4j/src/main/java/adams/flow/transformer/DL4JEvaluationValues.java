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
 * DL4JEvaluationValues.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import org.deeplearning4j.eval.Evaluation;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;


/**
 * TODO: what this class does
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JEvaluationValues
  extends AbstractTransformer {

  /* (non-Javadoc)
   * @see adams.core.option.AbstractOptionHandler#globalInfo()
   */
  @Override
  public String globalInfo() {
    // TODO
    return null;
  }

  /* (non-Javadoc)
   * @see adams.flow.core.InputConsumer#accepts()
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Evaluation.class};
  }

  /* (non-Javadoc)
   * @see adams.flow.core.OutputProducer#generates()
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /* (non-Javadoc)
   * @see adams.flow.core.AbstractActor#doExecute()
   */
  @Override
  protected String doExecute() {
    Evaluation	eval;
    SpreadSheet stats;
    Row 	row;
    
    eval  = (Evaluation) m_InputToken.getPayload();
    stats = new SpreadSheet();

    // header
    row = stats.getHeaderRow();
    row.addCell("N").setContent("Name");
    row.addCell("V").setContent("Value");
    
    // data
    row = stats.addRow();
    row.addCell("N").setContent("Accuracy");
    row.addCell("V").setContent(eval.accuracy());
    row = stats.addRow();
    row.addCell("N").setContent("Positive");
    row.addCell("V").setContent(eval.positive());
    row = stats.addRow();
    row.addCell("N").setContent("Negative");
    row.addCell("V").setContent(eval.negative());
    row = stats.addRow();
    row.addCell("N").setContent("Precision");
    row.addCell("V").setContent(eval.precision());
    row = stats.addRow();
    row.addCell("N").setContent("Recall");
    row.addCell("V").setContent(eval.recall());
    row = stats.addRow();
    row.addCell("N").setContent("False positive");
    row.addCell("V").setContent(eval.falsePositive());
    row = stats.addRow();
    row.addCell("N").setContent("True negatives");
    row.addCell("V").setContent(eval.trueNegatives());
    row = stats.addRow();
    row.addCell("N").setContent("F1");
    row.addCell("V").setContent(eval.f1());
    row = stats.addRow();
    row.addCell("N").setContent("Stats");
    row.addCell("V").setContent(eval.stats());

    m_OutputToken = new Token(stats);
    
    return null;
  }
}
