package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import tripleplay.entity.Entity;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.layout.AxisLayout;

import static com.google.common.base.Preconditions.checkNotNull;

public final class CompanyStatusGroupSystem extends tripleplay.entity.System {

    private final GameWorld gameWorld;
    public final Group group;

    public CompanyStatusGroupSystem(GameWorld world) {
        super(world, 0);
        this.gameWorld = checkNotNull(world);
        group = new Group(AxisLayout.vertical())
                .add(new Label("This is the label"));
    }

    @Override
    protected boolean isInterested(Entity entity) {
        return entity.has(gameWorld.progress);
    }


}
