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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.bsu.cybersec.core.systems.*;
import playn.core.Tile;
import react.Signal;
import react.UnitSignal;
import react.Value;
import tripleplay.entity.Component;
import tripleplay.entity.Entity;
import tripleplay.entity.World;

import java.util.List;
import java.util.Map;

public class GameWorld extends World {
    public final Map<String, Component> components = Maps.newHashMap();

    public final Signal<NarrativeEvent> onNarrativeEvent = Signal.create();
    public final UnitSignal onGameEnd = new UnitSignal();
    public final Value<GameTime> gameTime = Value.create(new GameTime(0, 0));
    public final Value<Float> exposure = Value.create(0f);
    public final Value<Float> users = Value.create(0f);
    public final List<Entity> workers = Lists.newArrayListWithCapacity(3);
    public final Value<Integer> gameEnd = Value.create(-1);

    public final Component.Generic<Task> tasked = register("tasked", new Component.Generic<Task>(this));
    public final Component.IScalar employeeNumber = register("employeeNumber", new Component.IScalar(this));
    public final Component.FScalar developmentSkill = register("developmentSkill", new Component.FScalar(this));
    public final Component.FScalar maintenanceSkill = register("maintenanceSkill", new Component.FScalar(this));
    public final Component.Generic<String> name = register("name", new Component.Generic<String>(this));
    public final Component.FScalar developmentProgress = register("developmentProgress", new Component.FScalar(this));
    public final Component.IScalar goal = register("goal", new Component.IScalar(this));
    public final Component.FScalar usersPerHour = register("usersPerHour", new Component.FScalar(this));
    public final Component.IScalar expiresIn = register("expiresIn", new Component.IScalar(this));
    public final Component.Generic<Tile> image = register("image", new Component.Generic<Tile>(this));
    public final Component.Generic<Updatable> onUpdate = register("onUpdate", new Component.Generic<Updatable>(this));
    public final Component.IScalar timeTrigger = register("timeTrigger", new Component.IScalar(this));
    public final Component.Generic<Runnable> event = register("event", new Component.Generic<Runnable>(this));
    public final Component.FScalar vulnerability = register("vulnerability", new Component.FScalar(this));
    public final Component.IScalar usersPerHourState = register("usersPerHourState", new Component.IScalar(this));
    public final Component.IScalar featureNumber = register("featureNumber", new Component.IScalar(this));
    public final Component.IScalar exploitNumber = register("exploitNumber", new Component.IScalar(this));
    public final Component.FScalar userAttrition = register("userAttrition", new Component.FScalar(this));
    public final Component.FScalar lostUsers = register("lostUsers", new Component.FScalar(this));
    public final Component.Generic<EmployeeProfile> profile = register("profile", new Component.Generic<EmployeeProfile>(this));
    public final Component.FScalar maintenanceProgress = register("maintenanceProgress", new Component.FScalar(this));

    private <T extends Component> T register(String name, T component) {
        components.put(name, component);
        return component;
    }

    public void advanceGameTime(int ms) {
        GameTime newTime = new GameTime(gameTime.get(), ms);
        gameTime.update(newTime);
    }

    public static class Systematized extends GameWorld {
        {
            initializeSystemsThatAreNeverDirectlyReferenced();
        }

        private void initializeSystemsThatAreNeverDirectlyReferenced() {
            new UpdatingSystem(this);
            new ExposureReductionSystem(this);
            new ExpirySystem(this);
            new EventTriggerSystem(this);
            new LearningSystem(this);
            new ExploitMaintenanceSystem(this);
            new UserAttritionSystem(this);
        }

        public final GameTimeSystem gameTimeSystem = new GameTimeSystem(this);
        public final UserGenerationSystem userGenerationSystem = new UserGenerationSystem(this);
        public final FeatureGenerationSystem featureGenerationSystem = new FeatureGenerationSystem(this);
        public final FeatureDevelopmentSystem featureDevelopmentSystem = new FeatureDevelopmentSystem(this);
        public final ExploitSystem exploitSystem = new ExploitSystem(this);
    }
}
