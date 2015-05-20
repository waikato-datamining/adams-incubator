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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import org.openml.apiconnector.xml.DataSetDescription;

import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;

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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    int			did;
    DataSetDescription	desc;
    SpreadSheet		sheet;
    Row			row;
    
    result = null;
    did    = (Integer) m_InputToken.getPayload();
	
    try {
      m_Connection.getSession();
      if (isLoggingEnabled())
	getLogger().info("Obtaining dataset description for #" + did);
      desc  = m_Connection.getConnector().openmlDataDescription(did);
      sheet = new SpreadSheet();
      
      // header
      row = sheet.getHeaderRow();
      row.addCell("k").setContent("Key");
      row.addCell("v").setContent("Value");

      // data
      row = sheet.addRow();
      row.addCell("k").setContent("ID");
      row.addCell("v").setContent(did);

      row = sheet.addRow();
      row.addCell("k").setContent("Name");
      row.addCell("v").setContent(desc.getName());

      row = sheet.addRow();
      row.addCell("k").setContent("Version");
      row.addCell("v").setContent(desc.getVersion());

      row = sheet.addRow();
      row.addCell("k").setContent("Description");
      row.addCell("v").setContent(desc.getDescription());

      row = sheet.addRow();
      row.addCell("k").setContent("Format");
      row.addCell("v").setContent(desc.getFormat());

      row = sheet.addRow();
      row.addCell("k").setContent("Creator(s)");
      row.addCell("v").setContent(desc.getCreator() == null ? "" : Utils.flatten(desc.getCreator(), ", "));

      row = sheet.addRow();
      row.addCell("k").setContent("Contributor(s)");
      row.addCell("v").setContent(desc.getContributor() == null ? "" : Utils.flatten(desc.getContributor(), ", "));

      row = sheet.addRow();
      row.addCell("k").setContent("Collection date");
      row.addCell("v").setContent(desc.getCollection_date());

      row = sheet.addRow();
      row.addCell("k").setContent("Upload date");
      row.addCell("v").setContent(desc.getUpload_date());

      row = sheet.addRow();
      row.addCell("k").setContent("Language");
      row.addCell("v").setContent(desc.getLanguage());

      row = sheet.addRow();
      row.addCell("k").setContent("Licence");
      row.addCell("v").setContent(desc.getLicence());

      row = sheet.addRow();
      row.addCell("k").setContent("URL");
      row.addCell("v").setContent(desc.getUrl());

      row = sheet.addRow();
      row.addCell("k").setContent("Row ID attribute");
      row.addCell("v").setContent(desc.getRow_id_attribute());

      row = sheet.addRow();
      row.addCell("k").setContent("Default target attribute");
      row.addCell("v").setContent(desc.getDefault_target_attribute());

      row = sheet.addRow();
      row.addCell("k").setContent("MD5");
      row.addCell("v").setContent(desc.getMd5_checksum());
      
      m_OutputToken = new Token(sheet);
    }
    catch (Exception e) {
      result = handleException("Failed to obtain description for dataset #" + did + " from OpenML!", e);
    }
    
    return result;
  }
}
