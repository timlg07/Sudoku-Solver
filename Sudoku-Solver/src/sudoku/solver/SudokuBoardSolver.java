package sudoku.solver;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * A SudokuBoardSolver uses the registered saturators and backtracking to solve
 * a sudoku.
 */
public class SudokuBoardSolver implements SudokuSolver {

    /**
     * The list of all registered solution strategies.
     */
    private List<Saturator> saturators = new ArrayList<Saturator>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSaturator(Saturator saturator) {
        saturators.add(saturator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board saturate(Board board) {
        Board resultingBoard = board.clone();
        
        try {
            saturateDirect(resultingBoard);
        } catch (UnsolvableSudokuException e) {
            return null;
        }
        
        return resultingBoard;
    }
    
    /**
     * Changes the given board by applying all registered saturators 
     * repeatedly on it as long as at least one of the saturators modifies it.
     * 
     * @param board The board that is directly modified by the saturators.
     * @throws UnsolvableSudokuException The given board is not solvable.
     */
    private void saturateDirect(Board board) throws UnsolvableSudokuException {
        boolean saturated = false;
        
        while (!saturated) {
            saturated = true; // Assume that no further changes can be done.
            
            for (Saturator saturator : saturators) {
                if (saturator.saturate(board)) {
                    
                    // The saturators must also be applied to the changed board.
                    saturated = false;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board findFirstSolution(Board board) {
        return solve(board, false).stream().findFirst().orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Board> findAllSolutions(Board board) {
        return solve(board, true);
    }
    
    /**
     * Generates all boards where one of the possible values is successfully
     * assigned to the cell with the lowest amount of possible values. 
     * These boards can be used as candidates for further backtracking steps.
     * 
     * @param board The board all other candidates are based on. Will not be
     *        changed.
     * @return A list of all candidates, each one with a different value 
     *         assigned to the cell with the minimum amount of possibilities.
     */
    private List<Board> generateCandidates(Board board) {
        List<Board> candidates = new LinkedList<Board>();
        Structure struct = Structure.ROW; // The coordinate system used here.
        
        // Information about the cell with the lowest amount of possible values.
        int minPossRow = 0;
        int minPossCol = 0;
        int[] minPossValues = new int[board.getNumbers() + 1];
        
        // Find the cell with the minimum amount of possibilities.
        for (int structNr = 0; structNr < board.getNumbers(); structNr++) {
            for (int cellNr = 0; cellNr < board.getNumbers(); cellNr++) {
                int[] current 
                        = board.getPossibilities(struct, structNr, cellNr);
                if ((current != null)
                        && (current.length < minPossValues.length)) {
                    minPossRow = structNr;
                    minPossCol = cellNr;
                    minPossValues = current;
                }
            }
        }
        
        // Create a board for each possibility the found cell can be set to.
        for (int possibility : minPossValues) {
            Board candidate = board.clone();
            try {
                candidate.setCell(struct, minPossRow, minPossCol, possibility);
            } catch (InvalidSudokuException e) {
                continue; // Ignore possibilities leading to an invalid sudoku.
            }
            candidates.add(candidate);
        }
        
        return candidates;
    }
    
    /**
     * Tries to solve a given sudoku using backtracking. The saturators are used
     * to speed up the process of sorting out unsolvable boards.
     * 
     * @param board The sudoku that should be solved. Will not be changed.
     * @param requestAllSolutions Whether all solutions are needed or one is
     *        sufficient.
     * @return A List containing one or, if requested, all solutions of board.
     *         For unsolvable sudokus an empty list is returned.
     */
    private List<Board> solve(Board board, boolean requestAllSolutions) {
        List<Board> solutions = new LinkedList<Board>();
        Deque<Board> candidates = new LinkedList<Board>();
        candidates.push(board.clone());
        
        while (!candidates.isEmpty()) {
            Board currentBoard = candidates.pop();
            try {
                saturateDirect(currentBoard);
            } catch (UnsolvableSudokuException e) {
                continue; // Current board not solvable, try with next one.
            }
            
            if (currentBoard.isSolution()) {
                solutions.add(currentBoard);
                if (!requestAllSolutions) {
                    return solutions;
                }
            } else {
                candidates.addAll(generateCandidates(currentBoard));
            }
        }
        
        return solutions;
    }

}
