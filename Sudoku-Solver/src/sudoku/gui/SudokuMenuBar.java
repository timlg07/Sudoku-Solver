package sudoku.gui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class SudokuMenuBar extends JMenuBar {
    
    private static final long serialVersionUID = 1L;

    SudokuMenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu solveMenu = new JMenu("Solve");
        
        JMenuItem open = new JMenuItem("Open");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem undo = new JMenuItem("Undo");
        JMenuItem suggest = new JMenuItem("Suggest Value");
        JMenuItem solve = new JMenuItem("Solve");
        
        fileMenu.add(open);
        fileMenu.add(exit);
        editMenu.add(undo);
        solveMenu.add(suggest);
        solveMenu.add(solve);

        add(fileMenu);
        add(editMenu);
        add(solveMenu);
    }
}
