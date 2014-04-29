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
 * CWBQueryHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import gov.usgs.anss.query.EdgeQueryOptions;

import java.util.ArrayList;
import java.util.List;

import adams.core.base.BaseString;

/**
 * Helper class for CWBQuery.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CWBQueryHelper {

  /**
   * Turns a {@link BaseString} array into an {@link EdgeQueryOptions} object.
   * 
   * @param opts	the options to turn into a {@link EdgeQueryOptions} object
   * @param omitType	whether to omit the type parameter ('-t')
   * @return		the generated options
   */
  public static EdgeQueryOptions toOptions(BaseString[] opts, boolean omitType) {
    EdgeQueryOptions	result;
    List<String>	options;
    int			i;
    
    options = new ArrayList<String>();
    if (omitType) {
      options.add("-t");
      options.add("NULL");
    }
    for (i = 0; i < opts.length; i++) {
      // skip type
      if (opts[i].getValue().equals("-t") && omitType) {
	i++;
	continue;
      }
      options.add(opts[i].getValue());
    }
    result = new EdgeQueryOptions(options.toArray(new String[options.size()]));
    
    return result;
  }
}
