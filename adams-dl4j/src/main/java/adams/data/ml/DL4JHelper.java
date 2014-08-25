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
 * DL4JHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.ml;

import java.util.List;

import org.deeplearning4j.datasets.DataSet;
import org.jblas.DoubleMatrix;

import adams.data.conversion.SpreadSheetBinarize;
import adams.data.spreadsheet.SpreadSheet;
import adams.ml.data.Dataset;

/**
 * Helper class for deeplearning4j related data operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JHelper {

  /**
   * Turns a {@link Dataset} into a dl4j {@link DataSet}. A single class
   * attribute must be set.
   * 
   * @param sheet	the dataset to convert
   * @return		the converted dataset
   */
  public static DataSet spreadsheetToDataSet(Dataset sheet) {
    int[]	classes;
    
    classes = sheet.getClassAttributeIndices();
    if (classes.length != 1)
      throw new IllegalStateException("Requires one class attribute, found: " + classes.length);
    
    return spreadsheetToDataSet(sheet, classes[0]);
  }

  /**
   * Turns a {@link SpreadSheet} into a dl4j {@link DataSet}.
   * 
   * @param sheet	the spreadsheet to convert
   * @param classIndex	the classIndex to use
   * @return		the converted dataset
   */
  public static DataSet spreadsheetToDataSet(SpreadSheet sheet, int classIndex) {
    String		prefix;
    List<String>	labels;
    SpreadSheetBinarize	binarize;
    String		msg;
    int			i;
    int			n;
    Dataset		data;
    String		label;
    int			index;
    SpreadSheet		inputs;
    SpreadSheet		outputs;
    DoubleMatrix	inputMatrix;
    DoubleMatrix	outputMatrix;
    
    prefix = sheet.getColumnName(classIndex);
    if (sheet.isNumeric(classIndex))
      labels = null;
    else
      labels = sheet.getCellValues(classIndex);
    
    // binarize
    binarize = new SpreadSheetBinarize();
    binarize.setInput(sheet);
    msg = binarize.convert();
    if (msg != null)
      throw new IllegalStateException("Failed to binarize spreadsheet: " + msg);
    sheet = (SpreadSheet) binarize.getOutput();
    if (sheet instanceof Dataset)
      data = (Dataset) sheet;
    else
      data = new Dataset(sheet);
    
    // set class attributes
    if (labels == null) {
      label = prefix;
      index = data.getHeaderRow().indexOfContent(label);
      if (index > -1)
	data.setClassAttribute(index, true);
      else
	System.err.println("Failed to locate: " + label);
    }
    else {
      for (i = 0; i < labels.size(); i++) {
	label = prefix + SpreadSheetBinarize.SEPARATOR + labels.get(i);
	index = data.getHeaderRow().indexOfContent(label);
	if (index > -1)
	  data.setClassAttribute(index, true);
	else
	  System.err.println("Failed to locate: " + label);
      }
    }
    
    // input matrix
    inputs      = data.getInputs();
    inputMatrix = new DoubleMatrix(inputs.getRowCount(), inputs.getColumnCount());
    for (n = 0; n < inputs.getRowCount(); n++) {
      for (i = 0; i < inputs.getColumnCount(); i++) {
	inputMatrix.put(n, i, inputs.getRow(n).getCell(i).toDouble());
      }
    }
    
    // output matrix
    outputs      = data.getOutputs();
    outputMatrix = new DoubleMatrix(outputs.getRowCount(), outputs.getColumnCount());
    for (n = 0; n < outputs.getRowCount(); n++) {
      for (i = 0; i < outputs.getColumnCount(); i++) {
	outputMatrix.put(n, i, outputs.getRow(n).getCell(i).toDouble());
      }
    }
    
    return new DataSet(inputMatrix, outputMatrix);
  }
}
