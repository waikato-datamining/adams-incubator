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
 * MiniSeedFileWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import gov.usgs.anss.query.EdgeQueryOptions;
import gov.usgs.anss.query.EdgeQueryOptions.OutputType;
import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.query.Outputer;
import gov.usgs.anss.query.ZeroFilledSpan;
import gov.usgs.anss.query.outputter.Filename;
import gov.usgs.anss.seed.MiniSeed;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import adams.core.AdditionalInformationHandler;
import adams.core.CWBQueryHelper;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderDirectory;

/**
 <!-- globalinfo-start -->
 * Writes a list of MiniSeed containers to disk using the specified outputer format defined in the options ('-t' option).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.util.ArrayList<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: MiniSeedFileWriter
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
 * <pre>-query-option &lt;adams.core.base.BaseString&gt; [-query-option ...] (property: queryOptions)
 * &nbsp;&nbsp;&nbsp;The options for the query client.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-output-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: outputDir)
 * &nbsp;&nbsp;&nbsp;The output directory to store the generated files in.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MiniSeedFileWriter
  extends AbstractSink
  implements AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4376242653273384154L;

  /** the query options. */
  protected BaseString[] m_QueryOptions;

  /** the output directory where to store the data. */
  protected PlaceholderDirectory m_OutputDir;
  
  /** the outputer to use. */
  protected transient Outputer m_Outputer;

  /** whether the output was initialized. */
  protected Boolean m_OutputerInitialized;
  
  /** the options in use. */
  protected transient EdgeQueryOptions m_EdgeOptions;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Writes a list of MiniSeed containers to disk using the specified "
	+ "outputer format defined in the options ('-t' option).";
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

    m_OptionManager.add(
	    "output-dir", "outputDir",
	    new PlaceholderDirectory());
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
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Outputer            = null;
    m_OutputerInitialized = null;
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "queryOptions", Utils.flatten(m_QueryOptions, " ") + " ", "options: ");
    result += QuickInfoHelper.toString(this, "outputDir", m_OutputDir, ", dir: ");
    
    return result;
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
    return "The options for the query client.";
  }

  /**
   * Sets the output directory.
   *
   * @param value	the dir
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the output directory.
   *
   * @return 		the dir
   */
  public PlaceholderDirectory getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String outputDirTipText() {
    return "The output directory to store the generated files in.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{ArrayList.class};
  }

  /**
   * Prints the MiniSeed containers to stdout.
   * 
   * @param list	the containers
   * @param options	the output options
   */
  protected void print(ArrayList list, EdgeQueryOptions options) {
    int fill = 0x80000000;
    boolean nogaps = false;
    for (int i = 0; i < options.extraArgs.size(); i++) {
      if (((String)options.extraArgs.get(i)).equals("-fill"))
	fill = Integer.parseInt((String)options.extraArgs.get(i + 1));
      if (((String)options.extraArgs.get(i)).equals("-nogaps"))
	nogaps = true;
    }

    GregorianCalendar start = new GregorianCalendar();
    start.setTimeInMillis(options.getBeginWithOffset().getMillis());
    ZeroFilledSpan span = new ZeroFilledSpan(list, start, options.getDuration().doubleValue(), fill);
    if (span.getRate() <= 0.0D)
      return;
    GregorianCalendar spanStart = span.getStart();
    double currentTime = spanStart.getTimeInMillis();
    double period = 1000D / span.getRate();
    for (int i = 0; i < span.getNsamp(); i++) {
      int data = span.getData(i);
      if (nogaps || data != fill) {
	StringBuilder sb = new StringBuilder().append(Math.round(currentTime)).append(" ").append(span.getData(i));
	getLogger().info(sb.toString());
      }
      currentTime += period;
    }
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    ArrayList		list;
    NSCL		nscl;
    String		filename;
    
    result = null;
    
    if (m_OutputerInitialized == null) {
      m_EdgeOptions         = CWBQueryHelper.toOptions(m_QueryOptions, false);
      m_Outputer            = m_EdgeOptions.getOutputter();
      m_OutputerInitialized = true;
    }
    
    list = (ArrayList) (m_InputToken.getPayload());
    if (list.size() > 0) {
      if (m_Outputer != null) {
	nscl     = NSCL.stringToNSCL(((MiniSeed) list.get(0)).getSeedName());
	if (   m_EdgeOptions.getType() == OutputType.ms
	    || m_EdgeOptions.getType() == OutputType.dcc
	    || m_EdgeOptions.getType() == OutputType.dcc512
	    || m_EdgeOptions.getType() == OutputType.msz)
	  filename = Filename.makeFilename(m_OutputDir.getAbsolutePath() + File.separator + m_EdgeOptions.filemask, nscl, (MiniSeed) list.get(0));
	else
	  filename = Filename.makeFilename(m_OutputDir.getAbsolutePath() + File.separator + m_EdgeOptions.filemask, nscl, m_EdgeOptions.getBegin());
	try {
	  m_Outputer.makeFile(nscl, filename, list);
	}
	catch (Exception e) {
	  result = handleException("Failed to write to output file '" + filename + "'!", e);
	}
      }
      else {
	// print to stdout
	print(list, m_EdgeOptions);
      }
    }
    else {
      result = "No data to write!";
    }
    
    return result;
  }
}
