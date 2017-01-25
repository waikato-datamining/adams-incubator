package adams.ml.jsat;/*
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
 * GPD.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

import adams.env.Environment;
import jsat.ARFFLoader;
import jsat.SimpleDataSet;
import jsat.regression.OrdinaryKriging;
import jsat.regression.RegressionDataSet;
import jsat.regression.RegressionModelEvaluation;
import jsat.regression.evaluation.CoefficientOfDetermination;
import jsat.regression.evaluation.MeanSquaredError;

import java.io.File;
import java.util.Random;

/**
 * TODO: What class does.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPD {

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    OrdinaryKriging reg = new OrdinaryKriging();
    reg.setMeasurementError(0.01);
    //SimpleDataSet data = ARFFLoader.loadArffFile(new File("/home/fracpete/temp/A_C_OF_no_sampid.arff"));
    SimpleDataSet data = ARFFLoader.loadArffFile(new File("/home/fracpete/temp/A_C_OF_no_sampid.arff"));
    RegressionDataSet rdata = new RegressionDataSet(data.getDataPoints(), data.getNumFeatures() - 1);
    RegressionModelEvaluation eval = new RegressionModelEvaluation(reg, rdata);
    MeanSquaredError rmse = new MeanSquaredError();
    rmse.setRMSE(true);
    eval.addScorer(rmse);
    CoefficientOfDetermination cc = new CoefficientOfDetermination();
    eval.addScorer(cc);
    eval.evaluateCrossValidation(10, new Random(1));
    System.out.println("rmse=" + eval.getScoreStats(rmse).getMean());
    System.out.println("cc=" + eval.getScoreStats(cc).getMean());
  }
}
