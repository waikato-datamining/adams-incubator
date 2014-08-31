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
 * DL4JTrainNetwork.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import org.deeplearning4j.datasets.DataSet;

import adams.core.QuickInfoHelper;
import adams.data.ml.AbstractNetworkBuilder;
import adams.data.ml.DBNBuilder;
import adams.flow.container.DL4JModelContainer;
import adams.flow.core.Token;

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
public class DL4JTrainNetwork
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5699857965056336060L;
  
  /** the network builder to use. */
  protected AbstractNetworkBuilder m_Builder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Trains the network obtained from the callable network builder on the incoming data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "builder", "builder",
	    new DBNBuilder());
  }

  /**
   * Sets the network builder.
   *
   * @param value	the builder
   */
  public void setBuilder(AbstractNetworkBuilder value) {
    m_Builder = value;
    reset();
  }

  /**
   * Returns the network builder.
   *
   * @return		the builder
   */
  public AbstractNetworkBuilder getBuilder() {
    return m_Builder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String builderTipText() {
    return "The builder to use for building and training the network.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "builder", m_Builder);
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{DataSet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{DL4JModelContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    DataSet 		data;
    
    result  = null;
    
    data          = (DataSet) m_InputToken.getPayload();
    m_OutputToken = new Token(new DL4JModelContainer(m_Builder.trainNetwork(data), data, m_Builder.shallowCopy(true)));
    
    return result;
  }
}
