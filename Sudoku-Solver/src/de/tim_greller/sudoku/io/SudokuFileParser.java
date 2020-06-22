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
    
    private static final String DELIMITER = "\\s+";

    public static Board parseToBoard(File sudokuFile) 
            throws IOException, InvalidSudokuException {
        BufferedReader in = new BufferedReader(new FileReader(sudokuFile));
        
        // Use the dimensions specified in the first line to create the Board.
        String line = in.readLine(); // throws IOException
        Board board = createBoard(line.split(DELIMITER));
        if (board == null) {
            abortParse("Invalid first line.", in);
            return null;
        }
        int boardSize = board.getBoxColumns() * board.getBoxRows();
        
        for (int i = 0; i < boardSize; i++) {
            line = in.readLine(); // throws IOException
            if (line == null) {
                abortParse("Invalid amount of lines.", in);
                return null;
            }
            
            String[] row = line.trim().split(DELIMITER);
            if (row.length != boardSize) {
                abortParse("Invalid line length.", in);
                return null;
            }
            
            if (!appendRow(board, i, row)) {
                abortParse("Invalid Board data.", in);
                return null;
            }
        }

        in.close(); // throws IOException
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
        return null;
    }
    
    private static boolean appendRow(Board board, int rowIndex, String[] row) 
            throws InvalidSudokuException {
        for (int i = 0; i < row.length; i++) {
            int cellValue = parseCellValue(row[i]);
            if (cellValue > 0 && cellValue <= row.length) {
                board.setCell(Structure.ROW, rowIndex, i, cellValue);
            } else if (cellValue != Board.UNSET_CELL) {
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
    
    private static void abortParse(String message, Reader activeReader) 
            throws IOException {
        System.out.println("Parse error! " + message);
        activeReader.close();
    }

}
