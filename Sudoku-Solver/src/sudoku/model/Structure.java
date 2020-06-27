package sudoku.model;

/**
 * The different types of structures/coordinate systems.
 */
public enum Structure {

    /**
     * Structure is a row. The rows are numbered top down from 0 to numbers - 1,
     * which is stored as major coordinate. Then, the minor coordinate is the
     * column.
     */
    ROW,

    /**
     * Structure is a column. The columns are numbered left to right from 0 to
     * numbers - 1, which is stored as major coordinate. Then, the minor
     * coordinate is the row.
     */
    COL,

    /**
     * Structure is a box. The boxes are numbered from 0 to numbers - 1 from
     * left to right from top to bottom, which is stored as major coordinate.
     * Then, the minor coordinate is the number of the cell in the box, which
     * are ordered from left to right from top to bottom from 0 to numbers - 1.
     */
    BOX

}
