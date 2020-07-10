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
    
    private static final int UNSET_CELL = -1;
    
    private int[][] uncheckedBoard;
    private Board intelligentBoard;
    private int boxRows;
    private int boxCols;
    private int numbers;
    
    public void loadSudokuFromFile(File sudokuFile) throws InvalidSudokuException, 
            FileNotFoundException, IOException, ParseException {
        intelligentBoard = SudokuFileParser.parseToBoard(sudokuFile);
    }
    
    public Board getIntelligentBoard() {
        return intelligentBoard;
    }
    
    void updateDisplayData() {
        setChanged();
        notifyObservers();
    }
    
    public void applyIntelligentBoard(Board board) {
        numbers = board.getNumbers();
        
        uncheckedBoard = new int[numbers][numbers];
        for (int boxNr = 0; boxNr < numbers; boxNr++) {
            for (int cellNr = 0; cellNr < numbers; cellNr++) {
                int cellValue = board.getCell(Structure.BOX, boxNr, cellNr);
                uncheckedBoard[boxNr][cellNr] = ((cellValue == Board.UNSET_CELL)
                                                 ? UNSET_CELL
                                                 : cellValue);
            }
        }
        
        updateDisplayData();
    }
    
    private void applyUncheckedBoard() throws InvalidSudokuException {
        intelligentBoard = new SudokuBoard(boxRows, boxCols);
        
        for (int boxNr = 0; boxNr < numbers; boxNr++) {
            for (int cellNr = 0; cellNr < numbers; cellNr++) {
                intelligentBoard.setCell(Structure.BOX, boxNr, cellNr,
                        uncheckedBoard[boxNr][cellNr]);
            }
        }
    }
    
}
