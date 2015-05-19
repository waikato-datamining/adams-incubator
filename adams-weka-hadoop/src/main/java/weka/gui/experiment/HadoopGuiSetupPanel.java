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
 *    HadoopGuiSetupPanel.java
 *    Copyright (C) 2002-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.gui.experiment;

import adams.core.io.TempUtils;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.chooser.FileChooserPanel;
import weka.classifiers.Classifier;
import weka.core.xml.KOML;
import weka.experiment.CSVResultListener;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.DatabaseResultListener;
import weka.experiment.Experiment;
import weka.experiment.PropertyNode;
import weka.experiment.RegressionSplitEvaluator;
import weka.experiment.SplitEvaluator;
import weka.gui.ExtensionFileFilter;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.io.File;

/** 
 * This panel controls the configuration of an experiment.
  * <p>
 * If <a href="http://koala.ilog.fr/XML/serialization/" target="_blank">KOML</a>
 * is in the classpath the experiments can also be serialized to XML instead of a
 * binary format.
*
 * @author Richard kirkby (rkirkby@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz) 
 * @version $Revision$
 */
public class HadoopGuiSetupPanel
  extends JPanel {

  /** for serialization */
  private static final long serialVersionUID = 5257424515609176509L;

  /** The experiment being configured */
  protected Experiment m_Exp;

  /** The panel which switched between simple and advanced setup modes */
  protected SetupModePanel m_modePanel = null;

  /** The database destination URL to store results into */
  protected String m_destinationDatabaseURL;

  /** The filename to store results into */
  protected String m_destinationFilename = "";

  /** The number of folds for a cross-validation experiment */
  protected int m_numFolds = 10;

  /** The training percentage for a train/test split experiment */
  protected double m_trainPercent = 66;

  /** The number of times to repeat the sub-experiment */
  protected int m_numRepetitions = 10;

  /** Whether or not the user has consented for the experiment to be simplified */
  protected boolean m_userHasBeenAskedAboutConversion;

  /** Filter for choosing CSV files */
  protected ExtensionFileFilter m_csvFileFilter =
    new ExtensionFileFilter(".csv", "Comma separated value files");

  /** FIlter for choosing ARFF files */
 // protected ExtensionFileFilter m_arffFileFilter =
 //   new ExtensionFileFilter(".arff", "ARFF files");

  /** Click to load an experiment */
  protected JButton m_OpenBut = new JButton("Open...");

  /** Click to save an experiment */
  protected JButton m_SaveBut = new JButton("Save...");

  /** Click to create a new experiment with default settings */
  protected JButton m_NewBut = new JButton("New");

  /** A filter to ensure only experiment files get shown in the chooser */
  protected FileFilter m_ExpFilter = 
    new ExtensionFileFilter(Experiment.FILE_EXTENSION, 
                            "Experiment configuration files (*" + Experiment.FILE_EXTENSION + ")");

  /** A filter to ensure only experiment (in KOML format) files get shown in the chooser */
  protected FileFilter m_KOMLFilter = 
    new ExtensionFileFilter(KOML.FILE_EXTENSION, 
                            "Experiment configuration files (*" + KOML.FILE_EXTENSION + ")");

  /** A filter to ensure only experiment (in XML format) files get shown in the chooser */
  protected FileFilter m_XMLFilter = 
    new ExtensionFileFilter(".xml", 
                            "Experiment configuration files (*.xml)");

  /** The file chooser for selecting experiments */
  protected JFileChooser m_FileChooser =
    new JFileChooser(new File(System.getProperty("user.dir")));

  /** The file chooser for selecting result destinations */
  protected JFileChooser m_DestFileChooser =
    new JFileChooser(new File(System.getProperty("user.dir")));

  /** Combo box for choosing experiment destination type */
  //protected JLabel m_ResultsDestinationCBox = new JLabel("CSV File");
  //protected JComboBox m_ResultsDestinationCBox = new JComboBox();

  /** Label for destination field */
  protected JLabel m_ResultsDestinationPathLabel = new JLabel("CSV Filename:");

  /** Input field for result destination path */ 
  protected FileChooserPanel m_ResultDestinationPath = new FileChooserPanel();
  //protected JTextField m_ResultsDestinationPathTField = new JTextField();
  protected DirectoryChooserPanel m_InputHadoopHome = new DirectoryChooserPanel(new File(System.getProperty("user.dir")));
  protected JLabel m_InputHadoopHomeLabel = new JLabel("Hadoop Home");
  
 // protected JTextField m_InputHadoopHomeField = new JTextField();
  
  protected JLabel m_inputHadoopConfFolder = new JLabel("Hadoop Conf Folder");
  
  protected DirectoryChooserPanel m_InputHadoopConf = new DirectoryChooserPanel(new File(System.getProperty("user.dir")));
  
  /** Button for browsing destination files */
  //protected JButton m_BrowseDestinationButton = new JButton("Browse...");
 // protected JButton m_HadoopHomeButton = new JButton("Browse");
  protected JButton m_HadoopConfButton = new JButton("Browse");
  
  /** Label for parameter field */
  protected JLabel m_ExperimentParameterLabel = new JLabel("Number of folds:");

  /** Input field for experiment parameter */
  protected JTextField m_ExperimentParameterTField = new JTextField(); 

  /** Radio button for choosing classification experiment */
  protected JRadioButton m_ExpClassificationRBut = 
    new JRadioButton("Classification");

  /** Radio button for choosing regression experiment */
  protected JRadioButton m_ExpRegressionRBut = 
    new JRadioButton("Regression");

  /** Input field for number of repetitions */
  protected JTextField m_NumberOfRepetitionsTField = new JTextField();  

  /** Radio button for choosing datasets first in order of execution */ 
  

  /** The strings used to identify the combo box choices */
  protected static String DEST_DATABASE_TEXT = ("JDBC database");
  protected static String DEST_ARFF_TEXT = ("ARFF file");
  protected static String DEST_CSV_TEXT = ("CSV file");
  protected static String TYPE_CROSSVALIDATION_TEXT = ("Cross-validation");
  protected static String TYPE_RANDOMSPLIT_TEXT = ("Train/Test Percentage Split (data randomized)");
  protected static String TYPE_FIXEDSPLIT_TEXT = ("Train/Test Percentage Split (order preserved)");

  /** The panel for configuring selected datasets */
  protected DatasetListPanel m_DatasetListPanel = new DatasetListPanel();

  /** The panel for configuring selected algorithms */
  protected AlgorithmListPanel m_AlgorithmListPanel = new AlgorithmListPanel();

  /** A button for bringing up the notes */
  protected JButton m_NotesButton =  new JButton("Notes");

  /** Frame for the notes */
  protected JFrame m_NotesFrame = new JFrame("Notes");

  /** Area for user notes Default of 10 rows */
  protected JTextArea m_NotesText = new JTextArea(null, 10, 0);

  /**
   * Manages sending notifications to people when we change the experiment,
   * at this stage, only the resultlistener so the resultpanel can update.
   */
  protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);

  protected String m_hadoopConf="", m_jarPath = "",m_expType="";

  
  /**
   * Creates the setup panel with the supplied initial experiment.
   *
   * @param exp a value of type 'Experiment'
   */
  public HadoopGuiSetupPanel(Experiment exp) {

    this();
    setExperiment(exp);
  }
  
  /**
   * Creates the setup panel with no initial experiment.
   */
  public HadoopGuiSetupPanel() {

    // everything disabled on startup
   // m_ResultsDestinationCBox.setEnabled(false);
  
   // m_InputHadoopHomeField.setEnabled(false);
    m_InputHadoopHomeLabel.setEnabled(false);
    m_InputHadoopHome.setEnabled(false);
    m_inputHadoopConfFolder.setEnabled(false);
    m_InputHadoopConf.setEnabled(false);
    m_ResultsDestinationPathLabel.setEnabled(false);
    m_ResultDestinationPath.setEnabled(false);
    //m_ResultsDestinationPathTField.setEnabled(false);
    //m_BrowseDestinationButton.setEnabled(false); 
    //m_ExperimentTypeCBox.setEnabled(false);
    m_ExperimentParameterLabel.setEnabled(false);
    m_ExperimentParameterTField.setEnabled(false);
    m_ExpClassificationRBut.setEnabled(false);
    m_ExpRegressionRBut.setEnabled(false);
    m_NumberOfRepetitionsTField.setEnabled(false);
   // m_HadoopHomeButton.setEnabled(false);
    m_HadoopConfButton.setEnabled(false);
  

    // get sensible default database address
    try {
      m_destinationDatabaseURL = (new DatabaseResultListener()).getDatabaseURL();
    } catch (Exception e) {}

    // create action listeners
    m_NewBut.setMnemonic('N');
    m_NewBut.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  Experiment newExp = new Experiment();
	  CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
	  cvrp.setNumFolds(10);
	  cvrp.setSplitEvaluator(new ClassifierSplitEvaluator());
	  newExp.setResultProducer(cvrp);
	  newExp.setPropertyArray(new Classifier[0]);
	  newExp.setUsePropertyIterator(true);
	  setExperiment(newExp);

          // defaults
          if (ExperimenterDefaults.getUseClassification())
            m_ExpClassificationRBut.setSelected(true);
          else
            m_ExpRegressionRBut.setSelected(true);
          
          //setSelectedItem(
           //   m_ResultsDestinationCBox, ExperimenterDefaults.getDestination());
         // destinationTypeChanged();
          
        //  setSelectedItem(
        //      m_ExperimentTypeCBox, ExperimenterDefaults.getExperimentType());
          
          m_numRepetitions = ExperimenterDefaults.getRepetitions();
          m_NumberOfRepetitionsTField.setText(
              "" + m_numRepetitions);
          
          if (ExperimenterDefaults.getExperimentType().equals(
                TYPE_CROSSVALIDATION_TEXT)) {
            m_numFolds = ExperimenterDefaults.getFolds();
            m_ExperimentParameterTField.setText(
                "" + m_numFolds);
          }
          else {
            m_trainPercent = ExperimenterDefaults.getTrainPercentage();
            m_ExperimentParameterTField.setText(
                "" + m_trainPercent);
          }
          


          expTypeChanged();
	}
      });
    m_SaveBut.setEnabled(false);
    m_SaveBut.setMnemonic('S');
    m_SaveBut.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  saveExperiment();
	}
      });
    m_OpenBut.setMnemonic('O');
    m_OpenBut.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  openExperiment();
	}
      });
    m_FileChooser.addChoosableFileFilter(m_ExpFilter);
    if (KOML.isPresent())
       m_FileChooser.addChoosableFileFilter(m_KOMLFilter);
    m_FileChooser.addChoosableFileFilter(m_XMLFilter);
    if (ExperimenterDefaults.getExtension().equals(".xml"))
      m_FileChooser.setFileFilter(m_XMLFilter);
    else if (KOML.isPresent() && ExperimenterDefaults.getExtension().equals(KOML.FILE_EXTENSION))
      m_FileChooser.setFileFilter(m_KOMLFilter);
    else
      m_FileChooser.setFileFilter(m_ExpFilter);
    m_FileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    m_DestFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

   /* m_BrowseDestinationButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  //using this button for both browsing file & setting username/password
	    chooseDestinationFile();
	}
      });
      */
   /* m_HadoopHomeButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
	chooseHadoopHomeFolder();
      }
    });
   */ 
  /*  m_HadoopConfButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
	chooseHadoopConfFolder();
      }
    });
  */  
    m_ExpClassificationRBut.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  expTypeChanged();
	}
      });
  
    m_ExpRegressionRBut.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  expTypeChanged();
	}
      });
