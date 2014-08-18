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

import gnu.trove.list.array.TIntArrayList;

import java.util.HashMap;
import java.util.List;

import org.deeplearning4j.datasets.DataSet;
import org.jblas.DoubleMatrix;

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
    int					i;
    int					n;
    DoubleMatrix			inputMatrix;
    DoubleMatrix			outputMatrix;
    List<String>			labels;
    HashMap<String,Integer>		labelLookup;
    TIntArrayList			inputCols;
    
    // input matrix
    inputCols = new TIntArrayList();
    for (i = 0; i < sheet.getColumnCount(); i++) {
      if (i == classIndex)
	continue;
      if (sheet.isNumeric(i))
	inputCols.add(i);
      else
	System.err.println("Skipping column #" + (i+1) + ": " + sheet.getColumnName(i));
    }
    if (inputCols.size() == 0)
      throw new IllegalStateException("No numeric columns found!");
    inputMatrix = new DoubleMatrix(sheet.getRowCount(), inputCols.size());
    for (n = 0; n < sheet.getRowCount(); n++) {
      for (i = 0; i < inputCols.size(); i++) {
	inputMatrix.put(n, inputCols.get(i), sheet.getRow(n).getCell(inputCols.get(i)).toDouble());
      }
    }
    
    // output matrix
    if (sheet.isNumeric(classIndex)) {
      outputMatrix = new DoubleMatrix(sheet.getRowCount(), 1);
      for (n = 0; n < sheet.getRowCount(); n++) {
	outputMatrix.put(n, 0, sheet.getRow(n).getCell(classIndex).toDouble());
      }
    }
    else {
      labels = sheet.getCellValues(classIndex);
      labelLookup = new HashMap<String,Integer>();
      for (i = 0; i < labels.size(); i++)
	labelLookup.put(labels.get(i), i);
      outputMatrix = new DoubleMatrix(sheet.getRowCount(), labels.size());
      for (n = 0; n < sheet.getRowCount(); n++) {
	outputMatrix.put(n, labelLookup.get(sheet.getRow(n).getContent(classIndex)), 1.0);
      }
    }
    
    return new DataSet(inputMatrix, outputMatrix);
  }
}
