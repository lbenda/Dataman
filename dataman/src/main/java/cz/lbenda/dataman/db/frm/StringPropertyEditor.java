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
package cz.lbenda.dataman.db.frm;

import cz.lbenda.common.*;
import cz.lbenda.dataman.db.ColumnDesc;
import cz.lbenda.dataman.db.dialect.ColumnType;
import cz.lbenda.dataman.db.ComboBoxTDExtension;
import cz.lbenda.dataman.db.ComboBoxTDExtension.ComboBoxItem;
import cz.lbenda.gui.controls.BinaryDataEditor;
import cz.lbenda.gui.controls.TextFieldArea;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import jfxtras.scene.control.LocalDateTimeTextField;
import jfxtras.scene.control.LocalTimeTextField;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 23.9.15. */
public class StringPropertyEditor implements PropertyEditor<Object> {

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(StringPropertyEditor.class);

  private TextFieldArea textFieldArea;
  private BinaryDataEditor binaryDataEditor;
  private DatePicker datePicker;
  private LocalTimeTextField localTimeTextField;
  private LocalDateTimeTextField localDateTimeTextField;
  private CheckBox checkBox;
  private ComboBox<ComboBoxItem> comboBox;
  private final StringConverter converter;
  private PropertySheet.Item item;
  private RowPropertyItem rpi = null;
  private ComboBoxTDExtension comboBoxTDExtension;
  private ChangeListener<Boolean> focusLostListener = (observable, oldValue, newValue) -> {
    if (!newValue) { item.setValue(getValue()); }
  };

  @SuppressWarnings("unchecked")
  public StringPropertyEditor(PropertySheet.Item item) {
    this.item = item;
    if (item instanceof RowPropertyItem) {
      rpi = (RowPropertyItem) item;
      rpi.rowProperty().addListener((observable, oldValue, newValue) -> {
        if (!AbstractHelper.nullEquals(oldValue, newValue)) {
          setValue(item.getValue());
        }
      });
      converter = rpi.getColumnDesc().getStringConverter();
    } else { converter = StringConverters.converterForClass(item.getType()); }

    if ((rpi != null && rpi.getColumnDesc().getDataType() == ColumnType.TIME)
        || java.sql.Time.class.isAssignableFrom(item.getType())
        || java.time.LocalTime.class.isAssignableFrom(item.getType())) {
      localTimeTextField = new LocalTimeTextField();
      localTimeTextField.focusedProperty().addListener(focusLostListener);
    } else if ((rpi != null && rpi.getColumnDesc().getDataType() == ColumnType.TIMESTAMP)
        || java.sql.Timestamp.class.isAssignableFrom(item.getType())
        || java.time.LocalDateTime.class.isAssignableFrom(item.getType())) {
      localDateTimeTextField = new LocalDateTimeTextField();
      localDateTimeTextField.focusedProperty().addListener(focusLostListener);
    } else if ((rpi != null && rpi.getColumnDesc().getDataType() == ColumnType.DATE)
        || java.sql.Date.class.isAssignableFrom(item.getType())
        || java.util.Date.class.isAssignableFrom(item.getType())
        || LocalDate.class.isAssignableFrom(item.getType())) {
      datePicker = new DatePicker();
      datePicker.focusedProperty().addListener(focusLostListener);
    } else if (Boolean.class.isAssignableFrom(item.getType())) {
      checkBox = new CheckBox();
      checkBox.focusedProperty().addListener(focusLostListener);
    } else if (rpi != null
        && (rpi.getColumnDesc().getDataType() == ColumnType.BLOB
        || rpi.getColumnDesc().getDataType() == ColumnType.BYTE_ARRAY
        || rpi.getColumnDesc().getDataType() == ColumnType.CLOB)) {
      BinaryData nullData =
          rpi.getColumnDesc().getDataType() == ColumnType.BLOB ? BlobBinaryData.NULL :
              rpi.getColumnDesc().getDataType() == ColumnType.BYTE_ARRAY ? ByteArrayBinaryData.NULL :
                  ClobBinaryData.NULL;
      binaryDataEditor = new BinaryDataEditor(nullData, item.isEditable());
      binaryDataEditor.binaryDataProperty().addListener((observable, oldValue, newValue) -> {
        if (!AbstractHelper.nullEquals(item.getValue(), newValue)) {
          item.setValue(newValue);
        }
      });
    } else {
      if (rpi != null) {
        ColumnDesc columnDesc = rpi.getColumnDesc();
        if (!columnDesc.getExtensions().isEmpty()) {
          columnDesc.getExtensions().stream().filter(tde -> tde instanceof ComboBoxTDExtension).forEach(tde -> {
            comboBoxTDExtension = (ComboBoxTDExtension) tde;
            comboBox = new ComboBox<>(((ComboBoxTDExtension) tde).getItems());
            comboBox.focusedProperty().addListener(focusLostListener);
          });
        }
      }
      if (comboBox == null) {
        boolean editField = rpi == null || rpi.getColumnDesc().getSize() <= Constants.MIN_SIZE_FOR_TEXT_AREA;
        String windowTitle = rpi == null ? "Edit window" :
            rpi.getColumnDesc().getLabel() != null
                ? rpi.getColumnDesc().getLabel() + " (" + rpi.getColumnDesc().getSchema() + "." + rpi.getColumnDesc().getTable() + "." + rpi.getColumnDesc().getName() + ")"
                : rpi.getColumnDesc().getSchema() + "." + rpi.getColumnDesc().getTable() + "." + rpi.getColumnDesc().getName();
        textFieldArea = new TextFieldArea(windowTitle, editField);
        textFieldArea.textProperty().addListener((observable, oldValue, newValue) -> {
          if (!AbstractHelper.nullEquals(oldValue, newValue)) { item.setValue(converter.fromString(newValue)); }
        });
      }
    }
    if (rpi != null && rpi.valueProperty() != null) {
      rpi.valueProperty().addListener((observable, oldValue, newValue) -> setValue(newValue));
    }
  }

