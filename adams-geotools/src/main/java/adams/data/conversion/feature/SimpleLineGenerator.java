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
 * SimpleLineGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.feature;

import gnu.trove.set.hash.TIntHashSet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import adams.core.QuickInfoHelper;
import adams.data.gps.AbstractGPS;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

/**
 <!-- globalinfo-start -->
 * Generates a line feature layer, using the GPS coordinates columns in the spreadsheet (at least columns required) and adds any additionally defined colunms as attributes to the feature points.<br/>
 * NB: Lines don't have to have the same number of segments (ie GPS locations). Any missing cells get ignored from the defined GPS range.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: INFO
 * </pre>
 * 
 * <pre>-featuretype-name &lt;java.lang.String&gt; (property: featureTypeName)
 * &nbsp;&nbsp;&nbsp;The name to use for the feature type.
 * &nbsp;&nbsp;&nbsp;default: SimpleLineGenerator
 * </pre>
 * 
 * <pre>-gps &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: GPS)
 * &nbsp;&nbsp;&nbsp;The range of the columns containing the GPS objects for the line (at least 
 * &nbsp;&nbsp;&nbsp;two columns required).
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-additional-attributes &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: additionalAttributes)
 * &nbsp;&nbsp;&nbsp;The range of column to add to the features as well.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;The fill color for the line.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-invisible &lt;boolean&gt; (property: invisible)
 * &nbsp;&nbsp;&nbsp;If enabled, an invisible data layer gets created.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleLineGenerator
  extends AbstractFeatureGenerator<SimpleFeature, DefaultFeatureCollection, FeatureLayer> {

  /** for serialization. */
  private static final long serialVersionUID = 8557388938912123237L;

  /** the index of the GPS coordinates columns. */
  protected SpreadSheetColumnRange m_GPS;

  /** the actual indices of the GPS coordinates columns. */
  protected int[] m_GPSIndices;

  /** the additional attributes to store in the features. */
  protected SpreadSheetColumnRange m_AdditionalAttributes;

  /** the actual indices of the additional attributes to store in the features. */
  protected int[] m_AdditionalAttributesIndices;
  
  /** the type of the additional attributes. */
  protected ContentType[] m_AdditionalAttributesType;

  /** the fill color to use. */
  protected Color m_FillColor;
  
  /** whether to create an invisible data layer. */
  protected boolean m_Invisible;

  /** the builder to use. */
  protected transient SimpleFeatureBuilder m_Builder;
  
  /** the feature type in use. */
  protected transient SimpleFeatureType m_FeatureType;
  
  /** the geometry factory to use. */
  protected transient GeometryFactory m_GeometryFactory;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a line feature layer, using the GPS coordinates columns in the "
	+ "spreadsheet (at least columns required) and adds any additionally "
	+ "defined colunms as attributes to the feature points.\n"
	+ "NB: Lines don't have to have the same number of segments (ie GPS locations). "
	+ "Any missing cells get ignored from the defined GPS range.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "gps", "GPS",
	    new SpreadSheetColumnRange());

    m_OptionManager.add(
	    "additional-attributes", "additionalAttributes",
	    new SpreadSheetColumnRange());

    m_OptionManager.add(
	    "fill-color", "fillColor",
	    Color.BLACK);

    m_OptionManager.add(
	    "invisible", "invisible",
	    false);
  }

  /**
   * Sets the range of the columns containing the GPS objects.
   *
   * @param value	the column range
   */
  public void setGPS(SpreadSheetColumnRange value) {
    m_GPS = value;
    reset();
  }

  /**
   * Returns the range of the columns containing the GPS objects.
   *
   * @return		the column range
   */
  public SpreadSheetColumnRange getGPS() {
    return m_GPS;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String GPSTipText() {
    return "The range of the columns containing the GPS objects for the line (at least two columns required).";
  }

  /**
   * Sets the range of columns of additional attributes to add to the feature.
   *
   * @param value	the additional columns
   */
  public void setAdditionalAttributes(SpreadSheetColumnRange value) {
    m_AdditionalAttributes = value;
    reset();
  }

  /**
   * Returns the range of columns of additional attributes to add to the feature.
   *
   * @return		the additional columns
   */
  public SpreadSheetColumnRange getAdditionalAttributes() {
    return m_AdditionalAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalAttributesTipText() {
    return "The range of column to add to the features as well.";
  }

  /**
   * Sets the fill color for the collection.
   *
   * @param value	the fill color
   */
  public void setFillColor(Color value) {
    m_FillColor = value;
    reset();
  }

  /**
   * Returns the fill color for the collection.
   *
   * @return		the fill color
   */
  public Color getFillColor() {
    return m_FillColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillColorTipText() {
    return "The fill color for the line.";
  }

  /**
   * Sets whether to create an invisible data layer.
   *
   * @param value	true if invisible
   */
  public void setInvisible(boolean value) {
    m_Invisible = value;
    reset();
  }

  /**
   * Returns whether to create an invisible data layer.
   *
   * @return		true if invisible
   */
  public boolean getInvisible() {
    return m_Invisible;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invisibleTipText() {
    return "If enabled, an invisible data layer gets created.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "GPS", m_GPS, ", GPS: ");
    result += QuickInfoHelper.toString(this, "additionalAttributes", m_AdditionalAttributes, ", additional: ");
    result += QuickInfoHelper.toString(this, "fillColor", m_FillColor, ", fill: ");
    result += QuickInfoHelper.toString(this, "invisible", m_Invisible, "invisible", ", ");

    return result;
  }

  /**
   * Checks the spreadsheet and throws an exception if it fails.
   * 
   * @param sheet	the spreadsheet to check
   */
  @Override
  protected void check(SpreadSheet sheet) {
    super.check(sheet);

    m_GPS.setData(sheet);
    m_AdditionalAttributes.setData(sheet);

    if (m_GPS.getIntIndices().length < 2)
      throw new IllegalArgumentException("Columns with GPS objects not found: " + m_GPS.getRange());
  }
  
  /**
   * Performs initializations specific to this spreadsheet.
   * 
   * @param sheet	the spreadsheet to initialize with
   */
  @Override
  protected void init(SpreadSheet sheet) {
    SimpleFeatureTypeBuilder	builder;
    int				i;
    TIntHashSet			indices;
    
    m_GPSIndices                  = m_GPS.getIntIndices();
    m_AdditionalAttributesIndices = m_AdditionalAttributes.getIntIndices();
    
    builder = new SimpleFeatureTypeBuilder();
    builder.setName(getFeatureTypeName());
    builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

    // GPS locations
    builder.add(GEOMETRY, LineString.class);
    
    // additional attributes
    indices                    = new TIntHashSet(m_GPSIndices);
    m_AdditionalAttributesType = new ContentType[m_AdditionalAttributesIndices.length];
    for (i = 0; i < m_AdditionalAttributesIndices.length; i++) {
      if (indices.contains(m_AdditionalAttributesIndices[i])) {
	m_AdditionalAttributesIndices[i] = -1;
	m_AdditionalAttributesType[i]    = null;
	continue;
      }
      if (sheet.isNumeric(m_AdditionalAttributesIndices[i], true)) {
	builder.add(sheet.getHeaderRow().getContent(m_AdditionalAttributesIndices[i]), Double.class);
	m_AdditionalAttributesType[i] = ContentType.DOUBLE;
      }
      else {
	builder.add(sheet.getHeaderRow().getContent(m_AdditionalAttributesIndices[i]), String.class);
	m_AdditionalAttributesType[i] = ContentType.STRING;
      }
    }

    m_FeatureType     = builder.buildFeatureType();
    m_Builder         = new SimpleFeatureBuilder(m_FeatureType);
    m_GeometryFactory = JTSFactoryFinder.getGeometryFactory(null);
  }

  /**
   * Creates a new and empty feature collection instance.
   * 
   * @return		the collection
   */
  @Override
  protected DefaultFeatureCollection newFeatureCollection() {
    return new DefaultFeatureCollection();
  }

  /**
   * Adds the feature to the collection.
   * 
   * @param collection	the collection to extend
   * @param feature	the feature to add
   * @return		true if successfully added
   */
  @Override
  protected boolean addFeature(DefaultFeatureCollection collection, SimpleFeature feature) {
    return collection.add(feature);
  }

  /**
   * Generates a single feature per row.
   * 
   * @param row		the row to create the feature for
   * @return		the generated feature, null if failed to generate
   */
  @Override
  protected SimpleFeature doGenerateFeature(Row row) {
    SimpleFeature	result;
    AbstractGPS		location;
    List<Coordinate>	coord;
    int			i;
    
    coord = new ArrayList<Coordinate>();
    for (i = 0; i < m_GPSIndices.length; i++) {
      if (!row.hasCell(m_GPSIndices[i]) || row.getCell(m_GPSIndices[i]).isMissing())
	continue;
      location = (AbstractGPS) row.getCell(m_GPSIndices[i]).getObject();
      coord.add(new Coordinate(
	    location.getLongitude().toDecimal(), 
	    location.getLatitude().toDecimal()));
    }
    
    m_Builder.add(m_GeometryFactory.createLineString(coord.toArray(new Coordinate[coord.size()])));
    
    for (i = 0; i < m_AdditionalAttributesIndices.length; i++) {
      if (m_AdditionalAttributesType[i] == null)
	continue;
      // missing?
      if (!row.hasCell(m_AdditionalAttributesIndices[i]) || row.getCell(m_AdditionalAttributesIndices[i]).isMissing()) {
	switch (m_AdditionalAttributesType[i]) {
	  case DOUBLE:
	    m_Builder.add(Double.NaN);
	    break;
	  default:
	    m_Builder.add("");
	}
      }
      else {
	switch (m_AdditionalAttributesType[i]) {
	  case DOUBLE:
	    m_Builder.add(row.getCell(m_AdditionalAttributesIndices[i]).toDouble());
	    break;
	  default:
	    m_Builder.add(row.getCell(m_AdditionalAttributesIndices[i]).getContent());
	}
      }
    }
    
    result = m_Builder.buildFeature(null);
    
    return result;
  }

  /**
   * Generates the actual layer from the collection.
   * 
   * @param collection	the collection to use
   * @return		the generated layer
   */
  @Override
  protected FeatureLayer doGenerateLayer(DefaultFeatureCollection collection) {
    FeatureLayer	result;
    Style 		style;

    if (m_Invisible) {
      result = new FeatureLayer(collection, new StyleImpl(){});
    }
    else {
      style  = SLD.createSimpleStyle(collection.getSchema(), m_FillColor);
      result = new FeatureLayer(collection, style);
    }
    
    return result;
  }
}
