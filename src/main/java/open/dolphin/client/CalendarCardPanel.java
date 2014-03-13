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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.*;
import open.dolphin.infomodel.SimpleDate;

/**
 * CalendarCardPanel
 *
 * @author Minagawa,Kazushi
 */
public class CalendarCardPanel extends JPanel  {
    
    public static final String PICKED_DATE = "pickedDate";
    
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
//minagawa^ Icon Server    
    //- private ImageIcon backIcon = ClientContext.getImageIcon("pback_16.png");
    //- private ImageIcon stopIcon = ClientContext.getImageIcon("splay_16.gif");
    //- private ImageIcon forwardIcon = ClientContext.getImageIcon("play_16.gif");
    private final ImageIcon backIcon = ClientContext.getImageIconArias("icon_play_back");
    private final ImageIcon stopIcon = ClientContext.getImageIconArias("icon_stop_play");
    private final ImageIcon forwardIcon = ClientContext.getImageIconArias("icon_play");
//minagawa$    
    private final JButton backBtn = new JButton(backIcon);
    private final JButton stopBtn = new JButton(stopIcon);
    private final JButton forwardBtn = new JButton(forwardIcon);
    private int current;
    private int[] range;
//minagawa^ 予定カルテ    (予定カルテ対応)
    private SimpleDate[] acceptRange;
//minagawa$    
    private final HashMap<String, LiteCalendarPanel> calendars = new HashMap<>(12,1.0f);
    private final HashMap colorTable;
    private ArrayList markList;
    private final PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    private final PropertyChangeListener calendarListener;
    
    private static final int TITLE_ALIGN = SwingConstants.CENTER;
    private static final int TITLE_FONT_SIZE = 12;
    private static final Font TITLE_FONT = new Font("Dialog", Font.PLAIN, TITLE_FONT_SIZE);
    
    private final JLabel titleLable;
    private final Color titleFore = ClientContext.getColor("color.calendar.title.fore");
    private final Color titleBack = ClientContext.getColor("color.calendar.title.back");
    private final int titleAlign = TITLE_ALIGN;
    private final Font titleFont = TITLE_FONT;
    
    /**
     * CalendarCardPanelを生成する。
     * 
     * @param colorTable カラーテーブル
     */
    public CalendarCardPanel(HashMap colorTable) {
        
        this.colorTable = colorTable;
        calendarListener = new CalendarListener(this);
        
        LiteCalendarPanel lc = new LiteCalendarPanel(current, false);
        lc.addPropertyChangeListener(LiteCalendarPanel.SELECTED_DATE_PROP, calendarListener);
        lc.setEventColorTable(colorTable);
        SimpleDate today = new SimpleDate(new GregorianCalendar());
        lc.setToday(today);
        String name = String.valueOf(current);
        calendars.put(name, lc);
        
        cardPanel.setLayout(cardLayout);
        cardPanel.add(lc, name);
        backBtn.setMargin(new Insets(0,0,0,0));
        stopBtn.setMargin(new Insets(0,0,0,0));
        forwardBtn.setMargin(new Insets(0,0,0,0));
        
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current -= 1;
                controlNavigation();
                showCalendar();
            }
        });
        
        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current = 0;
                controlNavigation();
                showCalendar();
            }
        });
        
        forwardBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current+=1;
                controlNavigation();
                showCalendar();
            }
        });
        
        titleLable = new JLabel();
        titleLable.setHorizontalAlignment(titleAlign);
        titleLable.setFont(titleFont);
        titleLable.setForeground(titleFore);
        titleLable.setBackground(titleBack);
        //titleLable.setOpaque(true);
        //Dimension s = titleLable.getPreferredSize();
        //titleLable.setMaximumSize(s);
        //titleLable.setMinimumSize(s);
        
        JPanel cmdPanel = createCommnadPanel();
        updateTitle(lc, titleLable);
        cmdPanel.add(titleLable);
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(cmdPanel);
        this.add(cardPanel);
        
        Dimension size = this.getPreferredSize();
        int h = size.height;
        int w = 220;    // 268
        size = new Dimension(w, h);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
    }
    
    private void updateTitle(LiteCalendarPanel lc, JLabel label) {
        StringBuilder buf = new StringBuilder();
        buf.append(lc.getYear());
        buf.append(ClientContext.getString("calendar.title.year"));
        buf.append(" ");
        String m = String.valueOf(lc.getMonth() + 1);
        if (m.length()==1) {
            buf.append("0");
        }
        buf.append(m);
        buf.append(ClientContext.getString("calendar.title.month"));
        label.setText(buf.toString());
    }
    
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    public void notifyPickedDate(SimpleDate picked) {
        boundSupport.firePropertyChange(PICKED_DATE, null, picked);
    }
    
    public int[] getRange() {
        return range;
    }
    
    public void setCalendarRange(int[] range) {
        this.range = range;
        controlNavigation();
    }
    
