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
 *    HadoopExperiment.java
 *    Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.hadoop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.swing.DefaultListModel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instances;
import weka.core.Summarizable;
import weka.core.Utils;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.InstancesResultListener;
import weka.experiment.RegressionSplitEvaluator;


@SuppressWarnings("deprecation")
public class HadoopExperiment extends Configured implements Tool{
  

  /** The length of a result */
  protected static final int RESULT_SIZE = 30;
  
  protected static final int Regression_RESULT_SIZE = 23;

  /** The number of IR statistics */
  protected static final int NUM_IR_STATISTICS = 14;
  
  /** The number of averaged IR statistics */
  protected static final int NUM_WEIGHTED_IR_STATISTICS = 8;
  
  /** The number of unweighted averaged IR statistics */
  protected static final int NUM_UNWEIGHTED_IR_STATISTICS = 2;
  
  /** The number of folds in the cross-validation */
  protected int m_NumFolds = 10;
  
  /** Repetition number */
  protected int m_Repetition = 10;
  
  /** Attribute index of instance identifier (default -1) */
  protected int m_attID = -1;
  
  /** Class index for information retrieval statistics (default 0) */
  protected int m_IRclass = 0;
  
  /** Default ResultProducer */
  protected CrossValidationResultProducer m_RP = new CrossValidationResultProducer(); 
  protected InstancesResultListener m_ResultListener = new InstancesResultListener();

  /** Flag for prediction and target columns output.*/
  protected boolean m_predTargetColumn = false;  

  /** The classifier used for evaluation */
  protected Classifier m_Classifier;
  
  /** two different Split Evaluators */
  protected  ClassifierSplitEvaluator m_SplitEvaluator = new ClassifierSplitEvaluator();
  protected  RegressionSplitEvaluator m_SplitEvaluator2 = new RegressionSplitEvaluator();
  
  /** Store additional measurements value */
  protected ArrayList<String> measures = new ArrayList<String>();;
  protected String [] m_AdditionalMeasures = null;
  
  /** String value of input lines split size for hadoop*/
  protected String num="";
  
  protected String uniqueFile="",uniqueFolder="";
  
  /** record total input size, datasets * algorithms * repetition * folds */
  protected int inputSize=0, folderCount = 0;
  
  /** HadoopExperiment object to be used in Map/reduce classes */
  public static HadoopExperiment m_Exp = new HadoopExperiment();
  

