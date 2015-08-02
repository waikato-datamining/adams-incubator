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
 * GeoToolsLayerFileChooser.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.ClassLister;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.io.input.AbstractGeoToolsLayerReader;
import adams.data.io.input.ShapeFileReader;
import adams.data.io.output.AbstractGeoToolsLayerWriter;
import adams.data.io.output.ShapeFileWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for GIS layers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GeoToolsLayerFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<AbstractGeoToolsLayerReader, AbstractGeoToolsLayerWriter> {

  /** for serialization. */
  private static final long serialVersionUID = -53374407938356183L;

  /** the file filters for the readers. */
  protected static Hashtable<Class,List<ExtensionFileFilterWithClass>> m_ReaderFileFilters = new Hashtable<Class,List<ExtensionFileFilterWithClass>>();

  /** the file filters for the writers. */
  protected static Hashtable<Class,List<ExtensionFileFilterWithClass>> m_WriterFileFilters = new Hashtable<Class,List<ExtensionFileFilterWithClass>>();

  /**
   * onstructs a FileChooser pointing to the user's default directory.
   */
  public GeoToolsLayerFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public GeoToolsLayerFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public GeoToolsLayerFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * initializes the filters.
   *
   * @param chooser	the chooser instance to use as reference
   * @param reader	if true then the reader filters are initialized
   * @param classnames	the classnames of the converters
   */
  protected static void initFilters(GeoToolsLayerFileChooser chooser, boolean reader, String[] classnames) {
    int				i;
    String 			classname;
    Class 			cls;
    String[] 			ext;
    String 			desc;
    Object		 	converter;
    ExtensionFileFilterWithClass 	filter;

    if (reader)
      m_ReaderFileFilters.put(chooser.getClass(), new ArrayList<ExtensionFileFilterWithClass>());
    else
      m_WriterFileFilters.put(chooser.getClass(), new ArrayList<ExtensionFileFilterWithClass>());

    for (i = 0; i < classnames.length; i++) {
      classname = (String) classnames[i];

      // get data from converter
      try {
	cls       = Class.forName(classname);
	converter = cls.newInstance();
	if (reader) {
	  desc = ((AbstractGeoToolsLayerReader) converter).getFormatDescription();
	  ext  = ((AbstractGeoToolsLayerReader) converter).getFormatExtensions();
	}
	else {
	  desc = ((AbstractGeoToolsLayerWriter) converter).getFormatDescription();
	  ext  = ((AbstractGeoToolsLayerWriter) converter).getFormatExtensions();
	}
      }
      catch (Exception e) {
        handleException("Failed to set up: " + classname, e);
	cls       = null;
	converter = null;
	ext       = new String[0];
	desc      = "";
      }

      if (converter == null)
	continue;

      // reader?
      if (reader) {
	filter = new ExtensionFileFilterWithClass(classname, desc, ext);
	m_ReaderFileFilters.get(chooser.getClass()).add(filter);
      }
      else {
	filter = new ExtensionFileFilterWithClass(classname, desc, ext);
	m_WriterFileFilters.get(chooser.getClass()).add(filter);
      }
    }
  }

  /**
   * Returns the file filters for opening files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getOpenFileFilters() {
    return m_ReaderFileFilters.get(getClass());
  }

  /**
   * Returns the file filters for writing files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getSaveFileFilters() {
    return m_WriterFileFilters.get(getClass());
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected AbstractGeoToolsLayerReader getDefaultReader() {
    return new ShapeFileReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractGeoToolsLayerWriter getDefaultWriter() {
    return new ShapeFileWriter();
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(this, true, ClassLister.getSingleton().getClassnames(AbstractGeoToolsLayerReader.class));
    initFilters(this, false, ClassLister.getSingleton().getClassnames(AbstractGeoToolsLayerWriter.class));
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractGeoToolsLayerReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractGeoToolsLayerWriter.class;
  }

  /**
   * configures the current converter.
   *
   * @param dialogType		the type of dialog to configure for
   */
  @Override
  protected void configureCurrentHandlerHook(int dialogType) {
    PlaceholderFile	selFile;
    String		classname;
    File		currFile;

    selFile = getSelectedPlaceholderFile();

    if (m_CurrentHandler == null) {
      classname = ((ExtensionFileFilterWithClass) getFileFilter()).getClassname();
      try {
	m_CurrentHandler = Class.forName(classname).newInstance();
      }
      catch (Exception e) {
	handleException("Failed to configure current handler:", e);
	m_CurrentHandler = null;
      }

      // none found?
      if (m_CurrentHandler == null)
	return;
    }

    // wrong type?
    if (selFile.isDirectory())
      return;

    try {
      if (m_CurrentHandler instanceof AbstractGeoToolsLayerReader)
	currFile = ((AbstractGeoToolsLayerReader) m_CurrentHandler).getInput();
      else
	currFile = ((AbstractGeoToolsLayerWriter) m_CurrentHandler).getOutput();
      if ((currFile == null) || (!currFile.getAbsolutePath().equals(selFile.getAbsolutePath()))) {
	if (m_CurrentHandler instanceof AbstractGeoToolsLayerReader)
	  ((AbstractGeoToolsLayerReader) m_CurrentHandler).setInput(selFile);
	else
	  ((AbstractGeoToolsLayerWriter) m_CurrentHandler).setOutput(selFile);
      }
    }
    catch (Exception e) {
      handleException("Failed to configure current handler: " + OptionUtils.getCommandLine(m_CurrentHandler), e);
    }
  }

  /**
   * Returns whether the filters have already been initialized.
   *
   * @return		true if the filters have been initialized
   */
  @Override
  protected boolean getFiltersInitialized() {
    return (m_ReaderFileFilters.containsKey(getClass()));
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public AbstractGeoToolsLayerReader getReaderForFile(File file) {
    AbstractGeoToolsLayerReader	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_ReaderFileFilters.values()) {
      for (ExtensionFileFilterWithClass filter: list) {
	if (filter.accept(file)) {
	  try {
	    result = (AbstractGeoToolsLayerReader) Class.forName(filter.getClassname()).newInstance();
	  }
	  catch (Exception e) {
	    handleException("Failed to instantiate reader: " + filter.getClassname(), e);
	  }
	}
      }
    }

    return result;
  }

  /**
   * Returns the writer for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the writer, null if none found
   */
  public AbstractGeoToolsLayerWriter getWriterForFile(File file) {
    AbstractGeoToolsLayerWriter	result;

    result = null;

    for (List<ExtensionFileFilterWithClass> list: m_WriterFileFilters.values()) {
      for (ExtensionFileFilterWithClass filter: list) {
	if (filter.accept(file)) {
	  try {
	    result = (AbstractGeoToolsLayerWriter) Class.forName(filter.getClassname()).newInstance();
	  }
	  catch (Exception e) {
	    handleException("Failed to instantiate writer: " + filter.getClassname(), e);
	  }
	}
      }
    }

    return result;
  }
}
