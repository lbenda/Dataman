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

import cz.lbenda.common.Tuple3;
import cz.lbenda.dataman.UserImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 19.9.15.
 * Prepare database for testing */
public class TestHelperPrepareDB {

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(TestHelperPrepareDB.class);

  public enum DBDriver {
    H2("org.h2.Driver", "testDbH2.sql"),
    HSQL("org.hsqldb.jdbcDriver", "testDbHSQL.sql"),
    DERBY("org.apache.derby.jdbc.EmbeddedDriver", "testDbDerby.sql"),
    SQLITE("org.sqlite.JDBC", "testDbSqlite.sql") ;
    private String driver;
    private String testScript;
    DBDriver(String driver, String testScript) {
      this.driver = driver;
      this.testScript = testScript;
    }
    public String getDriver() { return driver; }
    public String getTestScript() { return testScript; }
  }

  public static String USERNAME = "SA";
  public static String PASSWORD = "SA";

  public static Map<DBDriver, Map<String, String>> rowReplacements = new HashMap<>();
  static {
    Map<String, String> sqlMap = new HashMap<>();
    sqlMap.put("\"test\".", "");
    rowReplacements.put(DBDriver.SQLITE, sqlMap);
  }

  public static DbConfig createConfig(DBDriver driverClass, String url) {
    DbConfig config = new DbConfig();
    config.getJdbcConfiguration().setDriverClass(driverClass.getDriver());
    config.getJdbcConfiguration().setUrl(url);
    config.getJdbcConfiguration().setUsername(USERNAME);
    config.getJdbcConfiguration().setPassword(PASSWORD);
    config.setReader(new DbStructureFactory(config));
    config.getConnectionProvider().setUser(new UserImpl(USERNAME));
    return config;
  }

  public static List<Tuple3<DBDriver, String, String>> databases() {
    //noinspection ArraysAsListWithZeroOrOneArgument
    return Arrays.asList(
        new Tuple3<>(DBDriver.HSQL, "jdbc:hsqldb:mem:smallTest", "PUBLIC"),
        new Tuple3<>(DBDriver.H2, "jdbc:h2:mem:smallTest;DB_CLOSE_DELAY=-1", "SMALLTEST"),
        new Tuple3<>(DBDriver.DERBY, "jdbc:derby:memory:smallTest;create=true", "" )
    );
  }

  public static Object[][] databasesDataProvider() {
    Object[][] result = new Object[TestHelperPrepareDB.databases().size()][];
    int i = 0;
    for (Tuple3<TestHelperPrepareDB.DBDriver, String, String> tuple3 : TestHelperPrepareDB.databases()) {
      result[i] = new Object[] { tuple3.get1(), tuple3.get2(), tuple3.get3() };
      i++;
    }
    return result;
  }

  public static String[][] SQL_COMMANDS = new String[][] {
      new String[] { "H2", "HSQL", "DERBY", "create schema \"test\"" },
      new String[] { "H2", "HSQL", "SQLITE", "create table \"test\".table1 ( id integer IDENTITY, col varchar(20))" },
      new String[] { "DERBY", "create table \"test\".table1 ( ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), col varchar(20), PRIMARY KEY(ID))" },
      new String[] { "create table \"test\".table2 ( id varchar(20) NOT NULL, col varchar(20), table1_id integer, PRIMARY KEY(ID), FOREIGN KEY (table1_id) REFERENCES \"test\".table1(ID))" },
      new String[] { "create table \"test\".table3 ( id1 varchar(20) NOT NULL, id2 integer, col varchar(20), PRIMARY KEY(id1, id2)) "},
      new String[] { "insert into \"test\".table1 (col) values('value1')" },
      new String[] { "insert into \"test\".table1 (col) values('value2')" },
      new String[] { "insert into \"test\".table1 (col) values('value3')" },
      new String[] { "insert into \"test\".table2 (id, col, table1_id) values('table2_id1', 'table2_value1', 1)" },
      new String[] { "insert into \"test\".table2 (id, col, table1_id) values('table2_id2', 'table2_value2', 1)" },
      new String[] { "insert into \"test\".table2 (id, col, table1_id) values('table2_id3', 'table2_value3', 2)" },
      new String[] { "insert into \"test\".table3 (id1, id2, col) values('table3_id1', 0, 'table3_value1')" },
      new String[] { "insert into \"test\".table3 (id1, id2, col) values('table3_id2', 1, 'table3_value2')" },
      new String[] { "insert into \"test\".table3 (id1, id2, col) values('table3_id1', 1, 'table3_value3')" },
      new String[] { "insert into \"test\".table3 (id1, id2, col) values('table3_id2', 0, 'table3_value4')" }
  };

  public static Connection getConnection(DBDriver driverClass , String url) {
    try {
      Class.forName(driverClass.getDriver());
      return DriverManager.getConnection(url, USERNAME, PASSWORD);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void prepareSmallDb(DBDriver driverClass, String url) {
    try (Connection connection = getConnection(driverClass, url)) {
      prepareSmallDb(connection, driverClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static String replaceSql(String sql, DBDriver driver) {
    Map<String, String> map = rowReplacements.get(driver);
    if (map == null) { return sql; }
    for (Map.Entry<String, String> entry : map.entrySet()) {
      sql = sql.replaceAll(entry.getKey(), entry.getValue());
    }
    return sql;
  }

  public static void prepareSmallDb(Connection connection, DBDriver dbDriver) throws SQLException, IOException {
    if (dbDriver.getTestScript() != null) {
      String sql = IOUtils.toString(TestHelperPrepareDB.class.getResourceAsStream(dbDriver.getTestScript()));
      String[] lines = sql.split(";");
      try (Statement st = connection.createStatement()) {
        for (String line : lines) {
          line = line.trim();
          if (StringUtils.isNoneBlank(line)) {
            st.addBatch(line);
          }
        }
        st.executeBatch();
      }
    } else {
      try (Statement st = connection.createStatement()) {
        for (String[] sqls : SQL_COMMANDS) {
          if (sqls.length == 1) {
            st.addBatch(replaceSql(sqls[0], dbDriver));
          } else {
            boolean apply = false;
            for (int i = 0; i < sqls.length - 1; i++) {
              if (dbDriver.toString().equals(sqls[i])) {
                apply = true;
              }
            }
            if (apply) {
              st.addBatch(replaceSql(sqls[sqls.length - 1], dbDriver));
            }
          }
        }
        st.executeBatch();
      }
    }
  }
}

