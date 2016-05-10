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
 * OpenStackDeleteObject.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.jclouds;

import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.features.ObjectApi;

/**
 <!-- globalinfo-start -->
 * Deletes an object from a container in the cloud.<br>
 * Receives as input the name of the object to delete.
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
 * <pre>-container-name &lt;java.lang.String&gt; (property: containerName)
 * &nbsp;&nbsp;&nbsp;The container name the object resides in.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenStackDeleteObject
  extends AbstractJCloudsSinkAction {

  private static final long serialVersionUID = 5077164507336679181L;

  /** the region. */
  protected String m_Region;

  /** the name of the container. */
  protected String m_ContainerName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Deletes an object from a container in the cloud.\n"
	+ "Receives as input the name of the object to delete.";
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

    m_OptionManager.add(
      "container-name", "containerName",
      "");
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
   * Sets the container name to use.
   *
   * @param value	the name
   */
  public void setContainerName(String value) {
    m_ContainerName = value;
    reset();
  }

  /**
   * Returns the container name to use.
   *
   * @return		the name
   */
  public String getContainerName() {
    return m_ContainerName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String containerNameTipText() {
    return "The container name the object resides in.";
  }

  /**
   * Returns the accepted data types.
   *
   * @return		the data types
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the provider that this action requires.
   *
   * @return		the provider
   */
  @Override
  public String getProvider() {
    return "openstack-swift";
  }

  /**
   * Performs the actual action.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String		name;
    SwiftApi		swiftApi;
    ObjectApi		objectApi;

    result = null;

    if (m_Region.isEmpty())
      result = "No region provided!";
    else if (m_ContainerName.isEmpty())
      result = "No container name provided!";

    if (result == null) {
      name         = (String) m_Input;
      swiftApi     = (SwiftApi) getConnection().buildAPI(SwiftApi.class);
      objectApi    = swiftApi.getObjectApi(m_Region, m_ContainerName);
      if (isLoggingEnabled())
        getLogger().info("Deleting object: " + m_Region + "/" + m_ContainerName + "/" + name);
      objectApi.delete(name);
    }

    return result;
  }
}
