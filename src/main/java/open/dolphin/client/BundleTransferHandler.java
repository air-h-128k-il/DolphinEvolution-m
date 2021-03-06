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

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.IOException;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.ModuleModel;

/**
 * BundleTransferHandler
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 */
public class BundleTransferHandler extends TransferHandler implements IKarteTransferHandler {

    private ChartMediator mediator;
    private JTextComponent textCompo;
    private boolean hasSelection;
    
    // Start and end position in the source text.
    // We need this information when performing a MOVE
    // in order to remove the dragged text from the source.
    Position p0 = null, p1 = null;
    
    public BundleTransferHandler(ChartMediator med, JTextComponent tc) {
        this.textCompo = tc;
        this.textCompo.putClientProperty("karteCompositor", this.textCompo);
        this.mediator = med;
        textCompo.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent ce) {
                boolean newSelection = (ce.getDot() != ce.getMark()) ? true : false;
                if (newSelection != hasSelection) {
                    hasSelection = newSelection;
                    if (hasSelection) {
                        mediator.getAction(GUIConst.ACTION_COPY).setEnabled(true);
                        mediator.getAction(GUIConst.ACTION_CUT).setEnabled(textCompo.isEditable());
                    } else {
                        mediator.getAction(GUIConst.ACTION_COPY).setEnabled(false);
                        mediator.getAction(GUIConst.ACTION_CUT).setEnabled(false);
                    }
                }
            }
        });
    }
    
    /**
     * DropされたFlavorをインポートする。
     */
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        
        if (!canImport(support)) {
            return false;
        }

        JTextComponent tc = (JTextComponent) support.getComponent();

        try {
            if (support.getTransferable().isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
                return doStampDrop(tc, support.getTransferable());
                
            } else if (support.getTransferable().isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String str = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                tc.replaceSelection(str);
                return true;
            }
        } catch (UnsupportedFlavorException | IOException ufe) {
        }
        
        return false;
    }
    
    // Create a Transferable implementation that contains the
    // selected text.
    @Override
    protected Transferable createTransferable(JComponent c) {
        JTextComponent source = (JTextComponent) c;
        int start = source.getSelectionStart();
        int end = source.getSelectionEnd();
        Document doc = source.getDocument();
        if (start == end) {
            return null;
        }
        try {
            p0 = doc.createPosition(start);
            p1 = doc.createPosition(end);
        } catch (BadLocationException e) {
        }
        String data = source.getSelectedText();
        return new StringSelection(data);
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    // Remove the old text if the action is a MOVE.
    // However, we do not allow dropping on top of the selected text,
    // so in that case do nothing.
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action != MOVE) {
            return;
        }

        JTextComponent tc = (JTextComponent) c;
        
        if (tc.isEditable()) {
            if ((p0 != null) && (p1 != null) &&
                (p0.getOffset() != p1.getOffset())) {
                try {
                    tc.getDocument().remove(p0.getOffset(),
                            p1.getOffset() - p0.getOffset());
                } catch (BadLocationException e) {
                    System.out.println("Can't remove text from source.");
                }
            }
        }
    }
    
    /**
     * インポート可能かどうかを返す。
     */
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        JTextComponent tc = (JTextComponent) support.getComponent();
        boolean canImport = true;
        canImport = canImport && tc.isEditable();
        canImport = canImport && (support.isDataFlavorSupported(DataFlavor.stringFlavor) ||
                                  support.isDataFlavorSupported(OrderListTransferable.orderListFlavor));
        return canImport;
    }
    
    /**
     * DropされたStamp(ModuleModel)をインポートする。
     * @param tr Transferable
     * @return インポートに成功した時 true
     */
    private boolean doStampDrop(JTextComponent tc, Transferable tr) {
        
        try {
            // スタンプのリストを取得する
            OrderList list = (OrderList) tr.getTransferData(OrderListTransferable.orderListFlavor);
            ModuleModel[] stamps = list.orderList;
            // pPaneにスタンプを挿入する
            for (int i = 0; i < stamps.length; i++) {
                if (stamps[i].getModel() instanceof BundleDolphin) {
                    BundleDolphin bd = (BundleDolphin)stamps[i].getModel();
                    tc.replaceSelection(bd.toString());
                }
            }
            return true;
        } catch (UnsupportedFlavorException | IOException e) {        
            e.printStackTrace(System.err);
        }
        return false;
    }
    
    /**
     * クリップボードへデータを転送する。
     */
    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        super.exportToClipboard(comp, clip, action);
        // cut の場合を処理する
        if (action == MOVE) {
            JTextComponent pane = (JTextComponent) comp;
            if (pane.isEditable()) {
                pane.replaceSelection("");
            }
        }
    }

    @Override
    public JComponent getComponent() {
        return textCompo;
    }

    @Override
    public void enter(ActionMap map) {
        if (textCompo.isEditable()) {
            map.get(GUIConst.ACTION_PASTE).setEnabled(canPaste());
        }
    }

    @Override
    public void exit(ActionMap map) {
    }

    private boolean canPaste() {
        if (!textCompo.isEditable()) {
            return false;
        }
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (t == null) {
            return false;
        }

        boolean canImport = true;
        canImport = canImport && (t.isDataFlavorSupported(DataFlavor.stringFlavor) ||
                                  t.isDataFlavorSupported(OrderListTransferable.orderListFlavor));
        return canImport;
    }
}
