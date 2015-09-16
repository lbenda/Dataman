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
package cz.lbenda.rcp;

import cz.lbenda.rcp.localization.Message;
import cz.lbenda.rcp.localization.MessageFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.util.Optional;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 13.9.15.
 * Class which help with creating dialog */
public class DialogHelper {

  private static DialogHelper instance;
  public static DialogHelper getInstance() {
    if (instance == null) { instance = new DialogHelper(); }
    return instance;
  }

  @Message
  public String msgCanOverwriteTitle = "File overwrite";
  @Message
  public String msgCanOverwriteContent = "The file '%s' already exist, should be overwrite? ";
  @Message
  public String msgFileNotExistTitle = "File not exist";
  @Message
  public String msgFileNotExistHeader = "The file '%s' not exist.";
  @Message
  public String msgFileNotExistContent = "For importing configuration from file, the file must exist.";

  private DialogHelper() {
    MessageFactory.initializeMessages(this);
  }

  /** Ask user if file can be overwrite if file exist */
  public boolean canBeOverwriteDialog(File file) {
    if (file == null) { return false; }
    if (!file.exists()) { return true; }
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(msgCanOverwriteTitle);
    alert.setContentText(String.format(msgCanOverwriteContent, file.getName()));
    Optional<ButtonType> result = alert.showAndWait();
    return (result.isPresent()) && (result.get() == ButtonType.OK);
  }

  /** Inform user about not existing file */
  public void fileNotExist(File file) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(msgFileNotExistTitle);
    alert.setHeaderText(String.format(msgFileNotExistHeader, file.getName()));
    alert.setContentText(msgFileNotExistContent);
    alert.show();
  }
}
