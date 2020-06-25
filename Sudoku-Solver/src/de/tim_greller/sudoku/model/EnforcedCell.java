package de.tim_greller.sudoku.model;

public class EnforcedCell implements Saturator {

    @Override
    public boolean saturate(Board board) throws UnsolvableSudokuException {
        boolean modifiedBoard = false;
        Structure struct = Structure.ROW;
        
        for (int major = 0; major < board.getNumbers(); major++) {
            for (int minor = 0; minor < board.getNumbers(); minor++) {
                int[] possibilities = board.getPossibilities(
                        struct, major, minor);
                int value = board.getCell(struct, major, minor);
                if (possibilities.length == 1 && value == Board.UNSET_CELL) {
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
