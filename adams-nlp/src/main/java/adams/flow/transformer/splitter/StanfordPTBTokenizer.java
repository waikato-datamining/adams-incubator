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
 * StanfordPTBTokenizer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.splitter;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.util.StringUtils;

/**
 <!-- globalinfo-start -->
 * Uses Stanford's PTBTokenizer.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "2013 StackExchange",
    author = "Yaniv.H",
    license = License.CC_BY_SA_25,
    url = "http://stackoverflow.com/a/19464001"
)
public class StanfordPTBTokenizer
  extends AbstractDocumentToSentences {

  /** for serialization. */
  private static final long serialVersionUID = 4043221889853222507L;
  
  /** the tokenizer factory to use. */
  protected static TokenizerFactory m_TokenizerFactory;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses Stanford's PTBTokenizer.";
  }

  /**
   * Returns the tokenizer factory to use.
   * 
   * @return		the factory
   */
  protected static synchronized TokenizerFactory getTokenizerFactory() {
    if (m_TokenizerFactory == null) {
      m_TokenizerFactory = PTBTokenizer.factory(
	  new CoreLabelTokenFactory(),
          "normalizeParentheses=false,normalizeOtherBrackets=false,invertible=true");
    }
    return m_TokenizerFactory;
  }
  
  /**
   * Performs the actual splitting.
   * 
   * @param doc		the document to split
   * @return		the list of sentence strings
   */
  @Override
  protected List<String> doSplit(String doc) {
    List<String>		result;
    DocumentPreprocessor	preProcessor;
    
    result = new ArrayList<String>();
    
    preProcessor = new DocumentPreprocessor(new StringReader(doc));
    preProcessor.setTokenizerFactory(getTokenizerFactory());

    for (List sentence: preProcessor)
      result.add(StringUtils.joinWithOriginalWhiteSpace(sentence));
    
    return result;
  }
}
