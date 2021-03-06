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
package cz.lbenda.dataman.rc;

import cz.lbenda.dataman.db.DbConfig;
import cz.lbenda.dataman.db.DbStructureFactory;
import cz.lbenda.dataman.schema.dataman.DatamanType;
import cz.lbenda.dataman.schema.dataman.SessionType;
import cz.lbenda.dataman.schema.dbstructure.DatabaseStructureType;
import cz.lbenda.rcp.config.ConfigurationRW;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.*;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 12.9.15.
 * Database configuration factory and holder */
public class DbConfigFactory {

  private static final Logger LOG = LoggerFactory.getLogger(DbConfigFactory.class);
  private static final String CONFIG_FILE_NAME = "sessions.xml";
  private static final String CONFIG_FILE_CACHE_STRUC_NAME = "dbStructure-%s.xml";

  /** All configuration over all system */
  private static final ObservableList<DbConfig> configurations = FXCollections.observableArrayList();
  /** Flag which inform the configuration wasn't load yet */
  private static boolean configurationNotReadedYet = true;

  /** This method reload configuration which is stored in config file */
  public static void reloadConfiguration() {
    configurations.clear();
    configurationNotReadedYet = false;
    String config = ConfigurationRW.getInstance().readConfigAsString(CONFIG_FILE_NAME);
    if (config != null) {
      load(config);
    }
  }

  /** Db structure cache writer */
  public static Callback<DbConfig, String> dbStructureCacheWriter() {
    return dbConfig -> {
      DbStructureFactory.createXMLDatabaseStructure(dbConfig.getCatalogs());
      ConfigurationRW.getInstance().writeConfig(
          String.format(CONFIG_FILE_CACHE_STRUC_NAME, dbConfig.getId()),
          storeStructureCache(dbConfig)
      );
      return "config://" + String.format(CONFIG_FILE_CACHE_STRUC_NAME, dbConfig.getId());
    };
  }

  /** This method save configuration to file */
  public static void saveConfiguration() {
    new Thread(() -> ConfigurationRW.getInstance()
        .writeConfig(CONFIG_FILE_NAME, storeToString(dbStructureCacheWriter())))
        .start();
  }

  public static void removeConfiguration(DbConfig config) {
    configurations.remove(config);
  }

  public static ObservableList<DbConfig> getConfigurations() {
    if (configurationNotReadedYet) { reloadConfiguration(); }
    return configurations;
  }

  public static List<String> getConfigurationIDs() {
    ArrayList<String> result = new ArrayList<>();
    getConfigurations().forEach((sc) -> result.add(sc.getId()));
    return result;
  }

  public static DbConfig getConfiguration(String configName) {
    if (configName == null) { throw new AssertionError("The parameter configName can't be null."); }
    for (DbConfig sc : getConfigurations()) {
      if (configName.equals(sc.getId())) { return sc; }
    }
    return null;
  }

  public static void load(final String document) {
    if (StringUtils.isBlank(document)) { return; }
    try {
      JAXBContext jc = JAXBContext.newInstance(cz.lbenda.dataman.schema.dataman.ObjectFactory.class);
      Unmarshaller u = jc.createUnmarshaller();
      JAXBElement o = (JAXBElement) u.unmarshal(new StringReader(document));
      if (o.getValue() instanceof DatamanType) {
        DatamanType dc = (DatamanType) o.getValue();
        if (dc.getSessions() == null || dc.getSessions().getSession().isEmpty()) {
          LOG.info("No configuration for loading");
        } else {
          configurations.clear();
          for (SessionType session : dc.getSessions().getSession()) {
            DbConfig sc = new DbConfig();
            configurations.add(sc);
            sc.fromSessionType(session, true);
          }
        }
      } else {
        LOG.error("The string didn't contains dataman configuration: " + o.getClass().getName());
      }
    } catch (JAXBException e) {
      LOG.error("Problem with reading dataman configuration\nXML:" + document + "\n" + e.toString(), e);
    }
  }

  public static String storeStructureCache(DbConfig dbConfig) {
    cz.lbenda.dataman.schema.dbstructure.ObjectFactory of = new cz.lbenda.dataman.schema.dbstructure.ObjectFactory();
    try {
      JAXBContext jc = JAXBContext.newInstance(cz.lbenda.dataman.schema.dbstructure.ObjectFactory.class);
      Marshaller m = jc.createMarshaller();
      StringWriter sw = new StringWriter();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      DatabaseStructureType dst = DbStructureFactory.createXMLDatabaseStructure(dbConfig.getCatalogs());
      JAXBElement<DatabaseStructureType> element = of.createDatabaseType(dst);
      m.marshal(element, sw);
      return sw.toString();
    } catch (JAXBException e) {
      LOG.error("Problem with write configuration: " + e.toString(), e);
      throw new RuntimeException("Problem with write configuration: " + e.toString(), e);
    }
  }

  public static String storeToString(Callback<DbConfig, String> cacheDbStructureWriteFactory) {
    cz.lbenda.dataman.schema.dataman.ObjectFactory of = new cz.lbenda.dataman.schema.dataman.ObjectFactory();
    DatamanType config = of.createDatamanType();
    config.setSessions(of.createSessionsType());

    for (DbConfig sc : getConfigurations()) {
      config.getSessions().getSession().add(sc.storeToSessionType(null, cacheDbStructureWriteFactory.call(sc)));
    }

    try {
      JAXBContext jc = JAXBContext.newInstance(cz.lbenda.dataman.schema.dataman.ObjectFactory.class);
      Marshaller m = jc.createMarshaller();
      StringWriter sw = new StringWriter();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(of.createDataman(config), sw);
      return sw.toString();
    } catch (JAXBException e) {
      LOG.error("Problem with write configuration: " + e.toString(), e);
      throw new RuntimeException("Problem with write configuration: " + e.toString(), e);
    }
  }

  /** Method which import one configuration from file */
  public static void importConfig(File file) {
    DbConfig dbConfig = new DbConfig();
    dbConfig.load(file);
    configurations.add(dbConfig);
  }
}
