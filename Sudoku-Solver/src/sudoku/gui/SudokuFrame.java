package sudoku.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

public class SudokuFrame extends JFrame implements Observer {
    
    private static final long serialVersionUID = 1L;
    private static final Dimension PREF_SIZE = new Dimension(600, 300);
    private final Model model;

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

}
