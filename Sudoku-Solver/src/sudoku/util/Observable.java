package sudoku.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import sudoku.gui.model.DisplayDataChange;

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
        Collection<Observer> observersToNotify;

        synchronized (this) {
            if (!changed) {
                /* Do not notify the observers if nothing has changed. */
                return;
            }

            /*
             * Store all currently attached observers, so that the monitor does
             * not have to be hold for the actual notify calls and that no
             * ConcurrentModificationException occurs.
             */
            observersToNotify = new ArrayList<>(observers);
            
            clearChanged();
        }

        System.out.println(
            "change:" + (DisplayDataChange)argument + " by "
            + Thread.currentThread().getStackTrace()[2].getMethodName() + " @ "
            + Thread.currentThread().getName());

        observersToNotify.forEach(o -> o.update(this, argument));
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
