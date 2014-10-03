/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lbenda.dbapp.rc.frm.config;

import cz.lbenda.dbapp.rc.SessionConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lukas Benda <lbenda at lbenda.cz>
 */
class SingleConfigurationPanel extends javax.swing.JPanel {

  private static final Logger LOG = LoggerFactory.getLogger(SingleConfigurationPanel.class);

  private final SessionConfiguration sc;
  private final DBConfigurationPanel panel;
  private final LibraryListModel llm = new LibraryListModel();

  /** Creates new form SingleConfigurationPanel
   * @param sc session configuration (which is shown)
   * @param panel main panel
   */
  SingleConfigurationPanel(SessionConfiguration sc, DBConfigurationPanel panel) {
    initComponents();
    this.sc = sc;
    this.panel = panel;
    this.listLibraries.setModel(llm);
    LOG.debug("Start single configuration");
  }

  void load() {
    this.tfDriverClass.setText(sc.getJdbcConfiguration().getDriverClass());
    this.tfUsername.setText(sc.getJdbcConfiguration().getUsername());
    this.pfPassword.setText(sc.getJdbcConfiguration().getPassword());
    this.tfUrl.setText(sc.getJdbcConfiguration().getUrl());
    this.tfPath.setText(sc.getExtendedConfigurationPath());
    this.cbExtendConfigType.setSelectedIndex(sc.getExtendedConfigurationType().ordinal());
    if (sc.getConnectionTimeout() > 0) { this.tfTimeout.setText(String.valueOf(sc.getConnectionTimeout())); }
    else { tfTimeout.setText(""); }

    ChangeListener cl = new ChangeListener();
    tfUsername.getDocument().addDocumentListener(cl);
    pfPassword.getDocument().addDocumentListener(cl);
    tfUrl.getDocument().addDocumentListener(cl);
    tfDriverClass.getDocument().addDocumentListener(cl);
    tfPath.getDocument().addDocumentListener(cl);
    tfTimeout.getDocument().addDocumentListener(cl);
  }

  void store() {
    sc.getJdbcConfiguration().setDriverClass(tfDriverClass.getText());
    sc.getJdbcConfiguration().setPassword(pfPassword.getText());
    sc.getJdbcConfiguration().setUsername(tfUsername.getText());
    sc.getJdbcConfiguration().setUrl(tfUrl.getText());
    sc.setExtendedConfigurationType(SessionConfiguration.ExtendedConfigurationType.values()[cbExtendConfigType.getSelectedIndex()]);
    sc.setExtendedConfigurationPath(tfPath.getText());
    if (tfTimeout.getText() != null && !"".equals(tfTimeout.getText().trim())) {
      sc.setConnectionTimeout(Integer.valueOf(tfTimeout.getText()));
    } else { sc.setConnectionTimeout(-1); }
    SessionConfiguration.registerNewConfiguration(sc);
  }

  private void callChanged() {
    LOG.debug("call changed");
    if (!panel.controller.isChanged()) {
      LOG.debug("isChanged false");
      panel.controller.changed();
    }
  }

  void remove() {
    SessionConfiguration.removeConfiguration(sc.getId());
    callChanged();
  }

