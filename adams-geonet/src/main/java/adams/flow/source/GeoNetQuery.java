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
 * GeoNetQuery.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import gov.usgs.anss.query.EdgeQueryClient;
import gov.usgs.anss.query.EdgeQueryOptions;
import gov.usgs.anss.seed.MiniSeed;

import java.util.ArrayList;

import adams.core.AdditionalInformationHandler;
import adams.core.CWBQueryHelper;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;

/**
 <!-- globalinfo-start -->
 * Performs a query on against all files on a CWBQuery or Edge computer.<br/>
 * Thin wrapper around the GeoNetCWBQuery client.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.util.ArrayList<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: INFO
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: GeoNetQuery
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, outputs the gov.usgs.anss.seed.MiniSeed containers in an array 
 * &nbsp;&nbsp;&nbsp;rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-query-option &lt;adams.core.base.BaseString&gt; [-query-option ...] (property: queryOptions)
 * &nbsp;&nbsp;&nbsp;The options for the query client (all but '-t' can be supplied).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GeoNetQuery
  extends AbstractArrayProvider
  implements AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -9054269428610370288L;

  /** the query options. */
  protected BaseString[] m_QueryOptions;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Performs a query on against all files on a CWBQuery or Edge computer.\n"
	+ "Thin wrapper around the GeoNetCWBQuery client.";
  }
  
  /**
   * Returns the additional information.
   * 
   * @return		the additional information, null or 0-length string for no information
   */
  @Override
  public String getAdditionalInformation() {
    return new EdgeQueryOptions().getHelp();
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "query-option", "queryOptions",
	    new BaseString[0]);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "queryOptions", Utils.flatten(m_QueryOptions, " "));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, outputs the " + MiniSeed.class.getName() + " containers in an array rather than one-by-one.";
  }

  /**
   * Sets the query options to use.
   *
   * @param value	the options
   */
  public void setQueryOptions(BaseString[] value) {
    m_QueryOptions = value;
    reset();
  }

  /**
   * Returns the query options to use.
   *
   * @return 		the options
   */
  public BaseString[] getQueryOptions() {
    return m_QueryOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String queryOptionsTipText() {
    return "The options for the query client (all but '-t' can be supplied).";
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return ArrayList.class;
  }

  @Override
  protected String doExecute() {
    String				result;
    ArrayList<ArrayList<MiniSeed>>	list;
    EdgeQueryOptions			options;
    
    result  = null;
    options = CWBQueryHelper.toOptions(m_QueryOptions, true);
    list    = EdgeQueryClient.query(options);
    if (list == null) {
      result = "Failed to read data, check log!";
    }
    else {
      m_Queue.clear();
      for (ArrayList<MiniSeed> inner: list)
	m_Queue.add(inner);
    }
    
    return result;
  }
}
