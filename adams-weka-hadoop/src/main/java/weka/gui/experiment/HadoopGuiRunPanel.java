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
 * HadoopGuiRunPanel.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.experiment;

import adams.core.io.FileUtils;
import weka.core.SerializedObject;
import weka.core.Utils;
import weka.experiment.CSVResultListener;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.Experiment;
import weka.experiment.RemoteExperiment;
import weka.hadoop.HadoopExperiment;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;



/**
 * This panel controls the running of an experiment using Hadoop.
 *
 */
public class HadoopGuiRunPanel
  extends JPanel
  implements ActionListener {

  /** for serialization */
  private static final long serialVersionUID = 1691868018596872051L;

  /** The message displayed when no experiment is running */
  protected static final String NOT_RUNNING = "Not running";

  /** Click to start running the experiment */
  protected JButton m_StartBut = new JButton("Start");

  /** Click to signal the running experiment to halt */
  protected JButton m_StopBut = new JButton("Stop");

  protected HadoopRunLogPanel m_Log = new HadoopRunLogPanel();

  /** The experiment to run */
  protected Experiment m_Exp;

  /** The thread running the experiment */
  protected Thread m_RunThread = null;

  protected String m_hadoopconf ="", m_jarfile = "", m_hadoopJar="",m_hadoopLibjars="";

  protected HadoopGuiSetupPanel m_hadoopGuiSetupPanel;

  public HadoopGuiSetupPanel getHadoopGuiSetupPanel(){
    return m_hadoopGuiSetupPanel;
  }
  public void setHadoopGuiSetupPanel(HadoopGuiSetupPanel value){
    m_hadoopGuiSetupPanel = value;
  }
  protected String filename = "", jarname="";
  /*
   * A class that handles running a copy of the experiment
   * in a separate thread
   */
  class ExperimentRunner
    extends Thread
    implements Serializable {

    /** for serialization */
    private static final long serialVersionUID = -5591889874714150118L;

    Experiment m_ExpCopy;
    Process p;
    String outputFolder;
    File jarFile=null;
    String hadoopHomePath = m_hadoopGuiSetupPanel.getHadoopHomePath();
    String hadoopConfPath = m_hadoopGuiSetupPanel.getHadoopConfPath();
    String expType = m_hadoopGuiSetupPanel.getExpType();
    boolean isInterrupted = false;
    public ExperimentRunner(final Experiment exp) throws Exception {

      // Create a full copy using serialization
      if (exp == null) {
	System.err.println("Null experiment!!!");
      } else {
	System.err.println("Running experiment: " + exp.toString());
      }
      System.err.println("Writing experiment copy");
      SerializedObject so = new SerializedObject(exp);
      System.err.println("Reading experiment copy");
      m_ExpCopy = (Experiment) so.getObject();
      System.err.println("Made experiment copy");

    }

    /**
     * Aborts experiment.
     */
    public void abortExperiment(){
      p.destroy();
      isInterrupted = true;
      List<String> instruction = new ArrayList<String>();
      instruction.add(hadoopHomePath+"/bin/hadoop");
      instruction.add("--config");
      instruction.add(hadoopConfPath);
      instruction.add("fs");
      instruction.add("-rm");
      instruction.add("input*");
      String[] temp = instruction.toArray(new String[instruction.size()]);
      instruction = new ArrayList<String>();
      instruction.add(hadoopHomePath+"/bin/hadoop");
      instruction.add("--config");
      instruction.add(hadoopConfPath);
      instruction.add("fs");
      instruction.add("-rmr");
      instruction.add("output*");
      String[] temp2= instruction.toArray(new String[instruction.size()]);
      int i1 = -1, i2=-1;
      Process p2;

      try {
	logMessage("Deleting unnecessary files");
	p2 = Runtime.getRuntime().exec(temp);
	i1 = p2.waitFor();
	p2 = Runtime.getRuntime().exec(temp2);
	i2 = p2.waitFor();
	if(i1==0 && i2==0){
	  m_RunThread=null;
	  m_StartBut.setEnabled(true);
	}
      }
      catch (Exception e1) {
	e1.printStackTrace();
      }
    }

    /**
     * Starts running the experiment by creating new process
     * that runs Hadoop
     */

    public void run() {
      jarFile= FileUtils.createTempFile("hadoopGui", ".jar");
      jarFile.deleteOnExit();
      jarname = "";

      String currentJar = hadoopHomePath+"/"+jarFile.getName();
      createJar(hadoopHomePath,jarFile.getName());

      List<String> instruction = new ArrayList<String>();
      instruction.add(hadoopHomePath+"/bin/hadoop");
      instruction.add("--config");
      instruction.add(hadoopConfPath);

      instruction.add("jar");
      instruction.add(currentJar);

      instruction.add("-libjars");
      instruction.add(jarname);

      DefaultListModel m_Datasets = m_ExpCopy.getDatasets();
      for(int i=0;i<m_Datasets.size();i++){
	instruction.add("-dataset");
	instruction.add(m_Datasets.elementAt(i).toString());
      }

      for(int i=0;i<m_ExpCopy.getPropertyArrayLength();i++){
	instruction.add("-classifier");
	instruction.add(Utils.toCommandLine(m_ExpCopy.getPropertyArrayValue(i)));
      }
      instruction.add("-runs");
      instruction.add(""+m_ExpCopy.getRunUpper());

      instruction.add("-folds");
      instruction.add(""+((CrossValidationResultProducer) m_ExpCopy.getResultProducer()).getNumFolds());

      instruction.add("-csv");
      instruction.add(((CSVResultListener)m_ExpCopy.getResultListener()).outputFileName());

      instruction.add("-exptype");
      instruction.add(expType);

      instruction.add("-classindex");
      instruction.add("last");

      instruction.add("-confhome");
      instruction.add(hadoopConfPath);

      String[] temp = instruction.toArray(new String[instruction.size()]);
      try{

	logMessage("Started");
	statusMessage("Running");
	logMessage("Command line: "+Utils.joinOptions(temp));
	logMessage("Generated Jar file: "+currentJar);

	m_StartBut.setEnabled(false);
	m_StopBut.setEnabled(true);

	p = Runtime.getRuntime().exec(temp);

	BufferedReader m_Stream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	String line;
	while((line=m_Stream.readLine())!=null){
	  logMsgWithNoTime(line);
	}
	int complete = p.waitFor();

	m_StopBut.setEnabled(false);
	m_StartBut.setEnabled(true);
	if(complete==0){
	  statusMessage("Task Completed");
	  logMessage("Finished");
	  logMessage("There were 0 errors");
	}else{
	  statusMessage("Task failed");
	  logMessage("Interrupted");
	}
	isInterrupted = false;
	m_RunThread = null;

      }catch(Exception e){
	e.printStackTrace();
      }

    }
  }


  /**
   * Creates the run panel with input setting of hadoop and jar file.
   */
  public void setHadoop(String hadoopconf) {
    m_hadoopconf = hadoopconf;

  }

  public void setJar(String jarFile){
    m_jarfile = jarFile;
  }
  /**
   * Creates the run panel with no initial experiment.
   */
  public HadoopGuiRunPanel() {

    m_StartBut.addActionListener(this);
    m_StopBut.addActionListener(this);
    m_StartBut.setEnabled(false);
    m_StopBut.setEnabled(false);
    m_StartBut.setMnemonic('S');
    m_StopBut.setMnemonic('t');
    m_Log.statusMessage(NOT_RUNNING);

    // Set the GUI layout
    JPanel controls = new JPanel();
    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();
    controls.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    controls.setLayout(gb);
    constraints.gridx=0;constraints.gridy=0;constraints.weightx=5;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridwidth=1;constraints.gridheight=1;
    constraints.insets = new Insets(0,2,0,2);
    controls.add(m_StartBut,constraints);
    constraints.gridx=1;constraints.gridy=0;constraints.weightx=5;
    constraints.gridwidth=1;constraints.gridheight=1;
    controls.add(m_StopBut,constraints);
    setLayout(new BorderLayout());
    add(controls, BorderLayout.NORTH);
    add(m_Log, BorderLayout.CENTER);
  }
  /**
   * Creates the panel with the supplied initial experiment.
   *
   * @param exp a value of type 'Experiment'
   */
  public HadoopGuiRunPanel(Experiment exp) {

    this();
    setExperiment(exp);
  }

  /**
   * Sets the experiment the panel operates on.
   *
   * @param exp a value of type 'Experiment'
   */
  public void setExperiment(Experiment exp) {

    m_Exp = exp;
    m_StartBut.setEnabled(m_RunThread == null);
    m_StopBut.setEnabled(m_RunThread != null);
  }


  public void determineHadoopJar(){
    String classPath = adams.core.management.Java.getClassPath(false);
    StringTokenizer token = new StringTokenizer(classPath,":");
    token.nextToken();
    loop1:
    while(token.hasMoreElements()){
      String currentJar = token.nextToken();
      StringTokenizer token2 = new StringTokenizer(currentJar,"/");
      loop2:
      while(token2.hasMoreElements()){
	String currentFile = token2.nextToken();
	if(currentFile.startsWith("._")) {
	  continue loop2;
	}
	if(currentFile.startsWith("adams-weka-hadoop")){
	  m_hadoopJar = currentFile;
	  break loop1;
	}
      }
    }
  }

  public void determineHadoopLibJars(){
    String classPath = adams.core.management.Java.getClassPath(false);
    System.err.println(classPath);
    classPath = classPath.substring(1);

    m_hadoopLibjars = classPath.replaceAll(":", ",");

  }

  /**
   * Create jar file on the fly for current Hadoop experiment
   */
  public void createJar(String path,String jarName){
    try{
      StringBuilder st = new StringBuilder("");
      int BUFFER_SIZE = 10240;
      String classPath = adams.core.management.Java.getClassPath(false);
      StringTokenizer token = new StringTokenizer(classPath,":");
      byte buffer[] = new byte[BUFFER_SIZE];

      File archiveFile = new File(path+"/"+jarName);
      FileOutputStream stream = new FileOutputStream(archiveFile);

      Manifest manifest = new Manifest();
      Attributes attributes = manifest.getMainAttributes();
      attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");

      token.nextToken();
      String value = "";
      while(token.hasMoreElements()){
	String currentJarPath = token.nextToken();
	StringTokenizer rebuildJar = new StringTokenizer(currentJarPath,"/");
	String currentRebuildJar = "";
	while(rebuildJar.hasMoreElements()){
	  currentRebuildJar = rebuildJar.nextToken();
	}
	if(currentRebuildJar.startsWith("._")){
	  continue;
	}
	st.append(currentJarPath);
	st.append(",");

	value+=("lib/"+currentRebuildJar+" ");

      }
      st = st.deleteCharAt(st.length()-1);
      jarname = st.toString();
      attributes.put(Attributes.Name.CLASS_PATH, value.trim());
      attributes.put(Attributes.Name.MAIN_CLASS, HadoopExperiment.class.getName());
      JarOutputStream out = new JarOutputStream(stream, manifest);
      token = new StringTokenizer(classPath,":");
      token.nextToken();
      while(token.hasMoreElements()){
	String currentFile = token.nextToken();

	File cf = new File(currentFile);
	StringTokenizer rebuildFile = new StringTokenizer(currentFile,"/");
	String currentRebuildFile = "";
	while(rebuildFile.hasMoreTokens()){
	  currentRebuildFile = rebuildFile.nextToken();
	}
	if(currentRebuildFile.charAt(0)=='.' && currentRebuildFile.charAt(1)=='_'){
	  continue;
	}
	JarEntry jarAdd = new JarEntry("lib/"+currentRebuildFile);
	jarAdd.setTime(cf.lastModified());
	out.putNextEntry(jarAdd);

	FileInputStream in = new FileInputStream(cf);
	while(true){
	  int nRead = in.read(buffer,0,buffer.length);
	  if(nRead<=0)
	    break;
	  out.write(buffer,0,nRead);
	}
	FileUtils.closeQuietly(in);
      }
      out.flush();
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(stream);
      System.out.println("Adding completed OK");
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
  }

  /**
   * Controls starting and stopping the experiment.
   *
   * @param e a value of type 'ActionEvent'
   */
  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == m_StartBut) {
      if (m_RunThread == null) {
	try {
	  if(!m_Log.m_LogText.getText().equals("")){
	    logMsgWithNoTime("\n --------\n");
	  }
	  logMessage("Initializing");
	  m_StartBut.setEnabled(false);
	  m_StopBut.setEnabled(true);
	  statusMessage("Starting");
	  m_RunThread = new ExperimentRunner(m_Exp);
	  m_RunThread.setPriority(Thread.MIN_PRIORITY); // UI has most priority
	  m_RunThread.start();
	} catch (Exception ex) {
	  ex.printStackTrace();
	  logMessage("Problem creating experiment copy to run: "
	    + ex.getMessage());
	}
      }
    } else if (e.getSource() == m_StopBut) {

      m_StopBut.setEnabled(false);
      logMessage("User aborting experiment. ");
      if (m_Exp instanceof RemoteExperiment) {
	logMessage("Waiting for remote tasks to "
	  +"complete...");
      }

      ((ExperimentRunner)m_RunThread).abortExperiment();

      m_RunThread = null;
    }
  }

  /**
   * Sends the supplied message to the log panel log area.
   *
   * @param message the message to log
   */
  protected void logMessage(String message) {
    m_Log.logMessage(message);
  }

  /**
   * Send the supplied message to log pannel log area
   * @oaram message the message to log
   */
  protected void logMsgWithNoTime(String message){
    m_Log.logMsgWithNoTime(message);
  }

  /**
   * Sends the supplied message to the log panel status line.
   *
   * @param message the status message
   */
  protected void statusMessage(String message) {
    m_Log.statusMessage(message);
  }

  /**
   * Tests out the run panel from the command line.
   *
   * @param args may contain options specifying an experiment to run.
   */
  public static void main(String [] args) {

    try {
      boolean readExp = Utils.getFlag('l', args);
      final String expFile = Utils.getOption('f', args);
      if (readExp && (expFile.length() == 0)) {
	throw new Exception("A filename must be given with the -f option");
      }
      Experiment exp = null;
      if (readExp) {
	FileInputStream fi = new FileInputStream(expFile);
	ObjectInputStream oi = new ObjectInputStream(
	  new BufferedInputStream(fi));
	Object to = oi.readObject();
	if (to instanceof RemoteExperiment) {
	  exp = (RemoteExperiment)to;
	} else {
	  exp = (Experiment)to;
	}
	FileUtils.closeQuietly(oi);
	FileUtils.closeQuietly(fi);
      } else {
	exp = new Experiment();
      }
      System.err.println("Initial Experiment:\n" + exp.toString());
      final JFrame jf = new JFrame("Run Weka Experiment");
      jf.getContentPane().setLayout(new BorderLayout());
      final HadoopGuiRunPanel sp = new HadoopGuiRunPanel(exp);
      //sp.setBorder(BorderFactory.createTitledBorder("Setup"));
      jf.getContentPane().add(sp, BorderLayout.CENTER);
      jf.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  System.err.println("\nExperiment Configuration\n"
	    + sp.m_Exp.toString());
	  jf.dispose();
	  System.exit(0);
	}
      });
      jf.pack();
      jf.setVisible(true);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.println(ex.getMessage());
    }
  }
}
