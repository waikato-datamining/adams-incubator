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
 * OpenMLUploadDataset.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import org.openml.apiconnector.xml.UploadDataSet;

/**
 <!-- globalinfo-start -->
 * Uploads a dataset with its description.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
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
 * &nbsp;&nbsp;&nbsp;default: OpenMLUploadDataset
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
 * <pre>-description &lt;adams.core.io.PlaceholderFile&gt; (property: description)
 * &nbsp;&nbsp;&nbsp;The file with the description of the dataset.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-dataset &lt;adams.core.io.PlaceholderFile&gt; (property: dataset)
 * &nbsp;&nbsp;&nbsp;The dataset file.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenMLUploadDataset
  extends AbstractOpenMLSource {

  /** for serialization. */
  private static final long serialVersionUID = -5561129671356371714L;

  /** the description file. */
  protected PlaceholderFile m_Description;

  /** the dataset file. */
  protected PlaceholderFile m_Dataset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uploads a dataset with its description.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "description", "description",
      new PlaceholderFile());

    m_OptionManager.add(
      "dataset", "dataset",
      new PlaceholderFile());
  }

  /**
   * Sets the file with the description of the dataset.
   *
   * @param value	the file
   */
  public void setDescription(PlaceholderFile value) {
    m_Description = value;
    reset();
  }

  /**
   * Returns the file with the description of the dataset.
   *
   * @return		the file
   */
  public PlaceholderFile getDescription() {
    return m_Description;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String descriptionTipText() {
    return "The file with the description of the dataset.";
  }

  /**
   * Sets the dataset file.
   *
   * @param value	the file
   */
  public void setDataset(PlaceholderFile value) {
    m_Dataset = value;
    reset();
  }

  /**
   * Returns the dataset file.
   *
   * @return		the file
   */
  public PlaceholderFile getDataset() {
    return m_Dataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetTipText() {
    return "The dataset file.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "description", m_Description, "description: ");
    result += QuickInfoHelper.toString(this, "dataset", m_Dataset, ", dataset: ");

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Integer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    UploadDataSet	response;

    result = null;

    if (m_Dataset.isDirectory())
      result = "Dataset points to a directory: " + m_Dataset;
    else if (!m_Dataset.exists())
      result = "Dataset does not exist: " + m_Dataset;
    else if (m_Description.isDirectory())
      result = "Description points to a directory: " + m_Description;
    else if (!m_Description.exists())
      result = "Description does not exist: " + m_Description;

    if (result == null) {
      try {
	response = m_Connection.getConnector().dataUpload(
	  m_Description.getAbsoluteFile(),
	  m_Dataset.getAbsoluteFile());
	if (response != null)
	  m_OutputToken = new Token(response.getId());
      }
      catch (Exception e) {
	result = handleException("Failed to upload dataset: " + m_Dataset, e);
      }
    }

    return result;
  }
}
