/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lbenda.dbapp.rc.frm;

import cz.lbenda.dbapp.rc.db.Column;
import cz.lbenda.dbapp.rc.db.DbStructureReader;
import cz.lbenda.dbapp.rc.db.TableDescription;
import java.awt.BorderLayout;
import java.util.*;

import cz.lbenda.dbapp.rc.frm.gui.ColumnCellRenderer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//cz.lbenda.dbapp.rc.frm//FrmDBJoinedTable//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "FrmDBJoinedTableTopComponent",
        iconBase = "cz/lbenda/dbapp/rc/frm/join_indexes.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER//TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "output", openAtStartup = true)//mode = "bottomSlidingSide", openAtStartup = false)
@ActionID(category = "Window", id = "cz.lbenda.dbapp.rc.frm.FrmDBJoinedTableTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_FrmDBJoinedTableAction",
        preferredID = "FrmDBJoinedTableTopComponent"
)
@Messages({
  "CTL_FrmDBJoinedTableAction=Spojené tabulky",
  "CTL_FrmDBJoinedTableTopComponent=Spojené tabulky",
  "HINT_FrmDBJoinedTableTopComponent=Okno se spojenými tabulkami"
})
public final class FrmDBJoinedTableTopComponent extends TopComponent implements ExplorerManager.Provider, ChosenTable.ChosenTableListener {

  private static final Logger LOG = LoggerFactory.getLogger(FrmDBJoinedTableTopComponent.class);
  private OutlineView ov;
  private final ExplorerManager em = new ExplorerManager();
  private final InstanceContent ic = new InstanceContent();
  private JoinedTableChildFactory childFactory;

  public FrmDBJoinedTableTopComponent() {
    initComponents();
    ChosenTable.getDefault().addTableListener(this);
    setName(Bundle.CTL_FrmDBJoinedTableTopComponent());
    setToolTipText(Bundle.HINT_FrmDBJoinedTableTopComponent());

    setLayout(new BorderLayout());

    ic.add(em);
    ic.add(getActionMap());
    associateLookup(new AbstractLookup(ic));
  }

