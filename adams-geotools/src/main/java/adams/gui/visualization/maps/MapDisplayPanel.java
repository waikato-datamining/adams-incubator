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
 * MapDisplayPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.maps;

import adams.core.CleanUpHandler;
import adams.data.io.input.AbstractGeoToolsLayerReader;
import adams.flow.sink.infotool.AbstractInfoToolSupplier;
import adams.flow.sink.infotool.DefaultInfoToolSupplier;
import adams.flow.sink.infotool.InfoToolSupplier;
import adams.gui.action.AbstractBaseAction;
import adams.gui.chooser.GeoToolsLayerFileChooser;
import adams.gui.chooser.ImageFileChooser;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandlerWithCommandline;
import adams.gui.core.RecentFilesHandlerWithCommandline.Setup;
import adams.gui.core.ToolBarPanel;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.MapLayerTable;
import org.geotools.swing.action.NoToolAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ResetAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;
import org.geotools.swing.control.JMapStatusBar;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.List;

/**
 * Specialized panel for displaying maps.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MapDisplayPanel
  extends ToolBarPanel
  implements MenuBarProvider, SendToActionSupporter, AntiAliasingSupporter, CleanUpHandler {

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "MapDisplaySession.props";

  /** the zoom factor. */
  public final static double ZOOM_FACTOR = 0.1;
  
  /** for serialization. */
  private static final long serialVersionUID = 6932946817132289325L;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /** the underlying map. */
  protected JMapPane m_MapPane;

  /** the no-tool action. */
  protected NoToolAction m_ActionNoTool;
  
  /** the zoom-in action. */
  protected ZoomInAction m_ActionZoomIn;
  
  /** the zoom-out action. */
  protected ZoomOutAction m_ActionZoomOut;
  
  /** the pan action. */
  protected PanAction m_ActionPan;
  
  /** the info action. */
  protected ParametrizedInfoAction m_ActionInfo;
  
  /** the reset action. */
  protected ResetAction m_ActionReset;
  
  /** the status bar. */
  protected JMapStatusBar m_StatusBar;
  
  /** the layer table. */
  protected MapLayerTable m_TableLayers;
  
  /** the split pane for layers and map. */
  protected BaseSplitPane m_SplitPane;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemFileLoadRecent;

  /** the recent files handler. */
  protected RecentFilesHandlerWithCommandline<JMenu> m_RecentFilesHandler;

  /** the filechooser for opening/saving files. */
  protected GeoToolsLayerFileChooser m_FileChooser;
  
  /** the info tool to use. */
  protected AbstractInfoToolSupplier m_InfoTool;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RecentFilesHandler = null;
    m_FileChooser        = new GeoToolsLayerFileChooser();
    m_InfoTool           = new DefaultInfoToolSupplier();
  }
  
  /**
   * Initializes the widgets. 
   */
  @Override
  protected void initGUI() {
    MapContent	map;
    
    super.initGUI();

    getContentPanel().setLayout(new BorderLayout());
    
    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setResizeWeight(1.0);
    m_SplitPane.setOneTouchExpandable(true);
    getContentPanel().add(m_SplitPane, BorderLayout.CENTER);

    map = new MapContent();
    map.setTitle(getName());
    m_MapPane = new JMapPane();
    m_MapPane.setRenderer(new StreamingRenderer());
    m_MapPane.setMapContent(map);
    m_MapPane.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
	int clicks = e.getWheelRotation();
	// -ve means wheel moved up, +ve means down
	int sign = (clicks < 0 ? -1 : 1);

	ReferencedEnvelope env = m_MapPane.getDisplayArea();
	double width = env.getWidth();
	double delta = width * ZOOM_FACTOR * sign;

	env.expandBy(delta);
	m_MapPane.setDisplayArea(env);
	m_MapPane.repaint();      
      }
    });
    m_SplitPane.setLeftComponent(m_MapPane);

    m_TableLayers = new MapLayerTable(m_MapPane);
    m_TableLayers.setPreferredSize(new Dimension(200, -1));
    m_SplitPane.setRightComponent(m_TableLayers);
    
    m_StatusBar = JMapStatusBar.createDefaultStatusBar(m_MapPane);
    getContentPanel().add(m_StatusBar, BorderLayout.SOUTH);
  }
  
  /**
   * Sets up all the actions.
   *
   * @see		AbstractBaseAction
   */
  @Override
  protected void initActions() {
    m_ActionNoTool  = new NoToolAction(m_MapPane);
    m_ActionZoomIn  = new ZoomInAction(m_MapPane);
    m_ActionZoomOut = new ZoomOutAction(m_MapPane);
    m_ActionPan     = new PanAction(m_MapPane);
    m_ActionInfo    = new ParametrizedInfoAction(m_MapPane, m_InfoTool);
    m_ActionReset   = new ResetAction(m_MapPane);
  }

  /**
   * Sets up the toolbar, using the actions.
   *
   * @see		#initActions()
   */
  @Override
  protected void initToolBar() {
    BaseButton 		btn;
    ButtonGroup 	group;

    group = new ButtonGroup();

    btn = new BaseButton(m_ActionNoTool);
    btn.setName(JMapFrame.TOOLBAR_POINTER_BUTTON_NAME);
    m_ToolBar.add(btn);
    group.add(btn);

    btn = new BaseButton(m_ActionZoomIn);
    btn.setName(JMapFrame.TOOLBAR_ZOOMIN_BUTTON_NAME);
    m_ToolBar.add(btn);
    group.add(btn);

    btn = new BaseButton(m_ActionZoomOut);
    btn.setName(JMapFrame.TOOLBAR_ZOOMOUT_BUTTON_NAME);
    m_ToolBar.add(btn);
    group.add(btn);

    m_ToolBar.addSeparator();

    btn = new BaseButton(m_ActionPan);
    btn.setName(JMapFrame.TOOLBAR_PAN_BUTTON_NAME);
    m_ToolBar.add(btn);
    group.add(btn);

    m_ToolBar.addSeparator();

    btn = new BaseButton(m_ActionInfo);
    btn.setName(JMapFrame.TOOLBAR_INFO_BUTTON_NAME);
    m_ToolBar.add(btn);

    m_ToolBar.addSeparator();

    btn = new BaseButton(m_ActionReset);
    btn.setName(JMapFrame.TOOLBAR_RESET_BUTTON_NAME);
    m_ToolBar.add(btn);
  }

  /**
   * Updates the enabled state of the actions.
   */
  @Override
  protected void updateActions() {
    // nothing to do
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    
    if (m_MapPane.getRenderer().getRendererHints() == null) {
      m_MapPane.getRenderer().setRendererHints(
	  new RenderingHints(
	      RenderingHints.KEY_ANTIALIASING, 
	      value ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF));
    }
    else {
      m_MapPane.getRenderer().getRendererHints().put(
	  RenderingHints.KEY_ANTIALIASING, 
	  value ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    }
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "If enabled, uses anti-aliasing for drawing lines.";
  }
  
  /**
   * Returns the underlying map pane.
   * 
   * @return		the pane
   */
  public JMapPane getMapPane() {
    return m_MapPane;
  }
  
  /**
   * Returns the map content.
   * 
   * @return		the map content
   */
  public MapContent getMapContent() {
    return m_MapPane.getMapContent();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (getMapContent() != null)
      getMapContent().dispose();
  }
  
  /**
   * Removes all layers.
   */
  public void removeAllLayers() {
    List<Layer>		layers;
    
    layers = getMapContent().layers();
    for (Layer layer: layers)
      getMapContent().removeLayer(layer);
  }
  
  /**
   * Adds the layer.
   * 
   * @param layer	the layer to add
   */
  public void addLayer(Layer layer) {
    getMapContent().addLayer(layer);
  }
  
  /**
   * Removes the layer.
   * 
   * @param layer	the layer to remove
   * @return		true if the layer was removed
   */
  public boolean removeLayer(Layer layer) {
    return getMapContent().removeLayer(layer);
  }
  
  /**
   * Forces a reset of the map pane.
   */
  public void reset() {
    getMapPane().reset();
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenuItem		menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Clear
      menuitem = new JMenuItem("Clear");
      menu.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(GUIHelper.getIcon("new.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  removeAllLayers();
	}
      });

      // File/Open...
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  open();
	}
      });

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandlerWithCommandline<JMenu>(SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,Setup>() {
	public void recentItemAdded(RecentItemEvent<JMenu,Setup> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,Setup> e) {
	  load((AbstractGeoToolsLayerReader) e.getItem().getHandler(), e.getItem().getFile());
	}
      });
      m_MenuItemFileLoadRecent = submenu;

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  exit();
	}
      });

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;

    // File
    m_MenuItemFileLoadRecent.setEnabled(m_RecentFilesHandler.size() > 0);
  }
  
  /**
   * Opens a layer file.
   */
  protected void open() {
    int		retVal;
    File[]	files;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != ImageFileChooser.APPROVE_OPTION)
      return;

    files = m_FileChooser.getSelectedFiles();
    for (File file: files)
      load(m_FileChooser.getReader(), file);
  }

  /**
   * Loads the specified file. Determines the reader automatically.
   *
   * @param file	the file to load
   */
  public void load(File file) {
    load(m_FileChooser.getReaderForFile(file), file);
  }

  /**
   * Loads the specified file.
   *
   * @param reader	the reader to use
   * @param file	the file to load
   */
  public void load(AbstractGeoToolsLayerReader reader, File file) {
    Layer	layer;

    if (reader == null) {
      GUIHelper.showErrorMessage(
	  this, "No reader supplied for '" + m_FileChooser.getSelectedFile() + "'!");
      return;
    }
    
    layer = reader.read();
    
    if (layer == null) {
      GUIHelper.showErrorMessage(
	  this, "Failed to load layer from '" + m_FileChooser.getSelectedFile() + "'!");
    }
    else {
      m_MapPane.getMapContent().addLayer(layer);
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(new Setup(file, reader));
      reset();
    }

    updateMenu();
  }

  /**
   * Exits the display.
   */
  protected void exit() {
    cleanUp();

    if (getParentFrame() != null) {
      getParentFrame().setVisible(false);
      getParentFrame().dispose();
    }
    else if (getParentDialog() != null) {
      getParentDialog().setVisible(false);
      getParentDialog().dispose();
    }
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    return new Class[]{JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve an item for
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return true;
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object	result;

    result = null;

    if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      result = m_MapPane;
    }

    return result;
  }
  
  /**
   * Sets the info tool to use.
   * 
   * @param value	the info tool
   */
  public void setInfoTool(InfoToolSupplier value) {
    m_ActionInfo.setInfoToolSupplier(value);
  }
  
  /**
   * Returns the current info tool.
   * 
   * @return		the info tool
   */
  public InfoToolSupplier getInfoTool() {
    return m_ActionInfo.getInfoToolSupplier();
  }
}
