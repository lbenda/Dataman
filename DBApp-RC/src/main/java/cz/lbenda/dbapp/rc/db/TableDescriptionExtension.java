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
package cz.lbenda.dbapp.rc.db;

import org.jdom.Element;

/** General interface for all table description extension.
 * Created by Lukas Benda <lbenda @ lbenda.cz> on 9/16/14.
 */
public interface TableDescriptionExtension {

  public enum TableAction {
    SELECT, UPDATE, DELETE, INSERT, ;
  }

  /** Return table description which this class extends */
  TableDescription getTableDescription();

  /** Inform the extension when table was changed
   * @param td table which is affected
   * @param action action which was executed at the table
   */
  void tableWasChanged(TableDescription td, TableAction action);

  /** Return element to which is this extension stored */
  Element storeToElement();
}