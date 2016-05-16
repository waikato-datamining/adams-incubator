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
 * ListComputeLocations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.jclouds;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Location;

/**
 <!-- globalinfo-start -->
 * Lists the available compute locations.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-provider &lt;java.lang.String&gt; (property: provider)
 * &nbsp;&nbsp;&nbsp;The cloud provider to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListComputeLocations
  extends AbstractJCloudsSourceAction {

  private static final long serialVersionUID = 1442920448036162060L;

  /** the provider. */
  protected String m_Provider;

  /** the spreadsheet with the locations. */
  protected SpreadSheet m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists the available compute locations.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "provider", "provider",
      "");
  }

  /**
   * Sets the provider to use.
   *
   * @param value	the provider
   */
  public void setProvider(String value) {
    m_Provider = value;
    reset();
  }

  /**
   * Returns the provider that this action requires.
   *
   * @return		the provider
   */
  @Override
  public String getProvider() {
    return m_Provider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String providerTipText() {
    return "The cloud provider to use.";
  }

  /**
   * Returns the accepted data types.
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
    ComputeServiceContext 	context;
    ComputeService 		compute;
    SpreadSheet			sheet;
    Row				row;

    context = (ComputeServiceContext) m_Connection.buildView(ComputeServiceContext.class);
    compute = context.getComputeService();
    sheet   = new DefaultSpreadSheet();

    // header
    row = sheet.getHeaderRow();
    row.addCell("I").setContent("ID");
    row.addCell("D").setContent("Description");
    row.addCell("P").setContent("Parent");

    // data
    for (Location loc: compute.listAssignableLocations()) {
      row = sheet.addRow();
      row.addCell("I").setContentAsString(loc.getId());
      row.addCell("D").setContentAsString(loc.getDescription());
      if (loc.getParent() != null)
	row.addCell("P").setContentAsString(loc.getParent().getId());
    }

    m_Output = sheet;

    context.close();

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
