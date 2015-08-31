package edu.bsu.cybersec.core;

import static com.google.common.base.Preconditions.checkNotNull;

public enum Task {
    IDLE("Idle"),
    DEVELOPMENT("Development");

    public final String name;

    Task(String name) {
        this.name = checkNotNull(name);
    }

    public static Task forName(String name) {
        for (Task t : values()) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        throw new IllegalArgumentException("No task named " + name);
    }
}
