package sudoku.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import sudoku.solver.InvalidSudokuException;
import sudoku.solver.Structure;

public class SudokuFrame extends JFrame implements Observer {
    
    private static final long serialVersionUID = 1L;
    private static final Dimension PREF_SIZE = new Dimension(400, 300);
    private static final Border BOX_BORDER 
            = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    
    private final DisplayData data;
    
    /**
     * Using one instance of JFileChooser, the last folder will be remembered.
     */
    private final JFileChooser fileChooser = new JFileChooser();

    public SudokuFrame(DisplayData dataModel) {
        data = dataModel;
        data.attachObserver(this);

        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Sudoku File", "sud"));
        
        setJMenuBar(new SudokuMenuBar());
        
        // The info text that initially explains why the window is empty.
        String info = "No sudoku file loaded. Press CTRL + O to open a file.";
        
        // Apply basic inline CSS to the info text for proper line wrapping.
        add(new JLabel("<html><body style='width: 100%; text-align: center;'>" 
                + info + "</body></html>", SwingConstants.CENTER));

        /*
         * By setting the pref. size on the content pane, it won't be necessary
         * to restore the default behavior once a sudoku is loaded.
         */
        getContentPane().setPreferredSize(PREF_SIZE);
        pack();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    @Override
    public void update(Observable observable, Object argument) {
        assert data == ((DisplayData) observable);
        assert argument != null;
        
        Boolean newSudokuLoaded = (Boolean) argument;
        if (newSudokuLoaded) {
            resetBoardView();
        }
    }
    
    private void resetBoardView() {
        int numbers = data.getNumbers();
        int boxRows = data.getBoxRows();
        int boxCols = data.getBoxCols();
        Container content = new Container();

        // The layout manager used to arrange cells in a box.
        LayoutManager innerLayout = new GridLayout(boxRows, boxCols);
        
        // Set the layout manager of the container which contains all boxes.
        content.setLayout(new GridLayout(boxCols, boxRows));
        
        /*
         * The sudoku is only displayed correctly when the box coordinate system
         * is used. For other structures the index must be converted.
         */
        assert DisplayData.STRUCT == Structure.BOX;
        
        for (int boxNr = 0; boxNr < numbers; boxNr++) {
            JPanel boxPanel = new JPanel(innerLayout);
            boxPanel.setBorder(BOX_BORDER);
            for (int cellNr = 0; cellNr < numbers; cellNr++) {
                Component cell = new SudokuCell(boxNr, cellNr, data);
                boxPanel.add(cell);
            }
            content.add(boxPanel);
        }
        
        setContentPane(content);
        validate();
        pack();
    }

    private class SudokuMenuBar extends JMenuBar {
        
        private static final long serialVersionUID = 1L;
        private static final int CTRL = 2;

        SudokuMenuBar() {
            super();
            
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
