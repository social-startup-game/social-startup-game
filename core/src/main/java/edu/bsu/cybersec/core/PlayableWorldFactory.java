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

import com.google.common.collect.ImmutableList;
import edu.bsu.cybersec.core.narrative.DefaultNarrativeScript;
import edu.bsu.cybersec.core.ui.GameAssets;
import tripleplay.entity.Entity;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public class PlayableWorldFactory {

    private static final int DAYS_UNTIL_GAME_END = 14;

    private final GameWorld.Systematized world = new GameWorld.Systematized();
    private final GameConfig config;
    private final GameAssets assets;

    public PlayableWorldFactory(GameAssets assets, GameConfig config) {
        this.config = checkNotNull(config);
        this.assets = checkNotNull(assets);
    }

    public GameWorld.Systematized createPlayableGameWorld() {
        initializeWorld();
        return world;
    }

    private void initializeWorld() {
        world.gameTimeSystem.setGameTimeUnitsPerRealClockUnits(ClockUtils.SECONDS_PER_HOUR * 2);
        world.featureGenerationSystem.nextFeatureNumber(1);
        world.featureDevelopmentSystem.inefficiencyFactor.update(1 / 3f);
        world.exposure.update(0.10f);
        world.users.update(1000f);
        makeExistingFeature();
        makeDevelopers(3);
        setEndTime();
        if (SimGame.game.config.useNarrativeEvents()) {
            new DefaultNarrativeScript().createIn(world, config);
        }
    }

    private void setEndTime() {
        Entity end = world.create(true).add(world.timeTrigger, world.event);
        // SECONDS_PER_DAY = 60*60*24 = 8640.
        // DAYS_UNTIL_GAME_ENDS = 14
        // Product of these is 120,960, which is well within integers. The warning must be because
        // it doesn't know what the range of "now" is. So, we suppress the warning.
        //noinspection NumericOverflow
        int gameEnd = world.gameTime.get().now + ClockUtils.SECONDS_PER_DAY * DAYS_UNTIL_GAME_END;
        world.timeTrigger.set(end.id, gameEnd);
        world.event.set(end.id, new Runnable() {
            @Override
            public void run() {
                world.onGameEnd.emit();
            }
        });
        world.gameEnd.update(gameEnd);
    }

    private void makeDevelopers(int number) {
        checkArgument(number >= 0);
        EmployeePool pool = EmployeePool.create(assets);
        List<EmployeePool.Employee> employees = ImmutableList.copyOf(pool.recruit(number));
        for (int i = 0; i < number; i++) {
            Entity e = makeDeveloper(i, employees.get(i));
            world.workers.add(e);
        }
    }

    private Entity makeDeveloper(final int number, final EmployeePool.Employee employee) {
        Entity developer = world.create(true)
                .add(world.employeeNumber,
                        world.developmentSkill,
                        world.tasked,
                        world.maintenanceSkill,
                        world.profile,
                        world.image);
        world.employeeNumber.set(developer.id, number);
        world.tasked.set(developer.id, Task.MAINTENANCE);
        world.developmentSkill.set(developer.id, 5);
        world.maintenanceSkill.set(developer.id, 5);
        world.profile.set(developer.id, employee.profile);
        world.image.set(developer.id, employee.image);
        return developer;
    }

    private void makeExistingFeature() {
        Entity userGeneratingEntity = FeatureFactory.in(world).makeCompletedFeature(0);
        world.usersPerHour.set(userGeneratingEntity.id, 1);
        world.vulnerability.set(userGeneratingEntity.id, 10);
    }
}
