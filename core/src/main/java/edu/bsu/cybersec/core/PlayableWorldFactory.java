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

import edu.bsu.cybersec.core.narrative.DefaultNarrativeScript;
import playn.core.Image;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkNotNull;

public class PlayableWorldFactory {

    /**
     * The factor for scaling sprite images.
     * This number was empirically determined based on the production size of the character images.
     */
    private static final float CHARACTER_SPRITE_SCALE = SimGame.game.bounds.width() / 510f;
    private static final int DAYS_UNTIL_GAME_END = 10;

    private final GameWorld.Systematized world = new GameWorld.Systematized();
    private final GameConfig config;
    private final Company company;

    public PlayableWorldFactory(GameConfig config, Company company) {
        this.config = checkNotNull(config);
        this.company = checkNotNull(company);
    }

    public GameWorld.Systematized createPlayableGameWorld() {
        initializeWorld();
        world.company.update(company);
        return world;
    }

    private void initializeWorld() {
        world.featureGenerationSystem.nextFeatureNumber(1);
        world.featureDevelopmentSystem.inefficiencyFactor.update(1 / 10f);
        world.exposure.update(0.10f);
        world.users.update(1000f);
        makeExistingFeature();
        makeDevelopers();
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

    private void makeDevelopers() {
        for (int i = 0; i < company.employees.size(); i++) {
            Entity e = makeDeveloper(i, company.employees.get(i));
            world.workers.add(e);
        }
    }

    private Entity makeDeveloper(final int number, final Employee employee) {
        Entity developer = world.create(true)
                .add(world.employeeNumber,
                        world.developmentSkill,
                        world.task,
                        world.maintenanceSkill,
                        world.profile,
                        world.sprite,
                        world.position);
        world.employeeNumber.set(developer.id, number);
        world.task.set(developer.id, world.maintenanceTaskId);
        world.developmentSkill.set(developer.id, 5);
        world.maintenanceSkill.set(developer.id, 5);
        world.profile.set(developer.id, employee.profile);
        world.sprite.set(developer.id, makeImageLayer(employee.image));
        return developer;
    }

    private Layer makeImageLayer(Image image) {
        ImageLayer layer = new ImageLayer(image);
        layer.setOrigin(image.width() / 2, 0);
        layer.setScale(CHARACTER_SPRITE_SCALE);
        return layer;
    }

    private void makeExistingFeature() {
        Entity userGeneratingEntity = FeatureFactory.in(world).makeCompletedFeature(0);
        world.usersPerHour.set(userGeneratingEntity.id, 25);
        world.vulnerability.set(userGeneratingEntity.id, 10);
    }
}
