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

/*
 * TumblrConnection.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.base.BasePassword;
import adams.core.net.TumblrHelper;

import com.tumblr.jumblr.JumblrClient;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TumblrConnection
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the tumblr consumer key. */
  protected String m_ConsumerKey;

  /** the tumblr consumer secret. */
  protected BasePassword m_ConsumerSecret;

  /** the tumblr access token. */
  protected String m_AccessToken;

  /** the tumblr access token secret. */
  protected BasePassword m_AccessTokenSecret;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	  "Provides access to various tumblr services.\n"
	+ "For your own tumblr account, you can obtain consumer key and secret for an app:\n"
	+ "  http://www.tumblr.com/oauth/apps";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "consumer-key", "consumerKey",
	    TumblrHelper.getConsumerKey());

    m_OptionManager.add(
	    "consumer-secret", "consumerSecret",
	    TumblrHelper.getConsumerSecret());

    m_OptionManager.add(
	    "access-token", "accessToken",
	    TumblrHelper.getAccessToken());

    m_OptionManager.add(
	    "acces-token-secret", "accessTokenSecret",
	    TumblrHelper.getAccessTokenSecret());
  }

  /**
   * Sets the tumblr consumer key to use.
   *
   * @param value	the key
   */
  public void setConsumerKey(String value) {
    m_ConsumerKey = value;
    reset();
  }

  /**
   * Returns the tumblr consumer key to use.
   *
   * @return		the key
   */
  public String getConsumerKey() {
    return m_ConsumerKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String consumerKeyTipText() {
    return "The tumblr consumer key to use for connecting; leave empty for anonymous access.";
  }

  /**
   * Sets the tumblr consumer secret to use.
   *
   * @param value	the secret
   */
  public void setConsumerSecret(BasePassword value) {
    m_ConsumerSecret = value;
    reset();
  }

  /**
   * Returns the tumblr consumer secret to use.
   *
   * @return		the secret
   */
  public BasePassword getConsumerSecret() {
    return m_ConsumerSecret;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String consumerSecretTipText() {
    return "The consumer secret of the tumblr application to use for connecting.";
  }

  /**
   * Sets the tumblr acess token to use.
   *
   * @param value	the token
   */
  public void setAccessToken(String value) {
    m_AccessToken = value;
    reset();
  }

  /**
   * Returns the tumblr acess token to use.
   *
   * @return		the token
   */
  public String getAccessToken() {
    return m_AccessToken;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String accessTokenTipText() {
    return "The tumblr access token to use for connecting.";
  }

  /**
   * Sets the tumblr access token secret to use.
   *
   * @param value	the secret
   */
  public void setAccessTokenSecret(BasePassword value) {
    m_AccessTokenSecret = value;
    reset();
  }

  /**
   * Returns the tumblr access token secret to use.
   *
   * @return		the secret
   */
  public BasePassword getAccessTokenSecret() {
    return m_AccessTokenSecret;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String accessTokenSecretTipText() {
    return "The access token secret of the tumblr application to use for connecting.";
  }

  /**
   * Builds a configuration to use for the tumblr4j factories.
   *
   * @return		the configuration
   */
  public JumblrClient getClient() {
    return TumblrHelper.getClient(getConsumerKey(), getConsumerSecret().getValue(), getAccessToken(), getAccessTokenSecret().getValue());
  }

  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    return null;
  }
}
