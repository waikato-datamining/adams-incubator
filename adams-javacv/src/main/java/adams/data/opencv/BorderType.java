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
 * BorderType.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.opencv;

import org.bytedeco.javacpp.opencv_core;

/**
 * The border types.
 * <br><br>
 * See also:
 * <a href="http://docs.opencv.org/modules/imgproc/doc/filtering.html" 
 * target="_blank">http://docs.opencv.org/modules/imgproc/doc/filtering.html</a>
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum BorderType {
  CONSTANT(opencv_core.BORDER_CONSTANT),
  DEFAULT(opencv_core.BORDER_DEFAULT),
  ISOLATED(opencv_core.BORDER_ISOLATED),
  TRANSPARENT(opencv_core.BORDER_TRANSPARENT),
  REPLICATE(opencv_core.BORDER_REPLICATE),
  REFLECT(opencv_core.BORDER_REFLECT),
  REFLECT_101(opencv_core.BORDER_REFLECT_101),
  WRAP(opencv_core.BORDER_WRAP);
  
  /** the associated integer value. */
  private int m_Border;
  
  /**
   * Initializes the enum.
   * 
   * @param border	the associated border
   */
  private BorderType(int border) {
    m_Border = border;
  }
  
  /**
   * Returns the associated border.
   * 
   * @return		the border
   */
  public int getBorder() {
    return m_Border;
  }
}
