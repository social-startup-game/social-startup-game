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

package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.DecimalTruncator;
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
    private final Label usersPerHourLabel = new Label(TEXT);

    public CompanyStatusGroup(GameWorld world) {
        super(AxisLayout.vertical());
        this.gameWorld = checkNotNull(world);
        new UsersPerHourLabelSystem();
        layoutUI();
    }

    private void layoutUI() {
        add(usersPerHourLabel);
        add(new EstimatedExposureLabel(gameWorld));
    }

    private final class UsersPerHourLabelSystem extends tripleplay.entity.System {

        private DecimalTruncator truncator = new DecimalTruncator(0);

        private UsersPerHourLabelSystem() {
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
            String truncatedUsersPerHour = truncator.makeTruncatedString(sum);
            usersPerHourLabel.text.update(TEXT + truncatedUsersPerHour);
        }
    }

    private static final class EstimatedExposureLabel extends Label {
        private static final String TEXT_TEMPLATE = "Estimated exposure: ";

        private EstimatedExposureLabel(GameWorld gameWorld) {
            super(TEXT_TEMPLATE + gameWorld.exposure.get());
            gameWorld.exposure.connect(new ValueView.Listener<Float>() {
                private final DecimalTruncator truncator = new DecimalTruncator(1);
                @Override
                public void onChange(Float value, Float oldValue) {
                    text.update(TEXT_TEMPLATE + truncator.makeTruncatedString(value) + "%");
                }
            });
        }
    }

}