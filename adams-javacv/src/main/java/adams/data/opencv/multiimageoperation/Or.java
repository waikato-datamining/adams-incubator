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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.multiimageoperation;

import adams.data.opencv.OpenCVImageContainer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;

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
   * Returns the number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means any number accepted
   */
  @Override
  public int numImagesRequired() {
    return 2;
  }

  /**
   * Checks the images.
   * <br><br>
   * Default implementation only ensures that images are present.
   *
   * @param images	the images to check
   */
  @Override
  protected void check(OpenCVImageContainer[] images) {
    super.check(images);

    if (!checkSameDimensions(images[0], images[1]))
      throw new IllegalStateException(
	"Both images need to have the same dimensions: "
	  + images[0].getWidth() + "x" + images[0].getHeight()
	  + " != "
	  + images[1].getWidth() + "x" + images[1].getHeight());
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
    IplImage img0;
    IplImage			img1;
    IplImage 			output;

    result    = new OpenCVImageContainer[1];
    img0      = images[0].getImage();
    img1      = images[1].getImage();
    output    = img0.clone();
    opencv_core.bitwise_or(new Mat(img0.asCvMat()), new Mat(img1.asCvMat()), new Mat(output.asCvMat()));
    result[0] = new OpenCVImageContainer();
    result[0].setImage(output);

    return result;
  }
}
