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
import playn.core.Image;
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
                    SimGame.game.assets.getTile(GameAssets.TileKey.NARRATIVE_BACKGROUND_1)),
            new IntroSlideInformation("You were hired to manage a team of developers and protect the company from hackers! ",
                    SimGame.game.assets.getTile(GameAssets.TileKey.NARRATIVE_BACKGROUND_2)),
            new IntroSlideInformation("You have three employees. Assign them to develop features or maintain your current system.",
                    SimGame.game.assets.getTile(GameAssets.TileKey.NARRATIVE_BACKGROUND_3)),
            new IntroSlideInformation("You have a job review in two weeks. Do you have what it takes?",
                    SimGame.game.assets.getTile(GameAssets.TileKey.NARRATIVE_BACKGROUND_4)));

    private Button playButton;
    private Button creditsButton;

    public StartingScreen(ScreenStack screenStack) {
        super(SimGame.game.plat);
        this.screenStack = checkNotNull(screenStack);
        new Pointer(game().plat, layer, true);
        createUI();
    }

    private void createUI() {
        Root root = iface.createRoot(AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics()), layer)
                .setSize(size());
        Image logo = SimGame.game.assets.getImage(GameAssets.ImageKey.LOGO);
        Icon iconLogo = Icons.image(logo);
        final float buttonFontSize = percentOfViewHeight(0.04f);
        final Font font = FontCache.instance().REGULAR.derive(buttonFontSize);
        final Button[] buttons = new Button[]{
                playButton = new Button("Start the Game!")
                        .addStyles(Style.FONT.is(font))
                        .onClick(new Slot<Button>() {
                            @Override
                            public void onEmit(Button button) {
                                disableButtons();
                                screenStack.push(new IntroScreen(screenStack, narrativeInfoList.iterator()), screenStack.slide());
                            }
                        }),
                creditsButton = new Button("Credits")
                        .addStyles(Style.FONT.is(font))
                        .onClick(new Slot<Button>() {
                            @Override
                            public void onEmit(Button button) {
                                screenStack.push(new CreditScreen(screenStack), screenStack.slide());
                            }
                        })
        };
        root.add(new Label(iconLogo),
                buttons[0],
                new Shim(0, buttonFontSize / 2),
                buttons[1]);
        root.setStyles(Style.BACKGROUND.is(Background.solid(Palette.START_BACKGROUND)));
    }

    private void disableButtons() {
        playButton.setEnabled(false);
        creditsButton.setEnabled(false);
    }

    private void enableButtons() {
        playButton.setEnabled(true);
        creditsButton.setEnabled(true);
    }

    private float percentOfViewHeight(float percent) {
        return SimGame.game.bounds.percentOfHeight(percent);
    }

    @Override
    public Game game() {
        return SimGame.game;
    }

    @Override
    public void wasShown() {
        super.wasShown();
        enableButtons();
        Jukebox.instance().loop(MusicCache.instance().INTRO_THEME);
    }

}
