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
 * OpenStackListServers.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.jclouds;

import adams.core.DateTimeMsec;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;

/**
 <!-- globalinfo-start -->
 * Lists the server IDs from the specified region.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-region &lt;java.lang.String&gt; (property: region)
 * &nbsp;&nbsp;&nbsp;The region to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenStackListServers
  extends AbstractJCloudsSourceAction {

  private static final long serialVersionUID = -6630288063048110072L;

  /** the region. */
  protected String m_Region;

  /** the servers. */
  protected SpreadSheet m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists the server IDs in the specified region.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "region", "region",
      "");
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
   * Sets the region to use.
   *
   * @param value	the region
   */
  public void setRegion(String value) {
    m_Region = value;
    reset();
  }

  /**
   * Returns the region to use.
   *
   * @return		the region
   */
  public String getRegion() {
    return m_Region;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regionTipText() {
    return "The region to use.";
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
    String	result;
    NovaApi	novaApi;
    ServerApi 	serverApi;
    SpreadSheet	sheet;
    Row		row;

    result   = null;
    m_Output = null;

    if (m_Region.isEmpty())
      result = "No region provided!";

    if (result == null) {
      novaApi   = (NovaApi) m_Connection.buildAPI(NovaApi.class);
      serverApi = novaApi.getServerApi(m_Region);
      sheet     = new DefaultSpreadSheet();

      // header
      row = sheet.getHeaderRow();
      row.addCell("ID").setContentAsString("ID");
      row.addCell("TID").setContentAsString("TenantID");
      row.addCell("UID").setContentAsString("UserID");
      row.addCell("S").setContentAsString("Status");
      row.addCell("IM").setContentAsString("Image");
      row.addCell("CR").setContentAsString("Created");
      row.addCell("UP").setContentAsString("Updated");

      // data
      for (Server server : serverApi.listInDetail().concat()) {
	row = sheet.addRow();
	row.addCell("ID").setContentAsString(server.getId());
	row.addCell("TID").setContentAsString(server.getTenantId());
	row.addCell("UID").setContentAsString(server.getUserId());
	row.addCell("S").setContentAsString(server.getStatus().toString());
	row.addCell("IM").setContentAsString(server.getImage().toString());
	row.addCell("CR").setContent(new DateTimeMsec(server.getCreated()));
	row.addCell("UP").setContent(new DateTimeMsec(server.getUpdated()));
      }

      m_Output = sheet;
    }

    return result;
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