/*
    m_InputHadoopHomeField.getDocument().addDocumentListener(new DocumentListener() {
	public void insertUpdate(DocumentEvent e) {hadoopAddressChanged();}
	public void removeUpdate(DocumentEvent e) {hadoopAddressChanged();}
	public void changedUpdate(DocumentEvent e) {hadoopAddressChanged();}
    });
   */
   
   /* m_inputHadoopConfField.getDocument().addDocumentListener(new DocumentListener() {
	public void insertUpdate(DocumentEvent e) {jarAddressChanged();}
	public void removeUpdate(DocumentEvent e) {jarAddressChanged();}
	public void changedUpdate(DocumentEvent e) {jarAddressChanged();}
  });*/
   /* m_ResultDestinationPath.getDocument().addDocumentListener(new DocumentListener() {
	public void insertUpdate(DocumentEvent e) {destinationAddressChanged();}
	public void removeUpdate(DocumentEvent e) {destinationAddressChanged();}
	public void changedUpdate(DocumentEvent e) {destinationAddressChanged();}
      });
   */
    m_ResultDestinationPath.addChangeListener(new ChangeListener(){

      @Override
      public void stateChanged(ChangeEvent arg0) {
	// TODO Auto-generated method stub
	destinationAddressChanged();
      }
      
    });
    m_ExperimentParameterTField.getDocument().addDocumentListener(new DocumentListener() {
	public void insertUpdate(DocumentEvent e) {expParamChanged();}
	public void removeUpdate(DocumentEvent e) {expParamChanged();}
	public void changedUpdate(DocumentEvent e) {expParamChanged();}
      });

    m_NumberOfRepetitionsTField.getDocument().addDocumentListener(new DocumentListener() {
	public void insertUpdate(DocumentEvent e) {numRepetitionsChanged();}
	public void removeUpdate(DocumentEvent e) {numRepetitionsChanged();}
	public void changedUpdate(DocumentEvent e) {numRepetitionsChanged();}
      });

    m_NotesFrame.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  m_NotesButton.setEnabled(true);
	}
      });
    m_NotesFrame.getContentPane().add(new JScrollPane(m_NotesText));
    m_NotesFrame.setSize(600, 400);

    m_NotesButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_NotesButton.setEnabled(false);
	  m_NotesFrame.setVisible(true);
	}
      });
    m_NotesButton.setEnabled(false);

    m_NotesText.setEditable(true);
    //m_NotesText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_NotesText.addKeyListener(new KeyAdapter() {
	public void keyReleased(KeyEvent e) {
	  m_Exp.setNotes(m_NotesText.getText());
	}
      });
    m_NotesText.addFocusListener(new FocusAdapter() {
	public void focusLost(FocusEvent e) {
	  m_Exp.setNotes(m_NotesText.getText());
	}
      });
    
    // Set up the GUI layout
    JPanel buttons = new JPanel();
    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();
    buttons.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    buttons.setLayout(gb);
    constraints.gridx=0;constraints.gridy=0;constraints.weightx=5;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridwidth=1;constraints.gridheight=1;
    constraints.insets = new Insets(0,2,0,2);
    buttons.add(m_OpenBut,constraints);
    constraints.gridx=1;constraints.gridy=0;constraints.weightx=5;
    constraints.gridwidth=1;constraints.gridheight=1;
    buttons.add(m_SaveBut,constraints);
    constraints.gridx=2;constraints.gridy=0;constraints.weightx=5;
    constraints.gridwidth=1;constraints.gridheight=1;
    buttons.add(m_NewBut,constraints);

    
    JPanel inputName = new JPanel();
    inputName.setLayout(new BorderLayout(5,5));
    inputName.add(m_InputHadoopHomeLabel,BorderLayout.WEST);
    inputName.add(m_InputHadoopHome,BorderLayout.CENTER);
   // inputName.add(m_InputHadoopHome,BorderLayout.WEST);
   // inputName.add(m_InputHadoopHomeField,BorderLayout.CENTER);
    
   // JPanel inputButton = new JPanel();
   // inputButton.setLayout(new BorderLayout(5,5));
   // inputButton.add(inputName,BorderLayout.CENTER);
   // inputButton.add(m_HadoopConfButton,BorderLayout.EAST);
    
    JPanel outputName = new JPanel();
    outputName.setLayout(new BorderLayout(5,5));
    outputName.add(m_inputHadoopConfFolder,BorderLayout.WEST);
    outputName.add(m_InputHadoopConf,BorderLayout.CENTER);
    

    
    JPanel hadoopConf = new JPanel();
    hadoopConf.setLayout(new GridLayout(0,1));
    hadoopConf.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Path Setting"),
			BorderFactory.createEmptyBorder(0,5,5,5)));
    hadoopConf.add(inputName);
    hadoopConf.add(outputName);
    
   /* JPanel outputJar = new JPanel();
    outputJar.setLayout(new BorderLayout());
    outputJar.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Jar file output path"),
			BorderFactory.createEmptyBorder(0,5,5,5)));
    outputJar.add(outputName,BorderLayout.NORTH);
    */
    //inandout.add(outputName,BorderLayout.NORTH);
    //------
    JPanel destName = new JPanel();
    destName.setLayout(new BorderLayout(5, 5));
    destName.add(m_ResultsDestinationPathLabel, BorderLayout.WEST);
    destName.add(m_ResultDestinationPath, BorderLayout.CENTER);

    //JPanel destInner = new JPanel();
    //destInner.setLayout(new BorderLayout(5, 5));

    //destInner.add(destName, BorderLayout.CENTER);
    //destInner.add(m_BrowseDestinationButton, BorderLayout.EAST);

   // JPanel dest = new JPanel();
   // dest.setLayout(new BorderLayout());
   // dest.setBorder(BorderFactory.createCompoundBorder(
   //		  BorderFactory.createTitledBorder("Results Destination"),
