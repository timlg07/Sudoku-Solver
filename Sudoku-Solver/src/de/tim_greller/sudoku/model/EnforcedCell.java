package de.tim_greller.sudoku.model;

public class EnforcedCell implements Saturator {

    /** 
     * {@inheritDoc}
     * <p>This strategy traverses all cells of the board and sets the cells that
     * have only one possible value left to this value.</p>
     */
    @Override
    public boolean saturate(Board board) throws UnsolvableSudokuException {
        boolean modifiedBoard = false;
        Structure struct = Structure.ROW;
        
        for (int major = 0; major < board.getNumbers(); major++) {
            for (int minor = 0; minor < board.getNumbers(); minor++) {
                int[] possibilities = board.getPossibilities(
                        struct, major, minor);
                if ((possibilities != null) && (possibilities.length == 1)) {
                    // Cell is unset and can be set to exactly one value.
                    try {
                        board.setCell(struct, major, minor, possibilities[0]);
                    } catch (InvalidSudokuException e) {
                        throw new UnsolvableSudokuException();
                    }
                    modifiedBoard = true;
                }
            }
        }
        
        return modifiedBoard;
    }

}
