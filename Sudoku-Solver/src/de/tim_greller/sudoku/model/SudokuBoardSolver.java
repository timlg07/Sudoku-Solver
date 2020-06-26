package de.tim_greller.sudoku.model;

import java.util.ArrayList;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Board> findAllSolutions(Board board) {
        // TODO Auto-generated method stub
        return null;
    }

}
