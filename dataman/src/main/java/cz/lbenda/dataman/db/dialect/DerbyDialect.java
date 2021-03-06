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
package cz.lbenda.dataman.db.dialect;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 5.10.15. */
@SuppressWarnings("unused")
public class DerbyDialect implements SQLDialect {

  @Override
  public boolean isForDriver(String driver) {
    return driver != null && driver.startsWith("org.apache.derby");
  }

  @Override
  public byte incrementFrom() { return 0; }
  @Override
  public boolean isIdentityEditable() { return false; }
}
