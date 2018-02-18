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
 * MongoDbDocumentUpdate.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import org.bson.Document;

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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MongoDbCollectionUpdate
  extends AbstractMongoDbTransformer {

  private static final long serialVersionUID = 296859358661875498L;

  @Override
  public String globalInfo() {
    return null;
  }

  @Override
  public Class[] accepts() {
    return new Class[]{Document.class};
  }

  @Override
  public Class[] generates() {
    return new Class[]{Document.class};
  }

  @Override
  protected String queryDatabase() {
    return null;
  }
}
