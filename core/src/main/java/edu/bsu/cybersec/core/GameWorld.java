package edu.bsu.cybersec.core;

import tripleplay.entity.Component;
import tripleplay.entity.World;

public class GameWorld extends World {
    public final Component.Generic<SimClock> simClock = new Component.Generic<SimClock>(this);
    public final TimeElapseSystem timeElapseSystem = new TimeElapseSystem(this);
}
