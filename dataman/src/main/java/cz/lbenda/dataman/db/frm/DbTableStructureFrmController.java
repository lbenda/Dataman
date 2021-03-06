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
package cz.lbenda.dataman.db.frm;

import cz.lbenda.common.Tuple2;
import cz.lbenda.dataman.db.ColumnDesc;
import cz.lbenda.dataman.db.DbStructureFactory;
import cz.lbenda.dataman.db.TableDesc;
import cz.lbenda.dataman.db.dialect.ColumnType;
import cz.lbenda.gui.tableView.SimpleTableView;
import cz.lbenda.rcp.localization.Message;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 11.10.15.
 * Frame controller for view which show detail information about table */
public class DbTableStructureFrmController implements Initializable {

  private static Logger LOG = LoggerFactory.getLogger(DbTableStructureFrmController.class);
  private static final String FXML_RESOURCE = "DbTableStructureFrm.fxml";

  @Message
  public static final String WINDOW_TITLE = "Structure";
  @Message
  public static final String COLUMNS_PANEL_TITLE = "Columns: %s";
  @Message
  public static final String FOREIGN_KEY_PANEL_TITLE = "Foreign keys: %s";


  @FXML
  private Label lTableName;
  @FXML
  private Label lQualifiedName;
  @FXML
  private Label lTableType;
  @FXML
  private TextArea taComment;
  @FXML
  private SimpleTableView<ColumnDesc> tvColumns;
  @FXML
  private TableColumn<ColumnDesc, Boolean> tcColumnsPK;
  @FXML
  private TableColumn<ColumnDesc, String> tcColumnsName;
  @FXML
  private TableColumn<ColumnDesc, String> tcColumnsComment;
  @FXML
  private TableColumn<ColumnDesc, ColumnType> tcColumnsType;
  @FXML
  private TableColumn<ColumnDesc, Boolean> tcColumnsNull;
  @FXML
  private TableColumn<ColumnDesc, Integer> tcColumnsLength;
  @FXML
  private TableColumn<ColumnDesc, Integer> tcColumnsScale;
  @FXML
  private TableColumn<ColumnDesc, String> tcColumnsDefault;
  @FXML
  private TableColumn<ColumnDesc, Boolean> tcColumnsEditable;
  @FXML
  private SimpleTableView<DbStructureFactory.ForeignKey> tvForeignKeys;

  @FXML
  private TableColumn<DbStructureFactory.ForeignKey, String> tcForeignKeyInOut;
  @FXML
  private TableColumn<DbStructureFactory.ForeignKey, String> tcForeignKeyName;
  @FXML
  private TableColumn<DbStructureFactory.ForeignKey, String> tcForeignKeyCatalog;
  @FXML
  private TableColumn<DbStructureFactory.ForeignKey, String> tcForeignKeySchema;
  @FXML
  private TableColumn<DbStructureFactory.ForeignKey, String> tcForeignKeyTable;
  @FXML
  private TableColumn<DbStructureFactory.ForeignKey, String> tcForeignKeyPKColumns;
  @FXML
  private TableColumn<DbStructureFactory.ForeignKey, String> tcForeignKeyFKColumns;
  @FXML
  private TableColumn<DbStructureFactory.ForeignKey, String> tcForeignKeyUpdate;
  @FXML
  private TableColumn<DbStructureFactory.ForeignKey, String> tcForeignKeyDelete;

  @FXML
  private TitledPane tpColumns;
  @FXML
  private TitledPane tpForeignKeys;

  private ObjectProperty<TableDesc> tableDescProperty = new SimpleObjectProperty<>();

  public DbTableStructureFrmController() {
    tableDescProperty.addListener((observable, oldValue, newValue) -> {
      generalSet(newValue);
      columnsSet(newValue);
      foreignKeysSet(newValue);
    });
  }

