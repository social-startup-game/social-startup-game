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

import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Graphics;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import react.Slot;
import tripleplay.ui.*;
import tripleplay.ui.bgs.RoundRectBackground;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.util.BoxPoint;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkState;

public class EventsGroup extends InteractionAreaGroup {
    private static final Graphics graphics = SimGame.game.plat.graphics();
    private static final Background CALLOUT_BACKGROUND = new RoundRectBackground(graphics,
            Colors.WHITE, percentOfScreenHeight(0.05f), Palette.DIALOG_BORDER, percentOfScreenHeight(0.01f));
    private static final float TEXTBOX_WIDTH_PERCENT = 0.85f;
    private static final float SPEAKER_WIDTH_PERCENT = 0.12f;

    private static float percentOfScreenHeight(float percent) {
        return graphics.viewSize.height() * percent;
    }

    private final GameWorld gameWorld;
    private final Label noEventsLabel = new Label("No current events.").addStyles(Style.COLOR.is(Palette.FOREGROUND));
    private final Image eventSpeakerImage = ImageCache.instance().ADMIN;
    private IDimension parentSize;
    private Runnable onParented;

    public EventsGroup(GameWorld gameWorld) {
        super(AxisLayout.horizontal().offStretch());
        add(noEventsLabel.setConstraint(AbsoluteLayout.uniform(BoxPoint.CENTER)));
        this.gameWorld = gameWorld;
        gameWorld.onNarrativeEvent.connect(new Slot<NarrativeEvent>() {
            @Override
            public void onEmit(NarrativeEvent event) {
                post(event);
            }
        });
    }

    @Override
    protected void wasParented(Container<?> parent) {
        super.wasParented(parent);
        parentSize = new Dimension(parent.size());
        checkState(!parent.size().equals(new Dimension(0, 0)),
                "I expect parent to have non-zero size so I can lay out my content.");
        if (onParented != null) {
            onParented.run();
            onParented = null;
        }
    }

    private void post(final NarrativeEvent narrativeEvent) {
        needsAttention.update(true);
        ((GameWorld.Systematized) gameWorld).gameTimeSystem.setEnabled(false);
        Runnable constructEventUI = new Runnable() {
            @Override
            public void run() {
                removeAll();
                setConstraint(Constraints.fixedSize(parentSize.width(), parentSize.height()));
                Group textBox = makeTextBox(narrativeEvent);
                add(textBox,
                        new Group(AxisLayout.vertical())
                                .add(new Shim(0, 0).setConstraint(AxisLayout.stretched()),
                                        new Label(makeSpeakerIcon()),
                                        new Shim(0, percentOfScreenHeight(0.01f)))
                                .setConstraint(AxisLayout.stretched()));
            }

            private Icon makeSpeakerIcon() {
                float imageWidth = eventSpeakerImage.width();
                float proportion = SPEAKER_WIDTH_PERCENT;
                float desiredWidth = parentSize.width() * proportion;
                float scale = desiredWidth / imageWidth;
                return Icons.scaled(Icons.image(eventSpeakerImage), scale);
            }
        };
        if (parentSize != null) {
            constructEventUI.run();
        } else {
            checkState(onParented == null, "There is already a UI construction queued");
            onParented = constructEventUI;
        }
    }

    private Group makeTextBox(NarrativeEvent narrativeEvent) {
        final float width = parentSize.width() * TEXTBOX_WIDTH_PERCENT;
        final float shimSize = percentOfScreenHeight(0.01f);
        final float inset = percentOfScreenHeight(0.02f);
        Label label = new Label(narrativeEvent.text())
                .addStyles(Style.TEXT_WRAP.on,
                        Style.COLOR.is(Colors.BLACK),
                        Style.BACKGROUND.is(Background.blank().inset(inset, inset)));
        Group buttonGroup = makeButtonGroup(narrativeEvent);
        Group content = new Group(AxisLayout.vertical().offStretch())
                .add(new Shim(0, shimSize),
                        label,
                        buttonGroup,
                        new Shim(0, shimSize));
        Scroller scroller = new Scroller(content).setBehavior(Scroller.Behavior.VERTICAL)
                .setConstraint(Constraints.fixedSize(width, parentSize.height()));
        return new SizableGroup(AxisLayout.vertical().offStretch(), width, parentSize.height())
                .add(scroller)
                .addStyles(Style.BACKGROUND.is(CALLOUT_BACKGROUND));
    }

    private Group makeButtonGroup(NarrativeEvent narrativeEvent) {
        Group buttonGroup = new Group(AxisLayout.horizontal());
        for (final NarrativeEvent.Option option : narrativeEvent.options()) {
            buttonGroup.add(new Button(option.text()).onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button button) {
                    option.onSelected();
                    ((GameWorld.Systematized) gameWorld).gameTimeSystem.setEnabled(true);
                    removeAll();
                    needsAttention.update(false);
                    add(noEventsLabel);
                }
            }));
        }
        return buttonGroup;
    }
}