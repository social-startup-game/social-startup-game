package edu.bsu.cybersec.core;

public final class SimClock {
    public int elapsedMS;
    public int tickMS;

    public void advance(int ms) {
        elapsedMS = ms;
        tickMS += ms;
    }
}
