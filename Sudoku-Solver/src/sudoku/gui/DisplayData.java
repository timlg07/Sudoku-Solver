package sudoku.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import sudoku.io.SudokuFileParser;
import sudoku.solver.Board;
import sudoku.solver.InvalidSudokuException;
import sudoku.solver.Structure;
import sudoku.solver.SudokuBoard;

public class DisplayData extends Observable {
    
    /**
     * The representation of an unset cell in the unchecked board.
     */
    public static final int UNSET_CELL = -1;
    
    /**
     * The coordinate system used for all boards in this class.
     */
    public static final Structure STRUCT = Structure.BOX;
    
    private int[][] uncheckedBoard;
    private int boxRows;
    private int boxCols;
    private int numbers;

    public int getCell(int major, int minor) {
        return uncheckedBoard[major][minor];
    }
    
    public void setCell(int major, int minor, int value) {
        assertIndexInRange(major);
        assertIndexInRange(minor);
        assertValueInRange(value);
        
        uncheckedBoard[major][minor] = value;
    }
    
    private void assertIndexInRange(int index) {
        if ((index < 0) || (index >= numbers)) {
            throw new IllegalArgumentException("The index \"" + index 
                    + "\" is out of range for the current board size.");
        }
    }
    
    private void assertValueInRange(int value) {
        if ((value <= 0) || (value > numbers)) {
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
    
    public void loadSudokuFromFile(File sudokuFile) throws InvalidSudokuException, 
            FileNotFoundException, IOException, ParseException {
        Board intelligentBoard = SudokuFileParser.parseToBoard(sudokuFile);
        applyIntelligentBoard(intelligentBoard);
    }

    public void applyIntelligentBoard(Board board) {
        int newSize = board.getNumbers();
        boolean differs = (numbers != newSize);
        int[][] newUncheckedBoard = new int[newSize][newSize];
        
        for (int major = 0; major < newSize; major++) {
            for (int minor = 0; minor < newSize; minor++) {
                int cellValue = board.getCell(STRUCT, major, minor);
                
                // Convert unset cell representation:
                cellValue = ((cellValue == Board.UNSET_CELL) 
                          ? UNSET_CELL 
                          : cellValue);

                if (!differs) {
                    assert uncheckedBoard[major] != null;
                    differs = (uncheckedBoard[major][minor] != cellValue);
                }
                newUncheckedBoard[major][minor] = cellValue;
            }
        }

        if (differs) {
            uncheckedBoard = newUncheckedBoard;
            numbers = newSize;
            boxCols = board.getBoxColumns();
            boxRows = board.getBoxRows();
            setChanged();        
        }
        notifyObservers();
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
}