  @Override
  public ExplorerManager getExplorerManager() {
    return em;
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 300, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
  @Override
  public void componentOpened() {
    ChosenTable.getDefault().addTableListener(this);
    childFactory = new JoinedTableChildFactory();
    ov = new OutlineView();
    ov.getOutline().setRootVisible(false);
    ov.getOutline().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    add(ov, BorderLayout.CENTER);
    Node rootNode = new AbstractNode(Children.create(childFactory, true));
    em.setRootContext(rootNode);

    if (ChosenTable.getDefault().getLastChosenRow() != null) {
      this.tableChosen(ChosenTable.getDefault().getLastChosenRow().getTableDescription());
      childFactory.tableChosen(ChosenTable.getDefault().getLastChosenRow().getTableDescription());
      ChosenTable.getDefault().setSelectedRowValues(ChosenTable.getDefault().getLastChosenRow());
    } else if (ChosenTable.getDefault().getLastTD() != null) {
      this.tableChosen(ChosenTable.getDefault().getLastTD());
      childFactory.tableChosen(ChosenTable.getDefault().getLastTD());
    }
  }

  @Override
  public void componentClosed() {
    childFactory.close();
    this.childFactory = null;
    remove(ov);
    ov = null;
    ChosenTable.getDefault().removeTableListener(this);
  }

  void writeProperties(java.util.Properties p) {
    // better to version settings since initial version as advocated at
    // http://wiki.apidesign.org/wiki/PropertyFiles
    p.setProperty("version", "1.0");
    // TODO store your settings
  }

  void readProperties(java.util.Properties p) {
    String version = p.getProperty("version");
    // TODO read your settings according to their version
  }

  @Override
  public void tableChosen(TableDescription tableDescription) {
    LinkedHashSet<String> columnNames = new LinkedHashSet<>();
    List<Column> columns = new ArrayList<>();
    Map<String, String> descriptions = new HashMap<>();
    for (DbStructureReader.ForeignKey key : tableDescription.getForeignKeys()) {
      if (key.getMasterColumn().getExtensions().isEmpty()) { // FIXME only combo box is ignored
        final TableDescription td;
        final Column skipColumn;
        if (key.getMasterTable().equals(tableDescription)) {
          td = key.getSlaveTable();
          skipColumn = key.getSlaveColumn();
        } else {
          td = key.getMasterTable();
          skipColumn = key.getMasterColumn();
        }
        for (Column col : td.getColumns()) {
          if (col.getComment() != null && !"".equals(col.getComment().trim())) {
            if (descriptions.containsKey(col.getName())) {
              descriptions.put(col.getName(),
                  descriptions.get(col.getName()) + "; " + col.getTableDescription().getName() + ": " + col.getComment());
            } else {
              descriptions.put(col.getName(), col.getTableDescription().getName() + ": " + col.getComment());
            }
          }
          if (!col.equals(skipColumn) && !columnNames.contains(col.getName())) {
            columnNames.add(col.getName());
            columns.add(col);
          }
        }
      }
    }
    String[] cols = new String[columnNames.size() * 2];
    int i = 0;
    for (String col : columnNames) {
      cols[i] = col;
      i++;
      cols[i] = col;
      i++;
    }
    ColumnCellRenderer ccr = new ColumnCellRenderer(columns);
    ccr.setCentered(false);
    ov.getOutline().setDefaultRenderer(Node.Property.class, ccr);
    ov.setPropertyColumns(cols);


    for (Map.Entry<String, String> entry : descriptions.entrySet()) {
      ov.setPropertyColumnDescription(entry.getKey(), entry.getValue());
    }
  }

  public static class JoinedTableChildFactory extends ChildFactory<DbStructureReader.ForeignKey> implements ChosenTable.ChosenTableListener, ChosenTable.ChosenRowListener {
    /** Master table */
    private TableDescription masterTD;
    public JoinedTableChildFactory() {
      ChosenTable.getDefault().addTableListener(this);
      ChosenTable.getDefault().addRowListener(this);
    }
    public void close() {
      ChosenTable.getDefault().removeTableListener(this);
      ChosenTable.getDefault().removeRowListener(this);
      masterTD = null;
      this.childFactories.clear();
    }
    @Override
    protected boolean createKeys(List<DbStructureReader.ForeignKey> toPopulate) {
      if (masterTD == null) { return true; }
      for (DbStructureReader.ForeignKey key : masterTD.getForeignKeys()) {
        final Column col;
        if (masterTD.equals(key.getMasterTable())) { col = key.getMasterColumn(); }
        else { col = key.getSlaveColumn(); }
        if (col.getExtensions().isEmpty()) { // FIXME only combo box is ignored
          toPopulate.add(key);
        }
      }
      return true;
    }
    private List<JoinedRowChildFactory> childFactories = new ArrayList<>();
    @Override
    protected Node createNodeForKey(DbStructureReader.ForeignKey key) {
      JoinedRowChildFactory childFactory = new JoinedRowChildFactory(masterTD, key);
      childFactories.add(childFactory);
      AbstractNode node = new AbstractNode(Children.create(childFactory, true));
      if (key.getMasterTable().equals(masterTD)) { node.setName(key.getSlaveTable().getName() + " > " + key.getMasterColumn().getName()); }
      else { node.setName(key.getMasterTable().getName() + " > " + key.getSlaveColumn().getName()); }
      return node;
    }
    @Override
    public void tableChosen(TableDescription tableDescription) {
      if (!tableDescription.equals(masterTD)) {
        masterTD = tableDescription;
        childFactories.clear();
        refresh(true);
      }
    }
    @Override
    public void rowChosen(RowNode.Row selectedRowValues) {
      for (JoinedRowChildFactory fac : childFactories) {
        fac.setMasterRow(selectedRowValues);
      }
    }
  }

  public static class JoinedRowChildFactory extends ChildFactory<RowNode.Row> {
    /** Master table */
    private TableDescription masterTD;
    private final DbStructureReader.ForeignKey fKey;
    private RowNode.Row masterRow;
    public JoinedRowChildFactory(TableDescription masterTD, DbStructureReader.ForeignKey fKey) {
      this.masterTD = masterTD;
      this.fKey = fKey;
    }
    public void setMasterRow(RowNode.Row masterRow) {
      this.masterRow = masterRow;
      refresh(true);
    }
    @Override
    protected boolean createKeys(List<RowNode.Row> toPopulate) {
      if (masterRow == null) { return true; }
      toPopulate.addAll(masterTD.getSessionConfiguration().getReader().getJoinedRows(fKey, masterRow));
      return true;
    }
    @Override
    protected Node createNodeForKey(RowNode.Row key) {
      return new RowNode(key);
    }
  }
}
