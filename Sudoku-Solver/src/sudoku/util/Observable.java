package sudoku.util;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a data model that can be observed by {@link Observer}s.
 */
public abstract class Observable {

    /**
     * Whether the observable was changed since the last notify call or not.
     */
    private boolean changed = false;
    
    /**
     * A collection of all observers that are attached to this observable.
     */
    private List<Observer> observers = new LinkedList<>();
    
    /**
     * Attaches the given observer to this observable so it gets notified about
     * changes. Observers can only be attached once.
     * 
     * @param observer The observer that should be attached.
     */
    public void attachObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Detaches the given observer from this observable so it no longer gets 
     * notified about changes.
     * 
     * @param observer The observer that should be detached.
     */
    public void detachObserver(Observer observer) {
        observers.remove(observer);
    }
    
    /**
     * Detaches all observers that are currently attached to this observable.
     */
    public void detachAllObservers() {
        observers.clear();
    }

    /**
     * Notifies all attached observers with {@code null} as argument.
     */
    public void notifyObservers() {
        notifyObservers(null);
    }
    
    /**
     * Notifies all attached observers about changes in this observable and
     * passes the given argument to all observers.
     * 
     * @param argument The argument that every observers update method receives.
     */
    public void notifyObservers(Object argument) {
        List<Observer> observersToNotify;

        if (!changed) {
            /* Do not notify the observers if nothing has changed. */
            return;
        }

        /*
         * Store all currently attached observers in a separate list to prevent
         * a ConcurrentModificationException.
         */
        observersToNotify = new ArrayList<>(observers);

        clearChanged();
        observersToNotify.forEach(o -> o.update(this, argument));
    }
    
    /**
     * Returns {@code true} if the observable changed since the last notify
     * call.
     * 
     * @return Whether the observable changed or not.
     */
    public boolean hasChanged() {
        return changed;
    }
    
    /**
     * Sets the changed flag to true, signalizing that the observable was 
     * changed since the last notify call.
     */
    protected void setChanged() {
        changed = true;
    }
    
    /**
     * Sets the changed flag to false, signalizing that no changes were made to
     * the observable since the last notify call.
     */
    protected void clearChanged() {
        changed = false;
    }
}
