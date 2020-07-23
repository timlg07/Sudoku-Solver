package sudoku.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import sudoku.gui.model.DisplayData;

public class SudokuCell extends JLabel {

    private static final long serialVersionUID = 1L;
    

    /**
     * The foreground color each unmodifiable cell should have.
     */
    private static final Color FIXED_CELL_COLOR = Color.RED;
    
    /**
     * The lowered bordered of every cell.
     */
    private static final Border CELL_BORDER 
            = BorderFactory.createBevelBorder(BevelBorder.LOWERED);

    /** 
     * The font size of the cells text. This value is also used to calculate the
     * preferred and minimum size.
     */
    private static final int FONT_SIZE = 14;
    
    private final int majorIndex;
    private final int minorIndex;
    private final DisplayData data;
    private final JPopupMenu popupMenu;

    public SudokuCell(
            int major, int minor, DisplayData data, boolean isModifiable) {
        super("", SwingConstants.CENTER);
        majorIndex = major;
        minorIndex = minor;
        this.data = data;
        
        if (isModifiable) {
            popupMenu = new CellPopupMenu();
            setComponentPopupMenu(popupMenu);
        } else {
            popupMenu = null;
            setForeground(FIXED_CELL_COLOR);
        }

        setComponentPopupMenu(popupMenu);
        setFont(getFont().deriveFont((float) FONT_SIZE));
        setDimensions();
        setBorder(CELL_BORDER);
    }
    
    private void setDimensions() {
        // maybe constant size but lower the font size?
        int maxDigits = (int) (Math.log10(data.getNumbers()) + 1);
        int contentSize = maxDigits * FONT_SIZE;
        int sizeWithPadding = contentSize + (FONT_SIZE * 2);
        
        setPreferredSize(new Dimension(sizeWithPadding, sizeWithPadding));
        setMinimumSize(new Dimension(contentSize, contentSize));
    }
    
    void setPopupMenuEnabled(boolean enabled) {
        // popupMenu.setEnabled(enabled);
        if (enabled) {
            setComponentPopupMenu(popupMenu);
        } else {
            setComponentPopupMenu(null);
        }
    }

    void updateValue() {
        int value = data.getCell(majorIndex, minorIndex);
        String displayValue = (value == DisplayData.UNSET_CELL) 
                            ? "" 
                            : Integer.toString(value);
        setText(displayValue);
    }

    
    private class CellPopupMenu extends JPopupMenu {
        
        private static final long serialVersionUID = 1L;

        public CellPopupMenu() {
            for (int i = 1; i <= data.getNumbers(); i++) {
                JMenuItem item = add(Integer.toString(i));
                item.addActionListener(new ChangeCellActionListener(i));
            }
            
            JMenuItem removeOption = add("remove");
            removeOption.addActionListener(
                    new ChangeCellActionListener(DisplayData.UNSET_CELL));
        }
        
        private class ChangeCellActionListener implements ActionListener {
            
            private final int assignedValue;
            
            public ChangeCellActionListener(int assignedValue) {
                this.assignedValue = assignedValue;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                data.setCell(majorIndex, minorIndex, assignedValue);
            }
        }
    }
}
