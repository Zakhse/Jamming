package ru.zakhse;

/**
 * Class for counting elapsed time
 */
public class Timer {
    private enum State {READY, RUNNING, STOPPED}

    private long startTime;
    private int elapsedTime;
    private int addedTime;
    private State state;

    /**
     * Creates new timer
     */
    public Timer() {
        state = State.READY;
        elapsedTime = 0;
        addedTime = 0;
    }

    /**
     * Adds some elapsed time manually
     *
     * @param time the amount of time to add
     */
    public void addTime(int time) {
        if (state != State.READY)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        addedTime += time;
    }

    /**
     * Starts or resumes timer
     */
    public void start() {
        if (state != State.READY && state != State.STOPPED)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        startTime = System.currentTimeMillis();
        state = State.RUNNING;
    }

    /**
     * Stops or pauses timer
     */
    public void stop() {
        if (state != State.RUNNING)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        elapsedTime = (int) (System.currentTimeMillis() - startTime + addedTime);
        state = State.STOPPED;
    }

    /**
     * Gets the time which elapsed before last pause or stop (so it doesn't include the time which elapsed after last
     * start)
     *
     * @return elapsed time
     */
    public int getElapsedTime() {
        if (state != State.STOPPED && state != State.READY)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        return elapsedTime;
    }

    /**
     * Gets all elapsed time (so it includes the time which elapsed after last start)
     *
     * @return elapsed time
     */
    public int countAndGetElapsedTime() {
        if (state == State.READY) {
            elapsedTime = addedTime;
        } else
            elapsedTime = (int) (System.currentTimeMillis() - startTime + addedTime);
        return elapsedTime;
    }

    /**
     * Sets elapsed time to zero
     */
    public void clear() {
        if (state != State.STOPPED && state != State.READY)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        state = State.READY;
        elapsedTime = 0;
        addedTime = 0;
        startTime = 0;
    }
}
