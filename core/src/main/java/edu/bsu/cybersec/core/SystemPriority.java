package edu.bsu.cybersec.core;

public enum SystemPriority {

    CLOCK_LEVEL(10),
    MODEL_LEVEL(5),
    UI_LEVEL(1),
    DEBUG_LEVEL(0);

    public final int value;

    SystemPriority(int value) {
        this.value = value;
    }
}
