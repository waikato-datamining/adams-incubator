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
 * OpenMLDatasetDescription.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.data.openml.OpenMLHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import org.openml.apiconnector.xml.DataSetDescription;

/**
 <!-- globalinfo-start -->
 * Obtains the dataset description for the dataset identifier obtained as input.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: OpenMLDatasetDescription
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenMLDatasetDescription
  extends AbstractOpenMLTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 6069900053496647243L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Obtains the dataset description for the dataset identifier obtained as input.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Integer.class};
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
   * Adds a row to the spreadsheet. Skips it if the value is null.
   *
   * @param sheet	the sheet to add the row to
   * @param key		the key of the value to add
   * @param value	the value to add
   */
  protected void addRow(SpreadSheet sheet, String key, Object value) {
    Row		row;

    if (value == null)
      return;

    row = sheet.addRow();
    row.addCell("K").setContentAsString(key);
    row.addCell("V").setContentAsString(OpenMLHelper.toString(value, ";", ""));
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    int			did;
    DataSetDescription 	dataset;
    SpreadSheet 	meta;
    Row			row;
    
    result = null;
    did    = (Integer) m_InputToken.getPayload();
	
    try {
      if (isLoggingEnabled())
	getLogger().info("Obtaining dataset description for #" + did);
      dataset = m_Connection.getConnector().dataGet(did);
      meta = new DefaultSpreadSheet();
      
      // header
      row = meta.getHeaderRow();
      row.addCell("K").setContent("Key");
      row.addCell("V").setContent("Value");

      // data
      addRow(meta, "ID", did);
      addRow(meta, "Name", dataset.getName());
      addRow(meta, "Version", dataset.getVersion());
      addRow(meta, "Description", dataset.getDescription());
      addRow(meta, "Format", dataset.getFormat());
      addRow(meta, "Creators", dataset.getCreator());
      addRow(meta, "Contributors", dataset.getContributor());
      addRow(meta, "CollectionDate", dataset.getCollection_date());
      addRow(meta, "UploadDate", dataset.getUpload_date());
      addRow(meta, "Language", dataset.getLanguage());
      addRow(meta, "Licence", dataset.getLicence());
      addRow(meta, "URL", dataset.getUrl());
      addRow(meta, "RowIdAttribute", dataset.getRow_id_attribute());
      addRow(meta, "DefaultTargetAttribute", dataset.getDefault_target_attribute());
      addRow(meta, "IgnoreAttributes", dataset.getIgnore_attribute());
      addRow(meta, "Tags", dataset.getTag());
      addRow(meta, "MD5", dataset.getMd5_checksum());

      m_OutputToken = new Token(meta);
    }
    catch (Exception e) {
      result = handleException("Failed to obtain description for dataset #" + did + " from OpenML!", e);
    }
    
    return result;
  }
}
