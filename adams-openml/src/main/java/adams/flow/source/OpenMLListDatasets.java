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
 * OpenMLListDatasets.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import org.openml.apiconnector.xml.Data;
import org.openml.apiconnector.xml.Data.DataSet;
import org.openml.apiconnector.xml.Data.DataSet.Quality;

import java.util.HashMap;
import java.util.Map;

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
public class OpenMLListDatasets
  extends AbstractOpenMLSource {
  
  /** for serialization. */
  private static final long serialVersionUID = -5561129671356371714L;
  
  /** the filters. */
  protected BaseKeyValuePair[] m_Filters;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists all the datasets that match the filters:\n"
      + "https://www.openml.org/api_docs/#!/data/get_data_list";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filters",
	    new BaseKeyValuePair[0]);
  }
  
  /**
   * Sets the filters.
   *
   * @param value	the filters
   */
  public void setFilters(BaseKeyValuePair[] value) {
    m_Filters = value;
    reset();
  }

  /**
   * Returns the filters.
   *
   * @return		the filters
   */
  public BaseKeyValuePair[] getFilters() {
    return m_Filters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filtersTipText() {
    return "The filters that the datasets must match.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "filters", m_Filters, "filters: ");
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Map<String,String> 		filters;
    Data			data;
    SpreadSheet			sheet;
    Row				row;
    StringBuilder		quals;
    
    result = null;
    
    try {
      filters = null;
      if (m_Filters.length > 0) {
        filters = new HashMap<>();
        for (BaseKeyValuePair filter: m_Filters)
          filters.put(filter.getPairKey(), filter.getPairValue());
      }
      data  = m_Connection.getConnector().dataList(filters);
      sheet = new DefaultSpreadSheet();
      row   = sheet.getHeaderRow();
      row.addCell("id").setContent("ID");
      row.addCell("fid").setContent("File ID");
      row.addCell("name").setContent("Name");
      row.addCell("format").setContent("Format");
      row.addCell("version").setContent("Version");
      row.addCell("status").setContent("Status");
      row.addCell("qualities").setContent("Qualities");
      for (DataSet ds: data.getData()) {
        row = sheet.addRow();
        row.addCell("id").setContent(ds.getDid());
        row.addCell("fid").setContent(ds.getFileId());
        row.addCell("name").setContent(ds.getName());
        row.addCell("format").setContent(ds.getFormat());
        row.addCell("version").setContent(ds.getVersion());
        row.addCell("status").setContent(ds.getStatus());
        quals = new StringBuilder();
        if (ds.getQualities() != null) {
	  for (Quality q : ds.getQualities()) {
	    if (quals.length() > 0)
	      quals.append(" ");
	    quals.append(q.getName());
	    quals.append("=");
	    quals.append(q.getValue());
	  }
	}
        row.addCell("qualities").setContent(quals.toString());
      }
      m_OutputToken = new Token(sheet);
    }
    catch (Exception e) {
      result = handleException("Failed to query OpenML!", e);
    }
    
    return result;
  }
}
