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
 * TumblrBlogPosts.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.net.TumblrHelper;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

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
public class TumblrBlogPosts
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 3275675805290510843L;
  
  /** the name of the blog to list the posts for. */
  protected String m_Blog;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs posts from the specified tumblr blog.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "blog", "blog",
	    "");
  }

  /**
   * Sets the tumblr blog name.
   *
   * @param value	the blog name
   */
  public void setBlog(String value) {
    m_Blog = value;
    reset();
  }

  /**
   * Returns the tumblr blog name.
   *
   * @return		the blog name
   */
  public String getBlog() {
    return m_Blog;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String blogTipText() {
    return "The name of the tumblr blog to retrieve the blog posts from.";
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Post.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, outputs an array of posts rather than outputting them one-by-one";
  }
  
  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null) {
      if (m_Blog.isEmpty())
	result = "No blog name provided!";
    }
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    JumblrClient	client;
    
    result = null;

    try {
      client = TumblrHelper.getTumblrClient(this);
      m_Queue.addAll(client.blogPosts(m_Blog));
    }
    catch (Exception e) {
      result = handleException("Failed to retrieve blog posts for blog '" + m_Blog + "'!", e);
    }
    
    return result;
  }
}
