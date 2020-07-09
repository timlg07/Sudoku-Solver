package sudoku.gui;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        DisplayData model = new DisplayData();
        SwingUtilities.invokeLater(() -> new SudokuFrame(model));
    }

}
