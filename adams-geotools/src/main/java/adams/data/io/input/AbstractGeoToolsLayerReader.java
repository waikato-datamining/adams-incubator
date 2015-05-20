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
 * AbstractGeoToolsLayerReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

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
 * Abstract ancestor for readers that read files in various formats and
 * turn them into layers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of layer
 */
public abstract class AbstractGeoToolsLayerReader<T extends Layer>
  extends AbstractOptionHandler
  implements Comparable, ShallowCopySupporter<AbstractGeoToolsLayerReader>, FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4690065186988048507L;

  /** the file to parse. */
  protected PlaceholderFile m_Input;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
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
	    "input", "input",
	    new PlaceholderFile("."));
  }

  /**
   * Sets the file/directory to read.
   *
   * @param value	the file/directory to read
   */
  public void setInput(PlaceholderFile value) {
    if (value == null)
      m_Input = new PlaceholderFile(".");
    else
      m_Input = value;
    reset();
  }

  /**
   * The file/directory to read.
   *
   * @return		the file/directory to read
   */
  public PlaceholderFile getInput() {
    return m_Input;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputTipText() {
    return "The layer file to read.";
  }

  /**
   * The default implementation only checks whether the provided file object
   * is an actual file and whether it exists.
   */
  protected void check() {
    if (!m_Input.exists())
      throw new IllegalStateException("Input file '" + m_Input + "' does not exist!");
    if (m_Input.isDirectory())
      throw new IllegalStateException("No input file but directory provided ('" + m_Input + "')!");
  }

  /**
   * Performs the actual reading.
   * 
   * @return		the generated layer
   */
  protected abstract T doRead();
  
  /**
   * Returns the layer generated from the file.
   *
   * @return		the layer generated from the file
   */
  public T read() {
    check();
    return doRead();
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractGeoToolsLayerReader shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractGeoToolsLayerReader shallowCopy(boolean expand) {
    return (AbstractGeoToolsLayerReader) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of readers.
   *
   * @return		the reader classnames
   */
  public static String[] getReaders() {
    return ClassLister.getSingleton().getClassnames(AbstractGeoToolsLayerReader.class);
  }

  /**
   * Instantiates the spectrum reader with the given options.
   *
   * @param classname	the classname of the reader to instantiate
   * @param options	the options for the reader
   * @return		the instantiated reader or null if an error occurred
   */
  public static AbstractGeoToolsLayerReader forName(String classname, String[] options) {
    AbstractGeoToolsLayerReader	result;

    try {
      result = (AbstractGeoToolsLayerReader) OptionUtils.forName(AbstractGeoToolsLayerReader.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the spectrum reader from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			reader to instantiate
   * @return		the instantiated reader or null if an error occurred
   */
  public static AbstractGeoToolsLayerReader forCommandLine(String cmdline) {
    return (AbstractGeoToolsLayerReader) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
