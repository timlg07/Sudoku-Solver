package sudoku.model;

/**
 * An interface for the representation and for solving of Sudokus, which
 * provides all methods for editing. It does not only manage numbers, but also
 * provides an intelligent board which not only knows the chosen numbers but
 * also the remaining possibilities for the not yet set cells.
 * 
 * The overall size of the Sudoku is defined by the size of a single box. Thus,
 * only quadratic Sudokus are supported.
 * 
 * The addressing of cells is exclusively carried as per 3-tuples like
 * (structure, major, minor). See {@link Structure} for more details about that.
 */
public interface Board extends Cloneable, Comparable<Board> {
    
    /**
     * Constant to indicate, that the content of a cell is not yet set.
     */
    int UNSET_CELL = -1;

    /**
     * Gets the number of rows in a box.
     * 
     * @return The number of rows per box.
     */
    int getBoxRows();

    /**
     * Gets the number columns in a box.
     * 
     * @return The number of columns per box.
     */
    int getBoxColumns();

    /**
     * Gets the number of cells in each structure.
     * 
     * The result must be identical to the product of {@link #getBoxRows()} and
     * {@link #getBoxColumns()}.
     * 
     * This function is only for convenience.
     * 
     * @return The number of cells in each structure.
     */
    int getNumbers();

    /**
     * Specifies the content of a cell. This is exactly possible one time, i.e.,
     * the content of a already set cell cannot be overridden.
     * 
     * @param struct The coordinate type of the cell.
     * @param major The major coordinate component of the cell.
     * @param minor The minor coordinate component of the cell.
     * @param number The number to which the cell is fixed.
     * @throws InvalidSudokuException The Sudoku changed to unsolvable by
     *         setting the number.
     */
    void setCell(Structure struct, int major, int minor, int number)
        throws InvalidSudokuException;

    /**
     * Gets the coordinates of the last cell which was set.
     * 
     * @return The 2 dimensional coordinates of the last set cell as
     *         {@link Structure#ROW} {@code {major, minor}}, or {@code null} if
     *         no cell was set so far.
     */
    int[] getLastCellSet();

    /**
     * Gets the content of a cell. For a non-empty cell the content will be
     * returned, for an empty cell the constant {@link #UNSET_CELL}.
     * 
     * @param struct The coordinate type of the cell.
     * @param major The major coordinate component of the cell.
     * @param minor The minor coordinate component of the cell.
     * @return The content of the cell or {@link #UNSET_CELL}.
     */
    int getCell(Structure struct, int major, int minor);

    /**
     * Checks if the board is a correctly solved Sudoku.
     * 
     * @return {@code true} if the Sudoku is complete and correct, {@code false}
     *         otherwise.
     */
    boolean isSolution();

    /**
     * Gets all possible values of a cell, which can be assigned to the cell
     * without making the Sudoku invalid. If the cell is already set,
     * {@code null} is returned. The returned array may be changed without
     * having any effect on the board.
     * 
     * @param struct The coordinate type of the cell.
     * @param major The major coordinate component of the cell.
     * @param minor The minor coordinate component of the cell.
     * @return An array containing the left possible values for a cell, or
     *         {@code null} if the cell is already set.
     */
    int[] getPossibilities(Structure struct, int major, int minor);

    /**
     * Removes a certain number from the possibilities of a cell.
     * 
     * @param struct The coordinate type of the cell.
     * @param major The major coordinate component of the cell.
     * @param minor The minor coordinate component of the cell.
     * @param number The number to delete.
     * @throws InvalidSudokuException The only left possibility should be
     *         removed.
     */
    void removePossibility(Structure struct, int major, int minor,
        int number) throws InvalidSudokuException;

    /**
     * Deep copies the board.
     * 
     * @return The cloned board.
     */
    Board clone();

    /**
     * Compares two boards according to ascending numbers, which arise if the
     * boards are read as number by concatenating all rows. An unset digit '.' 
     * is treated to be larger than the highest symbol.
     * 
     * @param other The board to compare.
     * @return -1 if this board is smaller, 0 if equal, and 1 if bigger.
     * @see java.lang.Comparable#compareTo(Object)
     */
    @Override
    int compareTo(Board other);

    /**
     * Gets a single line string representation of board. The rows of the board
     * are appended by using one space separation.
     * 
     * @return The string representation.
     */
    @Override
    String toString();
    
    /**
     * Gets a string representation of the board layouted as rectangle.
     * 
     * @return The rectangle string representation of the board.
     */
    String prettyPrint();

}
