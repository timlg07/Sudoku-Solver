package sudoku.gui;

import java.awt.Color;
import java.awt.Component;
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

/**
 * This class models a cell of a graphically visible sudoku board.
 */
public class SudokuCell extends JLabel {
    
    private static final long serialVersionUID = 7427321882456642556L;

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
    
    /**
     * The major index of the position this cell has in the board.
     */
    private final int majorIndex;
    
    /**
     * The minor index of the position this cell has in the board.
     */
    private final int minorIndex;
    
    /**
     * The data model that is visualized by the board this cell is a part of.
     */
    private final DisplayData data;
    
    /**
     * The popup menu the user can use to change the cells value.
     */
    private final JPopupMenu popupMenu;

    /**
     * Creates a new cell at the given position that shows the value of the 
     * given data model at the same position. Unmodifiable cells are highlighted
     * and have no popup menu.
     * 
     * @param major The major coordinate of this cell.
     * @param minor The minor coordinate of this cell.
     * @param data The data model where the values are stored.
     * @param isModifiable Whether this cell can be modified or not.
     */
    public SudokuCell(
            int major, int minor, DisplayData data, boolean isModifiable) {
        super("", SwingConstants.CENTER);
        
        majorIndex = major;
        minorIndex = minor;
        this.data = data;
        
        if (isModifiable) {
            popupMenu = new CellPopupMenu();
        } else {
            popupMenu = null;
            setForeground(FIXED_CELL_COLOR);
        }

        setComponentPopupMenu(popupMenu);
        setFont(getFont().deriveFont((float) FONT_SIZE));
        setDimensions();
        setBorder(CELL_BORDER);
    }
    
    /**
     * Sets the preferred and minimum size of this cell depending on the font
     * size of the cell.
     */
    private void setDimensions() {
        int maxDigits = (int) (Math.log10(data.getNumbers()) + 1);
        int contentSize = maxDigits * FONT_SIZE;
        int sizeWithPadding = contentSize + (FONT_SIZE * 2);
        
        setPreferredSize(new Dimension(sizeWithPadding, sizeWithPadding));
        setMinimumSize(new Dimension(contentSize, contentSize));
    }
    
    /**
     * Enables or disables the popup menu that is used to change the cells 
     * value.
     * 
     * @param enabled Whether the popup menu should be enabled or disabled.
     */
    void setPopupMenuEnabled(boolean enabled) {
        if (enabled) {
            setComponentPopupMenu(popupMenu);
        } else {
            setComponentPopupMenu(null);
        }
    }

    /**
     * Updates the value of this cell to the value currently stored in the data
     * model.
     */
    void updateValue() {
        int value = data.getCell(majorIndex, minorIndex);
        String displayValue = (value == DisplayData.UNSET_CELL) 
                            ? "" 
                            : Integer.toString(value);
        setText(displayValue);
    }

    
    /**
     * This class models the custom popup menu of a cell.
     */
    private final class CellPopupMenu extends JPopupMenu {

        private static final long serialVersionUID = 5643550068619328847L;

        /**
         * Creates a new popup menu with the numbers a cell can be set to and a
         * option to remove the stored value from a cell.
         */
        private CellPopupMenu() {
            for (int i = 1; i <= data.getNumbers(); i++) {
                JMenuItem item = add(Integer.toString(i));
                item.addActionListener(new ChangeCellActionListener(i));
            }
            
            JMenuItem removeOption = add("remove");
            removeOption.addActionListener(
                    new ChangeCellActionListener(DisplayData.UNSET_CELL));
        }
        
        /**
         * The listener for change cell actions of the user. This listener can
         * change the value of a cell in the data model to its assigned value.
         */
        private final class ChangeCellActionListener implements ActionListener {
            
            /**
             * The value this listener was created for.
             */
            private final int assignedValue;
            
            /**
             * Creates a new {@code ChangeCellActionListener} which should
             * listen for the selection of the assigned value. When this action
             * is performed, the cells value in the data model gets updated to
             * the assigned value.
             *  
             * @param assignedValue The value this listener is responsible for.
             */
            private ChangeCellActionListener(int assignedValue) {
                this.assignedValue = assignedValue;
            }
            
            /**
             * Sets the value of the cell which this listener was created for to
             * the value that was assigned to this listener.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                data.setCell(majorIndex, minorIndex, assignedValue);
                Component rootFrame = SudokuCell.this.getRootPane().getParent();
                SudokuDialogMessages.showMessageIfFilled(rootFrame, data);
            }
        }
    }
}
