/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Baka.java
 *
 * Created on 2009/05/09, 9:13:54
 */

package open.dolphin.order;

/**
 *
 * @author kushiro
 */
public class BaseViewText extends javax.swing.JPanel implements IBaseView  {

    /** Creates new form Baka */
    public BaseViewText() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        infoLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        setTable = new javax.swing.JTable();
        techChk = new javax.swing.JCheckBox();
        searchTextField = new javax.swing.JTextField();
        rtBtn = new javax.swing.JCheckBox();
        countField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        searchResultTable = new javax.swing.JTable();
        partialChk = new javax.swing.JCheckBox();
        stampNameField = new javax.swing.JTextField();
        deleteBtn = new javax.swing.JButton();
        clearBtn = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();
        okCntBtn = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 11, 11));
        setMaximumSize(new java.awt.Dimension(1024, 768));
        setMinimumSize(new java.awt.Dimension(700, 600));
        setPreferredSize(new java.awt.Dimension(700, 600));

        infoLabel.setText("info"); // NOI18N

        setTable.setModel(new javax.swing.table.DefaultTableModel(
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
        setTable.setToolTipText("セット内容は Drag & Drop で順番を入れ替えることができます。"); // NOI18N
        jScrollPane1.setViewportView(setTable);

        techChk.setText("診療行為"); // NOI18N
        techChk.setToolTipText("診療行為がセットにあればチェックされます。"); // NOI18N
        techChk.setEnabled(false);

        searchTextField.setToolTipText("検索したい点数マスタ項目を入力します。"); // NOI18N

        rtBtn.setText("RT"); // NOI18N
        rtBtn.setToolTipText("チェックすると入力中にリアルタイムで検索します。"); // NOI18N

        countField.setToolTipText("検索結果の件数を表示します。"); // NOI18N

        jLabel4.setText("件数"); // NOI18N

        searchResultTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(searchResultTable);

        partialChk.setText("部分一致"); // NOI18N

        stampNameField.setBackground(new java.awt.Color(255, 255, 0));
        stampNameField.setColumns(12);
        stampNameField.setToolTipText("セット名を入力します。"); // NOI18N
        stampNameField.setMaximumSize(new java.awt.Dimension(140, 28));

        deleteBtn.setText("削除"); // NOI18N
        deleteBtn.setToolTipText("選択した項目を削除します。"); // NOI18N

        clearBtn.setText("クリア"); // NOI18N
        clearBtn.setToolTipText("セット内容をクリアします。"); // NOI18N

        okBtn.setText("展開"); // NOI18N
        okBtn.setToolTipText("セットをカルテに展開し終了します。"); // NOI18N

        okCntBtn.setText("展開継続"); // NOI18N
        okCntBtn.setToolTipText("セットをカルテに展開し継続します。"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(infoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(techChk))
            .addComponent(jScrollPane2)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(stampNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rtBtn)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(partialChk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 188, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(countField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(deleteBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okCntBtn))))
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(infoLabel)
                    .addComponent(techChk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stampNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBtn)
                    .addComponent(clearBtn)
                    .addComponent(okBtn)
                    .addComponent(okCntBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rtBtn)
                    .addComponent(partialChk)
                    .addComponent(jLabel4)
                    .addComponent(countField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearBtn;
    private javax.swing.JTextField countField;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton okBtn;
    private javax.swing.JButton okCntBtn;
    private javax.swing.JCheckBox partialChk;
    private javax.swing.JCheckBox rtBtn;
    private javax.swing.JTable searchResultTable;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JTable setTable;
    private javax.swing.JTextField stampNameField;
    private javax.swing.JCheckBox techChk;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the clearBtn
     */
    @Override
    public javax.swing.JButton getClearBtn() {
        return clearBtn;
    }

    /**
     * @return the countField
     */
    @Override
    public javax.swing.JTextField getCountField() {
        return countField;
    }

    /**
     * @return the deleteBtn
     */
    @Override
    public javax.swing.JButton getDeleteBtn() {
        return deleteBtn;
    }

    /**
     * @return the infoLabel
     */
    @Override
    public javax.swing.JLabel getInfoLabel() {
        return infoLabel;
    }


    /**
     * @return the okBtn
     */
    @Override
    public javax.swing.JButton getOkBtn() {
        return okBtn;
    }

    /**
     * @return the okCntBtn
     */
    @Override
    public javax.swing.JButton getOkCntBtn() {
        return okCntBtn;
    }

    /**
     * @return the rtBtn
     */
    @Override
    public javax.swing.JCheckBox getRtBtn() {
        return rtBtn;
    }

    /**
     * @return the searchResultTabel
     */
    @Override
    public javax.swing.JTable getSearchResultTable() {
        return searchResultTable;
    }

    /**
     * @return the searchTextField
     */
    @Override
    public javax.swing.JTextField getSearchTextField() {
        return searchTextField;
    }

    /**
     * @return the stampNameField
     */
    @Override
    public javax.swing.JTextField getStampNameField() {
        return stampNameField;
    }

    /**
     * @return the techChk
     */
    @Override
    public javax.swing.JCheckBox getTechChk() {
        return techChk;
    }

    /**
     * @return the setTable
     */
    @Override
    public javax.swing.JTable getSetTable() {
        return setTable;
    }

    @Override
    public javax.swing.JCheckBox getPartialChk() {
        return partialChk;
    }

}
