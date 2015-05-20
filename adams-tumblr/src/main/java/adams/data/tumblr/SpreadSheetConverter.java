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
 * SpreadSheetConverter.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.tumblr;

import java.util.Hashtable;

import adams.core.DateTime;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns a post update into a spreadsheet object containing a single row.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-field &lt;ID|REBLOG_KEY|BLOG_NAME|POST_URL|TYPE|TIMESTAMP|STATE|FORMAT|DATE|TAGS|BOOKMARKLET|MOBILE|SOURCE_URL|LIKED|REBLOGGED_FROM_ID|REBLOGGED_FROM_NAME&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to use for generating the output.
 * &nbsp;&nbsp;&nbsp;default: POST_URL
 * </pre>
 * 
 * <pre>-data-row-type &lt;DENSE|SPARSE&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: DENSE
 * </pre>
 * 
 * <pre>-spreadsheet-type &lt;adams.data.spreadsheet.SpreadSheet&gt; (property: spreadSheetType)
 * &nbsp;&nbsp;&nbsp;The type of spreadsheet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetConverter
  extends AbstractTumblrPostConverter<SpreadSheet> {

  /** for serialization. */
  private static final long serialVersionUID = -4932470309464987225L;

  /** the data row type to use. */
  protected DataRow m_DataRowType;

  /** the type of spreadsheet to use. */
  protected SpreadSheet m_SpreadSheetType;

  /** the current header. */
  protected SpreadSheet m_Header;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a post update into a spreadsheet object containing a single row.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "data-row-type", "dataRowType",
	    new DenseDataRow());

    m_OptionManager.add(
	    "spreadsheet-type", "spreadSheetType",
	    new SpreadSheet());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Header = null;
  }

  /**
   * Sets the type of data row to use.
   *
   * @param value	the type
   */
  public void setDataRowType(DataRow value) {
    m_DataRowType = value;
    reset();
  }

  /**
   * Returns the type of data row to use.
   *
   * @return		the type
   */
  public DataRow getDataRowType() {
    return m_DataRowType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataRowTypeTipText() {
    return "The type of row to use for the data.";
  }

  /**
   * Sets the type of spreadsheet to use.
   *
   * @param value	the type
   */
  public void setSpreadSheetType(SpreadSheet value) {
    m_SpreadSheetType = value;
    reset();
  }

  /**
   * Returns the type of spreadsheet to use.
   *
   * @return		the type
   */
  public SpreadSheet getSpreadSheetType() {
    return m_SpreadSheetType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String spreadSheetTypeTipText() {
    return "The type of spreadsheet to use for the data.";
  }

  /**
   * Returns the class of the output data that is generated.
   * 
   * @return		the data type
   */
  @Override
  public Class generates() {
    if (m_SpreadSheetType != null)
      return m_SpreadSheetType.getClass();
    else
      return SpreadSheet.class;
  }

  /**
   * Generates the dataset header.
   */
  protected void generateHeader() {
    SpreadSheet	sheet;
    Row		row;

    sheet = m_SpreadSheetType.newInstance();
    sheet.setDataRowClass(m_DataRowType.getClass());
    row   = sheet.getHeaderRow();
    for (TumblrPostField field: m_Fields)
      row.addCell("" + row.getCellCount()).setContent(field.toString());
    
    m_Header = sheet;
  }

  /**
   * Performs the actual conversion.
   * 
   * @param fields	the post data to convert
   * @return		the generated output
   */
  @Override
  protected SpreadSheet doConvert(Hashtable fields) {
    SpreadSheet	result;
    Row		row;
    int		i;
    Object	obj;

    if (m_Header == null)
      generateHeader();
    
    result = m_Header.getClone();
    row   = result.addRow();
    for (i = 0; i < m_Fields.length; i++) {
      obj = fields.get(m_Fields[i]);
      if (obj == null)
	continue;
      switch (m_Fields[i]) {
	case ID:
	case REBLOGGED_FROM_ID:
	  row.addCell(i).setContent((Long) obj);
	  break;

	case REBLOG_KEY:
	case BLOG_NAME:
	case POST_URL:
	case TYPE:
	case STATE:
	case FORMAT:
	case DATE:
	case TAGS:
	case SOURCE_URL:
	// TODO case SOURCE_TITLE:
	// TODO case SLUG:
	case REBLOGGED_FROM_NAME:
	  row.addCell(i).setContentAsString((String) obj);
	  break;

	case TIMESTAMP:
	  row.addCell(i).setContent((DateTime) obj);
	  break;
	  
	case BOOKMARKLET:
	case MOBILE:
	case LIKED:
	  row.addCell(i).setContent((Boolean) obj);
	  break;
	  
	default:
	  throw new IllegalStateException("Unhandled post field: " + m_Fields[i]);
      }
    }
    
    return result;
  }
}
