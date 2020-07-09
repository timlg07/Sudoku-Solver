package sudoku.gui;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        Model model = new Model();
        SwingUtilities.invokeLater(() -> new SudokuFrame(model));
    }

}
