package edu.bsu.cybersec.core;

import com.google.common.collect.ImmutableList;

public final class Task {
    public static final int IDLE = 0;
    public static final int DEVELOPMENT = 1;

    public static final ImmutableList<Integer> VALUES = ImmutableList.of(IDLE, DEVELOPMENT);

    private Task() {
    }
}
