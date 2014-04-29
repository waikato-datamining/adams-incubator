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
 * TumblrPostField.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.tumblr;

/**
 * The available fields for generating the post output.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum TumblrPostField {
  ID,
  REBLOG_KEY,
  BLOG_NAME,
  POST_URL,
  TYPE,
  TIMESTAMP,
  STATE,
  FORMAT,
  DATE,
  TAGS,
  BOOKMARKLET,
  MOBILE,
  SOURCE_URL,
  //SOURCE_TITLE,
  LIKED,
  //SLUG,
  REBLOGGED_FROM_ID,
  REBLOGGED_FROM_NAME
}