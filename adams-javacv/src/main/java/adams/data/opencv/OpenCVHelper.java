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
 * OpenCVHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.opencv;

import org.bytedeco.javacpp.opencv_core.IplImage;

import adams.data.Notes;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;

/**
 * Helper class for OpenCV-related operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenCVHelper {
  
  /**
   * Creates a {@link BufferedImageContainer} container if necessary, otherwise
   * it just casts the object.
   * 
   * @param cont	the cont to cast/convert
   * @return		the casted/converted container
   */
  public static BufferedImageContainer toBufferedImageContainer(AbstractImageContainer cont) {
    BufferedImageContainer	result;
    Report			report;
    Notes			notes;
    
    if (cont instanceof BufferedImageContainer)
      return (BufferedImageContainer) cont;

    report = cont.getReport().getClone();
    notes  = cont.getNotes().getClone();
    result = new BufferedImageContainer();
    result.setImage(cont.toBufferedImage());
    result.setReport(report);
    result.setNotes(notes);
    
    return result;
  }
  
  /**
   * Creates a {@link OpenCVImageContainer} container if necessary, otherwise
   * it just casts the object.
   * 
   * @param cont	the cont to cast/convert
   * @return		the casted/converted container
   */
  public static OpenCVImageContainer toOpenCVImageContainer(AbstractImageContainer cont) {
    OpenCVImageContainer	result;
    Report			report;
    Notes			notes;
    IplImage			img;
    
    if (cont instanceof OpenCVImageContainer)
      return (OpenCVImageContainer) cont;

    report = cont.getReport().getClone();
    notes  = cont.getNotes().getClone();
    result = new OpenCVImageContainer();
    img    = new IplImage();
    img.copyFrom(cont.toBufferedImage());
    result.setImage(img);
    result.setReport(report);
    result.setNotes(notes);
    
    return result;
  }
}
