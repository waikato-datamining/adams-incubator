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
 * MorphShape.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.opencv;

import org.bytedeco.javacpp.opencv_imgproc;

/**
 * Shapes of morphological operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum MorphShape {
  RECT(opencv_imgproc.MORPH_RECT),
  CROSS(opencv_imgproc.MORPH_CROSS),
  ELLIPSE(opencv_imgproc.MORPH_ELLIPSE);
  
  /** the associated shape. */
  private int m_Shape;
  
  /**
   * Initializes the enum element.
   * 
   * @param shape	the shape to associate
   */
  private MorphShape(int shape) {
    m_Shape = shape;
  }
  
  /**
   * Returns the associated shape.
   * 
   * @return		the shape
   */
  public int getShape() {
    return m_Shape;
  }
}
