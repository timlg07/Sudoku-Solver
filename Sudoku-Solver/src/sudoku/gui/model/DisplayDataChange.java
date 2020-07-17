package sudoku.gui.model;

public enum DisplayDataChange {
    
    /**
     * A completely new sudoku with a eventually different size was loaded.
     */
    NEW_SUDOKU,
    
    /**
     * One or more values of the current sudoku were changed by an edit directly
     * from the user, a solver functionality such as suggest and solve, or by an
     * undo operation.
     */
    SUDOKU_VALUES,
    
    /**
     * All operations on the current sudoku were disabled or enabled.
     */
    OPERATIONS_ENABLED_STATE;
}
