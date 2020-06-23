package de.tim_greller.sudoku.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import de.tim_greller.sudoku.model.Board;
import de.tim_greller.sudoku.model.InvalidSudokuException;
import de.tim_greller.sudoku.model.Structure;
import de.tim_greller.sudoku.model.SudokuBoard;

public final class SudokuFileParser {
    
    /**
     * The delimiter between characters in a sudoku-file.
     */
    private static final String DELIMITER = "\\s+";

    public static Board parseToBoard(File sudokuFile) 
            throws IOException, InvalidSudokuException {
        BufferedReader in = new BufferedReader(new FileReader(sudokuFile));
        
        // Use the dimensions specified in the first line to create the Board.
        String line = in.readLine();
        Board board = createBoard(line.split(DELIMITER));
        if (board == null) {
            in.close();
            return null;
        }
        
        for (int i = 0; i < board.getNumbers(); i++) {
            line = in.readLine();
            if (!appendRow(board, i, line)) {
                in.close();
                return null;
            }
        }

        in.close();
        return board;
    }
    
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
            if (cellValue > 0 && cellValue <= row.length) {
                board.setCell(Structure.ROW, rowIndex, i, cellValue);
            } else if (cellValue != Board.UNSET_CELL) {
                printParseError("Invalid Board data.");
                return false;
            }
        }
        return true;
    }
    
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
    
    private static void printParseError(String message) {
        System.out.println("Parse error! " + message);
    }

}
