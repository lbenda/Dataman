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

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 12.9.15.
 * Abstract ancestor of Action */
public abstract class AbstractAction implements Action {

  private List<Consumer<Boolean>> enableDisableConsumers = new ArrayList<>();
  private List<Consumer<Integer>> changeActionConfigConsumers = new ArrayList<>();

  private boolean enable = true;

  @Override
  public void addEnableDisableConsumer(Consumer<Boolean> consumer) { this.enableDisableConsumers.add(consumer); }
  @Override
  public void removeEnableDisableConsumer(Consumer<Boolean> consumer) { this.enableDisableConsumers.remove(consumer); }

  @Override
  public void addChangeActionConfigConsumer(Consumer<Integer> consumer) { this.changeActionConfigConsumers.add(consumer); }
  @Override
  public void removeChangeActionConfigConsumer(Consumer<Integer> consumer) { this.changeActionConfigConsumers.remove(consumer); }

  @Override
  public boolean isEnable() { return enable; }
  public void setEnable(boolean enable) {
    this.enable = enable;
    enableDisableConsumers.forEach(consumer -> consumer.accept(enable));
  }
  public void setConfig(int config) {
    changeActionConfigConsumers.forEach(consumer -> consumer.accept(config));
  }

  public static Stage extractStage(Window window) {
    if (window instanceof Stage) { return (Stage) window; }
    if (window instanceof ContextMenu) { return extractStage((ContextMenu) window); }
    throw new UnsupportedOperationException("Can't return stage.");
  }

  public static Stage extractStage(ContextMenu contextMenu) {
    if (contextMenu.getOwnerWindow() instanceof Stage) { return (Stage) contextMenu.getOwnerWindow(); }
    if (contextMenu.getOwnerWindow() instanceof ContextMenu) {
      return extractStage((ContextMenu) contextMenu.getOwnerWindow());
    }
    throw new UnsupportedOperationException("No stage found for class: " + contextMenu.getOwnerWindow().getClass());
  }

  public static Stage extractStage(MenuItem menuItem) {
    Window window = null;
    if (menuItem.getParentPopup() != null) {
      window = menuItem.getParentPopup().getScene().getWindow();
    } else {
      Menu parentMenu = menuItem.getParentMenu();
      do {
        if (parentMenu.getParentPopup() != null) { window = parentMenu.getParentPopup().getScene().getWindow(); }
        parentMenu = parentMenu.getParentMenu();
      } while (parentMenu != null);
    }
    if (window != null) { return extractStage(window); }
    throw new UnsupportedOperationException("Can't return stage no parent popup was found");
  }

  public static Stage extractStage(ActionEvent actionEvent) {
    if (actionEvent.getSource() instanceof Node) {
      return (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    } else if (actionEvent.getSource() instanceof MenuItem) {
      return extractStage((MenuItem) actionEvent.getSource());
    } else {
      throw new UnsupportedOperationException("Can't return stage for action event with source: " + actionEvent.getSource());
    }
  }
}
