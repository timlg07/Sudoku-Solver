package sudoku.gui;

import java.util.Collection;
import java.util.LinkedList;

public abstract class Observable {

    private boolean changed = false;
    private Collection<Observer> observers = new LinkedList<>();
    
    public synchronized void attachObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public synchronized void detachObserver(Observer observer) {
        observers.remove(observer);
    }
    
    public synchronized void detachAllObservers() {
        observers.clear();
    }

    public void notifyObservers() {
        notifyObservers(null);
    }
    
    public void notifyObservers(Object argument) {
        if (hasChanged()) {
            Collection<Observer> observersToNotify = new LinkedList<>();
            
            /*
             *  Store all currently attached observers, so that the monitor does
             *  not have to be hold for the actual notify calls without causing 
             *  a ConcurrentModificationException.
             */
            synchronized (this) {
                observersToNotify.addAll(observers);
            }
            
            observersToNotify.forEach(o -> o.update(this, argument));
        }
    }
    
    public synchronized boolean hasChanged() {
        return changed;
    }
    
    protected synchronized void setChanged() {
        changed = true;
    }
    
    protected synchronized void clearChanged() {
        changed = false;
    }
}
