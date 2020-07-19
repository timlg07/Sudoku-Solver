package sudoku.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import sudoku.gui.model.DisplayData;
import sudoku.gui.model.DisplayDataChange;
import sudoku.solver.InvalidSudokuException;
import sudoku.solver.Structure;
import sudoku.util.Observable;
import sudoku.util.Observer;

/**
 * The custom JFrame that is the main window of the graphical user interface.
 * It contains a {@link SudokuMenuBar} and, if a file was loaded, an editable
 * sudoku consisting of {@link SudokuCell}s.
 */
public class SudokuFrame extends JFrame implements Observer {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The preferred size of the initial content pane containing only the info 
     * text.
     */
    private static final Dimension PREF_SIZE = new Dimension(400, 300);
    
    /**
     * The minimum size the window can have while it still will be displayed as
     * a proper window and show the menu bar.
     */
    private static final Dimension MIN_SIZE = new Dimension(240, 150);
    
    /**
     * The border around each box of the sudoku.
     */
    private static final Border BOX_BORDER 
            = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    
    /**
     * The filter that will only accept sudoku files (with the extension .sud).
     */
    private static final FileFilter SUDOKU_FILE_FILTER 
            = new FileNameExtensionFilter("Sudoku File", "sud");
    
    /**
     * The data model which stores the displayed sudoku and provides operations
     * on it.
     */
    private final DisplayData data;
    
    /**
     * A collection of menu items that will perform operations on the current 
     * sudoku if the user performs an action on them. They will be disabled and
     * enabled depending on the {@link DisplayData#isOperationOnSudokuAllowed()}
     * state of the data model.
     */
    private Collection<JMenuItem> operationsOnSudoku = new ArrayList<>();
    
    /**
     * The file chooser that should be used to for selecting sudoku files.<p>
     * Using one instance of JFileChooser, the last folder will be remembered.
     */
    private final JFileChooser fileChooser = new JFileChooser();

    /**
     * Constructs and shows a SudokuFrame window that will always display the
     * current state of the given dataModel by observing it as an 
     * {@link sudoku.util.Observer}. The controller included in this frame will
     * perform the operations on the dataModel the user requests.
     * 
     * @param dataModel The data model that stores and manages the displayed 
     *                  data.
     */
    public SudokuFrame(DisplayData dataModel) {
        data = dataModel;
        data.attachObserver(this);

        fileChooser.setFileFilter(SUDOKU_FILE_FILTER);
        
        setJMenuBar(new SudokuMenuBar());
        setEnableStates();
        
        // The info text that initially explains why the window is empty.
        String info = "No sudoku file loaded. Press CTRL + O to open a file.";
        
        // Apply basic inline CSS to the info text for proper line wrapping.
        add(new JLabel("<html><body style='width: 100%; text-align: center;'>" 
                + info + "</body></html>", SwingConstants.CENTER));
        
        setTitle("Sudoku");

        /*
         * By setting the pref. size on the content pane, it won't be necessary
         * to restore the default behavior once a sudoku is loaded.
         */
        getContentPane().setPreferredSize(PREF_SIZE);
        
        setMinimumSize(MIN_SIZE);
        pack();
        setLocationRelativeTo(null);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                /*
                 * The result of the current calculation is not needed because
                 * the view that would display the updated data model is closed.
                 */
                data.stopOngoingCalculation();
            }
        });
        
        setVisible(true);
    }
    
    /**
     * {@inheritDoc}
     * Updates the view if a new sudoku is loaded. Only updates the enabled 
     * state of buttons that can perform operations on the current sudoku if the
     * sudoku-lock was changed.
     */
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
            break;
            
        default:
            /* No updates necessary, the cells get individually updated. */
            break;
        }
    }
    
    /**
     * Sets the enabled states of all components, which can do operations on the
     * current sudoku, to the state stored in the data model. This should lock
     * (or unlock) all operations on the current sudoku.
     */
    private void setEnableStates() {
        boolean allowed = data.isOperationOnSudokuAllowed();
        
        operationsOnSudoku.forEach(o -> {
            o.setEnabled(allowed);
        });
    }

    /**
     * Completely recreates the components used to display the sudoku board.
     * This is needed when the first sudoku or a new sudoku with different sizes
     * is loaded.
     * <p>
     * This method will replace the current content pane and then resize and 
     * relocate the frame.
     */
    private void resetBoardView() {
        int numbers = data.getNumbers();
        int boxRows = data.getBoxRows();
        int boxCols = data.getBoxCols();
        Container content = new Container();
        LayoutManager outerLayout = new GridLayout(boxCols, boxRows);
        LayoutManager innerLayout = new GridLayout(boxRows, boxCols);
        
        // Set the layout manager of the container which contains all boxes.
        content.setLayout(outerLayout);
        
        /*
         * The sudoku is only displayed correctly when the box coordinate system
         * is used. For other structures the index has to be converted.
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
        pack(); // Handles validation of the whole Container as well.
        setLocationRelativeTo(null);
    }

    /**
     * This class is the custom {@link JMenuBar} of a SudokuFrame and provides
     * options to: <ul>
     *     <li> Close the SudokuFrame.
     *     <li> Load a new sudoku from a sudoku file.
     *     <li> Undo the last operation on the current sudoku.
     *     <li> Suggest a correct value.
     *     <li> Completely solve the current sudoku.
     */
    private class SudokuMenuBar extends JMenuBar {
        
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new menu bar which already contains all menu items with
         * listeners added.
         */
        SudokuMenuBar() {
            super();
            
            JMenu fileMenu = new JMenu("File");
            JMenu editMenu = new JMenu("Edit");
            JMenu solveMenu = new JMenu("Solve");

            /*
             * These menus contain only operations on a sudoku, so they can be
             * locked completely.
             */
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
            solve.addActionListener(e -> {
                try {
                    data.solve();
                } catch (InvalidSudokuException e1) {
                    new JOptionPane(
                            "Invalid sudoku", JOptionPane.ERROR_MESSAGE);
                }
            });
            suggest.addActionListener(e -> {
                try {
                    data.suggestValue();
                } catch (InvalidSudokuException e1) {
                    new JOptionPane(
                            "Invalid sudoku", JOptionPane.ERROR_MESSAGE);
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
    
    /**
     * This {@link ActionListener} handles the action of opening a sudoku file.
     */
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
