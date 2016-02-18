/*
 * Copyright 2016 Paul Gestwicki
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
import playn.scene.Layer;
import react.Signal;
import react.UnitSignal;
import react.Value;
import tripleplay.entity.Component;
import tripleplay.entity.Entity;
import tripleplay.entity.World;

import java.util.List;
import java.util.Map;

public class GameWorld extends World {
    private static final long START_DATE_MIDNIGHT = 61410200400L;
    public static final int START_HOUR_OFFSET = ClockUtils.SECONDS_PER_HOUR * 8;
    private static final long EIGHT_OCLOCK_ON_A_MONDAY_IN_SECONDS = START_DATE_MIDNIGHT + START_HOUR_OFFSET;

    public final Map<String, Component> components = Maps.newHashMap();

    public final long startTime = EIGHT_OCLOCK_ON_A_MONDAY_IN_SECONDS;
    public final Signal<NarrativeEvent> onNarrativeEvent = Signal.create();
    public final UnitSignal onGameEnd = new UnitSignal();
    public final Value<GameTime> gameTime = Value.create(new GameTime(0, 0));
    public final Value<Float> exposure = Value.create(0f);
    public final Value<Float> users = Value.create(0f);
    public final List<Entity> workers = Lists.newArrayListWithCapacity(3);
    public final Value<Integer> gameEnd = Value.create(-1);
    public final Value<Company> company = Value.create(null);
    public final int developmentTaskId;
    public final int maintenanceTaskId;
    public final int notAtWorkTaskId;

    public final Component.IScalar employeeNumber = register("employeeNumber", new Component.IScalar(this));
    public final Component.FScalar developmentSkill = register("developmentSkill", new Component.FScalar(this));
    public final Component.FScalar maintenanceSkill = register("maintenanceSkill", new Component.FScalar(this));
    public final Component.Generic<String> name = register("name", new Component.Generic<String>(this));
    public final Component.FScalar developmentProgress = register("developmentProgress", new Component.FScalar(this));
    public final Component.IScalar goal = register("goal", new Component.IScalar(this));
    public final Component.FScalar usersPerHour = register("usersPerHour", new Component.FScalar(this));
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
    public final Component.Generic<Layer> sprite = register("sprite", new Component.Generic<Layer>(this));
    public final Component.XY position = register("position", new Component.XY(this));
    public final Component.IScalar secondsRemaining = register("secondsRemaining", new Component.IScalar(this));
    public final Component.Generic<Runnable> onComplete = register("onComplete", new Component.Generic<Runnable>(this));
    public final Component.IScalar owner = register("owner", new Component.IScalar(this));
    public final Component.IScalar task = register("task", new Component.IScalar(this));
    public final Component.IScalar taskFlags = register("taskFlags", new Component.IScalar(this));


    private <T extends Component> T register(String name, T component) {
        components.put(name, component);
        return component;
    }

    public GameWorld() {
        developmentTaskId = makeDevelopmentTask();
        maintenanceTaskId = makeMaintenanceTask();
        notAtWorkTaskId = makeNotAtWorkTask();
    }

    private int makeDevelopmentTask() {
        int id = create(true).add(name, taskFlags).id;
        name.set(id, "Development");
        taskFlags.set(id,
                TaskFlags.flags(TaskFlags.REASSIGNABLE, TaskFlags.BOUND_TO_WORKDAY, TaskFlags.DEVELOPMENT));
        return id;
    }

    private int makeMaintenanceTask() {
        int id = create(true).add(name, taskFlags).id;
        name.set(id, "Maintenance");
        taskFlags.set(id,
                TaskFlags.flags(TaskFlags.REASSIGNABLE, TaskFlags.BOUND_TO_WORKDAY, TaskFlags.MAINTENANCE));
        return id;
    }

    private int makeNotAtWorkTask() {
        int id = create(true).add(name, taskFlags).id;
        name.set(id, "Not at work");
        taskFlags.set(id,
                TaskFlags.flags(TaskFlags.NOT_AT_WORK));
        return id;
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
            new EventTriggerSystem(this);
            new LearningSystem(this);
            new ExploitMaintenanceSystem(this);
            new UserAttritionSystem(this);
            new LayerPositionSystem(this);
            new TaskProgressSystem(this);
        }

        public final GameTimeSystem gameTimeSystem = new GameTimeSystem(this);
        public final UserGenerationSystem userGenerationSystem = new UserGenerationSystem(this);
        public final FeatureGenerationSystem featureGenerationSystem = new FeatureGenerationSystem(this);
        public final FeatureDevelopmentSystem featureDevelopmentSystem = new FeatureDevelopmentSystem(this);
        public final ExploitSystem exploitSystem = new ExploitSystem(this);
        public final WorkHoursSystem workHoursSystem = new WorkHoursSystem(this, gameTimeSystem);
    }
}
