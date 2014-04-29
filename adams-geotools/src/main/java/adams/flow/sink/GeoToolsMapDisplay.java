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
 * GeoToolsMapDisplay.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseInteger;
import adams.core.io.PlaceholderFile;
import adams.data.RasterImageHelper;
import adams.data.RasterType;
import adams.flow.core.Token;
import adams.flow.sink.infotool.DefaultInfoToolSupplier;
import adams.flow.sink.infotool.InfoToolSupplier;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.BasePanel;
import adams.gui.visualization.maps.MapDisplayPanel;

/**
 <!-- globalinfo-start -->
 * Displays the incoming data using the specified raster image or shape files as background. Raster images get added first before shape files.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;org.geotools.map.Layer<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: GeoToolsMapDisplay
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
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 * <pre>-background-color &lt;java.awt.Color&gt; (property: backgroundColor)
 * &nbsp;&nbsp;&nbsp;The background color of the map.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 * <pre>-background-shape &lt;adams.core.io.PlaceholderFile&gt; [-background-shape ...] (property: backgroundShapes)
 * &nbsp;&nbsp;&nbsp;The shape file(s) to use for the map background.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-fill-color &lt;java.awt.Color&gt; [-fill-color ...] (property: fillColors)
 * &nbsp;&nbsp;&nbsp;The corresponding fill color(s) for the shape file(s).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-background-image &lt;adams.core.io.PlaceholderFile&gt; [-background-image ...] (property: backgroundImages)
 * &nbsp;&nbsp;&nbsp;The image file(s) to use for the map background.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-raster-type &lt;GREYSCALE|RGB&gt; [-raster-type ...] (property: rasterTypes)
 * &nbsp;&nbsp;&nbsp;The corresponding raster types of the background images.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-greyscale-band &lt;adams.core.base.BaseInteger&gt; [-greyscale-band ...] (property: greyScaleBands)
 * &nbsp;&nbsp;&nbsp;The corresponding greyscale band(s) to use for the background images.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-anti-aliasing-enabled &lt;boolean&gt; (property: antiAliasingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, uses anti-aliasing for drawing lines.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-info-tool &lt;adams.flow.sink.infotool.AbstractInfoTool&gt; (property: infoTool)
 * &nbsp;&nbsp;&nbsp;The info-tool to use for displaying details on locations on the map (when 
 * &nbsp;&nbsp;&nbsp;clicked).
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.infotool.DefaultInfoTool
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GeoToolsMapDisplay
  extends AbstractGraphicalDisplay
  implements AntiAliasingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3496431077256074404L;

  /** the background color. */
  protected Color m_BackgroundColor;

  /** the shapefiles to use as background. */
  protected PlaceholderFile[] m_BackgroundShapes;

  /** the fill colors. */
  protected Color[] m_FillColors;

  /** the raster images to use as background. */
  protected PlaceholderFile[] m_BackgroundImages;

  /** the raster types. */
  protected RasterType[] m_RasterTypes;
  
  /** the greyscale band. */
  protected BaseInteger[] m_GreyScaleBands;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;
  
  /** the info tool to use. */
  protected InfoToolSupplier m_InfoTool;

  /** the background layer. */
  protected List<Layer> m_BackgroundLayers;

  /** the data layer. */
  protected List<Layer> m_DataLayers;
  
  /** the panel. */
  protected MapDisplayPanel m_PanelMap;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Displays the incoming data using the specified raster image or "
	+ "shape files as background. Raster images get added first before "
	+ "shape files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "background-color", "backgroundColor",
	    Color.WHITE);

    m_OptionManager.add(
	    "background-shape", "backgroundShapes",
	    new PlaceholderFile[0]);

    m_OptionManager.add(
	    "fill-color", "fillColors",
	    new Color[0]);

    m_OptionManager.add(
	    "background-image", "backgroundImages",
	    new PlaceholderFile[0]);

    m_OptionManager.add(
	    "raster-type", "rasterTypes",
	    new RasterType[0]);

    m_OptionManager.add(
	    "greyscale-band", "greyScaleBands",
	    new BaseInteger[0]);

    m_OptionManager.add(
	    "anti-aliasing-enabled", "antiAliasingEnabled",
	    false);

    m_OptionManager.add(
	    "info-tool", "infoTool",
	    new DefaultInfoToolSupplier());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_BackgroundLayers = new ArrayList<Layer>();
    m_DataLayers       = new ArrayList<Layer>();
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "backgroundShapes", Utils.flatten(m_BackgroundShapes, ", "), "shapes: ");
    result += QuickInfoHelper.toString(this, "antiAliasingEnabled", m_AntiAliasingEnabled, "anti-aliasing", ", ");
    result += QuickInfoHelper.toString(this, "infoTool", m_InfoTool, ", info: ");

    return result;
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 1000;
  }

  /**
   * Sets the background color of the map.
   *
   * @param value	the background color
   */
  public void setBackgroundColor(Color value) {
    m_BackgroundColor = value;
    reset();
  }

  /**
   * Returns the background color of the map.
   *
   * @return		the background color
   */
  public Color getBackgroundColor() {
    return m_BackgroundColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundColorTipText() {
    return "The background color of the map.";
  }
  
  /**
   * Sets the shape files to use as background.
   *
   * @param value	the shape files
   */
  public void setBackgroundShapes(PlaceholderFile[] value) {
    m_BackgroundShapes = value;
    m_FillColors       = (Color[]) Utils.adjustArray(m_FillColors, m_BackgroundShapes.length, Color.BLACK);
    reset();
  }

  /**
   * Returns the shape files to use as background.
   *
   * @return		the shape files
   */
  public PlaceholderFile[] getBackgroundShapes() {
    return m_BackgroundShapes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundShapesTipText() {
    return "The shape file(s) to use for the map background.";
  }
  
  /**
   * Sets the fill colors for the shape files.
   *
   * @param value	the fill colors
   */
  public void setFillColors(Color[] value) {
    m_FillColors = (Color[]) Utils.adjustArray(value, m_BackgroundShapes.length, Color.BLACK);
    reset();
  }

  /**
   * Returns the fill colors for the shape files.
   *
   * @return		the fill colors
   */
  public Color[] getFillColors() {
    return m_FillColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillColorsTipText() {
    return "The corresponding fill color(s) for the shape file(s).";
  }
  
  /**
   * Sets the image files to use as background.
   *
   * @param value	the image files
   */
  public void setBackgroundImages(PlaceholderFile[] value) {
    m_BackgroundImages = value;
    m_RasterTypes       = (RasterType[]) Utils.adjustArray(m_RasterTypes, m_BackgroundImages.length, RasterType.RGB);
    m_GreyScaleBands    = (BaseInteger[]) Utils.adjustArray(m_GreyScaleBands, m_BackgroundImages.length, new BaseInteger(1));
    reset();
  }

  /**
   * Returns the image files to use as background.
   *
   * @return		the image files
   */
  public PlaceholderFile[] getBackgroundImages() {
    return m_BackgroundImages;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundImagesTipText() {
    return "The image file(s) to use for the map background.";
  }
  
  /**
   * Sets the raster types to display.
   *
   * @param value	the types
   */
  public void setRasterTypes(RasterType[] value) {
    m_RasterTypes = (RasterType[]) Utils.adjustArray(value, m_BackgroundImages.length, RasterType.RGB);
    reset();
  }

  /**
   * Returns the raster types to display.
   *
   * @return		the types
   */
  public RasterType[] getRasterTypes() {
    return m_RasterTypes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rasterTypesTipText() {
    return "The corresponding raster types of the background images.";
  }
  
  /**
   * Sets the greyscale bands to use for the background images.
   *
   * @param value	the bands
   */
  public void setGreyScaleBands(BaseInteger[] value) {
    m_GreyScaleBands = (BaseInteger[]) Utils.adjustArray(value, m_BackgroundImages.length, new BaseInteger(1));
    reset();
  }

  /**
   * Returns the greyscale bands to use for the background images.
   *
   * @return		the bands
   */
  public BaseInteger[] getGreyScaleBands() {
    return m_GreyScaleBands;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String greyScaleBandsTipText() {
    return "The corresponding greyscale band(s) to use for the background images.";
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    reset();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "If enabled, uses anti-aliasing for drawing lines.";
  }
  
  /**
   * Sets the info tool to use.
   * 
   * @param value	the info tool
   */
  public void setInfoTool(InfoToolSupplier value) {
    m_InfoTool = value;
    reset();
  }
  
  /**
   * Returns the current info tool.
   * 
   * @return		the info tool
   */
  public InfoToolSupplier getInfoTool() {
    return m_InfoTool;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String infoToolTipText() {
    return "The info-tool to use for displaying details on locations on the map (when clicked).";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Layer.class};
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel				result;
    int					i;
    FileDataStore 			store;
    SimpleFeatureSource[] 		featureSource;
    Style 				style;
    Layer				layer;
    AbstractGridFormat 			format;
    AbstractGridCoverage2DReader	reader;
    InfoToolSupplier			infoTool;
    
    result = new BasePanel(new BorderLayout());

    m_BackgroundLayers.clear();
    m_DataLayers.clear();

    infoTool = m_InfoTool.shallowCopy(true);
    if (infoTool.requiresDatabaseConnection())
      infoTool.updateDatabaseConnection(this);
    
    m_PanelMap = new MapDisplayPanel();
    m_PanelMap.getMapPane().setBackground(m_BackgroundColor);
    m_PanelMap.setAntiAliasingEnabled(m_AntiAliasingEnabled);
    m_PanelMap.setInfoTool(infoTool);

    // raster files
    for (i = 0; i < m_BackgroundImages.length; i++) {
      try {
	format = GridFormatFinder.findFormat(m_BackgroundImages[i].getAbsoluteFile());        
	reader = format.getReader(m_BackgroundImages[i].getAbsoluteFile());
      }
      catch (Exception e) {
	handleException("Failed to load raster file : " + m_BackgroundImages[i], e);
	return result;
      }

      switch (m_RasterTypes[i]) {
	case GREYSCALE:
	  style = RasterImageHelper.createGreyscaleStyle(m_GreyScaleBands[i].intValue());
	  break;
	case RGB:
	  style = RasterImageHelper.createRGBStyle(reader);
	  break;
	default:
	  throw new IllegalStateException("Unhandled raster type: " + m_RasterTypes[i]);
      }
      layer = new GridReaderLayer(reader, style);
      m_BackgroundLayers.add(layer);
      m_PanelMap.addLayer(layer);
    }
    
    // shape files
    featureSource = new SimpleFeatureSource[m_BackgroundShapes.length];
    for (i = 0; i < m_BackgroundShapes.length; i++) {
      try {
	store            = FileDataStoreFinder.getDataStore(m_BackgroundShapes[i].getAbsoluteFile());
	featureSource[i] = store.getFeatureSource();
      }
      catch (Exception e) {
	handleException("Failed to load shape file #" + (i+1) + ": " + m_BackgroundShapes[i], e);
	return result;
      }
    }
    for (i = 0; i < featureSource.length; i++) {
      style = SLD.createSimpleStyle(featureSource[i].getSchema(), m_FillColors[i]);
      layer = new FeatureLayer(featureSource[i], style);
      m_BackgroundLayers.add(layer);
      m_PanelMap.addLayer(layer);
    }
    
    result.add(m_PanelMap, BorderLayout.CENTER);
    
    return result;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    m_PanelMap.removeAllLayers();
    m_DataLayers.clear();
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    m_DataLayers.add((Layer) token.getPayload());
    m_PanelMap.addLayer((Layer) token.getPayload());
  }
  
  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    int		i;
    
    result = super.setUp();
    
    if (result == null) {
      for (i = 0; i < m_BackgroundImages.length; i++) {
	if (!m_BackgroundImages[i].exists())
	  result = "Raster file #" + (i+1) + " '" + m_BackgroundImages[i] + "' does not exist!";
	else if (m_BackgroundImages[i].isDirectory())
	  result = "Raster file #" + (i+1) + " '" + m_BackgroundImages[i] + "' points to a directory!";
      }
    }
    
    if (result == null) {
      for (i = 0; i < m_BackgroundShapes.length; i++) {
	if (!m_BackgroundShapes[i].exists())
	  result = "Shape file #" + (i+1) + " '" + m_BackgroundShapes[i] + "' does not exist!";
	else if (m_BackgroundShapes[i].isDirectory())
	  result = "Shape file #" + (i+1) + " '" + m_BackgroundShapes[i] + "' points to a directory!";
      }
    }
    
    return result;
  }

  /**
   * Returns the current component.
   *
   * @return		the current component, can be null
   */
  @Override
  public JComponent supplyComponent() {
    return m_PanelMap.getMapPane();
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		true if supported
   */
  @Override
  protected boolean supportsClear() {
    return true;
  }

  /**
   * Clears the display.
   */
  @Override
  protected void clear() {
    m_PanelMap.removeAllLayers();
  }

  @Override
  public void wrapUp() {
    Runnable	run;
    
    run = new Runnable() {
      @Override
      public void run() {
	m_PanelMap.reset();
      }
    };
    SwingUtilities.invokeLater(run);
    
    super.wrapUp();
  }
  
  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_BackgroundLayers != null) {
      for (Layer layer: m_BackgroundLayers)
	layer.dispose();
      m_BackgroundLayers.clear();
    }
    if (m_DataLayers != null) {
      for (Layer layer: m_DataLayers)
	layer.dispose();
      m_DataLayers.clear();
    }
    if (m_PanelMap != null) {
      m_PanelMap.cleanUp();
      m_PanelMap = null;
    }
    
    super.cleanUp();
  }
}
