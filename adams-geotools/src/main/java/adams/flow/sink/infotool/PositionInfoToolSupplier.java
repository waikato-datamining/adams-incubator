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
 * PositionInfoToolSupplier.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.infotool;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.swing.dialog.JTextReporter;
import org.geotools.swing.dialog.TextReporterListener;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * Only displays the position the user clicked on.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PositionInfoToolSupplier
  extends AbstractInfoToolSupplier {

  /** for serialization. */
  private static final long serialVersionUID = 5278512097499463854L;

  /**
   * Simple {@link CursorTool} that just reports the position.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  @MixedCopyright(
      copyright = "(C) 2008-2011, Open Source Geospatial Foundation (OSGeo)",
      author = "Michael Bedward",
      license = License.LGPL21,
      note = "Original code from: org.geotools.swing.tool.InfoTool"
  )
  public static class PositionInfoTool
    extends AbstractInfoTool
    implements TextReporterListener {

    /** for displaying the information. */
    protected JTextReporter.Connection textReporterConnection;

    /**
     * Gets the connection to the text reporter displayed by this tool. Returns
     * {@code null} if the reporter dialog is not currently displayed. The
     * connection should not be stored because it will expire when the reporter
     * dialog is closed.
     * <p>
     * This method was added for unit test purposes but may be useful for 
     * applications wishing to access the feature data report text, e.g. by
     * overriding {@linkplain #onReporterUpdated()}.
     * 
     * @return the text reporter connection or {@code null} if the reporter dialog
     *     is not currently displayed
     */
    public JTextReporter.Connection getTextReporterConnection() {
      return textReporterConnection;
    }

    /**
     * Creates and shows a {@code JTextReporter}. Does nothing if the 
     * reporter is already active.
     * 
     * @param e		the map click event
     */
    @Override
    protected void createReporter(MapMouseEvent e) {
      if (textReporterConnection == null) {
	textReporterConnection = JTextReporter.showDialog(
	    "Position info", 
	    null, 
	    JTextReporter.DEFAULT_FLAGS,
	    20, 40);
	textReporterConnection.addListener(this);
      }
      DirectPosition2D pos = e.getWorldPos();
      textReporterConnection.append(String.format("Pos x=%.4f y=%.4f\n", pos.x, pos.y));
    }

    /**
     * Called when a {@code JTextReporter} dialog used by this tool is closed.
     */
    @Override
    public void onReporterClosed() {
      textReporterConnection = null;
    }

    /**
     * Called when text is updated in a {@linkplain JTextReporter} dialog being used 
     * by this tool. This is an empty method but may be useful to override.
     * 
     */
    @Override
    public void onReporterUpdated() {
    }

    /**
     * Cleans up data structures, frees up memory.
     */
    @Override
    public void cleanUp() {
      if (textReporterConnection != null)
	textReporterConnection.closeDialog();
    }
  }
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Just displays the position the user clicked on.";
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
    PositionInfoTool	result;
    
    result = new PositionInfoTool();
    result.setLoggingLevel(m_LoggingLevel);
    
    return result;
  }
}
