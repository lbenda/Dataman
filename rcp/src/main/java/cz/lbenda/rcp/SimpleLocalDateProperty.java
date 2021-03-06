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

import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 13.9.15. */
@SuppressWarnings("unused")
public class SimpleLocalDateProperty extends SimpleObjectProperty<LocalDate> {

  public SimpleLocalDateProperty() { super(); }
  public SimpleLocalDateProperty(LocalDate var1) { super(var1); }
  public SimpleLocalDateProperty(java.sql.Date var1) {
    super(var1 == null ? null : var1.toLocalDate());
  }
  public SimpleLocalDateProperty(java.util.Date var1) {
    super(var1 == null ? null : var1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
  }
  public SimpleLocalDateProperty(Object var1, String var2) { super(var1, var2); }
  public SimpleLocalDateProperty(Object var1, String var2, LocalDate var3) { super(var1, var2, var3); }

  public void setDate(java.sql.Date date) {
    setValue(date == null ? null : date.toLocalDate());
  }
  public void setDate(java.util.Date date) {
    setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
  }
  public java.util.Date getDate() {
    return Date.from(getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
  }
  public java.sql.Date getSQLDate() {
    return java.sql.Date.valueOf(getValue());
  }
}
