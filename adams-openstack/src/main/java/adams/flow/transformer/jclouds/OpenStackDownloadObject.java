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

import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.conversion.ConversionFromString;
import adams.flow.sink.jclouds.OpenStackUploadObject;
import adams.flow.sink.jclouds.OpenStackUploadObject.Format;
import org.apache.commons.io.IOUtils;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ObjectApi;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Downloads an object from a container in the cloud.<br>
 * Receives the name of the object to download as input.
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
public class OpenStackDownloadObject
  extends AbstractJCloudsTransformerAction {

  private static final long serialVersionUID = 5077164507336679181L;

  /** the region. */
  protected String m_Region;

  /** the name of the container. */
  protected String m_ContainerName;

  /** the downloaded object. */
  protected Object m_Object;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Downloads an object from a container in the cloud.\n"
	+ "Receives the name of the object to download as input.";
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
   * Returns the data types being generated.
   *
   * @return		the data types
   */
  @Override
  public Class[] generates() {
    return new Class[]{Object.class};
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
    String			result;
    SwiftApi			swiftApi;
    ObjectApi 			objectApi;
    String			name;
    SwiftObject			swiftObject;
    Map<String, String>		meta;
    Format			format;
    ConversionFromString	conv;
    String			msg;
    ByteArrayOutputStream	bos;

    result   = null;
    m_Object = null;

    if (m_Region.isEmpty())
      result = "No region provided!";
    else if (m_ContainerName.isEmpty())
      result = "No container name provided!";

    if (result == null) {
      name = (String) m_Input;
      conv = null;
      try {
	swiftApi = (SwiftApi) getConnection().buildAPI(SwiftApi.class);
	objectApi = swiftApi.getObjectApi(m_Region, m_ContainerName);
	swiftObject = objectApi.get(name);
	meta = swiftObject.getMetadata();
	// what format?
	if (meta.containsKey(OpenStackUploadObject.METADATA_FORMAT))
	  format = Format.valueOf(meta.get(OpenStackUploadObject.METADATA_FORMAT));
	else
	  format = Format.STRING;
	// special conversion?
	if (format == Format.STRING_CONVERSION) {
	  if (meta.containsKey(OpenStackUploadObject.METADATA_CONVERSION)) {
	    conv = (ConversionFromString) OptionUtils.forCommandLine(ConversionFromString.class, meta.get(OpenStackUploadObject.METADATA_CONVERSION));
	  }
	  else {
	    getLogger().warning("No conversion commandline stored in meta-data, cannot convert from string back into object!");
	    format = Format.STRING;
	  }
	}
	// obtain data
	bos = new ByteArrayOutputStream();
	IOUtils.copy(swiftObject.getPayload().openStream(), bos);
	switch (format) {
	  case STRING:
	    m_Object = new String(bos.toByteArray());
	    break;
	  case STRING_CONVERSION:
	    conv.setInput(new String(bos.toByteArray()));
	    msg = conv.convert();
	    if (msg == null)
	      m_Object = conv.getOutput();
	    else
	      getLogger().severe("Failed to convert string: " + msg);
	    conv.cleanUp();
	    break;
	  case SERIALIZED_OBJECT:
	    m_Object = SerializationHelper.fromByteArray(bos.toByteArray());
	    break;
	  default:
	    throw new IllegalStateException("Unhandled format: " + format);
	}
      }
      catch (Exception e) {
	result = Utils.handleException(
	  this, "Failed to download object: " + m_Region + "/" + m_ContainerName + "/" + name, e);
      }
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
    return (m_Object != null);
  }

  /**
   * Returns the generated data.
   *
   * @return		the generated data
   */
  @Override
  public Object output() {
    return m_Object;
  }
}
