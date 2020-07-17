package sudoku.gui.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import sudoku.io.SudokuFileParser;
import sudoku.solver.Board;
import sudoku.solver.InvalidSudokuException;
import sudoku.solver.Structure;
import sudoku.solver.SudokuBoard;
import sudoku.util.Observable;

public class DisplayData extends Observable {
    
    /**
     * The representation of an unset cell in the unchecked board.
     */
    public static final int UNSET_CELL = -1;
    
    /**
     * The coordinate system used for all boards in this class.
     */
    public static final Structure STRUCT = Structure.BOX;
    
    private volatile int[][] uncheckedBoard;
    private volatile boolean[][] isConstant;
    private int boxRows;
    private int boxCols;
    private int numbers;
    private boolean operationsEnabled;
    private final SudokuHistory history = new SudokuHistory(this);

    public int getCell(int major, int minor) {
        assertIndexInRange(major);
        assertIndexInRange(minor);
        
        return uncheckedBoard[major][minor];
    }
    
    public void setCell(int major, int minor, int value) {
        assertIndexInRange(major);
        assertIndexInRange(minor);
        assertValueInRange(value);
        
        if (uncheckedBoard[major][minor] != value) {
            uncheckedBoard[major][minor] = value;
            setChanged();
        }

        notifyObservers(DisplayDataChange.SUDOKU_VALUES);
        clearChanged();
    }
    
    public boolean isCellModifiable(int major, int minor) {
        assertIndexInRange(major);
        assertIndexInRange(minor);
        
        return !isConstant[major][minor];
    }
    
    private void assertIndexInRange(int index) {
        if ((index < 0) || (index >= numbers)) {
            throw new IllegalArgumentException("The index \"" + index 
                    + "\" is out of range for the current board size.");
        }
    }
    
    private void assertValueInRange(int value) {
        if ((value != UNSET_CELL) && ((value <= 0) || (value > numbers))) {
            throw new IllegalArgumentException("The value \"" + value 
                    + "\" is not allowed in the current board.");
        }
    }
    
    public int getBoxRows() {
        return boxRows;
    }

    public int getBoxCols() {
        return boxCols;
    }

    public int getNumbers() {
        return numbers;
    }
    
    public void loadSudokuFromFile(File sudokuFile) 
            throws InvalidSudokuException, FileNotFoundException, IOException, 
            ParseException {
        Board intelligentBoard = SudokuFileParser.parseToBoard(sudokuFile);
        applyIntelligentBoard(intelligentBoard, true);
        notifyObservers(DisplayDataChange.NEW_SUDOKU);
        clearChanged();
    }

    /**
     * Applies the given intelligent board to the current unchecked board by
     * transferring all values from the intelligent board to a new array and
     * then updating the reference of the unchecked board to the new array.</p>
     * <p>
     * If the intelligent board is marked as initial board, it is allowed to
     * change the sudokus size. It also resets the constant markers and then 
     * sets all values of the intelligent board as unmodifiable constants of the
     * new unchecked board.
     * <p>
     * This method sets the changed flag of the {@link Observable} represented
     * by this DisplayData, but does not notify the observers.
     * <p>
     * After the execution of this method, operations on the sudoku are always
     * allowed, until a operations locks the state (again).
     * 
     * @param board The intelligent board that will be used as unchecked board.
     *              Should not be {@code null}.
     * @param isInitial Signalizes if the board is a new initial board, which
     *                  is allowed to overwrite the sudokus constant markers and
     *                  its size.
     */
    private void applyIntelligentBoard(Board board, boolean isInitial) {
        if (board == null) {
            throw new IllegalArgumentException(
                    "Cannot apply \"null\" as Board.");
        }

        if ((!isInitial) && ((boxCols != board.getBoxColumns()) 
                              || (boxRows != board.getBoxRows()))) {
            throw new IllegalArgumentException(
                      "The intelligent board has a different size than the "
                    + "current unchecked board and is not an initial board "
                    + "which could overwrite the size");
        }
        
        int newSize = board.getNumbers();
        int[][] newUncheckedBoard = new int[newSize][newSize];
        boolean[][] newIsConstant = null;
        if (isInitial) {
            /* Only needed if constants can be overwritten. */
            newIsConstant = new boolean[newSize][newSize];
        }
        
        for (int major = 0; major < newSize; major++) {
            for (int minor = 0; minor < newSize; minor++) {
                int cell = board.getCell(STRUCT, major, minor);
                boolean isSet = (cell != Board.UNSET_CELL);
                
                newUncheckedBoard[major][minor] = (isSet ? cell : UNSET_CELL);
                
                if (isInitial) {
                    assert newIsConstant != null;
                    newIsConstant[major][minor] = isSet;
                }
            }
        }

        if (isInitial) {
            isConstant = newIsConstant;
            numbers = newSize;
            boxCols = board.getBoxColumns();
            boxRows = board.getBoxRows();
        }
        uncheckedBoard = newUncheckedBoard;
        operationsEnabled = true;
        setChanged();
    }
    
    /**
     * Tries to generate an intelligent board based on the current unchecked
     * board.
     * 
     * @return An intelligent board containing the same values as the current
     *         unchecked board.
     * @throws InvalidSudokuException The current unchecked board is not a valid
     *                                sudoku and an intelligent board cannot be 
     *                                generated.
     */
    private Board generateIntelligentBoard() throws InvalidSudokuException {
        Board result = new SudokuBoard(boxRows, boxCols);
        
        for (int major = 0; major < numbers; major++) {
            for (int minor = 0; minor < numbers; minor++) {
                int cellValue = uncheckedBoard[major][minor];
                if (cellValue != UNSET_CELL) {
                    result.setCell(STRUCT, major, minor, cellValue);
                }
            }
        }
        
        return result;
    }

    /**
     * Creates and returns a copy of the currently displayed unchecked board.
     * 
     * @return A clone of the current unchecked board.
     */
    int[][] cloneUncheckedBoard() {
        int[][] copy = uncheckedBoard.clone();
        for (int i = 0; i < uncheckedBoard.length; i++) {
            copy[i] = uncheckedBoard[i].clone();
        }
        return copy;
    }
    
    /**
     * Reverts the last change of the displayed data and notifies the observers.
     */
    public void undo() {
        uncheckedBoard = history.undo();
        setChanged();
        notifyObservers(DisplayDataChange.SUDOKU_VALUES);
        clearChanged();
    }
    
    /**
     * Returns whether an operation on the sudoku is currently allowed or not.
     * 
     * @return {@code true} if operations on the sudoku are currently allowed.
     */
    public boolean isOperationOnSudokuAllowed() {
        return operationsEnabled;
    }
}