package sudoku.gui.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void applyIntelligentBoard(Board board, boolean isInitial) {
        if (board == null) {
            throw new IllegalArgumentException(
                    "Cannot apply \"null\" as Board.");
        }
        
        int newSize = board.getNumbers();
        int[][] newUncheckedBoard = new int[newSize][newSize];
        boolean[][] newIsConstant = new boolean[newSize][newSize];
        
        for (int major = 0; major < newSize; major++) {
            for (int minor = 0; minor < newSize; minor++) {
                int cell = board.getCell(STRUCT, major, minor);
                boolean isSet = (cell != Board.UNSET_CELL);
                
                newUncheckedBoard[major][minor] = (isSet ? cell : UNSET_CELL);
                newIsConstant[major][minor] = (isSet && isInitial);
            }
        }

        uncheckedBoard = newUncheckedBoard;
        isConstant = newIsConstant;
        numbers = newSize;
        boxCols = board.getBoxColumns();
        boxRows = board.getBoxRows();
        operationsEnabled = true;
        setChanged();
        
        DisplayDataChange changeType = isInitial 
                                       ? DisplayDataChange.SUDOKU_LOADED 
                                       : DisplayDataChange.SOLVER_CHANGE;
        notifyObservers(changeType);
        clearChanged();
    }
    
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

    public int[][] cloneUncheckedBoard() {
        int[][] copy = uncheckedBoard.clone();
        for (int i = 0; i < uncheckedBoard.length; i++) {
            copy[i] = uncheckedBoard[i].clone();
        }
        return copy;
    }
    
    public void undo() {
        System.out.println(Arrays.deepToString(uncheckedBoard));
        uncheckedBoard = history.undo();
        System.out.println(Arrays.deepToString(uncheckedBoard));
        System.out.println("----------");
        
        setChanged();
        notifyObservers(DisplayDataChange.SUDOKU_VALUES);
        clearChanged();
    }
    
    public boolean isOperationOnSudokuAllowed() {
        return operationsEnabled;
    }
}
