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

import adams.core.io.FileUtils;
import adams.core.net.JClouds;
import adams.env.Environment;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

import java.io.File;

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

    addPropertyType(JClouds.IDENTITY,   PropertyType.STRING);
    addPropertyType(JClouds.CREDENTIAL, PropertyType.PASSWORD);
    addPropertyType(JClouds.ENDPOINT,   PropertyType.STRING);
    setPropertyOrder(new String[]{
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

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String	props;

    props = Environment.getInstance().createPropertiesFilename(new File(JClouds.FILENAME).getName());
    return FileUtils.fileExists(props);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String	props;

    props = Environment.getInstance().createPropertiesFilename(new File(JClouds.FILENAME).getName());
    if (FileUtils.fileExists(props)) {
      if (!FileUtils.delete(props))
        return "Failed to remove custom JClouds properties: " + props;
    }

    return null;
  }
}
