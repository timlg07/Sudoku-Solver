package de.tim_greller.sudoku.model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class SudokuBoardSolver implements SudokuSolver {

    /**
     * A list of all registered solution strategies.
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
     * @param board The board that gets modified by the saturators.
     * @throws UnsolvableSudokuException The given board is not solvable.
     */
    private void saturateDirect(Board board) throws UnsolvableSudokuException {
        boolean saturated = false;
        while (!saturated) {
            saturated = true; // Assume that no further changes are needed.
            for (Saturator saturator : saturators) {
                if (saturator.saturate(board)) {
                    // The saturators must be applied to the changed board.
                    saturated = false;
                }
            }
        }
    }

    @Override
    public Board findFirstSolution(Board board) {
        return solve(board, false).get(0);
    }

    @Override
    public List<Board> findAllSolutions(Board board) {
        return solve(board, true);
    }
    
    /**
     * 
     * @param board
     * @return
     */
    private List<Board> getCandidates(Board board) {
        List<Board> candidates = new LinkedList<Board>();
        Structure struct = Structure.ROW; // The coordinate system used here
        
        int[] minPossibilitiesCell = new int[2]; // The {row, col} coordinates
        int[] minPossibleValues = new int[board.getNumbers()];
        
        // Find the cell with the minimum amount of possibilities.
        for (int structNr = 0; structNr < board.getNumbers(); structNr++) {
            for (int cellNr = 0; cellNr < board.getNumbers(); cellNr++) {
                int[] current 
                        = board.getPossibilities(struct, structNr, cellNr);
                if ((current != null) 
                        && (current.length < minPossibleValues.length)) {
                    minPossibilitiesCell[0] = structNr;
                    minPossibilitiesCell[1] = cellNr;
                    minPossibleValues = current;
                }
            }
        }
        
        // Create a board for each possibility the found cell can be set to.
        for (int possibility : minPossibleValues) {
            Board candidate = board.clone();
            try {
                candidate.setCell(struct, minPossibilitiesCell[0], 
                        minPossibilitiesCell[1], possibility);
            } catch (InvalidSudokuException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            candidates.add(candidate);
        }
        return candidates;
    }
    
    /**
     * 
     * @param board
     * @param requestAllSolutions
     * @return
     */
    private List<Board> solve(Board board, boolean requestAllSolutions) {
        List<Board> solutions = new LinkedList<Board>();
        Deque<Board> candidates = new LinkedList<Board>();
        candidates.push(board);
        
        while (!candidates.isEmpty()) {
            Board currentBoard = candidates.pop();
            try {
                saturateDirect(currentBoard);
            } catch (UnsolvableSudokuException e) {
                // Current board not solvable, try with next one.
                continue;
            }
            
            if (currentBoard.isSolution()) {
                solutions.add(currentBoard);
                if (!requestAllSolutions) {
                    return solutions;
                }
            } else {
                candidates.addAll(getCandidates(currentBoard));
            }
        }
        
        return solutions;
    }

}
