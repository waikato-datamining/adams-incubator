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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import adams.core.Constants;
import adams.core.Properties;
import adams.core.net.OpenMLHelper;
import adams.gui.core.ParameterPanel;

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

  /** the user. */
  protected JTextField m_TextUser;

  /** the password. */
  protected JPasswordField m_TextPassword;

  /** Whether to show the password. */
  protected JCheckBox m_CheckBoxShowPassword;

  /** the URL. */
  protected JTextField m_TextURL;

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

    m_TextUser = new JTextField(20);
    m_TextUser.setText(OpenMLHelper.getUser());
    m_PanelParameters.addParameter("_User", m_TextUser);

    m_TextPassword = new JPasswordField(20);
    m_TextPassword.setText(OpenMLHelper.getPassword().getValue());
    m_TextPassword.setEchoChar(Constants.PASSWORD_CHAR);
    m_PanelParameters.addParameter("_Password", m_TextPassword);

    m_CheckBoxShowPassword = new JCheckBox();
    m_CheckBoxShowPassword.setSelected(false);
    m_CheckBoxShowPassword.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_CheckBoxShowPassword.isSelected())
	  m_TextPassword.setEchoChar((char) 0);
	else
	  m_TextPassword.setEchoChar(Constants.PASSWORD_CHAR);
      }
    });
    m_PanelParameters.addParameter("Show password", m_CheckBoxShowPassword);

    m_TextURL = new JTextField(20);
    m_TextURL.setText(OpenMLHelper.getURL());
    m_PanelParameters.addParameter("U_RL", m_TextURL);

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

    result.setProperty(OpenMLHelper.USER, m_TextUser.getText());
    result.setProperty(OpenMLHelper.PASSWORD, m_TextPassword.getText());
    result.setProperty(OpenMLHelper.URL, m_TextURL.getText());
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
}
