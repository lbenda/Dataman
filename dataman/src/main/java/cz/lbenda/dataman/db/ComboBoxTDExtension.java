/*
 * Copyright 2014 Lukas Benda <lbenda at lbenda.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.lbenda.dataman.db;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import cz.lbenda.common.AbstractHelper;
import cz.lbenda.dataman.schema.exconf.ComboBoxType;
import cz.lbenda.dataman.schema.exconf.ObjectFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** When the table is show then on defined column is select box
 * Created by Lukas Benda <lbenda @ lbenda.cz> on 9/16/14.
 */
public class ComboBoxTDExtension implements TableDescriptionExtension {

  private static final Logger LOG = LoggerFactory.getLogger(ComboBoxTDExtension.class);

  private final TableDesc td;
  @SuppressWarnings("unused")
  public final TableDesc getTableDescription() { return td; }
  /** Name of column which is substitution by select box */
  private String columnName;
  @SuppressWarnings("unused")
  public final String getColumnName() { return columnName; }
  @SuppressWarnings("unused")
  public final void setColumnName(final String columnName) { this.columnName = columnName; }
  /** SQL which return three fields with identifier which is write to column, showed value and tool tipe. */
  private String tableOfKeySQL;
  @SuppressWarnings("unused")
  public final String getTableOfKeySQL() { return tableOfKeySQL; }
  @SuppressWarnings("unused")
  public final void setTableOfKeySQL(final String tableOfKeySQL) { this.tableOfKeySQL = tableOfKeySQL; }

  private String columnValue;
  @SuppressWarnings("unused")
  public final String getColumnValue() { return columnValue; }
  public final void setColumnValue(final String columnValue) { this.columnValue = columnValue; }
  private String columnChoice;
  @SuppressWarnings("unused")
  public final String getColumnChoice () { return columnChoice; }
  public final void setColumnChoice(final String columnChoice) { this.columnChoice = columnChoice; }
  private String columnTooltip;
  @SuppressWarnings("unused")
  public final String getColumnTooltip() { return columnTooltip; }
  public final void setColumnTooltip(final String columnTooltip) { this.columnTooltip = columnTooltip; }

  /** Flag which inform if combo box have already downloaded data */
  private boolean initialized = false;

  /** List of items which will be show in combo box */
  private final ObservableList<ComboBoxItem> items = FXCollections.observableArrayList();
  public synchronized final ObservableList<ComboBoxItem> getItems() {
    if (!initialized) {
      try {
        loadSelectBoxData();
      } catch (SQLException e) {
        LOG.error("Loading data of combo box failed: " + this, e);
        throw new RuntimeException("Loading data of combo box failed: " + this, e);
      }
    }
    return items;
  }

  public ComboBoxTDExtension(TableDesc td, String columnName, String tableOfKeySQL) {
    this.td = td;
    this.columnName = columnName;
    this.tableOfKeySQL = tableOfKeySQL;
  }

  /** Return the extended column */
  public ColumnDesc getColumn() {
    return this.td.getColumn(this.columnName);
  }

  @Override
  public final List<ColumnDesc> getColumns() {
    return Collections.singletonList(getColumn());
  }

  /** This method read data of selectbox
   * @throws java.sql.SQLException exception when data from conneciton is readed
   */
  public synchronized final void loadSelectBoxData() throws SQLException {
    initialized = true;
    LOG.trace("load data to select box");
    final List<Object[]> rows;
    String sql = td.getDbConfig().getExtConfFactory().getTableOfKeysSQL().get(this.tableOfKeySQL);
    if (columnValue == null || columnChoice == null || columnTooltip == null) {
      LOG.info("The name of column isn't defined, so the position of column 1, 2 and 3 is used.");
      rows = td.getDbConfig().getReader().getSQLRows(sql, 1, 2, 3);
    } else {
      rows = td.getDbConfig().getReader().getSQLRows(sql, columnValue, columnChoice, columnTooltip);
    }
    items.clear();
    items.add(ComboBoxItem.EMPTY);
    for (Object[] row : rows) {
      items.add(new ComboBoxItem(row[0], (String) row[1], (String) row[2]));
    }
  }
/*
  @Override
  public void tableWasChanged(TableDesc td, TableAction action) {
    switch (action) {
      case UPDATE :
      case DELETE :
      case INSERT :
        try {
          loadSelectBoxData();
        } catch (SQLException e) {
          LOG.error(String.format("The selctbox extension '%s.%s.%s' wasn't reloaded.",
              td.getSchema(), td.getName(), this.columnName), e);
        } break;
    }
  }
  */

  @SuppressWarnings("unused")
  public final ComboBoxType storeToComboBox() {
    ObjectFactory of = new ObjectFactory();
    ComboBoxType result = of.createComboBoxType();
    result.setColumn(columnName);
    result.setTableOfKeySQL(tableOfKeySQL);
    result.setColumnValue(columnValue);
    result.setColumnChoice(columnChoice);
    result.setColumnTooltip(columnTooltip);
    return result;
  }

  public final ComboBoxItem itemForValue(Object value) {
    for (ComboBoxItem item : getItems()) {
      if (AbstractHelper.nullEquals(item.getValue(), value)) { return item; }
    }
    return null;
  }

  public final ComboBoxItem itemForChoice(String choice) {
    for (ComboBoxItem item : getItems()) {
      if (AbstractHelper.nullEquals(item.getChoice(), choice)) { return item; }
    }
    return null;
  }

  @Override
  public String toString() {
    return "ComboBox: " + columnName + ", " + tableOfKeySQL;
  }

  public static class ComboBoxItem {

    public static final ComboBoxItem EMPTY = new ComboBoxItem(null, "", "");

    private final Object value; public final Object getValue() { return value; }
    private final String choice; public final String getChoice() { return choice; }
    private final String tooltip; public final String getTooltip() { return tooltip; }

    public ComboBoxItem(Object value, String choice, String tooltip) {
      this.value = value;
      this.choice = choice;
      this.tooltip = tooltip;
    }

    @Override
    public String toString() {
      return choice;
    }
  }
}
