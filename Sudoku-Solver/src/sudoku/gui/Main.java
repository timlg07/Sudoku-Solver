package sudoku.gui;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuFrame::new);
    }

}
