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
 * Append.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbdocumentupdate;

import adams.core.Utils;
import adams.core.Variables;
import adams.core.base.BaseKeyValuePair;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import org.bson.Document;

/**
 * Appends the document with the specified key-value pairs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Append
  extends AbstractMongoDbDocumentUpdate {

  private static final long serialVersionUID = 3771202579365692102L;

  /** the key-value pairs to add. */
  protected BaseKeyValuePair[] m_KeyValuePairs;

  /** the key-value pairs to add, with the value being a variable name. */
  protected BaseKeyValuePair[] m_KeyValuePairsVariables;

  /** the key-value pairs to add, with the value being a storage name. */
  protected BaseKeyValuePair[] m_KeyValuePairsStorage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Appends the document with the specified key-value pairs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key-value", "keyValuePairs",
      new BaseKeyValuePair[0]);

    m_OptionManager.add(
      "key-value-storage", "keyValuePairsStorage",
      new BaseKeyValuePair[0]);

    m_OptionManager.add(
      "key-value-var", "keyValuePairsVariables",
      new BaseKeyValuePair[0]);
  }

  /**
   * Sets the key-value pairs to add.
   *
   * @param value	the pairs
   */
  public void setKeyValuePairs(BaseKeyValuePair[] value) {
    m_KeyValuePairs = value;
    reset();
  }

  /**
   * Returns the key-value pairs to add.
   *
   * @return 		the pairs
   */
  public BaseKeyValuePair[] getKeyValuePairs() {
    return m_KeyValuePairs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String keyValuePairsTipText() {
    return "The key-value pairs to add.";
  }

  /**
   * Sets the key-value pairs to add, with the value representing a storage name.
   *
   * @param value	the pairs
   */
  public void setKeyValuePairsStorage(BaseKeyValuePair[] value) {
    m_KeyValuePairs = value;
    reset();
  }

  /**
   * Returns the key-value pairs to add, with the value representing a storage name.
   *
   * @return 		the pairs
   */
  public BaseKeyValuePair[] getKeyValuePairsStorage() {
    return m_KeyValuePairsStorage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String keyValuePairsStorageTipText() {
    return "The key-value pairs to add, with the value representing a storage name.";
  }

  /**
   * Sets the key-value pairs to add, with the value representing a variable
   * name (without surrounding @{...}).
   *
   * @param value	the pairs
   */
  public void setKeyValuePairsVariables(BaseKeyValuePair[] value) {
    m_KeyValuePairsVariables = value;
    reset();
  }

  /**
   * Returns the key-value pairs to add, with the value representing a variable
   * name (without surrounding @{...}).
   *
   * @return 		the pairs
   */
  public BaseKeyValuePair[] getKeyValuePairsVariables() {
    return m_KeyValuePairsVariables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String keyValuePairsVariablesTipText() {
    return "The key-value pairs to add, with the value representing a variable name (without surrounding @{...}).";
  }

  /**
   * Updates the document.
   *
   * @param doc		the document to update
   * @return		null if successful, otherwise the error message
   */
  @Override
  protected String doUpdate(Document doc) {
    String	result;
    Storage 	storage;
    Variables 	variables;

    result = null;

    storage   = getFlowContext().getStorageHandler().getStorage();
    variables = getFlowContext().getVariables();

    try {
      // regular
      for (BaseKeyValuePair pair: m_KeyValuePairs)
        doc.append(pair.getPairKey(), pair.getPairValue());

      // storage
      for (BaseKeyValuePair pair: m_KeyValuePairs)
        doc.append(pair.getPairKey(), storage.get(new StorageName(pair.getPairValue())));

      // variables
      for (BaseKeyValuePair pair: m_KeyValuePairs)
        doc.append(pair.getPairKey(), variables.get(pair.getPairValue()));
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to add document!", e);
    }

    return result;
  }
}
