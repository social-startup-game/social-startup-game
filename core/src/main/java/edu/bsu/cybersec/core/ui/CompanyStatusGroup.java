package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SystemPriority;
import playn.core.Clock;
import react.ValueView;
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
        add(new EstimatedExposureLabel(gameWorld));
    }

    private final class UsersPerSecondLabelSystem extends tripleplay.entity.System {

        private UsersPerSecondLabelSystem() {
            super(gameWorld, SystemPriority.UI_LEVEL.value);
        }

        @Override
        protected boolean isInterested(Entity entity) {
            return ((GameWorld.Systematized) gameWorld).userGenerationSystem.isActiveUserGeneratingEntity(entity);
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

    private static final class EstimatedExposureLabel extends Label {
        private static final String TEXT_TEMPLATE = "Estimated exposure: ";

        private EstimatedExposureLabel(GameWorld gameWorld) {
            super(TEXT_TEMPLATE + gameWorld.exposure.get());
            gameWorld.exposure.connect(new ValueView.Listener<Float>() {
                @Override
                public void onChange(Float value, Float oldValue) {
                    text.update(TEXT_TEMPLATE + value);
                }
            });
        }
    }

}