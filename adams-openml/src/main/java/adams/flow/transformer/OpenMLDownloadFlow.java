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
import adams.data.report.Report;
import adams.flow.container.OpenMLDatasetContainer;
import adams.flow.core.Token;
import org.openml.apiconnector.xml.DataSetDescription;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;

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
    return "Downloads a dataset and forwards it with its meta-data.";
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    int				did;
    DataSetDescription  	dataset;
    Instances			inst;
    Report			meta;
    OpenMLDatasetContainer	cont;
    File			file;
    AbstractFileLoader		loader;

    result = null;
    did    = (Integer) m_InputToken.getPayload();
	
    try {
      if (isLoggingEnabled())
	getLogger().info("Downloading dataset for #" + did);
      dataset = m_Connection.getConnector().dataGet(did);

      file = dataset.getDataset(m_Connection.getAPIKey());
      meta = new Report();
      meta.setStringValue("Name", dataset.getName());
      meta.setStringValue("Version", dataset.getVersion());
      meta.setStringValue("Creators", dataset.getCreator() == null ? "" : Utils.flatten(dataset.getCreator(), ";"));
      meta.setStringValue("Contributors", dataset.getContributor() == null ? "" : Utils.flatten(dataset.getContributor(), ";"));
      meta.setStringValue("Format", dataset.getFormat());
      meta.setStringValue("CollectionDate", dataset.getCollection_date() == null ? "" : dataset.getCollection_date());
      meta.setStringValue("Language", dataset.getLanguage() == null ? "" : dataset.getLanguage());
      meta.setStringValue("Licence", dataset.getLicence() == null ? "" : dataset.getLicence());
      meta.setStringValue("RowIdAttribute", dataset.getRow_id_attribute() == null ? "" : dataset.getRow_id_attribute());
      meta.setStringValue("DefaultTargetAttribute", dataset.getDefault_target_attribute());
      meta.setStringValue("IgnoreAttributes", dataset.getIgnore_attribute() == null ? "" : Utils.flatten(dataset.getIgnore_attribute(), ";"));
      meta.setStringValue("Tags", dataset.getTag() == null ? "" : Utils.flatten(dataset.getTag(), ";"));
      meta.setStringValue("MD5", dataset.getMd5_checksum());
      meta.setStringValue("URL", dataset.getUrl() == null ? "" : dataset.getUrl());
      meta.setStringValue("ID", "" + dataset.getId());
      meta.setStringValue("Dataset", "" + file);

      // TODO format?
      loader = null;
      if (dataset.getFormat().equalsIgnoreCase("arff"))
	loader = new ArffLoader();
      else
        result = "Unhandled format: " + dataset.getFormat();
      if (loader != null) {
	loader.setFile(file);
	inst = DataSource.read(loader);
	if (inst.attribute(dataset.getDefault_target_attribute()) != null)
	  inst.setClassIndex(inst.attribute(dataset.getDefault_target_attribute()).index());

	cont = new OpenMLDatasetContainer(inst, meta);
	m_OutputToken = new Token(cont);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to obtain dataset #" + did + " from OpenML!", e);
    }
    
    return result;
  }
}
