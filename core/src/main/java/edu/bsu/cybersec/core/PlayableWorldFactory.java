package edu.bsu.cybersec.core;

import tripleplay.entity.Entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class PlayableWorldFactory {

    private static final float SECONDS_PER_HOUR = 60 * 60;
    private static final String IMAGE_PREFIX = "images/";
    private static final String[] NAMES = {"Esteban", "Nancy", "Jerry"};
    
    private final GameWorld.Systematized world = new GameWorld.Systematized();

    public GameWorld.Systematized createPlayableGameWorld() {
        initializeWorld();
        return world;
    }

    private void initializeWorld() {
        world.gameTimeSystem.setScale(SECONDS_PER_HOUR);
        makeExistingFeature();
        makeFeatureInDevelopment();
        makeDevelopers(3);
    }

    private void makeDevelopers(int number) {
        checkArgument(number >= 0);
        for (int i = 0; i < number; i++) {
            final String name = NAMES[i];
            makeDeveloper(name);
        }
    }

    private Entity makeDeveloper(String name) {
        checkNotNull(name);
        Entity developer = world.create(true)
                .add(world.developmentSkill,
                        world.tasked,
                        world.maintenanceSkill,
                        world.name,
                        world.imagePath);
        world.tasked.set(developer.id, Task.IDLE);
        world.developmentSkill.set(developer.id, 5);
        world.maintenanceSkill.set(developer.id, 0.02f);
        world.name.set(developer.id, name);
        world.imagePath.set(developer.id, IMAGE_PREFIX + name + ".png");
        return developer;
    }

    private void makeExistingFeature() {
        Entity userGeneratingEntity = world.featureDevelopmentSystem.makeCompletedFeature(0);
        world.usersPerHour.set(userGeneratingEntity.id, 1);
        world.vulnerability.set(userGeneratingEntity.id, 10);
    }

    private void makeFeatureInDevelopment() {
        Entity e = world.featureDevelopmentSystem.makeFeatureInDevelopment(1);
        world.usersPerHour.set(e.id, 25);
        world.developmentProgress.set(e.id, 0);
        world.goal.set(e.id, 20);
        world.vulnerability.set(e.id, 10);
    }
}
