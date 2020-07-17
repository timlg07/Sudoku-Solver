package sudoku.gui.model;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import sudoku.util.Observable;
import sudoku.util.Observer;

public class SudokuHistory implements Observer {
    
    private Deque<int[][]> sudokuHistoryData = new LinkedList<>();
    
    public SudokuHistory(DisplayData displayData) {
        displayData.attachObserver(this);
    }

    private void saveSudoku(int[][] sudoku) {
        sudokuHistoryData.push(sudoku);
    }
    
    /**
     * Reverts the last saved edit and returns the state the sudoku was before
     * this edit. If no edits were made to the sudoku the current sudoku gets 
     * returned.
     * 
     * @return The state of the sudoku after reverting the last change or 
     *         {@code null} if the sudoku was never changed.
     */
    public int[][] undo() {
        if (sudokuHistoryData.isEmpty()) {
            return null;
        }
        
        /*
         * One element has to remain in the history, so that loading a sudoku 
         * cannot be reverted.
         */
        if (sudokuHistoryData.size() > 1) {
            sudokuHistoryData.pop();
        }
        
        return sudokuHistoryData.getLast();
    }

    @Override
    public void update(Observable observable, Object argument) {
        assert observable instanceof DisplayData;
        assert argument instanceof DisplayDataChange;
        
        switch ((DisplayDataChange) argument) {
        case SUDOKU_LOADED:
            sudokuHistoryData.clear();
            break;
            
        case UNDO:
            /* Do not store undo changes. */
            break;
        
        default:
            saveSudoku(((DisplayData) observable).cloneUncheckedBoard());
        }
    }

}
