/*
 * Copyright 2015 Paul Gestwicki
 *
 * This file is part of The Social Startup Game
 *
 * The Social Startup Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Social Startup Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The Social Startup Game.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.bsu.cybersec.core;

import com.google.common.collect.Maps;
import playn.core.Image;
import react.Signal;
import react.Value;
import tripleplay.entity.Component;
import tripleplay.entity.World;

import java.util.Map;

public class GameWorld extends World {
    public final Map<String, Component> components = Maps.newHashMap();

    public final Signal<NarrativeEvent> onNarrativeEvent = Signal.create();
    public int prevGameTimeMs;
    public int gameTimeMs;
    public final Value<Float> exposure = Value.create(0f);
    public final Value<Float> users = Value.create(0f);

    public final Component.Generic<Task> tasked = register("tasked", new Component.Generic<Task>(this));
    public final Component.IScalar employeeNumber = register("employeeNumber", new Component.IScalar(this));
    public final Component.FScalar developmentSkill = register("developmentSkill", new Component.FScalar(this));
    public final Component.FScalar maintenanceSkill = register("maintenanceSkill", new Component.FScalar(this));
    public final Component.Generic<Name> name = register("name", new Component.Generic<Name>(this));
    public final Component.FScalar developmentProgress = register("developmentProgress", new Component.FScalar(this));
    public final Component.IScalar goal = register("goal", new Component.IScalar(this));
    public final Component.FScalar usersPerHour = register("usersPerHour", new Component.FScalar(this));
    public final Component.IScalar expiresIn = register("expiresIn", new Component.IScalar(this));
    public final Component.Generic<Image> image = register("image", new Component.Generic<Image>(this));
    public final Component.Generic<Updatable> onUpdate = register("onUpdate", new Component.Generic<Updatable>(this));
    public final Component.IScalar timeTrigger = register("timeTrigger", new Component.IScalar(this));
    public final Component.Generic<Runnable> event = register("event", new Component.Generic<Runnable>(this));
    public final Component.FScalar vulnerability = register("vulnerability", new Component.FScalar(this));
    public final Component.IScalar vulnerabilityState = register("vulnerabilityState", new Component.IScalar(this));
    public final Component.IScalar usersPerHourState = register("usersPerHourState", new Component.IScalar(this));
    public final Component.IScalar featureNumber = register("featureNumber", new Component.IScalar(this));

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
        public final VulnerabilitySystem vulnerabilitySystem = new VulnerabilitySystem(this);
        public final FeatureGenerationSystem featureGenerationSystem = new FeatureGenerationSystem(this);
        public final LearningSystem learningSystem = new LearningSystem(this);
    }
}
