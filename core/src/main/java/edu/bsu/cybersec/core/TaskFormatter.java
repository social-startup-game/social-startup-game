package edu.bsu.cybersec.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import static com.google.common.base.Preconditions.checkArgument;

public final class TaskFormatter {

    private static final BiMap<Integer, String> MAP = ImmutableBiMap.of(
            Task.IDLE, "Idle",
            Task.DEVELOPMENT, "Development",
            Task.MAINTENANCE, "Maintenance");

    public String format(int task) {
        checkArgument(MAP.containsKey(task));
        return MAP.get(task);
    }

    public int asTask(String string) {
        checkArgument(MAP.containsValue(string));
        return MAP.inverse().get(string);
    }
}
