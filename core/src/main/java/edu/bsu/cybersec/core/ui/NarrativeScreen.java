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

import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Game;
import playn.core.Mouse;
import pythagoras.f.IRectangle;
import pythagoras.f.Rectangle;
import react.Connection;
import react.Slot;
import react.ValueView;
import tripleplay.game.ScreenStack;
import tripleplay.gesture.Gesture;
import tripleplay.gesture.GestureDirector;
import tripleplay.gesture.Swipe;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;
import tripleplay.util.Timer;

import java.util.Iterator;
import java.util.List;

public class NarrativeScreen extends ScreenStack.UIScreen {
    private final ScreenStack screenStack;
    private final Iterator<NarrativeSlideInformation> iterator;
    private final List<Connection> connections = Lists.newArrayList();
    private final Gesture swipe;
    private final GestureDirector director;

    public NarrativeScreen(ScreenStack screenStack, Iterator<NarrativeSlideInformation> iterator) {
        this.screenStack = screenStack;
        this.iterator = iterator;
        connections.add(game().plat.input().mouseEvents.connect(new Slot<Mouse.Event>() {
            @Override
            public void onEmit(Mouse.Event event) {
                if (isMouseButtonDownEvent(event)) {
                    advance();
                }
            }

            private boolean isMouseButtonDownEvent(Mouse.Event event) {
                if (!(event instanceof Mouse.ButtonEvent)) {
                    return false;
                } else {
                    Mouse.ButtonEvent e = (Mouse.ButtonEvent) event;
                    return e.down;
                }
            }
        }));

        director = new GestureDirector(game().plat, screenBounds(), new Timer());
        director.add(swipe = new Swipe(Gesture.Direction.LEFT).greedy(true));
        director.greedyGesture().connect(new ValueView.Listener<Gesture<?>>() {
            @Override
            public void onChange(Gesture<?> value, Gesture<?> oldValue) {
                game().plat.log().debug("Value: " + value);
                advance();
            }
        });
    }

    private IRectangle screenBounds() {
        return new Rectangle(0, 0, size().width(), size().height());
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics())).setSize(size());
        NarrativeSlideInformation info = iterator.next();
        root.add(new Label(info.text, Icons.image(info.background))
                .addStyles(Style.TEXT_WRAP.on,
                        Style.FONT.is(FontCache.instance().REGULAR.derive(25)),
                        Style.ICON_POS.below));
        root.addStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE)));
        root.add(createTransparentClickableArea());
        return root;
    }

    private Element createTransparentClickableArea() {
        return new ClickableLabel("")
                .onClick(new Slot<ClickableLabel>() {
                    @Override
                    public void onEmit(ClickableLabel event) {
                        if (iterator.hasNext()) {
                            screenStack.replace(new NarrativeScreen(screenStack, iterator), screenStack.slide());
                        } else {
                            screenStack.replace(new GameScreen(screenStack), screenStack.slide());
                        }
                    }
                });
    }

    private void advance() {
        for (Connection c : connections) {
            c.close();
        }
        director.remove(swipe);
        if (iterator.hasNext()) {
            screenStack.replace(new NarrativeScreen(screenStack, iterator), screenStack.slide());
        } else {
            screenStack.replace(new GameScreen(screenStack), screenStack.slide());
        }
    }

    @Override
    public Game game() {
        return SimGame.game;
    }


}
