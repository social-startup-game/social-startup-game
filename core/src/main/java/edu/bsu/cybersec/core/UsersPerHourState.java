package edu.bsu.cybersec.core;

public enum UsersPerHourState {
    ACTIVE(1),
    INACTIVE(0);

    public final int value;

    UsersPerHourState(int value) {
        this.value = value;
    }
}
