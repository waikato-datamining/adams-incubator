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

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.features.ContainerApi;

/**
 <!-- globalinfo-start -->
 * Lists the stored containers in the specified region.
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
public class OpenStackListContainers
  extends AbstractJCloudsSourceAction {

  private static final long serialVersionUID = -6630288063048110072L;

  /** the region. */
  protected String m_Region;

  /** the containers. */
  protected SpreadSheet m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists the stored containers in the specified region.";
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
    return "openstack-swift";
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
    String		result;
    SwiftApi 		swiftApi;
    ContainerApi 	containerApi;
    SpreadSheet		sheet;
    Row			row;

    result   = null;
    m_Output = null;

    if (m_Region.isEmpty())
      result = "No region provided!";

    if (result == null) {
      swiftApi     = (SwiftApi) m_Connection.buildAPI(SwiftApi.class);
      containerApi = swiftApi.getContainerApi(m_Region);
      sheet        = new DefaultSpreadSheet();

      // header
      row = sheet.getHeaderRow();
      row.addCell("N").setContentAsString("Name");
      row.addCell("BU").setContentAsString("BytesUsed");
      row.addCell("OC").setContentAsString("ObjectCount");
      row.addCell("AR").setContentAsString("AnybodyRead");

      // data
      for (Container container : containerApi.list().toSet()) {
	row = sheet.addRow();
	row.addCell("N").setContentAsString(container.getName());
	row.addCell("BU").setContent(container.getBytesUsed());
	row.addCell("OC").setContent(container.getObjectCount());
	row.addCell("AR").setContent(container.getAnybodyRead() == null ? null : container.getAnybodyRead().toString());
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
