/*
 * PatientSearchView.java
 *
 * Created on 2007/11/22, 18:43
 */

package open.dolphin.impl.schedule;

import javafx.embed.swing.JFXPanel;
import open.dolphin.impl.psearch.*;

/**
 * (予定カルテ対応)
 * @author  kazushi
 */
public class PatientScheduleView extends JFXPanel {
   
    
    /** Creates new form PatientSearchView */
    public PatientScheduleView() {
        initComponents();
    }

    public javax.swing.JLabel getCountLbl() {
        return countLbl;
    }

    public javax.swing.JTable getTable() {
        return table;
    }

    public javax.swing.JTextField getKeywordFld() {
        return keywordFld;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new AddressTipsTable();
        keywordFld = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        countLbl = new javax.swing.JLabel();
        rpButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        claimChk = new javax.swing.JCheckBox();

        setPreferredSize(new java.awt.Dimension(0, 0));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(table);

        keywordFld.setToolTipText("右クリックで予定日を選択します。");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("予定日:");

        countLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        countLbl.setText("0 件");

        rpButton.setText("処方適用");
        rpButton.setToolTipText("前回処方を適用し予定日のカルテを作成します。");

        updateButton.setText("更 新");
        updateButton.setToolTipText("予定リストを更新します。");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        claimChk.setText("CLAIM送信");
        claimChk.setToolTipText("カルテの作成と同時にORCAへ送信します。");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(keywordFld, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 188, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(countLbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(layout.createSequentialGroup()
                    .add(updateButton)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(rpButton)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(claimChk)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(keywordFld, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(countLbl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(updateButton)
                    .add(rpButton)
                    .add(claimChk))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .add(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_updateButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox claimChk;
    private javax.swing.JLabel countLbl;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField keywordFld;
    private javax.swing.JButton rpButton;
    private javax.swing.JTable table;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

    public javax.swing.JButton getRpButton() {
        return rpButton;
    }

    public javax.swing.JCheckBox getClaimChk() {
        return claimChk;
    }

    public javax.swing.JButton getUpdateButton() {
        return updateButton;
    }
}
