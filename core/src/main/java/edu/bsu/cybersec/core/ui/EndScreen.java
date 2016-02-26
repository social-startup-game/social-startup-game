/*
 * Copyright 2016 Paul Gestwicki
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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.bsu.cybersec.core.DecimalTruncator;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.study.PostSurveyScreen;
import playn.core.Game;
import react.Slot;
import tripleplay.entity.Entity;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;
import tripleplay.util.Colors;

import java.util.Map;

public class EndScreen extends ScreenStack.UIScreen {

    private final GameWorld gameWorld;
    private final ScreenStack screenStack;
    private final int finalUserAmount;
    private final float finalExposure;
    private final DecimalTruncator truncator = new DecimalTruncator(2);
    private static final float HGAP_BETWEEN_COLUMNS = SimGame.game.bounds.percentOfHeight(0.02f);
    private static final float TABLE_INSETS = SimGame.game.bounds.percentOfHeight(0.005f);

    public EndScreen(ScreenStack screenStack, GameWorld gameWorld) {
        super(SimGame.game.plat);
        this.gameWorld = gameWorld;
        this.screenStack = screenStack;
        finalUserAmount = gameWorld.users.get().intValue();
        finalExposure = gameWorld.exposure.get();
        logEndGameStats();
    }

    private void logEndGameStats() {
        float exposure = gameWorld.exposure.get();
        SimGame.game.plat.log().info("Final users: " + finalUserAmount
                + "; exposure: " + exposure);
    }

    @Override
    public void wasShown() {
        super.wasShown();
        createUI();
    }

    private void createUI() {
        Root root = iface.createRoot(AxisLayout.vertical(), SimGameStyle.newSheet(game().plat.graphics()), layer)
                .setSize(size())
                .add(new Label("Your two weeks is up! Lets take a look.")
                        .setStyles(Style.COLOR.is(GameColors.HUNTER_GREEN)))
                .setStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE)));
        root.add(createEndGameTable());
        root.add(new Label(determineOutcomeText())
                        .addStyles(Style.COLOR.is(GameColors.HUNTER_GREEN),
                                Style.TEXT_WRAP.on),
                BossAtDeskLabelFactory.create(gameWorld.company.get().boss.image),
                new Button("OK").onClick(new Slot<Button>() {
                    @Override
                    public void onEmit(Button button) {
                        if (!SimGame.game.config.skipIntro()) {
                            if (SimGame.game.consent.get()) {
                                screenStack.replace(new PostSurveyScreen(SimGame.game), screenStack.slide().right());
                            } else {
                                screenStack.remove(EndScreen.this, screenStack.slide().right());
                            }
                        } else {
                            screenStack.push(new StartingScreen(screenStack), screenStack.slide().right());
                        }
                    }
                }));
    }

    private Element<?> createEndGameTable() {
        Group wholeTable = new Group(AxisLayout.vertical())
                .addStyles(Style.BACKGROUND.is(Background.solid(GameColors.HALF_BAKED).inset(5, 5)));
        Group tableHeader = new Group(AxisLayout.horizontal());
        tableHeader.add(new Label("Performance Review").addStyles(Style.COLOR.is(Colors.WHITE)));
        Group summaryTable = new Group(new TableLayout(
                new ExposedColumn(Style.HAlign.LEFT, true, 1, 0),
                new ExposedColumn(Style.HAlign.CENTER, false, 1, HGAP_BETWEEN_COLUMNS),
                new ExposedColumn(Style.HAlign.RIGHT, false, 1, 0)))
                .addStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE).inset(TABLE_INSETS, TABLE_INSETS)));
        Map<String, String> data = tabulateResults();
        for (String key : data.keySet()) {
            summaryTable.add(new Label(key).addStyles(Style.HALIGN.left),
                    new Shim(0, 0),
                    new Label(data.get(key)).addStyles(Style.HALIGN.left));
        }
        wholeTable.add(tableHeader, summaryTable, new Shim(1, 1));
        return wholeTable;
    }

    private Map<String, String> tabulateResults() {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("Users", String.valueOf(finalUserAmount));
        map.put("Features", String.valueOf(completedFeatures()));
        map.put("Exposure", String.valueOf(truncator.makeTruncatedString(finalExposure * 100f)) + "%");
        map.put("Known exploits", String.valueOf(knownExploits()));
        return ImmutableMap.copyOf(map);
    }

    private int completedFeatures() {
        // There is always one in development, so the total count is one less than the total of featureNumbers.
        int count = -1;
        for (Entity e : gameWorld) {
            if (e.has(gameWorld.featureNumber)) count++;
        }
        return count;
    }

    private int knownExploits() {
        int count = 0;
        for (Entity e : gameWorld) {
            if (e.has(gameWorld.exploitNumber)) count++;
        }
        return count;
    }

    private String determineOutcomeText() {
        return new EndTextGenerator(gameWorld).generatorEndText();
    }

    @Override
    public Game game() {
        return SimGame.game;
    }
}
