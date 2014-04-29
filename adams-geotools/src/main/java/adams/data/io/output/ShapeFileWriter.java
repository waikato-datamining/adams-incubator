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
 * ShapeFileWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.map.FeatureLayer;

/**
 <!-- globalinfo-start -->
 * Writes the feature collection of the layer to a shape file.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the layer to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.shp
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ShapeFileWriter
  extends AbstractGeoToolsLayerWriter<FeatureLayer> {

  /** for serialization. */
  private static final long serialVersionUID = 1592574627548910901L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes the feature collection of the layer to a shape file.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Shape file";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"shp"};
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(FeatureLayer data) {
    ShapefileDataStoreFactory 	dataStoreFactory;
    Map<String, Serializable> 	params;
    ShapefileDataStore 		newDataStore;
    Transaction 		transaction;
    String 			typeName;
    SimpleFeatureSource 	featureSource;
    SimpleFeatureStore 		featureStore;

    try {
      dataStoreFactory = new ShapefileDataStoreFactory();

      params = new HashMap<String, Serializable>();
      params.put("url", m_Output.toURI().toURL());
      params.put("create spatial index", Boolean.TRUE);

      newDataStore  = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
      newDataStore.createSchema(data.getSimpleFeatureSource().getFeatures().getSchema());
      transaction   = new DefaultTransaction("create");
      typeName      = newDataStore.getTypeNames()[0];
      featureSource = newDataStore.getFeatureSource(typeName);

      if (featureSource instanceof SimpleFeatureStore) {
	featureStore = (SimpleFeatureStore) featureSource;
	featureStore.setTransaction(transaction);
	try {
	  featureStore.addFeatures(data.getSimpleFeatureSource().getFeatures());
	  transaction.commit();
	} 
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to write shape file to: " + m_Output, e);
	  transaction.rollback();
	} 
	finally {
	  transaction.close();
	}
      } 
      else {
	getLogger().severe(typeName + " does not support read/write access");
	return false;
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write shape file to: " + m_Output, e);
      return false;
    }

    return true;
  }
}
