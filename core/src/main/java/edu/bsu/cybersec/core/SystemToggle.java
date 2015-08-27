package edu.bsu.cybersec.core;

import tripleplay.entity.System;

public class SystemToggle {
    private final System[] systems;

    public SystemToggle(System... systems) {
        this.systems = new System[systems.length];
        java.lang.System.arraycopy(systems, 0, this.systems, 0, systems.length);
    }


    public SystemToggle disable() {
        for (System system : systems) {
            system.setEnabled(false);
        }
        return this;
    }

    public SystemToggle enable() {
        for (System system : systems) {
            system.setEnabled(true);
        }
        return this;
    }
}
