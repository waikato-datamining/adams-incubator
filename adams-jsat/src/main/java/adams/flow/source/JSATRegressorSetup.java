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
 * JSATRegressorSetup.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import jsat.classifiers.trees.DecisionStump;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JSATRegressorSetup
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the weka regressor. */
  protected jsat.regression.Regressor m_Regressor;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs an instance of the specified regressor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regressor", "regressor",
	    new DecisionStump());
  }

  /**
   * Sets the regressor to use.
   *
   * @param value	the regressor
   */
  public void setRegressor(jsat.regression.Regressor value) {
    m_Regressor = value;
    reset();
  }

  /**
   * Returns the regressor in use.
   *
   * @return		the regressor
   */
  public jsat.regression.Regressor getRegressor() {
    return m_Regressor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regressorTipText() {
    return "The JSAT regressor to output.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regressor", Shortening.shortenEnd(OptionUtils.getShortCommandLine(m_Regressor), 40));
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->jsat.regression.Regressor.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{jsat.regression.Regressor.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    jsat.regression.Regressor	cls;

    result = null;

    try {
      cls           = (jsat.regression.Regressor) OptionUtils.shallowCopy(m_Regressor);
      m_OutputToken = new Token(cls);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to create copy of regressor:", e);
    }

    return result;
  }
}
