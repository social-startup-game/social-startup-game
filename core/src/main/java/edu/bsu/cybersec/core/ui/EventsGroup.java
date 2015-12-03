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
import pythagoras.f.IDimension;
import react.Slot;
import tripleplay.ui.*;
import tripleplay.ui.bgs.RoundRectBackground;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;

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
    private final Image eventSpeakerImage = SimGame.game.assets.getImage(GameAssets.ImageKey.ADMIN);
    private NarrativeEvent currentEvent;
    private Scroller scroller;
    private Group content;

    public EventsGroup(final GameWorld gameWorld) {
        super(AxisLayout.horizontal().offStretch());
        this.gameWorld = gameWorld;
        if (SimGame.game.config.useNarrativeEvents()) {
            gameWorld.onNarrativeEvent.connect(new Slot<NarrativeEvent>() {
                @Override
                public void onEmit(NarrativeEvent event) {
                    needsAttention.update(true);
                    ((GameWorld.Systematized) gameWorld).gameTimeSystem.setEnabled(false);
                    currentEvent = checkNotNull(event);
                    if (content != null) {
                        updateContent();
                    }
                    invalidate();
                }
            });
        }
    }

    @Override
    protected void wasParented(Container<?> parent) {
        super.wasParented(parent);
        if (childCount() == 0) {
            layoutUI();
        }
    }

    private void layoutUI() {
        final IDimension parentSize = _parent.size();
        setConstraint(Constraints.fixedSize(parentSize.width(), parentSize.height()));
        Group textBox = makeTextBox(parentSize);
        add(textBox,
                new Group(AxisLayout.vertical())
                        .add(new Shim(0, 0).setConstraint(AxisLayout.stretched()),
                                new Label(makeSpeakerIcon(parentSize)),
                                new Shim(0, percentOfScreenHeight(0.01f)))
                        .setConstraint(AxisLayout.stretched()));
    }

    private Group makeTextBox(IDimension parentSize) {
        final float width = parentSize.width() * TEXTBOX_WIDTH_PERCENT;
        content = new Group(AxisLayout.vertical().offStretch());
        updateContent();
        scroller = new Scroller(content).setBehavior(Scroller.Behavior.VERTICAL)
                .setConstraint(Constraints.fixedSize(width, parentSize.height()));
        return new SizableGroup(AxisLayout.vertical().offStretch(), width, parentSize.height())
                .add(scroller)
                .addStyles(Style.BACKGROUND.is(CALLOUT_BACKGROUND));
    }

    private void updateContent() {
        content.removeAll();
        if (currentEvent == null) {
            content.add(makeSpeakerTextLabel("I don't need your attention right now. Good luck!"));
        } else {
            final float shimSize = percentOfScreenHeight(0.01f);
            Label speakerText = makeSpeakerTextLabel(currentEvent.text());
            Group buttonGroup = makeButtonGroup();
            content.add(new Shim(0, shimSize),
                    speakerText,
                    buttonGroup,
                    new Shim(0, shimSize));
        }
    }

    private Label makeSpeakerTextLabel(String message) {
        final float inset = percentOfScreenHeight(0.02f);
        return new Label(message)
                .addStyles(Style.TEXT_WRAP.on,
                        Style.COLOR.is(Colors.BLACK),
                        Style.BACKGROUND.is(Background.blank().inset(inset, inset)));
    }

    private Group makeButtonGroup() {
        Group buttonGroup = new Group(AxisLayout.horizontal());
        for (final NarrativeEvent.Option option : currentEvent.options()) {
            buttonGroup.add(new Button(option.text()).onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button button) {
                    currentEvent = null;
                    option.onSelected();
                    ((GameWorld.Systematized) gameWorld).gameTimeSystem.setEnabled(true);
                    needsAttention.update(false);
                    updateContent();
                    invalidate();
                }
            }));
        }
        return buttonGroup;
    }

    private Icon makeSpeakerIcon(IDimension parentSize) {
        float imageWidth = eventSpeakerImage.width();
        float proportion = SPEAKER_WIDTH_PERCENT;
        float desiredWidth = parentSize.width() * proportion;
        float scale = desiredWidth / imageWidth;
        return Icons.scaled(Icons.image(eventSpeakerImage), scale);
    }

    @Override
    protected void validate() {
        final IDimension parentSize = _parent.size();
        final float textBoxWidth = parentSize.width() * TEXTBOX_WIDTH_PERCENT;
        scroller.setConstraint(Constraints.fixedSize(textBoxWidth, parentSize.height()));
        scroller.content.setConstraint(Constraints.fixedSize(textBoxWidth, parentSize.height()));
        super.validate();
    }
}