package edu.bsu.cybersec.core;

import tripleplay.entity.Component;
import tripleplay.entity.World;

public final class GameWorld extends World {
    public final Component.IScalar elapsedSimMs = new Component.IScalar(this);
}
