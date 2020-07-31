package sudoku.gui.model;

import java.util.Deque;
import java.util.LinkedList;

import sudoku.util.Observable;
import sudoku.util.Observer;

/**
 * This class manages all previous states of a sudoku data model and provides
 * the functionality to go back to the last state. New states of the data 
 * model are automatically stored using the observer pattern.
 */
public class SudokuHistory implements Observer {
    
    /**
     * The last sudoku board states as a stack.
     */
    private final Deque<int[][]> sudokuHistoryData;
    
    /**
     * Constructs a new SudokuHistory and attaches it to the given observable
     * {@link DisplayData}, so that changes in the display data get saved
     * automatically.
     * 
     * @param displayData The display data whose changes should be saved.
     */
    public SudokuHistory(DisplayData displayData) {
        sudokuHistoryData = new LinkedList<>();
        sudokuHistoryData.add(displayData.cloneUncheckedBoard());
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
     * be returned.
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

    /**
     * Saves a copy of the current data model state.
     */
    @Override
    public void update(Observable observable, Object argument) {
        if (observable instanceof DisplayData) {
            int[][] sudoku = ((DisplayData) observable).cloneUncheckedBoard();
            saveSudoku(sudoku);
        }
    }
}
