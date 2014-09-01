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

import org.deeplearning4j.datasets.DataSet;
import org.jblas.DoubleMatrix;

import adams.data.conversion.SpreadSheetBinarize;
import adams.data.spreadsheet.Row;
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
    if (classes.length < 1)
      throw new IllegalStateException("Requires at least one class attribute!");
    
    return spreadsheetToDataSet(sheet, classes);
  }

  /**
   * Turns a {@link SpreadSheet} into a dl4j {@link DataSet}.
   * 
   * @param sheet	the spreadsheet to convert
   * @param classIndex	the classIndex to use
   * @return		the converted dataset
   */
  public static DataSet spreadsheetToDataSet(SpreadSheet sheet, int[] classIndex) {
    SpreadSheetBinarize	binarize;
    String		msg;
    int			i;
    int			n;
    Dataset		data;
    SpreadSheet		inputs;
    SpreadSheet		outputs;
    DoubleMatrix	inputMatrix;
    DoubleMatrix	outputMatrix;
    
    if (sheet instanceof Dataset)
      data = (Dataset) sheet;
    else
      data = new Dataset(sheet);
    for (int clsIndex: classIndex)
      data.setClassAttribute(clsIndex, true);
    
    inputs  = data.getInputs();
    outputs = data.getOutputs();
    
    // binarize
    binarize = new SpreadSheetBinarize();

    binarize.setInput(inputs);
    msg = binarize.convert();
    if (msg != null)
      throw new IllegalStateException("Failed to binarize inputs: " + msg);
    inputs = (SpreadSheet) binarize.getOutput();

    binarize.setInput(outputs);
    msg = binarize.convert();
    if (msg != null)
      throw new IllegalStateException("Failed to binarize outputs: " + msg);
    outputs = (SpreadSheet) binarize.getOutput();

    binarize.cleanUp();
    
    // input matrix
    inputMatrix = new DoubleMatrix(inputs.getRowCount(), inputs.getColumnCount());
    for (n = 0; n < inputs.getRowCount(); n++) {
      for (i = 0; i < inputs.getColumnCount(); i++) {
	inputMatrix.put(n, i, inputs.getRow(n).getCell(i).toDouble());
      }
    }
    
    // output matrix
    outputMatrix = new DoubleMatrix(outputs.getRowCount(), outputs.getColumnCount());
    for (n = 0; n < outputs.getRowCount(); n++) {
      for (i = 0; i < outputs.getColumnCount(); i++) {
	outputMatrix.put(n, i, outputs.getRow(n).getCell(i).toDouble());
      }
    }
    
    return new DataSet(inputMatrix, outputMatrix);
  }
  
  /**
   * Converts the double matrix into a spreadsheet.
   * 
   * @param matrix	the matrix to convert
   * @return		the spreadsheet 
   */
  public static SpreadSheet doubleMatrixToSpreadSheet(DoubleMatrix matrix) {
    return doubleMatrixToSpreadSheet(matrix, null);
  }
  
  /**
   * Converts the double matrix into a spreadsheet.
   * 
   * @param matrix	the matrix to convert
   * @param name	the name for the spreadsheet
   * @return		the spreadsheet 
   */
  public static SpreadSheet doubleMatrixToSpreadSheet(DoubleMatrix matrix, String name) {
    SpreadSheet	result;
    Row		row;
    int		i;
    int		n;
    
    result = new SpreadSheet();
    result.setName(name);
    
    // header
    row = result.getHeaderRow();
    for (i = 0; i < matrix.getColumns(); i++)
      row.addCell("" + i).setContent("Col-" + (i+1));
    
    // data
    for (n = 0; n < matrix.getRows(); n++) {
      row = result.addRow();
      for (i = 0; i < matrix.getColumns(); i++)
	row.addCell(i).setContent(matrix.get(n, i));
    }
    
    return result;
  }
}
