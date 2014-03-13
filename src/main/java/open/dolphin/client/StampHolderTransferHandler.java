/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp.
 * 825 Sylk BLDG., 1-Yamashita-Cho, Naka-Ku, Kanagawa-Ken, Yokohama-City, JAPAN.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA.
 * 
 * (R)OpenDolphin version 2.4, Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp. 
 * (R)OpenDolphin comes with ABSOLUTELY NO WARRANTY; for details see the GNU General 
 * Public License, version 3 (GPLv3) This is free software, and you are welcome to redistribute 
 * it under certain conditions; see the GPLv3 for details.
 */
package open.dolphin.client;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.*;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.project.Project;
import open.dolphin.stampbox.StampTreeNode;
import open.dolphin.util.BeanUtils;
import open.dolphin.util.Log;

/**
 * StampHolderTransferHandler
 * 
 * @author Kazushi Minagawa. Digital Globe, Inc.
 *
 */
public class StampHolderTransferHandler extends TransferHandler implements IKarteTransferHandler {

    private KartePane pPane;
    private StampHolder stampHolder;

    public StampHolderTransferHandler(KartePane pPane, StampHolder sh) {
        this.pPane = pPane;
        this.stampHolder = sh;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        StampHolder source = (StampHolder) c;
        KartePane context = source.getKartePane();
        context.setDrragedStamp(new ComponentHolder[]{source});
        context.setDraggedCount(1);
        ModuleModel stamp = source.getStamp();
        OrderList list = new OrderList(new ModuleModel[]{stamp});
        Transferable tr = new OrderListTransferable(list);
        return tr;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    private void replaceStamp(final StampHolder target, final ModuleInfoBean stampInfo) {
        
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    StampDelegater sdl = new StampDelegater();
                    StampModel getStamp = sdl.getStamp(stampInfo.getStampId());
                    final ModuleModel stamp = new ModuleModel();
                    if (getStamp != null) {
                        stamp.setModel((IInfoModel) BeanUtils.xmlDecode(getStamp.getStampBytes()));
                        stamp.setModuleInfoBean(stampInfo);
                    }
                    Runnable awt = new Runnable() {

                        @Override
                        public void run() {
                            target.importStamp(stamp);
                        }
                    };
                    EventQueue.invokeLater(awt);
                    
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        };
        Thread t = new Thread(r);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }
    
    private void confirmReplace(StampHolder target, ModuleInfoBean stampInfo) {
        
        Window w = SwingUtilities.getWindowAncestor(target);
        String replace = "置き換える";
        String cancel = "取消し";
        
         int option = JOptionPane.showOptionDialog(
                 w, 
                 "スタンプを置き換えますか?", 
                 "スタンプ Drag and Drop", 
                 JOptionPane.DEFAULT_OPTION, 
                 JOptionPane.QUESTION_MESSAGE, 
                 null, 
                 new String[]{replace, cancel}, replace);
         Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, "スタンプ Drag and Drop", "スタンプを置き換えますか?");
         
         if (option == 0) {
             Log.outputOperLogDlg(pPane.getParent(), Log.LOG_LEVEL_0, replace);
             replaceStamp(target, stampInfo);
         }else{
             Log.outputOperLogDlg(pPane.getParent(), Log.LOG_LEVEL_0, cancel);
         }
    }

    @Override
//minagawa^ Paste problem 2013/04/14 不具合修正(スタンプが消える)
    //public boolean importData(JComponent c, Transferable tr) {
    public boolean importData(TransferHandler.TransferSupport support) {    

        //if (canImport(c, tr.getTransferDataFlavors())) {
        if (canImport(support)) {    
            
            //final StampHolder target = (StampHolder) c;
            final StampHolder target = (StampHolder)support.getComponent();
            Transferable tr = support.getTransferable();
//minagawa$
            StampTreeNode droppedNode;

            try {
                droppedNode = (StampTreeNode) tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
                return false;
            }
            
            if (droppedNode == null || (!droppedNode.isLeaf())) {
                return false;
            }

            final ModuleInfoBean stampInfo = (ModuleInfoBean) droppedNode.getStampInfo();
            String role = stampInfo.getStampRole();
            
            if (!role.equals(IInfoModel.ROLE_P)) {
                return false;
            }
            
            if (Project.getBoolean("replaceStamp", false)) {
                replaceStamp(target, stampInfo);
                
            } else {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        confirmReplace(target, stampInfo);
                    }
                };
                EventQueue.invokeLater(r);
            } 
            return true;
        }
        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable tr, int action) {
        StampHolder test = (StampHolder) c;
        KartePane context = test.getKartePane();
        if (action == MOVE &&
                context.getDrragedStamp() != null &&
                context.getDraggedCount() == context.getDroppedCount()) {
//s.oh^ 2013/11/26 スクロールバーのリセット
            //context.removeStamp(test); // TODO 
            context.removeStamp(test, false);
//s.oh$
        }
        context.setDrragedStamp(null);
        context.setDraggedCount(0);
        context.setDroppedCount(0);
    }

    /**
     * インポート可能かどうかを返す。
     */
//minagawa^ Paste problem 2013/04/14 不具合修正(スタンプが消える)
//   @Override
//    public boolean canImport(JComponent c, DataFlavor[] flavors) {
//        StampHolder test = (StampHolder) c;
//        JTextPane tc = (JTextPane) test.getKartePane().getTextPane();
//        if (tc.isEditable() && hasFlavor(flavors)) {
//            return true;
//        }
//        return false;
//    }
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        StampHolder test = (StampHolder)support.getComponent();
        JTextPane tc = (JTextPane) test.getKartePane().getTextPane();
        boolean ok = tc.isEditable();
        ok = ok && hasFlavor(support.getDataFlavors());
        return ok;
    }
//minagawa$
    
    protected boolean hasFlavor(DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (LocalStampTreeNodeTransferable.localStampTreeNodeFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * スタンプをクリップボードへ転送する。
     */
    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        StampHolder sh = (StampHolder) comp;
        Transferable tr = createTransferable(comp);
        clip.setContents(tr, null);
        if (action == MOVE) {
            KartePane kartePane = sh.getKartePane();
            if (kartePane.getTextPane().isEditable()) {
//s.oh^ 2013/11/26 スクロールバーのリセット
                //kartePane.removeStamp(sh);
                kartePane.removeStamp(sh, true);
//s.oh$
            }
        }
    }

    @Override
    public JComponent getComponent() {
        return stampHolder;
    }

    @Override
    public void enter(ActionMap map) {
        stampHolder.setSelected(true);
        map.get(GUIConst.ACTION_COPY).setEnabled(true);
        boolean canCut = (pPane.getTextPane().isEditable());
        map.get(GUIConst.ACTION_CUT).setEnabled(canCut);
        map.get(GUIConst.ACTION_PASTE).setEnabled(false);
    }

    @Override
    public void exit(ActionMap map) {
        stampHolder.setSelected(false);
    }
}