  private TableDesc foreignTable(DbStructureFactory.ForeignKey foreignKey) {
    return foreignKey.getMasterTable() == getTableDesc() ? foreignKey.getSlaveTable() : foreignKey.getMasterTable();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    tcColumnsPK.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isPK()));
    tcColumnsName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
    tcColumnsComment.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLabel()));
    tcColumnsType.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDataType()));
    tcColumnsNull.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isNullable()));
    tcColumnsLength.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getSize()));
    tcColumnsScale.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getScale()));
    tcColumnsDefault.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDefaultValue()));
    tcColumnsEditable.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isEditable()));

    tcForeignKeyInOut.setCellValueFactory(cell -> new SimpleStringProperty(
        cell.getValue().getMasterTable() == getTableDesc() ? "Out" : "In"
    ));
    tcForeignKeyName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
    tcForeignKeyCatalog.setCellValueFactory(cell -> new SimpleStringProperty(
        foreignTable(cell.getValue()).getSchema().getCatalog().getName()));
    tcForeignKeySchema.setCellValueFactory(cell -> new SimpleStringProperty(
        foreignTable(cell.getValue()).getSchema().getName()));
    tcForeignKeyTable.setCellValueFactory(cell -> new SimpleStringProperty(
        foreignTable(cell.getValue()).getName()));
    tcForeignKeyPKColumns.setCellValueFactory(cell -> new SimpleStringProperty(
        cell.getValue().getMasterColumn().getName()));
    tcForeignKeyFKColumns.setCellValueFactory(cell -> new SimpleStringProperty(
        cell.getValue().getSlaveColumn().getName()));
    tcForeignKeyUpdate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUpdateRule()));
    tcForeignKeyDelete.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDeleteRule()));

    generalSet(null);
    columnsSet(null);
    foreignKeysSet(null);
  }

  /** Set values from table description to general part */
  private void generalSet(TableDesc tableDesc) {
    if (tableDesc == null) {
      lTableName.setText("");
      lQualifiedName.setText("");
      lTableType.setText("");
      taComment.setText("");
    } else {
      lTableName.setText(tableDesc.getName());
      lQualifiedName.setText(String.format("\"%s\".\"%s\".\"%s\"",
          tableDesc.getSchema().getCatalog().getName(),
          tableDesc.getSchema().getName(),
          tableDesc.getName()));
      lTableType.setText(tableDesc.getTableType().name());
      taComment.setText(tableDesc.getName());
    }
  }

  /** Set columns for columns part */
  private void columnsSet(TableDesc tableDesc) {
    tvColumns.getRows().clear();
    if (tableDesc != null) { tvColumns.getRows().addAll(tableDesc.getColumns()); }
    tpColumns.setText(String.format(COLUMNS_PANEL_TITLE, tvColumns.getRows().size()));
  }

  /** Set columns for foreign keys part */
  private void foreignKeysSet(TableDesc tableDesc) {
    tvForeignKeys.getRows().clear();
    if (tableDesc != null) { tvForeignKeys.getRows().addAll(tableDesc.getForeignKeys()); }
    tpForeignKeys.setText(String.format(FOREIGN_KEY_PANEL_TITLE, tvForeignKeys.getRows().size()));
  }

  /** Set table description which is currently showed */
  public void setTableDesc(TableDesc tableDesc) {
    tableDescProperty.setValue(tableDesc);
  }

  @SuppressWarnings("unused")
  public TableDesc getTableDesc() { return tableDescProperty.get(); }
  @SuppressWarnings("unused")
  public ObjectProperty<TableDesc> tableDescProperty() { return tableDescProperty; }

  /** Create new instance return main node and controller of this node and sub-nodes */
  public static Tuple2<Parent, DbTableStructureFrmController> createNewInstance() {
    URL resource = DbTableStructureFrmController.class.getResource(FXML_RESOURCE);
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(resource);
      loader.setBuilderFactory(new JavaFXBuilderFactory());
      Parent node = loader.load(resource.openStream());
      DbTableStructureFrmController controller = loader.getController();
      return new Tuple2<>(node, controller);
    } catch (IOException e) {
      LOG.error("Problem with reading FXML", e);
      throw new RuntimeException("Problem with reading FXML", e);
    }
  }
}
