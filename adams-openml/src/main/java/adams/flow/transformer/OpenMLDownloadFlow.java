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
 * OpenMLDownloadFlow.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.data.openml.OpenMLHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import org.openml.apiconnector.xml.Flow;

/**
 <!-- globalinfo-start -->
 * Downloads a flow's meta-data and forwards it.
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
 * &nbsp;&nbsp;&nbsp;default: OpenMLDownloadFlow
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenMLDownloadFlow
  extends AbstractOpenMLTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 6360812364500936369L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Downloads a flow's meta-data and forwards it.";
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
    String	result;
    int 	fid;
    Flow	flow;
    SpreadSheet	sheet;
    Row		row;

    result = null;
    fid = (Integer) m_InputToken.getPayload();
	
    try {
      if (isLoggingEnabled())
	getLogger().info("Downloading flow for #" + fid);
      flow = m_Connection.getConnector().flowGet(fid);

      sheet = new DefaultSpreadSheet();
      sheet.setName("Flow #" + fid);
      // header
      row = sheet.getHeaderRow();
      row.addCell("K").setContentAsString("Key");
      row.addCell("V").setContentAsString("Value");
      // data
      addRow(sheet, "ID", flow.getId());
      addRow(sheet, "FullName", flow.getFullName());
      addRow(sheet, "UploaderID", flow.getUploader());
      addRow(sheet, "Name", flow.getName());
      addRow(sheet, "Version", flow.getVersion());
      addRow(sheet, "ExternalVersion", flow.getExternal_version());
      addRow(sheet, "Description", flow.getDescription());
      addRow(sheet, "Creators", flow.getCreator());
      addRow(sheet, "Contributors", flow.getContributor());
      addRow(sheet, "UploadDate", flow.getUpload_date());
      addRow(sheet, "Licence", flow.getLicence());
      addRow(sheet, "Language", flow.getLanguage());
      addRow(sheet, "FullDescription", flow.getFull_description());
      addRow(sheet, "InstallationNotes", flow.getInstallation_notes());
      addRow(sheet, "Dependencies", flow.getDependencies());
      addRow(sheet, "Implement", flow.getImplement());
      addRow(sheet, "Parameters", flow.getParameter());
      addRow(sheet, "Components", flow.getComponent());
      addRow(sheet, "Tags", flow.getTag());
      addRow(sheet, "SourceURL", flow.getSource_url());
      addRow(sheet, "BinaryURL", flow.getBinary_url());
      addRow(sheet, "SourceFormat", flow.getSource_format());
      addRow(sheet, "BinaryFormat", flow.getBinary_format());
      addRow(sheet, "SourceMD5", flow.getSource_md5());
      addRow(sheet, "BinaryMD5", flow.getBinary_md5());

      m_OutputToken = new Token(sheet);
    }
    catch (Exception e) {
      result = handleException("Failed to obtain flow #" + fid + " from OpenML!", e);
    }
    
    return result;
  }
}
