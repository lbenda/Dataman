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
package cz.lbenda.dataman.db.sql;

import cz.lbenda.dataman.Constants;
import cz.lbenda.rcp.action.AbstractAction;
import cz.lbenda.rcp.action.ActionConfig;
import cz.lbenda.rcp.action.ActionGUIConfig;
import cz.lbenda.rcp.localization.Message;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;

import java.io.File;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 11.9.15. */
@ActionConfig(
    category = "/SQL/Files",
    id = "cz.lbenda.dataman.db.sql.OpenFileHandler",
    priority = 100,
    gui = @ActionGUIConfig(
        displayName = @Message(id = "OpenFile", msg = "Open"),
        displayTooltip = @Message(id="OpenFile_tooltip", msg="Open file with SQL commands"),
        iconBase = "document-open.png"
    )
)
public class OpenFileHandler extends AbstractAction {

  @Message
  public static final String msgDialogTitle = "Choose SQL file";

  public SQLEditorController sqlEditorController;

  public OpenFileHandler(SQLEditorController sqlEditorController) {
    this.sqlEditorController = sqlEditorController;
  }

  @Override
  public void handle(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(msgDialogTitle);
    fileChooser.getExtensionFilters().addAll(Constants.sqlFilter);
    if (sqlEditorController.lastFile() != null) {
      fileChooser.setInitialDirectory(sqlEditorController.lastFile().getParentFile());
      fileChooser.setInitialFileName(sqlEditorController.lastFile().getAbsolutePath());
    }
    File file = fileChooser.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());
    if (file != null) { sqlEditorController.loadFromFile(file); }
  }
}
