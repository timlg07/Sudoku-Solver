package sudoku.gui.model;

public enum DisplayDataChange {
    
    /**
     * A completely new sudoku with a eventually different size was loaded.
     * The new sudoku is initially not locked and may contain different values.
     */
    NEW_SUDOKU,
    
    /**
     * One or more values of the current sudoku were changed by an edit directly
     * from the user, a solver functionality such as suggest and solve, or by an
     * undo operation.
     */
    SUDOKU_VALUES,
    
    /**
     * The sudoku was locked or unlocked which means the ability to execute
     * operations on the current sudoku was disabled or enabled respectively.
     */
    SUDOKU_LOCK;
}
