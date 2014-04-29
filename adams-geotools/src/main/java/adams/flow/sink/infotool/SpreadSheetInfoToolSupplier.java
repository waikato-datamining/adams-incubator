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
 * SpreadSheetInfoToolSupplier.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.infotool;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JPanel;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.geotools.swing.tool.FeatureLayerHelper;
import org.geotools.swing.tool.InfoToolHelper;
import org.geotools.swing.tool.InfoToolResult;

import weka.core.Utils;
import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.spreadsheet.DataRowType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseMultiPagePane;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;

/**
 * SpreadSheet-based tool supplier.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetInfoToolSupplier
  extends AbstractInfoToolSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -4002971853300712434L;

  /**
   * SpreadSheet-based info tool.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  @MixedCopyright(
      author = "Michael Bedward",
      copyright = "2008-2011, Open Source Geospatial Foundation (OSGeo)",
      license = License.LGPL21,
      note = "retrieval of feature layer data based on org.geotools.swing.tool.InfoTool class"
  )
  public class SpreadSheetInfoTool
    extends AbstractInfoTool {

    /** the dialog with the spreadsheets. */
    protected ApprovalDialog m_Dialog;
    
    /** the multi-page pane for displaying the spectra. */
    protected BaseMultiPagePane m_Content;

    /**
     * Sets whether to allow the user to search the table.
     *
     * @param value 	true if to allow search
     */
    public void setAllowSearch(boolean value) {
      m_AllowSearch = value;
    }

    /**
     * Returns whether to allow the user to search the table.
     *
     * @return 		true if to allow search
     */
    public boolean getAllowSearch() {
      return m_AllowSearch;
    }

    /**
     * Creates and shows a reporter.
     * 
     * @param e		the map click event
     */
    @Override
    protected void createReporter(MapMouseEvent e) {
      DirectPosition2D 		pos;
      MapContent 		content;
      int 			n;
      InfoToolHelper 		helper;
      String 			layerName;
      InfoToolResult 		result;
      SpreadSheet		sheet;
      JPanel			panel;
      SearchPanel		searchPanel;
      Row			headerRow;
      Row			dataRow;
      String			id;
      Map<String,Object>	data;
      
      if (m_Dialog == null) {
	m_Dialog = new ApprovalDialog((Dialog) null, ModalityType.MODELESS);
	m_Dialog.setCancelVisible(false);
	m_Dialog.setDiscardVisible(false);
	m_Content = new BaseMultiPagePane();
	m_Dialog.getContentPane().add(m_Content, BorderLayout.CENTER);
	m_Dialog.setSize(800, 600);
	m_Dialog.setLocationRelativeTo(e.getComponent());
      }
      
      pos     = e.getWorldPos();
      content = getMapPane().getMapContent();
      n       = 0;
      m_Content.removeAll();
      for (Layer layer : content.layers()) {
	if (layer.isSelected()) {
	  layerName = layer.getTitle();
	  if ((layerName == null) || layerName.isEmpty())
	    layerName = layer.getFeatureSource().getName().getLocalPart();
	  if ((layerName == null) || layerName.isEmpty())
	    layerName = layer.getFeatureSource().getSchema().getName().getLocalPart();

	  helper = new FeatureLayerHelper();
	  helper.setMapContent(content);
	  helper.setLayer(layer);

	  try {
	    result    = helper.getInfo(pos);
	    sheet     = new SpreadSheet();
	    sheet.setDataRowClass(DataRowType.SPARSE.getRowClass());
	    headerRow = sheet.getHeaderRow();
	    headerRow.addCell("id").setContent("ID");
	    for (n = 0; n < result.getNumFeatures(); n++) {
	      id   = result.getFeatureId(n);
	      data = result.getFeatureData(n);
	      dataRow = sheet.addRow();
	      dataRow.addCell("id").setContent(id);
	      for (String key: data.keySet()) {
		if (!headerRow.hasCell(key))
		  headerRow.addCell(key).setContent(key);
		dataRow.addCell(key).setContent(data.get(key).toString());
	      }
	    }
	    final SpreadSheetTable table = new SpreadSheetTable(sheet);
	    panel = new JPanel(new BorderLayout());
	    panel.add(new BaseScrollPane(table));
	    if (m_AllowSearch) {
	      searchPanel = new SearchPanel(LayoutType.HORIZONTAL, true);
	      searchPanel.addSearchListener(new SearchListener() {
		@Override
		public void searchInitiated(SearchEvent e) {
		  table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
		}
	      });
	      panel.add(searchPanel, BorderLayout.SOUTH);
	    }
	    m_Content.addPage(layerName, panel);
	  } 
	  catch (Exception ex) {
	    getLogger().log(Level.WARNING, "Unable to query layer " + layerName);
	  }
	}
      }
      
      m_Dialog.setTitle("Long: " + Utils.doubleToString(pos.getX(), 2) + ", Lat: " + Utils.doubleToString(pos.getY(), 2));
      if (!m_Dialog.isVisible())
	m_Dialog.setVisible(true);
    }

    /**
     * Cleans up data structures, frees up memory.
     */
    @Override
    public void cleanUp() {
      if (m_Dialog != null) {
	m_Dialog.setVisible(false);
	m_Dialog.dispose();
      }
    }
  }
  
  /** the CursorTool instance in use. */
  protected SpreadSheetInfoTool m_InfoTool;
  
  /** whether to allow searching. */
  protected boolean m_AllowSearch;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Provides the a spreadsheet-based info tool.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "allow-search", "allowSearch",
	    false);
  }

  /**
   * Sets whether to allow the user to search the table.
   *
   * @param value 	true if to allow search
   */
  public void setAllowSearch(boolean value) {
    m_AllowSearch = value;
    reset();
  }

  /**
   * Returns whether to allow the user to search the table.
   *
   * @return 		true if to allow search
   */
  public boolean getAllowSearch() {
    return m_AllowSearch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowSearchTipText() {
    return "Whether to allow the user to search the table.";
  }

  /**
   * Returns whether a database connection is required.
   * 
   * @return		always false
   */
  @Override
  public boolean requiresDatabaseConnection() {
    return false;
  }

  /**
   * Returns a new {@link CursorTool} instance.
   * 
   * @return		the tool
   */
  @Override
  protected CursorTool newInfoTool() {
    return new SpreadSheetInfoTool();
  }
}
