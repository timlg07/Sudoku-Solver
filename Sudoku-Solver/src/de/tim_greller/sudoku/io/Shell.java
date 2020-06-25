package de.tim_greller.sudoku.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import de.tim_greller.sudoku.model.Board;
import de.tim_greller.sudoku.model.EnforcedCell;
import de.tim_greller.sudoku.model.InvalidSudokuException;
import de.tim_greller.sudoku.model.SudokuBoardSolver;
import de.tim_greller.sudoku.model.SudokuSolver;

/**
 * 
 */
public final class Shell {
    
    private static Board currentBoard;
    private static SudokuSolver currentSolver;
    
    /** 
     * Private constructor to prevent instantiation. 
     */
    private Shell() { }

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
        case "import":
            importSudoku(tokenizedInput);
            break;
            
        case "saturate":
            saturateSudoku();
            break;
            
        case "quit":
            return false;
            
        default:
            printError("Unknown command \"" + cmd + "\"");
            break;
        }
        
        return true;
    }
    
    private static void saturateSudoku() {
        if (isBoardImported()) {
            Board saturatedBoard = currentSolver.saturate(currentBoard);
            System.out.println(saturatedBoard.prettyPrint());
        }
    }
    
    private static void setupSolver() {
        currentSolver = new SudokuBoardSolver();
        currentSolver.addSaturator(new EnforcedCell());
    }
    
    private static boolean isBoardImported() {
        if (currentBoard == null) {
            printError("No sudoku imported.");
            return false;
        } else {
            return true;
        }
    }

    private static void importSudoku(String[] tokenizedInput) {
        if (tokenizedInput.length < 2) {
            printError("No filename specified.");
            return;
        }

        // This removes leading and trailing double quotes from the filename.
        String filename = tokenizedInput[1].replaceAll("^\"|\"$", "");  
        
        File sudokuFile = new File(filename);
        if (!(sudokuFile.exists() && sudokuFile.isFile())) {
            printError("The file \"" + sudokuFile.getAbsolutePath() 
                        + "\" was not found.");
        }
        
        try {
            currentBoard = SudokuFileParser.parseToBoard(sudokuFile);
        } catch (InvalidSudokuException e) {
            printError("The file contains an invalid sudoku.");
        }
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
