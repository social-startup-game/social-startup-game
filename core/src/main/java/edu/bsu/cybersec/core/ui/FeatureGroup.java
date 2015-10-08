package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SystemPriority;
import playn.core.Clock;
import tripleplay.entity.Entity;
import tripleplay.ui.Label;
import tripleplay.ui.layout.AxisLayout;

public class FeatureGroup extends InteractionAreaGroup {
    public FeatureGroup(final GameWorld gameWorld) {
        super(AxisLayout.vertical());
        new tripleplay.entity.System(gameWorld, SystemPriority.UI_LEVEL.value) {

            @Override
            protected boolean isInterested(Entity entity) {
                return entity.has(gameWorld.developmentProgress);
            }

            @Override
            protected void update(Clock clock, Entities entities) {
                super.update(clock, entities);
                removeAll();
                for (int i = 0, limit = entities.size(); i < limit; i++) {
                    final int id = entities.get(i);
                    final int progress = (int) (gameWorld.developmentProgress.get(id) / gameWorld.goal.get(id));
                    add(new Label("Progress : " + progress * 100 + " %"));
                }
            }
        };
    }
}
