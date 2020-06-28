package sudoku.model;

import java.util.BitSet;

/**
 * A SudokuBoard represents an intelligent board which is able to store sudokus
 * with multiple possibilities for unset cells.
 */
public class SudokuBoard implements Board {
    
    private int boxRows;
    private int boxCols;
    private BitSet[] board;
    private boolean[] isFixed;
    private int numbers;
    private int lastCellSetIndex;
    
    /**
     * Creates a new SudokuBoard with the given box-dimensions. Initially no
     * cells are fixed and every value between {@code 1} and {@code boxRows
     * * boxCols} is possible for every cell.
     * 
     * @param boxRows The amount of rows per box.
     * @param boxCols The amount of columns per box.
     */
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
        } else if ((number < 1) || (number > numbers)) {
            throw new IllegalArgumentException(
                    "This sudoku only allows numbers between 1 and " + numbers);
        } else if (!board[index].get(number - 1)) {
            throw new InvalidSudokuException(
                    "This cell cannot be set to " + number);
        }
        
        // Clear all bits except the set bit at (number - 1).
        board[index].clear(0, number - 1);
        board[index].clear(number, numbers);
        
        isFixed[index] = true;
        lastCellSetIndex = index;

        // Remove the number from all structures containing this cell.
        for (Structure currentStructure : Structure.values()) {
            int currentMajor = getStructNr(index, currentStructure);
            for (int i = 0; i < numbers; i++) {
                removePossibility(currentStructure, currentMajor, i, number);
            }
        }
        
        if (!isEveryNumberSetable()) {
            throw new InvalidSudokuException();
        }
    }

    /**
     * {@inheritDoc}
     * Does nothing if the cell is already set to a fixed value.
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
     * Gets all possible values a cell can be assigned to without making the
     * sudoku invalid.
     * 
     * @param absoluteIndex the absolute internally used index.
     * @return An array containing the values still possible for a cell, 
     *         or {@code null} if the cell is already set.
     * @see SudokuBoard#getPossibilities(Structure, int, int)
     */
    private int[] getPossibilities(int absoluteIndex) {
        if (isFixed[absoluteIndex]) {
            return null;
        }
        
        BitSet cell = board[absoluteIndex];
        int[] possibilities = new int[cell.cardinality()];
        
        /*
         * Traverse all possibilities by finding the next possibility bit after 
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
        return new int[] {
                getStructNr(lastCellSetIndex, Structure.ROW), 
                getStructNr(lastCellSetIndex, Structure.COL) };
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
     * Because the boards are read as numbers, if a board contains less numbers
     * than the other, it is treated as smaller.
     */
    @Override
    public int compareTo(Board other) {
        
        // Compare the size first.
        if (numbers > other.getNumbers()) {
            return 1;
        } else if (numbers < other.getNumbers()) {
            return -1;
        }
        
        Structure struct = Structure.ROW;
        for (int structNr = 0; structNr < numbers; structNr++) {
            for (int cellNr = 0; cellNr < numbers; cellNr++) {
                int a = getCell(struct, structNr, cellNr);
                int b = other.getCell(struct, structNr, cellNr);
                if (a != b) {
                    if ((a == Board.UNSET_CELL) 
                            || ((b != Board.UNSET_CELL) && (a > b))) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        }
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Board clone() {
        SudokuBoard copy;
        
        try {
            copy = (SudokuBoard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        
        // Deep clone for arrays.
        copy.isFixed = isFixed.clone();
        copy.board = board.clone();
        for (int i = 0; i < board.length; i++) {
            copy.board[i] = (BitSet) board[i].clone();
        }
        
        return copy;
    }

    /**
     * {@inheritDoc}
     * Unset cells are represented by a dot. Leading whitespace is added so that
     * every cells string representation has the same length and the columns are
     * aligned properly.
     */
    @Override
    public String prettyPrint() {
        return printHelper(" ", "\n");
    }
    
    /**
     * {@inheritDoc}
     * Unset cells are represented by a dot. Leading whitespace is added so that
     * every cells string representation has the same length and different
     * sudokus can be compared better.
     */
    @Override
    public String toString() {
        return printHelper(" ", " ");
    }
    
    /**
     * Returns a string representation of the sudoku. This method concatenates
     * the string representation of each cell in a row with the column separator
     * and each row with the row separator. A cell is represented by its value
     * if it is set, and by a dot if not.
     * 
     * @param colSeperator The delimiter between each column.
     * @param rowSeperator The delimiter between each row.
     * @return The string representation.
     */
    private String printHelper(String colSeparator, String rowSeperator) {
        StringBuilder result = new StringBuilder();
        int maxDigits = (int) (Math.log10(numbers) + 1);
        int lastIndex = (numbers * numbers) - 1;
        
        for (int i = 0; i <= lastIndex; i++) {
            
            // Create the string representation and format it to equal length.
            String cell = isFixed[i] ? Integer.toString(getFixedCell(i)) : ".";
            result.append(String.format("%" + maxDigits + "s", cell));

            // If necessary, append the row or column delimiter.
            if (i < lastIndex) {
                if ((i + 1) % numbers == 0) {
                    result.append(rowSeperator);
                } else {
                    result.append(colSeparator);
                }
            }
        }
        return result.toString();
    }

    /**
     * Returns the content of a fixed cell with the given index by returning the
     * first (and only) possibility of the cell.
     * 
     * @param index The absolute index of the fixed cell.
     * @return The value of the cell.
     */
    private int getFixedCell(int index) {
        assert isFixed[index];
        return board[index].nextSetBit(0) + 1;
    }
    
    /**
     * Converts from the given coordinates referring to a specific coordinate
     * type to the absolute index which is used internally to store the sudoku.
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
        return (y * numbers + x);
    }
    
    /**
     * Calculates from an absolute index to the major coordinate in a target 
     * coordinate system.
     * 
     * @param index The absolute index of a cell.
     * @param target The target coordinate system.
     * @return The major coordinate of the cell.
     */
    private int getStructNr(int index, Structure target) {
        int x = index / numbers;
        int y = index % numbers;
        
        switch (target) {
        case BOX:
            return ((x / boxRows) * boxRows + (y / boxCols));
            
        case ROW:
            return x;
            
        case COL:
            return y;
            
        default:
            throw new IllegalArgumentException(
                    "Unexpected structure: " + target);
        }
    }
    
    /**
     * 
     * @return
     */
    private boolean isEveryNumberSetable() {
        for (Structure struct : Structure.values()) {
            System.out.println();
            for (int structNr = 0; structNr < numbers; structNr++) {
                if (!isEveryNumberSetable(struct, structNr)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isEveryNumberSetable(Structure struct, int structNr) {
        int[] possibleCells = computePossibleCells(struct, structNr);
        
        for (int number = 0; number < numbers; number++) {
            if (possibleCells[number] == 0) {
                System.out.println("num:"+number+", posscells:"+possibleCells[number]);
                /*
                 * A number cannot be assigned to any cell in the current
                 * structure.
                 */
                return false;
            }
        }
        
        for (int cellNr = 0; cellNr < numbers; cellNr++) {
            
            /*
             * Indicates if the current cell is occupied by a number that cannot
             * be assigned to any other cell in the current structure.
             */
            boolean isOccupied = false;
            
            int[] possibilities = getPossibilities(struct, structNr, cellNr);

            if (possibilities != null) {
                for (int possibility : possibilities) {
                    
                    // The amount of cells that can contain the current number.
                    int currentPossibleCells = possibleCells[possibility - 1];
                    
                    if (currentPossibleCells == 1) {
                        if (isOccupied) {
                            /*
                             * Two or more values in this structure can only be
                             * assigned to the current cell. As a cell can only
                             * hold one value, not every number can be set.
                             */
                            return false;
                        }
                        isOccupied = true;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Computes the amount of possible cells a number can be placed at in the
     * specified structure.
     * 
     * @param struct The type of the structure.
     * @param structNr The number of the structure.
     * @return For each number the array contains the amount of cells in the
     *         structure this number can be assigned to, starting with the
     *         number 1 at index 0.
     */
    private int[] computePossibleCells(Structure struct, int structNr) {
        int[] amountsOfPossibleCells = new int[numbers];
        for (int cellNr = 0; cellNr < numbers; cellNr++) {
            int[] possibilities = getPossibilities(
                    struct, structNr, cellNr);
            if (possibilities != null) {
                for (int possibility : possibilities) {
                    amountsOfPossibleCells[possibility - 1]++;
                }
            }
        }
        return amountsOfPossibleCells;
    }
}
