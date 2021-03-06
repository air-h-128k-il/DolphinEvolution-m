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

import open.dolphin.stampbox.StampTreeNode;
import open.dolphin.stampbox.StampTree;
import open.dolphin.stampbox.StampBoxPlugin;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.project.Project;

/**
 * KartePane の抽象コードヘルパークラス。
 *
 * @author Kazyshi Minagawa
 */
public abstract class AbstractCodeHelper {
    
    /** キーワードの境界となる文字 */
    static final String[] WORD_SEPARATOR = {" ", " ", "、", "。", "\n", "\t"};
    
    static final String LISTENER_METHOD = "importStamp";

//minagawa^ Icon Server    
    //static final Icon icon = ClientContext.getImageIcon("foldr_16.gif");
    static final Icon icon = ClientContext.getImageIconArias("icon_foldr_small");
//minagawa$    
    
    /** 対象の KartePane */
    KartePane kartePane;
    
    /** KartePane の JTextPane */
    JTextPane textPane;
    
    /** 補完リストメニュー */
    JPopupMenu popup;
    
    /** キーワードパターン */
    Pattern pattern;
    
    /** キーワードの開始位置 */
    int start;
    
    /** キーワードの終了位置 */
    int end;
    
    /** ChartMediator */
    ChartMediator mediator;
    
    /** 修飾キー */
    int MODIFIER;
    
    
    /** 
     * Creates a new instance of CodeHelper 
     */
    public AbstractCodeHelper(KartePane kartePane, ChartMediator mediator) {
        
        this.kartePane = kartePane;
        this.mediator = mediator;
        this.textPane = kartePane.getTextPane();
        
        String modifier = Project.getString("modifier");
        
        if (modifier.equals("ctrl")) {
            MODIFIER =  KeyEvent.CTRL_DOWN_MASK;
        } else if (modifier.equals("meta")) {
            MODIFIER =  KeyEvent.META_DOWN_MASK;
        }

        this.textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (ClientContext.isMac()) {

                    // 元町皮膚科アイデア
                    if ((e.getModifiersEx() == MODIFIER) && e.getKeyCode() == KeyEvent.VK_ENTER) {
                        buildAndShowPopup();
                    }
                }
                else {
                    if ((e.getModifiersEx() == MODIFIER) && e.getKeyCode() == KeyEvent.VK_SPACE) {
                        buildAndShowPopup();
                    }
                }
            }
        });
    }
    
    protected abstract void buildPopup(String text);
    
    protected void buildEntityPopup(String entity) {
        
        //
        // 引数の entityに対応する StampTree を取得する
        //
        StampBoxPlugin stampBox = mediator.getStampBox();
        StampTree tree = stampBox.getStampTree(entity);
        if (tree == null) {
            return;
        }
        
        popup = new JPopupMenu();
        
        HashMap<Object, Object> ht = new HashMap<Object, Object>(5, 0.75f);
        
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
        ht.put(rootNode, popup);
        
        Enumeration e = rootNode.preorderEnumeration();
        
        if (e != null) {
            
            e.nextElement(); // consume root
            
            while (e.hasMoreElements()) {
                
                StampTreeNode node = (StampTreeNode) e.nextElement();
                
                if (!node.isLeaf()) {
                    
                    JMenu subMenu = new JMenu(node.getUserObject().toString());
                    if (node.getParent() == rootNode) {
                        JPopupMenu parent = (JPopupMenu) ht.get(node.getParent());
                        parent.add(subMenu);
                        ht.put(node, subMenu);
                    } else {
                        JMenu parent = (JMenu) ht.get(node.getParent());
                        parent.add(subMenu);
                        ht.put(node, subMenu);   
                    }
                    
            
                    // 配下の子を全て列挙しJmenuItemにまとめる
                    JMenuItem item = new JMenuItem(node.getUserObject().toString());
                    item.setIcon(icon);
                    subMenu.add(item);
                    
                    addActionListner(item, node);
                
                } else if (node.isLeaf()) {
                    
                    ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                    String stampName = info.getStampName();
                     
                    JMenuItem item = new JMenuItem(stampName);
                    addActionListner(item, node);
                    
                    if (node.getParent() == rootNode) {
                        JPopupMenu parent = (JPopupMenu) ht.get(node.getParent());
                        parent.add(item);
                    } else {
                        JMenu parent = (JMenu) ht.get(node.getParent());
                        parent.add(item);
                    }
                }
            }
        }
    }
    
    protected void addActionListner(JMenuItem item, StampTreeNode node) {
        
        ReflectActionListener ral = new ReflectActionListener(this, LISTENER_METHOD, 
                            new Class[]{JComponent.class, TransferHandler.class, LocalStampTreeNodeTransferable.class},
                            new Object[]{textPane, textPane.getTransferHandler(), new LocalStampTreeNodeTransferable(node)});
        
        item.addActionListener(ral);
    }

    protected void showPopup() {
        
        if (popup == null || popup.getComponentCount() < 1) {
            return;
        }
        
        try {
            int pos = textPane.getCaretPosition();
            Rectangle r = textPane.modelToView(pos);
            popup.show (textPane, r.x, r.y);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void importStamp(JComponent comp, TransferHandler handler, LocalStampTreeNodeTransferable tr) {
        textPane.setSelectionStart(start);
        textPane.setSelectionEnd(end);
        textPane.replaceSelection("");
//minagawa^ Hanazono 2013/06/05
        //handler.importData(comp, tr);
        TransferHandler.TransferSupport support = new TransferHandler.TransferSupport(comp, tr);
        handler.importData(support);
//minagawa$
        closePopup();
    }
    
    protected void closePopup() {
        if (popup != null) {
            popup.removeAll();
            popup = null;
        }
    }

    /**
     * 単語の境界からキャレットの位置までのテキストを取得し、
     * 長さがゼロ以上でれば補完メニューをポップアップする。
     */
    protected void buildAndShowPopup() {

        end = textPane.getCaretPosition();
        start = end;
        boolean found = false;

        while (start > 0) {
            
            start--;
  
            try {
                String text = textPane.getText(start, 1);
                for (String test : WORD_SEPARATOR) {
                    if (test.equals(text)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    start++;
                    break;
                }
                
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        try {
            
            String str = textPane.getText(start, end - start);
            
            if (str.length() > 0) {
                buildPopup(str);
                showPopup();
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
