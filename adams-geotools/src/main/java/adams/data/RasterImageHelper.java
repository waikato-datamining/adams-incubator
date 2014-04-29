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
 * RasterImageHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import java.io.IOException;

import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * Helper class for raster images (eg TIFF).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "2013 GeoTools",
    license = License.LGPL21,
    url = "http://docs.geotools.org/stable/tutorials/raster/image.html",
    note = "Style generation taken from tutorial website"
)
public class RasterImageHelper {

  /** constant for the RED channel. */
  public final static int RED = 0;

  /** constant for the GREEN channel. */
  public final static int GREEN = 1;

  /** constant for the BLUE channel. */
  public final static int BLUE = 2;

  /**
   * Create a Style to display the specified band of the GeoTIFF image
   * as a greyscale layer.
   *
   * @param band 	the image band to use for the greyscale display
   * @return 		a new Style instance to render the image in greyscale
   */
  public static Style createGreyscaleStyle(int band) {
    StyleFactory 		sf;
    FilterFactory2 		ff;
    ContrastEnhancement		ce;
    SelectedChannelType 	sct;
    RasterSymbolizer 		sym;
    ChannelSelection 		sel;
    
    sf  = CommonFactoryFinder.getStyleFactory(null);
    ff  = CommonFactoryFinder.getFilterFactory2(null);
    ce  = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
    sct = sf.createSelectedChannelType(String.valueOf(band), ce);
    sym = sf.getDefaultRasterSymbolizer();
    sel = sf.channelSelection(sct);
    sym.setChannelSelection(sel);

    return SLD.wrapSymbolizers(sym);
  }

  /**
   * This method examines the names of the sample dimensions in the provided coverage looking for
   * "red...", "green..." and "blue..." (case insensitive match). If these names are not found
   * it uses bands 1, 2, and 3 for the red, green and blue channels. It then sets up a raster
   * symbolizer and returns this wrapped in a Style.
   *
   * @param reader 	the raster reader to use
   * @return 		a new Style object containing a raster symbolizer set up for RGB image
   */
  public static Style createRGBStyle(AbstractGridCoverage2DReader reader) {
    StyleFactory 		sf;
    FilterFactory2 		ff;
    GridCoverage2D 		cov;
    int 			numBands;
    String[] 			sampleDimensionNames;
    int				i;
    GridSampleDimension 	dim;
    int[] 			channelNum;
    String 			name;
    SelectedChannelType[] 	sct;
    ContrastEnhancement 	ce;
    RasterSymbolizer 		sym;
    ChannelSelection 		sel;
    
    sf  = CommonFactoryFinder.getStyleFactory(null);
    ff  = CommonFactoryFinder.getFilterFactory2(null);
    cov = null;
    try {
      cov = reader.read(null);
    } 
    catch (IOException giveUp) {
      throw new RuntimeException(giveUp);
    }
    // We need at least three bands to create an RGB style
    numBands = cov.getNumSampleDimensions();
    if (numBands < 3)
      return null;
    
    // Get the names of the bands
    sampleDimensionNames = new String[numBands];
    for (i = 0; i < numBands; i++) {
      dim                     = cov.getSampleDimension(i);
      sampleDimensionNames[i] = dim.getDescription().toString();
    }
    channelNum = new int[]{-1, -1, -1};
    // We examine the band names looking for "red...", "green...", "blue...".
    // Note that the channel numbers we record are indexed from 1, not 0.
    for (i = 0; i < numBands; i++) {
      name = sampleDimensionNames[i].toLowerCase();
      if (name != null) {
	if (name.matches("red.*"))
	  channelNum[RED] = i + 1;
	else if (name.matches("green.*"))
	  channelNum[GREEN] = i + 1;
	else if (name.matches("blue.*"))
	  channelNum[BLUE] = i + 1;
      }
    }
    // If we didn't find named bands "red...", "green...", "blue..."
    // we fall back to using the first three bands in order
    if ((channelNum[RED] < 0) || (channelNum[GREEN] < 0) || (channelNum[BLUE] < 0)) {
      channelNum[RED]   = 1;
      channelNum[GREEN] = 2;
      channelNum[BLUE]  = 3;
    }
    // Now we create a RasterSymbolizer using the selected channels
    sct = new SelectedChannelType[cov.getNumSampleDimensions()];
    ce  = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
    for (i = 0; i < 3; i++)
      sct[i] = sf.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
    sym = sf.getDefaultRasterSymbolizer();
    sel = sf.channelSelection(sct[RED], sct[GREEN], sct[BLUE]);
    sym.setChannelSelection(sel);

    return SLD.wrapSymbolizers(sym);
  }

}
