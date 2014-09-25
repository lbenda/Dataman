/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lbenda.dbapp.rc.frm.config;

import cz.lbenda.dbapp.rc.SessionConfiguration;
import cz.lbenda.gui.EditablePanneTitle;
import java.awt.Component;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

final class DBConfigurationPanel extends javax.swing.JPanel {

  final DBConfigurationOptionsPanelController controller;
  private final ChangeListener listener;

  DBConfigurationPanel(DBConfigurationOptionsPanelController controller) {
    this.controller = controller;
    initComponents();
    listener = new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if (tbConfigs.getSelectedIndex() == tbConfigs.getTabCount() - 1) {
          createSinglePanel(SessionConfiguration.newConfiguration());
          tbConfigs.removeChangeListener(this);
          tbConfigs.setSelectedIndex(tbConfigs.getTabCount() - 2);
          tbConfigs.addChangeListener(this);
        }
      }
    };
    tbConfigs.addChangeListener(listener);
    // TODO listen to changes in form fields and call controller.changed()
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    tbConfigs = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();

    setBorder(javax.swing.BorderFactory.createEtchedBorder());

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 504, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 431, Short.MAX_VALUE)
    );

    tbConfigs.addTab(org.openide.util.NbBundle.getMessage(DBConfigurationPanel.class, "DBConfigurationPanel.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(tbConfigs)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(tbConfigs)
    );
  }// </editor-fold>//GEN-END:initComponents

  private void createSinglePanel(final SessionConfiguration sc) {
    tbConfigs.removeChangeListener(listener);
    EditablePanneTitle title = new EditablePanneTitle(sc.getId());
    title.addOnTitleChangeListener(new EditablePanneTitle.OnTitleChangeListener() {
      @Override
      public void onTitleChange(EditablePanneTitle tile, String text) {
        sc.setId(text);
      }
      @Override
      public void onRemove(EditablePanneTitle title) {
        SessionConfiguration.removeConfiguration(sc.getId());
      }
    });
    SingleConfigurationPanel panel = new SingleConfigurationPanel(sc, this);
    tbConfigs.add(panel, tbConfigs.getTabCount() - 1);
    tbConfigs.setTabComponentAt(tbConfigs.getTabCount() - 2, title); // Same position as previous row, but the previous row add new tab
    panel.load();
    tbConfigs.addChangeListener(listener);
  }

  void load() {
    tbConfigs.removeChangeListener(listener);
    while (tbConfigs.getTabCount() != 1) { tbConfigs.remove(0); }
    tbConfigs.addChangeListener(listener);
    if (SessionConfiguration.getConfigurations().isEmpty()) {
      createSinglePanel(SessionConfiguration.newConfiguration());
    } else {
      for (final SessionConfiguration sc : SessionConfiguration.getConfigurations()) {
        createSinglePanel(sc);
      }
    }
    tbConfigs.setSelectedIndex(0);
  }

  void store() {
    for (Component comp : tbConfigs.getComponents()) {
      if (comp instanceof SingleConfigurationPanel) {
        SingleConfigurationPanel scp = (SingleConfigurationPanel) comp;
        scp.store();
      }
    }
    SessionConfiguration.saveConfiguration();
  }

  boolean valid() {
    for (Component comp : tbConfigs.getComponents()) {
      if (comp instanceof SingleConfigurationPanel) {
        SingleConfigurationPanel scp = (SingleConfigurationPanel) comp;
        if (!scp.valid()) { return false; }
      }
    }
    return true;
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  private javax.swing.JTabbedPane tbConfigs;
  // End of variables declaration//GEN-END:variables

}