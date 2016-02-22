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

package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.*;
import playn.core.Clock;
import react.Slot;
import react.ValueView;
import tripleplay.entity.Entity;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.*;

public final class CompanyStatusGroup extends InteractionAreaGroup {

    private static final float HGAP_BETWEEN_COLUMNS = SimGame.game.bounds.percentOfHeight(0.02f);
    private static final float TABLE_INSETS = SimGame.game.bounds.percentOfHeight(0.005f);

    private final GameWorld gameWorld;
    private final Label usersPerHourLabel = new Label();

    public CompanyStatusGroup(GameWorld world) {
        super(AxisLayout.vertical().offStretch());
        this.gameWorld = checkNotNull(world);
        new UsersPerHourLabelSystem();
        layoutUI();
    }

    private void layoutUI() {
        Group summaryTable = new Group(new TableLayout(
                new ExposedColumn(Style.HAlign.LEFT, true, 1, 0),
                new ExposedColumn(Style.HAlign.RIGHT, false, 1, 0),
                new ExposedColumn(Style.HAlign.CENTER, false, 1, HGAP_BETWEEN_COLUMNS),
                new ExposedColumn(Style.HAlign.LEFT, true, 1, 0),
                new ExposedColumn(Style.HAlign.RIGHT, false, 1, 0)))
                .addStyles(Style.BACKGROUND.is(Background.blank().inset(TABLE_INSETS, TABLE_INSETS)));
        summaryTable.add(
                new Label("Users per hour").addStyles(Style.HALIGN.left),
                usersPerHourLabel,
                new Shim(0, 0),
                new Label("Days remaining").addStyles(Style.HALIGN.left),
                new DaysRemainingLabel(gameWorld),
                new Label("Estimated exposure").addStyles(Style.HALIGN.left),
                new EstimatedExposureLabel(gameWorld),
                new Shim(0, 0),
                new Label("Progress toward goal").addStyles(Style.HALIGN.left),
                new ProgressTowardGoalLabel(gameWorld));
        add(summaryTable,
                new Group(AxisLayout.horizontal().offStretch())
                        .add(unitShim(),
                                new CompanyStatusGraph(gameWorld).setConstraint(AxisLayout.stretched(14f)),
                                unitShim())
                        .setConstraint(AxisLayout.stretched()));
    }

    private Shim unitShim() {
        return new Shim(0, 0).setConstraint(AxisLayout.stretched(1));
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
            float sum = sumOfUsersPerHour(entities);
            String truncatedUsersPerHour = truncator.makeTruncatedString(sum);
            usersPerHourLabel.text.update(truncatedUsersPerHour);
        }

        private float sumOfUsersPerHour(Entities entities) {
            float sum = 0;
            for (int i = 0, limit = entities.size(); i < limit; i++) {
                sum += gameWorld.usersPerHour.get(entities.get(i));
            }
            return sum;
        }
    }

    private static final class EstimatedExposureLabel extends Label {

        private EstimatedExposureLabel(GameWorld gameWorld) {
            super(String.valueOf(gameWorld.exposure.get()));
            gameWorld.exposure.connect(new ValueView.Listener<Float>() {
                private final DecimalTruncator truncator = new DecimalTruncator(1);

                @Override
                public void onChange(Float value, Float oldValue) {
                    text.update(truncator.makeTruncatedString(value * 100) + "%");
                }
            });
        }
    }

    private static final class DaysRemainingLabel extends Label {

        private DaysRemainingLabel(final GameWorld world) {
            world.gameTime.connect(new ValueView.Listener<GameTime>() {
                @Override
                public void onChange(GameTime value, GameTime oldValue) {
                    final int secondsLeft = world.gameEnd.get() - value.now;
                    final int daysLeft = secondsLeft / ClockUtils.SECONDS_PER_DAY + 1;
                    text.update(String.valueOf(daysLeft));
                }
            });
        }
    }

    private static final class ExposedColumn extends TableLayout.Column {
        protected ExposedColumn(Style.HAlign halign, boolean stretch, float weight, float minWidth) {
            super(halign, stretch, weight, minWidth);
        }
    }

    private static final class ProgressTowardGoalLabel extends Label {
        private static final int PASS_COLOR = GameColors.WHITE;
        private static final int FAIL_COLOR = Colors.RED;
        private boolean metGoal = false;

        private ProgressTowardGoalLabel(final GameWorld world) {
            addStyles(Style.COLOR.is(FAIL_COLOR));
            world.users.connect(new Slot<Float>() {

                final DecimalTruncator truncator = new DecimalTruncator(1);

                @Override
                public void onEmit(Float users) {
                    float progress = users / world.company.get().goal.minimum;
                    text.update(truncator.makeTruncatedString(progress * 100) + "%");
                    if (progress >= 1 && !metGoal) {
                        addStyles(Style.COLOR.is(PASS_COLOR));
                        metGoal = true;
                    } else if (progress < 1 && metGoal) {
                        addStyles(Style.COLOR.is(FAIL_COLOR));
                        metGoal = false;
                    }
                }
            });
        }
    }

}