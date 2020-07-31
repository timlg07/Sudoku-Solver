package sudoku.gui;

import javax.swing.SwingUtilities;

/**
 * This is the main-class of the sudoku game that starts the GUI.
 */
public final class SudokuGameStarter {
    
    /** 
     * Private constructor to prevent instantiation.
     */
    private SudokuGameStarter() {
        throw new AssertionError("This class should not be instantiated.");
    }

    /**
     * The main method that starts the game by creating a new 
     * {@link SudokuFrame}.
     * 
     * @param args The arguments are ignored.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuFrame::new);
    }

}
