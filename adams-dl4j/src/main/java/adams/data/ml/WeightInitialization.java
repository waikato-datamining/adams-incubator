/*
 * WeightInitialization.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.ml;

import org.deeplearning4j.nn.WeightInit;

/**
 * Wrapper for the {@link WeightInit} enum.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum WeightInitialization {

  /** none. */
  NONE(null),
  /** variance normalized. */
  VARIANCE_NORMALIZED(WeightInit.VI),
  /** sparse. */
  SPARSE(WeightInit.SI);

  /** the corresponding WeightInit value. */
  private WeightInit m_WeightInit;

  /**
   * Initializes the enum.
   *
   * @param wi		the associated {@link WeightInit} value
   */
  private WeightInitialization(WeightInit wi) {
    m_WeightInit = wi;
  }

  /**
   * Returns the associated {@link WeightInit} value.
   *
   * @return		the initialization
   */
  public WeightInit getWeightInit() {
    return m_WeightInit;
  }
}
