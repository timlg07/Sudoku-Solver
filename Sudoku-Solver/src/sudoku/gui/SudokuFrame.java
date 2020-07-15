package sudoku.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import sudoku.solver.InvalidSudokuException;

public class SudokuFrame extends JFrame implements Observer {
    
    private static final long serialVersionUID = 1L;
    private static final Dimension PREF_SIZE = new Dimension(600, 300);
    private final DisplayData data;
    
    /**
     * Using one instance of JFileChooser, the last folder will be remembered.
     */
    private final JFileChooser fileChooser = new JFileChooser();

    public SudokuFrame(DisplayData dataModel) {
        this.data = dataModel;
        dataModel.attachObserver(this);

        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Sudoku File", "sud"));
        
        setJMenuBar(new SudokuMenuBar());

        setPreferredSize(PREF_SIZE);
        setSize(PREF_SIZE); // initial size when no sudoku is loaded.
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

            open.addActionListener(new OpenFileActionListener());
            
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
    
    
    private class OpenFileActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            int fileChooserState = fileChooser.showOpenDialog(SudokuFrame.this);
            if (fileChooserState == JFileChooser.APPROVE_OPTION) {
                File sudokuFile = fileChooser.getSelectedFile();
                try {
                    data.loadSudokuFromFile(sudokuFile);
                } catch (InvalidSudokuException | IOException 
                        | ParseException exc) {
                    JOptionPane.showMessageDialog(
                            SudokuFrame.this, 
                            exc.getMessage(),
                            "Unable to parse this file.", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
