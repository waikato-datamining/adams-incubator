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
 * OpenMLDatasetContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import adams.data.spreadsheet.SpreadSheet;
import adams.ml.data.Dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for downloaded OpenML datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenMLDatasetContainer
  extends AbstractContainer {

  private static final long serialVersionUID = 2166471546774500592L;

  /** the value for the dataset. */
  public final static String VALUE_DATASET = "Dataset";

  /** the value for the meta-data. */
  public final static String VALUE_METADATA = "Meta-data";

  /**
   * Initializes the container with no dataset and no meta-data.
   * <br><br>
   * Only used for generating help information.
   */
  public OpenMLDatasetContainer() {
    this(null, null);
  }

  /**
   * Initializes the container with the specified plot name and no X value.
   *
   * @param dataset	the dataset
   * @param metadata	the meta-data
   */
  public OpenMLDatasetContainer(Dataset dataset, SpreadSheet metadata) {
    super();

    store(VALUE_DATASET,  dataset);
    store(VALUE_METADATA, metadata);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_DATASET, "dataset; " + Dataset.class.getName());
    addHelp(VALUE_METADATA, "meta-data; " + SpreadSheet.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<>();

    result.add(VALUE_DATASET);
    result.add(VALUE_METADATA);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_DATASET);
  }
}
