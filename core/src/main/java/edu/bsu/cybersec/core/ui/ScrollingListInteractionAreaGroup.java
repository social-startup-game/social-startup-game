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
import tripleplay.entity.Component;
import tripleplay.entity.Entity;
import tripleplay.entity.System;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ScrollingListInteractionAreaGroup extends InteractionAreaGroup {

    private final Map<Integer, ScrollingListItem> map = Maps.newHashMap();
    private Group content = new Group(AxisLayout.vertical().offStretch());
    protected final GameWorld world;

    public ScrollingListInteractionAreaGroup(final GameWorld world, final Component.IScalar countingComponent) {
        super(AxisLayout.vertical());
        this.world = checkNotNull(world);
        new tripleplay.entity.System(world, SystemPriority.UI_LEVEL.value) {

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(countingComponent);
            }

            @Override
            protected void update(Clock clock, System.Entities entities) {
                super.update(clock, entities);
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    final int id = entities.get(i);
                    final int featureId = countingComponent.get(id);
                    if (!map.containsKey(featureId)) {
                        ScrollingListItem element = createLabel(id);
                        content.add(element);
                        map.put(featureId, element);
                    } else {
                        map.get(featureId).update(id);
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
            Scroller scroller = new Scroller(content)
                    .setBehavior(Scroller.Behavior.VERTICAL)
                    .setConstraint(Constraints.fixedSize(parentSize.width(), parentSize.height()));
            add(scroller);
        }
    }

    private boolean isThisTheFirstParenting() {
        return childCount() == 0;
    }

    protected abstract ScrollingListItem createLabel(int entityId);

    protected abstract class ScrollingListItem extends Label {
        protected ScrollingListItem(int entityId) {
            addStyles(Style.HALIGN.left);
            update(entityId);
        }

        protected void update(int entityId) {
        }
    }
}
