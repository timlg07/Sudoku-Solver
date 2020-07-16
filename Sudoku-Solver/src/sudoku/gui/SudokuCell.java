package sudoku.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class SudokuCell extends JLabel {

    private final int majorCoord;
    private final int minorCoord;
    private final DisplayData data;
    private int value;

    public SudokuCell(int major, int minor, DisplayData data) {
        majorCoord = major;
        minorCoord = minor;
        this.data = data;
        updateValue();
        setComponentPopupMenu(new CellPopupMenu(data.getNumbers()));
    }

    /**
     * @param newValue The new Value that should be displayed in this cell.
     */
    public void updateValue() {
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
        
        public CellPopupMenu(int numbers) {
            for (int i = 1; i < numbers; i++) {
                JMenuItem item = new JMenuItem(Integer.toString(i));
                item.addActionListener(new ChangeCellActionListener(i));
                add(item);
            }
            add(new JMenuItem("remove"));
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
