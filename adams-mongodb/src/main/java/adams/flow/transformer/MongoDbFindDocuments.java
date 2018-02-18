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
 * MongoDbFindDocuments.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
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
public class MongoDbFindDocuments
  extends AbstractArrayProvider {

  private static final long serialVersionUID = 4673338678018337975L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Locates and forwards documents matching the query.";
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Document.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the documents are output as array instead of one-by-one.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{MongoCollection.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    MongoCollection		coll;
    FindIterable<Document>	iter;

    result = null;
    coll   = m_InputToken.getPayload(MongoCollection.class);
    m_Queue.clear();
    try {
      iter = coll.find();  // TODO apply query
      for (Document doc: iter)
	m_Queue.add(doc);
    }
    catch (Exception e) {
      result = handleException("Failed to find documents!", e);
    }

    return result;
  }
}
