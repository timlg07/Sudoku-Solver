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
import sudoku.gui.model.DisplayDataChange;
import sudoku.util.Observable;
import sudoku.util.Observer;

public class SudokuCell extends JLabel implements Observer {

    private static final long serialVersionUID = 1L;
    
    /**
     * The foreground color of not modifiable cells.
     */
    private static final Color NOT_MODIFIABLE_FG = Color.RED;
    
    /**
     * The lowered bordered of every cell.
     */
    private static final Border CELL_BORDER 
            = BorderFactory.createBevelBorder(BevelBorder.LOWERED);

    private static final int FONT_SIZE = 14;
    private static final Dimension PREF_SIZE 
            = new Dimension(FONT_SIZE * 3, FONT_SIZE * 3);
    
    private final int majorCoord;
    private final int minorCoord;
    private final DisplayData data;
    private int value;

    public SudokuCell(int major, int minor, DisplayData data) {
        super("", SwingConstants.CENTER);
        
        majorCoord = major;
        minorCoord = minor;
        this.data = data;
        
        if (data.isCellModifiable(major, minor)) {
            setComponentPopupMenu(new CellPopupMenu(data.getNumbers()));
        } else {
            setForeground(NOT_MODIFIABLE_FG);
        }
        setBorder(CELL_BORDER);
        setPreferredSize(PREF_SIZE);
        setFont(getFont().deriveFont((float) FONT_SIZE));
        
        updateValue();
        data.attachObserver(this);
    }

    @Override
    public void update(Observable observable, Object argument) {
        assert observable instanceof DisplayData;
        assert data == ((DisplayData) observable);
        assert argument instanceof DisplayDataChange;
        
        switch ((DisplayDataChange) argument) {
        case NEW_SUDOKU:
            /*
             * This cell should no longer be updated as the sudoku it was part
             * of got replaced by a new one.
             */
            data.detachObserver(this);
            break;
            
        case OPERATIONS_ENABLED_STATE:
            getComponentPopupMenu().setEnabled(
                    data.isOperationOnSudokuAllowed());
            break;
            
        default:
            updateValue();
        }
    }
    
    private void updateValue() {
        int newValue = data.getCell(majorCoord, minorCoord);
        if (value != newValue) {
            value = newValue;
            setText(toString());
        }
    }
    
    @Override
    public String toString() {
        return (value == DisplayData.UNSET_CELL) ? "" : Integer.toString(value);
    }

    
    private class CellPopupMenu extends JPopupMenu {
        
        private static final long serialVersionUID = 1L;

        public CellPopupMenu(int numbers) {
            super();
            
            for (int i = 1; i <= numbers; i++) {
                JMenuItem item = new JMenuItem(Integer.toString(i));
                item.addActionListener(new ChangeCellActionListener(i));
                add(item);
            }
            JMenuItem removeOption = new JMenuItem("remove");
            removeOption.addActionListener(
                    new ChangeCellActionListener(DisplayData.UNSET_CELL));
            add(removeOption);
        }
        
        private class ChangeCellActionListener implements ActionListener {
            
            private final int assignedValue;
            
            public ChangeCellActionListener(int assignedValue) {
                this.assignedValue = assignedValue;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                // Use attributes of SudokuCell.this:
                data.setCell(majorCoord, minorCoord, assignedValue);
            }
        }
    }
}
