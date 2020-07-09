package sudoku.gui;

import java.util.Collection;
import java.util.LinkedList;

public abstract class Observable {

    private boolean changed = false;
    private Collection<Observer> observers = new LinkedList<Observer>();
    
    public void attachObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public void detachObserver(Observer observer) {
        observers.remove(observer);
    }
    
    public void detachAllObservers() {
        observers.clear();
    }

    public void notifyObservers() {
        notifyObservers(null);
    }
    
    public void notifyObservers(Object argument) {
        if (hasChanged()) {
            observers.forEach(o -> o.update(this, argument));
        }
    }
    
    public boolean hasChanged() {
        return changed;
    }
    
    protected void setChanged() {
        changed = true;
    }
    
    protected void clearChanged() {
        changed = false;
    }
}
