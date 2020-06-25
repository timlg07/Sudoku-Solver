package de.tim_greller.sudoku.model;

import java.util.ArrayList;
import java.util.List;

public class SudokuBoardSolver implements SudokuSolver {

    private List<Saturator> saturators;
    
    public SudokuBoardSolver() {
        saturators = new ArrayList<Saturator>();
    }
    
    @Override
    public void addSaturator(Saturator saturator) {
        saturators.add(saturator);
    }

    @Override
    public Board saturate(Board board) {
        Board resultingBoard = board.clone();
        System.out.println("#################################################");
        try {
            saturateDirect(resultingBoard);
        } catch (UnsolvableSudokuException e) {
            return null;
        }
        return resultingBoard;
    }
    
    private void saturateDirect(Board board) throws UnsolvableSudokuException {
        boolean saturated = false;
        while (!saturated) {
            saturated = true; // Assume that no further changes are needed.
            for (Saturator saturator : saturators) {
                if (saturator.saturate(board)) {
                    // The saturators must also be applied to the changed board.
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
