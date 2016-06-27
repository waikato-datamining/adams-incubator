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
 * AbstractDataTypeToString.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.openml;

import adams.core.ClassLister;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ancestor for conversions that turn OpenML data types to strings.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data type
 */
public abstract class AbstractDataTypeToString<T>
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -7137007761157572433L;

  /** the conversion cache. */
  private static HashMap<Class,Class> m_Cache = new HashMap<>();

  /** the logger for static methods. */
  private static Logger LOGGER = LoggingHelper.getLogger(AbstractDataTypeToString.class);

  /**
   * Checks whether this conversion handles the object.
   *
   * @param value	the object to check
   * @return		true if it can be handled
   */
  public boolean handles(Object value) {
    return handles(value.getClass());
  }

  /**
   * Checks whether this conversion handles the class.
   *
   * @param cls		the class to check
   * @return		true if it can be handled
   */
  public abstract boolean handles(Class cls);

  /**
   * Performs the conversion to a string representation.
   *
   * @param value	the value to convert
   * @return		the string representation
   */
  protected abstract String doConvert(T value);

  /**
   * Performs the conversion to a string representation.
   * Simply returns null if the value is null.
   *
   * @param value	the value to convert
   * @return		the string representation
   */
  public String convert(T value) {
    if (value == null)
      return null;
    else
      return doConvert(value);
  }

  /**
   * Returns the conversion for the specified OpenML object.
   *
   * @param value	the object to get the conversion for
   * @return		the conversion, null if none found
   */
  public synchronized static AbstractDataTypeToString getConversion(Object value) {
    return getConversion(value.getClass());
  }

  /**
   * Returns the conversion for the specified OpenML type.
   *
   * @param cls		the type to get the conversion for
   * @return		the conversion, null if none found
   */
  public synchronized static AbstractDataTypeToString getConversion(Class cls) {
    AbstractDataTypeToString	result;
    Class[]			classes;
    AbstractDataTypeToString	conv;

    result = null;

    if (m_Cache.containsKey(cls)) {
      try {
	result = (AbstractDataTypeToString) m_Cache.get(cls).newInstance();
      }
      catch (Exception e) {
	result = null;
	LOGGER.log(Level.SEVERE, "Failed to instantiate conversion: " + cls.getName(), e);
      }
    }
    else {
      classes = ClassLister.getSingleton().getClasses(AbstractDataTypeToString.class);
      for (Class c: classes) {
	try {
	  conv = (AbstractDataTypeToString) c.newInstance();
	  if (conv.handles(cls)) {
	    result = conv;
	    m_Cache.put(cls, c);
	    break;
	  }
	}
	catch (Exception e) {
	  result = null;
	  LOGGER.log(Level.SEVERE, "Failed to instantiate conversion: " + cls.getName(), e);
	}
      }
    }

    return result;
  }
}
