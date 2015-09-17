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
import cz.lbenda.dataman.Constants;
import cz.lbenda.dataman.rc.DbConfigFactory;
import cz.lbenda.dataman.rc.DbConfig;
import cz.lbenda.dataman.schema.dataman.ExtendedConfigTypeType;
import cz.lbenda.rcp.localization.Message;
import cz.lbenda.rcp.localization.MessageFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 11.9.15.
 * Controller for frame which is use for configure session */
public class DbConfigFrmController implements Initializable {

  private static Logger LOG = LoggerFactory.getLogger(DbConfigFrmController.class);
  private static final String FXML_RESOURCE = "DbConfigFrm.fxml";

  @Message
  private String msgDialogTitle = "Configure session";
  @Message
  private String msgDialogHeader = "Choose libraries in which is driver which is used for connect to database.";
  @Message
  private String msgLibraryChooseTitle = "Choose libraries";
  @Message
  private String msgExtendConfigChooseTitle = "Choose path to extended configuration";

  @FXML
  private TabPane mainPane;
  @FXML
  private Button btnRemoveLibrary;
  @FXML
  private Button btnAddLibrary;
  @FXML
  private Button btnExtendConfigFindPath;
  @FXML
  private TextField tfName;
  @FXML
  private TextField tfUrl;
  @FXML
  private TextField tfDriverClass;
  @FXML
  private TextField tfUsername;
  @FXML
  private PasswordField pfPassword;
  @FXML
  private TextField tfTimeout;
  @FXML
  private ListView<String> lvLibraries;
  @FXML
  private TextField tfExtendConfigPath;
  @FXML
  private ComboBox<ExtendedConfigTypeType> cbExtendConfigType;

  public void laodDataFromSessionConfiguration(DbConfig dbConfig) {
    tfName.setText(StringUtils.defaultString(dbConfig.getId()));
    tfUrl.setText(StringUtils.defaultString(dbConfig.getJdbcConfiguration().getUrl()));
    tfDriverClass.setText(StringUtils.defaultString(dbConfig.getJdbcConfiguration().getDriverClass()));
    tfUsername.setText(StringUtils.defaultString(dbConfig.getJdbcConfiguration().getUsername()));
    pfPassword.setText(StringUtils.defaultString(dbConfig.getJdbcConfiguration().getPassword()));
    if (dbConfig.getConnectionTimeout() < 0) {
      tfTimeout.setText(dbConfig.getConnectionTimeout() < 0 ? "" : Integer.toString(dbConfig.getConnectionTimeout()));
    }
    lvLibraries.getItems().clear();
    lvLibraries.getItems().addAll(dbConfig.getLibrariesPaths());

    tfExtendConfigPath.setText(StringUtils.defaultString(dbConfig.getExtendedConfigurationPath()));
    cbExtendConfigType.getSelectionModel().select(dbConfig.getExtendedConfigType() == null ?
        ExtendedConfigTypeType.NONE : dbConfig.getExtendedConfigType());
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    MessageFactory.initializeMessages(this);
    for (ExtendedConfigTypeType ectt : ExtendedConfigTypeType.values()) {
      cbExtendConfigType.getItems().add(ectt);
    }
    cbExtendConfigType.getSelectionModel().select(ExtendedConfigTypeType.NONE);
    lvLibraries.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    btnAddLibrary.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle(msgLibraryChooseTitle);
      fileChooser.getExtensionFilters().addAll(Constants.librariesFilter);
      List<File> files = fileChooser.showOpenMultipleDialog(btnAddLibrary.getScene().getWindow());
      if (files != null) { files.forEach(file -> lvLibraries.getItems().add(file.getAbsolutePath())); }
    });
    btnRemoveLibrary.setOnAction(event -> lvLibraries.getItems().removeAll(lvLibraries.getSelectionModel().getSelectedItems()));
    btnExtendConfigFindPath.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle(msgExtendConfigChooseTitle);
      fileChooser.getExtensionFilters().addAll(Constants.configFileFilter);
      if (StringUtils.isEmpty(tfExtendConfigPath.getText())) { fileChooser.setInitialFileName(tfExtendConfigPath.getText()); }
      File file = fileChooser.showOpenDialog(btnExtendConfigFindPath.getScene().getWindow());
      if (file != null) { tfExtendConfigPath.setText(file.getAbsolutePath()); }
    });
  }

  public void storeDataToSessionConfiguration(DbConfig sc) {
    sc.setId(tfName.getText());
    sc.getJdbcConfiguration().setUrl(tfUrl.getText());
    sc.getJdbcConfiguration().setDriverClass(tfDriverClass.getText());
    sc.getJdbcConfiguration().setUsername(tfUsername.getText());
    sc.getJdbcConfiguration().setPassword(pfPassword.getText());
    if (StringUtils.isBlank(tfTimeout.getText())) { sc.setConnectionTimeout(-1); }
    else { sc.setConnectionTimeout(Integer.parseInt(tfTimeout.getText())); }

    sc.getLibrariesPaths().clear();
    sc.getLibrariesPaths().addAll(lvLibraries.getItems());

    sc.setExtendedConfigType(cbExtendConfigType.getSelectionModel().getSelectedItem());
    sc.setExtendedConfigurationPath(tfExtendConfigPath.getText());
  }

  /** Create new instance return main node and controller of this node and subnodes */
  public static Tuple2<Parent, DbConfigFrmController> createNewInstance() {
    URL resource = DbConfigFrmController.class.getResource(FXML_RESOURCE);
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(resource);
      loader.setBuilderFactory(new JavaFXBuilderFactory());
      Parent node = loader.load(resource.openStream());
      DbConfigFrmController controller = loader.getController();
      return new Tuple2<>(node, controller);
    } catch (IOException e) {
      LOG.error("Problem with reading FXML", e);
      throw new RuntimeException("Problem with reading FXML", e);
    }
  }

  public static DbConfig openDialog(final DbConfig sc) {
    Dialog<DbConfig> dialog = new Dialog<>();
    dialog.setResizable(false);
    final Tuple2<Parent, DbConfigFrmController> tuple = createNewInstance();
    if (sc != null) { tuple.get2().laodDataFromSessionConfiguration(sc); }
    dialog.setTitle(tuple.get2().msgDialogTitle);
    dialog.setHeaderText(tuple.get2().msgDialogHeader);

    dialog.getDialogPane().setContent(tuple.get1());
    ButtonType buttonTypeOk = ButtonType.OK;
    ButtonType buttonTypeCancel = ButtonType.CANCEL;
    dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
    dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
    dialog.getDialogPane().setPadding(new Insets(0, 0, 0, 0));

    dialog.setResultConverter(b -> {
      if (b == buttonTypeOk) {
        DbConfig sc1 = sc;
        if (sc1 == null) {
          sc1 = new DbConfig();
          tuple.get2().storeDataToSessionConfiguration(sc1);
          DbConfigFactory.getConfigurations().add(sc1);
        } else {
          tuple.get2().storeDataToSessionConfiguration(sc1);
          int idx = DbConfigFactory.getConfigurations().indexOf(sc1);
          DbConfigFactory.getConfigurations().remove(sc1);
          DbConfigFactory.getConfigurations().add(idx, sc1);
        }
        DbConfigFactory.saveConfiguration();
        return sc1;
      }
      return null;
    });

    Optional<DbConfig> result = dialog.showAndWait();
    if (result.isPresent()) { return result.get(); }
    return null;
  }
}