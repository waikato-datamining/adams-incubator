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
 * SimpleCircleGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.feature;

import java.awt.Color;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
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
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

/**
 <!-- globalinfo-start -->
 * Generates a circle feature layer, using the GPS coordinates column in the spreadsheet as center for the circle, the radius column for the radius of the circle and adds any additionally defined colunms as attributes to the feature points.
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
 * &nbsp;&nbsp;&nbsp;default: SimpleCircleGenerator
 * </pre>
 * 
 * <pre>-gps &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: GPS)
 * &nbsp;&nbsp;&nbsp;The index of the column containing the GPS objects.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-radius &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: radius)
 * &nbsp;&nbsp;&nbsp;The index of the column containing the radius.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-num-points &lt;int&gt; (property: numPoints)
 * &nbsp;&nbsp;&nbsp;The number of points to use for drawing the circle.
 * &nbsp;&nbsp;&nbsp;default: 36
 * &nbsp;&nbsp;&nbsp;minimum: 2
 * </pre>
 * 
 * <pre>-additional-attributes &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: additionalAttributes)
 * &nbsp;&nbsp;&nbsp;The range of column to add to the features as well.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;The fill color for the point.
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
public class SimpleCircleGenerator
  extends AbstractFeatureGenerator<SimpleFeature, DefaultFeatureCollection, FeatureLayer> {

  /** for serialization. */
  private static final long serialVersionUID = 8557388938912123237L;

  /** the index of the GPS coordinates column. */
  protected SpreadSheetColumnIndex m_GPS;

  /** the actual index of the GPS coordinates column. */
  protected int m_GPSIndex;

  /** the index of the column with the radius information. */
  protected SpreadSheetColumnIndex m_Radius;

  /** the actual index of the radius column. */
  protected int m_RadiusIndex;

  /** the number of points for the radius. */
  protected int m_NumPoints;

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
  protected transient GeometricShapeFactory m_GeometryFactory;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a circle feature layer, using the GPS coordinates column in the "
	+ "spreadsheet as center for the circle, the radius column for the radius "
	+ "of the circle and adds any additionally defined colunms as attributes "
	+ "to the feature points.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "gps", "GPS",
	    new SpreadSheetColumnIndex());

    m_OptionManager.add(
	    "radius", "radius",
	    new SpreadSheetColumnIndex());

    m_OptionManager.add(
	    "num-points", "numPoints",
	    36, 2, null);

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
   * Sets the index of the column containing the GPS objects.
   *
   * @param value	the column index
   */
  public void setGPS(SpreadSheetColumnIndex value) {
    m_GPS = value;
    reset();
  }

  /**
   * Returns the index of the column containing the GPS objects.
   *
   * @return		the column index
   */
  public SpreadSheetColumnIndex getGPS() {
    return m_GPS;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String GPSTipText() {
    return "The index of the column containing the GPS objects.";
  }

  /**
   * Sets the index of the column containing the radius.
   *
   * @param value	the column index
   */
  public void setRadius(SpreadSheetColumnIndex value) {
    m_Radius = value;
    reset();
  }

  /**
   * Returns the index of the column containing the radius.
   *
   * @return		the column index
   */
  public SpreadSheetColumnIndex getRadius() {
    return m_Radius;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String radiusTipText() {
    return "The index of the column containing the radius.";
  }

  /**
   * Sets the number of points to use for drawing the circle.
   *
   * @param value	the number of points (> 1)
   */
  public void setNumPoints(int value) {
    if (value > 1) {
      m_NumPoints = value;
      reset();
    }
    else {
      getLogger().warning("At least 2 data points required, provided: " + value);
    }
  }

  /**
   * Returns the number of points for drawing the circle.
   *
   * @return		the number of points
   */
  public int getNumPoints() {
    return m_NumPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsTipText() {
    return "The number of points to use for drawing the circle.";
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
    return "The fill color for the point.";
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
    result += QuickInfoHelper.toString(this, "radius", m_Radius, ", radius: ");
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
    m_Radius.setData(sheet);
    m_AdditionalAttributes.setData(sheet);

    if (m_GPS.getIntIndex() == -1)
      throw new IllegalArgumentException("Column with GPS objects not found: " + m_GPS.getIndex());
    if (m_Radius.getIntIndex() == -1)
      throw new IllegalArgumentException("Column with radius not found: " + m_Radius.getIndex());
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
    
    m_GPSIndex                    = m_GPS.getIntIndex();
    m_RadiusIndex                 = m_Radius.getIntIndex();
    m_AdditionalAttributesIndices = m_AdditionalAttributes.getIntIndices();
    
    builder = new SimpleFeatureTypeBuilder();
    builder.setName(getFeatureTypeName());
    builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

    // GPS location
    builder.add(GEOMETRY, Polygon.class);
    
    // additional attributes
    m_AdditionalAttributesType = new ContentType[m_AdditionalAttributesIndices.length];
    for (i = 0; i < m_AdditionalAttributesIndices.length; i++) {
      if (m_AdditionalAttributesIndices[i] == m_GPSIndex) {
	m_AdditionalAttributesIndices[i] = -1;
	m_AdditionalAttributesType[i]    = null;
	continue;
      }
      if (sheet.isNumeric(m_AdditionalAttributesIndices[i])) {
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
    m_GeometryFactory = new GeometricShapeFactory();
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
    double		radius;
    int			i;
    
    location = (AbstractGPS) row.getCell(m_GPSIndex).getObject();
    radius   = row.getCell(m_RadiusIndex).toDouble();
    
    m_GeometryFactory.setNumPoints(m_NumPoints);
    m_GeometryFactory.setSize(radius * 2);
    m_GeometryFactory.setCentre(new Coordinate(
	    location.getLongitude().toDecimal(), 
	    location.getLatitude().toDecimal()));
    m_Builder.add(m_GeometryFactory.createCircle());
    
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
