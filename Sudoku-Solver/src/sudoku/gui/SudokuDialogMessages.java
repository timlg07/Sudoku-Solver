package sudoku.gui;

import java.awt.Component;

import javax.swing.JOptionPane;

import sudoku.gui.model.DisplayData;

/**
 * This class contains static methods used to show the same messages from 
 * different classes.
 */
public final class SudokuDialogMessages {
    
    /** 
     * Private constructor to prevent instantiation.
     */
    private SudokuDialogMessages() {
        throw new AssertionError("This class should not be instantiated.");
    }
    
    /**
     * If the given data model is completely filled, a message is shown which
     * informs the user about whether the sudoku is a valid solution or not.
     *  
     * @param parent Determines the Frame in which the dialog is displayed.
     * @param data The data model that should be checked if it is full and
     *             solved.
     */
    public static void showMessageIfFilled(Component parent, DisplayData data) {
        if (data.isFilled()) {
            String message;
            if (data.isSolution()) {
                message = "Sudoku solved!";
            } else {
                message = "This is not a valid solution.";
            }
            JOptionPane.showMessageDialog(parent, message);
        }
    }

    /**
     * Shows an error message that informs the user about an invalid sudoku.
     * 
     * @param parent Determines the Frame in which the dialog is displayed.
     */
    public static void showErrorInvalid(Component parent) {
        showError(parent, "Invalid sudoku", "The current sudoku is invalid.");
    }

    /**
     * Shows an error message that informs the user about a sudoku that cannot
     * be solved by the program.
     * 
     * @param parent Determines the Frame in which the dialog is displayed.
     */
    public static void showErrorUnsolvable(Component parent) {
        showError(parent, "Unsolvable sudoku", 
                "The current sudoku is not solvable.");
    }

    /**
     * Shows an error message that informs the user that the sudoku is already
     * filled and a suggestion cannot be made on it.
     * 
     * @param parent Determines the Frame in which the dialog is displayed.
     */
    public static void showErrorAlreadyFilled(Component parent) {
        showError(parent, "No empty cells", 
                "Cannot suggest a value if all cells are set.");
    }
    
    /**
     * Shows a error message with an option pane dialog. The given parameters
     * are used for displaying the message dialog.
     * 
     * @param parent Determines the Frame in which the dialog is displayed.
     * @param title The title of the error message.
     * @param message The description of the error message.
     */
    private static void showError(
            Component parent, String title, String message) {
        JOptionPane.showMessageDialog(
                parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
