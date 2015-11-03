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
import edu.bsu.cybersec.core.SystemPriority;
import playn.core.Clock;
import pythagoras.f.IDimension;
import tripleplay.entity.Entity;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

import java.util.Map;

public class FeatureGroup extends InteractionAreaGroup {

    private final Group featureList = new Group(AxisLayout.vertical().offStretch());
    private final Map<Integer, FeatureLabel> map = Maps.newHashMap();
    private final GameWorld world;

    public FeatureGroup(final GameWorld gameWorld) {
        super(AxisLayout.vertical());
        this.world = gameWorld;

        new tripleplay.entity.System(gameWorld, SystemPriority.UI_LEVEL.value) {

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(gameWorld.featureNumber);
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    final int id = entities.get(i);
                    final int featureId = gameWorld.featureNumber.get(id);
                    if (!map.containsKey(featureId)) {
                        FeatureLabel element = new FeatureLabel(id);
                        featureList.add(element);
                        map.put(featureId, element);
                    } else {
                        map.get(featureId).updateText(id);
                    }
                }
            }
        };
    }

    @Override
    protected void wasParented(Container<?> parent) {
        super.wasParented(parent);
        if (isThisTheFirstParenting()) {
            final IDimension parentSize = parent.size();
            Scroller scroller = new Scroller(featureList)
                    .setBehavior(Scroller.Behavior.VERTICAL)
                    .setConstraint(Constraints.fixedSize(parentSize.width(), parentSize.height()));
            add(scroller);
        }
    }

    private boolean isThisTheFirstParenting() {
        return childCount() == 0;
    }

    private final class FeatureLabel extends Label {

        private boolean hasCompletedText = false;

        FeatureLabel(int entityId) {
            super();
            addStyles(Style.HALIGN.left);
            updateText(entityId);
        }

        public void updateText(int entityId) {
            Entity entity = world.entity(entityId);
            final int number = world.featureNumber.get(entityId);
            final String numberAndName = number + ": " + world.name.get(entityId).fullName;
            if (entity.has(world.developmentProgress)) {
                final int progress = (int) (world.developmentProgress.get(entityId) / world.goal.get(entityId) * 100);
                text.update(numberAndName + " [" + progress + "%]");
            } else if (!hasCompletedText) {
                text.update(numberAndName);
                hasCompletedText = true;
            }
        }
    }
}
