package sudoku.gui;

import java.util.LinkedList;
import java.util.List;

public abstract class Observable {
    
    List<Observer> observers = new LinkedList<Observer>();
    
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void detachObserver(Observer observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }
}
