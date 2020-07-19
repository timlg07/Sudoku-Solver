package sudoku.gui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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

public class DisplayData {
    
    /**
     * The representation of an unset cell in the unchecked board.
     */
    public static final int UNSET_CELL = -1;
    
    /**
     * The coordinate system used for all boards in this class.
     */
    public static final Structure STRUCT = Structure.BOX;
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final SudokuHistory history = new SudokuHistory(this);
    private final SudokuSolver solver = new SudokuBoardSolver();
    
    private int[][] uncheckedBoard;
    private boolean[][] isConstant;
    private int boxRows;
    private int boxCols;
    private int numbers;
    private int amountOfUnsetCells;
    
    public DisplayData(Board intelligentBoard) {
        solver.addSaturator(new EnforcedNumber());
        solver.addSaturator(new EnforcedCell());
        
        numbers = intelligentBoard.getNumbers();
        boxCols = intelligentBoard.getBoxColumns();
        boxRows = intelligentBoard.getBoxRows();
        uncheckedBoard = new int[numbers][numbers];
        amountOfUnsetCells = 0;
        isConstant = new boolean[numbers][numbers];
        
        for (int major = 0; major < numbers; major++) {
            for (int minor = 0; minor < numbers; minor++) {
                int cell = intelligentBoard.getCell(STRUCT, major, minor);
                boolean isSet = (cell != Board.UNSET_CELL);
                
                uncheckedBoard[major][minor] = (isSet ? cell : UNSET_CELL);
                amountOfUnsetCells += (isSet ? 0 : 1);
                isConstant[major][minor] = isSet;
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(
            String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(
            String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }
    
    /**
     * Returns the property name used by the {@link PropertyChangeSupport} for
     * a cell with the given major and minor coordinates in the coordinate 
     * system specified by {@link DisplayData#STRUCT}.
     * <p>
     * The property name is in the format {@code "cell@STRUCTURE(major,minor)"}.
     * 
     * @param majorIndex The first coordinate.
     * @param minorIndex The second coordinate.
     * @return The property name as formatted String containing the given
     *         coordinates.
     */
    public static String getCellPropertyName(int majorIndex, int minorIndex) {
        return "cell@" + STRUCT + "(" + majorIndex + "," + minorIndex + ")";
    }

    public int getCell(int major, int minor) {
        assertIndexInRange(major);
        assertIndexInRange(minor);
        
        return uncheckedBoard[major][minor];
    }
    
    public void updateCell(int major, int minor, int value) {
        assertIndexInRange(major);
        assertIndexInRange(minor);
        assertValueInRange(value);
        
        updateCell(major, minor, value, true);
    }
    
    private void updateCell(int major, int minor, int value, 
            boolean updateIsFinished) {
        int oldValue = uncheckedBoard[major][minor];
        boolean oldIsFinished = isFinished();
        
        if (oldValue != value) {
            if (oldValue == UNSET_CELL) {
                amountOfUnsetCells--;
            } else if (value == UNSET_CELL) {
                amountOfUnsetCells++;
            }
            
            uncheckedBoard[major][minor] = value;
        }
        
        pcs.firePropertyChange(
                getCellPropertyName(major, minor), oldValue, value);
        
        if (updateIsFinished) {
            pcs.firePropertyChange("isFinished", oldIsFinished, isFinished());
        }
    }
    
    private void updateAllCells(int[][] newBoard) {
        assert newBoard != null;
        assert uncheckedBoard.length == newBoard.length;
        
        for (int major = 0; major < numbers; major++) {
            assert uncheckedBoard[major].length == newBoard[major].length;
            for (int minor = 0; minor < numbers; minor++) {
                updateCell(major, minor, newBoard[major][minor], false);
            }
        }
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

    public boolean isFinished() {
        return (amountOfUnsetCells == 0);
    }

    public boolean isSolution() {
        try {
            return (isFinished() && generateIntelligentBoard().isSolution());
        } catch (InvalidSudokuException e) {
            return false;
        }
    }

    /** TODO: Update JavaDoc:
     *      - same size or throw
     *      - only setting not unsetting
     *      - not changing constants
     * Applies the given intelligent board to the current unchecked board by
     * transferring all values from the intelligent board to a new array and
     * then updating the reference of the unchecked board to the new array.
     * <p>
     * If the intelligent board is marked as initial board, it is allowed to
     * change the sudokus size. It also resets the constant markers and then 
     * sets all values of the intelligent board as unmodifiable constants of the
     * new unchecked board.
     * <p>
     * This method sets the changed flag of the {@link Observable} represented
     * by this DisplayData, but does not notify the observers.
     * 
     * @param board The intelligent board that will be used as unchecked board.
     *              Should not be {@code null}.
     * @param updateIsFinished TODO
     */
    private void applyIntelligentBoard(Board board, boolean updateIsFinished) {
        assert board != null;
        assert boxCols == board.getBoxColumns();
        assert boxRows == board.getBoxRows();
        
        for (int major = 0; major < numbers; major++) {
            for (int minor = 0; minor < numbers; minor++) {
                int cellValue = board.getCell(STRUCT, major, minor);
                if (cellValue != Board.UNSET_CELL) {
                    updateCell(major, minor, cellValue, updateIsFinished);
                }
            }
        }
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
            updateAllCells(lastBoard);
        }
    }
    
    public void solve()
            throws InvalidSudokuException, UnsolvableSudokuException {
        Board initialBoard = generateIntelligentBoard();
        Board solvedBoard = solver.findFirstSolution(initialBoard);
        if (solvedBoard == null) {
            throw new UnsolvableSudokuException();
        }
        applyIntelligentBoard(solvedBoard, false);
    }
    
    public void suggestValue() 
            throws InvalidSudokuException, UnsolvableSudokuException {
        if (amountOfUnsetCells < 1) {
            throw new IllegalStateException(
                    "Cannot suggest a value if the sudoku is already solved.");
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
        
        /*
         * Applies the initial board with one changed cell and acts as if the
         * cell was set manually.
         */
        applyIntelligentBoard(initialBoard, true);
    }
    
        /*
         * Execute the solve on a seperate Thread. This ensures that the Swing
         * EventDispatcher stays responsive and can process user interaction.
         //currentCalculationThread = new Thread(() -> {
         */
    
    /*@SuppressWarnings("deprecation")
    public void stopOngoingCalculation() {
        if (currentCalculationThread != null) {
            currentCalculationThread.stop();
        }
    }*/
}
