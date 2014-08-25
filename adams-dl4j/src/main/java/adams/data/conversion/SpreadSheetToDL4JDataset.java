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
 * SpreadSheetToDL4JDataset.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.ml.DL4JHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToDL4JDataset
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -8659472246354986321L;

  /** the attribute(s) to use as class attribute. */
  protected SpreadSheetColumnRange m_ClassAttribute;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a DL4J dataset from the spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "class-attribute", "classAttribute",
	    new SpreadSheetColumnRange("last"));
  }

  /**
   * Sets the column to use as class attributes.
   *
   * @param value	the index
   */
  public void setClassAttribute(SpreadSheetColumnRange value) {
    m_ClassAttribute = value;
    reset();
  }

  /**
   * Returns the colum that identify a rowx
   *
   * @return		the index
   */
  public SpreadSheetColumnRange getClassAttribute() {
    return m_ClassAttribute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classAttributeTipText() {
    return "The column(s) to use as class attributes.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return org.deeplearning4j.datasets.DataSet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet		input;
    
    input = (SpreadSheet) m_Input;
    m_ClassAttribute.setData(input);

    return DL4JHelper.spreadsheetToDataSet(input, m_ClassAttribute.getIntIndices());
  }
}
