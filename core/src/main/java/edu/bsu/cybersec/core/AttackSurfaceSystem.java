package edu.bsu.cybersec.core;

import com.google.common.collect.Maps;
import playn.core.Clock;
import tripleplay.entity.Entity;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class AttackSurfaceSystem extends tripleplay.entity.System {
    private final GameWorld world;

    private final Map<Integer, Float> map = Maps.newHashMap();

    public AttackSurfaceSystem(GameWorld world) {
        super(world, 0);
        this.world = checkNotNull(world);
    }


    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(world.owner)
                && entity.has(world.exposure);
    }

    @Override
    protected void update(Clock clock, Entities entities) {
        super.update(clock, entities);
        readExposureValuesIntoMap(entities);
        copyMapValuesToWorld();
        map.clear();
    }

    private void readExposureValuesIntoMap(Entities entities) {
        for (int i = 0, limit = entities.size(); i < limit; i++) {
            int id = entities.get(i);
            int owner = world.owner.get(id);
            float increment = world.exposure.get(id);
            if (map.containsKey(owner)) {
                map.put(owner, map.get(owner) + increment);
            } else {
                map.put(owner, increment);
            }
        }
    }

    private void copyMapValuesToWorld() {
        for (Integer owner : map.keySet()) {
            world.attackSurface.set(owner, map.get(owner));
        }
    }
}
