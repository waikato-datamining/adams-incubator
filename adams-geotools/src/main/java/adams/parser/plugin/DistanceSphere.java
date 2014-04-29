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
 * DistanceSphere.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser.plugin;

import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.datum.DefaultEllipsoid;

/**
 * Calculates the distance between two GPS locations or two pairs of
 * longitude/latitude using a sphere ({@link DefaultEllipsoid#SPHERE}).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DistanceSphere
  extends AbstractGPSDistance {
  
  /** for serialization. */
  private static final long serialVersionUID = 7011189038406822521L;

  /**
   * Returns the function name.
   * Can only consist of letters, underscores, numbers.
   * 
   * @return		the name of the function
   */
  @Override
  public String getFunctionName() {
    return "distance_sphere";
  }

  /**
   * Returns the help string for the function.
   * 
   * @return		the help string
   */
  @Override
  public String getFunctionDetails() {
    return "Uses a sphere as basis for the calculation.";
  }
  
  /**
   * Calculates the distance.
   * 
   * @param long1	the longitude of the first location
   * @param lat1	the latitude of the first location
   * @param long2	the longitude of the second location
   * @param lat2	the latitude of the second location
   */
  @Override
  protected double distance(double long1, double lat1, double long2, double lat2) {
    GeodeticCalculator	calculator;
    
    calculator = new GeodeticCalculator(DefaultEllipsoid.SPHERE);
    calculator.setStartingGeographicPoint(long1, lat1);
    calculator.setDestinationGeographicPoint(long2, lat2);
    
    return calculator.getOrthodromicDistance();
  }
}
