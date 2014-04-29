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
 * AbstractGPSDistance.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser.plugin;

import adams.data.gps.AbstractGPS;

/**
 * Ancestor for functions that calculate the distance between two GPS locations 
 * or two pairs of longitude/latitude.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGPSDistance
  extends AbstractParserFunction {
  
  /** for serialization. */
  private static final long serialVersionUID = 7011189038406822521L;

  /**
   * Returns the function name.
   * Can only consist of letters and underscores.
   * 
   * @return		the name of the function
   */
  @Override
  public abstract String getFunctionName();

  /**
   * Returns the signature of the function.
   * 
   * @return		the signature
   */
  @Override
  public String getFunctionSignature() {
    return getFunctionName() + "(gps1,gps2|long1,lat1,long2,lat2)";
  }

  /**
   * Returns the details about this distance functions.
   * 
   * @return		the help string (one or more sentences, no trailing LF)
   */
  protected abstract String getFunctionDetails();
  
  /**
   * Returns the help string for the function.
   * 
   * @return		the help string
   */
  @Override
  public String getFunctionHelp() {
    return 
	getFunctionSignature() + "\n"
	+ "Computes the distance between two locations.\n"
	+ getFunctionDetails() + "\n"
	+ "In case of gps1/gps2 as parameters, the objects must be derived "
	+ "from " + AbstractGPS.class.getName() + ".\n"
	+ "Otherwise the parameters must be doubles.";
  }

  /**
   * Performs some checks on the input parameters.
   * 
   * @param params	the input parameters
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String check(Object[] params) {
    String	result;
    int		i;
    
    result = null;
    
    if (params.length == 2) {
      for (i = 0; i < params.length; i++) {
	if (!(params[i] instanceof AbstractGPS))
	  result = "Parameter " + (i+1) + " is not derived from " + AbstractGPS.class.getName() + "!";
	if (result != null)
	  break;
      }
    }
    else if (params.length == 4) {
      for (i = 0; i < params.length; i++) {
	if (!(params[i] instanceof Number))
	  result = "Parameter " + (i+1) + " is not a number!";
	if (result != null)
	  break;
      }
    }
    else {
      result = "Either two parameters (GPS objects) or 4 parameters (doubles) required!";
    }
    
    return result;
  }
  
  /**
   * Calculates the distance.
   * 
   * @param long1	the longitude of the first location
   * @param lat1	the latitude of the first location
   * @param long2	the longitude of the second location
   * @param lat2	the latitude of the second location
   */
  protected abstract double distance(double long1, double lat1, double long2, double lat2);

  /**
   * Gets called from the parser.
   * 
   * @param params	the parameters obtained through the parser
   * @return		the result to be used further in the parser
   */
  @Override
  protected Object doCallFunction(Object[] params) {
    double 	long1;
    double	lat1;
    double	long2;
    double	lat2;
    
    if (params.length == 2) {
      long1 = ((AbstractGPS) params[0]).getLongitude().toDecimal();
      lat1  = ((AbstractGPS) params[0]).getLatitude().toDecimal();
      long2 = ((AbstractGPS) params[1]).getLongitude().toDecimal();
      lat2  = ((AbstractGPS) params[1]).getLatitude().toDecimal();
    }
    else {
      long1 = ((Number) params[0]).doubleValue();
      lat1  = ((Number) params[1]).doubleValue();
      long2 = ((Number) params[2]).doubleValue();
      lat2  = ((Number) params[3]).doubleValue();
    }
    
    return distance(long1, lat1, long2, lat2);
  }
}
