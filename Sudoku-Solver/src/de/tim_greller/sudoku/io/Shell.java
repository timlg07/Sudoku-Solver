package de.tim_greller.sudoku.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.tim_greller.sudoku.model.Board;
import de.tim_greller.sudoku.model.EnforcedCell;
import de.tim_greller.sudoku.model.EnforcedNumber;
import de.tim_greller.sudoku.model.InvalidSudokuException;
import de.tim_greller.sudoku.model.SudokuBoardSolver;
import de.tim_greller.sudoku.model.SudokuSolver;

/**
 * The shell class handles the interaction between the user and the data model
 * using the standard in- and output.
 */
public final class Shell {
    
    private static Board currentBoard;
    private static SudokuSolver currentSolver;
    
    /** 
     * Private constructor to prevent instantiation. 
     */
    private Shell() {
        throw new AssertionError();
    }

    /**
     * The main method processes the input received on System.in (standard 
     * input).
     * 
     * @param args The arguments are ignored. All input must be sent per stdin.
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        setupSolver();
        
        BufferedReader stdin 
                = new BufferedReader(new InputStreamReader(System.in));
        boolean continueExecution = true;
        
        while (continueExecution) {
            System.out.print("sudoku> ");
            String input = stdin.readLine();
            continueExecution = processLine(input);
        }
    }

    /**
     * Reads the next input line and initiates the execution of it.
     * Returns {@code false} if EOF is reached.
     * Performs no operation for blank lines.
     * 
     * @param stdin The BufferedReader for the standard input stream.
     * @return whether the program should continue execution or terminate after 
     *         the current line of input is processed.
     */
    private static boolean processLine(String line) {
        if (line == null) {
            // exit program if EOF (end of file) is reached
            return false;
        } else if (line.isBlank()) {
            // show the prompt again if no input was given
            return true;
        }

        /* 
         * For each whitespace this regex looks for subsequent pairs of quotes 
         * (or sequences of any other characters except quotes) without matching
         * them. If a whitespace is contained in a pair of quotes, the rest of
         * the string (till the end "$") contains quotes that can't be matched
         * in pairs and the whitespace will be ignored.
         * The string should therefore not contain quotes that are not in pairs.
         */
        String regex = "\\s+(?=([^\"]*|(\"[^\"]*\"))*$)";
        String[] tokenizedInput = line.trim().split(regex);
        return executeCommand(tokenizedInput);
    }
    
    /**
     * If the first token of the input is a valid command, it gets executed.
     * 
     * @param tokenizedInput The input containing command and parameters.
     * @return whether the program should continue execution or terminate after 
     *         the current command was executed.
     */
    private static boolean executeCommand(String[] tokenizedInput) {
        String cmd = tokenizedInput[0].toLowerCase();

        switch (cmd) {
        case "input":
            inputSudoku(tokenizedInput);
            break;
            
        case "saturate":
            saturateSudoku();
            break;
            
        case "first":
            solveSudoku();
            break;
            
        case "all":
            printAllSolutions();
            break;
            
        case "print":
            if (requireLoadedBoard()) {
                prettyPrint(currentBoard);
            }
            break;
            
        case "quit":
            return false;
            
        default:
            printError("Unknown command \"" + cmd + "\"");
            break;
        }
        
        return true;
    }
    
    /**
     * Prints all possible solutions of the currently loaded board with one
     * solution per line.
     */
    private static void printAllSolutions() {
        if (requireLoadedBoard()) {
            List<Board> solutions 
                = currentSolver.findAllSolutions(currentBoard);
            Collections.sort(solutions);
            
            // Join the string representation of all sudokus.
            String output = solutions.stream()
                                     .map(Board::toString)
                                     .collect(Collectors.joining("\n"));

            System.out.println(output);
        }
    }

    private static void solveSudoku() {
        if (requireLoadedBoard()) {
            Board solvedBoard = currentSolver.findFirstSolution(currentBoard);
            prettyPrint(solvedBoard);
        }
    }
    
    private static void saturateSudoku() {
        if (requireLoadedBoard()) {
            Board saturatedBoard = currentSolver.saturate(currentBoard);
            prettyPrint(saturatedBoard);
        }
    }
    
    private static void setupSolver() {
        currentSolver = new SudokuBoardSolver();
        currentSolver.addSaturator(new EnforcedCell());
        currentSolver.addSaturator(new EnforcedNumber());
    }
    
    /**
     * Checks whether a board is currently loaded or not. If no board is loaded,
     * an error is printed. 
     * This should be used for all operations requiring a loaded currentBoard.
     * 
     * @return {@code true} if a board is loaded.
     */
    private static boolean requireLoadedBoard() {
        if (currentBoard == null) {
            printError("No sudoku loaded. Please input a sudoku file first.");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Loads a sudoku board from the file with the filename/path that is given
     * in the parameter of the input. 
     * 
     * @param tokenizedInput The complete tokenized user input.
     */
    private static void inputSudoku(String[] tokenizedInput) {
        if (tokenizedInput.length < 2) {
            printError("No filename specified.");
            return;
        }

        // This removes leading and trailing double quotes from the filename.
        String filename = tokenizedInput[1].replaceAll("^\"|\"$", "");  
        
        File sudokuFile = new File(filename);
        if (sudokuFile.canRead() && sudokuFile.isFile()) {
            try {
                currentBoard = SudokuFileParser.parseToBoard(sudokuFile);
            } catch (InvalidSudokuException e) {
                printError("The file contains an invalid sudoku.");
            }
        } else {
            printError("The file \"" + sudokuFile.getAbsolutePath() 
                       + "\" was not found.");
        }
    }
    
    /**
     * Prints the given {@link Board} as a rectangle.
     * @param board The board that should be printed, not null.
     */
    private static void prettyPrint(Board board) {
        assert (board != null);
        System.out.println(board.prettyPrint());
    }

    /**
     * Prints an error text starting with {@code "Error!"} followed by the given
     * message.
     * 
     * @param msg The message describing the error.
     */
    private static void printError(String msg) {
        System.out.println("Error! " + msg);
    }
}
