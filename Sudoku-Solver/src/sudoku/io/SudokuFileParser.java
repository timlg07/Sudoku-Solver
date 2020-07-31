package sudoku.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

import sudoku.solver.Board;
import sudoku.solver.InvalidSudokuException;
import sudoku.solver.Structure;
import sudoku.solver.SudokuBoard;

/**
 * This class provides the functionality to read a sudoku-file and generate a
 * Board with the files parsed content.
 */
public final class SudokuFileParser {
    
    /**
     * The delimiter between characters in a sudoku-file.
     */
    private static final String DELIMITER = "\\s+";

    /** 
     * Private constructor to prevent instantiation. 
     */
    private SudokuFileParser() {
        throw new AssertionError("This class should not be instantiated.");
    }
    
    /**
     * Parses the given sudoku-file to a Board. Returns {@code null} if the file
     * contains (syntactic or semantic) invalid data.
     * 
     * @param sudokuFile The file that should be parsed.
     * @return The created board.
     * @throws FileNotFoundException The given file was not found.
     * @throws IOException Unable to read the given sudoku file.
     * @throws ParseException The sudoku file cannot be parsed to a board.
     * @throws InvalidSudokuException The sudoku from the file cannot be solved.
     */
    public static Board parseToBoard(File sudokuFile) 
            throws FileNotFoundException, IOException, ParseException,
                   InvalidSudokuException {        
        try (BufferedReader in 
                = new BufferedReader(new FileReader(sudokuFile))) {
        
            // Create a Board using the dimensions specified in the first line.
            String line = in.readLine();
            if (line == null) {
                throw new ParseException("The file is empty.", 0);
            }
            Board board = createBoard(line.split(DELIMITER));
    
            // Append each line of the file as row to the board.
            for (int i = 0; i < board.getNumbers(); i++) {
                line = in.readLine();
                appendRow(board, i, line);
            }
            return board;
        }
    }
    
    /**
     * Parses the string representation of the dimensions and creates a new 
     * board with the parsed row- and col-dimensions.
     * 
     * @param dimensions The row- and col-dimensions.
     * @return The created board.
     * @throws ParseException The first line is invalid.
     */
    private static Board createBoard(String[] dimensions) 
            throws ParseException {
        if (dimensions.length >= 2) {
            Optional<Integer> rows = parseInt(dimensions[0]);
            Optional<Integer> cols = parseInt(dimensions[1]);
            if (rows.isPresent() && cols.isPresent()) {
                return new SudokuBoard(rows.get(), cols.get());
            }
        }
        
        throw new ParseException(
                "The first line contains invalid dimensions.", 0);
    }
    
    /**
     * Parses the string representation of a row and adds it to the board at the
     * given row-index. Returns whether the parsing was successful or not.
     * 
     * @param board The board to which the row should be appended.
     * @param rowIndex The (major) row-index of the new row.
     * @param line The line representing the row.
     * @throws InvalidSudokuException The sudoku cannot be solved with this row
     *         appended.
     * @throws ParseException The line contains data that cannot be parsed.
     */
    private static void appendRow(Board board, int rowIndex, String line) 
            throws ParseException, InvalidSudokuException {
        int parseOffset = rowIndex * board.getNumbers();
        
        if (line == null) {
            throw new ParseException("Invalid amount of lines.", parseOffset);
        }
        
        String[] row = line.trim().split(DELIMITER);
        if (row.length != board.getNumbers()) {
            throw new ParseException("Invalid line length.", parseOffset);
        }
        
        for (int i = 0; i < row.length; i++) {
            int cellValue = parseCellValue(row[i], parseOffset + i);
            if ((cellValue > 0) && (cellValue <= board.getNumbers())) {
                board.setCell(Structure.ROW, rowIndex, i, cellValue);
            }
        }
    }
    
    /**
     * Parses the string representation of a cell to its corresponding integer 
     * value.
     * 
     * @param cell The string that should be parsed.
     * @param parseOffset The offset of this cell in case a parse error occurs.
     * @return The integer representation of the cell.
     * @throws ParseException The cell contains invalid characters.
     */
    private static int parseCellValue(String cell, int parseOffset) 
            throws ParseException {
        if (cell.equals(".")) {
            return Board.UNSET_CELL;
        } else {
            Optional<Integer> parsedValue = parseInt(cell);
            if (parsedValue.isPresent()) {
                return parsedValue.get();
            }
        }
        
        throw new ParseException("Invalid Board data.", parseOffset);
    }
    
    /**
     * Tries to parse a String to an Integer.
     * 
     * @param value The String that should contain a numeric value.
     * @return An Optional containing the Integer if parsing was successful.
     */
    private static Optional<Integer> parseInt(String value) {
        try {
            return Optional.of(Integer.valueOf(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
