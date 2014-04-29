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
 * AbstractTumblrPostConverter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.tumblr;

import java.util.Hashtable;

import adams.core.DateTime;
import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;

import com.tumblr.jumblr.types.Post;

/**
 * Ancestor for classes that convert tweets into a different data structure.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of output data to generate
 */
public abstract class AbstractTumblrPostConverter<T>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5446751589621732002L;

  /** the fields to generate the output from. */
  protected TumblrPostField[] m_Fields;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "fields",
	    new TumblrPostField[]{TumblrPostField.POST_URL});
  }

  /**
   * Sets fields to generate the output from.
   *
   * @param value	the fields
   */
  public void setFields(TumblrPostField[] value) {
    m_Fields = value;
    reset();
  }

  /**
   * Returns the fields to generate the output from.
   *
   * @return		the fields
   */
  public TumblrPostField[] getFields() {
    return m_Fields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldsTipText() {
    return "The fields to use for generating the output.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "fields", Utils.flatten(m_Fields, ", "), "fields: ");
  }

  /**
   * Returns the class of the output data that is generated.
   * 
   * @return		the data type
   */
  public abstract Class generates();
  
  /**
   * Checks whether the post can be converted.
   * 
   * @param post	the post to check
   */
  protected void check(Post post) {
    if (post == null)
      throw new IllegalArgumentException("No post update provided!");
  }

  /**
   * Turns the post into a map of objects.
   *
   * @param post	the post to process
   * @return		the association between fields and post values
   */
  protected Hashtable<TumblrPostField,Object> postToMap(Post post) {
    Hashtable<TumblrPostField,Object>	result;

    result = new Hashtable<TumblrPostField,Object>();

    result.put(TumblrPostField.ID, post.getId());
    result.put(TumblrPostField.REBLOG_KEY, post.getReblogKey());
    result.put(TumblrPostField.BLOG_NAME, post.getBlogName());
    result.put(TumblrPostField.POST_URL, post.getPostUrl());
    result.put(TumblrPostField.TYPE, post.getType());
    result.put(TumblrPostField.TIMESTAMP, new DateTime(post.getTimestamp() * 1000));
    result.put(TumblrPostField.STATE, post.getState());
    result.put(TumblrPostField.FORMAT, post.getFormat());
    result.put(TumblrPostField.DATE, post.getDateGMT());
    result.put(TumblrPostField.TAGS, Utils.flatten(post.getTags(), ","));
    result.put(TumblrPostField.BOOKMARKLET, post.isBookmarklet() == null ? false : post.isBookmarklet());
    result.put(TumblrPostField.MOBILE, post.isMobile() == null ? false : post.isMobile());
    result.put(TumblrPostField.SOURCE_URL, post.getSourceUrl() == null ? "" : post.getSourceUrl());
    // TODO result.put(TumblrField.SOURCE_TITLE, post.);
    result.put(TumblrPostField.LIKED, post.isLiked() == null ? false : post.isLiked());
    // TODO result.put(TumblrField.SLUG, post.);
    result.put(TumblrPostField.REBLOGGED_FROM_ID, post.getRebloggedFromId() == null ? -1 : post.getRebloggedFromId());
    result.put(TumblrPostField.REBLOGGED_FROM_NAME, post.getRebloggedFromName() == null ? "" : post.getRebloggedFromName());

    return result;
  }

  /**
   * Performs the actual conversion.
   * 
   * @param fields	the post data to convert
   * @return		the generated output
   */
  protected abstract T doConvert(Hashtable<TumblrPostField,Object> fields);
  
  /**
   * Performs the conversion.
   * 
   * @param post	the post to convert
   * @return		the generated output
   */
  public T convert(Post post) {
    check(post);
    return doConvert(postToMap(post));
  }
}
