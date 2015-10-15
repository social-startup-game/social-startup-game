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

import com.google.common.collect.ImmutableMap;
import edu.bsu.cybersec.core.ui.PreloadedImage;
import tripleplay.entity.Entity;

import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class PlayableWorldFactory {

    private static final float SECONDS_PER_HOUR = 60 * 60;
    private static final Map<Name, PreloadedImage> DEVELOPERS = ImmutableMap.of(
            Name.first("Esteban").andLast("Cortez"), PreloadedImage.ESTEBAN,
            Name.first("Nancy").andLast("Stevens"), PreloadedImage.NANCY,
            Name.first("Jerry").andLast("Chen"), PreloadedImage.JERRY);

    private final GameWorld.Systematized world = new GameWorld.Systematized();

    public GameWorld.Systematized createPlayableGameWorld() {
        initializeWorld();
        return world;
    }

    private void initializeWorld() {
        world.gameTimeSystem.setScale(SECONDS_PER_HOUR);
        world.featureGenerationSystem.nextFeatureNumber(1);
        makeExistingFeature();
        makeDevelopers(3);
    }

    private void makeDevelopers(int number) {
        checkArgument(number >= 0);
        Iterator<Name> names = DEVELOPERS.keySet().iterator();
        for (int i = 0; i < number; i++) {
            makeDeveloper(i, names.next());
        }
    }

    private Entity makeDeveloper(final int number, final Name name) {
        Entity developer = world.create(true)
                .add(world.employeeNumber,
                        world.developmentSkill,
                        world.tasked,
                        world.maintenanceSkill,
                        world.name,
                        world.image);
        world.employeeNumber.set(developer.id, number);
        world.tasked.set(developer.id, Task.IDLE);
        world.developmentSkill.set(developer.id, 5);
        world.maintenanceSkill.set(developer.id, 5);
        world.name.set(developer.id, name);
        world.image.set(developer.id, DEVELOPERS.get(name).image);
        return developer;
    }

    private void makeExistingFeature() {
        Entity userGeneratingEntity = FeatureFactory.in(world).makeCompletedFeature(0);
        world.usersPerHour.set(userGeneratingEntity.id, 1);
        world.vulnerability.set(userGeneratingEntity.id, 10);
    }
}
