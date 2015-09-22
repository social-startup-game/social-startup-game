package edu.bsu.cybersec.core;

import com.google.common.collect.ImmutableMap;
import tripleplay.entity.Entity;

import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

public class PlayableWorldFactory {

    private static final float SECONDS_PER_HOUR = 60 * 60;

    private final String IMAGE_PREFIX = "images/";
    private final Map<String, String> nameImageMap = ImmutableMap.of(
            "Esteban", IMAGE_PREFIX + "employee_1.png",
            "Nancy", IMAGE_PREFIX + "employee_2.png",
            "Jerry", IMAGE_PREFIX + "employee_3.png");
    private final GameWorld.Systematized world = new GameWorld.Systematized();
    public Entity company;
    private Entity[] developers;

    public GameWorld.Systematized createPlayableGameWorld() {
        initializeWorld();
        return world;
    }

    private void initializeWorld() {
        makeCompany();
        makeClock();
        makeExistingFeature();
        makeFeatureInDevelopment();
        makeDevelopers(3);
    }

    private void makeCompany() {
        company = world.create(true)
                .add(world.type,
                        world.users,
                        world.attackSurface);
        world.type.set(company.id, Type.COMPANY);
        world.users.set(company.id, 0);
        world.attackSurface.set(company.id, 0);
    }

    private void makeClock() {
        Entity clock = world.create(true).add(world.type, world.gameTime, world.gameTimeScale);
        final int id = clock.id;
        world.type.set(id, Type.CLOCK);
        world.gameTime.set(id, 0);
        world.gameTimeScale.set(id, SECONDS_PER_HOUR);
    }

    private Entity[] makeDevelopers(int number) {
        checkArgument(number >= 0);
        checkState(developers == null, "Expected developers not yet to be initialized");
        developers = new Entity[number];
        Iterator<String> nameIterator = nameImageMap.keySet().iterator();
        for (int i = 0; i < number; i++) {
            final String name = nameIterator.next();
            developers[i] = makeDeveloper(name);
        }
        return developers;
    }

    private Entity makeDeveloper(String name) {
        checkNotNull(name);
        Entity developer = world.create(true)
                .add(world.developmentSkill,
                        world.tasked,
                        world.companyId,
                        world.maintenanceSkill,
                        world.name,
                        world.imagePath);
        world.tasked.set(developer.id, Task.IDLE);
        world.developmentSkill.set(developer.id, 5);
        world.maintenanceSkill.set(developer.id, 0.02f);
        world.companyId.set(developer.id, company.id);
        world.name.set(developer.id, name);
        world.imagePath.set(developer.id, nameImageMap.get(name));
        return developer;
    }

    private void makeExistingFeature() {
        Entity userGeneratingEntity = world.create(true).add(world.usersPerSecond, world.companyId, world.exposure);
        world.usersPerSecond.set(userGeneratingEntity.id, 1);
        world.companyId.set(userGeneratingEntity.id, company.id);
        world.attackSurface.set(company.id, 0.05f);
    }

    private void makeFeatureInDevelopment() {
        Entity feature = world.create(false)
                .add(world.usersPerSecond, world.companyId, world.exposure);
        world.usersPerSecond.set(feature.id, 25);
        world.companyId.set(feature.id, company.id);
        world.exposure.set(feature.id, 0.20f);

        Entity development = world.create(true)
                .add(world.progress, world.goal, world.featureId);
        world.progress.set(development.id, 0);
        world.goal.set(development.id, 20);
        world.featureId.set(development.id, feature.id);
    }
}
