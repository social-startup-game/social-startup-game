package edu.bsu.cybersec.core;

import com.google.common.collect.Maps;
import react.Signal;
import tripleplay.entity.Component;
import tripleplay.entity.World;

import java.util.Map;

public class GameWorld extends World {
    public final Map<String, Component> components = Maps.newHashMap();

    public final Signal<NarrativeEvent> onNarrativeEvent = Signal.create();
    public int prevGameTimeMs;
    public int gameTimeMs;
    public float exposure;
    public float users;

    public final Component.IScalar tasked = register("tasked", new Component.IScalar(this));
    public final Component.IScalar developmentSkill = register("developmentSkill", new Component.IScalar(this));
    public final Component.FScalar maintenanceSkill = register("maintenanceSkill", new Component.FScalar(this));
    public final Component.Generic<String> name = register("name", new Component.Generic<String>(this));
    public final Component.FScalar developmentProgress = register("developmentProgress", new Component.FScalar(this));
    public final Component.IScalar goal = register("goal", new Component.IScalar(this));
    public final Component.FScalar usersPerHour = register("usersPerHour", new Component.FScalar(this));
    public final Component.IScalar expiresIn = register("expiresIn", new Component.IScalar(this));
    public final Component.Generic<String> imagePath = register("imagePath", new Component.Generic<String>(this));
    public final Component.Generic<Updatable> onUpdate = register("onUpdate", new Component.Generic<Updatable>(this));
    public final Component.IScalar timeTrigger = register("timeTrigger", new Component.IScalar(this));
    public final Component.Generic<Runnable> event = register("event", new Component.Generic<Runnable>(this));
    public final Component.FScalar vulnerability = register("vulnerability", new Component.FScalar(this));
    public final Component.IScalar vulnerabilityState = register("vulnerabilityState", new Component.IScalar(this));
    public final Component.IScalar usersPerHourState = register("usersPerHourState", new Component.IScalar(this));

    private <T extends Component> T register(String name, T component) {
        components.put(name, component);
        return component;
    }

    public void advanceGameTime(int ms) {
        this.prevGameTimeMs = gameTimeMs;
        this.gameTimeMs += ms;
    }

    public static class Systematized extends GameWorld {
        public final UpdatingSystem updatingSystem = new UpdatingSystem(this);
        public final GameTimeSystem gameTimeSystem = new GameTimeSystem(this);
        public final UserGenerationSystem userGenerationSystem = new UserGenerationSystem(this);
        public final FeatureDevelopmentSystem featureDevelopmentSystem = new FeatureDevelopmentSystem(this);
        public final MaintenanceSystem maintenanceSystem = new MaintenanceSystem(this);
        public final ExpirySystem expirySystem = new ExpirySystem(this);
        public final EventTriggerSystem eventTriggerSystem = new EventTriggerSystem(this);
    }
}
