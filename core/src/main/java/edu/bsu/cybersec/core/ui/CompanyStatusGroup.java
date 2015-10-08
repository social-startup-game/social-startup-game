package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SystemPriority;
import playn.core.Clock;
import tripleplay.entity.Entity;
import tripleplay.ui.Label;
import tripleplay.ui.layout.AxisLayout;

import static com.google.common.base.Preconditions.checkNotNull;

public final class CompanyStatusGroup extends InteractionAreaGroup {

    private static final String TEXT = "Users per hour: ";
    private final GameWorld gameWorld;
    private final Label usersPerSecondLabel = new Label(TEXT);

    public CompanyStatusGroup(GameWorld world) {
        super(AxisLayout.vertical());
        this.gameWorld = checkNotNull(world);
        new UsersPerSecondLabelSystem();
        layoutUI();
    }

    private void layoutUI() {
        add(usersPerSecondLabel);
    }

    private final class UsersPerSecondLabelSystem extends tripleplay.entity.System {

        private UsersPerSecondLabelSystem() {
            super(gameWorld, SystemPriority.UI_LEVEL.value);
        }

        @Override
        protected boolean isInterested(Entity entity) {
            return entity.has(gameWorld.usersPerHour);
        }

        @Override
        protected void update(Clock clock, Entities entities) {
            float sum = 0;
            for (int i = 0, limit = entities.size(); i < limit; i++) {
                sum += gameWorld.usersPerHour.get(entities.get(i));
            }
            usersPerSecondLabel.text.update(TEXT + sum);
        }
    }

}