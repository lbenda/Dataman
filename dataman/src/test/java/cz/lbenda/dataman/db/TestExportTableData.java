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
package cz.lbenda.dataman.db;

import cz.lbenda.dataman.Constants;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Types;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 20.9.15. */
public class TestExportTableData {

  @Test
  public void saveToXLSX() {
    TableDesc tableDesc = new TableDesc("catalog", "scheme", "TABLE", "table");
    ColumnDesc cd1 = new ColumnDesc(tableDesc, "id", null, Types.INTEGER, 10, false, true, true);
    cd1.setPosition(0);
    ColumnDesc cd2 = new ColumnDesc(tableDesc, "column2", null, Types.VARCHAR, 250, true, false, false);
    cd2.setPosition(1);
    ColumnDesc cd3 = new ColumnDesc(tableDesc, "column3", null, Types.DATE, 12, true, false, false);
    cd3.setPosition(2);
    ColumnDesc cd4 = new ColumnDesc(tableDesc, "column4", null, Types.BOOLEAN, 1, true, false, false);
    cd4.setPosition(3);
    tableDesc.getQueryRow().getMetaData().getColumns().addAll(cd1, cd2, cd3, cd4);

    RowDesc row1 = tableDesc.addNewRowAction();
    row1.setColumnValue(cd1, 0);
    row1.setColumnValue(cd2, "varchar column 1");
    row1.setColumnValue(cd3, new Date((new java.util.Date()).getTime()));
    row1.setColumnValue(cd4, Boolean.TRUE);

    row1 = tableDesc.addNewRowAction();
    row1.setColumnValue(cd1, 1);
    row1.setColumnValue(cd2, "");
    row1.setColumnValue(cd3, new Date((new java.util.Date()).getTime()));
    row1.setColumnValue(cd4, Boolean.FALSE);

    row1 = tableDesc.addNewRowAction();
    row1.setColumnValue(cd1, 2);
    row1.setColumnValue(cd2, null);
    row1.setColumnValue(cd3, null);
    row1.setColumnValue(cd4, null);


    try {
      File file = File.createTempFile("TestExportTableData", ".XLSX");
      FileOutputStream fos = new FileOutputStream(file);
      ExportTableData.writeSqlQueryRowsToXLSX(tableDesc.getQueryRow(), Constants.EXPORT_SHEET_NAME, fos);
      fos.flush();
      fos.close();
    } catch (IOException e) {
      Assert.fail(e.toString());
    }
  }
}