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

import cz.lbenda.dataman.db.frm.DbConfigFrmController;
import cz.lbenda.dataman.db.DbConfig;
import cz.lbenda.rcp.action.AbstractAction;
import cz.lbenda.rcp.action.ActionConfig;
import cz.lbenda.rcp.action.ActionGUIConfig;
import cz.lbenda.rcp.localization.Message;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 11.9.15.
 * Action which edit database */
@ActionConfig(
    category = "/DbConfig/sessions",
    id = "cz.lbenda.dataman.db.handler.EditDatabaseHandler",
    priority = 200,
    gui = @ActionGUIConfig(
        iconBase = "database-edit.png",
        displayName = @Message(id="Edit_database", msg="Edit"),
        displayTooltip = @Message(id="Edit_database_tooltip", msg="Edit database config")
    )
)
public class EditDatabaseHandler extends AbstractAction {

  private static Logger LOG = LoggerFactory.getLogger(EditDatabaseHandler.class);

  /** The holder to which is set session configuration values */
  private ObjectProperty<DbConfig> dbConfigObserver;

  public EditDatabaseHandler(ObjectProperty<DbConfig> dbConfigObserver) {
    this.dbConfigObserver = dbConfigObserver;
    setEnable(dbConfigObserver.getValue() != null);
    dbConfigObserver.addListener(observable -> {
      setEnable(dbConfigObserver.getValue() != null);
    });
  }

  @Override
  public void handle(ActionEvent e) {
    DbConfig sc = DbConfigFrmController.openDialog(dbConfigObserver.get());
    if (sc != null) {
      dbConfigObserver.setValue(null);
      dbConfigObserver.setValue(sc);
    }
  }
}