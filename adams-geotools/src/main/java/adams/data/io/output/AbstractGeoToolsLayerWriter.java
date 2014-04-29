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
 * AbstractGeoToolsLayerWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import org.geotools.map.Layer;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;

/**
 * Abstract ancestor for writers that layer data to files in various
 * formats.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of layer
 */
public abstract class AbstractGeoToolsLayerWriter<T extends Layer>
  extends AbstractOptionHandler
  implements Comparable, ShallowCopySupporter<AbstractGeoToolsLayerWriter>, FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7097110023547675936L;

  /** the file to write to. */
  protected PlaceholderFile m_Output;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension(s) (without the dot!)
   */
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output", "output",
	    new PlaceholderFile(
		System.getProperty("java.io.tmpdir") + PlaceholderFile.separator + "out." + getDefaultFormatExtension()));
  }

  /**
   * Sets the file to write to.
   *
   * @param value	the file to write to
   */
  public void setOutput(PlaceholderFile value) {
    if (value == null)
      m_Output = new PlaceholderFile(".");
    else
      m_Output = value;
  }

  /**
   * The file to write to.
   *
   * @return 		the file to write to
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or
   *         for listing the options.
   */
  public String outputTipText() {
    return "The file to write the layer to.";
  }

  /**
   * Performs checks and writes the data.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  public boolean write(T data) {
    check(data);
    return writeData(data);
  }

  /**
   * The default implementation only checks whether the provided file is an
   * actual file and whether it exists.
   *
   * @param data	the data to write
   */
  protected void check(T data) {
    if (m_Output.isDirectory())
      throw new IllegalStateException(
	  "No output file but directory provided ('" + m_Output + "')!");
    if (!m_Output.getParentFile().exists())
      throw new IllegalStateException("Output file's directory '"
	  + m_Output.getParentFile() + "' does not exist!");
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  protected abstract boolean writeData(T data);

  /**
   * Compares this object with the specified object for order. Returns a
   * negative integer, zero, or a positive integer as this object is less than,
   * equal to, or greater than the specified object. <p/> Only compares the
   * commandlines of the two objects.
   *
   * @param o
   *          the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is
   *         less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException
   *           if the specified object's type prevents it from being compared to
   *           this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same. <p/> Only compares the
   * commandlines of the two objects.
   *
   * @param o
   *          the object to be compared
   * @return true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return the shallow copy
   */
  public AbstractGeoToolsLayerWriter shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return 		the shallow copy
   */
  public AbstractGeoToolsLayerWriter shallowCopy(boolean expand) {
    return (AbstractGeoToolsLayerWriter) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of writers.
   *
   * @return 		the writer classnames
   */
  public static String[] getWriters() {
    return ClassLister.getSingleton().getClassnames(AbstractGeoToolsLayerWriter.class);
  }

  /**
   * Instantiates the spectrum writer with the given options.
   *
   * @param classname	the classname of the writer to instantiate
   * @param options	the options for the writer
   * @return 		the instantiated writer or null if an error occurred
   */
  public static AbstractGeoToolsLayerWriter forName(String classname, String[] options) {
    AbstractGeoToolsLayerWriter result;

    try {
      result = (AbstractGeoToolsLayerWriter) OptionUtils.forName(AbstractGeoToolsLayerWriter.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the spectrum writer from the given commandline (i.e.,
   * classname and optional options).
   *
   * @param cmdline
   *          the classname (and optional options) of the writer to instantiate
   * @return the instantiated writer or null if an error occurred
   */
  public static AbstractGeoToolsLayerWriter forCommandLine(String cmdline) {
    return (AbstractGeoToolsLayerWriter) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
