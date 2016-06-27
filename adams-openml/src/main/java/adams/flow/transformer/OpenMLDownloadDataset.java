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
 * OpenMLDownloadDataset.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.Utils;
import adams.data.io.input.SimpleArffSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.openml.OpenMLHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.OpenMLDatasetContainer;
import adams.flow.core.Token;
import adams.ml.data.Dataset;
import adams.ml.data.DatasetView;
import adams.ml.data.DefaultDataset;
import gnu.trove.list.array.TIntArrayList;
import org.openml.apiconnector.xml.DataSetDescription;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Downloads a dataset and forwards it with its meta-data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.OpenMLDatasetContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.OpenMLDatasetContainer: Dataset, Meta-data
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
 * &nbsp;&nbsp;&nbsp;default: OpenMLDownloadDataset
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
 * <pre>-reader &lt;adams.data.io.input.SpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The spreadsheet reader to use for reading the downloaded dataset file.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.SimpleArffSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenMLDownloadDataset
  extends AbstractOpenMLTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 6360812364500936369L;

  /** the reader to use. */
  protected SpreadSheetReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Downloads a dataset and forwards it with its meta-data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new SimpleArffSpreadSheetReader());
  }

  /**
   * Sets the spreadsheet reader to use for reading the downloaded dataset file.
   *
   * @param value	the reader
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the spreadsheet reader to use for reading the downloaded dataset file.
   *
   * @return		the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The spreadsheet reader to use for reading the downloaded dataset file.";
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
    return new Class[]{OpenMLDatasetContainer.class};
  }

  /**
   * Adds a row to the spreadsheet. Skips it if the value is null.
   *
   * @param sheet	the sheet to add the row to
   * @param key		the key of the value to add
   * @param value	the value to add
   */
  protected void addRow(SpreadSheet sheet, String key, Object value) {
    Row row;

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
    String			result;
    int				did;
    DataSetDescription  	dataset;
    SpreadSheet 		sheet;
    Dataset			data;
    SpreadSheet			meta;
    OpenMLDatasetContainer	cont;
    File			file;
    TIntArrayList 		visible;
    int				index;
    Row				row;

    result = null;
    did    = (Integer) m_InputToken.getPayload();
	
    try {
      if (isLoggingEnabled())
	getLogger().info("Downloading dataset for #" + did);
      dataset = m_Connection.getConnector().dataGet(did);

      file = dataset.getDataset(m_Connection.getAPIKey());
      meta = new DefaultSpreadSheet();

      // header
      row = meta.getHeaderRow();
      row.addCell("K").setContent("Key");
      row.addCell("V").setContent("Value");

      // data
      addRow(meta, "ID", "" + dataset.getId());
      addRow(meta, "Name", dataset.getName());
      addRow(meta, "Version", dataset.getVersion());
      addRow(meta, "Description", dataset.getDescription());
      addRow(meta, "CollectionDate", dataset.getCollection_date());
      addRow(meta, "Creators", dataset.getCreator());
      addRow(meta, "Contributors", dataset.getContributor());
      addRow(meta, "Format", dataset.getFormat());
      addRow(meta, "UploadDate", dataset.getUpload_date());
      addRow(meta, "Language", dataset.getLanguage());
      addRow(meta, "Licence", dataset.getLicence());
      addRow(meta, "RowIdAttribute", dataset.getRow_id_attribute());
      addRow(meta, "DefaultTargetAttribute", dataset.getDefault_target_attribute());
      addRow(meta, "IgnoreAttributes", dataset.getIgnore_attribute());
      addRow(meta, "Tags", dataset.getTag());
      addRow(meta, "MD5", dataset.getMd5_checksum());
      addRow(meta, "URL", dataset.getUrl());
      addRow(meta, "Dataset", file.getAbsolutePath());

      sheet = m_Reader.read(file);
      data  = new DefaultDataset(sheet);

      // class attribute?
      if (data.indexOfColumn(dataset.getDefault_target_attribute()) > -1)
	data.setClassAttributeByName(dataset.getDefault_target_attribute(), true);

      // ignored columns?
      if (dataset.getIgnore_attribute() != null) {
	if (isLoggingEnabled())
	  getLogger().info("Creating view without ignore columns: " + Utils.flatten(dataset.getIgnore_attribute(), ","));
	visible = new TIntArrayList();
	for (index = 0; index < data.getColumnCount(); index++)
	  visible.add(index);
	for (String colName: dataset.getIgnore_attribute()) {
	  index = data.indexOfColumn(colName);
	  if (index == -1)
	    getLogger().warning("Failed to locate ignored column: " + colName);
	  else
	    visible.remove(index);
	}
	data = new DatasetView(data, null, visible.toArray());
      }

      cont = new OpenMLDatasetContainer(data, meta);
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      result = handleException("Failed to obtain dataset #" + did + " from OpenML!", e);
    }
    
    return result;
  }
}
