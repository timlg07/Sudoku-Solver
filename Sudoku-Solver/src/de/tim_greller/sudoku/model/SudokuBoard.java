package de.tim_greller.sudoku.model;

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
        }
        isFixed = new boolean[boardElements];
    }

    @Override
    public void setCell(Structure struct, int major, int minor, int number) 
            throws InvalidSudokuException {
        int index = calculateIndex(struct, major, minor);
        if (!isFixed[index]) {
            board[index].set(0, numbers, false);
            board[index].set(number - 1);
            isFixed[index] = true;
        }
    }
    
    @Override
    public void removePossibility(Structure struct, int major, int minor, int number) throws InvalidSudokuException {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String prettyPrint() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numbers * numbers; i++) {
            result.append(isFixed[i] ? board[i].nextSetBit(0) + 1 : ".");
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
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public int[] getPossibilities(Structure struct, int major, int minor) {
        // TODO Auto-generated method stub
        return null;
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
            return board[index].nextSetBit(0) + 1;
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
    
    private int calculateIndex(Structure struct, int major, int minor) {
        int x = 0;
        int y = 0;
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
        }
        return y * numbers + x;
    }
    
    /*
    SudokuBoard(int, int)
    clone() : SudokuBoard
    compareTo(Board) : int
    getBoxRows() : int
    getBoxColumns() : int
    getNumbers() : int
    setCell(Structure, int, int, int) : void
    getLastCellSet() : int[]
    getCell(Structure, int, int) : int
    isSolution() : boolean
    getPossibilities(Structure, int, int) : int[]
    removePossibility(Structure, int, int, int) : void
    getRow(Structure, int, int) : int
    getCol(Structure, int, int) : int
    getBox(Structure, int, int) : int
    getBoxMinor(Structure, int, int) : int
    toString() : String
    prettyPrint() : String
     */

}
