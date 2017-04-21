package ru.zakhse;

public class Timer {
    private enum State {READY, RUNNING, STOPPED}

    private long startTime;
    private long elapsedTime;
    private State state;

    public Timer() {
        state = State.READY;
        elapsedTime = 0;
    }

    public long start() {
        if (state != State.READY && state != State.STOPPED)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        startTime = System.currentTimeMillis();
        state = State.RUNNING;
        return startTime;
    }

    public void stop() {
        if (state != State.RUNNING)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        elapsedTime += System.currentTimeMillis() - startTime;
        state = State.STOPPED;
    }

    public long getElapsedTime() {
        if (state != State.STOPPED)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        return elapsedTime;
    }

    public void clear() {
        if (state != State.STOPPED && state != State.READY)
            throw new RuntimeException("Timer is in " + state.toString() + " state");
        elapsedTime = 0;
    }
}
