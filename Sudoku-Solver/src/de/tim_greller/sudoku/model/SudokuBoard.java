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
    private int lastCellSetIndex;
    
    public SudokuBoard(int boxRows, int boxCols) {
        this.boxRows = boxRows;
        this.boxCols = boxCols;
        numbers = boxRows * boxCols;
        
        int boardElements = numbers * numbers;
        isFixed = new boolean[boardElements];
        board = new BitSet[boardElements];
        for (int i = 0; i < boardElements; i++) {
            board[i] = new BitSet(numbers);
            board[i].set(0, numbers);
        }
    }

    /**
     * {@inheritDoc}
     * <p>Setting a cells content to a number removes this number from the 
     * possible values in all other cells sharing a structure with the specified
     * cell.</p>
     * 
     * @throws IllegalStateException Trying to overwrite a fixed cell.
     */
    @Override
    public void setCell(Structure struct, int major, int minor, int number) 
            throws InvalidSudokuException {
        int index = calculateIndex(struct, major, minor);
        if (isFixed[index]) {
            throw new IllegalStateException("This cell is already fixed.");
        } else if (number == Board.UNSET_CELL) {
            return;
        } else if (!board[index].get(number - 1)) {
            throw new InvalidSudokuException(
                    "This cell cannot be set to " + number);
        }
        
        board[index].clear(0, numbers);
        board[index].set(number - 1);
        isFixed[index] = true;
        lastCellSetIndex = index;

        // Remove the number from all structures containing this cell.
        for (Structure currentStructure : Structure.values()) {
            int currentMajor = getRelativeIndex(index, currentStructure);
            for (int i = 0; i < numbers; i++) {
                removePossibility(currentStructure, currentMajor, i, number);
            }
        }
        // TODO: print statements for quick debugging should be removed.
        // System.out.println("Set num " + number);
        // System.out.println(unprettyPrint());
    }
    
    /**
     * {@inheritDoc}
     * Does nothing if the cell is already set to fixed value.
     */
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
    
    /**
     * {@inheritDoc} 
     * Unset cells are represented by a dot. Leading whitespace is added so that
     * every cells string representation has the same length and the columns are
     * aligned properly.
     */
    @Override
    public String prettyPrint() {
        StringBuilder result = new StringBuilder();
        int maxDigits = (int) (Math.log10(numbers) + 1);
        
        for (int i = 0; i < numbers * numbers; i++) {
            
            // Create the string representation and format it to equal length.
            String cell = isFixed[i] ? Integer.toString(getFixedCell(i)) : ".";
            result.append(String.format("%" + maxDigits + "s", cell));

            // Append the row or coloumn delimiter.
            if ((i + 1) % numbers == 0) {
                result.append("\n");
            } else {
                result.append(' ');
            }
        }
        return result.toString();
    }
    
    // TODO: Remove.
    public String unprettyPrint() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numbers * numbers; i++) {
            result.append(isFixed[i] ? getFixedCell(i) 
                                     : Arrays.toString(getPossibilities(i)));
            if ((i + 1) % numbers == 0) {
                result.append("\n");
            } else {
                result.append(' ');
            }
        }
        return result.toString();
    }
    
    /**
     * {@inheritDoc}
     * A board is correctly solved if and only if every cells content was set
     * successfully. 
     */
    @Override
    public boolean isSolution() {
        for (boolean isCellFixed : isFixed) {
            if (!isCellFixed) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getPossibilities(Structure struct, int major, int minor) {
        return getPossibilities(calculateIndex(struct, major, minor));
    }
    
    /**
     * @see SudokuBoard#getPossibilities(Structure, int, int)
     * @param absoluteIndex the absolute internally used index.
     * @return An array containing the values still possible for a cell, 
     *         or {@code null} if the cell is already set.
     */
    private int[] getPossibilities(int absoluteIndex) {
        if (isFixed[absoluteIndex]) {
            return null;
        }
        
        BitSet cell = board[absoluteIndex];
        int[] possibilities = new int[cell.cardinality()];
        
        /*
         * Iterate all possibilities by finding the next possibility bit after 
         * the last one and store its index.
         * The value represents the fromIndex for the next iteration and the
         * current 1-indexed possibility value that gets stored.
         */
        for (int index = 0, value = 0; index < possibilities.length; index++) {
            value = cell.nextSetBit(value) + 1;
            possibilities[index] = value;
        }
        
        return possibilities;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumbers() {
        return numbers;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getLastCellSet() {
        return new int[]{
                lastCellSetIndex / numbers, lastCellSetIndex % numbers};
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getCell(Structure struct, int major, int minor) {
        int index = calculateIndex(struct, major, minor);
        if (isFixed[index]) {
            return getFixedCell(index);
        } else {
            return Board.UNSET_CELL;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxRows() {
        return boxRows;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxColumns() {
        return boxCols;
    }
    
    /**
     * not implemented
     */
    @Override
    public int compareTo(Board other) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Board clone() {
        Board clone = new SudokuBoard(boxRows, boxCols);
        Structure struct = Structure.ROW;
        for (int structNr = 0; structNr < numbers; structNr++) {
            for (int element = 0; element < numbers; element++) {
                int value = getCell(struct, structNr, element);
                if (value != Board.UNSET_CELL) {
                    try {
                        clone.setCell(struct, structNr, element, value);
                    } catch (InvalidSudokuException e) {
                        // This should not happen.
                        e.printStackTrace();
                    }
                }
            }
        }
        return clone;
    }

    /**
     * Returns the content of a fixed cell with the given index by returning the 
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
     * {@link Structure} to the absolute index used internally to store the 
     * boards data.
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

    // TODO: remove
    /*private int getBox(Structure struct, int major, int minor) {
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
    }//*/
    
    /**
     * Calculates from an absolute index to the major coordinate in a target 
     * coordinate system.
     * 
     * @param index The absolute index of a cell.
     * @param target The target coordinate system.
     * @return The major coordinate of the cell.
     */
    private int getRelativeIndex(int index, Structure target) {
        int row = index / numbers;
        int col = index % numbers;
        
        switch (target) {
        case BOX:
            return ((row / boxRows) * boxRows + (col / boxCols));
            
        case ROW:
            return row;
            
        case COL:
            return col;
            
        default:
            throw new IllegalArgumentException(
                    "Unexpected structure: " + target);
        }
    }
}
