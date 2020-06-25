package de.tim_greller.sudoku.model;

public class EnforcedNumber implements Saturator {
    
    /** 
     * {@inheritDoc}
     * <p>This strategy traverses all cells of all structures and sets the 
     * values that have only one possible cell they could be placed at.</p>
     */
    @Override
    public boolean saturate(Board board) throws UnsolvableSudokuException {
        boolean modifiedBoard = false;
        
        for (Structure currentStructure : Structure.values()) {
            for (int structNr = 0; structNr < board.getNumbers(); structNr++) {
                if (saturateStructure(board, currentStructure, structNr)) {
                    modifiedBoard = true;
                }
            }
        }
        
        return modifiedBoard;
    }
    
    /**
     * Applies the strategy to the specified structure and returns whether the
     * board was modified or not.
     * 
     * @param board The board that should be modified.
     * @param struct The type of the structure that is currently traversed.
     * @param major The number of the structure that is currently traversed.
     * @return {@code true} if the board was modified.
     * @throws UnsolvableSudokuException The passed sudoku is not solvable.
     */
    private boolean saturateStructure(Board board, Structure struct, int major) 
            throws UnsolvableSudokuException {
        boolean modifiedBoard = false;
        int[] amountsOfpossibleCells = getAmounts(board, struct, major);
        
        for (int minor = 0; minor < board.getNumbers(); minor++) {
            int[] possibilities = board.getPossibilities(struct, major, minor);
            if (possibilities != null) {
                for (int possibility : possibilities) {
                    if (amountsOfpossibleCells[possibility - 1] == 1) {
                        try {
                            board.setCell(struct, major, minor, possibility);
                        } catch (InvalidSudokuException e) {
                            throw new UnsolvableSudokuException();
                        }
                        modifiedBoard = true;
                    }
                }
            }
        }
        
        return modifiedBoard;
    }
    
    /**
     * Returns the amount of possible cells a number can be placed at.
     * 
     * @param board The board providing the possibilities of each cell.
     * @param struct The type of the structure that is currently traversed.
     * @param major The number of the structure that is currently traversed.
     * @return The amount of possible cells for each number, starting with the
     *         number 1 at index 0.
     */
    private int[] getAmounts(Board board, Structure struct, int major) {
        int[] amounts = new int[board.getNumbers()];
        for (int minor = 0; minor < board.getNumbers(); minor++) {
            int[] possibilities = board.getPossibilities(struct, major, minor);
            if (possibilities != null) {
                for (int possibility : possibilities) {
                    amounts[possibility - 1]++;
                }
            }
        }
        return amounts;
    }

}
