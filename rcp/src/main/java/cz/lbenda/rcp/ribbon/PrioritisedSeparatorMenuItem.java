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
package cz.lbenda.rcp.ribbon;

import javafx.scene.control.SeparatorMenuItem;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 28.9.15. */
public class PrioritisedSeparatorMenuItem extends SeparatorMenuItem implements Prioritised {
  private Integer priority;
  @Override public Integer getPriority() { return priority; }
  public void setPriority(Integer priority) { this.priority = priority; }

  public PrioritisedSeparatorMenuItem() { super(); }
}
