package edu.bsu.cybersec.core;

import tripleplay.entity.Component;
import tripleplay.entity.World;

public class GameWorld extends World {
    public final Component.Generic<SimClock> simClock = new Component.Generic<>(this);
    public final Component.Generic<Task> tasked = new Component.Generic<>(this);
    public final Component.IScalar developmentSkill = new Component.IScalar(this);
    public final Component.IScalar type = new Component.IScalar(this);
    public final Component.FScalar progressRate = new Component.FScalar(this);
    public final Component.FScalar progress = new Component.FScalar(this);
    public final Component.FScalar goal = new Component.FScalar(this);
    public final Component.FScalar usersPerSecond = new Component.FScalar(this);
    public final Component.FScalar users = new Component.FScalar(this);

    public static class Systematized extends GameWorld {
        public final TimeElapseSystem timeElapseSystem = new TimeElapseSystem(this);
        public final UserGenerationSystem userGenerationSystem = new UserGenerationSystem(this);
        public final ProgressSystem progressSystem = new ProgressSystem(this);
        public final FeatureDevelopmentSystem featureDevelopmentSystem = new FeatureDevelopmentSystem(this);
    }
}