  @Override
  public Node getEditor() {
    return
        checkBox != null ? checkBox :
            datePicker != null ? datePicker :
                localDateTimeTextField != null ? localDateTimeTextField :
                    localTimeTextField != null ? localTimeTextField :
                        comboBox != null ? comboBox :
                            binaryDataEditor != null ? binaryDataEditor :
                                textFieldArea;
  }

  @Override
  public Object getValue() {
    if (checkBox != null) { return checkBox.isSelected(); }
    else if (localTimeTextField != null) {
      LocalTime lt = localTimeTextField.getLocalTime();
      if (lt == null) { return null; }
      if ((rpi != null && rpi.getColumnDesc().getDataType() == ColumnType.TIME
          || java.sql.Time.class.isAssignableFrom(item.getType()))) {
        return Time.valueOf(lt);
      }
      return lt;
    } else if (localDateTimeTextField != null) {
      LocalDateTime lt = localDateTimeTextField.getLocalDateTime();
      if (lt == null) { return null; }
      if ((rpi != null && rpi.getColumnDesc().getDataType() == ColumnType.TIMESTAMP
          || java.sql.Timestamp.class.isAssignableFrom(item.getType()))) {
        return Timestamp.valueOf(lt);
      }
      return lt;
    } else if (datePicker != null) {
      LocalDate ldt = datePicker.getValue();
      if (ldt == null) { return null; }
      if (java.sql.Date.class.isAssignableFrom(item.getType())) {
        Instant instant = ldt.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
      } else if (Date.class.isAssignableFrom(item.getType())) {
        Instant instant = ldt.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return new java.sql.Date(Date.from(instant).getTime());
      }
      return ldt;
    } else if (comboBox != null) { return comboBox.selectionModelProperty().getValue().getSelectedItem().getValue(); }
    return converter.fromString(textFieldArea.textProperty().getValue());
  }

  @Override
  public void setValue(Object o) {
    if (checkBox != null) {
      checkBox.setSelected(Boolean.TRUE.equals(o));
    } else if (localTimeTextField != null) {
      if (o == null) { localTimeTextField.setLocalTime(null); }
      if (o instanceof java.sql.Time) { localTimeTextField.setLocalTime(((java.sql.Time) o).toLocalTime()); }
      else { localTimeTextField.setLocalTime((LocalTime) o); }
    } else if (localDateTimeTextField != null) {
      if (o == null) { localDateTimeTextField.setLocalDateTime(null); }
      if (o instanceof java.sql.Timestamp) {
        localDateTimeTextField.setLocalDateTime(((java.sql.Timestamp) o).toLocalDateTime());
      } else { localDateTimeTextField.setLocalDateTime((LocalDateTime) o); }
    } else if (datePicker != null) {
      if (o == null) { datePicker.setValue(null); }
      if (o instanceof java.sql.Date) { datePicker.setValue(((java.sql.Date) o).toLocalDate()); }
      if (o instanceof java.util.Date) {
        Instant instant = Instant.ofEpochMilli(((Date) o).getTime());
        datePicker.setValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate());
      } else { datePicker.setValue((LocalDate) o); }
    } else if (comboBox != null) {
      ComboBoxItem comboBoxItem = comboBoxTDExtension.itemForValue(o);
      comboBox.getSelectionModel().select(comboBoxItem);
    } else if (binaryDataEditor != null) {
      if (o == null && rpi != null) {
        if (rpi.getColumnDesc().getDataType() == ColumnType.CLOB) {
          binaryDataEditor.setBinaryData(CharSequenceBinaryData.NULL);
        } else {
          binaryDataEditor.setBinaryData(ByteArrayBinaryData.NULL);
        }
      } else {
        if (o == null) {
          binaryDataEditor.setBinaryData(ByteArrayBinaryData.NULL);
        } else {
          binaryDataEditor.setBinaryData((BinaryData) o);
        }
      }
    } else {
      //noinspection unchecked
      textFieldArea.setText(converter.toString(o));
    }
  }
}
