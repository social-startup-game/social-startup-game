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

import com.google.common.collect.ImmutableList;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.intro.IntroScreen;
import edu.bsu.cybersec.core.intro.IntroSlideInformation;
import playn.core.Font;
import playn.core.Game;
import playn.core.Tile;
import playn.scene.Pointer;
import react.Slot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

import static com.google.common.base.Preconditions.checkNotNull;

public class StartingScreen extends ScreenStack.UIScreen {
    private final ScreenStack screenStack;
    private final ImmutableList<IntroSlideInformation> narrativeInfoList = ImmutableList.of(
            new IntroSlideInformation("Social Jam is an up and coming social media service, and they have hired you as their chief security advisor.",
                    SimGame.game.assets.getTile(GameAssets.ImageKey.NARRATIVE_BACKGROUND_1)),
            new IntroSlideInformation("You were hired to manage a team of developers and protect the company from hackers! ",
                    SimGame.game.assets.getTile(GameAssets.ImageKey.NARRATIVE_BACKGROUND_2)),
            new IntroSlideInformation("You have three employees. Assign them to develop features or maintain your current system.",
                    SimGame.game.assets.getTile(GameAssets.ImageKey.NARRATIVE_BACKGROUND_3)),
            new IntroSlideInformation("You have a job review in two weeks. Do you have what it takes?",
                    SimGame.game.assets.getTile(GameAssets.ImageKey.NARRATIVE_BACKGROUND_4)));

    public StartingScreen(ScreenStack screenStack) {
        this.screenStack = checkNotNull(screenStack);
        new Pointer(game().plat, layer, true);
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics()))
                .setSize(size());
        Tile logo = SimGame.game.assets.getTile(GameAssets.ImageKey.LOGO);
        Icon iconLogo = Icons.image(logo.tile());
        final float buttonFontSize = percentOfViewHeight(0.04f);
        final Font font = FontCache.instance().REGULAR.derive(buttonFontSize);
        root.add(new Label(iconLogo))
                .add(new Button("Start the Game!")
                                .addStyles(Style.FONT.is(font))
                                .onClick(new Slot<Button>() {
                                    @Override
                                    public void onEmit(Button button) {
                                        screenStack.push(new IntroScreen(screenStack, narrativeInfoList.iterator()), screenStack.slide());
                                    }
                                }),
                        new Shim(0, buttonFontSize / 2),
                        new Button("Credits")
                                .addStyles(Style.FONT.is(font))
                                .onClick(new Slot<Button>() {
                                    @Override
                                    public void onEmit(Button button) {
                                        screenStack.push(new CreditScreen(screenStack), screenStack.slide());
                                    }
                                }));
        root.setStyles(Style.BACKGROUND.is(Background.solid(Palette.START_BACKGROUND)));
        return root;
    }

    private float percentOfViewHeight(float v) {
        return SimGame.game.plat.graphics().viewSize.height() * v;
    }

    @Override
    public void wasShown() {
        super.wasShown();
        Jukebox.instance().loop(MusicCache.instance().INTRO_THEME);
    }

    @Override
    public Game game() {
        return SimGame.game;
    }

}
