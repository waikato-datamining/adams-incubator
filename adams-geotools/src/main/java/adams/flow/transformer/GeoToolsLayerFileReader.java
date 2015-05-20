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
 * GeoToolsRasterFileReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.io.File;

import org.geotools.map.Layer;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractGeoToolsLayerReader;
import adams.data.io.input.ShapeFileReader;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Reads a layer from a file using the specified reader.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;org.geotools.map.Layer<br>
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
 * &nbsp;&nbsp;&nbsp;default: GeoToolsLayerFileReader
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
 * <pre>-reader &lt;adams.data.io.input.AbstractGeoToolsLayerReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for reading the layer.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.ShapeFileReader
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GeoToolsLayerFileReader
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 203312809352344085L;

  /** the reader. */
  protected AbstractGeoToolsLayerReader m_Reader;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads a layer from a file using the specified reader.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reader", "reader",
	    new ShapeFileReader());
  }
  
  /**
   * Sets the reader to use.
   *
   * @param value	the reader
   */
  public void setReader(AbstractGeoToolsLayerReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader to use.
   *
   * @return		the reader
   */
  public AbstractGeoToolsLayerReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for reading the layer.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "reader", m_Reader);
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Layer.class};
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile	file;
    Layer 		layer;

    result = null;
    
    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    
    m_Reader.setInput(file);
    layer = m_Reader.read();
    if (layer == null)
      result = "Failed to read layer from: " + file;

    if (result == null)
      m_OutputToken = new Token(layer);

    return result;
  }
}
