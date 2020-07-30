package sudoku.gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import sudoku.gui.model.DisplayData;
import sudoku.solver.Structure;
import sudoku.util.Observable;
import sudoku.util.Observer;

public class GameBoardPanel extends Container implements Observer {

    private static final long serialVersionUID = 770590847429244978L;

    /**
     * The border around each box of the sudoku.
     */
    private static final Border BOX_BORDER 
            = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    
    private final List<SudokuCell> cells = new ArrayList<>();
    
    /**
     * 
     * @param data The board data that should be visualized, not {@code null}.
     */
    public GameBoardPanel(DisplayData data) {
        if (data == null) {
            throw new IllegalArgumentException("The data must not be null.");
        }
        
        data.attachObserver(this);
        
        int numbers = data.getNumbers();
        int boxRows = data.getBoxRows();
        int boxCols = data.getBoxCols();
        
        LayoutManager outerLayout = new GridLayout(boxCols, boxRows);
        LayoutManager innerLayout = new GridLayout(boxRows, boxCols);
        
        // Set the layout manager of the container which contains all boxes.
        setLayout(outerLayout);
        
        /*
         * The sudoku is only displayed correctly when the box coordinate system
         * is used. For other structures the index has to be converted.
         */
        assert DisplayData.STRUCT == Structure.BOX;
        
        for (int boxNr = 0; boxNr < numbers; boxNr++) {
            JPanel boxPanel = new JPanel(innerLayout);
            boxPanel.setBorder(BOX_BORDER);
            
            for (int cellNr = 0; cellNr < numbers; cellNr++) {
                int cellValue = data.getCell(boxNr, cellNr);
                boolean isCellSet = (cellValue != DisplayData.UNSET_CELL);
                SudokuCell cellComponent 
                        = new SudokuCell(boxNr, cellNr, data, !isCellSet);
                
                if (isCellSet) {
                    cellComponent.updateValue();
                }
                
                cells.add(cellComponent);
                boxPanel.add(cellComponent);
            }
            add(boxPanel);
        }
    }

    @Override
    public void update(Observable observable, Object argument) {
        /*
         * Each cell could be an observer itself, but the cells have to be
         * stored in this class anyways (to disable/enable the popup menu),
         * so this approach is used to avoid storing all cells twice.
         */
        cells.forEach(SudokuCell::updateValue);
    }
    
    public void setPopupsEnabled(boolean enabled) {
        cells.forEach(c -> c.setPopupMenuEnabled(enabled));
    }
}