  /**
   * Mapper class that read a whole file as input if its size is small,
   * or read N lines as an inputsplit if input file is large. 
   * All the calculations are running here and it sends result strings to
   * Reducer class.
   * Currently It only support cross-validation.
   * @author zy53
   *
   */
  public static class Map extends
	Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context)
	throws IOException, InterruptedException {

	String line = value.toString();
	int tokenIndex=0;
	String[] token = line.split(",");
	String algorithm = token[tokenIndex++];
	int repetition = Integer.parseInt(token[tokenIndex++]);
	String datasets = token[tokenIndex++];
	int fold = Integer.parseInt(token[tokenIndex++]);
	String classIndex = token[tokenIndex++];	
	String isClassification = token[tokenIndex++];
	
	m_Exp.m_RP.setResultListener(m_Exp.m_ResultListener);
	if(isClassification.equals("true")){
	  m_Exp.m_RP.setSplitEvaluator(m_Exp.m_SplitEvaluator);      
	}else{
	  m_Exp.m_RP.setSplitEvaluator(m_Exp.m_SplitEvaluator2);
	}
	
	try{
	  
	  String[] options = Utils.splitOptions(algorithm);
	  String classname = options[0];
	  options[0]       = "";
	  Classifier c     = (Classifier) Utils.forName(Classifier.class, classname, options);
	  m_Exp.m_Classifier = c;


	  if (c instanceof AdditionalMeasureProducer) {
	      @SuppressWarnings("rawtypes")
	      Enumeration enm = ((AdditionalMeasureProducer)c).enumerateMeasures();
	      m_Exp.measures.clear();
	      while (enm.hasMoreElements()){
		String input = enm.nextElement().toString();
		  m_Exp.measures.add(input);
	      }
	  }else{
	      m_Exp.measures.clear();
	  }


	  if(isClassification.equals("true")){
	      ((ClassifierSplitEvaluator)m_Exp.m_SplitEvaluator).setClassifier(c);
	  }else{
	      ((RegressionSplitEvaluator)m_Exp.m_SplitEvaluator2).setClassifier(c);
	  } 

  	File currentFile = new File(datasets);
  	ArffLoader loader = new ArffLoader();
  	loader.setFile(currentFile);
  	Instances data = new Instances(loader.getDataSet());

  	if(classIndex.equals("last") || classIndex.equals("default")){
  	  data.setClassIndex(data.numAttributes()-1);
  	}else if(classIndex.equals("first")){
  	  data.setClassIndex(0);
  	}else{
  	  int in = Integer.parseInt(classIndex);
  	  data.setClassIndex(in);
  	}

  	//shuffle and stratify
    	Instances runInstances = new Instances(data);
    	Random random = new Random(repetition);
        runInstances.randomize(random);
        if (runInstances.classAttribute().isNominal()) {
          runInstances.stratify(m_Exp.m_NumFolds);
        }

        ArrayList<Object> result = new ArrayList<Object>();
        Instances train = runInstances.trainCV(m_Exp.m_NumFolds, fold, random);
        Instances test = runInstances.testCV(m_Exp.m_NumFolds, fold);

        double [] predictions;
        ThreadMXBean thMonitor = ManagementFactory.getThreadMXBean();
        boolean canMeasureCPUTime = thMonitor.isThreadCpuTimeSupported();
  	if(canMeasureCPUTime && !thMonitor.isThreadCpuTimeEnabled())
  	      thMonitor.setThreadCpuTimeEnabled(true);
  	    long thID = Thread.currentThread().getId();
  	    long CPUStartTime=-1, trainCPUTimeElapsed=-1, testCPUTimeElapsed=-1,
  	         trainTimeStart, trainTimeElapsed, testTimeStart, testTimeElapsed;   
  	   long time4 = System.currentTimeMillis();    
  	Evaluation eval = new Evaluation(train);
  	
  	trainTimeStart = System.currentTimeMillis();
  	
  	long time11 = System.currentTimeMillis();
  	if(canMeasureCPUTime)
  	    CPUStartTime = thMonitor.getThreadUserTime(thID);
  	c.buildClassifier(train);
  	System.out.println("[algorithm:"+algorithm+"] [dataset:"+datasets+"] [fold:"+fold+"] [repetition:"+repetition+"]"); 
  	long time12=System.currentTimeMillis();
	System.out.println("Train: "+(time12-time11));
	  
	if(canMeasureCPUTime)
	    trainCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
	    trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
	   
	    testTimeStart = System.currentTimeMillis();
	if(canMeasureCPUTime) 
	    CPUStartTime = thMonitor.getThreadUserTime(thID);
	    predictions = eval.evaluateModel(c, test);
	if(canMeasureCPUTime)
	    testCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
	    testTimeElapsed = System.currentTimeMillis() - testTimeStart;
	    thMonitor = null;    


	    Object[] seKey;
	    if(isClassification.equals("true")){
	      seKey = m_Exp.m_SplitEvaluator.getKey();
	    }else{
	      seKey = m_Exp.m_SplitEvaluator2.getKey();
	    }
	    /*
	     * If Weka version changes, and if there are new fields available, 
	     * you have to add them into the result array in order to have correct output.
	     */
	    if(isClassification.equals("true")){
	      	    result.add( data.relationName());
		    result.add(new Double(repetition));
		    result.add(new Double(fold+1));
		    result.add(seKey[0]);
		    result.add(new String("'"+seKey[1]+"'"));
		    result.add(seKey[2]);
		    result.add(new Double(getTimestamp()));
		   
		    result.add(new Double(train.numInstances()));
		    result.add(new Double(eval.numInstances()));
		    result.add(new Double(eval.correct()));
		    result.add(new Double(eval.incorrect()));
		    result.add(new Double(eval.unclassified()));
		    result.add(new Double(eval.pctCorrect()));
		    result.add(new Double(eval.pctIncorrect()));
		    result.add(new Double(eval.pctUnclassified()));
		    result.add(new Double(eval.kappa()));
		    
		    result.add(new Double(eval.meanAbsoluteError()));
		    result.add(new Double(eval.rootMeanSquaredError()));
		    result.add(new Double(eval.relativeAbsoluteError()));
		    result.add(new Double(eval.rootRelativeSquaredError()));
		    
		    result.add(new Double(eval.SFPriorEntropy()));
		    result.add ( new Double(eval.SFSchemeEntropy()));
		    result.add ( new Double(eval.SFEntropyGain()));
		    result.add ( new Double(eval.SFMeanPriorEntropy()));
		    result.add ( new Double(eval.SFMeanSchemeEntropy()));
		    result.add ( new Double(eval.SFMeanEntropyGain()));
		    
		    // K&B stats
		    result.add ( new Double(eval.KBInformation()));
		    result.add ( new Double(eval.KBMeanInformation()));
		    result.add ( new Double(eval.KBRelativeInformation()));
		    
		    // IR stats
		    result.add ( new Double(eval.truePositiveRate(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.numTruePositives(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.falsePositiveRate(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.numFalsePositives(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.trueNegativeRate(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.numTrueNegatives(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.falseNegativeRate(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.numFalseNegatives(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.precision(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.recall(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.fMeasure(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.areaUnderROC(m_Exp.m_IRclass)));
		    result.add ( new Double(eval.areaUnderPRC(m_Exp.m_IRclass)));
		    // Weighted IR stats
		    result.add ( new Double(eval.weightedTruePositiveRate()));
		    result.add ( new Double(eval.weightedFalsePositiveRate()));
		    result.add ( new Double(eval.weightedTrueNegativeRate()));
		    result.add ( new Double(eval.weightedFalseNegativeRate()));
		    result.add ( new Double(eval.weightedPrecision()));
		    result.add ( new Double(eval.weightedRecall()));
		    result.add ( new Double(eval.weightedFMeasure()));
		    result.add ( new Double(eval.weightedAreaUnderROC()));
		    result.add (new Double(eval.weightedAreaUnderPRC()));
		    
		    // Unweighted IR stats
		    result.add ( new Double(eval.unweightedMacroFmeasure()));
		    result.add ( new Double(eval.unweightedMicroFmeasure()));
		    
		    // Timing stats
		    result.add ( new Double(trainTimeElapsed / 1000.0));
		    result.add ( new Double(testTimeElapsed / 1000.0));
		    if(canMeasureCPUTime) {
		      result.add(new Double((trainCPUTimeElapsed/1000000.0) / 1000.0));
		      result.add(new Double((testCPUTimeElapsed /1000000.0) / 1000.0));
		    }
		    else {
		      result.add(new Double(Utils.missingValue()));
		      result.add(new Double(Utils.missingValue()));
		    }

		    // sizes
		    ByteArrayOutputStream bastream = new ByteArrayOutputStream();
		    ObjectOutputStream oostream = new ObjectOutputStream(bastream);
		    oostream.writeObject(c);
		    result.add(new Double(bastream.size()));
		    bastream = new ByteArrayOutputStream();
		    oostream = new ObjectOutputStream(bastream);
		    oostream.writeObject(train);
		    result.add(new Double(bastream.size()));
		    bastream = new ByteArrayOutputStream();
		    oostream = new ObjectOutputStream(bastream);
		    oostream.writeObject(test);
		    result.add(new Double(bastream.size()));
		    
		    // Prediction interval statistics
		    result.add(new Double(eval.coverageOfTestCasesByPredictedRegions()));
		    result.add(new Double(eval.sizeOfPredictedRegions()));

		    // IDs
		    if (m_Exp.m_attID >= 0){
		      StringBuilder idsString = new StringBuilder("");
		      if (test.attribute(m_Exp.m_attID).isNumeric()){
		        if (test.numInstances() > 0)
		          idsString.append(test.instance(0).value(m_Exp.m_attID));
		        for(int i=1;i<test.numInstances();i++){
		          idsString.append("|" + test.instance(i).value(m_Exp.m_attID));
		        }
		      } else {
		        if (test.numInstances() > 0)
		          idsString.append(test.instance(0).stringValue(m_Exp.m_attID));
		        for(int i=1;i<test.numInstances();i++){
		          idsString.append("|" + test.instance(i).stringValue(m_Exp.m_attID));
		        }
		      }
		      result.add(idsString.toString());
		    }
		    
		    if (m_Exp.m_predTargetColumn){
		      StringBuilder predictionsString = new StringBuilder("");
		      StringBuilder targetsString = new StringBuilder("");
		      if (test.classAttribute().isNumeric()){
		        // Targets
		        if (test.numInstances() > 0){

		          targetsString.append(test.instance(0).value(test.classIndex()));
		          for(int i=1;i<test.numInstances();i++){
		            targetsString.append("|" + test.instance(i).value(test.classIndex()));
		          }
		          result.add(targetsString.toString());
		        }
		        
		        // Predictions
		        if (predictions.length > 0){

		          predictionsString.append(predictions[0]);
		          for(int i=1;i<predictions.length;i++){
		            predictionsString.append("|" + predictions[i]);
		          }
		          result.add(predictionsString.toString());
		        }
		        
	      } else {
	        // Targets
	        if (test.numInstances() > 0){
	          
	          targetsString.append(test.instance(0).stringValue(test.classIndex()));
	          for(int i=1;i<test.numInstances();i++){
	            targetsString.append("|" + test.instance(i).stringValue(test.classIndex()));
	          }
	          result.add(targetsString.toString());
	        }
	        
	        // Predictions
	        if (predictions.length > 0){
	         
	          predictionsString.append(test.classAttribute().value((int) predictions[0]));
	          for(int i=1;i<predictions.length;i++){
	            predictionsString.append("|" + test.classAttribute().value((int) predictions[i]));
	          }
	          result.add( predictionsString.toString());
	          
	        }
	      }
	    }
	    //summary
	    if (c instanceof Summarizable) {
	      String out = ((Summarizable)c).toSummaryString();
	      result.add (new String("'"+Utils.backQuoteChars(out)+"'"));
	    } else {
	      result.add (new String("?"));
	    }
	    
	    //additional measurement
	    for (int i=0;i<m_Exp.measures.size();i++) {
	        try {
	          double dv = ((AdditionalMeasureProducer)c).getMeasure(m_Exp.measures.get(i));
	          if (!Utils.isMissingValue(dv)) {
	            Double v = new Double(dv);
	            result.add ( v);
	          } else {
	            result.add( null);
	          }
	        } catch (Exception ex) {
	          System.err.println(ex);
	        }

	    }

	}else{
	    result.add ( new String(data.relationName()));
	    result.add ( new Double(repetition));
	    result.add ( new Double(fold+1));
	    result.add ( seKey[0]);
	    result.add ( "'"+seKey[1]+"'");
	    result.add ( seKey[2]);
	    result.add ( new Double(getTimestamp()));

	   
	    result.add ( new Double(train.numInstances()));
	    result.add ( new Double(eval.numInstances()));

	    result.add ( new Double(eval.meanAbsoluteError()));
	    result.add ( new Double(eval.rootMeanSquaredError()));
	    result.add ( new Double(eval.relativeAbsoluteError()));
	    result.add ( new Double(eval.rootRelativeSquaredError()));
	    result.add ( new Double(eval.correlationCoefficient()));

	    result.add ( new Double(eval.SFPriorEntropy()));
	    result.add ( new Double(eval.SFSchemeEntropy()));
	    result.add ( new Double(eval.SFEntropyGain()));
	    result.add ( new Double(eval.SFMeanPriorEntropy()));
	    result.add ( new Double(eval.SFMeanSchemeEntropy()));
	    result.add ( new Double(eval.SFMeanEntropyGain()));
	    
	    // Timing stats
	    result.add ( new Double(trainTimeElapsed / 1000.0));
	    result.add ( new Double(testTimeElapsed / 1000.0));
	    if(canMeasureCPUTime) {
	      result.add ( new Double((trainCPUTimeElapsed/1000000.0) / 1000.0));
	      result.add ( new Double((testCPUTimeElapsed /1000000.0) / 1000.0));
	    }
	    else {
	      result.add ( new Double(Utils.missingValue()));
	      result.add ( new Double(Utils.missingValue()));
	    }

	    // sizes
	    ByteArrayOutputStream bastream = new ByteArrayOutputStream();
	    ObjectOutputStream oostream = new ObjectOutputStream(bastream);
	    oostream.writeObject(c);
	    result.add (new Double(bastream.size()));
	    bastream = new ByteArrayOutputStream();
	    oostream = new ObjectOutputStream(bastream);
	    oostream.writeObject(train);
	    result.add ( new Double(bastream.size()));
	    bastream = new ByteArrayOutputStream();
	    oostream = new ObjectOutputStream(bastream);
	    oostream.writeObject(test);
	    result.add ( new Double(bastream.size()));
	    
	    // Prediction interval statistics
	    result.add ( new Double(eval.coverageOfTestCasesByPredictedRegions()));
	    result.add ( new Double(eval.sizeOfPredictedRegions()));

	    
	    //summary
	    if (c instanceof Summarizable) {
	      String out = ((Summarizable)c).toSummaryString();
	      result.add ( new String("'"+Utils.backQuoteChars(out)+"'"));
	    } else {
	      result.add (new String( "?"));
	    }
	    
	    //additional measurement
	    for (int i=0;i<m_Exp.measures.size();i++) {
	        try {
	          double dv = ((AdditionalMeasureProducer)c).getMeasure(m_Exp.measures.get(i));
	          if (!Utils.isMissingValue(dv)) {
	            Double v = new Double(dv);
	            result.add(v);
	          } else {
	            result.add(null);
	          }
	        } catch (Exception ex) {
	          System.err.println(ex);
	        }
	    }
	}
  
	StringBuilder output = new StringBuilder();
	for(int i=0;i<result.size();i++){
	  if(i>0){
	    output.append(",");
	  }
	  if (result.get(i)!=null)
	    output.append(result.get(i).toString()); 
	}

	Text word = new Text();
	word.set(output.toString());

	
	StringBuilder values=new StringBuilder("");
	if(!m_Exp.measures.isEmpty()){
    	for(String s:m_Exp.measures){
    	  values.append(","+s);
    	}
	}else{
	  values.append("");
	}
	
  	StringBuilder re=new StringBuilder();
        String [] keyNames = m_Exp.m_RP.getKeyNames();
        String [] resultNames = m_Exp.m_RP.getResultNames();
              
        int [] m_AttributeTypes = new int [keyNames.length + resultNames.length];
            
        for (int i = 0; i < m_AttributeTypes.length; i++) {
            String attribName = "Unknown";
            if (i < keyNames.length) {
        	attribName = "Key_" + keyNames[i];
            } else{
        	attribName = resultNames[i - keyNames.length];
            }
            re.append(attribName);
  
            if(i<m_AttributeTypes.length-1){
        	re.append(",");
            } 
        }
        
        re.append(values.toString());
        Text mapValue = new Text();
  	mapValue.set(re.toString());
  	
	    
	context.write(new Text("1"),new Text("2"));

	long time6 = System.currentTimeMillis();

	System.out.println("Whole Process:"+(time6-time4));

	}catch(Exception e){
	  e.printStackTrace();
	}

    }
  }

  /** Collect text data from Mapper and write to output file. */
  public static class Reduce extends
	Reducer<Text, Text, Text, NullWritable> {

    @Override
    public void reduce(Text key, Iterable<Text> values,
		Context context) throws IOException, InterruptedException {
        try{
          Text word = null;
              
          for(Text s:values){
            word = s;
          }
          context.write(word, null);
          context.write(key,null); 
          
        }catch(Exception e){
  	  e.printStackTrace();
        }
    } 
  }

  /**
   * Get current time info.
   * @return time information in double
   */
  public static Double getTimestamp() {
    Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    double timestamp = now.get(Calendar.YEAR) * 10000
      + (now.get(Calendar.MONTH) + 1) * 100
      + now.get(Calendar.DAY_OF_MONTH)
      + now.get(Calendar.HOUR_OF_DAY) / 100.0
      + now.get(Calendar.MINUTE) / 10000.0;
    return new Double(timestamp);
  }
  
  /**
   * Setting up hadoop job and run
   * @param args type of String[]
   * @return 0 if job runs successfully
   */
  public int run(String[] args) throws Exception {

    	Job job = new Job(getConf());
    	FileSystem fs = FileSystem.get(getConf());
    	
    	int argIndex = 0;
        while (args[argIndex].startsWith("-"))
          argIndex += 2;
        argIndex++;
        
    	if(fs.isDirectory(new Path(args[argIndex]))){
    	  fs.delete(new Path(args[argIndex]), true);
    	}
    	
	job.setJarByClass(HadoopExperiment.class);
	job.setJobName("hadoopExperiment");
	
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(Text.class);


	job.setMapperClass(Map.class);
	job.setReducerClass(Reduce.class);

	if(m_Exp.inputSize>30){

	  job.setInputFormatClass(NLineInputFormat.class);
	  job.getConfiguration().set("mapreduce.input.lineinputformat.linespermap",m_Exp.num);
	}else{
	  job.setInputFormatClass(TextInputFormat.class);
	}
	job.setOutputFormatClass(TextOutputFormat.class);

	FileInputFormat.setInputPaths(job, new Path(args[argIndex-1]));
	FileOutputFormat.setOutputPath(job, new Path(args[argIndex]));
	
	boolean success = job.waitForCompletion(true);
	return success ? 0 : 1;
  }
  
  /**
   * Method to determine how many lines to read per input split.
   * @param number an total amount of lines will occur in
   * the final output file, given knowledge of datasets, algorithms, folds and runs number,.
   */
  public static void determineLinesPerMap(int number){

    int current = 30;

    while(true){
      if(number % current ==0){
	m_Exp.num = new Integer(current).toString();
	break;
      }
      current--;
    }
  }
  
  /**
   * Main method to run Hadoop experiment.
   * In the end, it will produced the required CSV file,
   * and an arff file with same name.
   * @param args type of String[]
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println(
	  "\nUsage: weka.hadoop.HardiredHadoopExperiment\n"
	  + "\t   -jar  the jar file to execute\n"
	  + "\t   -libjars  all the class paths\n"
	  + "\t   -classifier <classifier incl. parameters>(can be supplied multiple times)\n"
	  + "\t   -dataset  datasets (can be supplied multiple times)\n"
	  + "\t   -runs <# of runs>\n"
	  + "\t   -folds <folds for CV>\n"
	  + "\t   -exptype <classification|regression>\n"
	  + "\t   -classindex <first|last|a number|default>\n"
	  + "\t   -confhome  path of hadoop configuration folder\n" 	
	  + "\t   -csv <post-process csv file>");  
    }

    String option;
    ArrayList<String> tempargs = new ArrayList<String>();
    
    boolean isClassification = false;
    String[] originalArgs = args.clone();
    option = Utils.getOption("exptype", args);
    if (option.length() == 0)
      throw new IllegalArgumentException("No experiment type provided!");
    
    if (option.equals("classification")) {
      isClassification = true;
    }
    else if (option.equals("regression")) {
      isClassification = false;
    }
    else {
      throw new IllegalArgumentException("Unknown experiment type '" + option + "'!");
    }

    /**
     * Allow multiple classifiers
     */
    boolean c = false;
    DefaultListModel classifiers = new DefaultListModel();
    do {
	option = Utils.getOption("classifier", args);
	if(option.length()>0){
	  c = true;
	  classifiers.addElement(option);
	}
    }
    while(option.length()>0);
    if (!c)
	throw new IllegalArgumentException("No classifiers provided!");
    
    option = Utils.getOption("runs", args);
    int run = Integer.parseInt(option);
    m_Exp.m_Repetition= run;
    
    option = Utils.getOption("folds",args);
    int folds = Integer.parseInt(option);
    m_Exp.m_NumFolds = folds;
    
    option = Utils.getOption("confHome", args);
    String confFolder = option;
    
    /**
     * Allow multiple datasets
     */
    boolean data = false;
    DefaultListModel model = new DefaultListModel();
    do {
	      option = Utils.getOption("dataset", args);
	      if (option.length() > 0) {
		File file = new File(option);
		if (!file.exists())
		  throw new IllegalArgumentException("File '" + option + "' does not exist!");
		data = true;
		model.addElement(file);
	      }
	    }
    while (option.length() > 0);
    if (!data)
	      throw new IllegalArgumentException("No data files provided!");
    
    /** 
     * Classindex
     */
    String index = Utils.getOption("classindex", args);
    

    
    /** 
     * Write parameters into given file path
     */
    int argIndex = 0;
    while (args[argIndex].startsWith("-")){
      argIndex += 2;
    }
    File fi = null;
    
    try{

      fi = File.createTempFile("input", ".tmp");
      fi.deleteOnExit();
      tempargs.add(originalArgs[0]);
      tempargs.add(originalArgs[1]);
      tempargs.add(fi.getName());

    
    m_Exp.inputSize=classifiers.size() * run * model.size() * folds;
    determineLinesPerMap(m_Exp.inputSize);
    
    BufferedWriter output = new BufferedWriter(new FileWriter(fi));  
    
 
      for(int datasets=0;datasets<model.size();datasets++){//datasets      
	   for(int repetition =1;repetition<=run;repetition++){	//repetitions
		for(int algorithm=0;algorithm<classifiers.size();algorithm++){	//algorithms
		     for(int fold=0;fold<folds;fold++){//folds		
		    	String msg = classifiers.getElementAt(algorithm).toString()+","+repetition+","+model.getElementAt(datasets).toString()+","+fold+","+index+","+new Boolean(isClassification).toString()+","+confFolder;//+","+new Boolean(exp.classification).toString();
		  	output.write(msg+"\n");
		     }
		}
	   }
      }
  	output.flush();
  	output.close();
    }catch(Exception e){
	 e.printStackTrace();
    }
    Configuration conf = new Configuration();
    conf.addResource(new Path(confFolder+"/core-site.xml"));
    FileSystem fs = FileSystem.get(conf);
    fs.copyFromLocalFile(new Path(fi.getPath()), new Path(fi.getName()));
    File x = File.createTempFile("output", ".tmp");
    x.deleteOnExit();
    String foldername = x.getName().substring(0, x.getName().length()-4);
    tempargs.add(foldername);


    String[] a = tempargs.toArray(new String[tempargs.size()]);
    int ret = ToolRunner.run(m_Exp,a);
    
    if(ret==0){
    fi.delete();
    fs.delete(new Path(fi.getName()),false);
    ArrayList<String> title = new ArrayList<String>();


    File fii = File.createTempFile("temp", ".txt");
    fii.deleteOnExit();
 
    fs.copyToLocalFile(new Path(foldername+"/part-r-00000"), new Path(fii.getAbsolutePath()));
  
    BufferedReader bf = new BufferedReader(new FileReader(fii));
    
    option = Utils.getOption("csv", args);
    String csvPath = option;
    if(option.length()==0)
	throw new IllegalArgumentException("No csv output directory provided");
    File f = new File(option);
    if(f.exists()) f.delete();
    BufferedWriter output = new BufferedWriter(new FileWriter(f));
    
    /**
     * Determine CSV file titles
     */
    String strLine;
    int counter = 1;
    while((strLine = bf.readLine())!=null){
      if(counter%2==0){
	counter++;
	continue;
      }
      StringTokenizer token = new StringTokenizer(strLine,",");
      while(token.hasMoreElements()){
	String header = token.nextToken();
	if(title.isEmpty()){
	  title.add(header);
	}else{
	  if(!title.contains(header)){
	    title.add(header);
	  }
	}
      }
      counter++;
    }
    bf.close();

    /**
     * write output title into output CSV file
     */
    String temp="";
    for(int i=0;i<title.size();i++){
      temp+=title.get(i);
      if(i<title.size()-1){
	temp+=",";
      }      
    }
    output.write(temp+"\n");

    bf = new BufferedReader(new FileReader(fii));
    
    /**
     * Write data into CSV file according to individual titles
     */
    strLine="";
    counter = 1;
     List<String> key_title = new ArrayList<String>();
     Hashtable<String,String> table = new Hashtable<String,String>();
     while((strLine = bf.readLine())!=null){
       StringTokenizer token = new StringTokenizer(strLine,",");
       if(counter%2==0){

 	for(String k: key_title){
 	  String datA = token.nextToken();
 	  table.put(k,datA);
 	}
 	temp = "";
 	for(int i=0;i<title.size();i++){
 	  String key = title.get(i);
 	  if(table.containsKey(key)){
 	    temp+= table.get(key);
 	  }else{
 	    temp+="?";
 	  }
 	  if(i<title.size()-1){
 	    temp+=",";
 	  }    
 	}
 	output.write(temp+"\n");
 	table.clear();
 	key_title.clear();
       }else{
 	while(token.hasMoreElements()){
 	  String tk = token.nextToken();	
 	  table.put(tk,"");
 	  key_title.add(tk);
 	}
       }
       counter++;
     }
     output.flush();
     output.close();
     bf.close();
     
    Instances csvDataset = DataSource.read(csvPath);
    String arffPath = csvPath.substring(0, csvPath.length()-3)+"arff";
    
    DataSink.write(arffPath, csvDataset);
   
    System.exit(ret);
   }
  }
  
}
