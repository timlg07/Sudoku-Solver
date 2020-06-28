package sudoku.model;

/**
 * Exception if board gets unsolvable.
 */
public class UnsolvableSudokuException extends Exception {
    
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception without a user defined message.
     */
    public UnsolvableSudokuException() {
        super();
    }    

    /**
     * Creates a new exception with a user defined message.
     * 
     * @param errorText The error description.
     */
    public UnsolvableSudokuException(String errorText) {
        super(errorText);
    }
    
}
