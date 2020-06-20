package de.tim_greller.sudoku.model;

/**
 *
 */
public class SudokuBoard implements Board {

    @Override
    public void setCell(Structure struct, int major, int minor, int number) throws InvalidSudokuException {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void removePossibility(Structure struct, int major, int minor, int number) throws InvalidSudokuException {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String prettyPrint() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int[] getLastCellSet() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getCell(Structure struct, int major, int minor) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int getBoxRows() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int getBoxColumns() {
        // TODO Auto-generated method stub
        return 0;
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
