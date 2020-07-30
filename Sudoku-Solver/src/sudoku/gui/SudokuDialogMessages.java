package sudoku.gui;

import java.awt.Component;

import javax.swing.JOptionPane;

import sudoku.gui.model.DisplayData;

public final class SudokuDialogMessages {
    
    /** 
     * Private constructor to prevent instantiation. 
     */
    private SudokuDialogMessages() {
        throw new AssertionError();
    }
    
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
    
    public static void showError(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(
                parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorInvalid(Component parent) {
        showError(parent, "Invalid sudoku", "The current sudoku is invalid.");
    }

    public static void showErrorUnsolvable(Component parent) {
        showError(parent, "Unsolvable sudoku", 
                "This operation cannot be performed on an unsolvable sudoku.");
    }

    public static void showErrorAlreadyFilled(Component parent) {
        showError(parent, "No empty cells", 
                "Cannot suggest a value if all cells are set.");
    }
}
