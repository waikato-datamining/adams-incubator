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
 * AbstractFeatureGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.feature;

import org.geotools.feature.FeatureCollection;
import org.geotools.map.Layer;
import org.opengis.feature.Feature;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for classes that generate GeoTools features from spreadsheets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <F> the type of feature
 * @param <C> the type of feature collection
 * @param <L> the type of layer
 */
public abstract class AbstractFeatureGenerator<F extends Feature, C extends FeatureCollection, L extends Layer>
  extends AbstractOptionHandler
  implements ShallowCopySupporter<AbstractFeatureGenerator>, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -9220651981409562505L;

  /** the constant for the feature builder. */
  public final static String GEOMETRY = "the_geom";
  
  /** the name of the feature type. */
  protected String m_FeatureTypeName;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "featuretype-name", "featureTypeName",
	    getDefaultFeatureTypeName());
  }
  
  /**
   * Returns the default name for the feature type.
   * 
   * @return		the name of the feature type to generate
   */
  protected String getDefaultFeatureTypeName() {
    return getClass().getSimpleName();
  }

  /**
   * Sets the name for the feature type to generate.
   *
   * @param value	the name
   */
  public void setFeatureTypeName(String value) {
    m_FeatureTypeName = value;
    reset();
  }

  /**
   * Returns the name for the feature type to generate.
   *
   * @return		the name
   */
  public String getFeatureTypeName() {
    return m_FeatureTypeName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String featureTypeNameTipText() {
    return "The name to use for the feature type.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "featureTypeName", m_FeatureTypeName, "Name: ");
  }

  /**
   * Checks the spreadsheet and throws an exception if it fails.
   * <br><br>
   * Default implementation only ensures that data is present.
   * 
   * @param sheet	the spreadsheet to check
   */
  protected void check(SpreadSheet sheet) {
    if (sheet == null)
      throw new IllegalArgumentException("No spreadsheet supplied!");
  }

  /**
   * Performs initializations specific to this spreadsheet.
   * 
   * @param sheet	the spreadsheet to initialize with
   */
  protected abstract void init(SpreadSheet sheet);
  
  /**
   * Creates a new and empty feature collection instance.
   * 
   * @return		the collection
   */
  protected abstract C newFeatureCollection();
  
  /**
   * Adds the feature to the collection.
   * 
   * @param collection	the collection to extend
   * @param feature	the feature to add
   * @return		true if successfully added
   */
  protected abstract boolean addFeature(C collection, F feature);
  
  /**
   * Generates a single feature per row.
   * 
   * @param row		the row to create the feature for
   * @return		the generated feature, null if failed to generate
   */
  protected abstract F doGenerateFeature(Row row);
  
  /**
   * Generates a single feature per row.
   * 
   * @param row		the row to create the feature for
   * @return		the generated feature, null if failed to generate
   */
  public F generateFeature(Row row) {
    F		result;
    
    check(row.getOwner());
    init(row.getOwner());
    
    result = doGenerateFeature(row);
    
    return result;
  }
  
  /**
   * Generates the features from the given spreadsheet and adds them
   * to the feature collection.
   * 
   * @param sheet	the spreadsheet to use
   * @return		the generated feature collection
   */
  protected C doGenerateCollection(SpreadSheet sheet) {
    C		result;
    F		feature;
    int		i;
    
    result = newFeatureCollection();
    
    for (i = 0; i < sheet.getRowCount(); i++) {
      feature = doGenerateFeature(sheet.getRow(i));
      if (feature == null) {
	getLogger().warning("Failed to generate feature for row #" + (i+1) + ": " + sheet.getRow(i));
      }
      else {
	if (!addFeature(result, feature))
	  getLogger().warning("Feature generated for row #" + (i+1) + " not added to collection: " + feature);
      }
    }
    
    return result;
  }
  
  /**
   * Generates a feature collection from the given spreadsheet.
   * 
   * @param sheet	the spreadsheet to process
   * @return		the generated feature collection
   */
  public C generateCollection(SpreadSheet sheet) {
    C		result;
    
    check(sheet);
    init(sheet);
    
    result = doGenerateCollection(sheet);
    
    return result;
  }
  
  /**
   * Generates the actual layer from the collection.
   * 
   * @param collection	the collection to use
   * @return		the generated layer
   */
  protected abstract L doGenerateLayer(C collection);
  
  /**
   * Generates a layer from the given spreadsheet.
   * 
   * @param sheet	the spreadsheet to process
   * @return		the generated feature collection
   */
  public L generateLayer(SpreadSheet sheet) {
    L	result;
    C	collection;
    
    check(sheet);
    init(sheet);
    
    collection = doGenerateCollection(sheet);
    result     = doGenerateLayer(collection);
    result.setTitle(m_FeatureTypeName);
    
    return result;
  }
  
  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractFeatureGenerator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractFeatureGenerator shallowCopy(boolean expand) {
    return (AbstractFeatureGenerator) OptionUtils.shallowCopy(this, expand);
  }
}
