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
package cz.lbenda.dataman.db.handler;

import cz.lbenda.dataman.Constants;
import cz.lbenda.dataman.rc.DbConfigFactory;
import cz.lbenda.rcp.DialogHelper;
import cz.lbenda.rcp.action.AbstractAction;
import cz.lbenda.rcp.action.ActionConfig;
import cz.lbenda.rcp.action.ActionGUIConfig;
import cz.lbenda.rcp.localization.Message;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;

import java.io.File;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 11.9.15.
 * import db from file */
@ActionConfig(
    category = "/DbConfig/exportImport",
    id = "cz.lbenda.dataman.db.handler.ImportDatabaseHandler",
    priority = 990,
    gui = @ActionGUIConfig(
        iconBase = "database-import.png",
        displayName = @Message(id="Import_database", msg="Import"),
        displayTooltip = @Message(id="Import_database_tooltip", msg="Import database configuration from file")
    )
)
public class ImportDatabaseHandler extends AbstractAction {

  @Message
  public static final String msgFileChooseTitle = "Choose file from which will be configuration loaded";

  @Override
  public void handle(ActionEvent e) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(msgFileChooseTitle);
    Node node = (Node) e.getSource();
    fileChooser.getExtensionFilters().addAll(Constants.configFileFilter);
    File file = fileChooser.showOpenDialog(node.getScene().getWindow());
    if (file != null) {
      if (!file.exists()) { DialogHelper.getInstance().fileNotExist(file); }
      else { DbConfigFactory.importConfig(file); }
    }
  }
}
