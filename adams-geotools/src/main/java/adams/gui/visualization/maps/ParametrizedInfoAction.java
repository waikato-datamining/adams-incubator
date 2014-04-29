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
 * ParametrizedInfoAction.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.maps;

import java.awt.event.ActionEvent;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.MapAction;
import org.geotools.swing.tool.CursorTool;
import org.geotools.swing.tool.InfoTool;

import adams.flow.sink.infotool.InfoToolSupplier;

/**
 * InfoTool to be used in action can be supplied as parameter.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ParametrizedInfoAction
  extends MapAction {
  
  /** for serialization. */
  private static final long serialVersionUID = -7505049365611830828L;
  
  /** the info tool supplier to use. */
  protected InfoToolSupplier m_InfoToolSupplier;
  
  /** the current cursor tool. */
  protected CursorTool m_CursorTool;
  
  /**
   * Constructor. The associated control will be labelled with an icon.
   *
   * @param mapPane the map pane being serviced by this action
   */
  public ParametrizedInfoAction(MapPane mapPane, InfoToolSupplier infoTool) {
      this(mapPane, false, infoTool);
  }

  /**
   * Constructor. The associated control will be labelled with an icon and,
   * optionally, the tool name.
   *
   * @param mapPane the map pane being serviced by this action
   * @param showToolName set to true for the control to display the tool name
   */
  public ParametrizedInfoAction(MapPane mapPane, boolean showToolName, InfoToolSupplier infoTool) {
      m_InfoToolSupplier = infoTool;
      super.init(mapPane, (showToolName ? InfoTool.TOOL_NAME : null), InfoTool.TOOL_TIP, InfoTool.ICON_IMAGE);
  }

  /**
   * Called when the associated control is activated. Leads to the
   * map pane's cursor tool being set to a PanTool object
   * 
   * @param ev the event (not used)
   */
  public void actionPerformed(ActionEvent ev) {
    if (m_CursorTool == null)
      m_CursorTool = m_InfoToolSupplier.getInfoTool();
    getMapPane().setCursorTool(m_CursorTool);
  }
  
  /**
   * Sets the info tool supplier to use.
   * 
   * @param value	the info tool supplier
   */
  public void setInfoToolSupplier(InfoToolSupplier value) {
    m_InfoToolSupplier = value;
  }
  
  /**
   * Returns the current info tool supplier.
   * 
   * @return		the info tool supplier
   */
  public InfoToolSupplier getInfoToolSupplier() {
    return m_InfoToolSupplier;
  }
}
