package sudoku.gui.model;

import java.util.Deque;
import java.util.LinkedList;

import sudoku.util.Observable;
import sudoku.util.Observer;

public class SudokuHistory implements Observer {
    
    private Deque<int[][]> sudokuHistoryData = new LinkedList<>();
    
    /**
     * Constructs a new SudokuHistory and attaches it to the given observable
     * {@link DisplayData}, so that changes in the display data get saved
     * automatically.
     * 
     * @param displayData The display data whose changes should be saved.
     */
    public SudokuHistory(DisplayData displayData) {
        displayData.attachObserver(this);
    }

    /**
     * Saves the given state of a sudoku by pushing it to the sudoku history 
     * {@link Deque}.
     * 
     * @param sudoku The current state of the sudoku that should be saved.
     */
    private void saveSudoku(int[][] sudoku) {
        sudokuHistoryData.push(sudoku);
    }
    
    /**
     * Reverts the last saved edit and returns the state the sudoku was before
     * this edit. 
     * <p>
     * The initial state of the sudoku always remains in the history. If the
     * current state is the only and therefore initial state, {@code null} will
     * be returned. If no sudoku is loaded {@code null} is returned as well.
     * 
     * @return The state of the sudoku after reverting the last change or 
     *         {@code null} if there are no changes that could be reverted.
     */
    public int[][] undo() {
        if (sudokuHistoryData.size() > 1) {
            
            // Revert the last change.
            sudokuHistoryData.pop();
            
            /* 
             * Return the new sudoku state, which is going to be used by 
             * DisplayData and therefore will mutate. A clone of this state will
             * be added automatically.
             */
            return sudokuHistoryData.pop();
            
        } else {
            
            // The stack is empty or contains only an initial state of a sudoku.
            return null;
        }
    }

    @Override
    public void update(Observable observable, Object argument) {
        assert observable instanceof DisplayData;
        assert argument instanceof DisplayDataChange;
        
        if (((DisplayDataChange) argument) == DisplayDataChange.NEW_SUDOKU) {
            /*
             * Remove all states of the previous sudoku before adding states of
             * the new one.
             */
            sudokuHistoryData.clear();
        }
        
        /* Save a copy of the currently displayed board. */
        saveSudoku(((DisplayData) observable).cloneUncheckedBoard());
    }
}
