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
 * OpenCVImageContainer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.opencv;

import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.IplImage;

import adams.data.image.AbstractImageContainer;

/**
 * Container for an OpenCV {@link IplImage} image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenCVImageContainer
  extends AbstractImageContainer<IplImage>{

  /** for serialization. */
  private static final long serialVersionUID = 7581109072384001808L;

  /**
   * Returns the width of the image.
   * 
   * @return		the width
   */
  @Override
  public int getWidth() {
    if (m_Content == null)
      return 0;
    else
      return m_Content.width();
  }

  /**
   * Returns the height of the image.
   * 
   * @return		the height
   */
  @Override
  public int getHeight() {
    if (m_Content == null)
      return 0;
    else
      return m_Content.height();
  }
  
  /**
   * Returns a clone of the image. Actually, only for {@link ImageSingleBand}
   * a clone is returned, all other types are a "subimage" with the same
   * size as the original.
   * 
   * @return		the clone/subimage
   */
  @Override
  protected IplImage cloneContent() {
    return m_Content.clone();
  }

  /**
   * Turns the image into a buffered image.
   * 
   * @return		the buffered image
   */
  @Override
  public BufferedImage toBufferedImage() {
    return m_Content.getBufferedImage();
  }
}