  boolean valid() {
    return true;
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel2 = new javax.swing.JPanel();
    tfUrl = new javax.swing.JTextField();
    tfUsername = new javax.swing.JTextField();
    lDriverClass = new javax.swing.JLabel();
    lUrl = new javax.swing.JLabel();
    lUserName = new javax.swing.JLabel();
    lPassword = new javax.swing.JLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    listLibraries = new javax.swing.JList();
    lLibraries = new javax.swing.JLabel();
    bAddLibraries = new javax.swing.JButton();
    pfPassword = new javax.swing.JPasswordField();
    bRemoveLibs = new javax.swing.JButton();
    tfDriverClass = new javax.swing.JTextField();
    lTimeout = new javax.swing.JLabel();
    tfTimeout = new javax.swing.JTextField();
    jPanel1 = new javax.swing.JPanel();
    lExtendConfigType = new javax.swing.JLabel();
    cbExtendConfigType = new javax.swing.JComboBox();
    lPath = new javax.swing.JLabel();
    tfPath = new javax.swing.JTextField();
    bChoosePath = new javax.swing.JButton();

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.jPanel2.border.title"))); // NOI18N

    tfUrl.setText(org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.tfUrl.text")); // NOI18N

    tfUsername.setText(org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.tfUsername.text")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(lDriverClass, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.lDriverClass.text")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(lUrl, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.lUrl.text")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(lUserName, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.lUserName.text")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(lPassword, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.lPassword.text")); // NOI18N

    jScrollPane2.setViewportView(listLibraries);

    org.openide.awt.Mnemonics.setLocalizedText(lLibraries, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.lLibraries.text")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(bAddLibraries, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.bAddLibraries.text")); // NOI18N
    bAddLibraries.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        bAddLibrariesActionPerformed(evt);
      }
    });

    pfPassword.setText(org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.pfPassword.text")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(bRemoveLibs, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.bRemoveLibs.text")); // NOI18N
    bRemoveLibs.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        bRemoveLibsActionPerformed(evt);
      }
    });

    tfDriverClass.setText(org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.tfDriverClass.text")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(lTimeout, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.lTimeout.text")); // NOI18N

    tfTimeout.setText(org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.tfTimeout.text")); // NOI18N

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(lDriverClass)
          .addComponent(lPassword)
          .addComponent(lUserName)
          .addComponent(lUrl)
          .addComponent(lTimeout))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(tfUsername, javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(tfUrl)
          .addComponent(pfPassword)
          .addComponent(tfDriverClass)
          .addComponent(tfTimeout)))
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addComponent(lLibraries)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(bAddLibraries, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(23, 23, 23)
        .addComponent(bRemoveLibs, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lDriverClass)
          .addComponent(tfDriverClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(tfUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(lUrl))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lUserName)
          .addComponent(tfUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lPassword)
          .addComponent(pfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lTimeout)
          .addComponent(tfTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lLibraries, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(bAddLibraries)
          .addComponent(bRemoveLibs))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
    );

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.jPanel1.border.title"))); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(lExtendConfigType, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.lExtendConfigType.text")); // NOI18N

    cbExtendConfigType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Database table", "File" }));

    org.openide.awt.Mnemonics.setLocalizedText(lPath, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.lPath.text")); // NOI18N

    tfPath.setText(org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.tfPath.text")); // NOI18N

    org.openide.awt.Mnemonics.setLocalizedText(bChoosePath, org.openide.util.NbBundle.getMessage(SingleConfigurationPanel.class, "SingleConfigurationPanel.bChoosePath.text")); // NOI18N
    bChoosePath.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        bChoosePathActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(lExtendConfigType)
          .addComponent(lPath))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(cbExtendConfigType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(tfPath)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(bChoosePath))))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lExtendConfigType)
          .addComponent(cbExtendConfigType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(lPath)
          .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(tfPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(bChoosePath)))
        .addGap(0, 0, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void bAddLibrariesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddLibrariesActionPerformed
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(true);
    FileNameExtensionFilter filter = new FileNameExtensionFilter("JAR & another archives", "jar", "zip", "ear", "war");
    chooser.setFileFilter(filter);
    int returnValue = chooser.showOpenDialog(getParent());
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      for (File file : chooser.getSelectedFiles()) {
        sc.getLibrariesPaths().add(file.getAbsolutePath());
        LOG.debug("The user choose files: " + file.getAbsolutePath());
      }
      callChanged();
      llm.dataChanged();
    }
  }//GEN-LAST:event_bAddLibrariesActionPerformed

  private void bRemoveLibsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveLibsActionPerformed
    boolean removed = false;
    for (Object sel : listLibraries.getSelectedValuesList()) {
      sc.getLibrariesPaths().remove((String) sel);
      removed = true;
    }
    if (removed) {
      callChanged();
      llm.dataChanged();
    }
  }//GEN-LAST:event_bRemoveLibsActionPerformed

  private void bChoosePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bChoosePathActionPerformed
    if (cbExtendConfigType.getSelectedIndex() == 1) {
      // TODO database extended configuration
    } else if (cbExtendConfigType.getSelectedIndex() == 2) {
      JFileChooser chooser = new JFileChooser();
      chooser.setMultiSelectionEnabled(false);
      FileNameExtensionFilter filter = new FileNameExtensionFilter("DBApp session configuration", "xml", "dsc");
      chooser.setFileFilter(filter);
      int returnValue = chooser.showOpenDialog(getParent());
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        this.tfPath.setText(chooser.getSelectedFile().getAbsolutePath());
        LOG.debug("The user choose files: " + chooser.getSelectedFile().getAbsolutePath());
        callChanged();
        llm.dataChanged();
      }
    }
  }//GEN-LAST:event_bChoosePathActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton bAddLibraries;
  private javax.swing.JButton bChoosePath;
  private javax.swing.JButton bRemoveLibs;
  private javax.swing.JComboBox cbExtendConfigType;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JLabel lDriverClass;
  private javax.swing.JLabel lExtendConfigType;
  private javax.swing.JLabel lLibraries;
  private javax.swing.JLabel lPassword;
  private javax.swing.JLabel lPath;
  private javax.swing.JLabel lTimeout;
  private javax.swing.JLabel lUrl;
  private javax.swing.JLabel lUserName;
  private javax.swing.JList listLibraries;
  private javax.swing.JPasswordField pfPassword;
  private javax.swing.JTextField tfDriverClass;
  private javax.swing.JTextField tfPath;
  private javax.swing.JTextField tfTimeout;
  private javax.swing.JTextField tfUrl;
  private javax.swing.JTextField tfUsername;
  // End of variables declaration//GEN-END:variables

  private class ChangeListener implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
      LOG.debug("insert update");
      callChanged();
    }
    @Override
    public void removeUpdate(DocumentEvent e) {
      LOG.debug("remove update");
      callChanged();
    }
    @Override
    public void changedUpdate(DocumentEvent e) {
      LOG.debug("changed update");
      callChanged();
    }
  }

  private class LibraryListModel implements ListModel {
    private final List<ListDataListener> listeners = new ArrayList<>();
    @Override
    public int getSize() { return sc.getLibrariesPaths().size(); }
    @Override
    public Object getElementAt(int index) { return sc.getLibrariesPaths().get(index); }
    /** Is called when data is changed */
    public final void dataChanged() {
      ListDataEvent lde = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0,
              sc.getLibrariesPaths().size() - 1);
      for (ListDataListener l : listeners) { l.contentsChanged(lde); }
    }
    @Override
    public void addListDataListener(ListDataListener l) { listeners.add(l); }
    @Override
    public void removeListDataListener(ListDataListener l) { listeners.remove(l); }
  }
}
