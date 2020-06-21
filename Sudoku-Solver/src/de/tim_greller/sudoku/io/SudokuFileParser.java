package de.tim_greller.sudoku.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import de.tim_greller.sudoku.model.Board;
import de.tim_greller.sudoku.model.SudokuBoard;

public final class SudokuFileParser {

    public static Board parseToBoard(File sudokuFile) throws IOException {
        Board board = new SudokuBoard();

        BufferedReader in = new BufferedReader(new FileReader(sudokuFile));
        boolean eof = false;
        while (!eof) {
            String input = in.readLine(); // throws IOException
            if (input == null) {
                eof = true;
            } else {
                System.out.println(input);
            }
        }
        in.close(); // throws IOException

        return board;
    }

}
