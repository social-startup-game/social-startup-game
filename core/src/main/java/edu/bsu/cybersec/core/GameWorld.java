package edu.bsu.cybersec.core;

import tripleplay.entity.Component;
import tripleplay.entity.World;

public class GameWorld extends World {
    public final Component.Generic<SimClock> simClock = new Component.Generic<>(this);
    public final Component.Generic<Company> company = new Component.Generic<>(this);
    public final Component.Generic<Feature> feature = new Component.Generic<>(this);
    public final Component.Generic<FeatureInDevelopment> featureInDevelopment = new Component.Generic<>(this);
    public final Component.Generic<Task> tasked = new Component.Generic<>(this);
    public final Component.IScalar developmentSkill = new Component.IScalar(this);
    public final Component.FScalar progressRate = new Component.FScalar(this);
    public final Component.FScalar progress = new Component.FScalar(this);
    public final Component.FScalar goal = new Component.FScalar(this);

    public static class Systematized extends GameWorld {
        public final TimeElapseSystem timeElapseSystem = new TimeElapseSystem(this);
        public final UserAcquisitionSystem userAcquisitionSystem = new UserAcquisitionSystem(this);
    }
}
