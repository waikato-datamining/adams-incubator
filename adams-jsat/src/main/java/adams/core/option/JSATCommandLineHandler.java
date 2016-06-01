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
 * JSATCommandLineHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.ClassLocator;
import adams.core.Utils;
import adams.env.Environment;
import jsat.classifiers.boosting.AdaBoostM1;
import jsat.classifiers.svm.PlattSMO;
import jsat.classifiers.svm.SupportVectorLearner.CacheMode;
import jsat.classifiers.trees.DecisionStump;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.parameters.BooleanParameter;
import jsat.parameters.DoubleParameter;
import jsat.parameters.IntParameter;
import jsat.parameters.MetricParameter;
import jsat.parameters.ObjectParameter;
import jsat.parameters.Parameter;
import jsat.parameters.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Handles objects of classes with a package that starts with "jsat.".
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JSATCommandLineHandler
  extends AbstractCommandLineHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5233496867185402778L;

  /**
   * Generates an object from the specified commandline.
   *
   * @param cmd		the commandline to create the object from
   * @return		the created object, null in case of error
   */
  @Override
  public Object fromCommandLine(String cmd) {
    Object	result;

    try {
      result = fromArray(OptionUtils.splitOptions(cmd));
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to process commandline '" + cmd + "':", e);
      result = null;
    }

    return result;
  }

  /**
   * Generates an object from the commandline options.
   *
   * @param args	the commandline options to create the object from
   * @return		the created object, null in case of error
   */
  @Override
  public Object fromArray(String[] args) {
    Object	result;

    result = null;
    args   = args.clone();

    if (args.length > 0) {
      try {
	result = Class.forName(args[0]).newInstance();
	args[0]   = "";
	setOptions(result, args);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate object from array (fromArray):", e);
	result = null;
      }
    }

    return result;
  }

  /**
   * Generates a commandline from the specified object.
   *
   * @param obj		the object to create the commandline for
   * @return		the generated commandline
   */
  @Override
  public String toCommandLine(Object obj) {
    StringBuilder	result;

    result = new StringBuilder();
    result.append(obj.getClass().getName());
    if (obj instanceof Parameterized) {
      result.append(" ");
      result.append(OptionUtils.joinOptions(getOptions(obj)));
    }

    return result.toString().trim();
  }

  /**
   * Generates a commandline from the specified object. Uses a shortened
   * format, e.g., removing the package from the class.
   *
   * @param obj		the object to create the commandline for
   * @return		the generated commandline
   */
  @Override
  public String toShortCommandLine(Object obj) {
    StringBuilder	result;

    result = new StringBuilder();
    result.append(obj.getClass().getSimpleName());
    if (obj instanceof Parameterized) {
      result.append(" ");
      result.append(OptionUtils.joinOptions(getOptions(obj)));
    }

    return result.toString().trim();
  }

  /**
   * Generates an options array from the specified object.
   *
   * @param obj		the object to create the array for
   * @return		the generated array
   */
  @Override
  public String[] toArray(Object obj) {
    List<String>	result;

    result = new ArrayList<>();
    result.add(obj.getClass().getName());
    result.addAll(Arrays.asList(getOptions(obj)));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the commandline options (without classname) of the specified object.
   *
   * @param obj		the object to get the options from
   * @return		the options
   */
  @Override
  public String[] getOptions(Object obj) {
    List<String>	result;
    List<Parameter>	params;

    result = new ArrayList<>();

    if (obj instanceof Parameterized) {
      params = ((Parameterized) obj).getParameters();
      for (Parameter param: params) {
	result.add(param.getASCIIName());
	if (param instanceof ObjectParameter) {
	  if (((ObjectParameter) param).getObject() instanceof Parameterized)
	    result.add(OptionUtils.joinOptions(toArray(((ObjectParameter) param).getObject())));
	  else
	    result.add(param.getValueString());
	}
	else {
	  result.add(param.getValueString());
	}
      }
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the options of the specified object.
   *
   * @param obj		the object to set the options for
   * @param args	the options
   * @return		true if options successfully set
   */
  @Override
  public boolean setOptions(Object obj, String[] args) {
    boolean		result;
    Parameterized	pobj;
    List<Parameter>	params;
    int			i;
    List<String>	list;
    Class		cl;
    ObjectParameter	oparam;
    Object		o;

    try {
      if (obj instanceof Parameterized) {
	list   = new ArrayList<>(Arrays.asList(args));
	Utils.removeEmptyLines(list);
	args   = list.toArray(new String[list.size()]);
	pobj   = (Parameterized) obj;
	params = pobj.getParameters();
	for (i = 0; i < args.length; i += 2) {
	  for (Parameter param : params) {
	    if (param.getASCIIName().equals(args[i])) {
	      if (i + 1 < args.length) {
		try {
		  if (param instanceof IntParameter) {
		    ((IntParameter) param).setValue(Integer.parseInt(args[i + 1]));
		  }
		  else if (param instanceof DoubleParameter) {
		    ((DoubleParameter) param).setValue(Double.parseDouble(args[i + 1]));
		  }
		  else if (param instanceof BooleanParameter) {
		    ((BooleanParameter) param).setValue(Boolean.parseBoolean(args[i + 1]));
		  }
		  else if (param instanceof MetricParameter) {
		    ((MetricParameter) param).setMetric((DistanceMetric) fromCommandLine(args[i + 1]));
		  }
		  else if (param instanceof ObjectParameter) {
		    oparam = (ObjectParameter) param;
		    o      = oparam.getObject();
		    if (ClassLocator.isSubclass(Enum.class, o.getClass())) {
		      cl = oparam.getObject().getClass().asSubclass(Enum.class);
		      oparam.setObject(Enum.valueOf(cl, args[i + 1]));
		    }
		    else if (o instanceof Parameterized) {
		      oparam.setObject(fromCommandLine(args[i + 1]));
		    }
		    else {
		      // TODO
		      getLogger().severe("Not yet supported: " + param.getClass().getName());
		    }
		  }
		  else {
		    getLogger().severe("Unhandled parameter type: " + param.getClass().getName());
		  }
		}
		catch (Exception e) {
		  getLogger().log(Level.SEVERE, "Failed to set value for parameter '" + param.getASCIIName() + "': " + args[i+1], e);
		}
		args[i + 1] = "";
	      }
	      args[i] = "";
	    }
	  }
	}
      }
      result = true;
    }
    catch (Exception e) {
      result = false;
    }

    return result;
  }

  /**
   * Splits the commandline into an array.
   *
   * @param cmdline	the commandline to split
   * @return		the generated array of options
   */
  @Override
  public String[] splitOptions(String cmdline) {
    String[]	result;

    try {
      result = OptionUtils.splitOptions(cmdline);
    }
    catch (Exception e) {
      result = new String[0];
    }

    return result;
  }

  /**
   * Turns the option array back into a commandline.
   *
   * @param args	the options to turn into a commandline
   * @return		the generated commandline
   */
  @Override
  public String joinOptions(String[] args) {
    return OptionUtils.joinOptions(args);
  }

  /**
   * Checks whether the given class can be processed.
   *
   * @param cls		the class to inspect
   * @return		true if the handler can process the class
   */
  @Override
  public boolean handles(Class cls) {
    return (cls.getName().startsWith("jsat."));
  }

  /**
   * Only for testing.
   *
   * @param args		ignored
   * @throws Exception		if testing fails
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    JSATCommandLineHandler cmdline = new JSATCommandLineHandler();

    PlattSMO smo = new PlattSMO();
    smo.setC(2.0);
    smo.setTolerance(0.02);
    smo.setMaxIterations(1000);
    smo.setCacheMode(CacheMode.FULL);
    System.out.println(OptionUtils.getCommandLine(smo));

    smo = (PlattSMO) cmdline.fromCommandLine("jsat.classifiers.svm.PlattSMO \"Modification One\" true C 2.0 Epsilon 0.01 Tolerance 0.02 \"Cache Value\" 500 \"Cache Mode\" FULL \"Max Iterations\" 1000 LinearKernel_C 1.0");
    System.out.println(OptionUtils.getCommandLine(smo));

    AdaBoostM1 ada = new AdaBoostM1(new DecisionStump(), 100);
    System.out.println(OptionUtils.getCommandLine(ada));

    // TODO from commandline??
  }
}
