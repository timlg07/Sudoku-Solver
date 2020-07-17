package sudoku.gui.model;

public enum DisplayDataChange {
    
    /**
     * A completely new sudoku with a eventually different size was loaded.
     */
    SUDOKU_LOADED,
    
    /**
     * The current sudoku was changed by an edit directly from the user.
     */
    USER_CHANGE,
    
    /**
     * The current sudoku was changed by a solver functionality such as suggest
     * and solve.
     */
    SOLVER_CHANGE,
    
    /**
     * The last edit (from the user or the solver) was reverted.
     */
    UNDO,
    
    /**
     * All operations on the current sudoku were disabled or enabled.
     */
    OPERATIONS_ENABLE_STATE;
}
