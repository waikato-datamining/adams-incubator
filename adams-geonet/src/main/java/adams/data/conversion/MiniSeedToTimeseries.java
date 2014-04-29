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
 * MiniSeedToTimeseries.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import gov.usgs.anss.query.EdgeQueryOptions;
import gov.usgs.anss.query.ZeroFilledSpan;
import gov.usgs.anss.seed.MiniSeed;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import adams.core.AdditionalInformationHandler;
import adams.core.CWBQueryHelper;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 * Converts a {@link MiniSeed} object into a {@link Timeseries}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MiniSeedToTimeseries
  extends AbstractConversion
  implements AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -2447669967809014709L;

  /** the query options. */
  protected BaseString[] m_QueryOptions;
  
  /** the options in use. */
  protected transient EdgeQueryOptions m_EdgeOptions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a MiniSeed object into a Timeseries object.";
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
   * Returns the additional information.
   * 
   * @return		the additional information, null or 0-length string for no information
   */
  @Override
  public String getAdditionalInformation() {
    return new EdgeQueryOptions().getHelp();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "queryOptions", Utils.flatten(m_QueryOptions, " ") + " ", "options: ");
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
    return "The options to use in the conversion.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return MiniSeed.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Timeseries.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    MiniSeed 		ms;
    int 		fill;
    boolean 		nogaps;
    int			i;
    GregorianCalendar 	start;
    ArrayList<MiniSeed>	list;
    ZeroFilledSpan 	span;
    GregorianCalendar 	spanStart;
    double 		currentTime;
    double 		period;
    Timeseries 		result;
    int 		data;
    
    if (m_EdgeOptions == null)
      m_EdgeOptions = CWBQueryHelper.toOptions(m_QueryOptions, false);
    ms = (MiniSeed) m_Input;
    fill = 0x80000000;
    nogaps = false;
    for (i = 0; i < m_EdgeOptions.extraArgs.size(); i++) {
      if (((String)m_EdgeOptions.extraArgs.get(i)).equals("-fill"))
	fill = Integer.parseInt((String)m_EdgeOptions.extraArgs.get(i + 1));
      if (((String)m_EdgeOptions.extraArgs.get(i)).equals("-nogaps"))
	nogaps = true;
    }

    start = new GregorianCalendar();
    start.setTimeInMillis(m_EdgeOptions.getBeginWithOffset().getMillis());
    list = new ArrayList<MiniSeed>();
    list.add(ms);
    span = new ZeroFilledSpan(list, start, m_EdgeOptions.getDuration().doubleValue(), fill);
    if (span.getRate() <= 0.0D)
      throw new IllegalStateException();
    
    spanStart   = span.getStart();
    currentTime = spanStart.getTimeInMillis();
    period      = 1000D / span.getRate();
    result      = new Timeseries();
    result.setID(ms.getSeedName());
    for (i = 0; i < span.getNsamp(); i++) {
      data = span.getData(i);
      if (nogaps || data != fill)
	result.add(new TimeseriesPoint(new Date(Math.round(currentTime)), data));
      currentTime += period;
    }
    return result;
  }
}
