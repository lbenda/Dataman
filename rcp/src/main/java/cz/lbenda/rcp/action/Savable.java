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
package cz.lbenda.rcp.action;

import javafx.beans.property.BooleanProperty;

import javax.annotation.Nonnull;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 11.9.15.
 * Interface for object which can be saved */
public interface Savable {

  /** Invoke the save operation. */
  void save();

  /** Inform if the object is dirty */
  @Nonnull BooleanProperty dirtyProperty();

  /** String which represent savable object in list, of un-savable objects */
  @Nonnull String displayName();
}
