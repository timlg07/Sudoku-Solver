package sudoku.solver;

/**
 * Exception if one likes to set a number which is not possible.
 */
public class InvalidSudokuException extends Exception {
    
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception without a user defined message.
     */
    public InvalidSudokuException() {
        super();
    }

    /**
     * Creates a new exception with a user defined message.
     * 
     * @param errorText The error description.
     */
    public InvalidSudokuException(String errorText) {
        super(errorText);
    }
    
}
