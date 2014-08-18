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
 * DatasetToDL4JDataset.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import gnu.trove.list.array.TIntArrayList;

import java.util.HashMap;
import java.util.List;

import org.deeplearning4j.datasets.DataSet;
import org.jblas.DoubleMatrix;

/**
 * TODO: what this class does
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetToDL4JDataset
  extends AbstractConversion {

  /* (non-Javadoc)
   * @see adams.core.option.AbstractOptionHandler#globalInfo()
   */
  @Override
  public String globalInfo() {
    return null;
  }

  /* (non-Javadoc)
   * @see adams.data.conversion.AbstractConversion#accepts()
   */
  @Override
  public Class accepts() {
    return adams.ml.data.Dataset.class;
  }

  /* (non-Javadoc)
   * @see adams.data.conversion.AbstractConversion#generates()
   */
  @Override
  public Class generates() {
    return org.deeplearning4j.datasets.DataSet.class;
  }

  /* (non-Javadoc)
   * @see adams.data.conversion.AbstractConversion#doConvert()
   */
  @Override
  protected Object doConvert() throws Exception {
    adams.ml.data.Dataset		input;
    org.deeplearning4j.datasets.DataSet	output;
    int[]				classes;
    int					i;
    int					n;
    DoubleMatrix			inputMatrix;
    DoubleMatrix			outputMatrix;
    List<String>			labels;
    HashMap<String,Integer>		labelLookup;
    TIntArrayList			inputCols;
    
    input   = (adams.ml.data.Dataset) m_Input;
    classes = input.getClassAttributeIndices();
    if (classes.length != 1)
      throw new IllegalStateException("Requires one class attribute, found: " + classes.length);
    
    // input matrix
    inputCols = new TIntArrayList();
    for (i = 0; i < input.getColumnCount(); i++) {
      if (i == classes[0])
	continue;
      if (input.isNumeric(i))
	inputCols.add(i);
      else if (isLoggingEnabled())
	getLogger().fine("Skipping column #" + (i+1) + ": " + input.getColumnName(i));
    }
    if (inputCols.size() == 0)
      return new IllegalStateException("No numeric columns found!");
    inputMatrix = new DoubleMatrix(input.getRowCount(), inputCols.size());
    for (n = 0; n < input.getRowCount(); n++) {
      for (i = 0; i < inputCols.size(); i++) {
	inputMatrix.put(n, inputCols.get(i), input.getRow(n).getCell(inputCols.get(i)).toDouble());
      }
    }
    
    // output matrix
    if (input.isNumeric(classes[0])) {
      outputMatrix = new DoubleMatrix(input.getRowCount(), 1);
      for (n = 0; n < input.getRowCount(); n++) {
	outputMatrix.put(n, 0, input.getRow(n).getCell(classes[0]).toDouble());
      }
    }
    else {
      labels = input.getCellValues(classes[0]);
      labelLookup = new HashMap<String,Integer>();
      for (i = 0; i < labels.size(); i++)
	labelLookup.put(labels.get(i), i);
      outputMatrix = new DoubleMatrix(input.getRowCount(), labels.size());
      for (n = 0; n < input.getRowCount(); n++) {
	outputMatrix.put(n, labelLookup.get(input.getRow(n).getContent(classes[0])), 1.0);
      }
    }
    
    return new DataSet(inputMatrix, outputMatrix);
  }
}
