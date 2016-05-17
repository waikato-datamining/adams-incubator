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
 * Groovy.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j;

import adams.core.scripting.GroovyScript;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.optimize.api.ConvexOptimizer;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Groovy
  extends AbstractScriptedModel {

  private static final long serialVersionUID = 7199224184165322501L;

  /** the loaded script object. */
  protected transient Model m_ModelObject;

  /** the inline script. */
  protected GroovyScript m_InlineScript;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A filter that uses a Groovy script for processing the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "inline-script", "inlineScript",
	    getDefaultInlineScript());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String scriptOptionsTipText() {
    return
        "The options for the Groovy script; must consist of 'key=value' pairs "
      + "separated by blanks; the value of 'key' can be accessed via the "
      + "'getAdditionalOptions().getXYZ(\"key\")' method in the Groovy actor.";
  }

  /**
   * Returns the default inline script.
   *
   * @return		the default script
   */
  protected GroovyScript getDefaultInlineScript() {
    return new GroovyScript();
  }

  /**
   * Sets the inline script to use instead of the external script file.
   *
   * @param value 	the inline script
   */
  public void setInlineScript(GroovyScript value) {
    m_InlineScript = value;
    reset();
  }

  /**
   * Gets the inline script to use instead of the external script file.
   *
   * @return 		the inline script
   */
  public GroovyScript getInlineScript() {
    return m_InlineScript;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String inlineScriptTipText() {
    return "The inline script, if not using an external script file.";
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String loadScriptObject() {
    Object[]	result;

    result = adams.core.scripting.Groovy.getSingleton().loadScriptObject(
	Model.class,
	m_ScriptFile,
	m_InlineScript,
	m_ScriptOptions,
	getOptionManager().getVariables());
    m_ScriptObject = result[1];

    return (String) result[0];
  }

  /**
   * Checks the script object.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String checkScriptObject() {
    // TODO checks?
    return null;
  }

  /**
   * Hook method for checks before the actual execution.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null)
      m_ModelObject = (Model) m_ScriptObject;

    return result;
  }

  /**
   * Perform one update  applying the gradient
   * @param gradient the gradient to apply
   */
  @Override
  public void update(INDArray gradient, String paramType) {
    if (m_ModelObject != null)
      m_ModelObject.update(gradient, paramType);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * The score for the model
   * @return the score for the model
   */
  @Override
  public double score() {
    if (m_ModelObject != null)
      return m_ModelObject.score();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Update the score
   */
  @Override
  public void computeGradientAndScore() {
    if (m_ModelObject != null)
      m_ModelObject.computeGradientAndScore();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Sets a rolling tally for the score. This is useful for mini batch learning when
   * you are accumulating error across a dataset.
   * @param accum the amount to accum
   */
  @Override
  public void accumulateScore(double accum) {
    if (m_ModelObject != null)
      m_ModelObject.accumulateScore(accum);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Parameters of the model (if any)
   * @return the parameters of the model
   */
  @Override
  public INDArray params() {
    if (m_ModelObject != null)
      return m_ModelObject.params();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * the number of parameters for the model
   * @return the number of parameters for the model
   *
   */
  @Override
  public int numParams() {
    if (m_ModelObject != null)
      return m_ModelObject.numParams();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * the number of parameters for the model
   * @return the number of parameters for the model
   *
   */
  @Override
  public int numParams(boolean backwards) {
    if (m_ModelObject != null)
      return m_ModelObject.numParams(backwards);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Set the parameters for this model.
   * This expects a linear ndarray which then be unpacked internally
   * relative to the expected ordering of the model
   * @param params the parameters for the model
   */
  @Override
  public void setParams(INDArray params) {
    if (m_ModelObject != null)
      m_ModelObject.setParams(params);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Update learningRate using for this model.
   * Use the learningRateScoreBasedDecay to adapt the score
   * if the Eps termination condition is met
   */
  @Override
  public void applyLearningRateScoreDecay() {
    if (m_ModelObject != null)
      m_ModelObject.applyLearningRateScoreDecay();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Fits the model.
   */
  @Override
  public void fit() {
    String	msg;

    msg = check();
    if (msg != null)
      throw new IllegalStateException(msg);
    m_ModelObject.fit();
  }

  /**
   * Fit the model to the given data
   * @param data the data to fit the model to
   */
  @Override
  public void fit(INDArray data) {
    if (m_ModelObject != null)
      m_ModelObject.fit(data);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Run one iteration
   * @param input the input to iterate on
   */
  @Override
  public void iterate(INDArray input) {
    if (m_ModelObject != null)
      m_ModelObject.iterate(input);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Calculate a gradient
   * @return the gradient for this model
   */
  @Override
  public Gradient gradient() {
    if (m_ModelObject != null)
      return m_ModelObject.gradient();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Get the gradient and score
   * @return the gradient and score
   */
  @Override
  public Pair<Gradient, Double> gradientAndScore() {
    if (m_ModelObject != null)
      return m_ModelObject.gradientAndScore();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * The current inputs batch size
   * @return the current inputs batch size
   */
  @Override
  public int batchSize() {
    if (m_ModelObject != null)
      return m_ModelObject.batchSize();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * The configuration for the neural network
   * @return the configuration for the neural network
   */
  @Override
  public NeuralNetConfiguration conf() {
    if (m_ModelObject != null)
      return m_ModelObject.conf();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Setter for the configuration
   * @param conf
   */
  @Override
  public void setConf(NeuralNetConfiguration conf) {
    if (m_ModelObject != null)
      m_ModelObject.setConf(conf);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * The input/feature matrix for the model
   * @return the input/feature matrix for the model
   */
  @Override
  public INDArray input() {
    if (m_ModelObject != null)
      return m_ModelObject.input();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Validate the input
   */
  @Override
  public void validateInput() {
    if (m_ModelObject != null)
      m_ModelObject.validateInput();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Returns this models optimizer
   * @return this models optimizer
   */
  @Override
  public ConvexOptimizer getOptimizer() {
    if (m_ModelObject != null)
      return m_ModelObject.getOptimizer();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Get the parameter
   * @param param the key of the parameter
   * @return the parameter vector/matrix with that particular key
   */
  @Override
  public INDArray getParam(String param) {
    if (m_ModelObject != null)
      return m_ModelObject.getParam(param);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Initialize the parameters
   */
  @Override
  public void initParams() {
    if (m_ModelObject != null)
      m_ModelObject.initParams();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * The param table
   * @return
   */
  @Override
  public Map<String, INDArray> paramTable() {
    if (m_ModelObject != null)
      return m_ModelObject.paramTable();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Setter for the param table
   * @param paramTable
   */
  @Override
  public void setParamTable(Map<String, INDArray> paramTable) {
    if (m_ModelObject != null)
      m_ModelObject.setParamTable(paramTable);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Set the parameter with a new ndarray
   * @param key the key to se t
   * @param val the new ndarray
   */
  @Override
  public void setParam(String key, INDArray val) {
    if (m_ModelObject != null)
      m_ModelObject.setParam(key, val);
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Clear input
   */
  @Override
  public void clear() {
    if (m_ModelObject != null)
      m_ModelObject.clear();
    else
      throw new IllegalStateException("No model script loaded!");
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    super.destroy();

    m_ModelObject = null;
  }
}
