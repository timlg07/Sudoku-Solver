package sudoku.gui;

public class Controller implements Observer {

    
    Model model;
    View view;
    
    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        model.attachObserver(this);
    }

    @Override
    public void update() {

    }

}
