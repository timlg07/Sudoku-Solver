package sudoku.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import sudoku.gui.model.DisplayData;
import sudoku.io.SudokuFileParser;
import sudoku.solver.Board;
import sudoku.solver.InvalidSudokuException;
import sudoku.solver.UnsolvableSudokuException;

/**
 * The custom JFrame that is the main window of the graphical user interface.
 * It contains a simple menu-bar and, if a file was loaded, an editable sudoku.
 */
public class SudokuFrame extends JFrame {
    
    private static final long serialVersionUID = 5099335190420782125L;

    /**
     * The preferred size of the initial content pane containing only the info 
     * text.
     */
    private static final Dimension PREF_SIZE_INITIAL = new Dimension(400, 300);
    
    /**
     * The minimum size the window can have. This sizes ensures that it will 
     * still be displayed as a proper window and show the menu bar.
     */
    private static final Dimension MIN_SIZE = new Dimension(240, 160);
    
    /**
     * The filter that will only accept sudoku files (with the extension .sud).
     */
    private static final FileFilter SUDOKU_FILE_FILTER 
            = new FileNameExtensionFilter("Sudoku File", "sud");
    
    /**
     * The file chooser that should be used to select sudoku files.
     * <p>
     * Using only one instance of JFileChooser, the last folder will be
     * remembered.
     */
    private final JFileChooser fileChooser = new JFileChooser();
    
    /**
     * A collection of menu items that will perform operations on the current 
     * sudoku if the user performs an action on them. They can be disabled
     * temporarily when the sudoku should or cannot be edited.
     */
    private final List<JMenuItem> operationsOnSudoku = new ArrayList<>();
    
    /**
     * The data model which stores the currently displayed sudoku and provides
     * operations on it.
     */
    private DisplayData currentData;
    
    /**
     * The representation of the current sudoku board that gets updated by the
     * data model.
     */
    private GameBoardPanel currentGameBoardPanel;
    
    /**
     * The current calculation thread that is doing an operation on the sudoku
     * or {@code null} if no operation is running.
     */
    private Thread calculationThread;

