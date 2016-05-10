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
 * JCloudsSettingsPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.net.JClouds;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Panel for configuring the default JClouds settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6765 $
 */
public class JCloudsSettingsPanel
  extends AbstractPropertiesPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -5325521437739323748L;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    addPropertyType(JClouds.PROVIDER,   PropertyType.STRING);
    addPropertyType(JClouds.IDENTITY,   PropertyType.STRING);
    addPropertyType(JClouds.CREDENTIAL, PropertyType.PASSWORD);
    addPropertyType(JClouds.ENDPOINT,   PropertyType.STRING);
    setPropertyOrder(new String[]{
      JClouds.PROVIDER,
      JClouds.IDENTITY,
      JClouds.CREDENTIAL,
      JClouds.ENDPOINT
    });
    setPreferences(JClouds.getProperties());
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "JClouds";
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return false;
  }

  /**
   * Activates the settings.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    if (JClouds.writeProperties(getPreferences()))
      return null;
    else
      return "Failed to save JClouds setup!";
  }
}
