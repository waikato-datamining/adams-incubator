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
 * OpenStackUploadContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.jclouds;

import adams.core.io.PlaceholderFile;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.nova.v2_0.compute.NovaComputeService;
import org.jclouds.ssh.SshClient;

/**
 <!-- globalinfo-start -->
 * Stores an object with optional meta-data in the cloud.<br>
 * Outputs the name of the stored object.
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The object name to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-meta-data &lt;adams.core.base.BaseKeyValuePair&gt; [-meta-data ...] (property: metaData)
 * &nbsp;&nbsp;&nbsp;The meta-data to use (optional).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-format &lt;STRING|STRING_CONVERSION|SERIALIZED_OBJECT&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format to store the incoming data in.
 * &nbsp;&nbsp;&nbsp;default: STRING
 * </pre>
 * 
 * <pre>-to-string &lt;adams.data.conversion.ConversionToString&gt; (property: toString)
 * &nbsp;&nbsp;&nbsp;The conversion to convert object into string.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.StringToString
 * </pre>
 * 
 * <pre>-from-string &lt;adams.data.conversion.ConversionFromString&gt; (property: fromString)
 * &nbsp;&nbsp;&nbsp;The conversion to convert the string back into an object.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.StringToString
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenStackUploadFile
  extends AbstractJCloudsTransformerAction {

  private static final long serialVersionUID = 5077164507336679181L;

  /** the region. */
  protected String m_Region;

  /** the server ID. */
  protected String m_Server;

  /** the remote directory. */
  protected String m_RemoteDir;

  /** the uploaded file. */
  protected String m_Uploaded;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Copies the incoming file onto the server.";
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
      "server", "server",
      "");

    m_OptionManager.add(
      "remote-dir", "remoteDir",
      "/tmp");
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
   * Sets the ID of the server to copy the file to.
   *
   * @param value	the server ID
   */
  public void setServer(String value) {
    m_Server = value;
    reset();
  }

  /**
   * Returns the ID of the server to copy the file to.
   *
   * @return		the server ID
   */
  public String getServer() {
    return m_Server;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String serverTipText() {
    return "The ID of the server to copy the file to.";
  }

  /**
   * Sets the remote directory.
   *
   * @param value	the dir
   */
  public void setRemoteDir(String value) {
    m_RemoteDir = value;
    reset();
  }

  /**
   * Returns the remote dir.
   *
   * @return		the dir
   */
  public String getRemoteDir() {
    return m_RemoteDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteDirTipText() {
    return "The remote directory to copy the file to.";
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
   * Returns the data types being generated.
   *
   * @return		the data types
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Returns the provider that this action requires.
   *
   * @return		the provider
   */
  @Override
  public String getProvider() {
    return "openstack-nova";
  }

  /**
   * Performs the actual action.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExecute() {
    String 			result;
    ComputeServiceContext	context;
    NovaComputeService		compute;
    NodeMetadata		node;
    SshClient			client;
    PlaceholderFile		file;

    result     = null;
    m_Uploaded = null;

    if (m_Region.isEmpty())
      result = "No region set!";
    else if (m_Server.isEmpty())
      result = "No server ID set!";

    if (result == null) {
      file    = new PlaceholderFile((String) m_Input);
      context = (ComputeServiceContext) m_Connection.buildView(ComputeServiceContext.class);
      compute = (NovaComputeService) context.getComputeService();
      node    = compute.getNodeMetadata(m_Server);
      client = compute.getContext().utils().sshForNode().apply(node);
      client.connect();
      // TODO credentials? https://jclouds.apache.org/start/compute/
      client.put(m_RemoteDir + "/" + file.getName(), Payloads.newFilePayload(file.getAbsoluteFile()));
      client.disconnect();
      context.close();
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
    return (m_Uploaded != null);
  }

  /**
   * Returns the generated data.
   *
   * @return		the generated data
   */
  @Override
  public Object output() {
    return m_Uploaded;
  }
}
