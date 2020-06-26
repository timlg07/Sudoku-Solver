package de.tim_greller.sudoku.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import de.tim_greller.sudoku.model.Board;
import de.tim_greller.sudoku.model.InvalidSudokuException;
import de.tim_greller.sudoku.model.Structure;
import de.tim_greller.sudoku.model.SudokuBoard;

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
        throw new AssertionError();
    }
    
    /**
     * Parses the given sudoku-file to a Board. Returns {@code null} if the file
     * contains (syntactic or semantic) invalid data.
     * 
     * @param sudokuFile The file that should be parsed.
     * @return The created board or {@code null} if no valid board can be built.
     * @throws InvalidSudokuException The sudoku from the file cannot be solved.
     */
    public static Board parseToBoard(File sudokuFile) 
            throws InvalidSudokuException {
        try (BufferedReader in 
                = new BufferedReader(new FileReader(sudokuFile))) {
            
            // Create a Board using the dimensions specified in the first line.
            String line = in.readLine();
            if (line == null) {
                printParseError("The file is empty.");
                return null;
            }
            Board board = createBoard(line.split(DELIMITER));
            if (board == null) {
                return null;
            }
            
            // Append each line of the file as row to the board.
            for (int i = 0; i < board.getNumbers(); i++) {
                line = in.readLine();
                if (!appendRow(board, i, line)) {
                    return null;
                }
            }
            return board;
            
        } catch (IOException e) {
            printParseError("Unable to read the file.");
            return null;
        }
    }
    
    /**
     * Parses the string representation of the dimensions and creates a new 
     * board with the parsed row- and col-dimensions.
     * 
     * @param dimensions The row- and col-dimensions.
     * @return The created board or {@code null} if parsing was not successful.
     */
    private static Board createBoard(String[] dimensions) {
        if (dimensions.length >= 2) {
            Optional<Integer> rows = parseInt(dimensions[0]);
            Optional<Integer> cols = parseInt(dimensions[1]);
            if (rows.isPresent() && cols.isPresent()) {
                return new SudokuBoard(rows.get(), cols.get());
            }
        }
        
        printParseError("Invalid first line.");
        return null;
    }
    
    /**
     * Parses the string representation of a row and adds it to the board at the
     * given row-index. Returns whether the parsing was successful or not.
     * 
     * @param board The board to which the row should be appended.
     * @param rowIndex The (major) row-index of the new row.
     * @param line The line representing the row.
     * @return {@code true} if parsing the row was successful.
     * @throws InvalidSudokuException The sudoku cannot be solved with this row
     *         appended.
     */
    private static boolean appendRow(Board board, int rowIndex, String line) 
            throws InvalidSudokuException {
        if (line == null) {
            printParseError("Invalid amount of lines.");
            return false;
        }
        
        String[] row = line.trim().split(DELIMITER);
        if (row.length != board.getNumbers()) {
            printParseError("Invalid line length.");
            return false;
        }
        
        for (int i = 0; i < row.length; i++) {
            int cellValue = parseCellValue(row[i]);
            if (cellValue > 0 && cellValue <= board.getNumbers()) {
                board.setCell(Structure.ROW, rowIndex, i, cellValue);
            } else if (cellValue != Board.UNSET_CELL) {
                printParseError("Invalid Board data.");
                return false;
            }
        }
        return true;
    }
    
    /**
     * Parses the string representation of a cell to its corresponding integer 
     * value. Returns {@code 0} if the cell cannot be parsed.
     * 
     * @param cell The string that should be parsed.
     * @return The integer representation of the cell.
     */
    private static int parseCellValue(String cell) {
        if (cell.equals(".")) {
            return Board.UNSET_CELL;
        } else {
            Optional<Integer> parsedValue = parseInt(cell);
            if (parsedValue.isPresent()) {
                return parsedValue.get();
            }
        }
        return 0;
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
    
    /**
     * Prints an error text including the given message to the standard output.
     * 
     * @param message The message specifying the error.
     */
    private static void printParseError(String message) {
        System.out.println("Parse error! " + message);
    }

}
