package edu.bsu.cybersec.core;

import tripleplay.entity.Component;
import tripleplay.entity.World;

public class GameWorld extends World {
    public final Component.IScalar tasked = new Component.IScalar(this);
    public final Component.IScalar developmentSkill = new Component.IScalar(this);
    public final Component.IScalar type = new Component.IScalar(this);
    public final Component.FScalar progress = new Component.FScalar(this);
    public final Component.IScalar goal = new Component.IScalar(this);
    public final Component.FScalar usersPerSecond = new Component.FScalar(this);
    public final Component.IScalar owner = new Component.IScalar(this);
    public final Component.FScalar users = new Component.FScalar(this);
    public final Component.IScalar gameTime = new Component.IScalar(this);
    public final Component.FScalar gameTimeScale = new Component.FScalar(this);


    public static class Systematized extends GameWorld {
        public final GameTimeSystem gameTimeSystem = new GameTimeSystem(this);
        public final UserGenerationSystem userGenerationSystem = new UserGenerationSystem(this);
        public final FeatureDevelopmentSystem featureDevelopmentSystem = new FeatureDevelopmentSystem(this);
    }
}
