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
 * RasterFileReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.map.GridReaderLayer;
import org.geotools.styling.Style;

import adams.core.Utils;
import adams.data.RasterImageHelper;
import adams.data.RasterType;

/**
 <!-- globalinfo-start -->
 * Reads raster files.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The layer file to read.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-type &lt;GREYSCALE|RGB&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The raster type to display.
 * &nbsp;&nbsp;&nbsp;default: RGB
 * </pre>
 * 
 * <pre>-greyscale-band &lt;int&gt; (property: greyScaleBand)
 * &nbsp;&nbsp;&nbsp;The greyscale band to use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RasterFileReader
  extends AbstractGeoToolsLayerReader<GridReaderLayer> {

  /** for serialization. */
  private static final long serialVersionUID = -6604635751859495622L;

  /** the raster type. */
  protected RasterType m_Type;
  
  /** the greyscale band. */
  protected int m_GreyScaleBand;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads raster files.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Raster file";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"tif", "tiff"};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    RasterType.RGB);

    m_OptionManager.add(
	    "greyscale-band", "greyScaleBand",
	    1);
  }
  
  /**
   * Sets the raster type to display.
   *
   * @param value	the type
   */
  public void setType(RasterType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the raster type to display.
   *
   * @return		the type
   */
  public RasterType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The raster type to display.";
  }
  
  /**
   * Sets the greyscale band to use.
   *
   * @param value	the band
   */
  public void setGreyScaleBand(int value) {
    m_GreyScaleBand = value;
    reset();
  }

  /**
   * Returns the greyscale band to use.
   *
   * @return		the band
   */
  public int getGreyScaleBand() {
    return m_GreyScaleBand;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String greyScaleBandTipText() {
    return "The greyscale band to use.";
  }

  /**
   * Performs the actual reading.
   * 
   * @return		the generated layer
   */
  @Override
  protected GridReaderLayer doRead() {
    GridReaderLayer 			result;
    Style 				style;
    AbstractGridFormat 			format;
    AbstractGridCoverage2DReader	reader;

    try {
      format = GridFormatFinder.findFormat(m_Input.getAbsoluteFile());        
      reader = format.getReader(m_Input.getAbsoluteFile());
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to load raster file : " + m_Input, e);
      return null;
    }

    switch (m_Type) {
      case GREYSCALE:
	style = RasterImageHelper.createGreyscaleStyle(m_GreyScaleBand);
	break;
      case RGB:
	style = RasterImageHelper.createRGBStyle(reader);
	break;
      default:
	throw new IllegalStateException("Unhandled raster type: " + m_Type);
    }
    result = new GridReaderLayer(reader, style);

    return result;
  }
}