//		  BorderFactory.createEmptyBorder(0, 5, 5, 5)
//		  ));
  //  dest.add(destInner, BorderLayout.NORTH);
    hadoopConf.add(destName);
    //-------
    JPanel expParam = new JPanel();
    expParam.setLayout(new BorderLayout(5, 5));
    expParam.add(m_ExperimentParameterLabel, BorderLayout.WEST);
    expParam.add(m_ExperimentParameterTField, BorderLayout.CENTER);

    //ButtonGroup typeBG = new ButtonGroup();
    //typeBG.add(m_ExpClassificationRBut);
    //typeBG.add(m_ExpRegressionRBut);
    m_ExpClassificationRBut.setSelected(true);

    ButtonGroup typeBG = new ButtonGroup();
    typeBG.add(m_ExpClassificationRBut);
    typeBG.add(m_ExpRegressionRBut);
    
    JPanel typeRButtons = new JPanel();
    typeRButtons.setLayout(new GridLayout(1,0));
    typeRButtons.add(m_ExpClassificationRBut);
    typeRButtons.add(m_ExpRegressionRBut);

    //m_ExperimentTypeCBox.addItem(TYPE_CROSSVALIDATION_TEXT);
    //m_ExperimentTypeCBox.addItem(TYPE_RANDOMSPLIT_TEXT);
    //m_ExperimentTypeCBox.addItem(TYPE_FIXEDSPLIT_TEXT);

   /* m_ExperimentTypeCBox.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  expTypeChanged();
	}
      });
*/
    JPanel typeInner = new JPanel();
    typeInner.setLayout(new GridLayout(0,1));
   // typeInner.add(m_ExperimentTypeCBox);
    typeInner.add(expParam);
    typeInner.add(typeRButtons);

    JPanel type = new JPanel();
    type.setLayout(new BorderLayout());
    type.setBorder(BorderFactory.createCompoundBorder(
		  BorderFactory.createTitledBorder("Cross Validation"),
		  BorderFactory.createEmptyBorder(0, 5, 5, 5)
		  ));
    type.add(typeInner, BorderLayout.NORTH);




    JPanel numIter = new JPanel();
    numIter.setLayout(new BorderLayout(5, 5));
    numIter.add(new JLabel("Number of repetitions:"), BorderLayout.WEST);
    numIter.add(m_NumberOfRepetitionsTField, BorderLayout.CENTER);

    JPanel controlInner = new JPanel();
    controlInner.setLayout(new GridLayout(0,1));
    controlInner.add(numIter);
    //controlInner.add(m_OrderDatasetsFirstRBut);
    //controlInner.add(m_OrderAlgorithmsFirstRBut);

    JPanel control = new JPanel();
    control.setLayout(new BorderLayout());
    control.setBorder(BorderFactory.createCompoundBorder(
		  BorderFactory.createTitledBorder("Iteration Control"),
		  BorderFactory.createEmptyBorder(0, 5, 5, 5)
		  ));
    control.add(controlInner, BorderLayout.NORTH);

    JPanel type_control = new JPanel();
    type_control.setLayout(new GridLayout(1,0));
    type_control.add(type);
    type_control.add(control);

    JPanel notes = new JPanel();
    notes.setLayout(new BorderLayout());
    notes.add(m_NotesButton, BorderLayout.CENTER);

    
    JPanel top1 = new JPanel();
    top1.setLayout(new BorderLayout(5,5));
    top1.add(hadoopConf,BorderLayout.NORTH);
  //  top1.add(outputJar,BorderLayout.NORTH);
    //top1.add(dest, BorderLayout.NORTH);
    top1.add(type_control, BorderLayout.CENTER);

    JPanel top = new JPanel();
    top.setLayout(new BorderLayout());
    top.add(buttons, BorderLayout.NORTH);
    top.add(top1, BorderLayout.CENTER);  

    JPanel datasets = new JPanel();
    datasets.setLayout(new BorderLayout());
    datasets.add(m_DatasetListPanel, BorderLayout.CENTER);

    JPanel algorithms = new JPanel();
    algorithms.setLayout(new BorderLayout());
    algorithms.add(m_AlgorithmListPanel, BorderLayout.CENTER);

    JPanel schemes = new JPanel();
    schemes.setLayout(new GridLayout(1,0));
    schemes.add(datasets);
    schemes.add(algorithms);

    setLayout(new BorderLayout());
    add(top, BorderLayout.NORTH);
    add(schemes, BorderLayout.CENTER);
    add(notes, BorderLayout.SOUTH);
  }
  
  /**
   * Sets the selected item of an combobox, since using setSelectedItem(...)
   * doesn't work, if one checks object references!
   *
   * @param cb      the combobox to set the item for
   * @param item    the item to set active
   */
  protected void setSelectedItem(JComboBox cb, String item) {
    int       i;

    for (i = 0; i < cb.getItemCount(); i++) {
      if (cb.getItemAt(i).toString().equals(item)) {
        cb.setSelectedIndex(i);
        break;
      }
    }
  }
  
  /**
   * Deletes the notes frame.
   */
  protected void removeNotesFrame() {
    m_NotesFrame.setVisible(false);
  }

  /**
   * Gets te users consent for converting the experiment to a simpler form.
   *
   * @return true if the user has given consent, false otherwise
   */  
  private boolean userWantsToConvert() {
    
    if (m_userHasBeenAskedAboutConversion) return true;
    m_userHasBeenAskedAboutConversion = true;
    return (JOptionPane.showConfirmDialog(this,
					  "This experiment has settings that are too advanced\n" +
					  "to be represented in the simple setup mode.\n" +
					  "Do you want the experiment to be converted,\n" +
					  "losing some of the advanced settings?\n",
					  "Confirm conversion",
					  JOptionPane.YES_NO_OPTION,
					  JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION);
  }

  /**
   * Sets the panel used to switch between simple and advanced modes.
   *
   * @param modePanel the panel
   */
  public void setModePanel(SetupModePanel modePanel) {

    m_modePanel = modePanel;
  }

  /**
   * Sets the experiment to configure.
   *
   * @param exp a value of type 'Experiment'
   * @return true if experiment could be configured, false otherwise
   */
  public boolean setExperiment(Experiment exp) {
    
    m_userHasBeenAskedAboutConversion = false;
    m_Exp = null; // hold off until we are sure we want conversion
    m_SaveBut.setEnabled(true);
    
    if (exp.getResultListener() instanceof CSVResultListener) {
      //m_ResultsDestinationCBox.setSelectedItem(DEST_CSV_TEXT);
      //m_ResultsDestinationPathLabel.setText("Filename:");
      m_destinationFilename = ((CSVResultListener)exp.getResultListener()).outputFileName();
      m_ResultDestinationPath.setCurrent(new File(m_destinationFilename));   //.setText(m_destinationFilename);
      m_ResultDestinationPath.setEnabled(true);
    }

  //  m_ResultsDestinationCBox.setEnabled(true);
    m_InputHadoopHome.setEnabled(true);
    //m_InputHadoopHomeField.setEnabled(true);
    m_inputHadoopConfFolder.setEnabled(true);
    m_InputHadoopConf.setEnabled(true);
    m_ResultsDestinationPathLabel.setEnabled(true);
    m_ResultDestinationPath.setEnabled(true);
    //m_ResultsDestinationPathTField.setEnabled(true);
   // m_HadoopHomeButton.setEnabled(true);
    m_HadoopConfButton.setEnabled(true);
    m_InputHadoopHomeLabel.setEnabled(true);
    
    if (exp.getResultProducer() instanceof CrossValidationResultProducer) {
      CrossValidationResultProducer cvrp = (CrossValidationResultProducer) exp.getResultProducer();
      m_numFolds = cvrp.getNumFolds();
      m_ExperimentParameterTField.setText("" + m_numFolds);
      
      if (cvrp.getSplitEvaluator() instanceof ClassifierSplitEvaluator) {
	m_ExpClassificationRBut.setSelected(true);
	m_ExpRegressionRBut.setSelected(false);
      } else if (cvrp.getSplitEvaluator() instanceof RegressionSplitEvaluator) {
	m_ExpClassificationRBut.setSelected(false);
	m_ExpRegressionRBut.setSelected(true);
      } else {
	// unknown split evaluator
	System.out.println("SimpleSetup incompatibility: unrecognised split evaluator");
	if (userWantsToConvert()) {
	  m_ExpClassificationRBut.setSelected(true);
	  m_ExpRegressionRBut.setSelected(false);
	} else {
	  return false;
	}
      }
     // m_ExperimentTypeCBox.setSelectedItem(TYPE_CROSSVALIDATION_TEXT);
    }

   // m_ExperimentTypeCBox.setEnabled(true);
    m_ExperimentParameterLabel.setEnabled(true);
    m_ExperimentParameterTField.setEnabled(true);
    m_ExpClassificationRBut.setEnabled(true);
    m_ExpRegressionRBut.setEnabled(true);
    
    if (exp.getRunLower() == 1) {
      m_numRepetitions = exp.getRunUpper();
      m_NumberOfRepetitionsTField.setText("" + m_numRepetitions);
    } else {
      // unsupported iterations
      System.out.println("SimpleSetup incompatibility: runLower is not 1");
      if (userWantsToConvert()) {
	exp.setRunLower(1);
 
	  exp.setRunUpper(10);
	  m_numRepetitions = 10;
	  m_NumberOfRepetitionsTField.setText("" + m_numRepetitions);
	
	
      } else {
	return false;
      }
    }
    m_NumberOfRepetitionsTField.setEnabled(true);



    m_NotesText.setText(exp.getNotes());
    m_NotesButton.setEnabled(true);

    if (!exp.getUsePropertyIterator() || !(exp.getPropertyArray() instanceof Classifier[])) {
      // unknown property iteration
      System.out.println("SimpleSetup incompatibility: unrecognised property iteration");
      if (userWantsToConvert()) {
	exp.setPropertyArray(new Classifier[0]);
	exp.setUsePropertyIterator(true);
      } else {
	return false;
      }
    }

    m_DatasetListPanel.setExperiment(exp);
    m_AlgorithmListPanel.setExperiment(exp);
    
    m_Exp = exp;
    expTypeChanged(); // recreate experiment
    
    m_Support.firePropertyChange("", null, null);
    
    return true;
  }

  /**
   * Gets the currently configured experiment.
   *
   * @return the currently configured experiment.
   */
  public Experiment getExperiment() {

    return m_Exp;
  }
  
  /**
   * Prompts the user to select an experiment file and loads it.
   */
  private void openExperiment() {
    
    int returnVal = m_FileChooser.showOpenDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File expFile = m_FileChooser.getSelectedFile();
    
    // add extension if necessary
    if (m_FileChooser.getFileFilter() == m_ExpFilter) {
      if (!expFile.getName().toLowerCase().endsWith(Experiment.FILE_EXTENSION))
        expFile = new File(expFile.getParent(), expFile.getName() + Experiment.FILE_EXTENSION);
    }
    else if (m_FileChooser.getFileFilter() == m_KOMLFilter) {
      if (!expFile.getName().toLowerCase().endsWith(KOML.FILE_EXTENSION))
        expFile = new File(expFile.getParent(), expFile.getName() + KOML.FILE_EXTENSION);
    }
    else if (m_FileChooser.getFileFilter() == m_XMLFilter) {
      if (!expFile.getName().toLowerCase().endsWith(".xml"))
        expFile = new File(expFile.getParent(), expFile.getName() + ".xml");
    }
    
    try {
      Experiment exp = Experiment.read(expFile.getAbsolutePath());
   
      // cv?
      if(!(exp.getResultProducer() instanceof CrossValidationResultProducer)) 
	throw new Exception("Experiment does not use cross-validation!");
      if (!setExperiment(exp)) {
	if (m_modePanel != null) m_modePanel.switchToAdvanced(exp);
      }
      System.err.println("Opened experiment:\n" + exp);
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, "Couldn't open experiment file:\n"
				    + expFile
				    + "\nReason:\n" + ex.getMessage(),
				    "Open Experiment",
				    JOptionPane.ERROR_MESSAGE);
      // Pop up error dialog
    }
    
  }

  /**
   * Prompts the user for a filename to save the experiment to, then saves
   * the experiment.
   */
  private void saveExperiment() {

    int returnVal = m_FileChooser.showSaveDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File expFile = m_FileChooser.getSelectedFile();
    
    // add extension if necessary
    if (m_FileChooser.getFileFilter() == m_ExpFilter) {
      if (!expFile.getName().toLowerCase().endsWith(Experiment.FILE_EXTENSION))
        expFile = new File(expFile.getParent(), expFile.getName() + Experiment.FILE_EXTENSION);
    }
    else if (m_FileChooser.getFileFilter() == m_KOMLFilter) {
      if (!expFile.getName().toLowerCase().endsWith(KOML.FILE_EXTENSION))
        expFile = new File(expFile.getParent(), expFile.getName() + KOML.FILE_EXTENSION);
    }
    else if (m_FileChooser.getFileFilter() == m_XMLFilter) {
      if (!expFile.getName().toLowerCase().endsWith(".xml"))
        expFile = new File(expFile.getParent(), expFile.getName() + ".xml");
    }
    
    try {
      Experiment.write(expFile.getAbsolutePath(), m_Exp);
      System.err.println("Saved experiment:\n" + m_Exp);
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, "Couldn't save experiment file:\n"
				    + expFile
				    + "\nReason:\n" + ex.getMessage(),
				    "Save Experiment",
				    JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Adds a PropertyChangeListener who will be notified of value changes.
   *
   * @param l a value of type 'PropertyChangeListener'
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    m_Support.addPropertyChangeListener(l);
  }

  /**
   * Removes a PropertyChangeListener.
   *
   * @param l a value of type 'PropertyChangeListener'
   */
  public void removePropertyChangeListener(PropertyChangeListener l) {
    m_Support.removePropertyChangeListener(l);
  }

  /**
   * Responds to a change in the destination type.
   */
 /* @SuppressWarnings("unused")
  private void destinationTypeChanged() {

    if (m_Exp == null) return;

    String str = "";


    //  m_ResultsDestinationPathLabel.setText("Filename:");


	int ind = m_destinationFilename.lastIndexOf(".arff");
	if (ind > -1) {
	  m_destinationFilename = m_destinationFilename.substring(0, ind) + ".csv";
	}
      
      str = m_destinationFilename;


	ind = str.lastIndexOf(".arff");
	if (ind > -1) {
	  str = str.substring(0, ind) + ".csv";
	}
      
      m_BrowseDestinationButton.setEnabled(true);
      m_BrowseDestinationButton.setText("Browse...");
    



	CSVResultListener crl = new CSVResultListener();
	if (!m_destinationFilename.equals("")) {
	  crl.setOutputFile(new File(m_destinationFilename));
	}
	m_Exp.setResultListener(crl);
      
    

    m_ResultsDestinationPathTField.setText(str);

    m_Support.firePropertyChange("", null, null);
  }
*/
  
  /**
   * Responds to a change in the Jar file setting
   */

  /**
   * Responds to a change in the destination address.
   */
  private void destinationAddressChanged() {

    if (m_Exp == null) return;

      File resultsFile = null;
      m_destinationFilename = m_ResultDestinationPath.getCurrent().getAbsolutePath();

      // Use temporary file if no file name is provided
      if (m_destinationFilename.equals("")) {
        resultsFile = TempUtils.createTempFile("weka_experiment", ".csv");
        resultsFile.deleteOnExit();
      } else {
	  if (!m_destinationFilename.endsWith(".csv")) {
	    m_destinationFilename += ".csv";
	  }
	resultsFile = new File(m_destinationFilename);
      }
      ((CSVResultListener)m_Exp.getResultListener()).setOutputFile(resultsFile);
      ((CSVResultListener)m_Exp.getResultListener()).setOutputFileName(m_destinationFilename);

    m_Support.firePropertyChange("", null, null);
  }

  
  /**
   * Responds to a change in the experiment type.
   */
 /* private void expTypeChanged() {
    m_ExpClassificationRBut.setSelected(true);
    m_ExpRegressionRBut.setSelected(false);
    m_expType = "classification";
    SplitEvaluator se = null;
    Classifier sec = null;

      se = new ClassifierSplitEvaluator();
      sec = ((ClassifierSplitEvaluator)se).getClassifier();
    
    // build new ResultProducer
      CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
      cvrp.setNumFolds(m_numFolds);
      cvrp.setSplitEvaluator(se);
      
      PropertyNode[] propertyPath = new PropertyNode[2];
      try {
	propertyPath[0] = new PropertyNode(se, new PropertyDescriptor("splitEvaluator",
								      CrossValidationResultProducer.class),
					   CrossValidationResultProducer.class);
	propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor("classifier",
								       se.getClass()),
					   se.getClass());
      } catch (IntrospectionException e) {
	e.printStackTrace();
      }
      
      m_Exp.setResultProducer(cvrp);
      m_Exp.setPropertyPath(propertyPath);
      
      m_Exp.setUsePropertyIterator(true);
      m_Support.firePropertyChange("", null, null);
  }
  private void expTypeChangedRBut(){
    m_ExpClassificationRBut.setSelected(false);
    m_ExpRegressionRBut.setSelected(true);
    m_expType = "regression";
    SplitEvaluator se = null;
    Classifier sec = null;

    se = new RegressionSplitEvaluator();
    sec = ((RegressionSplitEvaluator)se).getClassifier();
    
    // build new ResultProducer
      CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
      cvrp.setNumFolds(m_numFolds);
      cvrp.setSplitEvaluator(se);
      
      PropertyNode[] propertyPath = new PropertyNode[2];
      try {
	propertyPath[0] = new PropertyNode(se, new PropertyDescriptor("splitEvaluator",
								      CrossValidationResultProducer.class),
					   CrossValidationResultProducer.class);
	propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor("classifier",
								       se.getClass()),
					   se.getClass());
      } catch (IntrospectionException e) {
	e.printStackTrace();
      }
      
      m_Exp.setResultProducer(cvrp);
      m_Exp.setPropertyPath(propertyPath);
      
      m_Exp.setUsePropertyIterator(true);
      m_Support.firePropertyChange("", null, null);
  }
*/
  
  private void expTypeChanged() {

    if (m_Exp == null) return;

   

    SplitEvaluator se = null;
    Classifier sec = null;
    if (m_ExpClassificationRBut.isSelected()) {
      se = new ClassifierSplitEvaluator();
      sec = ((ClassifierSplitEvaluator)se).getClassifier();
      m_expType= "classification";
    } else {
      se = new RegressionSplitEvaluator();
      sec = ((RegressionSplitEvaluator)se).getClassifier();
      m_expType= "regression";
    }
    

      CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
      cvrp.setNumFolds(m_numFolds);
      cvrp.setSplitEvaluator(se);
      
      PropertyNode[] propertyPath = new PropertyNode[2];
      try {
	propertyPath[0] = new PropertyNode(se, new PropertyDescriptor("splitEvaluator",
								      CrossValidationResultProducer.class),
					   CrossValidationResultProducer.class);
	propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor("classifier",
								       se.getClass()),
					   se.getClass());
      } catch (IntrospectionException e) {
	e.printStackTrace();
      }
      
      m_Exp.setResultProducer(cvrp);
      m_Exp.setPropertyPath(propertyPath);


    m_Exp.setUsePropertyIterator(true);
    m_Support.firePropertyChange("", null, null);
  }
  /**
   * Responds to a change in the experiment parameter.
   */
  private void expParamChanged() {

    if (m_Exp == null) return;

    
      try {
	m_numFolds = Integer.parseInt(m_ExperimentParameterTField.getText());
      } catch (NumberFormatException e) {
	return;
      }
    

      if (m_Exp.getResultProducer() instanceof CrossValidationResultProducer) {
	CrossValidationResultProducer cvrp = (CrossValidationResultProducer) m_Exp.getResultProducer();
	cvrp.setNumFolds(m_numFolds);
      } else {
	return;
      }

    m_Support.firePropertyChange("", null, null);
  }

  /**
   * Responds to a change in the number of repetitions.
   */
  private void numRepetitionsChanged() {

    if (m_Exp == null || !m_NumberOfRepetitionsTField.isEnabled()) return;

    try {
      m_numRepetitions = Integer.parseInt(m_NumberOfRepetitionsTField.getText());
    } catch (NumberFormatException e) {
      return;
    }

    m_Exp.setRunLower(1);
    m_Exp.setRunUpper(m_numRepetitions);

    m_Support.firePropertyChange("", null, null);
  }

  /**
   * Lets user enter username/password/URL.
   */
 /* @SuppressWarnings("unused")
  private void chooseURLUsername() {
    String dbaseURL=((DatabaseResultListener)m_Exp.getResultListener()).getDatabaseURL();
    String username=((DatabaseResultListener)m_Exp.getResultListener()).getUsername();
    DatabaseConnectionDialog dbd= new DatabaseConnectionDialog(null,dbaseURL,username);
    dbd.setVisible(true);
      
    //if (dbaseURL == null) {
    if (dbd.getReturnValue()==JOptionPane.CLOSED_OPTION) {
      return;
    }

    ((DatabaseResultListener)m_Exp.getResultListener()).setUsername(dbd.getUsername());
    ((DatabaseResultListener)m_Exp.getResultListener()).setPassword(dbd.getPassword());
    ((DatabaseResultListener)m_Exp.getResultListener()).setDatabaseURL(dbd.getURL());
    ((DatabaseResultListener)m_Exp.getResultListener()).setDebug(dbd.getDebug());
    m_ResultsDestinationPathTField.setText(dbd.getURL());
  }
  */
  
  /**
   * Lets user browse for a destination file..
   */
 /* private void chooseDestinationFile() {

    FileFilter fileFilter = null;
   // if (m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
      fileFilter = m_csvFileFilter;
   // } 
    m_DestFileChooser.setFileFilter(fileFilter);
    int returnVal = m_DestFileChooser.showSaveDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    m_ResultsDestinationPathTField.setText(m_DestFileChooser.getSelectedFile().toString());
  }
  */
  /**
   * Let user browse for the Hadoop folder
   */
 /* private void chooseHadoopHomeFolder(){
    JFileChooser tempChooser = new JFileChooser(new File(System.getProperty("user.dir")));
    tempChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = tempChooser.showSaveDialog(this);
    if(returnVal !=JFileChooser.APPROVE_OPTION){
      return;
    }
  //  m_InputHadoopHomeField.setText(tempChooser.getSelectedFile().toString());
  }
  */
  /**
   * Let user browse for the Jar file
   */
 /* private void chooseHadoopConfFolder(){
    JFileChooser tempChooser = new JFileChooser(new File(System.getProperty("user.dir")));
    tempChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = tempChooser.showSaveDialog(this);
    if(returnVal !=tempChooser.APPROVE_OPTION){
      return;
    }
  //  m_inputHadoopConfField.setText(tempChooser.getSelectedFile().toString());
  }
  */
  public String getHadoopHomePath(){
    return m_InputHadoopHome.getCurrent().getAbsolutePath();
  }
  public String getHadoopConfPath(){
    return m_InputHadoopConf.getCurrent().getAbsolutePath();
  }
  public String getExpType(){
    return m_expType;
  }
}
