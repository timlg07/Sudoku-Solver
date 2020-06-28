package sudoku.model;

import java.util.List;

/**
 * An interface for solving Sudoku puzzles.
 * 
 * This interface implements solving Sudokus by backtracking. For speeding up
 * the computation of a solution, additional strategies in the form of
 * {@link Saturator} objects can be added.
 */
public interface SudokuSolver {
    
    /**
     * Registers a solution strategy at this {@link SudokuSolver}. This strategy
     * will be used in all following tries to find a solution.
     * 
     * @param saturator A solution strategy.
     */
    void addSaturator(Saturator saturator);

    /**
     * Applies all registered solution strategies on the game board
     * {@code board} until a global fix point will be reached.
     * 
     * While saturating, {@code board.clone()} is called. Thus, an object
     * different from {@code board} is returned.
     *
     * @param board The Sudoku puzzle on which the solution strategies will be
     *        applied. Will not be changed.
     * @return A saturated version of {@code board} or {@code null}, if it is
     *         not solvable.
     */
    Board saturate(Board board);

    /**
     * Finds a single solution for a given Sudoku puzzle by applying the
     * registered solution strategies and by backtracking. The result is
     * repeatable, i.e., for a given Sudoku always the same solution is
     * computed, independent of the number of (other) solutions it has.
     * 
     * While solving, {@code board.clone()} is called. Thus, an object different
     * from {@code board} is returned.
     * 
     * @param board The Sudoku to find a solution for. Will not be changed.
     * @return A solution of {@code board} or {@code null}, if it is not
     *         solvable.
     */
    Board findFirstSolution(Board board);

    /**
     * Finds all solutions for a given Sudoku puzzle by applying the registered
     * solution strategies and by backtracking. All possible solution are
     * computed. The result is repeatable, i.e., for a given Sudoku always the
     * same solutions in the same order are computed.
     * 
     * @param board The Sudoku to find the solutions for. Will not be changed.
     * @return A list of all solutions of {@code board}.
     */
    List<Board> findAllSolutions(Board board);
    
}
