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
 * OpenMLJsonToSpreadSheet.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.openml.OpenMLHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Converts an OpenML.org JSON result into a spreadsheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenMLJsonToSpreadSheet
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 3930261101137436315L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts an OpenML.org JSON result into a spreadsheet.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return JSONAware.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet	result;
    JSONAware	input;
    Row		row;
    JsonPath	path;
    Object	val;
    List	list;
    JSONArray	array;
    int		i;
    String	error;
    
    input  = (JSONAware) m_Input;
    result = new DefaultSpreadSheet();

    // check for error message
    error = OpenMLHelper.extractErrorMessage(input);
    if (error != null)
      throw new IllegalStateException("JSON contains error: " + error);
    
    // header
    path = JsonPath.compile("$..title");
    try {
      val  = path.read(input);
      list = new ArrayList();
      if (val instanceof List)
	list.addAll((List) val);
      else
	list.add(val);
      row = result.getHeaderRow();
      for (Object l: list)
	row.addCell("" + result.getColumnCount()).setContent("" + l);
    }
    catch (Exception e) {
      throw new Exception("Failed to evaluate path (header): " + path + "\nJSON:\n" + input, e);
    }
    
    // data
    path = JsonPath.compile("$.data");
    try {
      val  = path.read(input);
      list = new ArrayList();
      if (val instanceof List)
	list.addAll((List) val);
      else
	list.add(val);
      for (Object l: list) {
	row   = result.addRow();
	array = (JSONArray) l;
	for (i = 0; i < array.size(); i++)
	  row.addCell(i).setContent("" + array.get(i));
      }
    }
    catch (Exception e) {
      throw new Exception("Failed to evaluate path (data): " + path + "\nJSON:\n" + input, e);
    }
    
    return result;
  }
}
