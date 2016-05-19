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

/*
 * Or.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.multiimageoperation;

import adams.data.opencv.OpenCVHelper;
import adams.data.opencv.OpenCVImageContainer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;

/**
 <!-- globalinfo-start -->
 * Performs a logical OR on the binary pixels of the images.<br>
 * Converts images automatically to type UNSIGNED_INT_8.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10569 $
 */
public class Or
  extends AbstractOpenCVMultiImageOperation {

  private static final long serialVersionUID = 2855986854754699508L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs a logical OR on the binary pixels of the images.";
  }

  /**
   * Returns the minimum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumImagesRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no upper limit
   */
  public int maxNumImagesRequired() {
    return 2;
  }

  /**
   * Checks the images.
   *
   * @param images	the images to check
   */
  @Override
  protected void check(OpenCVImageContainer[] images) {
    String	msg;

    super.check(images);

    msg = checkSameDimensions(images);
    if (msg != null)
      throw new IllegalStateException(msg);
  }

  /**
   * Performs the actual processing of the images.
   *
   * @param images	the images to process
   * @return		the generated image(s)
   */
  @Override
  protected OpenCVImageContainer[] doProcess(OpenCVImageContainer[] images) {
    OpenCVImageContainer[]	result;
    IplImage 			img0;
    IplImage			img1;
    IplImage 			output;

    result    = new OpenCVImageContainer[1];
    img0      = images[0].getImage();
    img1      = images[1].getImage();
    if (img0.nChannels() != img1.nChannels())
      throw new IllegalArgumentException("Images have different number of channels: " + img0.nChannels() + " != " + img1.nChannels());
    output    = img0.clone();
    opencv_core.bitwise_or(OpenCVHelper.toMat(img0), OpenCVHelper.toMat(img1), OpenCVHelper.toMat(output));
    result[0] = new OpenCVImageContainer();
    result[0].setImage(output);

    return result;
  }
}
