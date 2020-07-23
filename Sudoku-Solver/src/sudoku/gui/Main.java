package sudoku.gui;

import javax.swing.SwingUtilities;

import sudoku.gui.model.DisplayData;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuFrame::new);
    }

}
