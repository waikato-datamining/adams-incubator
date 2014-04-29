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
 * HadoopConfiguration.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderDirectory;

/**
 <!-- globalinfo-start -->
 * The Hadoop setup, used by other Hadoop actors.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: HadoopConfiguration
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-conf &lt;adams.core.io.PlaceholderDirectory&gt; (property: configuration)
 * &nbsp;&nbsp;&nbsp;The directory with the Hadoop configuration to use.
 * &nbsp;&nbsp;&nbsp;default: conf
 * </pre>
 * 
 * <pre>-bin &lt;adams.core.io.PlaceholderDirectory&gt; (property: binaries)
 * &nbsp;&nbsp;&nbsp;The directory with the Hadoop binaries to use.
 * &nbsp;&nbsp;&nbsp;default: bin
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HadoopConfiguration
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the hadoop configuration directory. */
  protected PlaceholderDirectory m_Configuration;

  /** the hadoop binaries directory. */
  protected PlaceholderDirectory m_Binaries;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Hadoop setup, used by other Hadoop actors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "conf", "configuration",
	    new PlaceholderDirectory("conf"));

    m_OptionManager.add(
	    "bin", "binaries",
	    new PlaceholderDirectory("bin"));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "configuration", m_Configuration, "Conf: ");
    result += QuickInfoHelper.toString(this, "binaries", m_Binaries, ", Bin: ");

    return result;
  }

  /**
   * Sets the directory with the Hadoop configuration.
   *
   * @param value	the directory
   */
  public void setConfiguration(PlaceholderDirectory value) {
    m_Configuration = value;
  }

  /**
   * Returns the directory with the Hadoop configuration.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getConfiguration() {
    return m_Configuration;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String configurationTipText() {
    return "The directory with the Hadoop configuration to use.";
  }

  /**
   * Sets the directory with the Hadoop binaries.
   *
   * @param value	the directory
   */
  public void setBinaries(PlaceholderDirectory value) {
    m_Binaries = value;
  }

  /**
   * Returns the directory with the Hadoop binaries.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getBinaries() {
    return m_Binaries;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binariesTipText() {
    return "The directory with the Hadoop binaries to use.";
  }

  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    return null;
  }
}
