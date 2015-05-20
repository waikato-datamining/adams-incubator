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

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import adams.data.opencv.OpenCVHelper;
import adams.data.opencv.OpenCVImageContainer;
import adams.data.report.Report;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 <!-- globalinfo-start -->
 * Applies an OpenCV cascade classifier to an image, returning all possible located objects.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ApplyCascadeClassifier
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Outputs the images either one by one or as array.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-classifier &lt;adams.core.io.PlaceholderFile&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;Cascade classifier training data.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-scale-factor &lt;double&gt; (property: scaleFactor)
 * &nbsp;&nbsp;&nbsp;How much the image size is reduced at each image scale.
 * &nbsp;&nbsp;&nbsp;default: 1.1
 * &nbsp;&nbsp;&nbsp;minimum: 1.1
 * </pre>
 * 
 * <pre>-min-neighbors &lt;int&gt; (property: minNeighbors)
 * &nbsp;&nbsp;&nbsp;How many neighbors each candidate rectangle should have to retain it.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-min-size &lt;int&gt; (property: minSize)
 * &nbsp;&nbsp;&nbsp;Minimum possible object size. Objects smaller than that are ignored.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-max-size &lt;int&gt; (property: maxSize)
 * &nbsp;&nbsp;&nbsp;Maximum possible object size. Objects larger than that are ignored.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
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

  /** the cascade classifier in use. */
  protected transient CascadeClassifier m_ActualClassifier;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies an OpenCV cascade classifier to an image, returning all possible located objects.";
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
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualClassifier = null;
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "classifier", m_Classifier, "classifier: ");
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
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String      result;

    result = super.setUp();

    if (result == null) {
      if (m_Classifier.isDirectory())
        result = "Classifier is pointing to a directory, not a file: " + m_Classifier;
      else if (!m_Classifier.exists())
        result = "Classifier file does not exist: " + m_Classifier;
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String result = null;
    OpenCVImageContainer cont = OpenCVHelper.toOpenCVImageContainer((AbstractImageContainer)m_InputToken.getPayload());
    try {
      IplImage input = cont.getImage();
      m_OutputToken = m_InputToken;

      // Init and apply classifier
      Rect rects = new Rect();
      if (m_ActualClassifier == null)
        m_ActualClassifier = new CascadeClassifier(m_Classifier.getAbsolutePath());
      m_ActualClassifier.detectMultiScale(new Mat(input), rects, m_ScaleFactor, m_MinNeighbors, 0, new Size(m_MinSize, m_MinSize), new Size(m_MaxSize, m_MaxSize));

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
