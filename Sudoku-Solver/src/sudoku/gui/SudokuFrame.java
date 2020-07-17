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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
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

import sudoku.gui.model.DisplayData;
import sudoku.gui.model.DisplayDataChange;
import sudoku.solver.InvalidSudokuException;
import sudoku.solver.Structure;
import sudoku.util.Observable;
import sudoku.util.Observer;

public class SudokuFrame extends JFrame implements Observer {
    
    private static final long serialVersionUID = 1L;
    private static final Dimension PREF_SIZE = new Dimension(400, 300);
    private static final Border BOX_BORDER 
            = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    
    private final DisplayData data;
    private Collection<AbstractButton> operationsOnSudoku = new ArrayList<>();
    
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
        setEnableStates();
        
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
        setLocationRelativeTo(null);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    @Override
    public void update(Observable observable, Object argument) {
        assert observable instanceof DisplayData;
        assert data == ((DisplayData) observable);
        assert argument instanceof DisplayDataChange;
        
        switch ((DisplayDataChange) argument) {
        case NEW_SUDOKU:
            resetBoardView();
            /* Falls through so the states get reseted for a new sudoku. */
        case SUDOKU_LOCK:
            setEnableStates();
            
        default:
            /* No updates necessary, the cells get individually updated. */
            break;
        }
    }
    
    private void setEnableStates() {
        boolean allowed = data.isOperationOnSudokuAllowed();
        
        operationsOnSudoku.forEach(o -> {
            o.setEnabled(allowed);
        });
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
                boxPanel.add(new SudokuCell(boxNr, cellNr, data));
            }
            content.add(boxPanel);
        }
        
        // Update the visible frame and its content.
        setContentPane(content);
        validate();
        pack();
        setLocationRelativeTo(null);
    }

    private class SudokuMenuBar extends JMenuBar {
        
        private static final long serialVersionUID = 1L;

        SudokuMenuBar() {
            super();
            
            JMenu fileMenu = new JMenu("File");
            JMenu editMenu = new JMenu("Edit");
            JMenu solveMenu = new JMenu("Solve");
            
            operationsOnSudoku.add(editMenu);
            operationsOnSudoku.add(solveMenu);
            
            JMenuItem open = new JMenuItem("Open");
            JMenuItem exit = new JMenuItem("Exit");
            JMenuItem undo = new JMenuItem("Undo");
            JMenuItem suggest = new JMenuItem("Suggest Value");
            JMenuItem solve = new JMenuItem("Solve");
            
            // The modifier used to generate key strokes including the CTRL key.
            final int ctrl = KeyEvent.CTRL_DOWN_MASK;
            
            open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ctrl));
            exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ctrl));
            undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ctrl));
            suggest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ctrl));
            solve.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ctrl));

            open.addActionListener(new OpenFileActionListener());
            exit.addActionListener(e -> SudokuFrame.super.dispose());
            undo.addActionListener(e -> data.undo());

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
