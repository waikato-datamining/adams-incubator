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
 * Tumblr.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net;

import adams.core.Properties;
import adams.core.base.BasePassword;
import adams.env.Environment;
import adams.env.TumblrDefinition;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.TumblrConnection;
import com.tumblr.jumblr.JumblrClient;

/**
 * A helper class for the twitter setup.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TumblrHelper {

  /** the name of the props file. */
  public final static String FILENAME = "Tumblr.props";

  /** the consumer key. */
  public final static String CONSUMER_KEY = "ConsumerKey";

  /** the consumer secret. */
  public final static String CONSUMER_SECRET = "ConsumerSecret";

  /** the access token. */
  public final static String ACCESS_TOKEN = "AccessToken";

  /** the access token secret. */
  public final static String ACCESS_TOKEN_SECRET = "AccessTokenSecret";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns the underlying properties.
   *
   * @return		the properties
   */
  public synchronized static Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Environment.getInstance().read(TumblrDefinition.KEY);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }

  /**
   * Writes the specified properties to disk.
   *
   * @param props	the properties to write to disk
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties() {
    return writeProperties(getProperties());
  }

  /**
   * Writes the specified properties to disk.
   *
   * @param props	the properties to write to disk
   * @return		true if successfully stored
   */
  public synchronized static boolean writeProperties(Properties props) {
    boolean	result;

    result = Environment.getInstance().write(TumblrDefinition.KEY, props);
    // require reload
    m_Properties = null;

    return result;
  }

  /**
   * Returns the consumer key.
   *
   * @return		the key
   */
  public static String getConsumerKey() {
    return getProperties().getPath(CONSUMER_KEY, "");
  }

  /**
   * Returns the consumer secret.
   *
   * @return		the secret
   */
  public static BasePassword getConsumerSecret() {
    return new BasePassword(getProperties().getPath(CONSUMER_SECRET, ""));
  }

  /**
   * Returns the access token.
   *
   * @return		the token
   */
  public static String getAccessToken() {
    return getProperties().getPath(ACCESS_TOKEN, "");
  }

  /**
   * Returns the access token secret.
   *
   * @return		the secret
   */
  public static BasePassword getAccessTokenSecret() {
    return new BasePassword(getProperties().getPath(ACCESS_TOKEN_SECRET, ""));
  }

  /**
   * Returns the closest TumblrConnection actor, if available.
   *
   * @param actor	the actor start the search from (towards the root)
   * @return		the TumblrConnection actor, or null if none found
   */
  protected static TumblrConnection getTumblrConnectionActor(Actor actor) {
    return (TumblrConnection) ActorUtils.findClosestType(actor, TumblrConnection.class, true);
  }

  /**
   * Builds a configuration to use for the tumblr4j factories.
   *
   * @return		the configuration
   */
  public static JumblrClient getClient(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
    if (consumerKey.isEmpty() || consumerSecret.isEmpty())
      return new JumblrClient();
    
    if (accessToken.isEmpty() || accessTokenSecret.isEmpty())
      return new JumblrClient(consumerKey, consumerSecret);
      
    return new JumblrClient(consumerKey, consumerSecret, accessToken, accessTokenSecret);
  }

  /**
   * Returns the twitter connection object.
   *
   * @param actor	the actor to start the search from
   * @return		the connection, default client if no TumblrConnection actor found
   */
  public static JumblrClient getTumblrClient(Actor actor) {
    JumblrClient	result;
    TumblrConnection	conn;

    result = null;
    
    conn = getTumblrConnectionActor(actor);
    if (conn != null)
      result = conn.getClient();
    else
      result = getClient(getConsumerKey(), getConsumerSecret().getValue(), getAccessToken(), getAccessTokenSecret().getValue());

    return result;
  }
}
