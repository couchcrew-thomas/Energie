package de.melchernetz.energy.simulation;

/**
 * Wrapper-Klasse für einen Task um eine Benachrichtigung zu erhalten, wenn der Task beendet wurde.
 * (Klasse wird einem Thread übergeben)
 */
public class CallbackRunnable implements Runnable {

    private final Runnable task;
    private final Callback callback;

    public CallbackRunnable(Runnable task, Callback callback) {
        this.task = task;
        this.callback = callback;
    }

    @Override
    public void run() {
        task.run();
        callback.complete();
    }

    public static interface Callback {

        void complete();
    }
}
