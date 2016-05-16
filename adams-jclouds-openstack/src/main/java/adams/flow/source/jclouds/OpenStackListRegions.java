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
 * OpenStackListRegions.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.jclouds;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import org.jclouds.openstack.nova.v2_0.NovaApi;

/**
 <!-- globalinfo-start -->
 * Lists the configured org.jclouds.openstack.nova.v2_0.NovaApi regions.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenStackListRegions
  extends AbstractJCloudsSourceAction {

  private static final long serialVersionUID = -6630288063048110072L;

  /** the regions. */
  protected SpreadSheet m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists the configured " + NovaApi.class.getName() + " regions.";
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Output = null;
  }

  /**
   * Returns the provider that this action requires.
   *
   * @return		the provider
   */
  public String getProvider() {
    return "openstack-nova";
  }

  /**
   * Returns the data types of the generated data.
   *
   * @return		the data types
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Performs the actual action.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExecute() {
    SpreadSheet		sheet;
    Row			row;

    sheet = new DefaultSpreadSheet();

    // header
    row = sheet.getHeaderRow();
    row.addCell("R").setContentAsString("Region");

    // data
    for (String region: ((NovaApi) m_Connection.buildAPI(NovaApi.class)).getConfiguredRegions()) {
      row = sheet.addRow();
      row.addCell("R").setContentAsString(region);
    }

    m_Output = sheet;

    return null;
  }

  /**
   * Returns whether any data was generated.
   *
   * @return		true if data available to be collected
   * @see		#output()
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Output != null);
  }

  /**
   * Returns the generated data.
   *
   * @return		the generated data
   */
  @Override
  public Object output() {
    Object	result;

    result   = m_Output;
    m_Output = null;

    return result;
  }
}
