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
 * Blur.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.opencv.transformer;

import java.awt.Dimension;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.base.BaseDimension;
import adams.core.base.BasePointInt;
import adams.data.opencv.BorderType;
import adams.data.opencv.OpenCVImageContainer;

/**
 <!-- globalinfo-start -->
 * Blurs an image using the normalized box filter.<br/>
 * For more information see:<br/>
 * OpenCV documentation. Blur.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{missing_id,
 *    author = {OpenCV documentation},
 *    title = {Blur},
 *    HTTP = {http:&#47;&#47;docs.opencv.org&#47;modules&#47;imgproc&#47;doc&#47;filtering.html#blur}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-kernel-size &lt;adams.core.base.BaseDimension&gt; (property: kernelSize)
 * &nbsp;&nbsp;&nbsp;The kernel size to use.
 * &nbsp;&nbsp;&nbsp;default: 5;5
 * </pre>
 * 
 * <pre>-anchor &lt;adams.core.base.BasePointInt&gt; (property: anchor)
 * &nbsp;&nbsp;&nbsp;The anchor point; (-1,-1) means anchor is the kernel center.
 * &nbsp;&nbsp;&nbsp;default: -1;-1
 * </pre>
 * 
 * <pre>-border-type &lt;CONSTANT|DEFAULT|ISOLATED|TRANSPARENT|REPLICATE|REFLECT|REFLECT_101|WRAP&gt; (property: borderType)
 * &nbsp;&nbsp;&nbsp;The type of border to use.
 * &nbsp;&nbsp;&nbsp;default: DEFAULT
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Blur
  extends AbstractOpenCVTransformer
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5988488238337756717L;
  
  /** the kernel size. */
  protected BaseDimension m_KernelSize;
  
  /** the anchor. */
  protected BasePointInt m_Anchor;
  
  /** the border type. */
  protected BorderType m_BorderType;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Blurs an image using the normalized box filter.\n"
	+ "For more information see:\n"
	+ getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "OpenCV documentation");
    result.setValue(Field.TITLE, "Blur");
    result.setValue(Field.HTTP, "http://docs.opencv.org/modules/imgproc/doc/filtering.html#blur");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "kernel-size", "kernelSize",
	    new BaseDimension(new Dimension(5, 5)));

    m_OptionManager.add(
	    "anchor", "anchor",
	    new BasePointInt(new java.awt.Point(-1, -1)));

    m_OptionManager.add(
	    "border-type", "borderType",
	    BorderType.DEFAULT);
  }

  /**
   * Sets the kernel size.
   *
   * @param value	the size
   */
  public void setKernelSize(BaseDimension value) {
    m_KernelSize = value;
    reset();
  }

  /**
   * Returns the kernel size.
   *
   * @return		the size
   */
  public BaseDimension getKernelSize() {
    return m_KernelSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String kernelSizeTipText() {
    return "The kernel size to use.";
  }

  /**
   * Sets the anchor point.
   *
   * @param value	the anchor
   */
  public void setAnchor(BasePointInt value) {
    m_Anchor = value;
    reset();
  }

  /**
   * Returns the anchor point.
   *
   * @return		the anchor
   */
  public BasePointInt getAnchor() {
    return m_Anchor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String anchorTipText() {
    return "The anchor point; (-1,-1) means anchor is the kernel center.";
  }

  /**
   * Sets the border type.
   *
   * @param value	the border type
   */
  public void setBorderType(BorderType value) {
    m_BorderType = value;
    reset();
  }

  /**
   * Returns the border type.
   *
   * @return		the border type
   */
  public BorderType getBorderType() {
    return m_BorderType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String borderTypeTipText() {
    return "The type of border to use.";
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img		the image to transform (can be modified, since it is a copy)
   * @return		the generated image(s)
   */
  @Override
  protected OpenCVImageContainer[] doTransform(OpenCVImageContainer img) {
    OpenCVImageContainer[]	result;
    IplImage			original;
    IplImage			blurred;
    Size			ksize;
    Point			anchor;
    
    result = new OpenCVImageContainer[1];
    result[0] = (OpenCVImageContainer) img.getHeader();
    original  = img.getImage();
    blurred   = IplImage.create(original.cvSize(), original.depth(), original.nChannels());
    ksize     = new Size(m_KernelSize.dimensionValue().width, m_KernelSize.dimensionValue().height);
    anchor    = new Point(m_Anchor.pointValue().x, m_Anchor.pointValue().y);
    
    opencv_imgproc.blur(new Mat(original.asCvMat()), new Mat(blurred.asCvMat()), ksize, anchor, m_BorderType.getBorder());
    result[0].setImage(blurred);
    
    return result;
  }
}
