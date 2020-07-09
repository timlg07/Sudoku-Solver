package sudoku.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import sudoku.io.SudokuFileParser;
import sudoku.solver.Board;
import sudoku.solver.InvalidSudokuException;

public class SudokuFrame extends JFrame implements Observer {
    
    private static final long serialVersionUID = 1L;
    private static final Dimension PREF_SIZE = new Dimension(600, 300);
    private final DisplayData model;

    public SudokuFrame(DisplayData model) {
        this.model = model;
        model.attachObserver(this);
        
        setJMenuBar(new SudokuMenuBar());

        setPreferredSize(PREF_SIZE);
        setSize(PREF_SIZE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    
    
    @Override
    public void update(Observable observable, Object argument) {
        
    }
    
    private class SudokuMenuBar extends JMenuBar {
        
        private static final long serialVersionUID = 1L;
        private static final int CTRL = 2;

        SudokuMenuBar() {
            JMenu fileMenu = new JMenu("File");
            JMenu editMenu = new JMenu("Edit");
            JMenu solveMenu = new JMenu("Solve");
            
            JMenuItem open = new JMenuItem("Open");
            JMenuItem exit = new JMenuItem("Exit");
            JMenuItem undo = new JMenuItem("Undo");
            JMenuItem suggest = new JMenuItem("Suggest Value");
            JMenuItem solve = new JMenuItem("Solve");

            open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL));
            exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, CTRL));
            undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, CTRL));
            suggest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, CTRL));
            solve.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, CTRL));
            
            open.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evt) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(
                            new FileNameExtensionFilter("Sudoku File", "sud"));
                    
                    int success = fileChooser.showOpenDialog(SudokuFrame.this);
                    if (success == JFileChooser.APPROVE_OPTION) {
                        File sudokuFile = fileChooser.getSelectedFile();
                        try {
                            Board b = SudokuFileParser.parseToBoard(sudokuFile);
                            System.out.println(b.prettyPrint());
                        } catch (InvalidSudokuException exc) {
                            JOptionPane.showMessageDialog(
                                    SudokuFrame.this, 
                                    "Invalid Sudoku.", 
                                    "Invalid Sudoku.",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            
            exit.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    SudokuFrame.super.dispose();
                }
            });

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

}
