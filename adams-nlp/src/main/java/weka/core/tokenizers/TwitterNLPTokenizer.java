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
 * TwitterNLPTokenizer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers;

import cmu.arktweetnlp.Twokenize;
import weka.core.RevisionUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Tokenizer using TweetNLP's Twokenize.
 * Taken from <a href="https://github.com/felipebravom/SentimentDomain/blob/master/src/weka/core/tokenizers/TwitterNLPTokenizer.java">here</a>
 *
 * @author Felipe Bravo
 * @version $Revision$
 */
public class TwitterNLPTokenizer extends Tokenizer {

  private static final long serialVersionUID = 4352757127093531518L;

  /** the iterator for the tokens. */
  protected transient Iterator<String> m_TokenIterator;

  /**
   * Returns a string describing the tokenizer.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  @Override
  public String globalInfo() {
    return "Tokenizer based on TwitterNLP's Twokenize.\n\n"
      + "For more information see:\n"
      + "http://www.ark.cs.cmu.edu/TweetNLP/\n\n"
      + "Original code from:\n"
      + "https://github.com/felipebravom/SentimentDomain/blob/master/src/weka/core/tokenizers/TwitterNLPTokenizer.java";
  }

  /**
   * Tests if this enumeration contains more elements.
   *
   * @return true if and only if this enumeration object contains at least one
   *         more element to provide; false otherwise.
   */
  @Override
  public boolean hasMoreElements() {
    return m_TokenIterator.hasNext();
  }

  /**
   * Returns the next element of this enumeration if this enumeration object has
   * at least one more element to provide.
   *
   * @return the next element of this enumeration.
   */
  @Override
  public String nextElement() {
    return m_TokenIterator.next();
  }

  /**
   * Sets the string to tokenize. Tokenization happens immediately.
   *
   * @param s the string to tokenize
   */
  @Override
  public void tokenize(String s) {
    List<String> words = Twokenize.tokenizeRawTweetText(s);
    m_TokenIterator = words.iterator();
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 10203 $");
  }

  /**
   * Runs the tokenizer with the given options and strings to tokenize. The
   * tokens are printed to stdout.
   *
   * @param args the commandline options and strings to tokenize
   */
  public static void main(String[] args) {
    runTokenizer(new TwitterNLPTokenizer(), args);
  }
}
