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
 * JCloudsSource.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.source.jclouds.Dummy;
import adams.flow.source.jclouds.JCloudsSourceAction;
import adams.flow.standalone.JCloudsConnection;

/**
 <!-- globalinfo-start -->
 * Executes a JClouds action that generates data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: JCloudsSource
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-action &lt;adams.flow.source.jclouds.JCloudsSourceAction&gt; (property: action)
 * &nbsp;&nbsp;&nbsp;The JClouds action to execute.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.jclouds.Dummy
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JCloudsSource
  extends AbstractSimpleSource {

  private static final long serialVersionUID = -2625144277594271179L;

  /** the action to perform. */
  protected JCloudsSourceAction m_Action;

  /** the JClouds connection. */
  protected JCloudsConnection m_Connection;

  @Override
  public String globalInfo() {
    return "Executes a JClouds action that generates data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "action", "action",
	    new Dummy());
  }

  /**
   * Sets the action to execute.
   *
   * @param value	the action
   */
  public void setAction(JCloudsSourceAction value) {
    m_Action = value;
    reset();
  }

  /**
   * Returns the action to execute.
   *
   * @return 		the action
   */
  public JCloudsSourceAction getAction() {
    return m_Action;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String actionTipText() {
    return "The JClouds action to execute.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "action", m_Action, "action: ");
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Connection = JCloudsConnection.getConnection(this, m_Action.getProvider());
      if (m_Connection == null)
        result = "Failed to locate " + JCloudsConnection.class.getName() + " instance!";
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return m_Action.generates();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    m_Action.setConnection(m_Connection);
    result = m_Action.execute();
    if (result == null) {
      if (m_Action.hasPendingOutput())
	m_OutputToken = new Token(m_Action.output());
    }
    else {
      getLogger().severe("Failed to execute action: " + result);
    }

    return result;
  }
}
