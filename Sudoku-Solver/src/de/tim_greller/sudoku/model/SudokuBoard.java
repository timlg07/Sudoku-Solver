package de.tim_greller.sudoku.model;

import java.util.Arrays;
import java.util.BitSet;

/**
 *
 */
public class SudokuBoard implements Board {
    
    private int boxRows;
    private int boxCols;
    private BitSet[] board;
    private final boolean[] isFixed;
    private int numbers;
    
    public SudokuBoard(int boxRows, int boxCols) {
        this.boxRows = boxRows;
        this.boxCols = boxCols;
        numbers = boxRows * boxCols;
        
        int boardElements = numbers * numbers;
        board = new BitSet[boardElements];
        for (int i = 0; i < boardElements; i++) {
            board[i] = new BitSet(numbers);
            board[i].set(0, numbers);
        }
        isFixed = new boolean[boardElements];
    }

    @Override
    public void setCell(Structure struct, int major, int minor, int number) 
            throws InvalidSudokuException {
        int index = calculateIndex(struct, major, minor);
        if (isFixed[index]) {
            throw new IllegalStateException("This cell is already fixed.");
        } else if (!board[index].get(number - 1)) {
            throw new InvalidSudokuException(
                    "This cell cannot be set to " + number);
        }
        board[index].clear(0, numbers);
        board[index].set(number - 1);
        isFixed[index] = true;

        // Remove the number from all structures containing this cell.
        for (Structure currentStructure : Structure.values()) {
            int currentMajor = getRelativeIndex(index, currentStructure);
            for (int i = 0; i < numbers; i++) {
                removePossibility(currentStructure, currentMajor, i, number);
            }
        }
    }
    
    @Override
    public void removePossibility(Structure struct, int major, int minor,
            int number) throws InvalidSudokuException {
        int index = calculateIndex(struct, major, minor);
        if (!isFixed[index]) {
            board[index].clear(number - 1);

            if (board[index].isEmpty()) {
                throw new InvalidSudokuException("The sudoku contains a "
                        + "cell with no possibilities left");
            }
        }
    }
    
    @Override
    public String prettyPrint() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numbers * numbers; i++) {
            result.append(isFixed[i] ? getFixedCell(i) : ".");
            if ((i + 1) % numbers == 0) {
                result.append("\n");
            } else {
                result.append(' ');
            }
        }
        return result.toString();
    }
    
    @Override
    public boolean isSolution() {
        for (boolean isCellFixed : isFixed) {
            if (!isCellFixed) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int[] getPossibilities(Structure struct, int major, int minor) {
        return getPossibilities(calculateIndex(struct, major, minor));
    }
    
    private int[] getPossibilities(int absoluteIndex) {
        BitSet cell = board[absoluteIndex];
        int[] possibilities = new int[cell.cardinality()];
        
        /*
         * Iterate all possibilities by finding the next possibility bit after 
         * the last one and store its index. Begin search at fromIndex = 0.
         */
        for (int index = 0, value = 0; index < possibilities.length; index++) {
            // This value represents the start index for the next iteration and
            // the 1-indizierter value.
            value = cell.nextSetBit(value) + 1;
            possibilities[index] = value;
        }
        
        return possibilities;
    }
    
    @Override
    public int getNumbers() {
        return numbers;
    }
    
    @Override
    public int[] getLastCellSet() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getCell(Structure struct, int major, int minor) {
        int index = calculateIndex(struct, major, minor);
        if (isFixed[index]) {
            return getFixedCell(index);
        } else {
            return Board.UNSET_CELL;
        }
    }
    
    @Override
    public int getBoxRows() {
        return boxRows;
    }
    
    @Override
    public int getBoxColumns() {
        return boxCols;
    }
    
    @Override
    public int compareTo(Board other) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public Board clone() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns the value of a fixed cell with the given index by returning the 
     * first (and only) possibility of the cell.
     * 
     * @param index The absolute index of the cell.
     * @return The value of the cell.
     */
    private int getFixedCell(int index) {
        return board[index].nextSetBit(0) + 1;
    }
    
    /**
     * Converts from the given coordinates referring to a specific 
     * {@link Structure} to the absolute index used internally.
     * 
     * @param struct The coordinate type of the cell.
     * @param  major The major coordinate component of the cell.
     * @param  minor The minor coordinate component of the cell. 
     * @return The absolute index of the cell.
     */
    private int calculateIndex(Structure struct, int major, int minor) {
        int x;
        int y;
        switch(struct) {
        case BOX: 
            x = (major % boxRows) * boxCols + minor % boxCols;
            y = (major / boxRows) * boxRows + minor / boxCols;
            break;
            
        case ROW:
            x = minor;
            y = major;
            break;
            
        case COL:
            x = major;
            y = minor;
            break;
            
        default:
            throw new IllegalArgumentException(
                    "Unexpected structure: " + struct);
        }
        return y * numbers + x;
    }

    private int getBox(Structure struct, int major, int minor) {
        switch(struct) {
        case BOX:
            return major;
        case ROW:
            return (major / boxRows) * boxRows + minor / boxCols;
        case COL:
            return (minor / boxRows) * boxRows + major / boxCols;
        default:
            throw new IllegalArgumentException(
                    "Unexpected structure: " + struct);
        }
    }
    
    private int getRelativeIndex(int index, Structure target) {
        switch (target) {
        case BOX:
            return getBox(Structure.ROW, index / numbers, index % numbers);
        case ROW:
            return index / numbers;
        case COL:
            return index % numbers;
        default:
            throw new IllegalArgumentException(
                    "Unexpected structure: " + target);
        }
    }
}
