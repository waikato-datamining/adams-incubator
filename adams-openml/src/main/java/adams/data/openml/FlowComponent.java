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
 * FlowComponent.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.openml;

import org.openml.apiconnector.xml.Flow.Component;

/**
 * Conversion for the {@link Component} class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowComponent
  extends AbstractDataTypeToString<Component> {

  private static final long serialVersionUID = -2552899345467374770L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Conversion for the " + Component.class.getName() + " class.";
  }

  /**
   * Checks whether this conversion handles the class.
   *
   * @param cls		the class to check
   * @return		true if it can be handled
   */
  @Override
  public boolean handles(Class cls) {
    return (cls == Component.class);
  }

  /**
   * Performs the conversion to a string representation.
   *
   * @param value	the value to convert
   * @return		the string representation
   */
  @Override
  protected String doConvert(Component value) {
    return "identifier=" + value.getIdentifier()
      + ";flowImplementationID=" + value.getImplementation().getId();
  }
}
