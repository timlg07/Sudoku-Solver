package sudoku.gui;

public class View implements Observer {
    
    Model model;
    Controller controller;

    public View(Model model) {
        this.model = model;
        controller = new Controller(model, this);
        model.attachObserver(this);
    }

    @Override
    public void update() {
        
    }

}
