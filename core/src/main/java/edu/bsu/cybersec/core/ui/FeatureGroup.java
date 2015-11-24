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

import com.google.common.collect.Maps;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.SystemPriority;
import playn.core.Clock;
import pythagoras.f.IDimension;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class FeatureGroup extends InteractionAreaGroup {

    private static class ExposedColumn extends TableLayout.Column {
        protected ExposedColumn(Style.HAlign halign, boolean stretch, float weight, float minWidth) {
            super(halign, stretch, weight, minWidth);
        }
    }

    private static final int AFTER_HEADER = 3;

    private final Map<Integer, ProgressLabel> map = Maps.newHashMap();
    private Group content = new Group(new TableLayout(
            new ExposedColumn(Style.HAlign.LEFT, true, 1f, 10f),
            new ExposedColumn(Style.HAlign.LEFT, true, 1f, 10f),
            new ExposedColumn(Style.HAlign.RIGHT, true, 1f, 10f))
    );
    private final GameWorld world;

    public FeatureGroup(final GameWorld gameWorld) {
        super(AxisLayout.vertical());
        configureInsets();
        content.add(new Label("FEATURE"),
                new Label("PROGRESS"),
                new Label("USERS PER HOUR"));
        this.world = checkNotNull(gameWorld);
        new tripleplay.entity.System(world, SystemPriority.UI_LEVEL.value) {
            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(gameWorld.featureNumber);
            }

            @Override
            protected void update(Clock clock, System.Entities entities) {
                super.update(clock, entities);
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    final int id = entities.get(i);
                    final int featureId = gameWorld.featureNumber.get(id);
                    if (!map.containsKey(featureId)) {
                        ProgressLabel progressLabel = new ProgressLabel(id);
                        map.put(featureId, progressLabel);
                        content.add(AFTER_HEADER, new Label(String.valueOf(gameWorld.usersPerHour.get(id))));
                        content.add(AFTER_HEADER, progressLabel);
                        content.add(AFTER_HEADER, new Label(gameWorld.name.get(id)).addStyles(Style.HALIGN.left));
                    } else {
                        map.get(featureId).update();
                    }
                }
            }
        };
    }

    private void configureInsets() {
        float horizontal = SimGame.game.plat.graphics().viewSize.height() * 0.02f;
        addStyles(Style.BACKGROUND.is(Background.blank().inset(horizontal, 0f)));
    }

    @Override
    protected void wasParented(Container<?> parent) {
        super.wasParented(parent);
        if (isThisTheFirstParenting()) {
            final IDimension parentSize = parent.size();
            Scroller scroller = new Scroller(content)
                    .setBehavior(Scroller.Behavior.VERTICAL)
                    .setConstraint(Constraints.fixedSize(parentSize.width(), parentSize.height()));
            add(scroller);
        }
    }

    private boolean isThisTheFirstParenting() {
        return childCount() == 0;
    }


    private final class ProgressLabel extends Label {

        private final int entityId;

        ProgressLabel(int entityId) {
            this.entityId = entityId;
        }

        public void update() {
            Entity entity = world.entity(entityId);
            if (entity.has(world.developmentProgress)) {
                final int progress = (int) (world.developmentProgress.get(entityId) / world.goal.get(entityId) * 100);
                text.update(progress + "%");
            } else {
                text.update("100%");
            }
        }
    }
}
