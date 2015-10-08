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

    public final Component.IScalar tasked = register("tasked", new Component.IScalar(this));
    public final Component.IScalar developmentSkill = register("developmentSkill", new Component.IScalar(this));
    public final Component.FScalar maintenanceSkill = register("maintenanceSkill", new Component.FScalar(this));
    public final Component.Generic<String> name = register("name", new Component.Generic<String>(this));
    public final Component.IScalar type = register("type", new Component.IScalar(this));
    public final Component.FScalar progress = register("progress", new Component.FScalar(this));
    public final Component.IScalar goal = register("goal", new Component.IScalar(this));
    public final Component.IScalar featureId = register("featureId", new Component.IScalar(this));
    public final Component.FScalar usersPerSecond = register("usersPerSecond", new Component.FScalar(this));
    public final Component.IScalar companyId = register("companyId", new Component.IScalar(this));
    public final Component.FScalar users = register("users", new Component.FScalar(this));
    public final Component.FScalar attackSurface = register("attackSurface", new Component.FScalar(this));
    public final Component.IScalar expiresIn = register("expiresIn", new Component.IScalar(this));
    public final Component.Generic<String> imagePath = register("imagePath", new Component.Generic<String>(this));
    public final Component.Generic<Updatable> onUpdate = register("onUpdate", new Component.Generic<Updatable>(this));
    public final Component.IScalar timeTrigger = register("timeTrigger", new Component.IScalar(this));
    public final Component.Generic<Runnable> event = register("event", new Component.Generic<Runnable>(this));

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
