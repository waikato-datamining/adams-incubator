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
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.opencv;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.Notes;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;

import java.awt.image.BufferedImage;

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
    
    if (cont instanceof OpenCVImageContainer)
      return (OpenCVImageContainer) cont;

    report = cont.getReport().getClone();
    notes  = cont.getNotes().getClone();
    result = toOpenCVImageContainer(cont.toBufferedImage());
    result.setReport(report);
    result.setNotes(notes);
    
    return result;
  }
  
  /**
   * Creates a {@link BufferedImage} to an {@link OpenCVImageContainer}.
   * 
   * @param image	the image to convert
   * @return		the generated container
   */
  public static OpenCVImageContainer toOpenCVImageContainer(BufferedImage image) {
    OpenCVImageContainer	result;
    
    result = new OpenCVImageContainer();
    result.setImage(toOpenCVImage(image));
    
    return result;
  }
  
  /**
   * Converts a {@link BufferedImage} to an {@link IplImage}.
   * 
   * @param image	the image to convert
   * @return		the generated container
   */
  @MixedCopyright(
    license = License.CC_BY_SA_3,
    author = "Matthias Braun",
    url = "http://stackoverflow.com/a/33630469/4698227"
  )
  public static IplImage toOpenCVImage(BufferedImage image) {
    IplImage 			result;
    ToIplImage 			iplConv;
    Java2DFrameConverter 	java2dConv;

    iplConv    = new OpenCVFrameConverter.ToIplImage();
    java2dConv = new Java2DFrameConverter();
    result     = iplConv.convert(java2dConv.convert(image));

    return result;
  }

  /**
   * Converts an {@link IplImage} to a {@link BufferedImage}.
   *
   * @param image	the image to convert
   * @return		the generated container
   */
  @MixedCopyright(
    license = License.CC_BY_SA_3,
    author = "Voynov Igor",
    url = "http://stackoverflow.com/a/32023724/4698227"
  )
  public static BufferedImage toBufferedImage(IplImage image) {
    BufferedImage		result;
    ToIplImage 			iplConv;
    Java2DFrameConverter 	java2dConv;
    Frame 			frame;

    iplConv    = new OpenCVFrameConverter.ToIplImage();
    java2dConv = new Java2DFrameConverter();
    frame      = iplConv.convert(image);
    result     = java2dConv.getBufferedImage(frame, 1);

    return result;
  }

  /**
   * Converts a {@link IplImage} to a {@link Mat}.
   *
   * @param image	the image to convert
   * @return		the generated matrix
   */
  public static Mat toMat(IplImage image) {
    return new Mat(image.asCvMat());
  }
}
