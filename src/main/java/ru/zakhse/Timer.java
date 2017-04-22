package ru.zakhse;

public class Timer {
    private enum State {READY, RUNNING, STOPPED}

    private long startTime;
    private int elapsedTime;
    private int addedTime;
    private State state;

    public Timer() {
        state = State.READY;
        elapsedTime = 0;
        addedTime = 0;
    }

    public void addTime(int time) {
        if (state != State.READY)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        addedTime += time;
    }

    public void start() {
        if (state != State.READY && state != State.STOPPED)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        startTime = System.currentTimeMillis();
        state = State.RUNNING;
    }

    public void stop() {
        if (state != State.RUNNING)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        elapsedTime = (int) (System.currentTimeMillis() - startTime + addedTime);
        state = State.STOPPED;
    }

    public int getElapsedTime() {
        if (state != State.STOPPED && state != State.READY)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        return elapsedTime;
    }

    public int countAndGetElapsedTime() {
        if (state == State.READY) {
            elapsedTime = addedTime;
        } else
            elapsedTime = (int) (System.currentTimeMillis() - startTime + addedTime);
        return elapsedTime;
    }

    public void clear() {
        if (state != State.STOPPED && state != State.READY)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        state = State.READY;
        elapsedTime = 0;
        addedTime = 0;
        startTime = 0;
    }
}
