package sudoku.util;

/**
 * This interface can be implemented by classes that want to be updated about
 * changes in an {@link Observable}.
 */
public interface Observer {

    /**
     * This method is called whenever an {@link Observable}, that this observer
     * is attached to, gets changed.
     * 
     * @param observable The observable that changed.
     * @param argument An argument the observable passed to its notify call.
     */
    void update(Observable observable, Object argument);
    
}