    /**
     * Constructs and shows a SudokuFrame window with a default information text
     * about how to get started and a menu bar with all available operations.
     */
    public SudokuFrame() {
        super("Sudoku");
        
        fileChooser.setFileFilter(SUDOKU_FILE_FILTER);
        
        setJMenuBar(createMenuBar());
        
        /*
         * Initially disable all operations on a sudoku, because no sudoku is
         * loaded.
         */
        setEnableStates(false);
        
        // The info text that initially explains why the window is empty.
        String info = "No sudoku file loaded. Press CTRL + O to open a file.";
        
        // Apply basic inline CSS to the info text for proper line wrapping.
        add(new JLabel("<html><body style='width: 100%; text-align: center;'>" 
                       + info + "</body></html>", SwingConstants.CENTER));

        /*
         * By setting the pref. size on the content pane, it won't be necessary
         * to restore the default behavior once a sudoku is loaded.
         */
        getContentPane().setPreferredSize(PREF_SIZE_INITIAL);
        
        setMinimumSize(MIN_SIZE);
        pack();
        setLocationRelativeTo(null);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evt) {
                /*
                 * The result of the current calculation is not needed because
                 * the view that would display the updated data model is closed.
                 */
                stopOngoingCalculation();
            }
        });

        setVisible(true);
    }
    
    /**
     * Sets the enabled states of all components, which can do operations on the
     * current sudoku, to the given state. This locks (or unlocks) all menu
     * items that can change the sudoku and all popup-menus of the current
     * sudoku.
     * 
     * @param allowed Whether operations on the sudoku are currently allowed or
     *                not.
     */
    private void setEnableStates(boolean allowed) {
        operationsOnSudoku.forEach(o -> o.setEnabled(allowed));
        if (currentGameBoardPanel != null) {
            currentGameBoardPanel.setPopupsEnabled(allowed);
        }
    }

    /**
     * Completely (re)creates the components used to display the sudoku board.
     * This is needed when the first sudoku or a new sudoku with different sizes
     * is loaded.
     * <p>
     * This method will replace the current content pane and then resize and 
     * relocate the frame. It also stops the calculation thread if there are
     * ongoing calculations and enables all operations on a sudoku again.
     */
    private void resetBoardView() {
        // A data model needs to be loaded. 
        assert currentData != null;

        // Update the visible frame and its content.
        currentGameBoardPanel = new GameBoardPanel(currentData);
        setContentPane(currentGameBoardPanel);
        pack(); // Handles validation of the whole Container as well.
        setLocationRelativeTo(null);
        
        /*
         * Stop ongoing calculation on the last sudoku, since the result can not
         * be shown anymore and the operations need to be unlocked.
         */
        stopOngoingCalculation();
    }

    /**
     * Creates a new menu bar which already contains all menu items with
     * listeners added. It provides options to: 
     * <ul>
     *     <li> Close the SudokuFrame.
     *     <li> Load a new sudoku from a sudoku file.
     *     <li> Undo the last operation on the current sudoku.
     *     <li> Suggest a correct value.
     *     <li> Completely solve the current sudoku.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu solveMenu = new JMenu("Solve");

        setEnableStates(false); // Initially there is no sudoku loaded.
        
        JMenuItem open = fileMenu.add("Open");
        JMenuItem exit = fileMenu.add("Exit");
        JMenuItem undo = editMenu.add("Undo");
        JMenuItem suggest = solveMenu.add("Suggest Value");
        JMenuItem solve = solveMenu.add("Solve");
        
        operationsOnSudoku.add(undo);
        operationsOnSudoku.add(suggest);
        operationsOnSudoku.add(solve);

        setCtrlAccelerator(open, KeyEvent.VK_O);
        setCtrlAccelerator(exit, KeyEvent.VK_X);
        setCtrlAccelerator(undo, KeyEvent.VK_Z);
        setCtrlAccelerator(suggest, KeyEvent.VK_V);
        setCtrlAccelerator(solve, KeyEvent.VK_A);

        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu.setMnemonic(KeyEvent.VK_E);
        solveMenu.setMnemonic(KeyEvent.VK_S);
        open.setMnemonic(KeyEvent.VK_O);
        exit.setMnemonic(KeyEvent.VK_X);
        undo.setMnemonic(KeyEvent.VK_U);
        suggest.setMnemonic(KeyEvent.VK_V);
        solve.setMnemonic(KeyEvent.VK_S);

        open.addActionListener(new OpenFileActionListener());
        exit.addActionListener(evt -> dispose());
        undo.addActionListener(evt -> currentData.undo());
        
        solve.addActionListener(evt -> {
            calculationThread = new Thread(new SudokuOperation(true) {
                
                @Override
                protected Board boardSupplierOperation() 
                        throws InvalidSudokuException,
                               UnsolvableSudokuException {
                    return currentData.getSolvedBoard();
                }
            });
            
            setEnableStates(false);
            calculationThread.start();
        });
        
        suggest.addActionListener(e -> {
            if (currentData.isFilled()) {
                SudokuDialogMessages.showErrorAlreadyFilled(SudokuFrame.this);
            } else {
                calculationThread = new Thread(new SudokuOperation(false) {
                    
                    @Override
                    protected Board boardSupplierOperation() 
                            throws InvalidSudokuException, 
                                   UnsolvableSudokuException  {
                        return currentData.getBoardWithSuggestion();
                    }
                });
                
                setEnableStates(false);
                calculationThread.start();
            }
        });

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(solveMenu);
        return menuBar;
    }
    
    /**
     * Sets the accelerator of the given menu item to the key stroke consisting
     * of the given key event masked with the CTRL key modifier.
     * 
     * @param component The menu item the accelerator should be set on.
     * @param key The numeric code for a keyboard key.
     */
    private void setCtrlAccelerator(JMenuItem component, int key) {
        component.setAccelerator(
                KeyStroke.getKeyStroke(key, KeyEvent.CTRL_DOWN_MASK));
    }
    
    /**
     * This {@link ActionListener} handles the action of opening a sudoku file.
     */
    private class OpenFileActionListener implements ActionListener {

        /**
         * Shows a file chooser dialog and then loads the selected file.
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            int fileChooserState = fileChooser.showOpenDialog(SudokuFrame.this);
            if (fileChooserState == JFileChooser.APPROVE_OPTION) {
                loadSudokuFile(fileChooser.getSelectedFile());
            }
        }
        
        /**
         * Loads the content of a sudoku file in the data model and shows a
         * representation of it. Shows an error message if the sudoku file
         * cannot be loaded and parsed to a valid sudoku.
         * 
         * @param sudokuFile The file that should be loaded as sudoku.
         */
        private void loadSudokuFile(File sudokuFile) {
            try {
                Board board = SudokuFileParser.parseToBoard(sudokuFile);
                currentData = new DisplayData(board);
                resetBoardView();
            } catch (InvalidSudokuException | IOException | ParseException e) {
                JOptionPane.showMessageDialog(
                        SudokuFrame.this, 
                        e.getMessage(),
                        "Unable to parse this file.", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * A {@code SudokuOperation} runnable should be used in threads to perform
     * heavy calculations on the current sudoku. Using a seperate thread ensures
     * that the Swing EventDispatcher stays responsive and can process user
     * interaction while the sudoku gets changed.
     */
    private abstract class SudokuOperation implements Runnable {
        
        /**
         * Signalizes if the operation is expected to always create a completely
         * solved sudoku.
         */
        private final boolean isSolutionExpected;
        
        /**
         * Creates a new {@code SudokuOperationThread} with the given solution
         * expected flag.
         * 
         * @param isSolutionExpected Whether the operation is expected to always
         *                           create a completely solved sudoku or not.
         */
        private SudokuOperation(boolean isSolutionExpected) {
            this.isSolutionExpected = isSolutionExpected;
        }
        
        /**
         * Performs the operation on the sudoku that may take a while and that
         * produces a result in the form of a {@link Board}.
         * 
         * @return The result of the operation.
         * @throws InvalidSudokuException The current sudoku is invalid.
         * @throws UnsolvableSudokuException The current sudoku is unsolvable.
         */
        protected abstract Board boardSupplierOperation() 
                throws InvalidSudokuException, UnsolvableSudokuException;
        
        /**
         * Executes the operation on the sudoku and then applies its result to
         * the data model. Sets the operations on the sudoku to allowed and
         * shows dialogs to the user if necessary.
         */
        @Override
        public void run() {
            try {
                // Execute the operation on the sudoku that may take a while.
                Board result = boardSupplierOperation();
                
                /*
                 * Update model and view sequential with the other operations
                 * on the AWT event dispatching thread.
                 */
                SwingUtilities.invokeLater(() -> {
                    currentData.applyMachineMove(result);
                    setEnableStates(true);
                    
                    if (!isSolutionExpected) {
                        SudokuDialogMessages.showMessageIfFilled(
                                SudokuFrame.this, currentData);
                    }
                });
            } catch (InvalidSudokuException exc) {
                SudokuDialogMessages.showErrorInvalid(SudokuFrame.this);
                setEnableStates(true);
            } catch (UnsolvableSudokuException exc) {
                SudokuDialogMessages.showErrorUnsolvable(SudokuFrame.this);
                setEnableStates(true);
            }
        }
    }
    
    /**
     * If there is an ongoing calculation in a separate thread, this thread is
     * stopped and all calculations on the sudoku are enabled again.
     */
    @SuppressWarnings("deprecation")
    public void stopOngoingCalculation() {
        if (calculationThread != null) {
            calculationThread.stop();
            calculationThread = null;
        }
        setEnableStates(true);
    }
}
