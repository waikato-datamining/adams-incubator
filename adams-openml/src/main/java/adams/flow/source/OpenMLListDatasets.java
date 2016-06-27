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
 * OpenMLListDatasets.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.openml.OpenMLHelper;
import adams.data.conversion.OpenMLJsonToSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.db.SQL;
import adams.flow.core.Token;
import net.minidev.json.JSONAware;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Sends a free-form SQL query to OpenML and returns the result as JSON.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: OpenMLListDatasets
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
 * <pre>-regexp-name &lt;adams.core.base.BaseRegExp&gt; (property: regExpName)
 * &nbsp;&nbsp;&nbsp;The regular expression that the dataset names must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenMLListDatasets
  extends AbstractOpenMLSource {
  
  /** for serialization. */
  private static final long serialVersionUID = -5561129671356371714L;
  
  /** the regular expression on the name. */
  protected BaseRegExp m_RegExpName;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends a free-form SQL query to OpenML and returns the result as JSON.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regexp-name", "regExpName",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));
  }
  
  /**
   * Sets the regular expression that the names must match.
   *
   * @param value	the regexp
   */
  public void setRegExpName(BaseRegExp value) {
    m_RegExpName = value;
    reset();
  }

  /**
   * Returns the regular expression that the names must match.
   *
   * @return		the regexp
   */
  public BaseRegExp getRegExpName() {
    return m_RegExpName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpNameTipText() {
    return "The regular expression that the dataset names must match.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regExpName", m_RegExpName, "name: ");
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    JSONAware			json;
    String			sql;
    List<String>		where;
    int				i;
    OpenMLJsonToSpreadSheet	conv;
    
    result = null;
    
    sql = null;
    try {
      sql    = "select did, name, version from dataset where ";
      where = new ArrayList<>();
      where.add("source = 0");
      if (!m_RegExpName.isMatchAll())
	where.add("name regexp " + SQL.backquote(m_RegExpName));
      for (i = 0; i < where.size(); i++) {
	if (i > 0)
	  sql += " and ";
	sql += where.get(i);
      }
      sql   += " order by name, version";
      if (isLoggingEnabled())
	getLogger().info("Executing query: " + sql);
      json   = OpenMLHelper.convertJson(m_Connection.getConnector().freeQuery(sql));
      result = checkJSON(json);
      if (result == null) {
	conv   = new OpenMLJsonToSpreadSheet();
	conv.setInput(json);
	result = conv.convert();
	if (result == null)
	  m_OutputToken = new Token(conv.getOutput());
	conv.cleanUp();
      }
    }
    catch (Exception e) {
      result = handleException("Failed to query OpenML, query: " + sql, e);
    }
    
    return result;
  }
}
