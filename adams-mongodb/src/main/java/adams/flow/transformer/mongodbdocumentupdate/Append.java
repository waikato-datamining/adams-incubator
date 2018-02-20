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

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.base.BaseKeyValuePair;
import adams.data.conversion.ConversionFromString;
import adams.data.conversion.StringToString;
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

  /** the value conversion. */
  protected ConversionFromString m_ValueConversion;

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
      "value-conversion", "valueConversion",
      new StringToString());
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
   * Sets the conversion for turning the value string into the actual type.
   *
   * @param value	the conversion
   */
  public void setValueConversion(ConversionFromString value) {
    m_ValueConversion = value;
    reset();
  }

  /**
   * Returns the conversion for turning the value string into the actual type.
   *
   * @return 		the conversion
   */
  public ConversionFromString getValueConversion() {
    return m_ValueConversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String valueConversionTipText() {
    return "For converting the value string into the actual type.";
  }

  /**
   * Updates the document.
   *
   * @param doc		the document to update
   * @return		null if successful, otherwise the error message
   */
  @Override
  protected String doUpdate(Document doc) {
    String		result;
    MessageCollection	errors;
    Object		val;
    String		msg;

    result = null;

    errors = new MessageCollection();
    try {
      for (BaseKeyValuePair pair: m_KeyValuePairs) {
        m_ValueConversion.setInput(pair.getPairValue());
        msg = m_ValueConversion.convert();
        if (msg != null) {
          errors.add("Failed to convert " + pair + " using " + m_ValueConversion + "\n" + msg);
	}
	else {
          val = m_ValueConversion.getOutput();
	  doc.append(pair.getPairKey(), val);
	}
      }
    }
    catch (Exception e) {
      errors.add(Utils.handleException(this, "Failed to update document!", e));
    }

    if (!errors.isEmpty())
      result = errors.toString();

    return result;
  }
}
