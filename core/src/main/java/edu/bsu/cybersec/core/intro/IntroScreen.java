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

package edu.bsu.cybersec.core.intro;

import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.ui.FontCache;
import edu.bsu.cybersec.core.ui.GameScreen;
import edu.bsu.cybersec.core.ui.SimGameStyle;
import playn.core.Game;
import playn.core.Mouse;
import playn.core.Touch;
import react.Connection;
import react.Slot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import java.util.Iterator;
import java.util.List;

public class IntroScreen extends ScreenStack.UIScreen {
    private final ScreenStack screenStack;
    private final Iterator<IntroSlideInformation> iterator;
    private final List<Connection> connections = Lists.newArrayList();

    public IntroScreen(ScreenStack screenStack, Iterator<IntroSlideInformation> iterator) {
        super(SimGame.game.plat);
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
        connections.add(game().plat.input().touchEvents.connect(new Slot<Touch.Event[]>() {
            @Override
            public void onEmit(Touch.Event[] events) {
                for (Touch.Event e : events) {
                    if (e.kind.isEnd) {
                        advance();
                    }
                }
            }
        }));
    }

    @Override
    public void wasShown() {
        super.wasShown();
        createUI();
    }

    private void createUI() {
        Root root = iface.createRoot(AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics()), layer)
                .setSize(size());
        IntroSlideInformation info = iterator.next();
        Group display = createDisplayGroup(info)
                .setConstraint(Constraints.fixedSize(SimGame.game.contentBounds.width(), SimGame.game.contentBounds.height()));
        root.add(display);
        root.addStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE)));
    }

    private Group createDisplayGroup(IntroSlideInformation info) {
        return new SizableGroup(AxisLayout.vertical(), SimGame.game.contentBounds.width(), SimGame.game.contentBounds.height())
                .add(new Label(info.text, Icons.image(info.background))
                        .addStyles(Style.TEXT_WRAP.on,
                                Style.FONT.is(FontCache.instance().REGULAR.derive(25)),
                                Style.ICON_POS.below,
                                Style.ICON_GAP.is(percentOfScreenHeight(0.04f))));
    }

    private int percentOfScreenHeight(float percent) {
        return (int) (SimGame.game.plat.graphics().viewSize.height() * percent);
    }

    private void advance() {
        for (Connection c : connections) {
            c.close();
        }
        if (iterator.hasNext()) {
            screenStack.replace(new IntroScreen(screenStack, iterator), screenStack.slide());
        } else {
            screenStack.replace(new GameScreen(screenStack), screenStack.slide());
        }
    }

    @Override
    public Game game() {
        return SimGame.game;
    }


}
