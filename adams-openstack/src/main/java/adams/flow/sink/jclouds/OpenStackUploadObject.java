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
 * OpenStackUploadObject.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.jclouds;

import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.base.BaseKeyValuePair;
import adams.core.option.OptionUtils;
import adams.data.conversion.ConversionFromString;
import adams.data.conversion.ConversionToString;
import adams.data.conversion.StringToString;
import com.google.common.io.ByteSource;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.features.ContainerApi;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.openstack.swift.v1.options.CreateContainerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Stores an object with optional meta-data in the cloud, creates the container if necessary.<br>
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
 * <pre>-container-name &lt;java.lang.String&gt; (property: containerName)
 * &nbsp;&nbsp;&nbsp;The container name to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-object-name &lt;java.lang.String&gt; (property: objectName)
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
public class OpenStackUploadObject
  extends AbstractJCloudsSinkAction {

  private static final long serialVersionUID = 5077164507336679181L;

  /** the key in the object's metadata stating the format. */
  public static final String METADATA_FORMAT = "Format";

  /** the key in the object's metadata listing the commandline for the
   * {@link ConversionToString} if the format is {@link Format#STRING_CONVERSION}. */
  public static final String METADATA_CONVERSION = "Conversion";

  /**
   * Enumeration of format types.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Format {
    STRING,
    STRING_CONVERSION,
    SERIALIZED_OBJECT
  }

  /** the region. */
  protected String m_Region;

  /** the name for the container. */
  protected String m_ContainerName;

  /** the name for the object. */
  protected String m_ObjectName;

  /** the meta-data. */
  protected BaseKeyValuePair[] m_MetaData;

  /** the format. */
  protected Format m_Format;

  /** the "to" string conversion. */
  protected ConversionToString m_ToString;

  /** the "from" string conversion. */
  protected ConversionFromString m_FromString;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Stores an object with optional meta-data in the cloud, creates the container if necessary.\n"
	+ "Outputs the name of the stored object.";
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

    m_OptionManager.add(
      "object-name", "objectName",
      "");

    m_OptionManager.add(
      "meta-data", "metaData",
      new BaseKeyValuePair[0]);

    m_OptionManager.add(
      "format", "format",
      Format.STRING);

    m_OptionManager.add(
      "to-string", "toString",
      new StringToString());

    m_OptionManager.add(
      "from-string", "fromString",
      new StringToString());
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
    return "The container name to use.";
  }

  /**
   * Sets the object name to use.
   *
   * @param value	the name
   */
  public void setObjectName(String value) {
    m_ObjectName = value;
    reset();
  }

  /**
   * Returns the object name to use.
   *
   * @return		the name
   */
  public String getObjectName() {
    return m_ObjectName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectNameTipText() {
    return "The object name to use.";
  }

  /**
   * Sets the meta-data to use.
   *
   * @param value	the meta-data
   */
  public void setMetaData(BaseKeyValuePair[] value) {
    m_MetaData = value;
    reset();
  }

  /**
   * Returns the meta-data to use.
   *
   * @return		the meta-data
   */
  public BaseKeyValuePair[] getMetaData() {
    return m_MetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataTipText() {
    return "The meta-data to use (optional).";
  }

  /**
   * Sets the format to store the incoming data in.
   *
   * @param value	the format
   */
  public void setFormat(Format value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format to store the incoming data in.
   *
   * @return		the format
   */
  public Format getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format to store the incoming data in.";
  }

  /**
   * Sets the conversion to convert object into a string.
   *
   * @param value	the conversion
   */
  public void setToString(ConversionToString value) {
    m_ToString = value;
    reset();
  }

  /**
   * Returns the conversion to convert object into a string.
   *
   * @return		the conversion
   */
  public ConversionToString getToString() {
    return m_ToString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String toStringTipText() {
    return "The conversion to convert object into string.";
  }

  /**
   * Sets the conversion to convert back to objecct.
   *
   * @param value	the conversion
   */
  public void setFromString(ConversionFromString value) {
    m_FromString = value;
    reset();
  }

  /**
   * Returns the conversion to convert back into object.
   *
   * @return		the conversion
   */
  public ConversionFromString getFromString() {
    return m_FromString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fromStringTipText() {
    return "The conversion to convert the string back into an object.";
  }

  /**
   * Returns the accepted data types.
   *
   * @return		the data types
   */
  @Override
  public Class[] accepts() {
    switch (m_Format) {
      case STRING:
	return new Class[]{String.class};
      case SERIALIZED_OBJECT:
      case STRING_CONVERSION:
	return new Class[]{Object.class};
      default:
	throw new IllegalStateException("Unhandled format: " + m_Format);
    }
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
    ContainerApi 		containerApi;
    Container			container;
    CreateContainerOptions 	options;
    Map<String,String> 		meta;
    ObjectApi 			objectApi;
    Payload 			payload;
    byte[]			data;
    String			msg;

    result = null;

    if (m_Region.isEmpty())
      result = "No region provided!";
    else if (m_ContainerName.isEmpty())
      result = "No container name provided!";

    if (result == null) {
      swiftApi     = (SwiftApi) getConnection().buildAPI(SwiftApi.class);
      containerApi = swiftApi.getContainerApi(m_Region);

      // container already present?
      container = containerApi.get(m_ContainerName);

      // meta data
      meta = new HashMap<>();
      meta.put(METADATA_FORMAT, m_Format.toString());
      if (m_Format == Format.STRING_CONVERSION)
	meta.put(METADATA_CONVERSION, OptionUtils.getCommandLine(m_FromString));
      if (m_MetaData.length > 0) {
	for (BaseKeyValuePair pair: m_MetaData)
	  meta.put(pair.getPairKey(), pair.getPairValue());
      }
      options = CreateContainerOptions.Builder.metadata(meta);
      if (container == null)
	containerApi.create(m_ContainerName, options);
      else
	containerApi.updateMetadata(m_ContainerName, meta);

      // convert data
      data = null;
      try {
	switch (m_Format) {
	  case STRING:
	    data = ((String) m_Input).getBytes();
	    break;
	  case STRING_CONVERSION:
	    m_ToString.setInput(m_Input);
	    msg = m_ToString.convert();
	    if (msg == null)
	      data = ((String) m_ToString.getOutput()).getBytes();
	    else
	      getLogger().severe("Failed to convert object to string: " + msg);
	    m_ToString.cleanUp();
	    break;
	  case SERIALIZED_OBJECT:
	    data = SerializationHelper.toByteArray(m_Input);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled format: " + m_Format);
	}
      }
      catch (Exception e) {
	result = Utils.handleException(this, "Failed to create byte array from input!", e);
      }

      // upload data
      if (data != null) {
	payload   = Payloads.newByteSourcePayload(ByteSource.wrap(data));
	objectApi = swiftApi.getObjectApi(m_Region, m_ContainerName);
	objectApi.put(m_ObjectName, payload);
	if (isLoggingEnabled())
	  getLogger().info("Data uploaded: " + m_Region + "/" + m_ContainerName + "/" + m_ObjectName);
      }
      else {
	getLogger().severe("Failed to generate payload!");
      }
    }

    return result;
  }
}
