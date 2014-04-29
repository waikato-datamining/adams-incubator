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
 * SpreadSheetToGeoToolsLayer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import org.geotools.map.Layer;

import adams.core.AdditionalInformationHandler;
import adams.core.QuickInfoHelper;
import adams.data.conversion.feature.AbstractFeatureGenerator;
import adams.data.conversion.feature.SimplePointGenerator;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Creates features from the incoming spreadsheet and turns them into a GeoTools layer.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: INFO
 * </pre>
 * 
 * <pre>-generator &lt;adams.data.conversion.feature.AbstractFeatureCollectionGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for turning the spreadsheet into a feature collection 
 * &nbsp;&nbsp;&nbsp;to be used as base for the layer.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.feature.SimpleLocationGenerator
 * </pre>
 * 
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;The fill color for the shape file.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToGeoToolsLayer
  extends AbstractConversion
  implements AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5141463557727753413L;
  
  /** the feature generator to use. */
  protected AbstractFeatureGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates features from the incoming spreadsheet and turns them into a GeoTools layer.";
  }

  /**
   * Returns the additional information.
   * 
   * @return		the additional information, null or 0-length string for no information
   */
  @Override
  public String getAdditionalInformation() {
    return "GeoTools tutorial on feature generation:\nhttp://docs.geotools.org/stable/tutorials/feature/csv2shp.html";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new SimplePointGenerator());
  }
  
  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractFeatureGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator to use.
   *
   * @return		the generator
   */
  public AbstractFeatureGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for turning the spreadsheet into a feature collection to be used as base for the layer.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator);
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Layer.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Layer		result;
    SpreadSheet		sheet;

    sheet  = (SpreadSheet) m_Input;
    result = m_Generator.generateLayer(sheet);

    return result;
  }
}
