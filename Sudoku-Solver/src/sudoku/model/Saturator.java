package sudoku.model;

/**
 * An interface which must be implemented by classes which provide a solution
 * strategy for a Sudoku puzzle.
 */
public interface Saturator {

    /**
     * Applies a solution strategy on a Sudoku, i.e., it tries to bring a Sudoku
     * a bit nearer torwards its solution. This is done by setting cells or
     * removing options.
     * 
     * As invariant, if a solvable Sudoku is passed to this method, then the
     * Sudoku is also solvable after the method's completion.
     * 
     * During the execution the parameter {@code board} is changed.
     * 
     * @param board The Sudoku to solve.
     * @return {@code true} if the {@code board} was changed, {@code false}
     *         otherwise.
     * @throws UnsolvableSudokuException The solution try resulted in an invalid
     *         Sudoku. This means that the passed Sudoku was not solvable.
     */
    boolean saturate(Board board) throws UnsolvableSudokuException;

}
