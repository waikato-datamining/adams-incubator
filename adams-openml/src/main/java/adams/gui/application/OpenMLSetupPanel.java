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
 * OpenMLSetupPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.data.openml.OpenMLHelper;
import adams.env.Environment;
import adams.env.OpenMLDefinition;
import adams.gui.core.BaseTextField;
import adams.gui.core.ParameterPanel;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;

/**
 * Panel for configuring the system-wide OpenML settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7103 $
 */
public class OpenMLSetupPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7937644706618374284L;

  /** the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the URL. */
  protected BaseTextField m_TextURL;

  /** the API Key. */
  protected BaseTextField m_TextAPIKey;

  /** the verbose level. */
  protected JSpinner m_SpinnerVerboseLevel;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.CENTER);

    m_TextURL = new BaseTextField(20);
    m_TextURL.setText(OpenMLHelper.getURL());
    m_PanelParameters.addParameter("U_RL", m_TextURL);

    m_TextAPIKey = new BaseTextField(20);
    m_TextAPIKey.setText(OpenMLHelper.getAPIKey());
    m_PanelParameters.addParameter("API _Key", m_TextAPIKey);

    m_SpinnerVerboseLevel = new JSpinner();
    ((SpinnerNumberModel) m_SpinnerVerboseLevel.getModel()).setMinimum(0);
    ((SpinnerNumberModel) m_SpinnerVerboseLevel.getModel()).setMaximum(2);
    ((SpinnerNumberModel) m_SpinnerVerboseLevel.getModel()).setStepSize(1);
    m_SpinnerVerboseLevel.setValue(OpenMLHelper.getVerboseLevel());
    m_PanelParameters.addParameter("_Verbose level", m_SpinnerVerboseLevel);
  }

  /**
   * Turns the parameters in the GUI into a properties object.
   *
   * @return		the properties
   */
  protected Properties toProperties() {
    Properties	result;

    result = new Properties();

    result.setProperty(OpenMLHelper.URL, m_TextURL.getText());
    result.setProperty(OpenMLHelper.APIKEY, m_TextAPIKey.getText());
    result.setInteger(OpenMLHelper.VERBOSE_LEVEL, ((SpinnerNumberModel) m_SpinnerVerboseLevel.getModel()).getNumber().intValue());

    return result;
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "OpenML";
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return true;
  }
  
  /**
   * Activates the OpenML setup.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    boolean	result;

    result = OpenMLHelper.writeProperties(toProperties());
    if (result)
      return null;
    else
      return "Failed to save OpenML setup to " + OpenMLHelper.FILENAME + "!";
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(OpenMLDefinition.KEY);
    return (props != null) && FileUtils.fileExists(props);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(OpenMLDefinition.KEY);
    if ((props != null) && FileUtils.fileExists(props)) {
      if (!FileUtils.delete(props))
	return "Failed to remove custom OpenML properties: " + props;
    }

    return null;
  }
}
