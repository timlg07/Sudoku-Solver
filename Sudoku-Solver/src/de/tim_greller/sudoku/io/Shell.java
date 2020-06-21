package de.tim_greller.sudoku.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.tim_greller.sudoku.model.Board;

/**
 * 
 */
public final class Shell {
    
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

        String[] tokenizedInput = line.trim().split("\".*?\"|\\s+");
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

        case "quit":
            return false;
            
        default:
            printError("Unknown command \"" + cmd + "\"");
            break;
        }
        
        return true;
    }
    
    private static void importSudoku(String[] tokenizedInput) {
        if (tokenizedInput.length < 2) {
            printError("No filename specified.");
            return;
        }
        
        File sudokuFile = new File(tokenizedInput[1]);
        
        if (!(sudokuFile.exists() && sudokuFile.isFile())) {
            printError("The file \"" + sudokuFile.getAbsolutePath() 
                        + "\" was not found.");
        }
        
        try {
            Board board = SudokuFileParser.parseToBoard(sudokuFile);
        } catch (IOException e) {
            printError("Unable to read the file.");
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
