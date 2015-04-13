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
 * ApplyCascadeClassifier.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import adams.data.opencv.OpenCVImageContainer;
import adams.data.report.Report;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * <!-- globalinfo-start -->
 * <!-- globalinfo-end -->
 * <!-- options-start -->
 * <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class ApplyCascadeClassifier extends AbstractArrayProvider {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = -2673127847873733814L;

  /**
   * Key name for original x position in report.
   */
  public static final String REPORT_KEY_X = "X";

  /**
   * Key name for original y position in report.
   */
  public static final String REPORT_KEY_Y = "Y";

  /**
   * Key name for cropped width position in report.
   */
  public static final String REPORT_KEY_WIDTH = "Width";

  /**
   * Key name for cropped height position in report.
   */
  public static final String REPORT_KEY_HEIGHT = "Height";

  /**
   * Cascade classifier training data.
   */
  protected PlaceholderFile m_Classifier;

  /**
   * How much the image size is reduced at each image scale.
   */
  protected double m_ScaleFactor;

  /**
   * How many neighbors each candidate rectangle should have to retain it.
   */
  protected int m_MinNeighbors;

  /**
   * Minimum possible object size. Objects smaller than that are ignored.
   */
  protected int m_MinSize;

  /**
   * Maximum possible object size. Objects larger than that are ignored.
   */
  protected int m_MaxSize;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies a OpenCV cascade classifier to an image, returning all possible located objects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("classifier", "classifier", new PlaceholderFile());
    m_OptionManager.add("scale-factor", "scaleFactor", 1.1d, 1.1d, null);
    m_OptionManager.add("min-neighbors", "minNeighbors", 1, 0, null);
    m_OptionManager.add("min-size", "minSize", 10, 0, null);
    m_OptionManager.add("max-size", "maxSize", 100, 0, null);
  }

  /**
   * Returns the trained classifier file.
   *
   * @return trained classifier file
   */
  public PlaceholderFile getClassifier() {
    return m_Classifier;
  }

  /**
   * Sets the trained classifier file.
   *
   * @param value trained classifier file
   */
  public void setClassifier(PlaceholderFile value) {
    if (value != null) {
      m_Classifier = value;
      reset();
    } else getLogger().severe("Classifier file must not be null!");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "Cascade classifier training data.";
  }

  /**
   * Returns h much the image size is reduced at each image scale.
   *
   * @return scale factor
   */
  public double getScaleFactor() {
    return m_ScaleFactor;
  }

  /**
   * Sets how much the image size is reduced at each image scale.
   *
   * @param value scale factor
   */
  public void setScaleFactor(double value) {
    if (value >= 1.1) {
      m_ScaleFactor = value;
      reset();
    } else
      getLogger().severe("Scale factor must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String scaleFactorTipText() {
    return "How much the image size is reduced at each image scale.";
  }

  /**
   * Returns how many neighbors each candidate rectangle should have to retain it.
   *
   * @return minimum neighbors
   */
  public int getMinNeighbors() {
    return m_MinNeighbors;
  }

  /**
   * Sets how many neighbors each candidate rectangle should have to retain it.
   *
   * @param value minimum neighbors
   */
  public void setMinNeighbors(int value) {
    if (value >= 0) {
      m_MinNeighbors = value;
      reset();
    } else
      getLogger().severe("Minimum neighbors must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String minNeighborsTipText() {
    return "How many neighbors each candidate rectangle should have to retain it.";
  }

  /**
   * Returns the inimum possible object size. Objects smaller than that are ignored.
   *
   * @return minimum object size
   */
  public int getMinSize() {
    return m_MinSize;
  }

  /**
   * Sets the minimum possible object size. Objects smaller than that are ignored.
   *
   * @param value minimum object size
   */
  public void setMinSize(int value) {
    if (value >= 0) {
      m_MinSize = value;
      reset();
    } else
      getLogger().severe("Minimum size must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String minSizeTipText() {
    return "Minimum possible object size. Objects smaller than that are ignored.";
  }

  /**
   * Returns the inimum possible object size. Objects smaller than that are ignored.
   *
   * @return maximum object size
   */
  public int getMaxSize() {
    return m_MaxSize;
  }

  /**
   * Sets the maximum possible object size. Objects larger than that are ignored.
   *
   * @param value maximum object size
   */
  public void setMaxSize(int value) {
    if (value >= 0) {
      m_MaxSize = value;
      reset();
    } else
      getLogger().severe("Maximum size must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String maxSizeTipText() {
    return "Maximum possible object size. Objects larger than that are ignored.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Outputs the images either one by one or as array.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of objects that it generates
   */
  @Override
  public Class[] generates() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the based class of the items.
   *
   * @return the class
   */
  @Override
  protected Class getItemClass() {
    return OpenCVImageContainer.class;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String result = null;
    try {
      IplImage input = ((OpenCVImageContainer) m_InputToken.getPayload()).getImage();
      m_OutputToken = m_InputToken;

      // Init and apply classifier
      Rect rects = new Rect();
      CascadeClassifier classifier = new CascadeClassifier(m_Classifier.getAbsolutePath());
      classifier.detectMultiScale(new Mat(input), rects, m_ScaleFactor, m_MinNeighbors, 0, new Size(m_MinSize, m_MinSize), new Size(m_MaxSize, m_MaxSize));

      // Crop sub images from results and create report
      for (int i = 0; i < rects.capacity(); i++) {
        Rect rect = rects.position(i);

        // Crop
        cvSetImageROI(input, rect.asCvRect());
        IplImage cropped = cvCreateImage(rect.size().asCvSize(), input.depth(), input.nChannels());
        cvCopy(input, cropped, null);

        // Report
        Report report = new Report();
        report.setNumericValue(REPORT_KEY_X, rect.x());
        report.setNumericValue(REPORT_KEY_Y, rect.y());
        report.setNumericValue(REPORT_KEY_WIDTH, rect.width());
        report.setNumericValue(REPORT_KEY_HEIGHT, rect.height());

        // Output
        OpenCVImageContainer container = new OpenCVImageContainer();
        container.setImage(cropped);
        container.setReport(report);

        m_Queue.add(container);
      }
    } catch (Exception e) {
      result = handleException("Failed to apply cascade classifier: ", e);
    }
    return result;
  }
}
