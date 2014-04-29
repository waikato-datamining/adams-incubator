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
 * AbstractInfoTool.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.infotool;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.locale.LocaleUtils;
import org.geotools.swing.tool.CursorTool;

import adams.core.CleanUpHandler;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;

/**
 * Ancestor for info tools.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInfoTool
  extends CursorTool
  implements CleanUpHandler {
  
  /** The tool name */
  public static final String TOOL_NAME = LocaleUtils.getValue("CursorTool", "Info");

  /** Tool tip text */
  public static final String TOOL_TIP = LocaleUtils.getValue("CursorTool", "InfoTooltip");

  /** Cursor */
  public static final String CURSOR_IMAGE = "/org/geotools/swing/icons/mActionIdentify.png";

  /** Cursor hotspot coordinates */
  public static final Point CURSOR_HOTSPOT = new Point(0, 0);

  /** Icon for the control */
  public static final String ICON_IMAGE = "/org/geotools/swing/icons/mActionIdentify.png";

  /** the custom cursor. */
  protected Cursor m_Cursor;

  /** the logging level. */
  protected LoggingLevel m_LoggingLevel;

  /** the logger in use. */
  protected transient Logger m_Logger;

  /**
   * Constructor.
   */
  public AbstractInfoTool() {
    Toolkit tk = Toolkit.getDefaultToolkit();
    ImageIcon cursorIcon = new ImageIcon(getClass().getResource(CURSOR_IMAGE));
    m_Cursor = tk.createCustomCursor(cursorIcon.getImage(), CURSOR_HOTSPOT, TOOL_TIP);
    initializeLogging();
  }
  
  /**
   * Pre-configures the logging.
   */
  protected void initializeLogging() {
    m_LoggingLevel = LoggingLevel.WARNING;
  }
  
  /**
   * Initializes the logger.
   * <p/>
   * Default implementation uses the class name.
   */
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(getClass());
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }
  
  /**
   * Returns the logger in use.
   * 
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      configureLogger();
    return m_Logger;
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
  }

  /**
   * Returns the logging level.
   *
   * @return 		the level
   */
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }
  
  /**
   * Returns whether logging is enabled.
   * 
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isLoggingEnabled() {
    return LoggingHelper.isAtLeast(m_LoggingLevel.getLevel(), Level.CONFIG);
  }

  /**
   * Get the cursor for this tool. Sub-classes should override this
   * method to provide a custom cursor.
   *
   * @return the default cursor
   */
  @Override
  public Cursor getCursor() {
    return m_Cursor;
  }

  /**
   * Respond to a mouse click.
   *
   * @param e 		the mouse event
   */
  @Override
  public void onMouseClicked(MapMouseEvent e) {
    if (isLoggingEnabled())
      getLogger().fine("World position: " + e.getWorldPos());
    createReporter(e);
  }

  /**
   * Creates and shows a reporter.
   * 
   * @param e		the map click event
   */
  protected abstract void createReporter(MapMouseEvent e);

  /**
   * Cleans up data structures, frees up memory.
   */
  public abstract void cleanUp();
}
