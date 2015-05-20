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
 * OpenCVImageWriter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.OpenCVImageReader;
import adams.data.opencv.OpenCVImageContainer;

/**
 <!-- globalinfo-start -->
 * OpenCV image writer for: jpg, png, tiff<br>
 * For more information see:<br>
 * http:&#47;&#47;opencv.org&#47;
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
 * @version $Revision$
 */
public class OpenCVImageWriter
  extends AbstractImageWriter<OpenCVImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 6385191315392140321L;

  /** the format extensions. */
  protected String[] m_FormatExtensions;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"OpenCV image writer for: " + Utils.flatten(getFormatExtensions(), ", ")
	+ "\n"
	+ "For more information see:\n"
	+ "http://opencv.org/";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_FormatExtensions = new String[]{
	"jpg",
	"png",
	"tiff"
    };
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "OpenCV";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return m_FormatExtensions;
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  @Override
  public AbstractImageReader getCorrespondingReader() {
    return new OpenCVImageReader();
  }

  /**
   * Performs the actual writing of the image file.
   * 
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, OpenCVImageContainer cont) {
    if (org.bytedeco.javacpp.opencv_highgui.cvSaveImage(file.getAbsolutePath(), cont.getImage()) == 0)
      return null;
    else
      return "Failed to write image to: " + file;
  }
}
