package sudoku.gui.model;

import sudoku.solver.Board;
import sudoku.solver.EnforcedCell;
import sudoku.solver.EnforcedNumber;
import sudoku.solver.InvalidSudokuException;
import sudoku.solver.Structure;
import sudoku.solver.SudokuBoard;
import sudoku.solver.SudokuBoardSolver;
import sudoku.solver.SudokuSolver;
import sudoku.solver.UnsolvableSudokuException;
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
    
    private final SudokuHistory history;
    private final SudokuSolver solver;
    private final int boxRows;
    private final int boxCols;
    private final int numbers;
    private int[][] uncheckedBoard;
    
    public DisplayData(Board intelligentBoard) {
        numbers = intelligentBoard.getNumbers();
        boxCols = intelligentBoard.getBoxColumns();
        boxRows = intelligentBoard.getBoxRows();
        uncheckedBoard = new int[numbers][numbers];
        
        for (int major = 0; major < numbers; major++) {
            for (int minor = 0; minor < numbers; minor++) {
                int cell = intelligentBoard.getCell(STRUCT, major, minor);
                boolean isSet = (cell != Board.UNSET_CELL);
                
                uncheckedBoard[major][minor] = (isSet ? cell : UNSET_CELL);
            }
        }
        
        history = new SudokuHistory(this);
        solver = new SudokuBoardSolver();
        solver.addSaturator(new EnforcedNumber());
        solver.addSaturator(new EnforcedCell());
    }

    public int getCell(int major, int minor) {
        assertIndexInRange(major);
        assertIndexInRange(minor);
        
        return uncheckedBoard[major][minor];
    }
    
    public void setCell(int major, int minor, int value) {
        assertIndexInRange(major);
        assertIndexInRange(minor);
        assertValueInRange(value);
        
        int oldValue = uncheckedBoard[major][minor];
        
        if (oldValue != value) {
            setChanged();
            uncheckedBoard[major][minor] = value;
        }
        
        notifyObservers();
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

    public boolean isFilled() {
        for (int[] box : uncheckedBoard) {
            for (int cell : box) {
                if (cell == UNSET_CELL) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Tries to generate an intelligent board from the current unchecked board
     * and returns whether this board is a valid solution for the sudoku or not.
     * 
     * @return {@code true} if the current unchecked board is a valid solution.
     */
    public boolean isSolution() {
        try {
            return generateIntelligentBoard().isSolution();
        } catch (InvalidSudokuException e) {
            return false;
        }
    }

    /** 
     * Applies the given intelligent board to the current unchecked board by
     * setting all values that are only set in the intelligent board.
     * <p>
     * This method may set the changed flag of the {@link Observable}
     * represented by this DisplayData and notify the observers.
     * 
     * @param board The intelligent board which values should be transferred to
     *              the unchecked board.
     *              Must not be {@code null} and must have the same sizes as the
     *              current unchecked board.
     */
    public void applyMachineMove(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("The board can not be null.");
        } else if ((boxCols != board.getBoxColumns()) 
                   || (boxRows != board.getBoxRows())) {
            throw new IllegalArgumentException(
                    "The board has a different size than the current one.");
        }
        
        for (int major = 0; major < numbers; major++) {
            for (int minor = 0; minor < numbers; minor++) {
                int cellValue = board.getCell(STRUCT, major, minor);
                if ((cellValue != Board.UNSET_CELL) 
                        && (uncheckedBoard[major][minor] == UNSET_CELL)) {
                    setChanged();
                    uncheckedBoard[major][minor] = cellValue;
                }
            }
        }
        
        notifyObservers();
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
        int[][] lastBoard = history.undo();
        if (lastBoard != null) {
            setChanged();
            uncheckedBoard = lastBoard;
        }
        notifyObservers();
    }
    
    public Board getSolvedBoard()
            throws InvalidSudokuException, UnsolvableSudokuException {
        Board initialBoard = generateIntelligentBoard();
        Board solvedBoard = solver.findFirstSolution(initialBoard);
        if (solvedBoard == null) {
            throw new UnsolvableSudokuException();
        }
        return solvedBoard;
    }
    
    public Board getBoardWithSuggestion() 
            throws InvalidSudokuException, UnsolvableSudokuException {
        if (isFilled()) {
            throw new IllegalStateException(
                    "Cannot suggest a value if the sudoku is already filled.");
        }

        Board initialBoard = generateIntelligentBoard();
        Board solvedBoard = solver.findFirstSolution(initialBoard);
        if (solvedBoard == null) {
            throw new UnsolvableSudokuException();
        }
        
        int[] suggestedCell = solvedBoard.getLastCellSet();
        int suggestedValue = solvedBoard.getCell(
                Structure.ROW, suggestedCell[0], suggestedCell[1]);

        /* 
         * This should never cause an InvalidSudokuException since the cell
         * was taken from the fully solved sudoku board.
         */
        initialBoard.setCell(Structure.ROW, suggestedCell[0], suggestedCell[1],
                suggestedValue);
        return initialBoard;
    }
}