//minagawa^ 予定カルテ    (予定カルテ対応)
    public void setAcceptRange(SimpleDate[] range) {
       this.acceptRange = range;
       String key = String.valueOf(current);
       LiteCalendarPanel lc = (LiteCalendarPanel)calendars.get(key);
       lc.setAcceptRange(this.acceptRange);
    }
//minagawa$    
    
    public void setMarkList(ArrayList newMark) {
        
        if (markList != newMark) {
            markList = newMark;
        }
        LiteCalendarPanel lc = (LiteCalendarPanel)calendars.get(String.valueOf(current));
        lc.getTableModel().setMarkDates(markList);
    }
    
    private void controlNavigation() {
        if (range != null) {
            if (current == range[0]) {
                if (backBtn.isEnabled()) {
                    backBtn.setEnabled(false);
                }
                if (! forwardBtn.isEnabled()) {
                    forwardBtn.setEnabled(true);
                }
            } else if (current == range[1]) {
                if (forwardBtn.isEnabled()) {
                    forwardBtn.setEnabled(false);
                }
                if (! backBtn.isEnabled()) {
                    backBtn.setEnabled(true);
                }
            } else {
                if (! backBtn.isEnabled()) {
                    backBtn.setEnabled(true);
                }
                if (! forwardBtn.isEnabled()) {
                    forwardBtn.setEnabled(true);
                }
            }
        }
    }
    
    private void showCalendar() {
        
        String key = String.valueOf(current);
        LiteCalendarPanel lc = (LiteCalendarPanel)calendars.get(key);
        if (lc == null) {
            lc = new LiteCalendarPanel(current, false);
//minagawa^ 予定カルテ            (予定カルテ対応)
            lc.setAcceptRange(this.acceptRange);
//minagawa$            
            lc.addPropertyChangeListener(LiteCalendarPanel.SELECTED_DATE_PROP, calendarListener);
            lc.setEventColorTable(colorTable);
            lc.getTableModel().setMarkDates(markList);
            calendars.put(key, lc);
            cardPanel.add(lc, key);
        } else {
            lc.getTableModel().setMarkDates(markList);
        }
        updateTitle(lc, titleLable);
        cardLayout.show(cardPanel, key);
    }
    
    private JPanel createCommnadPanel() {
        //JPanel cmd = new JPanel(new FlowLayout(FlowLayout.CENTER,2,0));
        JPanel cmd = new JPanel();
        cmd.setLayout(new BoxLayout(cmd, BoxLayout.X_AXIS));
        backBtn.setMargin(new Insets(0,0,0,0));
        stopBtn.setMargin(new Insets(0,0,0,0));
        forwardBtn.setMargin(new Insets(0,0,0,0));
        cmd.add(backBtn);
        cmd.add(Box.createHorizontalStrut(2));
        cmd.add(stopBtn);
        cmd.add(Box.createHorizontalStrut(2));
        cmd.add(forwardBtn);
        cmd.add(Box.createHorizontalStrut(2));
        return cmd;
    }
    
    class CalendarListener implements PropertyChangeListener {
        
        private final CalendarCardPanel owner;
        
        public CalendarListener(CalendarCardPanel owner) {
            this.owner = owner;
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(LiteCalendarPanel.SELECTED_DATE_PROP)) {
                SimpleDate sd = (SimpleDate)e.getNewValue();
                owner.notifyPickedDate(sd);
            }
        }
    }
}
